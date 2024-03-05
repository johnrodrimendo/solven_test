/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.client.service.ExtranetCompanyService;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jrodriguez
 */
public class RegisterEmployeeForm extends FormGeneric implements Serializable {

    private Integer docType;
    private String docNumber;
    private String name;
    private String firstSurname;
    private String lastSurname;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date employmentStartDate;
    private Character contractType;
    private String contractEndDate;
    private String email;
    private String phoneNumber;
    private String address;
    private Integer fixedGrossIncome;
    private Integer variableGrossIncome;
    private Integer monthlyDeduction;
    private String bank;
    private String accountNumber;
    private String accountNumberCci;
    private Integer employeeId;
    private Boolean salaryGarnishment;
    private Boolean unpaidLeave;
    private String employerRuc;
    private Boolean validationAgreementProduct;
    private Integer customMaxAmount;

    @Autowired
    private ExtranetCompanyService extranetCompanyService;

    public RegisterEmployeeForm() {
        this.setValidator(new RegisterEmployeeFormValidator());
    }

    public Boolean getValidationAgreementProduct() {
        return validationAgreementProduct;
    }

    public void setValidationAgreementProduct(Boolean validationAgreementProduct) {
        this.validationAgreementProduct = validationAgreementProduct;
    }

    public class RegisterEmployeeFormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator docType;
        public StringFieldValidator docNumber;
        public StringFieldValidator name;
        public StringFieldValidator firstSurname;
        public StringFieldValidator lastSurname;
        public DateFieldValidator employmentStartDate;
        public CharFieldValidator contractType;
        public StringFieldValidator contractEndDate;
        public StringFieldValidator email;
        public StringFieldValidator phoneNumber;
        public StringFieldValidator address;
        public IntegerFieldValidator fixedGrossIncome;
        public IntegerFieldValidator variableGrossIncome;
        public IntegerFieldValidator monthlyDeduction;
        public StringFieldValidator bank;
        public StringFieldValidator accountNumber;
        public StringFieldValidator accountNumberCci;
        public IntegerFieldValidator employeeId;
        public BooleanFieldValidator salaryGarnishment;
        public BooleanFieldValidator unpaidLeave;
        public StringFieldValidator employerRuc;
        public IntegerFieldValidator customMaxAmount;

        public RegisterEmployeeFormValidator() {
            addValidator(docType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID));
            addValidator(docNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI));
            addValidator(name = new StringFieldValidator(ValidatorUtil.NAME).setRequired(true));
            addValidator(firstSurname = new StringFieldValidator(ValidatorUtil.FIRST_SURNAME).setRequired(true));
            addValidator(lastSurname = new StringFieldValidator(ValidatorUtil.LAST_SURNAME).setRequired(false));
            addValidator(employmentStartDate = new DateFieldValidator(ValidatorUtil.EMPLOYEE_START_DATE).setRequired(true).setFutureDate(false));
            addValidator(contractType = new CharFieldValidator(ValidatorUtil.CONTRACT_TYPE).setRequired(false));
            addValidator(contractEndDate = new StringFieldValidator(ValidatorUtil.CONTRACT_END_DATE).setRequired(false));
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL).setRequired(false));
            addValidator(phoneNumber = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE).setRequired(false));
            addValidator(address = new StringFieldValidator(ValidatorUtil.FULL_ADDRESS).setValidRegex(null));
            addValidator(fixedGrossIncome = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME));
            addValidator(variableGrossIncome = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME).setRequired(false).setMinValue(0));
            addValidator(monthlyDeduction = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME).setRequired(false).setMinValue(0));
            addValidator(bank = new StringFieldValidator(ValidatorUtil.BANK_NAME).setRequired(false));
            addValidator(accountNumber = new StringFieldValidator(ValidatorUtil.BANK_ACCOUNT_NUMBER).setRequired(false));
            addValidator(accountNumberCci = new StringFieldValidator(ValidatorUtil.BANK_CCI_NUMBER).setRequired(false));
            addValidator(salaryGarnishment = new BooleanFieldValidator().setRequired(true));
            addValidator(unpaidLeave = new BooleanFieldValidator().setRequired(true));
            addValidator(employeeId = new IntegerFieldValidator().setRequired(false));
            addValidator(employerRuc = new StringFieldValidator(ValidatorUtil.RUC).setRequired(false));
            addValidator(customMaxAmount = new IntegerFieldValidator().setRequired(false).setRestricted(true).setMaxValue(12000));
        }

        @Override
        protected void setDynamicValidations() {
            if (RegisterEmployeeForm.this.docType != null)
                if (RegisterEmployeeForm.this.docType == IdentityDocumentType.DNI) {
                    docNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
                } else if (RegisterEmployeeForm.this.docType == IdentityDocumentType.CE) {
                    docNumber.update(ValidatorUtil.DOC_NUMBER_CE);
                }

            if (RegisterEmployeeForm.this.accountNumberCci != null) {
                try {
                    if (validationAgreementProduct) {
                        bank.setRequired(true);
                        accountNumber.setRequired(true);
                    } else {
                        bank.setRequired(false);
                        accountNumber.setRequired(false);
                        accountNumberCci.setRequired(true);
                    }

                } catch (Exception e) {

                }
            } else {
                bank.setRequired(true);
                accountNumber.setRequired(true);
                accountNumberCci.setRequired(false);
            }

            if (RegisterEmployeeForm.this.contractType != null && RegisterEmployeeForm.this.contractType == 'D') {
                contractEndDate.setRequired(true);
            } else {
                contractEndDate.setRequired(false);
            }


        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return RegisterEmployeeForm.this;
        }
    }

    public Integer getDocType() {
        return docType;
    }

    public void setDocType(Integer docType) {
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

    public Character getContractType() {
        return contractType;
    }

    public void setContractType(Character contractType) {
        this.contractType = contractType;
    }

    public String getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(String contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getFixedGrossIncome() {
        return fixedGrossIncome;
    }

    public void setFixedGrossIncome(Integer fixedGrossIncome) {
        this.fixedGrossIncome = fixedGrossIncome;
    }

    public Integer getVariableGrossIncome() {
        return variableGrossIncome;
    }

    public Integer getMonthlyDeduction() {
        return monthlyDeduction;
    }

    public void setMonthlyDeduction(Integer monthlyDeduction) {
        this.monthlyDeduction = monthlyDeduction;
    }

    public void setVariableGrossIncome(Integer variableGrossIncome) {
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

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
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

    public Date getEmploymentStartDate() {
        return employmentStartDate;
    }

    public void setEmploymentStartDate(Date employmentStartDate) {
        this.employmentStartDate = employmentStartDate;
    }

    public String getEmployerRuc() {
        return employerRuc;
    }

    public void setEmployerRuc(String employerRuc) {
        this.employerRuc = employerRuc;
    }

    public Integer getCustomMaxAmount() {
        return customMaxAmount;
    }

    public void setCustomMaxAmount(Integer customMaxAmount) {
        this.customMaxAmount = customMaxAmount;
    }
}
