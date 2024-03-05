package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

public class Question130Form extends FormGeneric implements Serializable {

    private Question21Form question21Form;
    private Question80Form question80Form;
    private Question108Form question108Form;
    private AddressForm question22Form;
    private Question23Form question23Form;
    private Question24Form question24Form;
    private Question118Form question118Form;
    private Question25Form question25Form;

    public void parseFromJson(JSONArray formJsons){
        for (int i = 0; i < formJsons.length(); i++) {
            JSONObject formJson = formJsons.getJSONObject(i);
            if(formJson != null && formJson.has("data"))
            switch (formJson.getInt("id")){
                case 21:
                    setQuestion21Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question21Form.class));
                    break;
                case 80:
                    setQuestion80Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question80Form.class));
                    break;
                case 108:
                    setQuestion108Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question108Form.class));
                    break;
                case 22:
                    setQuestion22Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), AddressForm.class));
                    break;
                case 23:
                    setQuestion23Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question23Form.class));
                    break;
                case 24:
                    setQuestion24Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question24Form.class));
                    break;
                case 118:
                    setQuestion118Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question118Form.class));
                    break;
                case 25:
                    setQuestion25Form(new Gson().fromJson(formJson.getJSONObject("data").toString(), Question25Form.class));
                    break;
            }
        }
    }

    public Question21Form getQuestion21Form() {
        return question21Form;
    }

    public void setQuestion21Form(Question21Form question21Form) {
        this.question21Form = question21Form;
    }

    public Question80Form getQuestion80Form() {
        return question80Form;
    }

    public void setQuestion80Form(Question80Form question80Form) {
        this.question80Form = question80Form;
    }

    public Question108Form getQuestion108Form() {
        return question108Form;
    }

    public void setQuestion108Form(Question108Form question108Form) {
        this.question108Form = question108Form;
    }

    public AddressForm getQuestion22Form() {
        return question22Form;
    }

    public void setQuestion22Form(AddressForm question22Form) {
        this.question22Form = question22Form;
    }

    public Question23Form getQuestion23Form() {
        return question23Form;
    }

    public void setQuestion23Form(Question23Form question23Form) {
        this.question23Form = question23Form;
    }

    public Question24Form getQuestion24Form() {
        return question24Form;
    }

    public void setQuestion24Form(Question24Form question24Form) {
        this.question24Form = question24Form;
    }

    public Question118Form getQuestion118Form() {
        return question118Form;
    }

    public void setQuestion118Form(Question118Form question118Form) {
        this.question118Form = question118Form;
    }

    public Question25Form getQuestion25Form() {
        return question25Form;
    }

    public void setQuestion25Form(Question25Form question25Form) {
        this.question25Form = question25Form;
    }
}