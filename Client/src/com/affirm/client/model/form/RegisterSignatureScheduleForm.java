package com.affirm.client.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * Created by john on 16/08/17.
 */
public class RegisterSignatureScheduleForm extends FormGeneric implements Serializable {

    private String scheduleDate;
    private Integer scheduleHour;
    private String address;
    private Integer locationId;

    public RegisterSignatureScheduleForm() {
        this.setValidator(new Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator scheduleDate;
        public IntegerFieldValidator scheduleHour;
        public StringFieldValidator address;
        public IntegerFieldValidator locationId;

        public Validator() {
            addValidator(scheduleDate = new StringFieldValidator().setRequired(true).setValidRegex(DateFieldValidator.REGEXP_DDMMYYYY_SLASH_DATE));
            addValidator(scheduleHour = new IntegerFieldValidator().setRequired(true));
            addValidator(address = new StringFieldValidator().setRequired(false).setMaxCharacters(4).setMinCharacters(0));
            addValidator(locationId = new IntegerFieldValidator().setRequired(true));
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
            return RegisterSignatureScheduleForm.this;
        }
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public Integer getScheduleHour() {
        return scheduleHour;
    }

    public void setScheduleHour(Integer scheduleHour) {
        this.scheduleHour = scheduleHour;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
}
