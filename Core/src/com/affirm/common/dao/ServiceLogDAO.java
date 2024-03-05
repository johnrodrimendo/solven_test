package com.affirm.common.dao;

import com.affirm.common.model.transactional.LogSmsBulkSend;
import org.json.JSONObject;

/**
 * Created by dev5 on 10/04/17.
 */
public interface ServiceLogDAO {

    Integer registerServiceCall(Integer entityId,
                                    Integer loanApplicationId,
                                    String request,
                                    String response,
                                    Integer processStatus,
                                    Integer serviceId,
                                    Integer operationId) throws Exception;

    void updateServiceCall(Integer lodId,
                                    Integer entityId,
                                    Integer loanApplicationId,
                                    String response,
                                    Integer processStatus,
                                    Integer serviceId,
                                    Integer operationId) throws Exception;


    void registerSMSSenderServiceLog(long failed,
                                     long success,
                                     Integer entityId,
                                     Integer productId,
                                     Integer sysuserId,
                                     int queryBotId) throws Exception;

    JSONObject getService(String operationName) throws Exception;

    LogSmsBulkSend getLogSmsBulkSendByQueryBot(Integer queryBotId) throws Exception;
}
