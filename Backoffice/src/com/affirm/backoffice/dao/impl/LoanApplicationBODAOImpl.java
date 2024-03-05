package com.affirm.backoffice.dao.impl;

import com.affirm.backoffice.dao.LoanApplicationBODAO;
import com.affirm.backoffice.model.LoanApplicationSummaryBoPainter;
import com.affirm.backoffice.model.SelfEvaluationBoPainter;
import com.affirm.backoffice.util.PaginationMetadata;
import com.affirm.backoffice.util.PaginationWrapper;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.service.CatalogService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */

@Repository("loanApplicationBoDao")
public class LoanApplicationBODAOImpl extends JsonResolverDAO implements LoanApplicationBODAO {

    @Autowired
    private CatalogService catalogService;


    @Autowired
    private MessageSource messageSource;

    @Override
    public PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsSummaries(String country,
                                                                                           String amountFilterFrom,
                                                                                           String amountFilterTo,
                                                                                           Date creationFrom,
                                                                                           Date creationTo,
                                                                                           String documentNumber,
                                                                                           Integer entity,
                                                                                           Integer employer,
                                                                                           Integer reason,
                                                                                           String analyst,
                                                                                           Locale locale, int viewId, int offset, int limit) throws Exception {
        JSONArray dbJson = queryForObject("select * from credit.bo_get_loan_applications_view(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::JSON)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, viewId),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.DATE, creationFrom),
                new SqlParameterValue(Types.DATE, creationTo),
                new SqlParameterValue(Types.INTEGER, entity),
                new SqlParameterValue(Types.INTEGER, employer),
                new SqlParameterValue(Types.INTEGER, reason),
                new SqlParameterValue(Types.NUMERIC, amountFilterFrom),
                new SqlParameterValue(Types.NUMERIC, amountFilterTo),
                new SqlParameterValue(Types.VARCHAR, analyst),
                new SqlParameterValue(Types.OTHER, country));
        if (dbJson == null) {
            return null;
        }

        PaginationWrapper<LoanApplicationSummaryBoPainter> wrapper =
                new PaginationWrapper<>(LoanApplicationSummaryBoPainter.class, new PaginationMetadata(offset, limit));
        wrapper.fillFromDb(dbJson, catalogService, null, locale);
        return wrapper;
    }

    @Override
    public PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsToManage(Date creationFrom,
                                                                                          Date creationTo,
                                                                                          String documentNumber,
                                                                                          Integer entity,
                                                                                          String analyst,
                                                                                          String countryId,
                                                                                          Locale locale,
                                                                                          Boolean assistedProcess,
                                                                                          Boolean isBranded,
                                                                                          Integer hoursNextContact,
                                                                                          int offset,
                                                                                          int limit) throws Exception {
        JSONArray dbJson = queryForObject("select * from credit.bo_get_loan_applications_tracking(?, ?, ?, ?, ?, ?, ?, ?::JSON, ?, ?, ?)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.DATE, creationFrom),
                new SqlParameterValue(Types.DATE, creationTo),
                new SqlParameterValue(Types.INTEGER, entity),
                new SqlParameterValue(Types.VARCHAR, analyst),
                new SqlParameterValue(Types.OTHER, countryId),
                new SqlParameterValue(Types.BOOLEAN, assistedProcess),
                new SqlParameterValue(Types.BOOLEAN, isBranded),
                new SqlParameterValue(Types.INTEGER, hoursNextContact));
        if (dbJson == null) {
            return null;
        }

        PaginationWrapper<LoanApplicationSummaryBoPainter> wrapper =
                new PaginationWrapper<>(LoanApplicationSummaryBoPainter.class, new PaginationMetadata(offset, limit));
        wrapper.fillFromDb(dbJson, catalogService, null, locale);
        return wrapper;
    }

    @Override
    public LoanApplicationSummaryBoPainter getLoanApplicationsToManageById(int loanApplicationId, Locale locale) throws Exception {
        JSONObject dbJson = queryForObject("select * from credit.bo_get_loan_applications_tracking_v2(?)", JSONObject.class, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return null;
        }

        LoanApplicationSummaryBoPainter application = new LoanApplicationSummaryBoPainter();
        application.fillFromDb(dbJson, catalogService, messageSource, locale);
        return application;
    }

