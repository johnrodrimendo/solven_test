package com.affirm.strategy.contracts;

import com.affirm.acceso.model.Direccion;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.lang.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccesoLibreDisponibilidadContractStrategy extends BaseContract {

    public AccesoLibreDisponibilidadContractStrategy(byte[] contract, CatalogService catalogService, PersonDAO personDAO, UtilService utilService, CreditDAO creditDAO, LoanApplicationDAO loanApplicationDAO) throws Exception {
        super(contract, catalogService, personDAO, utilService, creditDAO, loanApplicationDAO, null);
        this.defaultFont = prepareFont(pdfDoc, "/Arial.ttf");
    }

    @Override
    public void fillPdf(Person person, Credit credit, LoanOffer loanOffer, EntityProductParams params, User user, String signature, Person partner, PersonContactInformation contactInfo, List<PersonOcupationalInformation> ocupations, PersonOcupationalInformation principalOcupation, PersonBankAccountInformation personBankAccountInformation, LoanApplication loanApplication, Locale locale, SunatResult sunatResult) throws Exception {
        int defaultFontSize = 10;
        String defaultNoAplicaText = "N/A";

        Direccion disaggregatedHomeAddress;
        Double amount;
        Double downPayment;
        Double exchangeRate;
        String entityCreditCode;
        List<OriginalSchedule> originalSchedule;
        Integer installments;
        Double installmentAmountAvg;
        Double carInsuranceAmount;
        Double effectiveMonthlyRate;
        Double tea;
        Double tcea;
        Double scheduleInstallmentAmount;
        Double scheduleInstallmentCapital;
        Double scheduleInterest;
        Double scheduleInsurance;
        Double scheduleCollectionCommission;
        Double scheduleTotalCollectionCommission;
        Double scheduleInterestTax;
        long firstGracePeriodDays = 0;
        Date signatureDate;
        Date registerDate;
        Double insurance;
        if (credit != null) {
            disaggregatedHomeAddress = personDAO.getDisaggregatedHomeAddressByCredit(person.getId(), credit.getId());
            amount = credit.getAmount();
            downPayment = credit.getDownPayment();
            exchangeRate = credit.getExchangeRate();
            entityCreditCode = credit.getEntityCreditCode();
            originalSchedule = credit.getOriginalSchedule();
            installments = credit.getInstallments();
            installmentAmountAvg = credit.getInstallmentAmountAvg();
            carInsuranceAmount = credit.getOriginalSchedule().get(0).getCarInsurance();
            effectiveMonthlyRate = credit.getEffectiveMonthlyRate();
            tea = credit.getEffectiveAnnualRate();
            tcea = credit.getEffectiveAnnualCostRate();
            scheduleInstallmentAmount = credit.getTotalScheduleField("installmentAmount", 'O');
            scheduleInstallmentCapital = credit.getTotalScheduleField("installmentCapital", 'O');
            scheduleInterest = credit.getTotalScheduleField("interest", 'O');
            scheduleInsurance = credit.getTotalScheduleField("insurance", 'O');
            scheduleCollectionCommission = credit.getTotalScheduleField("collectionCommission", 'O');
            scheduleTotalCollectionCommission = credit.getTotalScheduleField("totalCollectionCommission", 'O');
            scheduleInterestTax = credit.getTotalScheduleField("interestTax", 'O');
            signatureDate = loanOffer != null ? loanOffer.getSignatureDate() : new Date(); // TODO Fix
            registerDate = credit.getRegisterDate();
            insurance = credit.getTotalScheduleField("insurance", 'O');
        } else {
            disaggregatedHomeAddress = personDAO.getDisggregatedAddress(person.getId(), "H");
            amount = loanOffer.getAmmount();
            downPayment = loanOffer.getDownPayment();
            exchangeRate = loanOffer.getExchangeRate();
            entityCreditCode = null;
            originalSchedule = loanOffer.getOfferSchedule();
            installments = loanOffer.getInstallments();
            installmentAmountAvg = loanOffer.getInstallmentAmountAvg();
            carInsuranceAmount = loanOffer.getOfferSchedule().get(0).getCarInsurance();
            effectiveMonthlyRate = loanOffer.getMonthlyRate() * 100.0;
            tea = loanOffer.getEffectiveAnualRate();
            tcea = loanOffer.getEffectiveAnnualCostRate();
            scheduleInstallmentAmount = loanOffer.getTotalScheduleField("installmentAmount");
            scheduleInstallmentCapital = loanOffer.getTotalScheduleField("installmentCapital");
            scheduleInterest = loanOffer.getTotalScheduleField("interest");
            scheduleInsurance = loanOffer.getTotalScheduleField("insurance");
            scheduleCollectionCommission = loanOffer.getTotalScheduleField("collectionCommission");
            scheduleTotalCollectionCommission = loanOffer.getTotalScheduleField("collectionCommission") + loanOffer.getTotalScheduleField("collectionCommissionTax");
            scheduleInterestTax = loanOffer.getTotalScheduleField("interestTax");
            signatureDate = new Date();
            registerDate = loanOffer.getRegisterDate();
            insurance = loanOffer.getInsurance();
        }

        if (originalSchedule != null) {
            OriginalSchedule primerCuota = originalSchedule.get(0);
            firstGracePeriodDays = utilService.daysBetween(registerDate, primerCuota.getDueDate());

            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(primerCuota.getDueDate());
            int dayOfMonth = cal1.get(Calendar.DAY_OF_MONTH);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(registerDate);

            while (originalSchedule.get(0).getInstallmentId() != null && !DateUtils.truncate(cal2.getTime(), Calendar.DATE).after(DateUtils.truncate(primerCuota.getDueDate(), Calendar.DATE))) {
                int dueDateToCompare = cal2.get(Calendar.DAY_OF_MONTH);

                if (dayOfMonth == dueDateToCompare) {
                    OriginalSchedule graceSchedule = new OriginalSchedule();
                    graceSchedule.setDueDate(cal2.getTime());
                    graceSchedule.setRemainingCapital(amount);
                    graceSchedule.setInstallmentCapital(0.0);
                    graceSchedule.setInterest(scheduleInstallmentCapital - amount);
                    graceSchedule.setInsurance(0.0);
                    graceSchedule.setCollectionCommission(0.0);
                    graceSchedule.setInterestTax(0.0);
                    graceSchedule.setInstallmentAmount(0.0);
                    originalSchedule.add(0, graceSchedule);
                }

                cal2.add(Calendar.DATE, 1);
            }
        }

//        COMUNES
        commonFillText("person.fullName", person.getFullName());
        fillText("date.place", "Lima");
        commonFillText("date.day", new SimpleDateFormat("dd").format(new Date()));
        fillText("date.monthdescription", new SimpleDateFormat("MMMM", Configuration.getDefaultLocale()).format(new Date()));
        commonFillText("date.month", new SimpleDateFormat("MM").format(new Date()));
        commonFillText("date.year", new SimpleDateFormat("yyyy").format(new Date()));
        fillText("dateAndPlace", "Lima, " + new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Configuration.getDefaultLocale()).format(new Date()));
        commonFillText("credit.periodoGracia", firstGracePeriodDays + "");
        fillText("credit.acceso.expediente", loanApplication.getEntityApplicationCode());

//        SOLICITUD DE CREDITO
        commonFillText("loanApplication.date", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        fillCheckBox("credit.type.consumo", true);
        fillCheckBox("credit.type.mes", false);
        fillCheckBox("credit.type.pequenaEmpresa", false);
        commonFillText("credit.maf", utilService.doubleMoneyFormat(amount, (String) null));
        commonFillText("credit.downPayment", downPayment != null && exchangeRate != null ? utilService.doubleMoneyFormat(downPayment * exchangeRate, (String) null) : 0);
        commonFillText("credit.codigoExpediente", entityCreditCode);
        commonFillText("credit.installments", installments + " meses");
        commonFillText("credit.servicioBancarizacion", "0.00");
        commonFillText("credit.installmentAmount", utilService.doubleMoneyFormat(installmentAmountAvg));
        commonFillText("credit.insuranceAmount", utilService.doubleMoneyFormat(carInsuranceAmount));
        commonFillText("credit.effectiveMonthlyRate", utilService.percentFormat(effectiveMonthlyRate));
        commonFillText("person.firstSurname", person.getFirstSurname());
        commonFillText("person.lastSurname", person.getLastSurname());
        commonFillText("person.firstName", person.getFirstName());
        commonFillText("person.secondName", person.getName().replace(person.getFirstName(), ""));
        fillCheckBox("person.docType.ce", person.getDocumentType().getId() == IdentityDocumentType.CE);
        fillCheckBox("person.docType.dni", person.getDocumentType().getId() == IdentityDocumentType.DNI);
        commonFillText("person.docNumber", person.getDocumentNumber());
        commonFillText("person.birthDate", utilService.dateFormat(person.getBirthday()));
        commonFillText("person.birthPlace", "Lima");
        fillCheckBox("person.gender.male", person.getGender() != null && person.getGender() == 'M');
        fillCheckBox("person.gender.female", person.getGender() != null && person.getGender() == 'F');
        commonFillText("person.nationality", person.getNationality().getName());
        commonFillText("person.residencia", "Perú");
        if (person.getMaritalStatus() != null) {
            fillCheckBox("person.maritalStatus.single", person.getMaritalStatus().getId() == MaritalStatus.SINGLE);
            fillCheckBox("person.maritalStatus.married", person.getMaritalStatus().getId() == MaritalStatus.MARRIED);
            fillCheckBox("person.maritalStatus.cohabitant", person.getMaritalStatus().getId() == MaritalStatus.COHABITANT);
            fillCheckBox("person.maritalStatus.divorced", person.getMaritalStatus().getId() == MaritalStatus.DIVORCED);
            fillCheckBox("person.maritalStatus.widow", person.getMaritalStatus().getId() == MaritalStatus.WIDOWED);
            commonFillText("person.maritalStatus.description", person.getMaritalStatus().getStatus());
        }
        commonFillText("person.address.address", contactInfo != null ? contactInfo.getAddressStreetName() : null, 6);
        commonFillText("person.address.mz", null);
        commonFillText("person.address.dpto", null);
        commonFillText("person.address.urbanizacion", null);
        if (disaggregatedHomeAddress != null) {
            Ubigeo ubigeo = catalogService.getUbigeo(disaggregatedHomeAddress.getUbigeo());
            commonFillText("person.address.district", ubigeo.getDistrict().getName(), 6);
            commonFillText("person.address.province", ubigeo.getProvince().getName(), 6);
            commonFillText("person.address.department", ubigeo.getDepartment().getName(), 6);
            commonFillText("person.address.reference", disaggregatedHomeAddress.getReferencia(), 6);
        } else if (contactInfo != null && contactInfo.getAddressUbigeo() != null) {
            commonFillText("person.address.district", contactInfo.getAddressUbigeo().getDistrict().getName(), 6);
            commonFillText("person.address.province", contactInfo.getAddressUbigeo().getProvince().getName(), 6);
            commonFillText("person.address.department", contactInfo.getAddressUbigeo().getDepartment().getName(), 6);
        }
        commonFillText("person.email", user.getEmail(), 6);
        commonFillText("person.phoneNumber", user.getPhoneNumber());
        if (principalOcupation != null) {
            switch (principalOcupation.getActivityType().getId()) {
                case ActivityType.INDEPENDENT:
                    fillCheckBox("person.activityType.independent", true);
                    break;
                case ActivityType.DEPENDENT:
                    fillCheckBox("person.activityType.dependent", true);
                    break;
                case ActivityType.PENSIONER:
                    fillCheckBox("person.activityType.pensioner", true);
                    break;
                default:
                    fillCheckBox("person.activityType.other", true);
                    break;
            }
            commonFillText("person.profession", person.getProfessionOccupation() != null ? person.getProfessionOccupation().getOccupation() : null, 6);
            commonFillText("person.ocupationalInformation.ocupation", principalOcupation.getOcupation() != null ? principalOcupation.getOcupation().getOcupation() : null, 6);
            commonFillText("person.ocupationalInformation.address", principalOcupation.getAddress(), 6);
            commonFillText("person.ocupationalInformation.ruc", principalOcupation.getRuc());
            commonFillText("person.ocupationalInformation.ciiu", null);
            commonFillText("person.ocupationalInformation.phoneNumber", principalOcupation.getPhoneNumber());
            commonFillText("person.ocupationalInformation.startDate", utilService.dateFormat(principalOcupation.getStartDate()));
            commonFillText("person.ocupationalInformation.monthlyNetIncome", utilService.doubleMoneyFormat(principalOcupation.getFixedGrossIncome()));
            commonFillText("person.ocupationalInformation.companyName", principalOcupation.getCompanyName());
        }

        if (partner != null) {
            commonFillText("person.partner.firstSurname", partner.getFirstSurname());
            commonFillText("person.partner.lastSurname", partner.getLastSurname());
            commonFillText("person.partner.firstName", partner.getFirstName());
            commonFillText("person.partner.secondName", partner.getName().replace(partner.getFirstName(), ""));
            fillCheckBox("person.partner.docType.ce", partner.getDocumentType().getId() == IdentityDocumentType.CE);
            fillCheckBox("person.partner.docType.dni", partner.getDocumentType().getId() == IdentityDocumentType.DNI);
            commonFillText("person.partner.docNumber", partner.getDocumentNumber());
            commonFillText("person.partner.birthDate", utilService.dateFormat(partner.getBirthday()));
            commonFillText("person.partner.birthPlace", null);
            fillCheckBox("person.partner.gender.male", partner.getGender() != null && partner.getGender() == 'M');
            fillCheckBox("person.partner.gender.female", partner.getGender() != null && partner.getGender() == 'F');
            commonFillText("person.partner.nationality", partner.getNationality().getName());
            commonFillText("person.partner.residencia", null);
            if (partner.getMaritalStatus() != null) {
                fillCheckBox("person.partner.maritalStatus.single", partner.getMaritalStatus().getId() == MaritalStatus.SINGLE);
                fillCheckBox("person.partner.maritalStatus.married", partner.getMaritalStatus().getId() == MaritalStatus.MARRIED);
                fillCheckBox("person.partner.maritalStatus.cohabitant", partner.getMaritalStatus().getId() == MaritalStatus.COHABITANT);
                fillCheckBox("person.partner.maritalStatus.divorced", partner.getMaritalStatus().getId() == MaritalStatus.DIVORCED);
                fillCheckBox("person.partner.maritalStatus.widow", partner.getMaritalStatus().getId() == MaritalStatus.WIDOWED);
                commonFillText("person.partner.maritalStatus.description", partner.getMaritalStatus().getStatus());
            }
        }
//        PAGARE - AVAL
        commonFillText("pagare.numero", "");
        commonFillText("pagare.vencimiento", "");
        commonFillText("pagare.monto", "");

//        AUTORIZACION DE DEBITO AUTOMATICO
        commonFillText("cc", "");
        commonFillText("bank.name", personBankAccountInformation.getBank().getName());
        commonFillText("bank.number", personBankAccountInformation.getBankAccount());

//        HOJA RESUMEN
        commonFillText("credit.acceso.tipo", credit != null ? credit.getProduct().getName() : loanOffer.getProduct().getName());
        commonFillText("credit.acceso.producto", credit != null ? credit.getEntityProductParams().getEntityProduct() : loanOffer.getEntityProductParam().getEntityProduct());
        commonFillText("credit.acceso.nroresolucionAprobacionContrato", defaultNoAplicaText);
        commonFillText("credit.acceso.nrocredito", credit != null ? credit.getEntityCreditCode() : null);
        commonFillText("credit.acceso.moneda", credit != null ? credit.getCurrency().getCurrency() : loanOffer.getCurrency().getCurrency());
        commonFillText("credit.acceso.monto", utilService.doubleMoneyFormat(credit != null ? credit.getAmount() : loanOffer.getAmmount(), ""));
        commonFillText("credit.acceso.tipoCuotas", credit != null ? credit.getProduct().getPaymentType().getName().toUpperCase() : loanOffer.getProduct().getPaymentType().getName().toUpperCase());
        commonFillText("credit.acceso.cuotas", credit != null ? credit.getInstallments() : loanOffer.getInstallments());
        commonFillText("credit.acceso.plazo", firstGracePeriodDays + "");
        commonFillText("credit.acceso.porcentajeRecaudacion", defaultNoAplicaText);
        commonFillText("credit.acceso.crono", new SimpleDateFormat("dd/MM/yyyy").format(signatureDate));

        commonFillText("credit.acceso.tea", tea != null ? utilService.percentFormat(tea) : null);
        commonFillText("credit.acceso.tasaMoratoriaIncumplimientoPago", defaultNoAplicaText);
        commonFillText("credit.acceso.tasaCompensatoriaIncumplimientoPago", credit != null ? utilService.percentFormat(credit.getMoratoriumRate()) : utilService.percentFormat(loanOffer.getMoratoriumRate()));
        commonFillText("credit.acceso.interesesCompensatorios", utilService.doubleFormat(scheduleInterest));
        commonFillText("credit.acceso.tcea", tcea != null ? utilService.customDoubleFormat(tcea, 2) + "%" : null);

        commonFillText("credit.acceso.revisionPolizaVehicularEndosada", null);
        commonFillText("credit.acceso.endosoPolizaDeDesgravamen", null);
        commonFillText("credit.acceso.segundaConstanciaDeNoAdeudo", null);
        commonFillText("credit.acceso.duplicadoDeDocumentos", null);
        commonFillText("credit.acceso.envioFisicoDelEstadoDeCuenta", null);
        commonFillText("credit.acceso.serviciosDeEstudioDeFactabilidadYGestionDeRuta", defaultNoAplicaText);
        commonFillText("credit.acceso.telemetria", defaultNoAplicaText);

        commonFillText("credit.acceso.gastosNotariales", null);
        commonFillText("credit.acceso.gastosRegistrales", null);
        commonFillText("credit.acceso.gastosProtestosPagare", null);
        commonFillText("credit.acceso.certificadoTaxi", defaultNoAplicaText);
        commonFillText("credit.acceso.recategoriaLicencia", defaultNoAplicaText);
        commonFillText("credit.acceso.soat", utilService.doubleMoneyFormat(0.0, "S/."));
        commonFillText("credit.acceso.recaudacionBanco", null);
        commonFillText("credit.acceso.gps", utilService.doubleMoneyFormat(0.0, "S/."));

        fillCheckBox("credit.acceso.desgravamenAcceso", false);
        fillCheckBox("credit.acceso.desgravamenEndosado", false);
        commonFillText("credit.acceso.desgravamenMontoPrima", null);
        commonFillText("credit.acceso.desgravamenNombreCompania", null);
        commonFillText("credit.acceso.desgravamenNroPoliza", null);

        fillCheckBox("credit.acceso.vehicularAcceso", false);
        fillCheckBox("credit.acceso.vehicularEndosado", false);
        commonFillText("credit.acceso.vehicularTasaPrima", utilService.doubleFormat(0.0));
        commonFillText("credit.acceso.vehicularNombreCompania", defaultNoAplicaText);
        commonFillText("credit.acceso.vehicularNroPoliza", null);

        if (originalSchedule != null) {
            for (int index = 1; index <= originalSchedule.size(); index++) {
                OriginalSchedule cuota = originalSchedule.get(index - 1);
                String baseFieldCuota = "cuota." + index;
                commonFillText(baseFieldCuota + ".nro", cuota.getInstallmentId());
                commonFillText(baseFieldCuota + ".date", utilService.dateFormat(cuota.getDueDate()));
                commonFillText(baseFieldCuota + ".saldoCapital", utilService.doubleFormat(cuota.getRemainingCapital() + cuota.getInstallmentCapital()));// SALDO + AMORTIZACION - PREVIO CUOTA GRACIA
                commonFillText(baseFieldCuota + ".amortizacion", utilService.doubleFormat(cuota.getInstallmentCapital()));
                commonFillText(baseFieldCuota + ".interesComp", utilService.doubleFormat(cuota.getInterest()));
                commonFillText(baseFieldCuota + ".seguroDesg", utilService.doubleFormat(cuota.getInsurance()));
                commonFillText(baseFieldCuota + ".seguro", utilService.doubleFormat(cuota.getCollectionCommission()));
                commonFillText(baseFieldCuota + ".comisionesGastos", utilService.doubleFormat(cuota.getTotalCollectionCommission()));
                commonFillText(baseFieldCuota + ".itf", utilService.doubleFormat(cuota.getInterestTax()));
                commonFillText(baseFieldCuota + ".totalCuota", utilService.doubleFormat(cuota.getInstallmentAmount()));
                if (index == 1) {
                    Date startDate = null;
                    if (credit != null)
                        startDate = credit.getRegisterDate();
                    else
                        startDate = loanOffer.getRegisterDate();
                    commonFillText(baseFieldCuota + ".plazo", utilService.daysBetween(startDate, cuota.getDueDate()));
                } else {
                    OriginalSchedule cuotaPasada = originalSchedule.get(index - 2);
                    commonFillText(baseFieldCuota + ".plazo", utilService.daysBetween(cuotaPasada.getDueDate(), cuota.getDueDate()));
                }
            }
            commonFillText("cuota.total.amortizacion", utilService.doubleFormat(scheduleInstallmentCapital));
            commonFillText("cuota.total.interesComp", utilService.doubleFormat(scheduleInterest));
            commonFillText("cuota.total.seguroDesg", utilService.doubleFormat(scheduleInsurance));
            commonFillText("cuota.total.seguro", utilService.doubleFormat(scheduleCollectionCommission));
            commonFillText("cuota.total.comisionesGastos", utilService.doubleFormat(scheduleTotalCollectionCommission));
            commonFillText("cuota.total.itf", utilService.doubleFormat(scheduleInterestTax));
            commonFillText("cuota.total.totalCuota", utilService.doubleFormat(scheduleInstallmentAmount));
        }

        fillText("person.sign", signature != null ? signature : " ", handWritingFont, 18);

        //CARTA DE APROBACIÓN
        fillText("approval_day",  new SimpleDateFormat("dd").format(new Date()), 10);
        fillText("approval_month", new SimpleDateFormat("MMMM", Configuration.getDefaultLocale()).format(new Date()), 10);
        fillText("approval_year", new SimpleDateFormat("yyyy").format(new Date()).substring(new SimpleDateFormat("yyyy").format(new Date()).length()-1), 10);
        fillTextWithColor("approval_name", person.getFullName(), 18, "#eb7b31");
        fillText("approval_dni", person.getDocumentNumber(), 14);
        fillText("approval_amount", utilService.doubleMoneyFormat(amount, "S/"), 10);
        fillText("approval_quota", utilService.doubleMoneyFormat(installmentAmountAvg, "S/"), 10);
        fillText("approval_term", String.format("%s meses",installments), 10);
        fillText("approval_tea", utilService.customDoubleFormat(tea,2)+"%", 10);
        fillText("approval_tcea", utilService.customDoubleFormat(tcea,2)+"%", 10);
        fillText("approval_insurance", utilService.customDoubleFormat(insurance,2), 10);
    }

    private void commonFillText(String fieldName, Object value){
        fillText(fieldName, value != null ? value.toString().toUpperCase() + "" : "", defaultFont, 8);
    }

    private void commonFillText(String fieldName, Object value, int fontSize){
        fillText(fieldName, value != null ? value.toString().toUpperCase() + "" : "", defaultFont, fontSize);
    }
}
