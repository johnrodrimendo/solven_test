/**
 *
 */
package com.affirm.common.service.impl;


import com.affirm.common.dao.CatalogDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.AfipActivitiy;
import com.affirm.common.model.catalog.Bureau;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.MaritalStatus;
import com.affirm.common.model.transactional.ApplicationBureau;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.service.ApplicationBureauUtil;
import com.affirm.common.service.BureauService;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.ErrorService;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.Marshall;
import com.affirm.equifax.HeaderHandlerResolver;
import com.affirm.equifax.ws.*;
import com.affirm.nosis.Dato;
import com.affirm.nosis.NosisResult;
import com.affirm.nosis.restApi.NosisRestResult;
import com.affirm.sentinel.WsSenestliteRSA;
import com.affirm.sentinel.WsSenestliteRSAExecute;
import com.affirm.sentinel.WsSenestliteRSAExecuteResponse;
import com.affirm.sentinel.WsSenestliteRSASoapPort;
import com.affirm.sentinel.infpri.WSSentinelInfPri;
import com.affirm.sentinel.infpri.WSSentinelInfPriExecute;
import com.affirm.sentinel.infpri.WSSentinelInfPriExecuteResponse;
import com.affirm.sentinel.infpri.WSSentinelInfPriSoapPort;
import com.affirm.system.configuration.Configuration;
import com.affirm.veraz.model.VerazResponse;
import com.google.gson.Gson;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.xml.ws.BindingProvider;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author jrodriguez
 */
@Service("bureauService")
public class BureauServiceImpl implements BureauService {

    private static Logger logger = Logger.getLogger(BureauServiceImpl.class);

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private ApplicationBureauUtil applicationBureauUtil;
    @Autowired
    private CatalogDAO catalogDAO;
    @Autowired
    private ErrorService errorService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private PersonDAO personDao;

    @Override
    public void runBureau(LoanApplication loanApplication, Person person, int bureauId) throws Exception {
        runBureau(loanApplication, person.getDocumentType().getId(), person.getDocumentNumber(), bureauId);
    }

