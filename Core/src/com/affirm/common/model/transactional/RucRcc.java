package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RucRcc implements Serializable {

    private Date rccDate;
    private List<RccDetail> rccDetail;


    public void fillFromDb(JSONObject json) throws Exception {
        Date current = JsonUtil.getPostgresDateFromJson(json, "fec_rep", null);
        setRccDate(current);
        setRccDetail(fillDetail(JsonUtil.getJsonArrayFromJson(json, "rcc_detail", null)));

    }

    private List<RccDetail> fillDetail(JSONArray detailJson) throws Exception {
        if (detailJson == null) {
            return null;
        }

        List<RccDetail> rccDetail = new ArrayList<>();
        for (int i = 0; i < detailJson.length(); i++) {
            RccDetail detail = new RccDetail();
            detail.fillFromDb(detailJson.getJSONObject(i));
            if (i == 0) {
                detail.setShowDate(true);
            } else {
                detail.setShowDate(false);
            }
            rccDetail.add(detail);
        }

        return rccDetail;
    }

    public Date getRccDate() {
        return rccDate;
    }

    public void setRccDate(Date rccDate) {
        this.rccDate = rccDate;
    }

    public List<RccDetail> getRccDetail() {
        return rccDetail;
    }

    public void setRccDetail(List<RccDetail> rccDetail) {
        this.rccDetail = rccDetail;
    }

    @Override
    public String toString() {
        return "PersonRcc{" +
                "rccDate=" + rccDate +
                ", rccDetail=" + rccDetail +
                '}';
    }
}


