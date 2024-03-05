package com.affirm.tests.dao

import com.affirm.common.dao.CreditDAO
import com.affirm.common.model.ReportLoans
import com.affirm.common.model.ReportOrigination
import com.affirm.common.model.catalog.Entity
import com.affirm.common.model.transactional.*
import com.affirm.common.service.CatalogService
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseConfig
import org.apache.poi.ss.formula.functions.T
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import java.text.SimpleDateFormat

class CreditDAOTest extends BaseConfig {

    @Autowired
    CreditDAO creditDAO

    @Autowired
    CatalogService catalogService;

    static final int PERSON_ID = 2007
    static final Locale LOCALE = Locale.US
    static final Class<T> RETURN_TYPE = Credit.class
    static final int CREDIT_ID = 1000
    static final Credit CREDIT = getCredit()
    static final int CREDIT_STATUS_ID = 1001
    static final int SYS_USER_ID = 1002
    static final Boolean CREDIT_WAITING_STATUS_ID = 9999
    static final boolean GET_SCHEDULE = true
    static final Integer FLAG_SOLVEN = 1
    static final Integer USER_FILE_ID = 55555
    static final Integer[] USER_FILE_IDS = [USER_FILE_ID]
    static final Integer OFFSET = 10
    static final Integer LIMIT = 10
    static final Integer EMPLOYER_ID = 11111
    static final Integer PRODUCT_ID = 22222
    static final Integer[] CREDIT_IDS = [CREDIT_ID]
    static final String CREDIT_CODE = "98765"
    static final int ENTITY_ID = 88888
    static final Date CURRENT_DATE = new Date()
    static final Date SIGNATURE_DATE = CURRENT_DATE
    static final Date DISBURSEMENT_DATE = CURRENT_DATE
    static final int ENTITY_USER_ID = 4444
    static final Date START_DATE = CURRENT_DATE
    static final Date END_DATE = CURRENT_DATE
    static final Integer USER_ID = 4321
    static final Integer DOWN_PAYMENT_BANK = 4321
    static final Integer CURRENCY = 1
    static final Double AMOUNT = 50000
    static final String DOWN_PAYMENT_OPS = ""
    static final Date SCHEDULE_DATE = CURRENT_DATE
    static final String SCHEDULE_HOUR = "4"
    static final String ADDRESS = "Jr. Mariscal La Mar 999"
    static final Boolean SIGNED_ON_ENTITY = 9999
    static final int LOAN_APPLICATION_ID = 78987
    static final int LOAN_APPLICATION_AUDIT_TYPE = 79999
    static final boolean APRROVED = true
    static final Integer AUDIT_REJECTION_REASON = 33333
    static final Integer CREDIT_REJECTION_REASON_ID = 22335
    static final Date PERIOD = CURRENT_DATE
    static final String COUNTRIES = '["51"]'
    static final String COUNTRY = "51"
    static final int LOAN_APPLICATION_FRAUD_ALERT_ID = 22555
    static final Integer FLAG_FRAUD_ALERT_REASON_ID = 98569
    static final Integer FRAUD_FLAG_ID = 45654
    static final String COMMENTARY = "Commentary"
    static final Integer FRAUD_ALERT_STATUS_ID = 87878
    static final Integer SUB_STATUS = 4
    static final String JSON_SCHEDULE = '["5"]'
    static final int CREDIT_SUB_STATUS_ID = 1009
    static final JSONArray RETURNING_REASON = new JSONArray()
    static final Integer CREDIT_ID_INTEGER = 65454
    static final Integer INSTALLMENTS = 8745
    static final Double TEA = 19
    static final Integer CREDIT_OBSERVATION_ID = 87897
    static final List<Integer> PRODUCTS = null
    static final List<Integer> STATUSES = null


    static Credit getCredit() {
        Credit credit = new Credit()
        credit.setId(810)
        credit.setEntityProductParameterId(11101)
        credit.setAmount(AMOUNT)
        credit.setHiddenCommission(50)
        credit.setCost(25)

        Entity entity = new Entity()
        entity.setAnnualizaXirr(true)
        entity.setCustomEffectiveAnnualCostRate(true)
        entity.setId(ENTITY_ID)

        credit.setEntity(entity)
        credit.setEffectiveAnnualRate(20)
        credit.setOriginalSchedule(new ArrayList<OriginalSchedule>())

        return credit
    }

