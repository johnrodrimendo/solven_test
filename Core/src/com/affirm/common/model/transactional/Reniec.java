package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.Ubigeo;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jrodriguez on 04/08/16.
 */
public class Reniec implements Serializable {

    private String docNumber;
    private String checker;
    private String table;
    private Ubigeo ubigeo;
    private String firstSurname;
    private String lastSurname;
    private String name;
    private Character gender;
    private Date birthday;
    private Integer codgrado;
    private String restriction;
    private IdentityDocumentType docType;
    private String disability;
    private Integer age;
    private String fullName;
    private String grado;

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        setDocNumber(JsonUtil.getStringFromJson(json, "documento", null));
        setChecker(JsonUtil.getStringFromJson(json, "verificador", null));
        setTable(JsonUtil.getStringFromJson(json, "mesa", null));
        if (JsonUtil.getStringFromJson(json, "ubigeo", null) != null) {
            setUbigeo(catalog.getUbigeo(JsonUtil.getStringFromJson(json, "ubigeo", null)));
        }
        setFirstSurname(JsonUtil.getStringFromJson(json, "paterno", null));
        setLastSurname(JsonUtil.getStringFromJson(json, "materno", null));
        setName(JsonUtil.getStringFromJson(json, "nombres", null));
        setGender(JsonUtil.getCharacterFromJson(json, "sexo", null));
        setBirthday(JsonUtil.getPostgresDateFromJson(json, "nacimiento", null));
        setCodgrado(JsonUtil.getIntFromJson(json, "codgrado", null));
        setRestriction(JsonUtil.getStringFromJson(json, "restriccion", null));
        if (JsonUtil.getIntFromJson(json, "tipodoc", null) != null) {
            setDocType(catalog.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "tipodoc", null)));
        }
        setDisability(JsonUtil.getStringFromJson(json, "discapacidad", null));
        setAge(JsonUtil.getIntFromJson(json, "edad", null));
        setFullName(JsonUtil.getStringFromJson(json, "nombrecompleto", null));
        setGrado(JsonUtil.getStringFromJson(json, "grado", null));
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getChecker() {
        return checker;
    }

    public void setChecker(String checker) {
        this.checker = checker;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Ubigeo getUbigeo() {
        return ubigeo;
    }

    public void setUbigeo(Ubigeo ubigeo) {
        this.ubigeo = ubigeo;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public String getLastSurname() {
        return lastSurname;
    }

    public void setLastSurname(String lastSurname) {
        this.lastSurname = lastSurname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getCodgrado() {
        return codgrado;
    }

    public void setCodgrado(Integer codgrado) {
        this.codgrado = codgrado;
    }

    public String getRestriction() {
        return restriction;
    }

    public void setRestriction(String restriction) {
        this.restriction = restriction;
    }

    public IdentityDocumentType getDocType() {
        return docType;
    }

    public void setDocType(IdentityDocumentType docType) {
        this.docType = docType;
    }

    public String getDisability() {
        return disability;
    }

    public void setDisability(String disability) {
        this.disability = disability;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }
}
