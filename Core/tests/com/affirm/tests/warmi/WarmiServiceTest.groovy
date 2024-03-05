package com.affirm.tests.warmi

import com.affirm.common.dao.LoanApplicationDAO
import com.affirm.common.dao.PersonDAO
import com.affirm.common.model.transactional.LoanApplication
import com.affirm.common.model.transactional.Person
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseConfig
import com.affirm.warmi.model.ProcessDetail
import com.affirm.warmi.service.WarmiService
import com.google.gson.Gson
import groovy.transform.CompileStatic
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class WarmiServiceTest extends BaseConfig {

    @Autowired
    private LoanApplicationDAO loanApplicationDAO
    @Autowired
    private PersonDAO personDAO
    @Autowired
    private WarmiService warmiService

    @Test
    void shouldResponseWithSuccessfullCode() {
        Locale locale = Configuration.getDefaultLocale()
        int loanApplicationId = 637402
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId, locale)
        Person person = personDAO.getPerson(loanApplication.getPersonId(), false, locale)

        JSONObject jsonObject = warmiService.runProcess(loanApplication, person)
        System.out.println(jsonObject.toString())
    }

    @Test
    void shouldSaveResultSuccessfully() {
        String json = "{\"processId\":\"f9660a66-2869-4c71-a7e0-797d155a9886\",\"person\":{\"documentType\":\"DNI\",\"documentNumber\":\"41344185\",\"dateOfBirth\":\"07/04/82\",\"surnames\":\"GARCIA TERRY\",\"names\":\"CARMEN\",\"foreignNationality\":\"Peruano\"},\"educations\":[],\"workInformation\":{},\"healthSystem\":{\"affiliateNumberSis\":\"2-41344185\",\"insuredTypeSis\":\"TITULAR\",\"insuredStatusSis\":\"CANCELADO\"},\"financialSystems\":[],\"penaltyFee\":{\"electoralFines\":[],\"driverFines\":[]},\"telephoneServices\":[{\"company\":\"Claro\",\"serviceType\":\"POSTPAGO CONSUMER\",\"phoneNumber\":\"51982243XXX\"},{\"company\":\"Claro\",\"serviceType\":\"PREPAGO\",\"phoneNumber\":\"51982166XXX\"},{\"company\":\"Entel\",\"serviceType\":\"Prepago\",\"phoneNumber\":\"934258***\"}],\"properties\":[],\"commercial\":{\"taxPayerId\":\"10413441850\",\"taxPayerName\":\"GARCIA TERRY CARMEN\",\"taxPayerType\":\"PERSONA NATURAL CON NEGOCIO\",\"name\":\"-\",\"taxPayerRegisterDate\":\"22/10/02\",\"taxPayerCondition\":\"HABIDO\",\"activity\":\"Principal - 93098 - OTRAS ACTIVID.DE TIPO SERVICIO NCP\",\"address\":\"-\",\"activityStartDate\":\"22/10/02\"},\"legals\":[],\"judicialBackgrounds\":[],\"internationalReport\":{},\"vehicle\":{\"driverLicenseNumber\":\"Q41344185\",\"driverLicenseCategory\":\"AI\",\"driverLicenseValidity\":\"09/09/29\",\"driverLicenseStatus\":\"VIGENTE\",\"verySeriousFoulsNumber\":0,\"seriousFoulsNumber\":0,\"accumPoints\":0},\"taxes\":[],\"policyResults\":[{\"policy\":\"Ingreso bruto estimado (dependientes)\",\"resultMessage\":\"No se encontró información suficiente para poder evaluar la política\",\"executionStatus\":\"Can\\u0027t execute\"},{\"policy\":\"Ocupación\",\"executionStatus\":\"Success\",\"approved\":true},{\"policy\":\"Rango de edad\",\"resultMessage\":\"Edad fuera del rango definido - 38 años.\",\"executionStatus\":\"Success\",\"approved\":false},{\"policy\":\"Calificación en el sistema financiero\",\"resultMessage\":\"Calificación peor a lo permitido en el (los) siguiente (s) rangos de meses: Desde 1 hasta 5 calificación, 5 calificación(es) No Reportado.\",\"executionStatus\":\"Success\",\"approved\":false}]}";
        ProcessDetail result = new Gson().fromJson(json, ProcessDetail.class)
        warmiService.saveResult(result)
    }


}
