package com.affirm.common.model.form;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.service.question.QuestionFlowService;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question53Form extends FormGeneric implements Serializable {

    private String areaCode;
    private String phoneNumber;
    private Integer countryId;

    public Question53Form() {
        this.setValidator(new Question53Form.Validator());
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator areaCode;
        public StringFieldValidator phoneNumber;

        public Validator() {
            addValidator(areaCode = new StringFieldValidator(ValidatorUtil.ARGENTINA_PHONE_AREA_CODE).setFieldName("Cod. Area"));
            addValidator(phoneNumber = new StringFieldValidator(ValidatorUtil.CELLPHONE_NUMBER).setFieldName("Nro. Celular"));
        }

        @Override
        protected void setDynamicValidations() {
            switch (Question53Form.this.countryId) {
                case CountryParam.COUNTRY_PERU: {
                    areaCode.setRequired(false);
                    phoneNumber.setMaxCharacters(10);
                    break;
                }
                case CountryParam.COUNTRY_ARGENTINA: {
                    int areaCodeValue = Question53Form.this.areaCode == null ? 0 : Question53Form.this.areaCode.length();
                    phoneNumber.setMaxCharacters(11 - areaCodeValue);
                    phoneNumber.setMinCharacters(11 - areaCodeValue);

                    if(Question53Form.this.areaCode != null && !Question53Form.this.areaCode.isEmpty()) {
                        phoneNumber.setRequired(true);
                    } else if(Question53Form.this.phoneNumber != null && !Question53Form.this.phoneNumber.isEmpty()) {
                        areaCode.setRequired(true);
                    }
                    break;
                }
                case CountryParam.COUNTRY_COLOMBIA: {
                    areaCode.setRequired(false);
                    phoneNumber.setRequired(true);

                    phoneNumber.setMaxCharacters(10);
                    phoneNumber.setMinCharacters(10);

                    break;
                }
            }

        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question53Form.this;
        }

        public void configValidator(Integer countryId, QuestionFlowService.Type flowType) {
            Question53Form.this.countryId = countryId;

            switch (countryId) {
                case CountryParam.COUNTRY_PERU: {
                    phoneNumber.update(ValidatorUtil.CELLPHONE_NUMBER);
                    areaCode.setRequired(false);
                    break;
                }
                case CountryParam.COUNTRY_ARGENTINA: {
                    phoneNumber.update(ValidatorUtil.CELLPHONE_NUMBER_ARGENTINA.setMinCharacters(9).setMaxCharacters(11));
                    areaCode.update(ValidatorUtil.ARGENTINA_PHONE_AREA_CODE);
                    break;
                }
                case CountryParam.COUNTRY_COLOMBIA: {
                    phoneNumber.update(ValidatorUtil.CELLPHONE_NUMBER_COLOMBIA);
                    areaCode.setRequired(false);
                }
            }

            if (flowType == QuestionFlowService.Type.LOANAPPLICATION) {
                phoneNumber.setRequired(true);
            } else if (flowType == QuestionFlowService.Type.SELFEVALUATION) {
                phoneNumber.setRequired(false);
                areaCode.setRequired(false);
            }
        }
    }


    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
}