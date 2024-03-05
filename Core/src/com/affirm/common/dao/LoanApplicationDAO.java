package com.affirm.common.dao;

import com.affirm.aws.RecognitionResultsPainter;
import com.affirm.common.model.AppointmentSchedule;
import com.affirm.common.model.PreApprovedInfo;
import com.affirm.common.model.transactional.*;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public interface LoanApplicationDAO {


    /**
     * Save the start of the preliminary evaluation
     *
     * @param loanApplicationId
     * @throws Exception
     */
    void startPreliminaryEvaluation(int loanApplicationId) throws Exception;

    LoanApplication registerLoanApplication(int userId, Integer ammount, Integer installments, Integer reasonId, Integer productId, Integer days, Integer clusterId, char originChar, Integer employerId, Integer registerType, Integer entityId, int countryId) throws Exception;

    LoanApplicationPreliminaryEvaluation getLastPreliminaryEvaluation(int loanApplicationId, Locale locale, int database) throws Exception;

    LoanApplicationPreliminaryEvaluation getLastPreliminaryEvaluation(int loanApplicationId, Locale locale) throws Exception;

    @Cacheable
    List<EntityRate> getEntityRatesByProduct(Locale locale, Integer productId) throws Exception;

    /**
     * Return json with the evaluation_id and run_bureau
     *
     * @param loanApplicationId
     * @return
     * @throws Exception
     */
    JSONObject executeEvaluationPreBureau(int loanApplicationId, int productId, int entityId) throws Exception;

    /**
     * Return true/false if the offer should be generated
     *
     * @param loanApplicationId
     * @return
     * @throws Exception
     */
    boolean executeEvaluationBureau(int loanApplicationId, Integer loanApplicationEvaluationId) throws Exception;

    /**
     * Return the las evaluation of the LoanApplicationSummaryBoPainter
     *
     * @param loanApplicationId
     * @return
     * @throws Exception
     */
    LoanApplicationEvaluation getLastEvaluation(int loanApplicationId, Locale locale) throws Exception;

    LoanApplicationEvaluation getLastEvaluation(int loanApplicationId, Locale locale, int database) throws Exception;

    List<LoanOffer> createLoanOffers(int loanApplicationId) throws Exception;

    void updateTCEA(int loanOfferId, double effectiveAnnualCostRate) throws Exception;

    void updateTEA(int loanOfferId, double effectiveAnnualRate) throws Exception;

    void updateSolvenTCEA(int loanOfferId, double solvenEffectiveAnnualCostRate) throws Exception;

    /**
     * Updates the values for the LoanOffer
     *
     * @param loanApplicationId
     * @param ammount
     * @param installments
     * @param productId
     * @throws Exception
     */
    void updateLoanApplication(int loanApplicationId, Integer ammount, Integer installments, Integer productId, Integer loanDays) throws Exception;

    void registerSelectedLoanOffer(int loanOfferId, Date firtDueDate) throws Exception;

    void updateFirstDueDateWithSchedules(int loanApplicationId, Date firstDueDate) throws Exception;

    void updateFirstDueDate(int loanApplicationId, Date firstDueDate) throws Exception;

    /**
     * Register the aprobation of the user
     *
     * @param loanApplicationOfferId
     * @param
     * @throws Exception
     */
    void registerLoanApplicationSIgnature(int loanApplicationOfferId, String fullName, Integer docType, String docNumber)
            throws Exception;

    com.affirm.common.model.transactional.LoanApplication getLoanApplication(int loanApplicationId, Locale locale) throws Exception;

    LoanApplication getLoanApplicationLite(int loanApplicationId, Locale locale) throws Exception;

    <T extends LoanApplication> T getLoanApplication(int loanApplicationId, Locale locale, Class<T> returntype) throws Exception;

    void updateLoanApplicationStatus(int loanApplicationId, int loanApplicationStatusId, Integer sysUserId) throws Exception;

    <T extends LoanApplication> List<T> getLoanApplicationsByPerson(Locale locale, int personId, Class<T> returntype) throws Exception;

    <T extends LoanApplication> List<T> getLoanApplicationsByEntityUser(Locale locale, int entityUserId, Class<T> returntype) throws Exception;

    LoanApplication getActiveLoanApplicationByPerson(Locale locale, int personId, int productCategoryId) throws Exception;

    LoanApplication getActiveLoanApplicationByPerson(Locale locale, int personId, int productCategoryId, Integer entityId) throws Exception;

    <T extends LoanApplication> List<T> getActiveLoanApplicationsByPerson(Locale locale, int personId, Class<T> returntype) throws Exception;

    List<LoanOffer> getLoanOffers(int loanApplicationId) throws Exception;

    void assignanalyst(int loanApplicationId, int creditAnalystSysUserId, int sysUserId) throws Exception;

    Credit generateCredit(int loanApplicationId, Locale locale) throws Exception;

    void selectLoanOfferAnalyst(int loanOfferId, int sysUserId) throws Exception;

    LoanOffer createLoanOfferAnalyst(int loanApplicationId, double loanAmmount, int installments, int entityId, int productId, Integer employerId) throws Exception;

    LoanOffer createLoanOfferAnalyst(int loanApplicationId, double loanAmmount, int installments, int entityId, int productId, Integer employerId, Integer entityProductParameterId) throws Exception;

    void registerRejection(int loanApplicationId, Integer applicationRejectionReasonId) throws Exception;

    void registerRejectionWithComment(int loanApplicationId, Integer applicationRejectionReasonId, String applicationRejectionComment) throws Exception;

    Integer registerIntent(Integer documentType, String documentNumber) throws Exception;

    void updateHumanFormId(int loanApplicationId, int humanFormId) throws Exception;

    void updateLoanApplicationMood(int loanApplicationId, int mood) throws Exception;

    void registerIcarValidation(int loanApplicationId, JSONObject icarValidation) throws Exception;

    void registerBureauResult(int loanApplicationId, Integer score, String riskLevel, String conclusion, String equifaxResult, Integer bureauId) throws Exception;

    void registerEvaluationSuccess(int loanApplicationId) throws Exception;

    IcarValidation getIcarValidation(int loanApplicationId) throws Exception;

    void registerClickSignLink(int loanApplicationId) throws Exception;

    void registerNoAuthLinkExpiration(int loanApplicationId, Integer seconds) throws Exception;

    JSONObject preApprovalValidation(int loanApplicationId) throws Exception;

    void updateMessengerLink(int loanApplicationId, Boolean messengerLink) throws Exception;

    boolean shouldCallBots(int loanApplicationId) throws Exception;

    SalaryAdvanceCalculatedAmount calculateSalaryAdvanceAmmount(Integer evaluationId, int employeeId, int employerId, Locale locale) throws Exception;

    SalaryAdvanceCalculatedAmount calculateSalaryAdvanceAmmount(int employeeId, int employerId, Locale locale) throws Exception;

    void resetLoanApplication(Integer loanApplicationId) throws Exception;

    LoanOffer createLoanOffersSalaryAdvance(int loanApplicationId, double amount, double comission, int employerId) throws Exception;

    void registerIovationResponse(int loanApplicationId, JSONObject ioResponse) ;

    boolean mustCallIovation(int loanApplicationId) throws Exception;

    JSONObject getIovationByLoanApplication(Integer loanApplicationId) throws Exception;

    JSONArray getIovationByPerson(Integer personId) throws Exception;

    void registerConsolidationAccount(int loanApplicationId, int consolidationAccountTypeId, String entityCode, Double balance, Integer installments, Double rate, Boolean active, String accountCardNumber, Integer brandId, String ubigeoDepartment) ;

    List<ConsolidableDebt> getConsolidationAccounts(int loanApplicationId) throws Exception;

    Double getPreConsolidationMonthlyInstallment(int loanApplicationId) throws Exception;

    LoanApplicationUpdateParams getLoanApplicationUpdateParams(int loanApplicationId, Locale locale) throws Exception;

    void updateConsentNavGeolocation(int loanApplicationId, Boolean consentNavGeolocation) throws Exception;

    List<RecognitionResultsPainter> getRecognitionResults(int personId, Locale locale) throws Exception;

    void registerAmazonRekognition(Integer loanAplicationId, Double highSimilarity, String jsonSimilarities, Integer userFilesDNIIdA, Integer userFilesDNIIdB, Integer userFilesDNIIdMerged, Integer userFilesSelfieId);

    void registerAmazonRekognitionFacesLabels(Integer loanAplicationId, String jsonFaces, String jsonLabels);

    void updateRegisterType(int loanApplicationId, int registerType) throws Exception;


    SalaryAdvanceCalculatedAmount calculateAgreementAmmount(int employeeId, int employerId, Locale locale) throws Exception;

    Double getConsolidationSavings(int loanApplicationId) throws Exception;

    public List<LoanOffer> getLoanOffersAll(int loanApplicationId) throws Exception;

    void updateCurrentQuestion(int loanApplicationId, int questionId);

    void updateQuestionSequence(int loanApplicationId, String sequence);

    void updatePerson(int loanApplicationId, int personId, int userId);

    void updateReason(int loanApplicationId, Integer reasonId);

    void updateAmount(int loanApplicationId, Integer amount);

    void updateInstallments(int loanApplicationId, Integer installments);

    void updateUsage(int loanApplicationId, Integer usageId);

    void updateDownPayment(int loanApplicationId, Integer downPayment);

    void updateDownPaymentCurrency(int loanApplicationId, Integer downPaymentCurrencyId);

    void updateProductCategory(int loanApplicationId, Integer productCategory) throws Exception;

    void updateFormAssistant(int loanApplicationId, Integer formAssistant) throws Exception;

    void registerLoanApplicationReclosure(int loanApplicationId, int entityId, boolean isReclosurable, double previousCreditAmount) throws Exception;

    void updateLoanApplicationFromSelfEvaluation(int selfEvaluationId, int loanApplicationId) throws Exception;

    void updateEntityId(int loanApplicationId, Integer selectedEntityId) throws Exception;

    void updateProductId(int loanApplicationId, Integer productId) throws Exception;

    void registerEFLSession(int loanApplicacionId, String eflSessionUid)throws Exception;

    void updateEFLSession(int loanApplicacionId, Double score, String jsonResult, String scoreConnfidence)throws Exception;

    void executeEFLEvaluation(int loanApplicationId) throws Exception;

    void updateEntityUser(int loanApplicationId, Integer entityUserId) throws Exception;

    void updateLoanOfferGeneratedStatus(int loanApplicationId, Boolean generatedOffer) throws Exception;

    void updateVehicleId(int loanApplicationId, Integer vehicleId) ;

    List<LoanApplicationPreliminaryEvaluation> getPreliminaryEvaluations(int loanApplicationId, Locale locale) throws Exception;

    List<LoanApplicationPreliminaryEvaluation> getPreliminaryEvaluations(int loanApplicationId, Locale locale, int database) throws Exception;

    void updateEntityApplicationExpirationDate(int loanApplicationId, Date entityApplicationExpirationDate);

    void updateEntityApplicationCode(int loanApplicationId, String entityApplicationCode);

    Integer insertLoanOffer(int loanApplicationId, int personId, LoanOffer loanOffer, Integer entityProductParameterId);

    void insertLoanOfferSchedule(int loanOfferId, String jsonSchedule);

    void updateOffersQueryBotId(int loanApplicationId, Integer offersQueryBotId);

    void updateEvaluationStep(int evaluationId, int policyId, Date expirationDate);

    void updatePreliminaryEvaluationStep(int preliminaryEvaluationId, int hardFilterId, String hardFilterMessage, Integer hardFilterHelpMessage, Date expirationDate);

    List<LoanApplicationEvaluation> getEvaluations(int loanApplicationId, Locale locale) throws Exception;

    List<LoanApplicationEvaluation> getEvaluations(int loanApplicationId, Locale locale, int database) throws Exception;

    LoanApplication getLoanApplicationByEntityApplicationCode(String entityApplicationCode, Locale locale) throws Exception;

    List<UserFile> getLoanApplicationUserFiles(Integer loanApplicationId) throws Exception;

    void assignManagementAnalyst(int loanApplicationId, int creditAnalystSysUserId, int sysUserId) throws Exception;

    void unassignManagementAnalyst(int loanApplicationId) throws Exception;

    void registerTrackingAction(Integer loanApplicationId,
                                Integer userId,
                                Integer actionId,
                                Integer detail,
                                Date scheduleDate) throws Exception;

    void registerTrackingActionContactPerson(Integer loanApplicationId, Integer userId, Integer actionId, Integer detail, Date scheduleDate, Boolean personAnswerCall, Integer contactRelationship) throws Exception;

    List<LoanApplicationTrackingAction> getLoanApplicationTrackingActions(int loanApplicationId) throws Exception;

    void reportMissingDocumentation(Integer loanApplicationId) throws Exception;

    Pair<Boolean, Boolean> getEflQuestionConfiguration(Integer loanApplicationId) throws Exception;

    void updateLoanApplicationChangeRate(Integer loanApplicationId, Double changeRate, Integer entityId) throws Exception;

    void updateDebtnessValues(Integer loanApplicationId) throws Exception;

    LoanApplicationEvaluationsProcess getLoanApplicationEvaluationsProcess(Integer loanApplicationId) ;

    void updateLoanApplicationEvaluationProcessEvaluationBot(int loanApplicationId, List<Integer> botIds);

    void updateLoanApplicationEvaluationProcessEvaluationStatus(int loanApplicationId, Character status);

    boolean entityWsHasResult(Integer loanApplicationId, Integer entityId) throws Exception;

    void updateLoanApplicationEvaluationProcessPreEvaluationStatus(int loanApplicationId, Character status);

    void updateSourceMediumCampaign(Integer loanApplicationId, String source, String medium, String campaign) throws Exception;

    void updateTermContent(Integer loanApplicationId, String term, String content) throws Exception;

    void updateGAClientID(Integer loanApplicationId, String gaClientID);
    void updateUserAgent(Integer loanApplicationId, String userAgent);

    void updateLoanApplicationFilesUploaded(Integer loanApplicationId, boolean isUploaded);

    void updatePercentageProgress(Integer loanApplicationId, double percentage);

    void updatePercentageRemoveProgress(Integer loanApplicationId);

    void updateEvaluationProcessReadyPreEvaluation(int loanApplicationId, boolean ready);

    void updateEvaluationProcessReadyEvaluation(int loanApplicationId, boolean ready);

    JSONArray getAdwordsConversions(Date from, Date to);

    void updateGoogleClickId(int loanApplicationId, String gclid);

    EntityCustomParamConfig getEntityCustomParamsConfig(int loanApplicationId);

    void updateEvaluationProcessSendDelayedEmail(int loanApplicationId, Boolean sendDelayedEmail);

    void updateEntityCustomData(int loanApplicationId, JSONObject entityCustomData);

    void generateSynthesized(String documentNumber);

    void updateEvaluationProcessSynthesizedStatus(int loanApplicationId, Character status);

    void registerAssistedProcessSchedule(int loanApplicationId, Date scheduledDate);

    Integer registerApplicationBureauLog(int bureauId, Integer loanApplicationId, Date startDate, Date finishDate, char status, String request, String response);

    void updateApplicationBureauLogResponse(int bureauId, String response);

    void updateApplicationBureauLogRequest(int bureauId, String request);

    void updateApplicationBureauLogFinishDate(int bureauId, Date finishDate);

    void updateApplicationBureauLogStatus(int bureaId, char status);

    List<ApplicationBureau> getBureauResults(Integer loanApplicationId) throws Exception;

    List <ExperianResult> getExperianResultList(Integer loanApplicationId) throws Exception;

    void updateApplicationOfferRejection(int loanApplicationId, Integer offerRejectionReasonId);

    List<LoanOffer> getAllLoanOffers(int loanApplicationId) throws Exception;

    List<LoanOffer> getComparableLoanOffers(int loanApplicationId) throws Exception;

    List<EntityProductEvaluationsProcess> getEntityProductEvaluationProceses(int loanApplicationId);

    void updateLoanApplicationPreliminaryEvaluationStatus(Integer loanApplicationId, Integer entityId, Integer productId, char status);

    void updateLoanApplicationEvaluationStatus(Integer loanApplicationId, Integer entityId, Integer productId, char status);

    void registerGuaranteedVehicle(Integer loanApplicationId, Integer guaranteedVehicleId, String plate);

    void updateLoanApplicationEvaluationStartDate(int loanApplicationId, Date date);

    List<AppointmentSchedule> getAvailableDates() throws Exception;

    String registerAppointmentSchedule(int loanApplicationId, Date dateSelected, int appointmentScheduleId);

    void registerAppointmentSchedule(int loanApplicationId, Date dateSelected, int appointmentScheduleId, String appointmentPlace);

    List<ReferredLead> getReferredLeadByLoanApplicationId(int loanApplicationId) throws Exception;

    void registerReferredLead(int loanApplicationId, int entityId);

    void updateSmsSent(int loanApplicationId);

    void updateSmsSent(int loanApplicationId, Boolean sent);

    RescueScreenParams getRescueScreenParams(int loanApplicationId);

    HashMap<String, HashMap<String, List>> getUtmParameters();

    List<PreApprovedInfo> getApprovedLoanApplication(Integer loanApplicationId, Locale locale) throws Exception;

    <T extends LoanApplication> List<T> getLoanApplicationByEntityUser(int entityUserId, Class<T> returntype) throws Exception;

    Integer getMaxPreapprovedAmount(Integer loanApplicationId) throws Exception;

    void approvedDataLoanApplication(Boolean approved, Integer entityId, Integer loanApplicationId, Integer entityProductParameterId) throws Exception;

    void updateDisableTracking(int loanApplicationId, boolean disabletracking);

    void updateQuestionFlow(int loanApplicationId, Character questionflow);

    void updateLeadParams(Integer loanApplicationId, JSONObject jsonParams);

    List<LeadLoanApplication> getLeadLoanApplicationsByEntityAndDate(Integer entityId, Date date) throws Exception;

    List<LeadLoanApplication> getApprovedLeadLoanApplications(Integer entityId) throws Exception;

    LeadLoanApplication getLeadLoanApplicationByLoanApplicationId(Integer loanApplicationId) throws Exception;

    void registerLeadsLoanApplication(Integer loanApplicationId, Integer prodId, Integer activityId) ;

    void registerLoanApplicationComment(Integer loanApplicationId, String comment, Integer sysUserId, String commentType, Integer entityUserId);

    void updateLoanApplicationComment(Integer loanApplicationId, String comment, Integer sysUserId, String commentType, Integer entityUserId);

    void registerLoanApplicationLiftComment(Integer loanApplicationId);

    void updateHardFilterMessage(int loanApplicationId, String hardFilterMessage) throws Exception;

    void updateExpirationDate(int loanApplicationId, Date expirationDate) throws Exception;

    void saveLastActiveOneSignalPlayerId(int loanApplicationId, JSONObject notificationsTokens) throws Exception;

    void updateLoanOfferSignaturePinValidation(int loanApplicationId, Boolean pinvalidation) throws Exception;

    JSONObject validateSignaturePin(int loanOfferId, String pin) throws Exception;

    void updateSelectedEntityId(int loanApplicationId, Integer selectedEntityId) throws Exception;

    void updateSelectedProductId(int loanApplicationId, Integer selectedProductId) throws Exception;

    void updateSelectedEntityProductParamId(int loanApplicationId, Integer selectedEntityProductParamId) throws Exception;

    void selectBestLoanOffer(Integer loanApplicationId) throws Exception;

    void generateOfferSchedule(Integer loanOfferId) throws Exception;

    Comment getLoanApplicationComment(Integer loanApplicationId, String commentType) throws Exception;

    List<Comment> getLoanApplicationComments(Integer loanApplicationId, String commentType) throws Exception;

    Integer getBureauExecutedCountByApplication(Integer loanApplicationId, int bureauId);

    void updateReferrerPersonId(Integer loanApplicationId, Integer referrerPersonId);

    void updatePolicyMessage(int loanApplicationId, String policyMessage) throws Exception;

    void updateDisposableIncome(int loanApplicationId, double disposableIncome) throws Exception;

    void updateFraudAlertQueryBots(int loanApplicationId, String fraudAlertQueries);

    void expireLoanApplication(int loanApplicationId);

    void boResetContract (int loanApplicationId) throws Exception;

    void updateApprovalQueryBotIds(int loanApplicationId, String approveQueryBotIds);

    <T extends LoanApplication> T getLoanApplicationLite(int loanApplicationId, Locale locale, Class<T> returntype) throws Exception;

    void reevaluate(String loanApplicationIds);

    void reactivateApplication(int loanApplicationId, int loanApplicationStatusId);

    void setFirstDueDateToNull(int loanApplicationId);

    void generateOfferScheduleAccesoPromo(int loanApplicationId) throws Exception;

    void updateOfferInstallments(int offerId, Integer installments);

    void updateEntityProductParameterId(int loanApplicationId, int entityProductParameter) throws Exception;

    void updateWarmiProcessId(int loanApplicationId, String warmiProcessId);

    void reevaluateButKeepBureaus(String loanApplicationIds);

    void updateFunnelStep(int loanApplicationId, List<LoanApplicationFunnelStep> steps);

    void updateAuxData(int loanApplicationId, LoanApplicationAuxData auxData);

    void updateAssignedEntityUserId(int loanApplicationId, Integer entityUserId, Integer asignerEntityUserId);

    List<LoanApplicationTrackingAction> getLoanApplicationTrackingActionsByTrackingId(int loanApplicationId, Integer[] trackingIds, Integer referralId) throws Exception;
    
    Boolean hasStatusInLog(int loanApplicationId, int applicationStatusId);
    
    void registerTrackingActionContactPerson(Integer loanApplicationId, Integer userId, Integer actionId, Integer detail, Date scheduleDate, Boolean personAnswerCall, Integer contactRelationship, Integer entityUserId, Integer referralId, Integer userFileId) throws Exception;

    void updateApprovalValidations(int loanApplicationId, List<LoanApplicationApprovalValidation> validations);

    void registerLoanApplicationComment(Integer loanApplicationId, String comment, Integer sysUserId, String commentType);

    void updateLoanApplicationComment(Integer loanApplicationId, String comment, Integer sysUserId, String commentType);

    void selectLoanOfferAnalystExtranet(int loanOfferId, int extranetUserId) throws Exception;

    void updateLoanApplicationCode(Integer loanApplicationId, String code);

    List<LoanApplication> getLoansApplicationByCollectionId(Locale locale, int productCategoryId, Integer entityId, Long collectionId) throws Exception;

    void updateLoanOfferFirstDueDate(Integer offerId, Date firstDueDate);

    void removeLoanOffers(int loanApplicationId) throws Exception;

    void updateLoanOfferEntityCustomData(int loanOfferId, JSONObject jsonObject);

    void updateOfferOriginalScheduleData(int loanOfferId, int installmentId, Date dueDate, Double installmentAmount, Double installmentCapital, Double interest, Double insurance, Double remainingCapital);

    void updateLoanOfferInstallmentAmountAndAvg(int loanOfferId, Double installmentAmount, Double installmentAmountAvg);

    void expireBanbifNewBaseLoans() throws Exception;

    List<LoanApplication> getLoansApplicationByCollectionIds(Locale locale, int productCategoryId, Integer entityId, List<Long> collectionIds) throws Exception;
    
    List<Integer> getLoanToSendBanbifKonectaLead() throws Exception;

    PreLoanApplication insertPreLoanApplication(int documentType, String documentNumber, Integer entityId, Integer categoryId) throws Exception;
}