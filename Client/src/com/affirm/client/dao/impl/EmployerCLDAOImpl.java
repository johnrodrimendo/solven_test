package com.affirm.client.dao.impl;

import com.affirm.client.dao.EmployerCLDAO;
import com.affirm.client.model.EmployeeCompanyExtranetPainter;
import com.affirm.client.model.EmployeeCredits;
import com.affirm.client.model.LoggedUserEmployer;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.model.transactional.Employee;
import com.affirm.common.model.transactional.EmployerPaymentDay;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.SqlErrorMessageException;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */

@Repository("employerClDao")
public class EmployerCLDAOImpl extends JsonResolverDAO implements EmployerCLDAO {

    @Autowired
    private CatalogService catalogService;

    @Override
    public String getHashedPassword(String email) {
        return queryForObjectTrx("select * from users.get_employer_user_password(?)",
                String.class,
                new SqlParameterValue(Types.VARCHAR, email));
    }

    @Override
    public LoggedUserEmployer registerSessionEmployer(String email, String ip, String metadata, Date signinDate, Locale locale) {
        JSONObject dbJson = queryForObjectTrx("select * from users.employer_sign_in(?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.VARCHAR, ip),
                new SqlParameterValue(Types.VARCHAR, metadata),
                new SqlParameterValue(Types.TIMESTAMP, signinDate));
        if (dbJson == null)
            return null;

        LoggedUserEmployer employer = new LoggedUserEmployer();
        employer.fillFromDb(dbJson, catalogService, locale);
        return employer;
    }

    @Override
    public void registerSessionLogout(int extranetSessionId, Date signoutDate) throws Exception {
        queryForObjectTrx("select * from users.employer_sign_out(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, extranetSessionId),
                new SqlParameterValue(Types.TIMESTAMP, signoutDate));
    }

    @Override
    public <T extends Employee> List<T> getEmployerEmployees(Integer employerId, int offset, int limit, Locale locale, boolean isActive, Class<T> returntype) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from person.get_employees(?, ?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, employerId),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.BOOLEAN, isActive));
        if (dbArray == null) {
            return null;
        }

        List<T> employees = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            T employee = returntype.getConstructor().newInstance();
            employee.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            employees.add(employee);
        }
        return employees;
    }

    @Override
    public List<EmployerPaymentDay> getEmployerPaymentDays(Integer employerId) {
        JSONArray array = queryForObjectTrx("select * from support.get_employer_payment_day(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, employerId));
        if (array == null)
            return null;

        List<EmployerPaymentDay> employerPaymentDays = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            EmployerPaymentDay epd = new EmployerPaymentDay();
            epd.fillFromDb(array.getJSONObject(i));
            employerPaymentDays.add(epd);
        }
        return employerPaymentDays;
    }

    @Override
    public void updateEmployerPaymentDay(Integer employerId, Date month, Date paymentDate) {
        JSONObject dbJson = queryForObjectTrx("select * from support.upd_employer_payment_day(?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, employerId),
                new SqlParameterValue(Types.DATE, month),
                new SqlParameterValue(Types.DATE, paymentDate));
        if (dbJson != null && dbJson.has("error_message")) {
            throw new SqlErrorMessageException(dbJson.getString("error_message"), null);
        }
    }

    @Override
    public List<Employee> insertEmployerEmployees(Integer employerId, String employeesJson, Integer employerUserId, boolean disablePrevious, Locale locale) throws Exception {
        JSONArray array = queryForObjectTrx("select * from person.register_employees(?, ?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, employerId),
                new SqlParameterValue(Types.OTHER, employeesJson),
                new SqlParameterValue(Types.INTEGER, employerUserId),
                new SqlParameterValue(Types.BOOLEAN, disablePrevious));
        if (array == null)
            return null;

        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Employee ec = new Employee();
            ec.fillFromDb(array.getJSONObject(i), catalogService, locale);
            employees.add(ec);
        }
        return employees;
    }

    @Override
    public void updateEmployeeStatus(int[] employeeIds, boolean active) {
        queryForObjectTrx("select * from person.update_employee_status(?, ?)", String.class,
                new SqlParameterValue(Types.OTHER, new Gson().toJson(employeeIds)),
                new SqlParameterValue(Types.BOOLEAN, active));
    }

    @Override
    public void updateUserEmployerPhoneNumber(int userEmployerId, String phoneNumber) {
        updateTrx("UPDATE users.tb_employer_users SET phone_number = ? where employer_user_id = ?",
                new SqlParameterValue(Types.VARCHAR, phoneNumber),
                new SqlParameterValue(Types.INTEGER, userEmployerId));
    }

    @Override
    public void updateUserEmployerAvatar(int userEmployerId, String avatar) {
        updateTrx("UPDATE users.tb_employer_users SET avatar = ? where employer_user_id = ?",
                new SqlParameterValue(Types.VARCHAR, avatar),
                new SqlParameterValue(Types.INTEGER, userEmployerId));
    }

    @Override
    public List<EmployeeCredits> getEmployeeCreditsByEmployer(int employerId, Date month, Integer creditStatus, Locale locale) throws Exception {
        JSONArray array = queryForObjectTrx("select * from credit.get_employee_credits_by_employer(?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, employerId),
                new SqlParameterValue(Types.DATE, month),
                new SqlParameterValue(Types.INTEGER, creditStatus));
        if (array == null)
            return null;

        List<EmployeeCredits> employeeCreditses = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            EmployeeCredits ec = new EmployeeCredits();
            ec.fillFromDb(array.getJSONObject(i), catalogService, locale);
            employeeCreditses.add(ec);
        }
        return employeeCreditses;
    }

    @Override
    public List<EmployeeCompanyExtranetPainter> getEmployerEmployeesPendingAuthorization(Integer employerId, int offset, int limit, Locale locale) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_pending_employer_authorization(?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, employerId),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit));
        if (dbArray == null) {
            return null;
        }

        List<EmployeeCompanyExtranetPainter> employees = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            EmployeeCompanyExtranetPainter employee = new EmployeeCompanyExtranetPainter();
            employee.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            employees.add(employee);
        }
        return employees;
    }
}
