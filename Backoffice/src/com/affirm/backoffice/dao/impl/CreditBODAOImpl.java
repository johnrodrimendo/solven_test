package com.affirm.backoffice.dao.impl;

import com.affirm.backoffice.dao.CreditBODAO;
import com.affirm.backoffice.model.CreditBoPainter;
import com.affirm.backoffice.model.CreditGatewayBoPainter;
import com.affirm.backoffice.model.CreditPendingDisbursementBoPainter;
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
import java.util.Date;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */

@Repository("creditBoDao")
public class CreditBODAOImpl extends JsonResolverDAO implements CreditBODAO {

    @Autowired
    private CatalogService catalogService;

    @Override
    public PaginationWrapper<CreditPendingDisbursementBoPainter> getPendigDisbursement(String country, String documentNumber, Integer productId, Integer entityId, Integer employerId, Locale locale, Integer offset, Integer limit) throws Exception {
        JSONArray dbJson = queryForObject("select * from credit.bo_get_pending_disbursement(?,?,?,?,?,?,?)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.INTEGER, productId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, employerId),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.OTHER, country));
        if (dbJson == null) {
            return null;
        }

        PaginationWrapper<CreditPendingDisbursementBoPainter> credits = new PaginationWrapper<>(CreditPendingDisbursementBoPainter.class, new PaginationMetadata(offset, limit));
        credits.fillFromDb(dbJson, catalogService, null, locale);
        return credits;
    }

    @Override
    public void registerDisbursementBuffer(int creditId) throws Exception {
        queryForObjectTrx("select * from credit.bo_register_disbursement(?)",
                String.class,
                new SqlParameterValue(Types.INTEGER, creditId)
                /*new SqlParameterValue(Types.DATE, date),
                new SqlParameterValue(Types.CHAR, paymentType),
                new SqlParameterValue(Types.INTEGER, signatureSysUserId),
                new SqlParameterValue(Types.VARCHAR, checkNumber),
                new SqlParameterValue(Types.INTEGER, sysUserId)*/);
    }

    @Override
     public PaginationWrapper<CreditBoPainter> getPendigDisbursementConfirmation(String country, Locale locale, Date startDate, Date endDate, String documentNumber, Integer productId, Integer entityId, Integer employerId, Integer limit, Integer offset) throws Exception {
        JSONArray dbJson = queryForObject("select * from credit.bo_get_pending_disbursement_confirmation(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.INTEGER, productId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, employerId),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.OTHER, country));
        if (dbJson == null) {
            return null;
        }

        PaginationWrapper<CreditBoPainter> credits = new PaginationWrapper<>(CreditBoPainter.class, new PaginationMetadata(offset, limit));
        credits.fillFromDb(dbJson, catalogService, null, locale);
        return credits;
    }

    @Override
    public void registerDisbursementConfirmation(int creditId, Integer creditRejectionReasonId, Integer sysUserId, Date date, Character paymentType, Integer signatureSysUserId, String checkNumber) throws Exception {
            queryForObjectTrx("select * from credit.bo_confirm_disbursement(?, ?, ?, ?, ?, ?, ?)",
                String.class,
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.INTEGER, creditRejectionReasonId),
                new SqlParameterValue(Types.INTEGER, sysUserId),
                new SqlParameterValue(Types.DATE, date),
                new SqlParameterValue(Types.CHAR, paymentType),
                new SqlParameterValue(Types.INTEGER, signatureSysUserId),
                new SqlParameterValue(Types.VARCHAR, checkNumber));
    }

    @Override
    public void registerDisbursementConfirmationWithComment(int creditId, Integer creditRejectionReasonId, Integer sysUserId, Date date, Character paymentType, Integer signatureSysUserId, String checkNumber, String creditRejectionReasonComment) throws Exception {
        queryForObjectTrx("select * from credit.bo_confirm_disbursement(?, ?, ?, ?, ?, ?, ?, ?)",
                String.class,
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.INTEGER, creditRejectionReasonId),
                new SqlParameterValue(Types.INTEGER, sysUserId),
                new SqlParameterValue(Types.DATE, date),
                new SqlParameterValue(Types.CHAR, paymentType),
                new SqlParameterValue(Types.INTEGER, signatureSysUserId),
                new SqlParameterValue(Types.VARCHAR, checkNumber),
                new SqlParameterValue(Types.VARCHAR, creditRejectionReasonComment));
    }

