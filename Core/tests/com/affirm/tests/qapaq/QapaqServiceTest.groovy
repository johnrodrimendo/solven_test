package com.affirm.tests.qapaq

import com.affirm.common.dao.LoanApplicationDAO
import com.affirm.common.dao.PersonDAO
import com.affirm.common.model.Offer
import com.affirm.common.model.transactional.LoanApplication
import com.affirm.qapaq.QapaqServiceCall
import com.affirm.qapaq.model.QapaqOffer
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseConfig
import com.opencsv.CSVWriter
import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class QapaqServiceTest extends BaseConfig {

    @Autowired
    private QapaqServiceCall qapaqServiceCall
    @Autowired
    private LoanApplicationDAO loanApplicationDAO

    @Test
    void shouldResponseWithSuccessfulCode() {
        List<String> validDocumentNumbers = Arrays.asList("70888960", "41010951", "40622561", "41691995", "40041178", "46159803", "08986229")
        Integer loanApplicationId = 8197

        for (String documentNumber : validDocumentNumbers) {
            Boolean response = qapaqServiceCall.capturarValor(documentNumber, loanApplicationId)

            Assertions.assertTrue(response != null)
            Assertions.assertTrue(response)

        }

    }

    @Test
    void shouldResponseWithUnsuccessfulCode() {
        List<String> invalidDocumentNumbers = Arrays.asList("71697274", "abcdefgh", "1a2b3c4d", "********")
        Integer loanApplicationId = 8197

        for (String documentNumber : invalidDocumentNumbers) {
            Boolean response = qapaqServiceCall.capturarValor(documentNumber, loanApplicationId)

            Assertions.assertTrue(response != null)
            Assertions.assertTrue(!response)

        }

    }

    @Test
    void shouldPassValidAndInvalidDocumentNumbers() {
        List<String> documentNumbers = Arrays.asList("70888960", "71697274")
        Integer loanApplicationId = 8197

        Boolean response

        response = qapaqServiceCall.capturarValor(documentNumbers.get(0), loanApplicationId)

        Assertions.assertTrue(response != null)
        Assertions.assertTrue(response)

        response = qapaqServiceCall.capturarValor(documentNumbers.get(1), loanApplicationId)

        Assertions.assertTrue(response != null)
        Assertions.assertTrue(!response)

    }

    @Test
    void shouldListQapaqOffers() {
        Integer loanApplicationId = 8244
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId, Configuration.defaultLocale)

        PersonDAO mockPersonDAO = Mockito.mock(PersonDAO)
        Mockito.doNothing().when(mockPersonDAO).registerPreApprovedBaseByEntityProductParameter(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyDouble(), Mockito.anyInt(), Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString())
        Mockito.doNothing().when(mockPersonDAO).deletePreApprovedBase(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString())

        Executable executable = {
            qapaqServiceCall.callGenerateLoanOffers(loanApplication)
        }

        Assertions.assertDoesNotThrow(executable)
    }

    @Test
    void shouldGetBaseOffers() {
        String input = "/home/omar/Downloads/base.csv"
        String output = "/home/omar/Downloads/outputQapaq.csv"

        List<Offer> offers = new ArrayList<>()

        new File(input).splitEachLine(",") {fields ->
            Offer offer = new Offer()
            offer.setDocumentNumber(fields[0])
            offer.setEmail(fields[1])
            offer.setPhoneNumber(fields[2])
            offer.setPersonName(fields[3])

            offers.add(offer)
        }

        offers.remove(0) // remove header

        for (Offer offer: offers) {
            QapaqOffer qapaqOffer = qapaqServiceCall.callGetOffer(offer.getDocumentNumber())

            if (qapaqOffer != null) {
                offer.setAmount(qapaqOffer.getCampaignAmount())
                offer.setInstallment(qapaqOffer.getInstallment())
                offer.setQuota(qapaqOffer.getQuota())
                offer.setRate(qapaqOffer.getRate())
            }
        }

        File file = new File(output)
        CSVWriter csvWriter = new CSVWriter(new FileWriter(file))
        List<String[]> data = new ArrayList<String[]>()

        String[] headers = [
                "document_number",
                "email",
                "phone_number",
                "split_part",
                "amount",
                "installment",
                "rate",
                "quota",
        ]

        data.add(headers)

        for (Offer offer : offers) {
            String[] array = [
                    offer.getDocumentNumber(),
                    offer.getEmail(),
                    offer.getPhoneNumber(),
                    offer.getPersonName(),
                    offer.getAmount(),
                    offer.getInstallment(),
                    offer.getRate(),
                    offer.getQuota()
            ]
            data.add(array)
        }

        csvWriter.writeAll(data)
        csvWriter.close()
    }

    @Test
    void shouldNotFailRequestWithNoQapaqClient() {
        Integer loanApplicationId = 8940
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId, Configuration.defaultLocale)

        PersonDAO mockPersonDAO = Mockito.mock(PersonDAO)
        Mockito.doNothing().when(mockPersonDAO).registerPreApprovedBaseByEntityProductParameter(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyDouble(), Mockito.anyInt(), Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString())
        Mockito.doNothing().when(mockPersonDAO).deletePreApprovedBase(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString())

        Executable executable = {
            qapaqServiceCall.callGenerateLoanOffers(loanApplication)
        }

        Assertions.assertDoesNotThrow(executable)
    }

}
