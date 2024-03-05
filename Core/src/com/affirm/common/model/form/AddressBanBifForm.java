package com.affirm.common.model.form;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.util.*;

import java.io.Serializable;

public class AddressBanBifForm extends FormGeneric implements Serializable {

    private String departamento;
    private String provincia;
    private String distrito;
    private String zoneType;
    private String zoneName;
    private String roadType;
    private String roadName;
    private String houseNumber;
    private String interior;
    private String reference;
    private Boolean acceptedTyC;
    private Character addressToSend;
    private Boolean homeAddress;
    private String ruc;
    private String companyName;

    public AddressBanBifForm() {
        this.setValidator(new AddressBanBifForm.Validator());
    }

    public String getRoadType() {
        return roadType;
    }

    public void setRoadType(String roadType) {
        this.roadType = roadType;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getInterior() {
        return interior;
    }

    public void setInterior(String interior) {
        this.interior = interior;
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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getZoneType() {
        return zoneType;
    }

    public void setZoneType(String zoneType) {
        this.zoneType = zoneType;
    }

    public Boolean getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Boolean homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Boolean getAcceptedTyC() {
        return acceptedTyC;
    }

    public void setAcceptedTyC(Boolean acceptedTyC) {
        this.acceptedTyC = acceptedTyC;
    }

    public Character getAddressToSend() {
        return addressToSend;
    }

    public void setAddressToSend(Character addressToSend) {
        this.addressToSend = addressToSend;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator departamento;
        public StringFieldValidator provincia;
        public StringFieldValidator distrito;
        public StringFieldValidator zoneType;
        public StringFieldValidator roadType;
        public StringFieldValidator roadName;
        public StringFieldValidator houseNumber;
        public StringFieldValidator interior;
        public StringFieldValidator reference;
        private BooleanFieldValidator acceptedTyC;
        private CharFieldValidator addressToSend;
        public StringFieldValidator zoneName;
        public StringFieldValidator ruc;
        public StringFieldValidator companyName;

        public Validator() {
            addValidator(roadType = new StringFieldValidator(ValidatorUtil.STREET_TYPE).setFieldName("Vía"));
            addValidator(roadName = new StringFieldValidator(ValidatorUtil.STREET_NAME).setFieldName("Dirección").setMinCharacters(3).setValidPattern(StringFieldValidator.LEAST_ONE_ALPHANUMERIC_CHARACTER));
            addValidator(houseNumber = new StringFieldValidator(ValidatorUtil.STREET_NUMBER).setFieldName("Nro"));
            addValidator(interior = new StringFieldValidator(ValidatorUtil.INTERIOR_NUMBER).setFieldName("Interior/Dept").setRequired(false));
            addValidator(ruc = new StringFieldValidator(ValidatorUtil.RUC).setFieldName("Ruc").setRequired(false));
            addValidator(companyName = new StringFieldValidator().setFieldName("Company Name").setRequired(false));
            addValidator(reference = new StringFieldValidator(ValidatorUtil.STREET_NAME).setFieldName("Referencia").setMinCharacters(3).setValidPattern(StringFieldValidator.LEAST_ONE_ALPHANUMERIC_CHARACTER));
            addValidator(departamento = new StringFieldValidator(ValidatorUtil.DEPARTMENT).setFieldName("Departamento"));
            addValidator(provincia = new StringFieldValidator(ValidatorUtil.PROVINCE).setFieldName("Provincia"));
            addValidator(distrito = new StringFieldValidator(ValidatorUtil.DISTRICT).setFieldName("Distrito"));
            addValidator(zoneType = new StringFieldValidator(ValidatorUtil.STREET_TYPE).setFieldName("Zona"));
            addValidator(acceptedTyC = new BooleanFieldValidator(ValidatorUtil.BANBIF_TYCS).setFieldName("Términos y condiciones").setRequiredErrorMsg("Necesitamos que aceptes las condiciones."));
            addValidator(addressToSend = new CharFieldValidator(ValidatorUtil.ADDRESS_TO_SEND).setFieldName("Dirección de entrega"));
            addValidator(zoneName = new StringFieldValidator(ValidatorUtil.STREET_NAME).setFieldName("Nombre de Zona").setRequired(true).setMinCharacters(3).setValidPattern(StringFieldValidator.LEAST_ONE_ALPHANUMERIC_CHARACTER));
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
            return AddressBanBifForm.this;
        }

        public void configValidator(char addressType) {
            if (addressType == 'H') {
                acceptedTyC.setRequired(false);
                addressToSend.setRequired(false);
            } else {
                acceptedTyC.setRequired(true).setFieldName("Términos y condiciones");
                addressToSend.setRequired(true).setFieldName("Dirección de entrega");
            }
        }

        public void configValidatorByEntity(Integer entityId,char addressType, Boolean showTermsAndCondition) {
            if(entityId != null){
                switch (entityId){
                    case Entity.BANBIF:
                        if (addressType == 'H') {
                            acceptedTyC.setRequired(false);
                            addressToSend.setRequired(false);
                        } else {
                            if(showTermsAndCondition != null){
                                if(showTermsAndCondition){
                                    acceptedTyC.setRequired(true).setFieldName("Términos y condiciones");
                                    addressToSend.setRequired(true).setFieldName("Dirección de entrega");
                                }else{
                                    acceptedTyC.setRequired(false);
                                    addressToSend.setRequired(false);
                                }
                            }else{
                                acceptedTyC.setRequired(true).setFieldName("Términos y condiciones");
                                addressToSend.setRequired(true).setFieldName("Dirección de entrega");
                            }
                        }
                        break;
                    default:
                        acceptedTyC.setRequired(false).setFieldName("Términos y condiciones");
                        addressToSend.setRequired(false).setFieldName("Dirección de entrega");
                }
            }
            else{
                acceptedTyC.setRequired(false);
                addressToSend.setRequired(false);
            }
        }
    }
}
