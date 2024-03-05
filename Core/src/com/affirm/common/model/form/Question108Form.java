package com.affirm.common.model.form;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.*;

import java.io.Serializable;

public class Question108Form extends FormGeneric implements Serializable {
    private Integer partnerDocType;
    private String partnerDocNumber;

    private String partnerName;
    private String partnerFirstSurname;
    private String partnerLastSurname;
    private Integer partnerDay;
    private Integer partnerMonth;
    private Character gender;
    private Boolean noIdcheckedBox;
    private Integer entityBrandingId;
    private Integer loanApplicationId;

    public Question108Form() {
        this.setValidator(new Question108Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator partnerDocType;
        public StringFieldValidator partnerDocNumber;
        public StringFieldValidator partnerName;
        public StringFieldValidator partnerFirstSurname;
        public StringFieldValidator partnerLastSurname;
        public IntegerFieldValidator partnerDay;
        public IntegerFieldValidator partnerMonth;
        public CharFieldValidator gender;

        public Validator() {
            addValidator(partnerDocType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID).setRequired(true).setRestricted(true));
            addValidator(partnerDocNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI).setRequired(true).setRestricted(true));
            addValidator(partnerName = new StringFieldValidator(ValidatorUtil.FULLNAME).setRequired(true).setRestricted(true));
            addValidator(partnerFirstSurname = new StringFieldValidator(ValidatorUtil.FIRST_SURNAME).setRequired(true).setRestricted(true));
            addValidator(partnerLastSurname = new StringFieldValidator(ValidatorUtil.LAST_SURNAME).setRequired(true).setRestricted(true));
            addValidator(partnerDay = new IntegerFieldValidator(ValidatorUtil.BIRTHDATE_DAY).setRequired(true).setRestricted(true));
            addValidator(partnerMonth = new IntegerFieldValidator(ValidatorUtil.BIRTHDATE_MONTH).setRequired(true).setRestricted(true));
            addValidator(gender = new CharFieldValidator().setRequired(true));

        }

        @Override
        protected void setDynamicValidations() {
            if (Question108Form.this.partnerDocType == IdentityDocumentType.DNI) {
                partnerDocNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
            } else if (Question108Form.this.partnerDocType == IdentityDocumentType.CE) {
                partnerDocNumber.update(ValidatorUtil.DOC_NUMBER_CE);
            } else if (Question108Form.this.partnerDocType == IdentityDocumentType.CDI) {
                partnerDocNumber.update(ValidatorUtil.DOC_NUMBER_CDI);
            } else if (Question108Form.this.partnerDocType == IdentityDocumentType.CUIL) {
                partnerDocNumber.update(ValidatorUtil.DOC_NUMBER_CUIL);
            } else if (Question108Form.this.partnerDocType == IdentityDocumentType.CUIT) {
                partnerDocNumber.update(ValidatorUtil.DOC_NUMBER_CUIT);
            }

            if (Question108Form.this.noIdcheckedBox == null || !Question108Form.this.noIdcheckedBox) {
                partnerDocType.setRequired(true);
                partnerDocNumber.setRequired(true);
                partnerName.setRequired(false);
                partnerFirstSurname.setRequired(false);
                partnerLastSurname.setRequired(false);
                partnerDay.setRequired(false);
                partnerMonth.setRequired(false);
                gender.setRequired(false);
            } else {
                partnerDocType.setRequired(false);
                partnerDocNumber.setRequired(false);
                partnerName.setRequired(true);
                partnerFirstSurname.setRequired(true);
                partnerLastSurname.setRequired(true);
                partnerLastSurname.setRequired(true);
                if(entityBrandingId == null || entityBrandingId != Entity.AZTECA){
                    partnerDay.setRequired(true);
                    partnerMonth.setRequired(true);
                }
                gender.setRequired(true);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question108Form.this;
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

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerFirstSurname() {
        return partnerFirstSurname;
    }

    public void setPartnerFirstSurname(String partnerFirstSurname) {
        this.partnerFirstSurname = partnerFirstSurname;
    }

    public String getPartnerLastSurname() {
        return partnerLastSurname;
    }

    public void setPartnerLastSurname(String partnerLastSurname) {
        this.partnerLastSurname = partnerLastSurname;
    }

    public Integer getPartnerDay() {
        return partnerDay;
    }

    public void setPartnerDay(Integer partnerDay) {
        this.partnerDay = partnerDay;
    }

    public Integer getPartnerMonth() {
        return partnerMonth;
    }

    public void setPartnerMonth(Integer partnerMonth) {
        this.partnerMonth = partnerMonth;
    }

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    public Boolean getNoIdcheckedBox() {
        return noIdcheckedBox;
    }

    public void setNoIdcheckedBox(Boolean noIdcheckedBox) {
        this.noIdcheckedBox = noIdcheckedBox;
    }

    public Integer getEntityBrandingId() {
        return entityBrandingId;
    }

    public void setEntityBrandingId(Integer entityBrandingId) {
        this.entityBrandingId = entityBrandingId;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }
}

