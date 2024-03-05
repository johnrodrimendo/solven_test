package com.affirm.tests.contracts

import com.affirm.common.dao.LoanApplicationDAO
import com.affirm.common.model.transactional.LoanApplication
import com.affirm.common.model.transactional.LoanOffer
import com.affirm.common.service.CreditService
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseClientConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.thymeleaf.spring4.SpringTemplateEngine

import java.text.SimpleDateFormat

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class AccesoLibreDisponibilidadContractStrategyTest extends BaseClientConfig {

    @Autowired
    private CreditService creditService;
    @Autowired
    private SpringTemplateEngine templateEngine
    @Autowired
    private LoanApplicationDAO loanApplicationDAO

    @Test
    void shouldRenderAccesoContract() {
        int creditId = 20887

        byte[] pdfAsBytes = creditService.createContract(creditId, null, null, Configuration.defaultLocale, templateEngine, null, true)
        def fileName = new SimpleDateFormat("yyyyMMddHHmm'.pdf'").format(new Date())
        def fileAbsolutePath = ABSOLUTE_TEST_DIR + '/' + fileName
        def createdFile = new File(fileAbsolutePath)

        checkIfDirExistsElseCreateDir ABSOLUTE_TEST_DIR
        createFileFromBytesArray fileAbsolutePath, pdfAsBytes

        assertTrue createdFile.exists()
        assertTrue createdFile.isFile()
        assertEquals fileName, createdFile.getName()
    }

    @Test
    void shouldRenderAztecaContract() {
        int loanId = 641759

        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanId, Configuration.defaultLocale)
        List<LoanOffer> offers =  loanApplicationDAO.getLoanOffersAll(loanId);
        LoanOffer selectedOffer = null;
        for (int i = 0; i < offers.size(); i++) {
            if(offers.get(i).getSelected() != null && offers.get(i).getSelected()) selectedOffer = offers.get(i);
        }
        byte[] pdfAsBytes = creditService.createOfferContract(loanApplication, selectedOffer, null, null, Configuration.defaultLocale, templateEngine, null, true)
        def fileName = new SimpleDateFormat("yyyyMMddHHmm'.pdf'").format(new Date())
        def fileAbsolutePath = ABSOLUTE_TEST_DIR + '/' + fileName
        def createdFile = new File(fileAbsolutePath)

        checkIfDirExistsElseCreateDir ABSOLUTE_TEST_DIR
        createFileFromBytesArray fileAbsolutePath, pdfAsBytes

        assertTrue createdFile.exists()
        assertTrue createdFile.isFile()
        assertEquals fileName, createdFile.getName()
    }

}
