/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationStep2Form extends FormGeneric implements Serializable {

    private Double browserLatitude;
    private Double browserLongitude;
    private String department;
    private String province;
    private String district;
    private Integer streetType;
    private String streetName;
    private String streetNumber;
    private String interior;
    private String detail;
    private String fullAddress;

    public LoanApplicationStep2Form() {
        this.setValidator(new LoanApplicationStep2FormValidator());
    }

    public class LoanApplicationStep2FormValidator extends FormValidator implements Serializable {

        public StringFieldValidator department;
        public StringFieldValidator province;
        public StringFieldValidator district;
        public IntegerFieldValidator streetType;
        public StringFieldValidator streetName;
        public StringFieldValidator streetNumber;
        public StringFieldValidator interior;
        public StringFieldValidator detail;
        public StringFieldValidator fullAddress;

        public LoanApplicationStep2FormValidator() {
            addValidator(department = new StringFieldValidator(ValidatorUtil.DEPARTMENT));
            addValidator(province = new StringFieldValidator(ValidatorUtil.PROVINCE));
            addValidator(district = new StringFieldValidator(ValidatorUtil.DISTRICT));
            addValidator(streetType = new IntegerFieldValidator(ValidatorUtil.STREET_TYPE_ID));
            addValidator(streetName = new StringFieldValidator(ValidatorUtil.STREET_NAME));
            addValidator(streetNumber = new StringFieldValidator(ValidatorUtil.STREET_NUMBER));
            addValidator(interior = new StringFieldValidator(ValidatorUtil.INTERIOR));
            addValidator(detail = new StringFieldValidator(ValidatorUtil.DETAIL));
            addValidator(fullAddress = new StringFieldValidator(ValidatorUtil.FULL_ADDRESS));
        }

        @Override
        protected void setDynamicValidations() {
//            streetName.setRequired(LoanApplicationStep2Form.this.streetType != null
//                    && LoanApplicationStep2Form.this.streetType != StreetType.AAHH);
//            detail.setRequired(LoanApplicationStep2Form.this.streetType != null
//                    && LoanApplicationStep2Form.this.streetType == StreetType.AAHH);
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return LoanApplicationStep2Form.this;
        }

    }

    public Double getBrowserLatitude() {
        return browserLatitude;
    }

    public void setBrowserLatitude(Double browserLatitude) {
        this.browserLatitude = browserLatitude;
    }

    public Double getBrowserLongitude() {
        return browserLongitude;
    }

    public void setBrowserLongitude(Double browserLongitude) {
        this.browserLongitude = browserLongitude;
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

    public Integer getStreetType() {
        return streetType;
    }

    public void setStreetType(Integer streetType) {
        this.streetType = streetType;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getInterior() {
        return interior;
    }

    public void setInterior(String interior) {
        this.interior = interior;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }
}
