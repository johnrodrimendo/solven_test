package com.affirm.common.model.rcc;

public class KeyRcc {

    private String debtCode;
    private String personType;

    public KeyRcc(String debtCode, String personType) {
        this.debtCode = debtCode;
        this.personType = personType;
    }

    public String getDebtCode() {
        return debtCode;
    }

    public void setDebtCode(String debtCode) {
        this.debtCode = debtCode;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }
}
