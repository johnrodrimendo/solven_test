package com.affirm.currencylayer;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by jarmando on 09/01/17.
 */
public class CurrencyLayerApi {

    private Double fetchUSDTarget(String target) {
        String url = "http://apilayer.net/api/live?access_key=" + "xxxxxxxxxxxxxxxxxxx"/*System.getenv("CURRENCY_LAYER")*/ + "&currencies=" + target;
        JSONObject js = JsonUtil.getJSONObjectFromUrl(url);
        System.out.println(js);
        if(JsonUtil.getBooleanFromJson(js, "success", false)) {
            return js.getJSONObject("quotes").getDouble("USD"+target);
        }
        return null;
    }

    public Double fetchUSDPEN() {
        return fetchUSDTarget("PEN");
    }

}
