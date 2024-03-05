/**
 *
 */
package com.affirm.common.model.catalog;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class Ubigeo implements Serializable {

    private String id;
    private Department department;
    private Province province;
    private District district;

    public String getCompleteDescription() {
        return department.getName() + "/ " + province.getName() + "/ " + district.getName();
    }

    public String getUbigeo(){
        return getDepartment().getId().concat(getProvince().getId().concat(getDistrict().getId()));
    }

    public String getIneiUbigeoId(){
        return getDepartment().getIneiId().concat(getProvince().getIneiId().concat(getDistrict().getIneiId()));
    }

    public String getProvinceUbigeoId(){
        if(department.getCountry() != null) return getDepartment().getCountry().getId().toString().concat(getDepartment().getId().concat(getProvince().getId()));
        return "51".concat(getDepartment().getId().concat(getProvince().getId()));
    }

    public String getDepartmentUbigeoId(){
        if(department.getCountry() != null) return getDepartment().getCountry().getId().toString().concat(getDepartment().getId());
        return "51".concat(getDepartment().getId());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }
}
