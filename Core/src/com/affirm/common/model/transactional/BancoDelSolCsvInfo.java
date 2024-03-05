package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class BancoDelSolCsvInfo {

    private Integer incomes;
    private Date employmentStartDate;
    private Integer level1;
    private Integer level2;
    private Integer level3;


    public void fillFromDb(JSONObject json) {
        setIncomes(JsonUtil.getIntFromJson(json, "incomes", null));
        setEmploymentStartDate(JsonUtil.getPostgresDateFromJson(json, "employment_start_date", null));
        setLevel1(JsonUtil.getIntFromJson(json, "level_1", null));
        setLevel2(JsonUtil.getIntFromJson(json, "level_2", null));
        setLevel3(JsonUtil.getIntFromJson(json, "level_3", null));
    }

    public Integer getIncomes() {
        return incomes;
    }

    public void setIncomes(Integer incomes) {
        this.incomes = incomes;
    }

    public Date getEmploymentStartDate() {
        return employmentStartDate;
    }

    public void setEmploymentStartDate(Date employmentStartDate) {
        this.employmentStartDate = employmentStartDate;
    }

    public Integer getLevel1() {
        return level1;
    }

    public void setLevel1(Integer level1) {
        this.level1 = level1;
    }

    public Integer getLevel2() {
        return level2;
    }

    public void setLevel2(Integer level2) {
        this.level2 = level2;
    }

    public Integer getLevel3() {
        return level3;
    }

    public void setLevel3(Integer level3) {
        this.level3 = level3;
    }
}
