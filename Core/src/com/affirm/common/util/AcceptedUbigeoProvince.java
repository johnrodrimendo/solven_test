package com.affirm.common.util;

import java.util.List;

public class AcceptedUbigeoProvince {

    private String provinceId;
    private List<AcceptedUbigeoDistrict> districts;

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public List<AcceptedUbigeoDistrict> getDistricts() {
        return districts;
    }

    public void setDistricts(List<AcceptedUbigeoDistrict> districts) {
        this.districts = districts;
    }
}
