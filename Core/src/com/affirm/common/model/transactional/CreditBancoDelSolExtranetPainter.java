package com.affirm.common.model.transactional;

import com.affirm.common.model.AfipActivitiy;
import com.affirm.common.model.catalog.ApplicationRejectionReason;
import com.affirm.common.model.catalog.MaritalStatus;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */
public class CreditBancoDelSolExtranetPainter extends Credit {

    private Integer creditId;
    private Integer loanApplicationId;
    private String code;
    private Date generationDate;
    private Boolean isPep;
    private Boolean isFacta;
    private String fullName;
    private String phone;
    private String mobile;
    private String email;
    private Date dateOfBirth;
    private String address;
    private String cbu;
    private MaritalStatus maritalStatus;
    private JSONObject entityCustomData;
    private Integer questionId;
    private List<LoanOffer> offers;
    private Boolean hasFiles;
    private ProductCategory productCategory;
    private Double percentageProgress;
    private ApplicationRejectionReason applicationRejectionReason;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        super.fillFromDb(json, catalog, locale);
        if (JsonUtil.getIntFromJson(json, "credit_id", null) != null) {
            setCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
        }
        if (JsonUtil.getBooleanFromJson(json, "pep", null) != null) {
            setPep(JsonUtil.getBooleanFromJson(json, "pep", null));
        }
        if (JsonUtil.getBooleanFromJson(json, "facta", null) != null) {
            setFacta(JsonUtil.getBooleanFromJson(json, "facta", null));
        }
        if (JsonUtil.getPostgresDateFromJson(json, "generation_date", null) != null) {
            setGenerationDate(JsonUtil.getPostgresDateFromJson(json, "generation_date", null));
        }
        if (JsonUtil.getPostgresDateFromJson(json, "date_of_birth", null) != null) {
            setDateOfBirth(JsonUtil.getPostgresDateFromJson(json, "date_of_birth", null));
        }
        if (JsonUtil.getStringFromJson(json, "code", null) != null) {
            setCode(JsonUtil.getStringFromJson(json, "code", null));
        }
        if (JsonUtil.getStringFromJson(json, "phone_number", null) != null) {
            setPhone(JsonUtil.getStringFromJson(json, "phone_number", null));
        }
        if (JsonUtil.getStringFromJson(json, "person_phone_number", null) != null) {
            setMobile(JsonUtil.getStringFromJson(json, "person_phone_number", null));
        }
        if (JsonUtil.getStringFromJson(json, "email", null) != null) {
            setEmail(JsonUtil.getStringFromJson(json, "email", null));
        }
        if (JsonUtil.getStringFromJson(json, "cci_code", null) != null) {
            setCbu(JsonUtil.getStringFromJson(json, "cci_code", null));
        }
        if (JsonUtil.getStringFromJson(json, "address", null) != null) {
            setAddress(JsonUtil.getStringFromJson(json, "address", null));
        }
        if (super.getPersonName() != null && super.getPersonFirstSurname() != null)
            setFullName(super.getPersonName() + " " + super.getPersonFirstSurname());
        if (JsonUtil.getIntFromJson(json, "marital_status_id", null) != null)
            setMaritalStatus(catalog.getMaritalStatus(locale, JsonUtil.getIntFromJson(json, "marital_status_id", null)));

