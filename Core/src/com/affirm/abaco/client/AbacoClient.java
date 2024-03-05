package com.affirm.abaco.client;

import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.RestApiDAO;
import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.catalog.UserFileType;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EntityWebServiceUtil;
import com.affirm.common.service.FileService;
import com.affirm.common.service.UserService;
import com.affirm.common.service.impl.EntityWebServiceUtilImpl;
import com.affirm.equifax.ws.CuotaHistorica;
import com.affirm.equifax.ws.DirectorioPersona;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by miberico on 11/04/17.
 */
@Service
public class AbacoClient {

    private static Logger logger = Logger.getLogger(AbacoClient.class);

    @Autowired
    TranslatorDAO translatorDAO;
    @Autowired
    FileService fileService;
    @Autowired
    UserDAO userDao;
    @Autowired
    PersonDAO personDao;
    @Autowired
    UserService userService;
    @Autowired
    RestApiDAO restApiDao;
    @Autowired
    CatalogService catalogService;
    @Autowired
    EntityWebServiceUtil entityWebServiceUtil;

    private int entityId = 7; //ABACO
    private int solvenWsClientId = 3; //SOLVEN
    private String solesId = "S/";
    private DateFormat df = new SimpleDateFormat("yyyyMMdd");

    private ESocio populateSocioAbaco(String socioAbacoId, User user, DirectorioPersona directorioPersona, PersonOcupationalInformation personOcupationalInformation, CuotaHistorica cuotaHistorica, PersonContactInformation contactInformation) throws Exception {
        ESocio socio = new ESocio();

        if (socioAbacoId != null && !socioAbacoId.isEmpty())
            socio.setIdSocio(socioAbacoId);
        socio.setNombres(user.getPerson().getName().substring(0, Math.min(user.getPerson().getName().length(), 30)));
        socio.setApellidoPaterno(user.getPerson().getFirstSurname().substring(0, Math.min(user.getPerson().getFirstSurname().length(), 20)));
        socio.setApellidoMaterno(user.getPerson().getLastSurname() != null ? user.getPerson().getLastSurname().substring(0, Math.min(user.getPerson().getLastSurname().length(), 20)) : "NN");
        socio.setNacionalidad(user.getPerson().getNationality() != null ? translatorDAO.translate(entityId, 9, user.getPerson().getNationality().getId().toString(), null) : null);
        socio.setFechaNacimiento(df.format(user.getPerson().getBirthday()));
        String gradoInstruccion = translatorDAO.translate(entityId, 7, user.getPerson().getStudyLevel().getId().toString(), null);
        if (gradoInstruccion != null && !gradoInstruccion.isEmpty())
            socio.setGradoInstruccion(gradoInstruccion.substring(0, Math.min(gradoInstruccion.length(), 1)));
        if(personOcupationalInformation != null && personOcupationalInformation.getOcupation() != null){
            String ocupacion = translatorDAO.translate(entityId, 8, personOcupationalInformation.getOcupation().getId().toString(), null);
            if (ocupacion != null && !ocupacion.isEmpty())
                socio.setOcupacion(ocupacion.substring(0, Math.min(ocupacion.length(), 2)));
        }
        if (user.getPerson().getProfession() != null)
            socio.setProfesion(translatorDAO.translate(entityId, 41, user.getPerson().getProfession().getId().toString(), null));
        socio.setTipoDomicilio(translatorDAO.translate(entityId, 42, contactInformation.getHousingType().getId().toString(), null));

        socio.setFechaIngresoEmpleador(df.format(personOcupationalInformation.getStartDate()));
        socio.setTipoDocumento(translatorDAO.translate(entityId, 1, user.getPerson().getDocumentType().getId().toString(), null));
        socio.setNroDocumento(user.getPerson().getDocumentNumber().substring(0, Math.min(user.getPerson().getDocumentNumber().length(), 11)));
        socio.setClasePersona(translatorDAO.translate(entityId, 10, user.getPerson().getDocumentType().getId().toString(), null));
        socio.setGenero(user.getPerson().getGender() != null ? translatorDAO.translate(entityId, 6, user.getPerson().getGender().toString(), null) : null);
        socio.setEstadoCivil(user.getPerson().getMaritalStatus() != null ? translatorDAO.translate(entityId, 3, user.getPerson().getMaritalStatus().getId().toString(), null) : null);
        if (contactInformation.getAddressStreetName() != null && !contactInformation.getAddressStreetName().isEmpty())
            socio.setDireccion(contactInformation.getAddressStreetName().substring(0, Math.min(contactInformation.getAddressStreetName().length(), 50)));

        PersonContactInformation personContactInformation = personDao.getPersonContactInformation(Configuration.getDefaultLocale(), user.getPersonId());
        socio.setCiiu(translatorDAO.translate(entityId, 43, personContactInformation.getAddressUbigeo().getUbigeo(), null));
        if (user.getPerson().getLandline() != null && !user.getPerson().getLandline().isEmpty())
            socio.setTelefonoFijo(user.getPerson().getLandline().substring(0, Math.min(user.getPerson().getLandline().length(), 12)));
        socio.setCelular(user.getPhoneNumber().substring(0, Math.min(user.getPhoneNumber().length(), 12)));
        socio.setTipoActividad(translatorDAO.translate(entityId, 4, personOcupationalInformation.getActivityType().getId().toString(), null));
        socio.setNombreEmpleador(translatorDAO.translate(entityId, 40, personOcupationalInformation.getEmployerId().toString(), null));
        if (personOcupationalInformation.getAddress() != null && !personOcupationalInformation.getAddress().isEmpty())
            socio.setDireccionEmpleador(personOcupationalInformation.getAddress().substring(0, Math.min(personOcupationalInformation.getAddress().length(), 50)));
        socio.setTelefonoEmpleador(personOcupationalInformation.getPhoneNumber().substring(0, Math.min(personOcupationalInformation.getPhoneNumber().length(), 12)));
        socio.setTiempoPermanencia(personOcupationalInformation.getEmploymentTime());
        socio.setRucEmpleador(personOcupationalInformation.getRuc().substring(0, Math.min(personOcupationalInformation.getRuc().length(), 11)));
        socio.setIngresoNetoMensual(personOcupationalInformation.getFixedGrossIncome() != null ? personOcupationalInformation.getFixedGrossIncome() : personOcupationalInformation.getMonthlyNetIncome() != null ? personOcupationalInformation.getMonthlyNetIncome() : 0);
        socio.setIngresoBrutoMensual(personOcupationalInformation.getFixedGrossIncome() != null ? personOcupationalInformation.getFixedGrossIncome() : 0);

        socio.setEmpresaEsSocio(personOcupationalInformation.getShareholderShareholding());
        socio.setEmail(user.getEmail().substring(0, Math.min(user.getEmail().length(), 30)));
        if (cuotaHistorica != null && cuotaHistorica.getDeudasPersonales() != null && cuotaHistorica.getDeudasPersonales().getEntidades() != null && cuotaHistorica.getDeudasPersonales().getEntidades().getEntidad() != null && cuotaHistorica.getDeudasPersonales().getEntidades().getEntidad().size() > 0) {
            if (cuotaHistorica.getDeudasPersonales().getEntidades().getEntidad().get(0).getNombre() != null && !cuotaHistorica.getDeudasPersonales().getEntidades().getEntidad().get(0).getNombre().isEmpty())
                socio.setEntidadFinanciadora(cuotaHistorica.getDeudasPersonales().getEntidades().getEntidad().get(0).getNombre().substring(0, Math.min(cuotaHistorica.getDeudasPersonales().getEntidades().getEntidad().get(0).getNombre().length(), 30)));
            if (cuotaHistorica.getDeudasPersonales().getEntidades().getEntidad().get(0).getDeudas().getDeuda().size() > 0) {
                if (cuotaHistorica.getDeudasPersonales().getEntidades().getEntidad().get(0).getDeudas().getDeuda().get(0).getLineaTotal().getMonto().size() > 0 && cuotaHistorica.getDeudasPersonales().getEntidades().getEntidad().get(0).getDeudas().getDeuda().get(0).getLineaTotal().getMonto().get(0).getValue() != null) {
                    socio.setMontoLineaCredito(cuotaHistorica.getDeudasPersonales().getEntidades().getEntidad().get(0).getDeudas().getDeuda().get(0).getLineaTotal().getMonto().get(0).getValue().doubleValue());
                    socio.setTipoPrestamo(cuotaHistorica.getDeudasPersonales().getEntidades().getEntidad().get(0).getDeudas().getDeuda().get(0).getTipoBanca());
                }
            }
        }

        user.getPerson().setPartner(personDao.getPerson(catalogService, Configuration.getDefaultLocale(), user.getPersonId(), true).getPartner());
        if (user.getPerson().getPartner() != null) {
            if (user.getPerson().getPartner().getName() != null && !user.getPerson().getPartner().getName().isEmpty())
                socio.setNombresConyugue(user.getPerson().getPartner().getName().substring(0, Math.min(user.getPerson().getPartner().getName().length(), 30)));
            if (user.getPerson().getPartner().getFirstSurname() != null && !user.getPerson().getPartner().getFirstSurname().isEmpty())
                socio.setApellidoPaternoConyugue(user.getPerson().getPartner().getFirstSurname().substring(0, Math.min(user.getPerson().getPartner().getFirstSurname().length(), 20)));
            if (user.getPerson().getPartner().getLastSurname() != null && !user.getPerson().getPartner().getLastSurname().isEmpty())
                socio.setApellidoMaternoConyugue(user.getPerson().getPartner().getLastSurname().substring(0, Math.min(user.getPerson().getPartner().getLastSurname().length(), 20)));
            if (user.getPerson().getPartner().getNationality() != null && user.getPerson().getPartner().getNationality().getId() != null)
                socio.setNacionalidadConyugue(translatorDAO.translate(entityId, 9, user.getPerson().getPartner().getNationality().getId().toString(), null));
            if (user.getPerson().getPartner().getBirthday() != null)
                socio.setFechaNacimientoConyugue(df.format(user.getPerson().getPartner().getBirthday()));
            if (user.getPerson().getPartner().getDocumentType() != null && user.getPerson().getPartner().getDocumentType().getId() != null)
                socio.setTipoDocumentoConyugue(translatorDAO.translate(entityId, 1, user.getPerson().getPartner().getDocumentType().getId().toString(), null));
            if (user.getPerson().getPartner().getDocumentNumber() != null && !user.getPerson().getPartner().getDocumentNumber().isEmpty())
                socio.setNroDocumentoConyugue(user.getPerson().getPartner().getDocumentNumber().substring(0, Math.min(user.getPerson().getPartner().getDocumentNumber().length(), 11)));
            if (user.getPerson().getPartner().getLandline() != null && !user.getPerson().getPartner().getLandline().isEmpty())
                socio.setTelefonoConyugue(user.getPerson().getPartner().getLandline().substring(0, Math.min(user.getPerson().getPartner().getLandline().length(), 11)));
            if (user.getPerson().getPartner().getGender() != null && !user.getPerson().getPartner().getGender().toString().isEmpty())
                socio.setGeneroConyugue(translatorDAO.translate(entityId, 6, user.getPerson().getPartner().getGender().toString(), null));
            if (user.getPerson().getPartner().getMaritalStatus() != null && user.getPerson().getPartner().getMaritalStatus().getId() != null)
                socio.setEstadoCivilConyugue(translatorDAO.translate(entityId, 3, user.getPerson().getPartner().getMaritalStatus().getId().toString(), null));
        }
        System.out.println(socio.toString());
        return socio;
    }

