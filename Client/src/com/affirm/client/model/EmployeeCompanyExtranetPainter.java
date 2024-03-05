package com.affirm.client.model;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.transactional.Employee;
import com.affirm.common.model.transactional.PersonAssociated;
import com.affirm.common.model.transactional.UserFile;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */
public class EmployeeCompanyExtranetPainter extends Employee {

    private List<UserFile> contractFile;
    private Employee employee;
    private PersonAssociated agreementAssociated;
    private Boolean agreementAssociatedValidated;
    private PersonAssociated consolidationAssociated;
    private Boolean consolidationAssociatedValidated;
    private Entity entity;
    private List<PersonAssociated> personAssociateds;
    private List<UserFile> contracts;
    private Integer activeCreditId;
    private boolean showAeluRecivePreliminaryDocButton = false;
    private boolean showAeluRecivePromisoryNoteButton = false;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        super.fillFromDb(json, catalog, locale);
        if (JsonUtil.getJsonArrayFromJson(json, "person_entity", null) != null) {
            JSONArray entities = JsonUtil.getJsonArrayFromJson(json, "person_entity", null);
            personAssociateds = new ArrayList<>();
            for (int i = 0; i < entities.length(); i++) {
                PersonAssociated associated = new PersonAssociated();
                associated.fillFromDb(entities.getJSONObject(i), catalog);
                personAssociateds.add(associated);
            }
        }
        if (JsonUtil.getJsonArrayFromJson(json, "user_files", null) != null) {
            JSONArray userFiles = JsonUtil.getJsonArrayFromJson(json, "user_files", null);
            contracts = new ArrayList<>();
            for (int i = 0; i < userFiles.length(); i++) {
                UserFile userFile = new UserFile();
                userFile.fillFromDb(userFiles.getJSONObject(i), catalog);
                contracts.add(userFile);
            }
        }
        setActiveCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
    }


    public List<UserFile> getContractFile() {
        return contractFile;
    }

    public void setContractFile(List<UserFile> contractFile) {
        this.contractFile = contractFile;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public PersonAssociated getAgreementAssociated() {
        return agreementAssociated;
    }

    public void setAgreementAssociated(PersonAssociated agreementAssociated) {
        this.agreementAssociated = agreementAssociated;
    }

    public Boolean getAgreementAssociatedValidated() {
        return agreementAssociatedValidated;
    }

    public void setAgreementAssociatedValidated(Boolean agreementAssociatedValidated) {
        this.agreementAssociatedValidated = agreementAssociatedValidated;
    }

    public PersonAssociated getConsolidationAssociated() {
        return consolidationAssociated;
    }

    public void setConsolidationAssociated(PersonAssociated consolidationAssociated) {
        this.consolidationAssociated = consolidationAssociated;
    }

    public Boolean getConsolidationAssociatedValidated() {
        return consolidationAssociatedValidated;
    }

    public void setConsolidationAssociatedValidated(Boolean consolidationAssociatedValidated) {
        this.consolidationAssociatedValidated = consolidationAssociatedValidated;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public List<PersonAssociated> getPersonAssociateds() {
        return personAssociateds;
    }

    public void setPersonAssociateds(List<PersonAssociated> personAssociateds) {
        this.personAssociateds = personAssociateds;
    }

    public List<UserFile> getContracts() {
        return contracts;
    }

    public void setContracts(List<UserFile> contracts) {
        this.contracts = contracts;
    }

    public Integer getActiveCreditId() {
        return activeCreditId;
    }

    public void setActiveCreditId(Integer activeCreditId) {
        this.activeCreditId = activeCreditId;
    }

    public boolean isShowAeluRecivePreliminaryDocButton() {
        return showAeluRecivePreliminaryDocButton;
    }

    public void setShowAeluRecivePreliminaryDocButton(boolean showAeluRecivePreliminaryDocButton) {
        this.showAeluRecivePreliminaryDocButton = showAeluRecivePreliminaryDocButton;
    }

    public boolean isShowAeluRecivePromisoryNoteButton() {
        return showAeluRecivePromisoryNoteButton;
    }

    public void setShowAeluRecivePromisoryNoteButton(boolean showAeluRecivePromisoryNoteButton) {
        this.showAeluRecivePromisoryNoteButton = showAeluRecivePromisoryNoteButton;
    }
}
