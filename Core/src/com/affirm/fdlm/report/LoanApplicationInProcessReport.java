package com.affirm.fdlm.report;

import com.affirm.common.model.catalog.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

public class LoanApplicationInProcessReport {

    private Date registerDate;
    private Date updatedTime;
    private Integer loanApplicationId;
    private String loanApplicationCode;
    private Double amount;
    private Integer installments;
    private IdentityDocumentType clientDocument;
    private String clientDocumentNumber;
    private LoanApplicationStatus loanApplicationStatus;
    private String rejectionMessage;
    private LoanApplicationReason loanApplicationReason;
    private String clientPhoneNumber;
    private String clientEmail;
    private String clientName;
    private String clientFirstSurname;
    private String clientLastSurname;
    private Date birthday;
    private Integer loanOriginId;
    private MaritalStatus maritalStatus;
    private Integer dependents;
    private String clientAddress;
    private HousingType clientHousingType;
    private Long clientLocalityId;
    private StudyLevel studyLevel;
    private ActivityType activityType;
    private String companyName;
    private String companyPhoneNumber;
    private Date clientCompanyStartDate;
    private String companyAddress;
    private Long companyLocalityId;
    private Integer professionOccupationId;
    private Integer professionId;
    private Integer ocupationId;
    private Double fixedGrossIncome;
    private String contractType;