    private ECredito populateCreditoAbaco(String socioId, String creditoCancelar, Double montoCreditoCancelar, Credit credito, Integer creditoAbacoToUpdate) throws Exception {
        ECredito creditoAbaco = new ECredito();

        creditoAbaco.setCodigoCredito(credito.getId());
        if(creditoAbacoToUpdate != null)
            creditoAbaco.setCodigoCreditoAbaco(creditoAbacoToUpdate);
        String soles = translatorDAO.translate(entityId, 5, solesId, null);
        creditoAbaco.setMonedaCredito(soles.substring(0, Math.min(soles.length(), 2)));
        creditoAbaco.setMontoCredito(credito.getAmount());
        creditoAbaco.setMontoDesembolsar(credito.getAmount() - montoCreditoCancelar);
        creditoAbaco.setNumeroCuotas(credito.getInstallments());
        creditoAbaco.setRazonCredito(Integer.valueOf(translatorDAO.translate(entityId, 12, credito.getLoanApplicationReason().getId() + "", null)));
        creditoAbaco.setTipoCredito(Integer.valueOf(translatorDAO.translate(entityId, 13, credito.getProduct().getId().toString(), null)));
        creditoAbaco.setTea(credito.getEffectiveAnnualRate());
        creditoAbaco.setTcea(credito.getEffectiveAnnualCostRate());
        creditoAbaco.setComision(credito.getCommission());
        creditoAbaco.setImpuestoComision(credito.getCommissionIgv());
        creditoAbaco.setTotalComision(credito.getTotalCommission());
        if (credito.getSignatureDate() != null)
            creditoAbaco.setFechaFirma(df.format(credito.getSignatureDate()));
        else {
            Calendar now = Calendar.getInstance();
            creditoAbaco.setFechaFirma(df.format(now.getTime()));
        }

        if (credito.getBankAccount() != null) {
            creditoAbaco.setCodigoBanco(Integer.valueOf(translatorDAO.translate(entityId, 11, credito.getBank().getId() + "", null)));
            creditoAbaco.setNumeroCuentaAsociado(credito.getBankAccount().substring(0, Math.min(credito.getBankAccount().length(), 30)));
        } else {
            throw new Exception("El usuario no tiene número de cuenta asociado.");
        }

        creditoAbaco.setTipoCuentaAsociado(translatorDAO.translate(entityId, 2, credito.getBankAccountType().toString(), null));

        List<ECuota> cuotasAbaco = new ArrayList<>();
        for (OriginalSchedule cuota : credito.getOriginalSchedule()) {
            ECuota cuotaAbaco = new ECuota();
            cuotaAbaco.setCodigoCuota(cuota.getInstallmentId());
            cuotaAbaco.setFechaVencimientoCuota(df.format(cuota.getDueDate()));
            cuotaAbaco.setRemanenteCapital(cuota.getRemainingCapital());
            cuotaAbaco.setCapitalCuota((double)Math.round(cuota.getInstallmentCapital() * 100d) / 100d);
            cuotaAbaco.setInteresCuota(cuota.getInterest());
            cuotaAbaco.setSubtotalCuota(cuota.getInstallment());
            cuotaAbaco.setMontoSeguro(cuota.getInsurance());
            cuotaAbaco.setTotalCuota(cuota.getInstallmentAmount());
            cuotaAbaco.setComision(cuota.getCollectionCommission());
            cuotaAbaco.setImpuestoComision(cuota.getCollectionCommissionTax());
            cuotasAbaco.add(cuotaAbaco);
        }

        ECredito.Cuotas cuotas = new ECredito.Cuotas();
        cuotas.setCuota(cuotasAbaco);
        creditoAbaco.setCuotas(cuotas);

        creditoAbaco.setCodigoCliente(socioId);
        creditoAbaco.setCodigoCreditoCancelar(creditoCancelar);
        creditoAbaco.setMontoCreditoCancelar(montoCreditoCancelar);

        if (credito.getContractUserFileId() != null && !credito.getContractUserFileId().isEmpty()) {
            creditoAbaco.setUrlDescargaContrato(fileService.generatePublicUserFileUrl(credito.getContractUserFileId().get(0), false));
        }

        List<LoanApplicationUserFiles> loanApplicationFiles = personDao.getLoanApplicationFiles(credito.getLoanApplicationId(), credito.getPersonId(), Configuration.getDefaultLocale());
        if (loanApplicationFiles != null) {
            List<UserFile> allLoanAppFiles = loanApplicationFiles
                    .stream()
                    .flatMap(x -> x.getUserFileList().stream())
                    .collect(Collectors.toList());

            List<UserFile> allServicesImages = userService.getUserFileByType(allLoanAppFiles, UserFileType.COMPROBANTE_DIRECCION);
            if (allServicesImages != null && !allServicesImages.isEmpty()) {
                creditoAbaco.setUrlDescargaSelfie(fileService.generatePublicUserFileUrl(allServicesImages.get(0).getId(), false));
            }

            List<UserFile> allIDImages = userService.getUserFileByType(allLoanAppFiles, UserFileType.DNI_MERGED);
            if (allIDImages != null && !allIDImages.isEmpty()) {
                creditoAbaco.setUrlDescargaDNI(fileService.generatePublicUserFileUrl(allIDImages.get(0).getId(), false));
            }else{
                List<UserFile> allDni = userService.getUserFileByType(allLoanAppFiles, UserFileType.DNI_FRONTAL, UserFileType.DNI_ANVERSO);
                if (allDni != null && !allDni.isEmpty()) {
                    creditoAbaco.setUrlDescargaDNI(fileService.generatePublicUserFileUrl(allDni.get(allDni.size() - 1).getId(), false));
                }
            }
        }
        return creditoAbaco;
    }

