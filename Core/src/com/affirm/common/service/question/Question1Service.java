package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.SelfEvaluationDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.Question1Form;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.*;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service("question1Service")
public class Question1Service extends AbstractQuestionService<Question1Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private SelfEvaluationDAO selfEvaluationDao;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private CountryContextService countryContextService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private BrandingService brandingService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserService userService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private SelfEvaluationService selfEvaluationService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private WebscrapperService webscrapperService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

//        HttpServletRequest request = (HttpServletRequest) params.get("request");
//        Integer agent = (Integer) params.get("agent");
//        String categoryUrl = (String) params.get("categoryUrl");
//        Integer countryId = countryContextService.getCountryParamsByRequest(request).getId();

        Map<String, Object> attributes = new HashMap<>();
        Question1Form form = new Question1Form();

        ProcessQuestion currentQuestion = null;
        switch (flowType) {
            case LOANAPPLICATION: {
                LoanApplication loanApplication = null;
                if (id != null) {
                    loanApplication = loanApplicationDao.getLoanApplication(id, locale);

                    if(fillSavedData){
                        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
                        User user = userDao.getUser(loanApplication.getUserId());
                        form.setDocNumber(person.getDocumentNumber());
                        form.setDocType(person.getDocumentType().getId());
                        form.setEmail(user.getEmail());
                        form.setName(person.getName());
                        form.setSurName(person.getFirstSurname());
                        form.setPep(person.getPep());
                    }
                }

                HttpServletRequest request = loanApplication != null ? null : (HttpServletRequest) params.get("request");
                Integer agent = loanApplication != null ? loanApplication.getAgent().getId() : (Integer) params.get("agent");
                Integer categoryId = loanApplication != null ? loanApplication.getProductCategory().getId() : ProductCategory.GET_ID_BY_URL((String) params.get("categoryUrl"));
                Integer countryId = loanApplication != null ? loanApplication.getCountryId() : countryContextService.getCountryParamsByRequest(request).getId();

                ((Question1Form.Validator) form.getValidator()).configValidator(countryId);

                attributes.put("agent", catalogService.getAgent(agent));

                currentQuestion = evaluationService.getQuestionFromEvaluationProcess(
                        loanApplication,
                        categoryId,
                        1,
                        countryId,
                        request);

                attributes.put("loanApplication", loanApplication);
                attributes.put("isSelfEvaluation", false);
                attributes.put("isEvaluation", true);
                if (loanApplication != null)
                    attributes.put("identityDocumentTypes", getValidIdentityDocumentTypes(loanApplication));
                else
                    attributes.put("identityDocumentTypes", getValidIdentityDocumentTypes(request));
                break;
            }
            case SELFEVALUATION: {
                SelfEvaluation selfEvaluation = null;
                if (id != null) {
                    selfEvaluation = selfEvaluationDao.getSelfEvaluation(id, locale);
                }

                HttpServletRequest request = selfEvaluation != null ? null : (HttpServletRequest) params.get("request");
                Integer agent = selfEvaluation != null ? selfEvaluation.getAgent().getId() : (Integer) params.get("agent");
                Integer countryId = selfEvaluation != null ? selfEvaluation.getCountryParam().getId() : countryContextService.getCountryParamsByRequest(request).getId();

                ((Question1Form.Validator) form.getValidator()).configValidator(countryId);

                attributes.put("agent", catalogService.getAgent(agent));

                attributes.put("isSelfEvaluation", true);
                attributes.put("isEvaluation", false);
                attributes.put("identityDocumentTypes", getValidIdentityDocumentTypes(request));
                break;
            }
        }

        if (currentQuestion != null && currentQuestion.getConfiguration() != null) {
            attributes.put("showPep", JsonUtil.getBooleanFromJson(currentQuestion.getConfiguration(), "showPep", true));
        } else
            attributes.put("showPep", true);
        attributes.put("form", form);

        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question1Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplicationLite(id, Configuration.getDefaultLocale());
                Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);

                // peruvian does not have first name when not found on reniec, so we'll ask for name
                if (loanApplication.getCountryId().equals(CountryParam.COUNTRY_PERU) && person.getFirstName().equals("")) {
                    return "NO_RENIEC";
                }

                return form.getDocType().equals(IdentityDocumentType.CE) ? "CE" : "DEFAULT";
            case SELFEVALUATION:
                return form.getDocType().equals(IdentityDocumentType.CE) ? "CE" : "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question1Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
            case SELFEVALUATION:
                ((Question1Form.Validator) form.getValidator()).configValidator(form.getCountryId());
                if (form.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
                    form.setDocType(IdentityDocumentType.CDI);
                }

                form.getValidator().validate(locale);
                if (!form.getValidator().isHasErrors()) {
                    if (ArrayUtils.contains(new int[]{IdentityDocumentType.CDI, IdentityDocumentType.CUIL, IdentityDocumentType.CUIT}, form.getDocType())
                            && (form.getDocNumber().startsWith("30") || form.getDocNumber().startsWith("33") || form.getDocNumber().startsWith("34"))) {
                        ((Question1Form.Validator) form.getValidator()).docNumber.setError("El CUIT ingresado corresponde a una empresa. Por el momento solo operamos con personas físicas. Gracias por visitarnos.");
                        return;
                    }

                    if (ArrayUtils.contains(new int[]{IdentityDocumentType.CDI, IdentityDocumentType.CUIL, IdentityDocumentType.CUIT}, form.getDocType())) {
                        if (!utilService.validarCuit(form.getDocNumber())) {
                            ((Question1Form.Validator) form.getValidator()).docNumber.setError(messageSource.getMessage("static.message.cuit", null, locale));
                            return;
                        }
                    }

                    // Validate that the document type is valid
                    if (getValidIdentityDocumentTypes(form.getRequest()).stream().noneMatch(d -> d.getId().equals(form.getDocType()))) {
                        throw new FormValidationException("El tipo de documento es inválido");
                    }

                    if (form.getAgentId() == null)
                        throw new Exception("There is no Agent in the request");

                    if (form.getCategoryUrl() == null)
                        throw new Exception("There is no Category in the request");

                }
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question1Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION: {
                User user = userService.getOrRegisterUser(form.getDocType(), form.getDocNumber(), null, form.getName() == null ? null : form.getName().trim(), form.getSurName() == null ? null : form.getSurName().trim());

                LoanApplication loanApplication;
                if (id != null) {
                    loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                } else {
                    EntityBranding entityBranding = brandingService.getEntityBranding(form.getRequest());

                    // Insert the email or respond that the email is invalid
                    createEmailPassword(form.getEmail(), user.getId());

                    loanApplication = loanApplicationDao.getActiveLoanApplicationByPerson(locale, user.getPersonId(), ProductCategory.GET_ID_BY_URL(form.getCategoryUrl()));
                    boolean continueQuestionProcess = true;

                    if (loanApplication != null) {
                        if (loanApplication.getEntityId() == null) { // Last LA was marketplace
                            if (entityBranding != null) { // Now the user is on a BRANDED site
                                if (loanApplication.getStatus().getId() < LoanApplicationStatus.WAITING_APPROVAL) {
                                    // Expire the LA and continue with as a new application for BRANDED
                                    loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.EXPIRED, null);
                                    continueQuestionProcess = false;
                                }
                            }
                        } else { // Last LA was BRANDED
                            if (entityBranding != null) {
                                if (!entityBranding.getEntity().getId().equals(loanApplication.getEntityId())) { // Now the user is on a another BRANDED site
                                    if (loanApplication.getStatus().getId() < LoanApplicationStatus.WAITING_APPROVAL) {
                                        // Expire the LA and continue with as a new application for BRANDED
                                        loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.EXPIRED, null);
                                        continueQuestionProcess = false;
                                    }
                                }
                            } else { // Now the user is on marketplace
                                // Redirect to LA BRANDED url
                                throw new ResponseEntityException(AjaxResponse.redirect(loanApplicationService.generateLoanApplicationLinkEntity(loanApplication)));
                            }
                        }

                        if (continueQuestionProcess) {
                            // If it comes from the comparator, force the entity and product
                            if (loanApplication.getStatus().getId() == LoanApplicationStatus.NEW || loanApplication.getStatus().getId() == LoanApplicationStatus.PRE_EVAL_APPROVED) {
                                applyExternalParams(form.getExternalParams(), loanApplication.getId());
                            } else if (loanApplication.getStatus().getId() == LoanApplicationStatus.EVAL_APPROVED) {
                                // If the evaluation is approved, only update the entity and product if it doesnt have an offer selected
                                List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
                                if (offers == null || offers.stream().noneMatch(o -> o.getSelected() != null && o.getSelected())) {
                                    applyExternalParams(form.getExternalParams(), loanApplication.getId());
                                }
                            }

                            throw new ResponseEntityException(AjaxResponse.redirect(form.getRequest().getContextPath() + "/" +
                                    ProductCategory.GET_URL_BY_ID(loanApplication.getProductCategoryId()) + "/" +
                                    Configuration.EVALUATION_CONTROLLER_URL + "/" +
                                    evaluationService.generateEvaluationToken(loanApplication.getUserId(), loanApplication.getPersonId(), loanApplication.getId()) + "?showWelcome=true"));
                        }
                    }

                    loanApplication = loanApplicationDao.registerLoanApplication(
                            user.getId(),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            LoanApplication.ORIGIN_WEB,
                            null,
                            null,
                            null,
                            form.getCountryId());
                    userService.registerIpUbication(Util.getClientIpAddres(form.getRequest()), loanApplication.getId());
                    loanApplicationDao.updateProductCategory(loanApplication.getId(), ProductCategory.GET_ID_BY_URL(form.getCategoryUrl()));
                    loanApplicationDao.updateFormAssistant(loanApplication.getId(), form.getAgentId());

                    loanApplicationDao.updateSourceMediumCampaign(loanApplication.getId(), form.getSource(), form.getMedium(), form.getCampaign());
                    loanApplicationDao.updateTermContent(loanApplication.getId(), form.getTerm(), form.getContent());
                    loanApplicationDao.updateGoogleClickId(loanApplication.getId(), form.getGclid());
                    loanApplicationDao.updateGAClientID(loanApplication.getId(), form.getGaClientID());
                    loanApplicationDao.updateUserAgent(loanApplication.getId(), form.getRequest().getHeader("User-Agent"));

                    // If it has an entity subdomain, set the forced entity
                    if (entityBranding != null)
                        loanApplicationDao.updateEntityId(loanApplication.getId(), entityBranding.getEntity().getId());

                    // If it comes from the comparator, force the entity and product
                    applyExternalParams(form.getExternalParams(), loanApplication.getId());

                    loanApplication = loanApplicationDao.getLoanApplication(loanApplication.getId(), locale);
                    evaluationService.forwardByResult(loanApplication, null, form.getRequest());
                    evaluationService.forwardByResult(loanApplication, "DEFAULT", form.getRequest());
                }

                loanApplicationDao.updatePerson(loanApplication.getId(), user.getPersonId(), user.getId());

                // Only if Argentina, start running the evaluation, so the bots doesnt take too long
                if (form.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
                    loanApplicationService.runEvaluationBot(loanApplication.getId(), false);
                } else if(form.getCountryId() == CountryParam.COUNTRY_PERU){
                    // IF Peru, start creating the synthesized so doesnt take too long
                    Person person = personDao.getPerson(catalogService, locale, user.getPersonId(), false);
                    webscrapperService.callRunSynthesized(person.getDocumentNumber(), loanApplication.getId());
                }

                throw new QuestionFlowChangeIdentifierException(loanApplication.getId());
            }
            case SELFEVALUATION: {
                User user = userService.getOrRegisterUser(form.getDocType(), form.getDocNumber(), null, form.getName() != null ? form.getName().trim() : null, form.getSurName() !=  null ? form.getSurName().trim() : null);

                SelfEvaluation selfEvaluation;
                if (id != null) {
                    selfEvaluation = selfEvaluationDao.getSelfEvaluation(id, locale);
                } else {
                    // Insert the email or respond that the email is invalid
                    createEmailPassword(form.getEmail(), user.getId());

                    selfEvaluation = selfEvaluationDao.getActiveSelfEvaluationByPerson(user.getPersonId(), locale);
                    if (selfEvaluation != null) {
                        throw new ResponseEntityException(AjaxResponse.redirect(form.getRequest().getContextPath() + "/" +
                                Configuration.SELF_EVALUATION_CONTROLLER_URL + "/" +
                                selfEvaluationService.generateSelfEvaluationToken(selfEvaluation.getId()) + "?showWelcome=true"));
                    }

                    selfEvaluation = selfEvaluationDao.registerSelfEvaluation(form.getCountryId());
                    selfEvaluationDao.updateFormAssistant(selfEvaluation.getId(), form.getAgentId());
                    selfEvaluationDao.updatePerson(selfEvaluation.getId(), user.getPersonId());

                    // Only if Argentina, run the bots
                    if (form.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
                        selfEvaluation = selfEvaluationDao.getSelfEvaluation(selfEvaluation.getId(), locale);
                        selfEvaluationService.runSelfEvaluationArgentina(selfEvaluation);
                    }

                    selfEvaluationService.forwardByResult(selfEvaluation, null, form.getRequest());

                    throw new QuestionFlowChangeIdentifierException(selfEvaluation.getId());
                }
            }
            break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
        return null;
    }

    private List<IdentityDocumentType> getValidIdentityDocumentTypes(HttpServletRequest request) throws Exception {
        if (brandingService.isBranded(request)) {
            EntityBranding branding = brandingService.getEntityBranding(request);
            if (branding.getEntity().getDocumentTypes() != null && !branding.getEntity().getDocumentTypes().isEmpty())
                return brandingService.getEntityBranding(request).getEntity().getDocumentTypes();
        }

        return catalogService.getIdentityDocumentTypeByCountry(countryContextService.getCountryParamsByRequest(request).getId(), countryContextService.isCountryContextInArgentina(request));
    }

    private List<IdentityDocumentType> getValidIdentityDocumentTypes(LoanApplication loanApplication) throws Exception {
        if (loanApplication.getEntityId() != null) {
            EntityBranding entityBranding = catalogService.getEntityBranding(loanApplication.getEntityId());
            if (entityBranding != null && entityBranding.getBranded()) {
                if (entityBranding.getEntity().getDocumentTypes() != null && !entityBranding.getEntity().getDocumentTypes().isEmpty())
                    return entityBranding.getEntity().getDocumentTypes();
            }
        }

        return catalogService.getIdentityDocumentTypeByCountry(loanApplication.getCountryId(), loanApplication.getCountryId() == CountryParam.COUNTRY_ARGENTINA);
    }

    private void createEmailPassword(String email, Integer userId) throws Exception {
        User user = userDao.getUser(userId);
        if (user.getEmail() != null && !user.getEmail().equalsIgnoreCase(email)) {
            throw new SqlErrorMessageException(null, "El  email no coincide con el registrado");
        } else if (user.getEmail() == null) {
            int emailId = userDao.registerEmailChange(userId, email.toLowerCase());
            userDao.validateEmailChange(userId, emailId);
        }
    }

    private void applyExternalParams(String externalParams, int loanApplicationId) throws Exception {
        if (externalParams != null && !externalParams.trim().isEmpty()) {
            JSONObject jsonExternalParams = new JSONObject(CryptoUtil.decrypt(externalParams));
            if (JsonUtil.getIntFromJson(jsonExternalParams, "forcedEntity", null) != null &&
                    JsonUtil.getIntFromJson(jsonExternalParams, "forcedProduct", null) != null) {
                loanApplicationDao.updateEntityId(loanApplicationId, JsonUtil.getIntFromJson(jsonExternalParams, "forcedEntity", null));
                loanApplicationDao.updateProductId(loanApplicationId, JsonUtil.getIntFromJson(jsonExternalParams, "forcedProduct", null));
            }else if(JsonUtil.getIntFromJson(jsonExternalParams, "forcedProduct", null) != null){
                loanApplicationDao.updateProductId(loanApplicationId, JsonUtil.getIntFromJson(jsonExternalParams, "forcedProduct", null));
            }
            if (JsonUtil.getIntFromJson(jsonExternalParams, "vehicleId", null) != null) {
                loanApplicationService.updateVehicle(loanApplicationId, JsonUtil.getIntFromJson(jsonExternalParams, "vehicleId", null));
            }
        }
    }

}

