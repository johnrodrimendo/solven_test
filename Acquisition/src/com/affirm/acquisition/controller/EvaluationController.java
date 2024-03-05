package com.affirm.acquisition.controller;

import com.affirm.acceso.AccesoServiceCall;
import com.affirm.acceso.model.Firma;
import com.affirm.bancodelsol.model.CampaniaBds;
import com.affirm.bancoazteca.model.AztecaAgency;
import com.affirm.bancodelsol.service.BancoDelSolService;
import com.affirm.client.controller.AgreementController;
import com.affirm.client.model.form.*;
import com.affirm.common.dao.*;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.UpdateLoanApplicationForm;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.service.impl.LoanApplicationServiceImpl;
import com.affirm.common.service.question.Question11Service;
import com.affirm.common.service.question.Question50Service;
import com.affirm.common.util.*;
import com.affirm.compartamos.CompartamosServiceCall;
import com.affirm.compartamos.model.GenerarCreditoRequest;
import com.affirm.compartamos.model.GenerarCreditoResponse;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */

@Controller("evaluationController")
@Scope("request")
public class EvaluationController {

    private static final Logger logger = Logger.getLogger(EvaluationController.class);
    public static final String URL = Configuration.EVALUATION_CONTROLLER_URL;

    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CreditService creditService;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private AgreementService agreementService;
    @Autowired
    private CountryContextService countryContextService;
    @Autowired
    private AccesoServiceCall accesoServiceCall;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private TranslatorDAO translatorDAO;
    @Autowired
    private CompartamosServiceCall compartamosServiceCall;
    @Autowired
    private UserService userService;
    @Autowired
    private BancoDelSolService bancoDelSolService;
    @Autowired
    private Question50Service question50Service;
    @Autowired
    private RccDAO rccDao;
    @Autowired
    private EvaluationDAO evaluationDao;
    @Autowired
    private QuestionPercentageService questionPercentageService;
    @Autowired
    private Question11Service question11Service;
    @Autowired
    private LoanApplicationApprovalValidationService loanApplicationApprovalValidationService;

    @RequestMapping(value = "/" + URL + "/backwards", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object goBack(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "token", required = false) String token) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        Integer questionToGo = evaluationService.backward(loanApplication, request);
        if(questionToGo == null){
            AjaxResponse.sendReloadPage(response);
            return null;
        }else{
            return ProcessQuestionResponse.goToQuestion(questionToGo);
        }
    }

    @RequestMapping(value = "/{categoryUrl:" +
            ProductCategory.CONSUMO_CATEGORY_URL + "|" +
            ProductCategory.ADELANTO_SUELDO_CATEGORY_URL + "|" +
            ProductCategory.LEADS_CATEGORY_URL + "|" +
            ProductCategory.TARJETA_CREDITO_CATEGORY_URL + "|" +
            ProductCategory.GATEWAY_URL + "|" +
            ProductCategory.CUENTA_BANCARIA_URL + "|" +
            ProductCategory.VALIDACION_IDENTIDAD_URL + "|" +
            ProductCategory.CONSEJ0_URL + "|" +
            ProductCategory.VEHICULO_CATEGORY_URL + "}/" + URL, method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object startEvaluation(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "externalParams", required = false) String externalParams,
            @PathVariable("categoryUrl") String categoryUrl,
            @RequestParam(value = "agentSelected", required = false) Integer agentSelected,
            @RequestParam(value = "skipWelcome", required = false) Boolean skipWelcome) throws Exception {

        if (token == null) {
            return new ModelAndView("redirect:/credito-de-consumo");

//            if (agentSelected == null) {
//                model.addAttribute("urlToPOst", "/" + categoryUrl + "/" + URL + "/agent");
//                return "loanApplication/formQuestions/chooseAgent";
//            } else {
//                if (skipWelcome == null || skipWelcome == false) {
//                    model.addAttribute("currentQuestion",
//                            evaluationService.getEvaluationProcessByProductCategory(
//                                    ProductCategory.GET_ID_BY_URL(categoryUrl),
//                                    countryContextService.getCountryParamsByRequest(request).getId(),
//                                    request
//                            ).getFirstQuestionId());
//                } else {
//                    model.addAttribute("currentQuestion", ProcessQuestion.Question.Constants.IDENTITY_DOCUMENT);
//                }
//                model.addAttribute("agent", catalogService.getAgent(agentSelected));
//                return "loanApplication/formQuestions/formQuestions";
//            }
        }

        model.addAttribute("externalParams", externalParams);
        model.addAttribute("evaluationType", "evaluation");
        model.addAttribute("categoryUrl", categoryUrl);
        model.addAttribute("processContactForm", new ProcessContactForm());

        Integer loanAppId = null;

        try{
            loanAppId =  evaluationService.getIdFromToken(token);
        }
        catch (Exception e){
            return "404";
        }

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanAppId, locale);

        // If the self evaluation is new, set the current question with the fist in the config
        if (loanApplication.getCurrentQuestionId() == null) {
            evaluationService.forwardByResult(loanApplication, null, request);
        }

        model.addAttribute("loanApplication", loanApplication);
        model.addAttribute("currentQuestion", loanApplication.getCurrentQuestionId());
        model.addAttribute("evaluationType", "evaluation");
        model.addAttribute("categoryUrl", categoryUrl);
        model.addAttribute("token", token);
        model.addAttribute("agent", loanApplication.getAgent());
        return "loanApplication/formQuestions/formQuestions";
    }

    @RequestMapping(value = "/{categoryUrl:" +
            ProductCategory.CONSUMO_CATEGORY_URL + "|" +
            ProductCategory.ADELANTO_SUELDO_CATEGORY_URL + "|" +
            ProductCategory.LEADS_CATEGORY_URL + "|" +
            ProductCategory.TARJETA_CREDITO_CATEGORY_URL + "|" +
            ProductCategory.GATEWAY_URL + "|" +
            ProductCategory.CUENTA_BANCARIA_URL + "|" +
            ProductCategory.VALIDACION_IDENTIDAD_URL + "|" +
            ProductCategory.CONSEJ0_URL + "|" +
            ProductCategory.VEHICULO_CATEGORY_URL + "}/" + URL + "/reset/{token}", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object Evaluation(
            Locale locale, HttpServletRequest request,
            @PathVariable("categoryUrl") String categoryUrl,
            @PathVariable(value = "token", required = false) String token) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        String agent = String.valueOf(loanApplication.getAgent().getId());

        String url = categoryUrl + "/evaluacion?agentSelected=" + agent + "&skipWelcome=true";

        loanApplicationDao.resetLoanApplication(loanApplication.getId());

        return AjaxResponse.redirect(request.getContextPath() + "/" + url);
    }

    @RequestMapping(value = "/{categoryUrl:" +
            ProductCategory.CONSUMO_CATEGORY_URL + "|" +
            ProductCategory.ADELANTO_SUELDO_CATEGORY_URL + "|" +
            ProductCategory.LEADS_CATEGORY_URL + "|" +
            ProductCategory.TARJETA_CREDITO_CATEGORY_URL + "|" +
            ProductCategory.GATEWAY_URL + "|" +
            ProductCategory.CUENTA_BANCARIA_URL + "|" +
            ProductCategory.VALIDACION_IDENTIDAD_URL + "|" +
            ProductCategory.CONSEJ0_URL + "|" +
            ProductCategory.VEHICULO_CATEGORY_URL + "}/" + URL + "/agent", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object sendAgent(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("categoryUrl") String categoryUrl,
            @RequestParam("agentSelected") String agentSelected,
            @RequestParam("externalParams") String externalParams) throws Exception {
        return AjaxResponse.redirect(request.getContextPath() + "/" + categoryUrl + "/" + URL + "?agentSelected=" + agentSelected + "&externalParams=" + externalParams);
    }

    @RequestMapping(value = "/{categoryUrl:" +
            ProductCategory.CONSUMO_CATEGORY_URL + "|" +
            ProductCategory.ADELANTO_SUELDO_CATEGORY_URL + "|" +
            ProductCategory.LEADS_CATEGORY_URL + "|" +
            ProductCategory.TARJETA_CREDITO_CATEGORY_URL + "|" +
            ProductCategory.GATEWAY_URL + "|" +
            ProductCategory.CUENTA_BANCARIA_URL + "|" +
            ProductCategory.VALIDACION_IDENTIDAD_URL + "|" +
            ProductCategory.CONSEJ0_URL + "|" +
            ProductCategory.VEHICULO_CATEGORY_URL + "}/" + URL + "/{token}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getEvaluationByToken(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("token") String token,
            @PathVariable("categoryUrl") String categoryUrl,
            @RequestParam(value = "showWelcome", required = false, defaultValue = "false") boolean showWelcome) throws Exception {

        Integer loanAppId = null;

        try{
            loanAppId =  evaluationService.getIdFromToken(token);
        }
        catch (Exception e){
            return "404";
        }

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanAppId, locale);
        ProcessContactForm processContactForm = new ProcessContactForm();
        model.addAttribute("processContactForm", processContactForm);
        model.addAttribute("showWelcome", showWelcome);
        model.addAttribute("showWelcomeMessage", "¡Bienvenido nuevamente! Te llevaré al lugar donde dejaste tu última solicitud para que ganes tiempo.");
        if(loanApplication.getEntityId() != null && loanApplication.getEntityId().equals(Entity.BANCO_DEL_SOL)){
            model.addAttribute("isCustomDocumentTitle", true);
            model.addAttribute("customDocumentTitle", "Banco del Sol");
        }

        String decripted = CryptoUtil.decrypt(token);
        if (decripted != null){
            JSONObject jsonObject = new JSONObject(decripted);
            boolean shouldValidateEmail = JsonUtil.getBooleanFromJson(jsonObject, LoanApplicationServiceImpl.SHOULD_VALIDATE_EMAIL_KEY, false);
            if(shouldValidateEmail) {
                List<UserEmail> userEmails = userDAO.getUserEmails(loanApplication.getUserId());
                UserEmail userEmail = userEmails.stream().filter(e->e.getActive()).findFirst().orElse(null);
                userDAO.verifyEmail(loanApplication.getUserId(), userEmail.getId(), true);
                loanApplicationApprovalValidationService.validateAndUpdate(loanApplication.getId(), ApprovalValidation.VERIF_CORREO_ELECTRONICO);
                if(loanApplication != null && loanApplication.getProductCategoryId() != null && loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA && Arrays.asList(ProductCategory.CONSUMO,ProductCategory.CUENTA_BANCARIA).contains(loanApplication.getProductCategoryId())){
                    if(loanApplication.getProductCategoryId() == ProductCategory.CONSUMO)  return "redirect:" + "https://www.alfinbanco.pe/prestamos";
                    else if(loanApplication.getProductCategoryId() == ProductCategory.CUENTA_BANCARIA) return "redirect:" + "https://www.alfinbanco.pe/ahorro";
                }

            }
        }

        Double defaultLatitude = -12.046374;
        Double defaultLongitude = -77.042793;
        model.addAttribute("navLatitude", defaultLatitude);
        model.addAttribute("navLongitude", defaultLongitude);

        // If status is pending_signature go to contract
        if (loanApplication.getStatus().getId() == LoanApplicationStatus.APPROVED && loanApplication.getCreditId() != null) {

            // Generate original schedule
            Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), locale, true, Credit.class);

            model.addAttribute("agent", loanApplication.getAgent());
            if (credit.getStatus().getId() == CreditStatus.REJECTED) {
                model.addAttribute("credit", credit);
                return "loanApplication/formQuestions/creditRejected";
            }

            if(credit.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO &&
                    credit.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_CERO_MEMBRESIA &&
                    credit.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO &&
                    credit.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY &&
                    credit.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY_VIGENTE &&
                    credit.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO &&
                    credit.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE &&
                    credit.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_AZTECA_IDENTIDAD){
                EntityProductParams entityParams = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());

