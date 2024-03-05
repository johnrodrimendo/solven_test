/**
 *
 */
package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class Bank implements Serializable {

    public static final int BCP = 1;
    public static final int BANCO_DEL_SOL = 111;

    private Integer id;
    private String name;
    private Boolean active;
    private Boolean payments;
    private String logoUrl;
    private Entity entity;
    private Boolean disbursement;
    private Integer country;
    private String bankCode;

    public void fillFromDb(JSONObject json, CatalogService catalog) {
        setId(JsonUtil.getIntFromJson(json, "bank_id", null));
        setName(JsonUtil.getStringFromJson(json, "bank", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setPayments(JsonUtil.getBooleanFromJson(json, "payments", null));
        setLogoUrl(JsonUtil.getStringFromJson(json, "url_logo", null));
        if (JsonUtil.getIntFromJson(json, "entity_id", null) != null)
            setEntity(catalog.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        setDisbursement(JsonUtil.getBooleanFromJson(json, "disbursement", null));
        setCountry(JsonUtil.getIntFromJson(json, "country_id", null));
        setBankCode(JsonUtil.getStringFromJson(json, "bank_code", null));
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getPayments() {
        return payments;
    }

    public void setPayments(Boolean payments) {
        this.payments = payments;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Boolean getDisbursement() {
        return disbursement;
    }

    public void setDisbursement(Boolean disbursement) {
        this.disbursement = disbursement;
    }

    public Integer getCountry() {
        return country;
    }

    public void setCountry(Integer country) {
        this.country = country;
    }

    public String getBankCode() { return bankCode; }

    public void setBankCode(String bankCode) { this.bankCode = bankCode; }
}
