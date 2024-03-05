package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question6Form extends FormGeneric implements Serializable {

    private Integer amount;
    private Integer installments;

    public Question6Form() {
        this.setValidator(new Question6Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator amount;
        public IntegerFieldValidator installments;

        public Validator() {
            addValidator(amount = new IntegerFieldValidator(ValidatorUtil.SELF_EVALUATION_AMOUNT).setMaxValueErrorMsg("validation.int.maxValueMoney").setMinValueErrorMsg("validation.int.minValueMoney").setFieldName("monto"));
            addValidator(installments = new IntegerFieldValidator(ValidatorUtil.SELF_EVALUATION_INSTALLMENTS).setFieldName("plazo"));
        }

        @Override
        protected void setDynamicValidations() {
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question6Form.this;
        }
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }
}