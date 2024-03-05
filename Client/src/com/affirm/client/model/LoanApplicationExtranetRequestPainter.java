package com.affirm.client.model;

import com.affirm.aws.RecognitionResultsPainter;
import com.affirm.common.model.PersonInteractionExtranetPainter;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.affirm.security.model.SysUser;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */
public class LoanApplicationExtranetRequestPainter extends LoanApplication {

    public final static String EFL_GREEN = "Green";
    public final static String EFL_RED = "Red";
    public final static String EFL_YELLOW = "Yellow";

    private String personName;
    private String personFirstSurname;
    private String personLastSurname;
    private boolean showExpanded = false; //Shows expanded the tab of the loan application
    private Integer recognitionHighSimilarity;
    private LoanApplicationUserFiles userFilesObjectList;
    private RecognitionResultsPainter recognition;
    private boolean showInteractions = false;
    private boolean showFiles = false;
    private boolean showRecognition = false;
    private boolean showFraudAlerts = false;
    private boolean showMissingDocumentationButton = false;
    private boolean showNotes = false;
    private boolean showPepOfac = false;
    private long notNeedToReview;
    private long needToReview;
    private SysUser creditAnalyst;
    private List<AuditRejectionReason> auditRejectionReasons;
    private List<LoanApplicationFraudAlert> loanApplicationFraudAlerts;
    private Date lastFraudAlertsBotRegisterDate;
    private Integer lastFraudAlertsBotStatusId;
    private List<LoanApplicationFraudAlert> loanApplicationFraudAlertsLogs;
    private boolean loadingOffers = false;
    private List<LoanApplicationPreliminaryEvaluation> preliminaryEvaluations;
    private List<LoanApplicationEvaluation> evaluations;
    private Integer lastApprovalBotStatusId;
    private String lastErrorMessageApprovalBot;
    PersonInteractionExtranetPainter interactions;
    private List<MatiResult> matiResults;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        super.fillFromDb(json, catalog, locale);
        setPersonName(JsonUtil.getStringFromJson(json, "person_name", null));
        setPersonFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setPersonLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        setRecognitionHighSimilarity(JsonUtil.getIntFromJson(json, "high_similarity", null));
    }

    public Integer getSelectedOfferEntityByClient() {
        if (getOffers() != null) {
            LoanOffer selectedClientOffer = getOffers().stream().filter(o -> o.getSelectedByClient() != null && o.getSelectedByClient()).findFirst().orElse(null);
            if (selectedClientOffer != null)
                return selectedClientOffer.getEntityId();
        }
        return null;
    }

    public LoanOffer getSelectedLoanOffer() {
        if (getOffers() != null) {
            return getOffers().stream().filter(o -> o.getSelected() != null && o.getSelected()).findFirst().orElse(null);
        }
        return null;
    }

    public EntityProductParams getSelectedLoanOfferEntityProductParam() {
        LoanOffer selectedOffer = getSelectedLoanOffer();
        if (selectedOffer != null) {
            return selectedOffer.getEntityProductParam();
        }
        return null;
    }

    public String getGuaranteedVehicleDescription() {
        if (getGuaranteedVehicleBrand() == null)
            return null;
        return getGuaranteedVehicleBrand().getBrand() + " / " + getGuaranteedVehicleModel() + " / " + getGuaranteedVehicleYear();
    }

    public boolean isWaitingForApproval() {
        return this.getStatus().getId() == LoanApplicationStatus.WAITING_APPROVAL;
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

    public boolean isShowExpanded() {
        return showExpanded;
    }

    public void setShowExpanded(boolean showExpanded) {
        this.showExpanded = showExpanded;
    }

    public Integer getRecognitionHighSimilarity() {
        return recognitionHighSimilarity;
    }

    public void setRecognitionHighSimilarity(Integer recognitionHighSimilarity) {
        this.recognitionHighSimilarity = recognitionHighSimilarity;
    }

    public LoanApplicationUserFiles getUserFilesObjectList() {
        return userFilesObjectList;
    }

    public void setUserFilesObjectList(LoanApplicationUserFiles userFilesObjectList) {
        this.userFilesObjectList = userFilesObjectList;
    }

    public RecognitionResultsPainter getRecognition() {
        return recognition;
    }

    public void setRecognition(RecognitionResultsPainter recognition) {
        this.recognition = recognition;
    }

    public boolean isShowInteractions() {
        return showInteractions;
    }

    public void setShowInteractions(boolean showInteractions) {
        this.showInteractions = showInteractions;
    }

    public boolean isShowFiles() {
        return showFiles;
    }

    public void setShowFiles(boolean showFiles) {
        this.showFiles = showFiles;
    }

    public boolean isShowRecognition() {
        return showRecognition;
    }

    public void setShowRecognition(boolean showRecognition) {
        this.showRecognition = showRecognition;
    }

    public List<AuditRejectionReason> getAuditRejectionReasons() {
        return auditRejectionReasons;
    }

    public void setAuditRejectionReasons(List<AuditRejectionReason> auditRejectionReasons) {
        this.auditRejectionReasons = auditRejectionReasons;
    }

    public List<LoanApplicationFraudAlert> getLoanApplicationFraudAlerts() {
        return loanApplicationFraudAlerts;
    }

    public void setLoanApplicationFraudAlerts(List<LoanApplicationFraudAlert> loanApplicationFraudAlerts) {
        this.loanApplicationFraudAlerts = loanApplicationFraudAlerts;
    }

    public Date getLastFraudAlertsBotRegisterDate() {
        return lastFraudAlertsBotRegisterDate;
    }

    public void setLastFraudAlertsBotRegisterDate(Date lastFraudAlertsBotRegisterDate) {
        this.lastFraudAlertsBotRegisterDate = lastFraudAlertsBotRegisterDate;
    }

    public Integer getLastFraudAlertsBotStatusId() {
        return lastFraudAlertsBotStatusId;
    }

    public void setLastFraudAlertsBotStatusId(Integer lastFraudAlertsBotStatusId) {
        this.lastFraudAlertsBotStatusId = lastFraudAlertsBotStatusId;
    }

    public List<LoanApplicationFraudAlert> getLoanApplicationFraudAlertsLogs() {
        return loanApplicationFraudAlertsLogs;
    }

    public void setLoanApplicationFraudAlertsLogs(List<LoanApplicationFraudAlert> loanApplicationFraudAlertsLogs) {
        this.loanApplicationFraudAlertsLogs = loanApplicationFraudAlertsLogs;
    }

    public boolean isShowFraudAlerts() {
        return showFraudAlerts;
    }

    public void setShowFraudAlerts(boolean showFraudAlerts) {
        this.showFraudAlerts = showFraudAlerts;
    }

    public long getNotNeedToReview() {
        return notNeedToReview;
    }

    public void setNotNeedToReview(long notNeedToReview) {
        this.notNeedToReview = notNeedToReview;
    }

    public long getNeedToReview() {
        return needToReview;
    }

    public void setNeedToReview(long needToReview) {
        this.needToReview = needToReview;
    }

    public SysUser getCreditAnalyst() {
        return creditAnalyst;
    }

    public void setCreditAnalyst(SysUser creditAnalyst) {
        this.creditAnalyst = creditAnalyst;
    }

    public boolean isLoadingOffers() {
        return loadingOffers;
    }

    public void setLoadingOffers(boolean loadingOffers) {
        this.loadingOffers = loadingOffers;
    }

    public boolean isShowMissingDocumentationButton() {
        return showMissingDocumentationButton;
    }

    public void setShowMissingDocumentationButton(boolean showMissingDocumentationButton) {
        this.showMissingDocumentationButton = showMissingDocumentationButton;
    }

    public boolean isShowNotes() {
        return showNotes;
    }

    public void setShowNotes(boolean showNotes) {
        this.showNotes = showNotes;
    }

    public boolean isShowPepOfac() {
        return showPepOfac;
    }

    public void setShowPepOfac(boolean showPepOfac) {
        this.showPepOfac = showPepOfac;
    }

    public List<LoanApplicationPreliminaryEvaluation> getPreliminaryEvaluations() {
        return preliminaryEvaluations;
    }

    public void setPreliminaryEvaluations(List<LoanApplicationPreliminaryEvaluation> preliminaryEvaluations) {
        this.preliminaryEvaluations = preliminaryEvaluations;
    }

    public List<LoanApplicationEvaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<LoanApplicationEvaluation> evaluations) {
        this.evaluations = evaluations;
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

    public PersonInteractionExtranetPainter getInteractions() {
        return interactions;
    }

    public void setInteractions(PersonInteractionExtranetPainter interactions) {
        this.interactions = interactions;
    }

    public Integer getLastRekognitionValue(){
        if(recognition == null || recognition.getResults() == null || recognition.getResults().isEmpty()) return null;
        return recognition.getResults().get(recognition.getResults().size() -1).getHighSimilarity();
    }

    public List<MatiResult> getMatiResults() {
        return matiResults;
    }

    public void setMatiResults(List<MatiResult> matiResults) {
        this.matiResults = matiResults;
    }
}
