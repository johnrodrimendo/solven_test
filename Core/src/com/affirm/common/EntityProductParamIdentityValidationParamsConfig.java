package com.affirm.common;

import java.util.List;

public class EntityProductParamIdentityValidationParamsConfig {

    public static final String LABEL_KEY_DOCUMENT_NUMBER_FRONT = "documentNumberFront";
    public static final String LABEL_KEY_FIRST_SURNAME = "firstSurname";
    public static final String LABEL_KEY_LAST_SURNAME = "lastSurname";
    public static final String LABEL_KEY_PERSON_NAME = "personName";
    public static final String LABEL_KEY_EXPIRATION_DOCUMENT_DATE = "expirationDocumentDate";
    public static final String LABEL_KEY_DATE_OF_BIRTH = "dateOfBirth";
    public static final String LABEL_KEY_DOCUMENT_NUMBER_BACK = "documentNumberBack";
    public static final String LABEL_KEY_GENERATED_DOCUMENT_DATA = "generatedDocumentDate";
    public static final String LABEL_KEY_MARITAL_STATUS = "maritalStatus";
    public static final String LABEL_KEY_LOCATION = "location";
    public static final String LABEL_KEY_FACE_MATCHING = "faceMatching";

    private String type;
    private Boolean exclude;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getExclude() {
        return exclude;
    }

    public void setExclude(Boolean exclude) {
        this.exclude = exclude;
    }
}
