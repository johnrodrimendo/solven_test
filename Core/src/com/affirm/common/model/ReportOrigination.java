package com.affirm.common.model;

import com.affirm.common.model.catalog.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by dev5 on 15/09/17.
 */
public class ReportOrigination {

    private Date originationDate;
    private String loanApplicationCode;
    private String creditCode;
    private Integer personId;
    private IdentityDocumentType identityDocumentType;
    private String documentNumber;
    private String ruc;
    private String surname;
    private String personName;
    private Double loanCapital;
    private Integer installments;
    private Double effectiveAnualRate;
    private Double effectiveAnualCostRate;
    private Double nominalAnualRate;
    private Double installmentAmountAvg;
    private Product product;
    private Entity entity;
    private Employer employer;
    private Double entityCommission;
    private String preliminaryEvaluationEntities;
    private String offerEntities;
    private ActivityType activityType;
    private String companyName;
    private Integer employmentTime;
    private Double income;
    private LoanApplicationReason loanApplicationReason;
    private String gender;
    private Date birthday;
    private Ubigeo ubigeo;
    private MaritalStatus maritalStatus;
    private StudyLevel studyLevel;
    private String score;
    private Nationality nationality;
    private CountryParam country;
    private String realizedBy;
    private Boolean assistedProcess;
    private Integer disbursementTime;

    private String source;
    private String medium;
    private String campaign;
    private String term;
    private String content;
    private String gclid;

