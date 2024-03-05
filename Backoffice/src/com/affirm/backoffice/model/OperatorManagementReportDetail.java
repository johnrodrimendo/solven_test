package com.affirm.backoffice.model;

import com.affirm.common.util.JsonUtil;
import com.affirm.security.model.SysUser;
import org.json.JSONObject;

public class OperatorManagementReportDetail {


    public static final int TYPE_MONTH = 1;
    public static final int TYPE_U6M = 2;
    public static final int TYPE_YTD = 3;
    public static final int TYPE_PERIOD_1 = 4;
    public static final int TYPE_PERIOD_2 = 5;

    private Integer type;
    private Integer callings;
    private Integer trackings;
    private Integer disbursements;
    private Double disbursementsAmount;
    private Integer assistedProcess;
    private Integer assistedProcessContact;
    private Integer assistedProcessNoContact;
    private Integer nonAssistedProcess;
    private Integer nonAssistedProcessContact;
    private Integer nonAssistedProcessNoContact;
    private Integer scheduledDate1;
    private Integer scheduledDate2;
    private Integer scheduledDateMore;

    public OperatorManagementReportDetail(int type, JSONObject json) {
        String keySuffix = "";
        if (type == TYPE_MONTH)
            keySuffix = "mtd";
        else if (type == TYPE_U6M)
            keySuffix = "6m";
        else if (type == TYPE_YTD)
            keySuffix = "ytd";

        setType(type);
        setCallings(JsonUtil.getIntFromJson(json, "callings_" + keySuffix, null));
        setTrackings(JsonUtil.getIntFromJson(json, "trackings_" + keySuffix, null));
        setDisbursements(JsonUtil.getIntFromJson(json, "disbursements_" + keySuffix, null));
        setDisbursementsAmount(JsonUtil.getDoubleFromJson(json, "disbursements_amount_" + keySuffix, null));
        setAssistedProcess(JsonUtil.getIntFromJson(json, "assisted_process_" + keySuffix, null));
        setAssistedProcessContact(JsonUtil.getIntFromJson(json, "assisted_process_contact_" + keySuffix, null));
        setAssistedProcessNoContact(JsonUtil.getIntFromJson(json, "assisted_process_no_contact_" + keySuffix, null));
        setNonAssistedProcess(JsonUtil.getIntFromJson(json, "non_assisted_process_" + keySuffix, null));
        setNonAssistedProcessContact(JsonUtil.getIntFromJson(json, "non_assisted_process_contact_" + keySuffix, null));
        setNonAssistedProcessNoContact(JsonUtil.getIntFromJson(json, "non_assisted_process_no_contact_" + keySuffix, null));
        setScheduledDate1(JsonUtil.getIntFromJson(json, "scheduled_date_1_" + keySuffix, null));
        setScheduledDate2(JsonUtil.getIntFromJson(json, "scheduled_date_2_" + keySuffix, null));
        setScheduledDateMore(JsonUtil.getIntFromJson(json, "scheduled_date_more_" + keySuffix, null));
    }

    public Double getEffectiveness() {
        double toDivide = (((assistedProcessContact != null ? assistedProcessContact : 0) * 1.0) + ((nonAssistedProcessContact != null ? nonAssistedProcessContact : 0) * 1.0));
        if(toDivide == 0.0)
            return 0.0;

        return ((disbursements != null ? disbursements : 0) * 1.0) / (((assistedProcessContact != null ? assistedProcessContact : 0) * 1.0) + ((nonAssistedProcessContact != null ? nonAssistedProcessContact : 0) * 1.0));
    }

    public String getTypeName() {
        if (type == TYPE_MONTH)
            return "MTD";
        else if (type == TYPE_U6M)
            return "U6M";
        else if (type == TYPE_YTD)
            return "YTD";

        return "??";
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getCallings() {
        return callings;
    }

    public void setCallings(Integer callings) {
        this.callings = callings;
    }

    public Integer getTrackings() {
        return trackings;
    }

    public void setTrackings(Integer trackings) {
        this.trackings = trackings;
    }

    public Integer getDisbursements() {
        return disbursements;
    }

    public void setDisbursements(Integer disbursements) {
        this.disbursements = disbursements;
    }

    public Double getDisbursementsAmount() {
        return disbursementsAmount;
    }

    public void setDisbursementsAmount(Double disbursementsAmount) {
        this.disbursementsAmount = disbursementsAmount;
    }

    public Integer getAssistedProcess() {
        return assistedProcess;
    }

    public void setAssistedProcess(Integer assistedProcess) {
        this.assistedProcess = assistedProcess;
    }

    public Integer getAssistedProcessContact() {
        return assistedProcessContact;
    }

    public void setAssistedProcessContact(Integer assistedProcessContact) {
        this.assistedProcessContact = assistedProcessContact;
    }

    public Integer getAssistedProcessNoContact() {
        return assistedProcessNoContact;
    }

    public void setAssistedProcessNoContact(Integer assistedProcessNoContact) {
        this.assistedProcessNoContact = assistedProcessNoContact;
    }

    public Integer getNonAssistedProcess() {
        return nonAssistedProcess;
    }

    public void setNonAssistedProcess(Integer nonAssistedProcess) {
        this.nonAssistedProcess = nonAssistedProcess;
    }

    public Integer getNonAssistedProcessContact() {
        return nonAssistedProcessContact;
    }

    public void setNonAssistedProcessContact(Integer nonAssistedProcessContact) {
        this.nonAssistedProcessContact = nonAssistedProcessContact;
    }

    public Integer getNonAssistedProcessNoContact() {
        return nonAssistedProcessNoContact;
    }

    public void setNonAssistedProcessNoContact(Integer nonAssistedProcessNoContact) {
        this.nonAssistedProcessNoContact = nonAssistedProcessNoContact;
    }

    public Integer getScheduledDate1() {
        return scheduledDate1;
    }

    public void setScheduledDate1(Integer scheduledDate1) {
        this.scheduledDate1 = scheduledDate1;
    }

    public Integer getScheduledDate2() {
        return scheduledDate2;
    }

    public void setScheduledDate2(Integer scheduledDate2) {
        this.scheduledDate2 = scheduledDate2;
    }

    public Integer getScheduledDateMore() {
        return scheduledDateMore;
    }

    public void setScheduledDateMore(Integer scheduledDateMore) {
        this.scheduledDateMore = scheduledDateMore;
    }
}
