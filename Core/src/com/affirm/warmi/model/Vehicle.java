package com.affirm.warmi.model;

public class Vehicle {

    private String driverLicenseNumber;
    private String driverLicenseCategory;
    private String driverLicenseValidity;
    private String driverLicenseStatus;
    private Integer verySeriousFoulsNumber;
    private Integer seriousFoulsNumber;
    private Integer accumPoints;

    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }

    public String getDriverLicenseCategory() {
        return driverLicenseCategory;
    }

    public void setDriverLicenseCategory(String driverLicenseCategory) {
        this.driverLicenseCategory = driverLicenseCategory;
    }

    public String getDriverLicenseValidity() {
        return driverLicenseValidity;
    }

    public void setDriverLicenseValidity(String driverLicenseValidity) {
        this.driverLicenseValidity = driverLicenseValidity;
    }

    public String getDriverLicenseStatus() {
        return driverLicenseStatus;
    }

    public void setDriverLicenseStatus(String driverLicenseStatus) {
        this.driverLicenseStatus = driverLicenseStatus;
    }

    public Integer getVerySeriousFoulsNumber() {
        return verySeriousFoulsNumber;
    }

    public void setVerySeriousFoulsNumber(Integer verySeriousFoulsNumber) {
        this.verySeriousFoulsNumber = verySeriousFoulsNumber;
    }

    public Integer getSeriousFoulsNumber() {
        return seriousFoulsNumber;
    }

    public void setSeriousFoulsNumber(Integer seriousFoulsNumber) {
        this.seriousFoulsNumber = seriousFoulsNumber;
    }

    public Integer getAccumPoints() {
        return accumPoints;
    }

    public void setAccumPoints(Integer accumPoints) {
        this.accumPoints = accumPoints;
    }
}
