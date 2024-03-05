package com.affirm.common.util;

import java.util.List;

public class AcceptedProvince {

    private String provinceId;
    private List<AcceptedDistrict> districts;

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public List<AcceptedDistrict> getDistricts() {
        return districts;
    }

    public void setDistricts(List<AcceptedDistrict> districts) {
        this.districts = districts;
    }
}
