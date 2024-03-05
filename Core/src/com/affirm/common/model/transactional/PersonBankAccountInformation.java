/**
 *
 */
package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Bank;
import com.affirm.common.model.catalog.Ubigeo;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public class PersonBankAccountInformation implements Serializable {

    private Integer personId;
    private Bank bank;
    private String bankAccount;
    private String cciCode;
    private Character bankAccountType;
    private Ubigeo bankAccountUbigeo;
    private Boolean isVerified;
    private String branchOfficeCode;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        if (JsonUtil.getIntFromJson(json, "bank_id", null) != null) {
            setBank(catalog.getBank(JsonUtil.getIntFromJson(json, "bank_id", null)));
        }
        setBankAccount(JsonUtil.getStringFromJson(json, "bank_account", null));
        setCciCode(JsonUtil.getStringFromJson(json, "cci_code", null));
        setBankAccountType(JsonUtil.getCharacterFromJson(json, "bank_account_type", null));
        if (JsonUtil.getStringFromJson(json, "account_ubigeo", null) != null) {
            setBankAccountUbigeo(catalog.getUbigeo(JsonUtil.getStringFromJson(json, "account_ubigeo", null)));
        }
        setVerified(JsonUtil.getBooleanFromJson(json, "bank_account_verified", null));
        setBranchOfficeCode(JsonUtil.getStringFromJson(json, "branch_office_code", null));
    }

    public String getCciCodeEncrypted() {
        if (cciCode == null)
            return null;
        return "***************" + cciCode.substring(cciCode.length() - 4, cciCode.length());
    }

    public String getBankAccountEncrypted() {
        if (bankAccount == null)
            return null;
        return "***************" + bankAccount.substring(bankAccount.length() - 4, bankAccount.length());
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Character getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(Character bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    public Ubigeo getBankAccountUbigeo() {
        return bankAccountUbigeo;
    }

    public void setBankAccountUbigeo(Ubigeo bankAccountUbigeo) {
        this.bankAccountUbigeo = bankAccountUbigeo;
    }

    public String getCciCode() {
        return cciCode;
    }

    public void setCciCode(String cciCode) {
        this.cciCode = cciCode;
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }

    public String getAccountType(){
        String accountType="";
        if(this.getBankAccountType()=='S'){
            accountType="Ahorros";
        }
        if(this.getBankAccountType()=='C'){
            accountType="Corriente";
        }
        return accountType;
    }

    public String getBranchOfficeCode() {
        return branchOfficeCode;
    }

    public void setBranchOfficeCode(String branchOfficeCode) {
        this.branchOfficeCode = branchOfficeCode;
    }
}

