package com.affirm.common.dao.impl;

import com.affirm.common.dao.EvaluationDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.model.*;
import com.affirm.common.model.transactional.DefaultPolicy;
import com.affirm.common.model.transactional.LoanApplicationEvaluation;
import com.affirm.common.service.CatalogService;
import com.affirm.system.configuration.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EvaluationDAOImpl extends JsonResolverDAO implements EvaluationDAO {

    @Autowired
    private CatalogService catalogService;

    @Override
    public Integer getClusterId(Integer entityId, Integer loanApplicationId, Integer entityProductParameterId) throws Exception {
        return queryForObjectTrx("SELECT * from credit.get_cluster_id(?, ?, ?)", Integer.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityProductParameterId)
        );
    }

    @Override
    public Double getAdmissionTotalIncome(Integer loanApplicationId, Integer entityId, Integer productId) throws Exception {
        return queryForObjectTrx("SELECT person.get_admission_total_income(?, ?, ?)", Double.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId));
    }

    @Override
    public Double getMaxInstallment(Integer loanApplicationId, Integer entityId, Integer productId, Integer entityProductParameterId) throws Exception {
        return queryForObjectEvaluation("select * from evaluation.get_max_installment(?, ?, ?, ?)", Double.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId),
                new SqlParameterValue(Types.INTEGER, entityProductParameterId));
    }

    @Override
    public Boolean evaluatePartnerRcc(Integer partnerId, Integer param1, Integer param2) {
        return queryForObjectEvaluation("SELECT * FROM evaluation.evaluate_partner_rcc(?, ?, ?)", Boolean.class,
                new SqlParameterValue(Types.INTEGER, partnerId),
                new SqlParameterValue(Types.INTEGER, param1),
                new SqlParameterValue(Types.INTEGER, param2)
        );
    }

    @Override
    public List<EquifaxDeudasHistoricas> getEquifaxDeudasHistoricasByLoanApplicationId(Integer loanApplicationId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("SELECT * FROM credit.get_efx_deudas_historicas(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId)
        );

        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<EquifaxDeudasHistoricas> equifaxDeudasHistoricasList = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            EquifaxDeudasHistoricas equifaxDeudasHistoricas = new EquifaxDeudasHistoricas();
            equifaxDeudasHistoricas.fillFromDb(dbArray.getJSONObject(i));
            equifaxDeudasHistoricasList.add(equifaxDeudasHistoricas);
        }

        return equifaxDeudasHistoricasList;
    }

    @Override
    public List<EquifaxMicrofinanzasCalificaciones> getEquifaxMicrofinanzasCalificacionesByLoanApplicationId(Integer loanApplicationId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("SELECT * FROM credit.get_efx_microfinanzas_calificaciones(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId)
        );

        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<EquifaxMicrofinanzasCalificaciones> equifaxMicrofinanzasCalificacionesList = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            EquifaxMicrofinanzasCalificaciones equifaxMicrofinanzasCalificaciones = new EquifaxMicrofinanzasCalificaciones();
            equifaxMicrofinanzasCalificaciones.fillFromDb(dbArray.getJSONObject(i));
            equifaxMicrofinanzasCalificacionesList.add(equifaxMicrofinanzasCalificaciones);
        }

        return equifaxMicrofinanzasCalificacionesList;
    }

    @Override
    public List<EquifaxSicomCabecera> getEquifaxSicomCabecera(Integer loanApplicationId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("SELECT * FROM credit.get_efx_sicom_cabecera(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId)
        );
        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<EquifaxSicomCabecera> equifaxSicomCabeceras = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            EquifaxSicomCabecera equifaxSicomCabecera = new EquifaxSicomCabecera();
            equifaxSicomCabecera.fillFromDb(dbArray.getJSONObject(i));
            equifaxSicomCabeceras.add(equifaxSicomCabecera);
        }

        return equifaxSicomCabeceras;
    }

    @Override
    public EquifaxIndicadoresConsultaU2M getConsultasFromEquifaxIndicadoresConsultaU2MByLoanApplicationId(Integer loanApplicationId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("SELECT * FROM credit.get_efx_indicadores_consulta_u2m(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId)
        );
        if (dbJson == null) {
            return null;
        }

        EquifaxIndicadoresConsultaU2M equifaxIndicadoresConsultaU2M = new EquifaxIndicadoresConsultaU2M();
        equifaxIndicadoresConsultaU2M.fillFromDb(dbJson);

        return equifaxIndicadoresConsultaU2M;
    }

    @Override
    public LoanApplicationReclosure getLoanApplicationReclosure(Integer loanApplicationId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("SELECT * FROM credit.get_loan_application_reclosure(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId)
        );
        if (dbJson == null) {
            return null;
        }

        LoanApplicationReclosure loanApplicationReclosure = new LoanApplicationReclosure();
        loanApplicationReclosure.fillFromDb(dbJson);

        return loanApplicationReclosure;
    }

    @Override
    public ApplicationEFLAssessment getEFLAssessmentByLoanApplicationId(Integer loanApplicationId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("SELECT * FROM credit.get_tb_application_efl_assessment(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId)
        );
        if (dbJson == null) {
            return null;
        }

        ApplicationEFLAssessment applicationEFLAssessment = new ApplicationEFLAssessment();
        applicationEFLAssessment.fillFromDb(dbJson);

        return applicationEFLAssessment;
    }

    @Override
    public Double getMonthlyInstallmentTotal(String documentNumber) throws Exception {
        return queryForObjectTrx("select * from sysrcc.get_monthly_installment_total(?)", Double.class,
                new SqlParameterValue(Types.VARCHAR, documentNumber));
    }

    @Override
    public Double getMonthlyInstallmentAcceso(String documentNumber) throws Exception {
        return queryForObjectTrx("select * from sysrcc.get_monthly_installment_acceso(?)", Double.class,
                new SqlParameterValue(Types.VARCHAR, documentNumber));
    }

    @Override
    public boolean isOverindebtedCompartamos(Integer loanApplicationId) throws Exception {
        return queryForObjectTrx("SELECT * FROM credit.is_overindebted_compartamos(?)", Boolean.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId)
        );
    }

    public Double getTotalDebt(Integer loanApplicationId) {
        return queryForObjectTrx("select * from sysrcc.get_total_debt(?)", Double.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public boolean isEmploymentContinuityBds(Integer loanApplicationId) {
        return queryForObjectTrx("select * from credit.employment_continuity_bds(?)", Boolean.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public boolean isEmploymentContinuityBds(Integer loanApplicationId, Integer bureauId) {
        return queryForObjectTrx("select * from credit.employment_continuity_bds(?, ?)", Boolean.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, bureauId)
        );
    }

    @Override
    public boolean isEmploymentTimeBds(Integer loanApplicationId) {
        return queryForObjectTrx("select * from credit.employment_time_bds(?)", Boolean.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public boolean isEmploymentTimeBds(Integer loanApplicationId, Integer bureauId) {
        return queryForObjectTrx("select * from credit.employment_time_bds(?, ?)", Boolean.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, bureauId)
        );
    }

    @Override
    public void startEvaluation(Integer loanApplicationId, Integer entityId, Integer productId) throws Exception {
        queryForObjectEvaluation("select * from evaluation.start_evaluation(?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, productId),
                new SqlParameterValue(Types.INTEGER, entityId));
    }

    @Override
    public List<LoanApplicationEvaluation> getEvaluationsWithPolicies(int loanApplicationId) throws Exception {
        JSONArray dbArray = queryForObjectEvaluation("select * from evaluation.get_evaluations_with_filters(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<LoanApplicationEvaluation> evaluations = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            LoanApplicationEvaluation evaluation = new LoanApplicationEvaluation();
            evaluation.fillFromDb(dbArray.getJSONObject(i), catalogService, Configuration.getDefaultLocale());
            evaluations.add(evaluation);
        }
        return evaluations;
    }

    @Override
    public void updateRunDefaultEvaluation(Integer evaluationId, Boolean runDefaultEvaluation) {
        update("UPDATE evaluation.tb_evaluation SET run_default_evaluation = ? WHERE evaluation_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.BOOLEAN, runDefaultEvaluation),
                new SqlParameterValue(Types.INTEGER, evaluationId));
    }

    @Override
    public void updateStep(Integer evaluationId, int step) {
        update("update evaluation.tb_evaluation set step = ? where evaluation_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.INTEGER, step),
                new SqlParameterValue(Types.INTEGER, evaluationId));
    }

    @Override
    public List<DefaultPolicy> getDefaultPolicyParameters() {
        JSONArray dbArray = queryForObjectTrx("select * from originator.get_ct_default_policy_parameters()", JSONArray.class);
        if (dbArray == null)
            return new ArrayList<>();

        List<DefaultPolicy> policies = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            JSONObject json = dbArray.getJSONObject(i);
            DefaultPolicy policy = new DefaultPolicy();
            policy.fillFromDb(json, catalogService);
            policies.add(policy);
        }
        return policies;
    }

    @Override
    public void updateDefaultEvaluationPolicyId(Integer evaluationId, Integer policyId) {
        update("UPDATE evaluation.tb_evaluation SET default_evaluation_policy_id = ? WHERE evaluation_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.INTEGER, policyId),
                new SqlParameterValue(Types.INTEGER, evaluationId));
    }

    @Override
    public void updateIsApproved(Integer evaluationId, Boolean isApproved) {
        update("UPDATE evaluation.tb_evaluation SET is_approved = ? WHERE evaluation_id = ?;",
                EVALUATION_DB,
                new SqlParameterValue(Types.BOOLEAN, isApproved),
                new SqlParameterValue(Types.INTEGER, evaluationId));
    }

    @Override
    public void updatePolicyMessage(Integer evaluationId, String policyMessage) {
        update("UPDATE evaluation.tb_evaluation SET policy_message = ? WHERE evaluation_id = ?;",
                EVALUATION_DB,
                new SqlParameterValue(Types.VARCHAR, policyMessage),
                new SqlParameterValue(Types.INTEGER, evaluationId));
    }

    @Override
    public void updateApplicationStatusEvaluation(Integer loanApplicationId) {
        queryForObjectEvaluation("select * from evaluation.update_application_status_evaluation(?)", Boolean.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

//    @Override
//    public PersonEntity getPersonEntity(Integer personId, Integer entityId) throws Exception {
//        JSONObject dbJson = queryForObjectTrx("SELECT * FROM person.get_person_entity(?, ?)", JSONObject.class,
//                new SqlParameterValue(Types.INTEGER, personId),
//                new SqlParameterValue(Types.INTEGER, entityId)
//        );
//        if (dbJson == null) {
//            return null;
//        }
//
//        PersonEntity personEntity = new PersonEntity();
//        personEntity.fillFromDb(dbJson);
//
//        return personEntity;
//    }

    @Override
    public List<ApprovedDataLoanApplication> getApprovedDataLoanApplication(Integer loanApplicationId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("SELECT * FROM originator.get_approved_data_loan_application(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId)
        );
        if (dbArray == null) {
            return new ArrayList();
        }

        List<ApprovedDataLoanApplication> approves = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            JSONObject json = dbArray.getJSONObject(i);
            ApprovedDataLoanApplication approvedDataLoanApplication = new ApprovedDataLoanApplication();
            approvedDataLoanApplication.fillFromDb(json);
            approves.add(approvedDataLoanApplication);
        }

        return approves;
    }

    @Override
    public Address getAddressByPersonId(Integer personId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.get_address(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId)
        );
        if (dbJson == null) {
            return null;
        }

        Address address = new Address();
        address.fillFromDb(dbJson);

        return address;
    }

    @Override
    public List<EquifaxAvales> getEquifaxAvales(Integer loanApplicationId) throws Exception {
        JSONArray dbArray = queryForObjectEvaluation("select * from evaluation.get_efx_avales(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId)
        );
        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<EquifaxAvales> equifaxAvales = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            EquifaxAvales equifaxAvale = new EquifaxAvales();
            equifaxAvale.fillFromDb(dbArray.getJSONObject(i));
            equifaxAvales.add(equifaxAvale);
        }

        return equifaxAvales;
    }

    @Override
    public void updateEvaluationPoliciy(Integer loanApplicationId, Integer entityId, Integer productId, Integer policyId) {
        update("UPDATE evaluation.tb_evaluation SET policy_id = ? WHERE loan_application_id = ? and entity_id = ? and product_id = ?;",
                EVALUATION_DB,
                new SqlParameterValue(Types.INTEGER, policyId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId));
    }
}
