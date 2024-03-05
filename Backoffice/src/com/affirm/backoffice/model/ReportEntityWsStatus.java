package com.affirm.backoffice.model;

import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class ReportEntityWsStatus {
    private EntityWebService entityWebService;
    private Integer total;
    private Integer failed;
    private Integer success;
    private Integer running;

    public void fillFromDb(JSONObject jsonObject) {
        EntityWebService entityWebService = new EntityWebService();
        entityWebService.fillFromDb(jsonObject);

        setEntityWebService(entityWebService);
        setTotal(JsonUtil.getIntFromJson(jsonObject, "total", null));
        setFailed(JsonUtil.getIntFromJson(jsonObject, "failed", null));
        setSuccess(JsonUtil.getIntFromJson(jsonObject, "success", null));
        setRunning(JsonUtil.getIntFromJson(jsonObject, "running", null));
    }

    public EntityWebService getEntityWebService() { return entityWebService; }

    public void setEntityWebService(EntityWebService entityWebService) { this.entityWebService = entityWebService; }

    public Integer getTotal() { return total; }

    public void setTotal(Integer total) { this.total = total; }

    public Integer getFailed() { return failed; }

    public void setFailed(Integer failed) { this.failed = failed; }

    public Integer getSuccess() { return success; }

    public void setSuccess(Integer success) { this.success = success; }

    public Integer getRunning() { return running; }

    public void setRunning(Integer running) { this.running = running; }
}
