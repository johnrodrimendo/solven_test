package com.affirm.common.util;

import com.affirm.common.model.transactional.RccSal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RccUtils {

    public static List<RccSal> getDebtSals(List<RccSal> sals) {
        return sals
                .stream()
                .filter(sal -> Objects.nonNull(sal.getCodCue()))
                .filter(sal ->
                        sal.getCodCue().matches("(81)\\d(302)\\d+") || // Saldo_Castigo
                                sal.getCodCue().matches("(14)\\d(1)\\d+") || // Saldo_Vigente
                                sal.getCodCue().matches("(14)\\d(3)\\d+") || // Saldo_Reestructurado
                                sal.getCodCue().matches("(14)\\d(4)\\d+") || // Saldo_Refinanciado
                                sal.getCodCue().matches("(14)\\d(5)\\d+") || // Saldo_Vencido
                                sal.getCodCue().matches("(14)\\d(6)\\d+")) // Saldo_Judicial
                .collect(Collectors.toList());
    }

    public static BigDecimal getMortgageInstallment(List<RccSal> sals) {
        return getTotalMortgage(sals).multiply(new BigDecimal("0.0089"));
    }

    public static BigDecimal getTotalMortgage(List<RccSal> sals) {
        return BigDecimal.valueOf(sals.stream()
                .filter(e -> e.getCodCue().matches("(14)\\d\\d(04)\\d+"))
                .filter(e -> !e.getCodCue().matches("(14)\\d(9)\\d+"))
                .mapToDouble(RccSal::getSaldo)
                .sum());
    }

    public static double getMonthlyInstallment(List<RccSal> sals) {
        BigDecimal saldoMicroEmpresa = BigDecimal.valueOf(sals.stream()
                .filter(e -> e.getCodCue().matches("(14)\\d\\d(02)\\d+"))
                .filter(e -> !e.getCodCue().matches("(14)\\d(9)\\d+"))
                .mapToDouble(RccSal::getSaldo)
                .sum());

        BigDecimal saldoConsumo = BigDecimal.valueOf(sals.stream()
                .filter(e -> e.getCodCue().matches("(14)\\d\\d(03)\\d+"))
                .filter(e -> !e.getCodCue().matches("(14)\\d(9)\\d+"))
                .mapToDouble(RccSal::getSaldo)
                .sum());

        BigDecimal saldoHipotecario = BigDecimal.valueOf(sals.stream()
                .filter(e -> e.getCodCue().matches("(14)\\d\\d(04)\\d+"))
                .filter(e -> !e.getCodCue().matches("(14)\\d(9)\\d+"))
                .mapToDouble(RccSal::getSaldo)
                .sum());

        BigDecimal saldoCorporativo = BigDecimal.valueOf(sals.stream()
                .filter(e -> e.getCodCue().matches("(14)\\d\\d(10)\\d+"))
                .filter(e -> !e.getCodCue().matches("(14)\\d(9)\\d+"))
                .mapToDouble(RccSal::getSaldo)
                .sum());

        BigDecimal saldoGranEmpresa = BigDecimal.valueOf(sals.stream()
                .filter(e -> e.getCodCue().matches("(14)\\d\\d(11)\\d+"))
                .filter(e -> !e.getCodCue().matches("(14)\\d(9)\\d+"))
                .mapToDouble(RccSal::getSaldo)
                .sum());

        BigDecimal saldoMedianaEmpresa = BigDecimal.valueOf(sals.stream()
                .filter(e -> e.getCodCue().matches("(14)\\d\\d(12)\\d+"))
                .filter(e -> !e.getCodCue().matches("(14)\\d(9)\\d+"))
                .mapToDouble(RccSal::getSaldo)
                .sum());

        BigDecimal saldoPequenaEmpresa = BigDecimal.valueOf(sals.stream()
                .filter(e -> e.getCodCue().matches("(14)\\d\\d(13)\\d+"))
                .filter(e -> !e.getCodCue().matches("(14)\\d(9)\\d+"))
                .mapToDouble(RccSal::getSaldo)
                .sum());

        BigDecimal lineaDisponibleTC = BigDecimal.valueOf(sals.stream()
                .filter(e -> e.getCodCue().matches("(72)\\d\\d(06)\\d+"))
                .mapToDouble(RccSal::getSaldo)
                .sum());

        BigDecimal saldoTC = BigDecimal.valueOf(sals.stream()
                .filter(e -> e.getCodCue().matches("(14)\\d\\d\\d\\d(02)\\d+"))
                .mapToDouble(RccSal::getSaldo)
                .sum());

        BigDecimal lineaDisponibleSTC = BigDecimal.valueOf(sals.stream()
                .filter(e -> e.getCodCue().matches("(72)\\d+"))
                .filter(e -> !e.getCodCue().matches("(72)\\d\\d(06)\\d+"))
                .mapToDouble(RccSal::getSaldo)
                .sum());

        BigDecimal saldoAval = BigDecimal.valueOf(sals.stream()
                .filter(e -> e.getCodCue().matches("(71)\\d(1)\\d+"))
                .mapToDouble(RccSal::getSaldo)
                .sum());

        BigDecimal saldoCastigo = BigDecimal.valueOf(sals.stream()
                .filter(e -> e.getCodCue().matches("(81)\\d(302)\\d+"))
                .mapToDouble(RccSal::getSaldo)
                .sum());

        BigDecimal saldoCorporativoM = saldoCorporativo.multiply(new BigDecimal("0.019"));
        BigDecimal saldoGranEmpresaM = saldoGranEmpresa.multiply(new BigDecimal("0.020"));
        BigDecimal saldoMedianaEmpresaM = saldoMedianaEmpresa.multiply(new BigDecimal("0.021"));
        BigDecimal saldoPequenaEmpresaM = saldoPequenaEmpresa.multiply(new BigDecimal("0.026"));
        BigDecimal saldoMicroEmpresaM = saldoMicroEmpresa.multiply(new BigDecimal("0.031"));
        BigDecimal saldoHipotecarioM = saldoHipotecario.multiply(new BigDecimal("0.0089"));
        BigDecimal saldoTCM = saldoTC.multiply(new BigDecimal("0.047"));
        BigDecimal lineaDisponibleTCM = lineaDisponibleTC.multiply(new BigDecimal("0.2")).multiply(new BigDecimal("0.05"));
        BigDecimal saldoAvalM = saldoAval.multiply(new BigDecimal("0.01"));
        BigDecimal lineaDisponibleSTCM = lineaDisponibleSTC.multiply(new BigDecimal("0.01"));
        BigDecimal saldoCatigoM = saldoCastigo.multiply(new BigDecimal("0.05"));

        return saldoCorporativoM
                .add(saldoGranEmpresaM)
                .add(saldoMedianaEmpresaM)
                .add(saldoPequenaEmpresaM)
                .add(saldoMicroEmpresaM)
                .add(saldoHipotecarioM)
                .add(saldoTCM)
                .add(saldoConsumo.subtract(saldoTC).multiply(new BigDecimal("0.029")))
                .add(lineaDisponibleTCM)
                .add(saldoAvalM)
                .add(lineaDisponibleSTCM)
                .add(saldoCatigoM)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
