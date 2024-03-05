package com.affirm.common.service.question;

import com.affirm.bancoazteca.model.BancoAztecaCampaniaApi;
import com.affirm.bantotalrest.exception.BT40147Exception;
import com.affirm.bpeoplerest.service.BPeopleApiRestService;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.Question135BankForm;
import com.affirm.common.model.form.Question135Form;
import com.affirm.common.model.form.Question135SmsPinForm;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.*;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service("question135Service")
public class Question135Service extends AbstractQuestionService<Question135Form> {

    @Autowired
    private PersonDAO personDao;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CreditService creditService;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private UserService userService;
    @Autowired
    private CreditDAO creditDAO;
    @Autowired
    private Question50Service question50Service;
    @Autowired
    private BPeopleApiRestService bPeopleApiRestService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private MessageSource messageSource;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                User user = userDao.getUser(loanApplication.getUserId());
                LoanOffer selectedOffer = loanApplicationDao.getLoanOffers(loanApplication.getId())
                        .stream().filter(o -> o.getSelected()).findFirst().orElse(null);
                EntityProductParams entityParams = selectedOffer.getEntityProductParam();
                String customMessage = null;
                boolean shouldVerifyEmail = false;
                boolean emailVerified = user.getEmailVerified() != null ? user.getEmailVerified() : false;
                if(selectedOffer != null && Arrays.asList(
                        EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE,
                        EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO
                ).contains(selectedOffer.getEntityProductParameterId())){
                    if(!emailVerified) shouldVerifyEmail = true;
                }

