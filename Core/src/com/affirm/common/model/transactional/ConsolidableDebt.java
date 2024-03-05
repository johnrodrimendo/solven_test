package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.ConsolidationAccountType;
import com.affirm.common.model.catalog.CreditCardBrand;
import com.affirm.common.model.catalog.Department;
import com.affirm.common.model.catalog.RccEntity;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

/**
 * Created by john on 13/01/17.
 */
public class ConsolidableDebt {

    private Integer loanApplicationId;
    private Integer consolidationAccounttype;
    private RccEntity entity;
    private Double balanceDouble;
    private int balance;
    private int balanceDE;
    private int balanceLP;
    private Double rate;
    private Double rateDE;
    private Double rateLP;
    private Integer installments;
    private boolean selected;
    private String accountCardNumber;
    private Integer brandId;
    private String departmentUbigeo;
    private CreditCardBrand creditCardBrand;
    private Department department;

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setConsolidationAccounttype(JsonUtil.getIntFromJson(json, "consolidation_account_id", null));
        if (JsonUtil.getStringFromJson(json, "entity_code", null) != null)
            setEntity(catalog.getRccEntity(JsonUtil.getStringFromJson(json, "entity_code", null)));
        setBalance(JsonUtil.getDoubleFromJson(json, "balance", 0.0).intValue());
        setBalanceDouble(JsonUtil.getDoubleFromJson(json, "balance", null));
        setInstallments(JsonUtil.getIntFromJson(json, "installments", null));
        setRate(JsonUtil.getDoubleFromJson(json, "rate", null));
        setSelected(JsonUtil.getBooleanFromJson(json, "is_selected", true));
        setAccountCardNumber(JsonUtil.getStringFromJson(json, "account_card_number", null));
        setBrandId(JsonUtil.getIntFromJson(json, "brand_id", null));
        setDepartmentUbigeo(JsonUtil.getStringFromJson(json, "ubigeo_department", null));
        if(brandId != null) setCreditCardBrand(catalog.getBrandById(brandId));
        if(departmentUbigeo != null) setDepartment(catalog.getDepartmentById(departmentUbigeo));
    }

    public ConsolidableDebt() {
    }

    public Integer getTotalBalance() {
        return balance + balanceDE + balanceLP;
    }

    public double getTotalRate() {
        double totalRate = (balance * 1.0 / getTotalBalance() * 1.0) * (rate / 100);
        if (balanceDE > 0) {
            totalRate = totalRate + (balanceDE * 1.0 / getTotalBalance() * 1.0) * (rateDE / 100);
        }
        if (balanceLP > 0) {
            totalRate = totalRate + (balanceLP * 1.0 / getTotalBalance() * 1.0) * (rateLP / 100);
        }
        return totalRate;
    }

    public String getAccountCardNumberEncrypted() {
        if (accountCardNumber == null)
            return null;
        return new StringBuffer(accountCardNumber).replace(0, accountCardNumber.length() - 6, StringUtils.repeat("*", accountCardNumber.length() - 6)).toString();
    }

    public ConsolidableDebt(RccEntity entity) {
        this.entity = entity;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public Integer getConsolidationAccounttype() {
        return consolidationAccounttype;
    }

    public void setConsolidationAccounttype(Integer consolidationAccounttype) {
        this.consolidationAccounttype = consolidationAccounttype;
    }

    public RccEntity getEntity() {
        return entity;
    }

    public void setEntity(RccEntity entity) {
        this.entity = entity;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getBalanceDE() {
        return balanceDE;
    }

    public void setBalanceDE(int balanceDE) {
        this.balanceDE = balanceDE;
    }

    public int getBalanceLP() {
        return balanceLP;
    }

    public void setBalanceLP(int balanceLP) {
        this.balanceLP = balanceLP;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Double getRateDE() {
        return rateDE;
    }

    public void setRateDE(Double rateDE) {
        this.rateDE = rateDE;
    }

    public Double getRateLP() {
        return rateLP;
    }

    public void setRateLP(Double rateLP) {
        this.rateLP = rateLP;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getAccountCardNumber() {
        return accountCardNumber;
    }

    public void setAccountCardNumber(String accountCardNumber) {
        this.accountCardNumber = accountCardNumber;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getDepartmentUbigeo() {
        return departmentUbigeo;
    }

    public void setDepartmentUbigeo(String departmentUbigeo) {
        this.departmentUbigeo = departmentUbigeo;
    }

    public Double getBalanceDouble() {
        return balanceDouble;
    }

    public void setBalanceDouble(Double balanceDouble) {
        this.balanceDouble = balanceDouble;
    }

    public CreditCardBrand getCreditCardBrand() {
        return creditCardBrand;
    }

    public void setCreditCardBrand(CreditCardBrand creditCardBrand) {
        this.creditCardBrand = creditCardBrand;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getShortNameAccountType(int accountTypeId){
        String shortName = "";
        switch (accountTypeId){
            case ConsolidationAccountType.TARJETA_CREDITO:
                shortName = "Tarjeta de Crédito";
                break;
            case ConsolidationAccountType.DISPONIBILIDAD_EFECTIVO:
                shortName = "Disposición de Efectivo";
                break;
            case ConsolidationAccountType.LINEA_PARALELA:
                shortName = "Línea Paralela";
                break;
            case ConsolidationAccountType.CREDITO_CONSUMO:
                shortName = "Crédito de Consumo";
                break;
            case ConsolidationAccountType.CREDITO_NEGOCIO:
                shortName = "Crédito para mi Negocio";
                break;
        }
        return shortName;
    }
}
