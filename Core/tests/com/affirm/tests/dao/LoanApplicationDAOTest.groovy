package com.affirm.tests.dao

import com.affirm.aws.RecognitionResultsPainter
import com.affirm.common.dao.JsonResolverDAO
import com.affirm.common.dao.LoanApplicationDAO
import com.affirm.common.model.AppointmentSchedule
import com.affirm.common.model.PreApprovedInfo
import com.affirm.common.model.catalog.*
import com.affirm.common.model.transactional.*
import com.affirm.equifax.ws.ReporteCrediticio
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseConfig
import org.apache.commons.lang3.tuple.Pair
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired

import static org.junit.jupiter.api.Assertions.*

class LoanApplicationDAOTest extends BaseConfig {

    @Autowired
    private LoanApplicationDAO loanApplicationDAO

    private static final AMOUNT = 5000

    private static final Integer LOAN_APPLICATION_ID = 9496
    private static final String LOAN_APPLICATION_CODE = 'LA1104049496'
    private static final Integer PRELIMINARY_EVALUATION_ID = 0
    private static final String ENTITY_APPLICATION_CODE = ''
    private static final Integer LOAN_APPLICATION_EVALUATION_ID = 3910
    private static final Integer ENTITY_PRODUCT_PARAMETER_ID = 3038
    private static final Integer USER_ID = 8762
    private static final Integer PERSON_ID = 9014
    private static final Integer REASON_ID = LoanApplicationReason.ADELANTO
    private static final Integer VEHICLE_ID = 328
    private static final Integer PRODUCT_ID = Product.GUARANTEED
    private static final Integer EMPLOYER_ID = 10
    private static final Integer EMPLOYEE_ID = 1
    private static final Integer ENTITY_ID = Entity.ACCESO
    private static final Integer PRODUCT_CATEGORY_ID = ProductCategory.CONSUMO
    private static final Integer ENTITY_USER_ID = 1
    private static final Integer LOAN_OFFER_ID = 8719
    private static final Integer EVALUATION_ID = 3910
    private static final Integer SELF_EVALUATION_ID = 448
    private static final Integer ACTIVITY_ID = ActivityType.DEPENDENT
    private static final Integer SYS_USER_ID = 1
    private static final Integer SELECTED_ENTITY_ID = Entity.ACCESO
    private static final Integer APPOINTMENT_SCHEDULE_ID = 18
    private static final Integer GUARANTEED_VEHICLE_ID = 4039

