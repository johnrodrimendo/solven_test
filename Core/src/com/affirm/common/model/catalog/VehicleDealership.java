/**
 *
 */
package com.affirm.common.model.catalog;

import com.affirm.common.model.transactional.BankAccount;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public class VehicleDealership implements Serializable {

    private Integer id;
    private String name;
    private Integer bankId;
    private Bank bank;
    private Character bankAccountType;
    private String bankAccount;
    private String cciCode;
    private Boolean active;
    private String email;
    private String logoUrl;
    private List<BankAccount> bankAccountList;
    private List<String> contactInformationImageUrls;
    private String ruc;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) {
        setId(JsonUtil.getIntFromJson(json, "car_dealership_id", null));
        setName(JsonUtil.getStringFromJson(json, "car_dealership", null));
        setBankId(JsonUtil.getIntFromJson(json, "bank_id", null));
        if (JsonUtil.getIntFromJson(json, "bank_id", null) != null)
            setBank(catalog.getBank(JsonUtil.getIntFromJson(json, "bank_id", null)));
        setBankAccountType(JsonUtil.getCharacterFromJson(json, "bank_account_type", null));
        setBankAccount(JsonUtil.getStringFromJson(json, "bank_account_number", null));
        setCciCode(JsonUtil.getStringFromJson(json, "cci_code", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        setLogoUrl(JsonUtil.getStringFromJson(json, "image", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_bank_account", null) != null) {
            List<BankAccount> bankAccounts = new ArrayList<>();
            JSONArray accountsArray = JsonUtil.getJsonArrayFromJson(json, "js_bank_account", null);
            for (int i = 0; i < accountsArray.length(); i++) {
                BankAccount account = new BankAccount();
                account.fillFromDb(accountsArray.getJSONObject(i), catalog);
                bankAccounts.add(account);
            }
            setBankAccountList(bankAccounts);
        }
        if (JsonUtil.getJsonArrayFromJson(json, "contact_information_images", null) != null) {
            contactInformationImageUrls = new ArrayList<>();
            JSONArray urlArray = JsonUtil.getJsonArrayFromJson(json, "contact_information_images", null);
            for (int i = 0; i < urlArray.length(); i++) {
                contactInformationImageUrls.add(urlArray.getString(i));
            }
        }
        setRuc(JsonUtil.getStringFromJson(json, "ruc", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public Character getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(Character bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getCciCode() {
        return cciCode;
    }

    public void setCciCode(String cciCode) {
        this.cciCode = cciCode;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<BankAccount> getBankAccountList() {
        return bankAccountList;
    }

    public void setBankAccountList(List<BankAccount> bankAccountList) {
        this.bankAccountList = bankAccountList;
    }

    public List<String> getContactInformationImageUrls() {
        return contactInformationImageUrls;
    }

    public void setContactInformationImageUrls(List<String> contactInformationImageUrls) {
        this.contactInformationImageUrls = contactInformationImageUrls;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }
}
