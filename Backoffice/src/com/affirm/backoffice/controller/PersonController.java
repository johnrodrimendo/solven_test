package com.affirm.backoffice.controller;

import com.affirm.acceso.AccesoServiceCall;
import com.affirm.acceso.model.Direccion;
import com.affirm.acceso.model.Expediente;
import com.affirm.aws.RecognitionResultsPainter;
import com.affirm.aws.elasticSearch.AWSElasticSearchClient;
import com.affirm.backoffice.dao.PersonBODAO;
import com.affirm.backoffice.model.CreditBoPainter;
import com.affirm.backoffice.model.LineaResultBoPainter;
import com.affirm.backoffice.model.LoanApplicationBoPainter;
import com.affirm.backoffice.model.PersonInteractionPainter;
import com.affirm.backoffice.model.form.UpdatePersonAddressForm;
import com.affirm.backoffice.service.ApplicationPainterService;
import com.affirm.backoffice.service.BackofficeService;
import com.affirm.backoffice.service.EmailBoService;
import com.affirm.backoffice.service.SysUserService;
import com.affirm.backoffice.strategies.bureau.BureauContext;
import com.affirm.common.dao.*;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.Currency;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.Question28Form;
import com.affirm.common.model.form.Question53Form;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.service.question.QuestionFlowService;
import com.affirm.common.util.*;
import com.affirm.nosis.NosisResult;
import com.affirm.nosis.ParteXML;
import com.affirm.security.model.SysUser;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */
@Controller
@Scope("request")
public class PersonController {

    private static Logger logger = Logger.getLogger(PersonController.class);
    @Autowired
    AWSElasticSearchClient awsElasticSearchClient;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private PersonBODAO personBoDao;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private PersonService personService;
    @Autowired
    private InteractionDAO interactionDao;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private WebscrapperService webscrapperService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private EmailBoService emailBoService;
    @Autowired
    private UserService userService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ApplicationPainterService applicationPainterService;
    @Autowired
    private TranslatorDAO translatorDao;
    @Autowired
    private AccesoServiceCall accesoServiceCall;
    @Autowired
    private BotDAO botDao;
    @Autowired
    CreditDAO creditDAO;
    @Autowired
    private AwsSesEmailService awsSesEmailService;
    @Autowired
    private BackofficeService backofficeService;

    /**
     * @param model
     * @param locale
     * @param personId
     * @param tab      Shows the tab as active in the first load. Possible values:<br>
     *                 credits <br>
     *                 applications <br>
     *                 data <br>
     *                 socialNetworks <br>
     *                 changelog <br>
     *                 documentation <br>
     *                 bankAccount <br>
     *                 cancellation <br>
     *                 refinancement <br>
     * @return
     */
    @RequestMapping(value = "/person", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "person:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showPerson(
            ModelMap model, Locale locale,
            @RequestParam("personId") Integer personId,
            @RequestParam(value = "applicationCode", required = false) String applicationCode,
            @RequestParam(value = "applicationId", required = false) Integer applicationId,
            @RequestParam(value = "creditCode", required = false) String creditCode,
            @RequestParam(value = "creditId", required = false) String creditId,
            @RequestParam(value = "tab", required = false) String tab) throws Exception {

        Long startTime = new Date().getTime();

        //Person
        Person person = personDao.getPerson(catalogService, locale, personId, true);
        locale = LocaleUtils.toLocale(catalogService.getCountryParam(person.getDocumentType().getCountryId()).getLocale());

        model.addAttribute("person", person);
        model.addAttribute("applicationCode", applicationCode);
        model.addAttribute("applicationId", applicationId);
        model.addAttribute("creditCode", creditCode);
        model.addAttribute("creditId", creditId);
        model.addAttribute("personContactInfo", personDao.getPersonContactInformation(locale, personId));
        if (person.getPartner() != null) {
            person.getPartner().setNegativeBase(personDao.getPersonNegativeBase(person.getPartner().getId()));
            person.getPartner().setHasDebt(personDao.getPersonHasDebt(person.getPartner().getId()));
        }

        List<Referral> referrals = personDao.getReferrals(personId, locale);
        if (referrals != null) {
            model.addAttribute("personReferrals", referrals.stream().filter(r -> r.getValidated()).collect(Collectors.toList()));
        }
        model.addAttribute("facebook", userDao.getUserFacebook(person.getUserId()));
        model.addAttribute("linkedin", userDao.getLinkedin(personId));
        model.addAttribute("lastSessionLog", userDao.getLastSessionLog(person.getUserId()));
        model.addAttribute("employees", personDao.getEmployeesByDocument(person.getDocumentType().getId(), person.getDocumentNumber(), locale));

        List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(locale, personId);
        if (ocupations != null) {
            for (PersonOcupationalInformation o : ocupations) {
                if (o.getNumber() == PersonOcupationalInformation.PRINCIPAL) {
                    model.addAttribute("personPrincipalOcupationalInfo", o);
                } else if (o.getNumber() == PersonOcupationalInformation.SECUNDARY) {
                    model.addAttribute("personSecundaryOcupationalInfo", o);
                } else if (o.getNumber() == PersonOcupationalInformation.TERTIARY) {
                    model.addAttribute("personTertiaryOcupationalInfo", o);
                } else if (o.getNumber() == PersonOcupationalInformation.QUATERNARY) {
                    model.addAttribute("personQuaternaryOcupationalInfo", o);
                } else if (o.getNumber() == PersonOcupationalInformation.OTHER) {
                    model.addAttribute("personOtherOcupationalInfo", o);
                }
            }
        }

        Employee employee = personDao.getEmployeeByPerson(personId, locale);
        if (employee != null && employee.getFixedGrossIncome() != null)
            model.addAttribute("employee", employee);
        else {
            Double fixedGrossIncomeESSalud = personDao.getExternalGrossIncome(personId);
            model.addAttribute("fixedGrossIncomeESSalud", fixedGrossIncomeESSalud);
        }

        model.addAttribute("tab", tab);

        // Add the person alerts
//        List<ExternalUserInfoState> externalsInfoStates = userDao.getUserExternalInfoState(person.getUserId());
//        ExternalUserInfoState sunatInfoState = externalsInfoStates.stream().filter(e -> e.getBotId() == Bot.SUNAT_BOT).findFirst().orElse(null);
//        ExternalUserInfoState claroInfoState = externalsInfoStates.stream().filter(e -> e.getBotId() == Bot.CLARO).findFirst().orElse(null);
//        ExternalUserInfoState movistarInfoState = externalsInfoStates.stream().filter(e -> e.getBotId() == Bot.MOVISTAR).findFirst().orElse(null);
//        ExternalUserInfoState bitelInfoState = externalsInfoStates.stream().filter(e -> e.getBotId() == Bot.BITEL).findFirst().orElse(null);
//        ExternalUserInfoState entelInfoState = externalsInfoStates.stream().filter(e -> e.getBotId() == Bot.ENTEL).findFirst().orElse(null);
//        ExternalUserInfoState migracionesInfoState = externalsInfoStates.stream().filter(e -> e.getBotId() == Bot.MIGRACIONES).findFirst().orElse(null);
//        ExternalUserInfoState afipInfoState = externalsInfoStates.stream().filter(e -> e.getBotId() == Bot.AFIP).findFirst().orElse(null);
//        ExternalUserInfoState bcraInfoState = externalsInfoStates.stream().filter(e -> e.getBotId() == Bot.BCRA).findFirst().orElse(null);
//        ExternalUserInfoState ansesInfoState = externalsInfoStates.stream().filter(e -> e.getBotId() == Bot.ANSES).findFirst().orElse(null);

        String errorMsgTemplate = "El bot <b>?</b> ha fallado";
        StringBuffer personErrorMsgBot = new StringBuffer();

        if (!botDao.botsRunning(person.getUserId())) {
            switch (person.getCountry().getId()) {
//                case CountryParam.COUNTRY_PERU:
//                    if (sunatInfoState != null && !sunatInfoState.getResult() && sunatInfoState.getStatusId() != null)
//                        personErrorMsgBot.append((!personErrorMsgBot.toString().equals("") ? "<br/>" : "") + errorMsgTemplate.replace("?", "Sunat"));
//                    if (claroInfoState != null && !claroInfoState.getResult() && claroInfoState.getStatusId() != null)
//                        personErrorMsgBot.append((!personErrorMsgBot.toString().equals("") ? "<br/>" : "") + errorMsgTemplate.replace("?", "Claro"));
//                    if (movistarInfoState != null && !movistarInfoState.getResult() && movistarInfoState.getStatusId() != null)
//                        personErrorMsgBot.append((!personErrorMsgBot.toString().equals("") ? "<br/>" : "") + errorMsgTemplate.replace("?", "Movistar"));
//                    if (bitelInfoState != null && !bitelInfoState.getResult() && bitelInfoState.getStatusId() != null)
//                        personErrorMsgBot.append((!personErrorMsgBot.toString().equals("") ? "<br/>" : "") + errorMsgTemplate.replace("?", "Bitel"));
//                    if (entelInfoState != null && !entelInfoState.getResult() && entelInfoState.getStatusId() != null)
//                        personErrorMsgBot.append((!personErrorMsgBot.toString().equals("") ? "<br/>" : "") + errorMsgTemplate.replace("?", "Entel"));
//                    if (person.getDocumentType().getId() == IdentityDocumentType.CE && migracionesInfoState != null && !migracionesInfoState.getResult() && migracionesInfoState.getStatusId() != null)
//                        personErrorMsgBot.append((!personErrorMsgBot.toString().equals("") ? "<br/>" : "") + errorMsgTemplate.replace("?", "Migraciones"));
//                    break;
                case CountryParam.COUNTRY_ARGENTINA:
                    NosisResult nosisResult = personDao.getBureauResult(personId, NosisResult.class);
                    ParteXML nosisXml = (nosisResult != null) ? nosisResult.getParteXML() : null;
                    AfipResult afipResult = (AfipResult) personDao.getAfipResult(personId);
//                    if (bcraInfoState != null && !bcraInfoState.getResult() && bcraInfoState.getStatusId() != null) {
//                        personErrorMsgBot.append((!personErrorMsgBot.toString().equals("") ? "<br/>" : "") + errorMsgTemplate.replace("?", "BCRA"));
//                    }
//                    if (ansesInfoState != null && !ansesInfoState.getResult() && ansesInfoState.getStatusId() != null) {
//                        personErrorMsgBot.append((!personErrorMsgBot.toString().equals("") ? "<br/>" : "") + errorMsgTemplate.replace("?", "ANSES"));
//                    }
//                    if (afipInfoState != null && !afipInfoState.getResult() && afipInfoState.getStatusId() != null) {
//                        personErrorMsgBot.append((!personErrorMsgBot.toString().equals("") ? "<br/>" : "") + errorMsgTemplate.replace("?", "AFIP"));
//                    }

                    List<PersonOcupationalInformation> ocupationsPerson = personDao.getPersonOcupationalInformation(locale, personId);
                    double fixedGrossIncome = 0;
                    if (ocupationsPerson != null) {
                        for (PersonOcupationalInformation o : ocupationsPerson) {
                            String validacion = "";
                            if (o.getNumber() == PersonOcupationalInformation.OTHER) {
                                fixedGrossIncome = fixedGrossIncome + o.getFixedGrossIncome() + (o.getVariableGrossIncome() != null ? o.getVariableGrossIncome() : 0);
                                continue;
                            }
                            if (o.getActivityType().getId() == ActivityType.MONOTRIBUTISTA) {
                                validacion = validateWithAfip(o, afipResult, locale);
                                model.addAttribute("validacionMonotributista", validacion);
                            }
                            if (o.getActivityType().getId() == ActivityType.INDEPENDENT) {
                                validacion = validateWithAfip(o, afipResult, locale);
                                model.addAttribute("validacionAutonomo", validacion);
                            }
                            if (o.getActivityType().getId() == ActivityType.PENSIONER) {
                                AnsesResult ansesResult = (AnsesResult) personDao.getAnsesResult(personId);
                                validacion = (ansesResult != null && ansesResult.isPensioner() ? "SI" : "NO");
                                model.addAttribute("validacionPensionist", validacion);
                            }
                            if (o.getActivityType().getId() == ActivityType.DEPENDENT) {
                                AnsesResult ansesResult = (AnsesResult) personDao.getAnsesResult(personId);
                                validacion = (ansesResult != null && ansesResult.isDependent() ? "SI" : "NO");
                                model.addAttribute("validacionDependent", validacion);
                            }
                            fixedGrossIncome = fixedGrossIncome + o.getFixedGrossIncome() + (o.getVariableGrossIncome() != null ? o.getVariableGrossIncome() : 0);
                        }
                    }
                    model.addAttribute("nosisXml", nosisXml);
                    model.addAttribute("afip", afipResult);
                    model.addAttribute("fixedGrossIncome", fixedGrossIncome);
                    break;
            }

        }

        if (!personErrorMsgBot.toString().equals(""))
            model.addAttribute("personErrorMsgBot", personErrorMsgBot);

        String avatar = ((SysUser) SecurityUtils.getSubject().getPrincipal()).getAvatar();
        System.out.println("Enviando avatar en controller: " + avatar);

        model.addAttribute("avatar", avatar);
        model.addAttribute("onlyView", false);
        model.addAttribute("personCountry", person.getCountry().getId());

        PersonOcupationalInformation.Regime[] regimesArray = PersonOcupationalInformation.Regime.values();

        List<Map<String, String>> regimes = new ArrayList<>();
        for (int i = 0; i < regimesArray.length; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("code", regimesArray[i].getCode());
            map.put("description", regimesArray[i].getDescription());
            regimes.add(map);
        }
        model.addAttribute("regimes", regimes);

        System.out.println("Finish loadin person.html: " + (new Date().getTime() - startTime));

        return "person";
    }