                if (selectedOffer.getSignatureFullName() != null && !selectedOffer.getSignatureFullName().isEmpty()) {
                    if(Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE,
                            EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO)
                            .contains(selectedOffer.getEntityProductParameterId())){
                        HttpServletRequest request = (HttpServletRequest) params.get("request");
                        HttpServletResponse response = (HttpServletResponse) params.get("response");
                        SpringTemplateEngine templateEngine = (SpringTemplateEngine) params.get("templateEngine");
                        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), true);
                        throw new GoToNextQuestionException("DEFAULT", ProcessQuestionSequence.TYPE_FORWARD);
                    }
                }

                if(selectedOffer != null && Arrays.asList(
                        EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE,
                        EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO
                ).contains(selectedOffer.getEntityProductParameterId()) && !shouldVerifyEmail){

                    if(Arrays.asList(
                            EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE
                    ).contains(selectedOffer.getEntityProductParameterId()) && (loanApplication.getBanTotalApiData() == null || loanApplication.getBanTotalApiData().getOperacionUIdSimulacionConCliente() == null)){
                        boolean isValidDueDate = loanApplicationService.isValidDueDate(loanApplication, 0);
                        if(!isValidDueDate){
                            List<Date> enableDates = question50Service.getScheduleEnablesDates(selectedOffer.getEntityProductParam());
                            Date newDueDate = this.loanApplicationService.updateAlfinDueDate(loanApplication, enableDates, selectedOffer.getEntityProductParam());
                            loanApplication.setFirstDueDate(newDueDate);
                            selectedOffer.setFirstDueDate(newDueDate);
                        }
                    }
                }

                boolean isBankRequired = bankRequired(entityParams, selectedOffer);
                boolean isPaymentApplicationTypeRequired = paymentAplicationTypeRequired(selectedOffer);
                boolean showContractResume = entityParams.getShowResumeContract();

                attributes.put("bankRequired", isBankRequired);
                attributes.put("paymentApplicationTypeRequired", isPaymentApplicationTypeRequired);
                attributes.put("showContractResume", showContractResume);
                attributes.put("smsPinRequired", smsPinRequired(selectedOffer));
                attributes.put("phoneNumberMasked", user.getPhoneNumber());
                attributes.put("person", personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false));

                attributes.put("showTerm1", true);
                attributes.put("showTerm2", true);
                attributes.put("showTerm3", true);

                if (!isBankRequired && !isPaymentApplicationTypeRequired) {
                    Question135Form form = new Question135Form();
                    if(selectedOffer.getEntityProductParameterId().equals(EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO)){
                        ((Question135Form.Validator) form.getValidator()).terms1.setRequired(false);
                        attributes.put("showTerm1", false);
                    } else {
                        ((Question135Form.Validator) form.getValidator()).terms1.setRequired(true);
                    }
                    ((Question135Form.Validator) form.getValidator()).terms2.setRequired(true);
                    if(entityParams.getEntity() != null && entityParams.getEntity().getId() != null && (entityParams.getEntity().getId() == Entity.PRISMA || entityParams.getEntity().getId() == Entity.AZTECA)){
                        ((Question135Form.Validator) form.getValidator()).terms3.setRequired(true);
                    }

                    attributes.put("signatureForm", form);
                } else {
                    attributes.put("signatureForm", new Question135Form());
                }

                if(shouldVerifyEmail) customMessage =  messageSource.getMessage("questions.135.emailMustBeVerified", null, Configuration.getDefaultLocale());
                
                if(selectedOffer != null && Arrays.asList(
                        EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE,
                        EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO
                ).contains(selectedOffer.getEntityProductParameterId()) && shouldVerifyEmail){
                    this.loanApplicationService.sendLoanApplicationApprovalMail(loanApplication.getId(), loanApplication.getPersonId(), locale);
                }


                attributes.put("bankForm", new Question135BankForm());
                attributes.put("smsPinForm", new Question135SmsPinForm());
                attributes.put("showAgent", true);
                attributes.put("offer", selectedOffer);
                attributes.put("customMessage", customMessage);
                attributes.put("shouldVerifyEmail", shouldVerifyEmail);
                Boolean bancoAztecaDueDateModified = JsonUtil.getBooleanFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.AZTECA_DUE_DATE_MODIFIED.getKey(), false);
                Boolean bancoAztecaDueDateModifiedAfterContract = JsonUtil.getBooleanFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.AZTECA_DUE_DATE_MODIFIED_AFTER_CONTRACT.getKey(), false);
                attributes.put("bancoAztecaDueDateModified", bancoAztecaDueDateModified);
                attributes.put("bancoAztecaDueDateModifiedAfterContract", bancoAztecaDueDateModifiedAfterContract);
                if(selectedOffer.getInstallmentAmountAvg() != null && selectedOffer.getOfferSchedule().stream() != null) attributes.put("maxInstallmentAmount", Math.max(selectedOffer.getInstallmentAmountAvg(), selectedOffer.getOfferSchedule().stream().filter(o -> o.getInstallmentId() == 1).mapToDouble(OriginalSchedule::getInstallmentAmount).findFirst().orElse(0.0)));
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question135Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question135Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question135Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanOffer selectedOffer = loanApplicationDao.getLoanOffers(id).stream().filter(o -> o.getSelected()).findFirst().orElse(null);
                if (selectedOffer.getSignatureFullName() == null || selectedOffer.getSignatureFullName().isEmpty())
                    throw new ResponseEntityException(AjaxResponse.errorMessage("Aún no tienes firma registrada."));
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanOffer selectedOffer = loanApplicationDao.getLoanOffers(id).stream().filter(o -> o.getSelected()).findFirst().orElse(null);
                if(!Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE,EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO).contains(selectedOffer.getEntityProductParameterId())) {
                    if (selectedOffer.getSignatureFullName() != null && !selectedOffer.getSignatureFullName().isEmpty()) {
                        return "DEFAULT";
                    }
                    if (!Arrays.asList(EntityProductParams.CONTRACT_TYPE_DIGITAL, EntityProductParams.CONTRACT_TYPE_MIXTO).contains(selectedOffer.getEntityProductParam().getSignatureType())) {
                        return "DEFAULT";
                    }
                }
                if (selectedOffer.getEntityProductParam().getId() == EntityProductParams.ENT_PROD_PARAM_TARJETA_PERUANA_PREPAGO) {
                    if (saveData) {
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
                        registerSignature(
                                loanApplication,
                                person,
                                selectedOffer,
                                null,
                                person.getName().substring(0, 1).concat(". ").concat(person.getFirstSurname()),
                                locale, null, null, null);
                    }
                    return "DEFAULT";
                }

                break;
        }
        return null;
    }

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                switch (path) {
                    case "contract": {
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                        LoanOffer selectedOffer = loanApplicationDao.getLoanOffers(loanApplication.getId()).stream().filter(o -> o.getSelected()).findFirst().orElse(null);

                        byte[] pdfAsBytes = creditService.createOfferContract(
                                loanApplication,
                                selectedOffer,
                                (HttpServletRequest) params.get("request"),
                                (HttpServletResponse) params.get("response"),
                                locale,
                                (SpringTemplateEngine) params.get("templateEngine"),
                                null,
                                true);

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_PDF);
                        ResponseEntity<byte[]> downloadablePdf = new ResponseEntity<byte[]>(pdfAsBytes, headers, HttpStatus.OK);
                        return downloadablePdf;
                    }
                    case "paymentTypeSignature": {
                        String paymentType = (String) params.get("paymenType");
                        String signature = (String) params.get("signature");
                        HttpServletRequest request = (HttpServletRequest) params.get("request");
                        HttpServletResponse response = (HttpServletResponse) params.get("response");
                        SpringTemplateEngine templateEngine = (SpringTemplateEngine) params.get("templateEngine");

                        // Validate the form
                        if (!paymentType.equals("A") && !paymentType.equals("D"))
                            return AjaxResponse.errorMessage("El valor enviado no es válido");
                        if (!new StringFieldValidator(ValidatorUtil.LOANAPPLICATION_SIGNATURE, signature).validate(locale))
                            return AjaxResponse.errorMessage("La firma no es v&aacute;lida");

                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                        LoanOffer selectedOffer = loanApplicationDao.getLoanOffers(loanApplication.getId()).stream().filter(o -> o.getSelected()).findFirst().orElse(null);
                        EntityProductParams entityParams = selectedOffer.getEntityProductParam();
                        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), true);

                        // Validate the signature
                        if (!isSignature2Ok(person, signature))
                            return AjaxResponse.errorMessage("Tu firma no coincide con tu nombre.");

                        // Save the payment type
                        loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.APPLICATION_PAYMENT_TYPE.getKey(), paymentType);
                        loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());

                        if (bankRequired(entityParams, selectedOffer))
                            return AjaxResponse.ok(null);

                        // Register the signature
                        registerSignature(loanApplication, person, selectedOffer, null, signature, locale, request, response, templateEngine);

                        throw new GoToNextQuestionException("DEFAULT", ProcessQuestionSequence.TYPE_FORWARD);
                    }
                    case "bankSignature": {
                        Question135BankForm bankForm = (Question135BankForm) params.get("bankForm");
                        String signature = (String) params.get("signature");
                        HttpServletRequest request = (HttpServletRequest) params.get("request");
                        HttpServletResponse response = (HttpServletResponse) params.get("response");
                        SpringTemplateEngine templateEngine = (SpringTemplateEngine) params.get("templateEngine");

                        bankForm.getValidator().validate(locale);
                        if (bankForm.getValidator().isHasErrors()) {
                            return AjaxResponse.errorFormValidation(bankForm.getValidator().getErrorsJson());
                        }
                        if (!new StringFieldValidator(ValidatorUtil.LOANAPPLICATION_SIGNATURE, signature).validate(locale)) {
                            return AjaxResponse.errorMessage("La firma no es v&aacute;lida");
                        }

                        // Validate the signature
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                        LoanOffer selectedOffer = loanApplicationDao.getLoanOffers(loanApplication.getId()).stream().filter(o -> o.getSelected()).findFirst().orElse(null);
                        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
                        if (!isSignature2Ok(person, signature)) {
                            return AjaxResponse.errorMessage("La firma no coincide con el texto resaltado en amarillo.");
                        }

                        // Register the signature
                        registerSignature(loanApplication, person, selectedOffer, bankForm, signature, locale, request, response, templateEngine);

                        throw new GoToNextQuestionException("DEFAULT", ProcessQuestionSequence.TYPE_FORWARD);
                    }
                    case "signature": {
                        String signature = (String) params.get("signature");
                        HttpServletRequest request = (HttpServletRequest) params.get("request");
                        HttpServletResponse response = (HttpServletResponse) params.get("response");
                        SpringTemplateEngine templateEngine = (SpringTemplateEngine) params.get("templateEngine");


                        // Validate the signature
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                        LoanOffer selectedOffer = loanApplicationDao.getLoanOffers(loanApplication.getId()).stream().filter(o -> o.getSelected()).findFirst().orElse(null);
                        EntityProductParams entityParams = selectedOffer.getEntityProductParam();
                        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
                        if (!isSignature2Ok(person, signature)) {
                            return AjaxResponse.errorMessage("La firma no coincide con el texto resaltado en amarillo.");
                        }

                        if(loanApplication.getAuxData() == null) loanApplication.setAuxData(new LoanApplicationAuxData());
                        loanApplication.getAuxData().setTerms1Contract(params.get("terms1") != null ? (Boolean) params.get("terms1") : null);
                        loanApplication.getAuxData().setTerms2Contract(params.get("terms2") != null ? (Boolean) params.get("terms2") : null);
                        loanApplication.getAuxData().setTerms3Contract(params.get("terms3") != null ? (Boolean) params.get("terms3") : null);
                        loanApplicationDao.updateAuxData(loanApplication.getId(),loanApplication.getAuxData());
                        
                        if (bankRequired(entityParams, selectedOffer))
                            return AjaxResponse.ok(null);
                        if (paymentAplicationTypeRequired(selectedOffer))
                            return AjaxResponse.ok(null);
                        if (smsPinRequired(selectedOffer)) {
                            if (selectedOffer.getSignaturePinTries() == null) {
                                User user = userDao.getUser(loanApplication.getUserId());
                                EntityBranding entityBranding = loanApplication.getEntityId() != null ? catalogService.getEntityBranding(loanApplication.getEntityId()) : null;
                                String entityShort = entityBranding != null && entityBranding.getBranded() ? entityBranding.getEntity().getShortName() : null;

                                sendContractPinInteraction(selectedOffer.getSignaturePin(), InteractionType.SMS, loanApplication, user, person, entityShort);
                            }
                            return AjaxResponse.ok(null);
                        }

                        // Register the signature
                        registerSignature(loanApplication, person, selectedOffer, null, signature, locale, request, response, templateEngine);
                        if(loanApplication.getCredit() != null && loanApplication.getCredit() && !Arrays.asList(LoanApplicationStatus.APPROVED_SIGNED,LoanApplicationStatus.APPROVED).contains(loanApplication.getStatus().getId())){
                            loanApplicationDao.updateLoanApplicationStatus(id,LoanApplicationStatus.APPROVED_SIGNED,null);
                        }

                        throw new GoToNextQuestionException("DEFAULT", ProcessQuestionSequence.TYPE_FORWARD);
                    }
                    case "validateSmsPin": {
                        Question135SmsPinForm smsPinForm = (Question135SmsPinForm) params.get("smsPinForm");
                        String signature = (String) params.get("signature");
                        HttpServletRequest request = (HttpServletRequest) params.get("request");
                        HttpServletResponse response = (HttpServletResponse) params.get("response");
                        SpringTemplateEngine templateEngine = (SpringTemplateEngine) params.get("templateEngine");


                        // validate and register the pin validation
                        smsPinForm.getValidator().validate(locale);
                        if (smsPinForm.getValidator().isHasErrors()) {
                            return AjaxResponse.errorFormValidation(smsPinForm.getValidator().getErrorsJson());
                        }
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                        LoanOffer selectedOffer = loanApplicationDao.getLoanOffers(loanApplication.getId()).stream().filter(LoanOffer::getSelected).findFirst().orElse(null);
                        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), true);

                        if (selectedOffer != null) {
                            JSONObject validatePinJson = userService.validateSmsContractToken(selectedOffer.getId(), smsPinForm.getPin());

                            if (!validatePinJson.getBoolean(LoanOffer.SIGNATURE_PIN_VALIDATED_KEY)) {
                                return AjaxResponse.errorMessage("El pin no es válido");
                            } else if (!validatePinJson.getBoolean(LoanOffer.SIGNATURE_PIN_VALIDATED_KEY) && validatePinJson.has(LoanOffer.SIGNATURE_PIN_PIN_KEY)) {
                                User user = userDao.getUser(loanApplication.getUserId());
                                EntityBranding entityBranding = loanApplication.getEntityId() != null ? catalogService.getEntityBranding(loanApplication.getEntityId()) : null;
                                String entityShort = entityBranding != null && entityBranding.getBranded() ? entityBranding.getEntity().getShortName() : null;

                                sendContractPinInteraction(selectedOffer.getSignaturePin(), InteractionType.SMS, loanApplication, user, person, entityShort);

                                return AjaxResponse.errorMessage("El pin no es válido. Un nuevo pin ha sido enviado");
                            } else {
                                loanApplicationDao.updateLoanOfferSignaturePinValidation(loanApplication.getId(), validatePinJson.getBoolean(LoanOffer.SIGNATURE_PIN_VALIDATED_KEY));
                            }
                        }




                        throw new GoToNextQuestionException("DEFAULT", ProcessQuestionSequence.TYPE_FORWARD);
                    }
                    case "resend":
                    case "call": {
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                        LoanOffer selectedOffer = loanApplicationDao.getLoanOffers(loanApplication.getId()).stream().filter(LoanOffer::getSelected).findFirst().orElse(null);
                        User user = userDao.getUser(loanApplication.getUserId());
                        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
                        EntityBranding entityBranding = loanApplication.getEntityId() != null ? catalogService.getEntityBranding(loanApplication.getEntityId()) : null;
                        String entityShort = entityBranding != null && entityBranding.getBranded() ? entityBranding.getEntity().getShortName() : null;

                        sendContractPinInteraction(selectedOffer.getSignaturePin(), path.equalsIgnoreCase("resend") ? InteractionType.SMS : InteractionType.CALL, loanApplication, user, person, entityShort);

                        return AjaxResponse.ok(null);
                    }
                }
                break;
        }
        throw new Exception("No method configured");
    }

    private boolean isSignature2Ok(Person person, String signature) {
        String name = person.getName().trim().substring(0, 1).concat(". ").concat(person.getFirstSurname().trim());
        return signature.equals(name);
    }

    private boolean bankRequired(EntityProductParams entityProductParams, LoanOffer selectedOffer) {
        return !entityProductParams.getSelfDisbursement() && entityProductParams.getDisbursementType() == EntityProductParams.DISBURSEMENT_TYPE_DEPOSIT && selectedOffer.getEmployer() == null;
    }

    private boolean paymentAplicationTypeRequired(LoanOffer selectedOffer) {
        return selectedOffer.getEntity().getId() == Entity.COMPARTAMOS;
    }

    private boolean smsPinRequired(LoanOffer selectedOffer) {
        return selectedOffer.getEntityProductParam().getSignaturePin();
    }

    private void registerSignature(LoanApplication loanApplication, Person person, LoanOffer offerSelected, Question135BankForm loanApplicationBankForm, String loanApplicationSign, Locale locale, HttpServletRequest request, HttpServletResponse response, SpringTemplateEngine templateEngine) throws Exception {

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
        if (offerSelected.getEntity().getId() == Entity.MULTIFINANZAS) {
            creditService.createAndSaveContractApplication(loanApplication, offerSelected, request, response, locale, templateEngine);
        }

        // Register the signature
        loanApplicationDao.registerLoanApplicationSIgnature(
                offerSelected.getId(),
                loanApplicationSign != null ? loanApplicationSign : null,
                person.getDocumentType().getId(),
                person.getDocumentNumber());

        // TODO This should be removed. the procedure "RegisterLoanSignature" should not change the status of the loan applicatin
        if(loanApplication.getSelectedEntityProductParameterId() == null || !Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE, EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO).contains(loanApplication.getSelectedEntityProductParameterId())){
            loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.EVAL_APPROVED, null);
        }


    }

    private void sendContractPinInteraction(String pin, int interactionType, LoanApplication loanApplication, User user, Person person, String entityShortName) throws Exception {
        if (interactionType == InteractionType.SMS) {
            try {
                userService.sendContractToken(pin, user, loanApplication.getId(), loanApplication.getPersonId(), person.getName(), person.getCountry().getId(), entityShortName, InteractionType.SMS, InteractionProvider.INFOBIP);
            } catch (Exception e) {
                userService.sendContractToken(pin, user, loanApplication.getId(), loanApplication.getPersonId(), person.getName(), person.getCountry().getId(), entityShortName, InteractionType.SMS, InteractionProvider.AWS);
            }
        } else if (interactionType == InteractionType.CALL) {
            userService.sendContractToken(pin, user, loanApplication.getId(), loanApplication.getPersonId(), person.getName(), person.getCountry().getId(), entityShortName, InteractionType.CALL, null);
        }
    }



}

