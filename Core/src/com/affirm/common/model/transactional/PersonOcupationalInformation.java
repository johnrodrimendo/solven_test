package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.*;

/**
 * @author jrodriguez
 */
public class PersonOcupationalInformation implements Serializable {

    public static final int PRINCIPAL = 1;
    public static final int SECUNDARY = 2;
    public static final int TERTIARY = 3;
    public static final int QUATERNARY = 4;
    public static final int OTHER = 0;

    public static final String NORMAL = "NOR";

    public enum Regime {
        REGIMEN_SIMPLIFICADO("NRUS", "Régimen Simplificado"),
        REGIMEN_ESPECIAL("RER", "Régimen Especial"),
        REGIMEN_MYPE("RMT", "Régimen MYPE"),
        REGIMEN_GENERAL("RG", "Régimen General");

        private String code;
        private String description;

        Regime(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public static PersonOcupationalInformation.Regime getByCode(String code) {
            if (code == null)
                return null;

            for (PersonOcupationalInformation.Regime e : values()) {
                if (code.equals(e.code)) return e;
            }
            return null;
        }
    }

    private Integer number;
    private Integer personId;
    private ActivityType activityType;
    private SubActivityType subActivityType;
    private Integer employerId;
    private String companyName;
    private String phoneNumber;
    private String phoneNumberType;
    private String ruc;
    private String shareholderShareholding;
    private Integer shareholderResultU12M;
    private Integer monthlyNetIncome;  // This should DIE!!
    private Double fixedGrossIncome;
    private Integer fixedGrossIncomeIncreasal;
    private String address;
    private Boolean validated;
    private String employmentTime;
    private Integer monthlyGrossIncome; // This should DIE!!
    private VoucherType voucherType;
    private ArrayList<Belonging> belonging;
    private PensionPayer pensionPayer;
    private JSONObject jsIndicators;
    private Date startDate;
    private ServiceType serviceType;
    private Double variableGrossIncome;
    private Ocupation ocupation;
    private String client1Ruc;
    private Boolean client1Ruc65;
    private String client2Ruc;
    private Double lastYearSellings;
    private Integer lastYearSellingsIncreasal;
    private Double exerciseOutcome;
    private Integer exerciseOutcomeIncreasal;
    private Double salesPercentageFixedCosts;
    private Double salesPercentageVariableCosts;
    private Double salesPercentageBestClient;
    private Integer salesPercentageBestClientIndicator;
    private Double averageDailyIncome;
    private Double lastYearCompensation;
    private Boolean otherIncome;
    private Boolean backofficeChanged;
    private FrequencySalary frequencySalary;
    private Boolean homeAddress;
    private Double addressLatitude;
    private Double addressLongitude;
    private String ciiu;
    private District district;
    private Boolean formal;


    private String searchQuery;
    private Ubigeo addressUbigeo;
    private Sector sector;
    private Regime taxRegime;
    private Date retirementDate;
    private RetirementScheme retirementScheme;

