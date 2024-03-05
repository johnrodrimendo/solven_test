package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;

import java.io.Serializable;

public class Question125Form extends FormGeneric implements Serializable {

    private Integer product;
    private Integer entityProductParam;

    public Question125Form() {
    }

    public Integer getProduct() {
        return product;
    }

    public void setProduct(Integer product) {
        this.product = product;
    }

    public Integer getEntityProductParam() {
        return entityProductParam;
    }

    public void setEntityProductParam(Integer entityProductParam) {
        this.entityProductParam = entityProductParam;
    }
}