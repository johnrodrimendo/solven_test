package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by stbn on 16/03/17.
 */
public class Contract {

    private Integer id;
    private String tittle;
    private String contract;
    private String pdfPath;
    private Date registerDate;

    public static final int SUMMARY_SHEET_POSITION = 998;
    public static final int INSURANCE_POSITION = 999;

    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "contract_id", null));
        setTittle(JsonUtil.getStringFromJson(json, "contract_tittle", null));
        setContract(JsonUtil.getStringFromJson(json, "contract", null));
        setPdfPath(JsonUtil.getStringFromJson(json, "pdf_path", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }
}
