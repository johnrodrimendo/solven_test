package com.affirm.common.util;

import com.affirm.common.model.catalog.Bank;

import java.util.List;

public class BankAccountValidator {

    /**
     * Validar CBU argentina
     * @param cbu Cuenta interbancaria
     * @param banks Lista de bancos disponibles
     * @return
     */
    public static boolean validateCBU(String cbu, List<Bank> banks, Bank bankSelected) throws Exception {
        String userBankCode = cbu.substring(0,3);
        if (!userBankCode.equals(bankSelected.getBankCode())) throw new Exception("El CBU no corresponde al Banco seleccionado");

        boolean bankCodeValid = false;
        for (Bank bank : banks) {
            if (bank.getBankCode().equals(userBankCode)) {
                bankCodeValid = true;
                break;
            }
        }

        if (!bankCodeValid) return false;

        int sumPart1 = 0;
        // Bank code
        sumPart1 += Character.getNumericValue(userBankCode.charAt(0)) * 7;
        sumPart1 += Character.getNumericValue(userBankCode.charAt(1));
        sumPart1 += Character.getNumericValue(userBankCode.charAt(2)) * 3;

        // Sucursal code
        sumPart1 += Character.getNumericValue(cbu.charAt(3)) * 9;
        sumPart1 += Character.getNumericValue(cbu.charAt(4)) * 7;
        sumPart1 += Character.getNumericValue(cbu.charAt(5));
        sumPart1 += Character.getNumericValue(cbu.charAt(6)) * 3;

        int digitVerificatorPart1 = Character.getNumericValue(cbu.charAt(7));

        int diff1 = 10 - (sumPart1 % 10);
        boolean validPart1 = (digitVerificatorPart1 != 0 && diff1 == digitVerificatorPart1) || (digitVerificatorPart1 == 0 && diff1 == 10);

        if (!validPart1) return false;

        int sumPart2 = 0;
        // Bank account
        sumPart2 += Character.getNumericValue(cbu.charAt(8)) * 3;
        sumPart2 += Character.getNumericValue(cbu.charAt(9)) * 9;
        sumPart2 += Character.getNumericValue(cbu.charAt(10)) * 7;
        sumPart2 += Character.getNumericValue(cbu.charAt(11));
        sumPart2 += Character.getNumericValue(cbu.charAt(12)) * 3;
        sumPart2 += Character.getNumericValue(cbu.charAt(13)) * 9;
        sumPart2 += Character.getNumericValue(cbu.charAt(14)) * 7;
        sumPart2 += Character.getNumericValue(cbu.charAt(15));
        sumPart2 += Character.getNumericValue(cbu.charAt(16)) * 3;
        sumPart2 += Character.getNumericValue(cbu.charAt(17)) * 9;
        sumPart2 += Character.getNumericValue(cbu.charAt(18)) * 7;
        sumPart2 += Character.getNumericValue(cbu.charAt(19));
        sumPart2 += Character.getNumericValue(cbu.charAt(20)) * 3;

        int digitVerificatorPart2 = Character.getNumericValue(cbu.charAt(21));
        int diff2 = 10 - (sumPart2 % 10);

        return (digitVerificatorPart2 != 0 && diff2 == digitVerificatorPart2) || (digitVerificatorPart2 == 0 && diff2 == 10); // Validate bank account
    }
}
