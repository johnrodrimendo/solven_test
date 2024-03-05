package com.affirm.tests.common.service


import com.affirm.common.dao.CreditDAO
import com.affirm.common.dao.LoanApplicationDAO
import com.affirm.common.dao.PersonDAO
import com.affirm.common.dao.UserDAO
import com.affirm.common.model.catalog.CountryParam
import com.affirm.common.model.catalog.Currency
import com.affirm.common.model.catalog.EntityProductParams
import com.affirm.common.model.catalog.InteractionContent
import com.affirm.common.model.catalog.LoanApplicationStatus
import com.affirm.common.model.catalog.StreetType
import com.affirm.common.model.transactional.Credit
import com.affirm.common.model.transactional.LoanApplication
import com.affirm.common.model.transactional.LoanOffer
import com.affirm.common.model.transactional.Person
import com.affirm.common.service.*
import com.affirm.common.service.question.Question50Service
import com.affirm.common.service.question.Question95Service
import com.affirm.common.service.question.Question9Service
import com.affirm.common.service.question.QuestionFlowService
import com.affirm.compartamos.CompartamosServiceCall
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseConfig
import com.amazonaws.services.simpleemail.model.SendRawEmailResult
import groovy.transform.CompileStatic
import org.apache.commons.lang3.tuple.Pair
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import java.text.SimpleDateFormat

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

@CompileStatic
class LoanApplicationServiceTest extends BaseConfig{

    @Autowired
    ReportsService reportsService
    @Autowired
    CatalogService catalogService
    @Autowired
    AwsSesEmailService awsSesEmailService
    @Autowired
    CompartamosServiceCall compartamosServiceCall
    @Autowired
    LoanApplicationService loanApplicationService
    @Autowired
    LoanApplicationDAO loanApplicationDAO
    @Autowired
    PixelConversionService pixelConversionService
    @Autowired
    CreditService creditService
    @Autowired
    CreditDAO creditDAO
    @Autowired
    Question9Service question9Service;
    @Autowired
    Question95Service question95Service;
    @Autowired
    Question50Service question50Service;
    @Autowired
    PersonDAO personDao;
    @Autowired
    UserDAO userDao;
    @Autowired
    EvaluationService evaluationService;
    @Autowired
    RekognitionService rekognitionService;

    @Test
    void applicationReportsGenerator(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2018-07-25");
        Date toDate = sdf.parse("2018-08-07");

        def xlsAsBytes = reportsService.createLoanReport(fromDate, toDate, "[51]")
        def fileName = new SimpleDateFormat ("yyyyMMddHHmm'.xls'").format(new Date())
        def fileAbsolutePath = ABSOLUTE_TEST_DIR+'/'+fileName
        def createdFile = new File(fileAbsolutePath)

        checkIfDirExistsElseCreateDir ABSOLUTE_TEST_DIR
        createFileFromBytesArray fileAbsolutePath,xlsAsBytes

        assertTrue createdFile.exists()
        assertTrue createdFile.isFile()
        assertEquals fileName, createdFile.getName()
    }

    @Test
    void sendCreaditGenerationEntityAlertinteraction(){

        EntityProductParams entityParams = catalogService.getEntityProductParamById(9901)

        String emailTo = null
        List<String> emailCC = new ArrayList<>();
        JSONArray arrayRecivers = entityParams.getCreditGenerationEntityAlertInteraction().getJSONArray("receiver");
        for (int i = 0; i < arrayRecivers.length(); i++) {
            if (i == 0)
                emailTo = arrayRecivers.getString(i);
            else
                emailCC.add(arrayRecivers.getString(i));
        }
        InteractionContent emailContent = catalogService.getInteractionContent(entityParams.getCreditGenerationEntityAlertInteraction().getInt("interaction_content_id"), 51);

        JSONObject jsonVariables = new JSONObject();
        jsonVariables.put("body", emailContent.getBody());

        SendRawEmailResult result = awsSesEmailService.sendRawEmail(
                null,
                emailContent.getFromMail(),
                null,
                emailTo,
                emailCC.toArray(new String[emailCC.size()]),
                emailContent.getSubject().replaceAll("%PRODUCT_NAME%", entityParams.getEntityProduct() != null ? entityParams.getEntityProduct() : ""),
                null,
                emailContent.getBody(),
                emailContent.getTemplate(),
                null, jsonVariables, emailContent.getHourOfDay(), null);

        assertTrue result.getSdkHttpMetadata().getHttpStatusCode() != 400
        assertTrue result.messageId != null && result.messageId != ''
    }