    @RequestMapping(value = "/person/panel/{panel}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "person:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showPersonPartnerPanel(
            ModelMap model, Locale locale,
            @PathVariable("panel") String panel,
            @RequestParam("personId") Integer personId) throws Exception {

        Person person = personDao.getPerson(catalogService, locale, personId, true);
        locale = LocaleUtils.toLocale(catalogService.getCountryParam(person.getDocumentType().getCountryId()).getLocale());

        model.addAttribute("onlyView", false);
        switch (panel) {
            case "partner":
                model.addAttribute("person", personDao.getPerson(catalogService, locale, personId, true));
                return "person :: #personPartnerPanel";
            case "contact":
                model.addAttribute("person", personDao.getPerson(catalogService, locale, personId, true));
                model.addAttribute("personContactInfo", personDao.getPersonContactInformation(locale, personId));

                List<Referral> referrals = personDao.getReferrals(personId, locale);
                if (referrals != null) {
                    model.addAttribute("personReferrals", referrals.stream().filter(r -> r.getValidated()).collect(Collectors.toList()));
                }
                model.addAttribute("onlyView", false);
                return new ModelAndView("person :: #personContactPanel");
            case "income":
                List<PersonOcupationalInformation> ocupationsPerson = personDao.getPersonOcupationalInformation(locale, personId);
                AfipResult afipResult = (AfipResult) personDao.getAfipResult(personId);
                for (PersonOcupationalInformation o : ocupationsPerson) {
                    String validacion = "";

                    if (o.getNumber() == PersonOcupationalInformation.PRINCIPAL) {
                        model.addAttribute("personPrincipalOcupationalInfo", o);
                    } else if (o.getNumber() == PersonOcupationalInformation.SECUNDARY) {
                        model.addAttribute("personSecundaryOcupationalInfo", o);
                    } else if (o.getNumber() == PersonOcupationalInformation.TERTIARY) {
                        model.addAttribute("personTertiaryOcupationalInfo", o);
                    } else if (o.getNumber() == PersonOcupationalInformation.QUATERNARY) {
                        model.addAttribute("personQuaternaryOcupationalInfo", o);
                    } else if (o.getNumber() == PersonOcupationalInformation.OTHER) {
                        model.addAttribute("personOtherOcupationalInfo", o);
                    }

                    if (person.getCountry().getId() == CountryParam.COUNTRY_ARGENTINA) {
                        if (o.getActivityType().getId() == ActivityType.MONOTRIBUTISTA) {
                            validacion = validateWithAfip(o, afipResult, locale);
                            model.addAttribute("validacionMonotributista", validacion);
                        } else if (o.getActivityType().getId() == ActivityType.INDEPENDENT) {
                            validacion = validateWithAfip(o, afipResult, locale);
                            model.addAttribute("validacionAutonomo", validacion);
                        } else if (o.getActivityType().getId() == ActivityType.PENSIONER) {
                            AnsesResult ansesResult = (AnsesResult) personDao.getAnsesResult(personId);
                            validacion = (ansesResult.isPensioner() ? "SI" : "NO");
                            model.addAttribute("validacionPensionist", validacion);
                        } else if (o.getActivityType().getId() == ActivityType.DEPENDENT) {
                            AnsesResult ansesResult = (AnsesResult) personDao.getAnsesResult(personId);
                            validacion = (ansesResult.isDependent() ? "SI" : "NO");
                            model.addAttribute("validacionDependent", validacion);
                        }
                    }
                }
                model.addAttribute("person", person);
                model.addAttribute("personCountry", person.getCountry().getId());
                return "person :: #personIncomePanel";
        }
        return null;
    }

    @RequestMapping(value = "/person/{tab}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "person:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showPersonTab(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("personId") Integer personId,
            @RequestParam("userId") Integer userId,
            @RequestParam(value = "applicationId", required = false) Integer applicationId,
            @RequestParam(value = "creditId", required = false) Integer creditId,
            @PathVariable("tab") String tab) throws Exception {

        long startTime = new Date().getTime();

        Person person = personDao.getPerson(catalogService, locale, personId, true);
        locale = LocaleUtils.toLocale(catalogService.getCountryParam(person.getDocumentType().getCountryId()).getLocale());

        model.addAttribute("tab", tab);

        Optional<PersonOcupationalInformation> principalOcupation = Optional.empty();
        List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(locale, personId);
        if (ocupations != null) {
            principalOcupation = ocupations.stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst();
            principalOcupation.ifPresent(x -> model.addAttribute("personPrincipalOcupationalInfo", x));
        }

        model.addAttribute("person", person);
        model.addAttribute("userId", userId);
        model.addAttribute("personCountry", person.getCountry().getId());

        Currency currency = catalogService.getCurrency(Currency.PEN);
        model.addAttribute("currency", currency);
        model.addAttribute("countryCode", userDao.getUser(userId).getCountryCode());

        String fragmentToRender = null;

        switch (tab) {
            case "credits": {
                if (!SecurityUtils.getSubject().isPermitted("person:creditTab:view")) {
                    return "403";
                }

                if (creditId != null) {
//                    CASO: CARGAR UN SOLO CREDIT
//                    SE DEVUELVE UN REGISTRO DE TIPO CreditBoPainter
                    CreditBoPainter creditBoPainter = creditDao.getCreditBO(creditId, locale, CreditBoPainter.class);

                    if (creditBoPainter != null) {
                        creditBoPainterSetter(creditBoPainter, true, person, locale);
                        creditBoPainter.setLoanApplication(applicationPainterService.getApplicationById(creditBoPainter.getLoanApplicationId(), locale, request));

                        List<CreditBoPainter> creditBoPainterList = new ArrayList<>();
                        creditBoPainterList.add(creditBoPainter);

                        model.addAttribute("credits", creditBoPainterList);
                    }

                } else {
//                    CASO: CARGAR LISTADO DE CREDITS
//                    SE DEVUELVE UN LISTADO DE TIPO Credit
                    List<Credit> creditList = creditDao.getCreditsByPerson(personId, locale, Credit.class);

                    if (creditList != null && creditList.size() == 1) {
//                    CUANDO EL LISTADO SOLO TIENE UN ITEM. DEVUELVO ESTA SOLICITUD YA CARGADA
                        Credit creditFull = creditList.get(0);
                        CreditBoPainter creditBoPainter = creditDao.getCreditBO(creditFull.getId(), locale, CreditBoPainter.class);
//                        C/P
                        if (creditBoPainter != null) {
                            creditBoPainterSetter(creditBoPainter, true, person, locale);
                            creditBoPainter.setLoanApplication(applicationPainterService.getApplicationById(creditBoPainter.getLoanApplicationId(), locale, request));

                            List<CreditBoPainter> creditBoPainterList = new ArrayList<>();
                            creditBoPainterList.add(creditBoPainter);

                            model.addAttribute("credits", creditBoPainterList);
                        }

                    } else {
//                        MAS DE UN ITEM DEVOLVERA LAS CABECERAS DE LA's ABRIENDO EL PRIMERO POR JS
                        model.addAttribute("credits", creditList);
                    }
                }

                fragmentToRender = "fragments/personFragments :: tab-credits";

                break;
            }
            case "applications": {
                if (!SecurityUtils.getSubject().isPermitted("person:loanApplicationTab:view")) {
                    return "403";
                }

                if (applicationId != null) {
//                    CASO: CARGA DE UN SOLO LA
//                    SOLO EN ESTE CASO SE DEVUELVE UNA CLASE DE TIPO LoanApplicationBoPainter
                    LoanApplication applicationFull = loanApplicationDao.getLoanApplication(applicationId, locale);
                    LoanApplicationBoPainter application = applicationPainterService.getApplicationById(applicationId, locale, request);

                    if (applicationFull != null && application != null) {
                        loanApplicationBoPainterSetter(applicationFull, application, true, false, request, locale);

                        List<LoanOffer> offers = loanApplicationDao.getLoanOffersAll(application.getId());
                        if (offers != null && !offers.isEmpty()) {
                            model.addAttribute("exchangeRate", offers.get(0).getExchangeRate());
                        }

                        if (application.isObserved() != null && application.isObserved()) {
                            Comment comment = loanApplicationDao.getLoanApplicationComment(applicationId, Comment.COMMENT_REASSIGN);
                            application.setObservedComment(comment.getComment());
                        }

                        List<LoanApplicationBoPainter> loanApplicationBoPainterList = new ArrayList<>();
                        loanApplicationBoPainterList.add(application);

                        model.addAttribute("loanApplications", loanApplicationBoPainterList);
                    }
                } else {
//                    CASO: CARGA 'N' LAs
//                    PARA ESTE CASO SE DEVUELVEN UN LISTADO DE CLASES DE TIPO LoanApplication
                    List<LoanApplication> loanApplicationList = loanApplicationDao.getLoanApplicationsByPerson(locale, personId, LoanApplication.class);

                    if (loanApplicationList != null) {
                        if (loanApplicationList.size() == 1) {
//                          CUANDO EL LISTADO SOLO TIENE UN ITEM. DEVUELVO ESTA SOLICITUD YA CARGADA
                            LoanApplication applicationFull = loanApplicationList.get(0);
                            LoanApplicationBoPainter application = applicationPainterService.getApplicationById(applicationFull.getId(), locale, request);
//                          C/P
                            if (applicationFull != null && application != null) {
                                loanApplicationBoPainterSetter(applicationFull, application, true, false, request, locale);

                                List<LoanOffer> offers = loanApplicationDao.getLoanOffersAll(application.getId());
                                if (offers != null && !offers.isEmpty()) {
                                    model.addAttribute("exchangeRate", offers.get(0).getExchangeRate());
                                }
                                if (application.isObserved() != null && application.isObserved()) {
                                    Comment comment = loanApplicationDao.getLoanApplicationComment(applicationId, Comment.COMMENT_REASSIGN);
                                    application.setObservedComment(comment.getComment());
                                }

                                List<LoanApplicationBoPainter> loanApplicationBoPainterList = new ArrayList<>();
                                loanApplicationBoPainterList.add(application);

                                model.addAttribute("loanApplications", loanApplicationBoPainterList);
                            }

                        } else if (loanApplicationList.size() > 1) {
//                        MAS DE UN ITEM DEVOLVERA LAS CABECERAS DE LA's ABRIENDO EL PRIMERO POR JS

                            for (LoanApplication loanApplication : loanApplicationList) {
                                if (loanApplication.isObserved() != null && loanApplication.isObserved()) {
                                    Comment comment = loanApplicationDao.getLoanApplicationComment(loanApplication.getId(), Comment.COMMENT_REASSIGN);
                                    loanApplication.setObservedComment(comment.getComment());
                                }
                            }

                            model.addAttribute("loanApplications", loanApplicationList);
                        }
                    }
                }

                if (person.getCountry().getId() == CountryParam.COUNTRY_ARGENTINA) {
                    NosisResult nosisResult = personDao.getBureauResult(personId, NosisResult.class);
                    ParteXML nosisXml = (nosisResult != null) ? nosisResult.getParteXML() : null;
                    model.addAttribute("nosisXml", nosisXml);
                }

                model.addAttribute("onlyView", false);

                fragmentToRender = "fragments/personFragments :: tab-applications";

                break;
            }
            case "data": {
                if (!SecurityUtils.getSubject().isPermitted("person:externalDataTab:view")) {
                    return "403";
                }

                List<ExternalUserInfoState> externals = userDao.getUserExternalInfoState(userId);
                List<LineaResultBoPainter> lineas = personBoDao.getPhoneContractLineaResult(personId);

                Optional<ExternalUserInfoState> sunatInfoState = externals.stream().filter(e -> e.getBotId() == Bot.SUNAT_BOT).findFirst();
                model.addAttribute("sunat", personDao.getSunatResult(personId));
                model.addAttribute("sunatState", sunatInfoState.orElse(null));

                Optional<ExternalUserInfoState> reniecInfoState = externals.stream().filter(e -> e.getBotId() == Bot.RENIEC_BOT).findFirst();
                model.addAttribute("reniec", personBoDao.getReniecResult(personId));
                model.addAttribute("reniecState", reniecInfoState.orElse(null));

                Optional<ExternalUserInfoState> essaludInfoState = externals.stream().filter(e -> e.getBotId() == Bot.ESSALUD_BOT).findFirst();
                model.addAttribute("essalud", personBoDao.getEssaludResult(personId));
                model.addAttribute("essaludState", essaludInfoState.orElse(null));

                Optional<ExternalUserInfoState> redamInfoState = externals.stream().filter(e -> e.getBotId() == Bot.REDAM_BOT).findFirst();
                model.addAttribute("redam", personDao.getRedamResult(personId));
                model.addAttribute("redamState", redamInfoState.orElse(null));

                Optional<ExternalUserInfoState> claroInfoState = externals.stream().filter(e -> e.getBotId() == Bot.CLARO).findFirst();
                Optional<LineaResultBoPainter> claroResult = Optional.empty();
                if (lineas != null) {
                    claroResult = lineas.stream().filter(l -> l.getOperador().toLowerCase().equals("claro")).findFirst();
                }
                model.addAttribute("claro", claroResult.orElse(null));
                model.addAttribute("claroState", claroInfoState.orElse(null));

                Optional<ExternalUserInfoState> virginInfoState = externals.stream().filter(e -> e.getBotId() == Bot.VIRGIN).findFirst();
                Optional<LineaResultBoPainter> virginResult = Optional.empty();
                if (lineas != null) {
                    virginResult = lineas.stream().filter(l -> l.getOperador().toLowerCase().equals("virgin")).findFirst();
                }
                model.addAttribute("virgin", virginResult.orElse(null));
                model.addAttribute("virginState", virginInfoState.orElse(null));

                Optional<ExternalUserInfoState> movistarInfoState = externals.stream().filter(e -> e.getBotId() == Bot.MOVISTAR).findFirst();
                Optional<LineaResultBoPainter> movistarResult = Optional.empty();
                if (lineas != null) {
                    movistarResult = lineas.stream().filter(l -> l.getOperador().toLowerCase().equals("movistar")).findFirst();
                }
                model.addAttribute("movistar", movistarResult.orElse(null));
                model.addAttribute("movistarState", movistarInfoState.orElse(null));

                Optional<ExternalUserInfoState> bitelInfoState = externals.stream().filter(e -> e.getBotId() == Bot.BITEL).findFirst();
                Optional<LineaResultBoPainter> bitelResult = Optional.empty();
                if (lineas != null) {
                    bitelResult = lineas.stream().filter(l -> l.getOperador().toLowerCase().equals("bitel")).findFirst();
                }
                model.addAttribute("bitel", bitelResult.orElse(null));
                model.addAttribute("bitelState", bitelInfoState.orElse(null));

                Optional<ExternalUserInfoState> entelInfoState = externals.stream().filter(e -> e.getBotId() == Bot.ENTEL).findFirst();
                Optional<LineaResultBoPainter> entelResult = Optional.empty();
                if (lineas != null) {
                    entelResult = lineas.stream().filter(l -> l.getOperador().toLowerCase().equals("entel")).findFirst();
                }
                model.addAttribute("entel", entelResult.orElse(null));
                model.addAttribute("entelState", entelInfoState.orElse(null));

                Optional<ExternalUserInfoState> satInfoState = externals.stream().filter(e -> e.getBotId() == Bot.SAT).findFirst();
                SatResult satResult = personBoDao.getSatResult(personId);
                model.addAttribute("sat", satResult);
                model.addAttribute("satState", satInfoState.orElse(null));

//                Optional<ExternalUserInfoState> sisInfoState = externals.stream().filter(e -> e.getBotId() == Bot.SIS).findFirst();
//                SisResult sisResult = personBoDao.getSisResult(personId);
//                model.addAttribute("sis", sisResult);
//                model.addAttribute("sisState", sisInfoState.orElse(null));

                Optional<ExternalUserInfoState> satPlateInfoState = externals.stream().filter(e -> e.getBotId() == Bot.SAT_PLATE).findFirst();
                List<SatPlateResult> satPlateResults = personDao.getSatPlateResult(personId);//TODO CHECK if this goes here or not
                model.addAttribute("satPlate", satPlateResults);
                model.addAttribute("satPlateState", satPlateInfoState.orElse(null));

                Optional<ExternalUserInfoState> soatRecordsInfoState = externals.stream().filter(e -> e.getBotId() == Bot.SOAT_RECORDS).findFirst();
                List<SoatRecordsResult> soatRecordsResult = personBoDao.getSoatRecordsResults(personId);//TODO CHECK if this goes here or not
                model.addAttribute("soat", soatRecordsResult);
                model.addAttribute("soatState", soatRecordsInfoState.orElse(null));
                model.addAttribute("afip", null);
                model.addAttribute("bcraDeudores", null);
                model.addAttribute("bcraCheques", null);
                model.addAttribute("anses", null);

                if (person.getCountry().getId() == CountryParam.COUNTRY_ARGENTINA) {
                    Optional<ExternalUserInfoState> afipInfoState = externals.stream().filter(e -> e.getBotId() == Bot.AFIP).findFirst();
                    model.addAttribute("afipState", afipInfoState.orElse(null));
                    AfipResult afipResult = (AfipResult) personDao.getAfipResult(personId);
                    model.addAttribute("afip", afipResult);

                    Optional<ExternalUserInfoState> ansesInfoState = externals.stream().filter(e -> e.getBotId() == Bot.ANSES).findFirst();
                    model.addAttribute("ansesState", ansesInfoState.orElse(null));
                    AnsesResult ansesResult = (AnsesResult) personDao.getAnsesResult(personId);
                    model.addAttribute("anses", ansesResult);
                }

                Optional<ExternalUserInfoState> migracionesInfoState = externals.stream().filter(e -> e.getBotId() == Bot.MIGRACIONES).findFirst();
                MigracionesResult migracionesResult = personDao.getMigraciones(personId);//TODO CHECK if this goes here or not

                if (migracionesResult != null) {
                    System.out.println("MIGRACIONES RESULT " + migracionesResult.toString());
                }

                if (person.getDocumentType().getId() == IdentityDocumentType.CE) {
                    model.addAttribute("migraciones", migracionesResult);
                    model.addAttribute("migracionesState", migracionesInfoState.orElse(null));
                    model.addAttribute("showMigraciones", true);
                } else {
                    model.addAttribute("showMigraciones", false);
                }

                Optional<ExternalUserInfoState> onpeInfoState = externals.stream().filter(e -> e.getBotId() == Bot.ONPE).findFirst();
                model.addAttribute("onpe", personDao.getOnpeResult(personId));
                model.addAttribute("onpeState", onpeInfoState.orElse(null));

                Optional<ExternalUserInfoState> universidadPeruInfoState = externals.stream().filter(e -> e.getBotId() == Bot.UNIVERSIDAD_PERU).findFirst();
                model.addAttribute("universidadPeru", personDao.getUniversidadPeru(personId));
                model.addAttribute("universidadPeruState", universidadPeruInfoState.orElse(null));

                String emailage = userDao.getUser(userId).getEmailValidationResult();

                model.addAttribute("emailValidation", emailage);

                Boolean botsRunning = botDao.botsRunning(userId);
                System.out.println("Bots running? " + botsRunning);
                logger.debug("Bots running? " + botsRunning);
                model.addAttribute("botsRunning", botsRunning);


                List<LoanApplicationBoPainter> loanApplications = loanApplicationDao.getLoanApplicationsByPerson(locale, personId, LoanApplicationBoPainter.class);
                boolean stateProductAllowsBot = false;
                if (loanApplications != null) {
                    for (LoanApplicationBoPainter labp : loanApplications) {
                        if (labp.getStatus().getId() != LoanApplicationStatus.REJECTED || labp.getStatus().getId() != LoanApplicationStatus.REJECTED_AUTOMATIC) {
                            stateProductAllowsBot = true;
                            break;
                        }
                    }
                }


                //There is at least one product with the right state and product active.
                Optional<LoanApplication> oLaTradi = Optional.ofNullable(loanApplicationDao.getActiveLoanApplicationByPerson(locale, personId, Product.TRADITIONAL));
                Optional<LoanApplication> oLaShort = Optional.ofNullable(loanApplicationDao.getActiveLoanApplicationByPerson(locale, personId, Product.SHORT_TERM));
                Optional<LoanApplication> oLaAutos = Optional.ofNullable(loanApplicationDao.getActiveLoanApplicationByPerson(locale, personId, Product.AUTOS));
                Optional<LoanApplication> oLaInsurance = Optional.ofNullable(loanApplicationDao.getActiveLoanApplicationByPerson(locale, personId, Product.INSURANCE));
                Optional<LoanApplication> oLaConsolidation = Optional.ofNullable(loanApplicationDao.getActiveLoanApplicationByPerson(locale, personId, Product.DEBT_CONSOLIDATION));
                Optional<LoanApplication> oLaAdvance = Optional.ofNullable(loanApplicationDao.getActiveLoanApplicationByPerson(locale, personId, Product.SALARY_ADVANCE));
                Optional<LoanApplication> oLaAgreement = Optional.ofNullable(loanApplicationDao.getActiveLoanApplicationByPerson(locale, personId, Product.AGREEMENT));

                boolean bTradi = oLaTradi.isPresent() &&
                        (oLaTradi.map(x -> x.getStatus().getId()).map(x ->
                                x != LoanApplicationStatus.REJECTED &&
                                        x != LoanApplicationStatus.REJECTED_AUTOMATIC).orElse(false));
                boolean bShort = oLaShort.isPresent() &&
                        (oLaShort.map(x -> x.getStatus().getId()).map(x ->
                                x != LoanApplicationStatus.REJECTED ||
                                        x != LoanApplicationStatus.REJECTED_AUTOMATIC).orElse(false));
                boolean bAutos = oLaAutos.isPresent() &&
                        (oLaAutos.map(x -> x.getStatus().getId()).map(x ->
                                x != LoanApplicationStatus.REJECTED ||
                                        x != LoanApplicationStatus.REJECTED_AUTOMATIC).orElse(false));
                boolean bInsurance = oLaInsurance.isPresent() &&
                        (oLaInsurance.map(x -> x.getStatus().getId()).map(x ->
                                x != LoanApplicationStatus.REJECTED ||
                                        x != LoanApplicationStatus.REJECTED_AUTOMATIC).orElse(false));
                boolean bConsolidation = oLaConsolidation.isPresent() &&
                        (oLaConsolidation.map(x -> x.getStatus().getId()).map(x ->
                                x != LoanApplicationStatus.REJECTED ||
                                        x != LoanApplicationStatus.REJECTED_AUTOMATIC).orElse(false));
                boolean bAdvance = oLaAdvance.isPresent() &&
                        (oLaAdvance.map(x -> x.getStatus().getId()).map(x ->
                                x != LoanApplicationStatus.REJECTED ||
                                        x != LoanApplicationStatus.REJECTED_AUTOMATIC).orElse(false));
                boolean bAgreement = oLaAgreement.isPresent() &&
                        (oLaAgreement.map(x -> x.getStatus().getId()).map(x ->
                                x != LoanApplicationStatus.REJECTED ||
                                        x != LoanApplicationStatus.REJECTED_AUTOMATIC).orElse(false));
                //TODO include bAdvance when only the right bot (redam) runs when it is appropiate.
                model.addAttribute("stateProductAllowsBot", stateProductAllowsBot);//bTradi || bShort || bAutos || bInsurance || bConsolidation || bAgreement);

                fragmentToRender = "fragments/personFragments :: tab-data";

                break;
            }
            case "socialNetworks": {
                if (!SecurityUtils.getSubject().isPermitted("person:socialNetworkTab:view")) {
                    return "403";
                }

                // Facebok
                model.addAttribute("facebook", userDao.getUserFacebook(userId));
                // Linkedin
                model.addAttribute("linkedin", userDao.getLinkedin(personId));
                // Mercado Libre
                UserNetworkToken userNetworkTokenMl = userDao.getUserNetworkTokenByProvider(userId, 'M');
                if (userNetworkTokenMl != null) {
                    model.addAttribute("mercadoLibre", userNetworkTokenMl);
                }
                // Google
                UserNetworkToken userNetworkTokenGo = userDao.getUserNetworkTokenByProvider(userId, 'G');
                if (userNetworkTokenGo != null) {
                    model.addAttribute("google", userNetworkTokenGo);
                }
                // Windows
                UserNetworkToken userNetworkTokenWn = userDao.getUserNetworkTokenByProvider(userId, 'W');
                if (userNetworkTokenWn != null) {
                    model.addAttribute("windows", userNetworkTokenWn);
                }
                // Yahoo
                UserNetworkToken userNetworkTokenYh = userDao.getUserNetworkTokenByProvider(userId, 'Y');
                if (userNetworkTokenYh != null) {
                    model.addAttribute("yahoo", userNetworkTokenYh);
                }

                fragmentToRender = "fragments/personFragments :: tab-socialNetworks";

                break;
            }
            case "interactions": {
                if (!SecurityUtils.getSubject().isPermitted("person:interactionTab:view")) {
                    return "403";
                }

                List<PersonInteraction> personInteractions = interactionDao.getPersonInteractions(personId, locale);
                List<GatewayContacts> gatewayContacts = personDao.getCollectionContacts(personId);

                List<Object> allInteractions = new ArrayList<>();
                if (personInteractions != null)
                    allInteractions.addAll(personInteractions);
                if (gatewayContacts != null)
                    allInteractions.addAll(gatewayContacts);
                allInteractions = allInteractions.stream().sorted(Comparator.nullsLast(
                        (e1, e2) -> {
                            Date e1d = e1 instanceof PersonInteraction ? ((PersonInteraction) e1).getRegisterDate() : ((GatewayContacts) e1).getRegisterDate();
                            Date e2d = e2 instanceof PersonInteraction ? ((PersonInteraction) e2).getRegisterDate() : ((GatewayContacts) e2).getRegisterDate();
                            if (e1d == null || e2d == null)
                                return -1;
                            return e2d.compareTo(e1d);
                        })
                ).collect(Collectors.toList());

                List<Object> unknowInteractions = new ArrayList<>();

                for (Object obj : allInteractions) {
                    Integer tempCreditId = obj instanceof PersonInteraction ? ((PersonInteraction) obj).getCreditId() : ((GatewayContacts) obj).getCreditId();
                    Integer tempLoanApplicationId = obj instanceof PersonInteraction ? ((PersonInteraction) obj).getLoanApplicationId() : null;

                    if (tempCreditId == null && tempLoanApplicationId == null) {
                        unknowInteractions.add(obj);
                    }
                }

                model.addAttribute("unknowInteractions", unknowInteractions);
                model.addAttribute("userId", userId);

                fragmentToRender = "fragments/personFragments :: tab-interactions";

                break;
            }
            case "changelog":
                if (!SecurityUtils.getSubject().isPermitted("person:changelogTab:view")) {
                    return "403";
                }
                break;
            case "bankAccount": {
                if (!SecurityUtils.getSubject().isPermitted("person:bankAccountTab:view")) {
                    return "403";
                }
                person = personDao.getPerson(catalogService, locale, personId, true);

                Integer[] creditIds = creditDao.getActiveCreditIdsByPerson(locale, personId, null);
                if (creditIds != null) {
                    for (int i = 0; i < creditIds.length; i++) {
                        Credit credit = creditDao.getCreditByID(creditIds[i], locale, false, Credit.class);
                        if (credit.getProduct().isAdvance()) {
                            model.addAttribute("advanceCredit", true);
                            break;
                        }
                    }
                }

                model.addAttribute("bankAccount", personDao.getPersonBankAccountInformation(locale, personId));
                model.addAttribute("employeeBankAccounts", personDao.getEmployeesByDocument(person.getDocumentType().getId(), person.getDocumentNumber(), locale));

                fragmentToRender = "fragments/personFragments :: tab-bankAccount";

                break;
//                case "cancellation":
//                    break;
//                case "refinancement":
//                    break;
            }
            case "rcc": {
                if (!SecurityUtils.getSubject().isPermitted("person:sbsTab:view")) {
                    return "403";
                }

                switch (person.getCountry().getId()) {
                    case CountryParam.COUNTRY_ARGENTINA:
                        BcraResult bcraResult = personDao.getBcraResult(personId);
                        if (bcraResult != null && bcraResult.getHistorialDeudas() != null && bcraResult.getHistorialDeudas().size() > 0) {
                            List<String> banks = bcraResult.getHistorialDeudas().stream().map(h -> h.getNombre()).collect(Collectors.toList());
                            List<String> periods = bcraResult.getHistorialDeudas().get(0).getHistorial().stream().map(e -> e.getPeriodo()).collect(Collectors.toList());
                            model.addAttribute("banks", banks);
                            model.addAttribute("periods", periods);
                        }
                        model.addAttribute("bcraResult", bcraResult);

                        break;
                    case CountryParam.COUNTRY_PERU:
                        model.addAttribute("personRccCal", personDao.getPersonRccCalification(personId));
                        model.addAttribute("personRcc", personDao.getPersonRcc(personId));
                        break;
                }

                Person partner = person.getPartner();

                if (partner != null) {
                    model.addAttribute("partnerRccCal", personDao.getPersonRccCalification(partner.getId()));
                    model.addAttribute("partnerRcc", personDao.getPersonRcc(partner.getId()));
                }

                PersonOcupationalInformation personOcupationalInformation = principalOcupation.orElse(null);

                if (personOcupationalInformation != null) {

                    List<LoanApplication> loanApplications =
                            loanApplicationDao.getLoanApplicationsByPerson(locale, personId, LoanApplication.class);

                    if (loanApplications != null &&
                            loanApplications.stream()
                                    .filter(la -> la.getSelectedEntityId() != null)
                                    .filter(la -> la.getSelectedEntityId() == Entity.CREDIGOB)
                                    .findFirst()
                                    .orElse(null) != null) {

                        model.addAttribute("rucRccCal", personDao.getRucRccCalification(personOcupationalInformation.getRuc()));
                        model.addAttribute("rucRcc", personDao.getRucRcc(personOcupationalInformation.getRuc()));
                    } else {
                        List<Credit> credits = creditDao.getCreditsByPerson(personId, locale, Credit.class);

                        if (credits != null &&
                                credits.stream()
                                        .filter(c -> c.getEntity().getId() == Entity.CREDIGOB)
                                        .findFirst()
                                        .orElse(null) != null) {

                            model.addAttribute("rucRccCal", personDao.getRucRccCalification(personOcupationalInformation.getRuc()));
                            model.addAttribute("rucRcc", personDao.getRucRcc(personOcupationalInformation.getRuc()));
                        }
                    }
                }

                fragmentToRender = "fragments/personFragments :: tab-rcc";

                break;
            }
            case "contact": {
                if (!SecurityUtils.getSubject().isPermitted("person:contactTab:view")) {
                    return "403";
                }
                person = personDao.getPerson(catalogService, locale, personId, true);
                model.addAttribute("person", person);
                model.addAttribute("personContactInfo", personDao.getPersonContactInformation(locale, personId));
                model.addAttribute("personReferrals", personDao.getReferrals(personId, locale));
                model.addAttribute("employees", personDao.getEmployeesByDocument(person.getDocumentType().getId(), person.getDocumentNumber(), locale));
                model.addAttribute("addresses", personDao.getHistoricAddressByPerson(person.getId(), locale));

                model.addAttribute("facebook", userDao.getUserFacebook(userId));
                model.addAttribute("linkedin", userDao.getLinkedin(personId));

                model.addAttribute("personNosisPhone", personService.getPhoneInfoNosis(personId));

                model.addAttribute("applicationId", applicationId);

                List<LineaResultBoPainter> lineasContact = personBoDao.getPhoneContractLineaResult(personId);

                Optional<LineaResultBoPainter> claroResultContact = Optional.empty();
                if (lineasContact != null) {
                    claroResultContact = lineasContact.stream().filter(l -> l.getOperador().toLowerCase().equals("Claro")).findFirst();
                }
                model.addAttribute("claro", claroResultContact.orElse(null));

                Optional<LineaResultBoPainter> movistarResultContact = Optional.empty();
                if (lineasContact != null) {
                    movistarResultContact = lineasContact.stream().filter(l -> l.getOperador().toLowerCase().equals("movistar")).findFirst();
                }
                model.addAttribute("movistar", movistarResultContact.orElse(null));

                Optional<LineaResultBoPainter> bitelResultContact = Optional.empty();
                if (lineasContact != null) {
                    bitelResultContact = lineasContact.stream().filter(l -> l.getOperador().toLowerCase().equals("bitel")).findFirst();
                }
                model.addAttribute("bitel", bitelResultContact.orElse(null));

                Optional<LineaResultBoPainter> entelResultContact = Optional.empty();
                if (lineasContact != null) {
                    entelResultContact = lineasContact.stream().filter(l -> l.getOperador().toLowerCase().equals("entel")).findFirst();
                }
                model.addAttribute("entel", entelResultContact.orElse(null));

                Optional<LineaResultBoPainter> virginResultContact = Optional.empty();
                if (lineasContact != null) {
                    virginResultContact = lineasContact.stream().filter(l -> l.getOperador().toLowerCase().equals("virgin")).findFirst();
                }
                model.addAttribute("virgin", virginResultContact.orElse(null));

                fragmentToRender = "fragments/personFragments :: tab-contact";

                break;
            }
            case "bureaus": {
                if (!SecurityUtils.getSubject().isPermitted("person:equifaxTab:view")) {
                    return "403";
                }

                List<Integer> bureauIds = new ArrayList<>();
                List<LoanApplicationBoPainter> loanApplications = loanApplicationDao.getLoanApplicationsByPerson(locale, personId, LoanApplicationBoPainter.class);
                List<CreditBoPainter> credits = creditDao.getCreditsByPerson(personId, locale, CreditBoPainter.class);
                List<ApplicationBureau> bureaus = new ArrayList<>();
                List<ExperianResult> experianResultList = new ArrayList<>();

                if (loanApplications != null) {
                    for (LoanApplication loanApplication : loanApplications) {
                        List<ApplicationBureau> bureausResult = loanApplicationDao.getBureauResults(loanApplication.getId());
                        bureauIds.addAll(bureausResult
                                .stream()
                                .filter(b -> b.getBureauId() != null && b.getEquifaxResult() != null)
                                .mapToInt(b -> b.getBureauId())
                                .distinct()
                                .boxed()
                                .collect(Collectors.toList()));
                        List<ExperianResult> experianResults = loanApplicationDao.getExperianResultList(loanApplication.getId());
                        if (experianResults != null && !experianResults.isEmpty()) {
                            experianResultList.addAll(experianResults);
                        }

                        bureaus.addAll(bureausResult);
                    }
                }

                if (credits != null) {
                    for (CreditBoPainter credit : credits) {
                        EntityProductParams entityProductParams = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());
                        if (entityProductParams != null && entityProductParams.getMustRunBureau() &&
                                credit.getEntity().getBureaus() != null && !credit.getEntity().getBureaus().isEmpty()) {
                            for (Bureau bureau : credit.getEntity().getBureaus()) {
                                if (!bureauIds.contains(bureau.getId())) {
                                    bureauIds.add(bureau.getId());
                                }
                            }
                        }

                        if (loanApplications == null || loanApplications.stream().noneMatch(a -> a.getId().equals(credit.getLoanApplicationId()))) {
                            bureaus.addAll(loanApplicationDao.getBureauResults(credit.getLoanApplicationId()));
                        }
                    }
                }

                bureaus = bureaus.stream().filter(b -> b.getBureauId() != null).collect(Collectors.toList());


                model.addAttribute("experianResultList", experianResultList);
                model.addAttribute("personId", personId);
                model.addAttribute("bureauIds", bureauIds);
                model.addAttribute("bureausResult", bureaus);

                ApplicationBureau bureauEquifaxToShow = bureaus.stream().filter(b -> b.getBureauId().equals(Bureau.EQUIFAX)).sorted((e1, e2) -> e2.getRegisterDate().compareTo(e1.getRegisterDate())).findFirst().orElse(null);
                model.addAttribute("equifax", bureauEquifaxToShow);
                if (bureauEquifaxToShow != null) {
                    BureauContext bureauContext = new BureauContext();
                    bureauContext.setStrategy(Bureau.EQUIFAX);
                    if (bureauContext.getStrategy() != null) {
                        bureauContext.getStrategy().updateModelMap(locale, bureauEquifaxToShow, model, loanApplicationDao, personDao);
                    }
                }

                fragmentToRender = "fragments/person/bureaus :: bureaus";

                break;
            }
            case "email": {
                if (!SecurityUtils.getSubject().isPermitted("person:emailageTab:view")) {
                    return "403";
                }
                User user = userDao.getUser(userId);
                model.addAttribute("emailReport", user.getEmailResult());
                model.addAttribute("emailIpReport", user.getEmailIpResult());
                model.addAttribute("deviceReport", loanApplicationDao.getIovationByPerson(personId));

                model.addAttribute("emailageReport", user.getJsEmailage());

                fragmentToRender = "fragments/personFragments :: tab-email";

                break;
            }
            case "rekognition": {
                // TODO put the permission
//                if (!SecurityUtils.getSubject().isPermitted("person:emailageTab:view")) {
//                    return "403";
//                }

                model.addAttribute("results", loanApplicationDao.getRecognitionResults(personId, locale));

                fragmentToRender = "fragments/personFragmentsRekognition :: tab-rekognition";

                break;
            }
            default: {
                break;
            }
        }

        logger.trace("Finish loading " + tab + " tab: " + (new Date().getTime() - startTime) + " ms");

        return fragmentToRender;
    }

    @RequestMapping(value = "/person/reject/loanApplicationFraudAlert", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanApplication:refuse", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object rejectLoanApplicationFraudAlert(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationFraudAlertId") Integer loanApplicationFraudAlertId
    ) throws Exception {

        creditDao.rejectLoanApplicationFraudAlert(loanApplicationFraudAlertId, sysUserService.getSessionSysuser().getId());
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/person/reject/allLoanApplicationFraudAlerts", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "person:fraudAlert:rejectAll", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object rejectAllLoanApplicationFraudAlerts(@RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception{

        creditDao.rejectAllLoanApplicationFraudAlerts(loanApplicationId, sysUserService.getSessionSysuser().getId());
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/person/assign/loanApplicationFraudFlag", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanApplication:refuse", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object assignLoanApplicationFraudFlag(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") Integer loanApplicationId,
            @RequestParam("fraudFlagId") Integer fraudFlagId,
            @RequestParam("commentary") String commentary
    ) throws Exception {
        creditDao.assignLoanApplicationFraudFlag(loanApplicationId, null, fraudFlagId, commentary, sysUserService.getSessionSysuser().getId());
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/person/assign/documentsMissingFlag", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanApplication:refuse", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object markAsDocumentsMissing(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") Integer loanApplicationId
    ) throws Exception {
        loanApplicationDao.updateLoanApplicationFilesUploaded(loanApplicationId, false);
        return AjaxResponse.ok("");
    }

    @RequestMapping(value = "/person/{field}", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> updateProperty(
            ModelMap model, Locale locale,
            @PathVariable("field") String field,
            @RequestParam("personId") Integer personId,
            @RequestParam(value = "creditId", required = false) Integer creditId,
            @RequestParam(value = "loanId", required = false) Integer loanId,
            @RequestParam(value = "value", required = false) String value,
            @RequestParam(value = "partnerDocumentType", required = false) Integer partnerDocumentType,
            UpdatePersonAddressForm updatePersonAddressForm,
            Question28Form occupationalPhoneNumberForm) throws Exception {

        if (!field.equals("address") && !field.equals("jobPhone") && (value == null || value.isEmpty())) {
            return AjaxResponse.errorMessage("El valor no puede ser vacio");
        } else {
            Person person;
            FieldValidator validator;
            switch (field) {
                case "birthUbigeo":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:personalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.BIRTH_UBIGEO, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    if (catalogService.getUbigeo(value).getDepartment() == null ||
                            catalogService.getUbigeo(value).getProvince() == null ||
                            catalogService.getUbigeo(value).getDistrict() == null) {
                        return AjaxResponse.errorMessage("El ubigeo es incorrecto.");
                    }
                    personDao.updateBirthUbigeo(personId, value);
                    break;
                case "birthday":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:personalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.BIRTHDAY, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateBirthday(personId, new SimpleDateFormat(com.affirm.system.configuration.Configuration.BACKOFFICE_FRONT_ONLY_DATE_FORMAT).parse(value));
                    break;
                case "nationality":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:personalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.NATIONALITY, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateNationality(personId, Integer.parseInt(value));
                    break;
                case "maritalStatus":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:personalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.MARITAL_STATUS_ID, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateMaritalStatus(personId, Integer.parseInt(value));
                    break;
                case "dependents":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:personalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.DEPENDENTS, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateDependents(personId, Integer.parseInt(value));
                    break;
                case "partnerDocument":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:personalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    if (partnerDocumentType == null) {
                        return AjaxResponse.errorMessage("Los valores enviados no son válidos");
                    }

                    validator = new StringFieldValidator(partnerDocumentType == IdentityDocumentType.DNI ? ValidatorUtil.DOC_NUMBER_DNI : ValidatorUtil.DOC_NUMBER_CE, value);
                    if (validator.validate(locale)) {
                        Integer partnerPersonId = personDao.getPersonIdByDocument(partnerDocumentType, value);
                        // If the person doesnt exist, create it
                        if (partnerPersonId == null) {
                            // Validate the permission
                            if (!SecurityUtils.getSubject().isPermitted("person:create")) {
                                return AjaxResponse.errorForbidden();
                            }
                            partnerPersonId = personDao.createPerson(partnerDocumentType, value, locale).getId();
                        }
                        personDao.updatePartner(personId, partnerPersonId);
                        webscrapperService.callRunSynthesized(value, null);
                        if (partnerDocumentType == IdentityDocumentType.DNI) {
                            personService.updatePersonDataFromReniecBD(partnerPersonId, value);
                        }
                    }
                    break;
                case "partnerName":
                case "partnerFirstSurname":
                case "partnerLastSurname":
                case "partnerBirthday":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:personalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    // Verify that the partner exists
                    person = personDao.getPerson(catalogService, locale, personId, true);
                    if (person.getPartner() == null) {
                        return AjaxResponse.errorMessage("El conyugue/ conviviente no existe");
                    }
                    if (field.equals("partnerName")) {
                        validator = new StringFieldValidator(ValidatorUtil.NAME, value);
                        if (!validator.validate(locale)) {
                            return AjaxResponse.errorMessage(validator.getErrors());
                        }
                        personDao.updateName(person.getPartner().getId(), value);
                    } else if (field.equals("partnerFirstSurname")) {
                        validator = new StringFieldValidator(ValidatorUtil.FIRST_SURNAME, value);
                        if (!validator.validate(locale)) {
                            return AjaxResponse.errorMessage(validator.getErrors());
                        }
                        personDao.updateFirstSurname(person.getPartner().getId(), value);
                    } else if (field.equals("partnerLastSurname")) {
                        validator = new StringFieldValidator(ValidatorUtil.LAST_SURNAME, value);
                        if (!validator.validate(locale)) {
                            return AjaxResponse.errorMessage(validator.getErrors());
                        }
                        personDao.updateLastSurname(person.getPartner().getId(), value);
                    } else if (field.equals("partnerBirthday")) {
                        validator = new StringFieldValidator(ValidatorUtil.BIRTHDAY, value);
                        if (!validator.validate(locale)) {
                            return AjaxResponse.errorMessage(validator.getErrors());
                        }
                        personDao.updateBirthday(person.getPartner().getId(), new SimpleDateFormat(com.affirm.system.configuration.Configuration.BACKOFFICE_FRONT_ONLY_DATE_FORMAT).parse(value));
                    }
                    break;
                case "email":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:contactInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.EMAIL, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }

                    if (loanId == null) {
                        return AjaxResponse.errorMessage("No se encontró valor: applicationId");
                    }

                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanId, Configuration.getDefaultLocale());
                    Integer userId = userDao.getUserIdByPersonId(personId);
                    User user = userDao.getUser(userId);
                    int emailId = userDao.registerEmailChange(userId, value);

                    userDao.validateEmailChange(userId, emailId);

//                    ALWAYS SEND VERIFICATION EMAIL
                    userDao.verifyEmail(userId, emailId, false);// return email to is_verified = false for users.tb_users and users.tb_email

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, 5);

                    PersonInteraction personInteraction = new PersonInteraction();
                    personInteraction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                    personInteraction.setInteractionContent(catalogService.getInteractionContent(loanApplication.getCountry().getId() == CountryParam.COUNTRY_PERU ? InteractionContent.PERU_PROCESS_SEND_EMAIL_VERIFICATION : InteractionContent.ARG_PROCESS_SEND_EMAIL_VERIFICATION, loanApplication.getCountry().getId()));
                    personInteraction.setDestination(user.getEmail());
                    personInteraction.setPersonId(personId);
                    personInteraction.setLoanApplicationId(loanApplication.getId());
                    personInteraction.setCreditId(creditId);

                    JSONObject verificationToken = new JSONObject();
                    verificationToken.put("emailId", emailId);
                    verificationToken.put("userId", loanApplication.getUserId());
                    verificationToken.put("timeout", calendar.getTime().getTime());

                    String baseUrl;
                    String countryDomain = loanApplication.getCountry().getDomains().get(0);
                    if (Configuration.hostEnvIsLocal()) {
                        baseUrl = "http://" + countryDomain + ":8080";
                    } else if(!Configuration.hostEnvIsProduction()) {
                        baseUrl = "https://" + Configuration.getEnvironmmentName() + "." + countryDomain;
                    } else {
                        baseUrl = "https://" + countryDomain;
                    }

                    JSONObject jsonVars = new JSONObject();
                    jsonVars.put("LINK", String.format("%s/email-verification/%s", baseUrl, CryptoUtil.encrypt(verificationToken.toString())));
                    jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
                    jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

                    interactionService.sendPersonInteraction(personInteraction, jsonVars, null);

                    CompletableFuture.runAsync(() -> {
                        try {
                            List<PersonInteraction> personInteractions = interactionDao.getPersonInteractionsByLoanApplication(loanApplication.getPersonId(), loanId, Configuration.getDefaultLocale());
                            int[] interactions = personInteractions != null ?
                                    personInteractions
                                            .stream()
                                            .filter(pi -> loanId.equals(pi.getLoanApplicationId()))
                                            .filter(pi -> pi.getInteractionProvider() != null && pi.getInteractionProvider().getId() == InteractionProvider.AWS)
                                            .map(PersonInteraction::getId)
                                            .mapToInt(i -> i)
                                            .toArray() : new int[]{};

                            Map<String, Date> map = awsElasticSearchClient.getEmailEventsByPersonInteractionIds(interactions);
                            if (map.keySet().contains("Abierto")) {
                                userDao.verifyEmail(userId, emailId, true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    break;
                case "phone":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:contactInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }

                    person = personDao.getPerson(catalogService, locale, personId, false);
                    switch (person.getDocumentType().getCountryId()) {
                        case CountryParam.COUNTRY_PERU:
                            validator = new StringFieldValidator(ValidatorUtil.CELLPHONE_NUMBER, value);
                            if (!validator.validate(locale)) {
                                return AjaxResponse.errorMessage(validator.getErrors());
                            }
                            personDao.updatePhoneNumber(person.getUserId(), "51", value);
                            break;
                        case CountryParam.COUNTRY_ARGENTINA:
                            Question53Form form = new Question53Form();
                            form.setAreaCode(value.substring(value.indexOf("(") + 1, value.indexOf(")")));
                            form.setPhoneNumber(value.substring(value.indexOf(")") + 1));
                            ((Question53Form.Validator) form.getValidator()).configValidator(CountryParam.COUNTRY_ARGENTINA, QuestionFlowService.Type.LOANAPPLICATION);
                            form.getValidator().validate(locale);
                            if (form.getValidator().isHasErrors()) {
                                return AjaxResponse.errorMessage("El numero es invalido");
                            }
                            personDao.updatePhoneNumber(person.getUserId(), "54", "(" + form.getAreaCode() + ") " + form.getPhoneNumber());
                            break;
                    }
                    break;
                case "address":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:contactInformation:addressInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    if (updatePersonAddressForm == null) {
                        return AjaxResponse.errorMessage("No hay datos enviados");
                    }

                    person = personDao.getPerson(catalogService, locale, personId, false);

                    ((UpdatePersonAddressForm.Validator) updatePersonAddressForm.getValidator()).setCountryId(person.getDocumentType().getCountryId(), updatePersonAddressForm.getWithoutNumber());
                    updatePersonAddressForm.getValidator().validate(locale);
                    if (updatePersonAddressForm.getValidator().isHasErrors()) {
                        return AjaxResponse.errorFormValidation(updatePersonAddressForm.getValidator().getErrorsJson());
                    }


                    if (updatePersonAddressForm.getAddressType() == null || !updatePersonAddressForm.getAddressType().equals("J")) {
                        updatePersonAddressForm.setAddressType("H");
                    }

                    if (updatePersonAddressForm.getAddressType().equals("H")) {
                        if (person.getDocumentType().getCountryId().equals(CountryParam.COUNTRY_PERU)) {
                            personDao.updateAddressInformation(
                                    personId,
                                    updatePersonAddressForm.getDepartamento(),
                                    updatePersonAddressForm.getProvincia(),
                                    updatePersonAddressForm.getDistrito(),
                                    updatePersonAddressForm.getRoadType() != null ? Integer.valueOf(updatePersonAddressForm.getRoadType()) : null,
                                    updatePersonAddressForm.getAggregatedAddress(),
                                    updatePersonAddressForm.getHouseNumber(),
                                    updatePersonAddressForm.getInterior(),
                                    updatePersonAddressForm.getReference(),
                                    updatePersonAddressForm.getLatitude(),
                                    updatePersonAddressForm.getLongitude());
                        } else if (person.getDocumentType().getCountryId().equals(CountryParam.COUNTRY_ARGENTINA)) {
                            personDao.updateAddressInformation(
                                    personId,
                                    updatePersonAddressForm.getDepartamento(),
                                    updatePersonAddressForm.getProvincia(),
                                    updatePersonAddressForm.getDistrito(),
                                    updatePersonAddressForm.getRoadType() != null ? Integer.valueOf(updatePersonAddressForm.getRoadType()) : null,
                                    (updatePersonAddressForm.getRoadType() != null ?
                                            catalogService.getAvenuesById(Integer.valueOf(updatePersonAddressForm.getRoadType())).getName() : "") + " " +
                                            updatePersonAddressForm.getRoadName() + " " +
                                            (updatePersonAddressForm.getWithoutNumber() != null ? "S/N" : "") + " " +
                                            (updatePersonAddressForm.getFloor() != null ? "Piso : " + updatePersonAddressForm.getFloor() : "") + " " +
                                            (updatePersonAddressForm.getHouseNumber() != null ? "Nro. : " + updatePersonAddressForm.getHouseNumber() : "") + " " + " " +
                                            (updatePersonAddressForm.getInterior() != null ? "Dep. " + updatePersonAddressForm.getInterior() : "") + " " +
                                            (updatePersonAddressForm.getZoneType() != null ?
                                                    catalogService.getAreaTypeById(Integer.valueOf(updatePersonAddressForm.getZoneType())).getName() : "") + " " +
                                            (updatePersonAddressForm.getZoneName() != null ? updatePersonAddressForm.getZoneName() : "") + " " +
                                            (updatePersonAddressForm.getReference() != null ? "Ref.: " + updatePersonAddressForm.getReference() : null),
                                    updatePersonAddressForm.getHouseNumber(),
                                    updatePersonAddressForm.getInterior(),
                                    updatePersonAddressForm.getReference(),
                                    updatePersonAddressForm.getLatitude(),
                                    updatePersonAddressForm.getLongitude());
                        } else if (person.getDocumentType().getCountryId().equals(CountryParam.COUNTRY_COLOMBIA)) {
                            personDao.updateAddressInformation(
                                    personId,
                                    updatePersonAddressForm.getDistrito(),
                                    updatePersonAddressForm.getRoadType() != null ? Integer.valueOf(updatePersonAddressForm.getRoadType()) : null,
                                    updatePersonAddressForm.getAggregatedAddress(),
                                    updatePersonAddressForm.getHouseNumber(),
                                    updatePersonAddressForm.getInterior(),
                                    updatePersonAddressForm.getReference(),
                                    updatePersonAddressForm.getLatitude(),
                                    updatePersonAddressForm.getLongitude());
                        }
                    } else if (updatePersonAddressForm.getAddressType().equals("J")) {
                        if (person.getDocumentType().getCountryId().equals(CountryParam.COUNTRY_PERU)) {
                            personDao.updateOcupationalAddress(
                                    personId,
                                    PersonOcupationalInformation.PRINCIPAL,
                                    updatePersonAddressForm.getAggregatedAddress(),
                                    false,
                                    updatePersonAddressForm.getDepartamento(),
                                    updatePersonAddressForm.getProvincia(),
                                    updatePersonAddressForm.getDistrito());
                        } else if (person.getDocumentType().getCountryId().equals(CountryParam.COUNTRY_ARGENTINA)) {
                            personDao.updateOcupationalAddress(
                                    personId,
                                    PersonOcupationalInformation.PRINCIPAL,
                                    updatePersonAddressForm.getAggregatedAddress(),
                                    false,
                                    null,
                                    null,
                                    null);
                        } else if (person.getDocumentType().getCountryId().equals(CountryParam.COUNTRY_COLOMBIA)) {
                            personDao.updateOcupationalAddress(
                                    personId,
                                    PersonOcupationalInformation.PRINCIPAL,
                                    updatePersonAddressForm.getAggregatedAddress(),
                                    false,
                                    updatePersonAddressForm.getDistrito());
                        }
                    }

                    String ubigeo = null;
                    if (person.getDocumentType().getCountryId().equals(CountryParam.COUNTRY_PERU)) {
                        ubigeo = updatePersonAddressForm.getDepartamento() + updatePersonAddressForm.getProvincia() + updatePersonAddressForm.getDistrito();
                    }
                    Direccion address = new Direccion(catalogService, updatePersonAddressForm.getAddressType(),
                            updatePersonAddressForm.getRoadType(),
                            updatePersonAddressForm.getRoadName(),
                            updatePersonAddressForm.getHouseNumber(),
                            updatePersonAddressForm.getInterior(),
                            updatePersonAddressForm.getManzana(),
                            updatePersonAddressForm.getLote(), ubigeo,
                            updatePersonAddressForm.getReference(),
                            updatePersonAddressForm.getZoneType(),
                            updatePersonAddressForm.getZoneName(),
                            Long.valueOf(updatePersonAddressForm.getDistrito()),
                            updatePersonAddressForm.getFloor(),
                            updatePersonAddressForm.getWithoutNumber(),
                            updatePersonAddressForm.getPostalCode());

                    personDao.registerDisgregatedAddress(personId, address);
                    address.setSearchQuery(updatePersonAddressForm.getSearchQuery());
                    address.setLatitude(updatePersonAddressForm.getLatitude());
                    address.setLongitude(updatePersonAddressForm.getLongitude());

                    if ("H".equals(updatePersonAddressForm.getAddressType())) {
                        personDao.registerAddressCoordinates(personId, address);
                    } else if ("J".equals(updatePersonAddressForm.getAddressType())) {
                        int ocupationalNumber = personService.getCurrentOcupationalInformation(personId, locale).getNumber();
                        personDao.registerJobAddressCoordinates(personId, ocupationalNumber, address);
                    }

                    break;
                case "principalActivityType":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.ACTIVITY_TYPE_ID, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupationalActivityType(personId, 1, Integer.parseInt(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;
                case "principalSubActivityType":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.ACTIVITY_TYPE_ID, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalSubActivityType(personId, 1, Integer.parseInt(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;
                case "principalDependentRuc":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.RUC, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupationalRuc(personId, 1, value);
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    person = personDao.getPerson(catalogService, locale, personId, false);

                    /*webscrapperService.setCountry(person.getCountry());
                    webscrapperService.callClaroBot(IdentityDocumentType.RUC, value, null);
                    webscrapperService.callMovistarBot(IdentityDocumentType.RUC, value, null);
                    webscrapperService.callBitelBot(IdentityDocumentType.RUC, value, null);
                    webscrapperService.callSunatRucBot(value, null);*/
                    break;
                case "principalDependentCompany":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.COMPANY_NAME, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupationalCompany(personId, 1, value);
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;
                case "principalDependentCiiu":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.CIIU, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupationalCiiu(personId, 1, value);
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;
                case "principalOcupation":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.OCUPATION_ID, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalOcupation(personId, 1, Integer.parseInt(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;
                case "principalSector":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.SECTOR_ID, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupationalSector(personId, 1, value);
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;
                case "principalDependentTime":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.ACTIVITY_TIME, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateEmploymentTime(personId, 1, value);
                    break;
                case "principalDependentGrossIncome":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }

                    validator = getGrossIncomeValidator(personId, locale, value);

                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateFixedGrossIncome(personId, 1, Integer.parseInt(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;

                case "principalDependentVariableGrossIncome":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = getGrossIncomeValidator(personId, locale, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateVariableGrossIncome(personId, 1, Integer.parseInt(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;

                case "principalIndependentRegime":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.REGIME, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupationalRegime(personId, 1, value);
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;

                case "principalIndependentPrincipalClient":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.RUC, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalClient1(personId, 1, value);
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;
                case "principalIndependentShareholding":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new DoubleFieldValidator(ValidatorUtil.SHAREHOLDING, Double.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupationalShareholding(personId, 1, String.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;
                case "principalIndependentLastYearsSellings":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new DoubleFieldValidator(ValidatorUtil.OCUPATION_LAST_YEAR_SELLINGS, Double.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalLastYearSellings(personId, 1, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;
                case "principalIndependentExerciseOutcome":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new DoubleFieldValidator(ValidatorUtil.OCUPATION_LAST_YEAR_SELLINGS, Double.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalExerciseOutcome(personId, 1, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;
                case "principalIndependentSalesPercentageFixedCosts":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.OCUPATION_SALES_PERCENTAGE, Integer.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalSalesPercentageFixedCosts(personId, 1, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;
                case "principalSalesPercentageVariableCosts":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.OCUPATION_SALES_PERCENTAGE, Integer.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalSalesPercentageVariableCosts(personId, 1, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;

                case "principalIndependentSalesPercentajeBestClient":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.OCUPATION_SALES_PERCENTAGE, Integer.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalSalesPercentageBestClient(personId, 1, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;
                case "principalIndependentDailyIncome":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new DoubleFieldValidator(ValidatorUtil.OCUPATION_DAILY_INCOME, Double.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalAverageDailyIncome(personId, 1, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;
                case "principalIndependentLastYearCompensation":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new DoubleFieldValidator(ValidatorUtil.OCUPATION_LAST_YEAR_SELLINGS, Double.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalLastYearCompensation(personId, 1, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;
                case "principalBelonging":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.RENTIER_BELONGING, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    int[] arr = Arrays.stream(value.substring(1, value.length() - 1).split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
                    personDao.updateBelonging(personId, 1, Arrays.toString(arr));
                    // Invalidate new ocupational information
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;
                case "jobPhone":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    if (occupationalPhoneNumberForm == null) {
                        return AjaxResponse.errorMessage("No hay datos enviados");
                    }

                    person = personDao.getPerson(catalogService, locale, personId, false);

                    ((Question28Form.Validator) occupationalPhoneNumberForm.getValidator()).configValidator(person.getDocumentType().getCountryId());
                    occupationalPhoneNumberForm.getValidator().validate(locale);

                    if (occupationalPhoneNumberForm.getValidator().isHasErrors())
                        return AjaxResponse.errorFormValidation(occupationalPhoneNumberForm.getValidator().getErrorsJson());

                    registerPhoneNumber(
                            personId,
                            occupationalPhoneNumberForm.getTypePhone(),
                            (occupationalPhoneNumberForm.getPhoneCode() != null && !occupationalPhoneNumberForm.getPhoneCode().isEmpty() ?
                                    "(" + occupationalPhoneNumberForm.getPhoneCode() + ") " :
                                    "") + occupationalPhoneNumberForm.getPhoneNumber());

                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 1, true);
                    break;

                case "personSecundaryActivityType":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.ACTIVITY_TYPE_ID, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupationalActivityType(personId, 2, Integer.parseInt(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 2, true);
                    break;
                case "personSecundarySubActivityType":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.ACTIVITY_TYPE_ID, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalSubActivityType(personId, 2, Integer.parseInt(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 2, true);
                    break;
                case "secundaryDependentRuc":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.RUC, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupationalRuc(personId, 2, value);
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 2, true);
                    person = personDao.getPerson(catalogService, locale, personId, false);

                    /*webscrapperService.setCountry(person.getCountry());
                    webscrapperService.callClaroBot(IdentityDocumentType.RUC, value, null);
                    webscrapperService.callMovistarBot(IdentityDocumentType.RUC, value, null);
                    webscrapperService.callBitelBot(IdentityDocumentType.RUC, value, null);
                    webscrapperService.callSunatRucBot(value, null);*/
                    break;
                case "secundaryDependentCompany":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.COMPANY_NAME, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupationalCompany(personId, 2, value);
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 2, true);
                    break;
                case "secundaryDependentTime":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.ACTIVITY_TIME, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateEmploymentTime(personId, 2, value);
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 2, true);
                    break;
                case "secundaryDependentGrossIncome":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateFixedGrossIncome(personId, 2, Integer.parseInt(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 2, true);
                    break;
                case "secundaryIndependentRegime":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.REGIME, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupationalRegime(personId, 2, value);
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 2, true);
                    break;

                case "secundaryIndependentPrincipalClient":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.RUC, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalClient1(personId, 2, value);
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 2, true);
                    break;
                case "secundaryIndependentLastYearsSellings":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new DoubleFieldValidator(ValidatorUtil.OCUPATION_LAST_YEAR_SELLINGS, Double.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalLastYearSellings(personId, 2, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 2, true);
                    break;
                case "secundaryIndependentExerciseOutcome":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new DoubleFieldValidator(ValidatorUtil.OCUPATION_LAST_YEAR_SELLINGS, Double.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalExerciseOutcome(personId, 2, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 2, true);
                    break;
                case "secundaryIndependentSalesPercentageFixedCosts":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.OCUPATION_SALES_PERCENTAGE, Integer.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalSalesPercentageFixedCosts(personId, 2, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 2, true);
                    break;
                case "secundarySalesPercentageVariableCosts":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.OCUPATION_SALES_PERCENTAGE, Integer.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalSalesPercentageVariableCosts(personId, 2, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 2, true);
                    break;

                case "secundaryIndependentSalesPercentajeBestClient":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.OCUPATION_SALES_PERCENTAGE, Integer.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalSalesPercentageBestClient(personId, 2, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 2, true);
                    break;
                case "secundaryIndependentDailyIncome":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new DoubleFieldValidator(ValidatorUtil.OCUPATION_DAILY_INCOME, Double.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalAverageDailyIncome(personId, 2, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 2, true);
                    break;
                case "secundaryIndependentLastYearCompensation":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new DoubleFieldValidator(ValidatorUtil.OCUPATION_LAST_YEAR_SELLINGS, Double.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalLastYearCompensation(personId, 2, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 2, true);
                    break;
                case "secundaryBelonging":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.RENTIER_BELONGING, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    int[] arrSec = Arrays.stream(value.substring(1, value.length() - 1).split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
                    personDao.updateBelonging(personId, 2, Arrays.toString(arrSec));
                    // Invalidate new ocupational information
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 2, true);
                    break;
                case "personTertiaryActivityType":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.ACTIVITY_TYPE_ID, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupationalActivityType(personId, 3, Integer.parseInt(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 3, true);
                    break;
                case "tertiarySubActivityType":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.ACTIVITY_TYPE_ID, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalSubActivityType(personId, 3, Integer.parseInt(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 3, true);
                    break;
                case "tertiaryDependentRuc":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.RUC, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupationalRuc(personId, 3, value);
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 3, true);
                    person = personDao.getPerson(catalogService, locale, personId, false);

                    /*webscrapperService.setCountry(person.getCountry());
                    webscrapperService.callClaroBot(IdentityDocumentType.RUC, value, null);
                    webscrapperService.callMovistarBot(IdentityDocumentType.RUC, value, null);
                    webscrapperService.callBitelBot(IdentityDocumentType.RUC, value, null);
                    webscrapperService.callSunatRucBot(value, null);*/
                    break;
                case "tertiaryDependentCompany":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.COMPANY_NAME, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupationalCompany(personId, 3, value);
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 3, true);
                    break;
                case "tertiaryOcupation":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.OCUPATION_ID, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalOcupation(personId, 3, Integer.parseInt(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 3, true);
                    break;
                case "tertiaryDependentTime":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.ACTIVITY_TIME, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateEmploymentTime(personId, 3, value);
                    break;
                case "tertiaryDependentGrossIncome":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateFixedGrossIncome(personId, 3, Integer.parseInt(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 3, true);
                    break;

                case "tertiaryDependentVariableGrossIncome":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateVariableGrossIncome(personId, 3, Integer.parseInt(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 3, true);
                    break;

                case "tertiaryIndependentPrincipalClient":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.RUC, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalClient1(personId, 3, value);
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 3, true);
                    break;
                case "tertiaryIndependentShareholding":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new DoubleFieldValidator(ValidatorUtil.SHAREHOLDING, Double.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupationalShareholding(personId, 3, String.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 3, true);
                    break;
                case "tertiaryIndependentLastYearsSellings":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new DoubleFieldValidator(ValidatorUtil.OCUPATION_LAST_YEAR_SELLINGS, Double.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalLastYearSellings(personId, 3, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 3, true);
                    break;
                case "tertiaryIndependentExerciseOutcome":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new DoubleFieldValidator(ValidatorUtil.OCUPATION_LAST_YEAR_SELLINGS, Double.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalExerciseOutcome(personId, 3, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 3, true);
                    break;
                case "tertiaryIndependentSalesPercentageFixedCosts":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.OCUPATION_SALES_PERCENTAGE, Integer.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalSalesPercentageFixedCosts(personId, 3, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 3, true);
                    break;
                case "tertiarySalesPercentageVariableCosts":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.OCUPATION_SALES_PERCENTAGE, Integer.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalSalesPercentageVariableCosts(personId, 3, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 3, true);
                    break;

                case "tertiaryIndependentSalesPercentajeBestClient":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.OCUPATION_SALES_PERCENTAGE, Integer.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalSalesPercentageBestClient(personId, 3, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 3, true);
                    break;
                case "tertiaryIndependentDailyIncome":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new DoubleFieldValidator(ValidatorUtil.OCUPATION_DAILY_INCOME, Double.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalAverageDailyIncome(personId, 3, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 3, true);
                    break;
                case "tertiaryIndependentLastYearCompensation":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new DoubleFieldValidator(ValidatorUtil.OCUPATION_LAST_YEAR_SELLINGS, Double.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalLastYearCompensation(personId, 3, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 3, true);
                    break;
                case "personQuaternaryActivityType":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.ACTIVITY_TYPE_ID, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupationalActivityType(personId, 4, Integer.parseInt(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 4, true);
                    break;
                case "quaternarySubActivityType":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.ACTIVITY_TYPE_ID, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalSubActivityType(personId, 4, Integer.parseInt(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 4, true);
                    break;
                case "quaternaryDependentRuc":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.RUC, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupationalRuc(personId, 4, value);
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 4, true);
                    person = personDao.getPerson(catalogService, locale, personId, false);

                    /*webscrapperService.setCountry(person.getCountry());
                    webscrapperService.callClaroBot(IdentityDocumentType.RUC, value, null);
                    webscrapperService.callMovistarBot(IdentityDocumentType.RUC, value, null);
                    webscrapperService.callBitelBot(IdentityDocumentType.RUC, value, null);
                    webscrapperService.callSunatRucBot(value, null);*/
                    break;
                case "quaternaryDependentCompany":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.COMPANY_NAME, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupationalCompany(personId, 4, value);
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 4, true);
                    break;
                case "quaternaryOcupation":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.OCUPATION_ID, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalOcupation(personId, 4, Integer.parseInt(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 4, true);
                    break;
                case "quaternaryDependentTime":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.ACTIVITY_TIME, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateEmploymentTime(personId, 4, value);
                    break;
                case "quaternaryDependentGrossIncome":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateFixedGrossIncome(personId, 4, Integer.parseInt(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 4, true);
                    break;
                case "quaternaryDependentVariableGrossIncome":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateVariableGrossIncome(personId, 4, Integer.parseInt(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 4, true);
                    break;

                case "quaternaryIndependentPrincipalClient":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.RUC, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalClient1(personId, 4, value);
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 4, true);
                    break;
                case "quaternaryIndependentShareholding":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new DoubleFieldValidator(ValidatorUtil.SHAREHOLDING, Double.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupationalShareholding(personId, 4, String.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 4, true);
                    break;
                case "quaternaryIndependentLastYearsSellings":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new DoubleFieldValidator(ValidatorUtil.OCUPATION_LAST_YEAR_SELLINGS, Double.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalLastYearSellings(personId, 4, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 4, true);
                    break;
                case "quaternaryIndependentExerciseOutcome":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new DoubleFieldValidator(ValidatorUtil.OCUPATION_LAST_YEAR_SELLINGS, Double.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalExerciseOutcome(personId, 4, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 4, true);
                    break;
                case "quaternaryIndependentSalesPercentageFixedCosts":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.OCUPATION_SALES_PERCENTAGE, Integer.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalSalesPercentageFixedCosts(personId, 4, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 4, true);
                    break;
                case "quaternarySalesPercentageVariableCosts":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.OCUPATION_SALES_PERCENTAGE, Integer.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalSalesPercentageVariableCosts(personId, 4, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 4, true);
                    break;

                case "quaternaryIndependentSalesPercentajeBestClient":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.OCUPATION_SALES_PERCENTAGE, Integer.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalSalesPercentageBestClient(personId, 4, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 4, true);
                    break;
                case "quaternaryIndependentDailyIncome":
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new DoubleFieldValidator(ValidatorUtil.OCUPATION_DAILY_INCOME, Double.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalAverageDailyIncome(personId, 4, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 4, true);
                    break;
                case "quaternaryIndependentLastYearCompensation":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new DoubleFieldValidator(ValidatorUtil.OCUPATION_LAST_YEAR_SELLINGS, Double.valueOf(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOcupatinalLastYearCompensation(personId, 4, Double.valueOf(value));
                    personDao.validateOcupationalInformation(personId, false);
                    personDao.updateOcupationalInformationBoChanged(personId, 4, true);
                    break;

                case "otherActivityIncome":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.OTHER_INCOME, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateOtherActivityTypeIncome(personId, Integer.parseInt(value));
                    // Invalidate new ocupational information
                    personDao.validateOcupationalInformation(personId, false);
                    break;
                case "bankAccount":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:bankInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.BANK_ACCOUNT_NUMBER, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updatePersonAccountByCredit(personId, creditId, null, value, null, null);
                    personDao.updatebankAccountVerified(false, personId);
                    //If we need to update the personal informacion as well, uncomment next line
                    //personDao.updatePersonBankAccountInformation(personId, null, null, value, null, null);
                    break;
                case "cciCode":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:bankInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    Integer countryId = personDao.getPerson(catalogService, locale, personId, false).getDocumentType().getCountryId();
                    switch (countryId) {
                        case CountryParam.COUNTRY_PERU:
                            validator = new StringFieldValidator(ValidatorUtil.BANK_CCI_NUMBER, value);
                            if (!validator.validate(locale)) {
                                return AjaxResponse.errorMessage(validator.getErrors());
                            }

                            personDao.updatePersonAccountByCredit(personId, creditId, null, null, null, value);
                            break;
                        case CountryParam.COUNTRY_ARGENTINA:
                            validator = new StringFieldValidator(ValidatorUtil.BANK_CBU_NUMBER, value);
                            if (!validator.validate(locale)) {
                                return AjaxResponse.errorMessage(validator.getErrors());
                            }
                            PersonBankAccountInformation bankAccountInformation = personDao.getPersonBankAccountInformation(locale, personId);
                            try {
                                if (!BankAccountValidator.validateCBU(value, catalogService.getBanks(0, true, countryId), bankAccountInformation.getBank())) {
                                    return AjaxResponse.errorMessage("CBU inválido");
                                }
                            } catch (Exception e) {
                                return AjaxResponse.errorMessage("CBU inválido");
                            }


                            personDao.updatePersonAccountByCredit(personId, creditId, null, value.substring(8, 21), null, value);
                            break;
                    }
                    personDao.updatebankAccountVerified(false, personId);
                    //If we need to update the personal informacion as well, uncomment next line
                    //personDao.updatePersonBankAccountInformation(personId, null, null, null, null, value);
                    break;
                case "bankDepartment":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:bankInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.DEPARTMENT, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updatePersonBankAccountInformation(personId, null, null, null, value + "0000", null);
                    break;
                case "bank":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:bankInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new IntegerFieldValidator(ValidatorUtil.BANK_ID, Integer.parseInt(value));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updatePersonAccountByCredit(personId, creditId, Integer.parseInt(value), null, null, null);
                    //If we need to update the personal informacion as well, uncomment next line
                    personDao.updatePersonBankAccountInformation(personId, Integer.parseInt(value), null, null, null, null);
                    personDao.updatebankAccountVerified(false, personId);
                    break;
                case "bankAccountType":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:bankInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new CharFieldValidator(ValidatorUtil.BANK_ACCOUNT_TYPE, value.charAt(0));
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updatePersonBankAccountInformation(personId, null, value.charAt(0), null, null, null);
                    personDao.updatebankAccountVerified(false, personId);
                    break;
                case "branchOfficeCode":
                    // Validate the permission
                    if (!SecurityUtils.getSubject().isPermitted("person:bankInformation:update")) {
                        return AjaxResponse.errorForbidden();
                    }
                    validator = new StringFieldValidator(ValidatorUtil.BANK_SUBSIDIARI_NUMBER, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDao.updateBranchOfficeCode(personId, value);
                    break;
            }
            return AjaxResponse.ok(null);
        }
    }

    @RequestMapping(value = "/person/{personId}/validateOcupationalInformation", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "person:ocupationalInformation:validate", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> validateOcupationalInformation(
            ModelMap model, Locale locale,
            @PathVariable("personId") Integer personId,
            @RequestParam("validated") Boolean validated) throws Exception {

        // Validate that have net income
        List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(locale, personId);
        if (ocupations == null) {
            return AjaxResponse.errorMessage("Debe tener al menos una ocupación");
        }
        PersonOcupationalInformation ocuFix = ocupations.stream().filter(o -> o.getFixedGrossIncome() != null).findAny().orElse(null);
        if (ocuFix == null) {
            return AjaxResponse.errorMessage("Debe tener al menos una ocupación con ingreso bruto");
        }
        personDao.validateOcupationalInformation(personId, validated);

        if (validated) {
            //Only recreate offers if any ocupational information was changed in BO
            if (ocupations.stream().anyMatch(o -> o.getBackofficeChanged())) {

                boolean recreatedOffers = false;

                // Recreate the offers of the LoanApplications that are WaitingForApproval
                List<LoanApplicationBoPainter> loans = loanApplicationDao.getLoanApplicationsByPerson(locale, personId, LoanApplicationBoPainter.class);
                if (loans != null) {
                    for (LoanApplicationBoPainter loan : loans) {
                        if (loan.getStatus().getId() == LoanApplicationStatus.WAITING_APPROVAL) {
                            if (loan.getProduct() != null && loan.getProduct().getId() == Product.AUTOS) {
                                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loan.getId(), locale);

                                // Update Acceso Expediente
                                Person person = personDao.getPerson(catalogService, locale, personId, false);
                                User user = userDao.getUser(loanApplication.getUserId());
                                PersonOcupationalInformation principalOcupation = personDao.getPersonOcupationalInformation(locale, person.getId())
                                        .stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL)
                                        .findFirst().orElse(null);
                                PersonContactInformation contactInformation = personDao.getPersonContactInformation(locale, person.getId());
                                Expediente expedienteToUpdate = new Expediente(translatorDao, person, user, principalOcupation, contactInformation);
                                expedienteToUpdate.setNroExpediente(Integer.parseInt(loanApplication.getEntityApplicationCode()));
                                accesoServiceCall.callCrearExpediente(expedienteToUpdate, loanApplication.getId(), 0);

                                // Rereate acceso offers only if the current queryBot (if exists) is not running
                                QueryBot offersQueryBot = null;
                                if (loanApplication.getOfferQueryBotId() != null)
                                    offersQueryBot = botDao.getQueryBot(loanApplication.getOfferQueryBotId());
                                if (offersQueryBot == null || (offersQueryBot.getStatusId() != QueryBot.STATUS_RUNNING && offersQueryBot.getStatusId() != QueryBot.STATUS_QUEUE)) {
                                    int queryBotId = webscrapperService.callCreateAccesoOffers(loanApplication.getId(), loanApplication.getDownPayment() != null ? loanApplication.getDownPayment() : 0.0);
                                    loanApplicationDao.updateOffersQueryBotId(loanApplication.getId(), queryBotId);
                                }

                            } else {
                                LoanApplicationEvaluation evaluation = loanApplicationService.getLastEvaluation(loan.getId(), loan.getPersonId(), locale, true);
                                if (evaluation != null) {
                                    loanApplicationDao.createLoanOffers(loan.getId());
                                }
                            }
                            recreatedOffers = true;
                        }
                    }
                }

                for (PersonOcupationalInformation ocu : ocupations)
                    personDao.updateOcupationalInformationBoChanged(personId, ocu.getNumber(), false);

                if (recreatedOffers) {
                    JSONObject jsonrResponse = new JSONObject();
                    jsonrResponse.put("updateApplications", true);
                    return AjaxResponse.ok(jsonrResponse.toString());
                }
            }
        }

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/interaction/{personInteractionId}/resend", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "person:interaction:resend", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> resendPersonInteraction(
            ModelMap model, Locale locale,
            @PathVariable("personInteractionId") Integer personInteractionId) throws Exception {

        PersonInteraction interaction = interactionDao.getPersonInteractionById(personInteractionId, locale);
        interactionService.resendPersonInteraction(interaction);
        if (interaction.getSent()) {
            return AjaxResponse.ok(null);
        } else {
            return AjaxResponse.errorMessage("La interaccion no ha podido ser reenviada");
        }
    }

    @RequestMapping(value = "/referral/{field}", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> updateReferralProperty(
            ModelMap model, Locale locale,
            @PathVariable("field") String field,
            @RequestParam("referralId") Integer referralId,
            @RequestParam(value = "value", required = false) String value) throws Exception {

        FieldValidator validator;
        switch (field) {
            case "fullName":
                if (!SecurityUtils.getSubject().isPermitted("person:contactInformation:referralInformation:update")) {
                    return AjaxResponse.errorForbidden();
                }
                validator = new StringFieldValidator(ValidatorUtil.NAME, value).setRequired(false);
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                personDao.updateReferralFulName(referralId, value);
                break;
            case "relationship":
                if (!SecurityUtils.getSubject().isPermitted("person:contactInformation:referralInformation:update")) {
                    return AjaxResponse.errorForbidden();
                }
                Integer id = null;
                try {
                    id = Integer.parseInt(value);
                } catch (Exception e) {
                }
                if (id != null) {
                    validator = new IntegerFieldValidator(ValidatorUtil.BANK_ID, id).setRequired(false);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                }
                personDao.updateReferralRelationship(referralId, Integer.parseInt(value));
                break;
            case "phoneType":
                if (!SecurityUtils.getSubject().isPermitted("person:contactInformation:referralInformation:update")) {
                    return AjaxResponse.errorForbidden();
                }
                personDao.updateReferralPhoneType(referralId, value);
                break;
            case "phoneNumber":
                if (!SecurityUtils.getSubject().isPermitted("person:contactInformation:referralInformation:update")) {
                    return AjaxResponse.errorForbidden();
                }
                validator = new StringFieldValidator(ValidatorUtil.BO_PHONE_NUMBER, value).setRequired(false);
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                personDao.updateReferralPhoneNumber(referralId, value);
                break;
            case "info":
                if (!SecurityUtils.getSubject().isPermitted("person:contactInformation:referralInformation:update")) {
                    return AjaxResponse.errorForbidden();
                }
                validator = new StringFieldValidator(ValidatorUtil.NAME, value).setValidRegex(StringFieldValidator.REGEXP_ALPHANUMERIC_SPEC_CHARS).setMaxCharacters(200).setRequired(false);
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                personDao.updateReferralInfo(referralId, value);
                break;
            case "validated":
                if (!SecurityUtils.getSubject().isPermitted("person:contactInformation:referralInformation:validate")) {
                    return AjaxResponse.errorForbidden();
                }
                personDao.updateReferralValidated(referralId, Boolean.parseBoolean(value));
                break;
        }
        model.addAttribute("onlyView", false);
        return AjaxResponse.ok("");
    }

    @RequestMapping(value = "/referral/create", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "person:contactInformation:referralInformation:create", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> createReferral(
            ModelMap model, Locale locale,
            @RequestParam("personId") Integer personId,
            @RequestParam("fullName") String fullName,
            @RequestParam("relationshipId") Integer relationshipId,
            @RequestParam(value = "phoneType", required = false) String phoneType,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("info") String info) throws Exception {

        Referral referral = personDao.createReferral(personId, phoneType, fullName, relationshipId, "51", phoneNumber, info, locale);
        return AjaxResponse.ok(new Gson().toJson(referral));
    }

    @RequestMapping(value = "/person/address/update/modal", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "person:contactInformation:addressInformation:update", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getPersonAddressUpdateModal(
            ModelMap model, Locale locale,
            @RequestParam("personId") int personId) throws Exception {

        Person person = personDao.getPerson(catalogService, locale, personId, false);

        UpdatePersonAddressForm form = new UpdatePersonAddressForm();
        ((UpdatePersonAddressForm.Validator) form.getValidator()).setCountryId(person.getDocumentType().getCountryId(), form.getWithoutNumber());

        PersonContactInformation personContact = personDao.getPersonContactInformation(locale, personId);

        Direccion disagregatedAddress = personDao.getDisggregatedAddress(personId, "H");
        if (disagregatedAddress != null) {
            String department = null;
            String province = null;
            String district = null;
            String postalCode = null;
            if (person.getDocumentType().getCountryId() == CountryParam.COUNTRY_PERU) {
                Ubigeo ubigeo = catalogService.getUbigeo(disagregatedAddress.getUbigeo());
                department = ubigeo.getDepartment().getId();
                province = ubigeo.getProvince().getId();
                district = ubigeo.getDistrict().getId();
            } else if (person.getDocumentType().getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
                District generalDistrict = catalogService.getGeneralDistrictById(disagregatedAddress.getLocalityId());
                province = generalDistrict.getProvince().getProvinceId() + "";
                district = generalDistrict.getDistrictId() + "";
                postalCode = generalDistrict.getPostalCode();
            }

            form.setDepartamento(department);
            form.setProvincia(province);
            form.setDistrito(district);
            form.setPostalCode(postalCode);
            form.setRoadType(disagregatedAddress.getTipoVia() + "");
            form.setRoadName(disagregatedAddress.getNombreVia());
            form.setWithoutNumber(disagregatedAddress.getWithoutNumber());
            form.setFloor(disagregatedAddress.getFloor());
            form.setHouseNumber(disagregatedAddress.getNumeroVia());
            form.setInterior(disagregatedAddress.getNumeroInterior());
            form.setManzana(disagregatedAddress.getManzana());
            form.setLote(disagregatedAddress.getLote());
            form.setReference(disagregatedAddress.getReferencia());
            form.setZoneType(disagregatedAddress.getTipoZona() + "");
            form.setZoneName(disagregatedAddress.getNombreZona());
            personDao.completeAddressCoordinates(personId, disagregatedAddress);
            form.setSearchQuery(disagregatedAddress.getSearchQuery());
        }

        if (personContact != null) {
            form.setLatitude(personContact.getAddressLatitude());
            form.setLongitude(personContact.getAddressLongitude());
            form.setAggregatedAddress(personContact.getAddressStreetName());
            if (person.getDocumentType().getCountryId() == CountryParam.COUNTRY_PERU) {
                if (personContact.getAddressUbigeo() != null) {
                    form.setDepartamento(form.getDepartamento() != null ? form.getDepartamento() : personContact.getAddressUbigeo().getDepartment().getId());
                    form.setProvincia(form.getProvincia() != null ? form.getProvincia() : personContact.getAddressUbigeo().getProvince().getId());
                    form.setDistrito(form.getDistrito() != null ? form.getDistrito() : personContact.getAddressUbigeo().getDistrict().getId());
                }
            } else if (person.getDocumentType().getCountryId() == CountryParam.COUNTRY_COLOMBIA) {
                form.setDepartamento(form.getDepartamento() != null ? form.getDepartamento() : personContact.getDepartment().getDepartmentId().toString());
                form.setProvincia(form.getProvincia() != null ? form.getProvincia() : personContact.getProvince().getProvinceId().toString());
                form.setDistrito(form.getDistrito() != null ? form.getDistrito() : personContact.getDistrict().getDistrictId().toString());
            }
        }
        form.setAddressType("H");

        model.addAttribute("person", person);
        model.addAttribute("personId", personId);
        model.addAttribute("updatePersonAddressForm", form);
        return new ModelAndView("fragments/personFragments :: updatePersonAddressModal");
    }

    @RequestMapping(value = "/person/jobaddress/update/modal", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "person:contactInformation:addressInformation:update", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getPersonJobAddressUpdateModal(
            ModelMap model, Locale locale,
            @RequestParam("personId") int personId) throws Exception {

        Person person = personDao.getPerson(catalogService, locale, personId, false);

        UpdatePersonAddressForm form = new UpdatePersonAddressForm();
        ((UpdatePersonAddressForm.Validator) form.getValidator()).setCountryId(person.getDocumentType().getCountryId(), form.getWithoutNumber());

        Direccion disagregatedAddress = personDao.getDisggregatedAddress(personId, "J");

        PersonOcupationalInformation principalOcupation = personDao.getPersonOcupationalInformation(locale, person.getId())
                .stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL)
                .findFirst().orElse(null);

        if (disagregatedAddress != null) {
            String department = null;
            String province = null;
            String district = null;
            String postalCode = null;
            if (person.getDocumentType().getCountryId() == CountryParam.COUNTRY_PERU) {
                Ubigeo ubigeo = catalogService.getUbigeo(disagregatedAddress.getUbigeo());
                department = ubigeo.getDepartment().getId();
                province = ubigeo.getProvince().getId();
                district = ubigeo.getDistrict().getId();
            } else if (person.getDocumentType().getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
                District generalDistrict = catalogService.getGeneralDistrictById(disagregatedAddress.getLocalityId());
                province = generalDistrict.getProvince().getProvinceId() + "";
                district = generalDistrict.getDistrictId() + "";
                postalCode = generalDistrict.getPostalCode();
            }

            form.setDepartamento(department);
            form.setProvincia(province);
            form.setDistrito(district);
            form.setPostalCode(postalCode);
            form.setRoadType(disagregatedAddress.getTipoVia() + "");
            form.setRoadName(disagregatedAddress.getNombreVia());
            form.setWithoutNumber(disagregatedAddress.getWithoutNumber());
            form.setFloor(disagregatedAddress.getFloor());
            form.setHouseNumber(disagregatedAddress.getNumeroVia());
            form.setInterior(disagregatedAddress.getNumeroInterior());
            form.setManzana(disagregatedAddress.getManzana());
            form.setLote(disagregatedAddress.getLote());
            form.setReference(disagregatedAddress.getReferencia());
            form.setZoneType(disagregatedAddress.getTipoZona() + "");
            form.setZoneName(disagregatedAddress.getNombreZona());
            personDao.completeAddressCoordinates(personId, disagregatedAddress);

            form.setSearchQuery(principalOcupation.getSearchQuery());
        }

        if (principalOcupation != null) {
            form.setLatitude(principalOcupation.getAddressLatitude());
            form.setLongitude(principalOcupation.getAddressLongitude());
            form.setAggregatedAddress(principalOcupation.getAddress());
            // TODO Putthe ubigeo of the work address
            if (person.getDocumentType().getCountryId() == CountryParam.COUNTRY_PERU) {
                if (principalOcupation.getAddressUbigeo() != null) {
                    form.setDepartamento(form.getDepartamento() != null ? form.getDepartamento() : principalOcupation.getAddressUbigeo().getDepartment().getId());
                    form.setProvincia(form.getProvincia() != null ? form.getProvincia() : principalOcupation.getAddressUbigeo().getProvince().getId());
                    form.setDistrito(form.getDistrito() != null ? form.getDistrito() : principalOcupation.getAddressUbigeo().getDistrict().getId());
                }
            } else if (person.getDocumentType().getCountryId() == CountryParam.COUNTRY_COLOMBIA) {
                if (principalOcupation.getDistrict() != null) {
                    form.setDepartamento(form.getDepartamento() != null ? form.getDepartamento() : principalOcupation.getDistrict().getProvince().getDepartment().getDepartmentId().toString());
                    form.setProvincia(form.getProvincia() != null ? form.getProvincia() : principalOcupation.getDistrict().getProvince().getProvinceId().toString());
                    form.setDistrito(form.getDistrito() != null ? form.getDistrito() : principalOcupation.getDistrict().getDistrictId().toString());
                }
            }
        }
        form.setAddressType("J");

        model.addAttribute("person", person);
        model.addAttribute("personId", personId);
        model.addAttribute("updatePersonAddressForm", form);
        return new ModelAndView("fragments/personFragments :: updatePersonAddressModal");
    }

    @RequestMapping(value = "/person/job-phone/update/modal", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "person:ocupationalInformation:update", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getPersonJobPhoneUpdateModal(
            ModelMap model, Locale locale,
            @RequestParam("personId") int personId) throws Exception {

        Person person = personDao.getPerson(catalogService, locale, personId, false);

        Question28Form form = new Question28Form();
        ((Question28Form.Validator) form.getValidator()).configValidator(person.getDocumentType().getCountryId());

        PersonOcupationalInformation principalOccupation = personDao.getPersonOcupationalInformation(locale, person.getId())
                .stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL)
                .findFirst().orElse(null);

        if (principalOccupation != null) {
            form.setPhoneCode(principalOccupation.getPhoneCode());
            form.setPhoneNumber(principalOccupation.getPhoneNumberWithoutCode());
            form.setTypePhone(principalOccupation.getPhoneNumberType());
        }

        model.addAttribute("person", person);
        model.addAttribute("personId", personId);
        model.addAttribute("occupationalPhoneNumberForm", form);
        return new ModelAndView("fragments/personFragments :: updateOccupationalPhoneNumberModal");
    }

    @RequestMapping(value = "/person/retryEquifax", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> retryEquifax(
            ModelMap model, Locale locale,
            @RequestParam("personId") Integer personId) throws Exception {
        Optional<LoanApplication> oLaTradi = Optional.ofNullable(loanApplicationDao.getActiveLoanApplicationByPerson(locale, personId, Product.TRADITIONAL));
        Optional<LoanApplication> oLaShort = Optional.ofNullable(loanApplicationDao.getActiveLoanApplicationByPerson(locale, personId, Product.SHORT_TERM));
        Optional<LoanApplication> oLaAutos = Optional.ofNullable(loanApplicationDao.getActiveLoanApplicationByPerson(locale, personId, Product.AUTOS));
        Optional<LoanApplication> oLaInsurance = Optional.ofNullable(loanApplicationDao.getActiveLoanApplicationByPerson(locale, personId, Product.INSURANCE));
        Optional<LoanApplication> oLaConsolidation = Optional.ofNullable(loanApplicationDao.getActiveLoanApplicationByPerson(locale, personId, Product.DEBT_CONSOLIDATION));

        boolean eqTradi = oLaTradi.isPresent() &&
                (oLaTradi.map(x -> x.getStatus().getId()).map(x ->
                        x == LoanApplicationStatus.EVAL_APPROVED ||
                                x == LoanApplicationStatus.WAITING_APPROVAL).orElse(false));
        boolean eqShort = oLaShort.isPresent() &&
                (oLaShort.map(x -> x.getStatus().getId()).map(x ->
                        x == LoanApplicationStatus.EVAL_APPROVED ||
                                x == LoanApplicationStatus.WAITING_APPROVAL).orElse(false));
        boolean eqAutos = oLaAutos.isPresent() &&
                (oLaAutos.map(x -> x.getStatus().getId()).map(x ->
                        x == LoanApplicationStatus.EVAL_APPROVED ||
                                x == LoanApplicationStatus.WAITING_APPROVAL).orElse(false));
        boolean eqInsurance = oLaInsurance.isPresent() &&
                (oLaInsurance.map(x -> x.getStatus().getId()).map(x ->
                        x == LoanApplicationStatus.EVAL_APPROVED ||
                                x == LoanApplicationStatus.WAITING_APPROVAL).orElse(false));
        boolean eqConsolidation = oLaConsolidation.isPresent() &&
                (oLaConsolidation.map(x -> x.getStatus().getId()).map(x ->
                        x == LoanApplicationStatus.EVAL_APPROVED ||
                                x == LoanApplicationStatus.WAITING_APPROVAL).orElse(false));

        List<Boolean> shouldRunEquifaxList = Arrays.asList(eqTradi, eqShort, eqAutos, eqInsurance, eqConsolidation);
        List<Optional<LoanApplication>> oLaList = Arrays.asList(oLaTradi, oLaShort, oLaAutos, oLaInsurance, oLaConsolidation);

        if (shouldRunEquifaxList.size() != oLaList.size()) {
            throw new RuntimeException("This lists should be equal in length: shouldRunEquifaxList and oLaList");
        }

        for (int i = 0; i < shouldRunEquifaxList.size(); i++) {
            if (shouldRunEquifaxList.get(i)) {
                int loanapp_id = oLaList.get(i).get().getId();
                int evaluation_id = loanApplicationService.getLastEvaluation(loanapp_id, personId, locale, true).getId();

                Person person = personDao.getPerson(catalogService, locale, personId, false);
                webscrapperService.runEquifax(evaluation_id, loanapp_id, person.getDocumentType().getId(), person.getDocumentNumber());

                LoanApplication laAfterEfx = loanApplicationDao.getLoanApplication(loanapp_id, locale);
                if (laAfterEfx != null && laAfterEfx.getStatus().getId() == LoanApplicationStatus.REJECTED_AUTOMATIC) {
                    emailBoService.sendRejectionMail(laAfterEfx, locale);
                }
                return AjaxResponse.ok(null);
            }
        }
        return AjaxResponse.ok(null);
    }


    @RequestMapping(value = "/person/verifyBankAccount", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> verifyBankAccount(
            ModelMap model, Locale locale,
            @RequestParam("personId") Integer personId,
            @RequestParam("verified") Boolean verified) throws Exception {

        personDao.updatebankAccountVerified(verified, personId);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/person/viewextranet", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "person:extranet:viewAsClient", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object viewUserExtranet(
            ModelMap model, Locale locale,
            @RequestParam("userId") int userId) throws Exception {

        // Open the no auth link for 5 seconds
        userDao.registerNoAuthExtranetLinkExpiration(userId, 5);

        // Redirect to the client
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("user", userId);
        jsonParam.put("sysUser", sysUserService.getSessionSysuser().getId());
        return "redirect:" + Configuration.getClientDomain() + "/extranetphantomlogin/" + CryptoUtil.encrypt(jsonParam.toString());
    }

    @RequestMapping(value = "/externalView/person/{token}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "person:view:externalView", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getPersonViewScreen(
            ModelMap model, Locale locale,
            @PathVariable("token") String token) throws Exception {
        JSONObject jsonToken = new JSONObject(CryptoUtil.decrypt(token));
        int personId = JsonUtil.getIntFromJson(jsonToken, "personId", null);
        String panel = JsonUtil.getStringFromJson(jsonToken, "panel", null);

        Person person = personDao.getPerson(catalogService, locale, personId, true);
        locale = LocaleUtils.toLocale(catalogService.getCountryParam(person.getDocumentType().getCountryId()).getLocale());

        model.addAttribute("person", person);
        model.addAttribute("personCountry", person.getCountry().getId());
        model.addAttribute("onlyView", true);

        switch (panel) {
            case "partner":
                if (person.getPartner() != null) {
                    person.getPartner().setNegativeBase(personDao.getPersonNegativeBase(person.getPartner().getId()));
                    person.getPartner().setHasDebt(personDao.getPersonHasDebt(person.getPartner().getId()));
                }
                return "person :: #personPartnerPanel";
            case "contact":
                model.addAttribute("personContactInfo", personDao.getPersonContactInformation(locale, personId));
                if (person.getPartner() != null) {
                    person.getPartner().setNegativeBase(personDao.getPersonNegativeBase(person.getPartner().getId()));
                    person.getPartner().setHasDebt(personDao.getPersonHasDebt(person.getPartner().getId()));
                }

                List<Referral> referrals = personDao.getReferrals(personId, locale);
                if (referrals != null) {
                    model.addAttribute("personReferrals", referrals.stream().filter(r -> r.getValidated()).collect(Collectors.toList()));
                }
                return "person :: #personContactPanel";
            case "income":
                List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(locale, personId);
                if (ocupations != null) {
                    for (PersonOcupationalInformation o : ocupations) {
                        if (o.getNumber() == PersonOcupationalInformation.PRINCIPAL) {
                            model.addAttribute("personPrincipalOcupationalInfo", o);
                        } else if (o.getNumber() == PersonOcupationalInformation.SECUNDARY) {
                            model.addAttribute("personSecundaryOcupationalInfo", o);
                        } else if (o.getNumber() == PersonOcupationalInformation.OTHER) {
                            model.addAttribute("personOtherOcupationalInfo", o);
                        }
                    }
                }
                model.addAttribute("employee", personDao.getEmployeeByPerson(personId, locale));

                NosisResult nosisResult = personDao.getBureauResult(personId, NosisResult.class);
                ParteXML nosisXml = (nosisResult != null) ? nosisResult.getParteXML() : null;
                model.addAttribute("nosisXml", nosisXml);

                return "person :: #personIncomePanel";
            default:
            case "client":
                model.addAttribute("facebook", userDao.getUserFacebook(person.getUserId()));
                model.addAttribute("linkedin", userDao.getLinkedin(personId));
                model.addAttribute("lastSessionLog", userDao.getLastSessionLog(person.getUserId()));
                return "person :: #personClientPanel";
        }

    }

    private void convertCountry(List<LoanApplicationFraudAlert> loanApplicationFraudAlerts) {

        List<LoanApplicationFraudAlert> rucRelatedArray = null;
        List<LoanApplicationFraudAlert> dniRelatedArray = null;
        List<LoanApplicationFraudAlert> cciRelatedArray = null;

        rucRelatedArray = loanApplicationFraudAlerts.stream().filter(p -> Arrays.asList(FraudAlert.RUC_RELATED).contains(p.getFraudAlert().getFraudAlertId())).collect(Collectors.toList());
        dniRelatedArray = loanApplicationFraudAlerts.stream().filter(p -> Arrays.asList(FraudAlert.DNI_RELATED).contains(p.getFraudAlert().getFraudAlertId())).collect(Collectors.toList());
        cciRelatedArray = loanApplicationFraudAlerts.stream().filter(p -> Arrays.asList(FraudAlert.CCI_RELATED).contains(p.getFraudAlert().getFraudAlertId())).collect(Collectors.toList());

        if (rucRelatedArray != null) {
            for (LoanApplicationFraudAlert rucRelated : rucRelatedArray) {
                rucRelated.getFraudAlert().setFraudAlertDescription(rucRelated.getFraudAlert().getFraudAlertDescription().replace("RUC", "CUIT"));
                rucRelated.getFraudAlert().setFraudAlertElemnt(rucRelated.getFraudAlert().getFraudAlertElemnt().replace("RUC", "CUIT"));
            }
        }

        if (dniRelatedArray != null) {
            for (LoanApplicationFraudAlert dniRelated : dniRelatedArray) {
                dniRelated.getFraudAlert().setFraudAlertDescription(dniRelated.getFraudAlert().getFraudAlertDescription().replace("DNI", "CUIT"));
                dniRelated.getFraudAlert().setFraudAlertElemnt(dniRelated.getFraudAlert().getFraudAlertElemnt().replace("DNI", "CUIT"));
            }
        }

        if (cciRelatedArray != null) {
            for (LoanApplicationFraudAlert cciRelated : cciRelatedArray) {
                cciRelated.getFraudAlert().setFraudAlertDescription(cciRelated.getFraudAlert().getFraudAlertDescription().replace("CCI", "CBU"));
                cciRelated.getFraudAlert().setFraudAlertElemnt(cciRelated.getFraudAlert().getFraudAlertElemnt().replace("CCI", "CBU"));
            }
        }
    }

    private String validateWithAfip(PersonOcupationalInformation personalOcupation, AfipResult afipResult, Locale locale) throws ParseException {
        String validate = "NO";
        if (personalOcupation.getActivityType().getId() == ActivityType.MONOTRIBUTISTA && afipResult != null) {
            if (afipResult.getMonotributistaStartDate() != null) {
                LocalDate startDateAfip = new SimpleDateFormat("MM/yyyy").parse(afipResult.getMonotributistaStartDate()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                long employmentTimeAfip = ChronoUnit.MONTHS.between(startDateAfip, LocalDate.now());
                if (personalOcupation.getEmploymentTime() != null && String.valueOf(employmentTimeAfip) != null)
                    validate = (personalOcupation.getEmploymentTime().equals(String.valueOf(employmentTimeAfip)) ? "SI" : "NO");
            }
        } else if (personalOcupation.getActivityType().getId() == ActivityType.INDEPENDENT && afipResult != null) {
            if (afipResult.getAutonomoStartDate() != null) {
                LocalDate startDateAfip = new SimpleDateFormat("MM/yyyy").parse(afipResult.getAutonomoStartDate()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                long employmentTimeAfip = ChronoUnit.MONTHS.between(startDateAfip, LocalDate.now());
                if (personalOcupation.getEmploymentTime() != null && String.valueOf(employmentTimeAfip) != null)
                    validate = (personalOcupation.getEmploymentTime().equals(String.valueOf(employmentTimeAfip)) ? "SI" : "NO");
            }
        }
        return validate;
    }


    @RequestMapping(value = "/person/dismissObservation", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object observeCredit(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("creditId") Integer creditId) throws Exception {

        creditDAO.updateObservation(creditId, null);
        Credit credit = creditDAO.getCreditByID(creditId, locale, false, Credit.class);
        String message = "Credito :" + credit.getCode() + " ha sido subsanado";

        awsSesEmailService.sendEmail(
                Configuration.EMAIL_CONTACT_FROM()
                , "cs@solven.pe"
                , null
                , "¡" + message + "!"
                , message + " satisfactoriamente!"
                , null
                , null);
        return AjaxResponse.ok("Observacion subsanada correctamente");
    }

//    TODO REFACTOR
    @RequestMapping(value = "/person/reject/loanApplicationFraudAlert", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getFraudAlertsFragment(HttpServletRequest request, ModelMap model, Locale locale, HttpServletResponse response, @RequestParam(name = "loanApplicationId", required = true) Integer loanApplicationId, @RequestParam(name = "showActions", required = true) Boolean showActions) throws Exception {
        LoanApplication loanApplication = loanApplicationDao.getLoanApplicationLite(loanApplicationId, locale);
        List<LoanApplicationFraudAlert> loanApplicationFraudAlerts = getPersonFraudAlertsByApplication(loanApplication, false);
        LoanApplicationBoPainter loanApplicationBoPainter = applicationPainterService.getApplicationById(loanApplicationId, locale, request);

        if (loanApplication.getFraudAlertQueryIds() != null) {
            Integer lastExecutedBot = loanApplication.getFraudAlertQueryIds().stream().sorted(Comparator.reverseOrder()).findFirst().orElse(null);
            if (lastExecutedBot != null) {
                QueryBot queryBot = botDao.getQueryBot(lastExecutedBot);
                if (queryBot != null) {
                    loanApplicationBoPainter.setLastFraudAlertsBotRegisterDate(queryBot.getRegisterDate());
                    loanApplicationBoPainter.setLastFraudAlertsBotStatusId(queryBot.getStatusId());
                }
            }
        }

        model.addAttribute("loanApplicationFraudAlerts", loanApplicationFraudAlerts);
        model.addAttribute("showActions", showActions);
        model.addAttribute("loanApplication", loanApplicationBoPainter);

        return "fragments/personFragmentFraudAlert :: tab-fraud-alerts-body";
    }

    @RequestMapping(value = "/person/reject/loanApplicationFraudLogAlert", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getFraudLogAlertsFragment(HttpServletRequest request, ModelMap model, Locale locale, HttpServletResponse response, @RequestParam(name = "loanApplicationId", required = true) Integer loanApplicationId, @RequestParam(name = "showActions", required = true) Boolean showActions) throws Exception {
        LoanApplication loanApplication = loanApplicationDao.getLoanApplicationLite(loanApplicationId, locale);
        List<LoanApplicationFraudAlert> loanApplicationFraudAlerts = getPersonFraudAlertsByApplication(loanApplication, true);

        model.addAttribute("loanApplicationFraudAlerts", loanApplicationFraudAlerts);
        model.addAttribute("showActions", showActions);
        model.addAttribute("loanApplication", applicationPainterService.getApplicationById(loanApplicationId, locale, request));

        return "fragments/personFragmentFraudLogAlert :: tab-fraud-log-alerts-body";
    }

    @RequestMapping(value = "/person/disqualifier", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getDisqualifierFragment(HttpServletRequest request, ModelMap model, Locale locale, HttpServletResponse response, @RequestParam(name = "loanApplicationId", required = true) Integer loanApplicationId, @RequestParam(name = "onlyView", required = true) Boolean onlyView) throws Exception {
        LoanApplication loanApplication = loanApplicationDao.getLoanApplicationLite(loanApplicationId, locale);
        Map<String, PersonDisqualifier> disqualifiers = personService.getPersonDisqualifierMap(request, loanApplication.getPersonId());
        model.addAttribute("loanApplication", loanApplication);
        model.addAttribute("onlyView", onlyView);
        model.addAttribute("disqualifiers", disqualifiers);
        return "fragments/personFragmentDisqualifier :: tab-disqualifier-body";
    }

    @RequestMapping(value = "/person/disqualifier", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object personDisqualifiers(HttpServletRequest request, HttpServletResponse response, @RequestParam("data") String jsonArrayStr, @RequestParam(name = "pepImage", required = false) MultipartFile pepImage, @RequestParam(name = "ofacImage", required = false) MultipartFile ofacImage) throws Exception {

        JSONArray jsonArray = new JSONArray(jsonArrayStr);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            PersonDisqualifier disqualifer = new PersonDisqualifier();
            disqualifer.setDetail(obj.getString("detail"));
            disqualifer.setDisqualified(obj.getBoolean("isDisqualified"));
            disqualifer.setType(obj.getString("type"));
            disqualifer.setPersonId(obj.getInt("person_id"));
            Integer loanAppid = obj.getInt("loan_app_id");
            MultipartFile file = null;
            if (obj.getString("type").equals("O") && ofacImage != null) {
                file = ofacImage;
            } else if (obj.getString("type").equals("P") && pepImage != null) {
                file = pepImage;
            }

            personService.savePersonDisqualifier(loanAppid, file, disqualifer.getType(), disqualifer.isDisqualified(), disqualifer.getDetail());
        }
        return AjaxResponse.ok("OK");
    }

    private List<LoanApplicationFraudAlert> getPersonFraudAlertsByApplication(LoanApplication application, boolean logAlerts) throws Exception {
        List<LoanApplicationFraudAlert> loanApplicationFraudAlerts = creditDao.getLoanApplicationFraudAlerts(application.getId(), !logAlerts ? FraudAlertStatus.NUEVO : FraudAlertStatus.REVISADO);

        if (loanApplicationFraudAlerts != null) {
            loanApplicationFraudAlerts = loanApplicationFraudAlerts.stream().filter(a -> a.getActive() != null && a.getActive()).collect(Collectors.toList());

            if (application.getCountry().getId().equals(CountryParam.COUNTRY_ARGENTINA)) {
                this.convertCountry(loanApplicationFraudAlerts);
            }
        }

        return loanApplicationFraudAlerts;
    }

    @RequestMapping(value = "/person/{personId}/application/{loanApplicationId}/notes", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object insertOrUpdateNote(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @PathVariable("personId") Integer personId,
            @PathVariable("loanApplicationId") Integer loanApplicationId,
            @RequestParam(name = "noteId", required = false) Integer noteId, @RequestParam(name = "operatorId", required = false) Integer operatorId, @RequestParam(name = "message") String message) throws Exception {

        Integer loggedUserId = backofficeService.getLoggedSysuser().getId();
        if (message != null && message.trim().length() > 500) {
            return AjaxResponse.errorMessage("La nota no puede contener más de 500 caracteres");
        } else if (noteId != null && !loggedUserId.equals(operatorId)) {
            return AjaxResponse.errorMessage("Solo puedes editar las notas que haz registrado");
        }

        LoanApplicationBoPainter loanApplicationBoPainter = applicationPainterService.getApplicationById(loanApplicationId, locale, request);
        if(!Arrays.asList(LoanApplicationStatus.WAITING_APPROVAL,
                LoanApplicationStatus.EVAL_APPROVED,
                LoanApplicationStatus.REJECTED,
                LoanApplicationStatus.REJECTED_AUTOMATIC,
                LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION).contains(loanApplicationBoPainter.getStatus().getId())) {
            return AjaxResponse.errorMessage("La solicitud no se encuentra en un estado valido para agregar notas.");
        }

        loanApplicationDao.registerLoanApplicationComment(loanApplicationId, message, loggedUserId, Comment.COMMENT_EVALUATION);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/person/{personId}/application/{loanApplicationId}/{accordion}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "person:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object loadLoanApplicationAccordion(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("personId") Integer personId,
            @PathVariable("loanApplicationId") Integer loanApplicationId,
            @PathVariable("accordion") String accordion) throws Exception {

        if (!SecurityUtils.getSubject().isPermitted("person:loanApplicationTab:view")) {
            return "403";
        }

        LoanApplicationBoPainter loanApplication = applicationPainterService.getApplicationById(loanApplicationId, locale, request);
        String fragmentToRender = null;

        if (loanApplication == null) {
            return AjaxResponse.errorMessage("No existe el Loan Application");
        }

        switch (accordion) {
            case "documentation": {
                List<LoanApplicationUserFiles> userFilesObjectList = personDao.getUserFiles(personId, locale);

                List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(locale, loanApplication.getPersonId());
                if (ocupations != null) {
                    Optional<PersonOcupationalInformation> principalOcupation = ocupations.stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst();
                    principalOcupation.ifPresent(x -> model.addAttribute("personPrincipalOcupationalInfo", x));
                }

                for (LoanApplicationUserFiles loanApplicationUserFiles : userFilesObjectList) {
                    if (loanApplicationUserFiles.getLoanApplicationCode() == null) continue;
                    if (loanApplicationUserFiles.getLoanApplicationCode().equals(loanApplication.getCode())) {
                        loanApplication.setUserFilesObjectList(loanApplicationUserFiles);
                    }
                }

                List<UserFile> userFiles = loanApplicationDao.getLoanApplicationUserFiles(loanApplication.getId());
                HashSet<Integer> userFilesId = new HashSet<>();

                if (userFiles != null) {
                    for (UserFile userFile : userFiles) {
                        userFilesId.add(userFile.getFileType().getId());
                    }
                }

                List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
                if (offers != null) {
                    List<Pair<Integer, Boolean>> pendingDocuments = loanApplicationService.getRequiredDocumentsByLoanApplication(loanApplication).stream().filter(p -> !userFilesId.contains(p.getLeft())).collect(Collectors.toList());
                    if (!pendingDocuments.isEmpty()) {
                        loanApplication.setShowMissingDocumentationButton(true);
                    }
                }

                model.addAttribute("userFilesObject", loanApplication.getUserFilesObjectList());
                model.addAttribute("showMissingDocumentationButton", loanApplication.isShowMissingDocumentationButton());
                model.addAttribute("person", personDao.getPerson(catalogService, Configuration.getDefaultLocale(), personId, false));

                fragmentToRender = "fragments/personFragmentsDocumentation :: tab-documentation-body";

                break;
            }
            case "rekognition": {
                List<RecognitionResultsPainter> recognitions = loanApplicationDao.getRecognitionResults(personId, locale);

                if (recognitions != null) {
                    for (RecognitionResultsPainter recognition : recognitions) {
                        if (recognition.getLoanApplicationId() == null) continue;
                        if (recognition.getLoanApplicationId().equals(loanApplication.getId())) {
                            loanApplication.setRecognition(recognition);
                        }
                    }
                }

                model.addAttribute("result", loanApplication.getRecognition());

                fragmentToRender = "fragments/personFragmentsRekognition :: tab-rekognition-body";

                break;
            }
            case "interactions": {
                List<PersonInteraction> personInteractions = interactionDao.getPersonInteractionsByLoanApplication(personId, loanApplication.getId(), locale);
                List<Object> interactions = personInteractions != null ? personInteractions.stream().filter(pi -> loanApplication.getId().equals(pi.getLoanApplicationId())).collect(Collectors.toList()) : new ArrayList<>();
                UserEmail activeUserEmail = userDao.getUserEmails(loanApplication.getUserId()).stream().filter(u -> u.getActive() != null && u.getActive()).findFirst().orElse(null);

                for (Object interactionObject : interactions) {
                    List<PersonInteractionStat> stats = null;

                    PersonInteraction interaction = (PersonInteraction) interactionObject;

                    if (null != interaction.getInteractionProvider() && interaction.getInteractionProvider().getId() == InteractionProvider.AWS) {
                        Map<String, Date> map = awsElasticSearchClient.getEmailEventsByPersonInteractionId(interaction.getId());

                        stats = new ArrayList<PersonInteractionStat>();
//                        int x = 0;
                        for (Map.Entry<String, Date> entry : map.entrySet()) {
                            PersonInteractionStat stat = new PersonInteractionStat();
                            stat.setEventDate(entry.getValue());
                            stat.setEvent(entry.getKey());
//                            stat.setId(x);
//                            stat.setPersonInteractionId(interaction.getId());
                            stats.add(stat);
//                            x++;
                        }

                        if (map.keySet().contains("Abierto") && activeUserEmail != null && (activeUserEmail.getVerified() != null && !activeUserEmail.getVerified())) {
                            userDao.verifyEmail(loanApplication.getUserId(), activeUserEmail.getId(), true);
                        }

                        interaction.setStats(stats);
                    }
                }


                PersonInteractionPainter painter = new PersonInteractionPainter();
                painter.setLoanApplicationId(loanApplication.getId());
                painter.setLoanApplicationCode(loanApplication.getCode());
                painter.setRegisterDate(loanApplication.getRegisterDate());
                painter.setInteractions(interactions);

                loanApplication.setInteractions(painter);

                model.addAttribute("interaction", loanApplication.getInteractions());
                model.addAttribute("interactionsParam", loanApplication.getInteractions().getInteractions());
                model.addAttribute("code", loanApplication.getInteractions().getCreditCode() != null ? loanApplication.getInteractions().getCreditCode() : loanApplication.getInteractions().getLoanApplicationCode());

                fragmentToRender = "fragments/personFragmentsInteraction :: tab-interactinos-acordeon";

                break;
            }
            case "notes": {
                model.addAttribute("notes", loanApplicationDao.getLoanApplicationComments(loanApplicationId, Comment.COMMENT_EVALUATION));

                fragmentToRender = "fragments/personFragmentsNote :: tab-notes-acordeon";
                break;
            }
            default:
                break;
        }

        model.addAttribute("loanApplication", loanApplication);
        model.addAttribute("person", personDao.getPerson(catalogService, locale, personId, true));
//        model.addAttribute("userId", userId);
//        model.addAttribute("personCountry", person.getCountry().getId());
//
//        Currency currency = catalogService.getCurrency(Currency.PEN);
//        model.addAttribute("currency", currency);
//        model.addAttribute("countryCode", userDao.getUser(userId).getCountryCode());

        logger.debug(fragmentToRender);

        return fragmentToRender;
    }

    @RequestMapping(value = "/person/{personId}/credit/{creditId}/{accordion}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "person:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object loadCreditAccordion(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("personId") Integer personId,
            @PathVariable("creditId") Integer creditId,
            @PathVariable("accordion") String accordion) throws Exception {

        if (!SecurityUtils.getSubject().isPermitted("person:creditTab:view")) {
            return "403";
        }

        CreditBoPainter credit = creditDAO.getCreditBO(creditId, locale, CreditBoPainter.class);
        String fragmentToRender = null;

        if (credit == null) {
            return AjaxResponse.errorMessage("No existe el Crédito");
        }

        switch (accordion) {
            case "application": {
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);
                LoanApplicationBoPainter loanApplicationBoPainter = applicationPainterService.getApplicationById(credit.getLoanApplicationId(), locale, request);

                if (loanApplication != null && loanApplicationBoPainter != null) {
                    loanApplicationBoPainterSetter(loanApplication, loanApplicationBoPainter, true, true, request, locale);

                    List<LoanOffer> offers = loanApplicationDao.getLoanOffersAll(loanApplication.getId());
                    if (offers != null && !offers.isEmpty()) {
                        model.addAttribute("exchangeRate", offers.get(0).getExchangeRate());
                    }

                    model.addAttribute("loanApplication", loanApplicationBoPainter);
                    model.addAttribute("currency", catalogService.getCurrency(Currency.PEN));
                }

                fragmentToRender = "fragments/personFragmentsLoanApplication :: loan-application";

                break;
            }
            case "documentation": {
                List<LoanApplicationUserFiles> userFilesObjectList = personDao.getUserFiles(personId, locale);

                List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(locale, credit.getPersonId());
                if (ocupations != null) {
                    Optional<PersonOcupationalInformation> principalOcupation = ocupations.stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst();
                    principalOcupation.ifPresent(x -> model.addAttribute("personPrincipalOcupationalInfo", x));
                }

                for (LoanApplicationUserFiles loanApplicationUserFiles : userFilesObjectList) {
                    if (loanApplicationUserFiles.getCreditCode() == null) continue;
                    if (loanApplicationUserFiles.getCreditCode().equals(credit.getCode())) {
                        credit.setUserFilesObjectList(loanApplicationUserFiles);
                    }
                }

                model.addAttribute("userFilesObject", credit.getUserFilesObjectList());

                credit.setLoanApplication(applicationPainterService.getApplicationById(credit.getLoanApplicationId(), locale, request));

                model.addAttribute("loanApplication", applicationPainterService.getApplicationById(credit.getLoanApplicationId(), locale, request));

                fragmentToRender = "fragments/personFragmentsDocumentation :: tab-documentation-body";

                break;
            }
            case "rekognition": {
                List<RecognitionResultsPainter> recognitions = loanApplicationDao.getRecognitionResults(personId, locale);

                if (recognitions != null) {
                    for (RecognitionResultsPainter recognition : recognitions) {
                        if (recognition.getCreditId() == null) continue;
                        if (recognition.getCreditId().equals(credit.getId())) {
                            credit.setRecognition(recognition);
                        }
                    }
                }

                model.addAttribute("result", credit.getRecognition());

                fragmentToRender = "fragments/personFragmentsRekognition :: tab-rekognition-body";

                break;
            }
            case "interactions": {
                List<PersonInteraction> personInteractions = interactionDao.getPersonInteractions(personId, locale);
                List<GatewayContacts> gatewayContacts = personDao.getCollectionContacts(personId);

                List<Object> allInteractions = new ArrayList<>();
                if (personInteractions != null) {
                    allInteractions.addAll(personInteractions);
                }
                if (gatewayContacts != null) {
                    allInteractions.addAll(gatewayContacts);
                }
                allInteractions = allInteractions.stream().sorted(Comparator.nullsLast(
                        (e1, e2) -> {
                            Date e1d = e1 instanceof PersonInteraction ? ((PersonInteraction) e1).getRegisterDate() : ((GatewayContacts) e1).getRegisterDate();
                            Date e2d = e2 instanceof PersonInteraction ? ((PersonInteraction) e2).getRegisterDate() : ((GatewayContacts) e2).getRegisterDate();
                            if (e1d == null || e2d == null)
                                return -1;
                            return e2d.compareTo(e1d);
                        })
                ).collect(Collectors.toList());

                List<Object> interactions = new ArrayList<>();

                for (Object obj : allInteractions) {
                    Integer tempLoanApplicationId = obj instanceof PersonInteraction ? ((PersonInteraction) obj).getLoanApplicationId() : null;
                    Integer tempCreditId = obj instanceof PersonInteraction ? ((PersonInteraction) obj).getCreditId() : ((GatewayContacts) obj).getCreditId();

                    if (credit.getId().equals(tempCreditId) || credit.getLoanApplicationId().equals(tempLoanApplicationId)) {

                        if(obj instanceof PersonInteraction){
                            PersonInteraction personInteraction = (PersonInteraction) obj;
                            Map<String, Date> map = awsElasticSearchClient.getEmailEventsByPersonInteractionId(personInteraction.getId());
                            List<PersonInteractionStat> stats = new ArrayList<PersonInteractionStat>();
                            for (Map.Entry<String, Date> entry : map.entrySet()) {
                                PersonInteractionStat stat = new PersonInteractionStat();
                                stat.setEventDate(entry.getValue());
                                stat.setEvent(entry.getKey());
                                stats.add(stat);
                            }
                            personInteraction.setStats(stats);
                            interactions.add(personInteraction);
                        }else{
                            interactions.add(obj);
                        }
                    }
                }

                PersonInteractionPainter painter = new PersonInteractionPainter();
                painter.setCreditId(credit.getId());
                painter.setCreditCode(credit.getCode());
                painter.setLoanApplicationId(credit.getLoanApplicationId());
                painter.setRegisterDate(credit.getRegisterDate());

                if (credit.getLoanApplicationId() != null) {
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);
                    painter.setLoanApplicationCode(loanApplication != null ? loanApplication.getCode() : null);
                }
                painter.setInteractions(interactions);

                credit.setInteractions(painter);
                model.addAttribute("interactionsParam", credit.getInteractions().getInteractions());

                fragmentToRender = "fragments/personFragmentsInteraction :: tab-interactinos-acordeon";

                break;
            }
            case "notes": {
                model.addAttribute("notes", loanApplicationDao.getLoanApplicationComments(credit.getLoanApplicationId(), Comment.COMMENT_EVALUATION));

                fragmentToRender = "fragments/personFragmentsNote :: tab-notes-acordeon";

                break;
            }
            default: {
                throw new Exception("Accordion with name " + accordion + " is not implemented");
            }
        }

        model.addAttribute("credit", credit);
        model.addAttribute("person", personDao.getPerson(catalogService, locale, personId, true));

        logger.debug(fragmentToRender);

        return fragmentToRender;

    }

    @RequestMapping(value = "/application/{applicationId}/onDemand", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "person:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object loadLoanApplicationBoPainterBodyOnDemand(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("applicationId") Integer applicationId
    ) throws Exception {
        if (!SecurityUtils.getSubject().isPermitted("person:loanApplicationTab:view")) {
            return "403";
        }

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(applicationId, locale);
        LoanApplicationBoPainter loanApplicationBoPainter = applicationPainterService.getApplicationById(applicationId, locale, request);

        if (loanApplication != null && loanApplicationBoPainter != null) {
            loanApplicationBoPainterSetter(loanApplication, loanApplicationBoPainter, true, false, request, locale);

            List<LoanOffer> offers = loanApplicationDao.getLoanOffersAll(loanApplicationBoPainter.getId());
            if (offers != null && !offers.isEmpty()) {
                model.addAttribute("exchangeRate", offers.get(0).getExchangeRate());
            }

            model.addAttribute("loanApplication", loanApplicationBoPainter);
            model.addAttribute("currency", catalogService.getCurrency(Currency.PEN));
            model.addAttribute("person", personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), true));
        }

        model.addAttribute("onlyView", false);

        return "fragments/personFragmentsLoanApplication :: loan-application";
    }

    @RequestMapping(value = "/credit/{creditId}/onDemand", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "person:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object loadCreditBoPainterBodyOnDemand(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("creditId") Integer creditId,
            @RequestParam("personId") Integer personId
    ) throws Exception {
        if (!SecurityUtils.getSubject().isPermitted("person:creditTab:view")) {
            return "403";
        }

        Person person = personDao.getPerson(catalogService, locale, personId, false);

        if (person != null) {
            CreditBoPainter creditBoPainter = creditDao.getCreditBO(creditId, locale, CreditBoPainter.class);

            if (creditBoPainter != null) {
                creditBoPainterSetter(creditBoPainter, true, person, locale);
                creditBoPainter.setLoanApplication(applicationPainterService.getApplicationById(creditBoPainter.getLoanApplicationId(), locale, request));

                model.addAttribute("credit", creditBoPainter);
            }

            model.addAttribute("person", person);
        }

        model.addAttribute("onlyView", false);

        return "fragments/personFragmentsCredit :: credit-body";
    }

    private void loanApplicationBoPainterSetter(LoanApplication applicationFull, LoanApplicationBoPainter application, boolean showExpanded, boolean isLoanApplicationCredit, HttpServletRequest request, Locale locale) throws Exception {
        List<UserFile> userFiles = loanApplicationDao.getLoanApplicationUserFiles(application.getId());

        if (userFiles != null && !userFiles.isEmpty()) {
            UserFile userFile = userFiles.stream().filter(e -> e.getFileType().getId() == UserFileType.CONTRACT_CALL).findFirst().orElse(null);
            if (userFile != null) {
                String tokyCallURL = fileService.generateUserFileUrl(userFile.getId(), request, false);
                application.setTokyCall(tokyCallURL);
            }
        }

        application.setShowExpanded(showExpanded);

        QueryBot offersQueryBot = null;
        if (applicationFull.getOfferQueryBotId() != null)
            offersQueryBot = botDao.getQueryBot(applicationFull.getOfferQueryBotId());
        if (offersQueryBot != null && (offersQueryBot.getStatusId() == QueryBot.STATUS_RUNNING || offersQueryBot.getStatusId() == QueryBot.STATUS_QUEUE))
            application.setLoadingOffers(true);

        List<LoanOffer> offersT = loanApplicationDao.getLoanOffersAll(application.getId());
        application.setOffers(offersT);
        application.setConsolidableDebts(loanApplicationDao.getConsolidationAccounts(application.getId()));
        if (application.getEntityUserId() != null)
            application.setEntityUser(userService.getUserEntityById(application.getEntityUserId(), locale));

        application.setPreliminaryEvaluations(loanApplicationDao.getPreliminaryEvaluations(application.getId(), locale, JsonResolverDAO.EVALUATION_FOLLOWER_DB));
        application.setEvaluations(loanApplicationDao.getEvaluations(application.getId(), locale, JsonResolverDAO.EVALUATION_FOLLOWER_DB));

        application.setAuditRejectionReasons(creditDao.getAuditRejectionReason(application.getId()));

        application.setShowFiles(!isLoanApplicationCredit);
        application.setShowRecognition(!isLoanApplicationCredit);
        application.setShowInteractions(!isLoanApplicationCredit);
        application.setShowFraudAlerts(!isLoanApplicationCredit);
        application.setShowNotes(!isLoanApplicationCredit);
        application.setShowPepOfac(!isLoanApplicationCredit);
    }

    private void creditBoPainterSetter(CreditBoPainter credit, boolean showExpanded, Person person, Locale locale) throws Exception {
        credit.setConsolidatedDebts(creditDao.getConsolidatedDebts(credit.getId()));
        credit.setShowExpanded(showExpanded);
        credit.setManagementSchedule(creditDao.getManagementSchedule(credit.getId()));
        credit.setDownPaymentDetail(creditDao.getDownPayments(credit.getId()));
        credit.setEntityProductParams(catalogService.getEntityProductParamById(credit.getEntityProductParameterId()));

        if (person.getDocumentType().getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
            String nse = personService.getNosisNSE(person.getId());
            SocioeconomicLevel socioeconomicLevel = null;
            if (nse != null) {
                socioeconomicLevel = catalogService.getSocioEconomicLevelByLevel(nse);
            }
            BcraResult bcraResult = personDao.getBcraResult(person.getId());
            Double bcraIndebtedness = null;
            if (bcraResult != null && !bcraResult.getHistorialDeudas().isEmpty()) {
                List<String> periods = bcraResult.getHistorialDeudas().get(0).getHistorial().stream().map(BcraResult.DeudaBanco.RegistroDeuda::getPeriodo).collect(Collectors.toList());
                for (int i = 0; i < periods.size(); i++) {
                    bcraIndebtedness = bcraResult.getDeudaWorstSituacionByPeriod(i);
                    if (bcraIndebtedness != null && bcraIndebtedness != 0) {
                        break;
                    }
                }
            }

            Double estimateIncome = null;
            Double nseAverageValue = Double.MAX_VALUE;
            Double personalIncome = Double.MAX_VALUE;
            if (socioeconomicLevel != null) {
                nseAverageValue = 0.8 * (new Double(socioeconomicLevel.getAvgIncome()));
            }
            List<PersonOcupationalInformation> principalOcupationInformationList = personDao.getPersonOcupationalInformation(locale, person.getId());
            PersonOcupationalInformation principalOcupationInformation = null;

            if (principalOcupationInformationList != null) {
                principalOcupationInformation = personDao.getPersonOcupationalInformation(locale, person.getId())
                        .stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL)
                        .findFirst().orElse(null);
            }

            if (principalOcupationInformation != null) {
                personalIncome = principalOcupationInformation.getFixedGrossIncome();
            }
            if (principalOcupationInformation != null || socioeconomicLevel != null) {
                estimateIncome = nseAverageValue < personalIncome ? nseAverageValue : personalIncome;
            }
            Double dti = null;
            Double nosisComitment = null;
            if (personService.getNosisCommitment(person.getId()) != null) {
                nosisComitment = new Double(personService.getNosisCommitment(person.getId()));
            }

            if (estimateIncome != null && estimateIncome != 0 && nosisComitment != null) {
                dti = (credit.getInstallmentAmountAvg() + nosisComitment) / estimateIncome;
            }
            credit.setNosisCommitment(nosisComitment);
            credit.setBcraIndebtedness(bcraIndebtedness);
            credit.setEstimateIncome(estimateIncome);
            credit.setDti(dti);
        }
    }

    @RequestMapping(value = "/person/interaction/{personInteractionId}/responses", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "person:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object loadLoanApplicationAccordion(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("personInteractionId") Integer personInteractionId) throws Exception {

        PersonInteraction personInteraction = interactionDao.getPersonInteractionWithResponses(personInteractionId, locale);
        model.addAttribute("interactionResponses", personInteraction != null ? personInteraction.getInteractionResponses() : null);
        return new ModelAndView("fragments/personFragmentsInteraction :: personinteractionResponses");
    }

    @RequestMapping(value = "/person/occupationalInfo/employees", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "person:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getRucTotalEmployees(@RequestParam("ruc") String ruc) throws Exception {
//        TODO CACHE
        return AjaxResponse.ok(personDao.getEmployeesQuantity(ruc).toString());
    }

    private void registerPhoneNumber(Integer personId, String phoneType, String phoneNumber) throws Exception {
        PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(personId, Configuration.getDefaultLocale());
        personDao.updateOcupatinalPhoneNumber(personId, phoneType, ocupation.getNumber(), phoneNumber);
    }

    private FieldValidator getGrossIncomeValidator(Integer personId, Locale locale, String value) throws Exception {
        Person person = personDao.getPerson(catalogService, locale, personId, true);

        if (person.getDocumentType().getCountryId().equals(CountryParam.COUNTRY_COLOMBIA))
            return new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME_COLOMBIA, Integer.parseInt(value));
        else
            return new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME, Integer.parseInt(value));
    }
}