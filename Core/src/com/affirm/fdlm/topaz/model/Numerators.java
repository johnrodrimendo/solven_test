package com.affirm.fdlm.topaz.model;

import com.google.gson.annotations.SerializedName;

public class Numerators {

    @SerializedName("GetNumerador")
    private Numerator numerator;

    public Numerator getNumerator() {
        return numerator;
    }

    public void setNumerator(Numerator numerator) {
        this.numerator = numerator;
    }
}
