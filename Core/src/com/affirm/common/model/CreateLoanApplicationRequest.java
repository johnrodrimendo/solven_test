package com.affirm.common.model;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.*;

import java.io.Serializable;

public class CreateLoanApplicationRequest{

    private Integer documentType;
    private String documentNumber;
    private String phoneNumber;
    private String email;
    private Integer productCategoryId;
    private Integer entityId;
    private Boolean skipSMSPinValidationQuestion;
    private Character abTesting;
    private Integer referenceLoanApplicationId;
    private Character origin;
    private Boolean forceCreation;

    public Integer getDocumentType() {
        return documentType;
    }

    public void setDocumentType(Integer documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(Integer productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Boolean getSkipSMSPinValidationQuestion() {
        return skipSMSPinValidationQuestion;
    }

    public void setSkipSMSPinValidationQuestion(Boolean skipSMSPinValidationQuestion) {
        this.skipSMSPinValidationQuestion = skipSMSPinValidationQuestion;
    }

    public Character getAbTesting() {
        return abTesting;
    }

    public void setAbTesting(Character abTesting) {
        this.abTesting = abTesting;
    }

    public Integer getReferenceLoanApplicationId() {
        return referenceLoanApplicationId;
    }

    public void setReferenceLoanApplicationId(Integer referenceLoanApplicationId) {
        this.referenceLoanApplicationId = referenceLoanApplicationId;
    }

    public Character getOrigin() {
        return origin;
    }

    public void setOrigin(Character origin) {
        this.origin = origin;
    }

    public Boolean getForceCreation() {
        return forceCreation;
    }

    public void setForceCreation(Boolean forceCreation) {
        this.forceCreation = forceCreation;
    }
}
