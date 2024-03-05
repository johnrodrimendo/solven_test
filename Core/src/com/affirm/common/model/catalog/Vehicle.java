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
import java.util.Locale;

/**
 * @author jrodriguez
 */
public class Vehicle implements Serializable {

    public static final char MECANICO = 'M';
    public static final char AUTOMATICO = 'A';

    private Integer groupId;
    private VehicleBrand brand;
    private Integer modelId;
    private String model;
    private String version;
    private Character transmission;
    private Integer price;
    private Integer listPrice;
    private VehicleGasType gasType;
    private Integer yearOfProduction;
    private Integer vehicleDealershipId;
    private VehicleDealership vehicleDealership;
    private String image;
    private String brochureURL;
    private List<VehicleDetails> vehicleDetails;
    private Boolean active;
    private String warranty;
    private String engine;
    private String power;
    private String torque;
    private String rims;
    private Boolean brakes;
    private String bodyType;
    private Boolean airbag;
    private Boolean radio;
    private Boolean airConditioning;
    private Currency currency;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) {
        setGroupId(JsonUtil.getIntFromJson(json, "group_id", null));
        setImage(JsonUtil.getStringFromJson(json, "image", null));
        setTransmission(JsonUtil.getCharacterFromJson(json, "transmission", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setPrice(JsonUtil.getIntFromJson(json, "price", null));
        if (JsonUtil.getIntFromJson(json, "gas_type_id", null) != null)
            setGasType(catalog.getVehicleGasType(JsonUtil.getIntFromJson(json, "gas_type_id", null), locale));
        setModel(JsonUtil.getStringFromJson(json, "model", null));
        setVersion(JsonUtil.getStringFromJson(json, "version", null));
        if (JsonUtil.getIntFromJson(json, "car_brand_id", null) != null)
            setBrand(catalog.getVehicleBrand(JsonUtil.getIntFromJson(json, "car_brand_id", null)));
        if (JsonUtil.getIntFromJson(json, "currency_id", null) != null)
            setCurrency(catalog.getCurrency(JsonUtil.getIntFromJson(json, "currency_id", null)));
        setBrochureURL(JsonUtil.getStringFromJson(json, "data_sheet", null));
        
        setYearOfProduction(JsonUtil.getIntFromJson(json, "year_of_production", null));
        setVehicleDealershipId(JsonUtil.getIntFromJson(json, "car_dealership_id", null));
        if (JsonUtil.getIntFromJson(json, "car_dealership_id", null) != null)
            setVehicleDealership(catalog.getVehicleDealership(JsonUtil.getIntFromJson(json, "car_dealership_id", null), locale));
        if(JsonUtil.getJsonArrayFromJson(json, "details", null) != null){
            List<VehicleDetails> vehicleDetailsTemp = new ArrayList<>();
            JSONArray vehicleDetails = JsonUtil.getJsonArrayFromJson(json, "details", null);
            for(int i = 0; i < vehicleDetails.length(); i++){
                VehicleDetails vehicleDetail = new VehicleDetails();
                vehicleDetail.fillFromDb(vehicleDetails.getJSONObject(i), catalog, locale);
                vehicleDetailsTemp.add(vehicleDetail);
            }
            setVehicleDetails(vehicleDetailsTemp);
        }
        setListPrice(JsonUtil.getIntFromJson(json, "list_price" , null));
        setWarranty(JsonUtil.getStringFromJson(json, "warranty", null));
        setEngine(JsonUtil.getStringFromJson(json, "motor_cc", null));
        setPower(JsonUtil.getStringFromJson(json, "power", null));
        setTorque(JsonUtil.getStringFromJson(json, "torque", null));
        setRims(JsonUtil.getStringFromJson(json, "rims", null));
        setBrakes(JsonUtil.getBooleanFromJson(json, "abs_brakes", null));
        setBodyType(JsonUtil.getStringFromJson(json, "body_type", null));
        setAirbag(JsonUtil.getBooleanFromJson(json, "airbag", null));
        setRadio(JsonUtil.getBooleanFromJson(json, "radio", null));
        setAirConditioning(JsonUtil.getBooleanFromJson(json, "air_conditioning", null));
        setModelId(JsonUtil.getIntFromJson(json, "cod_model", null));
    }

    public String getFullVehicleName(){
        return getBrand().getBrand() + " " + getModel();
    }

    public VehicleBrand getBrand() {
        return brand;
    }

    public void setBrand(VehicleBrand brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Character getTransmission() {
        return transmission;
    }

    public void setTransmission(Character transmission) {
        this.transmission = transmission;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getYearOfProduction() {
        return yearOfProduction;
    }

    public void setYearOfProduction(Integer yearOfProduction) {
        this.yearOfProduction = yearOfProduction;
    }

    public Integer getVehicleDealershipId() {
        return vehicleDealershipId;
    }

    public void setVehicleDealershipId(Integer vehicleDealershipId) {
        this.vehicleDealershipId = vehicleDealershipId;
    }

    public VehicleDealership getVehicleDealership() {
        return vehicleDealership;
    }

    public void setVehicleDealership(VehicleDealership vehicleDealership) {
        this.vehicleDealership = vehicleDealership;
    }

    public VehicleGasType getGasType() {
        return gasType;
    }

    public void setGasType(VehicleGasType gasType) {
        this.gasType = gasType;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getBrochureURL() {
        return brochureURL;
    }

    public void setBrochureURL(String brochureURL) {
        this.brochureURL = brochureURL;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<VehicleDetails> getVehicleDetails() {
        return vehicleDetails;
    }

    public void setVehicleDetails(List<VehicleDetails> vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getListPrice() {
        return listPrice;
    }

    public void setListPrice(Integer listPrice) {
        this.listPrice = listPrice;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getTorque() {
        return torque;
    }

    public void setTorque(String torque) {
        this.torque = torque;
    }

    public String getRims() {
        return rims;
    }

    public void setRims(String rims) {
        this.rims = rims;
    }

    public Boolean getBrakes() {
        return brakes;
    }

    public void setBrakes(Boolean brakes) {
        this.brakes = brakes;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public Boolean getAirbag() {
        return airbag;
    }

    public void setAirbag(Boolean airbag) {
        this.airbag = airbag;
    }

    public Boolean getRadio() {
        return radio;
    }

    public void setRadio(Boolean radio) {
        this.radio = radio;
    }

    public Boolean getAirConditioning() {
        return airConditioning;
    }

    public void setAirConditioning(Boolean airConditioning) {
        this.airConditioning = airConditioning;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }
}
