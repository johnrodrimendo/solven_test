package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by dev5 on 02/08/17.
 */
public class AreaType {

    public static final int NO_APLICA = 13;
    public static final int URBANIZACION = 1;
    public static final int RESIDENCIAL = 15;
    public static final int AAHH = 3;
    public static final int GRUPO = 17;

    private Integer id;
    private String name;

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setId(JsonUtil.getIntFromJson(json, "zone_type_id", null));
        setName(JsonUtil.getStringFromJson(json, "zone_type", null));
    }

    public AreaType(){}

    public AreaType(Integer id, String name){
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