    @Override
    public PaginationWrapper<SelfEvaluationBoPainter> getLoanApplicationsSelfEvaluation(String country,
                                                                                        String amountFilterFrom,
                                                                                        String amountFilterTo,
                                                                                        Date creationFrom,
                                                                                        Date creationTo,
                                                                                        String documentNumber,
                                                                                        Integer reason,
                                                                                        Integer score,
                                                                                        Locale locale, int offset, int limit) throws Exception {
        JSONArray dbJson = queryForObject("select * from evaluation.bo_get_self_evaluation(?,?,?,?,?,?,?,?,?,?::JSON)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.DATE, creationFrom),
                new SqlParameterValue(Types.DATE, creationTo),
                new SqlParameterValue(Types.INTEGER, reason),
                new SqlParameterValue(Types.NUMERIC, amountFilterFrom),
                new SqlParameterValue(Types.NUMERIC, amountFilterTo),
                new SqlParameterValue(Types.INTEGER, score),
                new SqlParameterValue(Types.OTHER, country));
        if (dbJson == null) {
            return null;
        }

        PaginationWrapper<SelfEvaluationBoPainter> wrapper = new PaginationWrapper<>(SelfEvaluationBoPainter.class, new PaginationMetadata(offset, limit));
        wrapper.fillFromDb(dbJson, catalogService, null, locale);
        return wrapper;
    }

    @Override
    public List<LoanApplicationSummaryBoPainter> getLoanApplicationsToAudit(String country, Locale locale) throws Exception {
        JSONArray dbJson = queryForObject("select * from credit.bo_get_pending_loan_application_audit(?::JSON)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.OTHER, country));
        if (dbJson == null) {
            return null;
        }

        List<LoanApplicationSummaryBoPainter> painters = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            LoanApplicationSummaryBoPainter painter = new LoanApplicationSummaryBoPainter();
            painter.fillFromDb(dbJson.getJSONObject(i), catalogService, null, locale);
            painters.add(painter);
        }
        return painters;
    }

    @Override
    public PaginationWrapper<LoanApplicationSummaryBoPainter> getPinLoanApplicationsToManage(String countryId, Date creationFrom, Date creationTo, String documentNumber, Integer entity, String analyst, Boolean isBranded, Locale locale, int offset, int limit) throws Exception {
        JSONArray dbJson = queryForObject("select * from credit.bo_get_loan_applications_tracking_pin(?, ?, ?, ?, ?, ?, ?, ?::JSON, ?)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.DATE, creationFrom),
                new SqlParameterValue(Types.DATE, creationTo),
                new SqlParameterValue(Types.INTEGER, entity),
                new SqlParameterValue(Types.VARCHAR, analyst),
                new SqlParameterValue(Types.OTHER, countryId),
                new SqlParameterValue(Types.BOOLEAN, isBranded));
        if (dbJson == null) {
            return null;
        }

        PaginationWrapper<LoanApplicationSummaryBoPainter> wrapper =
                new PaginationWrapper<>(LoanApplicationSummaryBoPainter.class, new PaginationMetadata(offset, limit));
        wrapper.fillFromDb(dbJson, catalogService, null, locale);
        return wrapper;
    }

    @Override
    public LoanApplicationSummaryBoPainter getPinLoanApplicationToManageById(int loanApplicationId, Locale locale) throws Exception {
        JSONObject dbJson = queryForObject("select * from credit.bo_get_loan_applications_tracking_pin(?)", JSONObject.class, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbJson == null) {
            return null;
        }

        LoanApplicationSummaryBoPainter wrapper = new LoanApplicationSummaryBoPainter();
        wrapper.fillFromDb(dbJson, catalogService, null, locale);
        return wrapper;
    }

