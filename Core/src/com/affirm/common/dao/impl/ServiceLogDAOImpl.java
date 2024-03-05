package com.affirm.common.dao.impl;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.ServiceLogDAO;
import com.affirm.common.model.transactional.LogSmsBulkSend;
import com.affirm.common.service.CatalogService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;

/**
 * Created by miberico on 10/04/17.
 */
@Repository("serviceLogDAO")
public class ServiceLogDAOImpl extends JsonResolverDAO implements ServiceLogDAO {


    @Autowired
    private CatalogService catalogService;



    @Override
    public Integer registerServiceCall(Integer entityId,
                                       Integer loanApplicationId,
                                       String request,
                                       String response,
                                       Integer processStatus,
                                       Integer serviceId,
                                       Integer operationId) throws Exception {
        return queryForObjectTrx("select * from security.ins_lg_agreement_loan_service(?,?,?,?,?,?,?)", Integer.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.SQLXML, request),
                new SqlParameterValue(Types.SQLXML, response),
                new SqlParameterValue(Types.INTEGER, processStatus),
                new SqlParameterValue(Types.INTEGER, serviceId),
                new SqlParameterValue(Types.INTEGER, operationId));
    }

    @Override
    public void updateServiceCall(Integer logId,
                                  Integer entityId,
                                  Integer loanApplicationId,
                                  String response,
                                  Integer processStatus,
                                  Integer serviceId,
                                  Integer operationId) throws Exception {
        queryForObjectTrx("select * from security.upd_lg_agreement_loan_service(?,?,?,?,?,?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, logId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.SQLXML, response),
                new SqlParameterValue(Types.INTEGER, processStatus),
                new SqlParameterValue(Types.INTEGER, serviceId),
                new SqlParameterValue(Types.INTEGER, operationId));

    }

    @Override
    public void registerSMSSenderServiceLog(long failed,
                                            long success,
                                            Integer entityId,
                                            Integer productId,
                                            Integer sysuserId,
                                            int queryBotId) throws Exception {
        queryForObjectExternal("select * from external.ins_lg_sms_approved(?, ?, ?, ?, ?, ?) ", String.class,
                new SqlParameterValue(Types.INTEGER, failed),
                new SqlParameterValue(Types.INTEGER, success),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId),
                new SqlParameterValue(Types.INTEGER, sysuserId),
                new SqlParameterValue(Types.INTEGER, queryBotId));
    }

    public JSONObject getService(String operationName) throws Exception {
        return queryForObjectTrx("select * from security.get_ct_service(?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, operationName));
    }

    @Override
    public LogSmsBulkSend getLogSmsBulkSendByQueryBot(Integer queryBotId) throws Exception {
        JSONObject dbJson = queryForObjectExternal("select * from external.get_lg_sms_approved(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, queryBotId));
        if (dbJson == null)
            return null;

        LogSmsBulkSend log = new LogSmsBulkSend();
        log.fillFromDb(dbJson);
        return log;
    }
}
