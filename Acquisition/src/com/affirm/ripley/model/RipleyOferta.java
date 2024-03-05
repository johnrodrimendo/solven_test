package com.affirm.ripley.model;

/**
 * Created by dev5 on 25/05/17.
 */
public class RipleyOferta {

    private int plazo;
    private double amount;
    private double tasa;

    public RipleyOferta(int plazo, double amount, double tasa){
        this.plazo = plazo;
        this.amount = amount;
        if(amount <= 4000) this.tasa = Math.max(tasa, 1.99);
        else this.tasa = tasa;
    }

    @Override
    public String toString() {
        return "RipleyOferta {" + " plazo = " + plazo + ", amount = " + amount + ", tasa = " + getTasa() + '}';
    }

    public boolean equals(RipleyOferta ofertaRipley){
        if(this.amount == ofertaRipley.getAmount() && this.plazo == ofertaRipley.getPlazo())
            return true;
        return false;
    }

    public int getPlazo() {
        return plazo;
    }

    public void setPlazo(int plazo) {
        this.plazo = plazo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getTasa() {
        return tasa;
    }

    public void setTasa(double tasa) {
        this.tasa = tasa;
    }
}
