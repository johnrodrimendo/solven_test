
package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author jrodriguez
 */
public class Department implements Serializable {

    private String id;
    private Integer departmentId;
    private String name;
    private String ineiId;
    private CountryParam country;

    private String phonePrefix;
    private transient Map<String, Province> provinces = new LinkedHashMap<>();

    public Department(String id, String name, String phonePrefix) {
        this.id = id;
        this.name = name;
        this.phonePrefix = phonePrefix;
    }

    public Department(String id, String name, String phonePrefix, String ineiId) {
        this.id = id;
        this.name = name;
        this.phonePrefix = phonePrefix;
        this.ineiId = ineiId;
    }

    public Department(){}

    public void fillFromDb(JSONObject json, CatalogService catalogService){
        setDepartmentId(JsonUtil.getIntFromJson(json, "department_id", null));
        setName(JsonUtil.getStringFromJson(json, "nombre", null));
        if(JsonUtil.getIntFromJson(json, "country_id", null) != null )
            setCountry(catalogService.getCountryParam(JsonUtil.getIntFromJson(json, "country_id", null)));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Province> getProvinces() { return provinces; }

    public void setProvinces(Map<String, Province> provinces) { this.provinces = provinces; }

    public String getPhonePrefix() {
        return phonePrefix;
    }

    public void setPhonePrefix(String phonePrefix) {
        this.phonePrefix = phonePrefix;
    }

    public String getDepartmentPrefix() {
        return name + " (" + phonePrefix + ")";
    }

    public CountryParam getCountry() {
        return country;
    }

    public void setCountry(CountryParam country) {
        this.country = country;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getIneiId() {
        return ineiId;
    }

    public void setIneiId(String ineiId) {
        this.ineiId = ineiId;
    }
}
