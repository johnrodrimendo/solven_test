package com.affirm.backoffice.controller;

import com.affirm.acceso.AccesoServiceCall;
import com.affirm.acceso.model.Direccion;
import com.affirm.acceso.model.Expediente;
import com.affirm.acceso.model.InformacionAdicional;
import com.affirm.backoffice.dao.LoanApplicationBODAO;
import com.affirm.backoffice.service.BackofficeService;
import com.affirm.backoffice.service.EmailBoService;
import com.affirm.backoffice.service.LoanAplicationBoService;
import com.affirm.common.dao.*;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.service.question.AbstractQuestionService;
import com.affirm.common.service.question.Question50Service;
import com.affirm.common.service.question.QuestionFlowService;
import com.affirm.common.util.*;
import com.affirm.compartamos.CompartamosServiceCall;
import com.affirm.onesignal.service.OneSignalService;
import com.affirm.onesignal.service.impl.OneSignalServiceImpl;
import com.affirm.security.model.SysUser;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
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
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */
@Controller
@Scope("request")
public class LoanApplicationController {

    private static Logger logger = Logger.getLogger(LoanApplicationController.class);

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private EmailBoService emailBoService;
    @Autowired
    private FileService fileService;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private BackofficeService backofficeService;
    @Autowired
    private AccesoServiceCall accesoServiceCall;
    @Autowired
    private TranslatorDAO translatorDao;
    @Autowired
    private PersonService personService;
    @Autowired
    private LoanApplicationQuestionService loanApplicationQuestionService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private BureauService bureauService;
    @Autowired
    private UserService userService;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private OneSignalService oneSignalService;
    @Autowired
    private LoanNotifierService loanNotifierService;
    @Autowired
    private WebscrapperService webscrapperService;
    @Autowired
    private BotDAO botDAO;
    @Autowired
    private Question50Service question50Service;
    @Autowired
    private InteractionService interactionService;

    @RequestMapping(value = "/loanApplication/approve", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanApplication:approve", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object approveLoanApplication(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("loanApplicationId") Integer loanApplicationId,
            @RequestParam("personId") Integer personId,
            @RequestParam(value = "approveAnyway", required = false, defaultValue = "false") boolean approveAnyway) throws Exception {

        JSONObject jsonResponse = new JSONObject();

        List<LoanOffer> selectedOffers = loanApplicationDao.getLoanOffers(loanApplicationId);
        LoanOffer selectedOffer = selectedOffers != null ? selectedOffers.stream().filter(LoanOffer::getSelected).findFirst().orElse(null) : null;

//        VALIDAR ALMENOS UN CONTACTO PARA PRODUCTOS EXCEPTO CONVENIO
        List<Integer> noNeedToContactProducts = Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AELU_CONVENIO, EntityProductParams.ENT_PROD_PARAM_ABACO_CONVENIO, EntityProductParams.ENT_PROD_PARAM_TARJETA_PERUANA_PREPAGO, EntityProductParams.ENT_PROD_PARAM_FINANSOL_CONSUMO, EntityProductParams.ENT_PROD_PARAM_FINANSOL_CONSUMO_BASE);
        if(selectedOffer != null && !noNeedToContactProducts.contains(selectedOffer.getEntityProductParameterId())) {
            List<LoanApplicationTrackingAction> loanApplicationTrackingActions = loanApplicationDao.getLoanApplicationTrackingActions(loanApplicationId);
            List<TrackingAction> trackingActions = loanApplicationTrackingActions == null ? Collections.emptyList() : loanApplicationTrackingActions.stream().map(LoanApplicationTrackingAction::getTrackingAction).filter(ta -> TrackingAction.CATEGORY_CONTACT.equals(ta.getTrackingActionCategory())).collect(Collectors.toList());
            if(trackingActions.isEmpty()) {
                return AjaxResponse.errorMessage("Se debe registrar al menos una llamada con el cliente");
            }
        }
        else if(selectedOffers == null || selectedOffer == null) {
            return AjaxResponse.errorMessage("La solicitud no tiene una oferta seleccionada");
        }

        if(false){
            List<PersonDisqualifier> pDisqualifiers = personDao.getPersonDisqualifierByPersonId(personId);
            if(pDisqualifiers == null || pDisqualifiers.size() == 0)
                return AjaxResponse.errorMessage("No se ha completado PEP / OFAC.");
            else if(pDisqualifiers.stream().anyMatch(p ->p.isDisqualified())){
                    return AjaxResponse.errorMessage("El solicitante es PEP / OFAC");
            }
        }

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        List<UserEmail> emails = userDAO.getUserEmails(loanApplication.getUserId());
        User user = userDAO.getUser(loanApplication.getUserId());

        if (loanApplication.getCreditAnalystSysUserId() == null)
            return AjaxResponse.errorMessage("La solicitud no está asignada a ningun analista.");

        if (loanApplication.getCreditAnalystSysUserId().intValue() != backofficeService.getLoggedSysuser().getId())
            return AjaxResponse.errorMessage("La solicitud está asignada a otro analista.");

        if (loanApplication.getGeneratedOffers())
            return AjaxResponse.errorMessage("Se deben regenerar las ofertas.");

        if (user.getEmailVerified() == null || !user.getEmailVerified()) {
            return AjaxResponse.errorMessage("El correo no está verificado.");
        }

        EntityProductParams selectedOfferParams = catalogService.getEntityProductParamById(selectedOffer.getEntityProductParameterId());

        if (selectedOffer.getSignatureFullName() == null && selectedOfferParams.getSignatureType() == EntityProductParams.CONTRACT_TYPE_DIGITAL) {// VERIFICAR LA OFERTA SELECCIONADO HA SIDO FIRMADA Y ES DIGITAL
            return AjaxResponse.errorMessage("Se debe registrar la firma del contrato");
        }

