package com.affirm.tests.wavy

import com.affirm.common.model.catalog.Currency
import com.affirm.common.service.UtilService
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseConfig
import com.affirm.wavy.model.Destination
import com.affirm.wavy.model.HSM
import com.affirm.wavy.model.HSMMessage
import com.affirm.wavy.model.Message
import com.affirm.wavy.service.WavyService
import groovy.transform.CompileStatic
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow

@CompileStatic
class WavyServiceTest extends BaseConfig {

    @Autowired
    private WavyService wavyService
    @Autowired
    private UtilService utilService

    private static final String additionalInfo = new JSONObject().put("loanApplicationId", "loanApplicationId").put("creditId", "creditId").toString()

    @Test
    void shouldSendWhatsappMessage() {
        List<Destination> destinations = new ArrayList<>()

        for (int i = 0; i < Configuration.TESTABLE_PHONENUMBERS.size(); i++) {
            Destination destination = new Destination()
            destination.setDestination(Configuration.TESTABLE_PHONENUMBERS.get(i))
            destinations.add(destination)
        }

//        YOU MUST CHOOSE ONLY ONE KIND
        Message message = new Message()
        message.setMessageText("Hola mundo")

        Executable executable = { wavyService.sendTextMessage(destinations, message, null, additionalInfo) }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldSendWhatsappHSMMessage() {
        List<Destination> destinations = new ArrayList<>()

        for (int i = 0; i < Configuration.TESTABLE_PHONENUMBERS.size(); i++) {
            Destination destination = new Destination()
            destination.setDestination(Configuration.TESTABLE_PHONENUMBERS.get(i))
            destinations.add(destination)
        }

        HSM hsm = new HSM()
        hsm.setElementName("cobranza_cuotas_por_vencer")
        hsm.setLanguageCode("es")// ONLY IF HSM HAS DEFINE THAT LANGUAGE CODE
        hsm.setParameters(Arrays.asList("Daniel", "1", utilService.doubleMoneyFormat(Double.parseDouble("1000"), Currency.PEN_SYMBOL)))

        HSMMessage hsmMessage = new HSMMessage()
        hsmMessage.setTtl(1)
        hsmMessage.setHsm(hsm)

        Executable executable = { wavyService.sendHSMMessage(destinations, hsmMessage, null, additionalInfo) }

        assertDoesNotThrow(executable)
    }
}
