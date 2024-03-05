package com.affirm.common.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OperatorManagementReport {

    private Integer sysUserId;
    //    private String sysUserName;
//    private String sysUserFirstSurname;
//    private String sysUserLastSurname;
    private List<OperatorManagementReportDetail> details;


    public void fillFromDb(JSONObject json) {
        setSysUserId(JsonUtil.getIntFromJson(json, "sysuser_id", null));
        details = new ArrayList<>();
        details.add(new OperatorManagementReportDetail(OperatorManagementReportDetail.TYPE_PERIOD_1, json));
        details.add(new OperatorManagementReportDetail(OperatorManagementReportDetail.TYPE_PERIOD_2, json));
        details.add(new OperatorManagementReportDetail(OperatorManagementReportDetail.TYPE_MONTH, json));
        details.add(new OperatorManagementReportDetail(OperatorManagementReportDetail.TYPE_U6M, json));
        details.add(new OperatorManagementReportDetail(OperatorManagementReportDetail.TYPE_YTD, json));
    }

    public OperatorManagementReportDetail getDetailByType(int type){
        return details.stream().filter(d -> d.getType().equals(type)).findFirst().orElse(null);
    }

    public Integer getTotalCallings(){
        return details.stream().mapToInt(d -> d.getCallings()).sum();
    }

    public Integer getTotalTrackings(){
        return details.stream().mapToInt(d -> d.getTrackings()).sum();
    }

    public Integer getTotalDisbursements(){
        return details.stream().mapToInt(d -> d.getDisbursements()).sum();
    }

    public Double getTotalDisbursementsAmount(){
        return details.stream().mapToDouble(d -> d.getDisbursementsAmount()).sum();
    }

    public Integer getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(Integer sysUserId) {
        this.sysUserId = sysUserId;
    }

    public List<OperatorManagementReportDetail> getDetails() {
        return details;
    }

    public void setDetails(List<OperatorManagementReportDetail> details) {
        this.details = details;
    }
}
