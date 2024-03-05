package com.affirm.backoffice.model;

import com.affirm.backoffice.util.IPaginationWrapperElement;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.LoanOffer;
import com.affirm.common.model.transactional.ReturningReason;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */
public class LoanApplicationSummaryBoPainter implements IPaginationWrapperElement {

    private Integer rowNumber;
    private Integer id;
    private Integer personId;
    private Date laRegisterDate;
    private Date loRegisterDate;
    private Integer userId;
    private String code;
    private IdentityDocumentType documentType;
    private String documentNumber;
    private String personName;
    private String personFirstSurname;
    private String personLastSurname;
    private Ubigeo ubigeo;
    private ActivityType activityType;
    private Product product;
    private Double amount;
    private Integer installments;
    private Integer loanDays;
    private Integer creditAnalystSysUserId;
    private String analystPersonName;
    private String analystFirstSurname;
    private String analystLastSurname;
    private String cluster;
    private LoanApplicationStatus status;
    private Integer score;
    private String ballColor;
    private String lineColor;
    private Boolean filesUploaded;
    private Date filesUploadedTime;
    private List<LoanOffer> offers;
    private Date updatedTime;
    private Character origin;
    private Integer creditId;
    private Credit credit;
    private ProductCategory productCategory;
    private Entity entity;
    private Integer userFileSpeechId;
    private Integer userFileWelcomeId;
    private Integer managementAnalystId;
    private String managementAnalyst;
    private Date scheduledDate;
    private String phoneNumber;
    private CountryParam country;
    private Integer entityUserId;
    private String trackingType;
    private Integer creditSubStatusId;
    private CreditSubStatus creditSubStatus;
    private Boolean assistedProcess;
    private Date assistedProcessScheduledCallingDate;
    private ProcessQuestion currentProcessQuestion;
    private Date lastTrackingActionDate;
    private Boolean isBranded;
    private Integer employerId;
    private Employer employer;
    private List<ReturningReason> returningReasons;
    private Integer countInteractions;
    private Date maxTrackingActionDate;
    private EntityProductParams entityProductParams;
    private Integer nonDisbursedDays;
    private String email;
    private Integer actionsCount;
    private Integer noContactCount;
    private String currentQuestionDescription;
    private Date lastClientActionDate;
    private Boolean isObserved;
    private String observedComment;
    private List<Integer> approvalQueryBotIds;
    private Integer lastApprovalBotStatusId;
    private String lastErrorMessageApprovalBot;
    private Entity selectedEntity;
    private Double approvedAmount;
    private Integer approvedInstallments;

    public LoanApplicationSummaryBoPainter() {
    }

