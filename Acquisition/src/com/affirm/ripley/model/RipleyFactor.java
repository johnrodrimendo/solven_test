package com.affirm.ripley.model;

/**
 * Created by dev5 on 25/05/17.
 */
public class RipleyFactor {

    private int plazo;
    private double factor;
    private double amount;

    public RipleyFactor(int plazo, double factor){
        this.plazo = plazo;
        this.factor = factor;
    }

    public RipleyFactor(int plazo, double factor, double amount){
        this.plazo = plazo;
        this.factor = factor;
        this.amount = amount*factor;
    }

    public int getPlazo() {
        return plazo;
    }

    public void setPlazo(int plazo) {
        this.plazo = plazo;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
