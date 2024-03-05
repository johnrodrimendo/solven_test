package com.affirm.entityExt.controller;

import com.affirm.acceso.model.Direccion;
import com.affirm.acceso.model.Expediente;
import com.affirm.aws.RecognitionResultsPainter;
import com.affirm.aws.elasticSearch.AWSElasticSearchClient;
import com.affirm.client.model.LoanApplicationExtranetRequestPainter;
import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.client.service.impl.EntityExtranetServiceImpl;
import com.affirm.common.dao.*;
import com.affirm.common.model.PersonInteractionExtranetPainter;
import com.affirm.common.model.PreApprovedInfo;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.Question28Form;
import com.affirm.common.model.form.Question53Form;
import com.affirm.common.model.rcc.RccResponse;
import com.affirm.common.model.rcc.Synthesized;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.service.question.Question50Service;
import com.affirm.common.service.question.QuestionFlowService;
import com.affirm.common.util.*;
import com.affirm.entityExt.models.*;
import com.affirm.entityExt.models.form.CreateLoanOfferForm;
import com.affirm.entityExt.models.form.UpdatePersonAddressForm;
import com.affirm.onesignal.service.OneSignalService;
import com.affirm.onesignal.service.impl.OneSignalServiceImpl;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.security.model.SysUser;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.shiro.SecurityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Controller("entityExtranetLoanEvaluationController")
public class EntityExtranetLoanEvaluationController {

    public static final String URL = "evaluation";

    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private EntityExtranetService entityExtranetService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDAO;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private PersonService personService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private CountryContextService countryContextService;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private UtilService utilService;
    @Autowired
    private InteractionDAO interactionDAO;
    @Autowired
    private AWSElasticSearchClient awsElasticSearchClient;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private OneSignalService oneSignalService;
    @Autowired
    private LoanNotifierService loanNotifierService;
    @Autowired
    private EmailExtranetService emailExtranetService;
    @Autowired
    private FileService fileService;
    @Autowired
    private BotDAO botDao;
    @Autowired
    private WebscrapperService webscrapperService;
    @Autowired
    private Question50Service question50Service;
    @Autowired
    private LoanApplicationApprovalValidationService loanApplicationApprovalValidationService;
    @Autowired
    private SecurityDAO securityDAO;
    @Autowired
    private BrandingService brandingService;

    @RequestMapping(value = "/" + URL, method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loan:evaluate:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showEvaluations(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        Integer limitPaginator = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.EVALUATION_MENU);
        List<Integer> productCategories = menuEntityProductCategory.getProductCategories();

        List<EntityProductParams> entityProductParamsForFilter = catalogService.getEntityProductParams().stream()
                .filter(p -> p.getEntity().getId().equals(loggedUserEntity.getPrincipalEntity().getId()))
                .filter(p -> productCategories.contains(p.getProduct().getProductCategoryId()))
                .collect(Collectors.toList());


        Integer[] entityProductParamsArray = entityProductParamsForFilter.stream().map(EntityProductParams::getId).toArray(Integer[]::new);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());

        List<LoanApplicationToEvaluationExtranetPainter> loans = creditDao.getEntityToEvaluateLoanApplications(loggedUserEntity.getPrincipalEntity().getId(), null, null, null, null, null, null, entityProductParamsArray, null, 0, limitPaginator, false, locale);

        List<EntityExtranetUser> users = userDAO.getEntityExtranetUsers(loggedUserEntity.getPrincipalEntity().getId(), null, null)
                .stream().filter(u -> u.getRoles() != null && u.getRoles().contains(Role.EXTTRANET_BANDEJA_EVALUACION_VISTA))
                .collect(Collectors.toList());
        Pair<Integer, Double> countAdSum = creditDao.getEntityToEvaluateLoanApplicationsCount(loggedUserEntity.getPrincipalEntity().getId(), null, null, null, null, null, null, entityProductParamsArray, null, locale);
        model.addAttribute("totalCount", countAdSum.getLeft());
        model.addAttribute("limitPaginator", limitPaginator);
        model.addAttribute("loans", loans);
        model.addAttribute("users", users);
        model.addAttribute("title", "Evaluación");
        model.addAttribute("page", "evaluation");
        model.addAttribute("products", products);
        model.addAttribute("productsRemoveText", Arrays.asList("Desembolso","desembolso"));
        model.addAttribute("disbursementTypes", entityProductParamsForFilter.stream().map(e -> e.getDisbursementTypeObject()).distinct().collect(Collectors.toList()));

        List<Integer> approvalValidationsId = new ArrayList<>();
        for (EntityProductParams entityProductParam : entityProductParamsForFilter) {
            if(entityProductParam.getAllApprovalValidationIds() != null && !entityProductParam.getAllApprovalValidationIds().isEmpty()) approvalValidationsId.addAll(entityProductParam.getAllApprovalValidationIds());
        }

        approvalValidationsId = approvalValidationsId.stream().distinct().collect(Collectors.toList());

        List<ApprovalValidation> approvalValidations = catalogService.getApprovalValidations();

        List<Integer> finalApprovalValidationsId = approvalValidationsId;

        model.addAttribute("sidebarClosed", true);
        model.addAttribute("tray", ExtranetMenu.EVALUATION_MENU);
        model.addAttribute("progresses", approvalValidations.stream().filter(e -> finalApprovalValidationsId.contains(e.getId())).collect(Collectors.toList()));

