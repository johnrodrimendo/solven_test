package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.BufferTransactionType;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jrodriguez on 11/08/16.
 */
public class BufferTransaction implements Serializable {

    private Integer id;
    private BufferTransactionType bufferTransactionType;
    private Integer sysuserId;
    private Date scheduledDate;
    private Date executionDate;
    private Boolean active;
    private String paramValue1;
    private Integer creditId;
    private Integer personId;
    private String creditCode;
    private String documentNumber;
    private String name;
    private String firstSurname;
    private String lastSurname;
    private Character disbursementType;
    private String checkNumber;
    private Boolean paused;
    private Character bankAccountType;
    private String bankAccount;
    private Product product;

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "buffer_transaction_id", null));
        if (JsonUtil.getIntFromJson(json, "transaction_type_id", null) != null) {
            setBufferTransactionType(catalog.getBufferTransactionType(JsonUtil.getIntFromJson(json, "transaction_type_id", null)));
        }
        setId(JsonUtil.getIntFromJson(json, "buffer_transaction_id", null));
        setSysuserId(JsonUtil.getIntFromJson(json, "sysuser_id", null));
        setScheduledDate(JsonUtil.getPostgresDateFromJson(json, "scheduled_date", null));
        setExecutionDate(JsonUtil.getPostgresDateFromJson(json, "execution_date", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setParamValue1(JsonUtil.getStringFromJson(json, "parameter_value_1", null));
        setCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
        if (JsonUtil.getIntFromJson(json, "product_id", null) != null) {
            product = catalog.getProduct(JsonUtil.getIntFromJson(json, "product_id", null));
        }
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setCreditCode(JsonUtil.getStringFromJson(json, "credit_code", null));
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setName(JsonUtil.getStringFromJson(json, "person_name", null));
        setFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        setDisbursementType(JsonUtil.getCharacterFromJson(json, "disbursement_type", null));
        setCheckNumber(JsonUtil.getStringFromJson(json, "check_number", null));
        setPaused(JsonUtil.getBooleanFromJson(json, "is_paused", null));
        setBankAccountType(JsonUtil.getCharacterFromJson(json, "bank_account_type", null));
        setBankAccount(JsonUtil.getStringFromJson(json, "bank_account", null));
    }

    public String getFullName() {
        return getName() + " " + getFirstSurname() + " " + getLastSurname();
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BufferTransactionType getBufferTransactionType() {
        return bufferTransactionType;
    }

    public void setBufferTransactionType(BufferTransactionType bufferTransactionType) {
        this.bufferTransactionType = bufferTransactionType;
    }

    public Integer getSysuserId() {
        return sysuserId;
    }

    public void setSysuserId(Integer sysuserId) {
        this.sysuserId = sysuserId;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getParamValue1() {
        return paramValue1;
    }

    public void setParamValue1(String paramValue1) {
        this.paramValue1 = paramValue1;
    }

    public Integer getCreditId() {
        return creditId;
    }

    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Character getDisbursementType() {
        return disbursementType;
    }

    public void setDisbursementType(Character disbursementType) {
        this.disbursementType = disbursementType;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public Boolean getPaused() {
        return paused;
    }

    public void setPaused(Boolean paused) {
        this.paused = paused;
    }

    public Character getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(Character bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
