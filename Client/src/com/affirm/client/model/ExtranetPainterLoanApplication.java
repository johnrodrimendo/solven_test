package com.affirm.client.model;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanOffer;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class ExtranetPainterLoanApplication extends LoanApplication {

    private IdentityDocumentType documentType;
    private String documentNumber;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        super.fillFromDb(json, catalog, locale);
        if (JsonUtil.getIntFromJson(json, "document_id", null) != null) {
            setDocumentType(catalog.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_id", null)));
        }
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        if (JsonUtil.getJsonArrayFromJson(json, "loan_offer", null) != null) {
            setOffers(new ArrayList<>());
            for (int i = 0; i < json.getJSONArray("loan_offer").length(); i++) {
                LoanOffer offer = new LoanOffer();
                offer.fillFromDb(json.getJSONArray("loan_offer").getJSONObject(i), catalog);
                getOffers().add(offer);
            }
        }
    }

    public IdentityDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(IdentityDocumentType documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
}
