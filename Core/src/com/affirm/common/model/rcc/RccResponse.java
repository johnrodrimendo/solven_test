package com.affirm.common.model.rcc;

import com.affirm.common.model.transactional.RccIdeGrouped;
import com.affirm.common.model.transactional.RccSal;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.RccUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class RccResponse {

    public static final String CALIFICATION_PERDIDA= "PERDIDA";
    public static final String CALIFICATION_DUDOSO= "DUDOSO";
    public static final String CALIFICATION_DEFICIENTE= "DEFICIENTE";
    public static final String CALIFICATION_CPP= "CPP";
    public static final String CALIFICATION_NORMAL= "NORMAL";
    public static final String CALIFICATION_SIN_CALIFICACION= "SIN CALIF.";

    private List<RccIdeGrouped> ideGroupeds;
    private List<Synthesized> synthesizeds;
    private List<FinancialSystem> financialSystems = new ArrayList<>();

    public void performFinancialSystems(UtilService utilService) {
        this.getCalificationsByPeriod().forEach((rccDate, calificationsRcc) -> {

            FinancialSystem financialSystem = new FinancialSystem();
            financialSystem.setPeriodDate(rccDate);
            financialSystem.setPeriod(utilService.startDateFormat(rccDate));
            financialSystem.setCalification(getNombreCalificacion(calificationsRcc));
            calificationsRcc.forEach(e -> financialSystem.addKeyRcc(new KeyRcc(e.getDebtCode(), e.getPersonType())));

            List<Synthesized> synthesizedsOfPeriod = this.synthesizeds
                    .stream()
                    .filter(e -> rccDate.equals(e.getFecRep()))
                    .collect(Collectors.toList());

            List<RccSal> debtSals = ideGroupeds
                    .stream()
                    .filter(e -> e.getRccIde() != null && e.getRccIde().getFecRep() != null && e.getRccIde().getFecRep().equals(rccDate))
                    .flatMap(e -> e.getRccSals().stream())
                    .collect(Collectors.toList());

            double sumTotalSoles = synthesizedsOfPeriod.stream().mapToDouble(Synthesized::getSaldo).sum();
            double sumMortgageSoles = synthesizedsOfPeriod.stream().mapToDouble(Synthesized::getHipotecario).sum();
            double sumTotalSolesWOmortgage = sumTotalSoles - sumMortgageSoles;
            double monthlyFeeSoles = RccUtils.getMonthlyInstallment(debtSals);
            double monthlyFeeSolesWOmorgage = monthlyFeeSoles - RccUtils.getMortgageInstallment(debtSals).doubleValue();

            if (sumTotalSoles >= 0.005) {
                financialSystem.setReportedDebtSoles(sumTotalSoles);
            }
            if (sumTotalSolesWOmortgage >= 0.005) {
                financialSystem.setReportedDebtSolesWOmortgage(sumTotalSolesWOmortgage);
            }
            if (monthlyFeeSoles >= 0.005) {
                financialSystem.setEstimatedFeeSoles(monthlyFeeSoles);
            }
            if (monthlyFeeSolesWOmorgage >= 0.005) {
                financialSystem.setEstimatedFeeSolesWOmortgage(monthlyFeeSolesWOmorgage);
            }

            financialSystem.setArrears((int) synthesizedsOfPeriod.stream().filter(e -> e.getDiasAtraso() != null).mapToDouble(Synthesized::getDiasAtraso).max().orElse(0));
            financialSystem.setEntitiesNumber(synthesizedsOfPeriod.stream().map(Synthesized::getCodEmp).distinct().count());

            List<FinancialSystemDetail> allDetailsOfPeriod = new ArrayList<>();
            allDetailsOfPeriod = getFinancialSystemDetails(synthesizedsOfPeriod);

            financialSystem.setFinancialSystemDetails(allDetailsOfPeriod);
            financialSystems.add(financialSystem);
        });

        this.financialSystems.sort(Comparator.comparing(FinancialSystem::getPeriodDate).reversed());
    }

    private List<FinancialSystemDetail> getFinancialSystemDetails(List<Synthesized> synthesizeds) {
        List<FinancialSystemDetail> allDetails = new ArrayList<>();
        synthesizeds.forEach(synthesized -> {
            List<FinancialSystemDetail> details = new ArrayList<>();

            if (synthesized.getMicroSaldoTc() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Micro Saldo TC", synthesized.getMicroSaldoTc()));
            }
            if (synthesized.getMicroPrestamosRev() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Micro Préstamos Revolventes", synthesized.getMicroPrestamosRev()));
            }
            if (synthesized.getMicroSobregiros() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Micro Sobregiros", synthesized.getMicroSobregiros()));
            }
            if (synthesized.getMicroPrestamosNoRev() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Micro Préstamos No Revolventes", synthesized.getMicroPrestamosNoRev()));
            }
            if (synthesized.getMicroArrendamiento() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Micro Arrendamiento", synthesized.getMicroArrendamiento()));
            }
            if (synthesized.getMicroLeaseBack() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Micro Lease Back", synthesized.getMicroLeaseBack()));
            }
            if (synthesized.getMicroLineaCredito() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Micro Línea de Crédito", synthesized.getMicroLineaCredito()));
            }
            if (synthesized.getPequenaSaldoTc() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Pequeña Saldo TC", synthesized.getPequenaSaldoTc()));
            }
            if (synthesized.getPequenaPrestamosRev() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Pequeña Préstamos Revolventes", synthesized.getPequenaPrestamosRev()));
            }
            if (synthesized.getPequenaSobregiros() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Pequeña Sobregiros", synthesized.getPequenaSobregiros()));
            }
            if (synthesized.getPequenaPrestamosNoRev() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Pequeña Préstamos No Revolventes", synthesized.getPequenaPrestamosNoRev()));
            }
            if (synthesized.getPequenaArrendamiento() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Pequeña Arrendamiento", synthesized.getPequenaArrendamiento()));
            }
            if (synthesized.getPequenaLeaseBack() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Pequeña Lease Back", synthesized.getPequenaLeaseBack()));
            }
            if (synthesized.getPequenaLineaCredito() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Pequeña Línea de Crédito", synthesized.getPequenaLineaCredito()));
            }
            if (synthesized.getMedianaSaldoTc() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Mediana Saldo TC", synthesized.getMedianaSaldoTc()));
            }
            if (synthesized.getMedianaPrestamosRev() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Mediana Préstamos Revolventes", synthesized.getMedianaPrestamosRev()));
            }
            if (synthesized.getMedianaSobregiros() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Mediana Sobregiros", synthesized.getMedianaSobregiros()));
            }
            if (synthesized.getMedianaPrestamosNoRev() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Mediana Préstamos No Revolventes", synthesized.getMedianaPrestamosNoRev()));
            }
            if (synthesized.getMedianaArrendamiento() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Mediana Arrendamiento", synthesized.getMedianaArrendamiento()));
            }
            if (synthesized.getMedianaLeaseBack() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Mediana Lease Back", synthesized.getMedianaLeaseBack()));
            }
            if (synthesized.getMedianaLineaCredito() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Mediana Línea de Crédito", synthesized.getMedianaLineaCredito()));
            }
            if (synthesized.getGranSaldoTc() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Gran Saldo TC", synthesized.getGranSaldoTc()));
            }
            if (synthesized.getGranPrestamosRev() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Gran Préstamos Revolventes", synthesized.getGranPrestamosRev()));
            }
            if (synthesized.getGranSobregiros() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Gran Sobregiros", synthesized.getGranSobregiros()));
            }
            if (synthesized.getGranPrestamosNoRev() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Gran Préstamos No Revolventes", synthesized.getGranPrestamosNoRev()));
            }
            if (synthesized.getGranArrendamiento() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Gran Arrendamiento", synthesized.getGranArrendamiento()));
            }
            if (synthesized.getGranLeaseBack() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Gran Lease Back", synthesized.getGranLeaseBack()));
            }
            if (synthesized.getGranLineaCredito() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Gran Línea de Crédito", synthesized.getGranLineaCredito()));
            }
            if (synthesized.getHipotecario() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Hipotecario", synthesized.getHipotecario()));
            }
            if (synthesized.getConsumoSaldoTc() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Saldo TC", synthesized.getConsumoSaldoTc()));
            }
            if (synthesized.getConsumoPrestamosRev() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Préstamos Revolventes", synthesized.getConsumoPrestamosRev()));
            }
            if (synthesized.getConsumoSobregiros() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Sobregiro", synthesized.getConsumoSobregiros()));
            }
            if (synthesized.getConsumoPrestamosNoRev() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Préstamos No Revolventes", synthesized.getConsumoPrestamosNoRev()));
            }
            if (synthesized.getConsumoLineaCredito() > 0) {
                details.add(new FinancialSystemDetail(synthesized, "Línea de Crédito", synthesized.getConsumoLineaCredito()));
            }

            if (details.isEmpty()) {
                details.add(new FinancialSystemDetail(synthesized, null, synthesized.getSaldo()));
            }

            allDetails.addAll(details);
        });

        return allDetails;
    }

    private List<CalificationDetailRcc> getPersonRccDetails(List<CuentaContable> cuentaContables) {
        List<CalificationDetailRcc> calificationDetailRccDetails = new ArrayList<>();

        getDeudasByPeriod().forEach((rccDate, rccDeudas) -> {
            CalificationDetailRcc calificationDetailRcc = new CalificationDetailRcc();
            calificationDetailRcc.setRccDate(rccDate);

            List<RccDetail> rccDetails = new ArrayList<>();

            cuentaContables.forEach(cuentaContable -> {
                if (getDistincCodigosCuenta(1, "0000000000000", rccDeudas).contains(cuentaContable.getCodigoCuenta()) ||
                        getDistincCodigosCuenta(2, "000000000000", rccDeudas).contains(cuentaContable.getCodigoCuenta()) ||
                        getDistincCodigosCuenta(4, "0000000000", rccDeudas).contains(cuentaContable.getCodigoCuenta()) ||
                        getDistincCodigosCuenta(6, "00000000", rccDeudas).contains(cuentaContable.getCodigoCuenta()) ||
                        getDistincCodigosCuenta(8, "000000", rccDeudas).contains(cuentaContable.getCodigoCuenta()) ||
                        getDistincCodigosCuenta(10, "0000", rccDeudas).contains(cuentaContable.getCodigoCuenta())
                ) {
                    RccDetail rccDetail = new RccDetail();
                    rccDetail.setAccountType(cuentaContable.getTipoCuenta());
                    rccDetail.setAccountCode(rtrim(cuentaContable.getCodigoCuenta(), '0'));
                    rccDetail.setAccountName(cuentaContable.getNombreCuenta());

                    rccDetails.add(rccDetail);
                }
            });

            rccDeudas.forEach(deudaRcc -> {
                RccDetail rccDetail = new RccDetail();
                rccDetail.setAccountType("X");
                rccDetail.setAccountCode(rtrim(deudaRcc.getCodigoCuenta(), '0'));
                rccDetail.setAccountName(deudaRcc.getNombreEmpresa());
                rccDetail.setArrears(deudaRcc.getConDia());
                rccDetail.setRccDate(rccDate);
                rccDetail.setOriginalDebtCode(deudaRcc.getCodigoCuentaOriginal());
                rccDetail.setCalificationName(deudaRcc.getNombreCalificacion());

                if ("1".equals(deudaRcc.getTipoMoneda())) {
                    rccDetail.setSolesAmount(deudaRcc.getSaldo());
                } else if ("2".equals(deudaRcc.getTipoMoneda())) {
                    rccDetail.setDollarAmount(deudaRcc.getSaldo());
                }

                rccDetails.add(rccDetail);
            });

            Comparator<RccDetail> rccDetailComparator = Comparator.comparing(RccDetail::getAccountCode)
                    .thenComparing(RccDetail::getAccountType)
                    .thenComparing(RccDetail::getSolesAmount)
                    .thenComparing(RccDetail::getDollarAmount)
                    .thenComparing(RccDetail::getArrears);
            System.out.println(rccDate);
            rccDetails.sort(rccDetailComparator);

            calificationDetailRcc.setRccDetail(rccDetails);

            calificationDetailRccDetails.add(calificationDetailRcc);
        });

        calificationDetailRccDetails.sort(Comparator.comparing(CalificationDetailRcc::getRccDate).reversed());

        return calificationDetailRccDetails
                .stream()
                .limit(12)
                .collect(Collectors.toList());
    }

    private Map<Date, List<CalificationRcc>> getCalificationsByPeriod() {
        Map<Date, List<CalificationRcc>> calificationsByPeriod = new HashMap<>();

        getIdeGroupsByPeriod().forEach((rccDate, ideGroupeds) -> {
            List<CalificationRcc> calificationRccs = new ArrayList<>();

            getIdeGroupeds().stream()
                    .map(RccIdeGrouped::getRccIde)
                    .filter(e -> e.getFecRep() != null && e.getFecRep().equals(rccDate))
                    .forEach(e -> {
                        CalificationRcc calificationRcc = new CalificationRcc();
                        calificationRcc.setRccDate(e.getFecRep());
                        calificationRcc.setEntities(e.getCanEmp());
                        calificationRcc.setNormal(e.getPorCal0());
                        calificationRcc.setCpp(e.getPorCal1());
                        calificationRcc.setDefficient(e.getPorCal2());
                        calificationRcc.setDoubtful(e.getPorCal3());
                        calificationRcc.setLoss(e.getPorCal4());
                        calificationRcc.setPersonType(e.getTipPer());
                        calificationRcc.setDebtCode(e.getCodDeu());

                        calificationRccs.add(calificationRcc);
                    });
            calificationsByPeriod.put(rccDate, calificationRccs);
        });

        return calificationsByPeriod;
    }

    private Map<Date, List<DeudaRcc>> getDeudasByPeriod() {
        Map<Date, List<DeudaRcc>> calificationsByPeriod = new HashMap<>();

        getIdeGroupsByPeriod().forEach((rccDate, ideGroupeds) -> {
            List<DeudaRcc> deudaRccs = new ArrayList<>();

            getIdeGroupeds().stream()
                    .flatMap(e -> e.getRccSals().stream())
                    .filter(e -> e.getFecRep() != null && e.getFecRep().equals(rccDate))
                    .forEach(e -> {
                        DeudaRcc deudaRcc = new DeudaRcc();
                        deudaRcc.setRccDate(e.getFecRep());
                        deudaRcc.setCodigoEmpresa(e.getCodEmp());
                        deudaRcc.setCodigoCuenta(String.format("%s0%s", e.getCodCue().substring(0, 2), e.getCodCue().substring(3)));
                        deudaRcc.setCodigoCuentaOriginal(e.getCodCue());
                        deudaRcc.setTipoMoneda(String.valueOf(e.getCodCue().charAt(2)));
                        deudaRcc.setSaldo(e.getSaldo());
                        deudaRcc.setTipoCredito(Integer.parseInt(e.getTipCre()));
                        deudaRcc.setClaDeu(Integer.parseInt(e.getClaDeu()));
                        deudaRcc.setConDia(e.getConDia());
                        deudaRcc.setNombreCalificacion(getNombreCalificacion(e.getConDia()));
                        deudaRcc.setNombreEmpresa(e.getEntityShortName());

                        deudaRccs.add(deudaRcc);
                    });
            calificationsByPeriod.put(rccDate, deudaRccs);
        });

        return calificationsByPeriod;
    }

    private Map<Date, List<RccIdeGrouped>> getIdeGroupsByPeriod() {
        return this.getIdeGroupeds()
                .stream()
                .filter(e -> e.getRccIde() != null && e.getRccIde().getFecRep() != null)
                .collect(Collectors.groupingBy(e -> e.getRccIde().getFecRep(), HashMap::new, Collectors.toCollection(ArrayList::new)));
    }

    private String getNombreCalificacion(Integer conDia) {
        if (conDia >= 0 && conDia <= 8) {
            return CALIFICATION_NORMAL;
        } else if (conDia >= 9 && conDia <= 30) {
            return CALIFICATION_CPP;
        } else if (conDia >= 31 && conDia <= 60) {
            return CALIFICATION_DEFICIENTE;
        } else if (conDia >= 61 && conDia <= 120) {
            return CALIFICATION_DUDOSO;
        } else if (conDia >= 121) {
            return CALIFICATION_PERDIDA;
        } else {
            return CALIFICATION_SIN_CALIFICACION;
        }
    }

    private String getNombreCalificacion(List<CalificationRcc> calificationsRcc) {
        if (calificationsRcc.stream().anyMatch(e -> e.getLoss() > 0)) {
            return CALIFICATION_PERDIDA;
        } else if (calificationsRcc.stream().anyMatch(e -> e.getDoubtful() > 0)) {
            return CALIFICATION_DUDOSO;
        } else if (calificationsRcc.stream().anyMatch(e -> e.getDefficient() > 0)) {
            return CALIFICATION_DEFICIENTE;
        } else if (calificationsRcc.stream().anyMatch(e -> e.getCpp() > 0)) {
            return CALIFICATION_CPP;
        } else if (calificationsRcc.stream().allMatch(e -> e.getNormal() == 100)) {
            return CALIFICATION_NORMAL;
        } else {
            return CALIFICATION_SIN_CALIFICACION;
        }
    }

    private String getNombreCalificacion(CalificationRcc calificationRcc) {
        if (calificationRcc.getLoss() > 0) {
            return CALIFICATION_PERDIDA;
        } else if (calificationRcc.getDoubtful() > 0) {
            return CALIFICATION_DUDOSO;
        } else if (calificationRcc.getDefficient() > 0) {
            return CALIFICATION_DEFICIENTE;
        } else if (calificationRcc.getCpp() > 0) {
            return CALIFICATION_CPP;
        } else if (calificationRcc.getNormal() == 100) {
            return CALIFICATION_NORMAL;
        } else {
            return CALIFICATION_SIN_CALIFICACION;
        }
    }

    private List<String> getDistincCodigosCuenta(int endIndex, String padRight, List<DeudaRcc> deudasRcc) {
        return deudasRcc
                .stream()
                .map(e -> e.getCodigoCuenta().substring(0, endIndex) + padRight)
                .distinct()
                .collect(Collectors.toList());
    }

    private static String rtrim(String value, char valueToRemove) {
        int i = value.length() - 1;
        while (i >= 0 && value.charAt(i) == valueToRemove) {
            i--;
        }
        return value.substring(0, i + 1);
    }

    public List<RccIdeGrouped> getIdeGroupeds() {
        return ideGroupeds;
    }

    public void setIdeGroupeds(List<RccIdeGrouped> ideGroupeds) {
        this.ideGroupeds = ideGroupeds;
    }

    public List<Synthesized> getSynthesizeds() {
        return synthesizeds;
    }

    public void setSynthesizeds(List<Synthesized> synthesizeds) {
        this.synthesizeds = synthesizeds;
    }

    public List<FinancialSystem> getFinancialSystems() {
        return financialSystems;
    }

    public void setFinancialSystems(List<FinancialSystem> financialSystems) {
        this.financialSystems = financialSystems;
    }
}
