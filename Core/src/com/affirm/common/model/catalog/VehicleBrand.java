/**
 *
 */
package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class VehicleBrand implements Serializable {

    private Integer id;
    private Integer brandId;
    private String brand;
    private String image;
    private Boolean active;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "car_brand_id", null));
        setBrand(JsonUtil.getStringFromJson(json, "car_brand", null));
        setImage(JsonUtil.getStringFromJson(json, "image", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setBrandId(JsonUtil.getIntFromJson(json, "cod_brand", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }
}
