package com.affirm.common.model.transactional;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RccIdeGrouped {

    private RccIde rccIde;
    private List<RccSal> rccSals;

    public void fillFromDb(JSONObject json, CatalogService catalogService) throws Exception {
        if(JsonUtil.getJsonObjectFromJson(json, "js_ide", null) != null){
            rccIde = new RccIde();
            rccIde.fillFromDb(JsonUtil.getJsonObjectFromJson(json, "js_ide", null));
        }
        if(JsonUtil.getJsonArrayFromJson(json, "js_sal", null) != null){
            rccSals = new ArrayList<>();
            JSONArray jsonSals = JsonUtil.getJsonArrayFromJson(json, "js_sal", null);
            for(int i=0; i<jsonSals.length(); i++){
                RccSal sal = new RccSal();
                sal.fillFromDb(jsonSals.getJSONObject(i), catalogService);
                rccSals.add(sal);
            }
        }
    }

    public RccIde getRccIde() {
        return rccIde;
    }

    public void setRccIde(RccIde rccIde) {
        this.rccIde = rccIde;
    }

    public List<RccSal> getRccSals() {
        return rccSals;
    }

    public void setRccSals(List<RccSal> rccSals) {
        this.rccSals = rccSals;
    }
}
