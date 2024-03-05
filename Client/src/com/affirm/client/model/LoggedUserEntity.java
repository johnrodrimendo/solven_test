package com.affirm.client.model;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.transactional.EntityExtranetUser;
import com.affirm.common.model.transactional.UserEntity;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.affirm.security.model.ITwoFactorAuthLoggedUser;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LoggedUserEntity extends UserEntity implements ITwoFactorAuthLoggedUser, Serializable {

    private Integer sessionId;
    private Date lastSigninDate;
    private Boolean firstSignIn;
    private String resultMessage;
    private Boolean tfaLogin;
    private String tfaSharedSecret;
    private String tfaScratchCodes;
    private boolean tfaLogged = false;
    private List<EntityExtranetUser.MenuEntityProductCategory> menuEntityProductCategories;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) {
        setSessionId(JsonUtil.getIntFromJson(json, "entity_session_id", null));
        setLastSigninDate(JsonUtil.getPostgresDateFromJson(json, "last_sign_in_date", null));
        setFirstSignIn(JsonUtil.getBooleanFromJson(json, "must_change_password", null));
        setResultMessage(JsonUtil.getStringFromJson(json, "result_message", null));
        setTfaLogin(JsonUtil.getBooleanFromJson(json, "tfa_login", null));
        setTfaSharedSecret(JsonUtil.getStringFromJson(json, "tfa_shared_secret", null));
        setTfaScratchCodes(JsonUtil.getStringFromJson(json, "tfa_scratch_codes", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_menu_entity_product_category", null) != null) {
            menuEntityProductCategories = new ArrayList<>();
            JSONArray array = JsonUtil.getJsonArrayFromJson(json, "js_menu_entity_product_category", null);
            for (int i = 0; i < array.length(); i++) {
                EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = new Gson().fromJson(array.getJSONObject(i).toString(), EntityExtranetUser.MenuEntityProductCategory.class);
                if(menuEntityProductCategory != null && menuEntityProductCategory.getMenuEntityId() != null) menuEntityProductCategory.setExtranetMenu(catalog.getExtranetMenu(menuEntityProductCategory.getMenuEntityId()));
                menuEntityProductCategories.add(menuEntityProductCategory);
            }
        }
        super.fillFromDb(json, catalog, locale);
    }

    public Entity getPrincipalEntity() {
        return getEntities() != null && !getEntities().isEmpty() ? getEntities().get(0) : null;
    }

    public Integer getProductCategoryId() {
        return getProducts() != null && !getProducts().isEmpty() ? getProducts().get(0).getProductCategoryId() : null;
    }

    @Override
    public boolean need2FA() {
        return tfaLogin == null ? false : tfaLogin;
    }

    @Override
    public boolean is2FALogged() {
        return tfaLogged;
    }

    @Override
    public String get2FASharedSecret() {
        return tfaSharedSecret;
    }

    @Override
    public void set2FALogged() {
        tfaLogged = true;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Date getLastSigninDate() {
        return lastSigninDate;
    }

    public void setLastSigninDate(Date lastSigninDate) {
        this.lastSigninDate = lastSigninDate;
    }

    public Boolean getFirstSignIn() {
        return firstSignIn;
    }

    public void setFirstSignIn(Boolean firstSignIn) {
        this.firstSignIn = firstSignIn;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public Boolean getTfaLogin() {
        return tfaLogin;
    }

    public void setTfaLogin(Boolean tfaLogin) {
        this.tfaLogin = tfaLogin;
    }

    public String getTfaSharedSecret() {
        return tfaSharedSecret;
    }

    public void setTfaSharedSecret(String tfaSharedSecret) {
        this.tfaSharedSecret = tfaSharedSecret;
    }

    public String getTfaScratchCodes() {
        return tfaScratchCodes;
    }

    public void setTfaScratchCodes(String tfaScratchCodes) {
        this.tfaScratchCodes = tfaScratchCodes;
    }

    public List<EntityExtranetUser.MenuEntityProductCategory> getMenuEntityProductCategories() {
        return menuEntityProductCategories;
    }
}