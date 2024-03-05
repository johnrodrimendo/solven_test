package com.affirm.common.model;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class New {

    Integer id;
    CountryParam country;
    String title;
    String link;
    String date;
    String pressMedium;
    String logo;
    String altLogo;
    private String summary;

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setId(JsonUtil.getIntFromJson(json, "news_id", null));
        setCountry(catalogService.getCountryParam(JsonUtil.getIntFromJson(json, "country_id", null)));
        setTitle(JsonUtil.getStringFromJson(json, "news", null));
        setLink(JsonUtil.getStringFromJson(json, "news_url" , null));
        setDate(JsonUtil.getStringFromJson(json, "news_date", null));
        setPressMedium(JsonUtil.getStringFromJson(json, "alt_text", null));
        setLogo(JsonUtil.getStringFromJson(json, "image_url", null));
        setAltLogo(JsonUtil.getStringFromJson(json, "alt_image_url", null));
        setSummary(JsonUtil.getStringFromJson(json, "summary", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CountryParam getCountry() {
        return country;
    }

    public void setCountry(CountryParam country) {
        this.country = country;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public String getDateFormatted(Locale locale) throws Exception {
        return new SimpleDateFormat("dd MMMM, yyyy", locale).format(new SimpleDateFormat("yyyy-MM-dd", locale).parse(this.date));
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPressMedium() { return pressMedium; }

    public void setPressMedium(String pressMedium) { this.pressMedium = pressMedium; }

    public String getLogo() { return logo; }

    public void setLogo(String logo) { this.logo = logo; }

    public String getAltLogo() {
        return altLogo;
    }

    public void setAltLogo(String altLogo) {
        this.altLogo = altLogo;
    }

    public String getSummary() { return summary; }

    public void setSummary(String summary) { this.summary = summary; }
}
