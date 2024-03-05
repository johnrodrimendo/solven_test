package com.affirm.common.dao;

import com.affirm.common.model.ReportEntityExtranetTrayReport;
import com.affirm.common.model.ReportLoans;
import com.affirm.common.model.ReportOrigination;
import com.affirm.common.model.UTMValue;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */
public interface CreditDAO {

    <T extends Credit> List<T> getCreditsByPerson(int personId, Locale locale, Class<T> returntype) throws Exception;

    <T extends Credit> T getCreditBO(int creditId, Locale locale, Class<T> returntype) throws Exception;

    void generateOriginalSchedule(Credit credit) throws Exception;

    List<OriginalSchedule> getOriginalSchedule(int creditId) throws Exception;

    List<ManagementSchedule> getManagementSchedule(int creditId) throws Exception;

    <T extends Credit> T getCreditByID(int creditId, Locale locale, boolean getSchedule, Class<T> returntype);

    String getResultEFL(Credit credit);

    Integer[] getActiveCreditIdsByPerson(Locale locale, int personId, Integer flagSolven) throws Exception;

    JSONObject preDisbursementValidation(int creditId) throws Exception;

    void updateCreditContract(int creditId, Integer[] userFileIds) throws Exception;

    List<Credit> getEmployerCredits(Integer productId, Integer employerId, Integer offset, Integer limit, Locale locale) throws Exception;

    <T extends Credit> List<T> getCreditsByIds(Integer[] ids, Locale locale, Class<T> returntype) throws Exception;

    Integer getCreditByCreditCode(String creditCode, int entityId) throws Exception;

    void updateCrediCodeByCreditId(int credtId, String creditCode) throws Exception;

    Integer getLoanApplicationIdByCreditCode(String creditCode, int entityId) throws Exception;

    void updateSignatureDate(int creditId, Date signatureDate) throws Exception;

    void updateDisbursementDate(int creditId, Date date) throws Exception;

    List<ConsolidableDebt> getConsolidatedDebts(int creditId) throws Exception;

    List<CreditEntityExtranetPainter> getEntityCredits(Integer entityId, Integer flag, Date startDate, Date endDate, Integer entityUserId, Locale locale, Integer offset, Integer limit, String search, boolean onlyLoanIds) throws Exception;

    Pair<Integer, Double> getEntityCreditsCount(Integer entityId, Integer flag, Date startDate, Date endDate, Integer entityUserId, Locale locale) throws Exception;

    Pair<Integer, Double> getEntityCreditsCount(Integer entityId, Integer flag, Date startDate, Date endDate, Integer entityUserId, Locale locale, String search) throws Exception;

    void updateGeneratedInEntity(Integer creditId, Integer user) throws Exception;

    void updateDisbursmentInInEntity(Integer creditId, Integer user) throws Exception;

    void updateCreditStatus(int creditId, int creditStatusId, Integer sysUserId) throws Exception;

    void updateCreditStatusExtranet(int creditId, int creditStatusId, int sysUserId) throws Exception;

    String registerDownPayment(int creditId, Integer downPaymentBank, Integer currency, Double amount, String downPaymentOps) throws Exception;

    List<DownPayment> getDownPayments(int creditId) throws Exception;

    void registerSignatureSchedule(int creditId, Date scheduleDate, String scheduleHour, String address) throws Exception;

    CreditSignatureSchedule getSignatureSchedule(int creditId) throws Exception;

    void updateSignedOnEntity(int creditId, Boolean signedOnEntity) throws Exception;

    void updateCreditWaitingStatusExtranet(int creditId, Boolean creditWaitingStatusId, int sysUserId) throws Exception;

    boolean registerLoanApplicationAudit(int loanApplicationId, int loanApplicationAuditType, boolean approved, Integer userFileId, Integer auditRejectionReason, String auditRejectionReasonComment, Integer sysuserId) throws Exception;

    List<AuditRejectionReason> getAuditRejectionReason(int loanApplicationId) throws Exception;

    void registerRejection(int creditId, Integer creditRejectionReasonId) throws Exception;

    void registerRejectionWithComment(int creditId, Integer creditRejectionReasonId, String creditRejectionReasonComment) throws Exception;

    List<Credit> getCreditByEmployer(Integer employerId, Date period, Locale locale);

    List<ReportLoans> getLoansReport(Date startDate, Date endDate, Locale locale, String countries);

    List<ReportLoans> getLoansLightReport(Date startDate, Date endDate, Locale locale, Integer[] countryIds, Integer[] entityIds);

    List<ReportLoans> getLoansLightReport(Integer minAge, Integer maxAge, String requestType, String cardType, Date startDate, Date endDate, Date startDate2, Date endDate2, Locale locale, Integer[] countryIds, Integer[] entityIds, Integer[] products);

