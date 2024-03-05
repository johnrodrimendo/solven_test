package com.affirm.common.model;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by dev5 on 24/08/17.
 */
public class PreApprovedInfo {

    private Integer approvedDataId;
    private Entity entity;
    private Product product;
    private IdentityDocumentType documentType;
    private String documentNumber;
    private Double maxAmount;
    private Integer maxInstallments;
    private Double effectiveAnualRate;
    private Date registerDate;
    private String cardType;
    private String cardNumber;
    private Integer paymentDay;
    private String firstName;
    private String firstSurname;
    private String cellphone;
    private Boolean smsSent;
    private String cluster;
    private List<Integer> entityProductParams;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setApprovedDataId(JsonUtil.getIntFromJson(json, "approved_data_id", null));
        setEntity(catalog.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        setProduct(catalog.getProduct(JsonUtil.getIntFromJson(json, "product_id", null)));
        setDocumentType(catalog.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_type_id", null)));
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setMaxAmount(JsonUtil.getDoubleFromJson(json, "max_amount", null));
        setMaxInstallments(JsonUtil.getIntFromJson(json, "max_installments", null));
        setEffectiveAnualRate(JsonUtil.getDoubleFromJson(json, "effective_annual_rate", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setCardType(JsonUtil.getStringFromJson(json, "card_type", null));
        setCardNumber(JsonUtil.getStringFromJson(json, "card_number", null));
        setPaymentDay(JsonUtil.getIntFromJson(json, "payment_day", null));
        setCluster(JsonUtil.getStringFromJson(json, "cluster", null));
        //ToDo
        //For SMS job
        setFirstName(JsonUtil.getStringFromJson(json, "", null));
        setFirstSurname(JsonUtil.getStringFromJson(json, "", null));
        setCellphone(JsonUtil.getStringFromJson(json, "", null));
        if (JsonUtil.getJsonArrayFromJson(json, "ar_entity_product_parameter_id", null) != null) {
            setEntityProductParams(
                    new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "ar_entity_product_parameter_id", null).toString(), new TypeToken<ArrayList<Integer>>() {
                    }.getType()));
        }
        setSmsSent(false);
    }


    public Integer getApprovedDataId() {
        return approvedDataId;
    }

    public void setApprovedDataId(Integer approvedDataId) {
        this.approvedDataId = approvedDataId;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Double maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Integer getMaxInstallments() {
        return maxInstallments;
    }

    public void setMaxInstallments(Integer maxInstallments) {
        this.maxInstallments = maxInstallments;
    }

    public Double getEffectiveAnualRate() {
        return effectiveAnualRate;
    }

    public void setEffectiveAnualRate(Double effectiveAnualRate) {
        this.effectiveAnualRate = effectiveAnualRate;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public IdentityDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(IdentityDocumentType documentType) {
        this.documentType = documentType;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Integer getPaymentDay() {
        return paymentDay;
    }

    public void setPaymentDay(Integer paymentDay) {
        this.paymentDay = paymentDay;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public Boolean getSmsSent() {
        return smsSent;
    }

    public void setSmsSent(Boolean smsSent) {
        this.smsSent = smsSent;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public List<Integer> getEntityProductParams() {
        return entityProductParams;
    }

    public void setEntityProductParams(List<Integer> entityProductParams) {
        this.entityProductParams = entityProductParams;
    }
}
