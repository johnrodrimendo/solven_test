package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author jrodriguez
 *
 */
public class Province implements Serializable {

	private String id;
	private Integer provinceId;
	private String ineiId;
	private String name;
	private String code31662;
	private CountryParam country;
	private transient Department department;
	private transient Map<String, District> districts = new LinkedHashMap<>();

	public Province(String id, String name, Department department) {
		super();
		this.id = id;
		this.name = name;
		this.department = department;
	}

	public Province(String id, String name, Department department, String ineiId) {
		super();
		this.id = id;
		this.name = name;
		this.department = department;
		this.ineiId = ineiId;
	}

	public Province(){}

	/*General province*/
	public void fillFromDb(JSONObject json, CatalogService catalogService) throws Exception{
		setProvinceId(JsonUtil.getIntFromJson(json, "province_id", null));
		setName(JsonUtil.getStringFromJson(json, "province", null));
		setCode31662(JsonUtil.getStringFromJson(json, "code_31662", null));
		if(JsonUtil.getIntFromJson(json, "country_id", null) != null)
			setCountry(catalogService.getCountryParam(JsonUtil.getIntFromJson(json, "country_id", null)));
		if(JsonUtil.getIntFromJson(json, "department_id", null) != null)
			setDepartment(catalogService.getGeneralDepartmentById(JsonUtil.getIntFromJson(json, "department_id", null)));
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

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Map<String, District> getDistricts() { return districts; }

	public void setDistricts(Map<String, District> districts) { this.districts = districts; }

	public String getCode31662() { return code31662; }

	public void setCode31662(String code31662) {
		this.code31662 = code31662;
	}

	public CountryParam getCountry() {
		return country;
	}

	public void setCountry(CountryParam country) {
		this.country = country;
	}

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public String getIneiId() {
		return ineiId;
	}

	public void setIneiId(String ineiId) {
		this.ineiId = ineiId;
	}
}
