package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class RccIde {

    private String tipReg;
    private String codDeu;
    private Date fecRep;
    private String tipPer;
    private String tipEmp;
    private Integer canEmp;
    private Double porCal0;
    private Double porCal1;
    private Double porCal2;
    private Double porCal3;
    private Double porCal4;

    public void fillFromDb(JSONObject json) throws Exception {
        setTipReg(JsonUtil.getStringFromJson(json, "tip_reg", null));
        setCodDeu(JsonUtil.getStringFromJson(json, "cod_deu", null));
        setFecRep(JsonUtil.getPostgresDateFromJson(json, "fec_rep", null));
        setTipPer(JsonUtil.getStringFromJson(json, "tip_per", null));
        setTipEmp(JsonUtil.getStringFromJson(json, "tip_emp", null));
        setCanEmp(JsonUtil.getIntFromJson(json, "can_emp", null));
        setPorCal0(JsonUtil.getDoubleFromJson(json, "por_cal0", null));
        setPorCal1(JsonUtil.getDoubleFromJson(json, "por_cal1", null));
        setPorCal2(JsonUtil.getDoubleFromJson(json, "por_cal2", null));
        setPorCal3(JsonUtil.getDoubleFromJson(json, "por_cal3", null));
        setPorCal4(JsonUtil.getDoubleFromJson(json, "por_cal4", null));
    }

    public String getTipReg() {
        return tipReg;
    }

    public void setTipReg(String tipReg) {
        this.tipReg = tipReg;
    }

    public String getCodDeu() {
        return codDeu;
    }

    public void setCodDeu(String codDeu) {
        this.codDeu = codDeu;
    }

    public Date getFecRep() {
        return fecRep;
    }

    public void setFecRep(Date fecRep) {
        this.fecRep = fecRep;
    }

    public String getTipPer() {
        return tipPer;
    }

    public void setTipPer(String tipPer) {
        this.tipPer = tipPer;
    }

    public String getTipEmp() {
        return tipEmp;
    }

    public void setTipEmp(String tipEmp) {
        this.tipEmp = tipEmp;
    }

    public Integer getCanEmp() {
        return canEmp;
    }

    public void setCanEmp(Integer canEmp) {
        this.canEmp = canEmp;
    }

    public Double getPorCal0() {
        return porCal0;
    }

    public void setPorCal0(Double porCal0) {
        this.porCal0 = porCal0;
    }

    public Double getPorCal1() {
        return porCal1;
    }

    public void setPorCal1(Double porCal1) {
        this.porCal1 = porCal1;
    }

    public Double getPorCal2() {
        return porCal2;
    }

    public void setPorCal2(Double porCal2) {
        this.porCal2 = porCal2;
    }

    public Double getPorCal3() {
        return porCal3;
    }

    public void setPorCal3(Double porCal3) {
        this.porCal3 = porCal3;
    }

    public Double getPorCal4() {
        return porCal4;
    }

    public void setPorCal4(Double porCal4) {
        this.porCal4 = porCal4;
    }
}
