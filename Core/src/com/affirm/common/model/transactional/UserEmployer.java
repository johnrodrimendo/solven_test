package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Employer;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserEmployer implements Serializable {

    private Integer id;
    private String email;
    private Date registerDate;
    private Boolean active;
    private String name;
    private String firstSurname;
    private String lastSurname;
    private String phoneNumber;
    private String avatar;
    private List<Employer> companies = new ArrayList<>();

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) {
        setId(JsonUtil.getIntFromJson(json, "employer_user_id", null));
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setName(JsonUtil.getStringFromJson(json, "person_name", null));
        setFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        setAvatar(JsonUtil.getStringFromJson(json, "avatar", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_employer_id", null) != null) {
            JSONArray arrayEmployers = JsonUtil.getJsonArrayFromJson(json, "js_employer_id", null);
            for (int i = 0; i < arrayEmployers.length(); i++) {
                companies.add(catalog.getEmployer(arrayEmployers.getInt(i)));
            }
        }
    }

    public String getFullName() {
        return (getName() != null ? getName() : "") + " " + (getFirstSurname() != null ? getFirstSurname() : "") + " " + (getLastSurname() != null ? getLastSurname() : "");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public String getLastSurname() {
        return lastSurname;
    }

    public void setLastSurname(String lastSurname) {
        this.lastSurname = lastSurname;
    }

    public List<Employer> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Employer> companies) {
        this.companies = companies;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}