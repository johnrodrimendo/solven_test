package com.affirm.tests.fdlm

import com.affirm.common.dao.LoanApplicationDAO
import com.affirm.common.model.catalog.CountryParam
import com.affirm.common.model.transactional.LoanApplication
import com.affirm.common.model.transactional.LoanOffer
import com.affirm.fdlm.FdlmServiceCall
import com.affirm.fdlm.creditoconsumo.request.ConsultaCreditoRequest
import com.affirm.fdlm.datacredito.model.ParametrosWSF
import com.affirm.fdlm.listascontrol.model.ConsultaPersonaRequest
import com.affirm.fdlm.listascontrol.model.ConsultaSolicitudRequest
import com.affirm.fdlm.topaz.model.ExecuteRequest
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseConfig
import groovy.transform.CompileStatic
import org.json.JSONObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired

import java.text.SimpleDateFormat

@CompileStatic
class FdlmServiceTest extends BaseConfig {

    @Autowired
    private FdlmServiceCall fdlmServiceCall
    @Autowired
    private LoanApplicationDAO loanApplicationDAO

    @Test
    void shouldNotFailRequestWithServiceName() {
        Integer loanApplicationId = 8940
        ExecuteRequest executeRequest = new ExecuteRequest()
        executeRequest.setServiceName("NumeradoresServicio")
        executeRequest.setNumero("9423")
        executeRequest.setSucursal("900")

        Executable executable = {
            fdlmServiceCall.callExecute(executeRequest, loanApplicationId)
        }

        Assertions.assertDoesNotThrow(executable)
    }

    @Test
    void shouldNotFailRequestConsultaPersona() {
        LoanApplication loanApplication = new LoanApplication()
        loanApplication.setId(345533)
        loanApplication.setPersonId(286705)

        Executable executable = {
            fdlmServiceCall.callConsultarPersona(loanApplication)
        }

        Assertions.assertDoesNotThrow(executable)
    }

    @Test
    void shouldNotFailRequestConsultaSolicitud() {
        Integer loanApplicationId = 8940

        ConsultaSolicitudRequest request = new ConsultaSolicitudRequest()
        request.setNumeroSolicitud("1081931459")
        request.setUsuarioConsulta("bellanire.florez")
        request.setAplicacionConsulta("Intranet")

        Executable executable = {
            fdlmServiceCall.callConsultarSolicitud(request, loanApplicationId)
        }

        Assertions.assertDoesNotThrow(executable)
    }

    @Test
    void shouldNotFailRequestGetDatosCliente() {
        Integer loanApplicationId = 8940
//
//        ParametrosWSF request = new ParametrosWSF()
//        request.setTipoId("C")
//        request.setNumeroId("10237621")
//        request.setNombre("toro")
//        request.setTipoConsulta("1")
//        request.setSucursal("104")
//        request.setCentralRiesgo("2")
//        request.setIdSolicitud(null)
//        request.setUsuario("LJC3")
//        request.setTipoResultado("1")

        Executable executable = {
            fdlmServiceCall.callGetDatosCliente(loanApplicationDAO.getLoanApplication(10349, Configuration.defaultLocale))
        }

        Assertions.assertDoesNotThrow(executable)
    }

    @Test
    void shouldNotFailRequestGetExisteInformacionLocal() {
        Integer loanApplicationId = 8940

        ParametrosWSF request = new ParametrosWSF()
        request.setTipoId("C")
        request.setNumeroId("91271238")
        request.setNombre("RODRIGUEZ")
        request.setTipoConsulta("1")
        request.setSucursal("104")
        request.setCentralRiesgo("2")
        request.setIdSolicitud(null)
        request.setUsuario("feac")
        request.setTipoResultado("1")

        Executable executable = {
            fdlmServiceCall.callGetExiteInformacionLocal(request, loanApplicationId)
        }

        Assertions.assertDoesNotThrow(executable)
    }

    @Test
    void shouldNotFailRequestGetProximaConsulta() {
        Integer loanApplicationId = 8940

        ParametrosWSF request = new ParametrosWSF()
        request.setTipoId("C")
        request.setNumeroId("91271238")
        request.setNombre("RODRIGUEZ")
        request.setTipoConsulta("1")
        request.setSucursal("104")
        request.setCentralRiesgo("2")
        request.setIdSolicitud("112399")
        request.setUsuario("feac")
        request.setTipoResultado("1")

        Executable executable = {
            fdlmServiceCall.callGetProximaConsulta(request, loanApplicationId)
        }

        Assertions.assertDoesNotThrow(executable)
    }

    @Test
    void shouldNotFailRequestGetReporteClienteTPZ() {
        Integer loanApplicationId = 8940

        ParametrosWSF request = new ParametrosWSF()
        request.setTipoId("C")
        request.setNumeroId("91271238")
        request.setNombre("RODRIGUEZ")
        request.setTipoConsulta("1")
        request.setSucursal("104")
        request.setCentralRiesgo("2")
        request.setIdSolicitud("112399")
        request.setUsuario("feac")
        request.setTipoResultado("1")

        Executable executable = {
            fdlmServiceCall.callGetReporteClienteTPZ(request, loanApplicationId)
        }

        Assertions.assertDoesNotThrow(executable)
    }

    @Test
    void shouldNotFailRequestGetUltimaConsulta() {
        Integer loanApplicationId = 8940

        ParametrosWSF request = new ParametrosWSF()
        request.setTipoId("C")
        request.setNumeroId("91271238")
        request.setNombre("RODRIGUEZ")
        request.setTipoConsulta("1")
        request.setSucursal("104")
        request.setCentralRiesgo("2")
        request.setIdSolicitud("")
        request.setUsuario("feac")
        request.setTipoResultado("1")

        Executable executable = {
            fdlmServiceCall.callGetUltimaConsulta(request, loanApplicationId)
        }

        Assertions.assertDoesNotThrow(executable)
    }

    @Test
    void shouldNotFailRequestConsultaCredito() {
        LoanApplication loanApplication = new LoanApplication()
        loanApplication.setId(10349)
        loanApplication.setPersonId(9877)

        Executable executable = {
            fdlmServiceCall.callConsultarCredito(loanApplication)
        }

        Assertions.assertDoesNotThrow(executable)
    }

    /*@Test
    void shouldNotFailReuestObtenerSucursal() {
        LoanApplication loanApplication = new LoanApplication()
        loanApplication.setId(10322)
        loanApplication.setPersonId(9850)

        Executable executable = {
            fdlmServiceCall.callObtenerSucursal(loanApplication)
        }

        Assertions.assertDoesNotThrow(executable)

        loanApplication.setId(10321)
        loanApplication.setPersonId(9849)

        Executable executable1 = {
            fdlmServiceCall.callObtenerSucursal(loanApplication)
        }

        Assertions.assertDoesNotThrow(executable1)
    }*/

    @Test
    void shouldNotFailRequestCrearCredito() {
        LoanApplication loanApplication = new LoanApplication()
        loanApplication.setId(10342)
        loanApplication.setPersonId(9870)
        loanApplication.setFirstDueDate(new SimpleDateFormat("yyyy-MM-dd").parse("2020-03-30"))
        loanApplication.setEntityCustomData(new JSONObject("{\"fdmProcedenciaSolicitud\":4}"))

        LoanOffer loanOffer = new LoanOffer()
        loanOffer.setInstallmentAmmount(150000d)
        loanOffer.setInstallments(24)
        loanOffer.setAmmount(1000000d)

        Executable executable = {
            fdlmServiceCall.callCrearCredito(loanApplication, loanOffer)
        }

        Assertions.assertDoesNotThrow(executable)
    }
}
