package com.affirm.common.model;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;


public class EntityErrorExtranetPainter extends EntityError {

    private IdentityDocumentType identityDocumentType;
    private String documentNumber;
    private String loanApplicationCode;

    public void fillFromDb(JSONObject jsonObject, CatalogService catalogService) {
        super.fillFromDb(jsonObject, catalogService);
        setDocumentNumber(JsonUtil.getStringFromJson(jsonObject, "document_number", null));
        setLoanApplicationCode(JsonUtil.getStringFromJson(jsonObject, "loan_application_code", null));
        if (JsonUtil.getIntFromJson(jsonObject, "document_id", null) != null) {
            setIdentityDocumentType(catalogService.getIdentityDocumentType(JsonUtil.getIntFromJson(jsonObject, "document_id", null)));
        }
    }

    public IdentityDocumentType getIdentityDocumentType() {
        return identityDocumentType;
    }

    public void setIdentityDocumentType(IdentityDocumentType identityDocumentType) {
        this.identityDocumentType = identityDocumentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getLoanApplicationCode() {
        return loanApplicationCode;
    }

    public void setLoanApplicationCode(String loanApplicationCode) {
        this.loanApplicationCode = loanApplicationCode;
    }
}
