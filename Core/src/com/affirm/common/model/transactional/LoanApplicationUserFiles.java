package com.affirm.common.model.transactional;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jarmando on 01/03/17.
 */
public class LoanApplicationUserFiles {

    private Integer loanApplicationId;
    private String loanApplicationCode;
    private Boolean filesValidated;
    private Boolean filesUploaded;
    private Integer creditId;
    private String creditCode;
    private List<UserFile> userFileList;

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        loanApplicationId = JsonUtil.getIntFromJson(json, "loan_application_id", null);
        loanApplicationCode = JsonUtil.getStringFromJson(json, "loan_application_code", null);
        filesValidated = JsonUtil.getBooleanFromJson(json, "files_validated", null);
        filesUploaded = JsonUtil.getBooleanFromJson(json, "files_uploaded", null);
        setCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
        setCreditCode(JsonUtil.getStringFromJson(json, "credit_code", null));
        userFileList = new ArrayList<>();
        JSONArray userFiles = JsonUtil.getJsonArrayFromJson(json, "user_files", null);
        for (int i = 0; i < userFiles.length(); i++) {
            JSONObject jsonUserFile = userFiles.getJSONObject(i);
            UserFile userFile = new UserFile();
            userFile.fillFromDb(jsonUserFile, catalogService);
            userFileList.add(userFile);
        }
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public String getLoanApplicationCode() {
        return loanApplicationCode;
    }

    public void setLoanApplicationCode(String loanApplicationCode) {
        this.loanApplicationCode = loanApplicationCode;
    }

    public Boolean getFilesValidated() {
        return filesValidated;
    }

    public void setFilesValidated(Boolean filesValidated) {
        filesValidated = filesValidated;
    }

    public Boolean getFilesUploaded() {
        return filesUploaded;
    }

    public void setFilesUploaded(Boolean filesUploaded) {
        this.filesUploaded = filesUploaded;
    }

    public List<UserFile> getUserFileList() {
        return userFileList;
    }

    public void setUserFileList(List<UserFile> userFileList) {
        this.userFileList = userFileList;
    }

    public Integer getCreditId() {
        return creditId;
    }

    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }
}