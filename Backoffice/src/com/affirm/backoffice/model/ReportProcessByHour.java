package com.affirm.backoffice.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class ReportProcessByHour {

    private Integer total;
    private Date tag;

    public void fillFromDb(JSONObject jsonObject) {
        setTotal(JsonUtil.getIntFromJson(jsonObject, "total", null));
        setTag(JsonUtil.getPostgresDateFromJson(jsonObject, "tag", null));
    }

    public Integer getTotal() { return total; }

    public void setTotal(Integer total) { this.total = total; }

    public Date getTag() { return tag; }

    public void setTag(Date tag) { this.tag = tag; }
}
