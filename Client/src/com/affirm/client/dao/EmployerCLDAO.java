/**
 *
 */
package com.affirm.client.dao;

import com.affirm.client.model.EmployeeCompanyExtranetPainter;
import com.affirm.client.model.EmployeeCredits;
import com.affirm.client.model.LoggedUserEmployer;
import com.affirm.common.model.transactional.Employee;
import com.affirm.common.model.transactional.EmployerPaymentDay;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public interface EmployerCLDAO {
    String getHashedPassword(String email);

    LoggedUserEmployer registerSessionEmployer(String email, String ip, String metadata, Date signinDate, Locale locale) throws Exception;

    void registerSessionLogout(int extranetSessionId, Date signoutDate) throws Exception;

    <T extends Employee> List<T> getEmployerEmployees(Integer employerId, int offset, int limit, Locale locale, boolean isActive, Class<T> returntype) throws Exception;

    List<EmployerPaymentDay> getEmployerPaymentDays(Integer employerId);

    void updateEmployerPaymentDay(Integer employerId, Date month, Date paymentDate);

    List<Employee> insertEmployerEmployees(Integer employerId, String employeesJson, Integer employerUserId, boolean disablePrevious, Locale locale) throws Exception;

    void updateEmployeeStatus(int[] employeeIds, boolean active);

    void updateUserEmployerPhoneNumber(int userEmployerId, String phoneNumber);

    void updateUserEmployerAvatar(int userEmployerId, String avatar);

    List<EmployeeCredits> getEmployeeCreditsByEmployer(int employerId, Date month, Integer creditStatus, Locale locale) throws Exception;

    List<EmployeeCompanyExtranetPainter> getEmployerEmployeesPendingAuthorization(Integer employerId, int offset, int limit, Locale locale) throws Exception;
}
