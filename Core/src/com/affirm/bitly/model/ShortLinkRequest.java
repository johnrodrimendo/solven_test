package com.affirm.bitly.model;


public class ShortLinkRequest {
    private String group_guid;
    private String domain;
    private String long_url;

    public String getGroup_guid() {
        return group_guid;
    }

    public void setGroup_guid(String group_guid) {
        this.group_guid = group_guid;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLong_url() {
        return long_url;
    }

    public void setLong_url(String long_url) {
        this.long_url = long_url;
    }
}