    public EMensaje enviarSocioAbaco(String socioAbacoId, User user, DirectorioPersona directorioPersona, PersonOcupationalInformation personOcupationalInformation, CuotaHistorica cuotaHistorica, PersonContactInformation contactInformation, Integer loanApplicationId) throws Exception {
        WSCreditoService creditoServ = new WSCreditoService();
        WSCredito credito = creditoServ.getWSCreditoPort();
        ESocio socio = populateSocioAbaco(socioAbacoId, user, directorioPersona, personOcupationalInformation, cuotaHistorica, contactInformation);
        authHeader(credito);
        EntityWebServiceLog<EMensaje> response;
        if (socioAbacoId == null || socioAbacoId.isEmpty()) {
            response = entityWebServiceUtil.callSoapWs(
                    catalogService.getEntityWebService(EntityWebService.ABACO_CREAR_SOCIO),
                    (BindingProvider) credito,
                    loanApplicationId,
                    new EntityWebServiceUtilImpl.ISOAPProcess() {
                        @Override
                        public EMensaje process() throws Exception {
                            return credito.crearSocio(socio);
                        }
                    });
        } else {
            response = entityWebServiceUtil.callSoapWs(
                    catalogService.getEntityWebService(EntityWebService.ABACO_ACTUALIZAR_SOCIO),
                    (BindingProvider) credito,
                    loanApplicationId,
                    new EntityWebServiceUtilImpl.ISOAPProcess() {
                        @Override
                        public EMensaje process() throws Exception {
                            return credito.actualizarSocio(socio);
                        }
                    });
        }

        if(response.getSoapResponse() == null || !response.getSoapResponse().isExitoso()){
            entityWebServiceUtil.updateLogStatusToFailed(response.getId(), true);
        }
        return response.getSoapResponse();
    }

