package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by dev5 on 12/12/17.
 */
public class VehicleDetails {

    private Integer id;
    private Integer entityPolicy;
    private String color;
    private List<String> image;
    private Date registerDate;
    private String boxClass;
    private String boxStyle;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) {
        setId(JsonUtil.getIntFromJson(json, "vehicle_id", null));
        setEntityPolicy(JsonUtil.getIntFromJson(json, "entity_policy", null));
        setColor(JsonUtil.getStringFromJson(json, "color", null));
        if(JsonUtil.getJsonArrayFromJson(json, "image", null) != null) {
            JSONArray images = JsonUtil.getJsonArrayFromJson(json, "image", null);
            image = new ArrayList<>();
            for(int i = 0; i < images.length(); i++){
                image.add(images.getString(i));
            }
        }
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setBoxClass(getBoxClassEN());
        setBoxStyle(getBoxStyleEN());
    }

    public String getBoxClassEN(){
        String color = getColor().toUpperCase();
        switch(color){
            case "ROJO" : return "box-color bg-red";
            case "NEGRO" : return "box-color bg-black";
            case "AMARILLO" : return "box-color bg-yellow";
            case "GRIS" : return "box-color bg-gray";
            default : return null;
        }
    }

    public String getBoxStyleEN(){
        String color = getColor().toUpperCase();
        switch(color){
            case "ROJO" : return "background: #ff0000";
            case "NEGRO" : return "background: #000000";
            case "AMARILLO" : return "background: #ffdc2c";
            case "GRIS" : return "background: #9d9d9d";
            default : return null;
        }
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEntityPolicy() {
        return entityPolicy;
    }

    public void setEntityPolicy(Integer entityPolicy) {
        this.entityPolicy = entityPolicy;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getBoxClass() {
        return boxClass;
    }

    public void setBoxClass(String boxClass) {
        this.boxClass = boxClass;
    }

    public String getBoxStyle() {
        return boxStyle;
    }

    public void setBoxStyle(String boxStyle) {
        this.boxStyle = boxStyle;
    }
}
