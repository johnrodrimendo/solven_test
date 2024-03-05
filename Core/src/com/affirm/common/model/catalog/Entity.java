package com.affirm.common.model.catalog;

import com.affirm.common.model.SenderMailConfiguration;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jrodriguez on 12/07/16.
 */
public class Entity implements Serializable {

    public static final int AFFIRM = 5;
    public static final int ABACO = 7;
    public static final int RIPLEY = 8;
    public static final int ACCESO = 9;
    public static final int EFL = 10;
    public static final int BF = 11;
    public static final int EFECTIVA = 12;
    public static final int COMPARTAMOS = 13;
    public static final int CAJASULLANA = 14;
    public static final int CAJA_LOS_ANDES = 15;
    public static final int MULTIFINANZAS = 5402;
    public static final int WENANCE = 5401;
    public static final int AELU = 17;
    public static final int QAPAQ = 16;
    public static final int TARJETAS_PERUANAS = 18;
    public static final int BANCO_DEL_SOL = 5403;
    public static final int AUTOPLAN = 19;
    public static final int CREDIGOB = 21;
    public static final int BANBIF = 20;
    public static final int INVERSIONES_LA_CRUZ = 22;
    public static final int FINANSOL = 23;
    public static final int FUNDACION_DE_LA_MUJER = 5702;
    public static final int PRESTAMYPE = 24;
    public static final int PRISMA = 25;
    public static final int AZTECA = 26;
    public static final int WARMI = 27;

    public static final String[] AZTECA_NOTIFICATION_LIST = new String[]{"dev@solven.pe", "Ernesto.Cok@alfinbanco.pe", "Carlos.LaTorre@alfinbanco.pe", "Shelly.Bejarano@alfinbanco.pe"};

    public enum JsParamsKeys {
        ACCESO_BANNER_COLLECTION("banner_collection"),
        ACCESO_PHONENUMBER("phone_number")
        ;

        private String key;

