package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Romulo Galindo Tanta
 */
public class AnsesResult implements Serializable {

    private Integer queryId;
    private Integer inDocumentType;
    private String inDocumentNumber;
    private String fullName;
    private String cuilCuit;
    private String codigoTransaccion;
    private List<AnsesDetail> details = new ArrayList<>();

    public void fillFromDb(JSONObject json) {
        setQueryId(JsonUtil.getIntFromJson(json, "queryId", null));
        setInDocumentType(JsonUtil.getIntFromJson(json, "in_document_type", null));
        setInDocumentNumber(JsonUtil.getStringFromJson(json, "in_document_number", null));
        setFullName(JsonUtil.getStringFromJson(json, "full_name", null));
        setCuilCuit(JsonUtil.getStringFromJson(json, "cuil_cuit", null));
        setCodigoTransaccion(JsonUtil.getStringFromJson(json, "codigo_transaccion", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_result", null) != null) {
            setDetails(new Gson().fromJson(
                    JsonUtil.getJsonArrayFromJson(json, "js_result", null).toString(),
                    new TypeToken<List<AnsesDetail>>() {
                    }.getType()));
        }
    }

    public Integer getQueryId() {
        return queryId;
    }

    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }

    public Integer getInDocumentType() {
        return inDocumentType;
    }

    public void setInDocumentType(Integer inDocumentType) {
        this.inDocumentType = inDocumentType;
    }

    public String getInDocumentNumber() {
        return inDocumentNumber;
    }

    public void setInDocumentNumber(String inDocumentNumber) {
        this.inDocumentNumber = inDocumentNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCuilCuit() {
        return cuilCuit;
    }

    public void setCuilCuit(String cuilCuit) {
        this.cuilCuit = cuilCuit;
    }

    public String getCodigoTransaccion() {
        return codigoTransaccion;
    }

    public void setCodigoTransaccion(String codigoTransaccion) {
        this.codigoTransaccion = codigoTransaccion;
    }

    public List<AnsesDetail> getDetails() {
        return details;
    }

    public void setDetails(List<AnsesDetail> details) {
        this.details = details;
    }

    public boolean isPensioner() {
        boolean isPensioner = false;
        List<AnsesDetail> list = this.getDetails();
        String regex = "Registra Prestación Previsional(.*)";

        for(int i=0; i<list.size(); i++){
            if(list.get(i).getText().matches(regex)){
                isPensioner = true;
                i = 99;
            }
        }
        return isPensioner;
    }

    public boolean isDependent() {
        boolean isDependent = false;
        List<AnsesDetail> list = this.getDetails();
        String regex = "Registra Declaraciones Juradas como Trabajador en Actividad(.*)";

        for(int i=0; i<list.size(); i++){
            if(list.get(i).getText().matches(regex)){
                isDependent = true;
                i = 99;
            }
        }
        return isDependent;
    }
}