    boolean runFraudAlerts(int loanApplicationId) throws Exception;

    void rejectLoanApplicationFraudAlert(Integer loanApplicationFraudAlertId, Integer sysuserId) throws Exception;

    void registerLoanApplicationFraudAlert(Integer fraudAlertId, Integer loanApplicationId) throws Exception;

    void assignLoanApplicationFraudFlag(Integer loanApplicationId, Integer flagFraudAlertReasonId, Integer fraudFlagId, String commentary, Integer sysuserId) throws Exception;

    void reviewFraudFlag(Integer loanApplicationFraudFlagId, Boolean approve, Integer sysuserId) throws Exception;

    List<LoanApplicationFraudAlert> getLoanApplicationFraudAlerts(Integer loanApplicationId, Integer fraudAlertStatusId) throws Exception;

    List<LoanApplicationFraudFlag> getToReviewLoanApplicationFraudFlags() throws Exception;

    List<LoanApplicationFraudFlag> getLogFraudFlags() throws Exception;

    void updateCreditSubStatus(int creditId, Integer subStatus) throws Exception;

    void registerSchedule(Integer creditId, String jsonSchedule);

    List<String> getEntityApplicationCodesByFilter(Integer entityId, Integer creditStatusId, Integer creditSubStatusId);

    List<LoanDetailsReport> generateLoanDetailsReport(CatalogService catalogService) throws Exception;

    Integer getCountryIdByCreditId(Integer creditId);

    List<ReportOrigination> getOriginationReport(String country, CatalogService catalogService, Locale locale, Date startDate, Date endDate) throws Exception;

    void updateReturningReasons(int creditId, JSONArray returningReason) throws Exception;

    void updateDisbursementDate(Integer creditId, Date disbursementDate) throws Exception;

    void updateCreditDataOnDisbursement(Integer creditId, Double amount, Integer installments, Double tea, Integer entityUserId) throws Exception;

    void updateObservation(int creditId, Integer creditObservationId) throws Exception;

    List<TarjetasPeruanasCreditActivation> getLigoCardReports() throws Exception;

    List<AutoplanCreditActivation> getAutoplanReports() throws Exception;

    List<Integer> getCreditsByPersonFiltered(int personId, List<Integer> products, List<Integer> statuses) throws Exception;

    List<CreditBancoDelSolExtranetPainter> getEntityCreditsByLoggedUserId(Integer userId,  Date startDate, Date endDate, String query, Integer offset, Integer limit, Locale locale) throws Exception;

    List<CreditBancoDelSolExtranetPainter> getEntityCreditsByLoggedUserId(Integer userId,  Date startDate, Date endDate, String query, Integer offset, Integer limit, Locale locale, boolean onlyIds,List<Integer> productsId,Integer minProgress, Integer maxProgress) throws Exception;

    JSONObject getBancoDelSolRiskReport(Integer loanApplicatoinId) throws Exception;

    void rejectAllLoanApplicationFraudAlerts(Integer loanApplicationId, Integer sysuserId);

    void returnCreditToLoanApplication(Integer creditId);

    void updateTCEA(int creditId, double effectiveAnnualCostRate) throws Exception;

    void updateSolvenTCEA(int creditId, double solvenEffectiveAnnualCostRate) throws Exception;

    void updateEntityCustomData(int creditId, JSONObject entityCustomData) throws Exception;

    void returnBDSCreditToLoanApplication(int creditId) throws Exception;

    List<BanbifTcLeadLoan> getBanbifLeadCreditCardLoan(Integer entityUserId, Date startDate, Date endDate, Integer filterType, Locale locale, Integer offset, Integer limit,String search) throws Exception;

    Integer getBanbifLeadCreditCardLoanCount(Integer entityUserId, Date startDate, Date endDate, String search, Integer filterType, Locale locale) throws Exception;

    List<ReportLoans> getLoansLightReportFunnel(Integer minAge, Integer maxAge, String requestType, String cardType, Date startDate, Date endDate,
                                                Date startDate2, Date endDate2, Locale locale, Integer[] countryIds, Integer[] entityIds, Integer[] products,Integer[] steps
                                                );

    List<ReportLoans> getLoansLightReportFunnel(Integer minAge, Integer maxAge, String requestType, String cardType, Date startDate, Date endDate,
                                                Date startDate2, Date endDate2, Locale locale, Integer[] countryIds, Integer[] entityIds, Integer[] products,Integer[] steps,
                                                List<String> utmSources,List<String> utmMedium,List<String> utmCampaign,List<String> utmContent, List<Integer> entityProductParams);

