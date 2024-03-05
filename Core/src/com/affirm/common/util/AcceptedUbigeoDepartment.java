package com.affirm.common.util;

import java.util.List;

public class AcceptedUbigeoDepartment {

    private String departmentId;
    private List<AcceptedUbigeoProvince> provinces;

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public List<AcceptedUbigeoProvince> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<AcceptedUbigeoProvince> provinces) {
        this.provinces = provinces;
    }
}
