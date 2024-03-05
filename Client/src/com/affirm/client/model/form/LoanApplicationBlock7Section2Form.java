/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationBlock7Section2Form extends FormGeneric {

    private Integer activityType;

    public LoanApplicationBlock7Section2Form() {
        this.setValidator(new LoanApplicationBlock7Section2FormValidator());
    }

    public class LoanApplicationBlock7Section2FormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator activityType;

        public LoanApplicationBlock7Section2FormValidator() {
            addValidator(activityType = new IntegerFieldValidator(ValidatorUtil.ACTIVITY_TYPE_ID).setRequiredErrorMsg("validation.activityJob.activityType"));
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
            return LoanApplicationBlock7Section2Form.this;
        }
    }

    public Integer getActivityType() {
        return activityType;
    }

    public void setActivityType(Integer activityType) {
        this.activityType = activityType;
    }
}