    @Override
    public void fillFromDb(JSONObject json, CatalogService catalog, MessageSource messageSource, Locale locale) throws Exception {
        setRowNumber(JsonUtil.getIntFromJson(json, "row_number", null));
        setId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setUserId(JsonUtil.getIntFromJson(json, "user_id", null));
        setCode(JsonUtil.getStringFromJson(json, "loan_application_code", null));
        setLaRegisterDate(JsonUtil.getPostgresDateFromJson(json, "la_register_date", null));
        setLoRegisterDate(JsonUtil.getPostgresDateFromJson(json, "lo_register_date", null));
        if (JsonUtil.getIntFromJson(json, "document_id", null) != null) {
            setDocumentType(catalog.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_id", null)));
        }
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setPersonName(JsonUtil.getStringFromJson(json, "person_name", null));
        setPersonFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setPersonLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        String ubigeoId;
        if ((ubigeoId = JsonUtil.getStringFromJson(json, "ubigeo_id", null)) != null) {
            setUbigeo(catalog.getUbigeo(ubigeoId));
        }
        if (JsonUtil.getIntFromJson(json, "activity_type_id", null) != null) {
            setActivityType(catalog.getActivityType(locale, JsonUtil.getIntFromJson(json, "activity_type_id", null)));
        }

        if (JsonUtil.getIntFromJson(json, "product_id", null) != null) {
            setProduct(catalog.getProduct(JsonUtil.getIntFromJson(json, "product_id", null)));
        }
        setAmount(JsonUtil.getDoubleFromJson(json, "amount", null));
        setInstallments(JsonUtil.getIntFromJson(json, "installments", null));
        setLoanDays(JsonUtil.getIntFromJson(json, "loan_days", null));
        setCreditAnalystSysUserId(JsonUtil.getIntFromJson(json, "credit_analyst_sysuser_id", null));
        setAnalystPersonName(JsonUtil.getStringFromJson(json, "analyst_person_name", null));
        setAnalystFirstSurname(JsonUtil.getStringFromJson(json, "analyst_first_surname", null));
        setAnalystLastSurname(JsonUtil.getStringFromJson(json, "analyst_last_surname", null));
        setCluster(JsonUtil.getStringFromJson(json, "cluster", null));
        setStatus(catalog.getLoanApplicationStatus(locale, JsonUtil.getIntFromJson(json, "application_status_id", null)));
        setScore(JsonUtil.getIntFromJson(json, "score", null));
        setBallColor(JsonUtil.getStringFromJson(json, "ball_color", null));
        setLineColor(JsonUtil.getStringFromJson(json, "line_color", null));
        setFilesUploaded(JsonUtil.getBooleanFromJson(json, "filesUploaded", null));
        setFilesUploadedTime(JsonUtil.getPostgresDateFromJson(json, "filesUploadedTime", null));
        if (JsonUtil.getJsonArrayFromJson(json, "loan_offer", null) != null) {
            offers = new ArrayList<>();
            for (int i = 0; i < json.getJSONArray("loan_offer").length(); i++) {
                LoanOffer offer = new LoanOffer();
                offer.fillFromDb(json.getJSONArray("loan_offer").getJSONObject(i), catalog);
                offers.add(offer);
            }
        }
        setUpdatedTime(JsonUtil.getPostgresDateFromJson(json, "updated_time", null));
        setOrigin(JsonUtil.getCharacterFromJson(json, "application_origin", null));
        setCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
        if (JsonUtil.getIntFromJson(json, "category_id", null) != null) {
            setProductCategory(catalog.getCatalogById(ProductCategory.class, (Object) JsonUtil.getIntFromJson(json, "category_id", null), locale));
        }
        if (JsonUtil.getIntFromJson(json, "entity_id", null) != null) {
            setEntity(catalog.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        }
        setEntityUserId(JsonUtil.getIntFromJson(json, "entity_user_id", null));
        setFilesUploaded(JsonUtil.getBooleanFromJson(json, "files_uploaded", null));
        setUserFileSpeechId(JsonUtil.getIntFromJson(json, "com_user_files_id", null));
        setUserFileWelcomeId(JsonUtil.getIntFromJson(json, "wlc_user_files_id", null));
        setManagementAnalystId(JsonUtil.getIntFromJson(json, "tracker_sysuser_id", null));
        setManagementAnalyst(JsonUtil.getStringFromJson(json, "tracker_username", null));
        setScheduledDate(JsonUtil.getPostgresDateFromJson(json, "scheduled_date", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        setCountry(catalog.getCountryParam(JsonUtil.getIntFromJson(json, "country_id", null)));
        setTrackingType(JsonUtil.getStringFromJson(json, "tracking_type", null));
        setCreditSubStatusId(JsonUtil.getIntFromJson(json, "credit_sub_status_id", null));
        if (getCreditSubStatusId() != null) {
            setCreditSubStatus(catalog.getCreditSubStatus(locale, getCreditSubStatusId()));
        }
        setAssistedProcess(JsonUtil.getBooleanFromJson(json, "assisted_process", null));
        setAssistedProcessScheduledCallingDate(JsonUtil.getPostgresDateFromJson(json, "assisted_process_scheduled_calling_date", null));
        if (JsonUtil.getIntFromJson(json, "current_process_question_id", null) != null) {
            setCurrentProcessQuestion(catalog.getProcessQuestion(JsonUtil.getIntFromJson(json, "current_process_question_id", null)));
            if (getCurrentProcessQuestion() != null && messageSource != null) {
                setCurrentQuestionDescription(messageSource.getMessage("process.question.id." + getCurrentProcessQuestion().getId(), null, Configuration.getDefaultLocale()));
            }
        }
        setLastTrackingActionDate(JsonUtil.getPostgresDateFromJson(json, "last_action_date", null));
        setBranded(JsonUtil.getBooleanFromJson(json, "is_branded", false));
        setEmployerId(JsonUtil.getIntFromJson(json, "employer_id", null));
        if (employerId != null) {
            setEmployer(catalog.getEmployer(employerId));
        }
        if (JsonUtil.getJsonArrayFromJson(json, "js_returning_reason_id", null) != null) {
            JSONArray array = JsonUtil.getJsonArrayFromJson(json, "js_returning_reason_id", null);
            returningReasons = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                returningReasons.add(catalog.getReturningReasonById(array.getInt(i)));
            }
        }
        setCountInteractions(JsonUtil.getIntFromJson(json, "count_interactions", 0));
        setMaxTrackingActionDate(JsonUtil.getPostgresDateFromJson(json, "max_tracking_action_date", null));

        Integer entityParamId = JsonUtil.getIntFromJson(json, "selected_entity_product_parameter_id", null);
        if (null != entityParamId) {
            setEntityProductParams(catalog.getEntityProductParamById(entityParamId));
        }
        setNonDisbursedDays(JsonUtil.getIntFromJson(json, "non_disbursed_days", null));
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        setNoContactCount(JsonUtil.getIntFromJson(json, "no_contact_count", 0));
        setActionsCount(JsonUtil.getIntFromJson(json, "actions_count", 0));
        setLastClientActionDate(JsonUtil.getPostgresDateFromJson(json, "last_client_action_date", null));
        setObserved(JsonUtil.getBooleanFromJson(json, "is_observed", null));
        setObservedComment(JsonUtil.getStringFromJson(json, "observed_comment", null));

        JSONArray approvalQueryBotIdsJson = JsonUtil.getJsonArrayFromJson(json, "js_approve_query_id", null);
        if (approvalQueryBotIdsJson != null) {
            setApprovalQueryBotIds(new ArrayList<>());

            for (int i = 0; i < approvalQueryBotIdsJson.length(); i++) {
                getApprovalQueryBotIds().add(approvalQueryBotIdsJson.getInt(i));
            }
        }
        if (JsonUtil.getIntFromJson(json, "selected_entity_id", null) != null) {
            setSelectedEntity(catalog.getEntity(JsonUtil.getIntFromJson(json, "selected_entity_id", null)));
        }
        setApprovedAmount(JsonUtil.getDoubleFromJson(json, "approved_amount", null));
        setApprovedInstallments(JsonUtil.getIntFromJson(json, "approved_installments", null));
    }

    public String getTooltipForReturningReasons() {
        if (returningReasons == null || returningReasons.isEmpty()) {
            return "";
        }
        String tootlip = "";
        for (ReturningReason reason : returningReasons) {
            tootlip = tootlip + reason.getReason() + "\n";
        }
        return tootlip;
    }

    public String getAnalystFullName() {
        String analystFullname = "";
        if (analystPersonName != null) {
            analystFullname = analystFullname + analystPersonName + " ";
        }
        if (analystFirstSurname != null) {
            analystFullname = analystFullname + analystFirstSurname + " ";
        }
        if (analystLastSurname != null) {
            analystFullname = analystFullname + analystLastSurname + " ";
        }
        return analystFullname;
    }

    public String getFullName() {
        String fullname = "";
        if (personName != null) {
            fullname = fullname + personName + " ";
        }
        if (personFirstSurname != null) {
            fullname = fullname + personFirstSurname + " ";
        }
        if (personLastSurname != null) {
            fullname = fullname + personLastSurname + " ";
        }
        return fullname;
    }

    public String getLoanApplicationAmmount(UtilService utilService) {
        String loanApplicationAmount = utilService.integerMoneyFormat(amount);
        if (installments != null) {
            loanApplicationAmount = loanApplicationAmount + " @ " + installments + "m";
        } else if (loanDays != null) {
            loanApplicationAmount = loanApplicationAmount + " @ " + loanDays + "d";
        }
        return loanApplicationAmount;
    }

    public String getLoanApplicationAmmounts(UtilService utilService, String symbol, String separator) {
        String loanApplicationAmounts = utilService.integerMoneyFormat(amount, symbol, separator);

        if (offers != null) {
            LoanOffer offerSelected = offers.stream().filter(LoanOffer::getSelected).findFirst().orElse(null);

            if (offerSelected != null) {
                loanApplicationAmounts += " / " + utilService.integerMoneyFormat(offerSelected.getAmmount(), symbol, separator);
            }
        }

        return loanApplicationAmounts;
    }

    public String getLoanApplicationAmmount(UtilService utilService, String symbol, String separator) {
        String loanApplicationAmount = utilService.integerMoneyFormat(amount, symbol, separator);
        if (installments != null) {
            loanApplicationAmount = loanApplicationAmount + " @ " + installments + "m";
        } else if (installments != null) {
            loanApplicationAmount = loanApplicationAmount + " @ " + installments + "d";
        }
        return loanApplicationAmount;
    }

    public String getLoanApplicationApprovedAmount(UtilService utilService, String symbol, String separator) {
        String loanApplicationApprovedAmount = utilService.integerMoneyFormat(approvedAmount, symbol, separator);
        if (approvedInstallments != null) {
            loanApplicationApprovedAmount = loanApplicationApprovedAmount + " @ " + approvedInstallments + "m";
        } else if (approvedInstallments != null) {
            loanApplicationApprovedAmount = loanApplicationApprovedAmount + " @ " + approvedInstallments + "d";
        }
        return loanApplicationApprovedAmount;
    }

    public String getOriginName() {
        if (origin != null) {
            return origin == 'W' ? "Web" : (origin == 'M' ? "Messenger" : "");
        }
        return null;
    }

    public String getApplicantType() {
        return (entityUserId == null) ? "U" : "F";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Date getLaRegisterDate() {
        return laRegisterDate;
    }

    public void setLaRegisterDate(Date laRegisterDate) {
        this.laRegisterDate = laRegisterDate;
    }

    public Date getLoRegisterDate() {
        return loRegisterDate;
    }

    public void setLoRegisterDate(Date loRegisterDate) {
        this.loRegisterDate = loRegisterDate;
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

    public String getPersonFirstSurname() {
        return personFirstSurname;
    }

    public void setPersonFirstSurname(String personFirstSurname) {
        this.personFirstSurname = personFirstSurname;
    }

    public String getPersonLastSurname() {
        return personLastSurname;
    }

    public void setPersonLastSurname(String personLastSurname) {
        this.personLastSurname = personLastSurname;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getCreditAnalystSysUserId() {
        return creditAnalystSysUserId;
    }

    public void setCreditAnalystSysUserId(Integer creditAnalystSysUserId) {
        this.creditAnalystSysUserId = creditAnalystSysUserId;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getBallColor() {
        return ballColor;
    }

    public void setBallColor(String ballColor) {
        this.ballColor = ballColor;
    }

    public String getLineColor() {
        return lineColor;
    }

    public void setLineColor(String lineColor) {
        this.lineColor = lineColor;
    }

    public List<LoanOffer> getOffers() {
        return offers;
    }

    public void setOffers(List<LoanOffer> offers) {
        this.offers = offers;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Integer getLoanDays() {
        return loanDays;
    }

    public void setLoanDays(Integer loanDays) {
        this.loanDays = loanDays;
    }

    public LoanApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(LoanApplicationStatus status) {
        this.status = status;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Boolean getFilesUploaded() {
        return filesUploaded;
    }

    public void setFilesUploaded(Boolean filesUploaded) {
        this.filesUploaded = filesUploaded;
    }

    public Date getFilesUploadedTime() {
        return filesUploadedTime;
    }

    public void setFilesUploadedTime(Date filesUploadedTime) {
        this.filesUploadedTime = filesUploadedTime;
    }

    public String getAnalystPersonName() {
        return analystPersonName;
    }

    public void setAnalystPersonName(String analystPersonName) {
        this.analystPersonName = analystPersonName;
    }

    public String getAnalystFirstSurname() {
        return analystFirstSurname;
    }

    public void setAnalystFirstSurname(String analystFirstSurname) {
        this.analystFirstSurname = analystFirstSurname;
    }

    public String getAnalystLastSurname() {
        return analystLastSurname;
    }

    public void setAnalystLastSurname(String analystLastSurname) {
        this.analystLastSurname = analystLastSurname;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Character getOrigin() {
        return origin;
    }

    public void setOrigin(Character origin) {
        this.origin = origin;
    }

    public Integer getCreditId() {
        return creditId;
    }

    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
    }

    public Credit getCredit() {
        return credit;
    }

    public void setCredit(Credit credit) {
        this.credit = credit;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setEntityUserId(Integer entityUserId) {
        this.entityUserId = entityUserId;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public Integer getUserFileSpeechId() {
        return userFileSpeechId;
    }

    public void setUserFileSpeechId(Integer userFileSpeechId) {
        this.userFileSpeechId = userFileSpeechId;
    }

    public Integer getUserFileWelcomeId() {
        return userFileWelcomeId;
    }

    public void setUserFileWelcomeId(Integer userFileWelcomeId) {
        this.userFileWelcomeId = userFileWelcomeId;
    }

    public String getManagementAnalyst() {
        return managementAnalyst;
    }

    public void setManagementAnalyst(String managementAnalyst) {
        this.managementAnalyst = managementAnalyst;
    }


    public Integer getManagementAnalystId() {
        return managementAnalystId;
    }

    public void setManagementAnalystId(Integer managementAnalystId) {
        this.managementAnalystId = managementAnalystId;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public CountryParam getCountry() {
        return country;
    }

    public void setCountry(CountryParam country) {
        this.country = country;
    }

    public String getTrackingType() {
        return trackingType;
    }

    public void setTrackingType(String trackingType) {
        this.trackingType = trackingType;
    }

    public Integer getCreditSubStatusId() {
        return creditSubStatusId;
    }

    public void setCreditSubStatusId(Integer creditSubStatusId) {
        this.creditSubStatusId = creditSubStatusId;
    }

    public CreditSubStatus getCreditSubStatus() {
        return creditSubStatus;
    }

    public void setCreditSubStatus(CreditSubStatus creditSubStatus) {
        this.creditSubStatus = creditSubStatus;
    }

    public Boolean getAssistedProcess() {
        return assistedProcess;
    }

    public void setAssistedProcess(Boolean assistedProcess) {
        this.assistedProcess = assistedProcess;
    }

    public Date getAssistedProcessScheduledCallingDate() {
        return assistedProcessScheduledCallingDate;
    }

    public void setAssistedProcessScheduledCallingDate(Date assistedProcessScheduledCallingDate) {
        this.assistedProcessScheduledCallingDate = assistedProcessScheduledCallingDate;
    }

    public ProcessQuestion getCurrentProcessQuestion() {
        return currentProcessQuestion;
    }

    public void setCurrentProcessQuestion(ProcessQuestion currentProcessQuestion) {
        this.currentProcessQuestion = currentProcessQuestion;
    }

    public Date getLastTrackingActionDate() {
        return lastTrackingActionDate;
    }

    public void setLastTrackingActionDate(Date lastTrackingActionDate) {
        this.lastTrackingActionDate = lastTrackingActionDate;
    }

    public String isBranded() {
        return isBranded ? "Brandeada" : "Marketplace";
    }

    public Boolean getBranded() {
        return isBranded;
    }

    public void setBranded(Boolean branded) {
        isBranded = branded;
    }

    public Integer getEmployerId() {
        return employerId;
    }

    public void setEmployerId(Integer employerId) {
        this.employerId = employerId;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    public List<ReturningReason> getReturningReasons() {
        return returningReasons;
    }

    public void setReturningReasons(List<ReturningReason> returningReasons) {
        this.returningReasons = returningReasons;
    }

    public Integer getCountInteractions() {
        return countInteractions;
    }

    public void setCountInteractions(Integer countInteractions) {
        this.countInteractions = countInteractions;
    }

    public Integer getNonDisbursedDays() {
        return nonDisbursedDays;
    }

    public void setNonDisbursedDays(Integer nonDisbursedDays) {
        this.nonDisbursedDays = nonDisbursedDays;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getActionsCount() {
        return actionsCount;
    }

    public void setActionsCount(Integer actionsCount) {
        this.actionsCount = actionsCount;
    }

    public Integer getNoContactCount() {
        return noContactCount;
    }

    public void setNoContactCount(Integer noContactCount) {
        this.noContactCount = noContactCount;
    }

    public String getCurrentQuestionDescription() {
        return currentQuestionDescription;
    }

    public EntityProductParams getEntityProductParams() {
        return entityProductParams;
    }

    public void setEntityProductParams(EntityProductParams entityProductParams) {
        this.entityProductParams = entityProductParams;
    }

    public void setCurrentQuestionDescription(String currentQuestionDescription) {
        this.currentQuestionDescription = currentQuestionDescription;
    }

    public Date getMaxTrackingActionDate() {
        return maxTrackingActionDate;
    }

    public void setMaxTrackingActionDate(Date maxTrackingActionDate) {
        this.maxTrackingActionDate = maxTrackingActionDate;
    }

    public Date getLastClientActionDate() {
        return lastClientActionDate;
    }

    public void setLastClientActionDate(Date lastClientActionDate) {
        this.lastClientActionDate = lastClientActionDate;
    }

    public Boolean getObserved() {
        return isObserved;
    }

    public void setObserved(Boolean observed) {
        isObserved = observed;
    }

    public String getObservedComment() {
        return observedComment;
    }

    public void setObservedComment(String observedComment) {
        this.observedComment = observedComment;
    }

    public Integer getLastApprovalBotStatusId() {
        return lastApprovalBotStatusId;
    }

    public void setLastApprovalBotStatusId(Integer lastApprovalBotStatusId) {
        this.lastApprovalBotStatusId = lastApprovalBotStatusId;
    }

    public String getLastErrorMessageApprovalBot() {
        return lastErrorMessageApprovalBot;
    }

    public void setLastErrorMessageApprovalBot(String lastErrorMessageApprovalBot) {
        this.lastErrorMessageApprovalBot = lastErrorMessageApprovalBot;
    }

    public List<Integer> getApprovalQueryBotIds() {
        return approvalQueryBotIds;
    }

    public void setApprovalQueryBotIds(List<Integer> approvalQueryBotIds) {
        this.approvalQueryBotIds = approvalQueryBotIds;
    }

    public Entity getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(Entity selectedEntity) {
        this.selectedEntity = selectedEntity;
    }

    public Double getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(Double approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public Integer getApprovedInstallments() {
        return approvedInstallments;
    }

    public void setApprovedInstallments(Integer approvedInstallments) {
        this.approvedInstallments = approvedInstallments;
    }
}

