package com.affirm.rextie.model;

import java.io.Serializable;
import java.util.Date;

public class Rextie implements Serializable {
    public class CurrencyType{
        public final  static String USD = "USD";
        public final  static String PEN = "PEN";
    }
    private String sourceCurrency;
    private String targetCurrency;
    private double potentialSavings;
    private double sourceCurrencyAmount;
    private double targetCurrencyAmount;
    private Date quoteExpiringDate;
    private double sellRate;
    private double buyRate;

    public String getSourceCurrency() {
        return sourceCurrency;
    }

    public void setSourceCurrency(String sourceCurrency) {
        this.sourceCurrency = sourceCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public double getPotentialSavings() {
        return potentialSavings;
    }

    public void setPotentialSavings(double potentialSavings) {
        this.potentialSavings = potentialSavings;
    }

    public double getSourceCurrencyAmount() {
        String doubleAsString = String.valueOf(sourceCurrencyAmount);
        return Double.valueOf(doubleAsString.substring(doubleAsString.length()-1));
    }

    public void setSourceCurrencyAmount(double sourceCurrencyAmount) {
        this.sourceCurrencyAmount = sourceCurrencyAmount;
    }

    public double getTargetCurrencyAmount() {
        return targetCurrencyAmount;
    }

    public void setTargetCurrencyAmount(double targetCurrencyAmount) {
        this.targetCurrencyAmount = targetCurrencyAmount;
    }

    public Date getQuoteExpiringDate() {
        return quoteExpiringDate;
    }

    public void setQuoteExpiringDate(Date quoteExpiringDate) {
        this.quoteExpiringDate = quoteExpiringDate;
    }

    public double getSellRate() {
        return sellRate;
    }

    public void setSellRate(double sellRate) {
        this.sellRate = sellRate;
    }

    public double getBuyRate() {
        return buyRate;
    }

    public void setBuyRate(double buyRate) {
        this.buyRate = buyRate;
    }

    @Override
    public String toString() {
        return "Rextie{" +
                "sourceCurrency='" + sourceCurrency + '\'' +
                ", targetCurrency='" + targetCurrency + '\'' +
                ", potentialSavings=" + potentialSavings +
                ", sourceCurrencyAmount=" + sourceCurrencyAmount +
                ", targetCurrencyAmount=" + targetCurrencyAmount +
                ", quoteExpiringDate=" + quoteExpiringDate +
                ", sellRate=" + sellRate +
                ", buyRate=" + buyRate +
                '}';
    }
}
