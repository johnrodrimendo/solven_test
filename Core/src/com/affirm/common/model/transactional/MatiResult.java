package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MatiResult implements Serializable {

    public final static int MATI_STATUS_REJECTED = 0;
    public final static int MATI_STATUS_VERIFIED = 1;
    public final static int MATI_STATUS_REVIEW = 2;

    private Integer id;
    private Integer loanApplicationId;
    private String matiResponse;
    private String matiVerificationId;
    private Date registerDate;
    private Date finishDate;
    private Integer queryId;
    private Integer status;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "mati_result_id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        if(JsonUtil.getJsonObjectFromJson(json, "response", null) != null){
            setMatiResponse(JsonUtil.getJsonObjectFromJson(json, "response", null).toString());
        }
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setFinishDate(JsonUtil.getPostgresDateFromJson(json, "finish_date", null));
        setQueryId(JsonUtil.getIntFromJson(json, "query_id", null));
        setMatiVerificationId(JsonUtil.getStringFromJson(json, "verification_id", null));
        setStatus(JsonUtil.getIntFromJson(json, "status", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public String getMatiResponse() {
        return matiResponse;
    }

    public void setMatiResponse(String matiResponse) {
        this.matiResponse = matiResponse;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Integer getQueryId() {
        return queryId;
    }

    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }

    public String getMatiVerificationId() {
        return matiVerificationId;
    }

    public void setMatiVerificationId(String matiVerificationId) {
        this.matiVerificationId = matiVerificationId;
    }

    public Double getScoreData(){
        if(matiResponse != null){
            JSONObject jsonObject = new JSONObject(matiResponse);
            if(jsonObject != null && JsonUtil.getJsonArrayFromJson(jsonObject, "documents", null) != null){
                JSONArray documents = JsonUtil.getJsonArrayFromJson(jsonObject, "documents", null);
                //Always get first
                if(JsonUtil.getJsonArrayFromJson(documents.getJSONObject(0), "steps", null) != null){
                    JSONArray steps = JsonUtil.getJsonArrayFromJson(documents.getJSONObject(0), "steps", null);
                    for (int i = 0; i < steps.length(); i++) {
                        JSONObject step = steps.getJSONObject(i);
                        String stepId= JsonUtil.getStringFromJson(step, "id", null);
                        if(stepId != null && stepId.equalsIgnoreCase("facematch")){
                            JSONObject stepData =  JsonUtil.getJsonObjectFromJson(step, "data", null);
                            if(stepData != null){
                                return JsonUtil.getDoubleFromJson(stepData, "score", null);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus(){
        if(status != null) return status;
        if(matiResponse != null){
            JSONObject jsonObject = new JSONObject(matiResponse);
            if(jsonObject != null && JsonUtil.getJsonObjectFromJson(jsonObject, "identity", null) != null){
                JSONObject identityJson = JsonUtil.getJsonObjectFromJson(jsonObject, "identity", null);
                if(identityJson != null && identityJson.get("status") != null){
                    switch (identityJson.get("status").toString()){
                        case "verified":
                            return MATI_STATUS_VERIFIED;
                        case "rejected":
                            return MATI_STATUS_REJECTED;
                        case "reviewNeeded":
                            return MATI_STATUS_REVIEW;
                    }
                }
            }
        }
        return null;
    }

    public Double getScore(){
        if(matiResponse != null){
            JSONObject jsonObject = new JSONObject(matiResponse);
            if(jsonObject != null && JsonUtil.getJsonArrayFromJson(jsonObject, "documents", null) != null){
                JSONArray documents = JsonUtil.getJsonArrayFromJson(jsonObject, "documents", null);
                //Always get first
                if(JsonUtil.getJsonArrayFromJson(documents.getJSONObject(0), "steps", null) != null){
                    JSONArray steps = JsonUtil.getJsonArrayFromJson(documents.getJSONObject(0), "steps", null);
                    for (int i = 0; i < steps.length(); i++) {
                        JSONObject step = steps.getJSONObject(i);
                        String id = JsonUtil.getStringFromJson(step, "id", null);
                        if(id != null && id.equalsIgnoreCase("facematch") && JsonUtil.getJsonObjectFromJson(step, "data", null) != null){
                            return JsonUtil.getDoubleFromJson(JsonUtil.getJsonObjectFromJson(step, "data", null),"score", null);
                        }
                    }
                }
            }
        }
        return null;
    }

    public List<String> getErrors(){
        List<String> errors = new ArrayList<>();
        if(matiResponse != null){
            JSONObject jsonObject = new JSONObject(matiResponse);
            if(jsonObject != null && JsonUtil.getJsonObjectFromJson(jsonObject, "computed", null) != null){
                JSONObject computedData = JsonUtil.getJsonObjectFromJson(jsonObject, "computed", null);
                JSONObject isDocumentExpiredJson = computedData != null ? JsonUtil.getJsonObjectFromJson(computedData, "isDocumentExpired", null) : null;
                JSONObject isDocumentExpiredData = isDocumentExpiredJson != null ? JsonUtil.getJsonObjectFromJson(isDocumentExpiredJson, "data", null) : null;
                if(isDocumentExpiredData != null && JsonUtil.getBooleanFromJson(isDocumentExpiredData, "national-id", null) != null && JsonUtil.getBooleanFromJson(isDocumentExpiredData, "national-id", null)) errors.add("Documento se encuentra expirado");
            }
            if(jsonObject != null && JsonUtil.getJsonArrayFromJson(jsonObject, "documents", null) != null){
                JSONArray documents = JsonUtil.getJsonArrayFromJson(jsonObject, "documents", null);
                //Always get first
                if(JsonUtil.getJsonArrayFromJson(documents.getJSONObject(0), "steps", null) != null){
                    JSONArray steps = JsonUtil.getJsonArrayFromJson(documents.getJSONObject(0), "steps", null);
                    for (int i = 0; i < steps.length(); i++) {
                        JSONObject step = steps.getJSONObject(i);
                        JSONObject error = JsonUtil.getJsonObjectFromJson(step, "error", null);
                        if(step.get("id") != null && !step.get("id").toString().equalsIgnoreCase("watchlists") && error != null && error.get("message") != null){
                            if(error.get("message").toString().equalsIgnoreCase("DNI is not defined")) errors.add("Documento no admitido");
                            else if(error.get("message").toString().equalsIgnoreCase("Document is considered as negligence")) errors.add("Posible adulteración documento");
                            else if(error.get("message").toString().equalsIgnoreCase("The face did not match the document")) errors.add("Comparación de rostros fallida");
                            else if(error.get("message").toString().equalsIgnoreCase("The date of birth could not be obtained")) errors.add("La Fecha de nacimiento no pudo validarse");
                            else if(error.get("message").toString().equalsIgnoreCase("Document is considered as fraud attempt")) errors.add("Intento de fraude");
                            else if(error.get("message").toString().equalsIgnoreCase("The DNI number was not found in the RENIEC database")) errors.add("El DNI no fue encontrado en RENIEC");
                            else errors.add(error.get("message").toString());
                        }
                    }
                }
            }
        }
        return errors;
    }

    public List<String> getWashLists(){
        List<String> errors = new ArrayList<>();
        if(matiResponse != null){
            JSONObject jsonObject = new JSONObject(matiResponse);
            if(jsonObject != null && JsonUtil.getJsonArrayFromJson(jsonObject, "documents", null) != null){
                JSONArray documents = JsonUtil.getJsonArrayFromJson(jsonObject, "documents", null);
                //Always get first
                if(JsonUtil.getJsonArrayFromJson(documents.getJSONObject(0), "steps", null) != null){
                    JSONArray steps = JsonUtil.getJsonArrayFromJson(documents.getJSONObject(0), "steps", null);
                    for (int i = 0; i < steps.length(); i++) {
                        JSONObject step = steps.getJSONObject(i);
                        JSONObject error = JsonUtil.getJsonObjectFromJson(step, "error", null);
                        if(step.get("id") != null && step.get("id").toString().equalsIgnoreCase("watchlists") && error != null && error.get("message") != null){
                            if(error.get("message").toString().equalsIgnoreCase("No data to extract information")) errors.add("No se encontró información para extraer");
                            else if(error.get("message").toString().equalsIgnoreCase("Document is considered as fraud attempt")) errors.add("Intento de fraude");
                            else if(error.get("message").toString().equalsIgnoreCase("The DNI number was not found in the RENIEC database")) errors.add("El DNI no fue encontrado en RENIEC");
                            else errors.add(error.get("message").toString());
                        }
                    }
                }
            }
        }
        return errors;
    }

    public String getDocumentNumber(){
        if(matiResponse != null){
            JSONObject jsonObject = new JSONObject(matiResponse);
            if(JsonUtil.getJsonArrayFromJson(jsonObject, "documents", null) != null){
                JSONArray documentsArray = JsonUtil.getJsonArrayFromJson(jsonObject, "documents", null);
                for(int i=0; i<documentsArray.length(); i++){
                    if(documentsArray.getJSONObject(i).has("type") && documentsArray.getJSONObject(i).getString("type").equalsIgnoreCase("national-id")){
                        JSONArray arrSteps = documentsArray.getJSONObject(i).getJSONArray("steps");
                        for(int j=0; j<arrSteps.length(); j++){
                            if(arrSteps.getJSONObject(j).getString("id").equalsIgnoreCase("peruvian-reniec-validation")){
                                if(arrSteps.getJSONObject(j).has("data") && arrSteps.getJSONObject(j).getJSONObject("data").has("dniNumber")){
                                    return arrSteps.getJSONObject(j).getJSONObject("data").getString("dniNumber");
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public String getGender(){
        if(matiResponse != null){
            JSONObject jsonObject = new JSONObject(matiResponse);
            if(JsonUtil.getJsonArrayFromJson(jsonObject, "documents", null) != null){
                JSONArray documentsArray = JsonUtil.getJsonArrayFromJson(jsonObject, "documents", null);
                for(int i=0; i<documentsArray.length(); i++){
                    if(documentsArray.getJSONObject(i).has("type") && documentsArray.getJSONObject(i).getString("type").equalsIgnoreCase("national-id")){
                        JSONArray arrSteps = documentsArray.getJSONObject(i).getJSONArray("steps");
                        for(int j=0; j<arrSteps.length(); j++){
                            if(arrSteps.getJSONObject(j).getString("id").equalsIgnoreCase("document-reading")){
                                if(arrSteps.getJSONObject(j).has("data") && arrSteps.getJSONObject(j).getJSONObject("data").has("sex")){
                                    JSONObject sexJson = arrSteps.getJSONObject(j).getJSONObject("data").optJSONObject("sex");
                                    if(sexJson != null && sexJson.has("value"))
                                        return sexJson.getString("value");
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

}
