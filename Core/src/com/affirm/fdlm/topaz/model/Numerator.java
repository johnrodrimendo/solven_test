package com.affirm.fdlm.topaz.model;

import com.google.gson.annotations.SerializedName;

public class Numerator {

    @SerializedName("valor")
    private Long value;

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