    @Override
    public PaginationWrapper <CreditBoPainter> getCreditsFilter(
            String country,
            Locale locale,
            Date creationFrom,
            Date creationTo,
            Integer[] productId,
            String[] analystId,
            Integer[] entityId,
            Integer tabId,
            String documentNumber,
            Integer employerId,
            Boolean isBranded,
            Boolean welcomeCall,
            int offset,
            int limit) throws Exception {
        JSONArray dbJson = queryForObject("select * from credit.bo_get_credits(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.DATE, creationFrom),
                new SqlParameterValue(Types.DATE, creationTo),
                new SqlParameterValue(Types.OTHER, productId != null ? new Gson().toJson(productId) : null),
                new SqlParameterValue(Types.OTHER, analystId != null ? new Gson().toJson(analystId) : null),
                new SqlParameterValue(Types.OTHER, entityId != null ? new Gson().toJson(entityId) : null),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.INTEGER, tabId),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.INTEGER, employerId),
                new SqlParameterValue(Types.OTHER, country),
                new SqlParameterValue(Types.BOOLEAN, isBranded),
                new SqlParameterValue(Types.BOOLEAN, welcomeCall));
        if (dbJson == null) {
            return null;
        }

        PaginationWrapper<CreditBoPainter> credits = new PaginationWrapper<>(CreditBoPainter.class, new PaginationMetadata(offset, limit));
        credits.fillFromDb(dbJson, catalogService, null,locale);
        return credits;
    }

    @Override
    public PaginationWrapper<CreditGatewayBoPainter> getCreditCollection(String country, Locale locale, Integer offset, Integer limit) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from credit.bo_get_collection(?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.OTHER, country),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit));
        if (dbJson == null) {
            return null;
        }

        PaginationWrapper<CreditGatewayBoPainter> creditCollections = new PaginationWrapper<>(CreditGatewayBoPainter.class, new PaginationMetadata(offset, limit));
        creditCollections.fillFromDb(dbJson, catalogService, null,locale);
        return creditCollections;
    }

    @Override
    public PaginationWrapper<CreditGatewayBoPainter> getCreditCollectionFilter(String country, Locale locale, Integer[] productId, Integer[] clusterId, String collector, Integer[] entityId, Integer[] trancheId, Boolean paused,
                                                                               Integer offset, Integer limit, String documentNumber, Integer employerId, Date dueDateFrom, Date dueDateTo) throws Exception {
        JSONArray dbJson = queryForObject("select * from credit.bo_get_collection(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::JSON)",
                JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.OTHER, productId != null ? new Gson().toJson(productId) : null),
                new SqlParameterValue(Types.OTHER, clusterId != null ? new Gson().toJson(clusterId) : null),
                new SqlParameterValue(Types.VARCHAR, collector),
                new SqlParameterValue(Types.OTHER, entityId != null ? new Gson().toJson(entityId) : null),
                new SqlParameterValue(Types.OTHER, trancheId != null ? new Gson().toJson(trancheId) : null),
                new SqlParameterValue(Types.BOOLEAN, paused),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.INTEGER, employerId),
                new SqlParameterValue(Types.DATE, dueDateFrom),
                new SqlParameterValue(Types.DATE, dueDateTo),
                new SqlParameterValue(Types.OTHER, country));
        if (dbJson == null) {
            return null;
        }

        PaginationWrapper<CreditGatewayBoPainter> creditCollections = new PaginationWrapper<>(CreditGatewayBoPainter.class, new PaginationMetadata(offset, limit));
        creditCollections.fillFromDb(dbJson, catalogService,null, locale);
        return creditCollections;
    }

    @Override
    public void registerCollectionContactResult(int creditId, Integer sysUserId, Integer contactResultId, Date date, Integer amount, String comment) throws Exception {
        queryForObjectTrx("select * from credit.bo_ins_contact_result(?, ?, ?, ?, ?, ?)",
                String.class,
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.INTEGER, sysUserId),
                new SqlParameterValue(Types.INTEGER, contactResultId),
                new SqlParameterValue(Types.DATE, date),
                new SqlParameterValue(Types.INTEGER, amount),
                new SqlParameterValue(Types.VARCHAR, comment));
    }

    @Override
    public void insertNegativBase(int creditId) {
        queryForObjectTrx("select * from credit.ins_negative_base(?)",
                String.class,
                new SqlParameterValue(Types.INTEGER, creditId));
    }

    @Override
    public String getLoanApplicationAnalystUsername(int loanApplicatonId) throws Exception {
        return queryForObjectTrx("select * from credit.get_application_analyst(?)",
                String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicatonId));

    }


}
