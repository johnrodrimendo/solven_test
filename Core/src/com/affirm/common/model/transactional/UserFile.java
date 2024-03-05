package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.catalog.UserFileType;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by jrodriguez on 14/06/16.
 */
public class UserFile implements Serializable {

    private Integer id;
    private Integer loanApplicationId;
    private Integer userId;
    private UserFileType fileType;
    private String fileName;
    private Date uploadTime;
    private Boolean validated;
    private ProductCategory productCategory;

    public void fillFromDb(JSONObject json, CatalogService catalog) {
        setId(JsonUtil.getIntFromJson(json, "user_files_id", null));
        setUserId(JsonUtil.getIntFromJson(json, "user_id", null));
        if (JsonUtil.getIntFromJson(json, "filetype_id", null) != null) {
            setFileType(catalog.getUserFileType(JsonUtil.getIntFromJson(json, "filetype_id", null)));
        }
        setFileName(JsonUtil.getStringFromJson(json, "filename", null));
        setUploadTime(JsonUtil.getPostgresDateFromJson(json, "upload_time", null));
        setValidated(JsonUtil.getBooleanFromJson(json, "is_validated", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public UserFileType getFileType() {
        return fileType;
    }

    public void setFileType(UserFileType fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public boolean isDocumentFile(){
        List<String> docType = Arrays.asList(".pdf",".doc",".xlsx",".xls",".dot");
        if(fileName != null){
            return docType.stream().anyMatch(e-> fileName.toLowerCase().contains(e.toLowerCase()));
        }
        return false;
    }
}
