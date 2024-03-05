package com.affirm.common.model;

import com.affirm.common.EntityProductParamIdentityValidationParamsConfig;
import com.affirm.common.model.catalog.IdentityDocumentType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RekognitionValidationData implements Serializable {

    public static final String REKOGNITION_RENIEC_PROCESS_TYPE = "REKOGNITION_RENIEC";
    public static final String SELFIE_DOCUMENTATION_TYPE = "SELFIE_DOCUMENTATION";
    public static final String DOCUMENTATION_RENIEC_TYPE = "DOCUMENTATION_RENIEC";
    public static final String SELFIE_RENIEC_TYPE = "SELFIE_RENIEC";

    private String type;
    private String description;
    private Boolean approved;
    private Boolean notFound;

    public RekognitionValidationData(String type, Boolean approved){
        this.type = type;
        this.approved = approved;
    }

    public RekognitionValidationData(String type, Boolean approved, Boolean notFound){
        this.type = type;
        this.approved = approved;
        this.notFound = notFound;
    }

    public RekognitionValidationData(String type, Boolean approved, Boolean notFound, String processType, String customType, Integer documentTypeId){
        this.type = type;
        this.approved = approved;
        this.notFound = notFound;
        this.description = this.getDescriptionByType(processType, customType, documentTypeId);
    }

    public RekognitionValidationData(String type, String description, Boolean approved){
        this.type = type;
        this.description = description;
        this.approved = approved;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        if(description == null) return getDescriptionByType(null, null, null);
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getDescriptionByType(String processType, String customType, Integer documentTypeId){
        if(description != null) return description;
        if(processType != null && processType.equalsIgnoreCase(REKOGNITION_RENIEC_PROCESS_TYPE)){
            if(customType != null){
                switch (customType){
                    case SELFIE_DOCUMENTATION_TYPE:
                        switch (type){
                            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FACE_MATCHING :
                                return "Comparación de rostros";
                            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_PERSON_NAME:
                                return "Nombres y apellidos consistentes"+additionalDataForDescription(processType, customType, documentTypeId);
                            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_EXPIRATION_DOCUMENT_DATE:
                                if(notFound != null && notFound) return "Fecha de caducidad no encontrada"+additionalDataForDescription(processType, customType, documentTypeId);
                                return "Documento vigente";
                            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_MARITAL_STATUS:
                                return "Estado civil consistente al declarado";
                            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LOCATION:
                                return "Localidad consistente"+additionalDataForDescription(processType, customType, documentTypeId);
                            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DATE_OF_BIRTH:
                                return "Fecha de nacimiento consistente"+additionalDataForDescription(processType, customType, documentTypeId);
                            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_FRONT:
                                return "Número de documento consistente"+additionalDataForDescription(processType, customType, documentTypeId);
                        }
                        break;
                    case DOCUMENTATION_RENIEC_TYPE:
                        switch (type){
                            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FACE_MATCHING :
                                return "Comparación de rostros";
                            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_PERSON_NAME:
                                if(notFound != null && notFound) return "Nombres y/o apellidos no encontrados"+additionalDataForDescription(processType, customType, documentTypeId);
                                return "Nombres y apellidos consistentes";
                            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_EXPIRATION_DOCUMENT_DATE:
                                return "Documento vigente";
                            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_MARITAL_STATUS:
                                if(notFound != null && notFound) return "Estado civil no encontrado"+additionalDataForDescription(processType, customType, documentTypeId);
                                else if(notFound != null && !notFound && !approved) return "Estado civil no consistente al declarado";
                                return "Estado civil";
                            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LOCATION:
                                if(notFound != null && notFound) return "Localidad no encontrada"+additionalDataForDescription(processType, customType, documentTypeId);
                                return "Localidad consistente";
                            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DATE_OF_BIRTH:
                                if(notFound != null && notFound) return "Fecha de nacimiento no encontrada"+additionalDataForDescription(processType, customType, documentTypeId);
                                return "Fecha de nacimiento";
                            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_FRONT:
                                if(notFound != null && notFound) return "Número de documento no encontrado" +additionalDataForDescription(processType, customType, documentTypeId);
                                return "Número de documento consistente";
                        }
                        break;
                    case SELFIE_RENIEC_TYPE:
                        break;
                }
            }
        }
        switch (type){
            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FACE_MATCHING :
                return "Comparación de rostros";
            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_PERSON_NAME:
                return "Nombres y apellidos consistentes";
            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_EXPIRATION_DOCUMENT_DATE:
                return "Documento vigente";
            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_MARITAL_STATUS:
                return "Estado civil consistente al declarado";
            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LOCATION:
                return "Localidad consistente";
            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DATE_OF_BIRTH:
                return "Fecha de nacimiento";
            case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_FRONT:
                return "Números de documento consistentes";
        }
        return null;
    }

    public Boolean getNotFound() {
        return notFound;
    }

    public void setNotFound(Boolean notFound) {
        this.notFound = notFound;
    }

    private String additionalDataForDescription(String processType, String customType, Integer documentTypeId) {
        if(processType != null && processType.equalsIgnoreCase(REKOGNITION_RENIEC_PROCESS_TYPE)){
            if(customType != null){
                switch (customType){
                    case SELFIE_DOCUMENTATION_TYPE:
                        if(documentTypeId != null){
                            if(getNotFound() != null && getNotFound()){
                                switch (documentTypeId){
                                    case IdentityDocumentType.DNI:
                                        return " en foto DNI";
                                    case IdentityDocumentType.CE:
                                        return " en foto CE";
                                }
                            }
                            switch (documentTypeId){
                                case IdentityDocumentType.DNI:
                                    switch (type){
                                        case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DATE_OF_BIRTH:
                                        case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LOCATION:
                                            return " solicitud vs Reniec";
                                    }
                                    return " solicitud vs foto DNI";
                                case IdentityDocumentType.CE:
                                    return " solicitud vs foto CE";
                            }
                        }
                        break;
                    case DOCUMENTATION_RENIEC_TYPE:
                        if(documentTypeId != null){
                            if(getNotFound() != null && getNotFound()){
                                switch (documentTypeId){
                                    case IdentityDocumentType.DNI:
                                        return " en foto DNI";
                                    case IdentityDocumentType.CE:
                                        return " en foto CE";
                                }
                            }
                        }
                        break;
                    case SELFIE_RENIEC_TYPE:
                        break;
                }
            }
        }
        return "";
    }
}
