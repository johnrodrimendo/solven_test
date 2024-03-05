package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationApprovalValidationService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class GatewayLoanApplicationExtranetPainter {

    private Integer loanApplicationId;
    private Integer creditId;
    private Date registerDate;
    private String loanApplicationCode;
    private String creditCode;
    private IdentityDocumentType documentType;
    private String documentNumber;
    private String name;
    private String firstSurname;
    private String lastSurname;
    private String phoneNumber;
    private String email;
    private GatewayPaymentMethod gatewayPaymentMethod;
    private JSONObject entityCustomData;
    private EntityProductParams entityProductParam;
    private Product product;
    private String paymentCode;
    private Double amount;
    private Integer quotas;
    private String campaignType;
    private LoanApplicationStatus loanApplicationStatus;
    private CreditStatus creditStatus;
    private Boolean hasFiles;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setLoanApplicationCode(JsonUtil.getStringFromJson(json, "loan_application_code", null));
        setCreditCode(JsonUtil.getStringFromJson(json, "credit_code", null));
        if (JsonUtil.getIntFromJson(json, "document_id", null) != null) setDocumentType(catalog.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_id", null)));
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setName(JsonUtil.getStringFromJson(json, "person_name", null));
        setFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        setEntityCustomData(JsonUtil.getJsonObjectFromJson(json, "entity_custom_data", new JSONObject()));
        if (JsonUtil.getIntFromJson(json, "product_id", null) != null) setProduct(catalog.getProduct(JsonUtil.getIntFromJson(json, "product_id", null)));
        if(JsonUtil.getIntFromJson(json, "entity_product_parameter_id", null) != null) setEntityProductParam(catalog.getEntityProductParamById(JsonUtil.getIntFromJson(json, "entity_product_parameter_id", null)));
        if(JsonUtil.getIntFromJson(json, "selected_entity_product_parameter_id", null) != null) setEntityProductParam(catalog.getEntityProductParamById(JsonUtil.getIntFromJson(json, "selected_entity_product_parameter_id", null)));
        setCampaignType(JsonUtil.getStringFromJson(json, "campaign_type", null));
        setAmount(JsonUtil.getDoubleFromJson(json, "monto_campania", null));
        setPaymentCode(JsonUtil.getStringFromJson(json, "payment_code", null));
        if (JsonUtil.getIntFromJson(json, "application_status_id", null) != null) setLoanApplicationStatus(catalog.getLoanApplicationStatus(locale, JsonUtil.getIntFromJson(json, "application_status_id", null)));
        if (JsonUtil.getIntFromJson(json, "credit_status_id", null) != null) setCreditStatus(catalog.getCreditStatus(locale, JsonUtil.getIntFromJson(json, "credit_status_id", null)));
        setQuotas(JsonUtil.getIntFromJson(json, "quotas", null));
        setHasFiles(JsonUtil.getBooleanFromJson(json , "has_files", false));
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

    public String getFullSurnames() {
        String fullname = "";
        if (firstSurname != null) {
            fullname = fullname + firstSurname + " ";
        }
        if (lastSurname != null) {
            fullname = fullname + lastSurname + " ";
        }
        return fullname;
    }

    public List<ApprovalValidation> getApprovalValidationsIds(LoanApplicationApprovalValidationService loanApplicationApprovalValidationService){
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setEntityCustomData(entityCustomData);
        return loanApplicationApprovalValidationService.getApprovalValidationIds(loanApplication, entityProductParam);
    }
    
    public JSONObject getEntityCustomData() {
        return entityCustomData;
    }

    public void setEntityCustomData(JSONObject entityCustomData) {
        this.entityCustomData = entityCustomData;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCampaignType() {
        return campaignType;
    }

    public void setCampaignType(String campaignType) {
        this.campaignType = campaignType;
    }

    public GatewayPaymentMethod getCollectionPaymentMethod() {
        return gatewayPaymentMethod;
    }

    public void setCollectionPaymentMethod(GatewayPaymentMethod gatewayPaymentMethod) {
        this.gatewayPaymentMethod = gatewayPaymentMethod;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public EntityProductParams getEntityProductParam() {
        return entityProductParam;
    }

    public void setEntityProductParam(EntityProductParams entityProductParam) {
        this.entityProductParam = entityProductParam;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getLoanApplicationCode() {
        return loanApplicationCode;
    }

    public void setLoanApplicationCode(String loanApplicationCode) {
        this.loanApplicationCode = loanApplicationCode;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public Boolean isCredit(){
        return creditCode != null ? true : false;
    }

    public LoanApplicationStatus getLoanApplicationStatus() {

        if(loanApplicationStatus != null){
            switch (loanApplicationStatus.getId()){
                case LoanApplicationStatus.PRE_EVAL_APPROVED:
                case LoanApplicationStatus.EVAL_APPROVED:
                    loanApplicationStatus.setStatus("Registro");
                    break;
            }
        }
        return loanApplicationStatus;

    }

    public void setLoanApplicationStatus(LoanApplicationStatus loanApplicationStatus) {
        this.loanApplicationStatus = loanApplicationStatus;
    }

    public CreditStatus getCreditStatus() {
        return creditStatus;
    }

    public void setCreditStatus(CreditStatus creditStatus) {
        this.creditStatus = creditStatus;
    }

    public Boolean getHasFiles() {
        return hasFiles;
    }

    public void setHasFiles(Boolean hasFiles) {
        this.hasFiles = hasFiles;
    }

    public Integer getCreditId() {
        return creditId;
    }

    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
    }

    public Integer getQuotas() {
        return quotas;
    }

    public void setQuotas(Integer quotas) {
        this.quotas = quotas;
    }
}
