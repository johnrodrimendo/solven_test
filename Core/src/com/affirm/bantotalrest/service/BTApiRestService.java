package com.affirm.bantotalrest.service;

import com.affirm.acceso.model.Direccion;
import com.affirm.bantotalrest.exception.BT40147Exception;
import com.affirm.bantotalrest.model.RBTPCO10.BTPersonasAgregarFATCAResponse;
import com.affirm.bantotalrest.model.RBTPCO12.BTPersonasObtenerFATCAResponse;
import com.affirm.bantotalrest.model.RBTPG006.BTClientesObtenerCuentasAhorroResponse;
import com.affirm.bantotalrest.model.RBTPG007.BTCuentasDeAhorroObtenerDatosResponse;
import com.affirm.bantotalrest.model.RBTPG012.BTPrestamoObtenerDetalleResponse;
import com.affirm.bantotalrest.model.RBTPG011.ObtenerPrestamosClienteResponse;
import com.affirm.bantotalrest.model.RBTPG013.BTClientesObtenerPlazosFijosResponse;
import com.affirm.bantotalrest.model.RBTPG015.BTClientesObtenerTarjetasDebitoResponse;
import com.affirm.bantotalrest.model.RBTPG019.BTPersonasValidarEnListasNegrasResponse;
import com.affirm.bantotalrest.model.RBTPG027.BTClientesCrearResponse;
import com.affirm.bantotalrest.model.RBTPG030.BTCuentasDeAhorroContratarProductoResponse;
import com.affirm.bantotalrest.model.RBTPG034.PagarCuotaResponse;
import com.affirm.bantotalrest.model.RBTPG036.BTPersonasObtenerProfesionesResponse;
import com.affirm.bantotalrest.model.RBTPG042.BTConfiguracionBantotalObtenerActividadesResponse;
import com.affirm.bantotalrest.model.RBTPG054.BTCuentasDeAhorroObtenerProductosResponse;
import com.affirm.bantotalrest.model.RBTPG066.BTTarjetasDeDebitoCrearResponse;
import com.affirm.bantotalrest.model.RBTPG072.BTPrestamosObtenerCronogramaResponse;
import com.affirm.bantotalrest.model.RBTPG075.BTPrestamosSimularRequest;
import com.affirm.bantotalrest.model.RBTPG075.BTPrestamosSimularResponse;
import com.affirm.bantotalrest.model.RBTPG077.BTPrestamosContratarResponse;
import com.affirm.bantotalrest.model.RBTPG079.BTConfiguracionBantotalObtenerPizarrasResponse;
import com.affirm.bantotalrest.model.RBTPG085.BTPersonasObtenerResponse;
import com.affirm.bantotalrest.model.RBTPG100.BTCuentasDeAhorroContratarConFacultadesResponse;
import com.affirm.bantotalrest.model.RBTPG146.BTPersonasAgregarDatosPEPResponse;
import com.affirm.bantotalrest.model.RBTPG182.ObtenerCuentasClienteResponse;
import com.affirm.bantotalrest.model.RBTPG211.BTClientesObtenerCuentaClienteResponse;
import com.affirm.bantotalrest.model.RBTPG218.BTClientesCrearConPersonaExistenteResponse;
import com.affirm.bantotalrest.model.RBTPG220.ObtenerIdentificadorUnicoResponse;
import com.affirm.bantotalrest.model.RBTPG265.BTPrestamosSimularAmortizableSinClienteRequest;
import com.affirm.bantotalrest.model.RBTPG292.BTPersonasObtenerDatosPEPResponse;
import com.affirm.bantotalrest.model.RBTPG328.BTPersonasActualizarProfesionResponse;
import com.affirm.bantotalrest.model.authentication.AuthenticationResponse;
import com.affirm.bantotalrest.model.common.BtError;
import com.affirm.bantotalrest.model.common.SBTPCOInformacionFATCA;
import com.affirm.bantotalrest.model.common.SBTPersona1;
import com.affirm.bantotalrest.model.common.SdtDatosPEP;
import com.affirm.bantotalrest.model.customs.BPBAZServicesGenerarCCIResponse;
import com.affirm.bantotalrest.model.customs.BPBAZServicesObtieneCCIResponse;
import com.affirm.bantotalrest.model.customs.BTCorresponsalesConsultaCreditosResponse;
import com.affirm.bantotalrest.model.customs.BTCorresponsalesObtenerDetallePagoDeCuotaResponse;
import com.affirm.bantotalrest.model.customs.BTCorresponsalesPagoDeCuotaResponse;
import com.affirm.bantotalrest.model.customs.*;
import com.affirm.bantotalrest.model.customs.ActualizarEnvioEstadoCuentaResponse;
import com.affirm.bantotalrest.model.customs.AgregarEnvioEstadoCuentaResponse;
import com.affirm.bantotalrest.model.customs.BPBAZServicesObtieneCCIResponse;
import com.affirm.bantotalrest.model.customs.ObtenerEnvioEstadoCuentaResponse;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.User;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface BTApiRestService {

    AuthenticationResponse authenticateRequest(Integer entityId) throws Exception;

    ObtenerIdentificadorUnicoResponse obtenerIdentificadorUnico(LoanApplication loanApplication, Person person, String token) throws Exception;

    BTPersonasObtenerResponse btPersonasObtener(LoanApplication loanApplication, Long personaUId, String token) throws Exception;

    ObtenerCuentasClienteResponse obtenerCuentasCliente(LoanApplication loanApplication, Long personaUId, String token) throws Exception;

    BTClientesObtenerCuentasAhorroResponse BtClientesObtenerCuentasAhorro(LoanApplication loanApplication, Long clienteUId, String token) throws Exception;

    BTCuentasDeAhorroObtenerProductosResponse btCuentasDeAhorroObtenerProductos(LoanApplication loanApplication, String token) throws Exception;

    BTCuentasDeAhorroContratarProductoResponse btCuentasDeAhorroContratarProducto(LoanApplication loanApplication, String token, Long clienteUId, Long productoUId, String nombreSubCuenta) throws Exception;

    BTPrestamosSimularResponse btPrestamosSimularResponse(LoanApplication loanApplication, String token, BTPrestamosSimularRequest.SBTPrestamoAlta sbtPrestamoAlta) throws BT40147Exception, Exception;

    BTPrestamosSimularResponse btPrestamosSimularSinCliente(LoanApplication loanApplication, String token, BTPrestamosSimularAmortizableSinClienteRequest.SBTDatosAmortizable sdtDatosAmortizable) throws BT40147Exception, Exception;

    BTClientesCrearConPersonaExistenteResponse btClientesCrearConPersonaExistente(LoanApplication loanApplication, String token, Long personaUId, Integer sectorId, Integer clasificacionInternaId, Integer ejecutivoId) throws Exception;

    BTClientesCrearResponse btClientesCrear(LoanApplication loanApplication, String token, SBTPersona1 sBTPersona1) throws Exception;

    BTPrestamosContratarResponse btPrestamosContratar(LoanApplication loanApplication, String token, Long operacionUId, Long clienteUId, Long operacionUId_desembolso, Long operacionUId_cobro) throws Exception;

    BTConfiguracionBantotalObtenerPizarrasResponse btConfiguracionBantotalObtenerPizarras(LoanApplication loanApplication, String token) throws Exception;

    BTConfiguracionBantotalObtenerActividadesResponse btConfiguracionBantotalObtenerActividades(LoanApplication loanApplication, String token) throws Exception;

    String convertDateToStringFormat(Date date);

    Date convertStringToDate(String date) throws ParseException;

    String convertDateToHourStringFormat(Date date);

    SBTPersona1 createSBTPersona1Object(LoanApplication loanApplication, Person person, User user) throws Exception;

    BTCorresponsalesObtenerDetallePagoDeCuotaResponse obtenerDetallePagoDeCuota(LoanApplication loanApplication, Long transactionId, String token) throws Exception;

    BTPersonasValidarEnListasNegrasResponse validarEnListasNegras(LoanApplication loanApplication, Person person, String token) throws Exception;

    BTPersonasAgregarFATCAResponse btPersonasAgregarFATCA(LoanApplication loanApplication, Person person, Long personaUId, SBTPCOInformacionFATCA sbtpcoInformacionFATCA, String token) throws Exception;

    BTPersonasAgregarDatosPEPResponse btPersonasAgregarDatosPEP(LoanApplication loanApplication, Long personaUId, String isPEP, SdtDatosPEP sdtDatosPEP, String token) throws Exception;

    BTPersonasObtenerFATCAResponse btPersonasObtenerFATCA(LoanApplication loanApplication, Long personaUId, String token) throws Exception;

    BTPersonasObtenerDatosPEPResponse btPersonasObtenerDatosPEP(LoanApplication loanApplication, Long personaUId, String token) throws Exception;

    BTClientesObtenerTarjetasDebitoResponse btClientesObtenerTarjetasDebito(LoanApplication loanApplication, Long clienteUId, String token) throws Exception;

    BTTarjetasDeDebitoCrearResponse btTarjetasDeDebitoCrear(LoanApplication loanApplication, Long clienteUId, String tipoTarjeta, String nombreTarjeta, String token) throws Exception;

    BTCuentasDeAhorroContratarConFacultadesResponse btCuentasDeAhorroContratarConFacultades(LoanApplication loanApplication, String token, Long clienteUId, Long productoUId, String nombreSubCuenta, String tipoIntegracion) throws Exception;

    BTPrestamoObtenerDetalleResponse btPrestamosObtenerDetalle(LoanApplication loanApplication, Long operacionUId, String token) throws Exception;

    BTCuentasDeAhorroObtenerDatosResponse btCuentasDeAhorroObtenerDatos(LoanApplication loanApplication, Long operacionUId, String token) throws Exception;

    // Obtener Prestamos
    ObtenerPrestamosClienteResponse obtenerPrestamos(LoanApplication loanApplication, Long clienteUID, String token) throws Exception;

    BTPrestamosObtenerCronogramaResponse btPrestamosObtenerCronograma(LoanApplication loanApplication, Long operacionUId, String token) throws Exception;

    BTPersonasObtenerProfesionesResponse btPersonaObtenerProfesiones(LoanApplication loanApplication, String token) throws Exception;

    BTPersonasActualizarProfesionResponse btPersonasActualizarProfesion(LoanApplication loanApplication, Long personaUId, Long profesionUId, Date fechaInicio, String token) throws Exception;

    String getToken(Integer entityId) throws Exception;

    BPBAZServicesObtieneCCIResponse bpBAZServicesObtieneCCI(LoanApplication loanApplication, Long productoUId, String token) throws Exception;

    //PAGAR CUOTA
    BTCorresponsalesPagoDeCuotaResponse pagarCuota(LoanApplication loanApplication, Long Credito, Date fechaDePago, Double ImpPago, Double ImpCom, Double ImpRed, String RefUnica, Integer Canal, Integer tipoMov, String token) throws Exception;

    BTCorresponsalesConsultaCreditosResponse BTCorresponsalesConsultaCreditos(LoanApplication loanApplication, Person person, String token) throws Exception;

    BPBAZServicesGenerarCCIResponse bpBAZServicesGenerarCCI(LoanApplication loanApplication, Long operacionUId, String token) throws Exception;

    AgregarDomicilioBcoAztecaResponse btAgregarDomicilioBcoAzteca(LoanApplication loanApplication, AgregarDomicilioBcoAztecaRequest request, String token) throws Exception;

    AgregarDomicilioBcoAztecaRequest createAgregarDomicilioBcoAztecaObject(LoanApplication loanApplication, Person person, User user) throws Exception;

    BTClientesObtenerCuentaClienteResponse btClientesObtenerCuentaCliente(LoanApplication loanApplication, Long clienteUid, String token) throws Exception;

    ObtenerDireccionPersonaBcoAztecaResponse btObtenerDireccionPersonaBcoAztecaResponse(LoanApplication loanApplication, Person person, Integer doCod, String token) throws Exception;

    ObtenerDireccionCuentaBcoAztecaResponse btObtenerDireccionCuentaBcoAztecaResponse(LoanApplication loanApplication, String cuentaBT, Integer doCod, String token) throws Exception;

    <T extends ActualizarDireccionCommonBcoAztecaRequest> T crearActualizarDireccionCommonBcoAztecaRequest(LoanApplication loanApplication, Person person, User user, Integer doCod, Direccion direccion, Class<T> returnType) throws Exception;

    ActualizarDireccionPersonaBcoAztecaResponse btActualizarDireccionPersona(LoanApplication loanApplication,Person person, ActualizarDireccionPersonaBcoAztecaRequest requestBase, Integer doCod, String token) throws Exception;

    ActualizarDireccionCuentaBcoAztecaResponse btActualizarDireccionCuenta(LoanApplication loanApplication,Person person,String cuentaBT, ActualizarDireccionCuentaBcoAztecaRequest requestBase, Integer doCod, String token) throws Exception;

    boolean existsError(List<BtError> btErrors, List<String> codes);

    InhabilitarDireccionPersonaBcoAztecaResponse btInhabilitarDireccionPersonaBcoAztecaResponse(LoanApplication loanApplication,Person person, Integer doCod, String token) throws Exception;

    InhabilitarDireccionCuentaBcoAztecaResponse btInhabilitarDireccionCuentaBcoAztecaResponse(LoanApplication loanApplication, String cuentaBT, Integer doCod, String token) throws Exception;

    ObtenerEnvioEstadoCuentaResponse aBCuentasVistaObtenerEnvioEstadoCuenta(LoanApplication loanApplication, Long operacionUid, String token) throws Exception;

    ActualizarEnvioEstadoCuentaResponse aBCuentasVistaActualizarEnvioEstadoCuenta(LoanApplication loanApplication, Long operacionUid, String envioEC, String modoEnvioEC, String token) throws Exception;

    AgregarEnvioEstadoCuentaResponse aBCuentasVistaAgregarEnvioEstadoCuenta(LoanApplication loanApplication, Long operacionUid, String envioEC, String modoEnvioEC, String token) throws Exception;

    List<BtError> excludeErrorCodes(List<BtError> data, List<String> errorCodes);

    BTClientesObtenerPlazosFijosResponse btClientesObtenerPlazosFijos(LoanApplication loanApplication, Long clienteUId, String token) throws Exception;

    ObtenerTipoDeMovimientoResponse btCorresponsalesObtenerTipoMovimiento(LoanApplication loanApplication, Long operacionUId, Long campaniaId, String token) throws Exception;

    Date convertStringWithHourToDate(String date) throws ParseException;
}
