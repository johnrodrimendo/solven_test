/**
 *
 */
package com.affirm.common.service.impl;


import com.affirm.aws.RecognitionResultsPainter;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.RekognitionProData;
import com.affirm.common.model.RekognitionReniecData;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationApprovalValidationService;
import com.affirm.common.service.question.Question174Service;
import com.affirm.common.util.JsonUtil;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */
@Service("loanApplicationApprovalValidationService")
public class LoanApplicationApprovalValidationServiceImpl implements LoanApplicationApprovalValidationService {

    private static final Logger logger = Logger.getLogger(LoanApplicationApprovalValidationServiceImpl.class);

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private SecurityDAO securityDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UserDAO userDAO;

    @Override
    public boolean validateAndUpdate(int loanApplicationId, int approvalValidationId) throws Exception {
        return validateAndUpdate(loanApplicationId, approvalValidationId, null);
    }

    @Override
    public boolean validateAndUpdate(int loanApplicationId, int approvalValidationId, Boolean forcedValidated) throws Exception {
        return validateAndUpdate(loanApplicationId, approvalValidationId, forcedValidated, null);
    }

    @Override
    public boolean validateAndUpdate(int loanApplicationId, int approvalValidationId, Boolean forcedValidated, Integer forcedRejectionReasonId) throws Exception {
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
        Triple<Boolean, String, Character> result;
        if(forcedValidated != null){
            result = Triple.of(forcedValidated, null, null);
        }else{
            result = isValidated(loanApplication, approvalValidationId);
        }
        updateLoanValidation(loanApplication, approvalValidationId, result.getLeft(), result.getMiddle(),result.getRight());
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        if(entityId != null && Arrays.asList(Entity.AZTECA).contains(entityId)){
            loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
            EntityProductParams entityProductParams = null;
            if(loanApplication.getSelectedEntityProductParameterId() == null){
                if(loanApplication.getProductCategoryId() == ProductCategory.VALIDACION_IDENTIDAD){
                    List<LoanApplicationPreliminaryEvaluation> preevals = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), Configuration.getDefaultLocale());
                    Integer entityProductParamId = preevals.stream().filter(p -> p.getStatus() == 'S' && p.getApproved() != null && p.getApproved()).map(p -> p.getEntityProductParameterId()).findFirst().orElse(null);
                    if(entityProductParamId != null)
                        entityProductParams = catalogService.getEntityProductParamById(entityProductParamId);
                }
            }
            boolean hasAnyRejection = loanHasAnyRejectedValidations(loanApplication, entityProductParams);
            if(hasAnyRejection) {
                if(loanApplication.getRejectionReason() == null)
                    loanApplicationDao.registerRejectionWithComment(loanApplicationId, forcedRejectionReasonId != null ? forcedRejectionReasonId : (loanApplication.getRejectionReason() != null && loanApplication.getRejectionReason().getId() != null ? loanApplication.getRejectionReason().getId() : ApplicationRejectionReason.VALIDACIONES_FALLIDAS), null);
            }
        }
        return result.getLeft();
    }

    private void updateLoanValidation(LoanApplication loanApplication, int approvalValidationId, boolean approved, String message, Character status) throws Exception {
        loanApplication.getApprovalValidations().removeIf(a -> a.getApprovalValidationId().equals(approvalValidationId));
        loanApplication.getApprovalValidations().add(new LoanApplicationApprovalValidation(approvalValidationId, new Date(), approved, message, status));
        loanApplicationDao.updateApprovalValidations(loanApplication.getId(), loanApplication.getApprovalValidations());
    }

    private Triple<Boolean, String, Character> isValidated(LoanApplication loanApplication, int approvalValidationId) throws Exception {
        switch (approvalValidationId) {
            case ApprovalValidation.VERIF_TELEFONICA: {
                List<LoanApplicationTrackingAction> actions = loanApplicationDao.getLoanApplicationTrackingActionsByTrackingId(loanApplication.getId(), new Integer[]{TrackingAction.PHONE_VERIFICATION_CONTACTED, TrackingAction.PHONE_VERIFICATION_NO_RESPONSE, TrackingAction.PHONE_VERIFICATION_WRONG_NUMBER, TrackingAction.TRACKING_PHONE_CALL}, null);
                if (actions != null && !actions.isEmpty()) {
                    LoanApplicationTrackingAction loanApplicationTrackingActionFirst = actions.stream().filter(e -> e.getTrackingAction().getTrackingActionId() != TrackingAction.TRACKING_PHONE_CALL).findFirst().orElse(null);
                    if (loanApplicationTrackingActionFirst != null && loanApplicationTrackingActionFirst.getTrackingAction().getTrackingActionId() == TrackingAction.PHONE_VERIFICATION_CONTACTED) {
                        return Triple.of(true, null, null);
                    }
                }
                return Triple.of(false, null, null);
            }
            case ApprovalValidation.VERIF_DOMICILIARIA: {
                List<LoanApplicationTrackingAction> actions = loanApplicationDao.getLoanApplicationTrackingActionsByTrackingId(loanApplication.getId(), new Integer[]{TrackingAction.ADDRESS_VERIFICATION_CONTACTED, TrackingAction.ADDRESS_VERIFICATION_NO_RESPONSE, TrackingAction.ADDRESS_VERIFICATION_WRONG_NUMBER}, null);
                if (actions != null && !actions.isEmpty()) {
                    LoanApplicationTrackingAction loanApplicationTrackingActionFirst = actions.stream().filter(e -> e.getTrackingAction().getTrackingActionId() != TrackingAction.TRACKING_PHONE_CALL).findFirst().orElse(null);
                    if (loanApplicationTrackingActionFirst != null && loanApplicationTrackingActionFirst.getTrackingAction().getTrackingActionId() == TrackingAction.ADDRESS_VERIFICATION_CONTACTED) {
                        return Triple.of(true, null, null);
                    }
                }
                return Triple.of(false, null, null);
            }
            case ApprovalValidation.ALERTAS_FRAUDE: {
                List<LoanApplicationFraudAlert> loanApplicationFraudAlerts = creditDao.getLoanApplicationFraudAlerts(loanApplication.getId(), FraudAlertStatus.NUEVO);
                if (loanApplicationFraudAlerts != null && loanApplicationFraudAlerts.stream().filter(a -> a.getActive() != null && a.getActive()).count() > 0) {
                    return Triple.of(false, null, null);
                }
                return Triple.of(true, null, null);
            }
            case ApprovalValidation.IDENTIDAD: {
                RekognitionReniecResult rekognitionReniecResult = securityDao.getRekognitionReniecResult(loanApplication.getId());
                if(rekognitionReniecResult != null && rekognitionReniecResult.getStatus() != null && rekognitionReniecResult.getStatus() == RekognitionReniecResult.SUCCESS_STATUS){
                    if(rekognitionReniecResult.getResponse() == null)  return Triple.of(false, null, LoanApplicationApprovalValidation.CUSTOM_STATUS_MANUAL_REVISION);
                    if(loanApplication.getProductCategoryId() == ProductCategory.VALIDACION_IDENTIDAD){
                        switch (rekognitionReniecResult.getResponse().getStatus()){
                            case RekognitionReniecData.APPROVED_STATUS:
                                if(rekognitionReniecResult.getResponse().getSelfieReniecFaceMatching() != null && (rekognitionReniecResult.getResponse().getSelfieReniecFaceMatching() < RekognitionReniecData.MINIMUM_VALUE_FOR_SELFIE_RENIEC || rekognitionReniecResult.getResponse().getSelfieReniecFaceMatching() >= 100.0)) return Triple.of(false, null, null);
                                else if(rekognitionReniecResult.getResponse().getMinimunFaceMatchingValue() == null || rekognitionReniecResult.getResponse().getMinimunFaceMatchingValue() < 80.0 || rekognitionReniecResult.getResponse().getMinimunFaceMatchingValue() >= 100.0) return Triple.of(false, null, null);
                                else return Triple.of(true, null, null);
                            default:
                                return Triple.of(false, null, null);
                        }
                    }
                    else {
                        switch (rekognitionReniecResult.getResponse().getStatus()){
                            case RekognitionReniecData
                                    .REVIEW_NEEDED_STATUS:
                                if(rekognitionReniecResult.getResponse().getSelfieReniecFaceMatching() != null && (rekognitionReniecResult.getResponse().getSelfieReniecFaceMatching() < RekognitionReniecData.MINIMUM_VALUE_FOR_SELFIE_RENIEC || rekognitionReniecResult.getResponse().getSelfieReniecFaceMatching() >= 100.0)) return Triple.of(false, null, null);
                                return Triple.of(false, null,  LoanApplicationApprovalValidation.CUSTOM_STATUS_MANUAL_REVISION);
                            case RekognitionReniecData
                                    .REJECTED_STATUS:
                                return Triple.of(false, null, null);
                            case RekognitionReniecData
                                    .APPROVED_STATUS:
                                if(loanApplication.getProductCategoryId() == ProductCategory.CUENTA_BANCARIA){
                                    if(rekognitionReniecResult.getResponse().getSelfieReniecFaceMatching() != null && (rekognitionReniecResult.getResponse().getSelfieReniecFaceMatching() < RekognitionReniecData.MINIMUM_VALUE_FOR_SELFIE_RENIEC || rekognitionReniecResult.getResponse().getSelfieReniecFaceMatching() >= 100.0)) return Triple.of(false, null, null);
                                    else if(rekognitionReniecResult.getResponse().getMinimunFaceMatchingValue() == null || rekognitionReniecResult.getResponse().getMinimunFaceMatchingValue() < 80.0 || rekognitionReniecResult.getResponse().getMinimunFaceMatchingValue() >= 100.0) return Triple.of(false, null, null);
                                    else if(rekognitionReniecResult.getResponse().getMinimunFaceMatchingValue() >= 80.0 && rekognitionReniecResult.getResponse().getMinimunFaceMatchingValue() < 100.0) return Triple.of(true, null, null);
                                }
                                else{
                                    return Triple.of(false, "Se requiere revisión manual", LoanApplicationApprovalValidation.CUSTOM_STATUS_MANUAL_REVISION_WAITING_APPROVAL);
                                }
                            default:
                                return Triple.of(false, null, null);
                        }
                    }

                }
                List<MatiResult> matiResults = securityDao.getMatiResultsByLoanApplication(loanApplication.getId());
                MatiResult lastMatiResult = matiResults.stream().sorted((e1, e2) -> e2.getRegisterDate().compareTo(e1.getRegisterDate())).findFirst().orElse(null);
                if(lastMatiResult != null){
                    Person person = personDao.getPerson(loanApplication.getPersonId(), false, Configuration.getDefaultLocale());
//                    if(!Configuration.hostEnvIsProduction()){
//                        return true;
//                    }else{
                        boolean res = lastMatiResult.getDocumentNumber() != null && lastMatiResult.getDocumentNumber().equalsIgnoreCase(person.getDocumentNumber());
                        if(!res)
                            return Triple.of(res, "Los documentos no corresponden al solicitante", null);
                        else
                        {
                            Integer matiStatus = lastMatiResult.getStatus();
                            if(matiStatus != null){
                                if(loanApplication.getProductCategoryId().equals(ProductCategory.VALIDACION_IDENTIDAD)){
                                    switch (matiStatus){
                                        case MatiResult.MATI_STATUS_VERIFIED:
                                            Double matiScore = lastMatiResult.getScore();
                                            if(matiScore == null || matiScore < 90.0) return Triple.of(false, null, null);
                                            else return Triple.of(res, null, null);
                                        default:
                                            return Triple.of(false, null, null);
                                    }
                                }
                                else {
                                    switch (matiStatus){
                                        case MatiResult.MATI_STATUS_VERIFIED:
                                            Double matiScore = lastMatiResult.getScore();
                                            if(matiScore == null || matiScore < 70.0) return Triple.of(false, null, null);
                                            else if(matiScore < 90.0) return Triple.of(false, "Se requiere revisión manual", LoanApplicationApprovalValidation.CUSTOM_STATUS_MANUAL_REVISION);
                                            //else return Triple.of(res, null, null);
                                            return Triple.of(false, "Se requiere revisión manual", LoanApplicationApprovalValidation.CUSTOM_STATUS_MANUAL_REVISION_WAITING_APPROVAL);
                                        case MatiResult.MATI_STATUS_REJECTED:
                                            return Triple.of(false, null, null);
                                        case MatiResult.MATI_STATUS_REVIEW:
                                            return Triple.of(false, "Se requiere revisión manual", LoanApplicationApprovalValidation.CUSTOM_STATUS_MANUAL_REVISION);
                                    }
                                }
                            }
                        }
//                    }
                }
                RekognitionProResult rekognitionProResult = securityDao.getRekognitionProResult(loanApplication.getId());
                if(rekognitionProResult != null){
                    if(rekognitionProResult.getResponse() == null) return Triple.of(false, null, null);
                    else{
                        RekognitionProData rekognitionProData = rekognitionProResult.getResponse();
                        if(rekognitionProData.getStatus() == null)  return Triple.of(false, null,  LoanApplicationApprovalValidation.CUSTOM_STATUS_MANUAL_REVISION);
                        switch (rekognitionProData.getStatus()){
                            case RekognitionProData
                                    .REVIEW_NEEDED_STATUS:
                                return Triple.of(false, null,  LoanApplicationApprovalValidation.CUSTOM_STATUS_MANUAL_REVISION);
                            case RekognitionProData
                                    .REJECTED_STATUS:
                                return Triple.of(false, null, null);
                            case RekognitionProData
                                        .APPROVED_STATUS:
                                    if(rekognitionProData.getFaceMatching() == null || rekognitionProData.getFaceMatching() < 60.0 || rekognitionProData.getFaceMatching() >= 100.0) return Triple.of(false, null, null);
                                    else if( rekognitionProData.getFaceMatching() >= 60.0 && rekognitionProData.getFaceMatching() < 80.0) return Triple.of(false, null, LoanApplicationApprovalValidation.CUSTOM_STATUS_MANUAL_REVISION);
                                    else if( rekognitionProData.getFaceMatching() >= 80.0 && rekognitionProData.getFaceMatching() < 100.0) return Triple.of(true, null, null);
                                default:
                                    return Triple.of(false, null, null);
                            }
                    }
                }
                List<RecognitionResultsPainter> recognitions = loanApplicationDao.getRecognitionResults(loanApplication.getPersonId(), Configuration.getDefaultLocale());
                if (recognitions != null) {
                    Double rekognitionMinPercentage = null;
                    if(loanApplication.getSelectedEntityProductParameterId() != null){
                        EntityProductParams entityParams = catalogService.getEntityProductParamById(loanApplication.getSelectedEntityProductParameterId());
                        if(entityParams != null && entityParams.getEntityProductParamIdentityValidationConfig() != null && entityParams.getEntityProductParamIdentityValidationConfig().getRekognitionMinPercentage() != null)
                            rekognitionMinPercentage = entityParams.getEntityProductParamIdentityValidationConfig().getRekognitionMinPercentage();
                    }
                    if(rekognitionMinPercentage != null){
                        RecognitionResultsPainter recognitionResultsPainter = recognitions.stream().filter(e-> e.getLoanApplicationId() != null && e.getLoanApplicationId().equals(loanApplication.getId())).findFirst().orElse(null);
                        if(recognitionResultsPainter != null && recognitionResultsPainter.getLastRekognitionResult() != null && recognitionResultsPainter.getLastRekognitionResult().getHighSimilarity() >= rekognitionMinPercentage) return Triple.of(true, null, null);
                    }
                    else return Triple.of(false, null, LoanApplicationApprovalValidation.CUSTOM_STATUS_NO_PARAM);
                }
                return Triple.of(false, null, null);
            }
            case ApprovalValidation.CCI: {
                PersonBankAccountInformation personBankAccountInformation = personDao.getPersonBankAccountInformation(Configuration.getDefaultLocale(), loanApplication.getPersonId());
                if(personBankAccountInformation != null && personBankAccountInformation.getCciCode() != null){
                    return Triple.of(true, null, null);
                }
                // If doesnt has the cci, go to manual revision
                return Triple.of(false, null, LoanApplicationApprovalValidation.CUSTOM_STATUS_MANUAL_REVISION);
//                return Triple.of(false, null, null);
            }
            case ApprovalValidation.VERIF_CORREO_ELECTRONICO: {
                User user = userDAO.getUser(loanApplication.getUserId());
                if(user.getEmailVerified() != null && user.getEmailVerified()) return Triple.of(true, null, null);
                return Triple.of(false, null, LoanApplicationApprovalValidation.CUSTOM_STATUS_MANUAL_REVISION);
            }
            case ApprovalValidation.LIMITE_DE_MONTO: {
                List<LoanOffer> loanOffers = loanApplicationDao.getLoanOffers(loanApplication.getId());
                LoanOffer selectedOffer = loanOffers != null ? loanOffers.stream().filter(o -> o.getSelected() != null && o.getSelected()).findFirst().orElse(null) : null;
                if(selectedOffer == null || selectedOffer.getAmmount() == null || selectedOffer.getAmmount().doubleValue() > 10000.0) return Triple.of(false, null, LoanApplicationApprovalValidation.CUSTOM_STATUS_MANUAL_REVISION);
                return Triple.of(true, null, null);
            }
        }
        return Triple.of(false, null,null);
    }

    @Override
    public List<ApprovalValidation> getApprovalValidationIds(LoanApplication loanApplication, EntityProductParams entityProductParams){
        if(entityProductParams.getApprovalValidationConfigs() == null)
            return null;
        List<EntityProductParamApprovalValidationConfig> validationConfig = entityProductParams.getApprovalValidationConfigs();
        String configCode = null;
        switch (entityProductParams.getId()){
            case EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE: {
                AztecaPreApprovedBase aztecaPreApprovedBase = null;
                if (loanApplication.getEntityCustomData() != null && loanApplication.getEntityCustomData().has(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_BASE_PREAPROBADA.getKey())) {
                    aztecaPreApprovedBase = new Gson().fromJson(loanApplication.getEntityCustomData().getJSONObject(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_BASE_PREAPROBADA.getKey()).toString(), AztecaPreApprovedBase.class);
                }
                configCode = "DEFAULT";
                /*
                if (aztecaPreApprovedBase != null) {
                    //configCode = "DEFAULT";
                    if (aztecaPreApprovedBase.getIdCampania() == 29)
                        configCode = "CAMP_29";
                    else if (aztecaPreApprovedBase.getIdCampania() == 49 && aztecaPreApprovedBase.getTipoVerificacion().equalsIgnoreCase("0"))
                        configCode = "CAMP_49_TIPO_0";
                    else if (aztecaPreApprovedBase.getIdCampania() == 49 && aztecaPreApprovedBase.getTipoVerificacion().equalsIgnoreCase("1"))
                        configCode = "CAMP_49_TIPO_1";
                    else if (aztecaPreApprovedBase.getIdCampania() == 50 && aztecaPreApprovedBase.getTipoVerificacion().equalsIgnoreCase("0"))
                        configCode = "CAMP_50_TIPO_0";
                    else if (aztecaPreApprovedBase.getIdCampania() == 50 && aztecaPreApprovedBase.getTipoVerificacion().equalsIgnoreCase("1"))
                        configCode = "CAMP_50_TIPO_1";

                    Integer disbursementOption = JsonUtil.getIntFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.AZTECA_DISBUSERMENT_OPTION.getKey(), null);
                    if (configCode != null && disbursementOption != null && disbursementOption.intValue() == Question174Service.AZTECA_SELECT_ACCOUNT_OPTION) {
                        configCode = configCode + "_OTRO_BANCO";
                    }
                }*/
                break;
            }
            case EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO: {
                Integer disbursementOption = JsonUtil.getIntFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.AZTECA_DISBUSERMENT_OPTION.getKey(), null);
                if (disbursementOption != null && disbursementOption.intValue() == Question174Service.AZTECA_SELECT_ACCOUNT_OPTION) {
                    configCode = "OTRO_BANCO";
                }else{
                    configCode = "DEFAULT";
                }
                break;
            }
            default:
                configCode = "DEFAULT";
                break;
        }
        if(configCode != null){
            String finalConfigCode = configCode;
            List<Integer> validations = validationConfig.stream().filter(v -> v.getCode().equalsIgnoreCase(finalConfigCode)).findFirst().map(c -> c.getValidationIds()).orElse(null);
            if(validations != null)
                return catalogService.getApprovalValidations().stream().filter(a -> validations.contains(a.getId())).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public boolean loanHasAllValidations(LoanApplication loanApplication, EntityProductParams entityProductParams) throws Exception{
        if(entityProductParams == null) entityProductParams = catalogService.getEntityProductParamById(loanApplication.getSelectedEntityProductParameterId());

        // Validate that the loan has the approval validations done
        List<ApprovalValidation> approvalValidations = getApprovalValidationIds(loanApplication, entityProductParams);
        if(approvalValidations != null){
            boolean allApproved = true;
            for(ApprovalValidation validation : approvalValidations){
                if(loanApplication.getApprovalValidations() == null || loanApplication.getApprovalValidations().stream().noneMatch(a -> a.getApprovalValidationId() == validation.getId().intValue()) ||
                        loanApplication.getApprovalValidations().stream().filter(a -> a.getApprovalValidationId() == validation.getId().intValue()).anyMatch(a -> a.getApproved() == null || !a.getApproved()))
                    return false;
            }
        }
        return true;
    }

    @Override
    public boolean loanHasAnyValidationsForManualApproval(LoanApplication loanApplication, EntityProductParams entityProductParams) throws Exception{
        if(entityProductParams == null) entityProductParams = catalogService.getEntityProductParamById(loanApplication.getSelectedEntityProductParameterId());

        // Validate that the loan has the approval validations done
        List<ApprovalValidation> approvalValidations = getApprovalValidationIds(loanApplication, entityProductParams);
        if(approvalValidations != null){
            boolean allApproved = true;
            for(ApprovalValidation validation : approvalValidations){
                if(loanApplication.getApprovalValidations() != null &&
                        loanApplication.getApprovalValidations().stream().filter(a -> a.getApprovalValidationId() == validation.getId().intValue()).anyMatch(a -> a.getStatus() != null && Arrays.asList(LoanApplicationApprovalValidation.CUSTOM_STATUS_MANUAL_REVISION,LoanApplicationApprovalValidation.CUSTOM_STATUS_MANUAL_REVISION_WAITING_APPROVAL).contains(a.getStatus())))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean loanHasAnyValidationsWithCustomStatus(LoanApplication loanApplication, Character status, EntityProductParams entityProductParams) throws Exception{
        if(status == null) return false;

        if(entityProductParams == null) entityProductParams = catalogService.getEntityProductParamById(loanApplication.getSelectedEntityProductParameterId());

        // Validate that the loan has the approval validations done
        List<ApprovalValidation> approvalValidations = getApprovalValidationIds(loanApplication, entityProductParams);
        if(approvalValidations != null){
            boolean allApproved = true;
            for(ApprovalValidation validation : approvalValidations){
                if(loanApplication.getApprovalValidations() != null &&
                        loanApplication.getApprovalValidations().stream().filter(a -> a.getApprovalValidationId() == validation.getId().intValue()).anyMatch(a -> a.getStatus() != null && status == a.getStatus()))
                    return true;
            }
        }
        return false;
    }



    @Override
    public boolean loanHasAnyFailedValidations(LoanApplication loanApplication, EntityProductParams entityProductParams) throws Exception{
        if(entityProductParams == null) entityProductParams = catalogService.getEntityProductParamById(loanApplication.getSelectedEntityProductParameterId());

        // Validate that the loan has the approval validations done
        List<ApprovalValidation> approvalValidations = getApprovalValidationIds(loanApplication, entityProductParams);
        if(approvalValidations != null){
            boolean allApproved = true;
            for(ApprovalValidation validation : approvalValidations){
                if(loanApplication.getApprovalValidations() != null &&
                        loanApplication.getApprovalValidations().stream()
                                .filter(a -> a.getApprovalValidationId() == validation.getId().intValue())
                                .anyMatch(a -> a.getApproved() != null && !a.getApproved()))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean existsApprovalValidationInLoan(LoanApplication loanApplication, Integer approvalValidationId, EntityProductParams entityProductParams) throws Exception {
        if(entityProductParams == null) entityProductParams = catalogService.getEntityProductParamById(loanApplication.getSelectedEntityProductParameterId());

        // Validate that the loan has the approval validations done
        List<ApprovalValidation> approvalValidations = getApprovalValidationIds(loanApplication, entityProductParams);
        return approvalValidations.stream().anyMatch(e -> e.getId().equals(approvalValidationId));
    }

    @Override
    public LoanApplicationApprovalValidation approvalValidationInLoan(LoanApplication loanApplication, Integer approvalValidationId) throws Exception {
        if(loanApplication.getApprovalValidations() != null){
            return  loanApplication.getApprovalValidations().stream()
                    .filter(a -> a.getApprovalValidationId() == approvalValidationId)
                    .findFirst().orElse(null);
        }
        return null;
    }

    @Override
    public boolean loanHasAnyRejectedValidations(LoanApplication loanApplication, EntityProductParams entityProductParams) throws Exception{
        if(entityProductParams == null) entityProductParams = catalogService.getEntityProductParamById(loanApplication.getSelectedEntityProductParameterId());

        // Validate that the loan has the approval validations done
        List<ApprovalValidation> approvalValidations = getApprovalValidationIds(loanApplication, entityProductParams);
        if(approvalValidations != null){
            boolean allApproved = true;
            for(ApprovalValidation validation : approvalValidations){
                if(loanApplication.getApprovalValidations() != null &&
                        loanApplication.getApprovalValidations().stream()
                                .filter(a -> a.getApprovalValidationId() == validation.getId().intValue())
                                .anyMatch(a -> a.getApproved() != null && !a.getApproved() && a.getStatus() == null))
                    return true;
            }
        }
        return false;
    }
}
