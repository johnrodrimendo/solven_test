package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.ConsolidationAccountType;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.RccEntity;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by john on 13/01/17.
 */
public class PendingDisbursementConsolidationReportDetail {

    private IdentityDocumentType documentType;
    private String documentNumber;
    private String personName;
    private String firstSurname;
    private String lastSurname;
    private RccEntity rccEntity;
    private String cardNumber;
    private Integer consolidationAccountType;
    private Double balance;
    private Integer creditId;

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        if (JsonUtil.getIntFromJson(json, "document_id", null) != null)
            setDocumentType(catalog.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_id", null)));
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setPersonName(JsonUtil.getStringFromJson(json, "person_name", null));
        setFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        if (JsonUtil.getStringFromJson(json, "entity_code", null) != null)
            setRccEntity(catalog.getRccEntity(JsonUtil.getStringFromJson(json, "entity_code", null)));
        setCardNumber(JsonUtil.getStringFromJson(json, "account_card_number", null));
        setConsolidationAccountType(JsonUtil.getIntFromJson(json, "consolidation_account_id", null));
        setBalance(JsonUtil.getDoubleFromJson(json, "balance", null));
        setCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
    }

    public String getFullName() {
        return (personName != null ? personName : "") + " " + (firstSurname != null ? firstSurname : "") + " " + (lastSurname != null ? lastSurname : "");
    }

    public String getConsolidationTypeShort() {
        if (getConsolidationAccountType() == ConsolidationAccountType.TARJETA_CREDITO)
            return "TC";
        else if (getConsolidationAccountType() == ConsolidationAccountType.CREDITO_CONSUMO)
            return "CP";
        return "";
    }

    public String getConsolidationTypeLong() {
        if (getConsolidationAccountType() == ConsolidationAccountType.TARJETA_CREDITO)
            return "Tarjeta de crédito";
        else if (getConsolidationAccountType() == ConsolidationAccountType.CREDITO_CONSUMO)
            return "Prestamo";
        return "";
    }

    public IdentityDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(IdentityDocumentType documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public String getLastSurname() {
        return lastSurname;
    }

    public void setLastSurname(String lastSurname) {
        this.lastSurname = lastSurname;
    }

    public RccEntity getRccEntity() {
        return rccEntity;
    }

    public void setRccEntity(RccEntity rccEntity) {
        this.rccEntity = rccEntity;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Integer getConsolidationAccountType() {
        return consolidationAccountType;
    }

    public void setConsolidationAccountType(Integer consolidationAccountType) {
        this.consolidationAccountType = consolidationAccountType;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Integer getCreditId() {
        return creditId;
    }

    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
    }
}
