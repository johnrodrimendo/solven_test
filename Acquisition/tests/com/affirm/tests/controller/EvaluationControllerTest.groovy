package com.affirm.tests.controller

import com.affirm.acquisition.controller.EvaluationController
import com.affirm.client.model.form.LoanApplicationBankForm
import com.affirm.common.dao.LoanApplicationDAO
import com.affirm.common.dao.PersonDAO
import com.affirm.common.model.transactional.LoanApplication
import com.affirm.common.model.transactional.Person
import com.affirm.common.service.CatalogService
import com.affirm.common.service.LoanApplicationService
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseAcquisitionConfig
import groovy.transform.CompileStatic
import org.json.JSONObject
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

@CompileStatic
class EvaluationControllerTest extends BaseAcquisitionConfig {

    @Autowired
    private EvaluationController evaluationController
    @Autowired
    private CatalogService catalogService
    @Autowired
    private LoanApplicationService loanApplicationService
    @Autowired
    private LoanApplicationDAO loanApplicationDAO
    @Autowired
    private PersonDAO personDAO

    private LoanApplicationBankForm loanApplicationBankForm

    @BeforeEach
    void setUp() {
        loanApplicationBankForm = new LoanApplicationBankForm()
        loanApplicationBankForm.bankId = catalogService.getBanks(false).get(0).id
        loanApplicationBankForm.bankAccountType = (char) 'S'// S - C
        loanApplicationBankForm.bankAccountNumber = '1234567'
        loanApplicationBankForm.bankAccountDepartment = catalogService.getDepartments().get(0).id
    }

    @Test
    void shouldSaveSignature() {
        int loanApplicationId = 8942

        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId, Configuration.defaultLocale)
        Person person = personDAO.getPerson(catalogService, Configuration.defaultLocale, loanApplication.personId, false)

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest()
        mockHttpServletRequest.setServerName("solven-test.pe")

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse()

        String token = loanApplicationService.generateLoanApplicationToken(0, 0, loanApplicationId)
        String signature = person.getName().substring(0, 1).concat(". ").concat(person.getFirstSurname())

        println(new JSONObject(loanApplicationBankForm))

        ResponseEntity<String> response = (ResponseEntity<String>) evaluationController.saveSignature(
                null,
                Configuration.getDefaultLocale(),
                mockHttpServletRequest,
                mockHttpServletResponse,
                loanApplicationBankForm,
                token,
                signature
        )

        println(response.toString())

        Assert.assertTrue(response.statusCodeValue == HttpStatus.PERMANENT_REDIRECT.value())
    }

}
