package com.affirm.common.dao;

import com.affirm.common.model.*;
import com.affirm.common.model.transactional.DefaultPolicy;
import com.affirm.common.model.transactional.LoanApplicationEvaluation;

import java.util.List;

public interface EvaluationDAO {// POLICY DAO COULD BE BETTER NAME

    Integer getClusterId(Integer entityId, Integer loanApplicationId, Integer entityProductParameterId) throws Exception;

    Double getAdmissionTotalIncome(Integer loanApplicationId, Integer entityId, Integer productId) throws Exception;

    Double getMaxInstallment(Integer loanApplicationId, Integer entityId, Integer productId, Integer entityProductParameterId) throws Exception;

    Boolean evaluatePartnerRcc(Integer partnerId, Integer param1, Integer param2);

    List<EquifaxDeudasHistoricas> getEquifaxDeudasHistoricasByLoanApplicationId(Integer loanApplicationId) throws Exception;

    List<EquifaxMicrofinanzasCalificaciones> getEquifaxMicrofinanzasCalificacionesByLoanApplicationId(Integer loanApplicationId) throws Exception;

    List<EquifaxSicomCabecera> getEquifaxSicomCabecera(Integer loanApplicationId) throws Exception;

    EquifaxIndicadoresConsultaU2M getConsultasFromEquifaxIndicadoresConsultaU2MByLoanApplicationId(Integer loanApplicationId) throws Exception;

    LoanApplicationReclosure getLoanApplicationReclosure(Integer loanApplicationId) throws Exception;

    ApplicationEFLAssessment getEFLAssessmentByLoanApplicationId(Integer loanApplicationId) throws Exception;

    Double getMonthlyInstallmentTotal(String documentNumber) throws Exception;

    Double getMonthlyInstallmentAcceso(String documentNumber) throws Exception;

    boolean isOverindebtedCompartamos(Integer loanApplicationId) throws Exception;

    Double getTotalDebt(Integer loanApplicationId);

    boolean isEmploymentContinuityBds(Integer loanApplicationId);

    boolean isEmploymentContinuityBds(Integer loanApplicationId, Integer bureauId);

    boolean isEmploymentTimeBds(Integer loanApplicationId);

    boolean isEmploymentTimeBds(Integer loanApplicationId, Integer bureauId);

    void startEvaluation(Integer loanApplicationId, Integer entityId, Integer productId) throws Exception;

    List<LoanApplicationEvaluation> getEvaluationsWithPolicies(int loanApplicationId) throws Exception;

    void updateRunDefaultEvaluation(Integer evaluationId, Boolean runDefaultEvaluation);

    void updateStep(Integer evaluationId, int step);

    List<DefaultPolicy> getDefaultPolicyParameters();

    void updateDefaultEvaluationPolicyId(Integer evaluationId, Integer policyId);

    void updateIsApproved(Integer evaluationId, Boolean isApproved);

    void updatePolicyMessage(Integer evaluationId, String policyMessage);

    void updateApplicationStatusEvaluation(Integer loanApplicationId);

//    PersonEntity getPersonEntity(Integer personId, Integer entityId) throws Exception;

    List<ApprovedDataLoanApplication> getApprovedDataLoanApplication(Integer loanApplicationId) throws Exception;

    Address getAddressByPersonId(Integer personId) throws Exception;

    List<EquifaxAvales> getEquifaxAvales(Integer loanApplicationId) throws Exception;

    void updateEvaluationPoliciy(Integer loanApplicationId, Integer entityId, Integer productId, Integer policyId);
}