        setEntityCustomData(JsonUtil.getJsonObjectFromJson(json, "entity_custom_data", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setQuestionId(JsonUtil.getIntFromJson(json, "current_process_question_id", null));
        if (JsonUtil.getJsonArrayFromJson(json, "loan_offer", null) != null) {
            offers = new ArrayList<>();
            for (int i = 0; i < json.getJSONArray("loan_offer").length(); i++) {
                LoanOffer offer = new LoanOffer();
                offer.fillFromDb(json.getJSONArray("loan_offer").getJSONObject(i), catalog);
                offers.add(offer);
            }
        }
        setHasFiles(JsonUtil.getBooleanFromJson(json , "has_files", false));
        if (JsonUtil.getIntFromJson(json, "category_id", null) != null) {
            setProductCategory(catalog.getCatalogById(ProductCategory.class, JsonUtil.getIntFromJson(json, "category_id", null), locale));
        }

        JSONArray jsonArray = JsonUtil.getJsonArrayFromJson(json, "percentage_progress", null);
        double percentage = 0.0;
        if (jsonArray != null) {

            if (jsonArray.length() > 0)
                percentage = jsonArray.getDouble(jsonArray.length() - 1);
        }
        setPercentageProgress(percentage);
        Integer loanRejectionId = JsonUtil.getIntFromJson(json, "rejection_loan_reason_id", null);
        if(loanRejectionId != null) setApplicationRejectionReason(catalog.getApplicationRejectionReason(loanRejectionId));

    }

    public String getHardFilterOrPolicyMessageToShow() {
        if (getPoliciesRejected() != null && !getPoliciesRejected().isEmpty()) {
            return getPoliciesRejected().get(0).getPolicy();
        } else if (getHardFiltersRejected() != null && !getHardFiltersRejected().isEmpty()) {
            return getHardFiltersRejected().get(0).getHardFilterMessage();
        }
        return null;
    }

    public String getBDSDestino() {
        if (entityCustomData == null)
            return null;
        return JsonUtil.getStringFromJson(entityCustomData, LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_DESTINATION.getKey(), null);
    }

    public String getBDSContacto() {
        if (entityCustomData == null)
            return null;
        return JsonUtil.getStringFromJson(entityCustomData, LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_REASON.getKey(), null);
    }

    public String getBDSTipoCliente() {
        if (entityCustomData == null)
            return null;
        return JsonUtil.getStringFromJson(entityCustomData, LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_CLIENT_TYPE.getKey(), null);
    }

    public String getBDSActividadAfip(CatalogService catalogService) {
        if (entityCustomData != null) {
            String afipActId = JsonUtil.getStringFromJson(entityCustomData, LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_ACTIVITY_TYPE.getKey(), null);
            if (afipActId != null) {
                Integer intAfipactivityId = Integer.parseInt(afipActId);
                AfipActivitiy afipActivitiy = catalogService.getAfipActivityById(intAfipactivityId);
                if (afipActivitiy != null)
                    return afipActivitiy.getDescription();
            }
        }
        return null;
    }

    public String getEntityCustomDataBancoDelSolInternalStatus() {
        if (entityCustomData == null)
            return "";

        return JsonUtil.getStringFromJson(entityCustomData, LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_INTERNAL_CREDIT_STATUS.getKey(), "");
    }

    public Date getGenerationDate() {
        return generationDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setGenerationDate(Date generationDate) {
        this.generationDate = generationDate;
    }

    public String getCbu() {
        return cbu;
    }

    public void setCbu(String cbu) {
        this.cbu = cbu;
    }

    @Override
    public String getCode() {
        return code;
    }

    public Integer getCreditId() {
        return creditId;
    }

    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getPep() {
        return isPep;
    }

    public void setPep(Boolean pep) {
        isPep = pep;
    }

    public Boolean getFacta() {
        return isFacta;
    }

    public void setFacta(Boolean facta) {
        isFacta = facta;
    }

    public boolean isPep() {
        return isPep;
    }

    public void setPep(boolean pep) {
        isPep = pep;
    }

    public boolean isFacta() {
        return isFacta;
    }

    public void setFacta(boolean facta) {
        isFacta = facta;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public JSONObject getEntityCustomData() {
        return entityCustomData;
    }

    public void setEntityCustomData(JSONObject entityCustomData) {
        this.entityCustomData = entityCustomData;
    }

    @Override
    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    @Override
    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public List<LoanOffer> getOffers() {
        return offers;
    }

    public void setOffers(List<LoanOffer> offers) {
        this.offers = offers;
    }

    public Boolean getHasFiles() {
        return hasFiles;
    }

    public void setHasFiles(Boolean hasFiles) {
        this.hasFiles = hasFiles;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }
    
    public Double getPercentageProgress() {
        return percentageProgress;
    }

    public void setPercentageProgress(Double percentageProgress) {
        this.percentageProgress = percentageProgress;
    }

    public ApplicationRejectionReason getApplicationRejectionReason() {
        return applicationRejectionReason;
    }

    public void setApplicationRejectionReason(ApplicationRejectionReason applicationRejectionReason) {
        this.applicationRejectionReason = applicationRejectionReason;
    }
}
