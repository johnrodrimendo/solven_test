package com.affirm.common.dao.impl;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.ReportEntityExtranetTrayReport;
import com.affirm.common.model.ReportLoans;
import com.affirm.common.model.ReportOrigination;
import com.affirm.common.model.UTMValue;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.CreditService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.net.URLDecoder;
import java.sql.Types;
import java.util.*;

/**
 * Created by jrodriguez on 08/06/16.
 */

@Repository("creditDAO")
public class CreditDAOImpl extends JsonResolverDAO implements CreditDAO {


    @Autowired
    private CatalogService catalogService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private CreditService creditService;

    @Override
    public <T extends Credit> List<T> getCreditsByPerson(int personId, Locale locale, Class<T> returntype) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from credit.bo_get_credits_person(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<T> credits = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            T credit = returntype.getConstructor().newInstance();
            credit.fillFromDb(dbJson.getJSONObject(i), catalogService, locale);
            credits.add(credit);
        }
        return credits;
    }

    @Override
    public <T extends Credit> T getCreditBO(int creditId, Locale locale, Class<T> returntype) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.bo_get_credit(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, creditId));
        if (dbJson == null) {
            return null;
        }

        T credit = returntype.getConstructor().newInstance();
        credit.fillFromDb(dbJson, catalogService, locale);
        return credit;
    }

    @Override
    public void generateOriginalSchedule(Credit credit) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from credit.generate_original_schedule(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, credit.getId()));
        if (dbJson != null) {

            EntityProductParams entityProductParam = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());

            if (entityProductParam.getSolvenGeneratesSchedule()) {

                List<OriginalSchedule> originalSchedule = new ArrayList<>();
                for (int i = 0; i < dbJson.length(); i++) {
                    OriginalSchedule installment = new OriginalSchedule();
                    installment.fillFromDb(dbJson.getJSONObject(i), OriginalSchedule.CREDIT);
                    originalSchedule.add(installment);
                }
                credit.setOriginalSchedule(originalSchedule);

                creditService.generateCreditTcea(credit);
            }
        }
    }

    @Override
    public List<OriginalSchedule> getOriginalSchedule(int creditId) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from credit.get_original_schedule(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, creditId));
        if (dbJson == null) {
            return null;
        }

        List<OriginalSchedule> originalSchedule = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            OriginalSchedule installment = new OriginalSchedule();
            installment.fillFromDb(dbJson.getJSONObject(i), OriginalSchedule.CREDIT);
            originalSchedule.add(installment);
        }
        return originalSchedule;
    }

    @Override
    public List<ManagementSchedule> getManagementSchedule(int creditId) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from credit.get_management_schedule(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, creditId));
        if (dbJson == null) {
            return null;
        }

        List<ManagementSchedule> managementSchedule = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            ManagementSchedule installment = new ManagementSchedule();
            installment.fillFromDb(dbJson.getJSONObject(i));
            managementSchedule.add(installment);
        }
        return managementSchedule;
    }

    @Override
    public void updateCreditStatus(int creditId, int creditStatusId, Integer sysUserId) throws Exception {
        queryForObjectTrx("select * from credit.upd_credit_status(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.INTEGER, sysUserId),
                new SqlParameterValue(Types.INTEGER, creditStatusId));
    }


    @Override
    public void updateCreditStatusExtranet(int creditId, int creditStatusId, int sysUserId) throws Exception {
        queryForObjectTrx("select * from credit.upd_credit_status_entity(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.INTEGER, sysUserId),
                new SqlParameterValue(Types.INTEGER, creditStatusId));
    }

    @Override
    public void updateCreditWaitingStatusExtranet(int creditId, Boolean creditWaitingStatusId, int sysUserId) throws Exception {
        updateTrx("UPDATE credit.tb_credit set waiting_for_generation_on_entity = ?, waiting_for_generation_on_entity_user_id = ? where credit_id = ?",
                new SqlParameterValue(Types.BOOLEAN, creditWaitingStatusId),
                new SqlParameterValue(Types.INTEGER, sysUserId),
                new SqlParameterValue(Types.INTEGER, creditId));
    }

    @Override
    public String getResultEFL(Credit credit) {
        try {

            return loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), Configuration.getDefaultLocale()).getResultEFL();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public <T extends Credit> T getCreditByID(int creditId, Locale locale, boolean getSchedule, Class<T> returntype) {
        JSONObject dbJson = queryForObjectTrx("select * from credit.get_credit(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, creditId));
        if (dbJson == null) {
            return null;
        }
        T credit;
        try {
            credit = returntype.getConstructor().newInstance();
            credit.fillFromDb(dbJson, catalogService, locale);
            if (getSchedule) {
                credit.setManagementSchedule(getManagementSchedule(creditId));
                credit.setOriginalSchedule(getOriginalSchedule(creditId));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return credit;
    }

    @Override
    public <T extends Credit> T getCreditByLoanApplicationId(int loanApplicationId, Locale locale, boolean getSchedule, Class<T> returntype) {
        JSONObject dbJson = queryForObjectTrx("select * from credit.get_credit_by_loan_application_id(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return null;
        }
        T credit;
        try {
            credit = returntype.getConstructor().newInstance();
            credit.fillFromDb(dbJson, catalogService, locale);
            if (getSchedule) {
                credit.setManagementSchedule(getManagementSchedule(credit.getId()));
                credit.setOriginalSchedule(getOriginalSchedule(credit.getId()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return credit;
    }

    @Override
    public Integer[] getActiveCreditIdsByPerson(Locale locale, int personId, Integer flagSolven) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_active_credit(?,?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, flagSolven));

        if (dbArray == null) {
            return null;
        }

        Integer[] creditIds = new Integer[dbArray.length()];
        for (int i = 0; i < dbArray.length(); i++) {
            creditIds[i] = dbArray.getJSONObject(i).getInt("credit_id");
        }
        return creditIds;
    }

    @Override
    public JSONObject preDisbursementValidation(int creditId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.pre_disbursement_validation(?)",
                JSONObject.class,
                new SqlParameterValue(Types.INTEGER, creditId));
        return dbJson;
    }

    @Override
    public void updateCreditContract(int creditId, Integer[] userFileIds) throws Exception {
        queryForObjectTrx("select * from credit.upd_credit_contract(?, ?)",
                String.class,
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.OTHER, new Gson().toJson(userFileIds)));
    }

    @Override
    public List<Credit> getEmployerCredits(Integer productId, Integer employerId, Integer offset, Integer limit, Locale locale) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.bo_get_credits_product(?, ?, ?, ?)",
                JSONArray.class,
                new SqlParameterValue(Types.INTEGER, productId),
                new SqlParameterValue(Types.INTEGER, employerId),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit));
        if (dbArray == null)
            return null;

        List<Credit> credits = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Credit credit = new Credit();
            credit.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            credits.add(credit);
        }
        return credits;
    }

    @Override
    public <T extends Credit> List<T> getCreditsByIds(Integer[] ids, Locale locale, Class<T> returntype) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_credit_array(?)",
                JSONArray.class,
                new SqlParameterValue(Types.OTHER, new Gson().toJson(ids)));
        if (dbArray == null)
            return null;

        List<T> credits = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            T credit = returntype.getConstructor().newInstance();
            credit.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            credits.add(credit);
        }
        return credits;
    }


    @Override
    public Integer getCreditByCreditCode(String creditCode, int entityId) throws Exception {
        Integer creditId = queryForObjectTrx("select * from credit.get_credit_by_entity_credit_code(?,?)",
                Integer.class,
                new SqlParameterValue(Types.VARCHAR, creditCode),
                new SqlParameterValue(Types.INTEGER, entityId));
        return creditId;
    }

    @Override
    public void updateCrediCodeByCreditId(int creditId, String creditCode) throws Exception {
        queryForObjectTrx("select * from credit.upd_credit_entity_code(?,?)",
                String.class,
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.VARCHAR, creditCode));

    }

    @Override
    public Integer getLoanApplicationIdByCreditCode(String creditCode, int entityId) throws Exception {
        return queryForObjectTrx("select * from credit.get_loan_application_id_by_credit_code(?,?)",
                Integer.class,
                new SqlParameterValue(Types.VARCHAR, creditCode),
                new SqlParameterValue(Types.INTEGER, entityId));

    }

    @Override
    public void updateSignatureDate(int creditId, Date signatureDate) throws Exception {
        updateTrx("UPDATE credit.tb_credit set signature_date = ? WHERE credit_id = ?;",
                new SqlParameterValue(Types.DATE, signatureDate),
                new SqlParameterValue(Types.INTEGER, creditId));
    }

    @Override
    public void updateDisbursementDate(int creditId, Date date) throws Exception {
        updateTrx("UPDATE credit.tb_credit set disbursement_date = ? WHERE credit_id = ?;",
                new SqlParameterValue(Types.TIMESTAMP, date),
                new SqlParameterValue(Types.INTEGER, creditId));
    }

    @Override
    public List<ConsolidableDebt> getConsolidatedDebts(int creditId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_credit_consolidation_accounts(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, creditId));

        if (dbArray == null)
            return null;

        List<ConsolidableDebt> credits = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ConsolidableDebt debt = new ConsolidableDebt();
            debt.fillFromDb(dbArray.getJSONObject(i), catalogService);
            credits.add(debt);
        }
        return credits;
    }

    @Override
    public List<CreditEntityExtranetPainter> getEntityCredits(Integer entityId, Integer flag, Date startDate, Date endDate, Integer entityUserId, Locale locale, Integer offset, Integer limit,String search, boolean onlyLoanIds) throws Exception{
        return getEntityCredits(entityId, flag, startDate, endDate, entityUserId, locale, offset, limit, search, null, onlyLoanIds,null);
    }

    @Override
    public List<CreditEntityExtranetPainter> getEntityCredits(Integer entityId, Integer flag, Date startDate, Date endDate, Integer entityUserId, Locale locale, Integer offset, Integer limit,String search, Integer[] entityProductsParam, boolean onlyLoanIds, List<Integer> products) throws Exception {
        if(offset != null && offset < 0) offset = 0;
        if(limit != null && limit < 0) limit = null;
        if(search != null && search.isEmpty()) search = null;
        JSONArray dbArray = null;
        if(entityId != null && entityId.equals(Entity.BANCO_DEL_SOL)){
            dbArray = queryForObjectTrx("select * from credit.get_pending_entity_credits(?, ?, ? , ?, ?, ?, ?, ?, ?, ?)", JSONArray.class,
                    new SqlParameterValue(Types.INTEGER, entityId),
                    new SqlParameterValue(Types.INTEGER, flag),
                    new SqlParameterValue(Types.VARCHAR, search),
                    new SqlParameterValue(Types.DATE, startDate),
                    new SqlParameterValue(Types.DATE, endDate),
                    new SqlParameterValue(Types.INTEGER, entityUserId),
                    new SqlParameterValue(Types.INTEGER, offset),
                    new SqlParameterValue(Types.INTEGER, limit),
                    new SqlParameterValue(Types.BOOLEAN, onlyLoanIds),
                    new SqlParameterValue(Types.OTHER, entityProductsParam == null ? entityProductsParam : new Gson().toJson(entityProductsParam))
            );
        }
        else{
            dbArray = queryForObjectTrx("select * from credit.get_pending_entity_credits(?, ?, ? , ?, ?, ?, ?, ?, ?, ? , ?)", JSONArray.class,
                    new SqlParameterValue(Types.INTEGER, entityId),
                    new SqlParameterValue(Types.INTEGER, flag),
                    new SqlParameterValue(Types.VARCHAR, search),
                    new SqlParameterValue(Types.DATE, startDate),
                    new SqlParameterValue(Types.DATE, endDate),
                    new SqlParameterValue(Types.INTEGER, entityUserId),
                    new SqlParameterValue(Types.INTEGER, offset),
                    new SqlParameterValue(Types.INTEGER, limit),
                    new SqlParameterValue(Types.BOOLEAN, onlyLoanIds),
                    new SqlParameterValue(Types.OTHER, entityProductsParam == null ? entityProductsParam : new Gson().toJson(entityProductsParam)),
                    new SqlParameterValue(products == null ? Types.NULL : Types.OTHER, products == null ? null : new Gson().toJson(products))
            );
        }

        if (dbArray == null)
            return null;

        List<CreditEntityExtranetPainter> credits = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            // Firs fix the json returned of the DB
            JSONObject json = dbArray.getJSONObject(i);
            if(JsonUtil.getJsonObjectFromJson(json, "credit_data", null) != null){
                JSONObject creditData = JsonUtil.getJsonObjectFromJson(json, "credit_data", null);
                for(String key : JSONObject.getNames(creditData)){
                    json.put(key, creditData.get(key));
                }
                json.remove("credit_data");
            }
            // Then convert it to the class
            CreditEntityExtranetPainter credit = new CreditEntityExtranetPainter();
            credit.fillFromDb(json, catalogService, locale);
            credits.add(credit);
        }
        return credits;
    }

    @Override
    public Pair<Integer, Double> getEntityCreditsCount(Integer entityId, Integer flag, Date startDate, Date endDate, Integer entityUserId, Locale locale) throws Exception {
        return this.getEntityCreditsCount(entityId,flag,startDate,endDate,entityUserId,locale,null);
    }

    @Override
    public Pair<Integer, Double> getEntityCreditsCount(Integer entityId, Integer flag, Date startDate, Date endDate, Integer entityUserId, Locale locale, String search) throws Exception{
        return this.getEntityCreditsCount(entityId,flag,startDate,endDate,entityUserId,locale,null, null,null);
    }

    @Override
    public Pair<Integer, Double> getEntityCreditsCount(Integer entityId, Integer flag, Date startDate, Date endDate, Integer entityUserId, Locale locale, String search, Integer[] entityProductsParam, List<Integer> products) throws Exception {

        JSONObject dbJson = null;
        if(entityId != null && entityId.equals(Entity.BANCO_DEL_SOL)){
            dbJson =  queryForObjectTrx("select * from credit.get_pending_entity_credits_count(?, ?, ?, ?, ?, ?, ?)", JSONObject.class,
                    new SqlParameterValue(Types.INTEGER, entityId),
                    new SqlParameterValue(Types.INTEGER, flag),
                    new SqlParameterValue(Types.VARCHAR, search),
                    new SqlParameterValue(Types.DATE, startDate),
                    new SqlParameterValue(Types.DATE, endDate),
                    new SqlParameterValue(Types.INTEGER, entityUserId),
                    new SqlParameterValue(Types.OTHER, entityProductsParam == null ? entityProductsParam : new Gson().toJson(entityProductsParam))
            );
        }
        else{
            dbJson =  queryForObjectTrx("select * from credit.get_pending_entity_credits_count(?, ?, ?, ?, ?, ?, ? , ?)", JSONObject.class,
                    new SqlParameterValue(Types.INTEGER, entityId),
                    new SqlParameterValue(Types.INTEGER, flag),
                    new SqlParameterValue(Types.VARCHAR, search),
                    new SqlParameterValue(Types.DATE, startDate),
                    new SqlParameterValue(Types.DATE, endDate),
                    new SqlParameterValue(Types.INTEGER, entityUserId),
                    new SqlParameterValue(Types.OTHER, entityProductsParam == null ? entityProductsParam : new Gson().toJson(entityProductsParam)),
                    new SqlParameterValue(products == null ? Types.NULL : Types.OTHER, products == null ? null : new Gson().toJson(products))
            );
        }
        if (dbJson == null)
            return Pair.of(0, 0.0);

        return Pair.of(JsonUtil.getIntFromJson(dbJson, "count", 0), JsonUtil.getDoubleFromJson(dbJson, "sum", 0.0));
    }

    @Override
    public void updateGeneratedInEntity(Integer creditId, Integer userId) throws Exception {
        updateTrx("UPDATE credit.tb_credit set generated_on_entity = true, generated_on_entity_user_id = ? WHERE credit_id = ?;",
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.INTEGER, creditId));
    }

    @Override
    public void updateDisbursmentInInEntity(Integer creditId, Integer userId) throws Exception {
        updateTrx("UPDATE credit.tb_credit set disbursed_on_entity = true, disbursed_on_entity_user_id = ? WHERE credit_id = ?;",
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.INTEGER, creditId));
    }

    @Override
    public String registerDownPayment(int creditId, Integer downPaymentBank, Integer currency, Double amount, String downPaymentOps) throws Exception {
        JSONObject dbObject = queryForObjectTrx("select * from credit.register_down_payment(?, ?, ?, ?, ?)",
                JSONObject.class,
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.NUMERIC, amount),
                new SqlParameterValue(Types.VARCHAR, downPaymentOps),
                new SqlParameterValue(Types.INTEGER, currency),
                new SqlParameterValue(Types.INTEGER, downPaymentBank));

        if (dbObject == null) return "";
        return dbObject.toString();
    }

    @Override
    public List<DownPayment> getDownPayments(int creditId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_down_payment(?)",
                JSONArray.class,
                new SqlParameterValue(Types.INTEGER, creditId));

        if (dbArray == null) return null;
        List<DownPayment> downPayments = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            DownPayment downPayment = new DownPayment();
            downPayment.fillFromDb(catalogService, dbArray.getJSONObject(i));
            downPayments.add(downPayment);
        }
        return downPayments;
    }

    @Override
    public void registerSignatureSchedule(int creditId, Date scheduleDate, String scheduleHour, String address) throws Exception {
        queryForObjectTrx("select * from credit.register_signature_schedule(?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.DATE, scheduleDate),
                new SqlParameterValue(Types.VARCHAR, scheduleHour),
                new SqlParameterValue(Types.VARCHAR, address));
    }

    @Override
    public CreditSignatureSchedule getSignatureSchedule(int creditId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.get_scheduled_signature(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, creditId));
        if (dbJson == null) {
            return null;
        }

        CreditSignatureSchedule signatreSchedule = new CreditSignatureSchedule();
        signatreSchedule.fillFromDb(dbJson);
        return signatreSchedule;
    }

    @Override
    public void updateSignedOnEntity(int creditId, Boolean signedOnEntity) throws Exception {
        updateTrx("UPDATE credit.tb_credit set signed_on_entity = ? WHERE credit_id = ?;",
                new SqlParameterValue(Types.BOOLEAN, signedOnEntity),
                new SqlParameterValue(Types.INTEGER, creditId));
    }

    @Override
    public boolean registerLoanApplicationAudit(int loanApplicationId, int loanApplicationAuditType, boolean approved, Integer userFileId, Integer auditRejectionReason, String auditRejectionReasonComment, Integer sysuserId) throws Exception {
        return queryForObjectTrx("select * from credit.register_loan_application_audit(?, ?, ?, ?, ?, ?, ?)", Boolean.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, loanApplicationAuditType),
                new SqlParameterValue(Types.BOOLEAN, approved),
                new SqlParameterValue(Types.INTEGER, userFileId),
                new SqlParameterValue(Types.INTEGER, auditRejectionReason),
                new SqlParameterValue(Types.INTEGER, sysuserId),
                new SqlParameterValue(Types.VARCHAR, auditRejectionReasonComment)
        );
    }

    @Override
    public List<AuditRejectionReason> getAuditRejectionReason(int loanApplicationId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_audit_rejection_reason(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));

        if (dbArray == null)
            return null;

        List<AuditRejectionReason> auditRejectionReasons = new ArrayList<>();

        for (int i = 0; i < dbArray.length(); i++) {
            AuditRejectionReason auditRejectionReason = new AuditRejectionReason();
            auditRejectionReason.fillFromDb(dbArray.getJSONObject(i));
            auditRejectionReasons.add(auditRejectionReason);
        }

        return auditRejectionReasons;
    }

    @Override
    public void registerRejection(int creditId, Integer creditRejectionReasonId) throws Exception {
        queryForObjectTrx("select * from credit.bo_upd_credit_rejection_reason(?, ?)",
                String.class,
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.INTEGER, creditRejectionReasonId));
    }

    @Override
    public void registerRejectionWithComment(int creditId, Integer creditRejectionReasonId, String creditRejectionReasonComment) throws Exception {
        queryForObjectTrx("select * from credit.bo_upd_credit_rejection_reason(?, ?, ?)",
                String.class,
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.INTEGER, creditRejectionReasonId),
                new SqlParameterValue(Types.VARCHAR, creditRejectionReasonComment));
    }

    @Override
    public List<Credit> getCreditByEmployer(Integer employerId, Date period, Locale locale) {
        JSONArray dbJson = queryForObjectTrx("select * from credit.get_credits_by_employer(?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, employerId),
                new SqlParameterValue(Types.DATE, period));
        if (dbJson == null) {
            return null;
        }
        List<Credit> credits = new ArrayList<>();
        try {
            for (int i = 0; i < dbJson.length(); i++) {
                Credit credit = new Credit();
                credit.fillFromDb(dbJson.getJSONObject(i), catalogService, locale);
                credits.add(credit);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return credits;
    }

    @Override
    public List<ReportLoans> getLoansReport(Date startDate, Date endDate, Locale locale, String countries) {
        JSONArray dbJson = queryForObject("select * from credit.rp_application(?, ?, ?::JSON);", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.OTHER, countries));
        if (dbJson == null) {
            return null;
        }
        List<ReportLoans> loans = new ArrayList<>();
        try {
            for (int i = 0; i < dbJson.length(); i++) {
                ReportLoans loan = new ReportLoans();
                loan.fillFromDb(dbJson.getJSONObject(i), catalogService, locale);
                loans.add(loan);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return loans;
    }

    @Override
    public List<ReportLoans> getLoansLightReport(Date startDate, Date endDate, Locale locale, Integer[] countryIds, Integer[] entityIds) {
        JSONArray dbJson = queryForObject("select * from credit.rp_application(?, ?, ?::JSON, ?::JSON);", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.OTHER, countryIds != null ? new Gson().toJson(countryIds) : null),
                new SqlParameterValue(Types.OTHER, entityIds != null ? new Gson().toJson(entityIds) : null));
        if (dbJson == null) {
            return null;
        }
        List<ReportLoans> loans = new ArrayList<>();
        try {
            for (int i = 0; i < dbJson.length(); i++) {
                ReportLoans loan = new ReportLoans();
                loan.fillFromDb(dbJson.getJSONObject(i), catalogService, locale);
                loans.add(loan);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return loans;
    }

    @Override
    public List<ReportLoans> getLoansLightReport(Integer minAge, Integer maxAge, String requestType, String cardType, Date startDate, Date endDate,
                                                 Date startDate2, Date endDate2, Locale locale, Integer[] countryIds, Integer[] entityIds, Integer[] products) {
        JSONArray dbJson = queryForObject("select * from credit.rp_application(?, ?, ?, ?, ?, ?, ?, ?, ?::JSON, ?::JSON, ?::JSON);", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, minAge),
                new SqlParameterValue(Types.INTEGER, maxAge),
                new SqlParameterValue(Types.CHAR, requestType),
                new SqlParameterValue(Types.CHAR, cardType),
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.DATE, startDate2),
                new SqlParameterValue(Types.DATE, endDate2),
                new SqlParameterValue(Types.OTHER, countryIds != null ? new Gson().toJson(countryIds) : null),
                new SqlParameterValue(Types.OTHER, entityIds != null ? new Gson().toJson(entityIds) : null),
                new SqlParameterValue(Types.OTHER, products != null ? new Gson().toJson(products) : null));
        if (dbJson == null) {
            return null;
        }
        List<ReportLoans> loans = new ArrayList<>();
        try {
            for (int i = 0; i < dbJson.length(); i++) {
                ReportLoans loan = new ReportLoans();
                loan.fillFromDb(dbJson.getJSONObject(i), catalogService, locale);
                loans.add(loan);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return loans;
    }

    @Override
    public List<ReportLoans> getLoansLightReportFunnel(Integer minAge, Integer maxAge, String requestType, String cardType, Date startDate, Date endDate,
                                                       Date startDate2, Date endDate2, Locale locale, Integer[] countryIds, Integer[] entityIds, Integer[] products,Integer[] steps){
        return getLoansLightReportFunnel(minAge, maxAge, requestType, cardType, startDate,  endDate, startDate2,  endDate2, locale, countryIds, entityIds, products, steps, null, null, null, null, null);
    }

    @Override
    public List<ReportLoans> getLoansLightReportFunnel(Integer minAge, Integer maxAge, String requestType, String cardType, Date startDate, Date endDate,
                                                 Date startDate2, Date endDate2, Locale locale, Integer[] countryIds, Integer[] entityIds, Integer[] products,Integer[] steps,
                                                List<String> utmSources,List<String> utmMedium,List<String> utmCampaign,List<String> utmContent, List<Integer> entityProductParams
                                                       ) {
        if(utmSources != null && utmSources.isEmpty()) utmSources = null;
        if(utmMedium != null && utmMedium.isEmpty()) utmMedium = null;
        if(utmCampaign != null && utmCampaign.isEmpty()) utmCampaign = null;
        if(utmContent != null && utmContent.isEmpty()) utmContent = null;
        if(entityProductParams != null && entityProductParams.isEmpty()) entityProductParams = null;

        JSONArray dbJson = queryForObject("select * from credit.rp_application_funnel(?, ?, ?, ?, ?, ?, ?, ?, ?::JSON, ?::JSON, ?::JSON, ?::JSON, ?::JSON, ?::JSON, ?::JSON, ?::JSON, ?::JSON);", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, minAge),
                new SqlParameterValue(Types.INTEGER, maxAge),
                new SqlParameterValue(Types.CHAR, requestType),
                new SqlParameterValue(Types.CHAR, cardType),
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.DATE, startDate2),
                new SqlParameterValue(Types.DATE, endDate2),
                new SqlParameterValue(Types.OTHER, countryIds != null ? new Gson().toJson(countryIds) : null),
                new SqlParameterValue(Types.OTHER, entityIds != null ? new Gson().toJson(entityIds) : null),
                new SqlParameterValue(Types.OTHER, products != null ? new Gson().toJson(products) : null),
                new SqlParameterValue(Types.OTHER, steps != null ? new Gson().toJson(steps) : null),
                new SqlParameterValue(utmSources != null ? Types.OTHER : Types.NULL, utmSources != null ? new Gson().toJson(utmSources) : null),
                new SqlParameterValue(utmMedium != null ? Types.OTHER : Types.NULL, utmMedium != null ? new Gson().toJson(utmMedium) : null),
                new SqlParameterValue(utmCampaign != null ? Types.OTHER : Types.NULL, utmCampaign != null ? new Gson().toJson(utmCampaign) : null),
                new SqlParameterValue(utmContent != null ? Types.OTHER : Types.NULL, utmContent != null ? new Gson().toJson(utmContent) : null),
                new SqlParameterValue(entityProductParams != null ? Types.OTHER : Types.NULL, entityProductParams != null ? new Gson().toJson(entityProductParams) : null)
                );
        if (dbJson == null) {
            return null;
        }
        List<ReportLoans> loans = new ArrayList<>();
        try {
            for (int i = 0; i < dbJson.length(); i++) {
                ReportLoans loan = new ReportLoans();
                loan.fillFromDb(dbJson.getJSONObject(i), catalogService, locale);
                loans.add(loan);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return loans;
    }

    @Override
    public List<ReportOrigination> getOriginationReport(String country, CatalogService catalogService, Locale locale, Date startDate, Date endDate) throws Exception {
        JSONArray dbArray = queryForObject("select * from credit.rp_credit_origination(?,?,?)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.OTHER, country));
        if (dbArray == null) {
            return null;
        }

        List<ReportOrigination> reportOriginations = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ReportOrigination reportOrigination = new ReportOrigination();
            reportOrigination.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            reportOriginations.add(reportOrigination);
        }
        return reportOriginations;
    }


    @Override
    public boolean runFraudAlerts(int loanApplicationId) throws Exception {
        return queryForObjectTrx("SELECT * FROM credit.run_fraud_alerts(?);", Boolean.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void rejectLoanApplicationFraudAlert(Integer loanApplicationFraudAlertId, Integer sysuserId) throws Exception {
        rejectLoanApplicationFraudAlert(loanApplicationFraudAlertId, sysuserId, null);
    }

    @Override
    public void rejectLoanApplicationFraudAlert(Integer loanApplicationFraudAlertId, Integer sysuserId, Integer entityUserId) throws Exception {
        queryForObjectTrx("select * from credit.reject_fraud_alert(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationFraudAlertId),
                new SqlParameterValue(Types.INTEGER, sysuserId),
                new SqlParameterValue(Types.INTEGER, entityUserId)
                );
    }

    @Override
    public void registerLoanApplicationFraudAlert(Integer fraudAlertId, Integer loanApplicationId) throws Exception {
        queryForObjectTrx("select * from credit.insert_validation_fraud_alert(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, fraudAlertId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }

    @Override
    public void assignLoanApplicationFraudFlag(Integer loanApplicationId, Integer flagFraudAlertReasonId, Integer fraudFlagId, String commentary, Integer sysuserId) throws Exception {
        queryForObjectTrx("select * from credit.assign_fraud_flag(?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, flagFraudAlertReasonId),
                new SqlParameterValue(Types.INTEGER, fraudFlagId),
                new SqlParameterValue(Types.VARCHAR, commentary),
                new SqlParameterValue(Types.INTEGER, sysuserId));
    }

    @Override
    public void reviewFraudFlag(Integer loanApplicationFraudFraudId, Boolean approve, Integer sysuserId) throws Exception {
        queryForObjectTrx("select * from credit.review_fraud_flag(?, ? , ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationFraudFraudId),
                new SqlParameterValue(Types.BOOLEAN, approve),
                new SqlParameterValue(Types.INTEGER, sysuserId));
    }

    @Override
    public List<LoanApplicationFraudAlert> getLoanApplicationFraudAlerts(Integer loanApplicationId, Integer fraudAlertStatusId) throws Exception {
        JSONArray jsonArray = queryForObjectTrx("select * from credit.get_loan_application_fraud_alerts(?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, fraudAlertStatusId));

        if (jsonArray == null) {
            return null;
        }

        List<LoanApplicationFraudAlert> loanApplicationFraudAlerts = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); ++i) {
                LoanApplicationFraudAlert loanApplicationFraudAlert = new LoanApplicationFraudAlert();
                loanApplicationFraudAlert.fillFromDb(jsonArray.getJSONObject(i), catalogService, Configuration.getDefaultLocale());
                loanApplicationFraudAlerts.add(loanApplicationFraudAlert);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return loanApplicationFraudAlerts;
    }

    @Override
    public List<LoanApplicationFraudFlag> getToReviewLoanApplicationFraudFlags() throws Exception {
        JSONArray jsonArray = queryForObjectTrx("select * from credit.get_to_review_loan_application_fraud_flags();", JSONArray.class);

        if (jsonArray == null) {
            return null;
        }

        List<LoanApplicationFraudFlag> rejectedLoanApplicationFraudAlerts = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); ++i) {
                LoanApplicationFraudFlag loanApplicationFraudAlert = new LoanApplicationFraudFlag();
                loanApplicationFraudAlert.fillFromDb(jsonArray.getJSONObject(i), catalogService, Configuration.getDefaultLocale());
                rejectedLoanApplicationFraudAlerts.add(loanApplicationFraudAlert);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return rejectedLoanApplicationFraudAlerts;
    }

    @Override
    public List<LoanApplicationFraudFlag> getLogFraudFlags() throws Exception {
        JSONArray jsonArray = queryForObjectTrx("select * from credit.get_log_fraud_flags();", JSONArray.class);

        if (jsonArray == null) {
            return null;
        }

        List<LoanApplicationFraudFlag> rejectedLoanApplicationFraudAlerts = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); ++i) {
                LoanApplicationFraudFlag loanApplicationFraudAlert = new LoanApplicationFraudFlag();
                loanApplicationFraudAlert.fillFromDb(jsonArray.getJSONObject(i), catalogService, Configuration.getDefaultLocale());
                rejectedLoanApplicationFraudAlerts.add(loanApplicationFraudAlert);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return rejectedLoanApplicationFraudAlerts;
    }

    @Override
    public void updateCreditSubStatus(int creditId, Integer subStatus) throws Exception {
        updateTrx("UPDATE credit.tb_credit set credit_sub_status_id = ? WHERE credit_id = ?;",
                new SqlParameterValue(Types.INTEGER, subStatus),
                new SqlParameterValue(Types.INTEGER, creditId));
    }

    @Override
    public void registerSchedule(Integer creditId, String jsonSchedule) {
        queryForObjectTrx("select * from credit.ins_entity_credit_schedule(?,?::json)", String.class,
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.OTHER, jsonSchedule));
    }

    @Override
    public List<String> getEntityApplicationCodesByFilter(Integer entityId, Integer creditStatusId, Integer creditSubStatusId) {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_credit_by_entyty_status(?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, creditStatusId),
                new SqlParameterValue(Types.INTEGER, creditSubStatusId));
        if (dbArray == null)
            return new ArrayList<>();

        List<String> entityApplicationCodes = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); ++i) {
            entityApplicationCodes.add(dbArray.getJSONObject(i).getString("entity_application_code"));
        }
        return entityApplicationCodes;
    }

    @Override
    public List<LoanDetailsReport> generateLoanDetailsReport(CatalogService catalogService) throws Exception {
        JSONArray dbArray = queryForObject("select * from credit.rp_efl_management_schedules()", JSONArray.class, READABLE_DB);
        if (dbArray == null)
            return new ArrayList<>();

        List<LoanDetailsReport> loanDetailsReports = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); ++i) {
            LoanDetailsReport loanDetailsReport = new LoanDetailsReport();
            loanDetailsReport.fillFromDb(dbArray.getJSONObject(i), catalogService);
            loanDetailsReports.add(loanDetailsReport);
        }
        return loanDetailsReports;
    }

    @Override
    public Integer getCountryIdByCreditId(Integer creditId) {
        Integer countryId = queryForObjectTrx("select country_id from credit.tb_credit where credit_id=?", Integer.class,
                new SqlParameterValue(Types.INTEGER, creditId));
        if (countryId == null)
            return null;
        return countryId;
    }

    @Override
    public void updateReturningReasons(int creditId, JSONArray returninReason) throws Exception {
        updateTrx("UPDATE credit.tb_credit set js_returning_reason_id = ?::JSON where credit_id = ?",
                new SqlParameterValue(Types.VARCHAR, returninReason != null ? returninReason.toString() : null),
                new SqlParameterValue(Types.INTEGER, creditId));
    }

    @Override
    public void updateDisbursementDate(Integer creditId, Date disbursementDate) throws Exception {
        updateTrx("UPDATE credit.tb_credit set disbursed_on_entity_date = ? where credit_id = ?;",
                new SqlParameterValue(Types.DATE, disbursementDate),
                new SqlParameterValue(Types.INTEGER, creditId));
    }

    @Override
    public void updateCreditDataOnDisbursement(Integer creditId, Double amount, Integer installments, Double tea, Integer entityUserId) throws Exception {
        queryForObjectTrx("select * from credit.upd_credit_condition(?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.NUMERIC, amount),
                new SqlParameterValue(Types.INTEGER, installments),
                new SqlParameterValue(Types.NUMERIC, tea),
                new SqlParameterValue(Types.INTEGER, entityUserId));
    }

    @Override
    public void updateObservation(int creditId, Integer creditObservationId) throws Exception {
        updateTrx("UPDATE credit.tb_credit set observation_reason_id = ? where credit_id = ?;",
                new SqlParameterValue(Types.INTEGER, creditObservationId),
                new SqlParameterValue(Types.INTEGER, creditId)
        );
    }

    @Override
    public List<TarjetasPeruanasCreditActivation> getLigoCardReports() throws Exception {
        JSONArray jsonArray = queryForObjectTrx("select * from credit.get_ligo_report()", JSONArray.class);
        if (jsonArray == null) {
            return new ArrayList<>();
        }

        List<TarjetasPeruanasCreditActivation> tarjetasPeruanasCreditActivationList = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); ++i) {
                TarjetasPeruanasCreditActivation tarjetasPeruanasCreditActivation = new TarjetasPeruanasCreditActivation();
                tarjetasPeruanasCreditActivation.fillFromDb(jsonArray.getJSONObject(i));
                tarjetasPeruanasCreditActivationList.add(tarjetasPeruanasCreditActivation);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tarjetasPeruanasCreditActivationList;
    }

    @Override
    public List<AutoplanCreditActivation> getAutoplanReports() throws Exception {
        JSONArray jsonArray = queryForObjectTrx("select * from credit.get_autoplan_report()", JSONArray.class);
        if (jsonArray == null) {
            return new ArrayList<>();
        }

        List<AutoplanCreditActivation> autoplanCreditActivationList = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); ++i) {
                AutoplanCreditActivation autoplanCreditActivation = new AutoplanCreditActivation();
                autoplanCreditActivation.fillFromDb(jsonArray.getJSONObject(i));
                autoplanCreditActivationList.add(autoplanCreditActivation);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return autoplanCreditActivationList;
    }

    @Override
    public List<Integer> getCreditsByPersonFiltered(int personId, List<Integer> products, List<Integer> statuses) throws Exception {
        JSONObject params = new JSONObject();
        params.put("product_id", products != null ? new JSONArray(new Gson().toJson(products)) : null);
        params.put("credit_status_id", statuses != null ? new JSONArray(new Gson().toJson(statuses)) : null);

        JSONArray jsonArray = queryForObjectTrx("select credit.get_credits_filtered(?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.OTHER, params.toString()));
        if (jsonArray == null) {
            return new ArrayList<>();
        }

        return new Gson().fromJson(jsonArray.toString(), new TypeToken<ArrayList<Integer>>() {}.getType());
    }

    @Override
    public List<CreditBancoDelSolExtranetPainter> getEntityCreditsByLoggedUserId(Integer userId,  Date startDate, Date endDate, String query, Integer offset, Integer limit, Locale locale) throws Exception {
        return getEntityCreditsByLoggedUserId(userId, startDate, endDate, query, offset, limit, locale, false, null, null, null);
    }

    @Override
    public List<CreditBancoDelSolExtranetPainter> getEntityCreditsByLoggedUserId(Integer userId,  Date startDate, Date endDate, String query, Integer offset, Integer limit, Locale locale, boolean onlyIds,List<Integer> productsIds,Integer minProgress, Integer maxProgress) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_not_originated_loan_applications_by_entity_user_id(?,?,?,?,?,?,?,?,?,?);", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.VARCHAR, query),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.BOOLEAN, onlyIds),
                new SqlParameterValue(productsIds == null ? Types.NULL : Types.OTHER, productsIds == null ? null : new Gson().toJson(productsIds)),
                new SqlParameterValue(Types.INTEGER, minProgress),
                new SqlParameterValue(Types.INTEGER, maxProgress)
        );
        if (dbArray == null)
            return null;

        List<CreditBancoDelSolExtranetPainter> credits = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            CreditBancoDelSolExtranetPainter credit = new CreditBancoDelSolExtranetPainter();
            credit.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            credits.add(credit);
        }
        return credits;
    }

    @Override
    public Pair<Integer, Double> getEntityCreditsByLoggedUserIdCount(Integer userId,  Date startDate, Date endDate, String query, List<Integer> productsIds,Integer minProgress, Integer maxProgress, Locale locale) throws Exception {
        if(productsIds != null && productsIds.isEmpty()) productsIds = null;
        JSONObject dbJson = queryForObjectTrx("select * from credit.get_not_originated_loan_applications_by_entity_user_id_count(?,?,?,?,?,?,?);", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.VARCHAR, query),
                new SqlParameterValue(productsIds == null ? Types.NULL : Types.OTHER, productsIds == null ? null : new Gson().toJson(productsIds)),
                new SqlParameterValue(Types.INTEGER, minProgress),
                new SqlParameterValue(Types.INTEGER, maxProgress)
        );
        if (dbJson == null)
            return Pair.of(0, 0.0);

        return Pair.of(JsonUtil.getIntFromJson(dbJson, "count", 0), null);
    }

    @Override
    public JSONObject getBancoDelSolRiskReport(Integer loanApplicatoinId) throws Exception {

        String query = null;

        if(Configuration.hostEnvIsProduction()){
            query = "select * from credit.get_validate_date_bds(?)";
        }else{
            query = "select * from credit.get_validate_date_bds_temp(?)";
        }
        return queryForObjectTrx(query, JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicatoinId));

    }

    @Override
    public void rejectAllLoanApplicationFraudAlerts(Integer loanApplicationId, Integer sysuserId) {
        rejectAllLoanApplicationFraudAlerts(loanApplicationId,sysuserId,null);
    }

    @Override
    public void rejectAllLoanApplicationFraudAlerts(Integer loanApplicationId, Integer sysuserId, Integer entityUserId) {
        queryForObjectTrx("select credit.reject_all_fraud_alerts_by_loan_application_id(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, sysuserId),
                new SqlParameterValue(Types.INTEGER, entityUserId)
                );
    }

    @Override
    public void returnCreditToLoanApplication(Integer creditId) {
        queryForObjectTrx("select * from credit.return_credit_to_application(?)", String.class,
                new SqlParameterValue(Types.INTEGER, creditId));
    }


    @Override
    public void updateTCEA(int creditId, double effectiveAnnualCostRate) throws Exception {
        updateTrx("UPDATE credit.tb_credit SET effective_annual_cost_rate = ? WHERE credit_id = ?;",
                new SqlParameterValue(Types.DOUBLE, effectiveAnnualCostRate),
                new SqlParameterValue(Types.INTEGER, creditId));
    }

    @Override
    public void updateSolvenTCEA(int creditId, double solvenEffectiveAnnualCostRate) throws Exception {
        updateTrx("UPDATE credit.tb_credit SET effective_annual_cost_rate_solven = ? WHERE credit_id = ?;",
                new SqlParameterValue(Types.DOUBLE, solvenEffectiveAnnualCostRate),
                new SqlParameterValue(Types.INTEGER, creditId));
    }

    @Override
    public void updateEntityCustomData(int creditId, JSONObject entityCustomData) {
        updateTrx("UPDATE credit.tb_credit set credit_entity_custom_data = ? where credit_id = ?",
                new SqlParameterValue(Types.OTHER, entityCustomData != null ? entityCustomData.toString() : null),
                new SqlParameterValue(Types.INTEGER, creditId));
    }

    @Override
    public void returnBDSCreditToLoanApplication(int creditId) throws Exception {
        queryForObjectTrx("select * from credit.return_bds_credit_to_application(?)", String.class,
                new SqlParameterValue(Types.INTEGER, creditId));
    }

    @Override
    public List<BanbifTcLeadLoan> getBanbifLeadCreditCardLoan(Integer entityUserId, Date startDate, Date endDate, Integer filterType, Locale locale, Integer offset, Integer limit, String search) throws Exception {
        if(offset != null && offset < 0) offset = 0;
        if(limit != null && limit < 0) limit = null;
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_banbif_lead_credit_card_loans(?, ?, ?, ?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.INTEGER, entityUserId),
                new SqlParameterValue(Types.INTEGER, filterType),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit)
                );
        if (dbArray == null)
            return null;

        List<BanbifTcLeadLoan> list = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            BanbifTcLeadLoan da = new BanbifTcLeadLoan();
            da.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            list.add(da);
        }
        return list;
    }

    @Override
    public Integer getBanbifLeadCreditCardLoanCount(Integer entityUserId, Date startDate, Date endDate, String search, Integer filterType, Locale locale) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_banbif_lead_credit_card_loans_count(?, ?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.INTEGER, entityUserId),
                new SqlParameterValue(Types.INTEGER, filterType)
        );
        if (dbArray == null)
            return 0;

        for (int i = 0; i < dbArray.length(); i++) {
            return JsonUtil.getIntFromJson(dbArray.getJSONObject(i), "count", 0);
        }

        return 0;
    }

    @Override
    public List<ReportEntityExtranetTrayReport> getExtranetEntityLoansForReport(Integer[] loanIds, Locale locale) {
        JSONArray dbJson = queryForObjectTrx("select * from credit.rp_entity_extranet_tray_report(?::JSON);", JSONArray.class,
                new SqlParameterValue(Types.OTHER, new Gson().toJson(loanIds))
        );
        if (dbJson == null) {
            return null;
        }
        List<ReportEntityExtranetTrayReport> loans = new ArrayList<>();
        try {
            for (int i = 0; i < dbJson.length(); i++) {
                ReportEntityExtranetTrayReport loan = new ReportEntityExtranetTrayReport();
                loan.fillFromDb(dbJson.getJSONObject(i), catalogService, locale);
                loans.add(loan);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return loans;
    }

    @Override
    public List<CreditBancoDelSolExtranetPainter> getEntityRejectedLoanApplications(Integer entityId,  Date startDate, Date endDate, String query, Integer offset, Integer limit, Locale locale, boolean onlyIds, List<Integer> productIds, List<String> rejectedReasonIds) throws Exception {
        if(productIds == null) productIds = new ArrayList<>();
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_rejected_loan_application_by_entity(?,?,?,?,?::json,?,?,?,?);", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.VARCHAR, query),
                new SqlParameterValue(productIds == null ? Types.NULL : Types.OTHER, productIds == null ? null : new Gson().toJson(productIds)),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.BOOLEAN, onlyIds),
                new SqlParameterValue(rejectedReasonIds == null ? Types.NULL : Types.OTHER, rejectedReasonIds == null ? null : new Gson().toJson(rejectedReasonIds))
        );
        if (dbArray == null)
            return null;

        List<CreditBancoDelSolExtranetPainter> credits = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            CreditBancoDelSolExtranetPainter credit = new CreditBancoDelSolExtranetPainter();
            credit.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            credits.add(credit);
        }
        return credits;
    }

    @Override
    public Pair<Integer, Double> getEntityRejectedLoanApplicationsCount(Integer entityId,  Date startDate, Date endDate, String query, List<Integer> productCategoryId,List<String> rejectedReason, Locale locale) throws Exception {
        if(productCategoryId == null) productCategoryId = new ArrayList<>();
        JSONObject dbJson = queryForObjectTrx("select * from credit.get_rejected_loan_application_by_entity_count(?, ?, ?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.VARCHAR, query),
                new SqlParameterValue(productCategoryId == null ? Types.NULL : Types.OTHER, productCategoryId == null ? null : new Gson().toJson(productCategoryId)),
                new SqlParameterValue(rejectedReason == null ? Types.NULL : Types.OTHER, rejectedReason == null ? null : new Gson().toJson(rejectedReason))
        );
        if (dbJson == null)
            return Pair.of(0, 0.0);

        return Pair.of(JsonUtil.getIntFromJson(dbJson, "count", 0), null);
    }

    @Override
    public List<LoanApplicationToEvaluationExtranetPainter> getEntityToEvaluateLoanApplications(Integer entityId, Date startDate, Date endDate, String query,  Date offerStartDate, Date offerEndDate, Integer[] analyst, Integer[] products, Integer[] progress, Integer offset, Integer limit, boolean onlyIds, Locale locale) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_to_evaluate_loan_application_by_entity(?,?,?,?,?,?,?,?,?,?,?,?);", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.DATE, offerStartDate),
                new SqlParameterValue(Types.DATE, offerEndDate),
                new SqlParameterValue(Types.OTHER, analyst != null ? new Gson().toJson(analyst) : null),
                new SqlParameterValue(Types.OTHER, products != null ? new Gson().toJson(products) : null),
                new SqlParameterValue(Types.OTHER, progress != null ? new Gson().toJson(progress) : null),
                new SqlParameterValue(Types.VARCHAR, query),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.BOOLEAN, onlyIds)
        );
        if (dbArray == null)
            return null;

        List<LoanApplicationToEvaluationExtranetPainter> loans = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            LoanApplicationToEvaluationExtranetPainter loan = new LoanApplicationToEvaluationExtranetPainter();
            loan.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            loans.add(loan);
        }
        return loans;
    }

    @Override
    public Pair<Integer, Double> getEntityToEvaluateLoanApplicationsCount(Integer entityId,  Date startDate, Date endDate, String query,  Date offerStartDate, Date offerEndDate, Integer[] analyst, Integer[] products, Integer[] progress, Locale locale) {
        JSONObject dbJson = queryForObjectTrx("select * from credit.get_to_evaluate_loan_application_by_entity_count(?,?,?,?,?,?,?,?,?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.DATE, offerStartDate),
                new SqlParameterValue(Types.DATE, offerEndDate),
                new SqlParameterValue(Types.OTHER, analyst != null ? new Gson().toJson(analyst) : null),
                new SqlParameterValue(Types.OTHER, products != null ? new Gson().toJson(products) : null),
                new SqlParameterValue(Types.OTHER, progress != null ? new Gson().toJson(progress) : null),
                new SqlParameterValue(Types.VARCHAR, query)
        );
        if (dbJson == null)
            return Pair.of(0, 0.0);

        return Pair.of(JsonUtil.getIntFromJson(dbJson, "count", 0), null);
    }

    @Override
    public List<LoanGatewayPaymentMethod> getLoanPaymentMethod(Integer loanApplicationId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_loan_collection_payment_methods(?);", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId)
        );
        if (dbArray == null)
            return new ArrayList<>();

        List<LoanGatewayPaymentMethod> list = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            LoanGatewayPaymentMethod data = new LoanGatewayPaymentMethod();
            data.fillFromDb(dbArray.getJSONObject(i), catalogService);
            list.add(data);
        }
        return list;
    }

    @Override
    public List<LoanGatewayPaymentMethod> getLoanPaymentMethodByTransactionCode(String code, Integer collectionPaymentMethodId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_loan_payment_method_by_transaction_code(?,?);", JSONArray.class,
                new SqlParameterValue(Types.VARCHAR, code),
                new SqlParameterValue(Types.INTEGER, collectionPaymentMethodId)
        );
        if (dbArray == null)
            return new ArrayList<>();

        List<LoanGatewayPaymentMethod> list = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            LoanGatewayPaymentMethod data = new LoanGatewayPaymentMethod();
            data.fillFromDb(dbArray.getJSONObject(i), catalogService);
            list.add(data);
        }
        return list;
    }

    @Override
    public void insertLoanPaymentMethod(Integer loanApplicationId, Integer collectionPaymentMethodId, Date expirationDate, String paymentVars, String paymentResponse){
        queryForObjectTrx("INSERT INTO credit.tb_loan_collection_payment_method (loan_application_id, payment_method_id, expiration_date, js_payment_vars, js_payment_response) VALUES (?,?,?,?,?) returning loan_collection_payment_id;", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, collectionPaymentMethodId),
                new SqlParameterValue(expirationDate != null ?Types.TIMESTAMP : Types.NULL , expirationDate),
                new SqlParameterValue(paymentVars != null ?Types.OTHER : Types.NULL , paymentVars),
                new SqlParameterValue(paymentResponse != null ? Types.OTHER : Types.NULL, paymentResponse)
        );
    }

    @Override
    public void inactivatePaymentMethodOfLoan(Integer loanApplicationId, Integer collectionPaymentMethodId) {
        updateTrx("UPDATE credit.tb_loan_collection_payment_method SET is_active = FALSE WHERE loan_application_id = ? AND payment_method_id = ? AND is_active = TRUE AND COALESCE(IS_PAYED, FALSE) = FALSE;",
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, collectionPaymentMethodId)
        );
    }

    @Override
    public void updatePaymentMethodOfLoanPayed(Integer loanCollectionPaymentMethodId, String response ) {
        updateTrx("UPDATE credit.tb_loan_collection_payment_method SET is_payed = TRUE, payment_date = ?, js_payment_response = ?  WHERE loan_collection_payment_id = ?",
                new SqlParameterValue(Types.TIMESTAMP, new Date()),
                new SqlParameterValue(Types.OTHER, response),
                new SqlParameterValue(Types.INTEGER, loanCollectionPaymentMethodId)
        );
    }

    @Override
    public void updatePaymentMethodOfLoanResponse(Integer loanCollectionPaymentMethodId, String response ) {
        updateTrx("UPDATE credit.tb_loan_collection_payment_method SET js_payment_response = ?  WHERE loan_collection_payment_id = ?",
                new SqlParameterValue(Types.OTHER, response),
                new SqlParameterValue(Types.INTEGER, loanCollectionPaymentMethodId)
        );
    }

    @Override
    public UTMValue getUTMValuesFromEntity(Integer entityId) {
        JSONObject dbJson = queryForObjectTrx("select * from credit.get_utm_values_from_entity(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, entityId)
        );
        if (dbJson == null) return null;
        UTMValue utmValue = new UTMValue();
        List<String> keys = Arrays.asList("source", "content", "campaign", "medium");
        for (String key : keys) {
            if(dbJson.has(key) && dbJson.get(key) != null){
                JSONArray dbArray = JsonUtil.getJsonArrayFromJson(dbJson,key, new JSONArray());
                for (int i = 0; i < dbArray.length(); i++) {
                    UTMValue.UTMValueDetail utmValueDetail = new UTMValue.UTMValueDetail();
                    utmValueDetail.setValue(dbArray.get(i).toString());
                    if(utmValueDetail.getValue() != null && utmValueDetail.getValue().isEmpty()) continue;
                    try{
                        utmValueDetail.setText(URLDecoder.decode( dbArray.get(i).toString(), "UTF-8" ));
                    }
                    catch (Exception e){
                        utmValueDetail.setText(dbArray.get(i).toString());
                    }
                    switch (key){
                        case "source":
                            utmValue.getSources().add(utmValueDetail);
                            break;
                        case "content":
                            utmValue.getContents().add(utmValueDetail);
                            break;
                        case "campaign":
                            utmValue.getCampaigns().add(utmValueDetail);
                            break;
                        case "medium":
                            utmValue.getMediums().add(utmValueDetail);
                            break;
                    }
                }
            }
        }
        return utmValue;
    }

    @Override
    public List<JSONObject> getLoanProgressesFromEntity(Integer entityId, Boolean processing) {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_loan_progress_by_entity(?,?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.BOOLEAN, processing)
        );
        if (dbArray == null) return null;
        List<JSONObject> jsonValues = new ArrayList<>();

        for (int i = 0; i < dbArray.length(); i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", dbArray.get(i));
            jsonObject.put("text", dbArray.getInt(i));
        }


        return jsonValues;
    }

    @Override
    public List<GatewayLoanApplicationExtranetPainter> getCollectionLoanApplication(Integer entityId, Date startDate, Date endDate, Integer[] loanStatus, Integer[] creditStatus, Integer[] entityProductParams, Integer offset, Integer limit, String search, Boolean onlyIds, Locale locale) throws Exception {
        if(offset != null && offset < 0) offset = 0;
        if(limit != null && limit < 0) limit = null;
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_entity_collection_loans(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.OTHER, loanStatus != null ? new Gson().toJson(loanStatus) : null),
                new SqlParameterValue(Types.OTHER, creditStatus != null ? new Gson().toJson(creditStatus) : null),
                new SqlParameterValue(Types.OTHER, entityProductParams != null ? new Gson().toJson(entityProductParams) : null),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.VARCHAR, search),
                new SqlParameterValue(Types.BOOLEAN, onlyIds)
        );
        if (dbArray == null)
            return null;

        List<GatewayLoanApplicationExtranetPainter> list = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            GatewayLoanApplicationExtranetPainter da = new GatewayLoanApplicationExtranetPainter();
            da.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            list.add(da);
        }
        return list;
    }

    @Override
    public Pair<Integer, Double>  getCollectionLoanApplicationCount(Integer entityId, Date startDate, Date endDate, Integer[] loanStatus, Integer[] creditStatus, String search, Integer[] entityProductParams, Locale locale) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.get_entity_collection_loans_count(?, ?, ?, ?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.OTHER, loanStatus != null ? new Gson().toJson(loanStatus) : null),
                new SqlParameterValue(Types.OTHER, creditStatus != null ? new Gson().toJson(creditStatus) : null),
                new SqlParameterValue(Types.OTHER, entityProductParams != null ? new Gson().toJson(entityProductParams) : null),
                new SqlParameterValue(Types.VARCHAR, search)
        );
        if (dbJson == null)
            return Pair.of(0, 0.0);

        return Pair.of(JsonUtil.getIntFromJson(dbJson, "count", 0), JsonUtil.getDoubleFromJson(dbJson, "sum", 0.0));
    }


    @Override
    public EntityAllRejectionReasons getEntityAllRejectionReason(Integer entityId,Locale locale) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select credit.get_all_rejection_reasons_by_entity(?);", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, entityId)
        );
        if (dbJson == null) return null;

        EntityAllRejectionReasons  entityAllRejectionReason = new EntityAllRejectionReasons();
        entityAllRejectionReason.fillFromDb(dbJson,catalogService,locale);

        return entityAllRejectionReason;
    }

    @Override
    public void updateCreditOriginalScheduleDueDateAndAmount(int creditId, int installmentId, Date dueDate, Double installmentAmount) throws Exception {
        updateTrx("update credit.tb_original_schedule set due_date = ?, installment_amount = ? where credit_id = ? and installment_id = ?",
                new SqlParameterValue(Types.TIMESTAMP, dueDate),
                new SqlParameterValue(Types.NUMERIC, installmentAmount),
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.INTEGER, installmentId));
    }

    @Override
    public void updateCreditOriginalScheduleData(int creditId, int installmentId, Date dueDate, Double installmentAmount, Double installmentCapital, Double interest, Double insurance) {
        updateTrx("update credit.tb_original_schedule set due_date = ?, installment_amount = ?, installment_capital = ?, interest = ?, insurance = ?  where credit_id = ? and installment_id = ?",
                new SqlParameterValue(Types.TIMESTAMP, dueDate),
                new SqlParameterValue(Types.NUMERIC, installmentAmount),
                new SqlParameterValue(Types.NUMERIC, installmentCapital),
                new SqlParameterValue(Types.NUMERIC, interest),
                new SqlParameterValue(Types.NUMERIC, insurance),
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.INTEGER, installmentId)
        );
    }

    @Override
    public void updateManagementScheduleData(int creditId, int installmentId, Date dueDate, Double installmentAmount, Double installmentCapital, Double interest, Double insurance) {
        updateTrx("update credit.tb_management_schedule set due_date = ?, installment_amount = ?, installment_capital = ?, interest = ?, insurance = ?  where credit_id = ? and installment_id = ?",
                new SqlParameterValue(Types.TIMESTAMP, dueDate),
                new SqlParameterValue(Types.NUMERIC, installmentAmount),
                new SqlParameterValue(Types.NUMERIC, installmentCapital),
                new SqlParameterValue(Types.NUMERIC, interest),
                new SqlParameterValue(Types.NUMERIC, insurance),
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.INTEGER, installmentId)

        );
    }

    @Override
    public void updateInstallmentAmountAndAvg(int creditId, Double installmentAmount, Double installmentAmountAvg) {
        updateTrx("update credit.tb_credit set installment_amount = ?, installment_amount_avg = ? where credit_id = ?",
                new SqlParameterValue(Types.NUMERIC, installmentAmount),
                new SqlParameterValue(Types.NUMERIC, installmentAmountAvg),
                new SqlParameterValue(Types.INTEGER, creditId)
        );
    }

}
