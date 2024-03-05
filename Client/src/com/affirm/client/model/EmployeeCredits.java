package com.affirm.client.model;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */
public class EmployeeCredits {

    private Integer personId;
    private Integer employeeId;
    private IdentityDocumentType docType;
    private String docNumber;
    private String name;
    private String firstSurname;
    private String lastSurname;
    private int credits;
    private double totalCreditAmount;
    private double totalCreditAmountToPay;
    private double totalPendingAmount;
    private Date registerDate;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setEmployeeId(JsonUtil.getIntFromJson(json, "employee_id", null));
        if (JsonUtil.getIntFromJson(json, "document_type_id", null) != null) {
            setDocType(catalog.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_type_id", null)));
        }
        setDocNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setName(JsonUtil.getStringFromJson(json, "person_name", null));
        setFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        setCredits(JsonUtil.getIntFromJson(json, "credits", null));
        setTotalCreditAmount(JsonUtil.getDoubleFromJson(json, "total_credit_amount", null));
        setTotalCreditAmountToPay(JsonUtil.getDoubleFromJson(json, "total_credit_amount_to_pay", null));
        setTotalPendingAmount(JsonUtil.getDoubleFromJson(json, "total_pending_amount", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
    }

    public String getFullName() {
        return (getName() != null ? getName() : "") + " " + (getFirstSurname() != null ? getFirstSurname() : "") + " " + (getLastSurname() != null ? getLastSurname() : "");
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public IdentityDocumentType getDocType() {
        return docType;
    }

    public void setDocType(IdentityDocumentType docType) {
        this.docType = docType;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public String getLastSurname() {
        return lastSurname;
    }

    public void setLastSurname(String lastSurname) {
        this.lastSurname = lastSurname;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public double getTotalCreditAmount() {
        return totalCreditAmount;
    }

    public void setTotalCreditAmount(double totalCreditAmount) {
        this.totalCreditAmount = totalCreditAmount;
    }

    public double getTotalPendingAmount() {
        return totalPendingAmount;
    }

    public void setTotalPendingAmount(double totalPendingAmount) {
        this.totalPendingAmount = totalPendingAmount;
    }

    public double getTotalCreditAmountToPay() {
        return totalCreditAmountToPay;
    }

    public void setTotalCreditAmountToPay(double totalCreditAmountToPay) {
        this.totalCreditAmountToPay = totalCreditAmountToPay;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }
}
