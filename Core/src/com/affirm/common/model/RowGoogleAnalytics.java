package com.affirm.common.model;

import java.util.HashMap;

public class RowGoogleAnalytics {
    private String name;
    private HashMap<String, RowGoogleAnalyticsDetail> values;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, RowGoogleAnalyticsDetail> getValues() {
        return values;
    }

    public void setValues(HashMap<String, RowGoogleAnalyticsDetail> values) {
        this.values = values;
    }
}