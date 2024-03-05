package com.affirm.warmi.model;

import java.util.List;

public class ProcessDetail {

    private String processId;
    private Person person;
    private List<Education> educations;
    private WorkInformation workInformation;
    private HealthSystem healthSystem;
    private List<FinancialSystem> financialSystems;
    private PenaltyFee penaltyFee;
    private List<TelephoneService> telephoneServices;
    private List<Property> properties;
    private Commercial commercial;
    private List<Legal> legals;
    private List<JudicialBackground> judicialBackgrounds;
    private InternationalReport internationalReport;
    private Vehicle vehicle;
    private AdditionalInformation additionalInformation;
    private Employer employer;
    private List<Tax> taxes;
    private PluginScore pluginScore;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<Education> getEducations() {
        return educations;
    }

    public void setEducations(List<Education> educations) {
        this.educations = educations;
    }

    public WorkInformation getWorkInformation() {
        return workInformation;
    }

    public void setWorkInformation(WorkInformation workInformation) {
        this.workInformation = workInformation;
    }

    public HealthSystem getHealthSystem() {
        return healthSystem;
    }

    public void setHealthSystem(HealthSystem healthSystem) {
        this.healthSystem = healthSystem;
    }

    public List<FinancialSystem> getFinancialSystems() {
        return financialSystems;
    }

    public void setFinancialSystems(List<FinancialSystem> financialSystems) {
        this.financialSystems = financialSystems;
    }

    public PenaltyFee getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee(PenaltyFee penaltyFee) {
        this.penaltyFee = penaltyFee;
    }

    public List<TelephoneService> getTelephoneServices() {
        return telephoneServices;
    }

    public void setTelephoneServices(List<TelephoneService> telephoneServices) {
        this.telephoneServices = telephoneServices;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public Commercial getCommercial() {
        return commercial;
    }

    public void setCommercial(Commercial commercial) {
        this.commercial = commercial;
    }

    public List<Legal> getLegals() {
        return legals;
    }

    public void setLegals(List<Legal> legals) {
        this.legals = legals;
    }

    public List<JudicialBackground> getJudicialBackgrounds() {
        return judicialBackgrounds;
    }

    public void setJudicialBackgrounds(List<JudicialBackground> judicialBackgrounds) {
        this.judicialBackgrounds = judicialBackgrounds;
    }

    public InternationalReport getInternationalReport() {
        return internationalReport;
    }

    public void setInternationalReport(InternationalReport internationalReport) {
        this.internationalReport = internationalReport;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public AdditionalInformation getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(AdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    public List<Tax> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<Tax> taxes) {
        this.taxes = taxes;
    }

    public PluginScore getPluginScore() {
        return pluginScore;
    }

    public void setPluginScore(PluginScore pluginScore) {
        this.pluginScore = pluginScore;
    }
}
