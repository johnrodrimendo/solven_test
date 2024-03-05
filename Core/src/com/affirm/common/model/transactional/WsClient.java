package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by jrodriguez on 08/06/16.
 */
public class WsClient {

    public enum WsClientType {
        PAYMENT_ENTITY(1, "Entidad de pago");

        private int id;
        private String name;

        WsClientType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public static WsClientType getById(Integer id) {
            if (id == null)
                return null;

            for (WsClientType w : values()) {
                if (w.id == id) return w;
            }
            return null;
        }
    }

    private Integer id;
    private String name;
    private WsClientType type;
    private Boolean active;
    private Integer apiKeyId;
    private String apiKeySecret;
    private String apiKeySharedKey;
    private Boolean apiKeyActive;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "ws_client_id", null));
        setName(JsonUtil.getStringFromJson(json, "ws_client", null));
        setType(WsClientType.getById(JsonUtil.getIntFromJson(json, "v", null)));
        setActive(JsonUtil.getBooleanFromJson(json, "client_status", null));
        setApiKeyId(JsonUtil.getIntFromJson(json, "apikey_id", null));
        setApiKeySecret(JsonUtil.getStringFromJson(json, "secret", null));
        setApiKeySharedKey(JsonUtil.getStringFromJson(json, "shared_key", null));
        setApiKeyActive(JsonUtil.getBooleanFromJson(json, "apikey_status", null));
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

    public WsClientType getType() {
        return type;
    }

    public void setType(WsClientType type) {
        this.type = type;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getApiKeyId() {
        return apiKeyId;
    }

    public void setApiKeyId(Integer apiKeyId) {
        this.apiKeyId = apiKeyId;
    }

    public String getApiKeySecret() {
        return apiKeySecret;
    }

    public void setApiKeySecret(String apiKeySecret) {
        this.apiKeySecret = apiKeySecret;
    }

    public String getApiKeySharedKey() {
        return apiKeySharedKey;
    }

    public void setApiKeySharedKey(String apiKeySharedKey) {
        this.apiKeySharedKey = apiKeySharedKey;
    }

    public Boolean getApiKeyActive() {
        return apiKeyActive;
    }

    public void setApiKeyActive(Boolean apiKeyActive) {
        this.apiKeyActive = apiKeyActive;
    }
}
