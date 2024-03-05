/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationBlock7Section13Form extends FormGeneric {

    private String housekeeperTime;
    private Integer housekeeperNetIncome;

    public LoanApplicationBlock7Section13Form() {
        this.setValidator(new LoanApplicationBlock7Section13FormValidator());
    }

    public class LoanApplicationBlock7Section13FormValidator extends FormValidator implements Serializable {

        public StringFieldValidator housekeeperTime;
        public IntegerFieldValidator housekeeperNetIncome;

        public LoanApplicationBlock7Section13FormValidator() {
            addValidator(housekeeperTime = new StringFieldValidator(ValidatorUtil.ACTIVITY_TIME).setRequiredErrorMsg("validation.activityJob.activityTime"));
            addValidator(housekeeperNetIncome = new IntegerFieldValidator(ValidatorUtil.NET_INCOME).setRequiredErrorMsg("validation.activityJob.netIncome"));
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
            return LoanApplicationBlock7Section13Form.this;
        }
    }

    public String getHousekeeperTime() {
        return housekeeperTime;
    }

    public void setHousekeeperTime(String housekeeperTime) {
        this.housekeeperTime = housekeeperTime;
    }

    public Integer getHousekeeperNetIncome() {
        return housekeeperNetIncome;
    }

    public void setHousekeeperNetIncome(Integer housekeeperNetIncome) {
        this.housekeeperNetIncome = housekeeperNetIncome;
    }
}