        return new ModelAndView("/entityExtranet/extranetEvaluation");
    }

    @RequestMapping(value = "/" + URL + "/detail/{loanId}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loan:evaluate:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showDetailEvaluation(
            ModelMap model, Locale locale, HttpServletRequest request, @PathVariable("loanId") Integer loanId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanId,locale);

        SummaryLoanExtranetPainter summaryLoanExtranetPainter = new SummaryLoanExtranetPainter();
        summaryLoanExtranetPainter.setLoan(loanApplication);
        summaryLoanExtranetPainter.setOffers(loanApplicationDao.getLoanOffers(loanId));

        model.addAttribute("loanApplication", loanApplication);
        model.addAttribute("loanId",loanId);

        return new ModelAndView("/entityExtranet/extranetEvaluation :: detail");
    }

    @RequestMapping(value = "/" + URL + "/summary/{loanId}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loan:evaluate:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getSummaryLoanApplication(
            ModelMap model, Locale locale, HttpServletRequest request, @PathVariable("loanId") Integer loanId) throws Exception {
        model.addAttribute("loanId",loanId);


        LoanApplicationExtranetRequestPainter loanApplication = entityExtranetService.getApplicationById(loanId, locale, request);

        LoanOffer selectedOffer = loanApplicationDao.getLoanOffersAll(loanId).stream().filter(o -> o.getSelected()).findFirst().orElse(null);
        if(selectedOffer == null)
            return AjaxResponse.errorMessage("no existe una oferta seleccionada a&uacute;n");

        List<Date> enableDates = question50Service.getScheduleEnablesDates(selectedOffer.getEntityProductParam());
        String[] enabledDatesFormatted = new String[enableDates.size()];
        for (int i = 0; i < enableDates.size(); i++) {
            enabledDatesFormatted[i] = new SimpleDateFormat("dd/MM/yyyy").format(enableDates.get(i));
        }

        if(loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA){
            AztecaPreApprovedBase aztecaPreApprovedBase = null;
            if(loanApplication.getEntityCustomData() != null && loanApplication.getEntityCustomData().has(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_BASE_PREAPROBADA.getKey())){
                aztecaPreApprovedBase = new Gson().fromJson(loanApplication.getEntityCustomData().getJSONObject(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_BASE_PREAPROBADA.getKey()).toString(),AztecaPreApprovedBase.class);
                if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getIdCampania() != null){
                    model.addAttribute("campaign", aztecaPreApprovedBase.getIdCampania());
//                    if(aztecaPreApprovedBase.getIdCampania() == AztecaPreApprovedBase.CALL_CENTER_CAMP) model.addAttribute("campaign", "Solo con DNI");
//                    else if(aztecaPreApprovedBase.getIdCampania() == AztecaPreApprovedBase.BANKED_CAMP) model.addAttribute("campaign", "Bancarizado");
//                    else if(aztecaPreApprovedBase.getIdCampania() == AztecaPreApprovedBase.NO_BANKED_CAMP) model.addAttribute("campaign", "No Bancarizados");
                }
            }
        }

        model.addAttribute("enableDates", enabledDatesFormatted);

        String selectedDateFormatted = null;
        if(loanApplication.getFirstDueDate() != null)
            selectedDateFormatted = new SimpleDateFormat("dd/MM/yyyy").format(loanApplication.getFirstDueDate());
        model.addAttribute("selectedDate", selectedDateFormatted);

        SummaryLoanExtranetPainter summaryLoanExtranetPainter = new SummaryLoanExtranetPainter();
        summaryLoanExtranetPainter.setLoan(loanApplicationDao.getLoanApplication(loanId,locale));
        summaryLoanExtranetPainter.setOffers(loanApplicationDao.getLoanOffersAll(loanId));
        model.addAttribute("loanApplication", loanApplication);

        SummaryIdentityValidationPainter identityValidationPainter = new SummaryIdentityValidationPainter();
        List<RecognitionResultsPainter> recognitions = loanApplicationDao.getRecognitionResults(loanApplication.getPersonId(), locale);
        if (recognitions != null) {
            for (RecognitionResultsPainter recognition : recognitions) {
                if (recognition.getLoanApplicationId() == null) continue;
                if (recognition.getLoanApplicationId().equals(loanApplication.getId())) {
                    identityValidationPainter.setLastRekognition(recognition);
                }
            }
        }
        if(loanApplication.getSelectedEntityProductParameterId() != null){
            EntityProductParams entityParams = catalogService.getEntityProductParamById(loanApplication.getSelectedEntityProductParameterId());
            if(entityParams != null && entityParams.getEntityProductParamIdentityValidationConfig() != null && entityParams.getEntityProductParamIdentityValidationConfig().getRekognitionMinPercentage() != null)
                identityValidationPainter.setRekognitionMinPercentage(entityParams.getEntityProductParamIdentityValidationConfig().getRekognitionMinPercentage());
        }

        identityValidationPainter.setIdentityApprovalValidation(loanApplication.getApprovalValidations().stream().filter(a-> a.getApprovalValidationId() == ApprovalValidation.IDENTIDAD).findFirst().orElse(null));
        List<MatiResult> matiResults = securityDAO.getMatiResultsByLoanApplication(loanId);
        if(matiResults != null) loanApplication.setMatiResults(matiResults.stream().sorted((e1, e2) -> e2.getRegisterDate().compareTo(e1.getRegisterDate())).collect(Collectors.toList()));
        if(!matiResults.isEmpty())
            identityValidationPainter.setMatiResult(matiResults.get(0));
        model.addAttribute("identityValidationPainter", identityValidationPainter);

        List<LoanApplicationFraudAlert> loanApplicationFraudAlerts = getPersonFraudAlertsByApplication(loanApplication, false);

        model.addAttribute("loanApplicationFraudAlerts", loanApplicationFraudAlerts);

        List<Comment> comments = loanApplicationDao.getLoanApplicationComments(loanApplication.getId(), Comment.COMMENT_EVALUATION);

        if(comments != null && !comments.isEmpty())  model.addAttribute("note", comments.get(0));

        if(loanApplication.getSelectedEntityProductParameterId() != null){
            EntityProductParams entityProductParams = catalogService.getEntityProductParamById(loanApplication.getSelectedEntityProductParameterId());
            if(entityProductParams != null) summaryLoanExtranetPainter.setFixedOffer(entityProductParams.getFixedOffer());
        }

        model.addAttribute("summaryData", summaryLoanExtranetPainter);

        return new ModelAndView("/entityExtranet/extranetEvaluation :: summaryData");
    }

    @RequestMapping(value = "/" + URL + "/applicant/{loanId}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loan:evaluate:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getApplicantLoanApplication(
            ModelMap model, Locale locale, HttpServletRequest request, @PathVariable("loanId") Integer loanId) throws Exception {
        ApplicantLoanExtranetPainter applicantLoanExtranetPainter = new ApplicantLoanExtranetPainter();
        applicantLoanExtranetPainter.setLoanApplicationId(loanId);
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanId,locale);
        UpdatePersonAddressForm form = new UpdatePersonAddressForm();
        model.addAttribute("loanId",loanId);
        if(loanApplication != null){
            Person person = personDAO.getPerson(catalogService,locale,loanApplication.getPersonId(),true);
            applicantLoanExtranetPainter.setPersonId(loanApplication.getPersonId());
            User user = userDAO.getUser(loanApplication.getUserId());
            applicantLoanExtranetPainter.setUserId(loanApplication.getUserId());

            if(person != null){
                applicantLoanExtranetPainter.setPartner(person.getPartner());
                model.addAttribute("person",person);
                ((UpdatePersonAddressForm.Validator) form.getValidator()).setCountryId(person.getDocumentType().getCountryId(), form.getWithoutNumber());
                applicantLoanExtranetPainter.setCountryParam(person.getCountry());
                applicantLoanExtranetPainter.setBirthday(person.getBirthday());
                applicantLoanExtranetPainter.setDependents(person.getDependents());
                applicantLoanExtranetPainter.setDocumentNumber(person.getDocumentNumber());
                applicantLoanExtranetPainter.setDocumentType(person.getDocumentType());
                applicantLoanExtranetPainter.setFirstSurname(person.getFirstSurname());
                applicantLoanExtranetPainter.setName(person.getName());
                applicantLoanExtranetPainter.setLastSurname(person.getLastSurname());
                applicantLoanExtranetPainter.setPep(person.getPep());
                applicantLoanExtranetPainter.setProfession(person.getProfession());
                applicantLoanExtranetPainter.setStudyLevel(person.getStudyLevel());
                applicantLoanExtranetPainter.setMaritalStatus(person.getMaritalStatus());
                applicantLoanExtranetPainter.setNationality(person.getNationality());
                applicantLoanExtranetPainter.setProfessionOccupation(person.getProfessionOccupation());
                List<PersonOcupationalInformation> ocupations = personDAO.getPersonOcupationalInformation(locale, loanApplication.getPersonId());
                if (ocupations != null) {
                    Optional<PersonOcupationalInformation> principalOcupation = ocupations.stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst();
                    principalOcupation.ifPresent(x -> applicantLoanExtranetPainter.setPersonOcupationalInformation(x));
                }
                Direccion disagregatedAddress = personDAO.getDisggregatedAddress(loanApplication.getPersonId(), "H");
                String department = null;
                String province = null;
                String district = null;
                String postalCode = null;
                PersonContactInformation personContactInformation = personDAO.getPersonContactInformation(locale, loanApplication.getPersonId());
                applicantLoanExtranetPainter.setPersonContactInformation(personContactInformation);
                if(personContactInformation != null){
                    form.setLatitude(personContactInformation.getAddressLatitude());
                    form.setLongitude(personContactInformation.getAddressLongitude());
                    form.setAggregatedAddress(personContactInformation.getAddressStreetName());
                }
                if (disagregatedAddress != null && disagregatedAddress.getUbigeo() != null && person.getDocumentType().getCountryId() == CountryParam.COUNTRY_PERU ) {
                    Ubigeo ubigeo = catalogService.getUbigeo(disagregatedAddress.getUbigeo());
                    department = ubigeo.getDepartment().getId();
                    province = ubigeo.getProvince().getId();
                    district = ubigeo.getDistrict().getId();
                } else if (disagregatedAddress != null && disagregatedAddress.getLocalityId() != null && person.getDocumentType().getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
                    District generalDistrict = catalogService.getGeneralDistrictById(disagregatedAddress.getLocalityId());
                    province = generalDistrict.getProvince().getProvinceId() + "";
                    district = generalDistrict.getDistrictId() + "";
                    postalCode = generalDistrict.getPostalCode();
                }
                else if(personContactInformation != null){
                    department = personContactInformation.getDepartment() != null ? personContactInformation.getDepartment().getId() : null;
                    province = personContactInformation.getProvince() != null ? personContactInformation.getProvince().getId() : null;
                    district = personContactInformation.getDistrict() != null ? personContactInformation.getDistrict().getId() : null;
                    if(personContactInformation.getAddressUbigeo() != null){
                        if(department == null && personContactInformation.getAddressUbigeo().getDepartment() != null) department =  personContactInformation.getAddressUbigeo().getDepartment().getId();
                        if(province == null && personContactInformation.getAddressUbigeo().getProvince() != null) province =  personContactInformation.getAddressUbigeo().getProvince().getId();
                        if(district == null && personContactInformation.getAddressUbigeo().getDistrict() != null) district =  personContactInformation.getAddressUbigeo().getDistrict().getId();
                    }
                    form.setReference(personContactInformation.getAddressDetail());
                    form.setLatitude(personContactInformation.getAddressLatitude());
                    form.setLongitude(personContactInformation.getAddressLongitude());
                    form.setAggregatedAddress(personContactInformation.getAddressStreetName());
                    model.addAttribute("latitude",personContactInformation.getAddressLatitude());
                    model.addAttribute("longitude",personContactInformation.getAddressLongitude());
                    applicantLoanExtranetPainter.setAddress(personContactInformation.getFullAddressBOWithReference());
                }
                form.setDepartamento(department);
                form.setProvincia(province);
                form.setDistrito(district);
                form.setPostalCode(postalCode);
                if(disagregatedAddress != null){
                    form.setRoadType(disagregatedAddress != null && disagregatedAddress.getTipoVia() != null ?  disagregatedAddress.getTipoVia() + "" : "");
                    form.setRoadName(disagregatedAddress.getNombreVia());
                    form.setWithoutNumber(disagregatedAddress.getWithoutNumber());
                    form.setFloor(disagregatedAddress.getFloor());
                    form.setHouseNumber(disagregatedAddress.getNumeroVia());
                    form.setInterior(disagregatedAddress.getNumeroInterior());
                    form.setManzana(disagregatedAddress.getManzana());
                    form.setLote(disagregatedAddress.getLote());
                    form.setReference(disagregatedAddress.getReferencia());
                    form.setZoneType(disagregatedAddress.getTipoZona() != null ? disagregatedAddress.getTipoZona() + "" : "");
                    form.setZoneName(disagregatedAddress.getNombreZona());
                    form.setSearchQuery(disagregatedAddress.getSearchQuery());
                    form.setAggregatedAddress(disagregatedAddress.getDireccionCompleta());
                    form.setLatitude(disagregatedAddress.getLatitude());
                    form.setLongitude(disagregatedAddress.getLongitude());
                    applicantLoanExtranetPainter.setAddress(disagregatedAddress.getDireccionCompleta());
                    personDAO.completeAddressCoordinates(person.getId(), disagregatedAddress);
                    model.addAttribute("latitude", disagregatedAddress.getLatitude());
                    model.addAttribute("longitude", disagregatedAddress.getLongitude());
                }
                applicantLoanExtranetPainter.setDisagregatedAddress(disagregatedAddress);
            }
            if(user != null){
                applicantLoanExtranetPainter.setEmail(user.getEmail());
                applicantLoanExtranetPainter.setEmailVerified(user.getEmailVerified());
                applicantLoanExtranetPainter.setPhoneNumber(user.getPhoneNumber());
                applicantLoanExtranetPainter.setPhoneVerified(user.getPhoneVerified());
            }
        }
        model.addAttribute("applicantData",applicantLoanExtranetPainter);
        model.addAttribute("updatePersonAddressForm", form);
        model.addAttribute("maritalStatuses", catalogService.getMaritalStatus(locale));
        return new ModelAndView("/entityExtranet/extranetEvaluation :: applicantData");
    }

    @RequestMapping(value = "/" + URL + "/incomes/{loanId}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loan:evaluate:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getIncomesLoanApplication(
            ModelMap model, Locale locale, HttpServletRequest request, @PathVariable("loanId") Integer loanId) throws Exception {
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanId,locale);

        Person person = personDAO.getPerson(catalogService, locale, loanApplication.getPersonId(), false);

        UpdatePersonAddressForm form = new UpdatePersonAddressForm();
        ((UpdatePersonAddressForm.Validator) form.getValidator()).setCountryId(person.getDocumentType().getCountryId(), form.getWithoutNumber());

        Direccion disagregatedAddress = personDAO.getDisggregatedAddress(loanApplication.getPersonId(), "J");

        List<PersonOcupationalInformation> ocupationalInformations = personDAO.getPersonOcupationalInformation(locale, person.getId());

        PersonOcupationalInformation principalOcupation = ocupationalInformations != null ?  ocupationalInformations.stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL)
                                                                    .findFirst().orElse(null) : null;

        Question28Form form28 = new Question28Form();
        ((Question28Form.Validator) form28.getValidator()).configValidator(person.getDocumentType().getCountryId());

        if (principalOcupation != null) {
            form28.setPhoneCode(principalOcupation.getPhoneCode());
            form28.setPhoneNumber(principalOcupation.getPhoneNumberWithoutCode());
            form28.setTypePhone(principalOcupation.getPhoneNumberType());
        }

        EntityExtranetConfiguration extranetConfiguration = brandingService.getExtranetBrandingAsJson(loggedUserEntity.getPrincipalEntity().getId());

        List<Integer> professionOccupationIds = extranetConfiguration.getProfessionOccupationIds();

        List<PersonProfessionOccupation> professionOccupations = null;

        if (professionOccupationIds != null) {
            professionOccupations = catalogService.getProfessionOccupations()
                    .stream()
                    .filter(e -> professionOccupationIds.contains(e.getId()))
                    .collect(Collectors.toList());
        }
        else {
            professionOccupations = catalogService.getProfessionOccupations();
        }
        professionOccupations.sort(Comparator.comparing(PersonProfessionOccupation::getOccupation));


        model.addAttribute("professionOccupations", professionOccupations);
        model.addAttribute("occupationalPhoneNumberForm", form28);

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
            form.setAggregatedAddress(disagregatedAddress.getDireccionCompleta());
            form.setLatitude(disagregatedAddress.getLatitude());
            form.setLongitude(disagregatedAddress.getLongitude());
            personDAO.completeAddressCoordinates(loanApplication.getPersonId(), disagregatedAddress);
            form.setSearchQuery(principalOcupation != null ? principalOcupation.getSearchQuery() : null);
            model.addAttribute("latitude",disagregatedAddress.getLatitude());
            model.addAttribute("longitude",disagregatedAddress.getLongitude());
            model.addAttribute("address", disagregatedAddress.getDireccionCompleta());
        }

        else if (principalOcupation != null) {
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
            model.addAttribute("address", principalOcupation.getFullAddress());
            model.addAttribute("latitude",principalOcupation.getAddressLatitude());
            model.addAttribute("longitude",principalOcupation.getAddressLongitude());
        }
        form.setAddressType("J");

        model.addAttribute("loanId",loanId);
        model.addAttribute("person", person);
        model.addAttribute("principalOcupation", principalOcupation);
        model.addAttribute("personId", loanApplication.getPersonId());
        model.addAttribute("updatePersonAddressForm", form);

        List<Integer> occupationIds = null;
        List<Ocupation> occupations = null;



        if (occupationIds != null) {
            occupations = catalogService.getOcupations(locale)
                    .stream()
                    .filter(e -> occupationIds.contains(e.getId()))
                    .collect(Collectors.toList());

            occupations.sort(Comparator.comparing(Ocupation::getOrderId));
        }
        else {
            occupations = catalogService.getOcupations(locale);
            occupations.sort(Comparator.comparing(Ocupation::getOrderId));
        }

        if (professionOccupationIds != null) {
            professionOccupations = catalogService.getProfessionOccupations()
                    .stream()
                    .filter(e -> professionOccupationIds.contains(e.getId()))
                    .collect(Collectors.toList());

            professionOccupations.sort(Comparator.comparing(PersonProfessionOccupation::getOccupation));
        }
        else {
            professionOccupations = catalogService.getProfessionOccupations();
            professionOccupations.sort(Comparator.comparing(PersonProfessionOccupation::getOccupation));
        }

        model.addAttribute("occupations",occupations);
        return new ModelAndView("/entityExtranet/extranetEvaluation :: incomesData");
    }

    @RequestMapping(value = "/" + URL + "/update/address", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:address:update", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object updateAddress(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("personId") Integer personId,
            @RequestParam(value = "loanId", required = false) Integer loanId,
            UpdatePersonAddressForm updatePersonAddressForm
            ) throws Exception {

        if (updatePersonAddressForm == null) {
            return AjaxResponse.errorMessage("No hay datos enviados");
        }

        Person person;
        FieldValidator validator;
        PersonOcupationalInformation personOcupationalInformation = null;

        person = personDAO.getPerson(catalogService, locale, personId, false);

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
                personDAO.updateAddressInformation(
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
                personDAO.updateAddressInformation(
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
                personDAO.updateAddressInformation(
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
            personOcupationalInformation = personService.getCurrentOcupationalInformation(personId, locale);
            if(personOcupationalInformation == null) personDAO.cleanOcupationalInformation(personId, PersonOcupationalInformation.PRINCIPAL);

            if (person.getDocumentType().getCountryId().equals(CountryParam.COUNTRY_PERU)) {
                personDAO.updateOcupationalAddress(
                        personId,
                        PersonOcupationalInformation.PRINCIPAL,
                        updatePersonAddressForm.getAggregatedAddress(),
                        false,
                        updatePersonAddressForm.getDepartamento(),
                        updatePersonAddressForm.getProvincia(),
                        updatePersonAddressForm.getDistrito());
            } else if (person.getDocumentType().getCountryId().equals(CountryParam.COUNTRY_ARGENTINA)) {
                personDAO.updateOcupationalAddress(
                        personId,
                        PersonOcupationalInformation.PRINCIPAL,
                        updatePersonAddressForm.getAggregatedAddress(),
                        false,
                        null,
                        null,
                        null);
            } else if (person.getDocumentType().getCountryId().equals(CountryParam.COUNTRY_COLOMBIA)) {
                personDAO.updateOcupationalAddress(
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

        personDAO.registerDisgregatedAddress(personId, address);
        address.setSearchQuery(updatePersonAddressForm.getSearchQuery());
        address.setLatitude(updatePersonAddressForm.getLatitude());
        address.setLongitude(updatePersonAddressForm.getLongitude());

        if ("H".equals(updatePersonAddressForm.getAddressType())) {
            personDAO.registerAddressCoordinates(personId, address);
        } else if ("J".equals(updatePersonAddressForm.getAddressType())) {
            int ocupationalNumber = personService.getCurrentOcupationalInformation(personId, locale).getNumber();
            personDAO.registerJobAddressCoordinates(personId, ocupationalNumber, address);
        }

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/" + URL + "/phone_verification/{loanId}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loan:evaluate:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getPhoneVerification(
            ModelMap model, Locale locale, HttpServletRequest request, @PathVariable("loanId") Integer loanId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanId,locale);

        Person person = personDAO.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
        User user = userDAO.getUser(loanApplication.getUserId());

        PhoneVerificationLoanExtranetPainter phoneVerificationLoanExtranetPainter = new PhoneVerificationLoanExtranetPainter();
        phoneVerificationLoanExtranetPainter.setPhone(user.getPhoneNumber());
        phoneVerificationLoanExtranetPainter.setDocumentNumber(person.getDocumentNumber());
        phoneVerificationLoanExtranetPainter.setName(person.getFullName());
        phoneVerificationLoanExtranetPainter.setDocumentType(person.getDocumentType());
        phoneVerificationLoanExtranetPainter.setCountryId(person.getCountry().getId());

        if(loanApplication != null){
            List<LoanApplicationTrackingAction> actions = loanApplicationDao.getLoanApplicationTrackingActionsByTrackingId(loanId,new Integer[]{TrackingAction.PHONE_VERIFICATION_CONTACTED,TrackingAction.PHONE_VERIFICATION_NO_RESPONSE,TrackingAction.PHONE_VERIFICATION_WRONG_NUMBER,TrackingAction.TRACKING_PHONE_CALL},null);
            if(actions != null && !actions.isEmpty()){
                LoanApplicationTrackingAction loanApplicationTrackingActionFirst = actions.stream().filter(e -> e.getTrackingAction().getTrackingActionId() != TrackingAction.TRACKING_PHONE_CALL).findFirst().orElse(null);
                phoneVerificationLoanExtranetPainter.setAction(loanApplicationTrackingActionFirst != null ? loanApplicationTrackingActionFirst.getTrackingAction() : null);
                LoanApplicationTrackingAction loanApplicationTrackingAction = actions.stream().filter(e-> e.getTrackingAction() != null && (int) e.getTrackingAction().getTrackingActionId() == TrackingAction.TRACKING_PHONE_CALL && e.getUserFileId() != null).findFirst().orElse(null);
                if(loanApplicationTrackingAction != null){
                    UserFile userFile = userDAO.getUserFile(loanApplicationTrackingAction.getUserFileId());
                    if(userFile != null) phoneVerificationLoanExtranetPainter.setUserFile(userFile);
                }
            }
        }

        List<Referral> referrals = personDAO.getReferrals(loanApplication.getPersonId(), locale);
        List<ReferralPainterExtranet> referralPainterExtranets = new ArrayList<>();
        if(referrals != null){
            for (Referral referral : referrals) {
                ReferralPainterExtranet referralExtranet = new Gson().fromJson(new Gson().toJson(referral),ReferralPainterExtranet.class);
                referralExtranet.setTrackingActions(loanApplicationDao.getLoanApplicationTrackingActionsByTrackingId(loanId,new Integer[]{TrackingAction.PHONE_VERIFICATION_CONTACTED_REFERRAL,TrackingAction.PHONE_VERIFICATION_NO_RESPONSE_REFERRAL,TrackingAction.PHONE_VERIFICATION_WRONG_NUMBER_REFERRAL},referral.getId()));
                referralPainterExtranets.add(referralExtranet);
            }
            phoneVerificationLoanExtranetPainter.setReferrals(referralPainterExtranets);
        }


        model.addAttribute("loanId",loanId);
        model.addAttribute("relationships",catalogService.getRelationships(locale));
        model.addAttribute("phoneVerificationData", phoneVerificationLoanExtranetPainter);
        return new ModelAndView("/entityExtranet/extranetEvaluation :: phoneVerificationData");
    }

    @RequestMapping(value = "/" + URL + "/address_verification/{loanId}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loan:evaluate:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getAddressVerification(
            ModelMap model, Locale locale, HttpServletRequest request, @PathVariable("loanId") Integer loanId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanId,locale);

        Person person = personDAO.getPerson(catalogService, locale, loanApplication.getPersonId(), false);

        User user = userDAO.getUser(loanApplication.getUserId());
        List<Referral> referrals = personDAO.getReferrals(loanApplication.getPersonId(), locale);

        Direccion disagregatedAddress = personDAO.getDisggregatedAddress(loanApplication.getPersonId(), "H");
        PersonContactInformation personContactInformation = personDAO.getPersonContactInformation(locale, loanApplication.getPersonId());

        UpdatePersonAddressForm form = new UpdatePersonAddressForm();
        ((UpdatePersonAddressForm.Validator) form.getValidator()).setCountryId(person.getDocumentType().getCountryId(), form.getWithoutNumber());

        String department = null;
        String province = null;
        String district = null;
        String postalCode = null;
        if (disagregatedAddress != null && disagregatedAddress.getUbigeo() != null && person.getDocumentType().getCountryId() == CountryParam.COUNTRY_PERU ) {
            Ubigeo ubigeo = catalogService.getUbigeo(disagregatedAddress.getUbigeo());
            department = ubigeo.getDepartment().getId();
            province = ubigeo.getProvince().getId();
            district = ubigeo.getDistrict().getId();
        } else if (disagregatedAddress != null && disagregatedAddress.getLocalityId() != null && person.getDocumentType().getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
            District generalDistrict = catalogService.getGeneralDistrictById(disagregatedAddress.getLocalityId());
            province = generalDistrict.getProvince().getProvinceId() + "";
            district = generalDistrict.getDistrictId() + "";
            postalCode = generalDistrict.getPostalCode();
        }
        else if(personContactInformation != null){
            department = personContactInformation.getDepartment() != null ? personContactInformation.getDepartment().getId() : null;
            province = personContactInformation.getProvince() != null ? personContactInformation.getProvince().getId() : null;
            district = personContactInformation.getDistrict() != null ? personContactInformation.getDistrict().getId() : null;
            if(personContactInformation.getAddressUbigeo() != null){
                if(department == null && personContactInformation.getAddressUbigeo().getDepartment() != null) department =  personContactInformation.getAddressUbigeo().getDepartment().getId();
                if(province == null && personContactInformation.getAddressUbigeo().getProvince() != null) province =  personContactInformation.getAddressUbigeo().getProvince().getId();
                if(district == null && personContactInformation.getAddressUbigeo().getDistrict() != null) district =  personContactInformation.getAddressUbigeo().getDistrict().getId();
            }
            form.setReference(personContactInformation.getAddressDetail());
            form.setLatitude(personContactInformation.getAddressLatitude());
            form.setLongitude(personContactInformation.getAddressLongitude());
            form.setAggregatedAddress(personContactInformation.getAddressStreetName());
            model.addAttribute("latitude",personContactInformation.getAddressLatitude());
            model.addAttribute("longitude",personContactInformation.getAddressLongitude());
        }

        form.setDepartamento(department);
        form.setProvincia(province);
        form.setDistrito(district);
        form.setPostalCode(postalCode);
        if(disagregatedAddress != null){
            form.setRoadType(disagregatedAddress != null && disagregatedAddress.getTipoVia() != null ?  disagregatedAddress.getTipoVia() + "" : "");
            form.setRoadName(disagregatedAddress.getNombreVia());
            form.setWithoutNumber(disagregatedAddress.getWithoutNumber());
            form.setFloor(disagregatedAddress.getFloor());
            form.setHouseNumber(disagregatedAddress.getNumeroVia());
            form.setInterior(disagregatedAddress.getNumeroInterior());
            form.setManzana(disagregatedAddress.getManzana());
            form.setLote(disagregatedAddress.getLote());
            form.setReference(disagregatedAddress.getReferencia());
            form.setZoneType(disagregatedAddress.getTipoZona() != null ? disagregatedAddress.getTipoZona() + "" : "");
            form.setZoneName(disagregatedAddress.getNombreZona());
            form.setSearchQuery(disagregatedAddress.getSearchQuery());
            form.setLatitude(disagregatedAddress.getLatitude());
            form.setLongitude(disagregatedAddress.getLongitude());
            form.setAggregatedAddress(disagregatedAddress.getDireccionCompleta());
            personDAO.completeAddressCoordinates(person.getId(), disagregatedAddress);
            model.addAttribute("latitude",disagregatedAddress.getLatitude());
            model.addAttribute("longitude",disagregatedAddress.getLongitude());
        }

        AddressVerificationLoanExtranetPainter addressVerificationLoanExtranetPainter = new AddressVerificationLoanExtranetPainter();
        addressVerificationLoanExtranetPainter.setEmail(user.getEmail());
        addressVerificationLoanExtranetPainter.setPhoneNumber(user.getPhoneNumber());
        addressVerificationLoanExtranetPainter.setPersonContactInformation(personContactInformation);
        addressVerificationLoanExtranetPainter.setLoanId(loanId);
        addressVerificationLoanExtranetPainter.setAddress(disagregatedAddress != null ? disagregatedAddress.getDireccionCompleta() : (personContactInformation != null ? personContactInformation.getFullAddressBOWithReference() : ""));
        addressVerificationLoanExtranetPainter.setLatitude(personContactInformation != null ? personContactInformation.getAddressLatitude() : null);
        addressVerificationLoanExtranetPainter.setLatitude(personContactInformation != null ? personContactInformation.getAddressLongitude() : null);


        if(loanApplication != null){
            List<LoanApplicationTrackingAction> actions = loanApplicationDao.getLoanApplicationTrackingActionsByTrackingId(loanId,new Integer[]{TrackingAction.ADDRESS_VERIFICATION_CONTACTED,TrackingAction.ADDRESS_VERIFICATION_NO_RESPONSE,TrackingAction.ADDRESS_VERIFICATION_WRONG_NUMBER},null);
            if(actions != null && !actions.isEmpty()){
                LoanApplicationTrackingAction loanApplicationTrackingActionFirst = actions.stream().filter(e -> e.getTrackingAction().getTrackingActionId() != TrackingAction.TRACKING_PHONE_CALL).findFirst().orElse(null);
                addressVerificationLoanExtranetPainter.setAction(loanApplicationTrackingActionFirst != null ? loanApplicationTrackingActionFirst.getTrackingAction() : null);
            }
        }


        model.addAttribute("loanId",loanId);
        model.addAttribute("updatePersonAddressForm", form);
        model.addAttribute("addressVerificationData", addressVerificationLoanExtranetPainter);
        model.addAttribute("person",person);
        return new ModelAndView("/entityExtranet/extranetEvaluation :: addressVerificationData");
    }

    @RequestMapping(value = "/" + URL + "/request/{loanId}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loan:evaluate:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getRequestData(
            ModelMap model, Locale locale, HttpServletRequest request, @PathVariable("loanId") Integer loanId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanId,locale);
        String defaultTab = "general";
        model.addAttribute("defaultTab",defaultTab);

        return new ModelAndView("/entityExtranet/extranetEvaluation :: request");
    }

    @RequestMapping(value = URL +"/request/{loanId}/tab/{accordion}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loan:evaluate:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object loadLoanApplicationAccordion(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("loanId") Integer loanId,
            @PathVariable("accordion") String accordion) throws Exception {

        LoanApplicationExtranetRequestPainter loanApplication = entityExtranetService.getApplicationById(loanId, locale, request);
        String fragmentToRender = null;
        Person person = null;

        if (loanApplication == null) {
            return AjaxResponse.errorMessage("No existe el Loan Application");
        }
        model.addAttribute("loanId", loanId);
        Integer personId = loanApplication.getPersonId();
        switch (accordion) {
            case "general":{
                model.addAttribute("reasons" , catalogService.getLoanApplicationReasons(locale));
                fragmentToRender = "/entityExtranet/extranetEvaluation :: generalInformation";
                break;
            }
            case "evaluation":{
                fragmentToRender = "/entityExtranet/extranetEvaluation :: evaluationResult";
                List<LoanOffer> offers = loanApplicationDao.getLoanOffersAll(loanApplication.getId());
                loanApplication.setOffers(offers);
                model.addAttribute("offers", offers);
                break;
            }
            case "documentation": {
                List<LoanApplicationUserFiles> userFilesObjectList = personDAO.getUserFiles(personId, locale);

                List<PersonOcupationalInformation> ocupations = personDAO.getPersonOcupationalInformation(locale, loanApplication.getPersonId());
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
                model.addAttribute("person", personDAO.getPerson(catalogService, Configuration.getDefaultLocale(), personId, false));

                fragmentToRender = "/entityExtranet/extranetEvaluation :: documentation";

                break;
            }
            case "identity_validation": {
                SummaryIdentityValidationPainter identityValidationPainter = new SummaryIdentityValidationPainter();

                List<RecognitionResultsPainter> recognitions = loanApplicationDao.getRecognitionResults(personId, locale);
                if (recognitions != null) {
                    for (RecognitionResultsPainter recognition : recognitions) {
                        if (recognition.getLoanApplicationId() == null) continue;
                        if (recognition.getLoanApplicationId().equals(loanApplication.getId())) {
                            identityValidationPainter.setLastRekognition(recognition);
                        }
                    }
                }

                if(loanApplication.getSelectedEntityProductParameterId() != null){
                    EntityProductParams entityParams = catalogService.getEntityProductParamById(loanApplication.getSelectedEntityProductParameterId());
                    if(entityParams != null && entityParams.getEntityProductParamIdentityValidationConfig() != null && entityParams.getEntityProductParamIdentityValidationConfig().getRekognitionMinPercentage() != null)
                        identityValidationPainter.setRekognitionMinPercentage(entityParams.getEntityProductParamIdentityValidationConfig().getRekognitionMinPercentage());
                }

                List<MatiResult> matiResults = securityDAO.getMatiResultsByLoanApplication(loanId);
                if(matiResults != null && !matiResults.isEmpty())
                    identityValidationPainter.setMatiResult(matiResults.stream()
                            .sorted((e1, e2) -> e2.getRegisterDate().compareTo(e1.getRegisterDate()))
                            .collect(Collectors.toList())
                            .get(0));

                identityValidationPainter.setIdentityApprovalValidation(loanApplication.getApprovalValidations().stream().filter(a-> a.getApprovalValidationId() == ApprovalValidation.IDENTIDAD).findFirst().orElse(null));

                LoanApplicationUserFiles files = new LoanApplicationUserFiles();
                List<LoanApplicationUserFiles> userFiles = personDAO.getUserFiles(personId, locale);
                for (LoanApplicationUserFiles file : userFiles) {
                    if (file.getLoanApplicationId().equals(loanId)) {
                        files = file;
                        break;
                    }
                }

                if(files.getUserFileList().stream().filter(e -> e.getFileType().getId() == UserFileType.DNI_FRONTAL).findFirst().orElse(null) != null)
                    model.addAttribute("dniMerge", files.getUserFileList().stream().filter(e -> e.getFileType().getId() == UserFileType.DNI_FRONTAL).findFirst().orElse(null));
                if(files.getUserFileList().stream().filter(e -> e.getFileType().getId() == UserFileType.SELFIE).findFirst().orElse(null) != null)
                    model.addAttribute("selfie", files.getUserFileList().stream().filter(e -> e.getFileType().getId() == UserFileType.SELFIE).findFirst().orElse(null));
                if(files.getUserFileList().stream().filter(e -> e.getFileType().getId() == UserFileType.DNI_ANVERSO).findFirst().orElse(null) != null)
                    model.addAttribute("dniBack", files.getUserFileList().stream().filter(e -> e.getFileType().getId() == UserFileType.DNI_ANVERSO).findFirst().orElse(null));
                model.addAttribute("identityValidationPainter", identityValidationPainter);

                fragmentToRender = "/entityExtranet/extranetEvaluation :: identityValidation";

                break;
            }
            case "bank_account": {
                person = personDAO.getPerson(catalogService, locale, personId, true);

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

                if(loanApplication.getSelectedEntityProductParameterId() != null){
                    switch (loanApplication.getSelectedEntityProductParameterId()){
                        case EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE:
                            AztecaPreApprovedBase aztecaPreApprovedBase = null;
                            if(loanApplication.getEntityCustomData() != null && loanApplication.getEntityCustomData().has(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_BASE_PREAPROBADA.getKey())){
                                aztecaPreApprovedBase = new Gson().fromJson(loanApplication.getEntityCustomData().getJSONObject(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_BASE_PREAPROBADA.getKey()).toString(),AztecaPreApprovedBase.class);
                            }
                            if(aztecaPreApprovedBase != null){
                                if(aztecaPreApprovedBase.getIdCampania().equals(AztecaPreApprovedBase.CALL_CENTER_CAMP)) model.addAttribute("callApprovalMethod", true);
                            }
                            break;
                    }
                }

                List<Bank> permittedBanks = getPermittedBanks(loanApplication.getId(), request);
                model.addAttribute("banks", permittedBanks);
                model.addAttribute("bankAccount", personDAO.getPersonBankAccountInformation(locale, personId));
                model.addAttribute("employeeBankAccounts", personDAO.getEmployeesByDocument(person.getDocumentType().getId(), person.getDocumentNumber(), locale));


                fragmentToRender = "/entityExtranet/extranetEvaluation :: bankAccount";
                break;
            }
            case "notes": {
                model.addAttribute("notes", loanApplicationDao.getLoanApplicationComments(loanApplication.getId(), Comment.COMMENT_EVALUATION));

                fragmentToRender = "/entityExtranet/extranetEvaluation :: notes";
                break;
            }
            case "sbs": {
//                if (!SecurityUtils.getSubject().isPermitted("person:sbsTab:view")) {
//                    return "403";
//                }
                Optional<PersonOcupationalInformation> principalOcupation = Optional.empty();
                List<PersonOcupationalInformation> ocupations = personDAO.getPersonOcupationalInformation(locale, personId);
                if (ocupations != null) {
                    principalOcupation = ocupations.stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst();
                    principalOcupation.ifPresent(x -> model.addAttribute("personPrincipalOcupationalInfo", x));
                }
                person = personDAO.getPerson(catalogService, locale, personId, true);
                model.addAttribute("personCountry", person != null ? person.getCountry().getId() : null);
                switch (person.getCountry().getId()) {
                    case CountryParam.COUNTRY_ARGENTINA:
                        BcraResult bcraResult = personDAO.getBcraResult(personId);
                        if (bcraResult != null && bcraResult.getHistorialDeudas() != null && bcraResult.getHistorialDeudas().size() > 0) {
                            List<String> banks = bcraResult.getHistorialDeudas().stream().map(h -> h.getNombre()).collect(Collectors.toList());
                            List<String> periods = bcraResult.getHistorialDeudas().get(0).getHistorial().stream().map(e -> e.getPeriodo()).collect(Collectors.toList());
                            model.addAttribute("banks", banks);
                            model.addAttribute("periods", periods);
                        }
                        model.addAttribute("bcraResult", bcraResult);

                        break;
                    case CountryParam.COUNTRY_PERU:
//                        model.addAttribute("personRccCal", personDAO.getPersonRccCalification(personId));
//                        model.addAttribute("personRcc", personDAO.getPersonRcc(personId));

                        //Nuevo RCC
                        personDAO.generateSynthesizedByDocument(person.getDocumentNumber());
                        List<Synthesized> synthesizeds = personDAO.getSynthesizeds(person.getDocumentNumber());
                        List<RccIdeGrouped> ideGroupeds = personDAO.getIdeGroupeds(person.getDocumentNumber());

                        RccResponse rccResponse = new RccResponse();
                        rccResponse.setIdeGroupeds(ideGroupeds);
                        rccResponse.setSynthesizeds(synthesizeds);

                        if (rccResponse.getIdeGroupeds() != null) {
                            rccResponse.performFinancialSystems(utilService);
                        }
                        model.addAttribute("rccData", rccResponse);

                        break;
                }

                Person partner = person.getPartner();

                if (partner != null) {

                    personDAO.generateSynthesizedByDocument(partner.getDocumentNumber());
                    List<Synthesized> partnerSynthesizeds = personDAO.getSynthesizeds(partner.getDocumentNumber());
                    List<RccIdeGrouped> partnerIdeGroupeds = personDAO.getIdeGroupeds(partner.getDocumentNumber());

                    RccResponse partnetRccResponse = new RccResponse();
                    partnetRccResponse.setIdeGroupeds(partnerIdeGroupeds);
                    partnetRccResponse.setSynthesizeds(partnerSynthesizeds);

                    if (partnetRccResponse.getIdeGroupeds() != null) {
                        partnetRccResponse.performFinancialSystems(utilService);
                    }
                    model.addAttribute("partnerRccData", partnerIdeGroupeds);
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

                        model.addAttribute("rucRccCal", personDAO.getRucRccCalification(personOcupationalInformation.getRuc()));
                        model.addAttribute("rucRcc", personDAO.getRucRcc(personOcupationalInformation.getRuc()));
                    } else {
                        List<Credit> credits = creditDao.getCreditsByPerson(personId, locale, Credit.class);

                        if (credits != null &&
                                credits.stream()
                                        .filter(c -> c.getEntity().getId() == Entity.CREDIGOB)
                                        .findFirst()
                                        .orElse(null) != null) {

                            model.addAttribute("rucRccCal", personDAO.getRucRccCalification(personOcupationalInformation.getRuc()));
                            model.addAttribute("rucRcc", personDAO.getRucRcc(personOcupationalInformation.getRuc()));
                        }
                    }
                }

                fragmentToRender = "/entityExtranet/extranetEvaluation :: sbs";

                break;
            }
            case "interaction": {
                List<PersonInteraction> personInteractions = interactionDAO.getPersonInteractionsByLoanApplication(personId, loanApplication.getId(), locale);
                List<Object> interactions = personInteractions != null ? personInteractions.stream().filter(pi -> loanApplication.getId().equals(pi.getLoanApplicationId())).collect(Collectors.toList()) : new ArrayList<>();
                UserEmail activeUserEmail = userDAO.getUserEmails(loanApplication.getUserId()).stream().filter(u -> u.getActive() != null && u.getActive()).findFirst().orElse(null);

                for (Object interactionObject : interactions) {
                    List<PersonInteractionStat> stats = null;

                    PersonInteraction interaction = (PersonInteraction) interactionObject;

                    if (null != interaction.getInteractionProvider() && interaction.getInteractionProvider().getId() == InteractionProvider.AWS) {
                        Map<String, Date> map = awsElasticSearchClient.getEmailEventsByPersonInteractionId(interaction.getId());

                        stats = new ArrayList<PersonInteractionStat>();
                        for (Map.Entry<String, Date> entry : map.entrySet()) {
                            PersonInteractionStat stat = new PersonInteractionStat();
                            stat.setEventDate(entry.getValue());
                            stat.setEvent(entry.getKey());
                            stats.add(stat);
                        }

                        if (map.keySet().contains("Abierto") && activeUserEmail != null && (activeUserEmail.getVerified() != null && !activeUserEmail.getVerified())) {
                            userDAO.verifyEmail(loanApplication.getUserId(), activeUserEmail.getId(), true);
                        }

                        interaction.setStats(stats);
                    }
                }


                PersonInteractionExtranetPainter painter = new PersonInteractionExtranetPainter();
                painter.setLoanApplicationId(loanApplication.getId());
                painter.setLoanApplicationCode(loanApplication.getCode());
                painter.setRegisterDate(loanApplication.getRegisterDate());
                painter.setInteractions(interactions);

                loanApplication.setInteractions(painter);

                model.addAttribute("interaction", loanApplication.getInteractions());
                model.addAttribute("interactionsParam", loanApplication.getInteractions().getInteractions());
                model.addAttribute("code", loanApplication.getInteractions().getCreditCode() != null ? loanApplication.getInteractions().getCreditCode() : loanApplication.getInteractions().getLoanApplicationCode());

                fragmentToRender = "/entityExtranet/extranetEvaluation :: interaction";

                break;
            }
            case "fraud_alert": {

                if (loanApplication.getFraudAlertQueryIds() != null) {
                    Integer lastExecutedBot = loanApplication.getFraudAlertQueryIds().stream().sorted(Comparator.reverseOrder()).findFirst().orElse(null);
                    if (lastExecutedBot != null) {
                        QueryBot queryBot = botDao.getQueryBot(lastExecutedBot);
                        if (queryBot != null) {
                            loanApplication.setLastFraudAlertsBotRegisterDate(queryBot.getRegisterDate());
                            loanApplication.setLastFraudAlertsBotStatusId(queryBot.getStatusId());
                        }
                    }
                }

                model.addAttribute("groupedLoanApplicationFraudAlerts", getGroupedPersonFraudAlertsByApplication(loanApplication, false));
                fragmentToRender = "/entityExtranet/extranetEvaluation :: fraudAlert";
                break;
            }
            default:
                break;
        }

        model.addAttribute("loanApplication", loanApplication);
        model.addAttribute("person", personDAO.getPerson(catalogService, locale, personId, true));

        return new ModelAndView(fragmentToRender);
    }


    @RequestMapping(value = URL+"/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:loan:evaluate:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showEvaluationList(
            ModelMap model, Locale locale, PaginatorEvaluationTableFilterForm filter) throws Exception {


        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        Integer offset = 0;
        Integer limit = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        if(filter != null) limit = filter.getLimit();
        if(filter != null && filter.getPage() != null && limit != null) {
            offset = filter.getPage() * limit;
        }
        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        String offerStartDate = null;
        String offerEndDate = null;
        Integer[] analyst = null;
        List<Integer> product = null;
        Integer[] progress = null;
        Integer[] entityProductsParam = null;

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.EVALUATION_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());

        if(filter != null){
            creationFrom = filter.getCreationFrom() != null && !filter.getCreationFrom().isEmpty() ? filter.getCreationFrom() : null;
            creationTo = filter.getCreationTo() != null && !filter.getCreationTo().isEmpty() ? filter.getCreationTo() : null;
            offerStartDate = filter.getOfferStartDate() != null && !filter.getOfferStartDate().isEmpty() ? filter.getOfferStartDate() : null;
            offerEndDate = filter.getOfferEndDate() != null && !filter.getOfferEndDate().isEmpty() ? filter.getOfferEndDate() : null;
            if(filter.getAnalyst() != null && !filter.getAnalyst().isEmpty()) {
                analyst = new Gson().fromJson(filter.getAnalyst(), Integer[].class);
            }
            if(filter.getProduct() != null) {
                product = new Gson().fromJson(filter.getProduct(), new TypeToken<ArrayList<Integer>>(){}.getType());
                List<Integer> finalProduct = product;
                products = products.stream().filter(e-> finalProduct.contains(e.getId())).collect(Collectors.toList());
            }
            if(filter.getProgress() != null) {
                progress = new Gson().fromJson(filter.getProgress(), Integer[].class);
            }

            if(filter.getDisbursementType() != null) {
                Integer[] disbursementTypes = new Gson().fromJson(filter.getDisbursementType(), Integer[].class);
                if(disbursementTypes.length > 0){
                    List<Product> finalProducts = products;
                    entityProductsParam = catalogService.getEntityProductParams().stream().filter(e -> e.getEntity().getId().equals(loggedUserEntity.getPrincipalEntity().getId()) && e.getExtranetCreditGeneration() != null && e.getExtranetCreditGeneration() && finalProducts.stream().anyMatch(pr -> pr.getId().equals(e.getProduct().getId())) && Arrays.stream(disbursementTypes).anyMatch(d->d.equals(e.getDisbursementType()))).map(EntityProductParams::getId).toArray(Integer[]::new);
                }
            }
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }

        if(offset < 0) offset = 0;

        if(entityProductsParam == null){
            List<Product> finalProducts1 = products;
            entityProductsParam = catalogService.getEntityProductParams().stream()
                    .filter(p -> p.getEntity().getId().equals(loggedUserEntity.getPrincipalEntity().getId()))
                    .filter(p -> finalProducts1.stream().anyMatch(e->e.getId().equals(p.getProduct().getId())))
                    .map(EntityProductParams::getId).toArray(Integer[]::new);
        }

        List<LoanApplicationToEvaluationExtranetPainter> loans = creditDao.getEntityToEvaluateLoanApplications(loggedUserEntity.getPrincipalEntity().getId(),
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                searchValue,
                offerStartDate != null && !offerStartDate.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(offerStartDate) : null,
                offerEndDate != null && !offerEndDate.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(offerEndDate) : null,
                analyst,
                entityProductsParam,
                progress,
                offset,
                limit, false, locale);
        List<EntityExtranetUser> users = userDAO.getEntityExtranetUsers(loggedUserEntity.getPrincipalEntity().getId(), null, null)
                .stream().filter(u -> u.getRoles() != null && u.getRoles().contains(Role.EXTTRANET_BANDEJA_EVALUACION_VISTA))
                .collect(Collectors.toList());;
        model.addAttribute("offset", offset);
        model.addAttribute("loans", loans);
        model.addAttribute("users", users);
        model.addAttribute("limitPaginator", limit);
        model.addAttribute("title", "Evaluación");
        model.addAttribute("page", "evaluation");
        model.addAttribute("tray", ExtranetMenu.EVALUATION_MENU);
        return new ModelAndView("/entityExtranet/extranetEvaluation :: list");
    }

    @RequestMapping(value = URL+"/count", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:loan:evaluate:view", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showBeingProcessedCount(
            ModelMap model, Locale locale, PaginatorEvaluationTableFilterForm filter) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        String offerStartDate = null;
        String offerEndDate = null;
        Integer[] analyst = null;
        List<Integer> product = null;
        Integer[] progress = null;

        Integer[] entityProductsParam = null;

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.EVALUATION_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());
        List<Integer> productCategories = menuEntityProductCategory.getProductCategories();

        if(filter != null){
            creationFrom = filter.getCreationFrom() != null && !filter.getCreationFrom().isEmpty() ? filter.getCreationFrom() : null;
            creationTo = filter.getCreationTo() != null && !filter.getCreationTo().isEmpty() ? filter.getCreationTo() : null;
            offerStartDate = filter.getOfferStartDate() != null && !filter.getOfferStartDate().isEmpty() ? filter.getOfferStartDate() : null;
            offerEndDate = filter.getOfferEndDate() != null && !filter.getOfferEndDate().isEmpty() ? filter.getOfferEndDate() : null;
            if(filter.getAnalyst() != null && !filter.getAnalyst().isEmpty()) {
                analyst = new Gson().fromJson(filter.getAnalyst(), Integer[].class);
            }
            if(filter.getProgress() != null) {
                progress = new Gson().fromJson(filter.getProgress(), Integer[].class);
            }
            if(filter.getProduct() != null) {
                product = new Gson().fromJson(filter.getProduct(), new TypeToken<ArrayList<Integer>>(){}.getType());
                List<Integer> finalProduct = product;
                products = products.stream().filter(e-> finalProduct.contains(e.getId())).collect(Collectors.toList());
            }
            if(filter.getProgress() != null) {
                progress = new Gson().fromJson(filter.getProgress(), Integer[].class);
            }
            if(filter.getDisbursementType() != null) {
                Integer[] disbursementTypes = new Gson().fromJson(filter.getDisbursementType(), Integer[].class);
                if(disbursementTypes.length > 0){
                    List<Product> finalProducts = products;
                    entityProductsParam = catalogService.getEntityProductParams().stream().filter(e -> e.getEntity().getId().equals(loggedUserEntity.getPrincipalEntity().getId()) && e.getExtranetCreditGeneration() != null && e.getExtranetCreditGeneration() && finalProducts.stream().anyMatch(pr -> pr.getId().equals(e.getProduct().getId())) && Arrays.stream(disbursementTypes).anyMatch(d->d.equals(e.getDisbursementType()))).map(EntityProductParams::getId).toArray(Integer[]::new);
                }
            }
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }

        if(entityProductsParam == null){
            List<Product> finalProducts1 = products;
            entityProductsParam = catalogService.getEntityProductParams().stream()
                    .filter(p -> p.getEntity().getId().equals(loggedUserEntity.getPrincipalEntity().getId()))
                    .filter(p -> finalProducts1.stream().anyMatch(e->e.getId().equals(p.getProduct().getId())))
                    .map(EntityProductParams::getId).toArray(Integer[]::new);
        }

        Pair<Integer, Double> countAdSum = creditDao.getEntityToEvaluateLoanApplicationsCount(loggedUserEntity.getPrincipalEntity().getId(),
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                searchValue,
                offerStartDate != null && !offerStartDate.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(offerStartDate) : null,
                offerEndDate != null && !offerEndDate.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(offerEndDate) : null,
                analyst,
                entityProductsParam,
                progress,
                locale);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("count",countAdSum.getLeft());
        model.addAttribute("tray", ExtranetMenu.EVALUATION_MENU);
        return AjaxResponse.ok(new Gson().toJson(data));
    }

    @RequestMapping(value = "/" + URL +"/action/assignAnalyst", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:analyst:assign", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object assignAnalyst(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("loanApplicationId") Integer loanApplicationId,
            @RequestParam("entityUserId") Integer entityUserId) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        loanApplicationDao.updateAssignedEntityUserId(loanApplicationId, entityUserId, loggedUserEntity != null ? loggedUserEntity.getId() : null);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/" + URL +"/action/resend_validation_email", method = RequestMethod.POST)
