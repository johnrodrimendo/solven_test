package com.affirm.mati.model;

public class MatiValidationError {

    public static final String MATI_IMAGE_SIZE_ERROR = "smallImageSize";
    public static final String MATI_BLURRY_TEXT_ERROR = "blurryText";
    public static final String MATI_MASK_DETECTED_ERROR = "maskDetected";

    private Integer userFileId;
    private String code;

    public MatiValidationError(Integer userFileId, String code) {
        this.userFileId = userFileId;
        this.code = code;
    }

    public Integer getUserFileId() {
        return userFileId;
    }

    public void setUserFileId(Integer userFileId) {
        this.userFileId = userFileId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