    @Override
    public PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsToManagev2(Date creationFrom,
                                                                                            Date creationTo,
                                                                                            String documentNumber,
                                                                                            Integer entity,
                                                                                            String analyst,
                                                                                            String countryId,
                                                                                            Locale locale,
                                                                                            Boolean assistedProcess,
                                                                                            Boolean isBranded,
                                                                                            Integer hoursNextContact,
                                                                                            int offset,
                                                                                            int limit) throws Exception {
        JSONArray dbJson = queryForObject("select * from credit.bo_get_loan_applications_tracking_v2(?, ?, ?, ?, ?, ?, ?, ?::JSON, ?, ?, ?)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.DATE, creationFrom),
                new SqlParameterValue(Types.DATE, creationTo),
                new SqlParameterValue(Types.INTEGER, entity),
                new SqlParameterValue(Types.VARCHAR, analyst),
                new SqlParameterValue(Types.OTHER, countryId),
                new SqlParameterValue(Types.BOOLEAN, assistedProcess),
                new SqlParameterValue(Types.BOOLEAN, isBranded),
                new SqlParameterValue(Types.INTEGER, hoursNextContact));
        if (dbJson == null) {
            return null;
        }

        PaginationWrapper<LoanApplicationSummaryBoPainter> wrapper = new PaginationWrapper<>(LoanApplicationSummaryBoPainter.class, new PaginationMetadata(offset, limit));
        wrapper.fillFromDb(dbJson, catalogService, messageSource, locale);
        return wrapper;
    }

    @Override
    public PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsListPreApproved(Date creationFrom,
                                                                                                 Date creationTo,
                                                                                                 String amountFrom, String amountTo,
                                                                                                 String documentNumber,
                                                                                                 Integer entity,
                                                                                                 Integer employer,
                                                                                                 Integer reason,
                                                                                                 String country,
                                                                                                 String analyst,
                                                                                                 Locale locale,
                                                                                                 int offset,
                                                                                                 int limit) throws Exception {
        JSONArray dbJson = queryForObject("select * from credit.bo_get_loan_application_list_preapproved(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::json)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.DATE, creationFrom),
                new SqlParameterValue(Types.DATE, creationTo),
                new SqlParameterValue(Types.INTEGER, entity),
                new SqlParameterValue(Types.INTEGER, employer),
                new SqlParameterValue(Types.INTEGER, reason),
                new SqlParameterValue(Types.NUMERIC, amountFrom),
                new SqlParameterValue(Types.NUMERIC, amountTo),
                new SqlParameterValue(Types.VARCHAR, analyst),
                new SqlParameterValue(Types.OTHER, country)
        );
        if (dbJson == null) {
            return null;
        }

        PaginationWrapper<LoanApplicationSummaryBoPainter> wrapper = new PaginationWrapper<>(LoanApplicationSummaryBoPainter.class, new PaginationMetadata(offset, limit));
        wrapper.fillFromDb(dbJson, catalogService, messageSource, locale);
        return wrapper;
    }

    @Override
    public LoanApplicationSummaryBoPainter getLoanApplicationsPreApprovedById(int loanApplicationId, Locale locale) throws Exception {
        JSONArray dbJson = queryForObject("select * from credit.bo_get_loan_application_list_preapproved(?)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, loanApplicationId)
        );

        if (dbJson == null) {
            return null;
        }

        LoanApplicationSummaryBoPainter application = new LoanApplicationSummaryBoPainter();
        application.fillFromDb(dbJson.getJSONObject(0), catalogService, messageSource, locale);
        return application;
    }

    @Override
    public PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsListCrossSelling(Date creationFrom,
                                                                                                  Date creationTo,
                                                                                                  String amountFrom, String amountTo,
                                                                                                  String documentNumber,
                                                                                                  Integer entity,
                                                                                                  Integer employer,
                                                                                                  Integer reason,
                                                                                                  String country,
                                                                                                  String analyst,
                                                                                                  Locale locale,
                                                                                                  int offset,
                                                                                                  int limit) throws Exception {
        JSONArray dbJson = queryForObject("select * from credit.bo_get_loan_application_list_cross_selling(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::json)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.DATE, creationFrom),
                new SqlParameterValue(Types.DATE, creationTo),
                new SqlParameterValue(Types.INTEGER, entity),
                new SqlParameterValue(Types.INTEGER, employer),
                new SqlParameterValue(Types.INTEGER, reason),
                new SqlParameterValue(Types.NUMERIC, amountFrom),
                new SqlParameterValue(Types.NUMERIC, amountTo),
                new SqlParameterValue(Types.VARCHAR, analyst),
                new SqlParameterValue(Types.OTHER, country)
        );
        if (dbJson == null) {
            return null;
        }

        PaginationWrapper<LoanApplicationSummaryBoPainter> wrapper = new PaginationWrapper<>(LoanApplicationSummaryBoPainter.class, new PaginationMetadata(offset, limit));
        wrapper.fillFromDb(dbJson, catalogService, messageSource, locale);
        return wrapper;
    }

