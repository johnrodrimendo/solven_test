package com.affirm.tests.dao

import com.affirm.common.dao.EmployerDAO
import com.affirm.common.model.catalog.Employer
import com.affirm.common.model.catalog.Profession
import com.affirm.common.model.transactional.EmployerCreditStats
import com.affirm.common.model.transactional.SalaryAdvancePayment
import com.affirm.common.model.transactional.UserEmployer
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseConfig
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired

import static org.junit.jupiter.api.Assertions.*

class EmployerDAOTest extends BaseConfig {

    @Autowired
    private EmployerDAO employerDAO

    private static final Integer EMPLOYER_ID = 5
    private static final Integer ENTITY_ID = 9
    private static final Integer CREDIT_ID = 1058
    private static final Integer MULTICREDITPAYMENT_ID = 0
    private static final Integer SYS_USER_ID = 13
    private static final Integer USER_EMPLOYER_ID = 0

    private static final String PAYMENT_JSON = new JSONObject().toString()
    private static final JSONArray IDS = new JSONArray()
    private static final String NAME = "Daniel"
    private static final String RUC = "1070061801"
    private static final String ADDRESS = "address"
    private static final String PHONE = "926105802"
    private static final Integer CUTOFFDAY = 30
    private static final Integer PAYMENT_DAY = 30
    private static final Integer TEA = 0
    private static final JSONArray USERS = new JSONArray()
    private static final Integer DAYSAFTERENDOFMONTH = 0
    private static final Integer DAYSBEFOREENDOFMONTH = 0
    private static final String FIRSTSURNAME = "Alvarez"
    private static final String LASTSURNAME = "Vera"
    private static final String EMAIL = "dalvarez@solven.pe"

    @Test
    void shouldGetUserEmployersByEmployer() {
        List<UserEmployer> employerList = employerDAO.getUserEmployersByEmployer(EMPLOYER_ID, Configuration.defaultLocale);

        assertNull(employerList);
    }

    @Test
    void shouldGetEmployerCreditStats() {
        EmployerCreditStats employerCreditStats = employerDAO.getEmployerCreditStats(EMPLOYER_ID)

        assertNotNull(employerCreditStats)
    }

    @Test
    void shouldGetEmployerPendingSalaryAdvancePayments() {
        SalaryAdvancePayment salaryAdvancePayment = employerDAO.getEmployerPendingSalaryAdvancePayments(EMPLOYER_ID, Configuration.defaultLocale)

        assertNull(salaryAdvancePayment)
    }

    @Test
    void shouldRegisterMultiCreditPayment() {
        Integer registerMultiCreditPayment = employerDAO.registerMultiCreditPayment(MULTICREDITPAYMENT_ID, CREDIT_ID, PAYMENT_JSON)

        assertNotNull(registerMultiCreditPayment)
    }

    @Test
    void shouldRegisterCreditPaymentConfirmation() {
        boolean trueAccreditedPayment = true
        Executable trueExecutable = {
            employerDAO.registerCreditPaymentConfirmation(IDS, SYS_USER_ID, trueAccreditedPayment)
        }

        boolean falseAccreditedPayment = true
        Executable falseExecutable = {
            employerDAO.registerCreditPaymentConfirmation(IDS, SYS_USER_ID, falseAccreditedPayment)
        }

        assertDoesNotThrow(trueExecutable)
        assertDoesNotThrow(falseExecutable)
    }

    @Test
    void shouldRegisterEmployer() {
        Executable executable = {
            employerDAO.registerEmployer(ENTITY_ID, NAME, RUC, ADDRESS, PHONE, Profession.OTHER, CUTOFFDAY, PAYMENT_DAY, TEA, USERS)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldRegisterAdvanceSalaryEmployer() {
        Executable executable = {
            employerDAO.registerAdvanceSalaryEmployer(ENTITY_ID, NAME, RUC, ADDRESS, PHONE, Profession.OTHER, DAYSAFTERENDOFMONTH, DAYSBEFOREENDOFMONTH, USERS)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdateEmployer() {
        Executable executable = {
            employerDAO.updateEmployer(ENTITY_ID, NAME, RUC, ADDRESS, PHONE, Profession.OTHER, CUTOFFDAY, PAYMENT_DAY)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdateUserEmployer() {
        Executable executable = {
            employerDAO.updateUserEmployer(USER_EMPLOYER_ID, NAME, FIRSTSURNAME, LASTSURNAME, EMAIL)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldGetEmployersByEntity() {
        List<Employer> employerList = employerDAO.getEmployersByEntity(ENTITY_ID, Configuration.defaultLocale)

        assertNotNull(employerList)
    }

    @Test
    void shouldActivateEmployerByEntity() {
        boolean active = true
        Executable activeExecutable = { employerDAO.activateEmployerByEntity(ENTITY_ID, EMPLOYER_ID, active) }

        boolean inactive = false
        Executable inactiveExecutable = { employerDAO.activateEmployerByEntity(ENTITY_ID, EMPLOYER_ID, inactive) }

        assertDoesNotThrow(activeExecutable)
        assertDoesNotThrow(inactiveExecutable)
    }

    @Test
    void shouldUpdateEmployerTeaByEntity() {
        Executable executable = { employerDAO.updateEmployerTeaByEntity(ENTITY_ID, EMPLOYER_ID, TEA) }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldRegisterUserEmployer() {
        Executable executable = { employerDAO.registerUserEmployer(ENTITY_ID, USERS) }

        assertDoesNotThrow(executable)
    }

}
