package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev5 on 20/06/17.
 */
public class ComparisonRates {

    private List<ComparisonCategory> currentRates;
    private List<ComparisonCategory> newRates;

    public void fillFromDb(JSONObject json){
        newRates = new ArrayList<>();
        currentRates = new ArrayList<>();
        JSONArray newRatesArray = JsonUtil.getJsonArrayFromJson(json, "new_rates", null);
        if(newRatesArray != null){
            for (int i=0; i < newRatesArray.length(); i++) {
                ComparisonCategory comparisonRates = new ComparisonCategory();
                comparisonRates.fillFromDb(newRatesArray.getJSONObject(i));
                newRates.add(comparisonRates);
            }
        }
        JSONArray currentRatesArray = JsonUtil.getJsonArrayFromJson(json, "current_rates", null);
        if(currentRatesArray != null){
            for (int i=0; i < currentRatesArray.length(); i++) {
                ComparisonCategory currentnRates = new ComparisonCategory();
                currentnRates.fillFromDb(currentRatesArray.getJSONObject(i));
                currentRates.add(currentnRates);
            }
        }

    }

    public List<ComparisonCategory> getCurrentRates() {
        return currentRates;
    }

    public void setCurrentRates(List<ComparisonCategory> currentRates) {
        this.currentRates = currentRates;
    }

    public List<ComparisonCategory> getNewRates() {
        return newRates;
    }

    public void setNewRates(List<ComparisonCategory> newRates) {
        this.newRates = newRates;
    }
}

