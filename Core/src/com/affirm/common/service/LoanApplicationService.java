package com.affirm.common.service;


import com.affirm.common.model.CreateLoanApplicationRequest;
import com.affirm.common.model.catalog.EntityBranding;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.catalog.HelpMessage;
import com.affirm.common.model.form.EntityExtranetCreateLoanApplicationForm;
import com.affirm.common.model.transactional.*;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author jrodriguez
 */
public interface LoanApplicationService {

    /**
     * Return the last valid Preliminary Evaluation
     *
     * @param loanApplicationId
     * @param locale
     * @return
     * @throws Exception
     */
    LoanApplicationPreliminaryEvaluation getLastPreliminaryEvaluation(int loanApplicationId, Locale locale, EntityBranding entityBranding)
            throws Exception;

    LoanApplicationPreliminaryEvaluation getLastPreliminaryEvaluation(int loanApplicationId, Locale locale, EntityBranding entityBranding, boolean followerDatabase)
            throws Exception;

    /**
     * Return the last valid Evaluation
     *
     * @param loanApplicationId
     * @param locale
     * @return
     * @throws Exception
     */
    LoanApplicationEvaluation getLastEvaluation(int loanApplicationId, int personId, Locale locale) throws Exception;

    LoanApplicationEvaluation getLastEvaluation(int loanApplicationId, int personId, Locale locale, boolean followerDatabase) throws Exception;

    boolean runEvaluationBot(int loanApplicationId, boolean createdFromEntityExtranet) throws Exception;

    boolean runEntityEvaluationBot(int loanApplicationId, int entityId, int productId, boolean isPreliminaryEvaluation, boolean isRetry) throws Exception;

    /**
     * Executes the Bureau
     *
     * @param evaluation_id
     * @param loanApplicationId
     * @param documentTypeId
     * @param documentNumber
     * @return
     * @throws Exception
     */
//    void runEquifax(int evaluation_id, int loanApplicationId, Integer documentTypeId, String documentNumber);

    /*
     * Updates the PersonalAditionalInformation and regieter the signature
     *
     * @param personId
     * @param loanApplicationOfferId
     * @param signature
     * @throws Exception
     */
//    void registerSignature(int personId, int loanApplicationOfferId, LoanApplicationSignatureForm signature)
//            throws Exception;

    Integer getLoanApplicationIdFromLoanAplicationToken(String loanAplicationToken) throws Exception;

    void sendPostSignatureInteractions(int userId, int loanApplicationId, HttpServletRequest request, HttpServletResponse response, SpringTemplateEngine templateEngine, Locale locale) throws Exception;

    void sendWaitingForDownPaymentInteraction(int userId, int loanApplicationId, Locale locale) throws Exception;

    String generateLoanApplicationToken(int userId, int personId, int loanApplicationId);

    String generateLoanApplicationToken(int userId, int personId, int loanApplicationId, boolean signLink);

    String generateLoanApplicationToken(int userId, int personId, int loanApplicationId, Map<String, Object> extraParams);

    void executeEmailage(Integer userId, String email, String ip) throws Exception;

//    void callUserBotsIfLoanOk(int loanApplicationId, Person person, String ipAddress, Locale locale) throws Exception;

    Pair<Boolean, String> validateLoanApplicationApproval(Integer loanApplicationId) throws Exception;

    void sendLoanApplicationApprovalMail(int loanApplicationId, int personId, Locale locale) throws Exception;

    JSONObject getIovationByLoanApplication(Integer id) throws Exception;

    void generateOfferTCEA(LoanOffer offer, int loanApplicationId) throws Exception;

    Double generateOfferTCEAWithoutIva(LoanOffer offer) throws Exception;

    void updateVehicle(int loanApplicationId, int veihcleId);

    void registerEvaluationRejection(LoanApplication loanApplication, int entiyToRejectId, Locale locale) throws Exception;

    void sendLoanApplicationExtranetApprovalMail(int loanApplicationId, int personId, int disbursementType, Locale locale) throws Exception;

    LoanApplication getLoanApplicationById(int loanApplicationId) throws Exception;

    boolean isConsolidationLoanApplication(int loanApplicationId) throws Exception;

    void sendSchedulePhysicalSignatureInteraction(int userId, int loanApplicationId, Locale locale) throws Exception;

    void sendMissingDocumentation(int userId, int loanApplicationId, Locale locale) throws Exception;

    String generateLoanApplicationLinkEntity(LoanApplication loanApplication) throws Exception;

    String generateLoanApplicationLinkEntity(LoanApplication loanApplication, String loanApplicationToken) throws Exception;

    void sendVehicleDisbursedInteraction(int userId, int loanApplicationId, Locale locale) throws Exception;

//    boolean runPreEvaluationBotsArgentinaIfNotYet(LoanApplication loanApplication, LoanApplicationEvaluationsProcess evaluationsProcess) throws Exception;

    void sendVehiclePartialDownPayment(int userId, int id, Locale locale) throws Exception;

    void sendVehicleTotalDownPayment(int userId, int loanApplicationId, Locale locale) throws Exception;

    void sendBackendGeneratedPreEvaluationEmail(LoanApplication loanApplication) throws Exception;

    List<Integer> getBureausPendingToRun(LoanApplication loanApplication, List<LoanApplicationEvaluation> evaluations) throws Exception;

