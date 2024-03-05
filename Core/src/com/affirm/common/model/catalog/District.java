package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author jrodriguez
 *
 */
public class District implements Serializable {

	private String id;
	private Long districtId;
	private String ineiId;
	private String name;
	private transient Province province;
	private String postalCode;
	private Ubigeo ubigeo;
	private CountryParam country;
	private Boolean isActive;

	public District(String id, String name, Province province, String ineiId) {
		super();
		this.id = id;
		this.name = name;
		this.province = province;
		this.ineiId = ineiId;
	}

	public District(String id, String name, Province province) {
		super();
		this.id = id;
		this.name = name;
		this.province = province;
	}

	public District(){}

	public void fillFromDb(JSONObject json, CatalogService catalogService) throws Exception{
		setDistrictId(JsonUtil.getLongFromJson(json, "locality_id", null));
		setName(JsonUtil.getStringFromJson(json, "locality", null));
		setProvince(catalogService.getGeneralProvinceById(JsonUtil.getIntFromJson(json, "province_id", null)));
		setPostalCode(JsonUtil.getStringFromJson(json, "postal_code", null));
		setUbigeo(catalogService.getUbigeo(JsonUtil.getStringFromJson(json, "ubigeo", null)));
		setCountry(catalogService.getCountryParam(JsonUtil.getIntFromJson(json, "country_id", null)));
		setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
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

	public Province getProvince() {
		return province;
	}

	public void setProvince(Province province) {
		this.province = province;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Ubigeo getUbigeo() {
		return ubigeo;
	}

	public void setUbigeo(Ubigeo ubigeo) {
		this.ubigeo = ubigeo;
	}

	public CountryParam getCountry() {
		return country;
	}

	public void setCountry(CountryParam country) {
		this.country = country;
	}

	public Long getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Long districtId) {
		this.districtId = districtId;
	}

	public Boolean getActive() {
		return isActive;
	}

	public void setActive(Boolean active) {
		isActive = active;
	}

	public String getIneiId() {
		return ineiId;
	}

	public void setIneiId(String ineiId) {
		this.ineiId = ineiId;
	}
}
