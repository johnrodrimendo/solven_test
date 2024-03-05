package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;


public class GuaranteedVehicle implements Serializable {

    private Integer id;
    private String model;
    private Integer brandId;
    private Mileage mileage;
    private Integer mileageId;
    private Integer year;
    private Integer currencyId;
    private Double price;
    private Date registerDate;
    private Boolean isAccepted;
    private Integer maintianedCarBrandId;


    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "guaranteed_vehicle_id", null));
        setMaintianedCarBrandId(JsonUtil.getIntFromJson(json, "maintained_car_brand_id", null));
        setModel(JsonUtil.getStringFromJson(json, "model", null));
        setMileageId(JsonUtil.getIntFromJson(json, "mileage_id", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setIsAccepted(JsonUtil.getBooleanFromJson(json, "is_accepted", null));
        setYear(JsonUtil.getIntFromJson(json, "year", null));
        setCurrencyId(JsonUtil.getIntFromJson(json, "currency_id", null));
        setPrice(JsonUtil.getDoubleFromJson(json, "price", null));
    }


    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setId(JsonUtil.getIntFromJson(json, "guaranteed_vehicle_id", null));
        setBrandId(JsonUtil.getIntFromJson(json, "maintained_car_brand_id", null));
        setModel(JsonUtil.getStringFromJson(json, "model", null));
        setMileageId(JsonUtil.getIntFromJson(json, "mileage_id", null));
        setMileage(catalogService.getMileage(getMileageId()));
        setYear(JsonUtil.getIntFromJson(json, "year", null));
        setCurrencyId(JsonUtil.getIntFromJson(json, "currency_id", null));
        setPrice(JsonUtil.getDoubleFromJson(json, "price", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setIsAccepted(JsonUtil.getBooleanFromJson(json, "is_accepted", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getMaintianedCarBrandId() {
        return maintianedCarBrandId;
    }

    public void setMaintianedCarBrandId(Integer maintianedCarBrandId) {
        this.maintianedCarBrandId = maintianedCarBrandId;
    }
    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getMileageId() {
        return mileageId;
    }

    public void setMileageId(Integer mileageId) {
        this.mileageId = mileageId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean isAccepted(){ return this.isAccepted; }

    public void setIsAccepted(Boolean isAccepted){this.isAccepted=isAccepted;}

    public void setIsAccepted(String accepted){
        if("SI".equals(accepted.toUpperCase())){
            this.isAccepted=true;
        }
        if("NO".equals(accepted.toUpperCase())){
            this.isAccepted=false;
        }
    }

    public String getAcceptedValue() {
                    if (this.isAccepted == true) {
                        return "si";
                    }
                    if (this.isAccepted == false) {
                        return "no";
                    }
                    return "";
                }
    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Mileage getMileage() {
        return mileage;
    }

    public void setMileage(Mileage mileage) {
        this.mileage = mileage;
    }
}
