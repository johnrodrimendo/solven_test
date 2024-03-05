package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jrodriguez on 08/06/16.
 */
public class IdentityDocumentType implements Serializable {

    public static final int DNI = 1;
    public static final int CE = 2;
    public static final int RUC = 3;
    public static final int CDI = 4;
    public static final int CUIL = 5;
    public static final int CUIT = 6;
    public static final int ARG_DNI = 7;
    public static final int COL_CEDULA_CIUDADANIA = 8;
    public static final int COL_CEDULA_EXTRANJERIA = 9;

    private Integer id;
    private String name;
    private Integer countryId;
    private Boolean taxIdentifier;

    public IdentityDocumentType() {
    }

    public IdentityDocumentType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "document_type_id", null));
        setName(JsonUtil.getStringFromJson(json, "document_type", null));
        setCountryId(JsonUtil.getIntFromJson(json, "country_id", null));
        setTaxIdentifier(JsonUtil.getBooleanFromJson(json, "tax_identifier", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Boolean getTaxIdentifier() {
        return taxIdentifier;
    }

    public void setTaxIdentifier(Boolean taxIdentifier) {
        this.taxIdentifier = taxIdentifier;
    }
}