//    @RequiresPermissionOr403(permissions = "loan:analyst:resend_validation_email", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object resendValidationEmail(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);

        Integer userId = userDAO.getUserIdByPersonId(loanApplication.getPersonId());
        Integer personId = loanApplication.getPersonId();
        User user = userDAO.getUser(userId);
        Integer creditId = null;
        List<UserEmail> userEmails = userDAO.getUserEmails(userId);
        UserEmail userEmail = userEmails.stream().filter(e->e.getActive()).findFirst().orElse(null);

//                    ALWAYS SEND VERIFICATION EMAIL
        userDAO.verifyEmail(userId, userEmail.getId(), false);// return email to is_verified = false for users.tb_users and users.tb_email

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
        verificationToken.put("emailId", userEmail.getId());
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
                List<PersonInteraction> personInteractions = interactionDAO.getPersonInteractionsByLoanApplication(loanApplication.getPersonId(), loanApplicationId, Configuration.getDefaultLocale());
                int[] interactions = personInteractions != null ?
                        personInteractions
                                .stream()
                                .filter(pi -> loanApplicationId.equals(pi.getLoanApplicationId()))
                                .filter(pi -> pi.getInteractionProvider() != null && pi.getInteractionProvider().getId() == InteractionProvider.AWS)
                                .map(PersonInteraction::getId)
                                .mapToInt(i -> i)
                                .toArray() : new int[]{};

                Map<String, Date> map = awsElasticSearchClient.getEmailEventsByPersonInteractionIds(interactions);
                if (map.keySet().contains("Abierto")) {
                    userDAO.verifyEmail(userId, userEmail.getId(), true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/" + URL +"/action/unassignAnalyst", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:analyst:unassign", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object unassignAnalyst(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        loanApplicationDao.updateAssignedEntityUserId(loanApplicationId, null, loggedUserEntity != null ? loggedUserEntity.getId() : null);
        return AjaxResponse.ok(null);
    }

    private List<Bank> getPermittedBanks(int loanApplicationId, HttpServletRequest request) throws Exception {
        EntityProductParams entityProductParams = null;
        List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplicationId);
        LoanOffer selectedoffer = offers.stream().filter(o -> o.getSelected()).findFirst().orElse(null);
        entityProductParams = catalogService.getEntityProductParam(selectedoffer.getEntityId(), selectedoffer.getProduct().getId());

        if (entityProductParams != null && entityProductParams.getDisbursementBanks() != null) {
            List<Bank> addmittedBanks = new ArrayList<>();
            for (Integer i : entityProductParams.getDisbursementBanks()) {
                addmittedBanks.add(catalogService.getBank(i));
            }
            return addmittedBanks;
        }
        return catalogService.getBanks(0, true, countryContextService.getCountryParamsByRequest(request));
    }

    @RequestMapping(value = URL +"/update/{loanId}/field/{field}", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:field:update", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> updateValuesLoanApplication(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("loanId") Integer loanId,
            @PathVariable("field") String field,
            @RequestParam(value = "value", required = false) String value,
            @RequestParam(value = "partnerDocumentType", required = false) Integer partnerDocumentType,
            Question28Form occupationalPhoneNumberForm
            ) throws Exception {

        LoanApplicationExtranetRequestPainter loanApplication = entityExtranetService.getApplicationById(loanId, locale, request);
        Person person;
        FieldValidator validator;
        Integer personId;
        if(loanApplication == null) return null;
        personId = loanApplication.getPersonId();
        person = personDAO.getPerson(catalogService, locale, personId, true);
        Integer creditId = null;
        if(loanApplication.getCredit() != null && loanApplication.getCredit()){

        }
        switch (field) {
            case "bankAccount":
                // Validate the permission
//                if (!SecurityUtils.getSubject().isPermitted("person:bankInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new StringFieldValidator(ValidatorUtil.BANK_ACCOUNT_NUMBER, value);
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                personDAO.updatePersonAccountByCredit(personId, creditId, null, value, null, null);
                personDAO.updatebankAccountVerified(false, personId);
                //If we need to update the personal informacion as well, uncomment next line
                //personDao.updatePersonBankAccountInformation(personId, null, null, value, null, null);
                break;
            case "cciCode":
                // Validate the permission
//                if (!SecurityUtils.getSubject().isPermitted("person:bankInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                Integer countryId = personDAO.getPerson(catalogService, locale, personId, false).getDocumentType().getCountryId();
                switch (countryId) {
                    case CountryParam.COUNTRY_PERU:
                        validator = new StringFieldValidator(ValidatorUtil.BANK_CCI_NUMBER, value);
                        if (!validator.validate(locale)) {
                            return AjaxResponse.errorMessage(validator.getErrors());
                        }

                        if(loanApplication.getSelectedEntityProductParameterId() != null && loanApplication.getSelectedEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE) {
                            // Validate that the bank account is inside the cci
                            PersonBankAccountInformation pbai = personDAO.getPersonBankAccountInformation(Configuration.getDefaultLocale(), loanApplication.getPersonId());
                            if (pbai.getBankAccount() != null && !value.contains(pbai.getBankAccount())) {
                                return AjaxResponse.errorMessage("El CCI no pertenece al nro. de cuenta");
                            }
                        }

                        personDAO.updatePersonAccountByCredit(personId, creditId, null, null, null, value);
                        break;
                    case CountryParam.COUNTRY_ARGENTINA:
                        validator = new StringFieldValidator(ValidatorUtil.BANK_CBU_NUMBER, value);
                        if (!validator.validate(locale)) {
                            return AjaxResponse.errorMessage(validator.getErrors());
                        }
                        PersonBankAccountInformation bankAccountInformation = personDAO.getPersonBankAccountInformation(locale, personId);
                        try {
                            if (!BankAccountValidator.validateCBU(value, catalogService.getBanks(0, true, countryId), bankAccountInformation.getBank())) {
                                return AjaxResponse.errorMessage("CBU inválido");
                            }
                        } catch (Exception e) {
                            return AjaxResponse.errorMessage("CBU inválido");
                        }


                        personDAO.updatePersonAccountByCredit(personId, creditId, null, value.substring(8, 21), null, value);
                        break;
                }
                personDAO.updatebankAccountVerified(false, personId);
                loanApplicationApprovalValidationService.validateAndUpdate(loanApplication.getId(), ApprovalValidation.CCI);
                //If we need to update the personal informacion as well, uncomment next line
                //personDao.updatePersonBankAccountInformation(personId, null, null, null, null, value);
                break;
            case "bankDepartment":
                // Validate the permission
//                if (!SecurityUtils.getSubject().isPermitted("person:bankInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new StringFieldValidator(ValidatorUtil.DEPARTMENT, value);
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                personDAO.updatePersonBankAccountInformation(personId, null, null, null, value + "0000", null);
                break;
            case "bank":
                // Validate the permission
//                if (!SecurityUtils.getSubject().isPermitted("person:bankInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new IntegerFieldValidator(ValidatorUtil.BANK_ID, Integer.parseInt(value));
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                personDAO.updatePersonAccountByCredit(personId, creditId, Integer.parseInt(value), null, null, null);
                //If we need to update the personal informacion as well, uncomment next line
                personDAO.updatePersonBankAccountInformation(personId, Integer.parseInt(value), null, null, null, null);
                personDAO.updatebankAccountVerified(false, personId);
                break;
            case "bankAccountType":
                // Validate the permission
//                if (!SecurityUtils.getSubject().isPermitted("person:bankInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new CharFieldValidator(ValidatorUtil.BANK_ACCOUNT_TYPE, value.charAt(0));
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                personDAO.updatePersonBankAccountInformation(personId, null, value.charAt(0), null, null, null);
                personDAO.updatebankAccountVerified(false, personId);
                break;
            case "nationality":
                // Validate the permission
//                if (!SecurityUtils.getSubject().isPermitted("person:personalInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new IntegerFieldValidator(ValidatorUtil.NATIONALITY, Integer.parseInt(value));
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                personDAO.updateNationality(personId, Integer.parseInt(value));
                break;
            case "maritalStatus":
                // Validate the permission
//                if (!SecurityUtils.getSubject().isPermitted("person:personalInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new IntegerFieldValidator(ValidatorUtil.MARITAL_STATUS_ID, Integer.parseInt(value));
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                personDAO.updateMaritalStatus(personId, Integer.parseInt(value));
                break;
            case "dependents":
                // Validate the permission
//                if (!SecurityUtils.getSubject().isPermitted("person:personalInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new IntegerFieldValidator(ValidatorUtil.DEPENDENTS, Integer.parseInt(value));
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                personDAO.updateDependents(personId, Integer.parseInt(value));
                break;
            case "email":
                // Validate the permission
//                if (!SecurityUtils.getSubject().isPermitted("person:contactInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new StringFieldValidator(ValidatorUtil.EMAIL, value);
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }

                if (loanId == null) {
                    return AjaxResponse.errorMessage("No se encontró valor: applicationId");
                }

                Integer userId = userDAO.getUserIdByPersonId(personId);
                User user = userDAO.getUser(userId);
                int emailId = userDAO.registerEmailChange(userId, value);

                userDAO.validateEmailChange(userId, emailId);

//                    ALWAYS SEND VERIFICATION EMAIL
                userDAO.verifyEmail(userId, emailId, false);// return email to is_verified = false for users.tb_users and users.tb_email

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
                        List<PersonInteraction> personInteractions = interactionDAO.getPersonInteractionsByLoanApplication(loanApplication.getPersonId(), loanId, Configuration.getDefaultLocale());
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
                            userDAO.verifyEmail(userId, emailId, true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                break;
            case "phoneNumber":
                // Validate the permission
//                if (!SecurityUtils.getSubject().isPermitted("person:personalInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                person = personDAO.getPerson(catalogService, locale, personId, false);
                switch (person.getDocumentType().getCountryId()) {
                    case CountryParam.COUNTRY_PERU:
                        validator = new StringFieldValidator(ValidatorUtil.CELLPHONE_NUMBER, value);
                        if (!validator.validate(locale)) {
                            return AjaxResponse.errorMessage(validator.getErrors());
                        }
                        personDAO.updatePhoneNumber(person.getUserId(), "51", value);
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
                        personDAO.updatePhoneNumber(person.getUserId(), "54", "(" + form.getAreaCode() + ") " + form.getPhoneNumber());
                        break;
                }
                break;
            case "principalActivityType":
//                if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new IntegerFieldValidator(ValidatorUtil.ACTIVITY_TYPE_ID, Integer.parseInt(value));
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                if(personService.getCurrentOcupationalInformation(personId, locale) == null) personDAO.cleanOcupationalInformation(personId, PersonOcupationalInformation.PRINCIPAL);
                personDAO.updateOcupationalActivityType(personId, 1, Integer.parseInt(value));
                personDAO.validateOcupationalInformation(personId, false);
                personDAO.updateOcupationalInformationBoChanged(personId, 1, true);
                break;
            case "principalSubActivityType":
//                if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new IntegerFieldValidator(ValidatorUtil.ACTIVITY_TYPE_ID, Integer.parseInt(value));
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                if(personService.getCurrentOcupationalInformation(personId, locale) == null) personDAO.cleanOcupationalInformation(personId, PersonOcupationalInformation.PRINCIPAL);
                personDAO.updateOcupatinalSubActivityType(personId, 1, Integer.parseInt(value));
                personDAO.validateOcupationalInformation(personId, false);
                personDAO.updateOcupationalInformationBoChanged(personId, 1, true);
                break;
            case "principalDependentRuc":
//                if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new StringFieldValidator(ValidatorUtil.RUC, value);
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                if(personService.getCurrentOcupationalInformation(personId, locale) == null) personDAO.cleanOcupationalInformation(personId, PersonOcupationalInformation.PRINCIPAL);
                personDAO.updateOcupationalRuc(personId, 1, value);
                personDAO.validateOcupationalInformation(personId, false);
                personDAO.updateOcupationalInformationBoChanged(personId, 1, true);
                person = personDAO.getPerson(catalogService, locale, personId, false);

                break;
            case "principalDependentCompany":
//                if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new StringFieldValidator(ValidatorUtil.COMPANY_NAME, value);
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                if(personService.getCurrentOcupationalInformation(personId, locale) == null) personDAO.cleanOcupationalInformation(personId, PersonOcupationalInformation.PRINCIPAL);
                personDAO.updateOcupationalCompany(personId, 1, value);
                personDAO.validateOcupationalInformation(personId, false);
                personDAO.updateOcupationalInformationBoChanged(personId, 1, true);
                break;
            case "principalDependentCiiu":
//                if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new StringFieldValidator(ValidatorUtil.CIIU, value);
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                if(personService.getCurrentOcupationalInformation(personId, locale) == null) personDAO.cleanOcupationalInformation(personId, PersonOcupationalInformation.PRINCIPAL);
                personDAO.updateOcupationalCiiu(personId, 1, value);
                personDAO.validateOcupationalInformation(personId, false);
                personDAO.updateOcupationalInformationBoChanged(personId, 1, true);
                break;
            case "principalOcupation":
//                if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new IntegerFieldValidator(ValidatorUtil.OCUPATION_ID, Integer.parseInt(value));
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                personDAO.updateOcupatinalOcupation(personId, 1, Integer.parseInt(value));
                personDAO.validateOcupationalInformation(personId, false);
                personDAO.updateOcupationalInformationBoChanged(personId, 1, true);
                break;
            case "principalSector":
//                if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new StringFieldValidator(ValidatorUtil.SECTOR_ID, value);
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
//                if(personService.getCurrentOcupationalInformation(personId, locale) == null) personDAO.cleanOcupationalInformation(personId, PersonOcupationalInformation.PRINCIPAL);
//                personDAO.updateOcupationalSector(personId, 1, value);
//                personDAO.validateOcupationalInformation(personId, false);
//                personDAO.updateOcupationalInformationBoChanged(personId, 1, true);
                personDAO.updateProfession(loanApplication.getPersonId(), Integer.valueOf(value));
                break;
            case "principalDependentTime":
//                if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new StringFieldValidator(ValidatorUtil.ACTIVITY_TIME, value);
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                if(personService.getCurrentOcupationalInformation(personId, locale) == null) personDAO.cleanOcupationalInformation(personId, PersonOcupationalInformation.PRINCIPAL);
                personDAO.updateEmploymentTime(personId, 1, value);
                break;
            case "principalDependentGrossIncome":
//                if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }

                validator = getGrossIncomeValidator(personId, locale, value);

                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                if(personService.getCurrentOcupationalInformation(personId, locale) == null) personDAO.cleanOcupationalInformation(personId, PersonOcupationalInformation.PRINCIPAL);
                personDAO.updateFixedGrossIncome(personId, 1, Integer.parseInt(value));
                personDAO.validateOcupationalInformation(personId, false);
                personDAO.updateOcupationalInformationBoChanged(personId, 1, true);
                updateRCI(loanId);
                break;

            case "principalDependentVariableGrossIncome":
//                if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = getGrossIncomeValidator(personId, locale, value);
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                if(personService.getCurrentOcupationalInformation(personId, locale) == null) personDAO.cleanOcupationalInformation(personId, PersonOcupationalInformation.PRINCIPAL);
                personDAO.updateVariableGrossIncome(personId, 1, Integer.parseInt(value));
                personDAO.validateOcupationalInformation(personId, false);
                personDAO.updateOcupationalInformationBoChanged(personId, 1, true);
                updateRCI(loanId);
                break;

            case "workPhone":
//                if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                if (occupationalPhoneNumberForm == null) {
                    return AjaxResponse.errorMessage("No hay datos enviados");
                }

                person = personDAO.getPerson(catalogService, locale, personId, false);

                ((Question28Form.Validator) occupationalPhoneNumberForm.getValidator()).configValidator(person.getDocumentType().getCountryId());
                occupationalPhoneNumberForm.getValidator().validate(locale);

                if (occupationalPhoneNumberForm.getValidator().isHasErrors())
                    return AjaxResponse.errorFormValidation(occupationalPhoneNumberForm.getValidator().getErrorsJson());

                if(personService.getCurrentOcupationalInformation(personId, locale) == null) personDAO.cleanOcupationalInformation(personId, PersonOcupationalInformation.PRINCIPAL);

                registerPhoneNumber(
                        personId,
                        occupationalPhoneNumberForm.getTypePhone(),
                        (occupationalPhoneNumberForm.getPhoneCode() != null && !occupationalPhoneNumberForm.getPhoneCode().isEmpty() ?
                                "(" + occupationalPhoneNumberForm.getPhoneCode() + ") " :
                                "") + occupationalPhoneNumberForm.getPhoneNumber());

                personDAO.validateOcupationalInformation(personId, false);
                personDAO.updateOcupationalInformationBoChanged(personId, 1, true);
                break;
            case "partnerDocument":
                // Validate the permission
//                if (!SecurityUtils.getSubject().isPermitted("person:personalInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                if (partnerDocumentType == null) {
                    return AjaxResponse.errorMessage("Los valores enviados no son válidos");
                }

                validator = new StringFieldValidator(partnerDocumentType == IdentityDocumentType.DNI ? ValidatorUtil.DOC_NUMBER_DNI : ValidatorUtil.DOC_NUMBER_CE, value);
                if (validator.validate(locale)) {
                    Integer partnerPersonId = personDAO.getPersonIdByDocument(partnerDocumentType, value);
                    // If the person doesnt exist, create it
                    if (partnerPersonId == null) {
                        // Validate the permission
//                        if (!SecurityUtils.getSubject().isPermitted("person:create")) {
//                            return AjaxResponse.errorForbidden();
//                        }
                        partnerPersonId = personDAO.createPerson(partnerDocumentType, value, locale).getId();
                    }
                    personDAO.updatePartner(personId, partnerPersonId);
                    webscrapperService.callRunSynthesized(value, null);
                    if (partnerDocumentType == IdentityDocumentType.DNI) {
                        personService.updatePersonDataFromReniecBD(partnerPersonId, value);
                    }

                    loanApplicationDao.boResetContract(loanId);
                }
                else return AjaxResponse.errorMessage("El número de documento no es válido");
                break;
            case "partnerName":
            case "partnerFirstSurname":
            case "partnerLastSurname":
            case "partnerBirthday":
                // Validate the permission
//                if (!SecurityUtils.getSubject().isPermitted("person:personalInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                // Verify that the partner exists
                person = personDAO.getPerson(catalogService, locale, personId, true);
                if (person.getPartner() == null) {
                    return AjaxResponse.errorMessage("El conyugue/ conviviente no existe");
                }
                if (field.equals("partnerName")) {
                    validator = new StringFieldValidator(ValidatorUtil.NAME, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDAO.updateName(person.getPartner().getId(), value);
                } else if (field.equals("partnerFirstSurname")) {
                    validator = new StringFieldValidator(ValidatorUtil.FIRST_SURNAME, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDAO.updateFirstSurname(person.getPartner().getId(), value);
                } else if (field.equals("partnerLastSurname")) {
                    validator = new StringFieldValidator(ValidatorUtil.LAST_SURNAME, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDAO.updateLastSurname(person.getPartner().getId(), value);
                } else if (field.equals("partnerBirthday")) {
                    validator = new StringFieldValidator(ValidatorUtil.BIRTHDAY, value);
                    if (!validator.validate(locale)) {
                        return AjaxResponse.errorMessage(validator.getErrors());
                    }
                    personDAO.updateBirthday(person.getPartner().getId(), new SimpleDateFormat(com.affirm.system.configuration.Configuration.BACKOFFICE_FRONT_ONLY_DATE_FORMAT).parse(value));
                }
                break;
            case "pep":
                // Validate the permission
//                if (!SecurityUtils.getSubject().isPermitted("person:bankInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new BooleanFieldValidator(Boolean.valueOf(value));
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                personDAO.updatePepInformation(personId,Boolean.valueOf(value),person.getPepDetail());
                //If we need to update the personal informacion as well, uncomment next line
                //personDao.updatePersonBankAccountInformation(personId, null, null, value, null, null);
                break;
            case "professionOccupation":
//                if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new IntegerFieldValidator(ValidatorUtil.OCUPATION_ID, Integer.parseInt(value));
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                personDAO.registerProfessionOccupation(loanApplication.getPersonId(), Integer.parseInt(value));
                break;
            case "creditReason":
//                if (!SecurityUtils.getSubject().isPermitted("person:ocupationalInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new IntegerFieldValidator(ValidatorUtil.OCUPATION_ID, Integer.parseInt(value));
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                loanApplicationDao.updateReason(loanApplication.getId(),Integer.parseInt(value));
                break;
        }
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = URL+"/person/{personId}/application/{loanApplicationId}/notes", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:notes:update", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object insertOrUpdateNote(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @PathVariable("personId") Integer personId,
            @PathVariable("loanApplicationId") Integer loanApplicationId,
            @RequestParam(name = "operatorId", required = false) Integer operatorId,
            @RequestParam(name = "noteId", required = false) Integer noteId, @RequestParam(name = "message") String message) throws Exception {


        Integer loggedUserEntityId = entityExtranetService.getLoggedUserEntity().getId();
        if (message != null && message.trim().length() > 500) {
            return AjaxResponse.errorMessage("La nota no puede contener más de 500 caracteres");
        } else if (noteId != null && !loggedUserEntityId.equals(operatorId)) {
            return AjaxResponse.errorMessage("Solo puedes editar las notas que haz registrado");
        }

        LoanApplicationExtranetRequestPainter loanApplication = entityExtranetService.getApplicationById(loanApplicationId, locale, request);
        if(!Arrays.asList(LoanApplicationStatus.WAITING_APPROVAL,
                LoanApplicationStatus.EVAL_APPROVED,
                LoanApplicationStatus.REJECTED,
                LoanApplicationStatus.REJECTED_AUTOMATIC,
                LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION).contains(loanApplication.getStatus().getId())) {
            return AjaxResponse.errorMessage("La solicitud no se encuentra en un estado valido para agregar notas.");
        }

        if(noteId == null) loanApplicationDao.registerLoanApplicationComment(loanApplicationId, message, null, Comment.COMMENT_EVALUATION, loggedUserEntityId);
        else loanApplicationDao.updateLoanApplicationComment(noteId, message, null, Comment.COMMENT_EVALUATION, loggedUserEntityId);
        
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = URL +"/action/returnToContract", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:action:returnToContract", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object returnToContract(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {

        loanApplicationDao.boResetContract(loanApplicationId);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = URL +"/action/runFraudAlerts", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:fraudAlerts:execute", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object runFraudAlerts(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);

        if (loanApplication.getFraudAlertQueryIds() == null || loanApplication.getFraudAlertQueryIds().isEmpty()) {
            webscrapperService.callRunFraudAlerts(loanApplication);
        } else {
            Integer lastExecutedBotId = loanApplication.getFraudAlertQueryIds().stream().sorted(Comparator.reverseOrder()).findFirst().orElse(null);
            if (lastExecutedBotId != null) {
                QueryBot queryBot = botDao.getQueryBot(lastExecutedBotId);
                if (queryBot.getStatusId() != QueryBot.STATUS_QUEUE && queryBot.getStatusId() != QueryBot.STATUS_RUNNING) {
                    webscrapperService.callRunFraudAlerts(loanApplication);
                } else {
                    return AjaxResponse.errorMessage("Ya se encuentran corriendo las alertas de fraude.");
                }
            }
        }

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = URL +"/action/expire", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:action:expire", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object expire(@RequestParam("loanApplicationId") Integer loanApplicationId) {

        loanApplicationDao.expireLoanApplication(loanApplicationId);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = URL +"/action/approve", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:action:approve", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object approveLoanApplication(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        if(!loanApplicationApprovalValidationService.loanHasAllValidations(loanApplication, null))
            return AjaxResponse.errorMessage("Faltan validaciones para aprobar la solicitud");

        loanApplicationService.approveLoanApplication(loanApplicationId, null, request, response, templateEngine, locale);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = URL +"/action/reject", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:action:reject", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object rejectLoanApplication(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("loanApplicationId") Integer loanApplicationId,
            @RequestParam("applicationRejectionReasonId") Integer applicationRejectionReasonId,
            @RequestParam("applicationRejectionComment") String applicationRejectionComment) throws Exception {

        loanApplicationDao.updateLoanApplicationStatus(loanApplicationId, LoanApplicationStatus.REJECTED,null);
        loanApplicationDao.registerRejectionWithComment(loanApplicationId, applicationRejectionReasonId, applicationRejectionComment);
        LoanApplication la = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
//        emailBoService.sendRejectionMail(la, locale);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = URL +"/action/reevaluate", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:action:reevaluate", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object reevaluateLoanApplication(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {

        loanApplicationService.reevaluateLoanApplications(loanApplicationId);
        return AjaxResponse.ok(null);
    }

    private FieldValidator getGrossIncomeValidator(Integer personId, Locale locale, String value) throws Exception {
        Person person = personDAO.getPerson(catalogService, locale, personId, true);

        if (person.getDocumentType().getCountryId().equals(CountryParam.COUNTRY_COLOMBIA))
            return new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME_COLOMBIA, Integer.parseInt(value));
        else
            return new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME, Integer.parseInt(value));
    }

    @RequestMapping(value = "/person/job-phone/update/modal", method = RequestMethod.GET)
