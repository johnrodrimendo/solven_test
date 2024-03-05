/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationBlock6Section2Form extends FormGeneric {

    private String department;
    private String province;
    private String district;

    public LoanApplicationBlock6Section2Form() {
        this.setValidator(new LoanApplicationBlock6Section2FormValidator());
    }

    public class LoanApplicationBlock6Section2FormValidator extends FormValidator implements Serializable {

        public StringFieldValidator department;
        public StringFieldValidator province;
        public StringFieldValidator district;

        public LoanApplicationBlock6Section2FormValidator() {
            addValidator(department = new StringFieldValidator(ValidatorUtil.DEPARTMENT).setRequiredErrorMsg("validation.address.department"));
            addValidator(province = new StringFieldValidator(ValidatorUtil.PROVINCE).setRequiredErrorMsg("validation.address.province"));
            addValidator(district = new StringFieldValidator(ValidatorUtil.DISTRICT).setRequiredErrorMsg("validation.address.district"));
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
            return LoanApplicationBlock6Section2Form.this;
        }
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
