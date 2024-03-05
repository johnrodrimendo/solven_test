package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by miberico on 27/03/17.
 */
public class Role implements Serializable {

    public static final int BANDEJA_1_VISIBLE = 20; // Solicitudes a verificar
    public static final int BANDEJA_1_EJECUCION = 21;
    public static final int BANDEJA_1_EJECUCION_RIPLEY = 28;
    public static final int BANDEJA_2_VISIBLE = 22; // Creacion cliente / credito
    public static final int BANDEJA_2_EJECUCION = 23;
    public static final int BANDEJA_3_VISIBLE = 24; // Creditos a desembolsar
    public static final int BANDEJA_3_EJECUCION = 25;
    public static final int BANDEJA_4_VISIBLE = 26; // CReditos desembolsados
    public static final int BANDEJA_5_VISIBLE = 27; // Reportes
    public static final int BANDEJA_6_VISIBLE = 30; // crear solicitud
    public static final int BANDEJA_6_EJECUCION = 31;
    public static final int BANDEJA_7_VISIBLE = 32; // Carga de asociados
    public static final int BANDEJA_7_EJECUCION = 33;
    public static final int BANDEJA_8_VISIBLE = 35; // Leads
    public static final int BANDEJA_8_EJECUCION = 36;
    public static final int BANDEJA_9_VISIBLE = 35; // Solocitudes en proceso, creados por el entity user
    public static final int BANDEJA_10_VISIBLE = 47; // Modulo comercial
    public static final int BANDEJA_10_EJECUCION = 48; // Modulo comercial
    public static final int EXTTRANET_BANDEJA_EVALUACION_VISTA = 72; // Modulo comercial

    private Integer id;
    private String name;
    private Boolean active;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "role_id", null));
        setName(JsonUtil.getStringFromJson(json, "role", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
