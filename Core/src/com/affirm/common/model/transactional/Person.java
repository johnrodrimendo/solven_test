/**
 *
 */
package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public class Person extends FormGeneric implements Serializable {

    private Integer id;
    private Integer userId;
    private String name;
    private String firstSurname;
    private String lastSurname;
    private IdentityDocumentType documentType;
    private String documentNumber;
    private MaritalStatus maritalStatus;
    private Character gender;
    private Date birthday;
    private Nationality nationality;
    private Ubigeo birthUbigeo;
    private Boolean pep;
    private String cityCode;
    private String landline;
    private String jsRandomPartners;
    private Boolean validatedPartner;
    private Boolean hasDebt;
    private Boolean negativeBase;
    private String efxMaritalStatus;
    private String efxAddresses;
    private String efxIncomePredictor;
    private StudyLevel studyLevel;
    private Profession profession;
    private Integer selfEvaluationResult;
    private Integer dependents;
    private Person partner;
    private CountryParam country;
    private String pepDetail;
    private CustomProfession customProfession;
    private Integer professionOccupationId;
    private PersonProfessionOccupation professionOccupation;
    private Boolean fatca;
    private Date registerDate;

    public void fillFromDb(CatalogService catalogService, JSONObject json, CatalogService catalog, Locale locale) throws Exception{
        setId(JsonUtil.getIntFromJson(json, "person_id", null));
        setUserId(JsonUtil.getIntFromJson(json, "user_id", null));
        setName(JsonUtil.getStringFromJson(json, "person_name", null));
        setFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        if (JsonUtil.getIntFromJson(json, "document_id", null) != null) {
            setDocumentType(catalog.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_id", null)));
        }
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        if (JsonUtil.getIntFromJson(json, "marital_status_id", null) != null) {
            setMaritalStatus(catalog.getMaritalStatus(locale, JsonUtil.getIntFromJson(json, "marital_status_id", null)));
        }
        setGender(JsonUtil.getCharacterFromJson(json, "gender", null));
        setBirthday(JsonUtil.getPostgresDateFromJson(json, "birthday", null));
        if (JsonUtil.getIntFromJson(json, "nationality_id", null) != null) {
            setNationality(catalog.getNationality(locale, JsonUtil.getIntFromJson(json, "nationality_id", null)));
        }
        setPep(JsonUtil.getBooleanFromJson(json, "pep", null));
        setCityCode(JsonUtil.getStringFromJson(json, "city_code", null));
        setLandline(JsonUtil.getStringFromJson(json, "landline", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_random_partners", null) != null) {
            setJsRandomPartners(JsonUtil.getJsonArrayFromJson(json, "js_random_partners", null).toString());
        }
        setValidatedPartner(JsonUtil.getBooleanFromJson(json, "il_validated_partner", null));
        setEfxMaritalStatus(JsonUtil.getStringFromJson(json, "efx_marital_status", null));
        if (JsonUtil.getJsonArrayFromJson(json, "efx_income_predictor", null) != null) {
            setEfxIncomePredictor(JsonUtil.getJsonObjectFromJson(json, "efx_income_predictor", null).toString());
        }
        if (JsonUtil.getJsonArrayFromJson(json, "efx_addresses", null) != null) {
            setEfxAddresses(JsonUtil.getJsonArrayFromJson(json, "efx_addresses", null).toString());
        }
        setNegativeBase(JsonUtil.getBooleanFromJson(json, "negative_base", null));
        if (JsonUtil.getIntFromJson(json, "study_level_id", null) != null) {
            setStudyLevel(catalog.getStudyLevel(locale, JsonUtil.getIntFromJson(json, "study_level_id", null)));
        }
        if (JsonUtil.getIntFromJson(json, "profession_id", null) != null) {
            setProfession(catalog.getProfession(locale, JsonUtil.getIntFromJson(json, "profession_id", null)));
        }
        setSelfEvaluationResult(JsonUtil.getIntFromJson(json, "self_evaluation_score", null));
        setDependents(JsonUtil.getIntFromJson(json, "dependents", null));
        if(getDocumentType()!=null) {
            setCountry(catalogService.getCountryParam(getDocumentType().getCountryId()));
        }
        if (JsonUtil.getStringFromJson(json, "birth_ubigeo_id", null) != null) {
            setBirthUbigeo(catalogService.getUbigeo(JsonUtil.getStringFromJson(json, "birth_ubigeo_id", null)));
        }
        setPepDetail(JsonUtil.getStringFromJson(json, "pep_detail", null));

        if (JsonUtil.getIntFromJson(json, "custom_profession_id", null) != null) {
            setCustomProfession(catalog.getCustomProfessionById( JsonUtil.getIntFromJson(json, "custom_profession_id", null)));
        }
        setProfessionOccupationId(JsonUtil.getIntFromJson(json, "profession_occupation_id", null));
        if (JsonUtil.getIntFromJson(json, "profession_occupation_id", null) != null) {
            setProfessionOccupation(catalog.getProfessionOccupation( JsonUtil.getIntFromJson(json, "profession_occupation_id", null)));
        }
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
    }

    public String getFullName() {
        return (getName() != null ? getName() : "") + " " + (getFirstSurname() != null ? getFirstSurname() : "") + " " + (getLastSurname() != null ? getLastSurname() : "");
    }

    public String getFullNameSurnameFirst() {
        return (getFirstSurname() != null ? getFirstSurname() : "") + " " + (getLastSurname() != null ? getLastSurname() : "") + " " + (getName() != null ? getName() : "");
    }

    public String getFullSurnames() {
        return (getFirstSurname() != null ? getFirstSurname() : "") + " " + (getLastSurname() != null ? getLastSurname() : "");
    }

    public String getFirstName() {
        if (name != null) {
            return getName().split(" ")[0];
        }
        return "";
    }

    public String getOtherNames() {
        if (name != null && name.contains(" ")) {
            return getName().substring(getName().indexOf(' ') + 1);
        }
        return "";
    }

    public String getDNIFromCUIT() {
        if (country.getId() == CountryParam.COUNTRY_ARGENTINA) {
            return getDocumentNumber().substring(2, getDocumentNumber().length() - 1);
        } else {
            return null;
        }
    }

    public String getGenderFromCUIT() {
        if (country.getId() == CountryParam.COUNTRY_ARGENTINA) {
            if(getDocumentNumber().substring(0, 2).equals("27"))
                return "F";
            else if(getDocumentNumber().substring(0, 2).equals("20"))
                return "M";
        }
        return null;
    }

    public boolean hasPartner() {
        return partner != null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public IdentityDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(IdentityDocumentType documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
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

    public Person getPartner() {
        return partner;
    }

    public void setPartner(Person partner) {
        this.partner = partner;
    }

    public Nationality getNationality() {
        return nationality;
    }

    public void setNationality(Nationality nationality) {
        this.nationality = nationality;
    }

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public Boolean getPep() {
        return pep;
    }

    public void setPep(Boolean pep) {
        this.pep = pep;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getJsRandomPartners() {
        return jsRandomPartners;
    }

    public void setJsRandomPartners(String jsRandomPartners) {
        this.jsRandomPartners = jsRandomPartners;
    }

    public Boolean getValidatedPartner() {
        return validatedPartner;
    }

    public void setValidatedPartner(Boolean validatedPartner) {
        this.validatedPartner = validatedPartner;
    }

    public String getEfxMaritalStatus() {
        return efxMaritalStatus;
    }

    public void setEfxMaritalStatus(String efxMaritalStatus) {
        this.efxMaritalStatus = efxMaritalStatus;
    }

    public String getEfxAddresses() {
        return efxAddresses;
    }

    public void setEfxAddresses(String efxAddresses) {
        this.efxAddresses = efxAddresses;
    }

    public String getEfxIncomePredictor() {
        return efxIncomePredictor;
    }

    public void setEfxIncomePredictor(String efxIncomePredictor) {
        this.efxIncomePredictor = efxIncomePredictor;
    }

    public Boolean getNegativeBase() {
        return negativeBase;
    }

    public void setNegativeBase(Boolean negativeBase) {
        this.negativeBase = negativeBase;
    }

    public Boolean getHasDebt() {
        return hasDebt;
    }

    public void setHasDebt(Boolean hasDebt) {
        this.hasDebt = hasDebt;
    }

    public StudyLevel getStudyLevel() {
        return studyLevel;
    }

    public void setStudyLevel(StudyLevel studyLevel) {
        this.studyLevel = studyLevel;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public Integer getSelfEvaluationResult() {
        return selfEvaluationResult;
    }

    public void setSelfEvaluationResult(Integer selfEvaluationResult) {
        this.selfEvaluationResult = selfEvaluationResult;
    }

    public Integer getDependents() {
        return dependents;
    }

    public void setDependents(Integer dependents) {
        this.dependents = dependents;
    }

    public CountryParam getCountry() {
        return country;
    }

    public void setCountry(CountryParam country) {
        this.country = country;
    }

    public Ubigeo getBirthUbigeo() {
        return birthUbigeo;
    }

    public void setBirthUbigeo(Ubigeo birthUbigeo) {
        this.birthUbigeo = birthUbigeo;
    }

    public String getPepDetail() {
        return pepDetail;
    }

    public void setPepDetail(String pepDetail) {
        this.pepDetail = pepDetail;
    }

    public CustomProfession getCustomProfession() {
        return customProfession;
    }

    public void setCustomProfession(CustomProfession customProfession) {
        this.customProfession = customProfession;
    }

    public Integer getProfessionOccupationId() {
        return professionOccupationId;
    }

    public void setProfessionOccupationId(Integer professionOccupationId) {
        this.professionOccupationId = professionOccupationId;
    }

    public PersonProfessionOccupation getProfessionOccupation() {
        return professionOccupation;
    }

    public void setProfessionOccupation(PersonProfessionOccupation professionOccupation) {
        this.professionOccupation = professionOccupation;
    }

    public Boolean getFatca() {
        return fatca;
    }

    public void setFatca(Boolean fatca) {
        this.fatca = fatca;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }
}

