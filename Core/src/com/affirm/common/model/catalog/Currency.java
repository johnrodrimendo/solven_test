package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by dev5 on 09/05/17.
 */
public class Currency implements Serializable {

    public static final int PEN = 0;
    public static final int USD = 1;
    public static final int ARS = 2;
    public static final int COP = 3;

    public static final String USD_CURRENCY = "USD";
    public static final String USD_SYMBOL = "$";
    public static final String PEN_SYMBOL = "S/";
    public static final String COP_SYMBOL = "$";

    private Integer id;
    private String currency;
    private String symbol;
    private String separator;
    private String decimalSeparator;
    private Double exchangeRate;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "currency_id", null));
        setCurrency(JsonUtil.getStringFromJson(json, "currency", null));
        setSymbol(JsonUtil.getStringFromJson(json, "symbol", null));
        setSeparator(JsonUtil.getStringFromJson(json, "separator", null));
        setDecimalSeparator(JsonUtil.getStringFromJson(json, "decimal_separator", null));
        setExchangeRate(JsonUtil.getDoubleFromJson(json, "exchange_rate", 0.0));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getDecimalSeparator() {
        return decimalSeparator;
    }

    public void setDecimalSeparator(String decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangRate) {
        this.exchangeRate = exchangRate;
    }
}