    @Override
    public PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsListManagement(Integer viewId, Date creationFrom,
                                                                                                Date creationTo,
                                                                                                String amountFrom, String amountTo,
                                                                                                String documentNumber,
                                                                                                Integer entity,
                                                                                                Integer employer,
                                                                                                Integer reason,
                                                                                                String country,
                                                                                                String analyst,
                                                                                                Locale locale,
                                                                                                int offset,
                                                                                                int limit,
                                                                                                Integer hoursNextContact,
                                                                                                Integer question) throws Exception {
        JSONArray dbJson = queryForObject("select * from credit.bo_get_loan_application_list_management(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::json, ?, ?)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, viewId),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.DATE, creationFrom),
                new SqlParameterValue(Types.DATE, creationTo),
                new SqlParameterValue(Types.INTEGER, entity),
                new SqlParameterValue(Types.INTEGER, employer),
                new SqlParameterValue(Types.INTEGER, reason),
                new SqlParameterValue(Types.NUMERIC, amountFrom),
                new SqlParameterValue(Types.NUMERIC, amountTo),
                new SqlParameterValue(Types.VARCHAR, analyst),
                new SqlParameterValue(Types.OTHER, country),
                new SqlParameterValue(Types.INTEGER, hoursNextContact),
                new SqlParameterValue(Types.INTEGER, question)
        );
        if (dbJson == null) {
            return null;
        }

        PaginationWrapper<LoanApplicationSummaryBoPainter> wrapper = new PaginationWrapper<>(LoanApplicationSummaryBoPainter.class, new PaginationMetadata(offset, limit));
        wrapper.fillFromDb(dbJson, catalogService, messageSource, locale);
        return wrapper;
    }

    @Override
    public LoanApplicationSummaryBoPainter getManagementLoanApplicationById(int viewId, int loanApplicationId, Locale locale) throws Exception {
        JSONArray dbJson = queryForObject("select * from credit.bo_get_loan_application_list_management(?,?)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, viewId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId)
        );

        if (dbJson == null) {
            return null;
        }

        LoanApplicationSummaryBoPainter application = new LoanApplicationSummaryBoPainter();
        application.fillFromDb(dbJson.getJSONObject(0), catalogService, messageSource, locale);
        return application;
    }

    @Override
    public PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsListEvaluation(Date creationFrom,
                                                                                                Date creationTo,
                                                                                                String amountFrom, String amountTo,
                                                                                                String documentNumber,
                                                                                                Integer entity,
                                                                                                Integer employer,
                                                                                                Integer reason,
                                                                                                String country,
                                                                                                String analyst,
                                                                                                Locale locale,
                                                                                                int offset,
                                                                                                int limit) throws Exception {
        JSONArray dbJson = queryForObject("select * from credit.bo_get_loan_application_list_evaluation(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::json)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.DATE, creationFrom),
                new SqlParameterValue(Types.DATE, creationTo),
                new SqlParameterValue(Types.INTEGER, entity),
                new SqlParameterValue(Types.INTEGER, employer),
                new SqlParameterValue(Types.INTEGER, reason),
                new SqlParameterValue(Types.NUMERIC, amountFrom),
                new SqlParameterValue(Types.NUMERIC, amountTo),
                new SqlParameterValue(Types.VARCHAR, analyst),
                new SqlParameterValue(Types.OTHER, country)
        );
        if (dbJson == null) {
            return null;
        }

        PaginationWrapper<LoanApplicationSummaryBoPainter> wrapper = new PaginationWrapper<>(LoanApplicationSummaryBoPainter.class, new PaginationMetadata(offset, limit));
        wrapper.fillFromDb(dbJson, catalogService, messageSource, locale);
        return wrapper;
    }

    @Override
    public LoanApplicationSummaryBoPainter getEvaluationLoanApplicationById(int loanApplicationId, Locale locale) throws Exception {
        JSONArray dbJson = queryForObject("select * from credit.bo_get_loan_application_list_evaluation(?)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, loanApplicationId)
        );

        if (dbJson == null) {
            return null;
        }

        LoanApplicationSummaryBoPainter application = new LoanApplicationSummaryBoPainter();
        application.fillFromDb(dbJson.getJSONObject(0), catalogService, messageSource, locale);
        return application;
    }
}