        if (selectedOffer.getEntity().getId() == Entity.COMPARTAMOS) {
            PersonOcupationalInformation personOcupationalInformation = personDao.getPersonOcupationalInformation(locale, personId).stream().filter(e -> e.getNumber().equals(PersonOcupationalInformation.PRINCIPAL)).findFirst().orElse(null);
            if (personOcupationalInformation != null) {
                if (personOcupationalInformation.getSector() == null || personOcupationalInformation.getSector().getId().isEmpty())
                    return AjaxResponse.errorMessage("Se debe registrar el sector de la empresa");
            }

            List<DisggregatedAddress> disggregatedAddresses = personDao.getDisggregatedAddress(personId);
            if (disggregatedAddresses != null) {
                if (disggregatedAddresses.stream().anyMatch(d -> d.getType() == 'H')) {
                    com.affirm.compartamos.model.Direccion dieccionPrueba = new com.affirm.compartamos.model.Direccion(
                            disggregatedAddresses.stream().filter(d -> d.getType() == 'H').findFirst().orElse(null),
                            null,
                            translatorDao,
                            null
                    );
                    if (dieccionPrueba.getVia().length() > 60)
                        return AjaxResponse.errorMessage("La dirección domiciliaria \"" + dieccionPrueba.getVia() + "\" tiene mas de 60 caracteres. Compartamos pide que sea max 60.");
                }
                if (disggregatedAddresses.stream().anyMatch(d -> d.getType() == 'J')) {
                    com.affirm.compartamos.model.Direccion dieccionPrueba = new com.affirm.compartamos.model.Direccion(
                            disggregatedAddresses.stream().filter(d -> d.getType() == 'J').findFirst().orElse(null),
                            null,
                            translatorDao,
                            null
                    );
                    if (dieccionPrueba.getVia().length() > 60)
                        return AjaxResponse.errorMessage("La direccion laboral \"" + dieccionPrueba.getVia() + "\" tiene mas de 60 caracteres. Compartamos pide que sea max 60.");
                }
            }
        }

        if(selectedOffer.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_TARJETA_PERUANA_PREPAGO) {
            List<PersonOcupationalInformation> personOcupationalInformationList = personDao.getPersonOcupationalInformation(locale, personId);
            if(personOcupationalInformationList != null){
                for (PersonOcupationalInformation personOcupationalInformation: personOcupationalInformationList) {
                    if(personOcupationalInformation != null && personOcupationalInformation.getActivityType() != null &&
                            (personOcupationalInformation.getActivityType().getId().equals(ActivityType.DEPENDENT) || personOcupationalInformation.getActivityType().getId().equals(ActivityType.INDEPENDENT))
                    ) {
                        String activityType = personOcupationalInformation.getNumber() == PersonOcupationalInformation.PRINCIPAL ? "Actividad Principal" : personOcupationalInformation.getNumber() == PersonOcupationalInformation.SECUNDARY ? "Actividad Secundaria" : "";

                        if(personOcupationalInformation.getSubActivityType() == null && personOcupationalInformation.getActivityType().getId().equals(ActivityType.INDEPENDENT)) {
                            return AjaxResponse.errorMessage("Se debe registrar la categoría en " + activityType);
                        }

                        if(personOcupationalInformation.getActivityType().getId().equals(ActivityType.DEPENDENT) || personOcupationalInformation.getSubActivityType() != null && Arrays.asList(SubActivityType.PROFESSIONAL_SERVICE, SubActivityType.OWN_BUSINESS).contains(personOcupationalInformation.getSubActivityType().getId())) {

                            if(personOcupationalInformation.getCompanyName() == null || personOcupationalInformation.getCompanyName().isEmpty()) {
                                return AjaxResponse.errorMessage("Se debe registrar la razón social en " + activityType);
                            }
                        }
                    }
                }
            }
        }

        Person person = personDao.getPerson(catalogService, locale, personId, false);
        if(EntityProductParams.ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD.contains(selectedOffer.getEntityProductParameterId())) {
            if(person.getNationality() == null)
                return AjaxResponse.errorMessage("Se debe registrar la nacionalidad.");
            PersonOcupationalInformation ocupationalInformation = personDao.getPersonOcupationalInformation(locale, personId).stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst().orElse(null);
            if(ocupationalInformation == null || ocupationalInformation.getCiiu() == null)
                return AjaxResponse.errorMessage("Se debe registrar el CIIU de la empresa.");
        }

        if (selectedOfferParams.getRequiresDisaggregatedAddress() != null && selectedOfferParams.getRequiresDisaggregatedAddress()) {
            if (!personService.isDisggregatedAddress(personId))
                return AjaxResponse.errorMessage("Se deben disgregar las direcciones.");
        }

        if (loanApplication.getSelectedProductId() == Product.AUTOS) {
            PersonOcupationalInformation ocupationalInformation = personDao.getPersonOcupationalInformation(locale, personId).stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst().orElse(null);
            if (ocupationalInformation == null || (!ocupationalInformation.getActivityType().getId().equals(ActivityType.INDEPENDENT) && ocupationalInformation.getCompanyName() == null))
                return AjaxResponse.errorMessage("Se debe registrar el nombre de la empresa en que trabaja.");
        }

        if (loanApplication.getCountryId() == CountryParam.COUNTRY_PERU && selectedOffer.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_CREDIGOB_PROOVEDOR_ESTADO) {
            PersonContactInformation personContactInformation = personDao.getPersonContactInformation(locale, personId);
            if (personContactInformation.getAddressUbigeo() == null || personContactInformation.getAddressUbigeo().getUbigeo() == null || personContactInformation.getAddressUbigeo().getUbigeo().isEmpty())
                return AjaxResponse.errorMessage("Se debe registrar el ubigeo en la dirección antes de aprobar el crédito.");
        }

        PersonBankAccountInformation bankAccount = personDao.getPersonBankAccountInformation(locale, personId);
        if (selectedOfferParams.getRequiresInterbankCode() && (bankAccount == null || bankAccount.getCciCode() == null))
            return AjaxResponse.errorMessage("Se debe registrar el codigo interbancario antes de aprobar el crédito.");

