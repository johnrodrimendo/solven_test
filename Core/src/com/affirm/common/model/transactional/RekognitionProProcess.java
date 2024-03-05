package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.UserFileType;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RekognitionProProcess implements Serializable {

    public final static char RUNNING_STATUS = 'R';
    public final static char FAILED_STATUS = 'F';
    public final static char SUCCESS_STATUS = 'S';

    public final static char FACIAL_ANALYSIS_TYPE = 'F';
    public final static char TEXT_IN_IMAGE_TYPE = 'I';
    public final static char FACE_COMPARISON_TYPE = 'C';
    public final static char LABEL_DETECTION_TYPE = 'L';

    public final static char TEXTRACT_TYPE = 'T';

    public final static char EQUIPMENT_DETECTION_TYPE = 'E';

    public final static char FACE_COMPARISON_RENIEC_TYPE = 'R';

    public final static char FACE_COMPARISON_RENIEC_WITH_DOCUMENT_TYPE = 'D';

    public final static char ISO_CODE_7501_VALIDATION = '7';

    private Integer id;
    private Integer loanApplicationId;
    private JSONObject response;
    private Date registerDate;
    private Date finishDate;
    private Character status;
    private Character type;
    private Integer userFileTypeId;
    private UserFileType userFileType;
    private Integer userFileId;
    private String errorDetail;

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setId(JsonUtil.getIntFromJson(json, "rekognition_pro_process_id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        if(JsonUtil.getJsonObjectFromJson(json, "response", null) != null){
            setResponse(JsonUtil.getJsonObjectFromJson(json, "response", null));
        }
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setFinishDate(JsonUtil.getPostgresDateFromJson(json, "finish_date", null));
        setStatus(JsonUtil.getCharacterFromJson(json, "status", null));
        setType(JsonUtil.getCharacterFromJson(json, "type", null));
        setUserFileTypeId(JsonUtil.getIntFromJson(json, "user_file_type_id", null));
        setUserFileId(JsonUtil.getIntFromJson(json, "user_file_id", null));
        if(userFileTypeId != null){
            setUserFileType(catalogService.getUserFileType(userFileTypeId));
        }
        setErrorDetail(JsonUtil.getStringFromJson(json, "error_detail", null));
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

    public JSONObject getResponse() {
        return response;
    }

    public void setResponse(JSONObject response) {
        this.response = response;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public Character getType() {
        return type;
    }

    public void setType(Character type) {
        this.type = type;
    }

    public Integer getUserFileTypeId() {
        return userFileTypeId;
    }

    public void setUserFileTypeId(Integer userFileTypeId) {
        this.userFileTypeId = userFileTypeId;
    }

    public UserFileType getUserFileType() {
        return userFileType;
    }

    public void setUserFileType(UserFileType userFileType) {
        this.userFileType = userFileType;
    }

    public Integer getUserFileId() {
        return userFileId;
    }

    public void setUserFileId(Integer userFileId) {
        this.userFileId = userFileId;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }
}