        JsParamsKeys(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    private Integer id;
    private Integer type;
    private String code;
    private String fullName;
    private String shortName;
    private Boolean active;
    private String logoUrl;
    private String applicationFormUrl;
    private Double igv;
    private Boolean annualizaXirr;
    private Boolean customEffectiveAnnualCostRate;
    private List<EntityProduct> products;
    private Integer countryId;
    private String email;
    private List<Bureau> bureaus;
    private List<IdentityDocumentType> documentTypes;
    private boolean tfaLogin;
    private List<Integer> extranetRoles;
    private String rccEntityCode;
    private Double loanNonCapitalizableCommission;
    private String grayLogoUrl;
    private Map<String, String> jsParams;

    public void fillFromDb(JSONObject json, CatalogService catalog) {
        setId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setType(JsonUtil.getIntFromJson(json, "type_id", null));
        setCode(JsonUtil.getStringFromJson(json, "entity_code", null));
        setFullName(JsonUtil.getStringFromJson(json, "full_name", null));
        setShortName(JsonUtil.getStringFromJson(json, "short_name", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setLogoUrl(JsonUtil.getStringFromJson(json, "logo_url", null));
        setApplicationFormUrl(JsonUtil.getStringFromJson(json, "application_form_url", null));
        setProducts(catalog.getEntityProductsByEntity(getId()));
        setIgv(JsonUtil.getDoubleFromJson(json, "igv", null));
        setAnnualizaXirr(JsonUtil.getBooleanFromJson(json, "annualize_xirr", null));
        setCustomEffectiveAnnualCostRate(JsonUtil.getBooleanFromJson(json, "custom_effective_annual_cost_rate", null));
        setCountryId(JsonUtil.getIntFromJson(json, "country_id", null));
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        if (JsonUtil.getJsonArrayFromJson(json, "bureau_id", null) != null){
            setBureaus(new ArrayList<>());
            JSONArray array = JsonUtil.getJsonArrayFromJson(json, "bureau_id", null);
            for(int i=0; i<array.length(); i++){
                getBureaus().add(catalog.getBureau(array.getInt(i)));
            }
        }
        if (JsonUtil.getJsonArrayFromJson(json, "ar_document_type_id", null) != null){
            setDocumentTypes(new ArrayList<>());
            JSONArray array = JsonUtil.getJsonArrayFromJson(json, "ar_document_type_id", null);
            for(int i=0; i<array.length(); i++){
                getDocumentTypes().add(catalog.getIdentityDocumentType(array.getInt(i)));
            }
        }
        setTfaLogin(JsonUtil.getBooleanFromJson(json, "tfa_login", null));
        if (JsonUtil.getJsonArrayFromJson(json, "extranet_roles_id", null) != null){
            setExtranetRoles(new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "extranet_roles_id", null).toString(), new TypeToken<ArrayList<Integer>>() {
            }.getType()));
        }
        setRccEntityCode(JsonUtil.getStringFromJson(json, "rcc_entity_code", null));
        setLoanNonCapitalizableCommission(JsonUtil.getDoubleFromJson(json, "loan_non_capitalizable_commission", null));
        setGrayLogoUrl(JsonUtil.getStringFromJson(json, "gray_logo_url", null));
        if (JsonUtil.getJsonObjectFromJson(json, "js_params", null) != null) {
            setJsParams(new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "js_params", new JSONObject()).toString() , HashMap.class));
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getApplicationFormUrl() {
        return applicationFormUrl;
    }

    public void setApplicationFormUrl(String applicationFormUrl) {
        this.applicationFormUrl = applicationFormUrl;
    }

    public List<EntityProduct> getProducts() {
        return products;
    }

    public void setProducts(List<EntityProduct> products) {
        this.products = products;
    }

    public Double getIgv() {
        return igv;
    }

    public void setIgv(Double igv) {
        this.igv = igv;
    }

    public Boolean getAnnualizaXirr() {
        return annualizaXirr;
    }

    public void setAnnualizaXirr(Boolean annualizaXirr) {
        this.annualizaXirr = annualizaXirr;
    }

    public Boolean getCustomEffectiveAnnualCostRate() {
        return customEffectiveAnnualCostRate;
    }

    public void setCustomEffectiveAnnualCostRate(Boolean customEffectiveAnnualCostRate) {
        this.customEffectiveAnnualCostRate = customEffectiveAnnualCostRate;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Bureau> getBureaus() {
        return bureaus;
    }

    public void setBureaus(List<Bureau> bureaus) {
        this.bureaus = bureaus;
    }

    public List<IdentityDocumentType> getDocumentTypes() {
        return documentTypes;
    }

    public void setDocumentTypes(List<IdentityDocumentType> documentTypes) {
        this.documentTypes = documentTypes;
    }

    public boolean isTfaLogin() {
        return tfaLogin;
    }

    public void setTfaLogin(boolean tfaLogin) {
        this.tfaLogin = tfaLogin;
    }

    public List<Integer> getExtranetRoles() {
        return extranetRoles;
    }

    public void setExtranetRoles(List<Integer> extranetRoles) {
        this.extranetRoles = extranetRoles;
    }

    public String getRccEntityCode() {
        return rccEntityCode;
    }

    public void setRccEntityCode(String rccEntityCode) {
        this.rccEntityCode = rccEntityCode;
    }

    public Double getLoanNonCapitalizableCommission() {
        return loanNonCapitalizableCommission;
    }

    public void setLoanNonCapitalizableCommission(Double loanNonCapitalizableCommission) {
        this.loanNonCapitalizableCommission = loanNonCapitalizableCommission;
    }

    public String getGrayLogoUrl() {
        return grayLogoUrl;
    }

    public void setGrayLogoUrl(String grayLogoUrl) {
        this.grayLogoUrl = grayLogoUrl;
    }

    public Map<String, String> getJsParams() {
        return jsParams;
    }

    public void setJsParams(Map<String, String> jsParams) {
        this.jsParams = jsParams;
    }

    public static class ClickToCallConfiguration{
        private Integer productCategoryId;
        private List<Integer> idCampaigns;

        public Integer getProductCategoryId() {
            return productCategoryId;
        }

        public void setProductCategoryId(Integer productCategoryId) {
            this.productCategoryId = productCategoryId;
        }

        public List<Integer> getIdCampaigns() {
            return idCampaigns;
        }

        public void setIdCampaigns(List<Integer> idCampaigns) {
            this.idCampaigns = idCampaigns;
        }
    }

    public List<ClickToCallConfiguration> getClickToCallConfiguration(){
        List<ClickToCallConfiguration> list = new ArrayList<>();
        if(jsParams == null || !jsParams.containsKey("clickToCallConfiguration")) return list;
        JSONArray jsonData = new JSONArray(jsParams.get("clickToCallConfiguration"));
        if(jsonData != null && jsonData.length() > 0){
            for(int i=0; i<jsonData.length(); i++){
                list.add(new Gson().fromJson(jsonData.get(i).toString(), ClickToCallConfiguration.class));
            }
        }
        return list;
    }

    public List<SenderMailConfiguration> getSenderMailConfiguration(){
        List<SenderMailConfiguration> list = new ArrayList<>();
        if(jsParams == null || !jsParams.containsKey("senderMailConfiguration")) return list;
        JSONArray jsonData = new JSONArray(jsParams.get("senderMailConfiguration"));
        if(jsonData != null && jsonData.length() > 0){
            for(int i=0; i<jsonData.length(); i++){
                list.add(new Gson().fromJson(jsonData.get(i).toString(), SenderMailConfiguration.class));
            }
        }
        return list;
    }




}