    @Test
    void shouldStartPreliminaryEvaluation() {
        Executable executable = {
            loanApplicationDAO.startPreliminaryEvaluation(LOAN_APPLICATION_ID)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldRegisterLoanApplication() {
        int countryPeru = CountryParam.COUNTRY_PERU
        LoanApplication loanApplicationPeru = loanApplicationDAO.registerLoanApplication(USER_ID, AMOUNT, Configuration.DEFAULT_INSTALLMENTS, REASON_ID, PRODUCT_ID, null, null, LoanApplication.ORIGIN_EXTRANET_ENTITY, EMPLOYER_ID, null, ENTITY_ID, countryPeru)

        int countryArgentina = CountryParam.COUNTRY_ARGENTINA
        LoanApplication loanApplicationArgentina = loanApplicationDAO.registerLoanApplication(USER_ID, AMOUNT, Configuration.DEFAULT_INSTALLMENTS, REASON_ID, PRODUCT_ID, null, null, LoanApplication.ORIGIN_EXTRANET_ENTITY, EMPLOYER_ID, null, ENTITY_ID, countryArgentina)

        assertNotNull(loanApplicationPeru)
        assertNotNull(loanApplicationArgentina)
    }

    @Test
    void shouldGetLastPreliminaryEvaluationByDb() {
        LoanApplicationPreliminaryEvaluation loanApplicationPreliminaryEvaluation = loanApplicationDAO.getLastPreliminaryEvaluation(LOAN_APPLICATION_ID, Configuration.defaultLocale, JsonResolverDAO.READABLE_DB)

        assertNull(loanApplicationPreliminaryEvaluation)
    }

    @Test
    void shouldGetLastPreliminaryEvaluation() {
        LoanApplicationPreliminaryEvaluation loanApplicationPreliminaryEvaluation = loanApplicationDAO.getLastPreliminaryEvaluation(LOAN_APPLICATION_ID, Configuration.defaultLocale)

        assertNull(loanApplicationPreliminaryEvaluation)
    }

//    @Test
//    void shouldGetEntityRatesByProduct() {
//        List<EntityRate> entityRateList = loanApplicationDAO.getEntityRatesByProduct(Configuration.defaultLocale, PRODUCT_ID)
//
//        assertNotNull(entityRateList)
//    }

    @Test
    void shouldExecuteEvaluationPreBureau() {
        JSONObject json = loanApplicationDAO.executeEvaluationPreBureau(LOAN_APPLICATION_ID, PRODUCT_ID, ENTITY_ID)

        assertNotNull(json)
    }

    @Test
    void shouldExecuteEvaluationBureau() {
        boolean done = loanApplicationDAO.executeEvaluationBureau(LOAN_APPLICATION_ID, LOAN_APPLICATION_EVALUATION_ID)

        assertTrue(done)
    }

    @Test
    void shouldGetLastEvaluation() {
        LoanApplicationEvaluation loanApplicationEvaluation = loanApplicationDAO.getLastEvaluation(LOAN_APPLICATION_ID, Configuration.defaultLocale)

        assertNull(loanApplicationEvaluation)
    }

    @Test
    void shouldCreateLoanOffers() {
        List<LoanOffer> loanOfferList = loanApplicationDAO.createLoanOffers(LOAN_APPLICATION_ID)

        assertNull(loanOfferList)
    }

    @Test
    void shouldUpdateLoanApplication() {
        Executable executable = {
            loanApplicationDAO.updateLoanApplication(LOAN_APPLICATION_ID, AMOUNT, Configuration.DEFAULT_INSTALLMENTS, PRODUCT_ID, 30)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldRegisterLoanApplicationSIgnature() {
        Executable executable = {
            loanApplicationDAO.registerLoanApplicationSIgnature(1, 'Daniel Alvarez Vera', IdentityDocumentType.DNI, '70061801')
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldGetLoanApplication() {
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(LOAN_APPLICATION_ID, Configuration.defaultLocale)

        assertNull(loanApplication)
    }

    @Test
    void shouldGetLoanApplicationLite() {
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplicationLite(LOAN_APPLICATION_ID, Configuration.defaultLocale)

        assertNull(loanApplication)
    }

    @Test
    void shouldGetLoanApplicationByReturn() {
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(LOAN_APPLICATION_ID, Configuration.defaultLocale, LoanApplication.class)

        assertNull(loanApplication)
    }

    @Test
    void shouldGetLoanApplicationsByPerson() {
        List<LoanApplication> loanApplicationList = loanApplicationDAO.getLoanApplicationsByPerson(Configuration.defaultLocale, PERSON_ID, LoanApplication.class)

        assertNull(loanApplicationList)
    }

    @Test
    void shouldLoanApplicationList() {
        List<LoanApplication> loanApplicationList = loanApplicationDAO.getLoanApplicationsByEntityUser(Configuration.defaultLocale, ENTITY_USER_ID, LoanApplication.class)

        assertNotNull(loanApplicationList)
    }

    @Test
    void shouldGetActiveLoanApplicationByPerson() {
        LoanApplication loanApplication = loanApplicationDAO.getActiveLoanApplicationByPerson(Configuration.defaultLocale, PERSON_ID, PRODUCT_CATEGORY_ID)

        assertNull(loanApplication)
    }

    @Test
    void shouldGetActiveLoanApplicationsByPerson() {
        List<LoanApplication> loanApplicationList = loanApplicationDAO.getActiveLoanApplicationsByPerson(Configuration.defaultLocale, PERSON_ID, LoanApplication.class)

        assertNull(loanApplicationList)
    }

    @Test
    void shouldGetLoanOffers() {
        List<LoanOffer> loanOfferList = loanApplicationDAO.getLoanOffers(LOAN_APPLICATION_ID)

        assertNull(loanOfferList)
    }

    @Test
    void shouldAssignanalyst() {
        Executable executable = {
            loanApplicationDAO.assignanalyst(LOAN_APPLICATION_ID, 1, SYS_USER_ID)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldGenerateCredit() {
        Credit credit = loanApplicationDAO.generateCredit(LOAN_APPLICATION_ID, Configuration.defaultLocale)

        assertNotNull(credit)
    }

    @Test
    void shouldSelectLoanOfferAnalyst() {
        Executable executable = { loanApplicationDAO.selectLoanOfferAnalyst(LOAN_OFFER_ID, 1) }

        assertDoesNotThrow(executable)
    }

//    LoanOffer createLoanOfferAnalyst(LOAN_APPLICATION_ID, double loanAMOUNT, int installments, ENTITY_ID, PRODUCT_ID, EMPLOYER_ID) throws Exception

//    @Test
//    void shouldCreateLoanOfferAnalyst() {
//        LoanOffer loanOffer = loanApplicationDAO.createLoanOfferAnalyst(LOAN_APPLICATION_ID, double loanAMOUNT, int installments, ENTITY_ID, PRODUCT_ID, EMPLOYER_ID, ENTITY_PRODUCT_PARAMETER_ID)
//
//        assertNotNull(loanOffer)
//    }

    @Test
    void shouldRegisterRejection() {
        Executable executable = {
            loanApplicationDAO.registerRejection(LOAN_APPLICATION_ID, ApplicationRejectionReason.BANK_ACCOUNT_INVALID)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldRegisterIntent() {
        Integer intent = loanApplicationDAO.registerIntent(IdentityDocumentType.DNI, '70061801')

        assertNotNull(intent)
    }

//    void updateHumanFormId(LOAN_APPLICATION_ID, int humanFormId) throws Exception

//    void updateLoanApplicationMood(LOAN_APPLICATION_ID, int mood) throws Exception

//    void registerIcarValidation(LOAN_APPLICATION_ID, JSONObject icarValidation) throws Exception

    @Test
    void shouldRegisterBureauResult() {
        Executable executable = {
            loanApplicationDAO.registerBureauResult(LOAN_APPLICATION_ID, 710, 'Riesgo Bajo', 'Conclusion', new JSONObject().toString(), Bureau.EQUIFAX)
        }

        assertDoesNotThrow(executable)
    }

//    void registerEvaluationSuccess(LOAN_APPLICATION_ID) throws Exception

//    IcarValidation getIcarValidation(LOAN_APPLICATION_ID) throws Exception

//    void registerClickSignLink(LOAN_APPLICATION_ID) throws Exception

    @Test
    void shouldRegisterNoAuthLinkExpiration() {
        Executable executable = {
            loanApplicationDAO.registerNoAuthLinkExpiration(LOAN_APPLICATION_ID, 90)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldPreApprovalValidation() {
        JSONObject json = loanApplicationDAO.preApprovalValidation(LOAN_APPLICATION_ID)

        assertNotNull(json)
    }

    @Test
    void shouldCallBots() {
        boolean done = loanApplicationDAO.shouldCallBots(LOAN_APPLICATION_ID)

        assertTrue(done)
    }

    @Test
    void shouldCalculateSalaryAdvanceAmountByEvaluationId() {
        SalaryAdvanceCalculatedAmount salaryAdvanceCalculatedAmount = loanApplicationDAO.calculateSalaryAdvanceAmmount(EVALUATION_ID, EMPLOYEE_ID, EMPLOYER_ID, Configuration.defaultLocale)

        assertNotNull(salaryAdvanceCalculatedAmount)
    }

    @Test
    void shouldCalculateSalaryAdvanceAmountByEmployeeId() {
        SalaryAdvanceCalculatedAmount salaryAdvanceCalculatedAmount = loanApplicationDAO.calculateSalaryAdvanceAmmount(EMPLOYEE_ID, EMPLOYER_ID, Configuration.defaultLocale)

        assertNotNull(salaryAdvanceCalculatedAmount)
    }

    @Test
    void shouldResetLoanApplication() {
        Executable executable = { loanApplicationDAO.resetLoanApplication(LOAN_APPLICATION_ID) }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldCreateLoanOffersSalaryAdvance() {
        LoanOffer loanOffer = loanApplicationDAO.createLoanOffersSalaryAdvance(LOAN_APPLICATION_ID, 5000.0, 0.0, EMPLOYER_ID)

        assertNotNull(loanOffer)
    }

    @Test
    void shouldRegisterIovationResponse() {
        Executable executable = {
            loanApplicationDAO.registerIovationResponse(LOAN_APPLICATION_ID, new JSONObject())
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void mustCallIovation() {
        boolean done = loanApplicationDAO.mustCallIovation(LOAN_APPLICATION_ID)

        assertTrue(done)
    }

    @Test
    void shouldGetIovationByLoanApplication() {
        JSONObject json = loanApplicationDAO.getIovationByLoanApplication(LOAN_APPLICATION_ID)

        assertNull(json)
    }

    @Test
    void shouldGetIovationByPerson() {
        JSONArray jsonArray = loanApplicationDAO.getIovationByPerson(PERSON_ID)

        assertNull(jsonArray)
    }

    @Test
    void shouldRegisterConsolidationAccount() {
        Executable executable = {
            loanApplicationDAO.registerConsolidationAccount(LOAN_APPLICATION_ID, ConsolidationAccountType.DISPONIBILIDAD_EFECTIVO, null, 0.0, Configuration.DEFAULT_INSTALLMENTS, 0.0, true, '11111111111111111111', ENTITY_ID, '030107')
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldGetConsolidationAccounts() {
        List<ConsolidableDebt> consolidableDebtList = loanApplicationDAO.getConsolidationAccounts(LOAN_APPLICATION_ID)

        assertNull(consolidableDebtList)
    }

//    Double getPreConsolidationMonthlyInstallment(LOAN_APPLICATION_ID) throws Exception

//    LoanApplicationUpdateParams getLoanApplicationUpdateParams(LOAN_APPLICATION_ID, Configuration.defaultLocale) throws Exception

    @Test
    void shouldGetRecognitionResults() {
        List<RecognitionResultsPainter> recognitionResultsPainterList = loanApplicationDAO.getRecognitionResults(PERSON_ID, Configuration.defaultLocale)

        assertNull(recognitionResultsPainterList)
    }

    @Test
    void shouldRegisterAmazonRekognition() {
        Executable executable = {
            loanApplicationDAO.registerAmazonRekognition(LOAN_APPLICATION_ID, 90.0, new JSONObject().toString(), 10, 11, 12, 13)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldRegisterAmazonRekognitionFacesLabels() {
        Executable executable = {
            loanApplicationDAO.registerAmazonRekognitionFacesLabels(LOAN_APPLICATION_ID, new JSONObject().toString(), new JSONObject().toString())
        }

        assertDoesNotThrow(executable)
    }

//    SalaryAdvanceCalculatedAmount calculateAgreementAMOUNT(EMPLOYEE_ID, EMPLOYER_ID, Configuration.defaultLocale) throws Exception

    @Test
    void shouldGetConsolidationSavings() {
        Double savings = loanApplicationDAO.getConsolidationSavings(LOAN_APPLICATION_ID)

        assertNull(savings)
    }

    @Test
    void shouldGetLoanOffersAll() {
        List<LoanOffer> loanOfferList = loanApplicationDAO.getLoanOffersAll(LOAN_APPLICATION_ID)

        assertNull(loanOfferList)
    }

    @Test
    void shouldDoUpdates() {
        Executable updateQuestion = {
            loanApplicationDAO.updateCurrentQuestion(LOAN_APPLICATION_ID, ProcessQuestion.Question.Constants.OFFER)
        }
        Executable updateSequence = {
            loanApplicationDAO.updateQuestionSequence(LOAN_APPLICATION_ID, new JSONObject().toString())
        }
        Executable updatePerson = { loanApplicationDAO.updatePerson(LOAN_APPLICATION_ID, PERSON_ID, USER_ID) }
        Executable updateReason = { loanApplicationDAO.updateReason(LOAN_APPLICATION_ID, REASON_ID) }
        Executable updateAmount = { loanApplicationDAO.updateAmount(LOAN_APPLICATION_ID, 5000) }
        Executable updateInstallments = {
            loanApplicationDAO.updateInstallments(LOAN_APPLICATION_ID, Configuration.DEFAULT_INSTALLMENTS)
        }
        Executable updateUsage = { loanApplicationDAO.updateUsage(LOAN_APPLICATION_ID, 1) }
        Executable updateDownPayment = {
            loanApplicationDAO.updateDownPayment(LOAN_APPLICATION_ID, 10000)
        }
        Executable updateDownPaymentCurrency = {
            loanApplicationDAO.updateDownPaymentCurrency(LOAN_APPLICATION_ID, Currency.PEN)
        }
        Executable updateRegisterType = {
            loanApplicationDAO.updateRegisterType(LOAN_APPLICATION_ID, LoanApplicationRegisterType.EMAIL)
        }
        Executable updateConsentNavGeolocation = {
            loanApplicationDAO.updateConsentNavGeolocation(LOAN_APPLICATION_ID, true)
        }
        Executable updateMessengerLink = {
            loanApplicationDAO.updateMessengerLink(LOAN_APPLICATION_ID, true)
        }
        Executable updateTCEA = { loanApplicationDAO.updateTCEA(LOAN_OFFER_ID, 100.0) }
        Executable updateSolvenTCEA = {
            loanApplicationDAO.updateSolvenTCEA(LOAN_OFFER_ID, 100.0)
        }
        Executable updateSelectedLoanOffer = {
            loanApplicationDAO.registerSelectedLoanOffer(LOAN_OFFER_ID, new Date())
        }
        Executable updateFirstDueDateWithSchedules = {
            loanApplicationDAO.updateFirstDueDateWithSchedules(LOAN_APPLICATION_ID, new Date())
        }
        Executable updateFirstDueDate = {
            loanApplicationDAO.updateFirstDueDate(LOAN_APPLICATION_ID, new Date())
        }
        Executable updateApplicationStatus = {
            loanApplicationDAO.updateLoanApplicationStatus(LOAN_APPLICATION_ID, LoanApplicationStatus.APPROVED, SYS_USER_ID)
        }
        Executable updateProductCategory = {
            loanApplicationDAO.updateProductCategory(LOAN_APPLICATION_ID, PRODUCT_CATEGORY_ID)
        }
        Executable updateFormAssistant = {
            loanApplicationDAO.updateFormAssistant(LOAN_APPLICATION_ID, SYS_USER_ID)
        }
        Executable updateEntityId = { loanApplicationDAO.updateEntityId(LOAN_APPLICATION_ID, SELECTED_ENTITY_ID) }
        Executable updateProductId = { loanApplicationDAO.updateProductId(LOAN_APPLICATION_ID, PRODUCT_ID) }
        Executable updateEntityUser = { loanApplicationDAO.updateEntityUser(LOAN_APPLICATION_ID, ENTITY_USER_ID) }
        Executable updateLoanOfferGeneratedStatus = {
            loanApplicationDAO.updateLoanOfferGeneratedStatus(LOAN_APPLICATION_ID, true)
        }
        Executable updateVehicleId = { loanApplicationDAO.updateVehicleId(LOAN_APPLICATION_ID, VEHICLE_ID) }
        Executable updateEntityApplicationExpirationDate = {
            loanApplicationDAO.updateEntityApplicationExpirationDate(LOAN_APPLICATION_ID, new Date())
        }
        Executable updateEntityApplicationCode = {
            loanApplicationDAO.updateEntityApplicationCode(LOAN_APPLICATION_ID, LOAN_APPLICATION_CODE)
        }
        Executable updateGAClientID = { loanApplicationDAO.updateGAClientID(LOAN_APPLICATION_ID, 'gaclientid') }
        Executable updateUserAgent = { loanApplicationDAO.updateUserAgent(LOAN_APPLICATION_ID, 'userAgent') }
        Executable updateLoanApplicationFilesUploaded = {
            loanApplicationDAO.updateLoanApplicationFilesUploaded(LOAN_APPLICATION_ID, true)
        }
        Executable updatePercentageProgress = {
            loanApplicationDAO.updatePercentageProgress(LOAN_APPLICATION_ID, 100.0)
        }
        Executable updatePercentageRemoveProgress = {
            loanApplicationDAO.updatePercentageRemoveProgress(LOAN_APPLICATION_ID)
        }
        Executable updateEvaluationProcessReadyPreEvaluation = {
            loanApplicationDAO.updateEvaluationProcessReadyPreEvaluation(LOAN_APPLICATION_ID, true)
        }
        Executable updateEvaluationProcessReadyEvaluation = {
            loanApplicationDAO.updateEvaluationProcessReadyEvaluation(LOAN_APPLICATION_ID, true)
        }
        Executable updateGoogleClickId = { loanApplicationDAO.updateGoogleClickId(LOAN_APPLICATION_ID, 'gclid') }
        Executable updateEvaluationProcessSendDelayedEmail = {
            loanApplicationDAO.updateEvaluationProcessSendDelayedEmail(LOAN_APPLICATION_ID, true)
        }
        Executable updateEntityCustomData = {
            loanApplicationDAO.updateEntityCustomData(LOAN_APPLICATION_ID, new JSONObject())
        }
        Executable updateEvaluationProcessSynthesizedStatus = {
            loanApplicationDAO.updateEvaluationProcessSynthesizedStatus(LOAN_APPLICATION_ID, LoanApplicationEvaluationsProcess.STATUS_FINISHED)
        }
        Executable updateLoanApplicationEvaluationStartDate = {
            loanApplicationDAO.updateLoanApplicationEvaluationStartDate(LOAN_APPLICATION_ID, new Date())
        }
        Executable updateSmsSent = { loanApplicationDAO.updateSmsSent(LOAN_APPLICATION_ID) }
        Executable updateDisableTracking = {
            loanApplicationDAO.updateDisableTracking(LOAN_APPLICATION_ID, true)
        }
        char questionFlow = 'F'
        Executable updateQuestionFlow = {
            loanApplicationDAO.updateQuestionFlow(LOAN_APPLICATION_ID, questionFlow)
        }
        Executable updateLeadParams = {
            loanApplicationDAO.updateLeadParams(LOAN_APPLICATION_ID, new JSONObject())
        }
        Executable updateHardFilterMessage = {
            loanApplicationDAO.updateHardFilterMessage(LOAN_APPLICATION_ID, 'hardFilterMessage')
        }
        Executable updateExpirationDate = {
            loanApplicationDAO.updateExpirationDate(LOAN_APPLICATION_ID, new Date())
        }
        Executable updateLoanOfferSignaturePinValidation = {
            loanApplicationDAO.updateLoanOfferSignaturePinValidation(LOAN_APPLICATION_ID, true)
        }
        Executable updateSelectedEntityId = {
            loanApplicationDAO.updateSelectedEntityId(LOAN_APPLICATION_ID, SELECTED_ENTITY_ID)
        }
        Executable updateSelectedProductId = {
            loanApplicationDAO.updateSelectedProductId(LOAN_APPLICATION_ID, PRODUCT_ID)
        }
        Executable updateSelectedEntityProductParamId = {
            loanApplicationDAO.updateSelectedEntityProductParamId(LOAN_APPLICATION_ID, ENTITY_PRODUCT_PARAMETER_ID)
        }

        assertDoesNotThrow(updateQuestion)
        assertDoesNotThrow(updateSequence)
        assertDoesNotThrow(updatePerson)
        assertDoesNotThrow(updateReason)
        assertDoesNotThrow(updateAmount)
        assertDoesNotThrow(updateInstallments)
        assertDoesNotThrow(updateUsage)
        assertDoesNotThrow(updateDownPayment)
        assertDoesNotThrow(updateDownPaymentCurrency)
        assertDoesNotThrow(updateRegisterType)
        assertDoesNotThrow(updateConsentNavGeolocation)
        assertDoesNotThrow(updateMessengerLink)
        assertDoesNotThrow(updateTCEA)
        assertDoesNotThrow(updateSolvenTCEA)
        assertDoesNotThrow(updateSelectedLoanOffer)
        assertDoesNotThrow(updateFirstDueDateWithSchedules)
        assertDoesNotThrow(updateFirstDueDate)
        assertDoesNotThrow(updateApplicationStatus)
        assertDoesNotThrow(updateProductCategory)
        assertDoesNotThrow(updateFormAssistant)
        assertDoesNotThrow(updateEntityId)
        assertDoesNotThrow(updateProductId)
        assertDoesNotThrow(updateEntityUser)
        assertDoesNotThrow(updateLoanOfferGeneratedStatus)
        assertDoesNotThrow(updateVehicleId)
        assertDoesNotThrow(updateEntityApplicationExpirationDate)
        assertDoesNotThrow(updateEntityApplicationCode)
        assertDoesNotThrow(updateGAClientID)
        assertDoesNotThrow(updateUserAgent)
        assertDoesNotThrow(updateLoanApplicationFilesUploaded)
        assertDoesNotThrow(updatePercentageProgress)
        assertDoesNotThrow(updatePercentageRemoveProgress)
        assertDoesNotThrow(updateEvaluationProcessReadyPreEvaluation)
        assertDoesNotThrow(updateEvaluationProcessReadyEvaluation)
        assertDoesNotThrow(updateGoogleClickId)
        assertDoesNotThrow(updateEvaluationProcessSendDelayedEmail)
        assertDoesNotThrow(updateEntityCustomData)
        assertDoesNotThrow(updateEvaluationProcessSynthesizedStatus)
        assertDoesNotThrow(updateLoanApplicationEvaluationStartDate)
        assertDoesNotThrow(updateSmsSent)
        assertDoesNotThrow(updateDisableTracking)
        assertDoesNotThrow(updateQuestionFlow)
        assertDoesNotThrow(updateLeadParams)
        assertDoesNotThrow(updateHardFilterMessage)
        assertDoesNotThrow(updateExpirationDate)
        assertDoesNotThrow(updateLoanOfferSignaturePinValidation)
        assertDoesNotThrow(updateSelectedEntityId)
        assertDoesNotThrow(updateSelectedProductId)
        assertDoesNotThrow(updateSelectedEntityProductParamId)
    }

    @Test
    void shouldUpdateOffersQueryBotId() {
        Executable executable = {
            loanApplicationDAO.updateOffersQueryBotId(LOAN_APPLICATION_ID, Bot.ACCESO_OFFERS)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldRegisterLoanApplicationReclosure() {
        Executable executable = {
            loanApplicationDAO.registerLoanApplicationReclosure(LOAN_APPLICATION_ID, ENTITY_ID, false, 4000.0)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdateEFLSession() {
        Executable executable = {
            loanApplicationDAO.updateEFLSession(LOAN_APPLICATION_ID, 5.0, new JSONObject().toString(), 'scoreConfidence')
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdateLoanApplicationFromSelfEvaluation() {
        Executable executable = {
            loanApplicationDAO.updateLoanApplicationFromSelfEvaluation(SELF_EVALUATION_ID, LOAN_APPLICATION_ID)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldExecuteEFLEvaluation() {
        Executable executable = { loanApplicationDAO.executeEFLEvaluation(LOAN_APPLICATION_ID) }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldRegisterEFLSession() {
        Executable executable = { loanApplicationDAO.registerEFLSession(LOAN_APPLICATION_ID, 'eflSessionUid') }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldGetPreliminaryEvaluations() {
        List<LoanApplicationPreliminaryEvaluation> loanApplicationPreliminaryEvaluationList = loanApplicationDAO.getPreliminaryEvaluations(LOAN_APPLICATION_ID, Configuration.defaultLocale)

        assertNotNull(loanApplicationPreliminaryEvaluationList)
    }

    @Test
    void shouldInsertLoanOffer() {
        LoanOffer loanOffer = new LoanOffer()
        loanOffer.setAmmount(5000.0)
        loanOffer.setInstallments(24)
        loanOffer.setInstallmentAmmount(4000.0)
        loanOffer.setEffectiveAnualRate(150.0)
        loanOffer.setEffectiveDailyRate(120.0)
        loanOffer.setMonthlyRate(100.0)
        loanOffer.setLoanCommission(50.0)
        loanOffer.setMinAmmount(5000.0)
        loanOffer.setMaxAmmount(10000.0)
        loanOffer.setMaxInstallments(36)
        loanOffer.setRccCodMes(100)
        loanOffer.setCommission(0.50)
        loanOffer.setCommissionIgv(5.0)
        loanOffer.setTotalCommission(5.0)
        loanOffer.setEntityId(ENTITY_ID)
        loanOffer.setLoanOfferOrder(1)
        loanOffer.setCommission2(50.0)
        loanOffer.setMoratoriumRate(10.0)
        loanOffer.setEntityScore(10)
        loanOffer.setDownPayment(100.0)
        loanOffer.setEffectiveAnnualCostRate(100.0)
        Integer done = loanApplicationDAO.insertLoanOffer(LOAN_APPLICATION_ID, PERSON_ID, loanOffer, ENTITY_PRODUCT_PARAMETER_ID)

        assertNotNull(done)
    }

    @Test
    void shouldInsertLoanOfferSchedule() {
        Executable executable = {
            loanApplicationDAO.insertLoanOfferSchedule(LOAN_OFFER_ID, new JSONArray().toString())
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldRegisterEvaluationRejection() {
        Executable executable = {
            loanApplicationDAO.updateEvaluationStep(EVALUATION_ID, 69, new Date())
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdatePreliminaryEvaluationStep() {
        Executable executable = {
            loanApplicationDAO.updatePreliminaryEvaluationStep(PRELIMINARY_EVALUATION_ID, HardFilter.EMPLOYER_ID, 'message', 1, new Date())
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldGetEvaluations() {
        List<LoanApplicationEvaluation> loanApplicationEvaluationList = loanApplicationDAO.getEvaluations(LOAN_APPLICATION_ID, Configuration.defaultLocale)

        assertNotNull(loanApplicationEvaluationList)
    }

    @Test
    void shouldGetLoanApplicationByEntityApplicationCode() {
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplicationByEntityApplicationCode(ENTITY_APPLICATION_CODE, Configuration.defaultLocale)

        assertNull(loanApplication)
    }

    @Test
    void shouldGetLoanApplicationUserFiles() {
        List<UserFile> userFileList = loanApplicationDAO.getLoanApplicationUserFiles(LOAN_APPLICATION_ID)

        assertNull(userFileList)
    }

    @Test
    void shouldAssignManagementAnalyst() {
        Executable executable = {
            loanApplicationDAO.assignManagementAnalyst(LOAN_APPLICATION_ID, SYS_USER_ID, SYS_USER_ID)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldUnassignManagementAnalyst() {
        Executable executable = { loanApplicationDAO.unassignManagementAnalyst(LOAN_APPLICATION_ID) }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldRegisterTrackingAction() {
        Executable executable = {
            loanApplicationDAO.registerTrackingAction(LOAN_APPLICATION_ID, USER_ID, TrackingAction.INTERESTED, 1, new Date())
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldRegisterTrackingActionContactPerson() {
        Executable executable = {
            loanApplicationDAO.registerTrackingActionContactPerson(LOAN_APPLICATION_ID, USER_ID, TrackingAction.BUSY, 1, new Date(), true, 1)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldGetLoanApplicationTrackingActions() {
        List<LoanApplicationTrackingAction> loanApplicationTrackingActionList = loanApplicationDAO.getLoanApplicationTrackingActions(LOAN_APPLICATION_ID)

        assertNull(loanApplicationTrackingActionList)
    }

    @Test
    void shouldReportMissingDocumentation() {
        Executable executable = { loanApplicationDAO.reportMissingDocumentation(LOAN_APPLICATION_ID) }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldGetEflQuestionConfiguration() {
        Pair<Boolean, Boolean> pair = loanApplicationDAO.getEflQuestionConfiguration(LOAN_APPLICATION_ID)

        assertNotNull(pair)
    }

    @Test
    void shouldUpdateLoanApplicationChangeRate() {
        Executable executable = {
            loanApplicationDAO.updateLoanApplicationChangeRate(LOAN_APPLICATION_ID, 0.0, ENTITY_ID)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdateDebtnessValues() {
        Executable executable = { loanApplicationDAO.updateDebtnessValues(LOAN_APPLICATION_ID) }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldGetLoanApplicationEvaluationsProcess() {
        LoanApplicationEvaluationsProcess loanApplicationEvaluationsProcess = loanApplicationDAO.getLoanApplicationEvaluationsProcess(LOAN_APPLICATION_ID)

        assertNull(loanApplicationEvaluationsProcess)
    }

    @Test
    void shouldUpdateLoanApplicationEvaluationProcessEvaluationBot() {
        Executable executable = {
            loanApplicationDAO.updateLoanApplicationEvaluationProcessEvaluationBot(LOAN_APPLICATION_ID, Arrays.asList(Bot.BCRA))
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdateLoanApplicationEvaluationProcessEvaluationStatus() {
        char status = 'S'
        Executable executable = {
            loanApplicationDAO.updateLoanApplicationEvaluationProcessEvaluationStatus(LOAN_APPLICATION_ID, status)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldGetBureauResult() {
        ReporteCrediticio reporteCrediticio = loanApplicationDAO.getBureauResult(LOAN_APPLICATION_ID, ReporteCrediticio.class)

        assertNull(reporteCrediticio)
    }

//    boolean entityWsHasResult(LOAN_APPLICATION_ID, ENTITY_ID) throws Exception

    @Test
    void shouldUpdateLoanApplicationEvaluationProcessPreEvaluationStatus() {
        char status = 'S'
        Executable executable = {
            loanApplicationDAO.updateLoanApplicationEvaluationProcessPreEvaluationStatus(LOAN_APPLICATION_ID, status)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdateSourceMediumCampaign() {
        Executable executable = {
            loanApplicationDAO.updateSourceMediumCampaign(LOAN_APPLICATION_ID, 'source', 'medium', 'campaign')
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdateTermContent() {
        Executable executable = {
            loanApplicationDAO.updateTermContent(LOAN_APPLICATION_ID, 'term', 'content')
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldGetAdwordsConversions() {
        JSONArray jsonArray = loanApplicationDAO.getAdwordsConversions(new Date(), new Date())

        assertNull(jsonArray)
    }

    @Test
    void shouldGetEntityCustomParamsConfig() {
        EntityCustomParamConfig entityCustomParamConfig = loanApplicationDAO.getEntityCustomParamsConfig(LOAN_APPLICATION_ID)

        assertNull(entityCustomParamConfig)
    }

    @Test
    void shouldGenerateSynthesized() {
        Executable executable = { loanApplicationDAO.generateSynthesized('70061801') }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldRegisterAssistedProcessSchedule() {
        Executable executable = {
            loanApplicationDAO.registerAssistedProcessSchedule(LOAN_APPLICATION_ID, new Date())
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldRegisterApplicationBureauLog() {
        char status = 'S'
        Integer done = loanApplicationDAO.registerApplicationBureauLog(Bureau.EQUIFAX, LOAN_APPLICATION_ID, new Date(), new Date(), status, 'request', 'response')

        assertNotNull(done)
    }

    @Test
    void shouldUpdateApplicationBureauLogResponse() {
        Executable executable = { loanApplicationDAO.updateApplicationBureauLogResponse(Bureau.EQUIFAX, 'response') }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdateApplicationBureauLogRequest() {
        Executable executable = { loanApplicationDAO.updateApplicationBureauLogRequest(Bureau.EQUIFAX, 'request') }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdateApplicationBureauLogFinishDate() {
        Executable executable = {
            loanApplicationDAO.updateApplicationBureauLogFinishDate(Bureau.EQUIFAX, new Date())
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdateApplicationBureauLogStatus() {
        char status = 'S'
        Executable executable = { loanApplicationDAO.updateApplicationBureauLogStatus(Bureau.EQUIFAX, status) }

        assertDoesNotThrow(executable)
    }

//    List<LoanOffer> getAllLoanOffers(LOAN_APPLICATION_ID) throws Exception

    @Test
    void shouldGetExperianResultList() {
        List<ExperianResult> experianResultList = loanApplicationDAO.getExperianResultList(LOAN_APPLICATION_ID)

        assertNull(experianResultList)
    }

    @Test
    void shouldUpdateApplicationOfferRejection() {
        Executable executable = {
            loanApplicationDAO.updateApplicationOfferRejection(LOAN_APPLICATION_ID, null)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldGetComparableLoanOffers() {
        List<LoanOffer> loanOfferList = loanApplicationDAO.getComparableLoanOffers(LOAN_APPLICATION_ID)

        assertNull(loanOfferList)
    }

    @Test
    void shouldGetEntityProductEvaluationProceses() {
        List<EntityProductEvaluationsProcess> entityProductEvaluationsProcessList = loanApplicationDAO.getEntityProductEvaluationProceses(LOAN_APPLICATION_ID)

        assertNotNull(entityProductEvaluationsProcessList)
    }

    @Test
    void shouldUpdateLoanApplicationPreliminaryEvaluationStatus() {
        char status = 'S'
        Executable executable = {
            loanApplicationDAO.updateLoanApplicationPreliminaryEvaluationStatus(LOAN_APPLICATION_ID, ENTITY_ID, PRODUCT_ID, status)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdateLoanApplicationEvaluationStatus() {
        char status = 'S'
        Executable executable = {
            loanApplicationDAO.updateLoanApplicationEvaluationStatus(LOAN_APPLICATION_ID, ENTITY_ID, PRODUCT_ID, status)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldRegisterGuaranteedVehicle() {
        Executable executable = {
            loanApplicationDAO.registerGuaranteedVehicle(LOAN_APPLICATION_ID, GUARANTEED_VEHICLE_ID, 'abcd1234')
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldGetAvailableDates() {
        List<AppointmentSchedule> appointmentScheduleList = loanApplicationDAO.getAvailableDates()

        assertNotNull(appointmentScheduleList)
    }

    @Test
    void shouldRegisterAppointmentSchedule() {
        String registered = loanApplicationDAO.registerAppointmentSchedule(LOAN_APPLICATION_ID, new Date(), APPOINTMENT_SCHEDULE_ID)

        assertNotNull(registered)
    }

    @Test
    void shouldRegisterAppointmentScheduleWithAppointmentPlace() {
        Executable executable = {
            loanApplicationDAO.registerAppointmentSchedule(LOAN_APPLICATION_ID, new Date(), APPOINTMENT_SCHEDULE_ID, 'appointmentPlace')
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldGetReferredLeadByLoanApplicationId() {
        List<ReferredLead> referredLeadList = loanApplicationDAO.getReferredLeadByLoanApplicationId(LOAN_APPLICATION_ID)

        assertNull(referredLeadList)
    }

    @Test
    void shouldRegisterReferredLead() {
        Executable executable = { loanApplicationDAO.registerReferredLead(LOAN_APPLICATION_ID, ENTITY_ID) }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldGetRescueScreenParams() {
        RescueScreenParams rescueScreenParams = loanApplicationDAO.getRescueScreenParams(LOAN_APPLICATION_ID)

        assertNotNull(rescueScreenParams)
    }

    @Test
    void shouldGetUtmParameters() {
        HashMap<String, HashMap<String, List>> hashMap = loanApplicationDAO.getUtmParameters()

        assertNotNull(hashMap)
    }

    @Test
    void shouldGetApprovedLoanApplication() {
        PreApprovedInfo preApprovedInfo = loanApplicationDAO.getApprovedLoanApplication(LOAN_APPLICATION_ID, Configuration.defaultLocale)

        assertNull(preApprovedInfo)
    }

    @Test
    void shouldGetLoanApplicationByEntityUser() {
        List<LoanApplication> loanApplicationList = loanApplicationDAO.getLoanApplicationByEntityUser(ENTITY_USER_ID, LoanApplication.class)

        assertNotNull(loanApplicationList)
    }

    @Test
    void shouldGetMaxPreapprovedAmount() {
        Integer max = loanApplicationDAO.getMaxPreapprovedAmount(LOAN_APPLICATION_ID)

        assertNotNull(max)
    }

//    void approvedDataLoanApplication(Boolean approved, ENTITY_ID, LOAN_APPLICATION_ID, ENTITY_PRODUCT_PARAMETER_ID) throws Exception

    @Test
    void shouldGetLeadLoanApplicationsByEntityAndDate() {
        List<LeadLoanApplication> loanApplicationList = loanApplicationDAO.getLeadLoanApplicationsByEntityAndDate(ENTITY_ID, new Date())

        assertNull(loanApplicationList)
    }

    @Test
    void shouldGetApprovedLeadLoanApplications() {
        List<LeadLoanApplication> leadLoanApplicationList = loanApplicationDAO.getApprovedLeadLoanApplications(ENTITY_ID)

        assertNotNull(leadLoanApplicationList)
    }

    @Test
    void shouldGetLeadLoanApplicationByLoanApplicationId() {
        LeadLoanApplication leadLoanApplication = loanApplicationDAO.getLeadLoanApplicationByLoanApplicationId(LOAN_APPLICATION_ID)

        assertNull(leadLoanApplication)
    }

    @Test
    void shouldRegisterLeadsLoanApplication() {
        Executable executable = {
            loanApplicationDAO.registerLeadsLoanApplication(LOAN_APPLICATION_ID, PRODUCT_ID, ACTIVITY_ID)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldRegisterLoanApplicationComment() {
        Executable executable = {
            loanApplicationDAO.registerLoanApplicationComment(LOAN_APPLICATION_ID, 'comment', SYS_USER_ID, Comment.COMMENT_REASSIGN)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldRegisterLoanApplicationLiftComment() {
        Executable executable = { loanApplicationDAO.registerLoanApplicationLiftComment(LOAN_APPLICATION_ID) }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldSaveLastActiveOneSignalPlayerId() {
        Executable executable = {
            loanApplicationDAO.saveLastActiveOneSignalPlayerId(LOAN_APPLICATION_ID, new JSONObject())
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldValidateSignaturePin() {
        JSONObject json = loanApplicationDAO.validateSignaturePin(LOAN_OFFER_ID, '1234')

        assertNotNull(json)
    }

    @Test
    void createLoanOffers() {
        loanApplicationDAO.createLoanOffers(635033)
    }

//    void selectBestLoanOffer(LOAN_APPLICATION_ID) throws Exception
//
//    void generateOfferSchedule(Integer loanOfferId) throws Exception

}
