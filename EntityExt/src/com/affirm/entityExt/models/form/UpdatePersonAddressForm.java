package com.affirm.entityExt.models.form;

import com.affirm.common.model.catalog.AreaType;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * Created by jrodriguez on 21/07/16.
 */
public class UpdatePersonAddressForm extends FormGeneric implements Serializable {

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
    private Double latitude;
    private Double longitude;
    private String searchQuery;
    private String addressType;
    private String aggregatedAddress;

    public UpdatePersonAddressForm() {
        this.setValidator(new Validator());
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

    public String getSearchQuery() {
        return this.searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
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
        public DoubleFieldValidator latitude;
        public DoubleFieldValidator longitude;
        public StringFieldValidator aggregatedAddress;

        public Validator() {
            addValidator(roadType = new StringFieldValidator(ValidatorUtil.STREET_TYPE));
            addValidator(roadName = new StringFieldValidator(ValidatorUtil.STREET_NAME));
            addValidator(houseNumber = new StringFieldValidator(ValidatorUtil.STREET_NUMBER));
            addValidator(withoutNumber = new BooleanFieldValidator());
            addValidator(floor = new StringFieldValidator(ValidatorUtil.STREET_NUMBER));
            addValidator(interior = new StringFieldValidator(ValidatorUtil.INTERIOR_NUMBER));
            addValidator(postalCode = new StringFieldValidator(ValidatorUtil.AR_POSTAL_CODE));
            addValidator(reference = new StringFieldValidator(ValidatorUtil.STREET_NAME));
            addValidator(manzana = new StringFieldValidator(ValidatorUtil.MANZANA));
            addValidator(lote = new StringFieldValidator(ValidatorUtil.LOTE));
            addValidator(departamento = new StringFieldValidator(ValidatorUtil.DEPARTMENT));
            addValidator(provincia = new StringFieldValidator(ValidatorUtil.PROVINCE));
            addValidator(distrito = new StringFieldValidator(ValidatorUtil.DISTRICT));
            addValidator(zoneType = new StringFieldValidator(ValidatorUtil.STREET_TYPE));
            addValidator(zoneName = new StringFieldValidator(ValidatorUtil.STREET_NAME));
            addValidator(latitude = new DoubleFieldValidator(ValidatorUtil.LATITUDE).setRequired(false));
            addValidator(longitude = new DoubleFieldValidator(ValidatorUtil.LONGITUDE).setRequired(false));
            addValidator(aggregatedAddress = new StringFieldValidator(ValidatorUtil.STREET_NAME).setRequired(false).setValidRegex(null));
        }

        @Override
        protected void setDynamicValidations() {
            if (zoneType != null) {
                if (zoneType.getValue().equals(String.valueOf(AreaType.NO_APLICA))) {
                    zoneName.setRequired(false);
                } else {
                    zoneName.setRequired(true);
                }
            }

        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return UpdatePersonAddressForm.this;
        }

        public void setCountryId(Integer countryId, Boolean withoutNumber) {
            if (countryId.equals(CountryParam.COUNTRY_PERU) || countryId.equals(CountryParam.COUNTRY_COLOMBIA)) {
                roadType.setRequired(true);
                roadName.setRequired(true);
                floor.setRequired(false);
                interior.setRequired(false);
                reference.setRequired(true);
                manzana.setRequired(false);
                lote.setRequired(false);
                departamento.setRequired(true);
                floor.setRequired(false);
                provincia.setRequired(true);
                distrito.setRequired(true);
                zoneType.setRequired(true);
                zoneName.setRequired(true);
                postalCode.setRequired(false);
            } else if (countryId.equals(CountryParam.COUNTRY_ARGENTINA)) {
                roadName.setRequired(true).setValidRegex(StringFieldValidator.REGEXP_ALPHANUMERIC_SPEC_CHARS);
                houseNumber.setRequired(false).setMinCharacters(1).setMaxCharacters(5).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
                floor.setRequired(false).setMinCharacters(1).setMaxCharacters(2).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
                if (withoutNumber != null) {
                    houseNumber.setRequired(!withoutNumber);
                }
                interior.setRequired(false).setMaxCharacters(4).setValidRegex(StringFieldValidator.REGEXP_ALPHANUMERIC);
                postalCode.setRequired(true).setMinCharacters(4).setMaxCharacters(4).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
                provincia.setRequired(true);
                distrito.setRequired(true);
                if (withoutNumber != null) {
                    reference.setRequired(withoutNumber);
                }
                roadType.setRequired(false);
                departamento.setRequired(false);
                zoneType.setRequired(false);
                zoneName.setRequired(false);
            }

            if (countryId.equals(CountryParam.COUNTRY_COLOMBIA)) {
                provincia.setFieldName("Municipio");
                distrito.setFieldName("Barrio");
            }
        }
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAggregatedAddress() {
        return aggregatedAddress;
    }

    public void setAggregatedAddress(String aggregatedAddress) {
        this.aggregatedAddress = aggregatedAddress;
    }
}