//    @RequiresPermissionOr403(permissions = "person:ocupationalInformation:update", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getPersonJobPhoneUpdateModal(
            ModelMap model, Locale locale,
            @RequestParam("loanId") int loanId,
            @RequestParam("personId") int personId) throws Exception {

        Person person = personDAO.getPerson(catalogService, locale, personId, false);

        Question28Form form = new Question28Form();
        ((Question28Form.Validator) form.getValidator()).configValidator(person.getDocumentType().getCountryId());

        PersonOcupationalInformation principalOccupation = personDAO.getPersonOcupationalInformation(locale, person.getId())
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
        return new ModelAndView("/entityExtranet/extranetEvaluation :: updateOccupationalPhoneNumberModal");
    }

    private void registerPhoneNumber(Integer personId, String phoneType, String phoneNumber) throws Exception {
        PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(personId, Configuration.getDefaultLocale());
        personDAO.updateOcupatinalPhoneNumber(personId, phoneType, ocupation.getNumber(), phoneNumber);
    }

    @RequestMapping(value = URL +"/loan/{loanApplicationId}/registerContactResult", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:contactResult:register", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerContact(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("loanApplicationId") Integer loanApplicationId,
            @RequestParam(value = "contactRelationship", required = false) Integer contactRelationship,
            @RequestParam(value = "referralId", required = false) Integer referralId,
            @RequestParam("contactResultId") Integer contactResultId

    ) throws Exception {

        Integer loggedUserEntityId = entityExtranetService.getLoggedUserEntity().getId();

        loanApplicationDao.registerTrackingActionContactPerson(loanApplicationId, null, contactResultId, null, null, true, null,loggedUserEntityId, referralId, null);
        loanApplicationApprovalValidationService.validateAndUpdate(loanApplicationId, ApprovalValidation.VERIF_TELEFONICA);
        loanApplicationApprovalValidationService.validateAndUpdate(loanApplicationId, ApprovalValidation.VERIF_DOMICILIARIA);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = URL +"/loan/{loanApplicationId}/identityManualApproval", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:update:identity:validation", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object identityManualApproval(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("loanApplicationId") Integer loanApplicationId,
            @RequestParam("matiResultId") Integer matiResultId,
            @RequestParam("status") Boolean status

    ) throws Exception {
        Integer loggedUserEntityId = entityExtranetService.getLoggedUserEntity().getId();
        securityDAO.updateMatiStatus(matiResultId,status != null && status ? MatiResult.MATI_STATUS_VERIFIED : MatiResult.MATI_STATUS_REJECTED);
        loanApplicationApprovalValidationService.validateAndUpdate( loanApplicationId, ApprovalValidation.IDENTIDAD, status);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = URL+"/referral/{field}", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:referral:update", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> updateReferralProperty(
            ModelMap model, Locale locale,
            @PathVariable("field") String field,
            @RequestParam("referralId") Integer referralId,
            @RequestParam(value = "value", required = false) String value) throws Exception {

        FieldValidator validator;
        switch (field) {
            case "fullName":
//                if (!SecurityUtils.getSubject().isPermitted("person:contactInformation:referralInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new StringFieldValidator(ValidatorUtil.NAME, value).setRequired(false);
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                personDAO.updateReferralFulName(referralId, value);
                break;
            case "relationship":
//                if (!SecurityUtils.getSubject().isPermitted("person:contactInformation:referralInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
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
                personDAO.updateReferralRelationship(referralId, Integer.parseInt(value));
                break;
            case "phoneType":
//                if (!SecurityUtils.getSubject().isPermitted("person:contactInformation:referralInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                personDAO.updateReferralPhoneType(referralId, value);
                break;
            case "phoneNumber":
//                if (!SecurityUtils.getSubject().isPermitted("person:contactInformation:referralInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new StringFieldValidator(ValidatorUtil.BO_PHONE_NUMBER, value).setRequired(false);
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                personDAO.updateReferralPhoneNumber(referralId, value);
                break;
            case "info":
//                if (!SecurityUtils.getSubject().isPermitted("person:contactInformation:referralInformation:update")) {
//                    return AjaxResponse.errorForbidden();
//                }
                validator = new StringFieldValidator(ValidatorUtil.NAME, value).setValidRegex(StringFieldValidator.REGEXP_ALPHANUMERIC_SPEC_CHARS).setMaxCharacters(200).setRequired(false);
                if (!validator.validate(locale)) {
                    return AjaxResponse.errorMessage(validator.getErrors());
                }
                personDAO.updateReferralInfo(referralId, value);
                break;
            case "validated":
//                if (!SecurityUtils.getSubject().isPermitted("person:contactInformation:referralInformation:validate")) {
//                    return AjaxResponse.errorForbidden();
//                }
                personDAO.updateReferralValidated(referralId, Boolean.parseBoolean(value));
                break;
            case "documentType":
//                if (!SecurityUtils.getSubject().isPermitted("person:contactInformation:referralInformation:validate")) {
//                    return AjaxResponse.errorForbidden();
//                }
                personDAO.updateDocumentType(referralId, value != null ? Integer.parseInt(value) : null);
                break;
            case "documentNumber":
//                if (!SecurityUtils.getSubject().isPermitted("person:contactInformation:referralInformation:validate")) {
//                    return AjaxResponse.errorForbidden();
//                }
                personDAO.updateDocumentNumber(referralId, value);
                break;
        }
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = URL+"/referral/create", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:referral:update", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> createReferral(
            ModelMap model, Locale locale,
            @RequestParam("personId") Integer personId,
            @RequestParam("fullName") String fullName,
            @RequestParam("relationshipId") Integer relationshipId,
            @RequestParam(value = "phoneType", required = false) String phoneType,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("info") String info) throws Exception {

        Referral referral = personDAO.createReferral(personId, phoneType, fullName, relationshipId, "51", phoneNumber, info, locale);
        return AjaxResponse.ok(new Gson().toJson(referral));
    }

    @RequestMapping(value = "/" + URL + "/welcomeCall/{loanId}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loan:evaluate:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getWelcomeCallLoanApplication(
            ModelMap model, Locale locale, HttpServletRequest request, @PathVariable("loanId") Integer loanId) throws Exception {

        LoanApplicationExtranetRequestPainter loanApplication = entityExtranetService.getApplicationById(loanId, locale,request);

        WelcomeCallLoanExtranetPainter welcomeCallLoanExtranetPainter = new WelcomeCallLoanExtranetPainter();
        welcomeCallLoanExtranetPainter.setTokyCall(loanApplication.getTokyCall());
        if(loanApplication != null){
            List<LoanApplicationTrackingAction> actions = loanApplicationDao.getLoanApplicationTrackingActionsByTrackingId(loanId,new Integer[]{TrackingAction.WELCOME_CALL_CONTACTED,TrackingAction.WELCOME_CALL_NO_RESPONSE,TrackingAction.WELCOME_CALL_WRONG_NUMBER},null);
            if(actions != null && !actions.isEmpty()){
                LoanApplicationTrackingAction loanApplicationTrackingActionFirst = actions.stream().filter(e -> e.getTrackingAction().getTrackingActionId() != TrackingAction.TRACKING_PHONE_CALL).findFirst().orElse(null);
                welcomeCallLoanExtranetPainter.setAction(loanApplicationTrackingActionFirst != null ? loanApplicationTrackingActionFirst.getTrackingAction() : null);
            }
        }

        model.addAttribute("loanId",loanId);
        model.addAttribute("welcomeCallData",welcomeCallLoanExtranetPainter);
        return new ModelAndView("/entityExtranet/extranetEvaluation :: welcomeCallData");
    }

    @RequestMapping(value = URL+"/loan/uploadTrackingCall", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:contactResul:uploadTrackingCall", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object registerTrackingCall(
            ModelMap model, Locale locale,
            @RequestParam("loanId") Integer loanId,
            @RequestParam("trackingCall") MultipartFile file) throws Exception {

        if (file.getContentType().toString().equals("audio/mpeg") || file.getContentType().toString().equals("audio/mp3")) {
            Integer loggedUserEntityId = entityExtranetService.getLoggedUserEntity().getId();
            LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanId, locale);
            String fileName = fileService.writeUserFile(file.getBytes(), loanApplication.getUserId(), new Date().getTime() +loanApplication.getCode().concat(".wav"));
            Integer userFileId = userDAO.registerUserFile(loanApplication.getUserId(), loanId, UserFileType.TRACKING_PHONE_CALL, fileName);
            loanApplicationDao.registerTrackingActionContactPerson(loanApplication.getId(), null, TrackingAction.TRACKING_PHONE_CALL, null, null, true, null,loggedUserEntityId, null, userFileId);
            return AjaxResponse.ok(null);
        } else {
            return AjaxResponse.errorMessage("El formato del archivo es incorrecto.");
        }
    }

    @RequestMapping(value = URL+"/reject/loanApplicationFraudAlert", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:fraudAlerts:reject", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object rejectLoanApplicationFraudAlert(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationFraudAlertIds") String loanApplicationFraudAlertIdsArray,
            @RequestParam("loanApplicationId") Integer loanId
    ) throws Exception {
        Integer loggedUserEntityId = entityExtranetService.getLoggedUserEntity().getId();
        List<Integer> loanApplicationFraudAlertIds = new Gson().fromJson(loanApplicationFraudAlertIdsArray, new TypeToken<ArrayList<Integer>>() {
        }.getType());
        for(Integer loanApplicationFraudAlertId: loanApplicationFraudAlertIds){
            creditDao.rejectLoanApplicationFraudAlert(loanApplicationFraudAlertId, null, loggedUserEntityId);
        }
        loanApplicationApprovalValidationService.validateAndUpdate(loanId, ApprovalValidation.ALERTAS_FRAUDE);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = URL+"/reject/allLoanApplicationFraudAlerts", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:fraudAlerts:reject:all", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object rejectAllLoanApplicationFraudAlerts(@RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception{
        Integer loggedUserEntityId = entityExtranetService.getLoggedUserEntity().getId();
        creditDao.rejectAllLoanApplicationFraudAlerts(loanApplicationId, null, loggedUserEntityId);
        loanApplicationApprovalValidationService.validateAndUpdate(loanApplicationId, ApprovalValidation.ALERTAS_FRAUDE);
        return AjaxResponse.ok(null);
    }

    private List<LoanApplicationFraudAlert> getPersonFraudAlertsByApplication(LoanApplication application, boolean logAlerts) throws Exception {
        List<LoanApplicationFraudAlert> loanApplicationFraudAlerts = creditDao.getLoanApplicationFraudAlerts(application.getId(), !logAlerts ? FraudAlertStatus.NUEVO : FraudAlertStatus.REVISADO);

        if (loanApplicationFraudAlerts != null) {
            loanApplicationFraudAlerts = loanApplicationFraudAlerts.stream().filter(a -> a.getActive() != null && a.getActive()).collect(Collectors.toList());

            if (application.getCountry().getId().equals(CountryParam.COUNTRY_ARGENTINA)) {
                this.convertCountry(loanApplicationFraudAlerts);
            }

            // Group the fraud alerts by code
            List<Integer> distictFraudAlerts = loanApplicationFraudAlerts.stream().map(f -> f.getFraudAlert().getFraudAlertId()).distinct().collect(Collectors.toList());
        }

        return loanApplicationFraudAlerts;
    }

    public List<GroupedLoanApplicationFraudAlert> getGroupedPersonFraudAlertsByApplication(LoanApplication application, boolean logAlerts) throws Exception {
        List<LoanApplicationFraudAlert> loanApplicationFraudAlerts = getPersonFraudAlertsByApplication(application, logAlerts);
        List<GroupedLoanApplicationFraudAlert> groupedAlerts = new ArrayList<>();
        if (loanApplicationFraudAlerts != null) {
            // Group the fraud alerts by code
            List<Integer> distinctFraudAlerts = loanApplicationFraudAlerts.stream().map(f -> f.getFraudAlert().getFraudAlertId()).distinct().collect(Collectors.toList());
            for(Integer fraudAlertId : distinctFraudAlerts){
                GroupedLoanApplicationFraudAlert alert = new GroupedLoanApplicationFraudAlert();
                alert.setFraudAlert(catalogService.getFraudAlert(fraudAlertId));
                alert.setLoanApplicationFraudAlertIds(loanApplicationFraudAlerts.stream().filter(f -> f.getFraudAlert().getFraudAlertId() == fraudAlertId.intValue()).map(f -> f.getId()).collect(Collectors.toList()));
                groupedAlerts.add(alert);
            }
        }

        return groupedAlerts;
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

    @RequestMapping(value = URL+"/action/changeFirstDueDate", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:action:firstDueDate:update", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object changeFirstDueDate(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") Integer loanApplicationId,
            @RequestParam("firstDueDate") String firstDueDate) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);

        if (loanApplication.getStatus().getId() != LoanApplicationStatus.WAITING_APPROVAL)
            return AjaxResponse.errorMessage("La solicitud no está en esperando aprobación.");

        if (!new StringFieldValidator(ValidatorUtil.FIRST_DUE_DATE, firstDueDate).validate(locale))
            return AjaxResponse.errorMessage("La fecha no es v&aacute;lida.");

        LoanOffer selectedOffer = loanApplicationDao.getLoanOffersAll(loanApplicationId).stream().filter(o -> o.getSelected()).findFirst().orElse(null);
        if(selectedOffer == null)
            return AjaxResponse.errorMessage("no existe una oferta seleccionada a&uacute;n");

        Date firstDueDateDate = new SimpleDateFormat("dd/MM/yyyy").parse(firstDueDate);
        loanApplicationDao.updateFirstDueDate(loanApplicationId, firstDueDateDate);
        LoanOffer newOffer = loanApplicationDao.createLoanOfferAnalyst(
                loanApplicationId,
                selectedOffer.getAmmount(),
                selectedOffer.getInstallments(),
                selectedOffer.getEntityId(),
                selectedOffer.getProduct().getId(),
                selectedOffer.getEmployer() != null ? selectedOffer.getEmployer().getId() : null,
                selectedOffer.getEntityProductParameterId());
