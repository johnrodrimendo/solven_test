package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class Proxy {
    private Integer id;
    private String port;
    private String ip;
    private String location;
    private Integer countryId;
    private String proxyType;
    private boolean bussy;

    public void fillFromDB(JSONObject json){
        setId(JsonUtil.getIntFromJson(json, "proxy_id", null));
        setPort(JsonUtil.getStringFromJson(json, "proxy_port", null));
        setIp(JsonUtil.getStringFromJson(json, "proxy_ip", null));
        setLocation(JsonUtil.getStringFromJson(json, "proxy_location", null));
        setCountryId(JsonUtil.getIntFromJson(json, "country_id", null));
        setProxyType(JsonUtil.getStringFromJson(json, "proxyType", null));
        setBussy(false);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getProxyType() {
        return proxyType;
    }

    public void setProxyType(String proxyType) {
        this.proxyType = proxyType;
    }

    public boolean isBussy() {
        return bussy;
    }

    public void setBussy(boolean bussy) {
        this.bussy = bussy;
    }
}
