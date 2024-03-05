package com.affirm.common.service.question;

import com.affirm.bancoazteca.model.BancoAztecaCampaniaApi;
import com.affirm.bancoazteca.model.RolConsejero;
import com.affirm.common.dao.*;
import com.affirm.common.model.CreateLoanApplicationRequest;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.JsonUtil;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.security.model.EntityWsResult;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service("question178Service")
public class Question178Service extends AbstractQuestionService<FormGeneric> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private LoanApplicationApprovalValidationService loanApplicationApprovalValidationService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private EvaluationDAO evaluationDAO;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private InteractionDAO interactionDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private PersonDAO personDAO;
    @Autowired
    private SecurityDAO securityDAO;
    @Autowired
    private ErrorService errorService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                String showCustomFooter = null;
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id,locale);
                Person person = personDAO.getPerson(loanApplication.getPersonId(),false,locale);
                boolean disablePresAlTokeButton = false;
                boolean showRedirectButton = false;
                String redirectLink = null;
                String message = null;
                String evaluationStatus = null;
                String textButton = null;
                String fbEventOnClick = null;
                List<RolConsejero.Diagnostico> diagnostics = new ArrayList<>();
                AztecaPreApprovedBase aztecaPreApprovedBase = null;
                EntityWsResult diagnosis = securityDAO.getEntityResultWS(id, EntityWebService.BANCO_AZTECA_OBTAIN_ADVISER_ROLE);
                if(diagnosis != null && diagnosis.getResult() != null){
                    RolConsejero rolConsejero = new Gson().fromJson(diagnosis.getResult().toString(), RolConsejero.class);
                    diagnostics = rolConsejero.getDiagnostico();
                    message = rolConsejero.getRecomendacion();
                    if(rolConsejero.getSolucionFinanciera() !=null && rolConsejero.getSolucionFinanciera().length() > 0){
                        textButton = rolConsejero.getSolucionFinanciera();
                        if(textButton.contains("OFERTA")){
                            textButton = textButton.replaceAll("OFERTA","");
                        }
                        if(textButton.contains("PrestAhorro") || textButton.contains("PrestAltoke")) textButton += "<br/> ¡La quiero!";
                        if(textButton.contains("PrestAhorro")) fbEventOnClick = "DiagnosticoAhorro";
                        if(textButton.contains("PrestAltoke")) fbEventOnClick = "DiagnosticoPrestamo";
                        if(rolConsejero.getSolucionFinanciera().toLowerCase().contains("ahorro meta")){
                            showRedirectButton = true;
                            redirectLink = "https://alfinbanco.pe/ahorrometa";
                            fbEventOnClick = "DiagnosticoMeta";
                        }
                    }
                }
                EntityWsResult campaign = securityDAO.getEntityResultWS(id, EntityWebService.BANCO_AZTECA_OBTAIN_PERSON_CAMPAIGNS);
                if(campaign != null && campaign.getResult() != null){
                    if(campaign != null && campaign.getResult() != null){
                        BancoAztecaCampaniaApi bancoAztecaCampaniaApiData = new Gson().fromJson(campaign.getResult().toString(),BancoAztecaCampaniaApi.class);
                        evaluationStatus = bancoAztecaCampaniaApiData.getCampo4();
                        aztecaPreApprovedBase = bancoAztecaCampaniaApiData.getAztecaPreApprovedBase();
                    }
                }

                String textSemaforo = null;
                Boolean disablePresMiPerfil = true;
                if(evaluationStatus != null && evaluationStatus.length() > 0){
                    textSemaforo = "¡Hola " + person.getFirstName() + "! Esta es tu posición dentro de nuestro semáforo crediticio.";
                    attributes.put("message", message);
                    disablePresMiPerfil = false;
                }else {
                    textSemaforo = "Estamos buscando cómo llegar a ti con alternativas digitales que te permitan seguir progresando. ¡Pronto tendremos noticias!";
                }


                if (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA) {
                    if(loanApplication.getProductCategoryId() != null && loanApplication.getProductCategoryId() == ProductCategory.CONSEJ0){
                        showCustomFooter = "#azteca-footer-rol-consejero";
                    }
                }
                attributes.put("showCustomFooter", "#azteca-footer-rol-consejero");

                attributes.put("showRedirectButton", showRedirectButton);
                attributes.put("redirectLink", redirectLink);
                attributes.put("textSemaforo", textSemaforo);
                attributes.put("textButton", textButton);
                attributes.put("disablePresAlTokeButton", disablePresAlTokeButton);
                attributes.put("disablePresMiPerfil", disablePresMiPerfil);
                attributes.put("diagnostics", diagnostics);
                attributes.put("evaluationStatus", evaluationStatus);
                attributes.put("person", person);
                attributes.put("fbEventOnClick", fbEventOnClick);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, FormGeneric form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, FormGeneric form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, FormGeneric form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
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

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                switch (path) {
                    case "prestAlToke":
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id,locale);
                        EntityWsResult campaign = securityDAO.getEntityResultWS(id, EntityWebService.BANCO_AZTECA_OBTAIN_PERSON_CAMPAIGNS);
                        AztecaPreApprovedBase aztecaPreApprovedBase = null;
                        if(campaign != null && campaign.getResult() != null){
                            if(campaign != null && campaign.getResult() != null){
                                BancoAztecaCampaniaApi bancoAztecaCampaniaApiData = new Gson().fromJson(campaign.getResult().toString(),BancoAztecaCampaniaApi.class);
                                aztecaPreApprovedBase = bancoAztecaCampaniaApiData.getAztecaPreApprovedBase();
                            }
                        }
                        if(aztecaPreApprovedBase == null || aztecaPreApprovedBase.getIdCampania() == null) return AjaxResponse.errorMessage("No puede realizar esta acción");
                        Person person = personDAO.getPerson(loanApplication.getPersonId(),false,locale);
                        User user = userDAO.getUser(loanApplication.getUserId());
                        CreateLoanApplicationRequest request = new CreateLoanApplicationRequest();
                        request.setAbTesting(LoanApplication.AB_TESTING_A.charAt(0));
                        request.setDocumentNumber(person.getDocumentNumber());
                        request.setDocumentType(person.getDocumentType().getId());
                        request.setEmail(user.getEmail());
                        request.setPhoneNumber(user.getPhoneNumber());
                        request.setEntityId(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
                        request.setProductCategoryId(ProductCategory.CONSUMO);
                        request.setReferenceLoanApplicationId(loanApplication.getId());
                        request.setSkipSMSPinValidationQuestion(true);
                        request.setOrigin(LoanApplication.ORIGIN_REFERENCED);
                        LoanApplication loanApplicationReferenced = loanApplicationService.createLoanApplication(request,(HttpServletRequest) params.get("request"));
                        if(loanApplication.getProductCategoryId() != null && loanApplication.getEntityId() != null && loanApplication.getEntityId().equals(Entity.AZTECA) && loanApplication.getEntityCustomData() != null && loanApplication.getEntityCustomData().has(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_BASE_PREAPROBADA.getKey())){
                            Entity entity = catalogService.getEntity(loanApplication.getEntityId());
                            boolean generateBotTConektaCall = false;
                            List<Entity.ClickToCallConfiguration> clickToCallConfiguration =  entity.getClickToCallConfiguration();
                            if(clickToCallConfiguration != null && !clickToCallConfiguration.isEmpty() && clickToCallConfiguration.stream().anyMatch(e -> e.getProductCategoryId() != null && e.getProductCategoryId().equals(loanApplication.getProductCategoryId()))){
                                List<Integer> idCampanias = clickToCallConfiguration.stream().filter(e -> e.getProductCategoryId() != null && e.getProductCategoryId().equals(loanApplication.getProductCategoryId())).findFirst().orElse(null).getIdCampaigns();
                                if(idCampanias != null && idCampanias.contains(aztecaPreApprovedBase.getIdCampania())) generateBotTConektaCall = true;
                                Boolean botAlreadyInitialized = JsonUtil.getBooleanFromJson(loanApplication.getEntityCustomData(),LoanApplication.EntityCustomDataKeys.TCONEKTA_INFORMATION_BOT_INITIALIZED.getKey(), null);
                                if(botAlreadyInitialized != null && botAlreadyInitialized) generateBotTConektaCall = false;
                                if(generateBotTConektaCall){
                                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.TCONEKTA_INFORMATION_BOT_INITIALIZED.getKey(), true);
                                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.TCONEKTA_INFORMATION_SENT.getKey(), true);
                                    loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                                }
                            }
                        }
                        return AjaxResponse.redirect(loanApplicationService.generateLoanApplicationLinkEntity(loanApplicationReferenced));
                }
                break;
        }
        throw new Exception("No method configured");
    }
}

