package com.affirm.tests.questions

import com.affirm.common.dao.LoanApplicationDAO
import com.affirm.common.dao.UserDAO
import com.affirm.common.model.transactional.LoanApplication
import com.affirm.common.model.transactional.PhoneNumber
import com.affirm.common.service.UserService
import com.affirm.common.service.UtilService
import com.affirm.common.service.question.Question161Service
import com.affirm.common.service.question.Question54Service
import com.affirm.common.service.question.QuestionFlowService
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class Question161ServiceTest extends BaseConfig {

    @Autowired
    private LoanApplicationDAO loanApplicationDAO
    @Autowired
    private UserDAO userDAO
    @Autowired
    private Question161Service question161Service
    @Autowired
    private UserService userService
    @Autowired
    private UtilService utilService

    @Test
    void doGet() {
        question161Service.getViewAttributes(QuestionFlowService.Type.LOANAPPLICATION, 635552, Configuration.defaultLocale, false, null)
    }
}
