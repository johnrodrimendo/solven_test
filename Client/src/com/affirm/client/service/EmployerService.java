package com.affirm.client.service;

import com.affirm.client.model.form.RegisterEmployeeForm;
import com.affirm.common.model.transactional.Employee;
import com.affirm.common.model.transactional.EmployerPaymentDay;
import com.affirm.common.model.transactional.MultiCreditPayment;
import org.json.JSONArray;

import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 26/09/16.
 */
public interface EmployerService {

    EmployerPaymentDay getEmployerCurrentPaymentDay(Integer employerId);

    JSONArray processEmployeeForm(int employerId, RegisterEmployeeForm... employees) throws Exception;

    List<Employee> registerEmployees(Integer employerId, Integer employerUserId, boolean disablePrevious, Locale locale, RegisterEmployeeForm... employees) throws Exception;

    void registerMultiPayment(MultiCreditPayment multiCreditPayment, int creditId, double paymentAmount) throws Exception;

    void registerEmployer(int entityId, String name, String ruc, String address, String phone, Integer professionId, Integer cutoffDay, Integer paymentDay, Double tea, JSONArray users) throws Exception;
}
