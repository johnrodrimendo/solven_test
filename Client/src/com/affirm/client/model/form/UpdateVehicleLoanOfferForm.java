package com.affirm.client.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

/**
 * Created by jrodriguez on 31/05/16.
 */
public class UpdateVehicleLoanOfferForm extends FormGeneric implements Serializable {

    private Integer downPayment;

    public UpdateVehicleLoanOfferForm() {
        this.setValidator(new Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator downPayment;

        public Validator() {
            addValidator(downPayment = new IntegerFieldValidator().setRequired(true).setRestricted(true));
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
            return UpdateVehicleLoanOfferForm.this;
        }
    }

    public Integer getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(Integer downPayment) {
        this.downPayment = downPayment;
    }

}
