/**
 *
 */
package com.affirm.common.dao;

import com.affirm.common.model.catalog.Employer;
import com.affirm.common.model.transactional.EmployerCreditStats;
import com.affirm.common.model.transactional.SalaryAdvancePayment;
import com.affirm.common.model.transactional.UserEmployer;
import org.json.JSONArray;

import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public interface EmployerDAO {

    List<UserEmployer> getUserEmployersByEmployer(int employerId, Locale locale) throws Exception;

    EmployerCreditStats getEmployerCreditStats(int employerId) throws Exception;

    SalaryAdvancePayment getEmployerPendingSalaryAdvancePayments(int employerId, Locale locale) throws Exception;

    Integer registerMultiCreditPayment(int multiCreditPaymentId, int creditId, String paymentJson) throws Exception;

    void registerCreditPaymentConfirmation(JSONArray ids, Integer sysUserId, boolean accreditedPayment) throws Exception;

    void registerEmployer(int entityId, String name, String ruc, String address, String phone, Integer professionId, Integer cutoffDay, Integer paymentDay, Double tea, JSONArray users) throws Exception;
    void registerAdvanceSalaryEmployer(int entityId, String name, String ruc, String address, String phone, Integer professionId, Integer daysAfterEndOfMonth, Integer daysBeforeEndOfMonth, JSONArray users) throws Exception;

    void updateEmployer(int entityId, String name, String ruc, String address, String phone, Integer professionId,
            Integer cutoffDay, Integer paymentDay) throws Exception;

    void updateUserEmployer(
            int userEmployerId, String name, String firstSurname, String lastSurname, String email) throws Exception;

    List<Employer> getEmployersByEntity(Integer entityId, Locale locale) throws Exception;

    void activateEmployerByEntity(int entityId, int employerId, boolean active) throws Exception;

    void updateEmployerTeaByEntity(int entityId, int employerId, double tea) throws Exception;

    void registerUserEmployer(int entityId, JSONArray users)  throws Exception;
}
