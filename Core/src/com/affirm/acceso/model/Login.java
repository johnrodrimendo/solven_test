package com.affirm.acceso.model;

/**
 * Created by miberico on 26/07/17.
 */
public class Login {

    private String p_no_usuari;
    private String p_pw_usuari;
    private String p_ip_remoto;
    private String p_co_phyadd;
    private String p_no_sisnav;
    private Integer p_ti_frawor;
    private Integer p_co_plataf;

    public Login(String userName, String password, String remoteIP, String physicalAddress, String browserType){
        setUserName(userName);
        setPassword(password);
        setRemoteIP(remoteIP);
        setPhysicalAddress(physicalAddress);
        setBrowserType(browserType);
        setFrameworkType(1);
        setPlatform(3);
    }

    public Login(String userName, String password, String remoteIP, String physicalAddress, String browserType, Integer frameworkType, Integer platform){
        setUserName(userName);
        setPassword(password);
        setRemoteIP(remoteIP);
        setPhysicalAddress(physicalAddress);
        setBrowserType(browserType);
        setFrameworkType(frameworkType);
        setPlatform(platform);
    }

    public String getUserName() {
        return p_no_usuari;
    }

    public void setUserName(String userName) {
        this.p_no_usuari = userName;
    }

    public String getPassword() {
        return p_pw_usuari;
    }

    public void setPassword(String password) {
        this.p_pw_usuari = password;
    }

    public String getRemoteIP() {
        return p_ip_remoto;
    }

    public void setRemoteIP(String remoteIP) {
        this.p_ip_remoto = remoteIP;
    }

    public String getPhysicalAddress() {
        return p_co_phyadd;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.p_co_phyadd = physicalAddress;
    }

    public String getBrowserType() {
        return p_no_sisnav;
    }

    public void setBrowserType(String browserType) {
        this.p_no_sisnav = browserType;
    }

    public Integer getFrameworkType() {
        return p_ti_frawor;
    }

    public void setFrameworkType(Integer frameworkType) {
        this.p_ti_frawor = frameworkType;
    }

    public Integer getPlatform() {
        return p_co_plataf;
    }

    public void setPlatform(Integer platform) {
        this.p_co_plataf = platform;
    }
}