    private Integer ocupationAreaId;


    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setNumber(JsonUtil.getIntFromJson(json, "ocupational_information_number", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        if (JsonUtil.getIntFromJson(json, "activity_type_id", null) != null) {
            setActivityType(catalog.getActivityType(locale, JsonUtil.getIntFromJson(json, "activity_type_id", null)));
        }
        if (JsonUtil.getIntFromJson(json, "sub_activity_type_id", null) != null) {
            setSubActivityType(catalog.getSubActivityType(locale, JsonUtil.getIntFromJson(json, "sub_activity_type_id", null)));
        }
        setRuc(JsonUtil.getStringFromJson(json, "ruc", null));
        setEmployerId(JsonUtil.getIntFromJson(json, "employer_id", null));
        setCompanyName(JsonUtil.getStringFromJson(json, "company_name", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        setPhoneNumberType(JsonUtil.getStringFromJson(json, "phone_number_type", null));
        setShareholderShareholding(JsonUtil.getStringFromJson(json, "shareholding", null));
        if (JsonUtil.getStringFromJson(json, "result_last_months", null) != null) {
            setShareholderResultU12M(Integer.parseInt(JsonUtil.getStringFromJson(json, "result_last_months", null)));
        }
        setAddress(JsonUtil.getStringFromJson(json, "address", null));
        setValidated(JsonUtil.getBooleanFromJson(json, "is_validated", null));
        setEmploymentTime(JsonUtil.getStringFromJson(json, "employment_time", null));
        setMonthlyNetIncome(JsonUtil.getIntFromJson(json, "net_income", null));
        setMonthlyGrossIncome(JsonUtil.getIntFromJson(json, "gross_income", null));
        if (JsonUtil.getIntFromJson(json, "voucher_type_id", null) != null) {
            setVoucherType(catalog.getVoucherType(locale, JsonUtil.getIntFromJson(json, "voucher_type_id", null)));
        }
        if (JsonUtil.getJsonArrayFromJson(json, "belonging_id", null) != null) {
            JSONArray jsonBelonings = JsonUtil.getJsonArrayFromJson(json, "belonging_id", null);
            ArrayList<Belonging> belongings = new ArrayList<>();
            for (int i = 0; i < jsonBelonings.length(); i++) {
                Belonging be = new Belonging();
                be = catalog.getBelonging(locale, (int) jsonBelonings.get(i));
                belongings.add(be);
            }
            setBelonging(belongings);
        }
        if (JsonUtil.getIntFromJson(json, "pension_payer_id", null) != null) {
            setPensionPayer(catalog.getPensionPayer(locale, JsonUtil.getIntFromJson(json, "pension_payer_id", null)));
        }
        if (JsonUtil.getJsonObjectFromJson(json, "js_indicators", null) != null) {
            setJsIndicators(json.getJSONObject("js_indicators"));
        }
        setStartDate(JsonUtil.getPostgresDateFromJson(json, "start_date", null));
        if (JsonUtil.getIntFromJson(json, "service_type_id", null) != null) {
            setServiceType(catalog.getServiceType(locale, JsonUtil.getIntFromJson(json, "service_type_id", null)));
        }
        setVariableGrossIncome(JsonUtil.getDoubleFromJson(json, "variable_gross_income", null));
        if (JsonUtil.getIntFromJson(json, "ocupation_id", null) != null) {
            setOcupation(catalog.getOcupation(locale, JsonUtil.getIntFromJson(json, "ocupation_id", null)));
        }
        setClient1Ruc(JsonUtil.getStringFromJson(json, "client_ruc_1", null));
        setClient2Ruc(JsonUtil.getStringFromJson(json, "client_ruc_2", null));
        setLastYearSellings(JsonUtil.getDoubleFromJson(json, "last_year_sellings", null));
        setExerciseOutcome(JsonUtil.getDoubleFromJson(json, "exercise_outcome", null));
        setSalesPercentageFixedCosts(JsonUtil.getDoubleFromJson(json, "sales_percentage_fixed_costs", null));
        setSalesPercentageVariableCosts(JsonUtil.getDoubleFromJson(json, "sales_percentage_variable_costs", null));
        setSalesPercentageBestClient(JsonUtil.getDoubleFromJson(json, "sales_percentage_best_client", null));
        setAverageDailyIncome(JsonUtil.getDoubleFromJson(json, "bank_account_money", null));
        setLastYearCompensation(JsonUtil.getDoubleFromJson(json, "last_year_compensation", null));
        setFixedGrossIncome(JsonUtil.getDoubleFromJson(json, "fixed_gross_income", null));
        setFixedGrossIncomeIncreasal(JsonUtil.getIntFromJson(json, "fixed_gross_income_increasal", null));
        setLastYearSellingsIncreasal(JsonUtil.getIntFromJson(json, "last_year_sellings_increasal", null));
        setExerciseOutcomeIncreasal(JsonUtil.getIntFromJson(json, "exercise_outcome_increasal", null));
        setSalesPercentageBestClientIndicator(JsonUtil.getIntFromJson(json, "sales_percentage_best_client_indicator", null));
        setClient1Ruc65(JsonUtil.getBooleanFromJson(json, "client_1_65_sellings", null));
        setOtherIncome(JsonUtil.getBooleanFromJson(json, "other_incomes", null));
        setHomeAddress(JsonUtil.getBooleanFromJson(json, "is_home_address", null));
        setBackofficeChanged(JsonUtil.getBooleanFromJson(json, "bo_changed", null));

        if (JsonUtil.getStringFromJson(json, "search_query", null) != null)
            setSearchQuery(JsonUtil.getStringFromJson(json, "search_query", null));
        if (JsonUtil.getDoubleFromJson(json, "address_latitude", null) != null)
            setAddressLatitude(JsonUtil.getDoubleFromJson(json, "address_latitude", null));
        if (JsonUtil.getDoubleFromJson(json, "address_longitude", null) != null)
            setAddressLongitude(JsonUtil.getDoubleFromJson(json, "address_longitude", null));
        if (JsonUtil.getStringFromJson(json, "address_ubigeo_id", null) != null) {
            setAddressUbigeo(catalog.getUbigeo(JsonUtil.getStringFromJson(json, "address_ubigeo_id", null)));
        }
        setSector(catalog.getSectorById(JsonUtil.getStringFromJson(json, "sector", null)));
        if (JsonUtil.getJsonObjectFromJson(json, "js_income_frequency", null) != null)
            setFrequencySalary(new FrequencySalary(JsonUtil.getJsonObjectFromJson(json, "js_income_frequency", null)));
        setTaxRegime(Regime.getByCode(JsonUtil.getStringFromJson(json, "tax_regime", null)));
        setRetirementDate(JsonUtil.getPostgresDateFromJson(json,"retirement_date",null));
        if(JsonUtil.getIntFromJson(json,"retirement_scheme_id",null) != null)
            setRetirementScheme(catalog.getRetirementSchemeById(JsonUtil.getIntFromJson(json,"retirement_scheme_id",null)));
        setCiiu(JsonUtil.getStringFromJson(json,"ciiu",null));
        if (JsonUtil.getLongFromJson(json, "locality_id", null) != null) {
            setDistrict(catalog.getGeneralDistrictById(JsonUtil.getLongFromJson(json, "locality_id", null)));
        }
        setOcupationAreaId(JsonUtil.getIntFromJson(json, "ocupation_area_id", null));
        setFormal(JsonUtil.getBooleanFromJson(json, "is_formal", null));
    }

    /**
     * Returns the merged list of the required documents from ActivityType, SubActivityType and Belonging
     */
    public List<Integer> getRequiredDocuments() {
        Set<Integer> set = new HashSet<>();
        if (activityType != null && activityType.getRequiredDocuments() != null)
            set.addAll(activityType.getRequiredDocuments());
        if (subActivityType != null && subActivityType.getRequiredDocuments() != null)
            set.addAll(subActivityType.getRequiredDocuments());
        if (belonging != null && belonging.size() > 0)
            for (Belonging be : belonging) {
                set.addAll(be.getRequiredDocuments());
            }

        return new ArrayList<>(set);
    }

    public String getPhoneNumberWithoutCode() {
        if (phoneNumber == null)
            return null;
        if (phoneNumber.contains("(") && phoneNumber.contains(")"))
            return phoneNumber.substring(phoneNumber.indexOf(')') + 1).replaceAll(" ", "");
        return phoneNumber;
    }

    public String getPhoneCode() {
        if (phoneNumber == null)
            return null;
        if (phoneNumber.contains("(") && phoneNumber.contains(")"))
            return phoneNumber.substring(phoneNumber.indexOf('(') + 1, phoneNumber.indexOf(')')).replaceAll(" ", "");
        return null;
    }

    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();

        fullAddress.append(address).append(" ");

        if (addressUbigeo != null) {
            fullAddress.append(String.format("%s %s %s ",
                    addressUbigeo.getDistrict().getName(),
                    addressUbigeo.getProvince().getName(),
                    addressUbigeo.getDepartment().getName()));
        }

        return fullAddress.toString().trim();
    }

    public String getRUCAlert() {
        return JsonUtil.getStringFromJson(getJsIndicators(), "calif", null);
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getShareholderShareholding() {
        return shareholderShareholding;
    }

    public void setShareholderShareholding(String shareholderShareholding) {
        this.shareholderShareholding = shareholderShareholding;
    }

    public Integer getShareholderResultU12M() {
        return shareholderResultU12M;
    }

    public void setShareholderResultU12M(Integer shareholderResultU12M) {
        this.shareholderResultU12M = shareholderResultU12M;
    }

    public Integer getMonthlyNetIncome() {
        return monthlyNetIncome;
    }

    public void setMonthlyNetIncome(Integer monthlyNetIncome) {
        this.monthlyNetIncome = monthlyNetIncome;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public String getEmploymentTime() {
        return employmentTime;
    }

    public void setEmploymentTime(String employmentTime) {
        this.employmentTime = employmentTime;
    }

    public Integer getMonthlyGrossIncome() {
        return monthlyGrossIncome;
    }

    public void setMonthlyGrossIncome(Integer monthlyGrossIncome) {
        this.monthlyGrossIncome = monthlyGrossIncome;
    }

    public VoucherType getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(VoucherType voucherType) {
        this.voucherType = voucherType;
    }

    public String getBelongingToString() {
        if (belonging != null && belonging.size() > 0) {
            String str = "";
            for (Belonging be : belonging) {
                str = str.concat(be.getName()).concat(", ");
            }
            return str.substring(0, str.length() - 3);
        }
        return "";
    }

    public ArrayList<Belonging> getBelonging() {
        return belonging;
    }

    public void setBelonging(ArrayList<Belonging> belonging) {
        this.belonging = belonging;
    }

    public PensionPayer getPensionPayer() {
        return pensionPayer;
    }

    public void setPensionPayer(PensionPayer pensionPayer) {
        this.pensionPayer = pensionPayer;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public JSONObject getJsIndicators() {
        return jsIndicators;
    }

    public void setJsIndicators(JSONObject jsIndicators) {
        this.jsIndicators = jsIndicators;
    }

    public Boolean essa() {
        return JsonUtil.getBooleanFromJson(jsIndicators, "essa", null);
    }

    public Boolean in_range() {
        return JsonUtil.getBooleanFromJson(jsIndicators, "in_range", null);
    }

    public Boolean ruc_active() {
        return JsonUtil.getBooleanFromJson(jsIndicators, "ruc_active", null);
    }

    public Boolean ruc_alert() {
        return JsonUtil.getBooleanFromJson(jsIndicators, "ruc_alert", null);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public Double getVariableGrossIncome() {
        return variableGrossIncome;
    }

    public void setVariableGrossIncome(Double variableGrossIncome) {
        this.variableGrossIncome = variableGrossIncome;
    }

    public Ocupation getOcupation() {
        return ocupation;
    }

    public void setOcupation(Ocupation ocupation) {
        this.ocupation = ocupation;
    }

    public SubActivityType getSubActivityType() {
        return subActivityType;
    }

    public void setSubActivityType(SubActivityType subActivityType) {
        this.subActivityType = subActivityType;
    }

    public String getClient1Ruc() {
        return client1Ruc;
    }

    public void setClient1Ruc(String client1Ruc) {
        this.client1Ruc = client1Ruc;
    }

    public String getClient2Ruc() {
        return client2Ruc;
    }

    public void setClient2Ruc(String client2Ruc) {
        this.client2Ruc = client2Ruc;
    }

    public Double getLastYearSellings() {
        return lastYearSellings;
    }

    public void setLastYearSellings(Double lastYearSellings) {
        this.lastYearSellings = lastYearSellings;
    }

    public Double getExerciseOutcome() {
        return exerciseOutcome;
    }

    public void setExerciseOutcome(Double exerciseOutcome) {
        this.exerciseOutcome = exerciseOutcome;
    }

    public Double getSalesPercentageFixedCosts() {
        return salesPercentageFixedCosts;
    }

    public void setSalesPercentageFixedCosts(Double salesPercentageFixedCosts) {
        this.salesPercentageFixedCosts = salesPercentageFixedCosts;
    }

    public Double getSalesPercentageVariableCosts() {
        return salesPercentageVariableCosts;
    }

    public void setSalesPercentageVariableCosts(Double salesPercentageVariableCosts) {
        this.salesPercentageVariableCosts = salesPercentageVariableCosts;
    }

    public Double getSalesPercentageBestClient() {
        return salesPercentageBestClient;
    }

    public void setSalesPercentageBestClient(Double salesPercentageBestClient) {
        this.salesPercentageBestClient = salesPercentageBestClient;
    }

    public Double getAverageDailyIncome() {
        return averageDailyIncome;
    }

    public void setAverageDailyIncome(Double averageDailyIncome) {
        this.averageDailyIncome = averageDailyIncome;
    }

    public Double getLastYearCompensation() {
        return lastYearCompensation;
    }

    public void setLastYearCompensation(Double lastYearCompensation) {
        this.lastYearCompensation = lastYearCompensation;
    }

    public Double getFixedGrossIncome() {
        return fixedGrossIncome;
    }

    public void setFixedGrossIncome(Double fixedGrossIncome) {
        this.fixedGrossIncome = fixedGrossIncome;
    }

    public Integer getFixedGrossIncomeIncreasal() {
        return fixedGrossIncomeIncreasal;
    }

    public void setFixedGrossIncomeIncreasal(Integer fixedGrossIncomeIncreasal) {
        this.fixedGrossIncomeIncreasal = fixedGrossIncomeIncreasal;
    }

    public Boolean getClient1Ruc65() {
        return client1Ruc65;
    }

    public void setClient1Ruc65(Boolean client1Ruc65) {
        this.client1Ruc65 = client1Ruc65;
    }

    public Integer getLastYearSellingsIncreasal() {
        return lastYearSellingsIncreasal;
    }

    public void setLastYearSellingsIncreasal(Integer lastYearSellingsIncreasal) {
        this.lastYearSellingsIncreasal = lastYearSellingsIncreasal;
    }

    public Integer getExerciseOutcomeIncreasal() {
        return exerciseOutcomeIncreasal;
    }

    public void setExerciseOutcomeIncreasal(Integer exerciseOutcomeIncreasal) {
        this.exerciseOutcomeIncreasal = exerciseOutcomeIncreasal;
    }

    public Integer getSalesPercentageBestClientIndicator() {
        return salesPercentageBestClientIndicator;
    }

    public void setSalesPercentageBestClientIndicator(Integer salesPercentageBestClientIndicator) {
        this.salesPercentageBestClientIndicator = salesPercentageBestClientIndicator;
    }

    public Integer getEmployerId() {
        return employerId;
    }

    public void setEmployerId(Integer employerId) {
        this.employerId = employerId;
    }

    public Boolean getOtherIncome() {
        return otherIncome;
    }

    public void setOtherIncome(Boolean otherIncome) {
        this.otherIncome = otherIncome;
    }

    public Boolean getHomeAddress() {
        return homeAddress != null ? homeAddress : false;
    }

    public void setHomeAddress(Boolean homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Boolean getBackofficeChanged() {
        return backofficeChanged;
    }

    public void setBackofficeChanged(Boolean backofficeChanged) {
        this.backofficeChanged = backofficeChanged;
    }

    public String getPhoneNumberType() {
        return phoneNumberType;
    }

    public void setPhoneNumberType(String phoneNumberType) {
        this.phoneNumberType = phoneNumberType;
    }

    public Double getAddressLatitude() {
        return addressLatitude;
    }

    public void setAddressLatitude(Double addressLatitude) {
        this.addressLatitude = addressLatitude;
    }

    public Double getAddressLongitude() {
        return addressLongitude;
    }

    public void setAddressLongitude(Double addressLongitude) {
        this.addressLongitude = addressLongitude;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Ubigeo getAddressUbigeo() {
        return addressUbigeo;
    }

    public void setAddressUbigeo(Ubigeo addressUbigeo) {
        this.addressUbigeo = addressUbigeo;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public FrequencySalary getFrequencySalary() { return frequencySalary; }

    public void setFrequencySalary(FrequencySalary frequencySalary) { this.frequencySalary = frequencySalary; }

    public Regime getTaxRegime() {
        return taxRegime;
    }

    public void setTaxRegime(Regime taxRegime) {
        this.taxRegime = taxRegime;
    }


    public Date getRetirementDate() {
        return retirementDate;
    }

    public void setRetirementDate(Date retirementDate) {
        this.retirementDate = retirementDate;
    }

    public RetirementScheme getRetirementScheme() {
        return retirementScheme;
    }

    public void setRetirementScheme(RetirementScheme retirementScheme) {
        this.retirementScheme = retirementScheme;
    }

    public String getCiiu() {
        return ciiu;
    }

    public void setCiiu(String ciiu) {
        this.ciiu = ciiu;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public Integer getOcupationAreaId() {
        return ocupationAreaId;
    }

    public void setOcupationAreaId(Integer ocupationAreaId) {
        this.ocupationAreaId = ocupationAreaId;
    }

    public Boolean getFormal() {
        return formal;
    }

    public void setFormal(Boolean formal) {
        this.formal = formal;
    }
}