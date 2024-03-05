package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Romulo Galindo Tanta
 */
public class AfipResult implements Serializable {

    private Integer queryId;
    private Integer inDocumentType;
    private String inDocumentNumber;
    private String fullName;
    private JSONArray irgJSONArray;
    private String df1;
    private String df2;
    private String di1;
    private String di2;
    private String di3;
    private String tipo;
    private String df3;
    private String control1;
    private String control2;
    private String control3;
    private String control4;
    private String controlCategoria;
    private String controlActividad;

    public void fillFromDb(JSONObject json) {
        setQueryId(JsonUtil.getIntFromJson(json, "queryId", null));
        setInDocumentType(JsonUtil.getIntFromJson(json, "in_document_type", null));
        setInDocumentNumber(JsonUtil.getStringFromJson(json, "in_document_number", null));
        setFullName(JsonUtil.getStringFromJson(json, "full_name", null));
        setIrgJSONArray(JsonUtil.getJsonArrayFromJson(json, "extra_information", null));
        setDf1(JsonUtil.getStringFromJson(json, "df1", null));
        setDf2(JsonUtil.getStringFromJson(json, "df2", null));
        setDi1(JsonUtil.getStringFromJson(json, "di1", null));
        setDi2(JsonUtil.getStringFromJson(json, "di2", null));
        setDi3(JsonUtil.getStringFromJson(json, "di3", null));
        setTipo(JsonUtil.getStringFromJson(json, "tipo", null));
        setDf3(JsonUtil.getStringFromJson(json, "df3", null));
        setControl1(JsonUtil.getStringFromJson(json, "control_campo_1", null));
        setControl2(JsonUtil.getStringFromJson(json, "control_campo_2", null));
        setControl3(JsonUtil.getStringFromJson(json, "control_campo_3", null));
        setControl4(JsonUtil.getStringFromJson(json, "control_campo_4", null));
        setControlCategoria(JsonUtil.getStringFromJson(json, "category", null));
        setControlActividad(JsonUtil.getStringFromJson(json, "activity", null));
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

    public JSONArray getIrgJSONArray() { return irgJSONArray; }

    public void setIrgJSONArray(JSONArray irgArray) { this.irgJSONArray = irgArray; }

    public String getDf1() {
        return df1;
    }

    public void setDf1(String df1) {
        this.df1 = df1;
    }

    public String getDf2() {
        return df2;
    }

    public void setDf2(String df2) {
        this.df2 = df2;
    }

    public String getDi1() {
        return di1;
    }

    public void setDi1(String di1) {
        this.di1 = di1;
    }

    public String getDi2() {
        return di2;
    }

    public void setDi2(String di2) {
        this.di2 = di2;
    }

    public String getDi3() {
        return di3;
    }

    public void setDi3(String di3) {
        this.di3 = di3;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDf3() {
        return df3;
    }

    public void setDf3(String df3) {
        this.df3 = df3;
    }

    public String getControl1() {
        return control1;
    }

    public void setControl1(String control1) {
        this.control1 = control1;
    }

    public String getControl2() {
        return control2;
    }

    public void setControl2(String control2) {
        this.control2 = control2;
    }

    public String getControl3() {
        return control3;
    }

    public void setControl3(String control3) {
        this.control3 = control3;
    }

    public String getControl4() {
        return control4;
    }

    public void setControl4(String control4) {
        this.control4 = control4;
    }

    public String getControlCategoria() {
        return controlCategoria;
    }

    public void setControlCategoria(String controlCategoria) {
        this.controlCategoria = controlCategoria;
    }

    public String getControlActividad() {
        return controlActividad;
    }

    public void setControlActividad(String controlActividad) {
        this.controlActividad = controlActividad;
    }

    public String getMonotributistaStartDate() throws ParseException {
        Date startDate = null;
        String dateString = null;

        /*String split[] = this.getControl4().split("(?<=:\\s)",2);

        SimpleDateFormat dmyFormat = new SimpleDateFormat("dd-MM-yyyy");

        startDate = dmyFormat.parse(split[1]);
        dmyFormat.applyPattern("yyyy-MM-dd");

        dateString = dmyFormat.format(startDate);*/

        try{
            String split[] = this.getControl4().split("(?<=-)",2);
            SimpleDateFormat myFormat = new SimpleDateFormat("MM-yyyy");

            startDate = myFormat.parse(split[1]);
            myFormat.applyPattern("MM/yyyy");

            dateString = myFormat.format(startDate);
        } catch (Exception e){
            e.printStackTrace();
        }

        return dateString;
    }

    public String getAutonomoStartDate() throws ParseException {
        Date startDate = null;
        String dateString = null;
        String date = null;
        String regex = "REG. TRAB. AUTONOMO(.*)";

        JSONArray jsonArray = this.getIrgJSONArray();

        try{
            for(int i= 0; i<jsonArray.length(); i++){
                if(jsonArray.getJSONObject(i).get("ir_name").toString().matches(regex)){
                    date = jsonArray.getJSONObject(i).get("ir_date").toString();
                    SimpleDateFormat myFormat = new SimpleDateFormat("MM-yyyy");

                    startDate = myFormat.parse(date);
                    myFormat.applyPattern("MM/yyyy");

                    dateString = myFormat.format(startDate);
                    i = 99;
                }

            }
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        return dateString;
    }

    public ArrayList<String> getTaxRegimesAndDate(){
        ArrayList<String> arrayList = new ArrayList<String>();

        JSONArray jsonArray = this.getIrgJSONArray();
        for(int i = 0; i<jsonArray.length(); i++){
            String ir = jsonArray.getJSONObject(i).get("ir_name").toString() + " (" + jsonArray.getJSONObject(i).get("ir_date").toString() + ")";
            arrayList.add(ir);
        }

        return arrayList;
    }
}
