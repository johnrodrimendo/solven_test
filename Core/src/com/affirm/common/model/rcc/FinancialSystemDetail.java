package com.affirm.common.model.rcc;


public class FinancialSystemDetail {

    private String shortName;
    private String debtType;
    private double reportedDebtSoles;
    private int arrears;
    private String calification;
    private String personType;
    private String classification;

    public FinancialSystemDetail() {

    }

    public FinancialSystemDetail(Synthesized synthesized, String debtType, double sal) {
        setShortName(synthesized.getEntityShortName());
        if (synthesized.getDiasAtraso() != null)
            setArrears(synthesized.getDiasAtraso().intValue());
        setReportedDebtSoles(sal);
        setDebtType(debtType);

        if (synthesized.getSaldoJudicial() > 0) {
            setClassification("JUDICIAL");
        } else if (synthesized.getSaldoCastigo() > 0) {
            setClassification("CASTIGO");
        } else if (synthesized.getSaldoVencido() > 0) {
            setClassification("VENCIDO");
        } else {
            setClassification("VIGENTE");
        }

        if (synthesized.getCalificacion() != null) {
            switch (synthesized.getCalificacion()) {
                case 0:
                    setCalification(RccResponse.CALIFICATION_NORMAL);
                    break;
                case 1:
                    setCalification(RccResponse.CALIFICATION_CPP);
                    break;
                case 2:
                    setCalification(RccResponse.CALIFICATION_DEFICIENTE);
                    break;
                case 3:
                    setCalification(RccResponse.CALIFICATION_DUDOSO);
                    break;
                case 4:
                    setCalification(RccResponse.CALIFICATION_PERDIDA);
                    break;
                default:
                    setCalification(RccResponse.CALIFICATION_SIN_CALIFICACION);
            }
        } else {
            setCalification(RccResponse.CALIFICATION_SIN_CALIFICACION);
        }
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDebtType() {
        return debtType;
    }

    public void setDebtType(String debtType) {
        this.debtType = debtType;
    }

    public double getReportedDebtSoles() {
        return reportedDebtSoles;
    }

    public void setReportedDebtSoles(double reportedDebtSoles) {
        this.reportedDebtSoles = reportedDebtSoles;
    }

    public int getArrears() {
        return arrears;
    }

    public void setArrears(int arrears) {
        this.arrears = arrears;
    }

    public String getCalification() {
        return calification;
    }

    public void setCalification(String calification) {
        this.calification = calification;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }
}
