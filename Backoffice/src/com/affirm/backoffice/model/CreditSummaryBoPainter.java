package com.affirm.backoffice.model;

import com.affirm.common.model.catalog.CreditStatus;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

/**
 * Created by sTbn on 23/06/16.
 */
public class CreditSummaryBoPainter {

    private int id;
    private String code;
    private int loanApplicationId;
    private int loanOfferId;
    private Date registerDate;
    private CreditStatus status;
    private Date disbursementDate;
    private IdentityDocumentType documentType;
    private String documentNumber;
    private String personName;
    private String personFirstSurname;
    private String personLastSurname;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {

        setId(JsonUtil.getIntFromJson(json, "credit_id", null));
        setCode(JsonUtil.getStringFromJson(json, "credit_code", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setLoanOfferId(JsonUtil.getIntFromJson(json, "loan_offer_id", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setStatus(catalog.getCreditStatus(locale, JsonUtil.getIntFromJson(json, "credit_status_id", null)));
        setDisbursementDate(JsonUtil.getPostgresDateFromJson(json, "disbursement_date", null));
        Integer documentTypeId;
        if ((documentTypeId = JsonUtil.getIntFromJson(json, "document_id", null)) != null) {
            setDocumentType(catalog.getIdentityDocumentType(documentTypeId));
        }
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setPersonName(JsonUtil.getStringFromJson(json, "person_name", null));
        setPersonFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setPersonLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(int loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public int getLoanOfferId() {
        return loanOfferId;
    }

    public void setLoanOfferId(int loanOfferId) {
        this.loanOfferId = loanOfferId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public CreditStatus getStatus() {
        return status;
    }

    public void setStatus(CreditStatus status) {
        this.status = status;
    }

    public Date getDisbursementDate() {
        return disbursementDate;
    }

    public void setDisbursementDate(Date disbursementDate) {
        this.disbursementDate = disbursementDate;
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

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonFirstSurname() {
        return personFirstSurname;
    }

    public void setPersonFirstSurname(String personFirstSurname) {
        this.personFirstSurname = personFirstSurname;
    }

    public String getPersonLastSurname() {
        return personLastSurname;
    }

    public void setPersonLastSurname(String personLastSurname) {
        this.personLastSurname = personLastSurname;
    }
}
