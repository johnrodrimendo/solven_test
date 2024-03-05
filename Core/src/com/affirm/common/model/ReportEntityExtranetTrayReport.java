package com.affirm.common.model;

import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.HardFilter;
import com.affirm.common.model.transactional.LoanGatewayPaymentMethod;
import com.affirm.common.model.transactional.Policy;
import com.affirm.common.model.transactional.Referral;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ReportEntityExtranetTrayReport {

    private Integer loanApplicationId;
    private CountryParam country;
    private String loanApplicationCode;
    private Date registerDate;
    private IdentityDocumentType identityDocumentType;
    private String documentNumber;
    private String surname;
    private String personName;
    private Date birthday;
    private String gender;
    private Nationality nationality;
    private MaritalStatus maritalStatus;
    private Integer dependents;
    private StudyLevel studyLevel;
    private Profession profession;
    private HousingType housingType;
    private String streetName;
    private Ubigeo ubigeo;
    private ActivityType activityType;
    private Ocupation ocupation;
    private Integer employmentTime;
    private String ruc;
    private String companyName;
    private String companyAddress;
    private Double income;
    private String pep;
    private IdentityDocumentType partnerIdentityDocumentType;
    private String partnerDocumentNumber;
    private String partnerSurname;
    private String partnerPersonName;
    private JSONObject entityCustomData;
    private List<Referral> referrals;
    private LoanApplicationReason reason;
    private Double amount;
    private Integer installments;
    private OfferRejectionReason offerRejectionReason;
    private Product product;
    private Double selectedAmount;
    private Integer selectedInstallments;
    private Double selectedEffectiveAnnualRate;

    private Date lastStatusUpdate;
    private LoanApplicationStatus applicationStatus;
    private CreditStatus creditStatus;
    private DisbursementType disbursementType;
    private Date disbursementDate;


    private String phoneNumber;
    private String agencyName;
    private String email;
    private Integer step1;
    private Integer step2;
    private Integer step3;
    private Integer step4;
    private Integer step5;
    private Integer step6;
    private Integer step7;
    private Integer step8;
    private Integer step9;
    private Integer step10;
    private Integer step11;
    private Integer step12;
    private Integer step13;
    private Integer step14;
    private Integer step15;

    private Double navLatitude;
    private Double navLongitude;

    private String cci;
    private String bankAccountNUmber;
    private Bank bank;

    private LoanApplicationStatus loanApplicationStatus;
    private List<LoanGatewayPaymentMethod> loanGatewayPaymentMethods;
    private String creditCode;

    private LoanApplicationReason loanApplicationReason;
    private List<Policy> policiesRejected;
    private List<HardFilter> hardFiltersRejected;

    private CreditRejectionReason rejectionReason;
    private ApplicationRejectionReason loanApplicationRejectionReason;


    public void fillFromDb(JSONObject json, CatalogService catalogService, Locale locale) throws Exception {
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setCountry(catalogService.getCountryParam(JsonUtil.getIntFromJson(json, "country_id", null)));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setLoanApplicationCode(JsonUtil.getStringFromJson(json, "loan_application_code", null));
        if(JsonUtil.getIntFromJson(json, "document_id", null) != null)
            setIdentityDocumentType(catalogService.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_id", null)));
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setSurname(JsonUtil.getStringFromJson(json, "surname", null));
        setPersonName(JsonUtil.getStringFromJson(json, "person_name", null));
        setBirthday(JsonUtil.getPostgresDateFromJson(json, "birthday", null));
        setGender(JsonUtil.getStringFromJson(json, "gender", null));
        if(JsonUtil.getIntFromJson(json, "nationality_id", null) != null)
            setNationality(catalogService.getNationality(locale, JsonUtil.getIntFromJson(json, "nationality_id", null)));
        if(JsonUtil.getIntFromJson(json, "marital_status_id", null) != null)
            setMaritalStatus(catalogService.getMaritalStatus(locale, JsonUtil.getIntFromJson(json, "marital_status_id", null)));
        setDependents(JsonUtil.getIntFromJson(json, "dependents", null));
        if(JsonUtil.getIntFromJson(json, "study_level_id", null) != null)
            setStudyLevel(catalogService.getStudyLevel(locale, JsonUtil.getIntFromJson(json, "study_level_id", null)));
        if(JsonUtil.getIntFromJson(json, "profession_id", null) != null)
            setProfession(catalogService.getProfession(locale, JsonUtil.getIntFromJson(json, "profession_id", null)));
        if(JsonUtil.getIntFromJson(json, "housing_type_id", null) != null)
            setHousingType(catalogService.getHousingType(locale, JsonUtil.getIntFromJson(json, "housing_type_id", null)));
        setStreetName(JsonUtil.getStringFromJson(json, "street_name", null));
        if(JsonUtil.getStringFromJson(json, "ubigeo_id", null) != null)
            setUbigeo(catalogService.getUbigeo(JsonUtil.getStringFromJson(json, "ubigeo_id", null)));
        if(JsonUtil.getIntFromJson(json, "activity_type_id", null) != null)
            setActivityType(catalogService.getActivityType(locale, JsonUtil.getIntFromJson(json, "activity_type_id", null)));
        if(JsonUtil.getIntFromJson(json, "ocupation_id", null) != null)
            setOcupation(catalogService.getOcupation(locale, JsonUtil.getIntFromJson(json, "ocupation_id", null)));
        setEmploymentTime(JsonUtil.getIntFromJson(json, "employment_time", null));
        setRuc(JsonUtil.getStringFromJson(json, "ruc", null));
        setCompanyName(JsonUtil.getStringFromJson(json, "company_name", null));
        setCompanyAddress(JsonUtil.getStringFromJson(json, "company_address", null));
        setIncome(JsonUtil.getDoubleFromJson(json, "incomes", null));
        if(JsonUtil.getBooleanFromJson(json, "pep", null) != null)
            setPep(JsonUtil.getBooleanFromJson(json, "pep", null) ? "Sí" : "No");
        setPartnerDocumentNumber(JsonUtil.getStringFromJson(json, "partner_document_number", null));
        setPartnerSurname(JsonUtil.getStringFromJson(json, "partner_person_name", null));
        setPartnerPersonName(JsonUtil.getStringFromJson(json, "partner_surname", null));
        setEntityCustomData(JsonUtil.getJsonObjectFromJson(json, "entity_custom_data", new JSONObject()));
        JSONArray referrals = JsonUtil.getJsonArrayFromJson(json, "referrals", null);
        List<Referral> referralList = new ArrayList<>();
        if(referrals != null && referrals.length() > 0){
            for(int i = 0; i < referrals.length(); i++){
                Referral referral = new Referral();
                referral.fillFromDb(referrals.getJSONObject(i), catalogService, locale);
                referralList.add(referral);
            }
            setReferrals(referralList);
        }
        if(JsonUtil.getIntFromJson(json, "reason_id", null)!=null)
            setReason(catalogService.getLoanApplicationReason(locale, JsonUtil.getIntFromJson(json, "reason_id", null)));
        setAmount(JsonUtil.getDoubleFromJson(json, "amount", null));
        setInstallments(JsonUtil.getIntFromJson(json, "installments", null));
        if(JsonUtil.getIntFromJson(json, "product_id", null) != null)
            setProduct(catalogService.getProduct(JsonUtil.getIntFromJson(json, "product_id", null)));
        setSelectedAmount(JsonUtil.getDoubleFromJson(json, "selected_amount", null));
        setSelectedInstallments(JsonUtil.getIntFromJson(json, "selected_installments", null));
        setSelectedEffectiveAnnualRate(JsonUtil.getDoubleFromJson(json, "selected_effective_annual_rate", null));
        if(JsonUtil.getIntFromJson(json, "offer_rejection_id", null) != null)
            setOfferRejectionReason(catalogService.getOfferRejectionReason(JsonUtil.getIntFromJson(json, "offer_rejection_id", null)));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        setStep1(JsonUtil.getIntFromJson(json, "step_1", null));
        setStep2(JsonUtil.getIntFromJson(json, "step_2", null));
        setStep3(JsonUtil.getIntFromJson(json, "step_3", null));
        setStep4(JsonUtil.getIntFromJson(json, "step_4", null));
        setStep5(JsonUtil.getIntFromJson(json, "step_5", null));
        setStep6(JsonUtil.getIntFromJson(json, "step_6", null));
        setStep7(JsonUtil.getIntFromJson(json, "step_7", null));
        setStep8(JsonUtil.getIntFromJson(json, "step_8", null));
        setStep9(JsonUtil.getIntFromJson(json, "step_9", null));
        setStep10(JsonUtil.getIntFromJson(json, "step_10", null));
        setStep11(JsonUtil.getIntFromJson(json, "step_11", null));
        setStep12(JsonUtil.getIntFromJson(json, "step_12", null));
        setStep13(JsonUtil.getIntFromJson(json, "step_13", null));
        setStep14(JsonUtil.getIntFromJson(json, "step_14", null));
        setStep15(JsonUtil.getIntFromJson(json, "step_15", null));
        if(this.entityCustomData != null) setAgencyName(JsonUtil.getStringFromJson(this.entityCustomData , "agencyName", null));
        setLastStatusUpdate(JsonUtil.getPostgresDateFromJson(json, "last_status_update", null));
        if(JsonUtil.getIntFromJson(json, "application_status_id", null) != null) setApplicationStatus(catalogService.getLoanApplicationStatus(Configuration.getDefaultLocale(),JsonUtil.getIntFromJson(json, "application_status_id", null)));
        if(JsonUtil.getIntFromJson(json, "credit_status_id", null) != null) setCreditStatus(catalogService.getCreditStatus(Configuration.getDefaultLocale(),JsonUtil.getIntFromJson(json, "credit_status_id", null)));
        if(JsonUtil.getIntFromJson(json, "disbursement_type_id", null) != null) setDisbursementType(catalogService.getDisbursementTypeById(JsonUtil.getIntFromJson(json, "disbursement_type_id", null)));
        setDisbursementDate(JsonUtil.getPostgresDateFromJson(json, "disbursement_date", null));
        setNavLatitude(JsonUtil.getDoubleFromJson(json, "nav_latitude", null));
        setNavLongitude(JsonUtil.getDoubleFromJson(json, "nav_longitude", null));
        if(JsonUtil.getIntFromJson(json, "partner_document_type", null) != null)
            setPartnerIdentityDocumentType(catalogService.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "partner_document_type", null)));
        setCci(JsonUtil.getStringFromJson(json, "cci", null));
        setBankAccountNUmber(JsonUtil.getStringFromJson(json, "account_number", null));
        if(JsonUtil.getIntFromJson(json, "bank_id", null) != null) setBank(catalogService.getBank(JsonUtil.getIntFromJson(json, "bank_id", null)));

        setCreditCode(JsonUtil.getStringFromJson(json, "credit_code", null));
        if (JsonUtil.getIntFromJson(json, "credit_status_id", null) != null) setCreditStatus(catalogService.getCreditStatus(locale, JsonUtil.getIntFromJson(json, "credit_status_id", null)));
        if (JsonUtil.getJsonArrayFromJson(json, "loan_collection_payment_method", null) != null) {
            loanGatewayPaymentMethods = new ArrayList<>();
            for (int i = 0; i < json.getJSONArray("loan_collection_payment_method").length(); i++) {
                LoanGatewayPaymentMethod loanGatewayPaymentMethod = new LoanGatewayPaymentMethod();
                loanGatewayPaymentMethod.fillFromDb(json.getJSONArray("loan_collection_payment_method").getJSONObject(i), catalogService);
                loanGatewayPaymentMethods.add(loanGatewayPaymentMethod);
            }
        }

        if (JsonUtil.getJsonArrayFromJson(json, "evaluation_detail", null) != null) {
            List<Policy> policies = new ArrayList<>();
            JSONArray policiesJson = JsonUtil.getJsonArrayFromJson(json, "evaluation_detail", null);
            for (int i = 0; i < policiesJson.length(); i++) {
                if (JsonUtil.getIntFromJson((JSONObject) policiesJson.get(i), "policy_id", null) != null)
                    policies.add(catalogService.getPolicyById(
                            JsonUtil.getIntFromJson((JSONObject) policiesJson.get(i), "policy_id", null))
                    );
            }
            setPoliciesRejected(policies);
        }
        if (JsonUtil.getJsonArrayFromJson(json, "preliminary_evaluation_detail", null) != null) {
            List<HardFilter> hardFilters = new ArrayList<>();
            JSONArray hardFiltersJson = JsonUtil.getJsonArrayFromJson(json, "preliminary_evaluation_detail", null);
            for (int i = 0; i < hardFiltersJson.length(); i++) {
                if (JsonUtil.getIntFromJson((JSONObject) hardFiltersJson.get(i), "hard_filter_id", null) != null)
                    hardFilters.add(catalogService.getHardFilterById(
                            JsonUtil.getIntFromJson((JSONObject) hardFiltersJson.get(i), "hard_filter_id", null))
                    );
            }
            setHardFiltersRejected(hardFilters);
        }

        if (JsonUtil.getIntFromJson(json, "reason_id", null) != null) {
            setLoanApplicationReason(catalogService.getLoanApplicationReason(locale, JsonUtil.getIntFromJson(json, "reason_id", null)));
        }

        if (JsonUtil.getIntFromJson(json, "rejection_reason_id", null) != null) {
            setRejectionReason(catalogService.getCreditRejectionReason(JsonUtil.getIntFromJson(json, "rejection_reason_id", null)));
        }

        Integer loanRejectionId = JsonUtil.getIntFromJson(json, "rejection_loan_reason_id", null);
        if(loanRejectionId != null) setLoanApplicationRejectionReason(catalogService.getApplicationRejectionReason(loanRejectionId));

    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public CountryParam getCountry() {
        return country;
    }

    public void setCountry(CountryParam country) {
        this.country = country;
    }

    public String getLoanApplicationCode() {
        return loanApplicationCode;
    }

    public void setLoanApplicationCode(String loanApplicationCode) {
        this.loanApplicationCode = loanApplicationCode;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public IdentityDocumentType getIdentityDocumentType() {
        return identityDocumentType;
    }

    public void setIdentityDocumentType(IdentityDocumentType identityDocumentType) {
        this.identityDocumentType = identityDocumentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Nationality getNationality() {
        return nationality;
    }

    public void setNationality(Nationality nationality) {
        this.nationality = nationality;
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

    public StudyLevel getStudyLevel() {
        return studyLevel;
    }

    public void setStudyLevel(StudyLevel studyLevel) {
        this.studyLevel = studyLevel;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public HousingType getHousingType() {
        return housingType;
    }

    public void setHousingType(HousingType housingType) {
        this.housingType = housingType;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public Ubigeo getUbigeo() {
        return ubigeo;
    }

    public void setUbigeo(Ubigeo ubigeo) {
        this.ubigeo = ubigeo;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public Ocupation getOcupation() {
        return ocupation;
    }

    public void setOcupation(Ocupation ocupation) {
        this.ocupation = ocupation;
    }

    public Integer getEmploymentTime() {
        return employmentTime;
    }

    public void setEmploymentTime(Integer employmentTime) {
        this.employmentTime = employmentTime;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public String getPep() {
        return pep;
    }

    public void setPep(String pep) {
        this.pep = pep;
    }

    public String getPartnerDocumentNumber() {
        return partnerDocumentNumber;
    }

    public void setPartnerDocumentNumber(String partnerDocumentNumber) {
        this.partnerDocumentNumber = partnerDocumentNumber;
    }

    public String getPartnerSurname() {
        return partnerSurname;
    }

    public void setPartnerSurname(String partnerSurname) {
        this.partnerSurname = partnerSurname;
    }

    public String getPartnerPersonName() {
        return partnerPersonName;
    }

    public void setPartnerPersonName(String partnerPersonName) {
        this.partnerPersonName = partnerPersonName;
    }

    public JSONObject getEntityCustomData() {
        return entityCustomData;
    }

    public void setEntityCustomData(JSONObject entityCustomData) {
        this.entityCustomData = entityCustomData;
    }

    public List<Referral> getReferrals() {
        return referrals;
    }

    public void setReferrals(List<Referral> referrals) {
        this.referrals = referrals;
    }

    public LoanApplicationReason getReason() {
        return reason;
    }

    public void setReason(LoanApplicationReason reason) {
        this.reason = reason;
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

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Double getSelectedAmount() {
        return selectedAmount;
    }

    public void setSelectedAmount(Double selectedAmount) {
        this.selectedAmount = selectedAmount;
    }

    public Integer getSelectedInstallments() {
        return selectedInstallments;
    }

    public void setSelectedInstallments(Integer selectedInstallments) {
        this.selectedInstallments = selectedInstallments;
    }

    public Double getSelectedEffectiveAnnualRate() {
        return selectedEffectiveAnnualRate;
    }

    public void setSelectedEffectiveAnnualRate(Double selectedEffectiveAnnualRate) {
        this.selectedEffectiveAnnualRate = selectedEffectiveAnnualRate;
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

    public Integer getStep1() {
        return step1;
    }

    public void setStep1(Integer step1) {
        this.step1 = step1;
    }

    public Integer getStep2() {
        return step2;
    }

    public void setStep2(Integer step2) {
        this.step2 = step2;
    }

    public Integer getStep3() {
        return step3;
    }

    public void setStep3(Integer step3) {
        this.step3 = step3;
    }

    public Integer getStep4() {
        return step4;
    }

    public void setStep4(Integer step4) {
        this.step4 = step4;
    }

    public Integer getStep5() {
        return step5;
    }

    public void setStep5(Integer step5) {
        this.step5 = step5;
    }

    public Integer getStep6() {
        return step6;
    }

    public void setStep6(Integer step6) {
        this.step6 = step6;
    }

    public Integer getStep7() {
        return step7;
    }

    public void setStep7(Integer step7) {
        this.step7 = step7;
    }

    public Integer getStep8() {
        return step8;
    }

    public void setStep8(Integer step8) {
        this.step8 = step8;
    }

    public Integer getStep9() {
        return step9;
    }

    public void setStep9(Integer step9) {
        this.step9 = step9;
    }

    public Integer getStep10() {
        return step10;
    }

    public void setStep10(Integer step10) {
        this.step10 = step10;
    }

    public Integer getStep11() {
        return step11;
    }

    public void setStep11(Integer step11) {
        this.step11 = step11;
    }

    public Integer getStep12() {
        return step12;
    }

    public void setStep12(Integer step12) {
        this.step12 = step12;
    }

    public Integer getStep13() {
        return step13;
    }

    public void setStep13(Integer step13) {
        this.step13 = step13;
    }

    public Integer getStep14() {
        return step14;
    }

    public void setStep14(Integer step14) {
        this.step14 = step14;
    }

    public Integer getStep15() {
        return step15;
    }

    public void setStep15(Integer step15) {
        this.step15 = step15;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public OfferRejectionReason getOfferRejectionReason() {
        return offerRejectionReason;
    }

    public void setOfferRejectionReason(OfferRejectionReason offerRejectionReason) {
        this.offerRejectionReason = offerRejectionReason;
    }

    public Date getLastStatusUpdate() {
        return lastStatusUpdate;
    }

    public void setLastStatusUpdate(Date lastStatusUpdate) {
        this.lastStatusUpdate = lastStatusUpdate;
    }

    public LoanApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(LoanApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public CreditStatus getCreditStatus() {
        return creditStatus;
    }

    public void setCreditStatus(CreditStatus creditStatus) {
        this.creditStatus = creditStatus;
    }

    public DisbursementType getDisbursementType() {
        return disbursementType;
    }

    public void setDisbursementType(DisbursementType disbursementType) {
        this.disbursementType = disbursementType;
    }

    public Date getDisbursementDate() {
        return disbursementDate;
    }

    public void setDisbursementDate(Date disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public Double getNavLatitude() {
        return navLatitude;
    }

    public void setNavLatitude(Double navLatitude) {
        this.navLatitude = navLatitude;
    }

    public Double getNavLongitude() {
        return navLongitude;
    }

    public void setNavLongitude(Double navLongitude) {
        this.navLongitude = navLongitude;
    }

    public IdentityDocumentType getPartnerIdentityDocumentType() {
        return partnerIdentityDocumentType;
    }

    public void setPartnerIdentityDocumentType(IdentityDocumentType partnerIdentityDocumentType) {
        this.partnerIdentityDocumentType = partnerIdentityDocumentType;
    }

    public String getCci() {
        return cci;
    }

    public void setCci(String cci) {
        this.cci = cci;
    }

    public String getBankAccountNUmber() {
        return bankAccountNUmber;
    }

    public void setBankAccountNUmber(String bankAccountNUmber) {
        this.bankAccountNUmber = bankAccountNUmber;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public LoanGatewayPaymentMethod getPaidCollectionPaymentMethod(){
        return loanGatewayPaymentMethods != null && !loanGatewayPaymentMethods.isEmpty() ? loanGatewayPaymentMethods.stream().filter(e-> e.getPayed() != null && e.getPayed()).findFirst().orElse(null) : null;
    }

    public LoanApplicationStatus getLoanApplicationStatus() {
        return loanApplicationStatus;
    }

    public void setLoanApplicationStatus(LoanApplicationStatus loanApplicationStatus) {
        this.loanApplicationStatus = loanApplicationStatus;
    }

    public List<LoanGatewayPaymentMethod> getLoanCollectionPaymentMethods() {
        return loanGatewayPaymentMethods;
    }

    public void setLoanCollectionPaymentMethods(List<LoanGatewayPaymentMethod> loanGatewayPaymentMethods) {
        this.loanGatewayPaymentMethods = loanGatewayPaymentMethods;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getHardFilterOrPolicyMessageToShow() {
        if (getPoliciesRejected() != null && !getPoliciesRejected().isEmpty()) {
            return getPoliciesRejected().get(0).getPolicy();
        } else if (getHardFiltersRejected() != null && !getHardFiltersRejected().isEmpty()) {
            return getHardFiltersRejected().get(0).getHardFilterMessage();
        }
        return null;
    }

    public LoanApplicationReason getLoanApplicationReason() {
        return loanApplicationReason;
    }

    public void setLoanApplicationReason(LoanApplicationReason loanApplicationReason) {
        this.loanApplicationReason = loanApplicationReason;
    }

    public List<Policy> getPoliciesRejected() {
        return policiesRejected;
    }

    public void setPoliciesRejected(List<Policy> policiesRejected) {
        this.policiesRejected = policiesRejected;
    }

    public List<HardFilter> getHardFiltersRejected() {
        return hardFiltersRejected;
    }

    public void setHardFiltersRejected(List<HardFilter> hardFiltersRejected) {
        this.hardFiltersRejected = hardFiltersRejected;
    }

    public CreditRejectionReason getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(CreditRejectionReason rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public ApplicationRejectionReason getLoanApplicationRejectionReason() {
        return loanApplicationRejectionReason;
    }

    public void setLoanApplicationRejectionReason(ApplicationRejectionReason loanApplicationRejectionReason) {
        this.loanApplicationRejectionReason = loanApplicationRejectionReason;
    }
}