    public ERptaCredito esSocioAbaco(int documentType, String documentNumber, Integer loanApplicationId) throws Exception {
        WSCreditoService creditoServ = new WSCreditoService();
        WSCredito credito = creditoServ.getWSCreditoPort();
        authHeader(credito);


        EntityWebServiceLog<ERptaCredito> response = entityWebServiceUtil.callSoapWs(
                catalogService.getEntityWebService(EntityWebService.ABACO_ES_SOCIO),
                (BindingProvider) credito,
                loanApplicationId,
                new EntityWebServiceUtilImpl.ISOAPProcess() {
                    @Override
                    public ERptaCredito process() throws Exception {
                        return credito.esSocio(translatorDAO.translate(entityId, 1, documentType + "", null), documentNumber);
                    }
                });

        ERptaCredito socio = response.getSoapResponse();
        if(socio == null || !socio.isExitoso()){
            entityWebServiceUtil.updateLogStatusToFailed(response.getId(), true);
        }

        return socio != null && socio.getIdSocio() != null ? socio : null;
    }

    public ERptaCredito crearCredito(String socioId, String creditoCancelar, Double montoCreditoCancelar, Credit objCredito, Integer loanApplicationId, Integer creditoAbacoToUpdate) throws Exception {
        WSCreditoService creditoServ = new WSCreditoService();
        WSCredito credito = creditoServ.getWSCreditoPort();
        authHeader(credito);

        ECredito creditToSend = populateCreditoAbaco(socioId, creditoCancelar, montoCreditoCancelar, objCredito, creditoAbacoToUpdate);
        EntityWebServiceLog<ERptaCredito> response = entityWebServiceUtil.callSoapWs(
                catalogService.getEntityWebService(EntityWebService.ABACO_CREAR_CREDITO),
                (BindingProvider) credito,
                loanApplicationId,
                new EntityWebServiceUtilImpl.ISOAPProcess() {
                    @Override
                    public ERptaCredito process() throws Exception {
                        return credito.crearCredito(creditToSend);
                    }
                });

        if(response.getSoapResponse() == null || !response.getSoapResponse().isExitoso()){
            entityWebServiceUtil.updateLogStatusToFailed(response.getId(), true);
        }
        return response.getSoapResponse();
    }

    private String encodeSignature(String secret, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return URLEncoder.encode(org.apache.commons.codec.binary.Base64.encodeBase64String(sha256_HMAC.doFinal(data.getBytes("UTF-8"))), "UTF-8");
    }

    private void authHeader(WSCredito credito) throws Exception {
        long requestUnixTime = Instant.now().getEpochSecond();
        WsClient wsClient = restApiDao.getWsClientByWsClient(3);
        String signature = wsClient.getApiKeySharedKey() + "=" + encodeSignature(wsClient.getApiKeySecret(), wsClient.getApiKeySharedKey() + requestUnixTime + "/servicio/WSCredito") + "=" + requestUnixTime;

        Map<String, Object> requestContext = ((BindingProvider) credito).getRequestContext();
        Map<String, List<String>> requestHeaders = new HashMap<>();
        System.out.println("Authorization : " + signature);
        requestHeaders.put("Authorization", Collections.singletonList(signature));
        requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, requestHeaders);
    }

}