        if (selectedOfferParams.getRequiresInterbankCode() && (bankAccount != null && bankAccount.getBankAccount() != null && bankAccount.getCciCode() != null)) {
            String code = bankAccount.getBankAccount().substring(bankAccount.getBankAccount().length() - 10);

            if (!bankAccount.getCciCode().contains(code)) {
                return AjaxResponse.errorMessage(String.format("El código <mark>%s</mark> no se encuentra incluido en el CCI <strong>%s</strong>", code, bankAccount.getCciCode()));
            }
        }

        if (loanApplication.getCountryId() == CountryParam.COUNTRY_ARGENTINA && (bankAccount.getVerified() == null || !bankAccount.getVerified()))
            return AjaxResponse.errorMessage("Se debe verificar el CBU");

        if (selectedOffer.getEntityProductParam().getRequiresContractCall()) {
            List<UserFile> userFiles = loanApplicationDao.getLoanApplicationUserFiles(loanApplicationId);
            if (userFiles != null && userFiles.size() > 0) {
                UserFile userFile = userFiles.stream().filter(e -> e.getFileType().getId() == UserFileType.CONTRACT_CALL).findFirst().orElse(null);
                if (userFile == null)
                    return AjaxResponse.errorMessage("Se debe subir la llamada de aceptación del crédito antes de aprobarlo.");
            }
        }

        List<Integer> noNeedToRunFraudAlerts = Arrays.asList(Entity.TARJETAS_PERUANAS, Entity.ABACO, Entity.AELU);
        if(!noNeedToRunFraudAlerts.contains(selectedOffer.getEntity().getId())) {
            if (loanApplication.getFraudAlertsProcessed() == null || !loanApplication.getFraudAlertsProcessed()) {
                return AjaxResponse.errorMessage("Falta correr las alertas de fraude.");
            }
        }

        List<Integer> noNeedToVerifyFraudAlerts = Arrays.asList(EntityProductParams.ENT_PROD_PARAM_TARJETA_PERUANA_PREPAGO);
        if(selectedOffer != null && !noNeedToVerifyFraudAlerts.contains(selectedOffer.getEntityProductParameterId())) {
            List<LoanApplicationFraudAlert> loanApplicationFraudAlerts = creditDao.getLoanApplicationFraudAlerts(loanApplicationId, FraudAlertStatus.NUEVO);

            if (loanApplication.getFraudFlagId() != null && !loanApplication.getFraudFlagId().equals(1)) {
                return AjaxResponse.errorMessage("No se puede aprobar porque hay riesgo de fraude");
            }

            if (loanApplicationFraudAlerts != null && loanApplicationFraudAlerts.stream().filter(a -> a.getActive() != null && a.getActive()).count() > 0) {
                return AjaxResponse.errorMessage("Se debe revisar las alertas de fraude");
            }
        }


        if (selectedOffer.getEntity().getId() == Entity.COMPARTAMOS && loanApplication.getCountryId() == CountryParam.COUNTRY_PERU && person.getDocumentType().getId() == IdentityDocumentType.DNI) {
            if (person.getBirthUbigeo() == null) {
                return AjaxResponse.errorMessage("Se debe registrar el ubigeo de nacimiento del solicitante");
            }
        }

        if (loanApplication.getFirstDueDate() == null) {
            return AjaxResponse.errorMessage("La fecha de primer pago no esta registrada.");
        }

        if (selectedOffer.getEntityProductParam().getGracePeriod() != null) {
            List<Date> enableDates = question50Service.getScheduleEnablesDates(selectedOffer.getEntityProductParam());
            boolean firstDueDateEnable = enableDates.stream().anyMatch(e -> DateUtils.truncate(e, Calendar.DATE).compareTo(DateUtils.truncate(selectedOffer.getFirstDueDate(), Calendar.DATE)) == 0);
            if (!firstDueDateEnable) {
                return AjaxResponse.errorMessage("La fecha de primer pago está fuera del rango permitido.");
            }
        }

