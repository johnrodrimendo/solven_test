package com.affirm.common.model.transactional;

import com.affirm.common.model.RekognitionProData;
import com.affirm.common.model.RekognitionReniecData;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class RekognitionReniecResult implements Serializable {

    public final static char RUNNING_STATUS = 'R';
    public final static char FAILED_STATUS = 'F';
    public final static char SUCCESS_STATUS = 'S';

    private Integer id;
    private Integer loanApplicationId;
    private RekognitionReniecData response;
    private Date registerDate;
    private Character status;


    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setId(JsonUtil.getIntFromJson(json, "rekognition_reniec_result_id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        if(JsonUtil.getJsonObjectFromJson(json, "response", null) != null){
            setResponse(new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "response", null).toString(), RekognitionReniecData.class));
        }
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setStatus(JsonUtil.getCharacterFromJson(json, "status", null));
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

    public RekognitionReniecData getResponse() {
        return response;
    }

    public void setResponse(RekognitionReniecData response) {
        this.response = response;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

}
