
package com.affirm.tests.stress

import com.affirm.landing.controller.LoanApplicationController
import com.affirm.common.service.question.QuestionFlowService
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseLandingConfig
import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.ui.ModelMap

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class LoanApplicationControllerTests extends BaseLandingConfig {

    MockHttpServletRequest mockedRequest

    @Autowired
    LoanApplicationController loanApplicationController

    @Test
    void postLoanApplicationSubmissionControllerSuccess() {

        mockedRequest = new MockHttpServletRequest()
        mockedRequest.setRequestURI('solven.pe')
        /*Object object = loanApplicationController.getPersonalLoan5(new ModelMap(), Configuration.getDefaultLocale(), mockedRequest,
                QuestionFlowService.Type.LOANAPPLICATION, getFormLandingPage15(), "credito-de-consumo-5", null, null, null, '', '', '', '', '', '', '', '1380719033.1535399068')
        assertEquals 308, object.getProperties().get('statusCodeValue')*/
    }

}