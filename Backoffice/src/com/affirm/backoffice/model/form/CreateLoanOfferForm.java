package com.affirm.backoffice.model.form;

import com.affirm.common.model.catalog.Product;
import com.affirm.common.util.*;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jrodriguez on 21/07/16.
 */
public class CreateLoanOfferForm extends FormGeneric implements Serializable {

    private Integer ammount;
    private Integer installments;
    private Integer entityId;

    public CreateLoanOfferForm() {
        this.setValidator(new CreateLoanOfferFormValidator());
    }

    public class CreateLoanOfferFormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator ammount;
        public IntegerFieldValidator installments;
        public IntegerFieldValidator entityId;

        public CreateLoanOfferFormValidator() {
            addValidator(ammount = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_AMMOUNT_TRADITIONAL));
            addValidator(installments = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_INSTALLMENTS));
            addValidator(entityId = new IntegerFieldValidator(ValidatorUtil.ENTITY_ID));
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
            return CreateLoanOfferForm.this;
        }
    }

    public Integer getAmmount() {
        return ammount;
    }

    public void setAmmount(Integer ammount) {
        this.ammount = ammount;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }
}
