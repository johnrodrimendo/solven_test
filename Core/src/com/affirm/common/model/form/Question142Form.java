package com.affirm.common.model.form;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.util.*;

import java.io.Serializable;

public class Question142Form extends FormGeneric implements Serializable {

    private String roadName;
    private String departamento;
    private String provincia;
    private String distrito;
    private String reference;
    private Integer housingType;

    public Question142Form() {
        this.setValidator(new Question142Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator roadName;
        public StringFieldValidator departamento;
        public StringFieldValidator provincia;
        public StringFieldValidator distrito;
        public IntegerFieldValidator housingType;
        public StringFieldValidator reference;

        public Validator() {
            addValidator(roadName = new StringFieldValidator(ValidatorUtil.STREET_NAME).setFieldName("Dirección").setMinCharacters(3));
            addValidator(departamento = new StringFieldValidator(ValidatorUtil.DEPARTMENT).setFieldName("Departamento"));
            addValidator(provincia = new StringFieldValidator(ValidatorUtil.PROVINCE).setFieldName("Provincia"));
            addValidator(distrito = new StringFieldValidator(ValidatorUtil.DISTRICT).setFieldName("Distrito"));
            addValidator(housingType = new IntegerFieldValidator().setRequired(true).setFieldName("Tipo de Residencia"));
            addValidator(reference = new StringFieldValidator().setFieldName("Referencia"));
        }

        @Override
        protected void setDynamicValidations() {
        }

        public void configValidator(Integer countryId) {
            if (CountryParam.COUNTRY_COLOMBIA == countryId) {
                provincia.setFieldName("Municipio");
                distrito.setFieldName("Barrio");
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question142Form.this;
        }
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public Integer getHousingType() {
        return housingType;
    }

    public void setHousingType(Integer housingType) {
        this.housingType = housingType;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
