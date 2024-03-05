/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationBlock3Form extends FormGeneric {

    private Integer partnerDocType;
    private String partnerDocNumber;

    public LoanApplicationBlock3Form() {
        this.setValidator(new LoanApplicationBlock3FormValidator());
    }

    public class LoanApplicationBlock3FormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator partnerDocType;
        public StringFieldValidator partnerDocNumber;

        public LoanApplicationBlock3FormValidator() {
            addValidator(partnerDocType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID).setRequiredErrorMsg("validation.process.docType"));
            addValidator(partnerDocNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI).setRequiredErrorMsg("validation.process.docNumber"));
        }

        @Override
        protected void setDynamicValidations() {
            if (LoanApplicationBlock3Form.this.partnerDocType == IdentityDocumentType.DNI) {
                partnerDocNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
            } else if (LoanApplicationBlock3Form.this.partnerDocType == IdentityDocumentType.CE) {
                partnerDocNumber.update(ValidatorUtil.DOC_NUMBER_CE);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return LoanApplicationBlock3Form.this;
        }
    }

    public Integer getPartnerDocType() {
        return partnerDocType;
    }

    public void setPartnerDocType(Integer partnerDocType) {
        this.partnerDocType = partnerDocType;
    }

    public String getPartnerDocNumber() {
        return partnerDocNumber;
    }

    public void setPartnerDocNumber(String partnerDocNumber) {
        this.partnerDocNumber = partnerDocNumber;
    }
}
