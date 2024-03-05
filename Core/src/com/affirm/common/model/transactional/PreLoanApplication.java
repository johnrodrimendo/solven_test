package com.affirm.common.model.transactional;

import com.affirm.common.model.BantotalApiData;
import com.affirm.common.model.catalog.Currency;
import com.affirm.common.model.catalog.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.Marshall;
import com.affirm.equifax.ws.ReporteCrediticio;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.*;

/**
 * Created by jrodriguez on 09/06/16.
 */

public class PreLoanApplication implements Serializable {

    private Integer id;
    private Integer documentId;
    private String documentNumber;
    private Integer entityId;
    private Integer productCategoryId;
    private Date registerDate;
    private Integer loanApplicationId;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "pre_loan_application_id", null));
        setDocumentId(JsonUtil.getIntFromJson(json, "document_id", null));
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setProductCategoryId(JsonUtil.getIntFromJson(json, "product_category_id", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(Integer productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }
}