package com.affirm.common.dao;

import com.affirm.common.model.transactional.EntityWebServiceLog;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * @author jrodriguez
 */
public interface WebServiceDAO {
    void registerEntityWebServiceResult(int loanApplicationId, int entityWebServiceId, String jsonResult);

    Integer registerEntityWebServiceLog(int entityWebServiceId, Integer loanApplicationId, Date startDate, Date finishDate, char status, String request, String response);

    void updateEntityWebServiceLogStatus(int entityWebServiceLogId, char status);

    void updateEntityWebServiceLogResponse(int entityWebServiceLogId, String response);

    void updateEntityWebServiceLogRequest(int entityWebServiceLogId, String request);

    void updateEntityWebServiceLogFinishDate(int entityWebServiceLogId, Date finishDate);

    JSONObject getExternalServiceResponse(Integer loanApplicationId, Integer wsServiceId);

    List<EntityWebServiceLog> getEntityWebServiceLog(Integer loanApplicationId, Integer wsServiceId);

    List<EntityWebServiceLog> getEntityWebServiceLogByWsServiceId(Integer wsServiceId, Integer offset, Integer limit);

    EntityWebServiceLog getEntityWebServiceLogById(Integer entityWebServiceLogId);
}