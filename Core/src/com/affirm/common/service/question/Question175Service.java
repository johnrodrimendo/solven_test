package com.affirm.common.service.question;

import com.affirm.common.dao.*;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.*;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

@Service("question175Service")
public class Question175Service extends AbstractQuestionService<FormGeneric> {

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
    private Question11Service question11Service;
    @Autowired
    private EvaluationService evaluationService;
    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id,locale);
                EntityProductParams entityProductParams = loanApplicationService.getEntityProductParamFromLoanApplication(loanApplication);
                String finalMessage = "";
                Integer loanStatus = null;
                boolean showAgencies = false;
                boolean runVerifyStatus = false;
                Boolean loanHasAllValidations = loanApplicationApprovalValidationService.loanHasAllValidations(loanApplication,entityProductParams);

                Double defaultLatitude = -12.046374;
                Double defaultLongitude = -77.042793;
                attributes.put("navLatitude", defaultLatitude);
                attributes.put("navLongitude", defaultLongitude);

                if((loanApplication.getCredit() == null || !loanApplication.getCredit()) && !Arrays.asList(LoanApplicationStatus.REJECTED_AUTOMATIC,LoanApplicationStatus.REJECTED,LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION).contains(loanApplication.getStatus().getId()) && !loanApplication.getStatus().getId().equals(LoanApplicationStatus.WAITING_APPROVAL)){
                    //if(Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE,EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO).contains(loanApplication.getSelectedEntityProductParameterId())) loanApplicationDao.updateLoanApplicationStatus(id,LoanApplicationStatus.WAITING_APPROVAL,null);
                }

 /*             ProcessQuestion currentQuestion = evaluationService.getQuestionFromEvaluationProcess(loanApplication, null, ProcessQuestion.Question.Constants.AZTECA_FINAL_MESSAGE, null, null);
                if(currentQuestion.getResults() != null && currentQuestion.getResults().has("REJECTED") && Arrays.asList(LoanApplicationStatus.REJECTED_AUTOMATIC,LoanApplicationStatus.REJECTED,LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION).contains(loanApplication.getStatus().getId())){
                    throw new GoToNextQuestionException("REJECTED", ProcessQuestionSequence.TYPE_FORWARD);
                }*/

                if(loanApplication.getApprovalValidations() == null || loanApplication.getApprovalValidations().stream().filter(a-> a.getApprovalValidationId() == ApprovalValidation.LIMITE_DE_MONTO).findFirst().orElse(null) == null){
                    loanApplicationApprovalValidationService.validateAndUpdate(loanApplication.getId(), ApprovalValidation.LIMITE_DE_MONTO);
                    loanApplication = loanApplicationDao.getLoanApplication(id,locale);
                }

                boolean ipLocationIsValid = this.loanApplicationService.validateLoanApplicationByIPGeolocation(loanApplication, entityProductParams, locale);
                if ((loanHasAllValidations != null && !loanHasAllValidations) || !ipLocationIsValid) {

                    List<ApprovalValidation> approvalValidations = loanApplicationApprovalValidationService.getApprovalValidationIds(loanApplication, entityProductParams);

                    if(loanApplicationApprovalValidationService.loanHasAnyRejectedValidations(loanApplication,entityProductParams) || !ipLocationIsValid){
                        int defaultRejectionReason = ApplicationRejectionReason.VALIDACIONES_FALLIDAS;
                        if(!ipLocationIsValid) defaultRejectionReason = ApplicationRejectionReason.GEOLOCALIZACION_IP;
                        loanStatus = null;
                        finalMessage = "Estas a un paso de obtener tu crédito, debes acercarte a la agencia más cercana para continuar con el proceso.";
                        if(Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_IDENTIDAD).contains(loanApplication.getSelectedEntityProductParameterId())) finalMessage = "Por favor acércate a una de nuestras agencias para finalizar el proceso.";
                        if(loanApplication.getProductCategoryId() ==  ProductCategory.CUENTA_BANCARIA) finalMessage = "Lo sentimos por el momento no podemos continuar con tu solicitud por este medio. <br/> Por favor acércate a tu agencia más cercana y continúa con el proceso.";
                        showAgencies = true;
                        if(!Arrays.asList(LoanApplicationStatus.REJECTED_AUTOMATIC,LoanApplicationStatus.REJECTED,LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION).contains(loanApplication.getStatus().getId())){
                            loanApplicationDao.registerRejectionWithComment(id, loanApplication.getRejectionReason() != null && loanApplication.getRejectionReason().getId() != null ? loanApplication.getRejectionReason().getId() : defaultRejectionReason, null);
                        }
                        if(EntityProductParams.ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD.contains(loanApplication.getSelectedEntityProductParameterId())){
                            throw new GoToNextQuestionException("REJECTED", ProcessQuestionSequence.TYPE_FORWARD);
                        }
                    }
                    else if (loanApplicationApprovalValidationService.loanHasAnyValidationsForManualApproval(loanApplication,entityProductParams)) {
                        Person person = personDAO.getPerson(loanApplication.getPersonId(),false,locale);
                        loanStatus = null;
                        if(loanApplicationApprovalValidationService.loanHasAnyValidationsWithCustomStatus(loanApplication, LoanApplicationApprovalValidation.CUSTOM_STATUS_MANUAL_REVISION_WAITING_APPROVAL, entityProductParams)){
                            finalMessage = "Estamos validando tu información. Espera un momento.";
                        }
                        else finalMessage = String.format("Excelente %s, estamos revisando tu solicitud, te enviaremos un correo para que puedas continuar el proceso",person.getFirstName());
                        runVerifyStatus = true;
                        if(!loanApplication.getStatus().getId().equals(LoanApplicationStatus.WAITING_APPROVAL) && loanApplicationApprovalValidationService.loanHasAnyValidationsWithCustomStatus(loanApplication, LoanApplicationApprovalValidation.CUSTOM_STATUS_MANUAL_REVISION, entityProductParams)) {
                            if(Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE,EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO).contains(entityProductParams.getId())) loanApplicationDao.updateLoanApplicationStatus(id,LoanApplicationStatus.WAITING_APPROVAL,null);
                        }
                        if(EntityProductParams.ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD.contains(loanApplication.getSelectedEntityProductParameterId())){
                            throw new GoToNextQuestionException("DEFAULT", ProcessQuestionSequence.TYPE_FORWARD);
                        }
                    }
                    else{
                        loanStatus = null;
                        finalMessage = "Estamos validando tu información. Espera un momento.";
                        attributes.put("showLoading", true);
                        runVerifyStatus = true;
                    }
                }
                else{
                    loanStatus = null;
                    finalMessage = "Estamos validando tu documentación. Espera un momento";
                    attributes.put("showLoading", true);
                    runVerifyStatus = true;
                }
                attributes.put("runVerifyStatus",runVerifyStatus);
                attributes.put("showAgencies", showAgencies);
                attributes.put("loanStatus", loanStatus);
                attributes.put("finalMessage", finalMessage);
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
                    case "verify_status":
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        EntityProductParams entityProductParams = loanApplicationService.getEntityProductParamFromLoanApplication(loanApplication);
                        boolean ipLocationIsValid = this.loanApplicationService.validateLoanApplicationByIPGeolocation(loanApplication, entityProductParams, locale);
                        Integer loanStatus = null;
                        if (!loanApplicationApprovalValidationService.loanHasAllValidations(loanApplication,entityProductParams) || !ipLocationIsValid) {
                            int defaultRejectionReason = ApplicationRejectionReason.VALIDACIONES_FALLIDAS;
                            if(!ipLocationIsValid) defaultRejectionReason = ApplicationRejectionReason.GEOLOCALIZACION_IP;
                            if(loanApplicationApprovalValidationService.loanHasAnyRejectedValidations(loanApplication, entityProductParams) || !ipLocationIsValid){
                                loanStatus = null;
                                if(!Arrays.asList(LoanApplicationStatus.REJECTED_AUTOMATIC,LoanApplicationStatus.REJECTED,LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION).contains(loanApplication.getStatus().getId())){
                                    loanApplicationDao.registerRejectionWithComment(id, loanApplication.getRejectionReason() != null && loanApplication.getRejectionReason().getId() != null ? loanApplication.getRejectionReason().getId() : defaultRejectionReason, null);
                                }
                                if(EntityProductParams.ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD.contains(loanApplication.getSelectedEntityProductParameterId())){
                                    throw new GoToNextQuestionException("REJECTED", ProcessQuestionSequence.TYPE_FORWARD);
                                }
                            }
                            else if (loanApplicationApprovalValidationService.loanHasAnyValidationsForManualApproval(loanApplication,entityProductParams)) {
                                loanStatus = null;
                                if(Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE,EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO).contains(loanApplication.getSelectedEntityProductParameterId())){
                                    if(loanApplicationApprovalValidationService.loanHasAnyValidationsWithCustomStatus(loanApplication, LoanApplicationApprovalValidation.CUSTOM_STATUS_MANUAL_REVISION_WAITING_APPROVAL, entityProductParams)) {
                                        if(loanApplication.getStatus().getId().equals(LoanApplicationStatus.WAITING_APPROVAL)) loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.EVAL_APPROVED, null);
                                        throw new GoToNextQuestionException("DEFAULT", ProcessQuestionSequence.TYPE_FORWARD);
                                    }
                                    else if(!loanApplication.getStatus().getId().equals(LoanApplicationStatus.WAITING_APPROVAL) && loanApplicationApprovalValidationService.loanHasAnyValidationsWithCustomStatus(loanApplication, LoanApplicationApprovalValidation.CUSTOM_STATUS_MANUAL_REVISION, entityProductParams)) {
                                        if(Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE,EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO).contains(entityProductParams.getId())) loanApplicationDao.updateLoanApplicationStatus(id,LoanApplicationStatus.WAITING_APPROVAL,null);
                                    }
                                }
                                if(EntityProductParams.ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD.contains(loanApplication.getSelectedEntityProductParameterId())) {
                                    throw new GoToNextQuestionException("DEFAULT", ProcessQuestionSequence.TYPE_FORWARD);
                                }
                            }
                            else if(loanApplicationApprovalValidationService.loanHasAnyFailedValidations(loanApplication,entityProductParams)){
                                loanStatus = null;
                            }
                            else{
                                loanStatus = null;
                            }
                        }
                        else{
                            loanStatus = null;
                            if(Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE,EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO).contains(loanApplication.getSelectedEntityProductParameterId())){
                                if(loanApplication.getStatus().getId().equals(LoanApplicationStatus.WAITING_APPROVAL)) loanApplicationDao.updateLoanApplicationStatus(id,LoanApplicationStatus.EVAL_APPROVED,null);
                                throw new GoToNextQuestionException("DEFAULT", ProcessQuestionSequence.TYPE_FORWARD);
                            }
                            if(loanApplication.getCredit() == null || !loanApplication.getCredit()) {
                                //if(Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE,EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO).contains(loanApplication.getSelectedEntityProductParameterId())) loanApplicationService.approveLoanApplication(id,null,(HttpServletRequest) params.get("request"),(HttpServletResponse) params.get("response"),(SpringTemplateEngine) params.get("templateEngine"),locale);
                            }
                            throw new GoToNextQuestionException("DEFAULT", ProcessQuestionSequence.TYPE_FORWARD);
                        }
                        return AjaxResponse.ok(loanStatus != null ? loanStatus.toString() : null);
                }
                break;
        }
        throw new Exception("No method configured");
    }


}

