package com.affirm.tests.dao

import com.affirm.client.dao.EmployerCLDAO
import com.affirm.client.model.EmployeeCompanyExtranetPainter
import com.affirm.client.model.EmployeeCredits
import com.affirm.client.model.LoggedUserEmployer
import com.affirm.common.model.transactional.EmployerPaymentDay
import com.affirm.tests.BaseAcquisitionConfig
import org.apache.poi.ss.formula.functions.T
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class EmployerCLDAOTest extends BaseAcquisitionConfig {

    @Autowired
    EmployerCLDAO employerCLDAO

    static final String EMAIL = "occoa@solven.pe"
    static final String IP = "occoa@solven.pe"
    static final String METADATA = "occoa@solven.pe"
    static final Date CURRENT_DATE = new Date()
    static final Date SIGNIN_DATE = CURRENT_DATE
    static final Locale LOCALE = Locale.US
    static final Integer EXTRANET_SESSION_ID = 12345
    static final Date SIGNOUT_DATE = CURRENT_DATE
    static final Class<T> RETURN_TYPE = EmployeeCompanyExtranetPainter.class
    static final Integer OFFSET = 10
    static final Integer LIMIT = 10
    static final Integer EMPLOYER_ID = 11111
    static final Boolean IS_ACTIVE = true
    static final Date MONTH = CURRENT_DATE
    static final Date PAYMENT_DATE = CURRENT_DATE
    static final String EMPLOYEES_JSON = '[{"employeeId": 11111}]'
    static final Integer EMPLOYEE_USER_ID = 22222
    static final Boolean DISABLE_PREVIOUS = true
    static final int[] EMPLOYEE_IDS = [EMPLOYER_ID]
    static final boolean ACTIVE = true
    static final String PHONE_NUMBER = "999999999"
    static final Integer USER_EMPLOYER_ID = 33333
    static final String AVATAR = ""
    static final Integer CREDIT_STATUS = 1

    @Test
    void getHashedPasswordFromEmployerCLDAO() {
        String result = employerCLDAO.getHashedPassword()
        Assert.assertNull(result)
    }

    @Test
    void registerSessionEmployerFromEmployerCLDAO() {
        LoggedUserEmployer result = employerCLDAO.registerSessionEmployer(EMAIL, IP, METADATA, SIGNIN_DATE, LOCALE)
        Assert.assertNull(result)
    }

    @Test
    void registerSessionLogoutFromEmployerCLDAO() {
        employerCLDAO.registerSessionLogout(EXTRANET_SESSION_ID, SIGNOUT_DATE)
    }

    @Test
    void getEmployerEmployeesFromEmployerCLDAO() {
        List<EmployeeCompanyExtranetPainter> result = employerCLDAO.getEmployerEmployees(EMPLOYER_ID, OFFSET, LIMIT, LOCALE,
                IS_ACTIVE, RETURN_TYPE)
        Assert.assertNull(result)
    }

    @Test
    void getEmployerPaymentDaysFromEmployerCLDAO() {
        List<EmployerPaymentDay> result = employerCLDAO.getEmployerPaymentDays(EMPLOYER_ID)
        Assert.assertNull(result)
    }

    @Test
    void updateEmployerPaymentDayFromEmployerCLDAO() {
        employerCLDAO.updateEmployerPaymentDay(EMPLOYER_ID, MONTH, PAYMENT_DATE)
    }

    @Test
    void insertEmployerEmployeesFromEmployerCLDAO() {
        employerCLDAO.insertEmployerEmployees(EMPLOYER_ID, EMPLOYEES_JSON, EMPLOYEE_USER_ID, DISABLE_PREVIOUS, LOCALE)
    }

    @Test
    void updateEmployeeStatusFromEmployerCLDAO() {
        employerCLDAO.updateEmployeeStatus(EMPLOYEE_IDS, ACTIVE)
    }

    @Test
    void updateUserEmployerPhoneNumberFromEmployerCLDAO() {
        employerCLDAO.updateUserEmployerPhoneNumber(USER_EMPLOYER_ID, PHONE_NUMBER)
    }

    @Test
    void updateUserEmployerAvatarFromEmployerCLDAO() {
        employerCLDAO.updateUserEmployerAvatar(USER_EMPLOYER_ID, AVATAR)
    }

    @Test
    void getEmployeeCreditsByEmployerFromEmployerCLDAO() {
        List<EmployeeCredits> result = employerCLDAO.getEmployeeCreditsByEmployer(EMPLOYER_ID, MONTH, CREDIT_STATUS, LOCALE)
        Assert.assertNull(result)
    }

    @Test
    void getEmployerEmployeesPendingAuthorizationFromEmployerCLDAO() {
        List<EmployeeCompanyExtranetPainter> result = employerCLDAO.getEmployerEmployeesPendingAuthorization(
                EMPLOYER_ID, OFFSET, LIMIT, LOCALE)
        Assert.assertNull(result)
    }

}
