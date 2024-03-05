package com.affirm.backoffice.model;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.backoffice.util.IPaginationWrapperElement;
import com.affirm.backoffice.util.IPaginationWrapperElement;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.Employer;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;
import org.springframework.context.MessageSource;

import java.util.Date;
import java.util.Locale;

/**
 * Created by dev5 on 18/09/17.
 */
public class ReportCreditGateway implements IPaginationWrapperElement {

    private Date registerDate;
    private Double amount;
    private Integer creditId;
    private String loanApplicatonCode;
    private String creditCode;
    private Integer personId;
    private IdentityDocumentType identityDocumentType;
    private String documentNumber;
    private String ruc;
    private String surname;
    private String personName;
    private Entity entity;
    private Employer employer;
    private Double loanCapital;
    private Double insurance;
    private Double collectionCommission;
    private Double collectionCommissionTax;
    private Double moratoriumInterest;
    private Double moratoriumInterestTax;
    private Double interest;
    private Double interestTax;
    private Double installmentCapital;
    private Double moratoriumCharge;
    private Double moratoriumChargeTax;
    private Double taxWithHolding;
    private Double solvenDistribution;
    private Double netToEntity;
    private CountryParam country;

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setAmount(JsonUtil.getDoubleFromJson(json, "amount", null));
        setCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
        setCreditCode(JsonUtil.getStringFromJson(json, "credit_code", null));
        setLoanApplicatonCode(JsonUtil.getStringFromJson(json, "loan_application_code", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        if(JsonUtil.getIntFromJson(json, "document_id", null) != null)
            setIdentityDocumentType(catalog.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_id", null)));
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setRuc(JsonUtil.getStringFromJson(json, "ruc", null));
        setSurname(JsonUtil.getStringFromJson(json, "surname", null));
        setPersonName(JsonUtil.getStringFromJson(json, "person_name", null));
        if(JsonUtil.getIntFromJson(json, "entity_id", null) != null)
            setEntity(catalog.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        if(JsonUtil.getIntFromJson(json, "employer_id", null) != null)
            setEmployer(catalog.getEmployer(JsonUtil.getIntFromJson(json, "employer_id", null)));
        setLoanCapital(JsonUtil.getDoubleFromJson(json, "loan_capital", null));
        setInsurance(JsonUtil.getDoubleFromJson(json, "insurance", null));
        setCollectionCommission(JsonUtil.getDoubleFromJson(json, "collection_commission", null));
        setCollectionCommissionTax(JsonUtil.getDoubleFromJson(json, "collection_commission_tax", null));
        setMoratoriumInterest(JsonUtil.getDoubleFromJson(json, "moratorium_interest", null));
        setMoratoriumInterestTax(JsonUtil.getDoubleFromJson(json, "moratorium_interest_tax", null));
        setInterest(JsonUtil.getDoubleFromJson(json, "interest", null));
        setInterestTax(JsonUtil.getDoubleFromJson(json, "interest_tax", null));
        setInstallmentCapital(JsonUtil.getDoubleFromJson(json, "installment_capital", null));
        setMoratoriumCharge(JsonUtil.getDoubleFromJson(json, "moratorium_charge", null));
        setMoratoriumChargeTax(JsonUtil.getDoubleFromJson(json, "moratorium_charge_tax", null));
        setTaxWithHolding(JsonUtil.getDoubleFromJson(json, "tax_withholding", null));
        setSolvenDistribution(JsonUtil.getDoubleFromJson(json, "solven_distribution", null));
        setNetToEntity(JsonUtil.getDoubleFromJson(json, "net_to_entity", null));
        setCountry(catalog.getCountryParam(JsonUtil.getIntFromJson(json, "country_id", null)));
    }

    @Override
    public void fillFromDb(JSONObject json, CatalogService catalog, MessageSource messageSource, Locale locale) throws Exception {
        fillFromDb(json, catalog);
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getCreditId() {
        return creditId;
    }

    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    public Double getLoanCapital() {
        return loanCapital;
    }

    public void setLoanCapital(Double loanCapital) {
        this.loanCapital = loanCapital;
    }

    public Double getInsurance() {
        return insurance;
    }

    public void setInsurance(Double insurance) {
        this.insurance = insurance;
    }

    public Double getCollectionCommission() {
        return collectionCommission;
    }

    public void setCollectionCommission(Double collectionCommission) {
        this.collectionCommission = collectionCommission;
    }

    public Double getCollectionCommissionTax() {
        return collectionCommissionTax;
    }

    public void setCollectionCommissionTax(Double collectionCommissionTax) {
        this.collectionCommissionTax = collectionCommissionTax;
    }

    public Double getMoratoriumInterest() {
        return moratoriumInterest;
    }

    public void setMoratoriumInterest(Double moratoriumInterest) {
        this.moratoriumInterest = moratoriumInterest;
    }

    public Double getMoratoriumInterestTax() {
        return moratoriumInterestTax;
    }

    public void setMoratoriumInterestTax(Double moratoriumInterestTax) {
        this.moratoriumInterestTax = moratoriumInterestTax;
    }

    public Double getInterest() {
        return interest;
    }

    public void setInterest(Double interest) {
        this.interest = interest;
    }

    public Double getInterestTax() {
        return interestTax;
    }

    public void setInterestTax(Double interestTax) {
        this.interestTax = interestTax;
    }

    public Double getInstallmentCapital() {
        return installmentCapital;
    }

    public void setInstallmentCapital(Double installmentCapital) {
        this.installmentCapital = installmentCapital;
    }

    public Double getMoratoriumCharge() {
        return moratoriumCharge;
    }

    public void setMoratoriumCharge(Double moratoriumCharge) {
        this.moratoriumCharge = moratoriumCharge;
    }

    public Double getMoratoriumChargeTax() {
        return moratoriumChargeTax;
    }

    public void setMoratoriumChargeTax(Double moratoriumChargeTax) {
        this.moratoriumChargeTax = moratoriumChargeTax;
    }

    public Double getTaxWithHolding() {
        return taxWithHolding;
    }

    public void setTaxWithHolding(Double taxWithHolding) {
        this.taxWithHolding = taxWithHolding;
    }

    public Double getSolvenDistribution() {
        return solvenDistribution;
    }

    public void setSolvenDistribution(Double solvenDistribution) {
        this.solvenDistribution = solvenDistribution;
    }

    public Double getNetToEntity() {
        return netToEntity;
    }

    public void setNetToEntity(Double netToEntity) {
        this.netToEntity = netToEntity;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getLoanApplicatonCode() {
        return loanApplicatonCode;
    }

    public void setLoanApplicatonCode(String loanApplicatonCode) {
        this.loanApplicatonCode = loanApplicatonCode;
    }

    public CountryParam getCountry() {
        return country;
    }

    public void setCountry(CountryParam country) {
        this.country = country;
    }
}