    @Test
    void getCreditsByPersonFromCreditDAO() {
        List<Credit> result = creditDAO.getCreditsByPerson(PERSON_ID, LOCALE, RETURN_TYPE)
        Assert.assertNotNull(result)
    }

    @Test
    void getCreditBOFromCreditDAO() {
        Credit result = creditDAO.getCreditBO(CREDIT_ID, LOCALE, Credit.class)
        Assert.assertNull(result)
    }

    @Test
    void generateOriginalScheduleFromCreditDAO() {
        creditDAO.generateOriginalSchedule(CREDIT)
    }

    @Test
    void getOriginalScheduleFromCreditDAO() {
        List<OriginalSchedule> result = creditDAO.getOriginalSchedule(CREDIT_ID)
        Assert.assertNull(result)
    }

    @Test
    void getManagementScheduleFromCreditDAO() {
        List<ManagementSchedule> result = creditDAO.getManagementSchedule(CREDIT_ID)
        Assert.assertNull(result)
    }

    @Test
    void updateCreditStatusFromCreditDAO() {
        creditDAO.getCreditsByPerson(PERSON_ID, LOCALE, RETURN_TYPE)
    }

    @Test
    void updateCreditStatusExtranetFromCreditDAO() {
        creditDAO.updateCreditStatusExtranet(CREDIT_ID, CREDIT_STATUS_ID, SYS_USER_ID)
    }

    @Test
    void updateCreditWaitingStatusExtranetFromCreditDAO() {
        creditDAO.updateCreditWaitingStatusExtranet(CREDIT_ID, CREDIT_WAITING_STATUS_ID, SYS_USER_ID)
    }

    @Test
    void getResultEFLFromCreditDAO() {
        String result = creditDAO.getResultEFL(CREDIT)
        Assert.assertNull(result)
    }

    @Test
    void getCreditByIDFromCreditDAO() {
        Credit result = creditDAO.getCreditByID(CREDIT_ID, LOCALE, GET_SCHEDULE, RETURN_TYPE)
        Assert.assertNull(result)
    }

    @Test
    void getActiveCreditIdsByPersonFromCreditDAO() {
        Integer[] result = creditDAO.getActiveCreditIdsByPerson(LOCALE, PERSON_ID, FLAG_SOLVEN)
        Assert.assertNull(result)
    }

