package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 22/05/17.
 */
public class ProductCategory implements Serializable, ICatalog, ICatalogIntegerId {

    public static final String CONSUMO_CATEGORY_URL = "credito-de-consumo";
    public static final String ADELANTO_SUELDO_CATEGORY_URL = "adelanto-de-sueldo";
    public static final String VEHICULO_CATEGORY_URL = "credito-vehicular";
    public static final String LEADS_CATEGORY_URL = "leads";
    public static final String TARJETA_CREDITO_CATEGORY_URL = "tarjeta-de-credito";
    public static final String GATEWAY_URL = "gateway";
    public static final String CUENTA_BANCARIA_URL = "cuenta";
    public static final String CONSEJ0_URL = "consejero";
    public static final String VALIDACION_IDENTIDAD_URL = "identidad";

    public static final int CONSUMO = 1;
    public static final int ADELANTO = 2;
    public static final int VEHICULO = 3;
    public static final int CONSOLIDAR_CREDITOS = 4;
    public static final int LEADS = 5;
    public static final int TARJETA_CREDITO = 6;
    public static final int GATEWAY = 7;
    public static final int CUENTA_BANCARIA = 8;
    public static final int CONSEJ0 = 10;
    public static final int VALIDACION_IDENTIDAD = 9;

    public static final int GET_ID_BY_URL(String categoryUrl) throws Exception {
        switch (categoryUrl) {
            case CONSUMO_CATEGORY_URL:
                return CONSUMO;
            case ADELANTO_SUELDO_CATEGORY_URL:
                return ADELANTO;
            case VEHICULO_CATEGORY_URL:
                return VEHICULO;
            case LEADS_CATEGORY_URL:
                return LEADS;
            case TARJETA_CREDITO_CATEGORY_URL:
                return TARJETA_CREDITO;
            case GATEWAY_URL:
                return GATEWAY;
            case CUENTA_BANCARIA_URL:
                return CUENTA_BANCARIA;
            case CONSEJ0_URL:
                return CONSEJ0;
            case VALIDACION_IDENTIDAD_URL:
                return VALIDACION_IDENTIDAD;
        }
        throw new Exception("There is no id for the url");
    }

    public static final String GET_URL_BY_ID(int id) throws Exception {
        switch (id) {
            case CONSUMO:
                return CONSUMO_CATEGORY_URL;
            case ADELANTO:
                return ADELANTO_SUELDO_CATEGORY_URL;
            case VEHICULO:
                return VEHICULO_CATEGORY_URL;
            case LEADS:
                return LEADS_CATEGORY_URL;
            case TARJETA_CREDITO:
                return TARJETA_CREDITO_CATEGORY_URL;
            case GATEWAY:
                return GATEWAY_URL;
            case CUENTA_BANCARIA:
                return CUENTA_BANCARIA_URL;
            case CONSEJ0:
                return CONSEJ0_URL;
            case VALIDACION_IDENTIDAD:
                return VALIDACION_IDENTIDAD_URL;
        }
        throw new Exception("There is no url for the id");
    }

    private Integer id;
    private String category;
    //    private String process;
    private List<ProductCategoryCountry> countriesConfig;
    private Integer countryId;
    private Boolean active = true;
    private String image;
    private String message;
    private String categoryName;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "category_id", null));
        setCategory(JsonUtil.getStringFromJson(json, "category", null));
        if (JsonUtil.getJsonArrayFromJson(json, "ar_js_evaluation_process", null) != null) {
            countriesConfig = new ArrayList<>();
            JSONArray arrayCountryConfig = JsonUtil.getJsonArrayFromJson(json, "ar_js_evaluation_process", null);
            for (int i = 0; i < arrayCountryConfig.length(); i++) {
                ProductCategoryCountry countryConfig = new ProductCategoryCountry();
                countryConfig.fillFromDb(arrayCountryConfig.getJSONObject(i));
                countriesConfig.add(countryConfig);
            }
        }
        setCountryId(JsonUtil.getIntFromJson(json, "country_id", null));
        setImage(JsonUtil.getStringFromJson(json, "image", null));
        setMessage(JsonUtil.getStringFromJson(json, "description", null));
        setCategoryName(JsonUtil.getStringFromJson(json, "text_int", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<ProductCategoryCountry> getCountriesConfig() {
        return countriesConfig;
    }

    public void setCountriesConfig(List<ProductCategoryCountry> countriesConfig) {
        this.countriesConfig = countriesConfig;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
