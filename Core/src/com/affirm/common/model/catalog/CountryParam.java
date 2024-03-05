/**
 *
 */
package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jrodriguez
 */
public class CountryParam implements Serializable, ICatalog, ICatalogIntegerId {

    public final static int COUNTRY_PERU = 51;
    public final static int COUNTRY_ARGENTINA = 54;
    public final static int COUNTRY_COLOMBIA = 57;
    public final static int COUNTRY_ALL = 0;

    private Integer id;
    private String name;
    private List<String> domains;
    private ProcessQuestionsConfiguration selfEvaluationprocessQuestion;
    private Currency currency;
    private String locale;
    private String countryCode;
    private String description;
    private String email;
    private String phone;
    private String addressLocality;
    private String postalCode;
    private String streetAddress;
    private Boolean active = true;
    private String separator;
    private String timezoneCode;
    private Integer wpTagId;
    private Integer scheduleId;

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setId(JsonUtil.getIntFromJson(json, "country_id", null));
        setName(JsonUtil.getStringFromJson(json, "country", null));
        if (JsonUtil.getJsonArrayFromJson(json, "ar_domanin", null) != null) {
            JSONArray jsonArray = JsonUtil.getJsonArrayFromJson(json, "ar_domanin", null);
            domains = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                domains.add(jsonArray.getString(i));
            }
        }
        if (JsonUtil.getJsonObjectFromJson(json, "js_self_evaluation_process", null) != null) {
            setSelfEvaluationprocessQuestion(new ProcessQuestionsConfiguration());
            getSelfEvaluationprocessQuestion().fillFromDb(JsonUtil.getJsonObjectFromJson(json, "js_self_evaluation_process", null));
        }
        if (JsonUtil.getIntFromJson(json, "currency_id", null) != null)
            setCurrency(catalogService.getCurrency(JsonUtil.getIntFromJson(json, "currency_id", null)));
        setLocale(JsonUtil.getStringFromJson(json, "locale", null));
        setCountryCode(JsonUtil.getStringFromJson(json, "code", null));
        setDescription(JsonUtil.getStringFromJson(json, "description", null));
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        setPhone(JsonUtil.getStringFromJson(json, "phone", null));
        setAddressLocality(JsonUtil.getStringFromJson(json, "address_locality", null));
        setPostalCode(JsonUtil.getStringFromJson(json, "postal_code", null));
        setStreetAddress(JsonUtil.getStringFromJson(json, "street_address", null));
        setSeparator(JsonUtil.getStringFromJson(json, "separator", null));
        setTimezoneCode(JsonUtil.getStringFromJson(json, "timezone_code", null));
        setWpTagId(JsonUtil.getIntFromJson(json, "wp_tag_id", null));
        setScheduleId(JsonUtil.getIntFromJson(json, "schedule_id", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public ProcessQuestionsConfiguration getSelfEvaluationprocessQuestion() {
        return selfEvaluationprocessQuestion;
    }

    public void setSelfEvaluationprocessQuestion(ProcessQuestionsConfiguration selfEvaluationprocessQuestion) {
        this.selfEvaluationprocessQuestion = selfEvaluationprocessQuestion;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressLocality() {
        return addressLocality;
    }

    public void setAddressLocality(String addressLocality) {
        this.addressLocality = addressLocality;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    @Override
    public Boolean getActive() {
        return active;
    }

    @Override
    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getTimezoneCode() {
        return timezoneCode;
    }

    public void setTimezoneCode(String timezoneCode) {
        this.timezoneCode = timezoneCode;
    }

    public Integer getWpTagId() { return wpTagId; }

    public void setWpTagId(Integer wpTagId) { this.wpTagId = wpTagId; }

    public Integer getScheduleId() { return scheduleId; }

    public void setScheduleId(Integer scheduleId) { this.scheduleId = scheduleId; }
}
