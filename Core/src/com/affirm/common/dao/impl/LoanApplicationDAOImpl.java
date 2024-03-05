package com.affirm.common.dao.impl;

import com.affirm.aws.RecognitionResultsPainter;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.AppointmentSchedule;
import com.affirm.common.model.PreApprovedInfo;
import com.affirm.common.model.catalog.ConsolidationAccountType;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.*;

/**
 * @author jrodriguez
 */

@Repository("loanApplicationDAO")
@CacheConfig(cacheNames = "catalogCache", keyGenerator = "cacheKeyGenerator")
public class LoanApplicationDAOImpl extends JsonResolverDAO implements LoanApplicationDAO {


    @Autowired
    private CatalogService catalogService;
    @Autowired
    private LoanApplicationService loanApplicationService;

    @Override
    public LoanApplication registerLoanApplication(int userId, Integer ammount, Integer installments, Integer reasonId, Integer productId, Integer days, Integer clusterId, char originChar, Integer employerId, Integer registerType, Integer entityId, int countryId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.ins_loan_application(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.INTEGER, ammount),
                new SqlParameterValue(Types.INTEGER, installments),
                new SqlParameterValue(Types.INTEGER, reasonId),
                new SqlParameterValue(Types.INTEGER, productId),
                new SqlParameterValue(Types.INTEGER, days),
                new SqlParameterValue(Types.INTEGER, clusterId),
                new SqlParameterValue(Types.CHAR, originChar),
                new SqlParameterValue(Types.INTEGER, employerId),
                new SqlParameterValue(Types.INTEGER, registerType),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, countryId));

        LoanApplication loan = new LoanApplication();
        loan.setId(JsonUtil.getIntFromJson(dbJson, "loan_application_id", null));
//        loan.setUserId(userId);
//        loan.setAmmount(form.getLoanApplicationAmmount());
//        loan.setInstallments(form.getLoanApplicationInstallemnts());
//        loan.setReasonId(form.getLoanApplicationReason());
        return loan;
    }

    @Override
    public LoanApplicationPreliminaryEvaluation getLastPreliminaryEvaluation(int loanApplicationId, Locale locale, int database) throws Exception {
        JSONObject dbJson = queryForObject("select * from evaluation.get_last_preliminary_evaluation(?)", JSONObject.class, database,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return null;
        }

        LoanApplicationPreliminaryEvaluation loanPreliminaryEvaluation = new LoanApplicationPreliminaryEvaluation();
        loanPreliminaryEvaluation.fillFromDb(dbJson, catalogService, locale);
        return loanPreliminaryEvaluation;
    }

    @Override
    public LoanApplicationPreliminaryEvaluation getLastPreliminaryEvaluation(int loanApplicationId, Locale locale) throws Exception {
        return getLastPreliminaryEvaluation(loanApplicationId, locale, EVALUATION_DB);
    }

    @Override
    public void startPreliminaryEvaluation(int loanApplicationId) throws Exception {
        queryForObjectEvaluation("select * from evaluation.start_preliminary_evaluation(?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    @Cacheable
    public List<EntityRate> getEntityRatesByProduct(Locale locale, Integer productId) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from product.get_rate_commission(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, productId));
        if (dbJson == null)
            return null;

        List<EntityRate> entityRates = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            EntityRate entityRate = new EntityRate();
            entityRate.fillFromDb(dbJson.getJSONObject(i), catalogService, locale);
            entityRates.add(entityRate);
        }
        return entityRates;
    }

    @Override
    public JSONObject executeEvaluationPreBureau(int loanApplicationId, int productId, int entityId) throws Exception {
        JSONObject dbjson = queryForObjectEvaluation("select * from evaluation.evaluation_pre_bureau(?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, productId),
                new SqlParameterValue(Types.INTEGER, entityId));
        return dbjson;
    }

    @Override
    public boolean executeEvaluationBureau(int loanApplicationId, Integer loanApplicationEvaluationId) throws Exception {
        JSONObject dbjson = queryForObjectEvaluation("select * from evaluation.evaluation_bureau(?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, loanApplicationEvaluationId));
        return dbjson.getBoolean("generate_offer");
    }

    @Override
    public LoanApplicationEvaluation getLastEvaluation(int loanApplicationId, Locale locale) throws Exception {
        return getLastEvaluation(loanApplicationId, locale, EVALUATION_DB);
    }

    @Override
    public LoanApplicationEvaluation getLastEvaluation(int loanApplicationId, Locale locale, int database) throws Exception {
        JSONObject dbJson = queryForObject("select * from evaluation.get_last_evaluation(?)", JSONObject.class, database,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return null;
        }

        LoanApplicationEvaluation loanEvaluation = new LoanApplicationEvaluation();
        loanEvaluation.fillFromDb(dbJson, catalogService, locale);
        return loanEvaluation;
    }

    @Override
    public List<LoanOffer> createLoanOffers(int loanApplicationId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.generate_loan_offer(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbArray == null) {
            return null;
        }

        List<LoanOffer> offers = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            LoanOffer offer = new LoanOffer();
            offer.fillFromDb(dbArray.getJSONObject(i), catalogService);
            loanApplicationService.generateOfferTCEA(offer, loanApplicationId);
            offers.add(offer);
        }
        return getLoanOffers(loanApplicationId);
    }

    @Override
    public void updateDebtnessValues(Integer loanApplicationId) throws Exception {
        queryForObjectTrx("select * from credit.upd_debtness_values(?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateTCEA(int loanOfferId, double effectiveAnnualCostRate) throws Exception {
        updateTrx("UPDATE credit.tb_loan_offer SET effective_annual_cost_rate = ? WHERE loan_offer_id = ?;",
                new SqlParameterValue(Types.DOUBLE, effectiveAnnualCostRate),
                new SqlParameterValue(Types.INTEGER, loanOfferId));
    }

    @Override
    public void updateTEA(int loanOfferId, double effectiveAnnualRate) throws Exception {
        updateTrx("UPDATE credit.tb_loan_offer SET effective_annual_rate = ? WHERE loan_offer_id = ?;",
                new SqlParameterValue(Types.DOUBLE, effectiveAnnualRate),
                new SqlParameterValue(Types.INTEGER, loanOfferId));
    }

    @Override
    public void updateSolvenTCEA(int loanOfferId, double solvenEffectiveAnnualCostRate) throws Exception {
        updateTrx("UPDATE credit.tb_loan_offer SET effective_annual_cost_rate_solven = ? WHERE loan_offer_id = ?;",
                new SqlParameterValue(Types.DOUBLE, solvenEffectiveAnnualCostRate),
                new SqlParameterValue(Types.INTEGER, loanOfferId));
    }

    @Override
    public void updateLoanApplication(int loanApplicationId, Integer ammount, Integer installments, Integer productId, Integer loanDays) throws Exception {
        queryForObjectTrx("select * from credit.upd_loan_application(?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, ammount),
                new SqlParameterValue(Types.INTEGER, installments),
                new SqlParameterValue(Types.INTEGER, productId),
                new SqlParameterValue(Types.INTEGER, loanDays));
    }

    @Override
    public void registerSelectedLoanOffer(int loanOfferId, Date firtDueDate) throws Exception {
        queryForObjectTrx("select * from credit.select_loan_offer(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanOfferId),
                new SqlParameterValue(Types.DATE, firtDueDate));
    }

    @Override
    public void updateFirstDueDateWithSchedules(int loanApplicationId, Date firstDueDate) throws Exception {
        queryForObjectTrx("select * from credit.bo_update_first_due_date(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.DATE, firstDueDate));
    }

    @Override
    public void updateFirstDueDate(int loanApplicationId, Date firstDueDate) throws Exception {
        queryForObjectTrx("select * from credit.select_first_due_date(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.DATE, firstDueDate));
    }

    @Override
    public void registerLoanApplicationSIgnature(int loanApplicationOfferId, String fullName, Integer docType, String docNumber) throws Exception {
        queryForObjectTrx("select * from credit.sign_loan_offer(?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationOfferId),
                new SqlParameterValue(Types.VARCHAR, fullName),
                new SqlParameterValue(Types.INTEGER, docType),
                new SqlParameterValue(Types.VARCHAR, docNumber));
    }

    @Override
    public com.affirm.common.model.transactional.LoanApplication getLoanApplication(int loanApplicationId, Locale locale) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.get_loan_application(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return null;
        }

        com.affirm.common.model.transactional.LoanApplication loanApplication = new com.affirm.common.model.transactional.LoanApplication();
        loanApplication.fillFromDb(dbJson, catalogService, locale);
        return loanApplication;
    }

    @Override
    public com.affirm.common.model.transactional.LoanApplication getLoanApplicationLite(int loanApplicationId, Locale locale) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.get_loan_application_lite(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return null;
        }

        com.affirm.common.model.transactional.LoanApplication loanApplication = new com.affirm.common.model.transactional.LoanApplication();
        loanApplication.fillFromDb(dbJson, catalogService, locale);
        return loanApplication;
    }

    @Override
    public <T extends LoanApplication> T getLoanApplication(int loanApplicationId, Locale locale, Class<T> returntype) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.get_loan_application(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return null;
        }

        T loanApplication = returntype.getConstructor().newInstance();

        loanApplication.fillFromDb(dbJson, catalogService, locale);
        return loanApplication;
    }

    @Override
    public void updateLoanApplicationStatus(int loanApplicationId, int loanApplicationStatusId, Integer sysUserId) throws Exception {
        queryForObjectTrx("select * from credit.upd_loan_application_status(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, sysUserId),
                new SqlParameterValue(Types.INTEGER, loanApplicationStatusId));
    }

    @Override
    public <T extends LoanApplication> List<T> getLoanApplicationsByPerson(Locale locale, int personId, Class<T> returntype) throws Exception {
        JSONArray dbJson = queryForObject("select * from credit.bo_get_loan_applications(?)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return null;
        }

        List<T> applications = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            T loan = returntype.getConstructor().newInstance();
            loan.fillFromDb(dbJson.getJSONObject(i), catalogService, locale);
            applications.add(loan);
        }
        return applications;
    }

    @Override
    public <T extends LoanApplication> List<T> getLoanApplicationsByEntityUser(Locale locale, int entityUserId, Class<T> returntype) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from credit.get_loan_applications_by_entity_user_id(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityUserId));
        if (dbJson == null) {
            return null;
        }

        List<T> applications = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            T loan = returntype.getConstructor().newInstance();
            loan.fillFromDb(dbJson.getJSONObject(i), catalogService, locale);
            applications.add(loan);
        }
        return applications;
    }

    @Override
    public LoanApplication getActiveLoanApplicationByPerson(Locale locale, int personId, int productCategoryId) throws Exception {
        return getActiveLoanApplicationByPerson(locale, personId, productCategoryId, null);
    }

    @Override
    public LoanApplication getActiveLoanApplicationByPerson(Locale locale, int personId, int productCategoryId, Integer entityId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.get_active_loan_application(?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, productCategoryId),
                new SqlParameterValue(Types.INTEGER, entityId));
        if (dbJson == null) {
            return null;
        }

        LoanApplication loanApplication = new LoanApplication();
        loanApplication.fillFromDb(dbJson, catalogService, locale);
        return loanApplication;
    }

    @Override
    public List<LoanApplication> getLoansApplicationByCollectionId(Locale locale, int productCategoryId, Integer entityId, Long collectionId) throws Exception{
        JSONArray dbJson = queryForObjectTrx("select * from credit.get_loans_application_by_collection_id(?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, productCategoryId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.BIGINT, collectionId)
                );
        if (dbJson == null) {
            return null;
        }

        List<LoanApplication> loanApplications = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            LoanApplication loan = new LoanApplication();
            loan.fillFromDb(dbJson.getJSONObject(i), catalogService, locale);
            loanApplications.add(loan);
        }
        return loanApplications;
    }

    @Override
    public <T extends LoanApplication> List<T> getActiveLoanApplicationsByPerson(Locale locale, int personId, Class<T> returntype) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from credit.get_active_loan_applications_by_person(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return null;
        }

        List<T> applications = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            T loan = returntype.getConstructor().newInstance();
            loan.fillFromDb(dbJson.getJSONObject(i), catalogService, locale);
            applications.add(loan);
        }
        return applications;
    }

    @Override
    public List<LoanOffer> getLoanOffers(int loanApplicationId) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from credit.get_loan_offer(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return null;
        }

        List<LoanOffer> offers = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            LoanOffer offer = new LoanOffer();
            offer.fillFromDb(dbJson.getJSONObject(i), catalogService);
            offers.add(offer);
        }
        return offers;
    }

    @Override
    public List<LoanOffer> getLoanOffersAll(int loanApplicationId) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from credit.bo_get_loan_offers(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<LoanOffer> offers = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            LoanOffer offer = new LoanOffer();
            offer.fillFromDb(dbJson.getJSONObject(i), catalogService);
            offers.add(offer);
        }
        return offers;
    }

    @Override
    public void assignanalyst(int loanApplicationId, int creditAnalystSysUserId, int sysUserId) throws Exception {
        queryForObjectTrx("select * from credit.bo_assign_analyst(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, creditAnalystSysUserId),
                new SqlParameterValue(Types.INTEGER, sysUserId));
    }

    @Override
    public void assignManagementAnalyst(int loanApplicationId, int creditAnalystSysUserId, int sysUserId) throws Exception {
        queryForObjectTrx("select * from credit.bo_assign_tracker(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, creditAnalystSysUserId),
                new SqlParameterValue(Types.INTEGER, sysUserId));
    }

    @Override
    public void unassignManagementAnalyst(int loanApplicationId) throws Exception {
        queryForObjectTrx("select * from credit.bo_unassign_analyst(?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public Credit generateCredit(int loanApplicationId, Locale locale) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.generate_credit(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return null;
        }

        Credit credit = new Credit();
        credit.fillFromDb(dbJson, catalogService, locale);
        return credit;
    }