    List<Pair<Integer, Boolean>> getRequiredDocumentsByLoanApplication(LoanApplication loanApplication) throws Exception;

    List<Integer> getRequiredDocumentsIdsByLoanApplication(LoanApplication loanApplication) throws Exception;

    List<ProcessQuestionSequence> getQuestionSequenceWithoutBackwards(List<ProcessQuestionSequence> questionSequence);

    EntityProductEvaluationsProcess getEntityProductEvaluationProcess(int loanApplicationId, int entityId, int productId) throws Exception;

    void sendLoanApplicationScheduleMail(int loanApplicationId, int personId, Locale locale) throws Exception;

    void approveLoanApplication(int loanApplicationId, Integer sysUserId, HttpServletRequest request, HttpServletResponse response, SpringTemplateEngine templateEngine, Locale locale) throws Exception;

    void approveLoanApplicationWithoutAuditValidation(LoanApplication loanApplication, LoanOffer offerSelected, Integer sysUserId, HttpServletRequest request, HttpServletResponse response, SpringTemplateEngine templateEngine, Locale locale) throws Exception;

    void sendLoanApplicationConfirmScheduleMail(int loanApplicationId, int personId, Date scheduledDate, Integer appointmentScheduleId, Locale locale) throws Exception;

    byte[] generateLoanRequestSheet(Credit credit) throws Exception;

    List<String> getUtmSources();

    List<String> getUtmMediumsBySource(String source);

    List<String> getUtmCampaignsByMedium(String source, String medium);

    ResponseEntity validateLoanApplicationCreateForm(EntityExtranetCreateLoanApplicationForm form, Locale locale) throws Exception;

    LoanApplication getActiveLoanApplication(int docType, String docNumber, int productCategoryId) throws Exception;

    ResponseEntity createLoanApplication(EntityExtranetCreateLoanApplicationForm form) throws Exception;

    boolean hasAnyApprovedPreEvaluation(int loanApplicationId, List<Integer> notThisEntity) throws Exception;

    RescueScreenParams getRescueSreenParams(int loanApplicationId, int personId) throws Exception;

    void sendBadTceaAlertEmail(Integer creditId, Integer offerId, Double tcea, Double tea);

    HelpMessage loanApplicationtHelpMessage(LoanApplication loanApplication) throws Exception;

    boolean runFraudAlertsAndSendEmail(LoanApplication loanApplication) throws Exception;

    void registerReferrerIfExists(Integer loanApplicationId, String referrerPersonId) throws Exception;

    void reevaluateLoanApplications(Integer... loanApplicationIds);

    LoanOffer getSelectedOffer(Integer loanApplicationId) throws Exception;

    List<LoanOffer> getLonaOffers(Integer loanApplicationId) throws Exception;

    void reevaluateLoanApplicationsButKeepBureaus(Integer... loanApplicationIds);

    void sendBanBifTCLead(String type, Integer loanApplicationId, Locale locale) throws Exception;

    String getStreetType(Integer streetTypeId) throws Exception;

    String getZoneType(Integer zoneType) throws Exception;

    void sendInteractionTcEnCaminoIfBanBif(LoanOffer offerSelected, LoanApplication loanApplication, Credit credit, Person person) throws Exception;

    String generateLoanApplicationLinkEntityMailing(LoanApplication loanApplication, String additionalData) throws Exception;

    void sendInteractionEmail(LoanOffer offerSelected, LoanApplication loanApplication, Credit credit, Person person) throws Exception;

    void deleteDocumentsAndAskAgain(LoanApplication loanApplication, List<Integer> userFileTypeIds) throws Exception;

    String generateLandingLinkEntity(Integer entityId, Integer productCategoryId) throws Exception;

    void updateMarketingCampaignByLoanApplication(LoanApplication loanApplication, Integer marketingCampaignId, String action) throws Exception;

    void sendLoanWebhookEvent(String event, LoanApplication loanApplication);

    void approveLoanApplicationAsync(int loanApplicationId, Integer sysUserId, HttpServletRequest request, HttpServletResponse response, SpringTemplateEngine templateEngine, Locale locale) throws Exception;

    String generateLoanApplicationLinkEntity(LoanApplication loanApplication,Map<String, Object> extraParams) throws Exception;

    LoanApplication createLoanApplication(CreateLoanApplicationRequest request, HttpServletRequest httpServletRequest) throws Exception;

    Date generateExpirationDateByLoan(Integer entityId, Integer productCategory);

    void sendLoanApplicationNewOfferEmail(int loanApplicationId, int personId, Locale locale) throws Exception;

    EntityProductParams getEntityProductParamFromLoanApplication(LoanApplication loanApplication) throws Exception;

    Boolean validateLoanApplicationByIPGeolocation( LoanApplication loanApplication, EntityProductParams entityProductParams, Locale locale) throws Exception;

    Integer getDifferenceFromLoanAndDueDate(LoanApplication loanApplication);

    boolean isValidDueDate(LoanApplication loanApplication, int minDays);

    Date updateAlfinDueDate(LoanApplication loanApplication, List<Date> enableDates, EntityProductParams entityProductParams) throws Exception;

    void sendLoanApplicationFirsDueDateEmail(int loanApplicationId, int personId, Locale locale) throws Exception;
}
