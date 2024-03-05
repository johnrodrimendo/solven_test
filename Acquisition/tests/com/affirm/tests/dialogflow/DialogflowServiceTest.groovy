package com.affirm.tests.dialogflow

import com.affirm.common.model.catalog.CountryParam
import com.affirm.common.model.catalog.IdentityDocumentType
import com.affirm.common.util.SqlErrorMessageException
import com.affirm.dialogflow.DialogflowRequest
import com.affirm.dialogflow.DialogflowService
import com.affirm.tests.BaseAcquisitionConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import static org.junit.jupiter.api.Assertions.assertTrue

class DialogflowServiceTest extends BaseAcquisitionConfig {

    @Autowired
    DialogflowService dialogflowService

    @Test
    void shouldValidatePeruDocumentNumber() {
//        DNI
        DialogflowRequest data = new DialogflowRequest()
        data.setCountry(CountryParam.COUNTRY_PERU)
        data.setIdentityDocument(IdentityDocumentType.DNI)
        data.setIdentityDocumentNumber('el dni esta 70061801 entre palabras')

        try {
            dialogflowService.preValidateUserHasNotActiveLoanApplication(data)
        } catch (SqlErrorMessageException e) {
            assertTrue(e.messageBody.contains("El DNI debe tener 8 digitos"))
        }

        data.setIdentityDocumentNumber('70061801 junto a un texto')

        try {
            dialogflowService.preValidateUserHasNotActiveLoanApplication(data)
        } catch (SqlErrorMessageException e) {
            assertTrue(e.messageBody.contains("El DNI debe tener 8 digitos"))
        }

        data.setIdentityDocumentNumber('70061801sinespacios')

        try {
            dialogflowService.preValidateUserHasNotActiveLoanApplication(data)
        } catch (SqlErrorMessageException e) {
            assertTrue(e.messageBody.contains("El DNI debe tener 8 digitos"))
        }

//        CE
        data = new DialogflowRequest()
        data.setCountry(CountryParam.COUNTRY_PERU)
        data.setIdentityDocument(IdentityDocumentType.CE)
        data.setIdentityDocumentNumber('el ce esta 001043328 entre palabras')

        try {
            dialogflowService.preValidateUserHasNotActiveLoanApplication(data)
        } catch (SqlErrorMessageException e) {
            assertTrue(e.messageBody.contains("El CE debe tener entre 9 a 12 dígitos y/o caracteres"))
        }

        data.setIdentityDocumentNumber('001043328 junto a un texto')

        try {
            dialogflowService.preValidateUserHasNotActiveLoanApplication(data)
        } catch (SqlErrorMessageException e) {
            assertTrue(e.messageBody.contains("El CE debe tener entre 9 a 12 dígitos y/o caracteres"))
        }

        data.setIdentityDocumentNumber('001043328sinespacios')

        try {
            dialogflowService.preValidateUserHasNotActiveLoanApplication(data)
        } catch (SqlErrorMessageException e) {
            assertTrue(e.messageBody.contains("El CE debe tener entre 9 a 12 dígitos y/o caracteres"))
        }
    }

    @Test
    void shouldValidateArgDocumentNumber() {
        DialogflowRequest data = new DialogflowRequest()
        data.setCountry(CountryParam.COUNTRY_ARGENTINA)
        data.setIdentityDocument(IdentityDocumentType.CUIT)
        data.setIdentityDocumentNumber('el cuit esta 20365526312 entre palabras')

        try {
            dialogflowService.preValidateUserHasNotActiveLoanApplication(data)
        } catch (SqlErrorMessageException e) {
            assertTrue(e.messageBody.contains("El documento debe tener 11 digitos"))
        }

        data.setIdentityDocumentNumber('20365526312 junto a un texto')

        try {
            dialogflowService.preValidateUserHasNotActiveLoanApplication(data)
        } catch (SqlErrorMessageException e) {
            assertTrue(e.messageBody.contains("El documento debe tener 11 digitos"))
        }

        data.setIdentityDocumentNumber('20365526312sinespacios')

        try {
            dialogflowService.preValidateUserHasNotActiveLoanApplication(data)
        } catch (SqlErrorMessageException e) {
            assertTrue(e.messageBody.contains("El documento debe tener 11 digitos"))
        }
    }

    @Test
    void shouldValidateUserHasActiveLA() {
        DialogflowRequest data = new DialogflowRequest()
        data.setCountry(CountryParam.COUNTRY_PERU)
        data.setIdentityDocument(IdentityDocumentType.DNI)
        data.setIdentityDocumentNumber('70061801')

        try {
            dialogflowService.preValidateUserHasNotActiveLoanApplication(data)
        } catch (SqlErrorMessageException e) {
            assertTrue(e.messageBody.contains("una solicitud activa"))
        }
    }

    @Test
    void shouldValidateEmail() {
        DialogflowRequest data = new DialogflowRequest()
        data.setEmail("micorreo@notienedominio")

        try {
            dialogflowService.updateLoanApplicationEmail(data)
        } catch (SqlErrorMessageException e) {
            assertTrue(e.messageBody.equals("Ingresa un email válido."))
        }

        data.setEmail("micorreonotienearroba.com")

        try {
            dialogflowService.updateLoanApplicationEmail(data)
        } catch (SqlErrorMessageException e) {
            assertTrue(e.messageBody.equals("Ingresa un email válido."))
        }
    }

    @Test
    void shouldValidatePeruPhoneNumber() {
        DialogflowRequest data = new DialogflowRequest()
        data.setCountry(CountryParam.COUNTRY_PERU)
        data.setPhoneNumber("9 2 6 1 0 5 8 0 2")

        try {
            dialogflowService.updateLoanApplicationPhoneAndReturnLink(data)
        } catch (SqlErrorMessageException e) {
            assertTrue(e.messageBody.equals("El texto no es válido."))
        }

        data.setPhoneNumber("123456789 junto con un texto")

        try {
            dialogflowService.updateLoanApplicationPhoneAndReturnLink(data)
        } catch (SqlErrorMessageException e) {
            assertTrue(e.messageBody.equals("El texto no es válido."))
        }

        data.setPhoneNumber("123456789sinespacios")

        try {
            dialogflowService.updateLoanApplicationPhoneAndReturnLink(data)
        } catch (SqlErrorMessageException e) {
            assertTrue(e.messageBody.equals("El texto no es válido."))
        }
    }

    @Test
    void shouldValidateArgPhoneNumber() {
        DialogflowRequest data = new DialogflowRequest()
        data.setCountry(CountryParam.COUNTRY_ARGENTINA)
        data.setPhoneNumber("12345678")

        try {
            dialogflowService.updateLoanApplicationPhoneAndReturnLink(data)
        } catch (SqlErrorMessageException e) {
            assertTrue(e.messageBody.contains("El número no es válido."))
        }

        data.setPhoneNumber("(1234)12345678")

        try {
            dialogflowService.updateLoanApplicationPhoneAndReturnLink(data)
        } catch (SqlErrorMessageException e) {
            assertTrue(e.messageBody.contains("El número no es válido."))
        }

        data.setPhoneNumber("(123)12345678")

        try {
            dialogflowService.updateLoanApplicationPhoneAndReturnLink(data)
        } catch (SqlErrorMessageException e) {
            assertTrue(e.messageBody.contains("El número no es válido."))
        }
    }
}
