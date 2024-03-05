package com.affirm.tests.questions

import com.affirm.common.dao.LoanApplicationDAO
import com.affirm.common.dao.UserDAO
import com.affirm.common.model.transactional.LoanApplication
import com.affirm.common.model.transactional.PhoneNumber
import com.affirm.common.service.UserService
import com.affirm.common.service.UtilService
import com.affirm.common.service.question.Question54Service
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class Question54ServiceTest extends BaseConfig {

    @Autowired
    private LoanApplicationDAO loanApplicationDAO
    @Autowired
    private UserDAO userDAO
    @Autowired
    private Question54Service question54Service
    @Autowired
    private UserService userService
    @Autowired
    private UtilService utilService

    private static final PE_LOAN_APPLICATION_ID = 9488
    private static final ARG_LOAN_APPLICATION_ID = 9496

    @Test
    void shouldRegisterPeruPhoneNumberAndReturnTheLastOneInserted() {
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(PE_LOAN_APPLICATION_ID, Configuration.defaultLocale)
        List<PhoneNumber> phoneNumbers
        PhoneNumber numberToVerify

//        FIRST TRY
        String firstPhoneNumber = '900111100'
        userDAO.registerPhoneNumber(loanApplication.userId, '51', firstPhoneNumber);

        phoneNumbers = userDAO.getAllPhoneNumbersByUser(loanApplication.userId)
        numberToVerify = userService.getUserPhoneNumberToVerify(phoneNumbers)

        Assertions.assertNotNull(numberToVerify)
        Assertions.assertEquals(firstPhoneNumber, numberToVerify.phoneNumber)

//        SECOND TRY
        String secondPhoneNumber = '900111101'
        userDAO.registerPhoneNumber(loanApplication.userId, '51', secondPhoneNumber);

        phoneNumbers = userDAO.getAllPhoneNumbersByUser(loanApplication.userId)
        numberToVerify = userService.getUserPhoneNumberToVerify(phoneNumbers)

        Assertions.assertNotNull(numberToVerify)
        Assertions.assertEquals(secondPhoneNumber, numberToVerify.phoneNumber)
    }

    @Test
    void shouldRegisterArgentinaPhoneNumberAndReturnTheLastOneInserted() {
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(ARG_LOAN_APPLICATION_ID, Configuration.defaultLocale)
        List<PhoneNumber> phoneNumbers
        PhoneNumber numberToVerify

//        FIRST TRY
        String firstPhoneNumber = '(11) 9512-3578'
        userDAO.registerPhoneNumber(loanApplication.userId, '54', firstPhoneNumber);

        phoneNumbers = userDAO.getAllPhoneNumbersByUser(loanApplication.userId)
        numberToVerify = userService.getUserPhoneNumberToVerify(phoneNumbers)

        Assertions.assertNotNull(numberToVerify)
        Assertions.assertEquals(firstPhoneNumber, numberToVerify.phoneNumber)

//        SECOND TRY
        String secondPhoneNumber = '(11) 3578-9512'
        userDAO.registerPhoneNumber(loanApplication.userId, '54', secondPhoneNumber);

        phoneNumbers = userDAO.getAllPhoneNumbersByUser(loanApplication.userId)
        numberToVerify = userService.getUserPhoneNumberToVerify(phoneNumbers)

        Assertions.assertNotNull(numberToVerify)
        Assertions.assertEquals(secondPhoneNumber, numberToVerify.phoneNumber)
    }

    @Test
    void shouldRegisterPhoneNumberAndValidate() {
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(PE_LOAN_APPLICATION_ID, Configuration.defaultLocale)
        List<PhoneNumber> phoneNumbers
        PhoneNumber numberToVerify

        String firstPhoneNumber = '900111102'
        userDAO.registerPhoneNumber(loanApplication.userId, '51', firstPhoneNumber);

        phoneNumbers = userDAO.getAllPhoneNumbersByUser(loanApplication.userId)
        numberToVerify = userService.getUserPhoneNumberToVerify(phoneNumbers)

        Assertions.assertNotNull(numberToVerify)
        Assertions.assertEquals(firstPhoneNumber, numberToVerify.phoneNumber)

        String errorMessage = userService.validateSmsAuthToken(loanApplication.getUserId(), numberToVerify.smsToken, Configuration.defaultLocale)
        Assertions.assertNull(errorMessage)
    }
}
