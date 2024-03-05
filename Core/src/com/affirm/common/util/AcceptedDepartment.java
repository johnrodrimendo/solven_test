package com.affirm.common.util;

import java.util.List;

public class AcceptedDepartment {

    private String departmentId;
    private List<AcceptedProvince> provinces;

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public List<AcceptedProvince> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<AcceptedProvince> provinces) {
        this.provinces = provinces;
    }
}
