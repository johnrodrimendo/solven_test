package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.Marshall;
import com.affirm.equifax.ws.ReporteCrediticio;
import com.affirm.nosis.NosisResult;
import com.affirm.nosis.restApi.NosisRestResult;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class ApplicationBureau implements Serializable {

    private Integer id;
    private Integer loanApplicationId;
    private Integer clusterId;
    private Integer score;
    private String riskLevel;
    private String conclusion;
    private String equifaxResult;
    private Date registerDate;
    private Integer bureauId;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "application_bureau_id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setClusterId(JsonUtil.getIntFromJson(json, "cluster_id", null));
        setScore(JsonUtil.getIntFromJson(json, "score", null));
        setRiskLevel(JsonUtil.getStringFromJson(json, "risk_level", null));
        setConclusion(JsonUtil.getStringFromJson(json, "conclusion", null));
        if(JsonUtil.getJsonObjectFromJson(json, "equifax_result", null) != null)
            setEquifaxResult(JsonUtil.getJsonObjectFromJson(json, "equifax_result", null).toString());
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setBureauId(JsonUtil.getIntFromJson(json, "bureau_id", null));
    }

    public NosisRestResult getNosisRestResult() {
        if(equifaxResult == null)
            return null;
        NosisRestResult restResult = new NosisRestResult();
        restResult.fillFromJson(new JSONObject(equifaxResult));
        return restResult;
    }

    public NosisResult getNosisResult() throws Exception{
        if(equifaxResult == null)
            return null;
        return new Marshall().unmarshall(equifaxResult, NosisResult.class);
    }

    public ReporteCrediticio getEquifaxReportCreditcio() throws Exception{
        if(equifaxResult == null)
            return null;
        return new Marshall().unmarshall(equifaxResult, ReporteCrediticio.class);
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

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public String getEquifaxResult() {
        return equifaxResult;
    }

    public void setEquifaxResult(String equifaxResult) {
        this.equifaxResult = equifaxResult;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Integer getBureauId() {
        return bureauId;
    }

    public void setBureauId(Integer bureauId) {
        this.bureauId = bureauId;
    }
}