    @Override
    public void runBureau(LoanApplication loanApplication, int docType, String docNumber, int bureauId) throws Exception {
        switch (bureauId) {
            case Bureau.EQUIFAX_RUC:
            case Bureau.EQUIFAX: {
                /*
                TIPO DOCUMENTO EQUIFAX
                    1 DNI Documento Nacional de Identificación
                    6 RUC Registro Único del Contribuyente
                    3 CE Carnet de Extranjería
                    4 PAS Pasaporte
                 */
                if (Configuration.hostEnvIsProduction()) {
                    int score = 0;
                    String riskLevel = null, conclusion = null;

                    CreditReportPortService creditReportPortService = new CreditReportPortService();
                    HeaderHandlerResolver handler = new HeaderHandlerResolver();
                    creditReportPortService.setHandlerResolver(handler);
                    Endpoint endpoint = creditReportPortService.getCreditReportPort();

                    QueryDataType queryDataType = new QueryDataType();
                    queryDataType.setCodigoReporte(String.valueOf(600));
                    queryDataType.setNumeroDocumento(docNumber);
                    queryDataType.setTipoDocumento(String.valueOf(
                            docType == IdentityDocumentType.CE ? 3 :
                                    (docType == IdentityDocumentType.RUC ? 6 : docType)));
                    queryDataType.setTipoPersona(docType == IdentityDocumentType.RUC ? "2" : "1"); // 1 NATURAL -- 2 JURIDICA
                    //ReporteCrediticio reporteCrediticio = endpoint.getReporteOnline(queryDataType);
                    ReporteCrediticio reporteCrediticio = applicationBureauUtil.callSoapWs(
                            bureauId,
                            (BindingProvider) endpoint,
                            loanApplication.getId(),
                            new EntityWebServiceUtilImpl.ISOAPProcess() {
                                @Override
                                public ReporteCrediticio process() throws Exception {
                                    return endpoint.getReporteOnline(queryDataType);
                                }
                            }, ReporteCrediticio.class);

                    for (ReporteCrediticio.Modulos.Modulo mod : reporteCrediticio.getModulos().getModulo()) {
                        if (mod.getCodigo().equals(docType == IdentityDocumentType.RUC ? "613" : "805") && mod.getData().getAny() instanceof RiskPredictor) {
                            // 601 RISK PREDICTOR EQUIFAX MODULE
                            if (mod.getData().getAny() != null) {
                                score = Integer.parseInt(((RiskPredictor) mod.getData().getAny()).getPuntaje().getValue());
                                riskLevel = ((RiskPredictor) mod.getData().getAny()).getNivelRiesgo().getValue();
                                conclusion = ((RiskPredictor) mod.getData().getAny()).getConclusion().getValue();
                            }
                            break;
                        }
                    }

                    Marshall marshall = new Marshall();
                    loanApplicationDao.registerBureauResult(loanApplication.getId(), score, riskLevel, conclusion, marshall.toJson(reporteCrediticio), Bureau.EQUIFAX);
                } else {
                    // Dummy response
                    String dummyEquifaxResult = null;
                    if(docType == IdentityDocumentType.RUC){
                        dummyEquifaxResult = StringUtils.join(new String[]{
                                "{\"Nombre\":\"Reporte Infocorp - Business Full\",\"FechaReporte\":\"2019-09-05 14:20:50\",\"NumeroOperacion\":\"S41909050137847\",\"DatosPrincipales\":{\"TipoDocumento\":\"RUC\",\"NumeroDocumento\":\"20601444764\",\"Direccion\":\"AV. MARISCAL JOSE DE LA MAR 398 D401 SANTA CRUZ\",\"Nombre\":\"SOLVEN FUNDING SOCIEDAD ANONIMA CERRADA\"},\"Modulos\":{\"Modulo\":[{\"Codigo\":\"614\",\"Nombre\":\"DIRECCIONES\",\"Data\":{\"flag\":false}},{\"Codigo\":\"615\",\"Nombre\":\"DIRECTORIO SUNAT\",\"Data\":{\"flag\":true,\"DirectorioSUNAT\":{\"Directorio\":[{\"RUC\":\"20601444764\",\"RazonSocial\":\"SOLVEN FUNDING SOCIEDAD ANONIMA CERRADA\",\"NombreComercial\":null,\"TipoContribuyente\":\"SOCIEDAD ANONIMA CERRADA\",\"EstadoContribuyente\":\"ACTIVO\",\"CondicionContribuyente\":\"HABIDO\",\"Dependencia\":\"I.R.LIMA-MEPECO\",\"CodigoCIIU\":\"72202\",\"DescripcionCIIU\":\"CONSULTORES PROG. Y SUMIN. INFORMATIC.\",\"InicioActividades\":\"08/09/2016\",\"ActividadComercioExterior\":\"SIN ACTIVIDAD\",\"Direcciones\":{\"Direccion\":[\"AV. MARISCAL JOSE DE LA MAR 398 D401 SANTA CRUZ \"]}}]}}},{\"Codigo\":\"616\",\"Nombre\":\"REPRESENTANTES LEGALES\",\"Data\":{\"flag\":true,\"RepresentantesLegales\":{\"RepresentantesDe\":{},\"RepresentandosPor\":{\"Representante\":[{\"Nombres\":\"TURCONI FACUNDO\",\"TipoDocumento\":\"3\",\"NumeroDocumento\":\"00000555030\",\"Cargo\":\"GERENTE GENERAL\",\"Fecha\":\"12/08/2016\"}]}}}},{\"Codigo\":\"613\",\"Nombre\":\"SCORE EMPRESAS 2.0\",\"Data\":{\"flag\":true,\"RiskPredictor\":{\"Puntaje\":\"771\",\"NivelRiesgo\":\"Riesgo Bajo\",\"Conclusion\":\"De cada 100 empresas con este Score, se espera que 8 de ellas incumplan en sus pagos durante los pr�ximos 12 meses.\"}}},{\"Codigo\":\"617\",\"Nombre\":\"SISTEMA FINANCIERO REGULADO (SBS) Y NO REGULADO (MICROFINANZAS)\",\"Data\":{\"flag\":true,\"SistemaFinanciero\":{\"ResumenComportamientoPago\":{\"Semaforo\":[{\"periodo\":\"201807\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201808\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201809\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201810\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201811\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201812\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201901\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201902\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201903\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201904\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201905\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201906\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201907\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0}]},\"DeudasUltimoPeriodo\":{\"periodo\":\"201907\",\"Deuda\":[{\"Entidad\":\"BANCO BBVA PER�\",\"SistemaFinanciero\":\"SR\",\"Calificacion\":\"NOR\",\"MontoTotal\":19233.33,\"Productos\":{\"Producto\":[{\"Tipo\":\"TarjCreComp\",\"Descripcion\":\"Tarjetas de cr�dito por compra\",\"Monto\":19233.33,\"DiasAtraso\":\"0\"}]}}]},\"DeudasHistoricas\":{\"Deuda\":[{\"periodo\":\"201907\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":19233.33,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":19233.33,\"DiasAtraso\":0},{\"periodo\":\"201906\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":18331.96,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":18331.96,\"DiasAtraso\":0},{\"periodo\":\"201905\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":22078.85,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":22078.85,\"DiasAtraso\":0},{\"periodo\":\"201904\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":20076.84,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":20076.84,\"DiasAtraso\":0},{\"periodo\":\"201903\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":16468.9,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":16468.9,\"DiasAtraso\":0},{\"periodo\":\"201902\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":17105.75,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":17105.75,\"DiasAtraso\":0},{\"periodo\":\"201901\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":19291.25,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":19291.25,\"DiasAtraso\":0},{\"periodo\":\"201812\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":16356.62,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":16356.62,\"DiasAtraso\":0},{\"periodo\":\"201811\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":1706.29,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":1706.29,\"DiasAtraso\":0},{\"periodo\":\"201810\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":8326.14,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":8326.14,\"DiasAtraso\":0},{\"periodo\":\"201809\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":709.64,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":709.64,\"DiasAtraso\":0},{\"periodo\":\"201808\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":7871.64,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":7871.64,\"DiasAtraso\":0},{\"periodo\":\"201807\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":3867.12,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":3867.12,\"DiasAtraso\":0},{\"periodo\":\"201806\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":8775.94,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":8775.94,\"DiasAtraso\":0},{\"periodo\":\"201805\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":5257.55,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":5257.55,\"DiasAtraso\":0},{\"periodo\":\"201804\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":9386.18,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":9386.18,\"DiasAtraso\":0},{\"periodo\":\"201803\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":6257.76,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":6257.76,\"DiasAtraso\":0},{\"periodo\":\"201802\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":6113.97,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":6113.97,\"DiasAtraso\":0},{\"periodo\":\"201801\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":5064.5,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":5064.5,\"DiasAtraso\":0},{\"periodo\":\"201712\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":6356.41,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":6356.41,\"DiasAtraso\":0},{\"periodo\":\"201711\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":3807.11,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":3807.11,\"DiasAtraso\":0},{\"periodo\":\"201710\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":8015.88,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":8015.88,\"DiasAtraso\":0},{\"periodo\":\"201709\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":4760.65,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":4760.65,\"DiasAtraso\":0},{\"periodo\":\"201708\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":3305.98,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":3305.98,\"DiasAtraso\":0},{\"periodo\":\"201707\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":6899.4,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":6899.4,\"DiasAtraso\":0},{\"periodo\":\"201706\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":8936.1,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":8936.1,\"DiasAtraso\":0},{\"periodo\":\"201705\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":1919.96,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":1919.96,\"DiasAtraso\":0},{\"periodo\":\"201704\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":6899.88,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":6899.88,\"DiasAtraso\":0},{\"periodo\":\"201703\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":4408.02,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":4408.02,\"DiasAtraso\":0},{\"periodo\":\"201702\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":2905.9,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":2905.9,\"DiasAtraso\":0},{\"periodo\":\"201701\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":3206.79,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":3206.79,\"DiasAtraso\":0},{\"periodo\":\"201612\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":2852.17,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":2852.17,\"DiasAtraso\":0},{\"periodo\":\"201611\"},{\"periodo\":\"201610\"},{\"periodo\":\"201609\"},{\"periodo\":\"201608\"},{\"periodo\":\"201607\"},{\"periodo\":\"201606\"},{\"periodo\":\"201605\"},{\"periodo\":\"201604\"},{\"periodo\":\"201603\"},{\"periodo\":\"201602\"},{\"periodo\":\"201601\"},{\"periodo\":\"201512\"},{\"periodo\":\"201511\"},{\"periodo\":\"201510\"},{\"periodo\":\"201509\"},{\"periodo\":\"201508\"}]},\"RCC\":{\"DetalleEntidades\":{\"periodo\":\"201907\",\"Entidad\":[{\"Codigo\":\"4\",\"Nombre\":\"BANCO BBVA PER�\",\"Calificacion\":\"NOR\",\"CreditosVigentes\":19233.33,\"CreditosRefinanciados\":0,\"CreditosVencidos\":0,\"CreditosJudicial\":0}]},\"Periodos\":{\"Periodo\":[{\"valor\":\"201907\",\"flag\":true,\"NroEntidades\":\"1\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":16516.14},{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":2717.19},{\"CodigoCuenta\":\"14080200000000\",\"NombreCuenta\":\"INTCRMES\",\"DescripcionCuenta\":\"Rendimientos devengados de cr�ditos a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":68.9},{\"CodigoCuenta\":\"15050900000000\",\"NombreCuenta\":\"OtrPagCtaTer\",\"DescripcionCuenta\":\"Otras cuentas pagadas por cuenta de terceros\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":15.47},{\"CodigoCuenta\":\"72050602000000\",\"NombreCuenta\":\"MicroemTjCred\",\"DescripcionCuenta\":\"Responsabilidad por l�neas de cr�dito para tarjetas de cr�dito a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":9319.2}]}},{\"valor\":\"201906\",\"flag\":true,\"NroEntidades\":\"1\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":3108.12},{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":15223.84},{\"CodigoCuenta\":\"14080200000000\",\"NombreCuenta\":\"INTCRMES\",\"DescripcionCuenta\":\"Rendimientos devengados de cr�ditos a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":68.48},{\"CodigoCuenta\":\"15050900000000\",\"NombreCuenta\":\"OtrPagCtaTer\",\"DescripcionCuenta\":\"Otras cuentas pagadas por cuenta de terceros\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":13.43},{\"CodigoCuenta\":\"72050602000000\",\"NombreCuenta\":\"MicroemTjCred\",\"DescripcionCuenta\":\"Responsabilidad por l�neas de cr�dito para tarjetas de cr�dito a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":10352.69}]}},{\"valor\":\"201905\",\"flag\":true,\"NroEntidades\":\"1\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":19896.81},{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":2182.04},{\"CodigoCuenta\":\"14080200000000\",\"NombreCuenta\":\"INTCRMES\",\"DescripcionCuenta\":\"Rendimientos devengados de cr�ditos a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":48.59},{\"CodigoCuenta\":\"15050900000000\",\"NombreCuenta\":\"OtrPagCtaTer\",\"DescripcionCuenta\":\"Otras cuentas pagadas por cuenta de terceros\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":8.16},{\"CodigoCuenta\":\"72050602000000\",\"NombreCuenta\":\"MicroemTjCred\",\"DescripcionCuenta\":\"Responsabilidad por l�neas de cr�dito para tarjetas de cr�dito a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":6397.45}]}},{\"valor\":\"201904\",\"flag\":true,\"NroEntidades\":\"1\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":18447.57},{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":1629.27},{\"CodigoCuenta\":\"14080200000000\",\"NombreCuenta\":\"INTCRMES\",\"DescripcionCuenta\":\"Rendimientos devengados de cr�ditos a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":20.39},{\"CodigoCuenta\":\"15050900000000\",\"NombreCuenta\":\"OtrPagCtaTer\",\"DescripcionCuenta\":\"Otras cuentas pagadas por cuenta de terceros\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":12.03},{\"CodigoCuenta\":\"72050602000000\",\"NombreCuenta\":\"MicroemTjCred\",\"DescripcionCuenta\":\"Responsabilidad por l�neas de cr�dito para tarjetas de cr�dito a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":8451.37}]}},{\"valor\":\"201903\",\"flag\":true,\"NroEntidades\":\"1\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":1777.85},{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":14691.05},{\"CodigoCuenta\":\"14080200000000\",\"NombreCuenta\":\"INTCRMES\",\"DescripcionCuenta\":\"Rendimientos devengados de cr�ditos a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":290.56},{\"CodigoCuenta\":\"14080200000000\",\"NombreCuenta\":\"INTCRMES\",\"DescripcionCuenta\":\"Rendimientos devengados de cr�ditos a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":7.78},{\"CodigoCuenta\":\"15050900000000\",\"NombreCuenta\":\"OtrPagCtaTer\",\"DescripcionCuenta\":\"Otras cuentas pagadas por cuenta de terceros\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":9},{\"CodigoCuenta\":\"72050602000000\",\"NombreCuenta\":\"MicroemTjCred\",\"DescripcionCuenta\":\"Responsabilidad por l�neas de cr�dito para tarjetas de cr�dito a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":12437.48}]}},{\"valor\":\"201902\",\"flag\":true,\"NroEntidades\":\"1\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":17040.25},{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":65.5},{\"CodigoCuenta\":\"72050602000000\",\"NombreCuenta\":\"MicroemTjCred\",\"DescripcionCuenta\":\"Responsabilidad por l�neas de cr�dito para tarjetas de cr�dito a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":11640.62}]}},{\"valor\":\"201901\",\"flag\":true,\"NroEntidades\":\"1\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":18801.83},{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":489.42},{\"CodigoCuenta\":\"71020000000000\",\"NombreCuenta\":\"CARTAFIANZA\",\"DescripcionCuenta\":\"CARTAS FIANZA OTORGADAS\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":10818.83},{\"CodigoCuenta\":\"72050602000000\",\"NombreCuenta\":\"MicroemTjCred\",\"DescripcionCuenta\":\"Responsabilidad por l�neas de cr�dito para tarjetas de cr�dito a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":9349.69}]}},{\"valor\":\"201812\",\"flag\":true,\"NroEntidades\":\"1\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":2348.52},{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":14008.1},{\"CodigoCuenta\":\"14080200000000\",\"NombreCuenta\":\"INTCRMES\",\"DescripcionCuenta\":\"Rendimientos devengados de cr�ditos a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":227.37},{\"CodigoCuenta\":\"14080200000000\",\"NombreCuenta\":\"INTCRMES\",\"DescripcionCuenta\":\"Rendimientos devengados de cr�ditos a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":17.15},{\"CodigoCuenta\":\"15050900000000\",\"NombreCuenta\":\"OtrPagCtaTer\",\"DescripcionCuenta\":\"Otras cuentas pagadas por cuenta de terceros\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":6.88},{\"CodigoCuenta\":\"71020000000000\",\"NombreCuenta\":\"CARTAFIANZA\",\"DescripcionCuenta\":\"CARTAS FIANZA OTORGADAS\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":10945.39},{\"CodigoCuenta\":\"72050602000000\",\"NombreCuenta\":\"MicroemTjCred\",\"DescripcionCuenta\":\"Responsabilidad por l�neas de cr�dito para tarjetas de cr�dito a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":12642.51}]}},{\"valor\":\"201811\",\"flag\":true,\"NroEntidades\":\"1\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":1706.29},{\"CodigoCuenta\":\"71020000000000\",\"NombreCuenta\":\"CARTAFIANZA\",\"DescripcionCuenta\":\"CARTAS FIANZA OTORGADAS\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":10968.1},{\"CodigoCuenta\":\"72050602000000\",\"NombreCuenta\":\"MicroemTjCred\",\"DescripcionCuenta\":\"Responsabilidad por l�neas de cr�dito para tarjetas de cr�dito a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":8169.53}]}},{\"valor\":\"201810\",\"flag\":true,\"NroEntidades\":\"1\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":7366.39},{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":959.75},{\"CodigoCuenta\":\"71020000000000\",\"NombreCuenta\":\"CARTAFIANZA\",\"DescripcionCuenta\":\"CARTAS FIANZA OTORGADAS\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":10919.43},{\"CodigoCuenta\":\"72050602000000\",\"NombreCuenta\":\"MicroemTjCred\",\"DescripcionCuenta\":\"Responsabilidad por l�neas de cr�dito para tarjetas de cr�dito a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":1104.69}]}},{\"valor\":\"201809\",\"flag\":true,\"NroEntidades\":\"1\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":692.41},{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":17.23},{\"CodigoCuenta\":\"71020000000000\",\"NombreCuenta\":\"CARTAFIANZA\",\"DescripcionCuenta\":\"CARTAS FIANZA OTORGADAS\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":10708.5},{\"CodigoCuenta\":\"72050602000000\",\"NombreCuenta\":\"MicroemTjCred\",\"DescripcionCuenta\":\"Responsabilidad por l�neas de cr�dito para tarjetas de cr�dito a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":9238.12}]}},{\"valor\":\"201808\",\"flag\":true,\"NroEntidades\":\"1\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":7843.23},{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":28.41},{\"CodigoCuenta\":\"71020000000000\",\"NombreCuenta\":\"CARTAFIANZA\",\"DescripcionCuenta\":\"CARTAS FIANZA OTORGADAS\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":10695.52},{\"CodigoCuenta\":\"72050602000000\",\"NombreCuenta\":\"MicroemTjCred\",\"DescripcionCuenta\":\"Responsabilidad por l�neas de cr�dito para tarjetas de cr�dito a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":1583.43}]}},{\"valor\":\"201712\",\"flag\":true,\"NroEntidades\":\"1\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":6356.41},{\"CodigoCuenta\":\"72050602000000\",\"NombreCuenta\":\"MicroemTjCred\",\"DescripcionCuenta\":\"Responsabilidad por l�neas de cr�dito para tarjetas de cr�dito a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":3272.92}]}},{\"valor\":\"201612\",\"flag\":true,\"NroEntidades\":\"1\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":1313.88},{\"CodigoCuenta\":\"14010202020000\",\"NombreCuenta\":\"TarjCreComp\",\"DescripcionCuenta\":\"Tarjetas de cr�dito por compra\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":1538.29},{\"CodigoCuenta\":\"72050602000000\",\"NombreCuenta\":\"MicroemTjCred\",\"DescripcionCuenta\":\"Responsabilidad por l�neas de cr�dito para tarjetas de cr�dito a microempresas\",\"CodigoEntidad\":\"4\",\"NombreEntidad\":\"BANCO BBVA PER�\",\"Calificacion\":\"\",\"Monto\":7083.66}]}},{\"valor\":\"201512\",\"flag\":false}]}},\"Rectificaciones\":{},\"Avalistas\":{},\"Microfinanzas\":{}}}},{\"Codigo\":\"618\",\"Nombre\":\"OTRAS DEUDAS IMPAGAS\",\"Data\":{\"flag\":true,\"DeudasImpagas\":{\"ResumenDeudasImpagas\":{\"SemaforoPeriodo\":[{\"periodo\":\"201808\",\"TieneDeuda\":false},{\"periodo\":\"201809\",\"TieneDeuda\":false},{\"periodo\":\"201810\",\"TieneDeuda\":false},{\"periodo\":\"201811\",\"TieneDeuda\":false},{\"periodo\":\"201812\",\"TieneDeuda\":false},{\"periodo\":\"201901\",\"TieneDeuda\":false},{\"periodo\":\"201902\",\"TieneDeuda\":false},{\"periodo\":\"201903\",\"TieneDeuda\":false},{\"periodo\":\"201904\",\"TieneDeuda\":false},{\"periodo\":\"201905\",\"TieneDeuda\":false},{\"periodo\":\"201906\",\"TieneDeuda\":false},{\"periodo\":\"201907\",\"TieneDeuda\":false},{\"periodo\":\"201908\",\"TieneDeuda\":false}]},\"Sicom\":{},\"NegativoSunat\":{},\"Omisos\":{},\"Protestos\":{},\"CuentasCerradas\":{},\"TarjetasAnuladas\":{},\"DeudasAFP\":{}}}},{\"Codigo\":\"619\",\"Nombre\":\"PROTESTOS POR GIRADOR\",\"Data\":{\"flag\":false}},{\"Codigo\":\"621\",\"Nombre\":\"COMERCIO EXTERIOR\",\"Data\":{\"flag\":false}},{\"Codigo\":\"736\",\"Nombre\":\"Buenos Contribuyentes\",\"Data\":{\"flag\":false}},{\"Codigo\":\"623\",\"Nombre\":\"BOLET�N OFICIAL\",\"Data\":{\"flag\":false}},{\"Codigo\":\"620\",\"Nombre\":\"ENTIDADES QUE CONSULTARON\",\"Data\":{\"flag\":true,\"IndicadorDeConsulta\":{\"Entidades\":{\"Entidad\":[{\"Mercado\":\"BANCA MULTIPLE\",\"Nombre\":\"MIBANCO, BANCO DE LA MICROEMPRESA S.A.\",\"Periodos\":{\"Periodo\":[{\"periodo\":\"201909\",\"value\":0},{\"periodo\":\"201908\",\"value\":1},{\"periodo\":\"201907\",\"value\":0},{\"periodo\":\"201906\",\"value\":0},{\"periodo\":\"201905\",\"value\":0},{\"periodo\":\"201904\",\"value\":0}]}},{\"Mercado\":\"SEGUROS\",\"Nombre\":\"AVLA PERU COMPA�IA DE SEGUROS S.A.\",\"Periodos\":{\"Periodo\":[{\"periodo\":\"201909\",\"value\":0},{\"periodo\":\"201908\",\"value\":0},{\"periodo\":\"201907\",\"value\":0},{\"periodo\":\"201906\",\"value\":1},{\"periodo\":\"201905\",\"value\":0},{\"periodo\":\"201904\",\"value\":0}]}},{\"Mercado\":\"COMERCIAL\",\"Nombre\":\"SOLVEN FUNDING SOCIEDAD ANONIMA CERRADA\",\"Periodos\":{\"Periodo\":[{\"periodo\":\"201909\",\"value\":0},{\"periodo\":\"201908\",\"value\":0},{\"periodo\":\"201907\",\"value\":0},{\"periodo\":\"201906\",\"value\":1},{\"periodo\":\"201905\",\"value\":0},{\"periodo\":\"201904\",\"value\":0}]}}]}}}},{\"Codigo\":\"622\",\"Nombre\":\"REVIS�ON DE RECLAMOS\",\"Data\":{\"flag\":false}}]}}"
                        });
                    }else{
                        dummyEquifaxResult = StringUtils.join(new String[]{
                                "{\"Nombre\":\"Reporte Infocorp - Business Full\",\"FechaReporte\":\"2019-05-15 12:18:37\",\"NumeroOperacion\":\"S31905150029380\",\"DatosPrincipales\":{\"TipoDocumento\":\"DNI\",\"NumeroDocumento\":\"41913616\",\"Direccion\":null,\"Nombre\":\"MENESES MITMA CARMEN ROSA\"},\"Modulos\":{\"Modulo\":[{\"Codigo\":\"602\",\"Nombre\":\"DIRECTORIO DE PERSONAS\",\"Data\":{\"flag\":true,\"DirectorioPersona\":{\"Nombres\":\"MENESES MITMA CARMEN ROSA\",\"TipoDocumento\":\"1\",\"NumeroDocumento\":\"41913616\",\"FechaNacimiento\":\"14/07/1983\",\"EstadoCivil\":\"SOLTERO\",\"Nacionalidad\":\"PERU\",\"GradoInstruccion\":\"SECUNDARIA\",\"Telefono\":\"\",\"Ocupacion\":\"NO DIO INFORMACION SOLICITADA\"}}},{\"Codigo\":\"603\",\"Nombre\":\"DIRECCIONES\",\"Data\":{\"flag\":false}},{\"Codigo\":\"604\",\"Nombre\":\"DIRECTORIO SUNAT\",\"Data\":{\"flag\":true,\"DirectorioSUNAT\":{\"Directorio\":[{\"RUC\":\"10419136161\",\"RazonSocial\":\"MENESES MITMA CARMEN ROSA\",\"NombreComercial\":null,\"TipoContribuyente\":\"PERSONA NATURAL SIN NEGOCIO\",\"EstadoContribuyente\":\"ACTIVO\",\"CondicionContribuyente\":\"HABIDO\",\"Dependencia\":\"I.R.LIMA-MEPECO\",\"CodigoCIIU\":\"93098\",\"DescripcionCIIU\":\"OTRAS ACTIVID.DE TIPO SERVICIO NCP.\",\"InicioActividades\":\"01/07/2003\",\"ActividadComercioExterior\":\"SIN ACTIVIDAD\",\"Direcciones\":{\"Direccion\":[\"- / / / A C P V VILLA CASUARINAS /\"]}}]}}},{\"Codigo\":\"605\",\"Nombre\":\"REPRESENTANTES LEGALES\",\"Data\":{\"flag\":true,\"RepresentantesLegales\":{\"RepresentantesDe\":{},\"RepresentandosPor\":{}}}},{\"Codigo\":\"607\",\"Nombre\":\"SISTEMA FINANCIERO REGULADO (SBS) Y NO REGULADO (MICROFINANZAS)\",\"Data\":{\"flag\":true,\"SistemaFinanciero\":{\"ResumenComportamientoPago\":{\"Semaforo\":[{\"periodo\":\"201803\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201804\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201805\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":1},{\"periodo\":\"201806\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201807\",\"NoTieneImpagos\":false,\"TieneDeudasAtrasadas\":true,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":30},{\"periodo\":\"201808\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201809\",\"NoTieneImpagos\":false,\"TieneDeudasAtrasadas\":true,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":30},{\"periodo\":\"201810\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201811\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":3},{\"periodo\":\"201812\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201901\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201902\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0},{\"periodo\":\"201903\",\"NoTieneImpagos\":true,\"TieneDeudasAtrasadas\":false,\"TieneDeudasImpagasInfocorp\":false,\"InformacionNoDisponible\":false,\"DiasAtraso\":0}]},\"DeudasUltimoPeriodo\":{\"periodo\":\"201903\",\"Deuda\":[{\"Entidad\":\"COMPARTAMOS FINANCIERA S.A.\",\"SistemaFinanciero\":\"SR\",\"Calificacion\":\"NOR\",\"MontoTotal\":119.46,\"Productos\":{\"Producto\":[{\"Tipo\":\"CUOTAFIJA\",\"Descripcion\":\"Préstamos a cuota fija\",\"Monto\":119.46,\"DiasAtraso\":\"0\"}]}},{\"Entidad\":\"BANCO AZTECA DEL PERU S A\",\"SistemaFinanciero\":\"SR\",\"Calificacion\":\"NOR\",\"MontoTotal\":313.5,\"Productos\":{\"Producto\":[{\"Tipo\":\"TarjCredComp\",\"Descripcion\":\"Tarjetas de crédito por compra\",\"Monto\":313.5,\"DiasAtraso\":\"0\"}]}},{\"Entidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"SistemaFinanciero\":\"SR\",\"Calificacion\":\"NOR\",\"MontoTotal\":3600.0,\"Productos\":{\"Producto\":[{\"Tipo\":\"NOREVLIBREDSP\",\"Descripcion\":\"Préstamos no revolventes para libre disponibilidad\",\"Monto\":3600.0,\"DiasAtraso\":\"0\"}]}}]},\"DeudasHistoricas\":{\"Deuda\":[{\"periodo\":\"201903\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":3,\"DeudaVigente\":4032.96,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":4032.96,\"DiasAtraso\":0},{\"periodo\":\"201902\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":2,\"DeudaVigente\":3459.78,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":3459.78,\"DiasAtraso\":0},{\"periodo\":\"201901\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":2,\"DeudaVigente\":3918.17,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":3918.17,\"DiasAtraso\":0},{\"periodo\":\"201812\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":2,\"DeudaVigente\":4400.0,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":4400.0,\"DiasAtraso\":0},{\"periodo\":\"201811\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":2,\"DeudaVigente\":2400.29,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":2400.29,\"DiasAtraso\":3},{\"periodo\":\"201810\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":2,\"DeudaVigente\":2690.16,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":2690.16,\"DiasAtraso\":0},{\"periodo\":\"201809\",\"Calificacion\":\"CPP\",\"Porcentaje\":\"27\",\"NroEntidades\":2,\"DeudaVigente\":3219.2,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":3219.2,\"DiasAtraso\":12},{\"periodo\":\"201808\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":2,\"DeudaVigente\":3411.28,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":3411.28,\"DiasAtraso\":0},{\"periodo\":\"201807\",\"Calificacion\":\"CPP\",\"Porcentaje\":\"37\",\"NroEntidades\":2,\"DeudaVigente\":3925.82,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":3925.82,\"DiasAtraso\":21},{\"periodo\":\"201806\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":2,\"DeudaVigente\":4020.05,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":4020.05,\"DiasAtraso\":0},{\"periodo\":\"201805\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":2,\"DeudaVigente\":2441.0,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":2441.0,\"DiasAtraso\":1},{\"periodo\":\"201804\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":2,\"DeudaVigente\":2536.72,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":2536.72,\"DiasAtraso\":0},{\"periodo\":\"201803\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":2,\"DeudaVigente\":2514.05,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":2514.05,\"DiasAtraso\":0},{\"periodo\":\"201802\",\"Calificacion\":\"NOR\",\"Porcentaje\":\"100\",\"NroEntidades\":1,\"DeudaVigente\":600.0,\"DeudaAtrasada\":0,\"DeudaJudicial\":0,\"DeudaCastigada\":0,\"DeudaTotal\":600.0,\"DiasAtraso\":0},{\"periodo\":\"201801\"},{\"periodo\":\"201712\"},{\"periodo\":\"201711\"},{\"periodo\":\"201710\"},{\"periodo\":\"201709\"},{\"periodo\":\"201708\"},{\"periodo\":\"201707\"},{\"periodo\":\"201706\"},{\"periodo\":\"201705\"},{\"periodo\":\"201704\"},{\"periodo\":\"201703\"},{\"periodo\":\"201702\"},{\"periodo\":\"201701\"},{\"periodo\":\"201612\"},{\"periodo\":\"201611\"},{\"periodo\":\"201610\"},{\"periodo\":\"201609\"},{\"periodo\":\"201608\"},{\"periodo\":\"201607\"},{\"periodo\":\"201606\"},{\"periodo\":\"201605\"},{\"periodo\":\"201604\"},{\"periodo\":\"201603\"},{\"periodo\":\"201602\"},{\"periodo\":\"201601\"},{\"periodo\":\"201512\"},{\"periodo\":\"201511\"},{\"periodo\":\"201510\"},{\"periodo\":\"201509\"},{\"periodo\":\"201508\"},{\"periodo\":\"201507\"},{\"periodo\":\"201506\"},{\"periodo\":\"201505\"},{\"periodo\":\"201504\"}]},\"RCC\":{\"DetalleEntidades\":{\"periodo\":\"201903\",\"Entidad\":[{\"Codigo\":\"142\",\"Nombre\":\"COMPARTAMOS FINANCIERA S.A.\",\"Calificacion\":\"NOR\",\"CreditosVigentes\":119.46,\"CreditosRefinanciados\":0,\"CreditosVencidos\":0,\"CreditosJudicial\":0},{\"Codigo\":\"361\",\"Nombre\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"NOR\",\"CreditosVigentes\":313.5,\"CreditosRefinanciados\":0,\"CreditosVencidos\":0,\"CreditosJudicial\":0},{\"Codigo\":\"184\",\"Nombre\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"NOR\",\"CreditosVigentes\":3600.0,\"CreditosRefinanciados\":0,\"CreditosVencidos\":0,\"CreditosJudicial\":0}]},\"Periodos\":{\"Periodo\":[{\"valor\":\"201903\",\"flag\":true,\"NroEntidades\":\"3\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010206020000\",\"NombreCuenta\":\"CUOTAFIJA\",\"DescripcionCuenta\":\"Préstamos a cuota fija\",\"CodigoEntidad\":\"142\",\"NombreEntidad\":\"COMPARTAMOS FINANCIERA S.A.\",\"Calificacion\":\"\",\"Monto\":119.46},{\"CodigoCuenta\":\"14010302020000\",\"NombreCuenta\":\"TarjCredComp\",\"DescripcionCuenta\":\"Tarjetas de crédito por compra\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":313.5},{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":3600.0},{\"CodigoCuenta\":\"14080200000000\",\"NombreCuenta\":\"INTCRMES\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos a microempresas\",\"CodigoEntidad\":\"142\",\"NombreEntidad\":\"COMPARTAMOS FINANCIERA S.A.\",\"Calificacion\":\"\",\"Monto\":1.15},{\"CodigoCuenta\":\"14080300000000\",\"NombreCuenta\":\"INTCRCONS\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos de consumo\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":58.36},{\"CodigoCuenta\":\"14090306000000\",\"NombreCuenta\":\"PROVGENSOBRE\",\"DescripcionCuenta\":\"Provisión genérica por sobreendeudamiento\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":3.14},{\"CodigoCuenta\":\"72050603000000\",\"NombreCuenta\":\"CONSUMO\",\"DescripcionCuenta\":\"Responsabilidad por líneas de crédito para tarjetas de crédito de consumo\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":3361.0},{\"CodigoCuenta\":\"81092300000000\",\"NombreCuenta\":\"L.CRED TC CONSUM\",\"DescripcionCuenta\":\"Líneas de crédito en tarjetas de crédito de consumo\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":3674.5},{\"CodigoCuenta\":\"84040900000000\",\"NombreCuenta\":\"OtrGarNoPref\",\"DescripcionCuenta\":\"Otras Garantías no preferidas\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":3600.0},{\"CodigoCuenta\":\"84041000000000\",\"NombreCuenta\":\"GARNOPRAVAFI\",\"DescripcionCuenta\":\"Garantías no Preferidas - Avales y Fianzas\",\"CodigoEntidad\":\"142\",\"NombreEntidad\":\"COMPARTAMOS FINANCIERA S.A.\",\"Calificacion\":\"\",\"Monto\":1314.06}]}},{\"valor\":\"201902\",\"flag\":true,\"NroEntidades\":\"2\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010206020000\",\"NombreCuenta\":\"CUOTAFIJA\",\"DescripcionCuenta\":\"Préstamos a cuota fija\",\"CodigoEntidad\":\"142\",\"NombreEntidad\":\"COMPARTAMOS FINANCIERA S.A.\",\"Calificacion\":\"\",\"Monto\":330.46},{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":3129.32},{\"CodigoCuenta\":\"14080200000000\",\"NombreCuenta\":\"INTCRMES\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos a microempresas\",\"CodigoEntidad\":\"142\",\"NombreEntidad\":\"COMPARTAMOS FINANCIERA S.A.\",\"Calificacion\":\"\",\"Monto\":0.79},{\"CodigoCuenta\":\"14080300000000\",\"NombreCuenta\":\"INTCRCONS\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos de consumo\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":6.17},{\"CodigoCuenta\":\"84040900000000\",\"NombreCuenta\":\"OtrGarNoPref\",\"DescripcionCuenta\":\"Otras Garantías no preferidas\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":3129.32},{\"CodigoCuenta\":\"84041000000000\",\"NombreCuenta\":\"GARNOPRAVAFI\",\"DescripcionCuenta\":\"Garantías no Preferidas - Avales y Fianzas\",\"CodigoEntidad\":\"142\",\"NombreEntidad\":\"COMPARTAMOS FINANCIERA S.A.\",\"Calificacion\":\"\",\"Monto\":3635.06}]}},{\"valor\":\"201901\",\"flag\":true,\"NroEntidades\":\"2\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010206020000\",\"NombreCuenta\":\"CUOTAFIJA\",\"DescripcionCuenta\":\"Préstamos a cuota fija\",\"CodigoEntidad\":\"142\",\"NombreEntidad\":\"COMPARTAMOS FINANCIERA S.A.\",\"Calificacion\":\"\",\"Monto\":527.77},{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":3390.4},{\"CodigoCuenta\":\"14080200000000\",\"NombreCuenta\":\"INTCRMES\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos a microempresas\",\"CodigoEntidad\":\"142\",\"NombreEntidad\":\"COMPARTAMOS FINANCIERA S.A.\",\"Calificacion\":\"\",\"Monto\":1.27},{\"CodigoCuenta\":\"14080300000000\",\"NombreCuenta\":\"INTCRCONS\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos de consumo\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":16.75},{\"CodigoCuenta\":\"84040900000000\",\"NombreCuenta\":\"OtrGarNoPref\",\"DescripcionCuenta\":\"Otras Garantías no preferidas\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":3390.4},{\"CodigoCuenta\":\"84041000000000\",\"NombreCuenta\":\"GARNOPRAVAFI\",\"DescripcionCuenta\":\"Garantías no Preferidas - Avales y Fianzas\",\"CodigoEntidad\":\"142\",\"NombreEntidad\":\"COMPARTAMOS FINANCIERA S.A.\",\"Calificacion\":\"\",\"Monto\":5805.47}]}},{\"valor\":\"201812\",\"flag\":true,\"NroEntidades\":\"2\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010206020000\",\"NombreCuenta\":\"CUOTAFIJA\",\"DescripcionCuenta\":\"Préstamos a cuota fija\",\"CodigoEntidad\":\"142\",\"NombreEntidad\":\"COMPARTAMOS FINANCIERA S.A.\",\"Calificacion\":\"\",\"Monto\":800.0},{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":3600.0},{\"CodigoCuenta\":\"14080200000000\",\"NombreCuenta\":\"INTCRMES\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos a microempresas\",\"CodigoEntidad\":\"142\",\"NombreEntidad\":\"COMPARTAMOS FINANCIERA S.A.\",\"Calificacion\":\"\",\"Monto\":23.33},{\"CodigoCuenta\":\"14080300000000\",\"NombreCuenta\":\"INTCRCONS\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos de consumo\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":60.81},{\"CodigoCuenta\":\"84040900000000\",\"NombreCuenta\":\"OtrGarNoPref\",\"DescripcionCuenta\":\"Otras Garantías no preferidas\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":3600.0},{\"CodigoCuenta\":\"84041000000000\",\"NombreCuenta\":\"GARNOPRAVAFI\",\"DescripcionCuenta\":\"Garantías no Preferidas - Avales y Fianzas\",\"CodigoEntidad\":\"142\",\"NombreEntidad\":\"COMPARTAMOS FINANCIERA S.A.\",\"Calificacion\":\"\",\"Monto\":8800.0}]}},{\"valor\":\"201811\",\"flag\":true,\"NroEntidades\":\"2\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":212.19},{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":2180.35},{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":7.75},{\"CodigoCuenta\":\"14080300000000\",\"NombreCuenta\":\"INTCRCONS\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos de consumo\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":0.07},{\"CodigoCuenta\":\"14080300000000\",\"NombreCuenta\":\"INTCRCONS\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos de consumo\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":4.39},{\"CodigoCuenta\":\"14090306000000\",\"NombreCuenta\":\"PROVGENSOBRE\",\"DescripcionCuenta\":\"Provisión genérica por sobreendeudamiento\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":2.2},{\"CodigoCuenta\":\"72050303020000\",\"NombreCuenta\":\"NOREVOLVENTE\",\"DescripcionCuenta\":\"Responsabilidad por líneas de crédito no revolvente\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":20.0},{\"CodigoCuenta\":\"84040900000000\",\"NombreCuenta\":\"OtrGarNoPref\",\"DescripcionCuenta\":\"Otras Garantías no preferidas\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":2180.35}]}},{\"valor\":\"201810\",\"flag\":true,\"NroEntidades\":\"2\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":2253.36},{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":436.8},{\"CodigoCuenta\":\"14080300000000\",\"NombreCuenta\":\"INTCRCONS\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos de consumo\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":1.03},{\"CodigoCuenta\":\"14080300000000\",\"NombreCuenta\":\"INTCRCONS\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos de consumo\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":2.95},{\"CodigoCuenta\":\"14090306000000\",\"NombreCuenta\":\"PROVGENSOBRE\",\"DescripcionCuenta\":\"Provisión genérica por sobreendeudamiento\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":4.37},{\"CodigoCuenta\":\"72050303020000\",\"NombreCuenta\":\"NOREVOLVENTE\",\"DescripcionCuenta\":\"Responsabilidad por líneas de crédito no revolvente\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":20.0},{\"CodigoCuenta\":\"84040900000000\",\"NombreCuenta\":\"OtrGarNoPref\",\"DescripcionCuenta\":\"Otras Garantías no preferidas\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":2253.36}]}},{\"valor\":\"201809\",\"flag\":true,\"NroEntidades\":\"2\",\"Calificaciones\":{\"NOR\":\"72.27\",\"CPP\":\"27.73\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":2326.51},{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":850.63},{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":42.06},{\"CodigoCuenta\":\"14080300000000\",\"NombreCuenta\":\"INTCRCONS\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos de consumo\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":1.47},{\"CodigoCuenta\":\"14080300000000\",\"NombreCuenta\":\"INTCRCONS\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos de consumo\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":31.21},{\"CodigoCuenta\":\"14090301000000\",\"NombreCuenta\":\"PROVESPECI\",\"DescripcionCuenta\":\"Provisión específica\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":44.63},{\"CodigoCuenta\":\"72050303020000\",\"NombreCuenta\":\"NOREVOLVENTE\",\"DescripcionCuenta\":\"Responsabilidad por líneas de crédito no revolvente\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":20.0},{\"CodigoCuenta\":\"84040900000000\",\"NombreCuenta\":\"OtrGarNoPref\",\"DescripcionCuenta\":\"Otras Garantías no preferidas\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":2326.51}]}},{\"valor\":\"201808\",\"flag\":true,\"NroEntidades\":\"2\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":2393.64},{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":1017.64},{\"CodigoCuenta\":\"14080300000000\",\"NombreCuenta\":\"INTCRCONS\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos de consumo\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":3.13},{\"CodigoCuenta\":\"14080300000000\",\"NombreCuenta\":\"INTCRCONS\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos de consumo\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":7.17},{\"CodigoCuenta\":\"14090306000000\",\"NombreCuenta\":\"PROVGENSOBRE\",\"DescripcionCuenta\":\"Provisión genérica por sobreendeudamiento\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":10.18},{\"CodigoCuenta\":\"72050303020000\",\"NombreCuenta\":\"NOREVOLVENTE\",\"DescripcionCuenta\":\"Responsabilidad por líneas de crédito no revolvente\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":20.0},{\"CodigoCuenta\":\"84040900000000\",\"NombreCuenta\":\"OtrGarNoPref\",\"DescripcionCuenta\":\"Otras Garantías no preferidas\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":2393.64}]}},{\"valor\":\"201807\",\"flag\":true,\"NroEntidades\":\"2\",\"Calificaciones\":{\"NOR\":\"62.61\",\"CPP\":\"37.39\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":2458.07},{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":1467.75},{\"CodigoCuenta\":\"14080300000000\",\"NombreCuenta\":\"INTCRCONS\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos de consumo\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":3.22},{\"CodigoCuenta\":\"14080300000000\",\"NombreCuenta\":\"INTCRCONS\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos de consumo\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":90.19},{\"CodigoCuenta\":\"14090301000000\",\"NombreCuenta\":\"PROVESPECI\",\"DescripcionCuenta\":\"Provisión específica\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":73.39},{\"CodigoCuenta\":\"72050303020000\",\"NombreCuenta\":\"NOREVOLVENTE\",\"DescripcionCuenta\":\"Responsabilidad por líneas de crédito no revolvente\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":20.0},{\"CodigoCuenta\":\"84040900000000\",\"NombreCuenta\":\"OtrGarNoPref\",\"DescripcionCuenta\":\"Otras Garantías no preferidas\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":2458.07}]}},{\"valor\":\"201806\",\"flag\":true,\"NroEntidades\":\"2\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":2500.0},{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":1520.05},{\"CodigoCuenta\":\"14080300000000\",\"NombreCuenta\":\"INTCRCONS\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos de consumo\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":14.27},{\"CodigoCuenta\":\"14080300000000\",\"NombreCuenta\":\"INTCRCONS\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos de consumo\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":22.98},{\"CodigoCuenta\":\"14090306000000\",\"NombreCuenta\":\"PROVGENSOBRE\",\"DescripcionCuenta\":\"Provisión genérica por sobreendeudamiento\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":15.2},{\"CodigoCuenta\":\"72050303020000\",\"NombreCuenta\":\"NOREVOLVENTE\",\"DescripcionCuenta\":\"Responsabilidad por líneas de crédito no revolvente\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":20.0},{\"CodigoCuenta\":\"84040900000000\",\"NombreCuenta\":\"OtrGarNoPref\",\"DescripcionCuenta\":\"Otras Garantías no preferidas\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":2500.0}]}},{\"valor\":\"201805\",\"flag\":true,\"NroEntidades\":\"2\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":1721.0},{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":720.0},{\"CodigoCuenta\":\"14080300000000\",\"NombreCuenta\":\"INTCRCONS\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos de consumo\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":36.49},{\"CodigoCuenta\":\"14080300000000\",\"NombreCuenta\":\"INTCRCONS\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos de consumo\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":8.08},{\"CodigoCuenta\":\"14090306000000\",\"NombreCuenta\":\"PROVGENSOBRE\",\"DescripcionCuenta\":\"Provisión genérica por sobreendeudamiento\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":17.21},{\"CodigoCuenta\":\"72050303020000\",\"NombreCuenta\":\"NOREVOLVENTE\",\"DescripcionCuenta\":\"Responsabilidad por líneas de crédito no revolvente\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":20.0},{\"CodigoCuenta\":\"84040900000000\",\"NombreCuenta\":\"OtrGarNoPref\",\"DescripcionCuenta\":\"Otras Garantías no preferidas\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":720.0}]}},{\"valor\":\"201804\",\"flag\":true,\"NroEntidades\":\"2\",\"Calificaciones\":{\"NOR\":\"100.0\",\"CPP\":\"0.0\",\"DEF\":\"0.0\",\"DUD\":\"0.0\",\"PER\":\"0.0\"},\"Deudas\":{\"Deuda\":[{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":720.0},{\"CodigoCuenta\":\"14010306030000\",\"NombreCuenta\":\"NOREVLIBREDSP\",\"DescripcionCuenta\":\"Préstamos no revolventes para libre disponibilidad\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":1816.72},{\"CodigoCuenta\":\"14080300000000\",\"NombreCuenta\":\"INTCRCONS\",\"DescripcionCuenta\":\"Rendimientos devengados de créditos de consumo\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":6.62},{\"CodigoCuenta\":\"14090306000000\",\"NombreCuenta\":\"PROVGENSOBRE\",\"DescripcionCuenta\":\"Provisión genérica por sobreendeudamiento\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":18.17},{\"CodigoCuenta\":\"72050303020000\",\"NombreCuenta\":\"NOREVOLVENTE\",\"DescripcionCuenta\":\"Responsabilidad por líneas de crédito no revolvente\",\"CodigoEntidad\":\"361\",\"NombreEntidad\":\"BANCO AZTECA DEL PERU S A\",\"Calificacion\":\"\",\"Monto\":20.0},{\"CodigoCuenta\":\"84040900000000\",\"NombreCuenta\":\"OtrGarNoPref\",\"DescripcionCuenta\":\"Otras Garantías no preferidas\",\"CodigoEntidad\":\"184\",\"NombreEntidad\":\"CAJA MUNICIPAL DE AHORRO Y CREDITO HUANCAYO\",\"Calificacion\":\"\",\"Monto\":720.0}]}},{\"valor\":\"201712\",\"flag\":false},{\"valor\":\"201612\",\"flag\":false},{\"valor\":\"201512\",\"flag\":false}]}},\"Rectificaciones\":{},\"Avalistas\":{\"Aval\":[{\"TipoDocumento\":\"DNI\",\"NumeroDocumento\":\"00080350886\",\"NombreAval\":\"MOZOMBITE FASANANDO MERCEDES \",\"Entidades\":{\"Entidad\":[{\"Descripcion\":\"COMPARTAMOS FINANCIERA S.A.\",\"Periodos\":{\"Periodo\":[{\"periodo\":\"201903\",\"Calificacion\":\"NOR\",\"Saldo\":121.14},{\"periodo\":\"201902\",\"Calificacion\":\"NOR\",\"Saldo\":341.54},{\"periodo\":\"201901\",\"Calificacion\":\"NOR\",\"Saldo\":547.63},{\"periodo\":\"201812\",\"Calificacion\":\"NOR\",\"Saldo\":832.00},{\"periodo\":\"201811\"},{\"periodo\":\"201810\"},{\"periodo\":\"201809\"},{\"periodo\":\"201808\"},{\"periodo\":\"201807\"},{\"periodo\":\"201806\"},{\"periodo\":\"201805\"},{\"periodo\":\"201804\"}]}}]}},{\"TipoDocumento\":\"DNI\",\"NumeroDocumento\":\"00070044042\",\"NombreAval\":\"AQUINO FELIX ELIZABETH JUANA\",\"Entidades\":{\"Entidad\":[{\"Descripcion\":\"COMPARTAMOS FINANCIERA S.A.\",\"Periodos\":{\"Periodo\":[{\"periodo\":\"201903\",\"Calificacion\":\"NOR\",\"Saldo\":121.14},{\"periodo\":\"201902\",\"Calificacion\":\"NOR\",\"Saldo\":341.54},{\"periodo\":\"201901\",\"Calificacion\":\"NOR\",\"Saldo\":547.63},{\"periodo\":\"201812\",\"Calificacion\":\"NOR\",\"Saldo\":832.00},{\"periodo\":\"201811\"},{\"periodo\":\"201810\"},{\"periodo\":\"201809\"},{\"periodo\":\"201808\"},{\"periodo\":\"201807\"},{\"periodo\":\"201806\"},{\"periodo\":\"201805\"},{\"periodo\":\"201804\"}]}}]}},{\"TipoDocumento\":\"DNI\",\"NumeroDocumento\":\"00044193172\",\"NombreAval\":\"ZAVALETA GAMARRA MALENA ANTUANE\",\"Entidades\":{\"Entidad\":[{\"Descripcion\":\"COMPARTAMOS FINANCIERA S.A.\",\"Periodos\":{\"Periodo\":[{\"periodo\":\"201903\",\"Calificacion\":\"NOR\",\"Saldo\":149.06},{\"periodo\":\"201902\",\"Calificacion\":\"NOR\",\"Saldo\":422.81},{\"periodo\":\"201901\",\"Calificacion\":\"NOR\",\"Saldo\":678.79},{\"periodo\":\"201812\",\"Calificacion\":\"NOR\",\"Saldo\":1032.00},{\"periodo\":\"201811\"},{\"periodo\":\"201810\"},{\"periodo\":\"201809\"},{\"periodo\":\"201808\"},{\"periodo\":\"201807\"},{\"periodo\":\"201806\"},{\"periodo\":\"201805\"},{\"periodo\":\"201804\"}]}}]}},{\"TipoDocumento\":\"DNI\",\"NumeroDocumento\":\"00006762039\",\"NombreAval\":\"SOTO HUAMAN TARCILA \",\"Entidades\":{\"Entidad\":[{\"Descripcion\":\"COMPARTAMOS FINANCIERA S.A.\",\"Periodos\":{\"Periodo\":[{\"periodo\":\"201903\",\"Calificacion\":\"NOR\",\"Saldo\":149.06},{\"periodo\":\"201902\",\"Calificacion\":\"NOR\",\"Saldo\":422.81},{\"periodo\":\"201901\",\"Calificacion\":\"NOR\",\"Saldo\":678.79},{\"periodo\":\"201812\",\"Calificacion\":\"NOR\",\"Saldo\":1032.00},{\"periodo\":\"201811\"},{\"periodo\":\"201810\"},{\"periodo\":\"201809\"},{\"periodo\":\"201808\"},{\"periodo\":\"201807\"},{\"periodo\":\"201806\"},{\"periodo\":\"201805\"},{\"periodo\":\"201804\"}]}}]}},{\"TipoDocumento\":\"DNI\",\"NumeroDocumento\":\"00044315141\",\"NombreAval\":\"ROJAS MANRIQUE ERCILA DEL CARMEN\",\"Entidades\":{\"Entidad\":[{\"Descripcion\":\"COMPARTAMOS FINANCIERA S.A.\",\"Periodos\":{\"Periodo\":[{\"periodo\":\"201903\",\"Calificacion\":\"NOR\",\"Saldo\":187.05},{\"periodo\":\"201902\",\"Calificacion\":\"NOR\",\"Saldo\":542.03},{\"periodo\":\"201901\",\"Calificacion\":\"NOR\",\"Saldo\":874.00},{\"periodo\":\"201812\",\"Calificacion\":\"NOR\",\"Saldo\":1332.00},{\"periodo\":\"201811\"},{\"periodo\":\"201810\"},{\"periodo\":\"201809\"},{\"periodo\":\"201808\"},{\"periodo\":\"201807\"},{\"periodo\":\"201806\"},{\"periodo\":\"201805\"},{\"periodo\":\"201804\"}]}}]}},{\"TipoDocumento\":\"DNI\",\"NumeroDocumento\":\"00070809334\",\"NombreAval\":\"CORDOVA VARGAS ROSA ADERLI\",\"Entidades\":{\"Entidad\":[{\"Descripcion\":\"COMPARTAMOS FINANCIERA S.A.\",\"Periodos\":{\"Periodo\":[{\"periodo\":\"201903\",\"Calificacion\":\"NOR\",\"Saldo\":147.37},{\"periodo\":\"201902\",\"Calificacion\":\"NOR\",\"Saldo\":411.73},{\"periodo\":\"201901\",\"Calificacion\":\"NOR\",\"Saldo\":658.92},{\"periodo\":\"201812\",\"Calificacion\":\"NOR\",\"Saldo\":1000.00},{\"periodo\":\"201811\"},{\"periodo\":\"201810\"},{\"periodo\":\"201809\"},{\"periodo\":\"201808\"},{\"periodo\":\"201807\"},{\"periodo\":\"201806\"},{\"periodo\":\"201805\"},{\"periodo\":\"201804\"}]}}]}},{\"TipoDocumento\":\"DNI\",\"NumeroDocumento\":\"00044858545\",\"NombreAval\":\"SOTO MUNARRIZ MARIBEL \",\"Entidades\":{\"Entidad\":[{\"Descripcion\":\"COMPARTAMOS FINANCIERA S.A.\",\"Periodos\":{\"Periodo\":[{\"periodo\":\"201903\",\"Calificacion\":\"NOR\",\"Saldo\":149.06},{\"periodo\":\"201902\",\"Calificacion\":\"NOR\",\"Saldo\":422.81},{\"periodo\":\"201901\",\"Calificacion\":\"NOR\",\"Saldo\":678.79},{\"periodo\":\"201812\",\"Calificacion\":\"NOR\",\"Saldo\":1032.00},{\"periodo\":\"201811\"},{\"periodo\":\"201810\"},{\"periodo\":\"201809\"},{\"periodo\":\"201808\"},{\"periodo\":\"201807\"},{\"periodo\":\"201806\"},{\"periodo\":\"201805\"},{\"periodo\":\"201804\"}]}}]}},{\"TipoDocumento\":\"DNI\",\"NumeroDocumento\":\"00070937095\",\"NombreAval\":\"PALOMINO MARTINEZ MARYORI MARIEL\",\"Entidades\":{\"Entidad\":[{\"Descripcion\":\"COMPARTAMOS FINANCIERA S.A.\",\"Periodos\":{\"Periodo\":[{\"periodo\":\"201903\",\"Calificacion\":\"NOR\",\"Saldo\":149.06},{\"periodo\":\"201902\",\"Calificacion\":\"NOR\",\"Saldo\":422.81},{\"periodo\":\"201901\",\"Calificacion\":\"NOR\",\"Saldo\":678.79},{\"periodo\":\"201812\",\"Calificacion\":\"NOR\",\"Saldo\":1032.00},{\"periodo\":\"201811\"},{\"periodo\":\"201810\"},{\"periodo\":\"201809\"},{\"periodo\":\"201808\"},{\"periodo\":\"201807\"},{\"periodo\":\"201806\"},{\"periodo\":\"201805\"},{\"periodo\":\"201804\"}]}}]}},{\"TipoDocumento\":\"DNI\",\"NumeroDocumento\":\"00072661932\",\"NombreAval\":\"RODRIGUEZ ZAMALLOA ALEXANDRA ISABEL\",\"Entidades\":{\"Entidad\":[{\"Descripcion\":\"COMPARTAMOS FINANCIERA S.A.\",\"Periodos\":{\"Periodo\":[{\"periodo\":\"201903\",\"Calificacion\":\"NOR\",\"Saldo\":149.06},{\"periodo\":\"201902\",\"Calificacion\":\"NOR\",\"Saldo\":422.81},{\"periodo\":\"201901\",\"Calificacion\":\"NOR\",\"Saldo\":678.79},{\"periodo\":\"201812\",\"Calificacion\":\"NOR\",\"Saldo\":1032.00},{\"periodo\":\"201811\"},{\"periodo\":\"201810\"},{\"periodo\":\"201809\"},{\"periodo\":\"201808\"},{\"periodo\":\"201807\"},{\"periodo\":\"201806\"},{\"periodo\":\"201805\"},{\"periodo\":\"201804\"}]}}]}},{\"TipoDocumento\":\"DNI\",\"NumeroDocumento\":\"00043226301\",\"NombreAval\":\"CARHUAPOMA PALOMARES ANYELINA MILAGROS\",\"Entidades\":{\"Entidad\":[{\"Descripcion\":\"COMPARTAMOS FINANCIERA S.A.\",\"Periodos\":{\"Periodo\":[{\"periodo\":\"201903\",\"Calificacion\":\"NOR\",\"Saldo\":147.37},{\"periodo\":\"201902\",\"Calificacion\":\"NOR\",\"Saldo\":411.73},{\"periodo\":\"201901\",\"Calificacion\":\"NOR\",\"Saldo\":658.92},{\"periodo\":\"201812\",\"Calificacion\":\"NOR\",\"Saldo\":1000.00},{\"periodo\":\"201811\"},{\"periodo\":\"201810\"},{\"periodo\":\"201809\"},{\"periodo\":\"201808\"},{\"periodo\":\"201807\"},{\"periodo\":\"201806\"},{\"periodo\":\"201805\"},{\"periodo\":\"201804\"}]}}]}},{\"TipoDocumento\":\"DNI\",\"NumeroDocumento\":\"00047019032\",\"NombreAval\":\"CASTRO MARINO LILY MARLEY\",\"Entidades\":{\"Entidad\":[{\"Descripcion\":\"COMPARTAMOS FINANCIERA S.A.\",\"Periodos\":{\"Periodo\":[{\"periodo\":\"201903\",\"Calificacion\":\"NOR\",\"Saldo\":149.06},{\"periodo\":\"201902\",\"Calificacion\":\"NOR\",\"Saldo\":422.81},{\"periodo\":\"201901\",\"Calificacion\":\"NOR\",\"Saldo\":678.79},{\"periodo\":\"201812\",\"Calificacion\":\"NOR\",\"Saldo\":1032.00},{\"periodo\":\"201811\"},{\"periodo\":\"201810\"},{\"periodo\":\"201809\"},{\"periodo\":\"201808\"},{\"periodo\":\"201807\"},{\"periodo\":\"201806\"},{\"periodo\":\"201805\"},{\"periodo\":\"201804\"}]}}]}}]},\"Microfinanzas\":{}}}},{\"Codigo\":\"608\",\"Nombre\":\"OTRAS DEUDAS IMPAGAS\",\"Data\":{\"flag\":true,\"DeudasImpagas\":{\"ResumenDeudasImpagas\":{\"SemaforoPeriodo\":[{\"periodo\":\"201804\",\"TieneDeuda\":false},{\"periodo\":\"201805\",\"TieneDeuda\":false},{\"periodo\":\"201806\",\"TieneDeuda\":false},{\"periodo\":\"201807\",\"TieneDeuda\":false},{\"periodo\":\"201808\",\"TieneDeuda\":false},{\"periodo\":\"201809\",\"TieneDeuda\":false},{\"periodo\":\"201810\",\"TieneDeuda\":false},{\"periodo\":\"201811\",\"TieneDeuda\":false},{\"periodo\":\"201812\",\"TieneDeuda\":false},{\"periodo\":\"201901\",\"TieneDeuda\":false},{\"periodo\":\"201902\",\"TieneDeuda\":false},{\"periodo\":\"201903\",\"TieneDeuda\":false},{\"periodo\":\"201904\",\"TieneDeuda\":false}]},\"Sicom\":{},\"NegativoSunat\":{},\"Omisos\":{},\"Protestos\":{},\"CuentasCerradas\":{},\"TarjetasAnuladas\":{}}}},{\"Codigo\":\"805\",\"Nombre\":\"RISK PREDICTOR 2.0\",\"Data\":{\"flag\":true,\"RiskPredictor\":{\"Puntaje\":\"658\",\"NivelRiesgo\":\"Riesgo Bajo\",\"Conclusion\":\"De cada 100 personas con este Score, se espera que 13 de ellas incumplan en sus pagos durante los próximos 12 meses.\"}}},{\"Codigo\":\"606\",\"Nombre\":\"INCOME PREDICTOR\",\"Data\":{\"flag\":true,\"IncomePredictor\":{\"Categoria\":\"05\",\"RangoInicial\":1300,\"RangoFinal\":2000}}},{\"Codigo\":\"735\",\"Nombre\":\"Buenos Contribuyentes\",\"Data\":{\"flag\":false}},{\"Codigo\":\"609\",\"Nombre\":\"BOLETÍN OFICIAL\",\"Data\":{\"flag\":false}},{\"Codigo\":\"610\",\"Nombre\":\"ENTIDADES QUE CONSULTARON\",\"Data\":{\"flag\":true,\"IndicadorDeConsulta\":{\"Entidades\":{\"Entidad\":[{\"Mercado\":\"TELECOMUNICACIONES\",\"Nombre\":\"TELEFONICA MOVILES SA\",\"Periodos\":{\"Periodo\":[{\"periodo\":\"201905\",\"value\":0},{\"periodo\":\"201904\",\"value\":0},{\"periodo\":\"201903\",\"value\":2},{\"periodo\":\"201902\",\"value\":3},{\"periodo\":\"201901\",\"value\":0},{\"periodo\":\"201812\",\"value\":0}]}},{\"Mercado\":\"CAJAS RURALES\",\"Nombre\":\"FINANCIERA EFECTIVA S.A.\",\"Periodos\":{\"Periodo\":[{\"periodo\":\"201905\",\"value\":0},{\"periodo\":\"201904\",\"value\":0},{\"periodo\":\"201903\",\"value\":0},{\"periodo\":\"201902\",\"value\":0},{\"periodo\":\"201901\",\"value\":0},{\"periodo\":\"201812\",\"value\":1}]}}]}}}},{\"Codigo\":\"611\",\"Nombre\":\"REVISÍON DE RECLAMOS\",\"Data\":{\"flag\":false}}]}}"
                        });
                    }
                    loanApplicationDao.registerBureauResult(loanApplication.getId(), 710, "Riesgo Bajo", "De cada 100 personas con este Score, se espera que 12 de ellas incumplan en sus pagos durante los próximos 12 meses.", dummyEquifaxResult, Bureau.EQUIFAX);
                }

                break;
            }
            case Bureau.NOSIS: {
                Marshall marshall = new Marshall();
                // Get info from Nosis
                String url = System.getenv("NOSIS_URL");

                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                        .queryParam("Usuario", System.getenv("NOSIS_USER"))
                        .queryParam("Password", System.getenv("NOSIS_PASSWORD"))
                        .queryParam("NroConsulta", loanApplication.getId())
                        .queryParam("Cons_CDA", 0)
                        .queryParam("Cons_SoloPorDoc", "Si")
                        .queryParam("ConsXML_Doc", docNumber)
                        .queryParam("ConsHTML_Doc", docNumber)
                        .queryParam("ConsXML_Setup", "CO|HIST.36,CI|HIST.24,QU|REL");
                NosisResult result = applicationBureauUtil.callRestWs(Bureau.NOSIS, builder, loanApplication.getId(), NosisResult.class);
                // Parse with marshall and save the bureau result
                if (Configuration.hostEnvIsProduction()) {
                    Integer score = result.getScore();
                    loanApplicationDao.registerBureauResult(
                            loanApplication.getId(),
                            score,
                            null, null, marshall.toJson(result), Bureau.NOSIS);
                } else {

                    Map<String, Integer> fixedScores = new HashMap<>();
                    fixedScores.put("20228643417", 350);
                    fixedScores.put("20065769760", 400);
                    fixedScores.put("20430401573", 450);
                    fixedScores.put("20419197727", 500);
                    fixedScores.put("20231520431", 520);
                    fixedScores.put("27348519234", 560);
                    fixedScores.put("20137934974", 570);
                    fixedScores.put("27143444312", 600);
                    fixedScores.put("27298850813", 800);
                    fixedScores.put("20232159783", 900);
                    fixedScores.put("27287267482", 900);
                    fixedScores.put("27341402048", 870);
                    fixedScores.put("20140455076", 700);
                    fixedScores.put("27303487986", 750);
                    fixedScores.put("27299375205", 500);

                    int scoreToRegister;
                    if (fixedScores.containsKey(docNumber)) {
                        scoreToRegister = fixedScores.get(docNumber);
                    } else {
                        int[] randomScores = new int[]{600, 700, 630, 680, 720};
                        scoreToRegister = randomScores[new Random().nextInt(randomScores.length)];
                    }

                    // Change the score in the xml
                    Dato dato = result.getParteXML().getDatos().stream().filter(d -> d.getClave().getPrefijo().equals("CD")).findFirst().orElse(null);
                    if(dato != null){
                        Dato.CalculoCDA.Item item = dato.getCalculoCDA().getItems().stream().filter(
                                itm -> itm.getClave().equals("SCORE")
                        ).findFirst().orElse(null);
                        if(item != null){
                            item.setValor(scoreToRegister + "");
                        }
                    }

                    loanApplicationDao.registerBureauResult(
                            loanApplication.getId(),
                            scoreToRegister,
                            null, null, marshall.toJson(result), Bureau.NOSIS);

                }

                AfipActivitiy activitiy = getAfaipActivityFromNosisHelperByDocNumber(result);
                if (null != activitiy) {
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_ACTIVITY_TYPE.getKey(), String.valueOf(activitiy.getId()));
                    loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                }
                break;
            }
            case Bureau.NOSIS_BDS: {
                Marshall marshall = new Marshall();
                // Get info from Nosis
                String url = System.getenv("NOSIS_BDS_URL");
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                        .queryParam("usuario", System.getenv("NOSIS_BDS_USER"))
                        .queryParam("token", System.getenv("NOSIS_BDS_TOKEN"))
                        .queryParam("documento", docNumber)
                        .queryParam("VR", "1")
                        .queryParam("format", "json");
                String result = applicationBureauUtil.callRestWs(Bureau.NOSIS_BDS, builder, loanApplication.getId(), String.class);

                NosisRestResult restResult = new NosisRestResult();
                restResult.fillFromJson(new JSONObject(result));
                loanApplicationDao.registerBureauResult(
                        loanApplication.getId(),
                        restResult.getScore(),
                        null, null, result, Bureau.NOSIS_BDS);
                break;
            }
            case Bureau.PYP: {

                String url = System.getenv("PYP_URL")
                        .replace("{DOC_NUMBER}", docNumber)
                        .replace("{GENDER}", "S");
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

                List<String> convertToArrays = new ArrayList<>();
                convertToArrays.add("inf_lab_hist_fecha_");
                convertToArrays.add("predictor_ingreso");
                convertToArrays.add("Tipo_Actividad");
                convertToArrays.add("RELACION_DEPENDENCIA");

                String result = applicationBureauUtil.callRestWs(Bureau.PYP, builder, loanApplication.getId(), String.class);
                if (result != null) {
                    JSONObject jsonBureau = new JSONObject(result);
                    if (JsonUtil.getJsonObjectFromJson(jsonBureau, "RESULTADO", null) != null) {
                        JSONObject jsonResult = JsonUtil.getJsonObjectFromJson(jsonBureau, "RESULTADO", null);
                        if (jsonResult != null) {

                            // Validate if its error
                            if (JsonUtil.getJsonObjectFromJson(jsonResult, "ERROR", null) != null) {
                                throw new Exception("PyP reteurned error");
                            }

                            // Conver to array this objects
                            for (String var : convertToArrays) {
                                if (JsonUtil.getJsonObjectFromJson(jsonResult, var, null) != null) {
                                    JSONObject jsonInfLab = JsonUtil.getJsonObjectFromJson(jsonResult, var, null);
                                    if (JsonUtil.getJsonObjectFromJson(jsonInfLab, "row", null) != null) {
                                        JSONObject jsonRow = JsonUtil.getJsonObjectFromJson(jsonInfLab, "row", null);
                                        JSONArray arrayRow = new JSONArray();
                                        arrayRow.put(jsonRow);
                                        jsonInfLab.put("row", arrayRow);
                                    }
                                }
                            }

                            // If  its SEMI_ACTIVO, then delete the object inf_lab_hist_fecha_
                            if (JsonUtil.getJsonObjectFromJson(jsonResult, "RELACION_DEPENDENCIA", null) != null) {
                                JSONObject jsonRelDep = JsonUtil.getJsonObjectFromJson(jsonResult, "RELACION_DEPENDENCIA", null);
                                if (JsonUtil.getJsonArrayFromJson(jsonRelDep, "row", null) != null) {
                                    JSONArray relDepRows = JsonUtil.getJsonArrayFromJson(jsonRelDep, "row", null);
                                    if (relDepRows.length() > 0) {
                                        JSONObject relDep0 = relDepRows.getJSONObject(0);
                                        if (JsonUtil.getStringFromJson(relDep0, "situacion_laboral_actual", null) != null
                                                && JsonUtil.getStringFromJson(relDep0, "situacion_laboral_actual", null).equalsIgnoreCase("SITUACION: SEMI ACTIVO")) {
                                            jsonResult.remove("inf_lab_hist_fecha_");
                                        }
                                    }
                                }
                            }
                        }
                    }
                    result = jsonBureau.toString();
                }
                loanApplicationDao.registerBureauResult(
                        loanApplication.getId(),
                        null,
                        null, null, result, Bureau.PYP);
                break;
            }
            case Bureau.SENTINEL: {
                    /*
                    Tipos de documento
                     D -> DNI
                     R -> RUC
                     4 -> CE
                     5 -> Pasaporte
                    * */
                String tipoDocumento;
                switch (docType) {
                    case IdentityDocumentType.DNI:
                        tipoDocumento = "D";
                        break;
                    case IdentityDocumentType.CE:
                        tipoDocumento = "4";
                        break;
                    case IdentityDocumentType.RUC:
                        tipoDocumento = "R";
                        break;
                    default:
                        tipoDocumento = null;
                }

                WsSenestliteRSA wsSenestliteRSA = new WsSenestliteRSA();
                HeaderHandlerResolver handler = new HeaderHandlerResolver();
                wsSenestliteRSA.setHandlerResolver(handler);
                WsSenestliteRSASoapPort wsSenestliteRSASoapPort = wsSenestliteRSA.getWsSenestliteRSASoapPort();

                WsSenestliteRSAExecute wsSenestliteRSAExecute = new WsSenestliteRSAExecute();
                wsSenestliteRSAExecute.setGxUsuenc(System.getenv("SENTINEL_USER"));
                wsSenestliteRSAExecute.setGxPasenc(System.getenv("SENTINEL_PASS"));
                wsSenestliteRSAExecute.setGxKey(System.getenv("SENTINEL_KEY"));
                wsSenestliteRSAExecute.setTipodoc(tipoDocumento);
                wsSenestliteRSAExecute.setNrodoc(docNumber);

                WsSenestliteRSAExecuteResponse response = applicationBureauUtil.callSoapWs(
                        bureauId,
                        (BindingProvider) wsSenestliteRSASoapPort,
                        loanApplication.getId(),
                        new EntityWebServiceUtilImpl.ISOAPProcess() {
                            @Override
                            public WsSenestliteRSAExecuteResponse process() {
                                return wsSenestliteRSASoapPort.execute(wsSenestliteRSAExecute);
                            }
                        }, WsSenestliteRSAExecuteResponse.class);

                // TODO
                Integer score = null;
                String riskLevel = null;
                String conclusion = null;

                Marshall marshall = new Marshall();
                String jsonResponse = marshall.toJson(response);
                if(jsonResponse == null) throw new Exception("Error SENTINEL BUREAU");

                JSONObject resultado = new JSONObject(jsonResponse).getJSONObject("Resultado");
                if(resultado != null){
                    String verificationCode = resultado.optString("CodigoWS");
                    if(verificationCode != null && !verificationCode.equalsIgnoreCase("0")) throw new Exception("Error SENTINEL BUREAU");
                }
                loanApplicationDao.registerBureauResult(loanApplication.getId(), score, riskLevel, conclusion, jsonResponse, Bureau.SENTINEL);

                break;
            }
            case Bureau.SENTINEL_INP_PRI: {


                WSSentinelInfPri wsSentinelInfPri = new WSSentinelInfPri();
                HeaderHandlerResolver handler = new HeaderHandlerResolver();
                wsSentinelInfPri.setHandlerResolver(handler);
                WSSentinelInfPriSoapPort wsSentinelInfPriSoapPort = wsSentinelInfPri.getWSSentinelInfPriSoapPort();

                WSSentinelInfPriExecute wsSentinelInfPriExecute = new WSSentinelInfPriExecute();
                wsSentinelInfPriExecute.setGxUsuenc(System.getenv("SENTINEL_INF_PRI_USER"));
                wsSentinelInfPriExecute.setGxPasenc(System.getenv("SENTINEL_INF_PRI_PASS"));
                wsSentinelInfPriExecute.setGxKey(System.getenv("SENTINEL_INF_PRI_KEY"));
                wsSentinelInfPriExecute.setTiTipodoc(getSentinelDocumentType(docType));
                wsSentinelInfPriExecute.setTiNrodoc(validateDocumentNumberLengthToSentinel(docNumber));

                Person personData = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), true);
                if(personData != null && personData.getPartner() != null && personData.getMaritalStatus() != null && personData.getMaritalStatus().getId() == MaritalStatus.MARRIED){
                    wsSentinelInfPriExecute.setCgTipodoc(getSentinelDocumentType(personData.getPartner().getDocumentType().getId()));
                    wsSentinelInfPriExecute.setCgNrodoc(personData.getPartner().getDocumentNumber());
                }
                WSSentinelInfPriExecuteResponse response = applicationBureauUtil.callSoapWs(
                        bureauId,
                        (BindingProvider) wsSentinelInfPriSoapPort,
                        loanApplication.getId(),
                        new EntityWebServiceUtilImpl.ISOAPProcess() {
                            @Override
                            public
                            WSSentinelInfPriExecuteResponse process() {
                                return wsSentinelInfPriSoapPort.execute(wsSentinelInfPriExecute);
                            }
                        },
                        WSSentinelInfPriExecuteResponse.class);

                // TODO
                Integer score = null;
                String riskLevel = null;
                String conclusion = null;

                loanApplicationDao.registerBureauResult(loanApplication.getId(), score, riskLevel, conclusion, new Gson().toJson(response), Bureau.SENTINEL_INP_PRI);

                break;
            }
            case Bureau.VERAZ_BDS: {

                String bodyParam = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><mensaje><identificador><userlogon><matriz>%VERAZ_MATRIZ%</matriz><usuario>%VERAZ_USER%</usuario><password><![CDATA[%VERAZ_PSWD%]]></password></userlogon><medio>https</medio><formatoInforme>T</formatoInforme><reenvio/><producto>experto</producto><lote><sectorVeraz>%VERAZ_SECTOR%</sectorVeraz><sucursalVeraz>0</sucursalVeraz><cliente>0</cliente><fechaHora>%VERAZ_DATETIME%</fechaHora></lote></identificador><consulta><integrantes>1</integrantes><integrante valor=\"1\"><nombre>%VERAZ_NAME%</nombre><sexo>%VERAZ_GENDER%</sexo><documento>%VERAZ_DOCUMENT_NUMBER%</documento></integrante></consulta></mensaje>";

                String url = System.getenv("VERAZ_URL");

                Person person = personDao.getPerson(loanApplication.getPersonId(), false, Configuration.getDefaultLocale());
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                sd.setTimeZone(TimeZone.getTimeZone("GMT"));

                bodyParam = bodyParam
                        .replace("%VERAZ_MATRIZ%", System.getenv("VERAZ_MATRIZ"))
                        .replace("%VERAZ_PSWD%", System.getenv("VERAZ_PSWD"))
                        .replace("%VERAZ_USER%", System.getenv("VERAZ_USER"))
                        .replace("%VERAZ_SECTOR%", System.getenv("VERAZ_SECTOR"))
                        .replace("%VERAZ_DATETIME%", sd.format(new Date()))
                        .replace("%VERAZ_NAME%", String.format("%s, %s", person.getName(), person.getFullSurnames()).trim())
                        .replace("%VERAZ_GENDER%", person.getGender().toString())
                        .replace("%VERAZ_DOCUMENT_NUMBER%", person.getDocumentNumber());


                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url+bodyParam);

                String result = applicationBureauUtil.callRestWs(Bureau.VERAZ_BDS, builder, loanApplication.getId(), String.class);
                if (result != null) {
                    JSONObject json = XML.toJSONObject(result);
                    if(json != null && json.has("mensaje")){
                        VerazResponse verazResponse = new Gson().fromJson(json.get("mensaje").toString(),VerazResponse.class);
                        if(verazResponse != null){

                            //Check if it's not an error
                            if(verazResponse.getEstado().getCodigoError() == 0){
                                result = new Gson().toJson(verazResponse);
                                loanApplicationDao.registerBureauResult(
                                        loanApplication.getId(),
                                        null,
                                        null, null, result, Bureau.VERAZ_BDS);
                                break;
                            }
                        }
                    }
                }
                throw new Exception("Veraz reteurned error");
            }
            case Bureau.VERAZ_REST_BDS: {

                //ACCESS TOKEN
                String urlToken = System.getenv("VERAZ_REST_BDS_TOKEN_URL");
                String clientId = System.getenv("VERAZ_REST_BDS_CLIENT_ID");
                String clientSecret = System.getenv("VERAZ_REST_BDS_CLIENT_SECRET");
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToken);
                HttpHeaders tokenHeaders = new HttpHeaders();
                tokenHeaders.set("Authorization", "Basic "+Base64.getEncoder().encodeToString((clientId+":"+clientSecret).getBytes()));
                MultiValueMap<String, String> bodyParam = new LinkedMultiValueMap<>();
                bodyParam.add("scope", "https://api.latam.equifax.com/business/integration-api-efx/v1");
                bodyParam.add("grant_type", "client_credentials");

                String tokenResult = applicationBureauUtil.callRestFormDataWs(Bureau.VERAZ_REST_BDS, builder, bodyParam, tokenHeaders, loanApplication.getId(), String.class);
                if (tokenResult != null) {
                    JSONObject json = new JSONObject(tokenResult);
                    if(json != null && json.has("access_token")){
                        String urlIntegration = System.getenv("VERAZ_REST_BDS_INTEGRATION_URL");
                        UriComponentsBuilder integrationBuilder = UriComponentsBuilder.fromHttpUrl(urlIntegration);
                        HttpHeaders integrationHeaders = new HttpHeaders();
                        integrationHeaders.set("Authorization", "Bearer "+json.getString("access_token"));
                        Person personData = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), true);
                        //INTEGRATION
                        String bodyRequestBase = "{\"applicants\":{\"primaryConsumer\":{\"personalInformation\":{\"entity\":{\"consumer\":{\"names\":[{\"data\":{\"documento\":\"%DOCUMENT_NUMBER%\",\"nombre\":\"%PERSON_NAME%\",\"sexo\":\"%PERSON_GENDER%\"}}]}},\"productData\":{\"sector\":\"%SECTOR%\",\"billTo\":\"%BILLTO%\",\"shipTo\":\"0\",\"formatReport\":\"%FORMAT_REPORT%\",\"producto\":\"%PRODUCT%\"},\"clientConfig\":{\"clientTxId\":\"%LOAN_ID%\",\"clientReference\":\"\"},\"variables\":{},\"globalVariables\":{},\"vinculos\":[]}}}}";
                        bodyRequestBase = bodyRequestBase
                                .replace("%DOCUMENT_NUMBER%", personData.getDocumentNumber())
                                .replace("%PERSON_NAME%", personData.getFullName())
                                .replace("%PERSON_GENDER%", personData.getGender() != null ? personData.getGender().toString() :  "I")
                                .replace("%SECTOR%", System.getenv("VERAZ_REST_BDS_SECTOR"))
                                .replace("%BILLTO%", System.getenv("VERAZ_REST_BDS_BILLTO"))
                                .replace("%FORMAT_REPORT%", "T")
                                .replace("%PRODUCT%", System.getenv("VERAZ_REST_BDS_PRODUCT"))
                                .replace("%LOAN_ID%", loanApplication.getId().toString());

                        String result = applicationBureauUtil.callRestPostMethodWs(Bureau.VERAZ_REST_BDS, integrationBuilder, new JSONObject(bodyRequestBase), integrationHeaders, loanApplication.getId(), String.class);
                        if(result != null){
                            JSONObject jsonResult = new JSONObject(result);
                            if(jsonResult != null && jsonResult.has("clientImplementationStatus")){
                                loanApplicationDao.registerBureauResult(
                                        loanApplication.getId(),
                                        null,
                                        null, null, result, Bureau.VERAZ_REST_BDS);
                                break;
                            }
                        }
                    }
                }


                throw new Exception("Veraz reteurned error");
            }
        }
    }

    private AfipActivitiy getAfaipActivityFromNosisHelperByDocNumber(NosisResult result) {
        if (result == null || result.getParteHTML() == null || result.getParteHTML().getHtml() == null) {
            return null;
        }

        String textResult = result.getParteHTML().getHtml().toLowerCase();
        String actividadPrincipalLabel = "<tr><td class=\"lbl\">actividad principal:</td><td>";

        int indexResult = textResult.indexOf(actividadPrincipalLabel);
        Integer activityId = null;
        if (indexResult != -1) {
            String substringResult = textResult.substring(indexResult).replace(actividadPrincipalLabel, "");
            String field = substringResult.substring(0, substringResult.indexOf("</td></tr>"));
            if (field.indexOf('-') != -1) {
                activityId = Integer.valueOf(field.substring(0, field.indexOf('-') + -1));
                System.out.println(activityId);
            }
        }

        if (null == activityId)
            return null;
        final int finalActivityId = activityId;
        return catalogDAO.getAfipActivities().stream().filter(act -> act.getId() == finalActivityId).findAny()
                .orElse(null);
    }

    @Override
    public byte[] renderBureauResultFromHtml(LoanApplication loanApplication, int bureauId) throws Exception {
        switch (bureauId) {
            case Bureau.NOSIS:
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ITextRenderer renderer = new ITextRenderer();
                ApplicationBureau nosisApplicationBureau = loanApplicationDao.getBureauResults(loanApplication.getId())
                        .stream().filter(b -> b.getBureauId() == Bureau.NOSIS).findFirst().orElse(null);
                NosisResult nosisResult = nosisApplicationBureau != null ? nosisApplicationBureau.getNosisResult() : null;
                String htmlContent = nosisResult.getParteHTML().getHtml();
                htmlContent = htmlContent.replaceAll("&(?!amp;)", "");
                // Parse str into a Document
                Document doc = Jsoup.parse(htmlContent);
                Elements print = doc.select(".ly-print");
                print.attr("style", "max-width: 80px !important; min-width: 680px !important;");
                Elements grillaAtraso = doc.select(".pref-ci .atrasos");
                grillaAtraso.attr("style", "width: 680px;");
                // set syntax xml to auto close tags... is very IMPORTANT!, then use escapeMode
                doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
                doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);

                renderer.setDocumentFromString(doc.html(), Configuration.getClientDomain());
                renderer.layout();
                renderer.createPDF(os);

                byte[] pdfAsBytes = os.toByteArray();
                //os.close();

                PDDocument document = new PDDocument();
                PDPage page = new PDPage();
                document.addPage(page);
                Marshall marshall = new Marshall();

                PDFont font = PDType1Font.HELVETICA;
                int fontSize = 7;
                float leading = 1.5f * fontSize;

                PDRectangle mediabox = page.getMediaBox();
                float margin = 72;
                float width = mediabox.getWidth() - 2 * margin;
                float startX = mediabox.getLowerLeftX() + margin;
                float startY = mediabox.getUpperRightY() - margin;

                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                contentStream.beginText();
                contentStream.setFont(font, fontSize);
                contentStream.newLineAtOffset(100, 700);

                String xml = marshall.toXml(nosisResult.getParteXML()).replace("\r", "").replace("\t", "");
                String[] parts = xml.split("\n");

                float currentY = startY;
                for (int i = 0; i < parts.length; i++) {
                    currentY -= leading;
                    if (parts[i].length() > width) {
                        List<String> subparts = new ArrayList<String>();
                        int index = 0;
                        int maxcharacters = 130;
                        while (index < parts[i].length()) {
                            subparts.add(parts[i].substring(index, Math.min(index + maxcharacters, parts[i].length())));
                            index += maxcharacters;
                        }
                        for (int j = 0; j < subparts.size(); j++) {
                            currentY -= leading;
                            if (currentY <= margin) {
                                contentStream.endText();
                                contentStream.close();
                                PDPage new_Page = new PDPage();
                                document.addPage(new_Page);
                                contentStream = new PDPageContentStream(document, new_Page);
                                contentStream.beginText();
                                contentStream.setFont(font, fontSize);
                                contentStream.newLineAtOffset(startX, startY);
                                currentY = startY;
                            }
                            contentStream.showText(subparts.get(j));
                            contentStream.newLineAtOffset(0, -leading);
                        }
                        currentY -= leading;
                        i = i + 1;
                    }
                    if (currentY <= margin) {
                        contentStream.endText();
                        contentStream.close();
                        PDPage new_Page = new PDPage();
                        document.addPage(new_Page);
                        contentStream = new PDPageContentStream(document, new_Page);
                        contentStream.beginText();
                        contentStream.setFont(font, fontSize);
                        contentStream.newLineAtOffset(startX, startY);
                        currentY = startY;
                    }
                    contentStream.showText(parts[i]);
                    contentStream.newLineAtOffset(0, -leading);
                }
                contentStream.endText();

                contentStream.close();
                ByteArrayOutputStream outXml = new ByteArrayOutputStream();
                document.save(outXml);

                /*merge between pdfs generated*/
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PDFMergerUtility mergeUtility = new PDFMergerUtility();
                if (pdfAsBytes != null)
                    mergeUtility.addSource(new ByteArrayInputStream(pdfAsBytes));

                if (outXml.toByteArray() != null)
                    mergeUtility.addSource(new ByteArrayInputStream(outXml.toByteArray()));

                mergeUtility.setDestinationStream(out);
                mergeUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());

                os.close();
                document.close();

                return out.toByteArray();
        }
        return null;
    }

    @Async
    @Override
    public void callNosisUpdateExternalData(String docNumber) {
        try {
            String url = "http://sacrelay.nosis.com/api/consultar?usuario=" + System.getenv("NOSIS_USER") + "&password=" + System.getenv("NOSIS_PASSWORD") + "&documento=" + docNumber;
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
            String result = restTemplate.getForObject(builder.build().encode().toUri(), String.class);
            logger.info("Result sacrelay nosis update: " + result);
        } catch (Exception ex) {
            errorService.onError(ex);
        }
    }

    private String getSentinelDocumentType(int docType){
       /*
        Tipos de documento
         D -> DNI
         R -> RUC
         4 -> CE
         5 -> Pasaporte
        * */
        String tipoDocumento;
        switch (docType) {
            case IdentityDocumentType.DNI:
                tipoDocumento = "D";
                break;
            case IdentityDocumentType.CE:
                tipoDocumento = "4";
                break;
            case IdentityDocumentType.RUC:
                tipoDocumento = "R";
                break;
            default:
                tipoDocumento = null;
        }
        return tipoDocumento;
    }

    private String validateDocumentNumberLengthToSentinel(String documentNumber){
        if(documentNumber.length() > 8) return documentNumber.substring(1,9);
        return documentNumber;
    }
}
