package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.RccEntity;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class RccSal {

    private String tipReg;
    private String codDeu;
    private String codEmp;
    private String tipCre;
    private String codCue;
    private Integer conDia;
    private Double saldo;
    private String claDeu;
    private Date fecRep;
    private String entityShortName;
    private String tipPer;

    public void fillFromDb(JSONObject json, CatalogService catalogService) throws Exception {
        setTipReg(JsonUtil.getStringFromJson(json, "tip_reg", null));
        setCodDeu(JsonUtil.getStringFromJson(json, "cod_deu", null));
        setCodEmp(JsonUtil.getStringFromJson(json, "cod_emp", null));
        setTipCre(JsonUtil.getStringFromJson(json, "tip_cre", null));
        setCodCue(JsonUtil.getStringFromJson(json, "cod_cue", null));
        setConDia(JsonUtil.getIntFromJson(json, "con_dia", null));
        setSaldo(JsonUtil.getDoubleFromJson(json, "saldo", null));
        setClaDeu(JsonUtil.getStringFromJson(json, "cla_deu", null));
        setFecRep(JsonUtil.getPostgresDateFromJson(json, "fec_rep", null));
        if(codEmp != null){
            try{
                RccEntity rccEntity = catalogService.getRccEntity(codEmp);
                if(rccEntity != null) setEntityShortName(rccEntity.getShortName());
            }
            catch (Exception e){

            }
        }

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

    public String getCodEmp() {
        return codEmp;
    }

    public void setCodEmp(String codEmp) {
        this.codEmp = codEmp;
    }

    public String getTipCre() {
        return tipCre;
    }

    public void setTipCre(String tipCre) {
        this.tipCre = tipCre;
    }

    public String getCodCue() {
        return codCue;
    }

    public void setCodCue(String codCue) {
        this.codCue = codCue;
    }

    public Integer getConDia() {
        return conDia;
    }

    public void setConDia(Integer conDia) {
        this.conDia = conDia;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public String getClaDeu() {
        return claDeu;
    }

    public void setClaDeu(String claDeu) {
        this.claDeu = claDeu;
    }

    public Date getFecRep() {
        return fecRep;
    }

    public void setFecRep(Date fecRep) {
        this.fecRep = fecRep;
    }

    public String getEntityShortName() {
        return entityShortName;
    }

    public void setEntityShortName(String entityShortName) {
        this.entityShortName = entityShortName;
    }

    public String getTipPer() {
        return tipPer;
    }

    public void setTipPer(String tipPer) {
        this.tipPer = tipPer;
    }
}
