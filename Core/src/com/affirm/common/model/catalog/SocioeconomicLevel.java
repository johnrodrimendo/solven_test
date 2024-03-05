package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by sTbn on 4/08/16.
 */
public class SocioeconomicLevel implements Serializable {

    private Integer id;
    private String level;
    private Integer minIncome;
    private Integer maxIncome;
    private Integer avgIncome;
    private Integer countryId;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "socioeconomic_level_id", null));
        setLevel(JsonUtil.getStringFromJson(json, "socioeconomic_level", null));
        setMinIncome(JsonUtil.getIntFromJson(json, "min_income", null));
        setMaxIncome(JsonUtil.getIntFromJson(json, "max_income", null));
        setAvgIncome(JsonUtil.getIntFromJson(json, "avg_income", null));
        setCountryId(JsonUtil.getIntFromJson(json, "country_id", null));
    }

    public String getDescriptionWithMaxMinAvg(UtilService utilService, CatalogService catalogService) {
        Currency currency = catalogService.getCurrency(Currency.ARS);
        return level + " (" + utilService.integerMoneyFormat(minIncome, currency)
                + " a " + utilService.integerMoneyFormat(maxIncome, currency)
                + " - Prom: " + utilService.integerMoneyFormat(avgIncome, currency) + ")";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getMinIncome() {
        return minIncome;
    }

    public void setMinIncome(Integer minIncome) {
        this.minIncome = minIncome;
    }

    public Integer getMaxIncome() {
        return maxIncome;
    }

    public void setMaxIncome(Integer maxIncome) {
        this.maxIncome = maxIncome;
    }

    public Integer getAvgIncome() {
        return avgIncome;
    }

    public void setAvgIncome(Integer avgIncome) {
        this.avgIncome = avgIncome;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }
}