    @Test
    void testCompartamosSignatur(){
        println compartamosServiceCall.authHeader("/CreditosSolven/rest/GestionCreditos/TraerVariablesPreEvaluacion")
    }

    @Test
    void testGetRequiredDocumentsByLoanApplication(){
        println loanApplicationService.getRequiredDocumentsByLoanApplication(loanApplicationDAO.getLoanApplication(289470, Configuration.defaultLocale))
    }

    @Test
    void generateLoanApplicatonRedirectionLinkByEntity() {
//        Change solven.properties
//        HOSTENV
//        TRX_DATABASE_URL
        Integer loanApplicationId = 9397
        String token = 'LOANAPPLICATIONTOKEN'

//        SOLVEN entity
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId, Configuration.defaultLocale)
        String solvenUrl = loanApplicationService.generateLoanApplicationLinkEntity(loanApplication, token)
        System.out.println(solvenUrl)
        assertTrue(solvenUrl.contains("dev.solven.pe"))
    }

    @Test
    void generateLinksOfLoans(){
        List<Integer> loanIds = new ArrayList<>();
        loanIds.add(637016);
//        loanIds.add(636922);
//        loanIds.add(636923);
//        loanIds.add(636929);
//        loanIds.add(636931);
//        loanIds.add(636934);
//        loanIds.add(636904);
//        loanIds.add(636942);
        for(Integer i : loanIds){
            LoanApplication loan = loanApplicationDAO.getLoanApplication(i, Configuration.defaultLocale);

            if(loan.getStatus().getId() == LoanApplicationStatus.NEW){
                question95Service.getViewAttributes(QuestionFlowService.Type.LOANAPPLICATION, loan.getId(), Configuration.defaultLocale, false, null);
            }

//            if(loan.getCurrentQuestionId() == 9 && loan.getStatus().getId() == LoanApplicationStatus.EVAL_APPROVED){
//                loanApplicationDAO.updateCurrentQuestion(loan.getId(), 50);
//                question50Service.getViewAttributes(QuestionFlowService.Type.LOANAPPLICATION, loan.getId(), Configuration.defaultLocale, false, null);
//            }

            String token = loanApplicationService.generateLoanApplicationLinkEntity(loan);
            println token
        }
    }

    @Test
    void getConversionToSend(){
        LoanApplication loan = loanApplicationDAO.getLoanApplication(9837, Configuration.defaultLocale);
        JSONArray array = pixelConversionService.getConversionToSend(loan)
        println array.toString()

    }

    @Test
    void getTceaWithoutTax(){
        int loanId = 344388

        List<LoanOffer> offers = loanApplicationDAO.getLoanOffers(loanId);
        LoanOffer selectedOffer = offers.stream().filter{ o -> ((LoanOffer)o).getSelected()}.findFirst().orElse(null)
        double tceaWithoutTax = loanApplicationService.generateOfferTCEAWithoutIva(selectedOffer)
        println tceaWithoutTax

        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanId, Configuration.defaultLocale)
        Credit credit = creditDAO.getCreditByID(loanApplication.getCreditId(), Configuration.defaultLocale, true, Credit.class)
        double creditTceaWithoutTax = creditService.generateCreditTceaWithoutIva(credit)
        println creditTceaWithoutTax
    }

    @Test
    void getAvailableDates(){
        List<Date> dates = question50Service.getScheduleEnablesDates(catalogService.getEntityProductParamById(9101))
        println dates
    }

    @Test
    void gtOfferView(){
        question50Service.getViewAttributes(QuestionFlowService.Type.LOANAPPLICATION, 634734, Configuration.defaultLocale, false, null);
    }

    @Test
    void approveLoanApplicationWithoutAuditValidationTest(){
        CountryParam country = new CountryParam()
        country.setId(CountryParam.COUNTRY_COLOMBIA)

        Currency currency = new Currency()
        currency.setId(3)
        currency.setCurrency("COP")
        currency.setSymbol(Currency.COP_SYMBOL)
        currency.setSeparator(".")
        currency.setDecimalSeparator(",")
        currency.setExchangeRate(3400.03d)

        LoanApplication loanApplication = new LoanApplication()
        loanApplication.setId(10342)
        loanApplication.setPersonId(9870)
        loanApplication.setFirstDueDate(new SimpleDateFormat("yyyy-MM-dd").parse("2020-03-30"))
        loanApplication.setEntityCustomData(new JSONObject("{\"fdmProcedenciaSolicitud\":4}"))
        loanApplication.setCountry(country)
        loanApplication.setCurrency(currency)

        LoanOffer offerSelected = new LoanOffer()
        offerSelected.setInstallmentAmmount(150000d)
        offerSelected.setInstallments(24)
        offerSelected.setAmmount(1000000d)
        offerSelected.setSelected(true)

        Integer sysUserId = 9602


        loanApplicationService.approveLoanApplicationWithoutAuditValidation(
                loanApplication, offerSelected, sysUserId, null, null, null, Configuration.getDefaultLocale())
    }

    @Test
    void approveLoanApplication(){
        loanApplicationService.approveLoanApplication(635289, null, null, null, null, Configuration.defaultLocale)
    }

    @Test
    void createOffers(){
        int loanId = 635589

        List<LoanOffer> offers = loanApplicationDAO.createLoanOffers(loanId);
    }

    @Test
    void sendEmailBanBif(){

        //loanApplicationService.sendBanBifTCLead("NEW_TC_REQUEST", 642556, null);
        loanApplicationService.sendBanBifTCLead("NEW_TC_REQUEST", 642863, null);
//        loanApplicationService.sendBanBifTCLead("GOOD_SCORE", 635940, null);
    }

    @Test
    @Disabled
    public String getStreetType(Integer streetTypeId) throws Exception {
        if(streetTypeId == null) return "-";
        List<StreetType> streetTypes = catalogService.getStreetTypes();
        StreetType streetType = null;
        for (StreetType street : streetTypes) {
            if(street.getId().equals(streetTypeId)) streetType = street;
        }
        if(streetType == null) return "-";
        return streetType.getType();
    }

    @Test
    void getEvaluationProcessByLoanApplication() {
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(638671, Configuration.defaultLocale)
        evaluationService.getEvaluationProcessByLoanApplication(loanApplication)
    }

    @Test
    void sendInteractionTcEnCaminoTest() {
        int loanApplicationId = 636554
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId, Configuration.defaultLocale)
        Person person = personDao.getPerson(loanApplication.getPersonId(), false, Configuration.getDefaultLocale())
        List<Credit> credits = creditDAO.getCreditsByPerson(loanApplication.getPersonId(), Configuration.getDefaultLocale(), Credit.class)
        List<LoanOffer> loanOffers = loanApplicationDAO.getLoanOffers(loanApplicationId)

        LoanOffer offerSelected = null
        Credit credit = null

        for (LoanOffer item : loanOffers) {
            if (item.getSelected()) {
                offerSelected = item
                break
            }
        }

        for (Credit item : credits) {
            if (item.getLoanApplicationId() == loanApplicationId) {
                credit = item;
                break;
            }
        }

        loanApplicationService.sendInteractionTcEnCaminoIfBanBif(offerSelected, loanApplication, credit, person);
    }

    @Test
    void generaetOfferTcea() {
        int loanApplicationId = 641847
        LoanOffer loanOffer = loanApplicationDAO.getLoanOffers(loanApplicationId).get(0)
        loanApplicationService.generateOfferTCEA(loanOffer, loanApplicationId)
    }
    
    @Test
    void generateOfferTCEA() {
        int loanId = 641847
        int offerId = 136708
        LoanOffer loanOffer = loanApplicationDAO.getLoanOffers(loanId).stream().filter{ o -> ((LoanOffer)o).getId() == offerId}.findFirst().orElse(null)
        loanApplicationService.generateOfferTCEA(loanOffer, loanId)
    }

    @Test
    void approveLoanApplicationWithoutAuditValidationTestPeru(){
        CountryParam country = new CountryParam()
        country.setId(CountryParam.COUNTRY_PERU)

        Currency currency = new Currency()
        currency.setId(Currency.PEN)
        currency.setCurrency("S/")
        currency.setSymbol(Currency.PEN_SYMBOL)
        currency.setSeparator(".")
        currency.setDecimalSeparator(",")

        LoanApplication loanApplication =  loanApplicationDAO.getLoanApplication(636689, Configuration.defaultLocale)


        List<LoanOffer> loanOffers = loanApplicationDAO.getLoanOffers(loanApplication.getId());

        Integer sysUserId = 26101


        loanApplicationService.approveLoanApplicationWithoutAuditValidation(
                loanApplication, loanOffers.get(0), sysUserId, null, null, null, Configuration.getDefaultLocale())
    }

    @Test
    void sendDocumentAzteca() {
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(637159, Configuration.getDefaultLocale());
        Date startDate = null;
        Date endDate = null;
        Integer entity = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        String query = null;

        Integer[] products = null;

        Integer[] steps = null;
//        EntityExtranetConfiguration.FunnelConfiguration funnelConfiguration = null;
//        EntityExtranetConfiguration extranetConfiguration = brandingService.getExtranetBrandingAsJson(entity);
//        if(extranetConfiguration != null && extranetConfiguration.getFunnelConfiguration() != null && !extranetConfiguration.getFunnelConfiguration().isEmpty() && loanApplication.getProduct() != null && loanApplication.getProductCategoryId() != null) funnelConfiguration = extranetConfiguration.getFunnelConfiguration().stream().filter(e -> e.getProductCategoryId() == loanApplication.getProductCategoryId()).findFirst().orElse(null);
//        if(funnelConfiguration != null && funnelConfiguration.getSteps() != null && !funnelConfiguration.getSteps().isEmpty()){
//            steps =  funnelConfiguration.getSteps().stream().map(EntityExtranetConfiguration.FunnelStep::getStepId).collect(Collectors.toList()).toArray(new Integer[funnelConfiguration.getSteps().size()]);
//        }

        Integer[] loanIds = [637159];

        Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
        Pair<byte[], String> result = Pair.of(reportsService.createTrayExtranetReport(entity, loanIds, products, steps), String.format("solicitud_%s_%s_%s.xls", person.getDocumentNumber(), new SimpleDateFormat("ddMMyyyy").format(new Date()), new SimpleDateFormat("HHmmss").format(new Date())));

        def fileName = new SimpleDateFormat("yyyyMMddHHmm'.pdf'").format(new Date())
        def fileAbsolutePath = ABSOLUTE_TEST_DIR + '/' + fileName
        def createdFile = new File(fileAbsolutePath)

        checkIfDirExistsElseCreateDir ABSOLUTE_TEST_DIR
        createFileFromBytesArray fileAbsolutePath, result.getLeft()

        assertTrue createdFile.exists()
        assertTrue createdFile.isFile()
        assertEquals fileName, createdFile.getName()
    }

    @Test
    void sendEmailInterTest(){

        int loanId = 636986
        LoanOffer loanOffer = loanApplicationDAO.getLoanOffers(loanId).stream().filter{ o -> ((LoanOffer)o).getSelected()}.findFirst().orElse(null)
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanId, Configuration.defaultLocale);
        Credit credit = creditDAO.getCreditByID(loanApplication.getCreditId(), Configuration.defaultLocale, false, Credit.class);
        Person person = personDao.getPerson(loanApplication.getPersonId(), false, Configuration.defaultLocale);
        loanApplicationService.sendInteractionEmail(loanOffer, loanApplication, credit, person)

    }

    @Test
    void getBackwardsQuestion(){

        int loanId = 637204
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanId, Configuration.defaultLocale);
        evaluationService.getBackwardId(loanApplication)

    }

    @Test
    void sendLoanWebhookEvent(){

        int loanId = 638253
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanId, Configuration.defaultLocale);
        loanApplicationService.sendLoanWebhookEvent("approved", loanApplication)

    }

    @Test
    void runRekognition() throws Exception {

        int loanId = 641319
        int personId = 512017
        int userId = 467963

        rekognitionService.compareOneDniVsSelfieNotYetCompared(641319, 512017, 467963, Configuration.getDefaultLocale(), true)
    }

    @Test
    @Disabled
    void ipIsValid(){
        int loanApplicationId = 641430;
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId,Configuration.getDefaultLocale());
        boolean ipLocationIsValid = loanApplicationService.validateLoanApplicationByIPGeolocation(loanApplication, null, Configuration.getDefaultLocale())
    }

    @Test
    @Disabled
    void sendPostSignatureInteractions(){
        int loanApplicationId = 643207;
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId,Configuration.getDefaultLocale());
        loanApplicationService.sendPostSignatureInteractions(loanApplication.getUserId(), loanApplicationId, null, null, null, Configuration.getDefaultLocale())
    }
}
