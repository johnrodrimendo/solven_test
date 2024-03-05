package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by dev5 on 02/08/17.
 */
public class HouseType {

    public static final int DEPARTAMENTO = 1;
    public static final int QUINTA = 2;
    public static final int PISO = 2;

    private Integer id;
    private String name;

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setId(JsonUtil.getIntFromJson(json, "interior_type_id", null));
        setName(JsonUtil.getStringFromJson(json, "interior_type", null));
    }

    public HouseType(){}

    public HouseType(Integer id, String name){
        this.id = id;
        this.name = name;
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

}