//        loanApplicationDao.selectLoanOfferAnalyst(newOffer.getId(), backofficeService.getLoggedSysuser().getId());
        loanApplicationDao.updateCurrentQuestion(loanApplicationId, ProcessQuestion.Question.Constants.CONTRACT_SIGNATURE);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = URL+"/loanOffer/modal/create", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "loan:action:offer:create", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getLoanOfferCreateModal(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") int loanApplicationId,
            @RequestParam(value = "entityId", required = false) Integer entityId) throws Exception {

        Integer loggedUserEntityId = entityExtranetService.getLoggedUserEntity().getId();

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        if (loanApplication.getAssignedEntityUserId() == null || loanApplication.getAssignedEntityUserId().intValue() != loggedUserEntityId)
            return AjaxResponse.errorMessage("La solicitud está asignada a otro analista.");

        CreateLoanOfferForm form = new CreateLoanOfferForm();
        Person person = personDAO.getPerson(catalogService, locale, loanApplication.getPersonId(), false);

        LoanOffer offer;
        if (entityId == null)
            offer = loanApplicationDao.getLoanOffers(loanApplicationId).stream().filter(i -> i.getSelected()).findFirst().orElse(null);
        else
            offer = loanApplicationDao.getLoanOffers(loanApplicationId).stream().filter(i -> i.getEntityId() == entityId).findFirst().orElse(null);

        if (offer == null)
            return AjaxResponse.errorMessage("No existe ninguna oferta creada aún");

        Employer employer = offer.getEmployer();

        List<EntityProduct> entityProducts = null;
        if (employer != null) {
            entityProducts = catalogService.getEntityProductsByProduct(offer.getProduct().getId()).stream()
                    .filter(e -> e.getEmployer() != null && e.getEmployer().getId() == employer.getId().intValue())
                    .collect(Collectors.toList());
        } else {
            entityProducts = catalogService.getEntityProductsByProduct(offer.getProduct().getId());
        }


        if (entityProducts == null || entityProducts.isEmpty()) {
            return AjaxResponse.errorMessage("No existe entidad financiadora para este producto");
        }

        List<PreApprovedInfo> preApprovedInfos = personDAO.getPreApprovedDataByDocument(offer.getProduct().getId(), person.getDocumentType().getId(), person.getDocumentNumber());
        PreApprovedInfo filteredPreApprovedInfo = preApprovedInfos != null ? preApprovedInfos.stream().filter(e -> e.getEntity().getId().intValue() == offer.getEntityId()).findFirst().orElse(null) : null;

        Double maxAmount;
        Integer maxInstallments;

        if (filteredPreApprovedInfo != null && filteredPreApprovedInfo.getMaxAmount() != null) {
            maxAmount = filteredPreApprovedInfo.getMaxAmount();
            maxInstallments = filteredPreApprovedInfo.getMaxInstallments();
            model.addAttribute("entitySelected", offer.getEntityId());
        } else {
            maxAmount = Double.valueOf(offer.getProduct().getProductParams(loanApplication.getCountryId()).getMaxAmount());
            maxInstallments = offer.getProduct().getProductParams(loanApplication.getCountryId()).getMaxInstallments();
            model.addAttribute("entitySelected", entityId);
        }


        ((CreateLoanOfferForm.CreateLoanOfferFormValidator) form.getValidator()).ammount.setMaxValue(maxAmount.intValue());
        ((CreateLoanOfferForm.CreateLoanOfferFormValidator) form.getValidator()).ammount.setMinValue(offer.getProduct().getProductParams(loanApplication.getCountryId()).getMinAmount());
        ((CreateLoanOfferForm.CreateLoanOfferFormValidator) form.getValidator()).installments.setMaxValue(maxInstallments);
        ((CreateLoanOfferForm.CreateLoanOfferFormValidator) form.getValidator()).installments.setMinValue(offer.getProduct().getProductParams(loanApplication.getCountryId()).getMinInstallments());

        List<Entity> entities = new ArrayList<>();
        entities.add(catalogService.getEntity(loanApplication.getSelectedEntityId()));

        if (offer.getProduct().getId() == Product.DEBT_CONSOLIDATION) {
            double newAmmount = 0;
            loanApplication.setConsolidableDebts(loanApplicationDao.getConsolidationAccounts(loanApplicationId));
            for (int i = 0; i < loanApplication.getConsolidableDebts().size(); i++) {
                if (loanApplication.getConsolidableDebts().get(i).isSelected())
                    newAmmount += loanApplication.getConsolidableDebts().get(i).getBalance();
            }
            model.addAttribute("loanAmmount", newAmmount);
        }

        model.addAttribute("entities", entities);
        model.addAttribute("createLoanOfferForm", form);
        model.addAttribute("loanApplicationId", loanApplicationId);
        loanApplication.setInstallments(Math.min(loanApplication.getInstallments(), maxInstallments));
        model.addAttribute("loanApplication", loanApplication);

        return new ModelAndView("/entityExtranet/extranetEvaluation :: createLoanOfferModal");
    }

    @RequestMapping(value = URL+"/loanOffer/create", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:action:offer:create", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> createLoanOffer(
            ModelMap model, Locale locale,
            //CreateLoanOfferForm form,
            @RequestParam("ammount") Double ammount,
            @RequestParam("installments") Integer installments,
            @RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {

        Integer loggedUserEntityId = entityExtranetService.getLoggedUserEntity().getId();

        // Generate new offer
        LoanApplication loan = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        if (loan.getAssignedEntityUserId() == null || loan.getAssignedEntityUserId().intValue() != loggedUserEntityId)
            return AjaxResponse.errorMessage("La solicitud está asignada a otro analista.");

        Employee employee = personDAO.getEmployeeByPerson(loan.getPersonId(), locale);

        List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplicationId);

        Integer entityProductParameterId = offers.get(0).getEntityProductParameterId();

        LoanOffer offer = loanApplicationDao.createLoanOfferAnalyst(loanApplicationId, ammount, installments, loan.getEntityId(), offers.get(0).getProduct().getId(), employee != null ? employee.getEmployer().getId() : null, entityProductParameterId);
        loanApplicationDao.selectLoanOfferAnalyst(offer.getId(), 1);
        loanApplicationDao.updateLoanApplicationStatus(loanApplicationId, LoanApplicationStatus.WAITING_APPROVAL, 1);

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);

        Person person = personDAO.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
        PersonContactInformation personContact = personDAO.getPersonContactInformation(locale, person.getId());
        loanApplicationDao.updateLoanOfferGeneratedStatus(loanApplicationId, false);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = URL+"/loanOffer/{loanOfferId}/notify", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "offer:notify", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> notifyClient(
            ModelMap model, Locale locale,
            @PathVariable("loanOfferId") Integer loanOfferId,
            @RequestParam("loanId") Integer loanApplicationId
    ) throws Exception {
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        Integer loggedUserEntityId = entityExtranetService.getLoggedUserEntity().getId();

        // Generate new offer
        if (loanApplication.getAssignedEntityUserId() == null || loanApplication.getAssignedEntityUserId().intValue() != loggedUserEntityId)
            return AjaxResponse.errorMessage("La solicitud está asignada a otro analista.");

        Person person = personDAO.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
        PersonContactInformation personContact = personDAO.getPersonContactInformation(locale, person.getId());

        List<LoanOffer> loanOffers = loanApplicationDao.getLoanOffers(loanApplicationId);

        LoanOffer offer = loanOffers.stream().filter(e -> e.getId().equals(loanOfferId)).findFirst().orElse(null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonVars = new JSONObject();
                    jsonVars.put("CLIENT_NAME", person.getName().split(" ")[0]);
                    jsonVars.put("AMOUNT", utilService.doubleMoneyFormat(offer.getAmmount()));
                    jsonVars.put("INSTALLMENTS", offer.getInstallments() + " Meses");
                    jsonVars.put("INSTALLMENT_AMOUNT", utilService.doubleMoneyFormat(offer.getInstallmentAmountAvg()));
                    jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
                    jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

                    PersonInteraction interaction = new PersonInteraction();
                    interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                    interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.FORCED_APPROVAl_MAIL, loanApplication.getCountryId()));
                    interaction.setDestination(personContact.getEmail());
                    interaction.setLoanApplicationId(loanApplicationId);
                    interaction.setPersonId(person.getId());

                    interactionService.sendPersonInteraction(interaction, jsonVars, null);
                } catch (Exception ex) {

                }
            }
        }).start();

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = URL+"/loanOffer/{loanOfferId}/select", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "offer:select", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> selectLoanOffer(
            ModelMap model, Locale locale,
            @PathVariable("loanOfferId") Integer loanOfferId,
            @RequestParam("loanId") Integer loanId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanId, locale);
        Integer loggedUserEntityId = entityExtranetService.getLoggedUserEntity().getId();

        // Generate new offer
        if (loanApplication.getAssignedEntityUserId() == null || loanApplication.getAssignedEntityUserId().intValue() != loggedUserEntityId)
            return AjaxResponse.errorMessage("La solicitud está asignada a otro analista.");

        loanApplicationDao.selectLoanOfferAnalystExtranet(loanOfferId, loggedUserEntityId);

        loanApplicationDao.updateLoanApplicationStatus(loanId, LoanApplicationStatus.WAITING_APPROVAL, 1);

        LoanOffer loanOffer = loanApplicationDao.getLoanOffersAll(loanId).stream().filter(o -> o.getId().equals(loanOfferId)).findFirst().orElse(null);

        if (loanOffer == null)
            return AjaxResponse.errorMessage("No tiene ofertas.");

        if (loanOffer.getEntityProductParam().getSignatureType() == 1)
            loanApplicationDao.boResetContract(loanId);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = URL+"/welcomeCall/uploadTokyCall", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:welcomeCall:action:upload", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object registerTokyCall(
            ModelMap model, Locale locale,
            @RequestParam("tokyCall") MultipartFile file,
            @RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {

        if (file.getContentType().toString().equals("audio/mpeg") || file.getContentType().toString().equals("audio/mp3")) {
            LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
            String fileName = fileService.writeUserFile(file.getBytes(), loanApplication.getUserId(), loanApplication.getCode().concat(".wav"));
            userDAO.registerUserFile(loanApplication.getUserId(), loanApplicationId, UserFileType.CONTRACT_CALL, fileName);
            return AjaxResponse.ok(null);
        } else {
            return AjaxResponse.errorMessage("El formato de la llamada es incorrecto.");
        }

    }

    @RequestMapping(value = URL+"/welcomeCall/deleteTokyCall", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:welcomeCall:action:delete", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object deleteTokyCall(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {

        Integer personId = loanApplicationDao.getLoanApplication(loanApplicationId, locale).getPersonId();

        LoanApplicationUserFiles files = new LoanApplicationUserFiles();
        List<LoanApplicationUserFiles> userFiles = personDAO.getUserFiles(personId, locale);
        for (LoanApplicationUserFiles file : userFiles) {
            if (file.getLoanApplicationId().equals(loanApplicationId)) {
                files = file;
                break;
            }
        }

        UserFile userFile = files.getUserFileList().stream().filter(e -> e.getFileType().getId() == UserFileType.CONTRACT_CALL).findFirst().orElse(null);
        userDAO.updateUserFileType(userFile.getId(), UserFileType.ELIMINADOS);

        return AjaxResponse.ok(null);
    }

    private void updateRCI(Integer loanApplicationId) throws Exception {
        System.out.println("updateRCI");
        // Recreate the offers of the LoanApplications that are WaitingForApproval
        LoanApplication loan = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
        if (loan.getStatus().getId() == LoanApplicationStatus.WAITING_APPROVAL) {
            if (loan.getProduct() == null || loan.getProduct().getId() != Product.AUTOS) {
                LoanApplicationEvaluation evaluation = loanApplicationService.getLastEvaluation(loan.getId(), loan.getPersonId(), Configuration.getDefaultLocale(), true);
                if (evaluation != null) {
                    loanApplicationDao.createLoanOffers(loan.getId());
                }
            }
        }
    }
}
