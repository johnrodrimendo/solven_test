package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class BDSBaseProcess {

    private Integer id;
    private Date processDate;
    private String fileName;
    private String type;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "id", null));
        setProcessDate(JsonUtil.getPostgresDateFromJson(json, "process_date", null));
        setFileName(JsonUtil.getStringFromJson(json, "file_name", null));
        setType(JsonUtil.getStringFromJson(json, "type", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