    List<ReportEntityExtranetTrayReport> getExtranetEntityLoansForReport(Integer[] loanIds, Locale locale);

    List<CreditBancoDelSolExtranetPainter> getEntityRejectedLoanApplications(Integer entityId,  Date startDate, Date endDate, String query, Integer offset, Integer limit, Locale locale, boolean onlyIds , List<Integer> productIds,List<String> rejectedReason) throws Exception;

    Pair<Integer, Double> getEntityRejectedLoanApplicationsCount(Integer entityId,  Date startDate, Date endDate, String query, List<Integer> productIds,List<String> rejectedReason, Locale locale) throws Exception;

    Pair<Integer, Double> getEntityCreditsByLoggedUserIdCount(Integer userId,  Date startDate, Date endDate, String query, List<Integer> productsIds,Integer minProgress, Integer maxProgress, Locale locale) throws Exception;

    Pair<Integer, Double> getEntityToEvaluateLoanApplicationsCount(Integer entityId,  Date startDate, Date endDate, String query,  Date offerStartDate, Date offerEndDate, Integer[] analyst, Integer[] products, Integer[] progress, Locale locale) throws Exception;

    void rejectLoanApplicationFraudAlert(Integer loanApplicationFraudAlertId, Integer sysuserId, Integer entityUserId) throws Exception;

    void rejectAllLoanApplicationFraudAlerts(Integer loanApplicationId, Integer sysuserId, Integer entityUserId) throws Exception;

    List<LoanApplicationToEvaluationExtranetPainter> getEntityToEvaluateLoanApplications(Integer entityId, Date startDate, Date endDate, String query,  Date offerStartDate, Date offerEndDate, Integer[] analyst, Integer[] products, Integer[] progress, Integer offset, Integer limit, boolean onlyIds, Locale locale) throws Exception;

    List<CreditEntityExtranetPainter> getEntityCredits(Integer entityId, Integer flag, Date startDate, Date endDate, Integer entityUserId, Locale locale, Integer offset, Integer limit,String search, Integer[] entityProductsParam, boolean onlyLoanIds, List<Integer> products) throws Exception;

    Pair<Integer, Double> getEntityCreditsCount(Integer entityId, Integer flag, Date startDate, Date endDate, Integer entityUserId, Locale locale, String search, Integer[] entityProductsParam, List<Integer> products) throws Exception;

    List<LoanGatewayPaymentMethod> getLoanPaymentMethod(Integer loanApplicationId) throws Exception;

    void insertLoanPaymentMethod(Integer loanApplicationId, Integer collectionPaymentMethodId, Date expirationDate, String paymentVars, String paymentResponse);

    void inactivatePaymentMethodOfLoan(Integer loanApplicationId, Integer collectionPaymentMethodId);

    void updatePaymentMethodOfLoanPayed(Integer loanCollectionPaymentMethodId, String response );

    List<GatewayLoanApplicationExtranetPainter> getCollectionLoanApplication(Integer entityId, Date startDate, Date endDate, Integer[] loanStatus, Integer[] creditStatus, Integer[] entityProductParams, Integer offset, Integer limit, String search, Boolean onlyIds, Locale locale) throws Exception;

    Pair<Integer, Double>  getCollectionLoanApplicationCount(Integer entityId, Date startDate, Date endDate, Integer[] loanStatus, Integer[] creditStatus,  String search, Integer[] entityProductParams, Locale locale) throws Exception;

    void updatePaymentMethodOfLoanResponse(Integer loanCollectionPaymentMethodId, String response );

    UTMValue getUTMValuesFromEntity(Integer entityId);

    EntityAllRejectionReasons getEntityAllRejectionReason(Integer entityId,Locale locale) throws Exception;

    List<JSONObject> getLoanProgressesFromEntity(Integer entityId, Boolean processing);

    void updateCreditOriginalScheduleDueDateAndAmount(int creditId, int installmentId, Date dueDate, Double installmentAmount) throws Exception;

    void updateCreditOriginalScheduleData(int creditId, int installmentId, Date dueDate, Double installmentAmount, Double installmentCapital, Double interest, Double insurance) throws Exception;

    void updateManagementScheduleData(int creditId, int installmentId, Date dueDate, Double installmentAmount, Double installmentCapital, Double interest, Double insurance);

    List<LoanGatewayPaymentMethod> getLoanPaymentMethodByTransactionCode(String code, Integer collectionPaymentMethodId) throws Exception;

    <T extends Credit> T getCreditByLoanApplicationId(int loanApplicationId, Locale locale, boolean getSchedule, Class<T> returntype);

    void updateInstallmentAmountAndAvg(int creditId, Double installmentAmount, Double installmentAmountAvg);
}
