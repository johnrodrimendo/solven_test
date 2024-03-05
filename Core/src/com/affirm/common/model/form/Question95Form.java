package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;

import java.io.Serializable;

public class Question95Form extends FormGeneric implements Serializable {

    private String completed;

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }
}