    @Test
    void preDisbursementValidationFromCreditDAO() {
        JSONObject result = creditDAO.preDisbursementValidation(CREDIT_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void updateCreditContractFromCreditDAO() {
        creditDAO.updateCreditContract(CREDIT_ID, USER_FILE_IDS)
    }

    @Test
    void getEmployerCreditsFromCreditDAO() {
        List<Credit> result = creditDAO.getEmployerCredits(PRODUCT_ID, EMPLOYER_ID, OFFSET, LIMIT, LOCALE)
        Assert.assertNull(result)
    }

    @Test
    void getCreditsByIdsFromCreditDAO() {
        Credit result = creditDAO.getCreditsByIds(CREDIT_IDS, LOCALE, RETURN_TYPE)
        Assert.assertNull(result)
    }

    @Test
    void getCreditByCreditCodeFromCreditDAO() {
        Integer result = creditDAO.getCreditByCreditCode(CREDIT_CODE, ENTITY_ID)
        Assert.assertNull(result)
    }

    @Test
    void updateCrediCodeByCreditIdFromCreditDAO() {
        creditDAO.updateCrediCodeByCreditId(CREDIT_ID, CREDIT_CODE)
    }

    @Test
    void getLoanApplicationIdByCreditCodeFromCreditDAO() {
        Integer result = creditDAO.getLoanApplicationIdByCreditCode(CREDIT_CODE, ENTITY_ID)
        Assert.assertNull(result)
    }

    @Test
    void updateSignatureDateFromCreditDAO() {
        creditDAO.updateSignatureDate(CREDIT_ID, SIGNATURE_DATE)
    }

    @Test
    void updateDisbursementDateFromCreditDAO() {
        creditDAO.updateDisbursementDate(CREDIT_ID, DISBURSEMENT_DATE)
    }

    @Test
    void getConsolidatedDebtsFromCreditDAO() {
        List<ConsolidableDebt> result = creditDAO.getConsolidatedDebts(CREDIT_ID)
        Assert.assertNull(result)
    }

    @Test
    void getEntityCreditsFromCreditDAO() {
        List<CreditEntityExtranetPainter> result = creditDAO.getEntityCredits(26, 2, null,null, 2601, Configuration.defaultLocale, null, null, null, false)
        Assert.assertNull(result)
    }

    @Test
    void updateGeneratedInEntityFromCreditDAO() {
        creditDAO.updateGeneratedInEntity(CREDIT_ID, USER_ID)
    }

    @Test
    void updateDisbursmentInInEntityFromCreditDAO() {
        creditDAO.updateDisbursmentInInEntity(CREDIT_ID, USER_ID)
    }

    @Test
    void registerDownPaymentFromCreditDAO() {
        String result = creditDAO.registerDownPayment(CREDIT_ID, DOWN_PAYMENT_BANK, CURRENCY, AMOUNT, DOWN_PAYMENT_OPS)
        Assert.assertNull(result)
    }

    @Test
    void getDownPaymentsFromCreditDAO() {
        List<DownPayment> result = creditDAO.getDownPayments(CREDIT_ID)
        Assert.assertNull(result)
    }

    @Test
    void registerSignatureScheduleFromCreditDAO() {
        creditDAO.registerSignatureSchedule(CREDIT_ID, SCHEDULE_DATE, SCHEDULE_HOUR, ADDRESS)
    }

    @Test
    void getSignatureScheduleFromCreditDAO() {
        CreditSignatureSchedule result = creditDAO.getSignatureSchedule(CREDIT_ID)
        Assert.assertNull(result)
    }

    @Test
    void updateSignedOnEntityFromCreditDAO() {
        creditDAO.updateSignedOnEntity(CREDIT_ID, SIGNED_ON_ENTITY)
    }

    @Test
    void registerLoanApplicationAuditFromCreditDAO() {
        boolean result = creditDAO.registerLoanApplicationAudit(LOAN_APPLICATION_ID, LOAN_APPLICATION_AUDIT_TYPE,
                APRROVED, USER_FILE_ID, AUDIT_REJECTION_REASON, 'comment', SYS_USER_ID)
        Assert.assertNull(result)
    }

    @Test
    void getAuditRejectionReasonFromCreditDAO() {
        List<AuditRejectionReason> result = creditDAO.getAuditRejectionReason(LOAN_APPLICATION_ID)
        Assert.assertNull(result)
    }

    @Test
    void registerRejectionFromCreditDAO() {
        creditDAO.registerRejection(CREDIT_ID, CREDIT_REJECTION_REASON_ID)
    }

    @Test
    void registerRejectionWithCommentFromCreditDAO() {
        creditDAO.registerRejectionWithComment(CREDIT_ID, CREDIT_REJECTION_REASON_ID, 'CreditDAOTest')
    }

    @Test
    void getCreditByEmployerFromCreditDAO() {
        List<Credit> result = creditDAO.getCreditByEmployer(EMPLOYER_ID, PERIOD, LOCALE)
        Assert.assertNull(result)
    }

    @Test
    void getLoansReportFromCreditDAO() {
        List<ReportLoans> result = creditDAO.getLoansReport(START_DATE, END_DATE, LOCALE, COUNTRIES)
        Assert.assertNull(result)
    }

    @Test
    void getLoansLightReportFromCreditDAO() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
        Date startDate = sdf.parse("2020-12-01")
        Date endDate = sdf.parse("2020-12-03")
        List<ReportLoans> result = creditDAO.getLoansLightReport(startDate, endDate, LOCALE, '["54"]', null)
        Assert.assertNotNull(result)
    }

    @Test
    void getOriginationReportFromCreditDAO() {
        List<ReportOrigination> result = creditDAO.getOriginationReport(COUNTRY, catalogService, LOCALE, START_DATE,
                END_DATE)
        Assert.assertNull(result)
    }

    @Test
    void runFraudAlertsFromCreditDAO() {
        creditDAO.runFraudAlerts(LOAN_APPLICATION_ID)
    }

    @Test
    void rejectLoanApplicationFraudAlertFromCreditDAO() {
        creditDAO.rejectLoanApplicationFraudAlert(LOAN_APPLICATION_FRAUD_ALERT_ID, SYS_USER_ID)
    }

    @Test
    void assignLoanApplicationFraudFlagFromCreditDAO() {
        creditDAO.assignLoanApplicationFraudFlag(LOAN_APPLICATION_ID, FLAG_FRAUD_ALERT_REASON_ID, FRAUD_FLAG_ID,
                COMMENTARY, SYS_USER_ID)
    }

    @Test
    void reviewFraudFlagFromCreditDAO() {
        creditDAO.reviewFraudFlag(LOAN_APPLICATION_ID, APRROVED, SYS_USER_ID)
    }

    @Test
    void getLoanApplicationFraudAlertsFromCreditDAO() {
        List<LoanApplicationFraudAlert> result = creditDAO.getLoanApplicationFraudAlerts(LOAN_APPLICATION_ID, FRAUD_ALERT_STATUS_ID)
        Assert.assertNull(result)
    }

    @Test
    void getToReviewLoanApplicationFraudFlagsFromCreditDAO() {
        List<LoanApplicationFraudFlag> result = creditDAO.getToReviewLoanApplicationFraudFlags()
        Assert.assertNotNull(result)
    }

    @Test
    void getLogFraudFlagsFromCreditDAO() {
        String result = creditDAO.getLogFraudFlags()
        Assert.assertNotNull(result)
    }

    @Test
    void updateCreditSubStatusFromCreditDAO() {
        creditDAO.updateCreditSubStatus(CREDIT_ID, SUB_STATUS)
    }

    @Test
    void registerScheduleFromCreditDAO() {
        String result = creditDAO.registerSchedule(CREDIT_ID, JSON_SCHEDULE)
        Assert.assertNull(result)
    }

    @Test
    void getEntityApplicationCodesByFilterFromCreditDAO() {
        List<String> result = creditDAO.getEntityApplicationCodesByFilter(ENTITY_ID, CREDIT_STATUS_ID, CREDIT_SUB_STATUS_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void generateLoanDetailsReportFromCreditDAO() {
        List<LoanDetailsReport> result = creditDAO.generateLoanDetailsReport(catalogService)
        Assert.assertNotNull(result)
    }

    @Test
    void getCountryIdByCreditIdFromCreditDAO() {
        Integer result = creditDAO.getCountryIdByCreditId(CREDIT_ID)
        Assert.assertNull(result)
    }

    @Test
    void updateReturningReasonsFromCreditDAO() {
        creditDAO.updateReturningReasons(CREDIT_ID, RETURNING_REASON)
    }

    @Test
    void updateDisbursementDate2FromCreditDAO() {
        creditDAO.updateDisbursementDate(CREDIT_ID_INTEGER, DISBURSEMENT_DATE)
    }

    @Test
    void updateCreditDataOnDisbursementFromCreditDAO() {
        creditDAO.updateCreditDataOnDisbursement(CREDIT_ID, AMOUNT, INSTALLMENTS, TEA, ENTITY_USER_ID)
    }

    @Test
    void updateObservationFromCreditDAO() {
        creditDAO.updateObservation(CREDIT_ID, CREDIT_OBSERVATION_ID)
    }

    @Test
    void getLigoCardReportsFromCreditDAO() {
        List<TarjetasPeruanasCreditActivation> result = creditDAO.getLigoCardReports()
        Assert.assertNotNull(result)
    }

    @Test
    void getAutoplanReportsFromCreditDAO() {
        List<AutoplanCreditActivation> result = creditDAO.getAutoplanReports()
        Assert.assertNotNull(result)
    }

    @Test
    void getCreditsByPersonFilteredFromCreditDAO() {
        List<Integer> result = creditDAO.getCreditsByPersonFiltered(PERSON_ID, PRODUCTS, STATUSES)
        Assert.assertNotNull(result)
    }

    @Test
    void getEntityCreditsByLoggedUserIdFromCreditDAO() {
//        List<CreditBancoDelSolExtranetPainter> result = creditDAO.getEntityCreditsByLoggedUserId(USER_ID, START_DATE,
//                END_DATE, LOCALE)
//        Assert.assertNull(result)
    }


}
