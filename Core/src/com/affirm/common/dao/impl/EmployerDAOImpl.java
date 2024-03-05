package com.affirm.common.dao.impl;

import com.affirm.common.dao.EmployerDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.model.catalog.Employer;
import com.affirm.common.model.transactional.EmployerCreditStats;
import com.affirm.common.model.transactional.SalaryAdvancePayment;
import com.affirm.common.model.transactional.UserEmployer;
import com.affirm.common.service.CatalogService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */

@Repository("employerDao")
public class EmployerDAOImpl extends JsonResolverDAO implements EmployerDAO {


    @Autowired
    private CatalogService catalogService;

    @Override
    public List<UserEmployer> getUserEmployersByEmployer(int employerId, Locale locale) throws Exception {
        JSONArray array = queryForObjectTrx("select * from person.get_employer_users(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, employerId));
        if (array == null)
            return null;

        List<UserEmployer> users = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            UserEmployer user = new UserEmployer();
            user.fillFromDb(array.getJSONObject(i), catalogService, locale);
            users.add(user);
        }
        return users;
    }

    @Override
    public EmployerCreditStats getEmployerCreditStats(int employerId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.get_employer_credit_stats(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, employerId));
        if (dbJson == null)
            return null;

        EmployerCreditStats stats = new EmployerCreditStats();
        stats.fillFromDb(dbJson);
        return stats;
    }

    @Override
    public SalaryAdvancePayment getEmployerPendingSalaryAdvancePayments(int employerId, Locale locale) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from credit.get_pending_credit_payment_multi(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, employerId));
        if (dbJson == null)
            return null;

        SalaryAdvancePayment payment = new SalaryAdvancePayment();
        payment.fillFromDb(dbJson, catalogService, locale);
        return payment;
    }

    @Override
    public Integer registerMultiCreditPayment(int multiCreditPaymentId, int creditId, String paymentJson) throws Exception {
        return queryForObjectTrx("select * from credit.bo_register_payment(?, ?, ?)", Integer.class,
                new SqlParameterValue(Types.INTEGER, multiCreditPaymentId),
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.OTHER, paymentJson));
    }

    @Override
    public void registerCreditPaymentConfirmation(JSONArray ids, Integer sysUserId, boolean accreditedPayment) throws Exception {
        queryForObjectTrx("select * from credit.bo_confirm_payment(?, ?, ?)", String.class,
                new SqlParameterValue(Types.OTHER, ids.toString()),
                new SqlParameterValue(Types.INTEGER, sysUserId),
                new SqlParameterValue(Types.BOOLEAN, accreditedPayment));
    }

    @Override
    public void registerEmployer(
            int entityId, String name, String ruc, String address, String phone, Integer professionId,
            Integer cutoffDay, Integer paymentDay, Double tea, JSONArray users) throws Exception {
        queryForObjectTrx("select * from support.create_employer(?,?,?,?,?,?,?,?,?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.VARCHAR, name),
                new SqlParameterValue(Types.VARCHAR, ruc),
                new SqlParameterValue(Types.INTEGER, cutoffDay),
                new SqlParameterValue(Types.INTEGER, paymentDay),
                new SqlParameterValue(Types.VARCHAR, phone),
                new SqlParameterValue(Types.VARCHAR, address),
                new SqlParameterValue(Types.NUMERIC, tea),
                new SqlParameterValue(Types.OTHER, users.toString()),
                new SqlParameterValue(Types.INTEGER, professionId));
    }

    @Override
    public void updateEmployer(
            int entityId, String name, String ruc, String address, String phone, Integer professionId,
            Integer daysAfterEndOfMonth, Integer daysBeforeEndOfMonth) throws Exception {
        updateTrx("UPDATE support.ct_employers set employer=?, ruc=?, phone_number=?, profession_id=?, days_after_end_of_month=?, days_before_end_of_month=?, address=? where employer_id=?",
                new SqlParameterValue(Types.VARCHAR, name),
                new SqlParameterValue(Types.VARCHAR, ruc),
                new SqlParameterValue(Types.VARCHAR, phone),
                new SqlParameterValue(Types.INTEGER, professionId),
                new SqlParameterValue(Types.INTEGER, daysAfterEndOfMonth),
                new SqlParameterValue(Types.INTEGER, daysBeforeEndOfMonth),
                new SqlParameterValue(Types.VARCHAR, address),
                new SqlParameterValue(Types.INTEGER, entityId));
    }

    public void updateUserEmployer(
            int userEmployerId, String name, String firstSurname, String lastSurname, String email) throws Exception {
        updateTrx("UPDATE users.tb_employer_users set person_name=?, first_surname=?, last_surname=?, email=? where employer_user_id=?",
                new SqlParameterValue(Types.VARCHAR, name),
                new SqlParameterValue(Types.VARCHAR, firstSurname),
                new SqlParameterValue(Types.VARCHAR, lastSurname),
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.INTEGER, userEmployerId));
    }

    public void registerUserEmployer(int entityId, JSONArray users){
        queryForObjectTrx("select * from users.register_employer_user(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.OTHER, users.toString()));
    }

    @Override
    public void registerAdvanceSalaryEmployer(
            int entityId, String name, String ruc, String address, String phone, Integer professionId,
            Integer daysAfterEndOfMonth, Integer daysBeforeEndOfMonth, JSONArray users) throws Exception {
        queryForObjectTrx("select * from support.create_employer_salary_advance(?,?,?,?,?,?,?,?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.VARCHAR, name),
                new SqlParameterValue(Types.VARCHAR, ruc),
                new SqlParameterValue(Types.INTEGER, daysAfterEndOfMonth),
                new SqlParameterValue(Types.INTEGER, daysBeforeEndOfMonth),
                new SqlParameterValue(Types.VARCHAR, phone),
                new SqlParameterValue(Types.VARCHAR, address),
                new SqlParameterValue(Types.OTHER, users.toString()),
                new SqlParameterValue(Types.INTEGER, professionId));
    }

    @Override
    public List<Employer> getEmployersByEntity(Integer entityId, Locale locale) {
        JSONArray jsonArray = queryForObjectTrx("select * from support.get_employers_by_entity_id(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId));

        if (jsonArray == null) {
            return null;
        }

        List<Employer> employers = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Employer employer = new Employer();
            employer.fillFromDb(catalogService, locale, jsonArray.getJSONObject(i));
            employers.add(employer);
        }

        return employers;
    }

    @Override
    public void activateEmployerByEntity(int entityId, int employerId, boolean active) {
        queryForObjectTrx("select * from support.enable_employer(?,?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, employerId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.BOOLEAN, active));
    }

    @Override
    public void updateEmployerTeaByEntity(int entityId, int employerId, double tea) {
        updateTrx("UPDATE product.ct_rate_commission SET effective_annual_rate = ? where employer_id = ? AND entity_id = ?",
                new SqlParameterValue(Types.DOUBLE, tea),
                new SqlParameterValue(Types.INTEGER, employerId),
                new SqlParameterValue(Types.INTEGER, entityId));
    }


}