    public void fillFromDb(JSONObject json, CatalogService catalogService, Locale locale) {
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setUpdatedTime(JsonUtil.getPostgresDateFromJson(json, "updated_time", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setLoanApplicationCode(JsonUtil.getStringFromJson(json, "loan_application_code", null));
        setAmount(JsonUtil.getDoubleFromJson(json, "amount", null));
        setInstallments(JsonUtil.getIntFromJson(json, "installments", null));

        Integer documentId = JsonUtil.getIntFromJson(json, "document_id", null);
        if (documentId != null) {
            setClientDocument(catalogService.getIdentityDocumentType(documentId));
        }

        setClientDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));

        Integer loanApplicationStatusId = JsonUtil.getIntFromJson(json, "application_status_id", null);
        if (loanApplicationStatusId != null) {
            setLoanApplicationStatus(catalogService.getLoanApplicationStatus(locale, loanApplicationStatusId));
        }

        setRejectionMessage(JsonUtil.getStringFromJson(json, "rejection_message", null));

        Integer reasonId = JsonUtil.getIntFromJson(json, "reason_id", null);
        if (reasonId != null) {
            setLoanApplicationReason(catalogService.getLoanApplicationReason(locale, reasonId));
        }

        setClientPhoneNumber(JsonUtil.getStringFromJson(json, "client_phone_number", null));
        setClientEmail(JsonUtil.getStringFromJson(json, "email", null));
        setClientName(JsonUtil.getStringFromJson(json, "client_name", null));
        setClientFirstSurname(JsonUtil.getStringFromJson(json, "client_first_surname", null));
        setClientLastSurname(JsonUtil.getStringFromJson(json, "client_last_surname", null));
        setBirthday(JsonUtil.getPostgresDateFromJson(json, "birthday", null));
        setLoanOriginId(JsonUtil.getIntFromJson(json, "loan_origin_id", null));

        Integer maritalStatus = JsonUtil.getIntFromJson(json, "marital_status_id", null);
        if (maritalStatus != null) {
            setMaritalStatus(catalogService.getMaritalStatus(locale, maritalStatus));
        }

        setDependents(JsonUtil.getIntFromJson(json, "dependents", null));
        setDependents(JsonUtil.getIntFromJson(json, "dependents", null));
        setClientAddress(JsonUtil.getStringFromJson(json, "street_name", null));

        Integer housingTypeId = JsonUtil.getIntFromJson(json, "housing_type_id", null);
        if (housingTypeId != null) {
            setClientHousingType(catalogService.getHousingType(locale, housingTypeId));
        }

        setClientLocalityId(JsonUtil.getLongFromJson(json, "client_locality_id", null));

        Integer studyLevelId = JsonUtil.getIntFromJson(json, "study_level_id", null);
        if (studyLevelId != null) {
            setStudyLevel(catalogService.getStudyLevel(locale, studyLevelId));
        }

        Integer activityTypeId = JsonUtil.getIntFromJson(json, "activity_type_id", null);
        if (activityTypeId != null) {
            setActivityType(catalogService.getActivityType(locale, activityTypeId));
        }

        setCompanyName(JsonUtil.getStringFromJson(json, "company_name", null));
        setCompanyPhoneNumber(JsonUtil.getStringFromJson(json, "company_phone_number", null));
        setClientCompanyStartDate(JsonUtil.getPostgresDateFromJson(json, "start_date", null));
        setCompanyAddress(JsonUtil.getStringFromJson(json, "company_address", null));
        setCompanyLocalityId(JsonUtil.getLongFromJson(json, "company_locality_id", null));
        setProfessionOccupationId(JsonUtil.getIntFromJson(json, "profession_occupation_id", null));
        setProfessionId(JsonUtil.getIntFromJson(json, "profession_id", null));
        setOcupationId(JsonUtil.getIntFromJson(json, "ocupation_id", null));
        setFixedGrossIncome(JsonUtil.getDoubleFromJson(json, "fixed_gross_income", null));
        setContractType(JsonUtil.getStringFromJson(json, "contract_type", null));
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public IdentityDocumentType getClientDocument() {
        return clientDocument;
    }

    public void setClientDocument(IdentityDocumentType clientDocument) {
        this.clientDocument = clientDocument;
    }

    public String getClientDocumentNumber() {
        return clientDocumentNumber;
    }

    public void setClientDocumentNumber(String clientDocumentNumber) {
        this.clientDocumentNumber = clientDocumentNumber;
    }

    public LoanApplicationStatus getLoanApplicationStatus() {
        return loanApplicationStatus;
    }

    public void setLoanApplicationStatus(LoanApplicationStatus loanApplicationStatus) {
        this.loanApplicationStatus = loanApplicationStatus;
    }

    public String getRejectionMessage() {
        return rejectionMessage;
    }

    public void setRejectionMessage(String rejectionMessage) {
        this.rejectionMessage = rejectionMessage;
    }

    public LoanApplicationReason getLoanApplicationReason() {
        return loanApplicationReason;
    }

    public void setLoanApplicationReason(LoanApplicationReason loanApplicationReason) {
        this.loanApplicationReason = loanApplicationReason;
    }

    public String getClientPhoneNumber() {
        return clientPhoneNumber;
    }

    public void setClientPhoneNumber(String clientPhoneNumber) {
        this.clientPhoneNumber = clientPhoneNumber;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientFirstSurname() {
        return clientFirstSurname;
    }

    public void setClientFirstSurname(String clientFirstSurname) {
        this.clientFirstSurname = clientFirstSurname;
    }

    public String getClientLastSurname() {
        return clientLastSurname;
    }

    public void setClientLastSurname(String clientLastSurname) {
        this.clientLastSurname = clientLastSurname;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getLoanOriginId() {
        return loanOriginId;
    }

    public void setLoanOriginId(Integer loanOriginId) {
        this.loanOriginId = loanOriginId;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Integer getDependents() {
        return dependents;
    }

    public void setDependents(Integer dependents) {
        this.dependents = dependents;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public HousingType getClientHousingType() {
        return clientHousingType;
    }

    public void setClientHousingType(HousingType clientHousingType) {
        this.clientHousingType = clientHousingType;
    }

    public Long getClientLocalityId() {
        return clientLocalityId;
    }

    public void setClientLocalityId(Long clientLocalityId) {
        this.clientLocalityId = clientLocalityId;
    }

    public StudyLevel getStudyLevel() {
        return studyLevel;
    }

    public void setStudyLevel(StudyLevel studyLevel) {
        this.studyLevel = studyLevel;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyPhoneNumber() {
        return companyPhoneNumber;
    }

    public void setCompanyPhoneNumber(String companyPhoneNumber) {
        this.companyPhoneNumber = companyPhoneNumber;
    }

    public Date getClientCompanyStartDate() {
        return clientCompanyStartDate;
    }

    public void setClientCompanyStartDate(Date clientCompanyStartDate) {
        this.clientCompanyStartDate = clientCompanyStartDate;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public Long getCompanyLocalityId() {
        return companyLocalityId;
    }

    public void setCompanyLocalityId(Long companyLocalityId) {
        this.companyLocalityId = companyLocalityId;
    }

    public Integer getProfessionOccupationId() {
        return professionOccupationId;
    }

    public void setProfessionOccupationId(Integer professionOccupationId) {
        this.professionOccupationId = professionOccupationId;
    }

    public Integer getProfessionId() {
        return professionId;
    }

    public void setProfessionId(Integer professionId) {
        this.professionId = professionId;
    }

    public Integer getOcupationId() {
        return ocupationId;
    }

    public void setOcupationId(Integer ocupationId) {
        this.ocupationId = ocupationId;
    }

    public Double getFixedGrossIncome() {
        return fixedGrossIncome;
    }

    public void setFixedGrossIncome(Double fixedGrossIncome) {
        this.fixedGrossIncome = fixedGrossIncome;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }
}
