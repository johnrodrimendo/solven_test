package com.affirm.tests.app.orphan;

import com.affirm.common.model.catalog.Bank;
import com.affirm.common.util.BankAccountValidator;

import java.util.ArrayList;
import java.util.List;

public class TestBankAccountValidator {

    private static void testCBU(String cbu, List<Bank> banks, Bank bankSelected) throws Exception {
        System.out.println(String.format("Testeando CBU: %s", cbu));
        System.out.println(BankAccountValidator.validateCBU(cbu, banks, bankSelected));
    }

    private static void testCBU() throws Exception {
        List<Bank> banks = new ArrayList<>();
        Bank bank285 = new Bank();
        bank285.setBankCode("285");
        banks.add(bank285);

        // Wikipedia test - 2/2/2018
        testCBU("2850590940090418135201", banks, bank285); // Must be true
        // Problem detected - 5/2/2018
        try {
            testCBU("0270097720028113220012", banks, bank285); // Must be false
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        Bank bank027 = new Bank();
        bank027.setBankCode("027");
        banks.add(bank027);
        // Right way of problem detected - 5/2/2018
        testCBU("0270097720028113220012", banks, bank027); // Must be true

        // Play with wikipedia cbu
        testCBU("2851590940090418135201", banks, bank285); // Must be false
        testCBU("2852590940090418135201", banks, bank285); // Must be false
        testCBU("2850290940090418135201", banks, bank285); // Must be false
    }


    public static void main(String ... vargs) throws Exception {
        // TODO: Change to JUnit
        testCBU();
    }
}
