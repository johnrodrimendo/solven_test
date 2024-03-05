package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;

import java.io.Serializable;

public class Question50Form extends FormGeneric implements Serializable {

    private Integer selectedOffer;
    private String selectedColor;

    public Integer getSelectedOffer() {
        return selectedOffer;
    }

    public void setSelectedOffer(Integer selectedOffer) {
        this.selectedOffer = selectedOffer;
    }

    public String getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
    }
}