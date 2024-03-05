package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Employer;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * Created by john on 04/01/17.
 */
public class Employee implements Serializable {

    private Integer id;
    private Employer employer;
    private IdentityDocumentType docType;
    private String docNumber;
    private String name;
    private String firstSurname;
    private String lastSurname;
    private Date employmentStartDateDate;
    private String employmentStartDate; // TODO Delete this!!
    private Date registerDate;
    private Character contractType;
    private Date contractEndDate;
    private String workEmail;
    private String phoneNumber;
    private String address;
    private Double fixedGrossIncome;
    private Double variableGrossIncome;
    private Double monthlyDeduction;
    private String bank;
    private String accountNumber;
    private String accountNumberCci;
    private Boolean salaryGarnishment;
    private Boolean unpaidLeave;
    private Integer personId;
    private Integer userId;
    private Boolean active;
    private Integer customMaxAmount;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "employee_id", null));
        if (JsonUtil.getIntFromJson(json, "employer_id", null) != null)
            setEmployer(catalog.getEmployer(JsonUtil.getIntFromJson(json, "employer_id", null)));
        if (JsonUtil.getIntFromJson(json, "document_type_id", null) != null)
            setDocType(catalog.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_type_id", null)));
        setDocNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setName(JsonUtil.getStringFromJson(json, "person_name", null));
        setFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        setEmploymentStartDateDate(JsonUtil.getPostgresDateFromJson(json, "employment_start_date", null));
        setEmploymentStartDate(JsonUtil.getStringFromJson(json, "employment_start_date", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setContractType(JsonUtil.getCharacterFromJson(json, "contract_type", null));
        setContractEndDate(JsonUtil.getPostgresDateFromJson(json, "contract_end_date", null));
        setWorkEmail(JsonUtil.getStringFromJson(json, "work_email", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        setAddress(JsonUtil.getStringFromJson(json, "address", null));
        setFixedGrossIncome(JsonUtil.getDoubleFromJson(json, "fixed_gross_income", null));
        setVariableGrossIncome(JsonUtil.getDoubleFromJson(json, "variable_gross_income", null));
        setMonthlyDeduction(JsonUtil.getDoubleFromJson(json, "monthly_deduction", null));
        setBank(JsonUtil.getStringFromJson(json, "bank", null));
        setAccountNumber(JsonUtil.getStringFromJson(json, "account_number", null));
        setAccountNumberCci(JsonUtil.getStringFromJson(json, "account_number_cci", null));
        setSalaryGarnishment(JsonUtil.getBooleanFromJson(json, "salary_garnishment", null));
        setUnpaidLeave(JsonUtil.getBooleanFromJson(json, "unpaid_leave", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setUserId(JsonUtil.getIntFromJson(json, "user_id", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setCustomMaxAmount(JsonUtil.getIntFromJson(json, "custom_max_amount", null));
    }

    public String getFullName() {
        String fullname = "";
        if (name != null) {
            fullname = fullname + name + " ";
        }
        if (firstSurname != null) {
            fullname = fullname + firstSurname + " ";
        }
        if (lastSurname != null) {
            fullname = fullname + lastSurname + " ";
        }
        return fullname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
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

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Character getContractType() {
        return contractType;
    }

    public void setContractType(Character contractType) {
        this.contractType = contractType;
    }

    public Date getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(Date contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getFixedGrossIncome() {
        return fixedGrossIncome;
    }

    public void setFixedGrossIncome(Double fixedGrossIncome) {
        this.fixedGrossIncome = fixedGrossIncome;
    }

    public Double getMonthlyDeduction() {
        return monthlyDeduction;
    }

    public void setMonthlyDeduction(Double monthlyDeduction) {
        this.monthlyDeduction = monthlyDeduction;
    }

    public Double getVariableGrossIncome() {
        return variableGrossIncome;
    }

    public void setVariableGrossIncome(Double variableGrossIncome) {
        this.variableGrossIncome = variableGrossIncome;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountNumberCci() {
        return accountNumberCci;
    }

    public void setAccountNumberCci(String accountNumberCci) {
        this.accountNumberCci = accountNumberCci;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getSalaryGarnishment() {
        return salaryGarnishment;
    }

    public void setSalaryGarnishment(Boolean salaryGarnishment) {
        this.salaryGarnishment = salaryGarnishment;
    }

    public Boolean getUnpaidLeave() {
        return unpaidLeave;
    }

    public void setUnpaidLeave(Boolean unpaidLeave) {
        this.unpaidLeave = unpaidLeave;
    }

    public String getEmploymentStartDate() {
        return employmentStartDate;
    }

    public void setEmploymentStartDate(String employmentStartDate) {
        this.employmentStartDate = employmentStartDate;
    }

    public Date getEmploymentStartDateDate() {
        return employmentStartDateDate;
    }

    public void setEmploymentStartDateDate(Date employmentStartDateDate) {
        this.employmentStartDateDate = employmentStartDateDate;
    }

    public Integer getCustomMaxAmount() {
        return customMaxAmount;
    }

    public void setCustomMaxAmount(Integer customMaxAmount) {
        this.customMaxAmount = customMaxAmount;
    }


}
