package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.PhoneContractOperator;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by john on 29/09/16.
 */
public class LineaResult implements Serializable {
    private Integer queryId;
    private Integer inDocType;
    private String inDocNumber;
    private Integer cantidad;
    private String operador;
    private String lineas;

    public void fillFromDb(JSONObject json) {
        setQueryId(JsonUtil.getIntFromJson(json, "query_id", null));
        setInDocType(JsonUtil.getIntFromJson(json, "in_document_type", null));
        setInDocNumber(JsonUtil.getStringFromJson(json, "in_document_number", null));
        setCantidad(JsonUtil.getIntFromJson(json, "quantity", null));
        setOperador(JsonUtil.getStringFromJson(json, "operator", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_lineas", null) != null) {
            setLineas(JsonUtil.getJsonArrayFromJson(json, "js_lineas", null).toString());
        }
    }

    public Integer getQueryId() {
        return queryId;
    }

    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }

    public Integer getInDocType() {
        return inDocType;
    }

    public void setInDocType(Integer inDocType) {
        this.inDocType = inDocType;
    }

    public String getInDocNumber() {
        return inDocNumber;
    }

    public void setInDocNumber(String inDocNumber) {
        this.inDocNumber = inDocNumber;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getLineas() {
        return lineas;
    }

    public void setLineas(String lineas) {
        this.lineas = lineas;
    }

    public static LineaResult fromVirgin(JSONObject js) {
        if(js.getInt("code") != 0) {
            JSONArray description = js.getJSONArray("description");
            if(description != null)
                System.out.println(description);
            return new LineaResult();
        }
        JSONArray arr = js.getJSONArray("data");

        LineaResult result = new LineaResult();
        result.setCantidad(arr.length());
        result.setLineas(arr.toString());
        result.setOperador(PhoneContractOperator.VIRGIN);
        return result;
    }

    @Override
    public String toString() {
        return "LineaResult{" +
                "queryId=" + queryId +
                ", inDocType=" + inDocType +
                ", inDocNumber='" + inDocNumber + '\'' +
                ", cantidad=" + cantidad +
                ", operador='" + operador + '\'' +
                ", lineas='" + lineas + '\'' +
                '}';
    }
}