//                creditDao.generateOriginalSchedule(credit);

                model.addAttribute("credit", credit);
                List<UserFile> userFiles = personDao.getUserFiles(loanApplication.getPersonId(), locale)
                        .stream().filter(x -> x.getLoanApplicationId().intValue() == loanApplication.getId()).flatMap(x -> x.getUserFileList().stream())
                        .collect(Collectors.toList());
                model.addAttribute("loanApplication", loanApplication);
                model.addAttribute("title", "Fima tu Contrato");
                model.addAttribute("loanApplicationBankForm", new LoanApplicationBankForm());
                model.addAttribute("loanApplicationSignatureForm", new LoanApplicationSignatureForm());
                model.addAttribute("person", personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false));
                model.addAttribute("entityParams", entityParams);
                List<UserFile> contractFiles = userFiles != null ? userFiles.stream().filter(u -> u.getFileType().getId() == UserFileType.CONTRATO_FIRMA).collect(Collectors.toList()) : null;
                model.addAttribute("signatureLoaded", contractFiles != null && !contractFiles.isEmpty());
                model.addAttribute("contractFiles", contractFiles);
                model.addAttribute("categoryUrl", categoryUrl);
                model.addAttribute("token", token);
                switch (entityParams.getSignatureType()) {
                    case EntityProductParams.CONTRACT_TYPE_DIGITAL:
                    case EntityProductParams.CONTRACT_TYPE_MIXTO:
                        boolean bankRequired = !entityParams.getSelfDisbursement() && entityParams.getDisbursementType() == EntityProductParams.DISBURSEMENT_TYPE_DEPOSIT && credit.getEmployer() == null;
                        boolean paymentApplicationTypeRequired = credit.getEntity().getId() == Entity.COMPARTAMOS;
                        model.addAttribute("bankRequired", bankRequired);
                        model.addAttribute("paymentApplicationTypeRequired", paymentApplicationTypeRequired);
                        return "newPdfViewer";
                    case EntityProductParams.CONTRACT_TYPE_MANUAL:
                        if(Arrays.asList(EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL, EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL_AGENCIAS, EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL_CAMPANA,EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL_SINIESTROS).contains(entityParams.getId())){
                            if(credit.getStatus().getId() == CreditStatus.INACTIVE_W_SCHEDULE){
                                model.addAttribute("type", "bancoDelSolUploadContract");
                                model.addAttribute("customDocumentTitle", "Banco del Sol");
                                model.addAttribute("isCustomDocumentTitle", true);
                                model.addAttribute("agent", loanApplication.getAgent());
                                model.addAttribute("loanApplication", loanApplication);
                                model.addAttribute("credit", credit);
                                model.addAttribute("url",Configuration.getClientDomain(CountryParam.COUNTRY_ARGENTINA));
                                return "formQuestionsExtras";
                            }else{
                                model.addAttribute("type", "bancoDelSolFinalMessage");
                                model.addAttribute("agent", loanApplication.getAgent());
                                model.addAttribute("loanApplication", loanApplication);
                                model.addAttribute("credit", credit);
                                return "formQuestionsExtras";
                            }
                        } else {
                            model.addAttribute("entity", credit.getEntity());
                            model.addAttribute("entityProductParams", entityParams);
                            model.addAttribute("bankAccountInformation", personDao.getPersonBankAccountInformation(locale, loanApplication.getPersonId()));

                            if (entityParams.getId() == EntityProductParams.ENT_PROD_PARAM_FDLM) {
                                model.addAttribute("offerSelected", loanApplicationService.getSelectedOffer(loanApplication.getId()));
                            }

                            return "loanApplication/formQuestions/postSignature";
                        }
                    default:
                        throw new Exception("Unknow signature type");
                }
            }
        } else if (loanApplication.getStatus().getId() == LoanApplicationStatus.APPROVED_SIGNED) {


            Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), locale, true, Credit.class);
            EntityProductParams entityParams = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());

            if(credit.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO &&
                    credit.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_CERO_MEMBRESIA &&
                    credit.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO &&
                    credit.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY &&
                    credit.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY_VIGENTE &&
                    credit.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO &&
                    credit.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE) {

                if (credit.getProduct().getId() == Product.AUTOS && credit.getEntity().getId() == Entity.ACCESO) {
                    // If it requires a down payment and it isnt payed yet, go to waiting for down payment
                    if (!credit.getIsDownPaymentPaid()) {
                        model.addAttribute("type", "waitingDownPayment");
                        model.addAttribute("agent", loanApplication.getAgent());
                        model.addAttribute("loanApplication", loanApplication);
                        model.addAttribute("credit", credit);
                        return "formQuestionsExtras";
                    }

                    // If it requires a down payment and it's payed, go to waiting for entity
                    if (credit.getIsDownPaymentPaid() && (credit.getSubStatus() == null || credit.getSubStatus().getId() == CreditSubStatus.ACCESO_WAITING_FOR_CAR_DEALERSHIP)) {
//                if (false) {
                        model.addAttribute("type", "waitingForEntity");
                        model.addAttribute("agent", loanApplication.getAgent());
                        model.addAttribute("loanApplication", loanApplication);
                        model.addAttribute("entity", credit.getEntity());
                        return "formQuestionsExtras";
                    }

                    // If the entity alred answer with the conformation of the down payment transfer, go to register the signature schedule
                    if (credit.getSubStatus() != null && credit.getSubStatus().getId() == CreditSubStatus.ACCESO_SCHEDULE_SIGNATURE) {
//                if (true) {

                        PersonContactInformation contactInformation = personDao.getPersonContactInformation(locale, loanApplication.getPersonId());
                        PersonOcupationalInformation principalOcupationalInformation =
                                personDao.getPersonOcupationalInformation(locale, loanApplication.getPersonId()).stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL)
                                        .findFirst().orElse(null);

                        model.addAttribute("addressPersonal", contactInformation.getFullAddressBO());
                        model.addAttribute("addressWorkplace", principalOcupationalInformation != null ? principalOcupationalInformation.getAddress() : null);
                        model.addAttribute("addressAcceso1", "Av. 28 de Julio 334, Jesús María");
                        model.addAttribute("addressAcceso2", "Av. Angamos Este N° 1669, Surquillo");
                        model.addAttribute("addressWorkplace", principalOcupationalInformation != null ? principalOcupationalInformation.getAddress() : null);
                        model.addAttribute("agent", loanApplication.getAgent());
                        model.addAttribute("type", "registerSignatureSchedule");
                        model.addAttribute("loanApplication", loanApplication);
                        model.addAttribute("signatureSchedule", creditDao.getSignatureSchedule(credit.getId()));
                        model.addAttribute("form", new RegisterSignatureScheduleForm());
                        model.addAttribute("token", token);
                        return "formQuestionsExtras";
                    }

                    // If the credit is alredy originated and Disbursment, show this message
                    if (credit.getStatus().getId() == CreditStatus.ORIGINATED_DISBURSED) {
//                if (true) {
                        model.addAttribute("agent", loanApplication.getAgent());
                        model.addAttribute("type", "vehicleDisbursed");
                        model.addAttribute("loanApplication", loanApplication);
                        return "formQuestionsExtras";
                    }
                }

                if (entityParams.getId() != EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO) {
                    switch (entityParams.getSignatureType()) {
                        case EntityProductParams.CONTRACT_TYPE_DIGITAL:
                        case EntityProductParams.CONTRACT_TYPE_MIXTO:
                        case EntityProductParams.CONTRACT_TYPE_MANUAL:

                            model.addAttribute("loanApplication", loanApplication);
                            model.addAttribute("entityProductParams", entityParams);
                            model.addAttribute("agent", loanApplication.getAgent());
                            model.addAttribute("bankAccountInformation", personDao.getPersonBankAccountInformation(locale, loanApplication.getPersonId()));
                            model.addAttribute("associated", personDao.getAssociated(credit.getPersonId(), credit.getEntity().getId()));
                            model.addAttribute("associatedUrl", request.getContextPath() + "/" + AgreementController.LOANAPPLICATION_URL + "/" + token + "/associatedFile");
                            model.addAttribute("person", personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false));

                            return "loanApplication/formQuestions/postSignature";

//                    PersonBankAccountInformation bankAccountInformation = personDao.getPersonBankAccountInformation(locale, loanApplication.getPersonId());
//                    model.addAttribute("loanApplication", loanApplication);
//                    model.addAttribute("agent", loanApplication.getAgent());
//                    model.addAttribute("entityProductParams", entityParams);
//                    model.addAttribute("bankAccountInformation", bankAccountInformation);
//                    return "sheetAssociate";
//                case EntityProductParams.CONTRACT_TYPE_MANUAL:
//                    model.addAttribute("entity", credit.getEntity());
//                    model.addAttribute("entityProductParams", entityParams);
//                    return "downloadContract";
                        default:
                            throw new Exception("Unknow signature type");
                    }
                }
            }

        }

        // If the self evaluation is new, set the current question with the first in the config
        if (loanApplication.getCurrentQuestionId() == null) {
            evaluationService.forwardByResult(loanApplication, null, request);
        }

        model.addAttribute("loanApplication", loanApplication);
        model.addAttribute("currentQuestion", loanApplication.getCurrentQuestionId());
        model.addAttribute("evaluationType", "evaluation");
        model.addAttribute("categoryUrl", categoryUrl);
        model.addAttribute("token", token);
        model.addAttribute("agent", loanApplication.getAgent());
        return "loanApplication/formQuestions/formQuestions";
    }

    @RequestMapping(value = "/{categoryUrl:" +
            ProductCategory.CONSUMO_CATEGORY_URL + "|" +
            ProductCategory.ADELANTO_SUELDO_CATEGORY_URL + "|" +
            ProductCategory.LEADS_CATEGORY_URL + "|" +
            ProductCategory.TARJETA_CREDITO_CATEGORY_URL + "|" +
            ProductCategory.GATEWAY_URL + "|" +
            ProductCategory.CUENTA_BANCARIA_URL + "|" +
            ProductCategory.VALIDACION_IDENTIDAD_URL + "|" +
            ProductCategory.CONSEJ0_URL + "|" +
            ProductCategory.VEHICULO_CATEGORY_URL + "}/" + URL + "/{conversionTypeUrl:" +
            Configuration.CONVERSION_EVALUATION + "|" + Configuration.CONVERSION_PRE_EVALUATION + "}/{token}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getEvaluationByTokenConversion(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("token") String token,
            @PathVariable("categoryUrl") String categoryUrl,
            @RequestParam(value = "showWelcome", required = false, defaultValue = "false") boolean showWelcome) throws Exception {
        return "redirect:/" + categoryUrl + "/" + URL + "/" + token;
    }


    @RequestMapping(value = "/" + URL + "/registerSignatureSchedule", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerSignatureSchedule(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response, RegisterSignatureScheduleForm form,
            @RequestParam("token") String token) throws Exception {

        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), locale, false, Credit.class);
        String address = null;
        if (form.getLocationId() == 1) {
            PersonContactInformation contactInformation = personDao.getPersonContactInformation(locale, loanApplication.getPersonId());
            address = contactInformation.getAddressStreetName();
        } else if (form.getLocationId() == 2) {
            PersonOcupationalInformation principalOcupationalInformation =
                    personDao.getPersonOcupationalInformation(locale, loanApplication.getPersonId()).stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL)
                            .findFirst().orElse(null);
            address = principalOcupationalInformation != null ? principalOcupationalInformation.getAddress() : null;
        } else if (form.getLocationId() == 3) {
            address = "Av. 28 de Julio 334, Jesús María";
        } else if (form.getLocationId() == 4) {
            address = "Av. Angamos Este N° 1669, Surquillo";
        }
        CreditSignatureScheduleHour signatureScheduleHour = catalogService.getCreditSignatureScheduleHour(form.getScheduleHour());

        if (credit.getEntity().getId() == Entity.ACCESO) {
            Firma firma = new Firma(
                    Integer.parseInt(loanApplication.getEntityApplicationCode()),
                    form.getScheduleDate(),
                    form.getLocationId(),
                    address,
                    signatureScheduleHour.getStartHour(),
                    signatureScheduleHour.getFinishHour(),
                    "");
            accesoServiceCall.callAgendarFirmas(firma, loanApplication.getId(), 0);
        }

        creditDao.registerSignatureSchedule(
                loanApplication.getCreditId(),
                new SimpleDateFormat("dd/MM/yyyy").parse(form.getScheduleDate()),
                signatureScheduleHour.getHour(),
                address);

        creditDao.updateCreditSubStatus(loanApplication.getCreditId(), CreditSubStatus.ACCESO_PHYSICAL_SIGNATURE);

        loanApplicationService.sendPostSignatureInteractions(loanApplication.getUserId(), loanApplication.getId(), request, response, templateEngine, locale);

        return AjaxResponse.redirect(request.getContextPath() + "/" + ProductCategory.GET_URL_BY_ID(loanApplication.getProductCategoryId()) + "/" + URL + "/" + token);
    }

    @RequestMapping(value = "/{categoryUrl:" +
            ProductCategory.CONSUMO_CATEGORY_URL + "|" +
            ProductCategory.ADELANTO_SUELDO_CATEGORY_URL + "|" +
            ProductCategory.LEADS_CATEGORY_URL + "|" +
            ProductCategory.TARJETA_CREDITO_CATEGORY_URL + "|" +
            ProductCategory.GATEWAY_URL + "|" +
            ProductCategory.CUENTA_BANCARIA_URL + "|" +
            ProductCategory.VALIDACION_IDENTIDAD_URL + "|" +
            ProductCategory.CONSEJ0_URL + "|" +
            ProductCategory.VEHICULO_CATEGORY_URL + "}/" + URL + "/{token}/amountAndInstallments", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getAmountAndInstallments(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("token") String token,
            @PathVariable("categoryUrl") String categoryUrl) throws Exception {
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        model.addAttribute("loanApplication", loanApplication);
        return "fragments/headers :: processHeaderAmount";
    }

    @RequestMapping(value = "/{categoryUrl:" +
            ProductCategory.CONSUMO_CATEGORY_URL + "|" +
            ProductCategory.ADELANTO_SUELDO_CATEGORY_URL + "|" +
            ProductCategory.LEADS_CATEGORY_URL + "|" +
            ProductCategory.TARJETA_CREDITO_CATEGORY_URL + "|" +
            ProductCategory.GATEWAY_URL + "|" +
            ProductCategory.CUENTA_BANCARIA_URL + "|" +
            ProductCategory.VALIDACION_IDENTIDAD_URL + "|" +
            ProductCategory.CONSEJ0_URL + "|" +
            ProductCategory.VEHICULO_CATEGORY_URL + "}/" + URL + "/{token}/updateLoanApplication", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getUpdateLoanApplication(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("token") String token,
            @PathVariable("categoryUrl") String categoryUrl) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        model.addAttribute("token", token);

        if (loanApplication.getProductCategory().getId() != ProductCategory.VEHICULO) {

            Integer maxInstallments;
            Integer minInstallments;
            Integer maxAmount;
            Integer minAmount;

            if (loanApplication.getEntityId() != null) {
                maxInstallments = productService.getMaxInstalmentsEntity(loanApplication.getProductCategoryId(), loanApplication.getCountryId(), loanApplication.getEntityId());
                minInstallments = productService.getMinInstalmentsEntity(loanApplication.getProductCategoryId(), loanApplication.getCountryId(), loanApplication.getEntityId());
                maxAmount = productService.getMaxAmountEntity(loanApplication.getProductCategoryId(), loanApplication.getCountryId(), loanApplication.getEntityId());
                minAmount = productService.getMinAmountEntity(loanApplication.getProductCategoryId(), loanApplication.getCountryId(), loanApplication.getEntityId());
            } else {
                maxInstallments = productService.getMaxInstalments(loanApplication.getProductCategoryId(), loanApplication.getCountryId());
                minInstallments = productService.getMinInstalments(loanApplication.getProductCategoryId(), loanApplication.getCountryId());
                maxAmount = productService.getMaxAmount(loanApplication.getProductCategoryId(), loanApplication.getCountryId());
                minAmount = productService.getMinAmount(loanApplication.getProductCategoryId(), loanApplication.getCountryId());
            }

            List<LoanOffer> loanOffers = loanApplicationDao.getLoanOffers(loanApplication.getId());
            if (loanOffers != null) {
                for (LoanOffer loanOffer : loanOffers) {
                    if (maxInstallments == null)
                        maxInstallments = loanOffer.getInstallments();
                    else
                        maxInstallments = Math.max(maxInstallments, loanOffer.getInstallments());
                    if (minInstallments == null)
                        minInstallments = loanOffer.getInstallments();
                    else
                        minInstallments = Math.max(minInstallments, loanOffer.getInstallments());
                }
            }

            UpdateLoanApplicationForm form = new UpdateLoanApplicationForm();
            ((UpdateLoanApplicationForm.Validator) form.getValidator()).installments.setMaxValue(maxInstallments);
            ((UpdateLoanApplicationForm.Validator) form.getValidator()).installments.setMinValue(minInstallments);
            ((UpdateLoanApplicationForm.Validator) form.getValidator()).amount.setMaxValue(maxAmount);
            ((UpdateLoanApplicationForm.Validator) form.getValidator()).amount.setMinValue(minAmount);

            form.setAmount(loanApplication.getAmount());
            form.setInstallments(loanApplication.getInstallments());

            model.addAttribute("loanApplication", loanApplication);
            model.addAttribute("updateLoanAppForm", form);
            return new ModelAndView("loanApplication/formQuestions/formQuestions :: modal_edit_loan_application");
        } else {
            return new ModelAndView("loanApplication/formQuestions/formQuestions :: modal_edit_vehicle");
        }
    }

    @RequestMapping(value = "/{categoryUrl:" +
            ProductCategory.VEHICULO_CATEGORY_URL + "}/" + URL + "/{token}/updateLoanApplication/vehicleResults", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getUpdateLoanApplicationVehicleResults(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("token") String token,
            @PathVariable("categoryUrl") String categoryUrl,
            @RequestParam("brands") String brands) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        List<Integer> brandsIds = new ArrayList<>();
        JSONArray jsonBrands = new JSONArray(brands);
        for (int i = 0; i < jsonBrands.length(); i++) {
            brandsIds.add(jsonBrands.getInt(i));
        }
        List<Vehicle> vehicles = catalogService.getVehicles(locale).stream().filter(v -> brandsIds.contains(v.getBrand().getId())).collect(Collectors.toList());

        model.addAttribute("results", vehicles);
        return new ModelAndView("loanApplication/formQuestions/formQuestions :: modal_edit_vehicle_results");
    }

    @RequestMapping(value = "/" + URL + "/updateLoanApplication", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object updateLoanApplication(
            ModelMap model, Locale locale, HttpServletRequest request, UpdateLoanApplicationForm form,
            @RequestParam(value = "token", required = false) String token) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        Integer maxInstallments;
        Integer minInstallments;
        Integer maxAmount;
        Integer minAmount;
        if (loanApplication.getEntityId() != null) {
            maxInstallments = productService.getMaxInstalmentsEntity(loanApplication.getProductCategoryId(), loanApplication.getCountryId(), loanApplication.getEntityId());
            minInstallments = productService.getMinInstalmentsEntity(loanApplication.getProductCategoryId(), loanApplication.getCountryId(), loanApplication.getEntityId());
            maxAmount = productService.getMaxAmountEntity(loanApplication.getProductCategoryId(), loanApplication.getCountryId(), loanApplication.getEntityId());
            minAmount = productService.getMinAmountEntity(loanApplication.getProductCategoryId(), loanApplication.getCountryId(), loanApplication.getEntityId());
        } else {
            maxInstallments = productService.getMaxInstalments(loanApplication.getProductCategoryId(), loanApplication.getCountryId());
            minInstallments = productService.getMinInstalments(loanApplication.getProductCategoryId(), loanApplication.getCountryId());
            maxAmount = productService.getMaxAmount(loanApplication.getProductCategoryId(), loanApplication.getCountryId());
            minAmount = productService.getMinAmount(loanApplication.getProductCategoryId(), loanApplication.getCountryId());
        }

        ((UpdateLoanApplicationForm.Validator) form.getValidator()).redefineMaxMin(maxAmount, minAmount, maxInstallments, minInstallments);
        form.getValidator().validate(locale);

        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        // TODO Falta validacion por status
        loanApplicationDao.updateLoanApplication(loanApplication.getId(), form.getAmount(), form.getInstallments(), null, null);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/" + URL + "/updateLoanApplicationVehicle", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object updateLoanApplicationVehicle(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam(value = "token", required = false) String token,
            @RequestParam("vehicleId") int vehicleId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        loanApplicationService.updateVehicle(loanApplication.getId(), vehicleId);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/{categoryUrl:" +
            ProductCategory.CONSUMO_CATEGORY_URL + "|" +
            ProductCategory.ADELANTO_SUELDO_CATEGORY_URL + "|" +
            ProductCategory.LEADS_CATEGORY_URL + "|" +
            ProductCategory.TARJETA_CREDITO_CATEGORY_URL + "|" +
            ProductCategory.GATEWAY_URL + "|" +
            ProductCategory.CUENTA_BANCARIA_URL + "|" +
            ProductCategory.VALIDACION_IDENTIDAD_URL + "|" +
            ProductCategory.CONSEJ0_URL + "|" +
            ProductCategory.VEHICULO_CATEGORY_URL + "}/" + URL + "/{token}/updateLoanOffer", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getUpdateLoanOffer(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("token") String token,
            @PathVariable("categoryUrl") String categoryUrl) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        if (loanApplication.getProductCategory().getId() == ProductCategory.VEHICULO) {
            List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());

            UpdateVehicleLoanOfferForm form = new UpdateVehicleLoanOfferForm();
            form.setDownPayment(loanApplication.getDownPayment() != null ? loanApplication.getDownPayment().intValue() : null);
            if (offers != null && offers.get(0).getMinAmmount() != null) {
                ((UpdateVehicleLoanOfferForm.Validator) form.getValidator()).downPayment.setMinValue(offers.get(0).getMinAmmount().intValue());
            }
            model.addAttribute("updateLoanOfferForm", form);
            return new ModelAndView("loanApplication/formQuestions/formQuestions :: modal_edit_loan_offer_vehicle");
        } else {
            List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
            List<Product> products = catalogService.getCatalog(Product.class, locale, true, p -> p.getProductCategoryId() == loanApplication.getProductCategoryId().intValue());
            Integer minInstallments = products.stream().filter(p -> p.getProductParams(loanApplication.getCountryId()) != null).collect(Collectors.toList()).stream().map(p -> p.getProductParams(loanApplication.getCountryId())).min(Comparator.comparingInt(p -> p.getMinInstallments())).get().getMinInstallments();

            UpdateLoanApplicationForm form = new UpdateLoanApplicationForm();

            ((UpdateLoanApplicationForm.Validator) form.getValidator()).amount.setMaxValue((int) offers.stream().mapToDouble(o -> o.getMaxAmmount()).max().orElse(0));
            ((UpdateLoanApplicationForm.Validator) form.getValidator()).amount.setMinValue((int) offers.stream().mapToDouble(o -> o.getMinAmmount()).min().orElse(0));
            ((UpdateLoanApplicationForm.Validator) form.getValidator()).installments.setMaxValue(offers.stream().mapToInt(o -> o.getMaxInstallments()).max().orElse(0));
            ((UpdateLoanApplicationForm.Validator) form.getValidator()).installments.setMinValue(minInstallments);

            if (!offers.isEmpty() && offers.stream().mapToInt(LoanOffer::getEntityProductParameterId).distinct().count() == 1) {
                EntityProductParams uniqueEntityProdParam = offers.get(0).getEntityProductParam();
                if (uniqueEntityProdParam.getFixedInstallments() != null && !uniqueEntityProdParam.getFixedInstallments().isEmpty()) {

                    if (EntityProductParams.ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD.contains(offers.get(0).getEntityProductParameterId())) {
                        List<Integer> fixedInstallments = new ArrayList<>();
                        uniqueEntityProdParam.getFixedInstallments().forEach(e -> fixedInstallments.add(e));

                        ((UpdateLoanApplicationForm.Validator) form.getValidator()).installments.setMaxValue(((UpdateLoanApplicationForm.Validator) form.getValidator()).installments.getMaxValue() + 2);

                        model.addAttribute("fixedInstallments", fixedInstallments);
                    } else {
                        model.addAttribute("fixedInstallments", uniqueEntityProdParam.getFixedInstallments());
                    }
                }
            }

            form.setAmount(loanApplication.getAmount());
            form.setInstallments(loanApplication.getInstallments());

            model.addAttribute("updateLoanOfferForm", form);
            model.addAttribute("currencySymbol", loanApplication.getCurrency() != null ? loanApplication.getCurrency().getSymbol() : offers.get(0).getCurrency().getSymbol());
            return new ModelAndView("loanApplication/formQuestions/formQuestions :: modal_edit_loan_offer");
        }
    }

    @RequestMapping(value = "/" + URL + "/updateLoanOffer", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object updateLoanOffer(
            ModelMap model, Locale locale, HttpServletRequest request, UpdateLoanApplicationForm form, UpdateVehicleLoanOfferForm vehicleForm,
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "firstDueDate", required = false) String firstDueDate) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
        Integer minInstallments = productService.getMinInstalmentsEntity(loanApplication.getProductCategoryId(), loanApplication.getCountryId(), offers.get(0).getEntityId());

        ((UpdateLoanApplicationForm.Validator) form.getValidator()).amount.setMaxValue((int) offers.stream().mapToDouble(o -> o.getMaxAmmount()).max().orElse(0));
        ((UpdateLoanApplicationForm.Validator) form.getValidator()).amount.setMinValue((int) offers.stream().mapToDouble(o -> o.getMinAmmount()).min().orElse(0));
        ((UpdateLoanApplicationForm.Validator) form.getValidator()).installments.setMaxValue(offers.stream().mapToInt(o -> o.getMaxInstallments()).max().orElse(0));
        ((UpdateLoanApplicationForm.Validator) form.getValidator()).installments.setMinValue(minInstallments);

        if (!offers.isEmpty() && offers.stream().mapToInt(LoanOffer::getEntityProductParameterId).distinct().count() == 1) {
            EntityProductParams uniqueEntityProdParam = offers.get(0).getEntityProductParam();
            if (uniqueEntityProdParam.getFixedInstallments() != null && !uniqueEntityProdParam.getFixedInstallments().isEmpty()) {

                if (EntityProductParams.ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD.contains(offers.get(0).getEntityProductParameterId())) {
                    ((UpdateLoanApplicationForm.Validator) form.getValidator()).installments.setMaxValue(((UpdateLoanApplicationForm.Validator) form.getValidator()).installments.getMaxValue() + 2);
                }
            }
        }

        if (loanApplication.getProductCategory().getId() == ProductCategory.VEHICULO) {
            vehicleForm.getValidator().validate(locale);
            if (vehicleForm.getValidator().isHasErrors()) {
                return AjaxResponse.errorFormValidation(vehicleForm.getValidator().getErrorsJson());
            }

            loanApplicationDao.updateDownPayment(loanApplication.getId(), vehicleForm.getDownPayment());
            return AjaxResponse.ok(null);
        } else {
            if (form != null) {
                form.getValidator().validate(locale);
                if (form.getValidator().isHasErrors()) {
                    return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
                }
            }

            // TODO Falta validacion por status
            if (firstDueDate != null) {
                loanApplicationDao.updateFirstDueDate(loanApplication.getId(), new SimpleDateFormat("dd/MM/yyyy").parse(firstDueDate));
                loanApplicationDao.createLoanOffers(loanApplication.getId());
                return AjaxResponse.ok(null);
            } else if (form != null) {
                // Get the offer and validate that the values are in the range
                offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
                LoanOffer offer = offers.get(0);
                if (form.getAmount() <= offer.getMaxAmmount() && form.getAmount() >= offer.getMinAmmount()
                    /*&& form.getInstallments() <= offer.getMaxInstallments() && form.getInstallments() >= loanApplication.getProduct().getMaxInstallments()*/) {
                    // The values are valid
                    loanApplicationDao.updateLoanApplication(loanApplication.getId(), form.getAmount(), form.getInstallments(), null, null);
                    loanApplicationDao.createLoanOffers(loanApplication.getId());
                    return AjaxResponse.ok(null);
                } else {
                    return AjaxResponse.errorMessage("Los valores son inválidos");
                }
            }
            return AjaxResponse.ok(null);
        }
    }


    // METHODS FOR PENDING SIGNATURE

    @RequestMapping(value = "/{categoryUrl:" +
            ProductCategory.CONSUMO_CATEGORY_URL + "|" +
            ProductCategory.ADELANTO_SUELDO_CATEGORY_URL + "|" +
            ProductCategory.VEHICULO_CATEGORY_URL + "}/" + URL + "/{token}/contract", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.OTHER)
    public Object getContract(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @PathVariable("token") String token,
            @PathVariable("categoryUrl") String categoryUrl) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);

        if (loanApplication.getStatus().getId() == LoanApplicationStatus.APPROVED) {
            String filename = loanApplication.getId() + "_contrato_" + personDao.getPerson(catalogService, locale, person.getId(), false).getFirstName() + ".pdf";
            byte[] pdfAsBytes = creditService.createContract(loanApplication.getCreditId(), request, response, locale, templateEngine, filename, true);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            ResponseEntity<byte[]> downloadablePdf = new ResponseEntity<byte[]>(pdfAsBytes, headers, HttpStatus.OK);
            return downloadablePdf;
        }
        return null;
    }

    @RequestMapping(value = "/" + URL + "/{token}/signature", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object saveSignature(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            LoanApplicationBankForm loanApplicationBankForm,
            @PathVariable("token") String token,
            @RequestParam(value = "loanApplicationSign", required = false) String loanApplicationSign) throws Exception {

        // Validate the form
        loanApplicationBankForm.getValidator().validate(locale);
        if (loanApplicationBankForm.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(loanApplicationBankForm.getValidator().getErrorsJson());
        }
        if (!new StringFieldValidator(ValidatorUtil.LOANAPPLICATION_SIGNATURE, loanApplicationSign).validate(locale)) {
            return AjaxResponse.errorMessage("La firma no es v&aacute;lida");
        }

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), locale, true, Credit.class);
        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);

        // Validate the signature
        if (!isSignature2Ok(person, loanApplicationSign)) {
            return AjaxResponse.errorMessage("La firma no coincide con el texto resaltado en amarillo.");
        }

        // Register the signature
        registerSignature(loanApplication, person, credit, loanApplicationBankForm, loanApplicationSign, locale, request, response);


        return AjaxResponse.redirect(request.getContextPath() + "/" +
                ProductCategory.GET_URL_BY_ID(loanApplication.getProductCategoryId()) + "/" + URL + "/" + token);
    }

    @RequestMapping(value = "/" + URL + "/{token}/paymentTypeSignature", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object savePAymentTypeSignature(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("type") String type,
            @PathVariable("token") String token,
            @RequestParam(value = "loanApplicationSign", required = false) String loanApplicationSign) throws Exception {

        // Validate the form
        if (!type.equals("A") && !type.equals("D")) {
            return AjaxResponse.errorMessage("El valor enviado no es válido");
        }
        if (!new StringFieldValidator(ValidatorUtil.LOANAPPLICATION_SIGNATURE, loanApplicationSign).validate(locale)) {
            return AjaxResponse.errorMessage("La firma no es v&aacute;lida");
        }

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), locale, true, Credit.class);
        EntityProductParams entityParams = catalogService.getEntityProductParam(credit.getEntity().getId(), credit.getProduct().getId());
        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), true);

        // Validate the signature
        if (!isSignature2Ok(person, loanApplicationSign)) {
            return AjaxResponse.errorMessage("Tu firma no coincide con tu nombre.");
        }

        // Save the payment type
        loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.APPLICATION_PAYMENT_TYPE.getKey(), type);
        loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());

        boolean bankRequired = !entityParams.getSelfDisbursement() && entityParams.getDisbursementType() == EntityProductParams.DISBURSEMENT_TYPE_DEPOSIT && credit.getEmployer() == null;
        if (bankRequired)
            return AjaxResponse.ok(null);

        LoanOffer offerSelected = loanApplicationDao.getLoanOffers(loanApplication.getId())
                .stream().filter(o -> o.getSelected()).findFirst().orElse(null);

        if (offerSelected.getProduct().getId() == ProductCategory.CONSUMO && offerSelected.getEntity().getId() == Entity.COMPARTAMOS) {
            GenerarCreditoResponse result = null;
            User user = userDAO.getUser(userDAO.getUserIdByPersonId(person.getId()));
            List<PersonOcupationalInformation> personOcupationalInformations = personDao.getPersonOcupationalInformation(locale, person.getId());
            PersonOcupationalInformation personOcupationalInformation = null;

            PersonContactInformation personContactInformation = personDao.getPersonContactInformation(locale, person.getId());
            List<DisggregatedAddress> disggregatedAddresses = personDao.getDisggregatedAddress(person.getId());
            PersonBankAccountInformation personBankAccountInformation = personDao.getPersonBankAccountInformation(locale, person.getId());

            if (personOcupationalInformations != null) {
                personOcupationalInformation = personOcupationalInformations.stream().filter(p -> p.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst().orElse(null);
            }

            SunatResult sunatResult = personDao.getSunatResult(person.getId());
            SunatResult sunatResultRuc = null;
            if (personOcupationalInformation != null && personOcupationalInformation.getRuc() != null) {
                sunatResultRuc = personDao.getSunatResultByRuc(personOcupationalInformation.getRuc());
            }
            ExperianResult experianResult = loanApplicationDao.getExperianResultList(loanApplication.getId()).get(0);
            RucInfo rucInfo = personDao.getRucInfo(personOcupationalInformation.getRuc());
            LoanOffer loanOffer = loanApplicationDao.getLoanOffers(loanApplication.getId()).stream().filter(e -> e.getSelected()).findFirst().orElse(null);

            GenerarCreditoRequest generarCreditoRequest = new GenerarCreditoRequest("01", //Código 01 - Generación de un crédito
                    person,
                    user,
                    personOcupationalInformation,
                    disggregatedAddresses,
                    loanApplication.getRegisterDate(),
                    loanApplication, loanOffer,
                    personBankAccountInformation,
                    personContactInformation,
                    sunatResult,
                    experianResult,
                    rucInfo,
                    translatorDAO,
                    null,
                    sunatResultRuc,
                    credit);

            result = compartamosServiceCall.callGeneracionCredito(generarCreditoRequest, loanApplication.getId(), credit.getId());
            Gson gson = new Gson();
            creditDao.registerSchedule(credit.getId(), gson.toJson(result));
            loanApplicationDao.updateEntityApplicationCode(loanApplication.getId(), result.getCredito().getNumeroCuenta());
        }

        // Register the signature
        registerSignature(loanApplication, person, credit, null, loanApplicationSign, locale, request, response);


        return AjaxResponse.redirect(request.getContextPath() + "/" +
                ProductCategory.GET_URL_BY_ID(loanApplication.getProductCategoryId()) + "/" + URL + "/" + token);
    }

    @RequestMapping(value = "/" + URL + "/{token}/validateSignature", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object validateSignature(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @PathVariable("token") String token,
            @RequestParam("loanApplicationSign") String loanApplicationSign) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), locale, true, Credit.class);
        EntityProductParams entityParams = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());
        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);

        // Validate the signature
        if (!isSignature2Ok(person, loanApplicationSign)) {
            return AjaxResponse.errorMessage("La firma no coincide con el texto resaltado en amarillo.");
        }

        boolean bankRequired = !entityParams.getSelfDisbursement() && entityParams.getDisbursementType() == EntityProductParams.DISBURSEMENT_TYPE_DEPOSIT && credit.getEmployer() == null;
        boolean paymentApplicationTypeRequired = credit.getEntity().getId() == Entity.COMPARTAMOS;
        if (bankRequired)
            return AjaxResponse.ok(null);
        if (paymentApplicationTypeRequired)
            return AjaxResponse.ok(null);

        // Register the signature
        registerSignature(loanApplication, person, credit, null, loanApplicationSign, locale, request, response);


        return AjaxResponse.redirect(request.getContextPath() + "/" +
                ProductCategory.GET_URL_BY_ID(loanApplication.getProductCategoryId()) + "/" + URL + "/" + token);
    }

    @RequestMapping(value = "/" + URL + "/{token}/disbursementStore", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object saveDisbursementStore(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            LoanApplicationBankForm loanApplicationBankForm,
            @PathVariable("token") String token,
            @RequestParam("storeId") int storeId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        // TODO Save the store in the DB
        return AjaxResponse.redirect(request.getContextPath() + "/" +
                ProductCategory.GET_URL_BY_ID(loanApplication.getProductCategoryId()) + "/" + URL + "/" + token);
    }

    private void registerSignature(LoanApplication loanApplication, Person person, Credit credit, LoanApplicationBankForm loanApplicationBankForm, String loanApplicationSign, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Get the LoanOffer
        LoanOffer offerSelected = loanApplicationDao.getLoanOffers(loanApplication.getId()).stream().filter(o -> o.getSelected()).findFirst().orElse(null);

        // Call abaco if the person is assciated, else wait till RRHH check the associated check
        if (credit.getProduct().getId() == Product.AGREEMENT && credit.getEntity().getId() == Entity.ABACO) {
            PersonAssociated associated = personDao.getAssociated(loanApplication.getPersonId(), credit.getEntity().getId());
            if (associated.getValidated() && credit.getEntityCreditCode() == null) {
                agreementService.createAssociatedCredit(person, credit);
            }
        } else if (credit.getProduct().getId() == Product.AGREEMENT && credit.getEntity().getId() == Entity.EFL) {
            PersonAssociated associated = personDao.getAssociated(loanApplication.getPersonId(), credit.getEntity().getId());
            if (associated == null)
                personDao.registerAssociated(person.getId(), credit.getEntity().getId(), null, null);
        } else if (credit.getProduct().getId() == Product.DEBT_CONSOLIDATION) {
            PersonAssociated associated = personDao.getAssociated(loanApplication.getPersonId(), credit.getEntity().getId());
            if (associated == null)
                personDao.registerAssociated(person.getId(), credit.getEntity().getId(), null, null);
        }

        // Register bank account information
        if (loanApplicationBankForm != null) {
            personDao.updatePersonBankAccountInformation(
                    loanApplication.getPersonId(),
                    loanApplicationBankForm.getBankId(),
                    loanApplicationBankForm.getBankAccountType(),
                    loanApplicationBankForm.getBankAccountNumber(),
                    loanApplicationBankForm.getBankAccountDepartment() != null ? loanApplicationBankForm.getBankAccountDepartment() + "0000" : null,
                    null);
        }

        // Save the Contrato Solicitud
        if (credit.getEntity().getId() == Entity.MULTIFINANZAS) {
            creditService.createAndSaveContractApplication(loanApplication, offerSelected, request, response, locale, templateEngine);
        }

        // Register the signature
        loanApplicationDao.registerLoanApplicationSIgnature(
                offerSelected.getId(),
                loanApplicationSign != null ? loanApplicationSign : null,
                person.getDocumentType().getId(),
                person.getDocumentNumber());

        // Send the signature mail to the client
        if (credit.getProduct().getId() == Product.AUTOS)
            loanApplicationService.sendWaitingForDownPaymentInteraction(loanApplication.getUserId(), loanApplication.getId(), locale);
        else
            loanApplicationService.sendPostSignatureInteractions(loanApplication.getUserId(), loanApplication.getId(), null, null, templateEngine, locale);
    }

    private boolean isSignatureOk(Person person, String signature) {

        // Validate the signature
        List<String> names = Arrays.asList(signature.toLowerCase().split(" "));
        List<String> persoNames = Arrays.asList(person.getName().toLowerCase().split(" "));

        boolean containsNames = false;
        boolean containsSurnames = false;

        for (String name : names) {
            if (persoNames.contains(name)) {
                containsNames = true;
            } else if (person.getFirstSurname().toLowerCase().equals(name) || person.getLastSurname().toLowerCase().equals(name)) {
                containsSurnames = true;
            } else {
                containsNames = false;
                break;
            }
        }
        if (!containsSurnames || !containsNames) {

            return false;
        }

        return true;
    }

    private boolean isSignature2Ok(Person person, String signature) {
        String name = person.getName().substring(0, 1).concat(". ").concat(person.getFirstSurname());
        if (signature.equals(name)) return true;
        return false;
    }

    @RequestMapping(value = "/" + URL + "/registerAppointmentSchedule", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerAppointmentSchedule(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("token") String token,
            @RequestParam("appointmentScheduleId") Integer appointmentScheduleId,
            @RequestParam("dateSelected") String dateSelected) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);

        DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sourceFormat.parse(dateSelected);

        loanApplicationService.sendLoanApplicationConfirmScheduleMail(loanApplication.getId(), loanApplication.getPersonId(), date, appointmentScheduleId, locale);


        String message = loanApplicationDao.registerAppointmentSchedule(loanApplication.getId(), date, appointmentScheduleId);
        // TODO Save the store in the DB
        if (message.equals("schedule.ocupied")) {
            return AjaxResponse.errorMessage("El horario elegido se encuentra ocupado, por favor elige uno nuevo");
        }
        return AjaxResponse.redirect(request.getContextPath() + "/" +
                ProductCategory.GET_URL_BY_ID(loanApplication.getProductCategoryId()) + "/" + URL + "/" + token);
    }

    @RequestMapping(value = "/" + URL +  "/downloadBancoDelSolContract", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity validateCanDownloadBancoDelSolDOcumentation(
            Locale locale,
            @RequestParam("token") String token) throws Exception {
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);

        try {
            validateFirstDueDateInScheduleEnablesDates(loanApplication);
        }
        catch (BDSCampaignValidationException e) {
            if(loanApplication.getCreditId() != null) creditDao.returnBDSCreditToLoanApplication(loanApplication.getCreditId());
            loanApplicationService.reevaluateLoanApplications(loanApplication.getId());
            return AjaxResponse.errorMessageWithCustomMessage(e.getMessage(), "recharge");
        }
        catch (Exception e) {
            return AjaxResponse.errorMessage(e.getMessage());
        }

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/" + URL +  "/validateBanboDelSolBase", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity validateBancoDelSolBaseNewInformation(
            Locale locale,
            @RequestParam("token") String token) throws Exception {
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);

        try {
            validateBancoDelSolBase(loanApplication);
        } catch (Exception e) {
            return AjaxResponse.errorMessage(e.getMessage());
        }

        return AjaxResponse.ok(null);
    }

    private void validateBancoDelSolBase(LoanApplication loanApplication) throws Exception {
        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
        if (credit == null) {
            throw new Exception("RELOAD");
        } else if (CreditStatus.REJECTED == credit.getStatus().getId()) {
            throw new Exception("REJECT:Lamentamos informarte que tu solicitud se encuentra rechazada ya que tu condición crediticia cambió.");
        }

        Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
        List<BDSBase> bdsBasesInDB = rccDao.getBancoDelSolBase(person.getDocumentNumber());
        BDSBase bdsBaseInDB = bdsBasesInDB != null && !bdsBasesInDB.isEmpty() ? bdsBasesInDB.get(0) : null;

        // Validate negative base HardFilter.BASE_NEGATIVA_BDS
        if (bdsBaseInDB != null) {
            if ((bdsBaseInDB.getInhabcc() != null && bdsBaseInDB.getInhabcc() == 1)
                    || (bdsBaseInDB.getEntliq() != null && bdsBaseInDB.getEntliq() == 1)
                    || (bdsBaseInDB.getMora() != null && bdsBaseInDB.getMora() == 1)) {
                creditDao.registerRejection(loanApplication.getCreditId(), CreditRejectionReason.BLACK_LIST);
                throw new Exception("REJECT:Lamentamos informarte que tu solicitud se encuentra rechazada ya que tu condición crediticia cambió.");
            }
        }

        BDSBase bdsBaseInLoan = getBdsBaseData(loanApplication.getEntityCustomData());

        boolean changedInfo = false;
        if (bdsBaseInDB != null && bdsBaseInLoan != null) {
            if ( !Objects.equals(bdsBaseInDB.getVcapitales(), bdsBaseInLoan.getVcapitales()) ||
                    !Objects.equals(bdsBaseInDB.getVcuotas(), bdsBaseInLoan.getVcuotas()) ) {
                changedInfo = true;
            }
        } else if ( bdsBaseInDB == null && bdsBaseInLoan != null ) {
            changedInfo = true;
        } else if (bdsBaseInDB != null) {
            changedInfo = true;
        }

        if (changedInfo) {
            creditDao.returnCreditToLoanApplication(loanApplication.getCreditId());
            loanApplicationService.reevaluateLoanApplications(loanApplication.getId());

            throw new Exception("REPROCESS:Lamentamos informarte que tu solicitud se debe reprocesar ya que tu condición crediticia cambió.");
        }
    }

    private BDSBase getBdsBaseData(JSONObject entityCustomData) {
        if (entityCustomData != null) {
            JSONObject jsonBdsData = JsonUtil.getJsonObjectFromJson(entityCustomData, LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_BASE_DATA.getKey(), null);
            if (jsonBdsData != null) {
                return new Gson().fromJson(jsonBdsData.toString(), BDSBase.class);
            }
        }
        return null;
    }

    @RequestMapping(value = "/" + URL +  "/{loanApplicationToken}/downloadBancoDelSolContract", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object downloadBancoDelSolDOcumentation(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @PathVariable("loanApplicationToken") String token) throws Exception {

        String filename = "solicitud-de-credito.pdf";
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);

        byte[] contractBytes = creditService.createContract(loanApplication.getCreditId(), request, response, locale, templateEngine, filename, true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
        credit.getEntityCustomData().put(Credit.EntityCustomDataKeys.BANCO_DEL_SOL_CONTRACT_DOWNLOAD_DATE.getKey(), new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        creditDao.updateEntityCustomData(credit.getId(), credit.getEntityCustomData());

        return new ResponseEntity<>(contractBytes, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/" + URL + "/canUploadBancoDelSolContract", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object canUploadBancoDelSolDocumentation(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("token") String token) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);

        if (credit.getEntityCustomData() != null && credit.getEntityCustomData().has(Credit.EntityCustomDataKeys.BANCO_DEL_SOL_CONTRACT_DOWNLOAD_DATE.getKey())) {
            Date contractDownloadDate = new SimpleDateFormat("dd/MM/yyyy").parse(credit.getEntityCustomData().getString(Credit.EntityCustomDataKeys.BANCO_DEL_SOL_CONTRACT_DOWNLOAD_DATE.getKey()));

            if (DateUtils.truncate(new Date(), Calendar.DATE).compareTo(DateUtils.truncate(contractDownloadDate, Calendar.DATE)) != 0) {
                return AjaxResponse.errorMessage("La fecha de subida no puede ser distinta a la fecha de descarga. Por favor descargar un nuevo contrato.");
            }
        } else {
            return AjaxResponse.errorMessage("No se ha registrado la fecha de descarga del contrato. Por favor, descargar de nuevo el contrato.");
        }

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/" + URL + "/uploadBancoDelSolContract", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object uploadBancoDelSolDocumentation(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("token") String token,
            @RequestParam("file") MultipartFile[] file,
            @RequestParam("base64") String[] base64
            ) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        List<UserFile> loanFiles = loanApplicationDao.getLoanApplicationUserFiles(loanApplication.getId());

        if (loanFiles != null && loanFiles.stream().filter(l -> l.getFileType().getId().equals(UserFileType.CONTRATO_FIRMA)).count() >= 10) {
            return AjaxResponse.errorMessage("Se llegó al limite de archivos permitidos");
        }
        personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
        userService.registerUserFilesBase64(base64, loanApplication.getId(), loanApplication.getUserId(), UserFileType.CONTRATO_FIRMA, loanApplication);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/" + URL + "/returnOfferSchedule", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object returnOfferSchedule(
            Locale locale,
            @RequestParam("token") String token) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);

        try {
            validateFirstDueDateInScheduleEnablesDates(loanApplication);
        } catch (Exception e) {
            creditDao.returnBDSCreditToLoanApplication(loanApplication.getCreditId());
        }

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/" + URL + "/filesUploadedAndRedirect", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object filesUploadedAndRedirectToDashBoardBancoDelSol(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("token") String token) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
        bancoDelSolService.uploadCSVdocsToFTP(loanApplication.getCreditId(), loanApplication.getPersonId(), person.getDocumentNumber(), loanApplication.getId());

        // Update status to originated
        creditDao.updateCreditStatus(loanApplication.getCreditId(), CreditStatus.ORIGINATED, null);
        creditDao.updateCreditSubStatus(loanApplication.getCreditId(), CreditSubStatus.PENDING_ENTIY_DISBURSEMENT);
        return AjaxResponse.redirect("/funcionarios/beingProcessed");
    }

