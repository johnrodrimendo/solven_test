package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jrodriguez on 07/09/16.
 */
public class ConsolidationAccountType implements Serializable {

    public static final int TARJETA_CREDITO = 1;
    public static final int DISPONIBILIDAD_EFECTIVO = 2;
    public static final int LINEA_PARALELA = 3;
    public static final int CREDITO_CONSUMO = 4;
    public static final int CREDITO_NEGOCIO = 5;

    private Integer id;
    private String type;

    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "consolidation_account_id", null));
        setType(JsonUtil.getStringFromJson(json, "consolidation_account", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
