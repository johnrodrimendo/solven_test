package com.affirm.fdlm.creditoconsumo.request;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class ObtenerSucursalRequest {

    private Long departamento;
    private Long ciudad;
    private Long barrio;

    public ObtenerSucursalRequest(JSONObject json) {
        setDepartamento(JsonUtil.getLongFromJson(json, "entity_department_id", null));
        setCiudad(JsonUtil.getLongFromJson(json, "entity_province_id", null));
        setBarrio(JsonUtil.getLongFromJson(json, "entity_locality_id", null));
    }

    public Long getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Long departamento) {
        this.departamento = departamento;
    }

    public Long getCiudad() {
        return ciudad;
    }

    public void setCiudad(Long ciudad) {
        this.ciudad = ciudad;
    }

    public Long getBarrio() {
        return barrio;
    }

    public void setBarrio(Long barrio) {
        this.barrio = barrio;
    }
}
