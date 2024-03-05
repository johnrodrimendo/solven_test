package com.affirm.common.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;
import java.util.Calendar;

public class Question121Form extends FormGeneric implements Serializable {

    private Integer brand;
    private String model;
    private Integer year;
    private Integer mileage;
    private String plate;

    public Question121Form() {
        this.setValidator(new Question121Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        private IntegerFieldValidator brand;
        private StringFieldValidator model;
        private IntegerFieldValidator year;
        private IntegerFieldValidator mileage;
        private StringFieldValidator plate;

        public Validator() {
            Calendar yearCarRange = Calendar.getInstance();
            addValidator(brand = new IntegerFieldValidator().setRequired(true).setFieldName("Marca"));
            addValidator(model = new StringFieldValidator().setRequired(true).setFieldName("Modelo"));
            addValidator(year = new IntegerFieldValidator().setRequired(true).setMinValue(yearCarRange.get(Calendar.YEAR) - 60).setMaxValue(yearCarRange.get(Calendar.YEAR)).setFieldName("Año"));
            addValidator(mileage = new IntegerFieldValidator().setRequired(true).setFieldName("Kilometraje"));
            addValidator(plate = new StringFieldValidator(ValidatorUtil.VEHICLE_PLATE).setFieldName("Placa"));
        }

        @Override
        protected void setDynamicValidations() { }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question121Form.this;
        }
    }

    public Integer getBrand() { return brand; }

    public void setBrand(Integer brand) { this.brand = brand; }

    public String getModel() { return model; }

    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }

    public void setYear(Integer year) { this.year = year; }

    public Integer getMileage() { return mileage; }

    public void setMileage(Integer mileage) { this.mileage = mileage; }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }
}