//    BDS ONLY
    private void validateFirstDueDateInScheduleEnablesDates(LoanApplication loanApplication) throws Exception {
        LoanOffer selectedOffer = loanApplicationDao.getLoanOffersAll(loanApplication.getId()).stream().filter(LoanOffer::getSelected).findFirst().orElse(null);

        if (selectedOffer == null) {
            throw new Exception("La solicitud no ha seleccionado una oferta");
        }

        List<Date> enableDates = question50Service.getScheduleEnablesDates(selectedOffer.getEntityProductParam());
        try {
            if (enableDates.stream().map(d -> DateUtils.truncate(d, Calendar.DATE)).noneMatch(d -> DateUtils.truncate(loanApplication.getFirstDueDate(), Calendar.DATE).compareTo(d) == 0)) {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception("La fecha de vencimiento no cumple con la politica del banco. Por favor, volver a seleccionar la oferta con las condiciones de préstamo actualizadas");
        }

        if(loanApplication.getEntityCustomData().has(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_CAMPAIGN_ID.getKey())){
            CampaniaBds campaign = rccDao.getCampaniaBdsById(loanApplication.getEntityCustomData().getInt(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_CAMPAIGN_ID.getKey()));
            if(!campaign.isVigente()){
                throw new BDSCampaignValidationException("La campaña de esta solicitud ya no se encuentra vigente");
            }
            else{
                CampaniaBds lastCampaign = rccDao.getLastCampaniaBds(campaign.getCuit());
                if(lastCampaign != null && !CampaniaBds.hasSameValues(campaign, lastCampaign)){
                    throw new BDSCampaignValidationException("La campaña ha sido actualizada. Por favor, volver a seleccionar la oferta con las nuevas condiciones de préstamo.");
                }
            }
        }
    }

    @RequestMapping(value = "/" + URL + "/percentage", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getEvaluationPercentage(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("token") String token) throws Exception {

        Map<String, Object> data = new HashMap<String, Object>();
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        if(loanApplication != null){
            Integer originalCurrentQuestion = loanApplication.getCurrentQuestionId();
            Integer originalCurrentQuestionCategory = originalCurrentQuestion != null ? catalogService.getProcessQuestion(originalCurrentQuestion).getCategory().getId() : null;

            int currentQuestionCategoryId = catalogService.getProcessQuestion(loanApplication.getCurrentQuestionId()).getCategory().getId();
            double previousPercentage = loanApplication.getPercentage();
            if(originalCurrentQuestionCategory == null || originalCurrentQuestionCategory != currentQuestionCategoryId)
                previousPercentage = 0.0;

            double percentage = questionPercentageService.getCurrentCategoryPercentage(loanApplication.getCurrentQuestionId(), loanApplication.getCountryId(), loanApplication.getProductCategoryId(), previousPercentage, loanApplication.getId());

            data.put("percentage",percentage);
            loanApplicationDao.updatePercentageProgress(loanApplication.getId(), Math.min(percentage, 100.0));
        } else data.put("percentage",0);

        return AjaxResponse.ok(new Gson().toJson(data));
    }

    @RequestMapping(value = "/" + URL + "/gaclientid", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object saveGaClientId(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("token") String token, @RequestParam(value = "clientId", required = false) String clientId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), locale);
        if(loanApplication.getAuxData() == null)
            loanApplication.setAuxData(new LoanApplicationAuxData());
        loanApplication.getAuxData().setGaClientId(clientId);
        loanApplicationDao.updateAuxData(loanApplication.getId(),loanApplication.getAuxData());

        return AjaxResponse.ok(null);
    }

}
