package com.affirm.backoffice.dao.impl;

import com.affirm.backoffice.dao.BackofficeDAO;
import com.affirm.backoffice.model.GeneralSearchResult;
import com.affirm.backoffice.model.OriginationReportPeriod;
import com.affirm.backoffice.model.ReportCreditGateway;
import com.affirm.backoffice.model.ReportNoHolding;
import com.affirm.backoffice.util.PaginationMetadata;
import com.affirm.backoffice.util.PaginationWrapper;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.service.CatalogService;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
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

@Repository("backofficeDao")
public class BackofficeDAOImpl extends JsonResolverDAO implements BackofficeDAO {

    @Autowired
    private CatalogService catalogService;

    @Override
    public GeneralSearchResult generalSearch(String query, Locale locale) throws Exception {
        JSONArray dbArray = queryForObject("select * from credit.bo_search(?)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.VARCHAR, query));
        if (dbArray == null) {
            return null;
        }

        GeneralSearchResult result = new GeneralSearchResult();
        result.fillFromDb(dbArray, catalogService, locale);
        return result;
    }

    @Override
    public String getSharedSecret(String username){
        String secret = queryForObjectTrx("select * from security.get_tfa_secret(?)", String.class,
                new SqlParameterValue(Types.VARCHAR, username));
        return secret;
    }

    @Override
    public PaginationWrapper<ReportNoHolding> getNoHoldingReport(String country, Integer offset, Integer limit) throws Exception {
        JSONArray dbArray = queryForObject("select * from credit.get_tax_withholding(?::JSON)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.OTHER, country));
        if (dbArray == null) {
            return null;
        }

        PaginationWrapper<ReportNoHolding> reportNoHoldings = new PaginationWrapper<>(ReportNoHolding.class, new PaginationMetadata(offset, limit));
        reportNoHoldings.fillFromDb(dbArray, catalogService, null,null);
        return reportNoHoldings;
    }

    /*@Override
    public PaginationWrapper<ReportOrigination> getOriginationReport(String country, CatalogService catalogService, Locale locale, Date startDate, Date endDate, Integer offset, Integer limit) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.rp_credit_origination(?,?,?,?,?)", JSONArray.class,
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.OTHER, country),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit));
        if (dbArray == null) {
            return null;
        }

        PaginationWrapper<ReportOrigination> reportOriginations = new PaginationWrapper<>(ReportOrigination.class, new PaginationMetadata(offset, limit));
        reportOriginations.fillFromDb(dbArray, catalogService, null);
        return reportOriginations;
    }*/

    @Override
    public List<ReportCreditGateway> getCreditCollectionReport(String country, CatalogService catalogService, Locale locale, Date startDate, Date endDate) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.rp_credit_collection(?,?,?::JSON)", JSONArray.class,
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.OTHER, country));
        if (dbArray == null) {
            return null;
        }

        List<ReportCreditGateway> reportCreditGateways = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ReportCreditGateway reportCreditGateway = new ReportCreditGateway();
            reportCreditGateway.fillFromDb(dbArray.getJSONObject(i), catalogService);
            reportCreditGateways.add(reportCreditGateway);
        }
        return reportCreditGateways;
    }

    @Override
    public PaginationWrapper<ReportCreditGateway> getCreditCollectionReport(String country, CatalogService catalogService, Locale locale, Date startDate, Date endDate, Integer offset, Integer limit) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.rp_credit_collection(?,?,?,?,?)", JSONArray.class,
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.OTHER, country),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit));

        if (dbArray == null) {
            return null;
        }

        PaginationWrapper<ReportCreditGateway> reportCreditCollections = new PaginationWrapper<>(ReportCreditGateway.class, new PaginationMetadata(offset, limit));
        reportCreditCollections.fillFromDb(dbArray, catalogService, null,locale);
        return reportCreditCollections;
    }


    @Override
    public void updateSpeech(Integer entityProductParameterId, Integer speechTypeId, String speech) {
        updateTrx("UPDATE originator.ct_entity_product_parameter SET commercial_speech = case when ? = 1 then commercial_speech else ? end, " +
                        "welcome_speech = case when ? = 2 then welcome_speech else ? end WHERE entity_product_parameter_id = ?",
                new SqlParameterValue(Types.INTEGER, speechTypeId),
                new SqlParameterValue(Types.VARCHAR, speech),
                new SqlParameterValue(Types.INTEGER, speechTypeId),
                new SqlParameterValue(Types.VARCHAR, speech),
                new SqlParameterValue(Types.INTEGER, entityProductParameterId)
        );
    }


    @Override
    public List<OriginationReportPeriod> getOriginationProductReportPeriod(String country, Date period1From, Date period1To, Date period2From, Date period2To, Integer[] entities) throws Exception {
        JSONArray dbArray = queryForObject("SELECT * from credit.rp_dashboard_by_product(? ,?, ?, ?, ?, ?::JSON)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.DATE, period1From),
                new SqlParameterValue(Types.DATE, period1To),
                new SqlParameterValue(Types.DATE, period2From),
                new SqlParameterValue(Types.DATE, period2To),
                new SqlParameterValue(Types.OTHER, entities != null ? new Gson().toJson(entities) : null),
                new SqlParameterValue(Types.OTHER, country));
        if (dbArray == null) {
            return null;
        }

        List<OriginationReportPeriod> periods = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            OriginationReportPeriod period = new OriginationReportPeriod();
            period.fillFromDb(dbArray.getJSONObject(i), catalogService);
            periods.add(period);
        }
        return periods;
    }

    @Override
    public List<OriginationReportPeriod> getOriginationEntityProductReportPeriod(String country, CatalogService catalogService) throws Exception {
        JSONArray dbArray = queryForObject("SELECT * from credit.rp_dashboard_by_entity_product(?::JSON)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.OTHER, country));
        if (dbArray == null) {
            return null;
        }

        List<OriginationReportPeriod> periods = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            OriginationReportPeriod period = new OriginationReportPeriod();
            period.fillFromDb(dbArray.getJSONObject(i), catalogService);
            periods.add(period);
        }
        return periods;
    }

}