    public void fillFromDb(JSONObject json, CatalogService catalogService, Locale locale) throws Exception {
        if (JsonUtil.getPostgresDateFromJson(json, "origination_date", null) != null)
            setOriginationDate(JsonUtil.getPostgresDateFromJson(json, "origination_date", null));
        if (JsonUtil.getStringFromJson(json, "loan_application_code", null) != null)
            setLoanApplicationCode(JsonUtil.getStringFromJson(json, "loan_application_code", null));
        if (JsonUtil.getStringFromJson(json, "credit_code", null) != null)
            setCreditCode(JsonUtil.getStringFromJson(json, "credit_code", null));
        if (JsonUtil.getIntFromJson(json, "person_id", null) != null)
            setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        if (JsonUtil.getIntFromJson(json, "document_id", null) != null)
            setIdentityDocumentType(catalogService.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_id", null)));
        if (JsonUtil.getStringFromJson(json, "document_number", null) != null)
            setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        if (JsonUtil.getStringFromJson(json, "ruc", null) != null)
            setRuc(JsonUtil.getStringFromJson(json, "ruc", null));
        if (JsonUtil.getStringFromJson(json, "surname", null) != null)
            setSurname(JsonUtil.getStringFromJson(json, "surname", null));
        if (JsonUtil.getStringFromJson(json, "person_name", null) != null)
            setPersonName(JsonUtil.getStringFromJson(json, "person_name", null));
        if (JsonUtil.getDoubleFromJson(json, "loan_capital", null) != null)
            setLoanCapital(JsonUtil.getDoubleFromJson(json, "loan_capital", null));
        if (JsonUtil.getIntFromJson(json, "installments", null) != null)
            setInstallments(JsonUtil.getIntFromJson(json, "installments", null));
        if (JsonUtil.getDoubleFromJson(json, "effective_annual_rate", null) != null)
            setEffectiveAnualRate(JsonUtil.getDoubleFromJson(json, "effective_annual_rate", null));
        if (JsonUtil.getDoubleFromJson(json, "effective_annual_cost_rate", null) != null)
            setEffectiveAnualCostRate(JsonUtil.getDoubleFromJson(json, "effective_annual_cost_rate", null));
        if (JsonUtil.getDoubleFromJson(json, "nominal_annual_rate", null) != null)
            setNominalAnualRate(JsonUtil.getDoubleFromJson(json, "nominal_annual_rate", null));
        if (JsonUtil.getDoubleFromJson(json, "installment_amount_avg", null) != null)
            setInstallmentAmountAvg(JsonUtil.getDoubleFromJson(json, "installment_amount_avg", null));
        if (JsonUtil.getIntFromJson(json, "product_id", null) != null)
            setProduct(catalogService.getProduct(JsonUtil.getIntFromJson(json, "product_id", null)));
        if (JsonUtil.getIntFromJson(json, "entity_id", null) != null)
            setEntity(catalogService.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        if (JsonUtil.getIntFromJson(json, "employer_id", null) != null)
            setEmployer(catalogService.getEmployer(JsonUtil.getIntFromJson(json, "employer_id", null)));
        if (JsonUtil.getDoubleFromJson(json, "entity_commission", null) != null)
            setEntityCommission(JsonUtil.getDoubleFromJson(json, "entity_commission", null));

        String entityNames = "";
        List<Integer> entitiesId;
        if (json.optJSONArray("preliminary_evaluation_entities") != null) {
            entitiesId = JsonUtil.getListFromJsonArray(json.optJSONArray("preliminary_evaluation_entities"), (arr, i) -> arr.getInt(i));
            for (Integer entityId : entitiesId) {
                entityNames = entityNames.concat("/");
                entityNames = entityNames.concat(catalogService.getEntity(entityId).getShortName());
            }
            setPreliminaryEvaluationEntities(entityNames);
        }

        if (json.optJSONArray("offer_entities") != null) {
            entitiesId = JsonUtil.getListFromJsonArray(json.getJSONArray("offer_entities"), (arr, i) -> arr.getInt(i));
            entityNames = "";
            for (Integer entityId : entitiesId) {
                entityNames = entityNames.concat("/");
                entityNames = entityNames.concat(catalogService.getEntity(entityId).getShortName());
            }
            setOfferEntities(entityNames);
        }

        if(JsonUtil.getIntFromJson(json, "activity_type_id", null) != null)
            setActivityType(catalogService.getActivityType(locale, JsonUtil.getIntFromJson(json, "activity_type_id", null)));
        if(JsonUtil.getStringFromJson(json, "company_name", null) != null)
            setCompanyName(JsonUtil.getStringFromJson(json, "company_name", null));
        if(JsonUtil.getIntFromJson(json, "employment_time", null)!= null)
            setEmploymentTime(JsonUtil.getIntFromJson(json, "employment_time", null));
        if(JsonUtil.getDoubleFromJson(json, "income", null) != null)
            setIncome(JsonUtil.getDoubleFromJson(json, "income", null));
        if(JsonUtil.getIntFromJson(json, "reason_id", null) != null)
            setLoanApplicationReason(catalogService.getLoanApplicationReason(locale, JsonUtil.getIntFromJson(json, "reason_id", null)));
        if(JsonUtil.getStringFromJson(json, "gender", null) != null)
            setGender(JsonUtil.getStringFromJson(json, "gender", null));
        if(JsonUtil.getPostgresDateFromJson(json, "birthday", null) != null)
            setBirthday(JsonUtil.getPostgresDateFromJson(json, "birthday", null));
        if(JsonUtil.getStringFromJson(json, "ubigeo_id", null) != null)
            setUbigeo(catalogService.getUbigeo(JsonUtil.getStringFromJson(json, "ubigeo_id", null)));
        if(JsonUtil.getIntFromJson(json, "marital_status_id", null) != null)
            setMaritalStatus(catalogService.getMaritalStatus(locale, JsonUtil.getIntFromJson(json, "marital_status_id", null)));
        StudyLevel studyLevel;
        if(JsonUtil.getIntFromJson(json, "study_level_id", null) != null){
            studyLevel = catalogService.getStudyLevel(locale, JsonUtil.getIntFromJson(json, "study_level_id", null));
            if(studyLevel != null) setStudyLevel(studyLevel);
        }

        if(JsonUtil.getStringFromJson(json, "score", null) != null)
            setScore(JsonUtil.getStringFromJson(json, "score", null));
        if (JsonUtil.getStringFromJson(json, "utm_source", null) != null)
            setSource(JsonUtil.getStringFromJson(json, "utm_source", null));

        if (JsonUtil.getStringFromJson(json, "utm_medium", null) != null)
            setMedium(JsonUtil.getStringFromJson(json, "utm_medium", null));

        if (JsonUtil.getStringFromJson(json, "utm_campaign", null) != null)
            setCampaign(JsonUtil.getStringFromJson(json, "utm_campaign", null));

        if (JsonUtil.getStringFromJson(json, "utm_term", null) != null)
            setTerm(JsonUtil.getStringFromJson(json, "utm_term", null));

        if (JsonUtil.getStringFromJson(json, "utm_content", null) != null)
            setContent(JsonUtil.getStringFromJson(json, "utm_content", null));

        if (JsonUtil.getIntFromJson(json, "nationality_id", null) != null)
            setNationality(catalogService.getNationality(locale, JsonUtil.getIntFromJson(json, "nationality_id", null)));
        setCountry(catalogService.getCountryParam(JsonUtil.getIntFromJson(json, "country_id", null)));
        setAssistedProcess(JsonUtil.getBooleanFromJson(json, "assisted_process", false));
        setDisbursementTime(JsonUtil.getIntFromJson(json, "disbursement_time", null));
        if (JsonUtil.getIntFromJson(json, "entity_user_id", null) == null)
            setRealizedBy("Usuario");
        else
            setRealizedBy("Funcionario");
        if(JsonUtil.getBooleanFromJson(json, "with_gclid", null) != null)
            setGclid(JsonUtil.getBooleanFromJson(json, "with_gclid", null) ? "Sí" : "No");
    }

    public Date getOriginationDate() {
        return originationDate;
    }

    public void setOriginationDate(Date originationDate) {
        this.originationDate = originationDate;
    }

    public String getLoanApplicationCode() {
        return loanApplicationCode;
    }

    public void setLoanApplicationCode(String loanApplicationCode) {
        this.loanApplicationCode = loanApplicationCode;
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

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public Double getLoanCapital() {
        return loanCapital;
    }

    public void setLoanCapital(Double loanCapital) {
        this.loanCapital = loanCapital;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Double getEffectiveAnualRate() {
        return effectiveAnualRate;
    }

    public void setEffectiveAnualRate(Double effectiveAnualRate) {
        this.effectiveAnualRate = effectiveAnualRate;
    }

    public Double getEffectiveAnualCostRate() {
        return effectiveAnualCostRate;
    }

    public void setEffectiveAnualCostRate(Double effectiveAnualCostRate) {
        this.effectiveAnualCostRate = effectiveAnualCostRate;
    }

    public Double getInstallmentAmountAvg() {
        return installmentAmountAvg;
    }

    public void setInstallmentAmountAvg(Double installmentAmountAvg) {
        this.installmentAmountAvg = installmentAmountAvg;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Double getEntityCommission() {
        return entityCommission;
    }

    public void setEntityCommission(Double entityCommission) {
        this.entityCommission = entityCommission;
    }

    public String getPreliminaryEvaluationEntities() {
        return preliminaryEvaluationEntities;
    }

    public void setPreliminaryEvaluationEntities(String preliminaryEvaluationEntities) {
        this.preliminaryEvaluationEntities = preliminaryEvaluationEntities;
    }

    public String getOfferEntities() {
        return offerEntities;
    }

    public void setOfferEntities(String offerEntities) {
        this.offerEntities = offerEntities;
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

    public Integer getEmploymentTime() {
        return employmentTime;
    }

    public void setEmploymentTime(Integer employmentTime) {
        this.employmentTime = employmentTime;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public LoanApplicationReason getLoanApplicationReason() {
        return loanApplicationReason;
    }

    public void setLoanApplicationReason(LoanApplicationReason loanApplicationReason) {
        this.loanApplicationReason = loanApplicationReason;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Ubigeo getUbigeo() {
        return ubigeo;
    }

    public void setUbigeo(Ubigeo ubigeo) {
        this.ubigeo = ubigeo;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public StudyLevel getStudyLevel() {
        return studyLevel;
    }

    public void setStudyLevel(StudyLevel studyLevel) {
        this.studyLevel = studyLevel;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public IdentityDocumentType getIdentityDocumentType() {
        return identityDocumentType;
    }

    public void setIdentityDocumentType(IdentityDocumentType identityDocumentType) {
        this.identityDocumentType = identityDocumentType;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    public Double getNominalAnualRate() {
        return nominalAnualRate;
    }

    public void setNominalAnualRate(Double nominalAnualRate) {
        this.nominalAnualRate = nominalAnualRate;
    }

    public Integer getDisbursementTime() {
        return disbursementTime;
    }

    public void setDisbursementTime(Integer disbursementTime) {
        this.disbursementTime = disbursementTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Nationality getNationality() {
        return nationality;
    }

    public void setNationality(Nationality nationality) {
        this.nationality = nationality;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public void setRealizedBy(String realizedBy) { this.realizedBy = realizedBy; }

    public String getRealizedBy() { return this.realizedBy; }

    public CountryParam getCountry() {
        return country;
    }

    public void setCountry(CountryParam country) {
        this.country = country;
    }

    public Boolean getAssistedProcess() { return assistedProcess; }

    public void setAssistedProcess(Boolean assistedProcess) { this.assistedProcess = assistedProcess; }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGclid() {
        return gclid;
    }

    public void setGclid(String gclid) {
        this.gclid = gclid;
    }
}