        PersonOcupationalInformation principalOcupation = personDao.getPersonOcupationalInformation(locale, person.getId())
                .stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL)
                .findFirst().orElse(null);

        if (selectedOffer.getEntity().getId() == Entity.ACCESO && principalOcupation.getPhoneNumber() == null) {
            return AjaxResponse.errorMessage("Se debe registrar el teléfono laboral.");
        }

        Pair<Boolean, String> loanValidation = loanApplicationService.validateLoanApplicationApproval(loanApplicationId);
        if (loanValidation.getLeft() || approveAnyway) {
            // If acceso, call the acceso webservices
            if (selectedOffer.getEntity().getId() == Entity.ACCESO && selectedOffer.getProduct().getId() == Product.AUTOS) {
                PersonContactInformation contactInformation = personDao.getPersonContactInformation(locale, person.getId());

                Expediente expedienteToUpdate = new Expediente(translatorDao, person, user, principalOcupation, contactInformation);
                expedienteToUpdate.setNroExpediente(Integer.parseInt(loanApplication.getEntityApplicationCode()));
                accesoServiceCall.callCrearExpediente(expedienteToUpdate, loanApplicationId, 0);

                List<UserFile> userFiles = loanApplicationDao.getLoanApplicationUserFiles(loanApplication.getId());
                accesoServiceCall.callRegistrarDocumentos(new Expediente(fileService, Integer.parseInt(loanApplication.getEntityApplicationCode()), userFiles), loanApplicationId, 0);

                Direccion direccionCasa = personDao.getDisggregatedAddress(loanApplication.getPersonId(), "H");
                direccionCasa.setNroExpediente(Integer.valueOf(loanApplication.getEntityApplicationCode()));
                direccionCasa.setUbigeo(direccionCasa.getUbigeoInei());
                direccionCasa.setTipoZona(Integer.parseInt(translatorDao.translate(Entity.ACCESO, 40, direccionCasa.getTipoZona() + "", null)));
                direccionCasa.setNombreZona(direccionCasa.getNombreZona());
                Direccion direccionTrabajo = personDao.getDisggregatedAddress(loanApplication.getPersonId(), "J");
                direccionTrabajo.setNroExpediente(Integer.valueOf(loanApplication.getEntityApplicationCode()));
                direccionTrabajo.setUbigeo(direccionTrabajo.getUbigeoInei());
                direccionTrabajo.setTipoZona(Integer.parseInt(translatorDao.translate(Entity.ACCESO, 40, direccionTrabajo.getTipoZona() + "", null)));
                direccionTrabajo.setNombreZona(direccionTrabajo.getNombreZona());
                accesoServiceCall.callCrearDireccion(direccionCasa, loanApplicationId, 0);
                accesoServiceCall.callCrearDireccion(direccionTrabajo, loanApplicationId, 0);

                PersonOcupationalInformation personOcupationalInformation = personDao.getPersonOcupationalInformation(locale, loanApplication.getPersonId()).stream().filter(e -> e.getNumber().equals(PersonOcupationalInformation.PRINCIPAL)).findFirst().orElse(null);
                if (personOcupationalInformation != null) {
                    InformacionAdicional informacionAdicional = new InformacionAdicional(
                            translatorDao,
                            selectedOffer.getEntity().getId(),
                            Integer.valueOf(loanApplication.getEntityApplicationCode()),
                            person.getNationality().getId(),
                            personOcupationalInformation.getOcupation().getOcupation(),
                            personOcupationalInformation.getCompanyName(),
                            personOcupationalInformation.getRuc(),
                            personOcupationalInformation.getActivityType().getType(),
                            person.getStudyLevel().getId().toString(),
                            userDAO.getUserByDocument(person.getDocumentType().getId(), person.getDocumentNumber()).getEmail(),
                            new SimpleDateFormat("yy-MM-dd").format(utilService.addMonths(new Date(), Integer.valueOf(personOcupationalInformation.getEmploymentTime()) * (-1))),
                            personOcupationalInformation.getPhoneNumber(),
                            personOcupationalInformation.getFixedGrossIncome(),
                            personOcupationalInformation.getOcupation().getOcupation());
                    accesoServiceCall.callInformacionAdicional(informacionAdicional, loanApplicationId, 0);
                }
            }

            if (selectedOffer.getEntity().getId() == Entity.MULTIFINANZAS) {
                byte[] result = bureauService.renderBureauResultFromHtml(loanApplication, Bureau.NOSIS);
                String extension = "pdf";
                PersonInteractionAttachment summarySheetAttachment = new PersonInteractionAttachment();
                summarySheetAttachment.setBytes(result);
                summarySheetAttachment.setFilename("Consulta Nosis_".concat(selectedOffer.getEntity().getFullName()).concat(".").concat(extension));
                userService.registerUserFileByte(result, loanApplication.getId(), loanApplication.getUserId(), UserFileType.BUREAU_RESULT, extension);
            }

            loanApplicationService.approveLoanApplication(loanApplicationId, ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId(), request, response, templateEngine, locale);
            loanApplicationDao.updateLoanApplicationFilesUploaded(loanApplicationId, true);
            jsonResponse.put("ok", true);
            return AjaxResponse.ok(jsonResponse.toString());
        } else {
            jsonResponse.put("ok", false);
            jsonResponse.put("message", loanValidation.getRight());
            jsonResponse.put("canSkip", true); //TODO this should come from DB
            return AjaxResponse.ok(jsonResponse.toString());
        }
    }

    @RequestMapping(value = "/loanApplication/reject", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanApplication:refuse", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object rejectLoanApplication(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") Integer loanApplicationId,
            @RequestParam("applicationRejectionReasonId") Integer applicationRejectionReasonId,
            @RequestParam("applicationRejectionComment") String applicationRejectionComment) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        if (loanApplication.getCreditAnalystSysUserId() == null || loanApplication.getCreditAnalystSysUserId().intValue() != backofficeService.getLoggedSysuser().getId())
            return AjaxResponse.errorMessage("La solicitud está asignada a otro analista.");

        if(applicationRejectionComment != null && !applicationRejectionComment.trim().isEmpty() && applicationRejectionComment.trim().length() > 200) {
            return AjaxResponse.errorMessage("El comentario no puede tener más de 200 caracteres");
        }

        loanApplicationDao.updateLoanApplicationStatus(
                loanApplicationId, LoanApplicationStatus.REJECTED,
                ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId());
        loanApplicationDao.registerRejectionWithComment(loanApplicationId, applicationRejectionReasonId, applicationRejectionComment);
        LoanApplication la = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        emailBoService.sendRejectionMail(la, locale);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanApplication/assign/analyst", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanApplication:analyst:assign", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object assignAnalyst(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        if (loanApplication == null || loanApplication.getCreditAnalystSysUserId() != null)
            return AjaxResponse.errorMessage("La Loan Application ya ha sido asignada a un analista.");

        loanApplicationDao.assignanalyst(loanApplicationId,
                ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId(),
                ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId());
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanApplication/assign/management", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanApplication:analyst:assign", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object assignAnalystToManagement(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {
        loanApplicationDao.assignManagementAnalyst(loanApplicationId,
                ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId(),
                ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId());
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanApplication/unassign/analyst", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanApplication:analyst:unassign", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object unassignAnalystToManagement(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {

        loanApplicationDao.unassignManagementAnalyst(loanApplicationId);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanApplication/returnAssistedProcess", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:management", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object returnAssistedProcess(ModelMap model, Locale locale,
                                        @RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {
//        REASIGNAR ANALISTA
        loanApplicationDao.assignManagementAnalyst(loanApplicationId,
                ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId(),
                ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId());
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        //update assisted process flag and date
        loanApplicationDao.registerAssistedProcessSchedule(loanApplication.getId(), null);
        emailBoService.sendLinkReturningAssistedProcess(loanApplication, locale);
        return AjaxResponse.ok("");
    }

    @RequestMapping(value = "/loanApplication/{loanApplicationId}/firstDueDate", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanApplication:firstDueDate:update", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> updateFirstDueDate(
            ModelMap model, Locale locale,
            @RequestParam(value = "value", required = false) String value,
            @PathVariable("loanApplicationId") Integer loanApplicationId) throws Exception {

        if (value == null || value.isEmpty()) {
            return AjaxResponse.errorMessage("El valor no puede ser vacio");
        } else {
            loanApplicationDao.updateFirstDueDateWithSchedules(loanApplicationId, new SimpleDateFormat(com.affirm.system.configuration.Configuration.BACKOFFICE_FRONT_ONLY_DATE_FORMAT).parse(value));
            loanApplicationDao.createLoanOffers(loanApplicationId);
            return AjaxResponse.ok(null);
        }
    }

    @RequestMapping(value = "/loanApplication/locationMap/modal", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getLoanApplicationLocationMapModal(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") int loanApplicationId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        model.addAttribute("loanApplication", loanApplication);
        model.addAttribute("personContactInformation", personDao.getPersonContactInformation(locale, loanApplication.getPersonId()));
        return new ModelAndView("fragments/personFragments :: loanApplicationLocationMapModal");
    }

    @RequestMapping(value = "/loanApplication/viewasclient", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "loanApplication:viewAsClient", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object viewLoanApplicationAsClient(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") int loanApplicationId,
            @RequestParam("personId") int personId,
            @RequestParam("userId") int userId) throws Exception {

        // Open the no auth link for 5 seconds
        loanApplicationDao.registerNoAuthLinkExpiration(loanApplicationId, 5);
        // Redirect to the loanApplication
        return "redirect:" + utilService.createLoanApplicationClientUrl(userId, loanApplicationId, personId);
    }

    @RequestMapping(value = "/loanApplication/uploadTokyCall", method = RequestMethod.POST)
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
            return AjaxResponse.errorMessage("El formato de la llamada Toky es incorrecto.");
        }

    }


    @RequestMapping(value = "/loanApplication/deleteTokyCall", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object deleteTokyCall(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {

        Integer personId = loanApplicationDao.getLoanApplication(loanApplicationId, locale).getPersonId();

        LoanApplicationUserFiles files = new LoanApplicationUserFiles();
        List<LoanApplicationUserFiles> userFiles = personDao.getUserFiles(personId, locale);
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

    @RequestMapping(value = "/loanApplication/{loanApplicationId}/registerContactResult", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanApplication:management:contactResult:register", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerContact(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("loanApplicationId") Integer loanApplicationId,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "detailReason", required = false) Integer detailReason,
            @RequestParam(value = "detailReasonComment", required = false) String detailReasonComment,
            @RequestParam("contactResultId") Integer contactResultId,
            @RequestParam(value = "personAnswerCall", required = false) Boolean personAnswerCall,
            @RequestParam(value = "contactRelationship", required = false) Integer contactRelationship) throws Exception {

        /*Date finalDate = new Date();
        if (contactResultId.equals(TrackingAction.BUSY)) finalDate = utilService.addHours(finalDate, 18);
        else if (contactResultId.equals(TrackingAction.INTERESTED)) finalDate = utilService.addHours(finalDate, 48);
        else finalDate = date != null && !date.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy H:mm").parse(date) : null;*/

        if(detailReasonComment != null && !detailReasonComment.trim().isEmpty() && detailReasonComment.trim().length() > 200) {
            return AjaxResponse.errorMessage("El comentario no puede tener más de 200 caracteres");
        }

//        REASIGNAR ANALISTA
        loanApplicationDao.assignManagementAnalyst(loanApplicationId,
                ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId(),
                ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId());

        List<Holiday> holidays = catalogService.getHolidaysByCountry(loanApplicationDao.getLoanApplication(loanApplicationId, locale).getCountryId());
        Date finalDate = utilService.getAvailableDate(holidays, contactResultId, date);

        if(contactResultId.equals(TrackingAction.INTERESTED) || contactResultId.equals(TrackingAction.BUSY) || contactResultId.equals(TrackingAction.NO_SE_ENCUENTRA)){
            loanApplicationDao.registerTrackingActionContactPerson(loanApplicationId, ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId(), contactResultId, detailReason, finalDate, personAnswerCall, contactRelationship);
        }else {
            loanApplicationDao.registerTrackingAction(loanApplicationId, ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId(), contactResultId, detailReason, finalDate);

            if (contactResultId.equals(TrackingAction.NOT_INTERESTED)) {
                loanApplicationDao.registerRejectionWithComment(loanApplicationId, ApplicationRejectionReason.USER_CANCELATION, detailReasonComment);

                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
                Integer creditId = loanApplication.getCreditId();
                Credit credit = null;
                if (creditId != null) {
                    creditDao.registerRejectionWithComment(creditId, CreditRejectionReason.USER_CANCELATION, detailReasonComment);
                    credit = creditDao.getCreditByID(creditId, locale, false, Credit.class);
                }
                loanNotifierService.notifyRejection(loanApplication, credit);
            }else if (contactResultId.equals(TrackingAction.DESACTIVAR_GESTION)) {
                loanApplicationDao.updateDisableTracking(loanApplicationId, true);
            }
        }

        if((contactResultId.equals(TrackingAction.INTERESTED) || contactResultId.equals(TrackingAction.MISSING_DOCUMENTATION) || contactResultId.equals(TrackingAction.SCHEDULE_PHONECALL)) && contactRelationship == null){
            LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
            emailBoService.sendLinkReturningAssistedProcess(loanApplication, locale);
        }

        switch (contactResultId) {
            case TrackingAction.BUSY:
            case TrackingAction.WRONG_NUMBER:
            case TrackingAction.NO_RESPONSE:
            case TrackingAction.DESACTIVAR_GESTION:
            case TrackingAction.NO_SE_ENCUENTRA: {
                String oneSignalJSONActive = "active";
                String oneSignalJSONScheduledNotificationId = "scheduled_notification_id";

                String notificationProvider = OneSignalServiceImpl.ONESIGNAL_PROVIDER;
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
                Integer countryId = loanApplication.getCountryId();
                if(loanApplication.getJsNotificationTokens() != null &&
                        loanApplication.getJsNotificationTokens().has(notificationProvider) &&
                        loanApplication.getJsNotificationTokens().getJSONObject(notificationProvider).has(oneSignalJSONActive)
                ) {
                    JSONObject tokens = loanApplication.getJsNotificationTokens();
                    JSONObject oneSignalTokens = tokens.getJSONObject(OneSignalServiceImpl.ONESIGNAL_PROVIDER);

                    String[] notificationMessage = Configuration.getOneSignalTemplate(OneSignalServiceImpl.NotificationTemplate.CALL_INTERACTION_NO_RESPONSE);
                    String activeOneSignalPlayerId = oneSignalTokens.getString(oneSignalJSONActive);

//                    SI YA CONTABA CON UNA NOTIFICACION A FUTURO. ELIMINO REGISTRO Y CANCELO NOTIFICACION
                    if(oneSignalTokens.has(oneSignalJSONScheduledNotificationId)) {
                        boolean canceledNotification = oneSignalService.cancelScheduledNotification(loanApplication.getEntityId(), countryId, oneSignalTokens.getString(oneSignalJSONScheduledNotificationId));
                        logger.debug((canceledNotification ? "Scheduled notification canceled with id: " : "Could not cancel scheduled notification with id: ") + oneSignalTokens.getString(oneSignalJSONScheduledNotificationId));
                        oneSignalTokens.remove(oneSignalJSONScheduledNotificationId);
                    }

//                    ENVIAR NOTIFICACION DENTRO DE X MINUTOS
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.MINUTE, Configuration.hostEnvIsProduction() ? 10 : 2);

                    String timeToSendNotification = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 0));

                    JSONObject filters = oneSignalService.applyNotificationsScheduleDelivery(null, timeToSendNotification, null);
                    String notificationId = oneSignalService.sendNotification(loanApplication.getEntityId(), countryId, notificationMessage, Collections.singletonList(activeOneSignalPlayerId), null, filters);

                    oneSignalTokens.put(oneSignalJSONScheduledNotificationId, notificationId);
                    logger.debug(oneSignalTokens);
                    tokens.put(OneSignalServiceImpl.ONESIGNAL_PROVIDER, oneSignalTokens);

                    loanApplicationDao.saveLastActiveOneSignalPlayerId(loanApplicationId, tokens);
                }
                break;
            }
        }

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanApplication/{loanApplicationId}/getTrackingDetails", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanApplication:management:contactResult:register", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getTrackingDetails(
            ModelMap model, Locale locale,
            @RequestParam("contactResultId") Integer contactResultId) throws Exception {

        return AjaxResponse.ok(utilService.toJson(catalogService.getTrackingDetailsByTrackingAction(contactResultId)));
    }


    @RequestMapping(value = "/loanApplication/questionProcess/modal", method = RequestMethod.GET)
//    @RequiresPermissionOr403(permissions = "credit:disbursement:register", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getQuestionProcessModal(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {
//        REASIGNAR ANALISTA
        loanApplicationDao.assignManagementAnalyst(loanApplicationId,
                ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId(),
                ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId());

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);

//        TreeProcessQuestionWrapper wrapper = loanApplicationQuestionService.getCurrentCategoryQuestions(loanApplication, loanApplication.getCurrentQuestionId(), categoryId);
        Map<Integer, TreeProcessQuestionWrapper> categoriesWrappers = loanApplicationQuestionService.getAnsweredQuestionsWrapperGroupedByCategory(loanApplication);

        // Get the current category
        Integer currentCategory = catalogService.getProcessQuestion(loanApplication.getCurrentQuestionId()).getCategory().getId();

        // Delete the categories that doesnt show in the modal
        if (categoriesWrappers.containsKey(ProcessQuestionCategory.VERIFICATION))
            categoriesWrappers.remove(ProcessQuestionCategory.VERIFICATION);
        if (categoriesWrappers.containsKey(ProcessQuestionCategory.WAITING_APPROVAL))
            categoriesWrappers.remove(ProcessQuestionCategory.WAITING_APPROVAL);

        if (categoriesWrappers.containsKey(currentCategory)) {
            TreeProcessQuestionWrapper currentCategoryWrapper = categoriesWrappers.get(currentCategory);
            // If the user is in the first question of the category, check if the question needs to be skipped
            if (currentCategoryWrapper.getChilds() == null) {
                Object questionServiceObject = applicationContext.getBean("question" + currentCategoryWrapper.getProcessQuestion().getId() + "Service");
                if (questionServiceObject != null) {
                    String whereToSkip = ((AbstractQuestionService) questionServiceObject).getSkippedQuestionResultToGo(QuestionFlowService.Type.LOANAPPLICATION, loanApplicationId, locale, false);
                    if (whereToSkip != null) {
                        ((AbstractQuestionService) questionServiceObject).skipQuestionBackoffice(QuestionFlowService.Type.LOANAPPLICATION, loanApplicationId,
                                locale,
                                null,
                                currentCategoryWrapper.getProcessQuestion().getId());

                        loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());

                        // Redo the ame as above
                        categoriesWrappers = loanApplicationQuestionService.getAnsweredQuestionsWrapperGroupedByCategory(loanApplication);

                        // Get the current category
                        currentCategory = catalogService.getProcessQuestion(loanApplication.getCurrentQuestionId()).getCategory().getId();

                        // Delete the categories that doesnt show in the modal
                        if (categoriesWrappers.containsKey(ProcessQuestionCategory.VERIFICATION))
                            categoriesWrappers.remove(ProcessQuestionCategory.VERIFICATION);
                        if (categoriesWrappers.containsKey(ProcessQuestionCategory.WAITING_APPROVAL))
                            categoriesWrappers.remove(ProcessQuestionCategory.WAITING_APPROVAL);
                    }
                }
            }
        }

        Map<Integer, String> categoryClasses = new HashMap<>();
        categoryClasses.put(ProcessQuestionCategory.PRE_INFORMATION, "category-pre-information");
        categoryClasses.put(ProcessQuestionCategory.PERSONAL_INFORMATION, "personal-information");
        categoryClasses.put(ProcessQuestionCategory.INCOME, "income");
        categoryClasses.put(ProcessQuestionCategory.OFFER, "offer");
        categoryClasses.put(ProcessQuestionCategory.EVALUATION, "evaluation");
        categoryClasses.put(ProcessQuestionCategory.RESULT, "result");

        Map<Integer, String> categoryNames = new HashMap<>();
        categoryNames.put(ProcessQuestionCategory.PRE_INFORMATION, "Pre Información");
        categoryNames.put(ProcessQuestionCategory.PERSONAL_INFORMATION, "Información Personal");
        categoryNames.put(ProcessQuestionCategory.INCOME, "Ingresos");
        categoryNames.put(ProcessQuestionCategory.OFFER, "Ofertas");
        categoryNames.put(ProcessQuestionCategory.EVALUATION, "Evaluación");
        categoryNames.put(ProcessQuestionCategory.RESULT, "Resultado");


        model.addAttribute("currentCategory", currentCategory);
        model.addAttribute("categoryClasses", categoryClasses);
        model.addAttribute("categoryNames", categoryNames);
        model.addAttribute("loanApplication", loanApplication);
        model.addAttribute("categoriesWrappers", categoriesWrappers);
        model.addAttribute("answeredQuestion", true);
        return new ModelAndView("fragments/questionProcessFragments :: questionsModal");
    }

    @RequestMapping(value = "/loanApplication/questionProcess/refresh/modal", method = RequestMethod.POST)
//    @RequiresPermissionOr403(permissions = "credit:disbursement:register", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object refreshQuestionProcessModal(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("loanApplicationId") Integer loanApplicationId,
            @RequestParam("questionId") Integer questionId,
            @RequestParam("jsonParams") String jsonParam) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        Object questionServiceObject = applicationContext.getBean("question" + questionId + "Service");

        Integer questionIdToGo = null;
        if (questionServiceObject != null) {
            FormGeneric form = ((AbstractQuestionService) questionServiceObject).fromJsonToForm(jsonParam);

            ResponseEntity validationResponse = ((AbstractQuestionService) questionServiceObject).validateFormBackoffice(
                    QuestionFlowService.Type.LOANAPPLICATION,
                    loanApplicationId,
                    form,
                    locale);
            if (validationResponse != null) {
                return validationResponse;
            }

            questionIdToGo = ((AbstractQuestionService) questionServiceObject).getQuestionIdToGo(
                    QuestionFlowService.Type.LOANAPPLICATION,
                    loanApplicationId,
                    form,
                    request,
                    questionId);
        }

        int categoryId = catalogService.getProcessQuestion(questionId).getCategory().getId();
        TreeProcessQuestionWrapper wrapper = loanApplicationQuestionService.getCurrentCategoryQuestions(loanApplication, questionIdToGo, categoryId);

        model.addAttribute("treeQuestion", wrapper);
        model.addAttribute("answeredQuestion", false);
        if (wrapper == null) {
            // theres no more questions, so show the button to register the questions
            model.addAttribute("categoryId", categoryId);
            return new ModelAndView("fragments/questionProcessFragments :: categorySaveButonDiv");
        } else {
            return new ModelAndView("fragments/questionProcessFragments :: questionDiv");
        }
    }

    @RequestMapping(value = "/loanApplication/questionProcess/customMethod/{customMethodName}", method = RequestMethod.POST)
//    @RequiresPermissionOr403(permissions = "credit:disbursement:register", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object customMethodQuestionProcessModal(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("customMethodName") String customMethodName,
            @RequestParam("loanApplicationId") Integer loanApplicationId,
            @RequestParam("questionId") Integer questionId,
            @RequestParam("jsonParams") String jsonParam) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        Object questionServiceObject = applicationContext.getBean("question" + questionId + "Service");

        if (questionServiceObject != null) {
            Map<String, Object> mapParams = new HashMap<>();
            if (jsonParam != null) {
                JSONObject json = new JSONObject(jsonParam);
                mapParams = json.toMap();
            }

            return ((AbstractQuestionService) questionServiceObject).customMethod(
                    customMethodName,
                    QuestionFlowService.Type.LOANAPPLICATION,
                    loanApplicationId,
                    locale,
                    mapParams);
        }

        throw new Exception("No method configured for " + customMethodName);
    }

    @RequestMapping(value = "/loanApplication/runFraudAlerts", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object runFraudAlerts(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);

        if (loanApplication.getCreditAnalystSysUserId().intValue() != backofficeService.getLoggedSysuser().getId()) {
            return AjaxResponse.errorMessage("La solicitud está asignada a otro analista.");
        }

        if (loanApplication.getFraudAlertQueryIds() == null || loanApplication.getFraudAlertQueryIds().isEmpty()) {
            webscrapperService.callRunFraudAlerts(loanApplication);
        } else {
            Integer lastExecutedBotId = loanApplication.getFraudAlertQueryIds().stream().sorted(Comparator.reverseOrder()).findFirst().orElse(null);
            if (lastExecutedBotId != null) {
                QueryBot queryBot = botDAO.getQueryBot(lastExecutedBotId);
                if (queryBot.getStatusId() != QueryBot.STATUS_QUEUE && queryBot.getStatusId() != QueryBot.STATUS_RUNNING) {
                    webscrapperService.callRunFraudAlerts(loanApplication);
                } else {
                    return AjaxResponse.errorMessage("Ya se encuentran corriendo las alertas de fraude.");
                }
            }
        }

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanApplication/expire", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanApplication:expire", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object expire(@RequestParam("loanApplicationId") Integer loanApplicationId) {

        loanApplicationDao.expireLoanApplication(loanApplicationId);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanApplication/reassign", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:offers", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object loanApplicationOfferReasignWithComment(
            ModelMap model, Locale locale,
            @RequestParam(value = "loanApplicationId") Integer loanApplicationId,
            @RequestParam(value = "comment") String comment) throws Exception {
//        REASIGNAR ANALISTA
        loanApplicationDao.assignManagementAnalyst(loanApplicationId,
                ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId(),
                ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId());

        loanApplicationDao.registerLoanApplicationComment(loanApplicationId, comment, backofficeService.getLoggedSysuser().getId(), Comment.COMMENT_REASSIGN);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanApplication/liftComment", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:management", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object loanApplicationOfferLiftComment(
            ModelMap model, Locale locale,
            @RequestParam(value = "loanApplicationId") Integer loanApplicationId) throws Exception {
//        REASIGNAR ANALISTA
        loanApplicationDao.assignManagementAnalyst(loanApplicationId,
                ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId(),
                ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId());

        loanApplicationDao.registerLoanApplicationLiftComment(loanApplicationId);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanApplication/changeFirstDueDate", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object changeFirstDueDate(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") Integer loanApplicationId,
            @RequestParam("firstDueDate") String firstDueDate) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);

        if (loanApplication.getCreditAnalystSysUserId().intValue() != backofficeService.getLoggedSysuser().getId())
            return AjaxResponse.errorMessage("La solicitud está asignada a otro analista.");

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
        loanApplicationDao.selectLoanOfferAnalyst(newOffer.getId(), backofficeService.getLoggedSysuser().getId());
        loanApplicationDao.updateCurrentQuestion(loanApplicationId, ProcessQuestion.Question.Constants.CONTRACT_SIGNATURE);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanApplication/changeFirstDueDate/modal", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getChangeFirstDueDateModal(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") int loanApplicationId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        LoanOffer selectedOffer = loanApplicationDao.getLoanOffersAll(loanApplicationId).stream().filter(o -> o.getSelected()).findFirst().orElse(null);
        if(selectedOffer == null)
            return AjaxResponse.errorMessage("no existe una oferta seleccionada a&uacute;n");

        List<Date> enableDates = question50Service.getScheduleEnablesDates(selectedOffer.getEntityProductParam());
        String[] enabledDatesFormatted = new String[enableDates.size()];
        for (int i = 0; i < enableDates.size(); i++) {
            enabledDatesFormatted[i] = new SimpleDateFormat("dd/MM/yyyy").format(enableDates.get(i));
        }

        model.addAttribute("loanApplication", loanApplication);
        model.addAttribute("enableDates", enabledDatesFormatted);

        String selectedDateFormatted = null;
        if(loanApplication.getFirstDueDate() != null)
            selectedDateFormatted = new SimpleDateFormat("dd/MM/yyyy").format(loanApplication.getFirstDueDate());
        model.addAttribute("selectedDate", selectedDateFormatted);
        return new ModelAndView("fragments/personFragments :: changeFirstDueDateModal");
    }

    @RequestMapping(value = "/loanApplication/returnToContract", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanApplication:returnToContract", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object returnToContract(@RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception{

        loanApplicationDao.boResetContract(loanApplicationId);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanApplication/reevaluate", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanApplication:reevaluate", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object reevaluate(@RequestParam("loanApplicationId") Integer loanApplicationId){

        loanApplicationService.reevaluateLoanApplications(loanApplicationId);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanApplication/returnToEvaluationList", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanApplication:returnToEvaluationList", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object returnToEvaluationList(@RequestParam("loanApplicationId") Integer loanApplicationId){

        loanApplicationDao.reactivateApplication(loanApplicationId, LoanApplicationStatus.WAITING_APPROVAL);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanApplication/returnToManagementList", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanApplication:returnToManagementList", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object returnToManagementList(@RequestParam("loanApplicationId") Integer loanApplicationId){

        loanApplicationDao.reactivateApplication(loanApplicationId, LoanApplicationStatus.EVAL_APPROVED);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanApplication/verificationEmail", method = RequestMethod.POST)
//    @RequiresPermissionOr403(permissions = "loanApplication:returnToManagementList", type = RequiresPermissionOr403.Type.AJAX)// TODO PERMISSIONS
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object sendVerificationEmail(@RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
        User user = userDAO.getUser(loanApplication.getUserId());

        if ((user.getEmailVerified() == null || !user.getEmailVerified())) {// IF EMAIL IS NOT VERIFIED
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 5);

            PersonInteraction personInteraction = new PersonInteraction();
            personInteraction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
            if(loanApplication.getCountry().getId() == CountryParam.COUNTRY_ARGENTINA){
                personInteraction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.ARG_PROCESS_SEND_EMAIL_VERIFICATION, loanApplication.getCountry().getId()));
            }else{
                personInteraction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.PERU_PROCESS_SEND_EMAIL_VERIFICATION, loanApplication.getCountry().getId()));
            }
            personInteraction.setDestination(user.getEmail());
            personInteraction.setPersonId(loanApplication.getPersonId());
            personInteraction.setLoanApplicationId(loanApplication.getId());
            personInteraction.setCreditId(loanApplication.getCreditId());

            Integer emailId = userDAO.getUserEmails(loanApplication.getUserId()).stream().filter(e -> e.getActive() != null && e.getActive()).map(UserEmail::getId).findFirst().orElse(0);
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

            userDAO.verifyEmail(loanApplication.getUserId(), emailId, false);// return email to is_verified = false for users.tb_users and users.tb_email
        } else {
            return AjaxResponse.errorMessage("El correo ya se encuentra verificado");
        }

        return AjaxResponse.ok(null);
    }
}
