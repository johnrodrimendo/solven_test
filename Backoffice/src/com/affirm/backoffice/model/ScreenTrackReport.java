package com.affirm.backoffice.model;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.transactional.ProcessQuestionSequence;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScreenTrackReport {

    private Integer loanApplicationId;
    private String loanApplicationCode;
    private ProductCategory productCategory;
    private Date newLoanStatusDate;
    private IdentityDocumentType docType;
    private String docNumber;
    private LoanApplicationStatus loanApplicationStatus;
    private Date maxStatusDate;
    private Long proccesDuration;
    private Long disbursementTime;
    private JSONArray screenQuantityJson;
    private JSONArray screenTimeJson;
    private Integer currentQuestionId;
    private List<ProcessQuestionSequence> questionSequence = new ArrayList<>();


    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setLoanApplicationCode(JsonUtil.getStringFromJson(json, "loan_application_code", null));
        if (JsonUtil.getIntFromJson(json, "category_id", null) != null)
            setProductCategory(catalog.getCatalogById(ProductCategory.class, JsonUtil.getIntFromJson(json, "category_id", null), Configuration.getDefaultLocale()));
        setNewLoanStatusDate(JsonUtil.getPostgresDateFromJson(json, "new", null));
        if (JsonUtil.getIntFromJson(json, "document_id", null) != null)
            setDocType(catalog.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_id", null)));
        setDocNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        if (JsonUtil.getIntFromJson(json, "application_status_id", null) != null)
            setLoanApplicationStatus(catalog.getLoanApplicationStatus(locale, (JsonUtil.getIntFromJson(json, "application_status_id", null))));
        setMaxStatusDate(JsonUtil.getPostgresDateFromJson(json, "max_status_date", null));
        setProccesDuration(JsonUtil.getLongFromJson(json, "procces_duration", null));
        setDisbursementTime(JsonUtil.getLongFromJson(json, "disbursement_time", null));
        setScreenQuantityJson(JsonUtil.getJsonArrayFromJson(json, "screens_quantity", null));
        setScreenTimeJson(JsonUtil.getJsonArrayFromJson(json, "screens_time", null));
        setCurrentQuestionId(JsonUtil.getIntFromJson(json, "current_process_question_id", null));
        if (JsonUtil.getJsonArrayFromJson(json, "proccess_question_id_sequence", null) != null) {
            setQuestionSequence(
                    new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "proccess_question_id_sequence", null).toString(), new TypeToken<ArrayList<ProcessQuestionSequence>>() {
                    }.getType()));
        }
    }

    public int getScreensByCategory(int categoryId) {
        if (screenQuantityJson == null)
            return 0;

        for (int i = 0; i < screenQuantityJson.length(); i++) {
            if (screenQuantityJson.getJSONObject(i).optInt("process_questions_category_id", -1) == categoryId) {
                return screenQuantityJson.getJSONObject(i).getInt("quantity");
            }
        }
        return 0;
    }

    public int getScreensTimeByCategory(int categoryId) {
        if (screenQuantityJson == null)
            return 0;

        for (int i = 0; i < screenQuantityJson.length(); i++) {
            if (screenQuantityJson.getJSONObject(i).optInt("process_questions_category_id", -1) == categoryId) {
                return screenQuantityJson.getJSONObject(i).optInt("category_time", 0);
            }
        }
        return 0;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public String getLoanApplicationCode() {
        return loanApplicationCode;
    }

    public void setLoanApplicationCode(String loanApplicationCode) {
        this.loanApplicationCode = loanApplicationCode;
    }

    public Date getNewLoanStatusDate() {
        return newLoanStatusDate;
    }

    public void setNewLoanStatusDate(Date newLoanStatusDate) {
        this.newLoanStatusDate = newLoanStatusDate;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public LoanApplicationStatus getLoanApplicationStatus() {
        return loanApplicationStatus;
    }

    public void setLoanApplicationStatus(LoanApplicationStatus loanApplicationStatus) {
        this.loanApplicationStatus = loanApplicationStatus;
    }

    public Date getMaxStatusDate() {
        return maxStatusDate;
    }

    public void setMaxStatusDate(Date maxStatusDate) {
        this.maxStatusDate = maxStatusDate;
    }

    public Long getProccesDuration() {
        return proccesDuration;
    }

    public void setProccesDuration(Long proccesDuration) {
        this.proccesDuration = proccesDuration;
    }

    public Long getDisbursementTime() {
        return disbursementTime;
    }

    public void setDisbursementTime(Long disbursementTime) {
        this.disbursementTime = disbursementTime;
    }

    public JSONArray getScreenQuantityJson() {
        return screenQuantityJson;
    }

    public void setScreenQuantityJson(JSONArray screenQuantityJson) {
        this.screenQuantityJson = screenQuantityJson;
    }

    public Integer getCurrentQuestionId() {
        return currentQuestionId;
    }

    public void setCurrentQuestionId(Integer currentQuestionId) {
        this.currentQuestionId = currentQuestionId;
    }

    public List<ProcessQuestionSequence> getQuestionSequence() {
        return questionSequence;
    }

    public void setQuestionSequence(List<ProcessQuestionSequence> questionSequence) {
        this.questionSequence = questionSequence;
    }

    public JSONArray getScreenTimeJson() {
        return screenTimeJson;
    }

    public void setScreenTimeJson(JSONArray screenTimeJson) {
        this.screenTimeJson = screenTimeJson;
    }

    public IdentityDocumentType getDocType() {
        return docType;
    }

    public void setDocType(IdentityDocumentType docType) {
        this.docType = docType;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }
}
