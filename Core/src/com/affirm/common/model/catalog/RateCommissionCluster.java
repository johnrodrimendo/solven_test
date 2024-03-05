/**
 *
 */
package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jrodriguez
 */
public class RateCommissionCluster implements Serializable {

    private Integer id;
    private String cluster;
    private List<RateCommission> rateCommissions;

    public void fillFromDb(JSONObject json){
        setId(JsonUtil.getIntFromJson(json, "cluster_id", null));
        setCluster(JsonUtil.getStringFromJson(json, "cluster", null));
        setRateCommissions(new ArrayList<>());
        if (JsonUtil.getJsonArrayFromJson(json, "rate_commission", null) != null) {
            JSONArray rateCommissionsJson = JsonUtil.getJsonArrayFromJson(json, "rate_commission", null);
            for (int i = 0; i < rateCommissionsJson.length(); i++) {
                RateCommission rateCommission = new RateCommission();
                rateCommission.fillFromDb(rateCommissionsJson.getJSONObject(i));
                getRateCommissions().add(rateCommission);
            }
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public List<RateCommission> getRateCommissions() {
        return rateCommissions;
    }

    public void setRateCommissions(List<RateCommission> rateCommissions) {
        this.rateCommissions = rateCommissions;
    }
}

