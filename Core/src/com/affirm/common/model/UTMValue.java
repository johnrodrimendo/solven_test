package com.affirm.common.model;

import java.util.ArrayList;
import java.util.List;

public class UTMValue {

    public List<UTMValueDetail> sources = new ArrayList<>();
    public List<UTMValueDetail> campaigns = new ArrayList<>();
    public List<UTMValueDetail> mediums= new ArrayList<>();
    public List<UTMValueDetail> contents= new ArrayList<>();

    public static class UTMValueDetail{
        public String value;
        public String text;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public List<UTMValueDetail> getSources() {
        return sources;
    }

    public void setSources(List<UTMValueDetail> sources) {
        this.sources = sources;
    }

    public List<UTMValueDetail> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(List<UTMValueDetail> campaigns) {
        this.campaigns = campaigns;
    }

    public List<UTMValueDetail> getMediums() {
        return mediums;
    }

    public void setMediums(List<UTMValueDetail> mediums) {
        this.mediums = mediums;
    }

    public List<UTMValueDetail> getContents() {
        return contents;
    }

    public void setContents(List<UTMValueDetail> contents) {
        this.contents = contents;
    }
}
