package com.affirm.common.model.form;

import com.affirm.acceso.model.Direccion;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.transactional.PersonContactInformation;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.util.*;

import java.io.Serializable;

public class AddressForm extends FormGeneric implements Serializable {

    private String roadType;
    private String roadName;
    private String houseNumber;
    private Boolean withoutNumber;
    private String floor;
    private String interior;
    private String postalCode;
    private String manzana;
    private String lote;
    private String departamento;
    private String provincia;
    private String distrito;
    private String reference;
    private String zoneType;
    private String zoneName;
    private Boolean homeAddress;

    public void fillData(Direccion direccion) {
        if (direccion != null) {
            setRoadType(direccion.getTipoVia() != null ? direccion.getTipoVia() + "" : null);
            setRoadName(direccion.getNombreVia());
            setHouseNumber(direccion.getNumeroVia());
            setInterior(direccion.getNumeroInterior());
            setManzana(direccion.getManzana());
            setLote(direccion.getLote());
            setReference(direccion.getReferencia());
            setZoneType(direccion.getTipoZona() != null ? direccion.getTipoZona() + "" : null);
            setZoneName(direccion.getNombreZona());
            setDistrito(direccion.getDistrictId() != null ? direccion.getDistrictId() + "" : null);
            setFloor(direccion.getFloor() != null ? direccion.getFloor() + "" : null);
            setWithoutNumber(direccion.getWithoutNumber());
            setPostalCode(direccion.getPostalCode());
        }
    }

    public void fillData(PersonOcupationalInformation ocupation) {
        if (ocupation != null) {
            setRoadName(ocupation.getAddress());
            setDepartamento(ocupation.getAddressUbigeo() != null ? ocupation.getAddressUbigeo().getDepartment().getId() : null);
            setProvincia(ocupation.getAddressUbigeo() != null ? ocupation.getAddressUbigeo().getProvince().getId() : null);
            setDistrito(ocupation.getAddressUbigeo() != null ? ocupation.getAddressUbigeo().getDistrict().getId() : null);
        }
    }

    public void fillData(PersonContactInformation contactInformation) {
        if (contactInformation != null) {
            setRoadName(contactInformation.getAddressStreetName());
            setDepartamento(contactInformation.getAddressUbigeo() != null ? contactInformation.getAddressUbigeo().getDepartment().getId() : null);
            setProvincia(contactInformation.getAddressUbigeo() != null ? contactInformation.getAddressUbigeo().getProvince().getId() : null);
            setDistrito(contactInformation.getAddressUbigeo() != null ? contactInformation.getAddressUbigeo().getDistrict().getId() : null);
        }
    }

    public AddressForm() {
        this.setValidator(new AddressForm.Validator());
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

    public Boolean getWithoutNumber() {
        return withoutNumber;
    }

    public void setWithoutNumber(Boolean withoutNumber) {
        this.withoutNumber = withoutNumber;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getInterior() {
        return interior;
    }

    public void setInterior(String interior) {
        this.interior = interior;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getManzana() {
        return manzana;
    }

    public void setManzana(String manzana) {
        this.manzana = manzana;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
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

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public Boolean getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Boolean homeAddress) {
        this.homeAddress = homeAddress;
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator roadType;
        public StringFieldValidator roadName;
        public StringFieldValidator houseNumber;
        public BooleanFieldValidator withoutNumber;
        public StringFieldValidator floor;
        public StringFieldValidator interior;
        public StringFieldValidator postalCode;
        public StringFieldValidator reference;
        public StringFieldValidator manzana;
        public StringFieldValidator lote;
        public StringFieldValidator departamento;
        public StringFieldValidator provincia;
        public StringFieldValidator distrito;
        public StringFieldValidator zoneType;
        public StringFieldValidator zoneName;

        public Validator() {
            addValidator(roadType = new StringFieldValidator(ValidatorUtil.STREET_TYPE));
            addValidator(roadName = new StringFieldValidator(ValidatorUtil.STREET_NAME).setFieldName("Dirección"));
            addValidator(houseNumber = new StringFieldValidator(ValidatorUtil.STREET_NUMBER).setFieldName("Altura"));
            addValidator(withoutNumber = new BooleanFieldValidator());
            addValidator(floor = new StringFieldValidator(ValidatorUtil.STREET_NUMBER).setFieldName("Piso").setRequired(false));
            addValidator(interior = new StringFieldValidator(ValidatorUtil.INTERIOR_NUMBER));
            addValidator(postalCode = new StringFieldValidator(ValidatorUtil.AR_POSTAL_CODE).setFieldName("Código postal"));
            addValidator(reference = new StringFieldValidator(ValidatorUtil.STREET_NAME).setFieldName("Referencia").setRequired(false));
            addValidator(manzana = new StringFieldValidator(ValidatorUtil.MANZANA));
            addValidator(lote = new StringFieldValidator(ValidatorUtil.LOTE));
            addValidator(departamento = new StringFieldValidator(ValidatorUtil.DEPARTMENT).setFieldName("Departamento"));
            addValidator(provincia = new StringFieldValidator(ValidatorUtil.PROVINCE).setFieldName("Provincia"));
            addValidator(distrito = new StringFieldValidator(ValidatorUtil.DISTRICT).setFieldName("Distrito"));
            addValidator(zoneType = new StringFieldValidator(ValidatorUtil.STREET_TYPE));
            addValidator(zoneName = new StringFieldValidator(ValidatorUtil.STREET_NAME));
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
            return AddressForm.this;
        }

        public void configValidator(Integer countryId, Boolean withoutNumber) {
            if (countryId.equals(CountryParam.COUNTRY_PERU)) {
                roadType.setRequired(false);
                roadName.setRequired(true);
                houseNumber.setRequired(false);
                floor.setRequired(false);
                interior.setRequired(false);
                reference.setRequired(false);
                manzana.setRequired(false);
                lote.setRequired(false);
                departamento.setRequired(true);
                provincia.setRequired(true);
                distrito.setRequired(true);
                zoneType.setRequired(false);
                zoneName.setRequired(false);
                postalCode.setRequired(false);
            } else if (countryId.equals(CountryParam.COUNTRY_ARGENTINA)) {
                roadName.setRequired(true).setValidRegex(StringFieldValidator.REGEXP_ALPHANUMERIC_SPEC_CHARS).setFieldName("Calle / Avenida");
                houseNumber.setRequired(true).setMinCharacters(1).setMaxCharacters(5).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
                //floor.setRequired(false).setMinCharacters(1).setMaxCharacters(2).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
                if (withoutNumber != null) {
                    houseNumber.setRequired(!withoutNumber);
                } else {
                    houseNumber.setRequired(true);
                }
                interior.setRequired(false).setMaxCharacters(4).setValidRegex(StringFieldValidator.REGEXP_ALPHANUMERIC);
                postalCode.setRequired(true).setMinCharacters(4).setMaxCharacters(4).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
                provincia.setRequired(true);
                distrito.setRequired(true);
                reference.setRequired(true);
                if (withoutNumber != null) {
                    reference.setRequired(withoutNumber);
                }
                roadType.setRequired(false);
                departamento.setRequired(false);
                zoneType.setRequired(false);
                zoneName.setRequired(false);
            } else if (countryId.equals(CountryParam.COUNTRY_COLOMBIA)) {
                provincia.setFieldName("Municipio");
                distrito.setFieldName("Barrio");
                distrito.setRequired(true);
                roadType.setRequired(false);
                postalCode.setRequired(false);
                houseNumber.setRequired(false);
                zoneType.setRequired(false);
                zoneName.setRequired(false);
                interior.setRequired(false);
            }
        }
    }
}
