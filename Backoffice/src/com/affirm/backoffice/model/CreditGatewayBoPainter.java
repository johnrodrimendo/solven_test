package com.affirm.backoffice.model;

import com.affirm.backoffice.util.IPaginationWrapperElement;
import com.affirm.common.model.catalog.Cluster;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.transactional.PersonContactInformation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;
import org.springframework.context.MessageSource;

import java.util.Date;
import java.util.Locale;

/**
 * Created by sTbn on 15/08/16.
 */
public class CreditGatewayBoPainter implements IPaginationWrapperElement {

    private Integer id;
    private String code;
    private String personName;
    private String personFirstSurname;
    private String personLastSurname;
    private Product product;
    private Cluster cluster;
    private Entity entity;
    private Integer minInstallmentId;
    private Integer maxInstallmentId;
    private Integer countInstallments;
    private Date dueDate;
    private Double pendingInstallmentAmount;
    private Double collection;
    private Double ammount;
    private Integer installments;
    private Double effectiveAnnualCostRate;
    private Double effectiveAnualRate;
    private Integer daysInArrears;
    private PersonContactInformation personContactInformation;
    private Integer personId;
    private Integer loanApplicationId;
    private Integer userId;
    private Boolean isExpiring;
    private CountryParam country;

    public void fillFromDb(JSONObject json, CatalogService catalog, MessageSource messageSource,Locale locale) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "credit_id", null));
        setCode(JsonUtil.getStringFromJson(json, "credit_code", null));
        setPersonName(JsonUtil.getStringFromJson(json, "person_name", null));
        setPersonFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setPersonLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        if (JsonUtil.getIntFromJson(json, "product_id", null) != null) {
            setProduct(catalog.getProduct(JsonUtil.getIntFromJson(json, "product_id", null)));
        }
        if (JsonUtil.getIntFromJson(json, "cluster_id", null) != null) {
            setCluster(catalog.getCluster(JsonUtil.getIntFromJson(json, "cluster_id", null), locale));
        }
        if (JsonUtil.getIntFromJson(json, "entity_id", null) != null) {
            setEntity(catalog.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        }
        setMinInstallmentId(JsonUtil.getIntFromJson(json, "min_installment_id", null));
        setMaxInstallmentId(JsonUtil.getIntFromJson(json, "max_installment_id", null));
        setDueDate(JsonUtil.getPostgresDateFromJson(json, "due_date", null));
        setPendingInstallmentAmount(JsonUtil.getDoubleFromJson(json, "pending_installment_amount", null));
        setCollection(JsonUtil.getDoubleFromJson(json, "collection", null));

        PersonContactInformation personContactInformation = new PersonContactInformation();
        personContactInformation.fillFromDb(json.getJSONObject("contact_information"), catalog, locale);
        setPersonContactInformation(personContactInformation);

        setAmmount(JsonUtil.getDoubleFromJson(json, "amount", null));
        setInstallments(JsonUtil.getIntFromJson(json, "installments", null));
        setEffectiveAnualRate(JsonUtil.getDoubleFromJson(json, "effective_annual_rate", null));
        setEffectiveAnnualCostRate(JsonUtil.getDoubleFromJson(json, "effective_annual_cost_rate", null));
        setDaysInArrears(JsonUtil.getIntFromJson(json, "days_in_arrears", null));
        setCountInstallments(JsonUtil.getIntFromJson(json, "count_installments", null));
        setExpiring(JsonUtil.getBooleanFromJson(json,"is_expiring", null));
        personId = JsonUtil.getIntFromJson(json, "person_id", null);
        loanApplicationId = JsonUtil.getIntFromJson(json, "loan_application_id", null);
        userId = JsonUtil.getIntFromJson(json, "user_id", null);
        setCountry(catalog.getCountryParam(JsonUtil.getIntFromJson(json, "country_id", null)));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public PersonContactInformation getPersonContactInformation() {
        return personContactInformation;
    }

    public void setPersonContactInformation(PersonContactInformation personContactInformation) {
        this.personContactInformation = personContactInformation;
    }

    public Integer getMinInstallmentId() {
        return minInstallmentId;
    }

    public void setMinInstallmentId(Integer minInstallmentId) {
        this.minInstallmentId = minInstallmentId;
    }

    public Integer getMaxInstallmentId() {
        return maxInstallmentId;
    }

    public void setMaxInstallmentId(Integer maxInstallmentId) {
        this.maxInstallmentId = maxInstallmentId;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Double getPendingInstallmentAmount() {
        return pendingInstallmentAmount;
    }

    public void setPendingInstallmentAmount(Double pendingInstallmentAmount) {
        this.pendingInstallmentAmount = pendingInstallmentAmount;
    }


    public Double getCollection() {
        return collection;
    }

    public void setCollection(Double collection) {
        this.collection = collection;
    }

    public Double getAmmount() {
        return ammount;
    }

    public void setAmmount(Double ammount) {
        this.ammount = ammount;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Double getEffectiveAnnualCostRate() {
        return effectiveAnnualCostRate;
    }

    public void setEffectiveAnnualCostRate(Double effectiveAnnualCostRate) {
        this.effectiveAnnualCostRate = effectiveAnnualCostRate;
    }

    public Double getEffectiveAnualRate() {
        return effectiveAnualRate;
    }

    public void setEffectiveAnualRate(Double effectiveAnualRate) {
        this.effectiveAnualRate = effectiveAnualRate;
    }

    public Integer getDaysInArrears() {
        return daysInArrears;
    }

    public void setDaysInArrears(Integer daysInArrears) {
        this.daysInArrears = daysInArrears;
    }

    public Integer getCountInstallments() {
        return countInstallments;
    }

    public void setCountInstallments(Integer countInstallments) {
        this.countInstallments = countInstallments;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
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

    public String getFullName() {
        String fullname = "";
        if (personName != null) {
            fullname = fullname + personName + " ";
        }
        if (personFirstSurname != null) {
            fullname = fullname + personFirstSurname + " ";
        }
        if (personLastSurname != null) {
            fullname = fullname + personLastSurname + " ";
        }
        return fullname;
    }

    public String getCreditDescription(UtilService utilService) {
        String creditDescription = utilService.doubleMoneyFormat(ammount);
        if (installments != null) {
            creditDescription = creditDescription + "\t@ " + installments + "m\t@ " + effectiveAnualRate + "%\t@ " + effectiveAnnualCostRate + "%";
        }
        return creditDescription;
    }

    public String getCreditDescription(UtilService utilService, String symbol, String separator) {
        String creditDescription = utilService.doubleMoneyFormat(ammount, symbol, separator);
        if (installments != null) {
            creditDescription = creditDescription + "\t@ " + installments + "m\t@ " + effectiveAnualRate + "%\t@ " + effectiveAnnualCostRate + "%";
        }
        return creditDescription;
    }

    public String getTranche() {
        //(Al día, 1 a 8, 9 a 30, 31 a 60, 61 a 90, 91 a 120, 121 a 180, Más de 180)
        if (daysInArrears <= 0) {
            return "-";
        } else if (1 <= daysInArrears && daysInArrears <= 8) {
            return "1 - 8";
        } else if (9 <= daysInArrears && daysInArrears <= 30) {
            return "9 - 30";
        } else if (31 <= daysInArrears && daysInArrears <= 60) {
            return "31 - 60";
        } else if (61 <= daysInArrears && daysInArrears <= 90) {
            return "61 - 90";
        } else if (91 <= daysInArrears && daysInArrears <= 120) {
            return "91 - 120";
        } else if (121 <= daysInArrears && daysInArrears <= 180) {
            return "121 - 180";
        } else {
            return "180+";
        }
    }

    public Boolean getExpiring() {
        return isExpiring;
    }

    public void setExpiring(Boolean expiring) {
        isExpiring = expiring;
    }

    public CountryParam getCountry() {
        return country;
    }

    public void setCountry(CountryParam country) {
        this.country = country;
    }
}
