package com.affirm.common.model.transactional;

import com.affirm.common.model.AfipActivitiy;
import com.affirm.common.model.catalog.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationApprovalValidationService;
import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.xpath.operations.Bool;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */
public class LoanApplicationToEvaluationExtranetPainter {

    private Integer loanApplicationId;
    private Integer assignedEntityUserId;
    private Date registerDate;
    private String code;
    private IdentityDocumentType documentType;
    private String documentNumber;
    private String name;
    private String firstSurname;
    private String lastSurname;
    private String countryCode;
    private String phoneNumber;
    private String email;
    private Product product;
    private List<LoanOffer> offers;
    private List<LoanApplicationApprovalValidation> approvalValidations;
    private Integer entityProductParameterId;
    private EntityProductParams entityProductParam;
    private ApprovalValidation lastApprovalValidation;
    private JSONObject entityCustomData;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setCode(JsonUtil.getStringFromJson(json, "loan_application_code", null));
        if (JsonUtil.getIntFromJson(json, "document_id", null) != null) {
            setDocumentType(catalog.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_id", null)));
        }
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setName(JsonUtil.getStringFromJson(json, "person_name", null));
        setFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        setCountryCode(JsonUtil.getStringFromJson(json, "country_code", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        setAssignedEntityUserId(JsonUtil.getIntFromJson(json, "assigned_entity_user_id", null));
        if (JsonUtil.getIntFromJson(json, "product_id", null) != null) {
            setProduct(catalog.getProduct(JsonUtil.getIntFromJson(json, "product_id", null)));
        }
        if (JsonUtil.getJsonArrayFromJson(json, "loan_offer", null) != null) {
            offers = new ArrayList<>();
            for (int i = 0; i < json.getJSONArray("loan_offer").length(); i++) {
                LoanOffer offer = new LoanOffer();
                offer.fillFromDb(json.getJSONArray("loan_offer").getJSONObject(i), catalog);
                offers.add(offer);
            }
        }
        setApprovalValidations(new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "js_approval_validations", new JSONArray()).toString(), new TypeToken<ArrayList<LoanApplicationApprovalValidation>>() {
        }.getType()));
        setEntityProductParameterId(JsonUtil.getIntFromJson(json, "selected_entity_product_parameter_id", null));
        if(entityProductParameterId != null){
            setEntityProductParam(catalog.getEntityProductParamById(entityProductParameterId));
        }
        if(approvalValidations != null && !approvalValidations.isEmpty()){
            LoanApplicationApprovalValidation lastApproval = approvalValidations.get(approvalValidations.size()-1);
            if(lastApproval != null) setLastApprovalValidation(catalog.getApprovalValidation(lastApproval.getApprovalValidationId()));
        }
        setEntityCustomData(JsonUtil.getJsonObjectFromJson(json, "entity_custom_data", new JSONObject()));
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<LoanOffer> getOffers() {
        return offers;
    }

    public void setOffers(List<LoanOffer> offers) {
        this.offers = offers;
    }

    public String getFullName() {
        String fullname = "";
        if (name != null) {
            fullname = fullname + name + " ";
        }
        if (firstSurname != null) {
            fullname = fullname + firstSurname + " ";
        }
        if (lastSurname != null) {
            fullname = fullname + lastSurname + " ";
        }
        return fullname;
    }

    public Integer getAssignedEntityUserId() {
        return assignedEntityUserId;
    }

    public void setAssignedEntityUserId(Integer assignedEntityUserId) {
        this.assignedEntityUserId = assignedEntityUserId;
    }

    public Integer getEntityProductParameterId() {
        return entityProductParameterId;
    }

    public void setEntityProductParameterId(Integer entityProductParameterId) {
        this.entityProductParameterId = entityProductParameterId;
    }

    public List<LoanApplicationApprovalValidation> getApprovalValidations() {
        return approvalValidations;
    }

    public void setApprovalValidations(List<LoanApplicationApprovalValidation> approvalValidations) {
        this.approvalValidations = approvalValidations;
    }

    public EntityProductParams getEntityProductParam() {
        return entityProductParam;
    }

    public void setEntityProductParam(EntityProductParams entityProductParam) {
        this.entityProductParam = entityProductParam;
    }

//    public Integer calculateProgressPercentage(){
//        Integer percentage = 0;
//        if(entityProductParam == null || approvalValidations == null || approvalValidations.isEmpty()) return percentage;
//        if(entityProductParam.getApprovalValidations().isEmpty()) percentage = 100;
//        else{
//            percentage = (approvalValidations.size()*100)/entityProductParam.getApprovalValidations().size();
//        }
//        return percentage;
//    }

    public List<ApprovalValidation> getApprovalValidationsIds(LoanApplicationApprovalValidationService loanApplicationApprovalValidationService){
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setEntityCustomData(entityCustomData);
        return loanApplicationApprovalValidationService.getApprovalValidationIds(loanApplication, entityProductParam);
    }

    public ApprovalValidation getLastApprovalValidation() {
        return lastApprovalValidation;
    }

    public void setLastApprovalValidation(ApprovalValidation lastApprovalValidation) {
        this.lastApprovalValidation = lastApprovalValidation;
    }

    public boolean existsApprovalValidation( Integer approvalValidationId){
        if(approvalValidationId != null && approvalValidations != null && !approvalValidations.isEmpty()){
            LoanApplicationApprovalValidation validation = approvalValidations.stream().filter(e -> e.getApprovalValidationId().equals(approvalValidationId)).findFirst().orElse(null);
            if(validation != null && validation.getApproved() != null)
                return validation.getApproved();
            return false;
        }
        return false;
    }
    
    public JSONObject getEntityCustomData() {
        return entityCustomData;
    }

    public void setEntityCustomData(JSONObject entityCustomData) {
        this.entityCustomData = entityCustomData;
    }

    public Boolean showDisbursementTypeColumn(){

        if(this.product != null && this.product.getId() != Product.SAVINGS_ACCOUNT){
            return true;
        }
        return false;
    }
}
