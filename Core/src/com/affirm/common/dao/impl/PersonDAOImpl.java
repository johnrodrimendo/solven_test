package com.affirm.common.dao.impl;

import com.affirm.acceso.model.Direccion;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.PreApprovedInfo;
import com.affirm.common.model.rcc.Synthesized;
import com.affirm.common.model.UniversidadPeru;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.FileService;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.Marshall;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by jrodriguez on 08/06/16.
 */

@Repository
public class PersonDAOImpl extends JsonResolverDAO implements PersonDAO {
    @Autowired
    FileService fileService;

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private TranslatorDAO translatorDAO;

    @Override
    public Person getPerson(CatalogService catalogService, Locale locale, int personId, boolean getPartner) throws Exception {
        return getPerson(personId, getPartner, locale);
    }

    @Override
    public Person getPerson(int personId, boolean getPartner, Locale locale) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from  person.get_person(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return null;
        }

        Person person = new Person();
        person.fillFromDb(catalogService, dbJson, catalogService, locale);

        if (getPartner && JsonUtil.getIntFromJson(dbJson, "partner_id", null) != null) {
            person.setPartner(getPerson(catalogService, locale, JsonUtil.getIntFromJson(dbJson, "partner_id", null), false));
        }
        return person;
    }

    @Override
    public Integer getPersonIdByDocument(int documentType, String documentNumber) throws Exception {
        return queryForObjectTrx("select person_id from person.tb_person where document_id = ? and document_number = ?", Integer.class,
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber));
    }

    @Override
    public Boolean getPersonNegativeBase(Integer personId) throws Exception {
        return queryForObjectTrx("select * from users.is_in_negative_base(?)", Boolean.class,
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public Boolean getPersonHasDebt(Integer personId) throws Exception {
        return queryForObjectTrx("select * from person.has_solven_debts(?)", Boolean.class,
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public PersonContactInformation getPersonContactInformation(Locale locale, int personId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.bo_get_contact_information(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return null;
        }

        PersonContactInformation contact = new PersonContactInformation();
        contact.fillFromDb(dbJson, catalogService, locale);

        return contact;
    }

    @Override
    public List<PersonOcupationalInformation> getPersonOcupationalInformation(Locale locale, int personId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from person.bo_get_ocupational_information(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbArray == null) {
            return null;
        }

        List<PersonOcupationalInformation> ocupationalInformations = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            JSONObject json = dbArray.getJSONObject(i);

            PersonOcupationalInformation painter = new PersonOcupationalInformation();
            painter.fillFromDb(json, catalogService, locale);

            ocupationalInformations.add(painter);
        }
        return ocupationalInformations;
    }

    @Override
    public PersonBankAccountInformation getPersonBankAccountInformationByCredit(Locale locale, int personId, int creditId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.bo_get_account_information_credit(?,?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, creditId));
        if (dbJson == null) {
            return null;
        }

        PersonBankAccountInformation bank = new PersonBankAccountInformation();
        bank.fillFromDb(dbJson, catalogService, locale);
        return bank;
    }

    @Override
    public PersonBankAccountInformation getPersonBankAccountInformation(Locale locale, int personId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.bo_get_account_information(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return null;
        }

        PersonBankAccountInformation bank = new PersonBankAccountInformation();
        bank.fillFromDb(dbJson, catalogService, locale);
        return bank;
    }

    @Override
    public List<LoanApplicationUserFiles> getUserFiles(int personId, Locale locale) {
        JSONArray dbArray = queryForObjectTrx("select * from users.bo_get_user_files(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbArray == null) {
            return null;
        }
        List<LoanApplicationUserFiles> laFilesList = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            LoanApplicationUserFiles lauf = new LoanApplicationUserFiles();
            lauf.fillFromDb(dbArray.getJSONObject(i), catalogService);
            laFilesList.add(lauf);
        }
        return laFilesList;
    }

    @Override
    public List<LoanApplicationUserFiles> getLoanApplicationFiles(int loanAppId, int personId, Locale locale) {
        return getUserFiles(personId, locale).stream().filter(x -> x.getLoanApplicationId() == loanAppId).collect(Collectors.toList());
    }

    @Override
    public void updateBirthday(int personId, Date birthday) throws Exception {
        updateTrx("UPDATE person.tb_person set birthday = ? where person_id = ?",
                new SqlParameterValue(Types.DATE, birthday),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void updateNationality(int personId, Integer nationalityId) throws Exception {
        updateTrx("UPDATE person.tb_person set nationality_id = ? where person_id = ?",
                new SqlParameterValue(Types.INTEGER, nationalityId),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void updateMaritalStatus(int personId, Integer maritalStatusId) throws Exception {
        updateTrx("UPDATE person.tb_person set marital_status_id = ? where person_id = ?",
                new SqlParameterValue(Types.INTEGER, maritalStatusId),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void updateName(int personId, String name) throws Exception {
        updateTrx("UPDATE person.tb_person set person_name = ? where person_id = ?",
                new SqlParameterValue(Types.VARCHAR, name),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void updateFirstSurname(int personId, String firstSurname) throws Exception {
        updateTrx("UPDATE person.tb_person set first_surname = ? where person_id = ?",
                new SqlParameterValue(Types.VARCHAR, firstSurname),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void updateLastSurname(int personId, String lastSurname) throws Exception {
        updateTrx("UPDATE person.tb_person set last_surname = ? where person_id = ?",
                new SqlParameterValue(Types.VARCHAR, lastSurname),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void updateGender(int personId, char gender) throws Exception {
        updateTrx("UPDATE person.tb_person set gender = ? where person_id = ?",
                new SqlParameterValue(Types.CHAR, gender),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void updatePartner(int personId, Integer partnerPersonId) throws Exception {
        updateTrx("UPDATE person.tb_person set partner_id = ? where person_id = ?",
                new SqlParameterValue(Types.INTEGER, partnerPersonId),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void updateEmail(int personId, String email) throws Exception {
        updateTrx("UPDATE users.tb_user set email = ? where person_id = ?",
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void updatePhoneNumber(int userId, String countryCode, String phoneNumber) throws Exception {
        queryForObjectTrx("select * from users.ins_phone_number(?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.VARCHAR, countryCode),
                new SqlParameterValue(Types.VARCHAR, phoneNumber),
                new SqlParameterValue(Types.BOOLEAN, true));
    }

    @Override
    public void updateOcupationalActivityType(int personId, int ocupationalInformationNumber, int activityTypeId) throws Exception {
        queryForObjectTrx("select * from person.bo_update_activity_type(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, activityTypeId),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupationalCompany(int personId, int ocupationalInformationNumber, String companyName) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set company_name = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.VARCHAR, companyName),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupationalCiiu(int personId, int ocupationalInformationNumber, String ciiu) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set ciiu = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.VARCHAR, ciiu),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupationalRegime(int personId, int ocupationalInformationNumber, String regimeId) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set tax_regime = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.VARCHAR, regimeId),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupationalRuc(int personId, int ocupationalInformationNumber, String ruc) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set ruc = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.VARCHAR, ruc),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
        queryForObjectTrx("select * from person.ins_occupational_indicators(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupationalRuc(int personId, int ocupationalInformationNumber, String ruc, String taxRegime) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set ruc = ?, tax_regime = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.VARCHAR, ruc),
                new SqlParameterValue(Types.VARCHAR, taxRegime),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
        queryForObjectTrx("select * from person.ins_occupational_indicators(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupationalShareholding(int personId, int ocupationalInformationNumber, String shareholding) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set shareholding = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.VARCHAR, shareholding),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupationalResultU12M(int personId, int ocupationalInformationNumber, Integer resultU12M) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set result_last_months = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.VARCHAR, resultU12M),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

//    @Override
//    public void updateNetIncome(int personId, int ocupationalInformationNumber, Integer income) throws Exception {
//        updateTrx("UPDATE person.tb_ocupational_information set net_income = ? where person_id = ? and ocupational_information_number = ?",
//                new SqlParameterValue(Types.NUMERIC, income),
//                new SqlParameterValue(Types.INTEGER, personId),
//                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
//    }

    @Override
    public void updateFixedGrossIncome(int personId, int ocupationalInformationNumber, Integer fixedGrossIncome) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set fixed_gross_income = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.NUMERIC, fixedGrossIncome),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateFixedGrossIncomeDouble(int personId, int ocupationalInformationNumber, Double fixedGrossIncome) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set fixed_gross_income = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.NUMERIC, fixedGrossIncome),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateHasOtherIncome(int personId, int ocupationalInformationNumber, Boolean otherIncome) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set other_incomes = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.BOOLEAN, otherIncome),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateVariableGrossIncome(int personId, int ocupationalInformationNumber, Integer variableGrossIncome) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set variable_gross_income = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.NUMERIC, variableGrossIncome),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateVariableGrossIncomeDouble(int personId, int ocupationalInformationNumber, Double variableGrossIncome) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set variable_gross_income = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.NUMERIC, variableGrossIncome),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupationalAddress(int personId, int ocupationalInformationNumber, String address, Boolean isHomeAddress, String addressDepartment, String addressProvince, String addressDistrict) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set address = ? , is_home_address = ? , address_ubigeo_id = ? where person_id = ? and ocupational_information_number = ?;",
                new SqlParameterValue(Types.VARCHAR, address),
                new SqlParameterValue(Types.BOOLEAN, isHomeAddress),
                new SqlParameterValue(Types.VARCHAR, addressDepartment != null && addressProvince != null && addressDistrict != null ? addressDepartment + addressProvince + addressDistrict : null),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupationalAddress(int personId, int ocupationalInformationNumber, String address, Boolean isHomeAddress, String addressDistrict) {
        updateTrx("UPDATE person.tb_ocupational_information set address = ? , is_home_address = ? , locality_id = ? where person_id = ? and ocupational_information_number = ?;",
                new SqlParameterValue(Types.VARCHAR, address),
                new SqlParameterValue(Types.BOOLEAN, isHomeAddress),
                new SqlParameterValue(Types.BIGINT, addressDistrict),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateEmploymentTime(int personId, int ocupationalInformationNumber, String employmentTime) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set employment_time = ?, start_date = (date_trunc('month', now()) - (? || 'months')::interval)::date where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.VARCHAR, employmentTime),
                new SqlParameterValue(Types.VARCHAR, employmentTime),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateVariableIncome(int personId, int ocupationalInformationNumber, Integer variableIncome) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set variable_gross_income = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.NUMERIC, variableIncome),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateBelonging(int personId, int ocupationalInformationNumber, String belongings) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set belonging_id = ('{' || ? || '}') :: INTEGER [] where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.VARCHAR, belongings),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupatinalPhoneNumber(int personId, String phoneType, int ocupationalInformationNumber, String phoneNumber) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set phone_number_type = ?, phone_number = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.VARCHAR, phoneType),
                new SqlParameterValue(Types.VARCHAR, phoneNumber),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupatinalStartDate(int personId, int ocupationalInformationNumber, Date startDate) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set start_date = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOtherActivityTypeIncome(int personId, Integer netIncome) throws Exception {
        queryForObjectTrx("select * from person.bo_update_activity_type_others(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.NUMERIC, netIncome));
    }

    @Override
    public void updateOcupatinalServiceType(int personId, int ocupationalInformationNumber, Integer serviceTypeId) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set service_type_id = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.INTEGER, serviceTypeId),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupatinalOcupation(int personId, int ocupationalInformationNumber, Integer ocupationId) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set ocupation_id = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.INTEGER, ocupationId),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupationalSector(int personId, int ocupationalInformationNumber, String sectorId) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set sector = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.VARCHAR, sectorId),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupatinalSubActivityType(int personId, int ocupationalInformationNumber, Integer subActivityType) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set sub_activity_type_id = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.INTEGER, subActivityType),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupatinalClient1(int personId, int ocupationalInformationNumber, String client1) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set client_ruc_1 = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.VARCHAR, client1),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupatinalClient2(int personId, int ocupationalInformationNumber, String client2) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set client_ruc_2 = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.VARCHAR, client2),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupatinalLastYearSellings(int personId, int ocupationalInformationNumber, Double sellings) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set last_year_sellings = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.NUMERIC, sellings),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupatinalExerciseOutcome(int personId, int ocupationalInformationNumber, Double exerciseOutcome) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set exercise_outcome = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.NUMERIC, exerciseOutcome),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupatinalSalesPercentageFixedCosts(int personId, int ocupationalInformationNumber, Double salesPercentageFixedCosts) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set sales_percentage_fixed_costs = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.NUMERIC, salesPercentageFixedCosts),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupatinalSalesPercentageVariableCosts(int personId, int ocupationalInformationNumber, Double salesPercentageVariableCosts) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set sales_percentage_variable_costs = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.NUMERIC, salesPercentageVariableCosts),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupatinalSalesPercentageBestClient(int personId, int ocupationalInformationNumber, Double salesPercentageBestClient) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set sales_percentage_best_client = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.NUMERIC, salesPercentageBestClient),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupatinalAverageDailyIncome(int personId, int ocupationalInformationNumber, Double averageDailyIncome) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set bank_account_money = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.NUMERIC, averageDailyIncome),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateOcupatinalLastYearCompensation(int personId, int ocupationalInformationNumber, Double lastYearCompensation) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set last_year_compensation = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.NUMERIC, lastYearCompensation),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateFrequencySalary(int personId, int ocupationalInformationNumber, FrequencySalary frequencySalary) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set js_income_frequency = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.OTHER, new Gson().toJson(frequencySalary)),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }


    @Override
    public void updateRetirementDate(int personId, int ocupationalInformationNumber, Date retirementDate) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set retirement_date = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.DATE, retirementDate),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateRetirementScheme(int personId, int ocupationalInformationNumber, int retirementSchemeId) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set retirement_scheme_id = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.INTEGER, retirementSchemeId),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public Person createPerson(int documentType, String documentNumber, Locale locale) throws Exception {
        JSONObject personJson = queryForObjectTrx("select * from person.ins_person(?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber));
        Person person = new Person();
        person.fillFromDb(catalogService, personJson, catalogService, locale);
        return person;
    }

    @Override
    public void updateAddressInformation(
            int personId, String departmentId, String provinceId, String districtId, Integer streetTypeId, String streetName,
            String streetNumber, String interior, String detail, Double latitude, Double longitude) {
        queryForObjectTrx("select * from person.upd_address_information(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.VARCHAR,
                        departmentId != null && provinceId != null && districtId != null ? departmentId + provinceId + districtId : null),
                new SqlParameterValue(Types.INTEGER, streetTypeId),
                new SqlParameterValue(Types.VARCHAR, streetName),
                new SqlParameterValue(Types.VARCHAR, streetNumber),
                new SqlParameterValue(Types.VARCHAR, interior),
                new SqlParameterValue(Types.VARCHAR, detail),
                new SqlParameterValue(Types.NUMERIC, latitude),
                new SqlParameterValue(Types.NUMERIC, longitude),
                new SqlParameterValue(Types.INTEGER, districtId));
    }

    @Override
    public void updateAddressInformation(
            int personId, String districtId, Integer streetTypeId, String streetName, String streetNumber, String interior,
            String detail, Double latitude, Double longitude) {
        queryForObjectTrx("select * from person.upd_address_information(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.VARCHAR, null),
                new SqlParameterValue(Types.INTEGER, streetTypeId),
                new SqlParameterValue(Types.VARCHAR, streetName),
                new SqlParameterValue(Types.VARCHAR, streetNumber),
                new SqlParameterValue(Types.VARCHAR, interior),
                new SqlParameterValue(Types.VARCHAR, detail),
                new SqlParameterValue(Types.NUMERIC, latitude),
                new SqlParameterValue(Types.NUMERIC, longitude),
                new SqlParameterValue(Types.BIGINT, districtId));
    }

    @Override
    public List<Referral> getReferrals(int personId, Locale locale) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from person.bo_get_referrals(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbArray == null) {
            return null;
        }

        List<Referral> referrals = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Referral referral = new Referral();
            referral.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            referrals.add(referral);
        }
        return referrals;
    }

    @Override
    public List<PersonRcc> getPersonRcc(int personId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from analyst.get_person_rcc(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbArray == null) {
            return null;
        }

        List<PersonRcc> personRcc = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            JSONObject json = dbArray.getJSONObject(i);

            PersonRcc painter = new PersonRcc();
            painter.fillFromDb(json);

            personRcc.add(painter);
        }

        return personRcc;
    }

    @Override
    public List<RccCalification> getPersonRccCalification(int personId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from analyst.get_person_rcc_cal(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbArray == null) {
            return null;
        }

        List<RccCalification> rccCalifications = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            JSONObject json = dbArray.getJSONObject(i);

            RccCalification painter = new RccCalification();
            painter.fillFromDb(json);

            rccCalifications.add(painter);
        }

        return rccCalifications;
    }

    @Override
    public List<RucRccCalification> getRucRccCalification(String ruc) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from analyst.get_ruc_rcc_cal(?)", JSONArray.class,
                new SqlParameterValue(Types.VARCHAR, ruc));
        if (dbArray == null) {
            return null;
        }

        List<RucRccCalification> rccCalifications = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            JSONObject json = dbArray.getJSONObject(i);

            RucRccCalification painter = new RucRccCalification();
            painter.fillFromDb(json);

            rccCalifications.add(painter);
        }

        return rccCalifications;
    }

    @Override
    public List<RucRcc> getRucRcc(String ruc) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from analyst.get_ruc_rcc(?)", JSONArray.class,
                new SqlParameterValue(Types.VARCHAR, ruc));
        if (dbArray == null) {
            return null;
        }

        List<RucRcc> businessRcc = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            JSONObject json = dbArray.getJSONObject(i);

            RucRcc painter = new RucRcc();
            painter.fillFromDb(json);

            businessRcc.add(painter);
        }

        return businessRcc;
    }

    @Override
    public Reniec getReniecDBData(String docNumber) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from reniec.get_person_reniec(?)", JSONArray.class,
                new SqlParameterValue(Types.VARCHAR, docNumber));
        if (dbArray == null) {
            return null;
        }

        for (int i = 0; i < dbArray.length(); i++) {
            Reniec reniec = new Reniec();
            reniec.fillFromDb(dbArray.getJSONObject(i), catalogService);
            return reniec;
        }
        return null;
    }

    @Override
    public void validateOcupationalInformation(int personId, boolean validated) throws Exception {
        queryForObjectTrx("select * from person.validate_ocupational_information(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.BOOLEAN, validated));
    }

    @Override
    public List<GatewayContacts> getCollectionContacts(int personId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.bo_get_collection_contacts(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbArray == null) {
            return null;
        }
        List<GatewayContacts> gatewayContactsBoPainters = new ArrayList<>();

        for (int i = 0; i < dbArray.length(); i++) {
            GatewayContacts gatewayContactsBoPainter = new GatewayContacts();
            gatewayContactsBoPainter.fillFromDb(dbArray.getJSONObject(i), catalogService);
            gatewayContactsBoPainters.add(gatewayContactsBoPainter);
        }
        return gatewayContactsBoPainters;
    }

    @Override
    public void updateReferralFulName(int referralId, String fullName) throws Exception {
        updateTrx("UPDATE person.tb_referral set full_name = ? where referral_id = ?",
                new SqlParameterValue(Types.VARCHAR, fullName),
                new SqlParameterValue(Types.INTEGER, referralId));
    }

    @Override
    public void updateReferralRelationship(int referralId, int relationshipId) throws Exception {
        updateTrx("UPDATE person.tb_referral set relationship_id = ? where referral_id = ?",
                new SqlParameterValue(Types.INTEGER, relationshipId),
                new SqlParameterValue(Types.INTEGER, referralId));
    }

    @Override
    public void updateReferralPhoneNumber(int referralId, String phoneNumber) throws Exception {
        updateTrx("UPDATE person.tb_referral set phone_number = ? where referral_id = ?",
                new SqlParameterValue(Types.VARCHAR, phoneNumber),
                new SqlParameterValue(Types.INTEGER, referralId));
    }

    @Override
    public void updateReferralPhoneType(int referralId, String phoneType) throws Exception {
        updateTrx("UPDATE person.tb_referral set phone_number_type = ? where referral_id = ?",
                new SqlParameterValue(Types.VARCHAR, phoneType),
                new SqlParameterValue(Types.INTEGER, referralId));
    }

    @Override
    public void updateReferralInfo(int referralId, String info) throws Exception {
        updateTrx("UPDATE person.tb_referral set referral_info = ? where referral_id = ?",
                new SqlParameterValue(Types.VARCHAR, info),
                new SqlParameterValue(Types.INTEGER, referralId));
    }

    @Override
    public void updateReferralValidated(int referralId, boolean validated) throws Exception {
        updateTrx("UPDATE person.tb_referral set is_validated = ? where referral_id = ?",
                new SqlParameterValue(Types.BOOLEAN, validated),
                new SqlParameterValue(Types.INTEGER, referralId));
    }

    @Override
    public void updateDocumentType(int referralId, Integer documentTypeId) throws Exception {
        updateTrx("UPDATE person.tb_referral set referral_document_id = ? where referral_id = ?",
                new SqlParameterValue(Types.INTEGER, documentTypeId),
                new SqlParameterValue(Types.INTEGER, referralId));
    }

    @Override
    public void updateDocumentNumber(int referralId, String documentNumber) throws Exception {
        updateTrx("UPDATE person.tb_referral set referral_document_number = ? where referral_id = ?",
                new SqlParameterValue(Types.INTEGER, documentNumber),
                new SqlParameterValue(Types.INTEGER, referralId));
    }

    @Override
    public Referral createReferral(Integer personId, String phoneType, String fullName, Integer relationshipId, String countryCode, String phoneNumber, String info, Locale locale) throws Exception {
        JSONObject personJson = queryForObjectTrx("select * from person.ins_referral(?, ?, ?, ?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, relationshipId),
                new SqlParameterValue(Types.VARCHAR, fullName),
                new SqlParameterValue(Types.VARCHAR, countryCode),
                new SqlParameterValue(Types.VARCHAR, phoneNumber),
                new SqlParameterValue(Types.VARCHAR, info),
                new SqlParameterValue(Types.VARCHAR, phoneType));
        Referral referral = new Referral();
        referral.fillFromDb(personJson, catalogService, locale);
        return referral;
    }

    @Override
    public void deletePreviousReferrals(Integer personId) throws Exception {
        updateTrx("delete from person.tb_referral where person_id = ?",
                new SqlParameterValue(Types.INTEGER, personId)
        );
    }

    @Override
    public void updatePersonBankAccountInformation(Integer personId, Integer bankId, Character accountType, String account, String ubigeo, String cci) throws Exception {
        queryForObjectTrx("select * from person.upd_account_number(?, ?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, bankId),
                new SqlParameterValue(Types.CHAR, accountType),
                new SqlParameterValue(Types.VARCHAR, account),
                new SqlParameterValue(Types.VARCHAR, ubigeo),
                new SqlParameterValue(Types.VARCHAR, cci));
    }

    @Override
    public void updateBranchOfficeCode(int personId, String branchOfficeCode) throws Exception {
        updateTrx("UPDATE person.tb_additional_information set branch_office_code = ? where person_id = ?",
                new SqlParameterValue(Types.VARCHAR, branchOfficeCode),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void updateRandomPartner(int personId, String randomPartners) throws Exception {
        updateTrx("UPDATE person.tb_person set js_random_partners = ? where person_id = ?",
                new SqlParameterValue(Types.OTHER, randomPartners),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void updatePartnerValidated(int personId, Boolean validated) throws Exception {
        updateTrx("UPDATE person.tb_person set il_validated_partner = ? where person_id = ?",
                new SqlParameterValue(Types.BOOLEAN, validated),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public <T> T getBureauResult(Integer personId, Class<T> klass) throws Exception {
        String bureauResult = queryForObjectTrx("select * from credit.bo_get_last_equifax_result(?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId));

        if (bureauResult != null) {
            return new Marshall().unmarshall(bureauResult, klass);
        }
        return null;
    }

    @Override
    public List<Employee> getEmployeesByDocument(Integer docType, String docNumber, Locale locale) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from person.get_employee(?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, 1),
                new SqlParameterValue(Types.VARCHAR, docType),
                new SqlParameterValue(Types.VARCHAR, docNumber));
        if (dbJson == null)
            return null;

        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            Employee employee = new Employee();
            employee.fillFromDb(dbJson.getJSONObject(i), catalogService, locale);
            employees.add(employee);
        }
        return employees;
    }

    @Override
    public <T extends Employee> List<T> getEmployeesBySlug(int employerUserId, String slug, Boolean isActive, Locale locale, Class<T> returntype) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from person.employee_search(?,?,?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, employerUserId),
                new SqlParameterValue(Types.VARCHAR, slug),
                new SqlParameterValue(Types.BOOLEAN, isActive));
        if (dbArray == null)
            return null;

        List<T> employees = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            T employee = returntype.getConstructor().newInstance();
            employee.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            employees.add(employee);
        }
        return employees;
    }

    @Override
    public List<Employee> getEmployeesByEmail(String email, Locale locale) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from person.get_employee(?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, 2),
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.VARCHAR, null));
        if (dbJson == null)
            return null;

        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            Employee employee = new Employee();
            employee.fillFromDb(dbJson.getJSONObject(i), catalogService, locale);
            employees.add(employee);
        }
        return employees;
    }

    @Override
    public Employee getEmployeeByPerson(int personId, Integer employerId, Locale locale) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from person.get_employee(?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, 3),
                new SqlParameterValue(Types.VARCHAR, personId),
                new SqlParameterValue(Types.VARCHAR, employerId));
        if (dbJson == null)
            return null;

        Employee employee = new Employee();
        employee.fillFromDb(dbJson.getJSONObject(0), catalogService, locale);
        return employee;
    }

    @Override
    public Employee getEmployeeByPerson(int personId, Locale locale) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from person.get_employee(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null)
            return null;

        Employee employee = new Employee();
        employee.fillFromDb(dbJson.getJSONObject(0), catalogService, locale);
        return employee;
    }

    @Override
    public Employee getEmployeeById(int employeeId, Locale locale) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from person.get_employee(?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, 4),
                new SqlParameterValue(Types.VARCHAR, employeeId),
                new SqlParameterValue(Types.VARCHAR, null));
        if (dbJson == null)
            return null;

        Employee employee = new Employee();
        employee.fillFromDb(dbJson.getJSONObject(0), catalogService, locale);
        return employee;
    }

    @Override
    public void updateEmployeePersonOnly(int employeeId, int personId) throws Exception {
        queryForObjectTrx("select * from person.update_employee_person_only(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, employeeId),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void updateEmployeePerson(int employeeId, int personId) throws Exception {
        queryForObjectTrx("select * from person.update_employee_person(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, employeeId),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public List<EntityConsolidableDebt> getConsolidableDebts(int docType, String docNumber, Boolean isConsolidable) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from sysrcc.get_consolidable_debts(?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, docType),
                new SqlParameterValue(Types.VARCHAR, docNumber),
                new SqlParameterValue(Types.BOOLEAN, isConsolidable));
        if (dbArray == null)
            return new ArrayList<>();
        List<EntityConsolidableDebt> debts = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            EntityConsolidableDebt debt = new EntityConsolidableDebt();
            debt.fillFromDb(dbArray.getJSONObject(i), catalogService);
            debts.add(debt);
        }
        return debts;
    }

    @Override
    public void validateAssociated(int personId, int entityId, boolean isValidated) throws Exception {
        queryForObjectTrx("select * from person.validate_associated(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.BOOLEAN, isValidated));
    }

    @Override
    public PersonAssociated getAssociated(int personId, int entityId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.get_associated(?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, entityId));
        if (dbJson == null)
            return null;

        PersonAssociated associated = new PersonAssociated();
        associated.fillFromDb(dbJson, catalogService);
        return associated;
    }

    @Override
    public Object registerAssociated(int personId, int entityId, String associatedId, String passbookNumber) throws Exception {
        return queryForObjectTrx("select * from person.register_associated(?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.VARCHAR, associatedId),
                new SqlParameterValue(Types.VARCHAR, passbookNumber));
    }

    @Override
    public void registerNegativeBase(Integer personId) throws Exception {
        queryForObjectTrx("select * from person.ins_negative_base(?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void updateHousingType(int personId, Integer housingTypeId) throws Exception {
        updateTrx("UPDATE person.tb_address set housing_type_id = ? where person_id = ?",
                new SqlParameterValue(Types.INTEGER, housingTypeId),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void updateResidenceTime(int personId, int residenceTimeInMonths) throws Exception {
        updateTrx("UPDATE person.tb_address set residence_time = ? where person_id = ?",
                new SqlParameterValue(Types.INTEGER, residenceTimeInMonths),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void updateFixedGrossIncomeIncreasal(int personId, int number, Integer fixedGrossIncomeIncreasal) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set fixed_gross_income_increasal = ? WHERE person_id = ? AND ocupational_information_number = ?",
                new SqlParameterValue(Types.INTEGER, fixedGrossIncomeIncreasal),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, number));
    }

    @Override
    public void updateClient1Ruc65(int personId, int number, Boolean client1Ruc65) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set client_1_65_sellings = ? WHERE person_id = ? AND ocupational_information_number = ?",
                new SqlParameterValue(Types.BOOLEAN, client1Ruc65),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, number));
    }

    @Override
    public void updateLastYearSellingsIncreasal(int personId, int number, Integer lastYearSellingsIncreasal) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set last_year_sellings_increasal = ? WHERE person_id = ? AND ocupational_information_number = ?",
                new SqlParameterValue(Types.INTEGER, lastYearSellingsIncreasal),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, number));
    }

    @Override
    public void updateExerciseOutcomeIncreasal(int personId, int number, Integer exerciseOutcomeIncreasal) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set exercise_outcome_increasal = ? WHERE person_id = ? AND ocupational_information_number = ?",
                new SqlParameterValue(Types.INTEGER, exerciseOutcomeIncreasal),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, number));
    }

    @Override
    public void updatesalesPercentageBestClientIndicator(int personId, int number, Integer salesPercentageBestClientIndicator) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set sales_percentage_best_client_indicator = ? WHERE person_id = ? AND ocupational_information_number = ?",
                new SqlParameterValue(Types.INTEGER, salesPercentageBestClientIndicator),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, number));
    }

    @Override
    public void updatePersonAccountByCredit(Integer personId, Integer creditId, Integer bankId, String account, Character accountType, String cci) throws Exception {
        queryForObjectTrx("select * from person.update_person_account_credit(?, ?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.INTEGER, bankId),
                new SqlParameterValue(Types.VARCHAR, account),
                new SqlParameterValue(Types.CHAR, accountType),
                new SqlParameterValue(Types.VARCHAR, cci));
    }

    @Override
    public void updateStudyLevel(int personId, Integer studyLevelId) throws Exception {
        updateTrx("UPDATE person.tb_person set study_level_id = ? where person_id = ?",
                new SqlParameterValue(Types.INTEGER, studyLevelId),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void updateProfession(int personId, Integer professionId) throws Exception {
        updateTrx("UPDATE person.tb_person set profession_id = ? where person_id = ?",
                new SqlParameterValue(Types.INTEGER, professionId),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void cleanOcupationalInformation(int personId, int ocupatinalInformationNumber) {
        queryForObjectTrx("select * from person.clean_ocupational_information(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupatinalInformationNumber));
    }

    @Override
    public void updateConcludedStudies(int personId, Boolean completed) {
        updateTrx("UPDATE person.tb_person set concluded_studies = ? where person_id = ?",
                new SqlParameterValue(Types.BOOLEAN, completed),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    public List<PersonValidator> validatePerson(Locale locale, int employerId, String json) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from person.validate_js_employees(?,?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, employerId),
                new SqlParameterValue(Types.OTHER, json));

        if (dbJson == null) {
            return null;
        }

        List<PersonValidator> personValidators = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            PersonValidator personValidator = new PersonValidator();
            personValidator.fillFromDb(dbJson.getJSONObject(i), locale);
            personValidators.add(personValidator);
        }
        return personValidators;
    }

    @Override
    public List<PersonContactInformation> getHistoricAddressByPerson(int personId, Locale locale) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from person.get_ht_address_credit(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId));

        if (dbJson == null) {
            return null;
        }

        List<PersonContactInformation> personAddresses = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            PersonContactInformation personAddress = new PersonContactInformation();
            personAddress.fillFromDb(dbJson.getJSONObject(i), catalogService, locale);
            personAddresses.add(personAddress);
        }
        return personAddresses;
    }

    @Override
    public Double getMonthlyInstallmentMortage(String docNumber) {
        return queryForObjectTrx("select * from sysrcc.get_monthly_installment_mortgage(?)", Double.class,
                new SqlParameterValue(Types.VARCHAR, docNumber));
    }

    @Override
    public void updateDependents(int personId, Integer dependents) {
        updateTrx("UPDATE person.tb_person set dependents = ? where person_id = ?",
                new SqlParameterValue(Types.INTEGER, dependents),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void updatePepInformation(int personId, Boolean pep, String pepDetail) {
        updateTrx("UPDATE person.tb_person set pep = ?, pep_detail = ? where person_id = ?",
                new SqlParameterValue(Types.BOOLEAN, pep),
                new SqlParameterValue(Types.VARCHAR, pepDetail),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public boolean isDocumentValidForEntity(int entityId, int productCategoryId, int docType, String docNumber) {
        return queryForObjectTrx("select * from originator.is_valid_document_for_entity(?, ?, ?, ?)", Boolean.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productCategoryId),
                new SqlParameterValue(Types.INTEGER, docType),
                new SqlParameterValue(Types.VARCHAR, docNumber));
    }

    @Override
    public void registerDisgregatedAddress(Integer personId, Direccion direccion) {
        queryForObjectTrx("select * from person.register_disaggregated_address(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.CHAR, direccion.getTipoDomicilio()),
                new SqlParameterValue(Types.INTEGER, direccion.getTipoVia()),
                new SqlParameterValue(Types.VARCHAR, direccion.getNombreVia()),
                new SqlParameterValue(Types.VARCHAR, direccion.getNumeroVia()),
                new SqlParameterValue(Types.VARCHAR, direccion.getManzana()),
                new SqlParameterValue(Types.VARCHAR, direccion.getLote()),
                new SqlParameterValue(Types.INTEGER, direccion.getTipoInterior()),
                new SqlParameterValue(Types.VARCHAR, direccion.getNumeroInterior()),
                new SqlParameterValue(Types.INTEGER, direccion.getTipoZona()),
                new SqlParameterValue(Types.VARCHAR, direccion.getNombreZona()),
                new SqlParameterValue(Types.VARCHAR, direccion.getReferencia()),
                new SqlParameterValue(Types.VARCHAR, direccion.getBloque()),
                new SqlParameterValue(Types.VARCHAR, direccion.getKilometro()),
                new SqlParameterValue(Types.VARCHAR, direccion.getBarrio()),
                new SqlParameterValue(Types.VARCHAR, direccion.getEtapa()),
                new SqlParameterValue(Types.VARCHAR, direccion.getGrupo()),
                new SqlParameterValue(Types.VARCHAR, direccion.getSector()),
                new SqlParameterValue(Types.VARCHAR, direccion.getParcela()),
                new SqlParameterValue(Types.VARCHAR, direccion.getUbigeo()),
                new SqlParameterValue(Types.BIGINT, direccion.getDistrictId()),
                new SqlParameterValue(Types.VARCHAR, direccion.getFloor()),
                new SqlParameterValue(Types.BOOLEAN, direccion.getWithoutNumber()));
    }

    @Override
    public Direccion getDisggregatedAddress(Integer personId, String tipoDomicilio) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.get_disaggregated_address(?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.VARCHAR, tipoDomicilio));
        if (dbJson == null)
            return null;
        Direccion direccion = new Direccion();
        direccion.fillFromDb(dbJson, catalogService, translatorDAO, Configuration.getDefaultLocale());
        return direccion;
    }

    @Override
    public int hasDisggregatedAddress(Integer personId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from person.get_disaggregated_address(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbArray == null)
            return 0;
        return dbArray.length();
    }

    @Override
    public List<DisggregatedAddress> getDisggregatedAddress(Integer personId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from person.get_disaggregated_address(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbArray == null)
            return null;

        List<DisggregatedAddress> disggregatedAddresses = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            DisggregatedAddress disggregatedAddress = new DisggregatedAddress();
            disggregatedAddress.fillFromDb(dbArray.getJSONObject(i), catalogService);
            disggregatedAddresses.add(disggregatedAddress);
        }
        return disggregatedAddresses;

    }

    @Override
    public Double getExternalGrossIncome(Integer personId) throws Exception {
        return queryForObjectTrx("select * from person.get_external_gross_income(?)", Double.class,
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public List<PreApprovedInfo> getPreApprovedDataByDocument(Integer productId, Integer documentType, String documentNumber) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from originator.get_person_from_approved_data(?,?,?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.INTEGER, productId));
        if (dbArray == null)
            return null;

        List<PreApprovedInfo> preApprovedInfos = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            PreApprovedInfo preApprovedInfo = new PreApprovedInfo();
            preApprovedInfo.fillFromDb(dbArray.getJSONObject(i), catalogService, Configuration.getDefaultLocale());
            preApprovedInfos.add(preApprovedInfo);
        }
        return preApprovedInfos;
    }

    @Override
    public List<PreApprovedInfo> getPreApprovedData(Integer documentType, String documentNumber) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from originator.get_person_from_approved_data(?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber));
        if (dbArray == null)
            return new ArrayList<>();

        List<PreApprovedInfo> preApprovedInfos = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            PreApprovedInfo preApprovedInfo = new PreApprovedInfo();
            preApprovedInfo.fillFromDb(dbArray.getJSONObject(i), catalogService, Configuration.getDefaultLocale());
            preApprovedInfos.add(preApprovedInfo);
        }
        return preApprovedInfos;
    }

    @Override
    public void deletePreApprovedBase(Integer entityId, Integer productId, Integer documentType, String documentNumber) {
        queryForObjectTrx("select * from originator.del_approved_data(?,?,?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId),
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber));
    }

    @Override
    public void deletePreApprovedLoanApplicationBase(Integer loanApplicationId) {
        updateTrx("delete from originator.tb_approved_data_loan_application where loan_application_id = ?",
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
    }


    @Override
    public void registerRejection(Integer entityId, Integer documentType, String documentNumber, String evaluationResult, String rejectionCode, String rejectionDescription) {
        queryForObjectTrx("select * from originator.register_approved_data_rejection(?,?,?,?,?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.VARCHAR, evaluationResult),
                new SqlParameterValue(Types.VARCHAR, rejectionCode),
                new SqlParameterValue(Types.VARCHAR, rejectionDescription));
    }

    @Override
    public void registerPreApprovedBase(Integer entityId, Integer productId, Integer documentType, String documentNumber,
                                        Double maxAmount, Integer maxInstallments, Double tea, String cardType, String cardNumber,
                                        Integer paymentDay, Integer billingClose, String cluster) {
        queryForObjectTrx("select * from originator.ins_approved_data(?,?,?,?,?,?,?,?,?,?,?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId),
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.NUMERIC, maxAmount),
                new SqlParameterValue(Types.INTEGER, maxInstallments),
                new SqlParameterValue(Types.NUMERIC, tea),
                new SqlParameterValue(Types.VARCHAR, cardType),
                new SqlParameterValue(Types.VARCHAR, cardNumber),
                new SqlParameterValue(Types.INTEGER, paymentDay),
                new SqlParameterValue(Types.INTEGER, billingClose),
                new SqlParameterValue(Types.VARCHAR, cluster));
    }

    @Override
    public void registerPreApprovedBaseByEntityProductParameter(Integer entityId, Integer productId, Integer documentType, String documentNumber,
                                        Double maxAmount, Integer maxInstallments, Double tea, String cardType, String cardNumber,
                                        Integer paymentDay, Integer billingClose, String cluster, String entityProductParameter) {
        queryForObjectTrx("select * from originator.ins_approved_data(?,?,?,?,?,?,?,?,?,?,?,?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId),
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.NUMERIC, maxAmount),
                new SqlParameterValue(Types.INTEGER, maxInstallments),
                new SqlParameterValue(Types.NUMERIC, tea),
                new SqlParameterValue(Types.VARCHAR, cardType),
                new SqlParameterValue(Types.VARCHAR, cardNumber),
                new SqlParameterValue(Types.INTEGER, paymentDay),
                new SqlParameterValue(Types.INTEGER, billingClose),
                new SqlParameterValue(Types.VARCHAR, cluster),
                new SqlParameterValue(Types.VARCHAR, entityProductParameter));
    }

    @Override
    public void insertPreApprovedBaseByEntityProductParameter(Integer entityId, Integer productId, Integer documentType, String documentNumber,
                                        Double maxAmount, Integer maxInstallments, Double tea, String cardType, String cardNumber,
                                        Integer paymentDay, Integer billingClose, String cluster, String entityProductParameter) {
        updateTrx("INSERT INTO originator.tb_approved_data (entity_id, product_id, document_type_id, document_number, max_amount," +
                        "                                           max_installments, effective_annual_rate, card_type, card_number, payment_day," +
                        "                                           billing_close, is_active, cluster, ar_entity_product_parameter_id)" +
                        "  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TRUE, ?, ?::integer[]);",
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId),
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.NUMERIC, maxAmount),
                new SqlParameterValue(Types.INTEGER, maxInstallments),
                new SqlParameterValue(Types.NUMERIC, tea),
                new SqlParameterValue(Types.VARCHAR, cardType),
                new SqlParameterValue(Types.VARCHAR, cardNumber),
                new SqlParameterValue(Types.INTEGER, paymentDay),
                new SqlParameterValue(Types.INTEGER, billingClose),
                new SqlParameterValue(Types.VARCHAR, cluster),
                new SqlParameterValue(Types.VARCHAR, entityProductParameter));
    }

    @Override
    public SunatResult getSunatResult(int personId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.bo_get_sunat(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return null;
        }

        SunatResult sunat = new SunatResult();
        sunat.fillFromDb(dbJson);
        return sunat;
    }

    @Override
    public SunatResult getSunatResultByRuc(String ruc) throws Exception {
        JSONObject dbJson = queryForObjectExternal("select * from external.get_sunat_resutl(?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, ruc));
        if (dbJson == null) {
            return null;
        }

        SunatResult sunat = new SunatResult();
        sunat.fillFromDb(dbJson);
        return sunat;
    }


    @Override
    public void registerAbandonedApplication(String email) {
        queryForObjectTrx("select * from support.reister_bandoned_applications_email(?)", String.class,
                new SqlParameterValue(Types.VARCHAR, email));
    }

    @Override
    public Direccion getDisaggregatedHomeAddressByCredit(int personId, int creditId) throws Exception {
        return getDisaggregatedAddressByCredit(personId, creditId, 'H');
    }

    @Override
    public Direccion getDisaggregatedJobAddressByCredit(int personId, int creditId) throws Exception {
        return getDisaggregatedAddressByCredit(personId, creditId, 'J');
    }

    private Direccion getDisaggregatedAddressByCredit(int personId, int creditId, char type) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.get_disaggregated_address_credit(?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.CHAR, type));
        if (dbJson == null)
            return null;

        Direccion direccion = new Direccion();
        direccion.fillFromDb(dbJson, catalogService, translatorDAO, Configuration.getDefaultLocale());
        return direccion;
    }

    @Override
    public EssaludResult getEssaludResult(int personId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.bo_get_essalud(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return null;
        }

        EssaludResult essalud = new EssaludResult();
        essalud.fillFromDb(dbJson);
        return essalud;
    }

    @Override
    public Date getExternalEssaludStartDate(int personId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.get_external_essalud(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return null;
        }

        return JsonUtil.getPostgresDateFromJson(dbJson, "inicio", null);
    }

    @Override
    public void updateOcupationalInformationBoChanged(int personId, int ocupationalInformationNumber, boolean changed) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set bo_changed = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.BOOLEAN, changed),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public AfipResult getAfipResult(int personId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.bo_get_afip(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return null;
        }

        AfipResult afipResult = new AfipResult();
        afipResult.fillFromDb(dbJson);
        return afipResult;
    }

    @Override
    public BcraResult getBcraResult(int personId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.bo_get_bcra(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return null;
        }

        BcraResult bcraResult = new BcraResult();
        bcraResult.fillFromDb(dbJson);
        return bcraResult;
    }

    @Override
    public AnsesResult getAnsesResult(int personId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.bo_get_anses(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return null;
        }

        AnsesResult ansesResult = new AnsesResult();
        ansesResult.fillFromDb(dbJson);
        return ansesResult;
    }


    @Override
    public void registerAddressCoordinates(Integer personId, Direccion direccion) {
        queryForObjectTrx("select * from person.upd_address_coordinates(?,?,?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.NUMERIC, direccion.getLatitude()),
                new SqlParameterValue(Types.NUMERIC, direccion.getLongitude()),
                new SqlParameterValue(Types.VARCHAR, direccion.getSearchQuery())
        );
    }

    ;

    @Override
    public void registerJobAddressCoordinates(Integer personId, Integer ocupationalInformationNumber, Direccion direccion) {
        queryForObjectTrx("select * from person.upd_ocupational_information_address_coordinates(?,?,?,?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber),
                new SqlParameterValue(Types.NUMERIC, direccion.getLatitude()),
                new SqlParameterValue(Types.NUMERIC, direccion.getLongitude()),
                new SqlParameterValue(Types.VARCHAR, direccion.getSearchQuery())
        );
    }

    ;

    @Override
    public Direccion completeAddressCoordinates(Integer personId, Direccion direccion) {
        JSONObject dbJson = queryForObjectTrx("select * from person.get_address(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null)
            return null;
        direccion.fillCoordinatesFromDb(dbJson, direccion);
        return direccion;
    }

    @Override
    public StaticDBInfo getInfoFromStaticDB(Integer personId) throws Exception {
        StaticDBInfo staticDBInfo = new StaticDBInfo();
        JSONObject dbJson = queryForObjectTrx("select * from bases.get_essalud_info(?);", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null)
            return null;
        staticDBInfo.fillFromDB(dbJson);
        return staticDBInfo;
    }

    @Override
    public RucInfo getRucInfo(String ruc) throws Exception {
        RucInfo rucInfo = new RucInfo();
        JSONObject dbJson = queryForObjectExternal("select * from external.get_ruc_info(?);", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, ruc));
        if (dbJson == null)
            return null;
        rucInfo.fillFromDB(dbJson);
        return rucInfo;
    }

    @Override
    public void updateBirthUbigeo(int personId, String ubigeo) throws Exception {
        updateTrx("UPDATE person.tb_person set birth_ubigeo_id = ? where person_id = ?",
                new SqlParameterValue(Types.VARCHAR, ubigeo),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public void updatebankAccountVerified(Boolean verified, Integer personId) throws Exception {
        updateTrx("UPDATE person.tb_additional_information set bank_account_verified = ? where person_id = ?",
                new SqlParameterValue(Types.BOOLEAN, verified),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public OnpeResult getOnpeResult(int personId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.bo_get_onpe(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return null;
        }

        OnpeResult onpeResult = new OnpeResult();
        onpeResult.fillFromDb(dbJson);
        return onpeResult;
    }

    @Override
    public Boolean isRawAssociated(int personId, int entityId) throws Exception {
        Boolean result = queryForObjectTrx("select * from person.is_raw_associated(?, ?)", Boolean.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, personId));
        return result;
    }

    @Override
    public JSONObject searchPersonByNames(String name, String firstSurname, String secondSurname, Character gender, Integer month, Integer day) throws Exception {
        return queryForObjectTrx("select * from reniec.search_person_by_name(?,?,?,?,?,?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, name),
                new SqlParameterValue(Types.VARCHAR, firstSurname),
                new SqlParameterValue(Types.VARCHAR, secondSurname),
                new SqlParameterValue(Types.CHAR, gender),
                new SqlParameterValue(Types.INTEGER, month),
                new SqlParameterValue(Types.INTEGER, day));
    }

    @Override
    public void updatePensionerIncome(int personId, int ocupationalInformationNumber, Integer income, Integer scheme, Date retDate) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set fixed_gross_income = ? , retirement_scheme_id = ? , retirement_date = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.NUMERIC, income),
                new SqlParameterValue(Types.INTEGER, scheme),
                new SqlParameterValue(Types.DATE, retDate),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber)
        );
    }
    @Override
    public void updateCustomProfession(int personId, Integer customProfessionId) throws Exception {
        updateTrx("UPDATE person.tb_person set custom_profession_id = ? where person_id = ?",
                new SqlParameterValue(Types.INTEGER, customProfessionId),
                new SqlParameterValue(Types.INTEGER, personId));
    }

    @Override
    public List<PersonDisqualifier> getPersonDisqualifierByPersonId(Integer personId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from person.get_discualifier_by_person_id(?) ", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbArray == null) {
            return null;
        }
        List<PersonDisqualifier> personDisqualifiers = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            PersonDisqualifier personDisqualifier = new PersonDisqualifier();
            personDisqualifier.fillFromDb(dbArray.getJSONObject(i));
            personDisqualifiers.add(personDisqualifier);
        }
        return personDisqualifiers;
    }


    @Override
    public void savePersonDisqualifier(int personId, String type, boolean isDisquilified, String detail, int userFileId) throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String now = dateFormat.format(date);

        String queryInsert = "insert into person.tb_disqualifier (person_id,type,disqualifier,detail,user_files_id,register_date) values(?,?,?,?,?,?)";
        String storeProcedInsert = "insert into person.ins_discualifier(person_id, type, disqualifier, detail, user_files_id, register_date) values(?,?,?,?,?,?)";

        updateTrx(queryInsert,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.VARCHAR, type),
                new SqlParameterValue(Types.BOOLEAN, isDisquilified),
                new SqlParameterValue(Types.VARCHAR, detail),
                new SqlParameterValue(Types.INTEGER, userFileId),
                new SqlParameterValue(Types.TIMESTAMP, now)
        );
    }


    @Override
    public void updatePersonDisqualifier(int personId, boolean isDisquilified, String detail, int userFileId, String type) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String now = dateFormat.format(date);

        String queryUpdate = "UPDATE person.tb_disqualifier set disqualifier = ?,detail = ?,user_files_id = ?,register_date = ? where person_id = ? and type = ? ";
        updateTrx(queryUpdate,
                new SqlParameterValue(Types.BOOLEAN, isDisquilified),
                new SqlParameterValue(Types.VARCHAR, detail),
                new SqlParameterValue(Types.INTEGER, userFileId),
                new SqlParameterValue(Types.TIMESTAMP, now),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.VARCHAR, type)
        );
    }

    @Override
    public void updatePersonDisqualifierNoImage(int personId, boolean isDisquilified, String detail, String type) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String now = dateFormat.format(date);

        String queryUpdate = "UPDATE person.tb_disqualifier set disqualifier = ?,detail = ?,register_date = ? where person_id = ? and type = ? ";
        updateTrx(queryUpdate,
                new SqlParameterValue(Types.BOOLEAN, isDisquilified),
                new SqlParameterValue(Types.VARCHAR, detail),
                new SqlParameterValue(Types.TIMESTAMP, now),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.VARCHAR, type)
        );
    }

    @Override
    public void registerEmailToBlacklist(String email) throws Exception {
        queryForObjectTrx("select * from person.register_email_to_blacklist(?)", String.class,
                new SqlParameterValue(Types.VARCHAR, email));
    }

    @Override
    public List<PersonRawAssociated> getPersonRawAssociatedByDocument(int documentType, String documentNumber) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select person.get_raw_associateds(?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber));

        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<PersonRawAssociated> personRawAssociatedList = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            JSONObject json = dbArray.getJSONObject(i);

            PersonRawAssociated personRawAssociated = new PersonRawAssociated();
            personRawAssociated.fillFromDb(json, catalogService);

            personRawAssociatedList.add(personRawAssociated);
        }

        return personRawAssociatedList;
    }

    @Override
    public List<CendeuResult> getCendeuResult(String documentNumber) throws Exception {
        JSONArray dbArray = queryForObject("select * from cendeu.get_cendeu(?)", JSONArray.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentNumber));

        if (dbArray == null)
            return new ArrayList<>();

        List<CendeuResult> cendeuResultList = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            JSONObject json = dbArray.getJSONObject(i);
            CendeuResult cendeuResult = new CendeuResult();
            cendeuResult.fillFromDb(json);
            cendeuResultList.add(cendeuResult);
        }
        return cendeuResultList;
    }

    @Override
    public void migrateFromApprovedData(int loanApplicationId, Integer entityId, Integer productId, Integer docType, String docNumber) throws Exception {
        queryForObjectTrx("select * from originator.migrate_from_approved_date(?, ?, ?, ?, ?);", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId),
                new SqlParameterValue(Types.INTEGER, docType),
                new SqlParameterValue(Types.VARCHAR, docNumber));
    }

    @Override
    public void updateLandline(int personId, String landline) throws Exception {
        updateTrx("UPDATE person.tb_person set landline = ? where person_id = ?",
                new SqlParameterValue(Types.VARCHAR, landline),
                new SqlParameterValue(Types.INTEGER, personId)
        );
    }

    @Override
    public String getPadronAfipFullName(String docNumber) throws Exception {
        return queryForObject("select * from cendeu.get_padron_name(?);", String.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, docNumber));
    }
    @Override
    public void updateOrInsertPersonDisqualifierByPersonIdOnly(int personId, String type, Boolean isDisqualifed, String detail){

        String queryUpdate = "select * from person.ins_tb_disqualifier(?, ?, ?, ?, ?)";
        queryForObjectTrx(queryUpdate,String.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.VARCHAR, type),
                new SqlParameterValue(Types.BOOLEAN, isDisqualifed),
                new SqlParameterValue(Types.VARCHAR, detail),
                new SqlParameterValue(Types.INTEGER, null)
        );
    }

    @Override
    public BancoDelSolCsvInfo getBancoDelSolEmploymentDetailsByPersonId (Integer loanApplicationId) throws Exception {
        JSONObject  jsonObject = queryForObjectTrx("select * from person.get_bds_csv_info(?);",JSONObject.class, new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if(jsonObject == null)
            return null;

        BancoDelSolCsvInfo result = new BancoDelSolCsvInfo();
        result.fillFromDb(jsonObject);
        return result;
    }

    @Override
    public JSONObject getBancoDelSolEmployeeHierarchy(Integer userEntityId) {
        JSONObject jsonObject = queryForObjectTrx("select * from person.get_bds_hierarchy_info(?)", JSONObject.class, new SqlParameterValue(Types.INTEGER, userEntityId));
        if (jsonObject == null)
            return null;

        return jsonObject;
    }

    @Override
    public void registerProfessionOccupation(int personId, int professionOccupationId) throws Exception {
        updateTrx("UPDATE person.tb_person set profession_occupation_id=? where person_id=?",
                new SqlParameterValue(Types.INTEGER, professionOccupationId),
                new SqlParameterValue(Types.INTEGER, personId)
        );
    }

    @Override
    public MigracionesResult getMigraciones(Integer personId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.bo_get_migraciones(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return null;
        }

        MigracionesResult mig = new MigracionesResult();
        mig.fillFromDb(dbJson);
        return mig;
    }

    @Override
    public RedamResult getRedamResult(int personId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.bo_get_redam(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return null;
        }

        RedamResult redam = new RedamResult();
        redam.fillFromDb(dbJson);
        return redam;
    }

    @Override
    public List<SatPlateResult> getSatPlateResult(int personId) throws Exception {
        JSONArray dbJsonArray = queryForObjectTrx("select * from person.bo_get_sat_plate(?)"
                , JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJsonArray == null) {
            return null;
        }

        List<SatPlateResult> satPlates = new ArrayList<>();

        for (int i = 0; i < dbJsonArray.length(); i++) {
            SatPlateResult spr = new SatPlateResult();
            spr.fillFromDb(new JSONObject(dbJsonArray.get(i).toString()));
            satPlates.add(spr);
        }
        return satPlates;
    }

    @Override
    public void deleteDisaggregatedAddress(int personId) {
        updateTrx("DELETE from person.tb_disaggregated_address where person_id = ?",
                new SqlParameterValue(Types.INTEGER, personId)
        );
    }

    @Override
    public void deleteDisaggregatedAddress(int personId, char addressType) {
        updateTrx("DELETE from person.tb_disaggregated_address where person_id = ? and disaggregated_address_type = ?",
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.CHAR, addressType)
        );
    }

    @Override
    public Integer getEmployeesQuantity(String enterpriseRuc) {
        return queryForObject("select * from bases.get_employers_count(?)", Integer.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, enterpriseRuc));
    }

    @Override
    public UniversidadPeru getUniversidadPeru(int personId)  {
        JSONObject dbJson = queryForObjectTrx("select * from person.get_person_universidad_peru(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return null;
        }

        UniversidadPeru universidadPeru = new UniversidadPeru();
        universidadPeru.fillFromDb(dbJson);
        return universidadPeru;
    }

    @Override
    public void updateOcupationAreaId(int personId, int ocupationalInformationNumber, int ocupationalAreaId) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set ocupation_area_id = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.INTEGER, ocupationalAreaId),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public void updateContractType(int personId, int ocupationalInformationNumber, char type) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set contract_type = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.CHAR, type),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public Boolean isInBdsSancorEmployees(String cuit) {
        return queryForObject("select * from bases.is_in_bds_sancor_employees(?)", Boolean.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, cuit));
    }

    @Override
    public List<RccIdeGrouped> getIdeGroupeds(String documentNumber) throws Exception {

        JSONArray dbJson = queryForObject("select * from sbsrcc.get_rcc_ide_solven(?)", JSONArray.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentNumber));

        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<RccIdeGrouped> results = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            RccIdeGrouped result = new RccIdeGrouped();
            result.fillFromDb(dbJson.getJSONObject(i),catalogService);
            results.add(result);
        }
        return results;
    }

    @Override
    public List<Synthesized> getSynthesizeds(String documentNumber)  {

        JSONArray dbJson = queryForObject("select * from sysrcc.get_rcc_synthesized_solven(?)", JSONArray.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentNumber));

        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<Synthesized> results = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            Synthesized result = new Synthesized();
            result.fillFromDb(dbJson.getJSONObject(i),catalogService);
            results.add(result);
        }
        return results;
    }

    @Override
    public void generateSynthesizedByDocument(String documentNumber)  {

        queryForObject("select * from sysrcc.get_rcc_synthesized_solven(?)", String.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentNumber));

    }

    @Override
    public void updateOcupationalFormal(int personId, int ocupationalInformationNumber, Boolean formal) throws Exception {
        updateTrx("UPDATE person.tb_ocupational_information set is_formal = ? where person_id = ? and ocupational_information_number = ?",
                new SqlParameterValue(Types.BOOLEAN, formal),
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, ocupationalInformationNumber));
    }

    @Override
    public List<CendeuUlt24Result> getCendeu24Result(String documentNumber) throws Exception {
        JSONArray dbArray = queryForObject("select * from cendeu.get_cendeu_24(?)", JSONArray.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentNumber));

        if (dbArray == null)
            return new ArrayList<>();

        List<CendeuUlt24Result> cendeuResultList = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            JSONObject json = dbArray.getJSONObject(i);
            CendeuUlt24Result cendeuResult = new CendeuUlt24Result();
            cendeuResult.fillFromDb(json);
            cendeuResultList.add(cendeuResult);
        }
        return cendeuResultList;
    }

}