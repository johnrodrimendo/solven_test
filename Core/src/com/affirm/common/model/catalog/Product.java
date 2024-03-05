/**
 *
 */
package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jrodriguez
 */
public class Product implements Serializable, ICatalog, ICatalogIntegerId {
    //
    public static final int TRADITIONAL = 1;
    public static final int SHORT_TERM = 2; //  TODO Ya no debe existir, se cambio de nombre a leads redirect
    public static final int SALARY_ADVANCE = 3;
    public static final int AUTOS = 4;
    public static final int DEBT_CONSOLIDATION = 5;
    public static final int INSURANCE = 6;
    public static final int AGREEMENT = 7;
    public static final int DEBT_CONSOLIDATION_OPEN = 8;
    public static final int GUARANTEED = 9;
    public static final int PREPAY_CARD = 10;
    public static final int LEADS_CONSUMO = 2;
    public static final int LEADS = 11;
    public static final int TARJETA_CREDITO = 12;
    public static final int GATEWAY_PAGO = 14;
    public static final int SAVINGS_ACCOUNT = 15;
    public static final int VALIDACION_IDENTIDAD = 16;
    public static final int ROL_CONSEJERO = 17;

    public enum ProductPaymentType {
        BIWEEKLY('B', "quincenal", "quincenas", "quincena"),
        MONTHLY('M', "mensual", "meses", "mes"),
        WEEKLY('W', "semanal", "semanas", "semana");

        private char type;
        private String name;
        private String pluralName;
        private String singularName;

        ProductPaymentType(char type, String name, String pluralName, String singularName) {
            this.type = type;
            this.name = name;
            this.pluralName = pluralName;
            this.singularName = singularName;
        }

        public char getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getPluralName() {
            return pluralName;
        }

        public String getSingularName() {
            return singularName;
        }

        public static ProductPaymentType getByType(Character type) {
            if (type == null)
                return null;

            for (ProductPaymentType e : values()) {
                if (e.type == type) return e;
            }
            return null;
        }
    }

    private Integer id;
    private String name;
    private String shortName;
    private ProductPaymentType paymentType;
    private Boolean active;
    private List<ProductPersonCategoryAmount> personCategories;
    private Integer productCategoryId;
    private ProductCategory productCategory;
    private List<ProductMaxMinParameter> productMaxMinParameters = new ArrayList<>();
    private List<ProductCountryDomain> countryDomains = new ArrayList<>();

    public void fillFromDb(JSONObject json, CatalogService catalog) {
        setId(JsonUtil.getIntFromJson(json, "product_id", null));
        setName(JsonUtil.getStringFromJson(json, "product", null));
        setShortName(JsonUtil.getStringFromJson(json, "short_name", null));
        setPaymentType(ProductPaymentType.getByType(JsonUtil.getCharacterFromJson(json, "payment_type", null)));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setProductCategoryId(JsonUtil.getIntFromJson(json, "category_id", null));
        if (getProductCategoryId() != null) {
            setProductCategory(catalog.getCatalogById(ProductCategory.class, getProductCategoryId(), null));
        }
        if (JsonUtil.getJsonArrayFromJson(json, "js_domain", null) != null) {
            setCountryDomains(new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "js_domain", null).toString(), new TypeToken<ArrayList<ProductCountryDomain>>() {
            }.getType()));
            getCountryDomains().forEach(c -> c.setProductId(getId()));
        }
    }

    public ProductMaxMinParameter getProductParams(int countryId) {
        return getProductMaxMinParameters().stream().filter(p -> p.getCountryId() == countryId).findFirst().orElse(null);
    }

    public ProductMaxMinParameter getProductParams(int countryId, int entityId) {
        return getProductMaxMinParameters().stream().filter(p -> p.getEntityId() != null && p.getCountryId() == countryId && p.getEntityId() == entityId).findFirst().orElse(null);
    }

    public ProductCountryDomain getProductCountryDomainByDomain(String domain) {
        if (countryDomains == null)
            return null;
        return countryDomains
                .stream()
                .filter(c -> c.getDomains() != null && c.getDomains().stream().anyMatch(d ->d.equalsIgnoreCase(domain)))
                .findFirst().orElse(null);
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public boolean isTraditional() {
        return this.id == TRADITIONAL;
    }

    public boolean isShortTerm() {
        return this.id == SHORT_TERM;
    }

    public boolean isDebtConsolidation() {
        return this.id == DEBT_CONSOLIDATION;
    }

    public boolean isAdvance() {
        return this.id == SALARY_ADVANCE;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public ProductPaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(ProductPaymentType paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public Boolean getActive() {
        return active;
    }

    @Override
    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<ProductPersonCategoryAmount> getPersonCategories() {
        return personCategories;
    }

    public void setPersonCategories(List<ProductPersonCategoryAmount> personCategories) {
        this.personCategories = personCategories;
    }

    public List<ProductMaxMinParameter> getProductMaxMinParameters() {
        return productMaxMinParameters;
    }

    public void setProductMaxMinParameters(List<ProductMaxMinParameter> productMaxMinParameters) {
        this.productMaxMinParameters = productMaxMinParameters;
    }

    public Integer getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(Integer productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public List<ProductCountryDomain> getCountryDomains() {
        return countryDomains;
    }

    public void setCountryDomains(List<ProductCountryDomain> countryDomains) {
        this.countryDomains = countryDomains;
    }
}