//    @Override
//    public LoanApplicationBoPainter getLoanApplicationBO(int loanApplicationId, Locale locale) throws Exception {
//        StringBuffer query = new StringBuffer("select * from credit.get_loan_application(p_loan_application_id)");
//        PGQeuryUtil.replaceInt(query, "p_loan_application_id", loanApplicationId);
//
//        JSONObject dbJson = executeForJson(query.toString());
//        if (dbJson == null) {
//            return null;
//        }
//
//        LoanApplicationBoPainter loanApplication = new LoanApplicationBoPainter();
//        loanApplication.fillFromDb(dbJson, catalogService, locale);
//        return loanApplication;
//    }

    @Override
    public LoanOffer createLoanOfferAnalyst(int loanApplicationId, double loanAmmount, int installments, int entityId, int productId, Integer employerId) throws Exception {
        return this.createLoanOfferAnalyst(loanApplicationId, loanAmmount, installments, entityId, productId, employerId, null);
    }

    @Override
    public LoanOffer createLoanOfferAnalyst(int loanApplicationId, double loanAmmount, int installments, int entityId, int productId, Integer employerId, Integer entityProductParameterId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.generate_loan_offer_solven(?, ?, ?, ?, ?, ?, ?)",
                JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId),
                new SqlParameterValue(Types.INTEGER, employerId),
                new SqlParameterValue(Types.INTEGER, installments),
                new SqlParameterValue(Types.NUMERIC, loanAmmount),
                new SqlParameterValue(Types.INTEGER, entityProductParameterId));
        if (dbJson == null) {
            return null;
        }

        LoanOffer offer = new LoanOffer();
        offer.fillFromDb(dbJson, catalogService);
        loanApplicationService.generateOfferTCEA(offer, loanApplicationId);
        return offer;
    }

    @Override
    public void registerRejection(int loanApplicationId, Integer applicationRejectionReasonId) throws Exception {
        queryForObjectTrx("select * from credit.bo_upd_application_rejection_reason(?, ?)",
                String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, applicationRejectionReasonId));
    }

    @Override
    public void registerRejectionWithComment(int loanApplicationId, Integer applicationRejectionReasonId, String applicationRejectionComment) throws Exception {
        queryForObjectTrx("select * from credit.bo_upd_application_rejection_reason(?, ?, ?)",
                String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, applicationRejectionReasonId),
                new SqlParameterValue(Types.VARCHAR, applicationRejectionComment));
    }

    @Override
    public Integer registerIntent(Integer documentType, String documentNumber) throws Exception {
        return queryForObjectEvaluation("select * from evaluation.ins_salary_advance_evaluation(?, ?)", Integer.class,
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber));
    }

    @Override
    public void selectLoanOfferAnalyst(int loanOfferId, int sysUserId) throws Exception {
        queryForObjectTrx("select * from credit.select_loan_offer_analyst(?, ?)",
                String.class,
                new SqlParameterValue(Types.INTEGER, loanOfferId),
                new SqlParameterValue(Types.INTEGER, sysUserId));
    }

    @Override
    public void selectLoanOfferAnalystExtranet(int loanOfferId, int extranetUserId) throws Exception {
        queryForObjectTrx("select * from credit.select_loan_offer_analyst_extranet(?, ?)",
                String.class,
                new SqlParameterValue(Types.INTEGER, loanOfferId),
                new SqlParameterValue(Types.INTEGER, extranetUserId));
    }

    @Override
    public void updateHumanFormId(int loanApplicationId, int humanFormId) throws Exception {
        queryForObjectTrx("select * from credit.upd_loan_application_human_form(?, ?)",
                String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, humanFormId));
    }

    @Override
    public void updateLoanApplicationMood(int loanApplicationId, int mood) throws Exception {
        updateTrx("UPDATE credit.tb_loan_application set mood = ? where loan_application_id = ?;",
                new SqlParameterValue(Types.INTEGER, mood),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void registerIcarValidation(int loanApplicationId, JSONObject icarValidation) throws Exception {
        queryForObjectTrx("select * from credit.ins_icar_validation_result(?, ?::JSON)",
                String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.VARCHAR, icarValidation));
    }

    @Override
    public void registerBureauResult(int loanApplicationId, Integer score, String riskLevel, String conclusion, String equifaxResult, Integer bureauId) throws Exception {
        queryForObjectTrx("select * from credit.register_equifax_result(?,?,?,?,?::JSON,?)",
                String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, score),
                new SqlParameterValue(Types.VARCHAR, riskLevel),
                new SqlParameterValue(Types.VARCHAR, conclusion),
                new SqlParameterValue(Types.VARCHAR, equifaxResult),
                new SqlParameterValue(Types.INTEGER, bureauId));
    }

    @Override
    public void registerEvaluationSuccess(int loanApplicationId) throws Exception {
        queryForObjectEvaluation("select * from evaluation.evaluation_success_all(?)",
                String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public IcarValidation getIcarValidation(int loanApplicationId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.get_icar_validation(?)",
                JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return null;
        }

        IcarValidation icarvalidation = new IcarValidation();
        icarvalidation.fillFromDb(dbJson);
        return icarvalidation;
    }

    @Override
    public void registerClickSignLink(int loanApplicationId) throws Exception {
        queryForObjectTrx("select * from credit.click_sign_link(?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void registerNoAuthLinkExpiration(int loanApplicationId, Integer seconds) throws Exception {
        queryForObjectTrx("select * from credit.upd_auth_link_expiration_(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, seconds));
    }

    @Override
    public JSONObject preApprovalValidation(int loanApplicationId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.pre_approval_validation(?)",
                JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        return dbJson;
    }

    @Override
    public void updateMessengerLink(int loanApplicationId, Boolean messengerLink) throws Exception {
        updateTrx("UPDATE credit.tb_loan_application set messenger_link = ? where loan_application_id = ?",
                new SqlParameterValue(Types.BOOLEAN, messengerLink),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public boolean shouldCallBots(int loanApplicationId) throws Exception {
        JSONObject dbJson = queryForObjectEvaluation("select * from evaluation.in_proccess_evaluation(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return false;
        }

        return dbJson.getBoolean("run_bots");
    }

    @Override
    public SalaryAdvanceCalculatedAmount calculateSalaryAdvanceAmmount(Integer evaluationId, int employeeId, int employerId, Locale locale) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.calculate_salary_advance_ammount(?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, evaluationId),
                new SqlParameterValue(Types.INTEGER, employerId),
                new SqlParameterValue(Types.INTEGER, employeeId));
        if (dbJson == null)
            return null;

        SalaryAdvanceCalculatedAmount advance = new SalaryAdvanceCalculatedAmount();
        advance.fillFromDb(dbJson, catalogService, locale);
        return advance;
    }

    @Override
    public SalaryAdvanceCalculatedAmount calculateSalaryAdvanceAmmount(int employeeId, int employerId, Locale locale) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.calculate_salary_advance_ammount(?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, employerId),
                new SqlParameterValue(Types.INTEGER, employeeId));
        if (dbJson == null)
            return null;

        SalaryAdvanceCalculatedAmount advance = new SalaryAdvanceCalculatedAmount();
        advance.fillFromDb(dbJson, catalogService, locale);
        return advance;
    }

    @Override
    public void resetLoanApplication(Integer loanApplicationId) throws Exception {
        queryForObjectTrx("select * from credit.reset_loan_application(?);", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public LoanOffer createLoanOffersSalaryAdvance(int loanApplicationId, double amount, double comission, int employerId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.generate_loan_offer_salary_advance(?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.NUMERIC, amount),
                new SqlParameterValue(Types.NUMERIC, comission),
                new SqlParameterValue(Types.INTEGER, employerId));
        if (dbJson == null) {
            return null;
        }

        LoanOffer offer = new LoanOffer();
        offer.fillFromDb(dbJson, catalogService);
        loanApplicationService.generateOfferTCEA(offer, loanApplicationId);
        return offer;
    }

    @Override
    public void registerIovationResponse(int loanApplicationId, JSONObject ioResponse) {
        queryForObjectTrx("select * from credit.register_iovation_result(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.OTHER, ioResponse));
    }

    @Override
    public boolean mustCallIovation(int loanApplicationId) throws Exception {
        return queryForObjectTrx("select * from credit.must_call_iovation(?)", Boolean.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public JSONObject getIovationByLoanApplication(Integer loanApplicationId) throws Exception {
        return queryForObjectTrx("select * from credit.get_js_iovation(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public JSONArray getIovationByPerson(Integer personId) throws Exception {
        return queryForObjectTrx("select * from credit.get_js_iovation_by_person(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void registerConsolidationAccount(int loanApplicationId, int consolidationAccountTypeId, String entityCode, Double balance, Integer installments, Double rate, Boolean active, String accountCardNumber, Integer brandId, String ubigeoDepartment) {
        queryForObjectTrx("select * from credit.ins_application_consolidation_account(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, consolidationAccountTypeId),
                new SqlParameterValue(Types.VARCHAR, entityCode),
                new SqlParameterValue(Types.NUMERIC, balance),
                new SqlParameterValue(Types.INTEGER, installments),
                new SqlParameterValue(Types.NUMERIC, rate),
                new SqlParameterValue(Types.BOOLEAN, active),
                new SqlParameterValue(Types.VARCHAR, accountCardNumber),
                new SqlParameterValue(Types.INTEGER, brandId),
                new SqlParameterValue(Types.VARCHAR, ubigeoDepartment));
    }

    @Override
    public List<ConsolidableDebt> getConsolidationAccounts(int loanApplicationId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_application_consolidation_accounts(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbArray == null)
            return null;

        List<ConsolidableDebt> consolidableDebts = new ArrayList<>();
        List<ConsolidableDebt> auxDebts = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ConsolidableDebt debt = new ConsolidableDebt();
            debt.fillFromDb(dbArray.getJSONObject(i), catalogService);
            if (debt.getConsolidationAccounttype() == ConsolidationAccountType.LINEA_PARALELA ||
                    debt.getConsolidationAccounttype() == ConsolidationAccountType.DISPONIBILIDAD_EFECTIVO) {
                auxDebts.add(debt);
            } else {
                consolidableDebts.add(debt);
            }
        }
        if (!auxDebts.isEmpty()) {
            auxDebts.stream().filter(d -> d.getConsolidationAccounttype() == ConsolidationAccountType.LINEA_PARALELA).forEach(d -> {
                consolidableDebts.stream()
                        .filter(c -> c.getConsolidationAccounttype() == ConsolidationAccountType.TARJETA_CREDITO && c.getEntity().getCode() == d.getEntity().getCode())
                        .findFirst().ifPresent(c -> {
                    c.setBalanceLP(d.getBalance());
                    c.setRateLP(d.getRate());
                });
            });
            auxDebts.stream().filter(d -> d.getConsolidationAccounttype() == ConsolidationAccountType.DISPONIBILIDAD_EFECTIVO).forEach(d -> {
                consolidableDebts.stream()
                        .filter(c -> c.getConsolidationAccounttype() == ConsolidationAccountType.TARJETA_CREDITO && c.getEntity().getCode() == d.getEntity().getCode())
                        .findFirst().ifPresent(c -> {
                    c.setBalanceDE(d.getBalance());
                    c.setRateDE(d.getRate());
                });
            });
        }

        return consolidableDebts;
    }

    @Override
    public Double getPreConsolidationMonthlyInstallment(int loanApplicationId) throws Exception {
        return queryForObjectTrx("select * from credit.get_pre_consolidation_monthly_installment(?)", Double.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public LoanApplicationUpdateParams getLoanApplicationUpdateParams(int loanApplicationId, Locale locale) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from  credit.calculate_application_parameters(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null)
            return null;

        LoanApplicationUpdateParams params = new LoanApplicationUpdateParams();
        params.fillFromDb(dbJson, catalogService, locale);
        return params;
    }

    @Override
    public void updatePercentageProgress(Integer loanApplicationId, double percentage) {
        updateTrx("UPDATE credit.tb_loan_application set percentage_progress = percentage_progress ||  (?) where loan_application_id = ?;",
                new SqlParameterValue(Types.NUMERIC, percentage),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updatePercentageRemoveProgress(Integer loanApplicationId) {
        updateTrx("UPDATE credit.tb_loan_application set percentage_progress = array_remove(percentage_progress, (percentage_progress)[array_length(percentage_progress,1)]) where loan_application_id = ?;", new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateConsentNavGeolocation(int loanApplicationId, Boolean consentNavGeolocation) throws Exception {
        updateTrx("UPDATE credit.tb_loan_application set consent_nav_geolocation = ? where loan_application_id = ?",
                new SqlParameterValue(Types.BOOLEAN, consentNavGeolocation),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public List<RecognitionResultsPainter> getRecognitionResults(int personId, Locale locale) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.get_amazon_rekognition_process(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null || !dbJson.has("loan_applications")) {
            return null;
        }

        List<RecognitionResultsPainter> results = new ArrayList<>();
        for (int i = 0; i < dbJson.getJSONArray("loan_applications").length(); i++) {
            RecognitionResultsPainter result = new RecognitionResultsPainter();
            result.fillFromDb(dbJson.getJSONArray("loan_applications").getJSONObject(i), catalogService, locale);
            results.add(result);
        }
        return results;
    }

    @Override
    public void registerAmazonRekognition(Integer loanAplicationId, Double highSimilarity, String jsonSimilarities, Integer userFilesDNIIdA, Integer userFilesDNIIdB, Integer userFilesDNIIdMerged, Integer userFilesSelfieId) {
        queryForObjectTrx("select * from credit.register_amazon_rekognition_result(?, ?, ?::json, ?, ?, ?, ?)",
                String.class,
                new SqlParameterValue(Types.INTEGER, loanAplicationId),
                new SqlParameterValue(Types.NUMERIC, highSimilarity),
                new SqlParameterValue(Types.OTHER, jsonSimilarities),
                new SqlParameterValue(Types.INTEGER, userFilesDNIIdA),
                new SqlParameterValue(Types.INTEGER, userFilesDNIIdB),
                new SqlParameterValue(Types.INTEGER, userFilesDNIIdMerged),
                new SqlParameterValue(Types.INTEGER, userFilesSelfieId));
    }

    @Override
    public void registerAmazonRekognitionFacesLabels(Integer loanAplicationId, String jsonFaces, String jsonLabels) {
        queryForObjectTrx("select * from credit.register_amazon_rekognition_faces_labels_result(?, ?::json, ?::json)",
                String.class,
                new SqlParameterValue(Types.INTEGER, loanAplicationId),
                new SqlParameterValue(Types.OTHER, jsonFaces),
                new SqlParameterValue(Types.OTHER, jsonLabels));
    }


    @Override
    public void updateRegisterType(int loanApplicationId, int registerType) throws Exception {
        updateTrx("UPDATE credit.tb_loan_application set register_type_id = ? where loan_application_id = ?",
                new SqlParameterValue(Types.INTEGER, registerType),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public SalaryAdvanceCalculatedAmount calculateAgreementAmmount(int employeeId, int employerId, Locale locale) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.calculate_agreement_ammount(?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, employerId),
                new SqlParameterValue(Types.INTEGER, employeeId));
        if (dbJson == null)
            return null;

        SalaryAdvanceCalculatedAmount advance = new SalaryAdvanceCalculatedAmount();
        advance.fillFromDb(dbJson, catalogService, locale);
        return advance;
    }

    @Override
    public Double getConsolidationSavings(int loanApplicationId) throws Exception {
        return queryForObjectTrx("select * from credit.get_consolidation_savings(?)", Double.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateCurrentQuestion(int loanApplicationId, int questionId) {
        updateTrx("UPDATE credit.tb_loan_application set current_process_question_id = ? where loan_application_id = ?",
                new SqlParameterValue(Types.INTEGER, questionId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateQuestionSequence(int loanApplicationId, String sequence) {
        updateTrx("UPDATE credit.tb_loan_application set proccess_question_id_sequence = ? where loan_application_id = ?",
                new SqlParameterValue(Types.OTHER, sequence),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updatePerson(int loanApplicationId, int personId, int userId) {
        updateTrx("UPDATE credit.tb_loan_application set user_id = ?, person_id = ? where loan_application_id = ?",
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateReason(int loanApplicationId, Integer reasonId) {
        updateTrx("UPDATE credit.tb_loan_application set reason_id = ? where loan_application_id = ?",
                new SqlParameterValue(Types.INTEGER, reasonId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateAmount(int loanApplicationId, Integer amount) {
        updateTrx("UPDATE credit.tb_loan_application set amount = ? where loan_application_id = ?",
                new SqlParameterValue(Types.DOUBLE, amount != null ? amount.doubleValue() : null),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateInstallments(int loanApplicationId, Integer installments) {
        updateTrx("UPDATE credit.tb_loan_application set installments = ? where loan_application_id = ?",
                new SqlParameterValue(Types.INTEGER, installments),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateUsage(int loanApplicationId, Integer usageId) {
        updateTrx("UPDATE credit.tb_loan_application set usage_id = ? where loan_application_id = ?",
                new SqlParameterValue(Types.INTEGER, usageId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateDownPayment(int loanApplicationId, Integer downPayment) {
        updateTrx("UPDATE credit.tb_loan_application set down_payment = ? where loan_application_id = ?",
                new SqlParameterValue(Types.DOUBLE, downPayment != null ? downPayment.doubleValue() : null),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateDownPaymentCurrency(int loanApplicationId, Integer downPaymentCurrencyId) {
        updateTrx("UPDATE credit.tb_loan_application set down_payment_currency_id = ? where loan_application_id = ?",
                new SqlParameterValue(Types.INTEGER, downPaymentCurrencyId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateProductCategory(int loanApplicationId, Integer productCategory) throws Exception {
        updateTrx("UPDATE credit.tb_loan_application set category_id = ? where loan_application_id = ?",
                new SqlParameterValue(Types.INTEGER, productCategory),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateFormAssistant(int loanApplicationId, Integer formAssistant) throws Exception {
        if (formAssistant != null)
            updateTrx("UPDATE credit.tb_loan_application set form_assistant_id = ? where loan_application_id = ?",
                    new SqlParameterValue(Types.INTEGER, formAssistant),
                    new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void registerLoanApplicationReclosure(int loanApplicationId, int entityId, boolean isReclosurable, double previousCreditAmount) throws Exception {
        queryForObjectTrx("select * from credit.register_loan_application_reclosure(?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.BOOLEAN, isReclosurable),
                new SqlParameterValue(Types.NUMERIC, previousCreditAmount));
    }

    @Override
    public void updateLoanApplicationFromSelfEvaluation(int selfEvaluationId, int loanApplicationId) throws Exception {
        queryForObjectTrx("select * from credit.update_loan_application_from_self_evaluation(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, selfEvaluationId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateEntityId(int loanApplicationId, Integer selectedEntityId) throws Exception {
        updateTrx("UPDATE credit.tb_loan_application set entity_id = ? where loan_application_id = ?",
                new SqlParameterValue(Types.INTEGER, selectedEntityId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateProductId(int loanApplicationId, Integer productId) throws Exception {
        updateTrx("UPDATE credit.tb_loan_application set product_id = ? where loan_application_id = ?",
                new SqlParameterValue(Types.INTEGER, productId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void registerEFLSession(int loanApplicacionId, String eflSessionUid) throws Exception {
        queryForObjectTrx("select * from credit.ins_efl_assesstment(?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicacionId),
                new SqlParameterValue(Types.VARCHAR, eflSessionUid));
    }

    @Override
    public void updateEFLSession(int loanApplicacionId, Double score, String jsonResult, String scoreConfidence) throws Exception {
        queryForObjectTrx("select * from credit.upd_efl_assesstment(?,?,?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicacionId),
                new SqlParameterValue(Types.NUMERIC, score),
                new SqlParameterValue(Types.OTHER, jsonResult),
                new SqlParameterValue(Types.VARCHAR, scoreConfidence));
    }

    @Override
    public void executeEFLEvaluation(int loanApplicationId) throws Exception {
        queryForObjectEvaluation("select * from evaluation.evaluation_efl(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateEntityUser(int loanApplicationId, Integer entityUserId) throws Exception {
        updateTrx("UPDATE credit.tb_loan_application set entity_user_id = ? where loan_application_id = ?",
                new SqlParameterValue(Types.INTEGER, entityUserId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateLoanOfferGeneratedStatus(int loanApplicationId, Boolean generatedOffer) throws Exception {
        updateTrx("UPDATE credit.tb_loan_application set pending_analyst_offer = ? where loan_application_id = ?",
                new SqlParameterValue(Types.BOOLEAN, generatedOffer),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateVehicleId(int loanApplicationId, Integer vehicleId) {
        queryForObjectTrx("select * from credit.upd_loan_application_vehicle(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, vehicleId));
    }

    @Override
    public List<LoanApplicationPreliminaryEvaluation> getPreliminaryEvaluations(int loanApplicationId, Locale locale) throws Exception {
        return getPreliminaryEvaluations(loanApplicationId, locale, EVALUATION_DB);
    }

    @Override
    public List<LoanApplicationPreliminaryEvaluation> getPreliminaryEvaluations(int loanApplicationId, Locale locale, int database) throws Exception {
        JSONArray dbArray = queryForObject("select * from evaluation.get_preliminary_evaluations(?)", JSONArray.class,  database,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<LoanApplicationPreliminaryEvaluation> evaluations = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            LoanApplicationPreliminaryEvaluation evaluation = new LoanApplicationPreliminaryEvaluation();
            evaluation.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            evaluations.add(evaluation);
        }
        return evaluations;
    }

    @Override
    public void updateEntityApplicationExpirationDate(int loanApplicationId, Date entityApplicationExpirationDate) {
        updateTrx("UPDATE credit.tb_loan_application set entity_application_expiration_date = ? where loan_application_id = ?",
                new SqlParameterValue(Types.DATE, entityApplicationExpirationDate),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateEntityApplicationCode(int loanApplicationId, String entityApplicationCode) {
        updateTrx("UPDATE credit.tb_loan_application set entity_application_code = ? where loan_application_id = ?",
                new SqlParameterValue(Types.VARCHAR, entityApplicationCode),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public Integer insertLoanOffer(int loanApplicationId, int personId, LoanOffer loanOffer, Integer entityProductParameterId) {
        return queryForObjectTrx("select * from credit.ins_loan_offer(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Integer.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.NUMERIC, loanOffer.getAmmount()),
                new SqlParameterValue(Types.INTEGER, loanOffer.getInstallments()),
                new SqlParameterValue(Types.NUMERIC, loanOffer.getInstallmentAmmount()),
                new SqlParameterValue(Types.NUMERIC, loanOffer.getEffectiveAnualRate()),
                new SqlParameterValue(Types.NUMERIC, loanOffer.getEffectiveDailyRate()),
                new SqlParameterValue(Types.NUMERIC, loanOffer.getMonthlyRate()),
                new SqlParameterValue(Types.NUMERIC, loanOffer.getLoanCommission()),
                new SqlParameterValue(Types.NUMERIC, loanOffer.getMinAmmount()),
                new SqlParameterValue(Types.NUMERIC, loanOffer.getMaxAmmount()),
                new SqlParameterValue(Types.INTEGER, loanOffer.getMaxInstallments()),
                new SqlParameterValue(Types.INTEGER, loanOffer.getRccCodMes()),
                new SqlParameterValue(Types.NUMERIC, loanOffer.getCommission()),
                new SqlParameterValue(Types.NUMERIC, loanOffer.getCommissionIgv()),
                new SqlParameterValue(Types.NUMERIC, loanOffer.getTotalCommission()),
                new SqlParameterValue(Types.INTEGER, loanOffer.getEntityId()),
                new SqlParameterValue(Types.INTEGER, loanOffer.getLoanOfferOrder()),
                new SqlParameterValue(Types.NUMERIC, null),
                new SqlParameterValue(Types.NUMERIC, loanOffer.getCommission2()),
                new SqlParameterValue(Types.NUMERIC, loanOffer.getMoratoriumRate()),
                new SqlParameterValue(Types.CHAR, null),
                new SqlParameterValue(Types.NUMERIC, null),
                new SqlParameterValue(Types.INTEGER, loanOffer.getProduct() != null ? loanOffer.getProduct().getId() : null),
                new SqlParameterValue(Types.INTEGER, null),
                new SqlParameterValue(Types.INTEGER, loanOffer.getEntityScore()),
                new SqlParameterValue(Types.NUMERIC, loanOffer.getDownPayment()),
                new SqlParameterValue(Types.INTEGER, loanOffer.getDownPaymentCurrency() != null ? loanOffer.getDownPaymentCurrency().getId() : null),
                new SqlParameterValue(Types.NUMERIC, loanOffer.getEffectiveAnnualCostRate()),
                new SqlParameterValue(Types.CHAR, null),
                new SqlParameterValue(Types.NUMERIC, null),
                new SqlParameterValue(Types.INTEGER, entityProductParameterId),
                new SqlParameterValue(Types.OTHER, loanOffer.getEntityCustomData().toString())
                );
    }

    @Override
    public void updateLoanOfferEntityCustomData(int loanOfferId, JSONObject jsonObject) {
        queryForObjectTrx("select * from credit.update_loan_offer_js_entity_custom_data(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanOfferId),
                new SqlParameterValue(Types.OTHER, jsonObject.toString())
        );
    }

    @Override
    public void insertLoanOfferSchedule(int loanOfferId, String jsonSchedule) {
        queryForObjectTrx("select * from credit.ins_entity_offer_schedule(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanOfferId),
                new SqlParameterValue(Types.OTHER, jsonSchedule));
    }

    @Override
    public void updateOffersQueryBotId(int loanApplicationId, Integer offersQueryBotId) {
        updateTrx("UPDATE credit.tb_loan_application set offers_bot_id = ? where loan_application_id = ?",
                new SqlParameterValue(Types.INTEGER, offersQueryBotId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateEvaluationStep(int evaluationId, int policyId, Date expirationDate) {
        queryForObjectEvaluation("select * from evaluation.update_evaluation_step(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, evaluationId),
                new SqlParameterValue(Types.INTEGER, policyId),
                new SqlParameterValue(Types.DATE, expirationDate));
    }

    @Override
    public void updatePreliminaryEvaluationStep(int preliminaryEvaluationId, int hardFilterId, String hardFilterMessage, Integer hardFilterHelpMessage, Date expirationDate) {
        queryForObjectEvaluation("select * from evaluation.update_preliminary_evaluation_step(?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, preliminaryEvaluationId),
                new SqlParameterValue(Types.INTEGER, hardFilterId),
                new SqlParameterValue(Types.VARCHAR, hardFilterMessage),
                new SqlParameterValue(Types.INTEGER, hardFilterHelpMessage),
                new SqlParameterValue(Types.DATE, expirationDate));
    }

    @Override
    public List<LoanApplicationEvaluation> getEvaluations(int loanApplicationId, Locale locale) throws Exception {
        return getEvaluations(loanApplicationId, locale, EVALUATION_DB);
    }

    @Override
    public List<LoanApplicationEvaluation> getEvaluations(int loanApplicationId, Locale locale, int database) throws Exception {
        JSONArray dbArray = queryForObject("select * from evaluation.get_evaluations(?)", JSONArray.class, database,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<LoanApplicationEvaluation> evaluations = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            LoanApplicationEvaluation evaluation = new LoanApplicationEvaluation();
            evaluation.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            evaluations.add(evaluation);
        }
        return evaluations;
    }

    @Override
    public LoanApplication getLoanApplicationByEntityApplicationCode(String entityApplicationCode, Locale locale) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.get_loan_application_by_entity_application_code(?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, entityApplicationCode));
        if (dbJson == null) {
            return null;
        }

        LoanApplication loanApplication = new LoanApplication();
        loanApplication.fillFromDb(dbJson, catalogService, locale);
        return loanApplication;
    }

    @Override
    public List<UserFile> getLoanApplicationUserFiles(Integer loanApplicationId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from users.bo_get_user_files_by_application(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return null;
        }

        LoanApplicationUserFiles userFiles = new LoanApplicationUserFiles();
        userFiles.fillFromDb(dbJson, catalogService);

        return userFiles.getUserFileList();
    }

    @Override
    public void registerTrackingAction(Integer loanApplicationId,
                                       Integer userId,
                                       Integer actionId,
                                       Integer detail,
                                       Date scheduleDate) throws Exception {
        queryForObjectTrx("select * from credit.register_application_tracking_action(?,?,?,?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.INTEGER, actionId),
                new SqlParameterValue(Types.INTEGER, detail),
                new SqlParameterValue(Types.TIMESTAMP, scheduleDate));

    }

    @Override
    public void registerTrackingActionContactPerson(Integer loanApplicationId, Integer userId, Integer actionId, Integer detail, Date scheduleDate, Boolean personAnswerCall, Integer contactRelationship) throws Exception {
        registerTrackingActionContactPerson(loanApplicationId,userId,actionId,detail,scheduleDate,personAnswerCall,contactRelationship,null, null,null);
    }

    @Override
    public void registerTrackingActionContactPerson(Integer loanApplicationId, Integer userId, Integer actionId, Integer detail, Date scheduleDate, Boolean personAnswerCall, Integer contactRelationship, Integer entityUserId,Integer referralId,Integer userFileId) throws Exception {
        queryForObjectTrx("select * from credit.register_application_tracking_action(?,?,?,?,?,?,?,?,?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.INTEGER, actionId),
                new SqlParameterValue(Types.INTEGER, detail),
                new SqlParameterValue(Types.TIMESTAMP, scheduleDate),
                new SqlParameterValue(Types.BOOLEAN, personAnswerCall),
                new SqlParameterValue(Types.INTEGER, contactRelationship),
                new SqlParameterValue(Types.INTEGER, entityUserId),
                new SqlParameterValue(Types.INTEGER, referralId),
                new SqlParameterValue(Types.INTEGER, userFileId)
                );
    }

    @Override
    public List<LoanApplicationTrackingAction> getLoanApplicationTrackingActions(int loanApplicationId) throws Exception {
        JSONArray jsonArray = queryForObjectTrx("select * from credit.get_tracking_actions_by_loan_application(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));

        if (jsonArray == null)
            return null;

        List<LoanApplicationTrackingAction> loanApplicationTrackingActions = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); ++i) {
            LoanApplicationTrackingAction loanApplicationTrackingAction = new LoanApplicationTrackingAction();
            loanApplicationTrackingAction.fillFromDb(jsonArray.getJSONObject(i), catalogService);
            loanApplicationTrackingActions.add(loanApplicationTrackingAction);
        }

        return loanApplicationTrackingActions;
    }

    @Override
    public void reportMissingDocumentation(Integer loanApplicationId) throws Exception {
        queryForObjectTrx("select * from credit.report_missing_documentation(?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    /**
     * This method return if the efl question should show and if can be updated
     *
     * @param loanApplicationId
     * @return Pair with Key (if should be shown) and value (if could be skipped)
     * @throws Exception
     */
    @Override
    public Pair<Boolean, Boolean> getEflQuestionConfiguration(Integer loanApplicationId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.pre_select_entity_product(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return null;
        }

        return Pair.of(
                JsonUtil.getBooleanFromJson(dbJson, "show_efl_questions", null),
                JsonUtil.getBooleanFromJson(dbJson, "efl_is_skippable", null));

    }

    @Override
    public void updateLoanApplicationChangeRate(Integer loanApplicationId, Double changeRate, Integer entityId) throws Exception {
        queryForObjectTrx("select * from credit.upd_exchange_rate_offer(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.NUMERIC, changeRate),
                new SqlParameterValue(Types.INTEGER, entityId));

    }

    public LoanApplicationEvaluationsProcess getLoanApplicationEvaluationsProcess(Integer loanApplicationId) {
        JSONObject dbJson = queryForObjectEvaluation("select * from evaluation.get_application_evaluations(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return null;
        }

        LoanApplicationEvaluationsProcess process = new LoanApplicationEvaluationsProcess();
        process.fillFromDb(dbJson);
        return process;
    }

    @Override
    public void updateLoanApplicationEvaluationProcessEvaluationBot(int loanApplicationId, List<Integer> botIds) {
        update("UPDATE evaluation.tb_application_evaluations set js_evaluation_bots = ? where loan_application_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.OTHER, new Gson().toJson(botIds)),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateLoanApplicationEvaluationProcessEvaluationStatus(int loanApplicationId, Character status) {
        update("UPDATE evaluation.tb_application_evaluations set evaluation_status = ? where loan_application_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.VARCHAR, status),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateLoanApplicationEvaluationProcessPreEvaluationStatus(int loanApplicationId, Character status) {
        update("UPDATE evaluation.tb_application_evaluations set pre_evaluation_status = ? where loan_application_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.VARCHAR, status),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateSourceMediumCampaign(Integer loanApplicationId, String source, String medium, String campaign) throws Exception {
        queryForObjectTrx("select * from credit.update_source_medium_campaign(?, ?, ?, ?);", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.VARCHAR, source),
                new SqlParameterValue(Types.VARCHAR, medium),
                new SqlParameterValue(Types.VARCHAR, campaign));
    }

    @Override
    public void updateTermContent(Integer loanApplicationId, String term, String content) throws Exception {
        queryForObjectTrx("select * from credit.update_term_content(?, ?, ?);", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.VARCHAR, term),
                new SqlParameterValue(Types.VARCHAR, content));
    }

    @Override
    public List<ApplicationBureau> getBureauResults(Integer loanApplicationId) throws Exception {
        JSONArray dbResult = queryForObjectTrx("select * from credit.get_loan_application_bureu(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbResult == null)
            return new ArrayList<>();

        List<ApplicationBureau> bureaus = new ArrayList<>();
        for (int i = 0; i < dbResult.length(); i++) {
            ApplicationBureau bureau = new ApplicationBureau();
            bureau.fillFromDb(dbResult.getJSONObject(i));
            bureaus.add(bureau);
        }
        return bureaus;
    }

    @Override
    public List<ExperianResult> getExperianResultList(Integer loanApplicationId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_entity_ws_bureau(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbArray == null)
            return null;

        List<ExperianResult> experianResultList = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ExperianResult experianResult = new ExperianResult();
            experianResult.fillFromDb(dbArray.getJSONObject(i), catalogService, Configuration.getDefaultLocale());
            experianResultList.add(experianResult);
        }
        return experianResultList;
    }

    @Override
    public boolean entityWsHasResult(Integer loanApplicationId, Integer entityId) throws Exception {
        String dbResult = queryForObjectTrx("select * from credit.get_entity_ws_result(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityId));

        return dbResult != null;
    }

    @Override
    public void updateGAClientID(Integer loanApplicationId, String gaClientID) {
        queryForObjectTrx("select * from credit.upd_loan_application_ga_client_id(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.VARCHAR, gaClientID));
    }

    @Override
    public void updateUserAgent(Integer loanApplicationId, String userAgent) {
        queryForObjectTrx("select * from credit.update_loan_application_user_agent(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.VARCHAR, userAgent));
    }

    @Override
    public void updateLoanApplicationFilesUploaded(Integer loanApplicationId, boolean isUploaded) {
        queryForObjectTrx("select * from credit.upd_files_uploaded(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.BOOLEAN, isUploaded));
    }

    @Override
    public void updateEvaluationProcessReadyPreEvaluation(int loanApplicationId, boolean ready) {
        update("UPDATE evaluation.tb_application_evaluations set ready_for_pre_evaluation = ? where loan_application_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.BOOLEAN, ready),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateEvaluationProcessReadyEvaluation(int loanApplicationId, boolean ready) {
        update("UPDATE evaluation.tb_application_evaluations set ready_for_evaluation = ? where loan_application_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.BOOLEAN, ready),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public JSONArray getAdwordsConversions(Date from, Date to) {
        JSONArray dbJson = queryForObjectTrx("select * from credit.get_adwords_conversions(?, ?)", JSONArray.class,
                new SqlParameterValue(Types.DATE, from),
                new SqlParameterValue(Types.DATE, to));

        return dbJson;
    }

    @Override
    public void updateGoogleClickId(int loanApplicationId, String gclid) {
        updateTrx("UPDATE credit.tb_loan_application set gclid = ? where loan_application_id = ?;",
                new SqlParameterValue(Types.VARCHAR, gclid),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public EntityCustomParamConfig getEntityCustomParamsConfig(int loanApplicationId) {
        JSONObject dbJson = queryForObjectTrx("select * from credit.ask_for_custom_params(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null)
            return null;

        EntityCustomParamConfig config = new EntityCustomParamConfig();
        config.fillFromDb(dbJson);
        return config;
    }

    @Override
    public void updateEvaluationProcessSendDelayedEmail(int loanApplicationId, Boolean sendDelayedEmail) {
        update("UPDATE evaluation.tb_application_evaluations set send_delayed_email = ? where loan_application_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.BOOLEAN, sendDelayedEmail),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateEntityCustomData(int loanApplicationId, JSONObject entityCustomData) {
        updateTrx("UPDATE credit.tb_loan_application set entity_custom_data = ? where loan_application_id = ?",
                new SqlParameterValue(Types.OTHER, entityCustomData != null ? entityCustomData.toString() : null),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void generateSynthesized(String documentNumber) {
        queryForObject("select * from sysrcc.generate_synthesized_process(?)", String.class,
                REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentNumber));
    }

    @Override
    public void updateEvaluationProcessSynthesizedStatus(int loanApplicationId, Character status) {
        update("UPDATE evaluation.tb_application_evaluations set synthesized_status = ? where loan_application_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.CHAR, status),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void registerAssistedProcessSchedule(int loanApplicationId, Date scheduledDate) {
        queryForObjectTrx("select * from credit.register_assisted_process_schedule(?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.TIMESTAMP, scheduledDate));
    }

    @Override
    public Integer registerApplicationBureauLog(int bureauId, Integer loanApplicationId, Date startDate, Date finishDate, char status, String request, String response) {
        return queryForObjectTrx("select * from originator.register_lg_application_bureau(?, ?, ?, ?, ?, ?, ?)", Integer.class,
                new SqlParameterValue(Types.INTEGER, bureauId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.TIMESTAMP, startDate),
                new SqlParameterValue(Types.TIMESTAMP, finishDate),
                new SqlParameterValue(Types.CHAR, status),
                new SqlParameterValue(Types.VARCHAR, request),
                new SqlParameterValue(Types.VARCHAR, response));
    }

    @Override
    public void updateApplicationBureauLogResponse(int bureauId, String response) {
        updateTrx("UPDATE originator.lg_application_bureau SET response = ? WHERE application_bureau_id = ?;",
                new SqlParameterValue(Types.VARCHAR, response),
                new SqlParameterValue(Types.INTEGER, bureauId));
    }

    @Override
    public void updateApplicationBureauLogRequest(int bureauId, String request) {
        updateTrx("UPDATE originator.lg_application_bureau SET request = ? WHERE application_bureau_id = ?;",
                new SqlParameterValue(Types.VARCHAR, request),
                new SqlParameterValue(Types.INTEGER, bureauId));
    }

    @Override
    public void updateApplicationBureauLogFinishDate(int bureauId, Date finishDate) {
        updateTrx("UPDATE originator.lg_application_bureau SET finish_date = ? WHERE application_bureau_id = ?;",
                new SqlParameterValue(Types.TIMESTAMP, finishDate),
                new SqlParameterValue(Types.INTEGER, bureauId));
    }

    @Override
    public void updateApplicationBureauLogStatus(int bureaId, char status) {
        updateTrx("UPDATE originator.lg_application_bureau SET status = ? WHERE application_bureau_id = ?;",
                new SqlParameterValue(Types.CHAR, status),
                new SqlParameterValue(Types.INTEGER, bureaId));
    }

    @Override
    public void updateApplicationOfferRejection(int loanApplicationId, Integer offerRejectionReasonId) {
        queryForObjectTrx("select * from credit.upd_loan_application_offer_rejection_reason(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, offerRejectionReasonId));
    }

    @Override
    public List<LoanOffer> getAllLoanOffers(int loanApplicationId) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from credit.get_all_loan_offers(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return null;
        }

        List<LoanOffer> offers = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            LoanOffer offer = new LoanOffer();
            offer.fillFromDb(dbJson.getJSONObject(i), catalogService);
            offers.add(offer);
        }
        return offers;
    }

    @Override
    public List<LoanOffer> getComparableLoanOffers(int loanApplicationId) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from credit.get_comparable_installment_amount(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return null;
        }

        List<LoanOffer> offers = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            LoanOffer offer = new LoanOffer();
            offer.fillFromDb(dbJson.getJSONObject(i), catalogService);
            offers.add(offer);
        }
        return offers;
    }

    @Override
    public List<EntityProductEvaluationsProcess> getEntityProductEvaluationProceses(int loanApplicationId) {
        JSONArray dbJson = queryForObjectEvaluation("select * from evaluation.get_entity_product_evaluation(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<EntityProductEvaluationsProcess> evaluations = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            EntityProductEvaluationsProcess entityProductEva = new EntityProductEvaluationsProcess();
            entityProductEva.fillFromDb(dbJson.getJSONObject(i));
            evaluations.add(entityProductEva);
        }
        return evaluations;
    }

    @Override
    public void updateLoanApplicationPreliminaryEvaluationStatus(Integer loanApplicationId, Integer entityId, Integer productId, char status) {
        update("UPDATE evaluation.tb_preliminary_evaluation SET status = ? WHERE loan_application_id = ? and entity_id = ? and product_id = ?;",
                EVALUATION_DB,
                new SqlParameterValue(Types.CHAR, status),
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId));
    }

    @Override
    public void updateLoanApplicationEvaluationStatus(Integer loanApplicationId, Integer entityId, Integer productId, char status) {
        update("UPDATE evaluation.tb_evaluation SET status = ? WHERE loan_application_id = ? and entity_id = ? and product_id = ?;",
                EVALUATION_DB,
                new SqlParameterValue(Types.CHAR, status),
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId));
    }

    @Override
    public void registerGuaranteedVehicle(Integer loanApplicationId, Integer guaranteedVehicleId, String plate) {
        queryForObjectTrx("select * from credit.register_loan_application_guaranteed_vehicle(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, guaranteedVehicleId),
                new SqlParameterValue(Types.VARCHAR, plate));
    }

    @Override
    public void updateLoanApplicationEvaluationStartDate(int loanApplicationId, Date date) {
        update("UPDATE evaluation.tb_application_evaluations set evaluation_start_date = ? where loan_application_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.TIMESTAMP, date),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public List<AppointmentSchedule> getAvailableDates() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_schedules_availables_for_appointment()", JSONArray.class);
        if (dbArray == null)
            return new ArrayList<>();

        List<AppointmentSchedule> availableDates = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); ++i) {
            AppointmentSchedule availableDate = new AppointmentSchedule();
            availableDate.fillFromDb(dbArray.getJSONObject(i));
            availableDates.add(availableDate);
        }
        return availableDates;
    }

    @Override
    public String registerAppointmentSchedule(int loanApplicationId, Date dateSelected, int appointmentScheduleId) {
        JSONObject dbJson = queryForObjectTrx("select * from credit.register_appointment_schedule(?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.DATE, dateSelected),
                new SqlParameterValue(Types.INTEGER, appointmentScheduleId));

        String message = JsonUtil.getStringFromJson(dbJson, "message", null);

        return message;
    }

    @Override
    public void registerAppointmentSchedule(int loanApplicationId, Date dateSelected, int appointmentScheduleId, String appointmentPlace) {
        updateTrx("UPDATE credit.tb_loan_application_guaranteed_vehicle SET appointment_date=?, appointment_schedule_id=?, appointment_place=?, active_appointment=true WHERE loan_application_id=?",
                new SqlParameterValue(Types.TIMESTAMP, dateSelected),
                new SqlParameterValue(Types.INTEGER, appointmentScheduleId),
                new SqlParameterValue(Types.VARCHAR, appointmentPlace),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public List<ReferredLead> getReferredLeadByLoanApplicationId(int loanApplicationId) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from credit.get_referred_lead_by_loan_application(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null)
            return null;

        List<ReferredLead> referredLeads = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            ReferredLead referredLead = new ReferredLead();
            referredLead.fillFromDb(dbJson.getJSONObject(i), catalogService);
            referredLeads.add(referredLead);
        }
        return referredLeads;
    }

    @Override
    public void registerReferredLead(int loanApplicationId, int entityId) {
        queryForObjectTrx("select * from credit.register_referred_lead(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityId));
    }

    @Override
    public void updateSmsSent(int loanApplicationId) {
        updateTrx("UPDATE credit.tb_loan_application set sms_sent = ? where loan_application_id = ?",
                new SqlParameterValue(Types.BOOLEAN, true),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateSmsSent(int loanApplicationId, Boolean sent) {
        updateTrx("update credit.tb_loan_application set sms_sent = ? where loan_application_id = ?",
                new SqlParameterValue(Types.BOOLEAN, sent),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public RescueScreenParams getRescueScreenParams(int loanApplicationId) {
        JSONObject dbJson = queryForObjectTrx("select * from credit.show_rescue_screen(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null)
            return null;

        RescueScreenParams params = new RescueScreenParams();
        params.fillFromDb(dbJson);
        return params;
    }

    @Override
    public HashMap<String, HashMap<String, List>> getUtmParameters() {
        JSONArray jsonArray = queryForObjectTrx("select * from support.get_utm_parameters();", JSONArray.class);
        if (jsonArray == null) {
            return null;
        }

        HashMap<String, HashMap<String, List>> utmParameters = new HashMap<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            String utmSource = JsonUtil.getStringFromJson(jsonArray.getJSONObject(i), "source", null);
            JSONArray mediumArray = JsonUtil.getJsonArrayFromJson(jsonArray.getJSONObject(i), "mediums", null);
            HashMap<String, List> medium = new HashMap<>();
            for (int j = 0; j < mediumArray.length(); ++j) {
                String utmMedium = JsonUtil.getStringFromJson(mediumArray.getJSONObject(j), "medium", null);
                JSONArray campaignArray = JsonUtil.getJsonArrayFromJson(mediumArray.getJSONObject(j), "campaigns", null);
                List<String> campaignsList = new ArrayList<>();
                for (int k = 0; k < campaignArray.length(); ++k) {
                    try {
                        campaignsList.add(campaignArray.optString(k, null));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw ex;
                    }
                }
                medium.put(utmMedium, campaignsList);
            }
            utmParameters.put(utmSource, medium);
        }

        return utmParameters;
    }

    @Override
    public List<PreApprovedInfo> getApprovedLoanApplication(Integer loanApplicationId, Locale locale) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from originator.get_approved_data_loan_application(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<PreApprovedInfo> preApprovedInfos = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            PreApprovedInfo preApprovedInfo = new PreApprovedInfo();
            preApprovedInfo.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            preApprovedInfos.add(preApprovedInfo);
        }
        return preApprovedInfos;
    }

    @Override
    public <T extends LoanApplication> List<T> getLoanApplicationByEntityUser(int entityUserId, Class<T> returntype) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from credit.get_loan_applications_entity_user(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityUserId));
        if (dbJson == null)
            return null;

        List<T> applications = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            T loan = returntype.getConstructor().newInstance();
            loan.fillFromDb(dbJson.getJSONObject(i), catalogService, Configuration.getDefaultLocale());
            applications.add(loan);
        }
        return applications;
    }

    @Override
    public Integer getMaxPreapprovedAmount(Integer loanApplicationId) throws Exception {
        return queryForObjectTrx("select * from credit.get_max_pre_approved_amount(?)", Integer.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void approvedDataLoanApplication(Boolean approved, Integer entityId, Integer loanApplicationId, Integer entityProductParameterId) throws Exception {
        updateTrx("UPDATE originator.tb_approved_data_loan_application set allowed_process = ? where entity_id = ? and loan_application_id = ? and ? = ANY (ar_entity_product_parameter_id);",
                new SqlParameterValue(Types.BOOLEAN, approved),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityProductParameterId));
    }

    @Override
    public void updateDisableTracking(int loanApplicationId, boolean disabletracking) {
        updateTrx("UPDATE credit.tb_loan_application set disable_tracking = ? where loan_application_id = ?",
                new SqlParameterValue(Types.BOOLEAN, disabletracking),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateQuestionFlow(int loanApplicationId, Character questionflow) {
        updateTrx("UPDATE credit.tb_loan_application set question_flow = ? where loan_application_id = ?",
                new SqlParameterValue(Types.CHAR, questionflow),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateLeadParams(Integer loanApplicationId, JSONObject jsonParams) {
        updateTrx("UPDATE credit.tb_loan_application set js_lead_params = ? where loan_application_id = ?",
                new SqlParameterValue(Types.OTHER, jsonParams != null ? jsonParams.toString() : null),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public List<LeadLoanApplication> getLeadLoanApplicationsByEntityAndDate(Integer entityId, Date date) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from lead.get_approved_leads(?,?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.DATE, date)
        );
        if (dbJson == null)
            return null;

        List<LeadLoanApplication> leadLoanApplications = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            LeadLoanApplication leadLoanApplication = new LeadLoanApplication();
            leadLoanApplication.fillFromDb(dbJson.getJSONObject(i), catalogService, Configuration.getDefaultLocale());
            leadLoanApplications.add(leadLoanApplication);
        }
        return leadLoanApplications;
    }

    @Override
    public List<LeadLoanApplication> getApprovedLeadLoanApplications(Integer entityId) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from lead.get_approved_leads(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId)
        );
        if (dbJson == null)
            return Collections.emptyList();

        List<LeadLoanApplication> leadLoanApplications = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            LeadLoanApplication leadLoanApplication = new LeadLoanApplication();
            leadLoanApplication.fillFromDb(dbJson.getJSONObject(i), catalogService, Configuration.getDefaultLocale());
            leadLoanApplications.add(leadLoanApplication);
        }
        return leadLoanApplications;
    }

    @Override
    public LeadLoanApplication getLeadLoanApplicationByLoanApplicationId(Integer loanApplicationId) throws Exception {
        JSONObject jsonObject = queryForObjectTrx("select * from credit.get_loan_application_lead(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (jsonObject == null) {
            return null;
        }

        LeadLoanApplication leadLoanApplication = new LeadLoanApplication();
        leadLoanApplication.fillFromDb(jsonObject, catalogService, Configuration.getDefaultLocale());
        return leadLoanApplication;
    }

    @Override
    public void registerLeadsLoanApplication(Integer loanApplicationId, Integer prodId, Integer activityId) {
        queryForObjectTrx("select * from lead.register_loan_application_lead(?,?,?);", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, prodId),
                new SqlParameterValue(Types.INTEGER, activityId));
    }

    @Override
    public void registerLoanApplicationComment(Integer loanApplicationId, String comment, Integer sysUserId, String commentType) {
        registerLoanApplicationComment(loanApplicationId, comment, sysUserId, commentType, null);
    }

    @Override
    public void registerLoanApplicationComment(Integer loanApplicationId, String comment, Integer sysUserId, String commentType, Integer entityUserId) {
        queryForObjectTrx("select * from credit.register_loan_application_comment(?, ?, ?, ?, ?);", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.VARCHAR, comment),
                new SqlParameterValue(Types.INTEGER, sysUserId),
                new SqlParameterValue(Types.CHAR, commentType),
                new SqlParameterValue(Types.INTEGER, entityUserId)
                );
    }

    @Override
    public void updateLoanApplicationComment(Integer loanApplicationId, String comment, Integer sysUserId, String commentType) {
        updateLoanApplicationComment(loanApplicationId, comment, sysUserId, commentType, null);
    }

    @Override
    public void updateLoanApplicationComment(Integer loanApplicationId, String comment, Integer sysUserId, String commentType, Integer entityUserId) {
        queryForObjectTrx("select * from credit.update_loan_application_comment(?, ?, ?, ?, ?);", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.VARCHAR, comment),
                new SqlParameterValue(Types.INTEGER, sysUserId),
                new SqlParameterValue(Types.CHAR, commentType),
                new SqlParameterValue(Types.INTEGER, entityUserId)
        );
    }

    @Override
    public void registerLoanApplicationLiftComment(Integer loanApplicationId) {
        updateTrx("UPDATE credit.tb_loan_application set is_observed = false where loan_application_id = ?",
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateHardFilterMessage(int loanApplicationId, String hardFilterMessage) throws Exception {
        updateTrx("UPDATE credit.tb_loan_application set hard_filter_message = ? where loan_application_id = ?",
                new SqlParameterValue(Types.VARCHAR, hardFilterMessage),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateExpirationDate(int loanApplicationId, Date expirationDate) throws Exception {
        updateTrx("UPDATE credit.tb_loan_application set expiration_date = ? where loan_application_id = ?",
                new SqlParameterValue(Types.TIMESTAMP, expirationDate),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void saveLastActiveOneSignalPlayerId(int loanApplicationId, JSONObject notificationsTokens) throws Exception {
        updateTrx("UPDATE credit.tb_loan_application set notification_tokens = ?::JSON where loan_application_id = ?",
                new SqlParameterValue(Types.VARCHAR, notificationsTokens),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateLoanOfferSignaturePinValidation(int loanApplicationId, Boolean pinvalidation) throws Exception {
        updateTrx("UPDATE credit.tb_loan_offer set signature_pin_validated = ? where loan_application_id = ?",
                new SqlParameterValue(Types.BOOLEAN, pinvalidation),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public JSONObject validateSignaturePin(int loanOfferId, String pin) throws Exception {
        return queryForObjectTrx("select * from credit.validate_signature_pin(?,?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanOfferId),
                new SqlParameterValue(Types.VARCHAR, pin)
        );
    }

    @Override
    public void updateSelectedEntityId(int loanApplicationId, Integer selectedEntityId) throws Exception {
        updateTrx("UPDATE credit.tb_loan_application set selected_entity_id = ? where loan_application_id = ?",
                new SqlParameterValue(Types.INTEGER, selectedEntityId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateSelectedProductId(int loanApplicationId, Integer selectedProductId) throws Exception {
        updateTrx("UPDATE credit.tb_loan_application set selected_product_id = ? where loan_application_id = ?",
                new SqlParameterValue(Types.INTEGER, selectedProductId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateSelectedEntityProductParamId(int loanApplicationId, Integer selectedEntityProductParamId) throws Exception {
        updateTrx("UPDATE credit.tb_loan_application set selected_entity_product_parameter_id = ? where loan_application_id = ?",
                new SqlParameterValue(Types.INTEGER, selectedEntityProductParamId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void selectBestLoanOffer(Integer loanApplicationId) throws Exception {
        queryForObjectTrx("select * from credit.select_best_loan_offer(?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void generateOfferSchedule(Integer loanOfferId) throws Exception {
        updateTrx("select * from credit.generate_offer_schedule(?)",
                new SqlParameterValue(Types.INTEGER, loanOfferId));
    }

    @Override
    public Comment getLoanApplicationComment(Integer loanApplicationId, String commentType) throws Exception {
        JSONObject jsonObject = queryForObjectTrx("select * from credit.get_last_application_comment(?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.CHAR, commentType));
        if (jsonObject == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.fillFromDb(jsonObject);

        return comment;
    }

    @Override
    public List<Comment> getLoanApplicationComments(Integer loanApplicationId, String commentType) throws Exception {
        JSONArray jsonArray = queryForObjectTrx("select * from credit.get_application_comments(?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.CHAR, commentType));
        if (jsonArray == null) {
            return null;
        }

        List<Comment> loanApplicationComments = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Comment loanApplicationComment = new Comment();
            loanApplicationComment.fillFromDb(jsonArray.getJSONObject(i));
            loanApplicationComments.add(loanApplicationComment);
        }
        return loanApplicationComments;
    }

    @Override
    public Integer getBureauExecutedCountByApplication(Integer loanApplicationId, int bureauId) {
        return queryForObjectTrx("select count(*) from originator.lg_application_bureau where loan_application_id =? and bureau_id = ?;", Integer.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, bureauId));
    }

    @Override
    public void updateReferrerPersonId(Integer loanApplicationId, Integer referrerPersonId) {
        updateTrx("UPDATE credit.tb_loan_application SET referrer_person_id = ? WHERE loan_application_id = ?;",
                new SqlParameterValue(Types.INTEGER, referrerPersonId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updatePolicyMessage(int loanApplicationId, String policyMessage) throws Exception {
        updateTrx("UPDATE credit.tb_loan_application set policy_message = ? where loan_application_id = ?",
                new SqlParameterValue(Types.VARCHAR, policyMessage),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateDisposableIncome(int loanApplicationId, double disposableIncome) throws Exception {
        updateTrx("UPDATE credit.tb_loan_application set disposable_income = ? where loan_application_id = ?",
                new SqlParameterValue(Types.NUMERIC, disposableIncome),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateFraudAlertQueryBots(int loanApplicationId, String fraudAlertQueries) {
        updateTrx("UPDATE credit.tb_loan_application set js_fraud_alert_query_id = ? where loan_application_id = ?",
                new SqlParameterValue(Types.OTHER, fraudAlertQueries),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void expireLoanApplication(int loanApplicationId) {
        queryForObjectTrx("select * from credit.expire_loan_application(?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void boResetContract(int loanApplicationId) throws Exception {
        queryForObjectTrx("select * from credit.bo_reset_contract(?)",String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateApprovalQueryBotIds(int loanApplicationId, String approveQueryBotIds) {
        updateTrx("UPDATE credit.tb_loan_application set js_approve_query_id = ? where loan_application_id = ?",
                new SqlParameterValue(Types.OTHER, approveQueryBotIds),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public <T extends LoanApplication> T getLoanApplicationLite(int loanApplicationId, Locale locale, Class<T> returntype) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.get_loan_application_lite(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return null;
        }

        T loanApplication = returntype.getConstructor().newInstance();

        loanApplication.fillFromDb(dbJson, catalogService, locale);
        return loanApplication;
    }

    @Override
    public void reevaluate(String loanApplicationIds) {
        queryForObjectEvaluation("select * from evaluation.clean_evaluations(?)",String.class,
                new SqlParameterValue(Types.OTHER, loanApplicationIds));
    }

    @Override
    public void reactivateApplication(int loanApplicationId, int loanApplicationStatusId) {
        queryForObjectTrx("select * from credit.reactivate_application(?, ?)",String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, loanApplicationStatusId));
    }

    @Override
    public void setFirstDueDateToNull(int loanApplicationId) {
        updateTrx("UPDATE credit.tb_loan_application set first_due_date = null where loan_application_id = ?",
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void generateOfferScheduleAccesoPromo(int loanApplicationId) throws Exception {
        queryForObjectTrx("select * from credit.generate_offer_schedule_acceso_promo(?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateOfferInstallments(int offerId, Integer installments) {
        updateTrx("update credit.tb_loan_offer set installments = ? where loan_offer_id = ?",
                new SqlParameterValue(Types.INTEGER, installments),
                new SqlParameterValue(Types.INTEGER, offerId));
    }

    @Override
    public void updateEntityProductParameterId(int loanApplicationId, int entityProductParameter) throws Exception {
        updateTrx("UPDATE credit.tb_loan_application set entity_product_parameter_id = ? where loan_application_id = ?",
                new SqlParameterValue(Types.INTEGER, entityProductParameter),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateWarmiProcessId(int loanApplicationId, String warmiProcessId) {
        updateTrx("UPDATE credit.tb_loan_application SET warmi_process_id = ? WHERE loan_application_id = ?;",
                new SqlParameterValue(Types.VARCHAR, warmiProcessId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void reevaluateButKeepBureaus(String loanApplicationIds) {
        queryForObjectEvaluation("select * from evaluation.clean_evaluations_keep_bureaus(?)",String.class,
                new SqlParameterValue(Types.OTHER, loanApplicationIds));
    }

    @Override
    public void updateFunnelStep(int loanApplicationId, List<LoanApplicationFunnelStep> steps) {
        updateTrx("UPDATE credit.tb_loan_application set js_funnel_step = ? where loan_application_id = ?",
                new SqlParameterValue(Types.OTHER, steps != null ? new Gson().toJson(steps) : null),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateAuxData(int loanApplicationId, LoanApplicationAuxData auxData) {
        updateTrx("UPDATE credit.tb_loan_application set js_aux_data = ? where loan_application_id = ?",
                new SqlParameterValue(Types.OTHER, auxData != null ? new Gson().toJson(auxData) : null),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateAssignedEntityUserId(int loanApplicationId, Integer entityUserId, Integer asignerEntityUserId) {
        queryForObjectTrx("select credit.assign_loan_entity_user_id(?, ?, ?);", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityUserId),
                new SqlParameterValue(Types.INTEGER, asignerEntityUserId));
    }

    @Override
    public List<LoanApplicationTrackingAction> getLoanApplicationTrackingActionsByTrackingId(int loanApplicationId, Integer[] trackingId, Integer referralId) throws Exception {
        JSONArray jsonArray = queryForObjectTrx("select * from credit.get_application_tracking_action_by_tracking_id(?,?::JSON,?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.OTHER, new Gson().toJson(trackingId)),
                new SqlParameterValue(Types.INTEGER, referralId)
                );

        if (jsonArray == null)
            return null;

        List<LoanApplicationTrackingAction> loanApplicationTrackingActions = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); ++i) {
            LoanApplicationTrackingAction loanApplicationTrackingAction = new LoanApplicationTrackingAction();
            loanApplicationTrackingAction.fillFromDb(jsonArray.getJSONObject(i), catalogService);
            loanApplicationTrackingActions.add(loanApplicationTrackingAction);
        }

        return loanApplicationTrackingActions;
    }

    @Override
    public Boolean hasStatusInLog(int loanApplicationId, int applicationStatusId) {
        return queryForObjectTrx("select count(*) > 0 from credit.lg_application_status where loan_application_id = ? and application_status_id = ?", Boolean.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, applicationStatusId));
    }

    @Override
    public void updateApprovalValidations(int loanApplicationId, List<LoanApplicationApprovalValidation> validations) {
        updateTrx("UPDATE credit.tb_loan_application set js_approval_validations = ? where loan_application_id = ?",
                new SqlParameterValue(Types.OTHER, validations != null ? new Gson().toJson(validations) : null),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateLoanApplicationCode(Integer loanApplicationId, String code) {
        updateTrx("UPDATE credit.tb_loan_application SET loan_application_code = ?  where loan_application_id = ?",
                new SqlParameterValue(Types.VARCHAR, code),
                new SqlParameterValue(Types.INTEGER, loanApplicationId)
        );
    }

    @Override
    public void updateLoanOfferFirstDueDate(Integer offerId, Date firstDueDate) {
        updateTrx("UPDATE credit.tb_loan_offer SET first_due_date = ?  where loan_offer_id = ?",
                new SqlParameterValue(Types.DATE, firstDueDate),
                new SqlParameterValue(Types.INTEGER, offerId)
        );
    }

    @Override
    public void removeLoanOffers(int loanApplicationId) throws Exception {
        updateTrx("delete from credit.tb_offer_schedule where loan_offer_id in (select of.loan_offer_id from credit.tb_loan_offer of where of.loan_application_id = ?);\n" +
                        "delete from credit.tb_loan_offer where loan_application_id = ?;",
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void updateOfferOriginalScheduleData(int loanOfferId, int installmentId, Date dueDate, Double installmentAmount, Double installmentCapital, Double interest, Double insurance, Double remainingCapital) {
        updateTrx("update credit.tb_offer_schedule set due_date = ?, installment_amount = ?, installment_capital = ?, interest = ?, insurance = ?,  remaining_capital = ?  where loan_offer_id = ? and installment_id = ?",
                new SqlParameterValue(Types.TIMESTAMP, dueDate),
                new SqlParameterValue(Types.NUMERIC, installmentAmount),
                new SqlParameterValue(Types.NUMERIC, installmentCapital),
                new SqlParameterValue(Types.NUMERIC, interest),
                new SqlParameterValue(Types.NUMERIC, insurance),
                new SqlParameterValue(Types.NUMERIC, remainingCapital),
                new SqlParameterValue(Types.INTEGER, loanOfferId),
                new SqlParameterValue(Types.INTEGER, installmentId)
        );
    }

    @Override
    public void updateLoanOfferInstallmentAmountAndAvg(int loanOfferId, Double installmentAmount, Double installmentAmountAvg) {
        updateTrx("update credit.tb_loan_offer set installment_amount = ?, installment_amount_avg = ? where loan_offer_id = ?",
                new SqlParameterValue(Types.NUMERIC, installmentAmount),
                new SqlParameterValue(Types.NUMERIC, installmentAmountAvg),
                new SqlParameterValue(Types.INTEGER, loanOfferId)
        );
    }

    @Override
    public List<Integer> getLoanToSendBanbifKonectaLead() throws Exception {
        JSONArray jsonArray = queryForObjectTrx("SELECT json_agg(tx)\n" +
                "FROM (\n" +
                "         select distinct loan_application_id\n" +
                "         from credit.tb_loan_application la\n" +
                "                  join evaluation.tb_evaluation ev using (loan_application_id)\n" +
                "         where la.entity_id = 20\n" +
                "           and la.application_status_id = 3\n" +
                "           and la.selected_entity_product_parameter_id = 201201\n" +
                "           and coalesce(la.entity_custom_data->>'banbifCallKonectaLead', 'false') <> 'true'\n" +
                "           and current_timestamp >= ev.evaluation_date + (5 || ' minutes')::interval\n" +
                "           and current_timestamp < ev.evaluation_date + (30 || ' minutes')::interval) AS tx", JSONArray.class
        );

        if (jsonArray == null)
            return new ArrayList<>();

        List<Integer> results = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            results.add(jsonArray.optJSONObject(i).getInt("loan_application_id"));
        }

        return results;
    }

    @Override
    public void expireBanbifNewBaseLoans() throws Exception {
        queryForObjectTrx("select * from credit.expire_banbif_new_base_loans()", String.class);
    }


    @Override
    public List<LoanApplication> getLoansApplicationByCollectionIds(Locale locale, int productCategoryId, Integer entityId, List<Long> collectionIds) throws Exception{
        JSONArray dbJson = queryForObjectTrx("select * from credit.get_loans_application_customized_by_collection_ids(?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, productCategoryId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.OTHER, new Gson().toJson(collectionIds))
        );
        if (dbJson == null) {
            return null;
        }

        List<LoanApplication> loanApplications = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            LoanApplication loan = new LoanApplication();
            loan.fillFromDb(dbJson.getJSONObject(i), catalogService, locale);
            loanApplications.add(loan);
        }
        return loanApplications;
    }

    @Override
    public PreLoanApplication insertPreLoanApplication(int documentType, String documentNumber, Integer entityId, Integer categoryId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.ins_pre_loan_application(?, ?, ?, ?)",
                JSONObject.class,
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, categoryId));

        PreLoanApplication  preLoan = new PreLoanApplication();
        preLoan.fillFromDb(dbJson,catalogService,Configuration.getDefaultLocale());
        return preLoan;
    }

}