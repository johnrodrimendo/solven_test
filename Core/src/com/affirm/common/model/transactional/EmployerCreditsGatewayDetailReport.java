package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Employer;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dev5 on 19/06/17.
 */
public class EmployerCreditsGatewayDetailReport {

    private Date period;
    private Integer personId;
    private IdentityDocumentType documentType;
    private String documentNumber;
    private String name;
    private String firstSurname;
    private String lastSurname;
    private List<Product> productIds;
    private List<Employer> employerIds;
    private Integer credits;
    private Double salaryAdvanceAmount;
    private Double creditAmount;
    private Double discountAmount;

    public void fillFromDb(JSONObject json, CatalogService catalogService){
        setPeriod(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        if(JsonUtil.getIntFromJson(json, "document_id", null) != null)
            setDocumentType(catalogService.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_id", null)));
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setName(JsonUtil.getStringFromJson(json, "person_name", null));
        setFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        if(JsonUtil.getJsonArrayFromJson(json, "ar_product_id", null) != null){
            setProductIds(new ArrayList<>());
            JSONArray array = JsonUtil.getJsonArrayFromJson(json, "ar_product_id", null);
            for(int i=0; i<array.length(); i++){
                getProductIds().add(catalogService.getProduct(array.getInt(i)));
            }
        }
        if(JsonUtil.getJsonArrayFromJson(json, "ar_employer_id", null) != null){
            setEmployerIds(new ArrayList<>());
            JSONArray array = JsonUtil.getJsonArrayFromJson(json, "ar_employer_id", null);
            for(int i=0; i<array.length(); i++){
                getEmployerIds().add(catalogService.getEmployer(array.getInt(i)));
            }
        }
        setCredits(JsonUtil.getIntFromJson(json, "credits", null));
        setSalaryAdvanceAmount(JsonUtil.getDoubleFromJson(json, "salary_advance_amount", null));
        setCreditAmount(JsonUtil.getDoubleFromJson(json, "credit_amount", null));
        setDiscountAmount(JsonUtil.getDoubleFromJson(json, "discount_amount", null));
    }

    public String getFullName() {
        return (getName() != null ? getName() : "") + " " + (getFirstSurname() != null ? getFirstSurname() : "") + " " + (getLastSurname() != null ? getLastSurname() : "");
    }

    public Date getPeriod() {
        return period;
    }

    public void setPeriod(Date period) {
        this.period = period;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
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

    public List<Product> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Product> productIds) {
        this.productIds = productIds;
    }

    public List<Employer> getEmployerIds() {
        return employerIds;
    }

    public void setEmployerIds(List<Employer> employerIds) {
        this.employerIds = employerIds;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Double getSalaryAdvanceAmount() {
        return salaryAdvanceAmount;
    }

    public void setSalaryAdvanceAmount(Double salaryAdvanceAmount) {
        this.salaryAdvanceAmount = salaryAdvanceAmount;
    }

    public Double getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(Double creditAmount) {
        this.creditAmount = creditAmount;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }
}
