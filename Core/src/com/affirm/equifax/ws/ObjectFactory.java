
package com.affirm.equifax.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import java.math.BigDecimal;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.affirm.equifax.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Cuentas_QNAME = new QName("http://ws.creditreport.equifax.com.pe/endpoint", "Cuentas");
    private final static QName _EquifaxInterfaceException_QNAME = new QName("http://ws.creditreport.equifax.com.pe/endpoint", "EquifaxInterfaceException");
    private final static QName _DatosConsulta_QNAME = new QName("http://ws.creditreport.equifax.com.pe/document", "DatosConsulta");
    private final static QName _GetReporteOnlineResponse_QNAME = new QName("http://ws.creditreport.equifax.com.pe/endpoint", "GetReporteOnlineResponse");
    private final static QName _GetReporteHistoricoResponse_QNAME = new QName("http://ws.creditreport.equifax.com.pe/endpoint", "GetReporteHistoricoResponse");
    private final static QName _ConsultaHistorica_QNAME = new QName("http://ws.creditreport.equifax.com.pe/document", "ConsultaHistorica");
    private final static QName _Cuenta_QNAME = new QName("http://ws.creditreport.equifax.com.pe/endpoint", "Cuenta");
    private final static QName _GetReporteOnline_QNAME = new QName("http://ws.creditreport.equifax.com.pe/endpoint", "GetReporteOnline");
    private final static QName _GetReporteHistorico_QNAME = new QName("http://ws.creditreport.equifax.com.pe/endpoint", "GetReporteHistorico");
    private final static QName _CarteraMorosaDetalleProtestos_QNAME = new QName("", "DetalleProtestos");
    private final static QName _CarteraMorosaResumenProtestos_QNAME = new QName("", "ResumenProtestos");
    private final static QName _ComercioExteriorComercioExteriorAnualPeriodosPeriodoExportacionesExportacionNumeroOperacion_QNAME = new QName("", "NumeroOperacion");
    private final static QName _ComercioExteriorComercioExteriorAnualPeriodosPeriodoExportacionesExportacionTotalFOB_QNAME = new QName("", "TotalFOB");
    private final static QName _ComercioExteriorComercioExteriorAnualPeriodosPeriodoExportacionesExportacionPaisDestino_QNAME = new QName("", "PaisDestino");
    private final static QName _SistemaFinancieroRCCPeriodos_QNAME = new QName("", "Periodos");
    private final static QName _SistemaFinancieroRCCDetalleEntidades_QNAME = new QName("", "DetalleEntidades");
    private final static QName _SistemaFinancieroRectificacionesRectificacionEntidades_QNAME = new QName("", "Entidades");
    private final static QName _SistemaFinancieroMicrofinanzasPeriodosPeriodoDeudasDeudaMonto_QNAME = new QName("", "Monto");
    private final static QName _SistemaFinancieroMicrofinanzasPeriodosPeriodoDeudasDeudaNombreEntidad_QNAME = new QName("", "NombreEntidad");
    private final static QName _SistemaFinancieroMicrofinanzasPeriodosPeriodoDeudasDeudaCodigoEntidad_QNAME = new QName("", "CodigoEntidad");
    private final static QName _SistemaFinancieroMicrofinanzasPeriodosPeriodoDeudasDeudaCuenta_QNAME = new QName("", "Cuenta");
    private final static QName _DirectorioSUNATDirectorioCodigoCIIU_QNAME = new QName("", "CodigoCIIU");
    private final static QName _ComercioExteriorComercioExteriorAnualPeriodosPeriodoExportaciones_QNAME = new QName("", "Exportaciones");
    private final static QName _ComercioExteriorComercioExteriorAnualPeriodosPeriodoImportaciones_QNAME = new QName("", "Importaciones");
    private final static QName _CarteraMorosaDetalleProtestosProtestoTipoDocumento_QNAME = new QName("", "TipoDocumento");
    private final static QName _CarteraMorosaDetalleProtestosProtestoMoneda_QNAME = new QName("", "Moneda");
    private final static QName _CarteraMorosaDetalleProtestosProtestoEmisor_QNAME = new QName("", "Emisor");
    private final static QName _CarteraMorosaDetalleProtestosProtestoFechaVencimiento_QNAME = new QName("", "FechaVencimiento");
    private final static QName _CarteraMorosaDetalleProtestosProtestoFechaAclaracion_QNAME = new QName("", "FechaAclaracion");
    private final static QName _CarteraMorosaDetalleProtestosProtestoNotaria_QNAME = new QName("", "Notaria");
    private final static QName _CarteraMorosaDetalleProtestosProtestoNumeroBoletin_QNAME = new QName("", "NumeroBoletin");
    private final static QName _DireccionesDireccionFecha_QNAME = new QName("", "Fecha");
    private final static QName _DireccionesDireccionTelefono_QNAME = new QName("", "Telefono");
    private final static QName _DireccionesDireccionUbigeo_QNAME = new QName("", "Ubigeo");
    private final static QName _DireccionesDireccionAnexo_QNAME = new QName("", "Anexo");
    private final static QName _DireccionesDireccionDescripcion_QNAME = new QName("", "Descripcion");
    private final static QName _DireccionesDireccionFuente_QNAME = new QName("", "Fuente");
    private final static QName _SistemaFinancieroRCCPeriodosPeriodoDeudasDeudaNombreCuenta_QNAME = new QName("", "NombreCuenta");
    private final static QName _SistemaFinancieroRCCPeriodosPeriodoDeudasDeudaDescripcionCuenta_QNAME = new QName("", "DescripcionCuenta");
    private final static QName _SistemaFinancieroRCCPeriodosPeriodoDeudasDeudaCodigoCuenta_QNAME = new QName("", "CodigoCuenta");
    private final static QName _SistemaFinancieroRCCPeriodosPeriodoDeudasDeudaCalificacion_QNAME = new QName("", "Calificacion");
    private final static QName _ComercioExteriorComercioExteriorPaisPaises_QNAME = new QName("", "Paises");
    private final static QName _ScoreEmpresasNivelRiesgo_QNAME = new QName("", "NivelRiesgo");
    private final static QName _ScoreEmpresasPuntaje_QNAME = new QName("", "Puntaje");
    private final static QName _ScoreEmpresasConclusion_QNAME = new QName("", "Conclusion");
    private final static QName _ComercioExteriorComercioExteriorMensualPeriodosPeriodoImportacionesImportacionPaisProcedencia_QNAME = new QName("", "PaisProcedencia");
    private final static QName _SistemaFinancieroRectificacionesRectificacionEntidadesEntidadNombre_QNAME = new QName("", "Nombre");
    private final static QName _SistemaFinancieroRectificacionesRectificacionEntidadesEntidadCodigo_QNAME = new QName("", "Codigo");
    private final static QName _SistemaFinancieroRectificacionesRectificacionEntidadesEntidadDetalles_QNAME = new QName("", "Detalles");
    private final static QName _ComercioExteriorComercioExteriorAnual_QNAME = new QName("", "ComercioExteriorAnual");
    private final static QName _ComercioExteriorComercioExteriorMensual_QNAME = new QName("", "ComercioExteriorMensual");
    private final static QName _ComercioExteriorComercioExteriorPais_QNAME = new QName("", "ComercioExteriorPais");
    private final static QName _ReclamosReclamoProducto_QNAME = new QName("", "Producto");
    private final static QName _CarteraMorosaResumenProtestosPeriodoMontoSolesAclarados_QNAME = new QName("", "MontoSolesAclarados");
    private final static QName _CarteraMorosaResumenProtestosPeriodoMontoSolesNoAclarados_QNAME = new QName("", "MontoSolesNoAclarados");
    private final static QName _CarteraMorosaResumenProtestosPeriodoMontoDolaresAclarados_QNAME = new QName("", "MontoDolaresAclarados");
    private final static QName _CarteraMorosaResumenProtestosPeriodoMontoDolaresNoAclarados_QNAME = new QName("", "MontoDolaresNoAclarados");
    private final static QName _SistemaFinancieroRCCDetalleEntidadesEntidadCreditosRefinanciados_QNAME = new QName("", "CreditosRefinanciados");
    private final static QName _SistemaFinancieroRCCDetalleEntidadesEntidadCreditosVencidos_QNAME = new QName("", "CreditosVencidos");
    private final static QName _SistemaFinancieroRCCDetalleEntidadesEntidadCreditosVigentes_QNAME = new QName("", "CreditosVigentes");
    private final static QName _SistemaFinancieroRCCDetalleEntidadesEntidadCreditosJudicial_QNAME = new QName("", "CreditosJudicial");
    private final static QName _SistemaFinancieroRectificacionesRectificacionEntidadesEntidadDetallesDetalleDice_QNAME = new QName("", "Dice");
    private final static QName _SistemaFinancieroRectificacionesRectificacionEntidadesEntidadDetallesDetalleDebedecir_QNAME = new QName("", "Debedecir");
    private final static QName _SistemaFinancieroRectificacionesRectificacionEntidadesEntidadDetallesDetalleConcepto_QNAME = new QName("", "Concepto");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.affirm.equifax.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BoletinOficial }
     * 
     */
    public BoletinOficial createBoletinOficial() {
        return new BoletinOficial();
    }

    /**
     * Create an instance of {@link ComercioExterior }
     * 
     */
    public ComercioExterior createComercioExterior() {
        return new ComercioExterior();
    }

    /**
     * Create an instance of {@link DirectorioSUNAT }
     * 
     */
    public DirectorioSUNAT createDirectorioSUNAT() {
        return new DirectorioSUNAT();
    }

    /**
     * Create an instance of {@link BuenosContribuyentes }
     * 
     */
    public BuenosContribuyentes createBuenosContribuyentes() {
        return new BuenosContribuyentes();
    }

    /**
     * Create an instance of {@link Reclamos }
     * 
     */
    public Reclamos createReclamos() {
        return new Reclamos();
    }

    /**
     * Create an instance of {@link RepresentantesLegales }
     * 
     */
    public RepresentantesLegales createRepresentantesLegales() {
        return new RepresentantesLegales();
    }

    /**
     * Create an instance of {@link CuotaHistorica }
     * 
     */
    public CuotaHistorica createCuotaHistorica() {
        return new CuotaHistorica();
    }

    /**
     * Create an instance of {@link DeudasImpagas }
     * 
     */
    public DeudasImpagas createDeudasImpagas() {
        return new DeudasImpagas();
    }

    /**
     * Create an instance of {@link CarteraMorosa }
     * 
     */
    public CarteraMorosa createCarteraMorosa() {
        return new CarteraMorosa();
    }

    /**
     * Create an instance of {@link CuotaVigente }
     * 
     */
    public CuotaVigente createCuotaVigente() {
        return new CuotaVigente();
    }

    /**
     * Create an instance of {@link SistemaFinanciero }
     * 
     */
    public SistemaFinanciero createSistemaFinanciero() {
        return new SistemaFinanciero();
    }

    /**
     * Create an instance of {@link com.affirm.equifax.ws.Direcciones }
     * 
     */
    public com.affirm.equifax.ws.Direcciones createDirecciones() {
        return new com.affirm.equifax.ws.Direcciones();
    }

    /**
     * Create an instance of {@link IndicadorDeConsulta }
     * 
     */
    public IndicadorDeConsulta createIndicadorDeConsulta() {
        return new IndicadorDeConsulta();
    }

    /**
     * Create an instance of {@link ReporteCrediticio }
     * 
     */
    public ReporteCrediticio createReporteCrediticio() {
        return new ReporteCrediticio();
    }

    /**
     * Create an instance of {@link Reporte }
     * 
     */
    public Reporte createReporte() {
        return new Reporte();
    }

    /**
     * Create an instance of {@link Reporte.Modulos }
     * 
     */
    public Reporte.Modulos createReporteModulos() {
        return new Reporte.Modulos();
    }

    /**
     * Create an instance of {@link Reporte.Modulos.Modulo }
     * 
     */
    public Reporte.Modulos.Modulo createReporteModulosModulo() {
        return new Reporte.Modulos.Modulo();
    }

    /**
     * Create an instance of {@link ReporteCrediticio.Modulos }
     * 
     */
    public ReporteCrediticio.Modulos createReporteCrediticioModulos() {
        return new ReporteCrediticio.Modulos();
    }

    /**
     * Create an instance of {@link ReporteCrediticio.Modulos.Modulo }
     * 
     */
    public ReporteCrediticio.Modulos.Modulo createReporteCrediticioModulosModulo() {
        return new ReporteCrediticio.Modulos.Modulo();
    }

    /**
     * Create an instance of {@link IndicadorDeConsulta.Entidades }
     * 
     */
    public IndicadorDeConsulta.Entidades createIndicadorDeConsultaEntidades() {
        return new IndicadorDeConsulta.Entidades();
    }

    /**
     * Create an instance of {@link IndicadorDeConsulta.Entidades.Entidad }
     * 
     */
    public IndicadorDeConsulta.Entidades.Entidad createIndicadorDeConsultaEntidadesEntidad() {
        return new IndicadorDeConsulta.Entidades.Entidad();
    }

    /**
     * Create an instance of {@link IndicadorDeConsulta.Entidades.Entidad.Periodos }
     * 
     */
    public IndicadorDeConsulta.Entidades.Entidad.Periodos createIndicadorDeConsultaEntidadesEntidadPeriodos() {
        return new IndicadorDeConsulta.Entidades.Entidad.Periodos();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Microfinanzas }
     * 
     */
    public SistemaFinanciero.Microfinanzas createSistemaFinancieroMicrofinanzas() {
        return new SistemaFinanciero.Microfinanzas();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Microfinanzas.Periodos }
     * 
     */
    public SistemaFinanciero.Microfinanzas.Periodos createSistemaFinancieroMicrofinanzasPeriodos() {
        return new SistemaFinanciero.Microfinanzas.Periodos();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Microfinanzas.Periodos.Periodo }
     * 
     */
    public SistemaFinanciero.Microfinanzas.Periodos.Periodo createSistemaFinancieroMicrofinanzasPeriodosPeriodo() {
        return new SistemaFinanciero.Microfinanzas.Periodos.Periodo();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Microfinanzas.Periodos.Periodo.Deudas }
     * 
     */
    public SistemaFinanciero.Microfinanzas.Periodos.Periodo.Deudas createSistemaFinancieroMicrofinanzasPeriodosPeriodoDeudas() {
        return new SistemaFinanciero.Microfinanzas.Periodos.Periodo.Deudas();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Microfinanzas.CalificacionEntidad }
     * 
     */
    public SistemaFinanciero.Microfinanzas.CalificacionEntidad createSistemaFinancieroMicrofinanzasCalificacionEntidad() {
        return new SistemaFinanciero.Microfinanzas.CalificacionEntidad();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Microfinanzas.CalificacionEntidad.Entidades }
     * 
     */
    public SistemaFinanciero.Microfinanzas.CalificacionEntidad.Entidades createSistemaFinancieroMicrofinanzasCalificacionEntidadEntidades() {
        return new SistemaFinanciero.Microfinanzas.CalificacionEntidad.Entidades();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Avalistas }
     * 
     */
    public SistemaFinanciero.Avalistas createSistemaFinancieroAvalistas() {
        return new SistemaFinanciero.Avalistas();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Avalistas.Aval }
     * 
     */
    public SistemaFinanciero.Avalistas.Aval createSistemaFinancieroAvalistasAval() {
        return new SistemaFinanciero.Avalistas.Aval();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Avalistas.Aval.Entidades }
     * 
     */
    public SistemaFinanciero.Avalistas.Aval.Entidades createSistemaFinancieroAvalistasAvalEntidades() {
        return new SistemaFinanciero.Avalistas.Aval.Entidades();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Avalistas.Aval.Entidades.Entidad }
     * 
     */
    public SistemaFinanciero.Avalistas.Aval.Entidades.Entidad createSistemaFinancieroAvalistasAvalEntidadesEntidad() {
        return new SistemaFinanciero.Avalistas.Aval.Entidades.Entidad();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Avalistas.Aval.Entidades.Entidad.Periodos }
     * 
     */
    public SistemaFinanciero.Avalistas.Aval.Entidades.Entidad.Periodos createSistemaFinancieroAvalistasAvalEntidadesEntidadPeriodos() {
        return new SistemaFinanciero.Avalistas.Aval.Entidades.Entidad.Periodos();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Rectificaciones }
     * 
     */
    public SistemaFinanciero.Rectificaciones createSistemaFinancieroRectificaciones() {
        return new SistemaFinanciero.Rectificaciones();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Rectificaciones.Rectificacion }
     * 
     */
    public SistemaFinanciero.Rectificaciones.Rectificacion createSistemaFinancieroRectificacionesRectificacion() {
        return new SistemaFinanciero.Rectificaciones.Rectificacion();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Rectificaciones.Rectificacion.Entidades }
     * 
     */
    public SistemaFinanciero.Rectificaciones.Rectificacion.Entidades createSistemaFinancieroRectificacionesRectificacionEntidades() {
        return new SistemaFinanciero.Rectificaciones.Rectificacion.Entidades();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad }
     * 
     */
    public SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad createSistemaFinancieroRectificacionesRectificacionEntidadesEntidad() {
        return new SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.Detalles }
     * 
     */
    public SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.Detalles createSistemaFinancieroRectificacionesRectificacionEntidadesEntidadDetalles() {
        return new SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.Detalles();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.RCC }
     * 
     */
    public SistemaFinanciero.RCC createSistemaFinancieroRCC() {
        return new SistemaFinanciero.RCC();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.RCC.Periodos }
     * 
     */
    public SistemaFinanciero.RCC.Periodos createSistemaFinancieroRCCPeriodos() {
        return new SistemaFinanciero.RCC.Periodos();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.RCC.Periodos.Periodo }
     * 
     */
    public SistemaFinanciero.RCC.Periodos.Periodo createSistemaFinancieroRCCPeriodosPeriodo() {
        return new SistemaFinanciero.RCC.Periodos.Periodo();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.RCC.Periodos.Periodo.Deudas }
     * 
     */
    public SistemaFinanciero.RCC.Periodos.Periodo.Deudas createSistemaFinancieroRCCPeriodosPeriodoDeudas() {
        return new SistemaFinanciero.RCC.Periodos.Periodo.Deudas();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.RCC.DetalleEntidades }
     * 
     */
    public SistemaFinanciero.RCC.DetalleEntidades createSistemaFinancieroRCCDetalleEntidades() {
        return new SistemaFinanciero.RCC.DetalleEntidades();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.DeudasHistoricas }
     * 
     */
    public SistemaFinanciero.DeudasHistoricas createSistemaFinancieroDeudasHistoricas() {
        return new SistemaFinanciero.DeudasHistoricas();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.DeudasUltimoPeriodo }
     * 
     */
    public SistemaFinanciero.DeudasUltimoPeriodo createSistemaFinancieroDeudasUltimoPeriodo() {
        return new SistemaFinanciero.DeudasUltimoPeriodo();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.DeudasUltimoPeriodo.Deuda }
     * 
     */
    public SistemaFinanciero.DeudasUltimoPeriodo.Deuda createSistemaFinancieroDeudasUltimoPeriodoDeuda() {
        return new SistemaFinanciero.DeudasUltimoPeriodo.Deuda();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.DeudasUltimoPeriodo.Deuda.Productos }
     * 
     */
    public SistemaFinanciero.DeudasUltimoPeriodo.Deuda.Productos createSistemaFinancieroDeudasUltimoPeriodoDeudaProductos() {
        return new SistemaFinanciero.DeudasUltimoPeriodo.Deuda.Productos();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.ResumenComportamientoPago }
     * 
     */
    public SistemaFinanciero.ResumenComportamientoPago createSistemaFinancieroResumenComportamientoPago() {
        return new SistemaFinanciero.ResumenComportamientoPago();
    }

    /**
     * Create an instance of {@link CuotaVigente.DeudasPersonales }
     * 
     */
    public CuotaVigente.DeudasPersonales createCuotaVigenteDeudasPersonales() {
        return new CuotaVigente.DeudasPersonales();
    }

    /**
     * Create an instance of {@link CuotaVigente.DeudasPersonales.Entidades }
     * 
     */
    public CuotaVigente.DeudasPersonales.Entidades createCuotaVigenteDeudasPersonalesEntidades() {
        return new CuotaVigente.DeudasPersonales.Entidades();
    }

    /**
     * Create an instance of {@link CuotaVigente.DeudasPersonales.Entidades.Entidad }
     * 
     */
    public CuotaVigente.DeudasPersonales.Entidades.Entidad createCuotaVigenteDeudasPersonalesEntidadesEntidad() {
        return new CuotaVigente.DeudasPersonales.Entidades.Entidad();
    }

    /**
     * Create an instance of {@link CuotaVigente.DeudasPersonales.Entidades.Entidad.Deudas }
     * 
     */
    public CuotaVigente.DeudasPersonales.Entidades.Entidad.Deudas createCuotaVigenteDeudasPersonalesEntidadesEntidadDeudas() {
        return new CuotaVigente.DeudasPersonales.Entidades.Entidad.Deudas();
    }

    /**
     * Create an instance of {@link CuotaVigente.DeudasNoSupervisadas }
     * 
     */
    public CuotaVigente.DeudasNoSupervisadas createCuotaVigenteDeudasNoSupervisadas() {
        return new CuotaVigente.DeudasNoSupervisadas();
    }

    /**
     * Create an instance of {@link CuotaVigente.DeudasNoSupervisadas.Entidades }
     * 
     */
    public CuotaVigente.DeudasNoSupervisadas.Entidades createCuotaVigenteDeudasNoSupervisadasEntidades() {
        return new CuotaVigente.DeudasNoSupervisadas.Entidades();
    }

    /**
     * Create an instance of {@link CuotaVigente.DeudasNoSupervisadas.Entidades.Entidad }
     * 
     */
    public CuotaVigente.DeudasNoSupervisadas.Entidades.Entidad createCuotaVigenteDeudasNoSupervisadasEntidadesEntidad() {
        return new CuotaVigente.DeudasNoSupervisadas.Entidades.Entidad();
    }

    /**
     * Create an instance of {@link CuotaVigente.DeudasNoSupervisadas.Entidades.Entidad.Deudas }
     * 
     */
    public CuotaVigente.DeudasNoSupervisadas.Entidades.Entidad.Deudas createCuotaVigenteDeudasNoSupervisadasEntidadesEntidadDeudas() {
        return new CuotaVigente.DeudasNoSupervisadas.Entidades.Entidad.Deudas();
    }

    /**
     * Create an instance of {@link CuotaVigente.DeudasSupervisadas }
     * 
     */
    public CuotaVigente.DeudasSupervisadas createCuotaVigenteDeudasSupervisadas() {
        return new CuotaVigente.DeudasSupervisadas();
    }

    /**
     * Create an instance of {@link CuotaVigente.DeudasSupervisadas.Entidades }
     * 
     */
    public CuotaVigente.DeudasSupervisadas.Entidades createCuotaVigenteDeudasSupervisadasEntidades() {
        return new CuotaVigente.DeudasSupervisadas.Entidades();
    }

    /**
     * Create an instance of {@link CuotaVigente.DeudasSupervisadas.Entidades.Entidad }
     * 
     */
    public CuotaVigente.DeudasSupervisadas.Entidades.Entidad createCuotaVigenteDeudasSupervisadasEntidadesEntidad() {
        return new CuotaVigente.DeudasSupervisadas.Entidades.Entidad();
    }

    /**
     * Create an instance of {@link CuotaVigente.DeudasSupervisadas.Entidades.Entidad.Deudas }
     * 
     */
    public CuotaVigente.DeudasSupervisadas.Entidades.Entidad.Deudas createCuotaVigenteDeudasSupervisadasEntidadesEntidadDeudas() {
        return new CuotaVigente.DeudasSupervisadas.Entidades.Entidad.Deudas();
    }

    /**
     * Create an instance of {@link CarteraMorosa.DetalleProtestos }
     * 
     */
    public CarteraMorosa.DetalleProtestos createCarteraMorosaDetalleProtestos() {
        return new CarteraMorosa.DetalleProtestos();
    }

    /**
     * Create an instance of {@link CarteraMorosa.ResumenProtestos }
     * 
     */
    public CarteraMorosa.ResumenProtestos createCarteraMorosaResumenProtestos() {
        return new CarteraMorosa.ResumenProtestos();
    }

    /**
     * Create an instance of {@link DeudasImpagas.DeudasAFP }
     * 
     */
    public DeudasImpagas.DeudasAFP createDeudasImpagasDeudasAFP() {
        return new DeudasImpagas.DeudasAFP();
    }

    /**
     * Create an instance of {@link DeudasImpagas.DeudasAFP.PeriodoAFP }
     * 
     */
    public DeudasImpagas.DeudasAFP.PeriodoAFP createDeudasImpagasDeudasAFPPeriodoAFP() {
        return new DeudasImpagas.DeudasAFP.PeriodoAFP();
    }

    /**
     * Create an instance of {@link DeudasImpagas.DeudasAFP.PeriodoAFP.TipoDeudas }
     * 
     */
    public DeudasImpagas.DeudasAFP.PeriodoAFP.TipoDeudas createDeudasImpagasDeudasAFPPeriodoAFPTipoDeudas() {
        return new DeudasImpagas.DeudasAFP.PeriodoAFP.TipoDeudas();
    }

    /**
     * Create an instance of {@link DeudasImpagas.TarjetasAnuladas }
     * 
     */
    public DeudasImpagas.TarjetasAnuladas createDeudasImpagasTarjetasAnuladas() {
        return new DeudasImpagas.TarjetasAnuladas();
    }

    /**
     * Create an instance of {@link DeudasImpagas.TarjetasAnuladas.Detalle }
     * 
     */
    public DeudasImpagas.TarjetasAnuladas.Detalle createDeudasImpagasTarjetasAnuladasDetalle() {
        return new DeudasImpagas.TarjetasAnuladas.Detalle();
    }

    /**
     * Create an instance of {@link DeudasImpagas.CuentasCerradas }
     * 
     */
    public DeudasImpagas.CuentasCerradas createDeudasImpagasCuentasCerradas() {
        return new DeudasImpagas.CuentasCerradas();
    }

    /**
     * Create an instance of {@link DeudasImpagas.CuentasCerradas.Detalle }
     * 
     */
    public DeudasImpagas.CuentasCerradas.Detalle createDeudasImpagasCuentasCerradasDetalle() {
        return new DeudasImpagas.CuentasCerradas.Detalle();
    }

    /**
     * Create an instance of {@link DeudasImpagas.Protestos }
     * 
     */
    public DeudasImpagas.Protestos createDeudasImpagasProtestos() {
        return new DeudasImpagas.Protestos();
    }

    /**
     * Create an instance of {@link DeudasImpagas.Protestos.ProtestosNoAclarados }
     * 
     */
    public DeudasImpagas.Protestos.ProtestosNoAclarados createDeudasImpagasProtestosProtestosNoAclarados() {
        return new DeudasImpagas.Protestos.ProtestosNoAclarados();
    }

    /**
     * Create an instance of {@link DeudasImpagas.Protestos.ProtestosNoAclarados.Detalle }
     * 
     */
    public DeudasImpagas.Protestos.ProtestosNoAclarados.Detalle createDeudasImpagasProtestosProtestosNoAclaradosDetalle() {
        return new DeudasImpagas.Protestos.ProtestosNoAclarados.Detalle();
    }

    /**
     * Create an instance of {@link DeudasImpagas.Protestos.ProtestosAclarados }
     * 
     */
    public DeudasImpagas.Protestos.ProtestosAclarados createDeudasImpagasProtestosProtestosAclarados() {
        return new DeudasImpagas.Protestos.ProtestosAclarados();
    }

    /**
     * Create an instance of {@link DeudasImpagas.Protestos.ProtestosAclarados.Detalle }
     * 
     */
    public DeudasImpagas.Protestos.ProtestosAclarados.Detalle createDeudasImpagasProtestosProtestosAclaradosDetalle() {
        return new DeudasImpagas.Protestos.ProtestosAclarados.Detalle();
    }

    /**
     * Create an instance of {@link DeudasImpagas.Omisos }
     * 
     */
    public DeudasImpagas.Omisos createDeudasImpagasOmisos() {
        return new DeudasImpagas.Omisos();
    }

    /**
     * Create an instance of {@link DeudasImpagas.Omisos.Detalle }
     * 
     */
    public DeudasImpagas.Omisos.Detalle createDeudasImpagasOmisosDetalle() {
        return new DeudasImpagas.Omisos.Detalle();
    }

    /**
     * Create an instance of {@link DeudasImpagas.NegativoSunat }
     * 
     */
    public DeudasImpagas.NegativoSunat createDeudasImpagasNegativoSunat() {
        return new DeudasImpagas.NegativoSunat();
    }

    /**
     * Create an instance of {@link DeudasImpagas.NegativoSunat.Detalle }
     * 
     */
    public DeudasImpagas.NegativoSunat.Detalle createDeudasImpagasNegativoSunatDetalle() {
        return new DeudasImpagas.NegativoSunat.Detalle();
    }

    /**
     * Create an instance of {@link DeudasImpagas.Sicom }
     * 
     */
    public DeudasImpagas.Sicom createDeudasImpagasSicom() {
        return new DeudasImpagas.Sicom();
    }

    /**
     * Create an instance of {@link DeudasImpagas.Sicom.Detalle }
     * 
     */
    public DeudasImpagas.Sicom.Detalle createDeudasImpagasSicomDetalle() {
        return new DeudasImpagas.Sicom.Detalle();
    }

    /**
     * Create an instance of {@link DeudasImpagas.ResumenDeudasImpagas }
     * 
     */
    public DeudasImpagas.ResumenDeudasImpagas createDeudasImpagasResumenDeudasImpagas() {
        return new DeudasImpagas.ResumenDeudasImpagas();
    }

    /**
     * Create an instance of {@link DeudasImpagas.ResumenDeudasImpagas.SemaforoPeriodo }
     * 
     */
    public DeudasImpagas.ResumenDeudasImpagas.SemaforoPeriodo createDeudasImpagasResumenDeudasImpagasSemaforoPeriodo() {
        return new DeudasImpagas.ResumenDeudasImpagas.SemaforoPeriodo();
    }

    /**
     * Create an instance of {@link DeudasImpagas.ResumenDeudasImpagas.SemaforoPeriodo.DetalleProductos }
     * 
     */
    public DeudasImpagas.ResumenDeudasImpagas.SemaforoPeriodo.DetalleProductos createDeudasImpagasResumenDeudasImpagasSemaforoPeriodoDetalleProductos() {
        return new DeudasImpagas.ResumenDeudasImpagas.SemaforoPeriodo.DetalleProductos();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasPersonales }
     * 
     */
    public CuotaHistorica.DeudasPersonales createCuotaHistoricaDeudasPersonales() {
        return new CuotaHistorica.DeudasPersonales();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasPersonales.Entidades }
     * 
     */
    public CuotaHistorica.DeudasPersonales.Entidades createCuotaHistoricaDeudasPersonalesEntidades() {
        return new CuotaHistorica.DeudasPersonales.Entidades();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasPersonales.Entidades.Entidad }
     * 
     */
    public CuotaHistorica.DeudasPersonales.Entidades.Entidad createCuotaHistoricaDeudasPersonalesEntidadesEntidad() {
        return new CuotaHistorica.DeudasPersonales.Entidades.Entidad();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas }
     * 
     */
    public CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas createCuotaHistoricaDeudasPersonalesEntidadesEntidadDeudas() {
        return new CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda }
     * 
     */
    public CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda createCuotaHistoricaDeudasPersonalesEntidadesEntidadDeudasDeuda() {
        return new CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.CuotaMensual }
     * 
     */
    public CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.CuotaMensual createCuotaHistoricaDeudasPersonalesEntidadesEntidadDeudasDeudaCuotaMensual() {
        return new CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.CuotaMensual();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.SaldoPromedio }
     * 
     */
    public CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.SaldoPromedio createCuotaHistoricaDeudasPersonalesEntidadesEntidadDeudasDeudaSaldoPromedio() {
        return new CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.SaldoPromedio();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.SaldoUltimoMes }
     * 
     */
    public CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.SaldoUltimoMes createCuotaHistoricaDeudasPersonalesEntidadesEntidadDeudasDeudaSaldoUltimoMes() {
        return new CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.SaldoUltimoMes();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.LineaTotal }
     * 
     */
    public CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.LineaTotal createCuotaHistoricaDeudasPersonalesEntidadesEntidadDeudasDeudaLineaTotal() {
        return new CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.LineaTotal();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasPersonales.EntidadesReportadas }
     * 
     */
    public CuotaHistorica.DeudasPersonales.EntidadesReportadas createCuotaHistoricaDeudasPersonalesEntidadesReportadas() {
        return new CuotaHistorica.DeudasPersonales.EntidadesReportadas();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasNoSupervisadas }
     * 
     */
    public CuotaHistorica.DeudasNoSupervisadas createCuotaHistoricaDeudasNoSupervisadas() {
        return new CuotaHistorica.DeudasNoSupervisadas();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasNoSupervisadas.Entidades }
     * 
     */
    public CuotaHistorica.DeudasNoSupervisadas.Entidades createCuotaHistoricaDeudasNoSupervisadasEntidades() {
        return new CuotaHistorica.DeudasNoSupervisadas.Entidades();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad }
     * 
     */
    public CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad createCuotaHistoricaDeudasNoSupervisadasEntidadesEntidad() {
        return new CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas }
     * 
     */
    public CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas createCuotaHistoricaDeudasNoSupervisadasEntidadesEntidadDeudas() {
        return new CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda }
     * 
     */
    public CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda createCuotaHistoricaDeudasNoSupervisadasEntidadesEntidadDeudasDeuda() {
        return new CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.CuotaMensual }
     * 
     */
    public CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.CuotaMensual createCuotaHistoricaDeudasNoSupervisadasEntidadesEntidadDeudasDeudaCuotaMensual() {
        return new CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.CuotaMensual();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoPromedio }
     * 
     */
    public CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoPromedio createCuotaHistoricaDeudasNoSupervisadasEntidadesEntidadDeudasDeudaSaldoPromedio() {
        return new CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoPromedio();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoUltimoMes }
     * 
     */
    public CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoUltimoMes createCuotaHistoricaDeudasNoSupervisadasEntidadesEntidadDeudasDeudaSaldoUltimoMes() {
        return new CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoUltimoMes();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.LineaTotal }
     * 
     */
    public CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.LineaTotal createCuotaHistoricaDeudasNoSupervisadasEntidadesEntidadDeudasDeudaLineaTotal() {
        return new CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.LineaTotal();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasNoSupervisadas.EntidadesReportadas }
     * 
     */
    public CuotaHistorica.DeudasNoSupervisadas.EntidadesReportadas createCuotaHistoricaDeudasNoSupervisadasEntidadesReportadas() {
        return new CuotaHistorica.DeudasNoSupervisadas.EntidadesReportadas();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasSupervisadas }
     * 
     */
    public CuotaHistorica.DeudasSupervisadas createCuotaHistoricaDeudasSupervisadas() {
        return new CuotaHistorica.DeudasSupervisadas();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasSupervisadas.Entidades }
     * 
     */
    public CuotaHistorica.DeudasSupervisadas.Entidades createCuotaHistoricaDeudasSupervisadasEntidades() {
        return new CuotaHistorica.DeudasSupervisadas.Entidades();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasSupervisadas.Entidades.Entidad }
     * 
     */
    public CuotaHistorica.DeudasSupervisadas.Entidades.Entidad createCuotaHistoricaDeudasSupervisadasEntidadesEntidad() {
        return new CuotaHistorica.DeudasSupervisadas.Entidades.Entidad();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas }
     * 
     */
    public CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas createCuotaHistoricaDeudasSupervisadasEntidadesEntidadDeudas() {
        return new CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda }
     * 
     */
    public CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda createCuotaHistoricaDeudasSupervisadasEntidadesEntidadDeudasDeuda() {
        return new CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.CuotaMensual }
     * 
     */
    public CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.CuotaMensual createCuotaHistoricaDeudasSupervisadasEntidadesEntidadDeudasDeudaCuotaMensual() {
        return new CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.CuotaMensual();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoPromedio }
     * 
     */
    public CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoPromedio createCuotaHistoricaDeudasSupervisadasEntidadesEntidadDeudasDeudaSaldoPromedio() {
        return new CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoPromedio();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoUltimoMes }
     * 
     */
    public CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoUltimoMes createCuotaHistoricaDeudasSupervisadasEntidadesEntidadDeudasDeudaSaldoUltimoMes() {
        return new CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoUltimoMes();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.LineaTotal }
     * 
     */
    public CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.LineaTotal createCuotaHistoricaDeudasSupervisadasEntidadesEntidadDeudasDeudaLineaTotal() {
        return new CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.LineaTotal();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasSupervisadas.EntidadesReportadas }
     * 
     */
    public CuotaHistorica.DeudasSupervisadas.EntidadesReportadas createCuotaHistoricaDeudasSupervisadasEntidadesReportadas() {
        return new CuotaHistorica.DeudasSupervisadas.EntidadesReportadas();
    }

    /**
     * Create an instance of {@link RepresentantesLegales.RepresentandosPor }
     * 
     */
    public RepresentantesLegales.RepresentandosPor createRepresentantesLegalesRepresentandosPor() {
        return new RepresentantesLegales.RepresentandosPor();
    }

    /**
     * Create an instance of {@link RepresentantesLegales.RepresentantesDe }
     * 
     */
    public RepresentantesLegales.RepresentantesDe createRepresentantesLegalesRepresentantesDe() {
        return new RepresentantesLegales.RepresentantesDe();
    }

    /**
     * Create an instance of {@link BuenosContribuyentes.Contribuyente }
     * 
     */
    public BuenosContribuyentes.Contribuyente createBuenosContribuyentesContribuyente() {
        return new BuenosContribuyentes.Contribuyente();
    }

    /**
     * Create an instance of {@link DirectorioSUNAT.Directorio }
     * 
     */
    public DirectorioSUNAT.Directorio createDirectorioSUNATDirectorio() {
        return new DirectorioSUNAT.Directorio();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorPais }
     * 
     */
    public ComercioExterior.ComercioExteriorPais createComercioExteriorComercioExteriorPais() {
        return new ComercioExterior.ComercioExteriorPais();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorPais.Paises }
     * 
     */
    public ComercioExterior.ComercioExteriorPais.Paises createComercioExteriorComercioExteriorPaisPaises() {
        return new ComercioExterior.ComercioExteriorPais.Paises();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorPais.Paises.Pais }
     * 
     */
    public ComercioExterior.ComercioExteriorPais.Paises.Pais createComercioExteriorComercioExteriorPaisPaisesPais() {
        return new ComercioExterior.ComercioExteriorPais.Paises.Pais();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorPais.Paises.Pais.Exportaciones }
     * 
     */
    public ComercioExterior.ComercioExteriorPais.Paises.Pais.Exportaciones createComercioExteriorComercioExteriorPaisPaisesPaisExportaciones() {
        return new ComercioExterior.ComercioExteriorPais.Paises.Pais.Exportaciones();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorPais.Paises.Pais.Importaciones }
     * 
     */
    public ComercioExterior.ComercioExteriorPais.Paises.Pais.Importaciones createComercioExteriorComercioExteriorPaisPaisesPaisImportaciones() {
        return new ComercioExterior.ComercioExteriorPais.Paises.Pais.Importaciones();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorMensual }
     * 
     */
    public ComercioExterior.ComercioExteriorMensual createComercioExteriorComercioExteriorMensual() {
        return new ComercioExterior.ComercioExteriorMensual();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorMensual.Periodos }
     * 
     */
    public ComercioExterior.ComercioExteriorMensual.Periodos createComercioExteriorComercioExteriorMensualPeriodos() {
        return new ComercioExterior.ComercioExteriorMensual.Periodos();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorMensual.Periodos.Periodo }
     * 
     */
    public ComercioExterior.ComercioExteriorMensual.Periodos.Periodo createComercioExteriorComercioExteriorMensualPeriodosPeriodo() {
        return new ComercioExterior.ComercioExteriorMensual.Periodos.Periodo();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Exportaciones }
     * 
     */
    public ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Exportaciones createComercioExteriorComercioExteriorMensualPeriodosPeriodoExportaciones() {
        return new ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Exportaciones();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Importaciones }
     * 
     */
    public ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Importaciones createComercioExteriorComercioExteriorMensualPeriodosPeriodoImportaciones() {
        return new ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Importaciones();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorAnual }
     * 
     */
    public ComercioExterior.ComercioExteriorAnual createComercioExteriorComercioExteriorAnual() {
        return new ComercioExterior.ComercioExteriorAnual();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorAnual.Periodos }
     * 
     */
    public ComercioExterior.ComercioExteriorAnual.Periodos createComercioExteriorComercioExteriorAnualPeriodos() {
        return new ComercioExterior.ComercioExteriorAnual.Periodos();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorAnual.Periodos.Periodo }
     * 
     */
    public ComercioExterior.ComercioExteriorAnual.Periodos.Periodo createComercioExteriorComercioExteriorAnualPeriodosPeriodo() {
        return new ComercioExterior.ComercioExteriorAnual.Periodos.Periodo();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Exportaciones }
     * 
     */
    public ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Exportaciones createComercioExteriorComercioExteriorAnualPeriodosPeriodoExportaciones() {
        return new ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Exportaciones();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Importaciones }
     * 
     */
    public ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Importaciones createComercioExteriorComercioExteriorAnualPeriodosPeriodoImportaciones() {
        return new ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Importaciones();
    }

    /**
     * Create an instance of {@link BoletinOficial.EscisionPatrimonio }
     * 
     */
    public BoletinOficial.EscisionPatrimonio createBoletinOficialEscisionPatrimonio() {
        return new BoletinOficial.EscisionPatrimonio();
    }

    /**
     * Create an instance of {@link BoletinOficial.ExtincionPatrimonioFamiliar }
     * 
     */
    public BoletinOficial.ExtincionPatrimonioFamiliar createBoletinOficialExtincionPatrimonioFamiliar() {
        return new BoletinOficial.ExtincionPatrimonioFamiliar();
    }

    /**
     * Create an instance of {@link BoletinOficial.ExtincionPatrimonioFamiliar.PatrimonioFamiliar }
     * 
     */
    public BoletinOficial.ExtincionPatrimonioFamiliar.PatrimonioFamiliar createBoletinOficialExtincionPatrimonioFamiliarPatrimonioFamiliar() {
        return new BoletinOficial.ExtincionPatrimonioFamiliar.PatrimonioFamiliar();
    }

    /**
     * Create an instance of {@link BoletinOficial.ExtincionPatrimonioFamiliar.PatrimonioFamiliar.Bienes }
     * 
     */
    public BoletinOficial.ExtincionPatrimonioFamiliar.PatrimonioFamiliar.Bienes createBoletinOficialExtincionPatrimonioFamiliarPatrimonioFamiliarBienes() {
        return new BoletinOficial.ExtincionPatrimonioFamiliar.PatrimonioFamiliar.Bienes();
    }

    /**
     * Create an instance of {@link BoletinOficial.ExtincionPatrimonioFamiliar.PatrimonioFamiliar.Personas }
     * 
     */
    public BoletinOficial.ExtincionPatrimonioFamiliar.PatrimonioFamiliar.Personas createBoletinOficialExtincionPatrimonioFamiliarPatrimonioFamiliarPersonas() {
        return new BoletinOficial.ExtincionPatrimonioFamiliar.PatrimonioFamiliar.Personas();
    }

    /**
     * Create an instance of {@link BoletinOficial.ConstitucionPatrimonioFamiliar }
     * 
     */
    public BoletinOficial.ConstitucionPatrimonioFamiliar createBoletinOficialConstitucionPatrimonioFamiliar() {
        return new BoletinOficial.ConstitucionPatrimonioFamiliar();
    }

    /**
     * Create an instance of {@link BoletinOficial.ConstitucionPatrimonioFamiliar.PatrimonioFamiliar }
     * 
     */
    public BoletinOficial.ConstitucionPatrimonioFamiliar.PatrimonioFamiliar createBoletinOficialConstitucionPatrimonioFamiliarPatrimonioFamiliar() {
        return new BoletinOficial.ConstitucionPatrimonioFamiliar.PatrimonioFamiliar();
    }

    /**
     * Create an instance of {@link BoletinOficial.ConstitucionPatrimonioFamiliar.PatrimonioFamiliar.Bienes }
     * 
     */
    public BoletinOficial.ConstitucionPatrimonioFamiliar.PatrimonioFamiliar.Bienes createBoletinOficialConstitucionPatrimonioFamiliarPatrimonioFamiliarBienes() {
        return new BoletinOficial.ConstitucionPatrimonioFamiliar.PatrimonioFamiliar.Bienes();
    }

    /**
     * Create an instance of {@link BoletinOficial.ConstitucionPatrimonioFamiliar.PatrimonioFamiliar.Personas }
     * 
     */
    public BoletinOficial.ConstitucionPatrimonioFamiliar.PatrimonioFamiliar.Personas createBoletinOficialConstitucionPatrimonioFamiliarPatrimonioFamiliarPersonas() {
        return new BoletinOficial.ConstitucionPatrimonioFamiliar.PatrimonioFamiliar.Personas();
    }

    /**
     * Create an instance of {@link BoletinOficial.AvisosQuiebra }
     * 
     */
    public BoletinOficial.AvisosQuiebra createBoletinOficialAvisosQuiebra() {
        return new BoletinOficial.AvisosQuiebra();
    }

    /**
     * Create an instance of {@link BoletinOficial.AvisosQuiebra.Quiebra }
     * 
     */
    public BoletinOficial.AvisosQuiebra.Quiebra createBoletinOficialAvisosQuiebraQuiebra() {
        return new BoletinOficial.AvisosQuiebra.Quiebra();
    }

    /**
     * Create an instance of {@link BoletinOficial.AvisosQuiebra.Quiebra.Liquidadores }
     * 
     */
    public BoletinOficial.AvisosQuiebra.Quiebra.Liquidadores createBoletinOficialAvisosQuiebraQuiebraLiquidadores() {
        return new BoletinOficial.AvisosQuiebra.Quiebra.Liquidadores();
    }

    /**
     * Create an instance of {@link BoletinOficial.ObligacionDarDinero }
     * 
     */
    public BoletinOficial.ObligacionDarDinero createBoletinOficialObligacionDarDinero() {
        return new BoletinOficial.ObligacionDarDinero();
    }

    /**
     * Create an instance of {@link BoletinOficial.ObligacionDarDinero.Obligacion }
     * 
     */
    public BoletinOficial.ObligacionDarDinero.Obligacion createBoletinOficialObligacionDarDineroObligacion() {
        return new BoletinOficial.ObligacionDarDinero.Obligacion();
    }

    /**
     * Create an instance of {@link BoletinOficial.ObligacionDarDinero.Obligacion.Deudores }
     * 
     */
    public BoletinOficial.ObligacionDarDinero.Obligacion.Deudores createBoletinOficialObligacionDarDineroObligacionDeudores() {
        return new BoletinOficial.ObligacionDarDinero.Obligacion.Deudores();
    }

    /**
     * Create an instance of {@link BoletinOficial.ObligacionDarDinero.Obligacion.Acreedores }
     * 
     */
    public BoletinOficial.ObligacionDarDinero.Obligacion.Acreedores createBoletinOficialObligacionDarDineroObligacionAcreedores() {
        return new BoletinOficial.ObligacionDarDinero.Obligacion.Acreedores();
    }

    /**
     * Create an instance of {@link BoletinOficial.CambiosCapital }
     * 
     */
    public BoletinOficial.CambiosCapital createBoletinOficialCambiosCapital() {
        return new BoletinOficial.CambiosCapital();
    }

    /**
     * Create an instance of {@link BoletinOficial.Disoluciones }
     * 
     */
    public BoletinOficial.Disoluciones createBoletinOficialDisoluciones() {
        return new BoletinOficial.Disoluciones();
    }

    /**
     * Create an instance of {@link BoletinOficial.Disoluciones.Disolucion }
     * 
     */
    public BoletinOficial.Disoluciones.Disolucion createBoletinOficialDisolucionesDisolucion() {
        return new BoletinOficial.Disoluciones.Disolucion();
    }

    /**
     * Create an instance of {@link BoletinOficial.Disoluciones.Disolucion.Liquidadores }
     * 
     */
    public BoletinOficial.Disoluciones.Disolucion.Liquidadores createBoletinOficialDisolucionesDisolucionLiquidadores() {
        return new BoletinOficial.Disoluciones.Disolucion.Liquidadores();
    }

    /**
     * Create an instance of {@link BoletinOficial.FusionSociedades }
     * 
     */
    public BoletinOficial.FusionSociedades createBoletinOficialFusionSociedades() {
        return new BoletinOficial.FusionSociedades();
    }

    /**
     * Create an instance of {@link BoletinOficial.FusionSociedades.Fusion }
     * 
     */
    public BoletinOficial.FusionSociedades.Fusion createBoletinOficialFusionSociedadesFusion() {
        return new BoletinOficial.FusionSociedades.Fusion();
    }

    /**
     * Create an instance of {@link BoletinOficial.FusionSociedades.Fusion.Empresas }
     * 
     */
    public BoletinOficial.FusionSociedades.Fusion.Empresas createBoletinOficialFusionSociedadesFusionEmpresas() {
        return new BoletinOficial.FusionSociedades.Fusion.Empresas();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesWarrants }
     * 
     */
    public BoletinOficial.RematesWarrants createBoletinOficialRematesWarrants() {
        return new BoletinOficial.RematesWarrants();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesWarrants.RemateWarrant }
     * 
     */
    public BoletinOficial.RematesWarrants.RemateWarrant createBoletinOficialRematesWarrantsRemateWarrant() {
        return new BoletinOficial.RematesWarrants.RemateWarrant();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesWarrants.RemateWarrant.Mercaderias }
     * 
     */
    public BoletinOficial.RematesWarrants.RemateWarrant.Mercaderias createBoletinOficialRematesWarrantsRemateWarrantMercaderias() {
        return new BoletinOficial.RematesWarrants.RemateWarrant.Mercaderias();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesWarrants.RemateWarrant.Inmuebles }
     * 
     */
    public BoletinOficial.RematesWarrants.RemateWarrant.Inmuebles createBoletinOficialRematesWarrantsRemateWarrantInmuebles() {
        return new BoletinOficial.RematesWarrants.RemateWarrant.Inmuebles();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesWarrants.RemateWarrant.Muebles }
     * 
     */
    public BoletinOficial.RematesWarrants.RemateWarrant.Muebles createBoletinOficialRematesWarrantsRemateWarrantMuebles() {
        return new BoletinOficial.RematesWarrants.RemateWarrant.Muebles();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesWarrants.RemateWarrant.Demandados }
     * 
     */
    public BoletinOficial.RematesWarrants.RemateWarrant.Demandados createBoletinOficialRematesWarrantsRemateWarrantDemandados() {
        return new BoletinOficial.RematesWarrants.RemateWarrant.Demandados();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesWarrants.RemateWarrant.Demandantes }
     * 
     */
    public BoletinOficial.RematesWarrants.RemateWarrant.Demandantes createBoletinOficialRematesWarrantsRemateWarrantDemandantes() {
        return new BoletinOficial.RematesWarrants.RemateWarrant.Demandantes();
    }

    /**
     * Create an instance of {@link BoletinOficial.ConvocatoriaAccredores }
     * 
     */
    public BoletinOficial.ConvocatoriaAccredores createBoletinOficialConvocatoriaAccredores() {
        return new BoletinOficial.ConvocatoriaAccredores();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesInmuebles }
     * 
     */
    public BoletinOficial.RematesBienesInmuebles createBoletinOficialRematesBienesInmuebles() {
        return new BoletinOficial.RematesBienesInmuebles();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesInmuebles.RemateInmueble }
     * 
     */
    public BoletinOficial.RematesBienesInmuebles.RemateInmueble createBoletinOficialRematesBienesInmueblesRemateInmueble() {
        return new BoletinOficial.RematesBienesInmuebles.RemateInmueble();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesInmuebles.RemateInmueble.Mercaderias }
     * 
     */
    public BoletinOficial.RematesBienesInmuebles.RemateInmueble.Mercaderias createBoletinOficialRematesBienesInmueblesRemateInmuebleMercaderias() {
        return new BoletinOficial.RematesBienesInmuebles.RemateInmueble.Mercaderias();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesInmuebles.RemateInmueble.Inmuebles }
     * 
     */
    public BoletinOficial.RematesBienesInmuebles.RemateInmueble.Inmuebles createBoletinOficialRematesBienesInmueblesRemateInmuebleInmuebles() {
        return new BoletinOficial.RematesBienesInmuebles.RemateInmueble.Inmuebles();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesInmuebles.RemateInmueble.Muebles }
     * 
     */
    public BoletinOficial.RematesBienesInmuebles.RemateInmueble.Muebles createBoletinOficialRematesBienesInmueblesRemateInmuebleMuebles() {
        return new BoletinOficial.RematesBienesInmuebles.RemateInmueble.Muebles();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesInmuebles.RemateInmueble.Demandados }
     * 
     */
    public BoletinOficial.RematesBienesInmuebles.RemateInmueble.Demandados createBoletinOficialRematesBienesInmueblesRemateInmuebleDemandados() {
        return new BoletinOficial.RematesBienesInmuebles.RemateInmueble.Demandados();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesInmuebles.RemateInmueble.Demandantes }
     * 
     */
    public BoletinOficial.RematesBienesInmuebles.RemateInmueble.Demandantes createBoletinOficialRematesBienesInmueblesRemateInmuebleDemandantes() {
        return new BoletinOficial.RematesBienesInmuebles.RemateInmueble.Demandantes();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesMuebles }
     * 
     */
    public BoletinOficial.RematesBienesMuebles createBoletinOficialRematesBienesMuebles() {
        return new BoletinOficial.RematesBienesMuebles();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesMuebles.RemateMueble }
     * 
     */
    public BoletinOficial.RematesBienesMuebles.RemateMueble createBoletinOficialRematesBienesMueblesRemateMueble() {
        return new BoletinOficial.RematesBienesMuebles.RemateMueble();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesMuebles.RemateMueble.Mercaderias }
     * 
     */
    public BoletinOficial.RematesBienesMuebles.RemateMueble.Mercaderias createBoletinOficialRematesBienesMueblesRemateMuebleMercaderias() {
        return new BoletinOficial.RematesBienesMuebles.RemateMueble.Mercaderias();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesMuebles.RemateMueble.Inmuebles }
     * 
     */
    public BoletinOficial.RematesBienesMuebles.RemateMueble.Inmuebles createBoletinOficialRematesBienesMueblesRemateMuebleInmuebles() {
        return new BoletinOficial.RematesBienesMuebles.RemateMueble.Inmuebles();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesMuebles.RemateMueble.Muebles }
     * 
     */
    public BoletinOficial.RematesBienesMuebles.RemateMueble.Muebles createBoletinOficialRematesBienesMueblesRemateMuebleMuebles() {
        return new BoletinOficial.RematesBienesMuebles.RemateMueble.Muebles();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesMuebles.RemateMueble.Demandados }
     * 
     */
    public BoletinOficial.RematesBienesMuebles.RemateMueble.Demandados createBoletinOficialRematesBienesMueblesRemateMuebleDemandados() {
        return new BoletinOficial.RematesBienesMuebles.RemateMueble.Demandados();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesMuebles.RemateMueble.Demandantes }
     * 
     */
    public BoletinOficial.RematesBienesMuebles.RemateMueble.Demandantes createBoletinOficialRematesBienesMueblesRemateMuebleDemandantes() {
        return new BoletinOficial.RematesBienesMuebles.RemateMueble.Demandantes();
    }

    /**
     * Create an instance of {@link EquifaxInterfaceException }
     * 
     */
    public EquifaxInterfaceException createEquifaxInterfaceException() {
        return new EquifaxInterfaceException();
    }

    /**
     * Create an instance of {@link ScoreEmpresas }
     * 
     */
    public ScoreEmpresas createScoreEmpresas() {
        return new ScoreEmpresas();
    }

    /**
     * Create an instance of {@link GetReporteOnlineResponse }
     * 
     */
    public GetReporteOnlineResponse createGetReporteOnlineResponse() {
        return new GetReporteOnlineResponse();
    }

    /**
     * Create an instance of {@link Reclamos.Reclamo }
     * 
     */
    public Reclamos.Reclamo createReclamosReclamo() {
        return new Reclamos.Reclamo();
    }

    /**
     * Create an instance of {@link GetReporteHistoricoResponse }
     * 
     */
    public GetReporteHistoricoResponse createGetReporteHistoricoResponse() {
        return new GetReporteHistoricoResponse();
    }

    /**
     * Create an instance of {@link CuotaHistorica.Garantias }
     * 
     */
    public CuotaHistorica.Garantias createCuotaHistoricaGarantias() {
        return new CuotaHistorica.Garantias();
    }

    /**
     * Create an instance of {@link GetReporteOnline }
     * 
     */
    public GetReporteOnline createGetReporteOnline() {
        return new GetReporteOnline();
    }

    /**
     * Create an instance of {@link IncomePredictor }
     * 
     */
    public IncomePredictor createIncomePredictor() {
        return new IncomePredictor();
    }

    /**
     * Create an instance of {@link RiskPredictor }
     * 
     */
    public RiskPredictor createRiskPredictor() {
        return new RiskPredictor();
    }

    /**
     * Create an instance of {@link CuotaVigente.Garantias }
     * 
     */
    public CuotaVigente.Garantias createCuotaVigenteGarantias() {
        return new CuotaVigente.Garantias();
    }

    /**
     * Create an instance of {@link DirectorioPersona }
     * 
     */
    public DirectorioPersona createDirectorioPersona() {
        return new DirectorioPersona();
    }

    /**
     * Create an instance of {@link com.affirm.equifax.ws.Direcciones.Direccion }
     * 
     */
    public com.affirm.equifax.ws.Direcciones.Direccion createDireccionesDireccion() {
        return new com.affirm.equifax.ws.Direcciones.Direccion();
    }

    /**
     * Create an instance of {@link GetReporteHistorico }
     * 
     */
    public GetReporteHistorico createGetReporteHistorico() {
        return new GetReporteHistorico();
    }

    /**
     * Create an instance of {@link ReporteCrediticio.DatosPrincipales }
     * 
     */
    public ReporteCrediticio.DatosPrincipales createReporteCrediticioDatosPrincipales() {
        return new ReporteCrediticio.DatosPrincipales();
    }

    /**
     * Create an instance of {@link QueryDataType }
     * 
     */
    public QueryDataType createQueryDataType() {
        return new QueryDataType();
    }

    /**
     * Create an instance of {@link Reporte.DatosPrincipales }
     * 
     */
    public Reporte.DatosPrincipales createReporteDatosPrincipales() {
        return new Reporte.DatosPrincipales();
    }

    /**
     * Create an instance of {@link HistoricType }
     * 
     */
    public HistoricType createHistoricType() {
        return new HistoricType();
    }

    /**
     * Create an instance of {@link Reporte.Modulos.Modulo.Data }
     * 
     */
    public Reporte.Modulos.Modulo.Data createReporteModulosModuloData() {
        return new Reporte.Modulos.Modulo.Data();
    }

    /**
     * Create an instance of {@link ReporteCrediticio.Modulos.Modulo.Data }
     * 
     */
    public ReporteCrediticio.Modulos.Modulo.Data createReporteCrediticioModulosModuloData() {
        return new ReporteCrediticio.Modulos.Modulo.Data();
    }

    /**
     * Create an instance of {@link IndicadorDeConsulta.Entidades.Entidad.Periodos.Periodo }
     * 
     */
    public IndicadorDeConsulta.Entidades.Entidad.Periodos.Periodo createIndicadorDeConsultaEntidadesEntidadPeriodosPeriodo() {
        return new IndicadorDeConsulta.Entidades.Entidad.Periodos.Periodo();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Microfinanzas.Periodos.Periodo.Calificaciones }
     * 
     */
    public SistemaFinanciero.Microfinanzas.Periodos.Periodo.Calificaciones createSistemaFinancieroMicrofinanzasPeriodosPeriodoCalificaciones() {
        return new SistemaFinanciero.Microfinanzas.Periodos.Periodo.Calificaciones();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Microfinanzas.Periodos.Periodo.Deudas.Deuda }
     * 
     */
    public SistemaFinanciero.Microfinanzas.Periodos.Periodo.Deudas.Deuda createSistemaFinancieroMicrofinanzasPeriodosPeriodoDeudasDeuda() {
        return new SistemaFinanciero.Microfinanzas.Periodos.Periodo.Deudas.Deuda();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Microfinanzas.CalificacionEntidad.Entidades.Entidad }
     * 
     */
    public SistemaFinanciero.Microfinanzas.CalificacionEntidad.Entidades.Entidad createSistemaFinancieroMicrofinanzasCalificacionEntidadEntidadesEntidad() {
        return new SistemaFinanciero.Microfinanzas.CalificacionEntidad.Entidades.Entidad();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Avalistas.Aval.Entidades.Entidad.Periodos.Periodo }
     * 
     */
    public SistemaFinanciero.Avalistas.Aval.Entidades.Entidad.Periodos.Periodo createSistemaFinancieroAvalistasAvalEntidadesEntidadPeriodosPeriodo() {
        return new SistemaFinanciero.Avalistas.Aval.Entidades.Entidad.Periodos.Periodo();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.Detalles.Detalle }
     * 
     */
    public SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.Detalles.Detalle createSistemaFinancieroRectificacionesRectificacionEntidadesEntidadDetallesDetalle() {
        return new SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.Detalles.Detalle();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.RCC.Periodos.Periodo.Calificaciones }
     * 
     */
    public SistemaFinanciero.RCC.Periodos.Periodo.Calificaciones createSistemaFinancieroRCCPeriodosPeriodoCalificaciones() {
        return new SistemaFinanciero.RCC.Periodos.Periodo.Calificaciones();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.RCC.Periodos.Periodo.Deudas.Deuda }
     * 
     */
    public SistemaFinanciero.RCC.Periodos.Periodo.Deudas.Deuda createSistemaFinancieroRCCPeriodosPeriodoDeudasDeuda() {
        return new SistemaFinanciero.RCC.Periodos.Periodo.Deudas.Deuda();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.RCC.DetalleEntidades.Entidad }
     * 
     */
    public SistemaFinanciero.RCC.DetalleEntidades.Entidad createSistemaFinancieroRCCDetalleEntidadesEntidad() {
        return new SistemaFinanciero.RCC.DetalleEntidades.Entidad();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.DeudasHistoricas.Deuda }
     * 
     */
    public SistemaFinanciero.DeudasHistoricas.Deuda createSistemaFinancieroDeudasHistoricasDeuda() {
        return new SistemaFinanciero.DeudasHistoricas.Deuda();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.DeudasUltimoPeriodo.Deuda.Productos.Producto }
     * 
     */
    public SistemaFinanciero.DeudasUltimoPeriodo.Deuda.Productos.Producto createSistemaFinancieroDeudasUltimoPeriodoDeudaProductosProducto() {
        return new SistemaFinanciero.DeudasUltimoPeriodo.Deuda.Productos.Producto();
    }

    /**
     * Create an instance of {@link SistemaFinanciero.ResumenComportamientoPago.Semaforo }
     * 
     */
    public SistemaFinanciero.ResumenComportamientoPago.Semaforo createSistemaFinancieroResumenComportamientoPagoSemaforo() {
        return new SistemaFinanciero.ResumenComportamientoPago.Semaforo();
    }

    /**
     * Create an instance of {@link CuotaVigente.DeudasPersonales.Entidades.Entidad.Deudas.Deuda }
     * 
     */
    public CuotaVigente.DeudasPersonales.Entidades.Entidad.Deudas.Deuda createCuotaVigenteDeudasPersonalesEntidadesEntidadDeudasDeuda() {
        return new CuotaVigente.DeudasPersonales.Entidades.Entidad.Deudas.Deuda();
    }

    /**
     * Create an instance of {@link CuotaVigente.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda }
     * 
     */
    public CuotaVigente.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda createCuotaVigenteDeudasNoSupervisadasEntidadesEntidadDeudasDeuda() {
        return new CuotaVigente.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda();
    }

    /**
     * Create an instance of {@link CuotaVigente.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda }
     * 
     */
    public CuotaVigente.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda createCuotaVigenteDeudasSupervisadasEntidadesEntidadDeudasDeuda() {
        return new CuotaVigente.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda();
    }

    /**
     * Create an instance of {@link CarteraMorosa.DetalleProtestos.Protesto }
     * 
     */
    public CarteraMorosa.DetalleProtestos.Protesto createCarteraMorosaDetalleProtestosProtesto() {
        return new CarteraMorosa.DetalleProtestos.Protesto();
    }

    /**
     * Create an instance of {@link CarteraMorosa.ResumenProtestos.Periodo }
     * 
     */
    public CarteraMorosa.ResumenProtestos.Periodo createCarteraMorosaResumenProtestosPeriodo() {
        return new CarteraMorosa.ResumenProtestos.Periodo();
    }

    /**
     * Create an instance of {@link DeudasImpagas.DeudasAFP.PeriodoAFP.TipoDeudas.TipoDeuda }
     * 
     */
    public DeudasImpagas.DeudasAFP.PeriodoAFP.TipoDeudas.TipoDeuda createDeudasImpagasDeudasAFPPeriodoAFPTipoDeudasTipoDeuda() {
        return new DeudasImpagas.DeudasAFP.PeriodoAFP.TipoDeudas.TipoDeuda();
    }

    /**
     * Create an instance of {@link DeudasImpagas.TarjetasAnuladas.Cabecera }
     * 
     */
    public DeudasImpagas.TarjetasAnuladas.Cabecera createDeudasImpagasTarjetasAnuladasCabecera() {
        return new DeudasImpagas.TarjetasAnuladas.Cabecera();
    }

    /**
     * Create an instance of {@link DeudasImpagas.TarjetasAnuladas.Detalle.InformacionNegativa }
     * 
     */
    public DeudasImpagas.TarjetasAnuladas.Detalle.InformacionNegativa createDeudasImpagasTarjetasAnuladasDetalleInformacionNegativa() {
        return new DeudasImpagas.TarjetasAnuladas.Detalle.InformacionNegativa();
    }

    /**
     * Create an instance of {@link DeudasImpagas.CuentasCerradas.Cabecera }
     * 
     */
    public DeudasImpagas.CuentasCerradas.Cabecera createDeudasImpagasCuentasCerradasCabecera() {
        return new DeudasImpagas.CuentasCerradas.Cabecera();
    }

    /**
     * Create an instance of {@link DeudasImpagas.CuentasCerradas.Detalle.InformacionNegativa }
     * 
     */
    public DeudasImpagas.CuentasCerradas.Detalle.InformacionNegativa createDeudasImpagasCuentasCerradasDetalleInformacionNegativa() {
        return new DeudasImpagas.CuentasCerradas.Detalle.InformacionNegativa();
    }

    /**
     * Create an instance of {@link DeudasImpagas.Protestos.ProtestosNoAclarados.Cabecera }
     * 
     */
    public DeudasImpagas.Protestos.ProtestosNoAclarados.Cabecera createDeudasImpagasProtestosProtestosNoAclaradosCabecera() {
        return new DeudasImpagas.Protestos.ProtestosNoAclarados.Cabecera();
    }

    /**
     * Create an instance of {@link DeudasImpagas.Protestos.ProtestosNoAclarados.Detalle.Deuda }
     * 
     */
    public DeudasImpagas.Protestos.ProtestosNoAclarados.Detalle.Deuda createDeudasImpagasProtestosProtestosNoAclaradosDetalleDeuda() {
        return new DeudasImpagas.Protestos.ProtestosNoAclarados.Detalle.Deuda();
    }

    /**
     * Create an instance of {@link DeudasImpagas.Protestos.ProtestosAclarados.Cabecera }
     * 
     */
    public DeudasImpagas.Protestos.ProtestosAclarados.Cabecera createDeudasImpagasProtestosProtestosAclaradosCabecera() {
        return new DeudasImpagas.Protestos.ProtestosAclarados.Cabecera();
    }

    /**
     * Create an instance of {@link DeudasImpagas.Protestos.ProtestosAclarados.Detalle.Deuda }
     * 
     */
    public DeudasImpagas.Protestos.ProtestosAclarados.Detalle.Deuda createDeudasImpagasProtestosProtestosAclaradosDetalleDeuda() {
        return new DeudasImpagas.Protestos.ProtestosAclarados.Detalle.Deuda();
    }

    /**
     * Create an instance of {@link DeudasImpagas.Omisos.Cabecera }
     * 
     */
    public DeudasImpagas.Omisos.Cabecera createDeudasImpagasOmisosCabecera() {
        return new DeudasImpagas.Omisos.Cabecera();
    }

    /**
     * Create an instance of {@link DeudasImpagas.Omisos.Detalle.Omision }
     * 
     */
    public DeudasImpagas.Omisos.Detalle.Omision createDeudasImpagasOmisosDetalleOmision() {
        return new DeudasImpagas.Omisos.Detalle.Omision();
    }

    /**
     * Create an instance of {@link DeudasImpagas.NegativoSunat.Cabecera }
     * 
     */
    public DeudasImpagas.NegativoSunat.Cabecera createDeudasImpagasNegativoSunatCabecera() {
        return new DeudasImpagas.NegativoSunat.Cabecera();
    }

    /**
     * Create an instance of {@link DeudasImpagas.NegativoSunat.Detalle.Deuda }
     * 
     */
    public DeudasImpagas.NegativoSunat.Detalle.Deuda createDeudasImpagasNegativoSunatDetalleDeuda() {
        return new DeudasImpagas.NegativoSunat.Detalle.Deuda();
    }

    /**
     * Create an instance of {@link DeudasImpagas.Sicom.Cabecera }
     * 
     */
    public DeudasImpagas.Sicom.Cabecera createDeudasImpagasSicomCabecera() {
        return new DeudasImpagas.Sicom.Cabecera();
    }

    /**
     * Create an instance of {@link DeudasImpagas.Sicom.Detalle.Deuda }
     * 
     */
    public DeudasImpagas.Sicom.Detalle.Deuda createDeudasImpagasSicomDetalleDeuda() {
        return new DeudasImpagas.Sicom.Detalle.Deuda();
    }

    /**
     * Create an instance of {@link DeudasImpagas.ResumenDeudasImpagas.SemaforoPeriodo.DetalleProductos.ProductoDeuda }
     * 
     */
    public DeudasImpagas.ResumenDeudasImpagas.SemaforoPeriodo.DetalleProductos.ProductoDeuda createDeudasImpagasResumenDeudasImpagasSemaforoPeriodoDetalleProductosProductoDeuda() {
        return new DeudasImpagas.ResumenDeudasImpagas.SemaforoPeriodo.DetalleProductos.ProductoDeuda();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.CuotaMensual.Monto }
     * 
     */
    public CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.CuotaMensual.Monto createCuotaHistoricaDeudasPersonalesEntidadesEntidadDeudasDeudaCuotaMensualMonto() {
        return new CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.CuotaMensual.Monto();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.SaldoPromedio.Monto }
     * 
     */
    public CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.SaldoPromedio.Monto createCuotaHistoricaDeudasPersonalesEntidadesEntidadDeudasDeudaSaldoPromedioMonto() {
        return new CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.SaldoPromedio.Monto();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.SaldoUltimoMes.Monto }
     * 
     */
    public CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.SaldoUltimoMes.Monto createCuotaHistoricaDeudasPersonalesEntidadesEntidadDeudasDeudaSaldoUltimoMesMonto() {
        return new CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.SaldoUltimoMes.Monto();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.LineaTotal.Monto }
     * 
     */
    public CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.LineaTotal.Monto createCuotaHistoricaDeudasPersonalesEntidadesEntidadDeudasDeudaLineaTotalMonto() {
        return new CuotaHistorica.DeudasPersonales.Entidades.Entidad.Deudas.Deuda.LineaTotal.Monto();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasPersonales.EntidadesReportadas.NroReportados }
     * 
     */
    public CuotaHistorica.DeudasPersonales.EntidadesReportadas.NroReportados createCuotaHistoricaDeudasPersonalesEntidadesReportadasNroReportados() {
        return new CuotaHistorica.DeudasPersonales.EntidadesReportadas.NroReportados();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.CuotaMensual.Monto }
     * 
     */
    public CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.CuotaMensual.Monto createCuotaHistoricaDeudasNoSupervisadasEntidadesEntidadDeudasDeudaCuotaMensualMonto() {
        return new CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.CuotaMensual.Monto();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoPromedio.Monto }
     * 
     */
    public CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoPromedio.Monto createCuotaHistoricaDeudasNoSupervisadasEntidadesEntidadDeudasDeudaSaldoPromedioMonto() {
        return new CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoPromedio.Monto();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoUltimoMes.Monto }
     * 
     */
    public CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoUltimoMes.Monto createCuotaHistoricaDeudasNoSupervisadasEntidadesEntidadDeudasDeudaSaldoUltimoMesMonto() {
        return new CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoUltimoMes.Monto();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.LineaTotal.Monto }
     * 
     */
    public CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.LineaTotal.Monto createCuotaHistoricaDeudasNoSupervisadasEntidadesEntidadDeudasDeudaLineaTotalMonto() {
        return new CuotaHistorica.DeudasNoSupervisadas.Entidades.Entidad.Deudas.Deuda.LineaTotal.Monto();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasNoSupervisadas.EntidadesReportadas.NroReportados }
     * 
     */
    public CuotaHistorica.DeudasNoSupervisadas.EntidadesReportadas.NroReportados createCuotaHistoricaDeudasNoSupervisadasEntidadesReportadasNroReportados() {
        return new CuotaHistorica.DeudasNoSupervisadas.EntidadesReportadas.NroReportados();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.CuotaMensual.Monto }
     * 
     */
    public CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.CuotaMensual.Monto createCuotaHistoricaDeudasSupervisadasEntidadesEntidadDeudasDeudaCuotaMensualMonto() {
        return new CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.CuotaMensual.Monto();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoPromedio.Monto }
     * 
     */
    public CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoPromedio.Monto createCuotaHistoricaDeudasSupervisadasEntidadesEntidadDeudasDeudaSaldoPromedioMonto() {
        return new CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoPromedio.Monto();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoUltimoMes.Monto }
     * 
     */
    public CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoUltimoMes.Monto createCuotaHistoricaDeudasSupervisadasEntidadesEntidadDeudasDeudaSaldoUltimoMesMonto() {
        return new CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.SaldoUltimoMes.Monto();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.LineaTotal.Monto }
     * 
     */
    public CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.LineaTotal.Monto createCuotaHistoricaDeudasSupervisadasEntidadesEntidadDeudasDeudaLineaTotalMonto() {
        return new CuotaHistorica.DeudasSupervisadas.Entidades.Entidad.Deudas.Deuda.LineaTotal.Monto();
    }

    /**
     * Create an instance of {@link CuotaHistorica.DeudasSupervisadas.EntidadesReportadas.NroReportados }
     * 
     */
    public CuotaHistorica.DeudasSupervisadas.EntidadesReportadas.NroReportados createCuotaHistoricaDeudasSupervisadasEntidadesReportadasNroReportados() {
        return new CuotaHistorica.DeudasSupervisadas.EntidadesReportadas.NroReportados();
    }

    /**
     * Create an instance of {@link RepresentantesLegales.RepresentandosPor.Representante }
     * 
     */
    public RepresentantesLegales.RepresentandosPor.Representante createRepresentantesLegalesRepresentandosPorRepresentante() {
        return new RepresentantesLegales.RepresentandosPor.Representante();
    }

    /**
     * Create an instance of {@link RepresentantesLegales.RepresentantesDe.Representante }
     * 
     */
    public RepresentantesLegales.RepresentantesDe.Representante createRepresentantesLegalesRepresentantesDeRepresentante() {
        return new RepresentantesLegales.RepresentantesDe.Representante();
    }

    /**
     * Create an instance of {@link BuenosContribuyentes.Contribuyente.Conceptos }
     * 
     */
    public BuenosContribuyentes.Contribuyente.Conceptos createBuenosContribuyentesContribuyenteConceptos() {
        return new BuenosContribuyentes.Contribuyente.Conceptos();
    }

    /**
     * Create an instance of {@link DirectorioSUNAT.Directorio.Direcciones }
     * 
     */
    public DirectorioSUNAT.Directorio.Direcciones createDirectorioSUNATDirectorioDirecciones() {
        return new DirectorioSUNAT.Directorio.Direcciones();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorPais.Paises.Pais.Exportaciones.Exportacion }
     * 
     */
    public ComercioExterior.ComercioExteriorPais.Paises.Pais.Exportaciones.Exportacion createComercioExteriorComercioExteriorPaisPaisesPaisExportacionesExportacion() {
        return new ComercioExterior.ComercioExteriorPais.Paises.Pais.Exportaciones.Exportacion();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorPais.Paises.Pais.Importaciones.Importacion }
     * 
     */
    public ComercioExterior.ComercioExteriorPais.Paises.Pais.Importaciones.Importacion createComercioExteriorComercioExteriorPaisPaisesPaisImportacionesImportacion() {
        return new ComercioExterior.ComercioExteriorPais.Paises.Pais.Importaciones.Importacion();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Exportaciones.Exportacion }
     * 
     */
    public ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Exportaciones.Exportacion createComercioExteriorComercioExteriorMensualPeriodosPeriodoExportacionesExportacion() {
        return new ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Exportaciones.Exportacion();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Importaciones.Importacion }
     * 
     */
    public ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Importaciones.Importacion createComercioExteriorComercioExteriorMensualPeriodosPeriodoImportacionesImportacion() {
        return new ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Importaciones.Importacion();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Exportaciones.Exportacion }
     * 
     */
    public ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Exportaciones.Exportacion createComercioExteriorComercioExteriorAnualPeriodosPeriodoExportacionesExportacion() {
        return new ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Exportaciones.Exportacion();
    }

    /**
     * Create an instance of {@link ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Importaciones.Importacion }
     * 
     */
    public ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Importaciones.Importacion createComercioExteriorComercioExteriorAnualPeriodosPeriodoImportacionesImportacion() {
        return new ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Importaciones.Importacion();
    }

    /**
     * Create an instance of {@link BoletinOficial.EscisionPatrimonio.Escision }
     * 
     */
    public BoletinOficial.EscisionPatrimonio.Escision createBoletinOficialEscisionPatrimonioEscision() {
        return new BoletinOficial.EscisionPatrimonio.Escision();
    }

    /**
     * Create an instance of {@link BoletinOficial.ExtincionPatrimonioFamiliar.PatrimonioFamiliar.Bienes.Bien }
     * 
     */
    public BoletinOficial.ExtincionPatrimonioFamiliar.PatrimonioFamiliar.Bienes.Bien createBoletinOficialExtincionPatrimonioFamiliarPatrimonioFamiliarBienesBien() {
        return new BoletinOficial.ExtincionPatrimonioFamiliar.PatrimonioFamiliar.Bienes.Bien();
    }

    /**
     * Create an instance of {@link BoletinOficial.ExtincionPatrimonioFamiliar.PatrimonioFamiliar.Personas.Persona }
     * 
     */
    public BoletinOficial.ExtincionPatrimonioFamiliar.PatrimonioFamiliar.Personas.Persona createBoletinOficialExtincionPatrimonioFamiliarPatrimonioFamiliarPersonasPersona() {
        return new BoletinOficial.ExtincionPatrimonioFamiliar.PatrimonioFamiliar.Personas.Persona();
    }

    /**
     * Create an instance of {@link BoletinOficial.ConstitucionPatrimonioFamiliar.PatrimonioFamiliar.Bienes.Bien }
     * 
     */
    public BoletinOficial.ConstitucionPatrimonioFamiliar.PatrimonioFamiliar.Bienes.Bien createBoletinOficialConstitucionPatrimonioFamiliarPatrimonioFamiliarBienesBien() {
        return new BoletinOficial.ConstitucionPatrimonioFamiliar.PatrimonioFamiliar.Bienes.Bien();
    }

    /**
     * Create an instance of {@link BoletinOficial.ConstitucionPatrimonioFamiliar.PatrimonioFamiliar.Personas.Persona }
     * 
     */
    public BoletinOficial.ConstitucionPatrimonioFamiliar.PatrimonioFamiliar.Personas.Persona createBoletinOficialConstitucionPatrimonioFamiliarPatrimonioFamiliarPersonasPersona() {
        return new BoletinOficial.ConstitucionPatrimonioFamiliar.PatrimonioFamiliar.Personas.Persona();
    }

    /**
     * Create an instance of {@link BoletinOficial.AvisosQuiebra.Quiebra.Liquidadores.Liquidador }
     * 
     */
    public BoletinOficial.AvisosQuiebra.Quiebra.Liquidadores.Liquidador createBoletinOficialAvisosQuiebraQuiebraLiquidadoresLiquidador() {
        return new BoletinOficial.AvisosQuiebra.Quiebra.Liquidadores.Liquidador();
    }

    /**
     * Create an instance of {@link BoletinOficial.ObligacionDarDinero.Obligacion.Deudores.Deudor }
     * 
     */
    public BoletinOficial.ObligacionDarDinero.Obligacion.Deudores.Deudor createBoletinOficialObligacionDarDineroObligacionDeudoresDeudor() {
        return new BoletinOficial.ObligacionDarDinero.Obligacion.Deudores.Deudor();
    }

    /**
     * Create an instance of {@link BoletinOficial.ObligacionDarDinero.Obligacion.Acreedores.Acreedor }
     * 
     */
    public BoletinOficial.ObligacionDarDinero.Obligacion.Acreedores.Acreedor createBoletinOficialObligacionDarDineroObligacionAcreedoresAcreedor() {
        return new BoletinOficial.ObligacionDarDinero.Obligacion.Acreedores.Acreedor();
    }

    /**
     * Create an instance of {@link BoletinOficial.CambiosCapital.Cambio }
     * 
     */
    public BoletinOficial.CambiosCapital.Cambio createBoletinOficialCambiosCapitalCambio() {
        return new BoletinOficial.CambiosCapital.Cambio();
    }

    /**
     * Create an instance of {@link BoletinOficial.Disoluciones.Disolucion.Liquidadores.Liquidador }
     * 
     */
    public BoletinOficial.Disoluciones.Disolucion.Liquidadores.Liquidador createBoletinOficialDisolucionesDisolucionLiquidadoresLiquidador() {
        return new BoletinOficial.Disoluciones.Disolucion.Liquidadores.Liquidador();
    }

    /**
     * Create an instance of {@link BoletinOficial.FusionSociedades.Fusion.Empresas.Empresa }
     * 
     */
    public BoletinOficial.FusionSociedades.Fusion.Empresas.Empresa createBoletinOficialFusionSociedadesFusionEmpresasEmpresa() {
        return new BoletinOficial.FusionSociedades.Fusion.Empresas.Empresa();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesWarrants.RemateWarrant.Mercaderias.Mercaderia }
     * 
     */
    public BoletinOficial.RematesWarrants.RemateWarrant.Mercaderias.Mercaderia createBoletinOficialRematesWarrantsRemateWarrantMercaderiasMercaderia() {
        return new BoletinOficial.RematesWarrants.RemateWarrant.Mercaderias.Mercaderia();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesWarrants.RemateWarrant.Inmuebles.Inmueble }
     * 
     */
    public BoletinOficial.RematesWarrants.RemateWarrant.Inmuebles.Inmueble createBoletinOficialRematesWarrantsRemateWarrantInmueblesInmueble() {
        return new BoletinOficial.RematesWarrants.RemateWarrant.Inmuebles.Inmueble();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesWarrants.RemateWarrant.Muebles.Mueble }
     * 
     */
    public BoletinOficial.RematesWarrants.RemateWarrant.Muebles.Mueble createBoletinOficialRematesWarrantsRemateWarrantMueblesMueble() {
        return new BoletinOficial.RematesWarrants.RemateWarrant.Muebles.Mueble();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesWarrants.RemateWarrant.Demandados.Demandado }
     * 
     */
    public BoletinOficial.RematesWarrants.RemateWarrant.Demandados.Demandado createBoletinOficialRematesWarrantsRemateWarrantDemandadosDemandado() {
        return new BoletinOficial.RematesWarrants.RemateWarrant.Demandados.Demandado();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesWarrants.RemateWarrant.Demandantes.Demandante }
     * 
     */
    public BoletinOficial.RematesWarrants.RemateWarrant.Demandantes.Demandante createBoletinOficialRematesWarrantsRemateWarrantDemandantesDemandante() {
        return new BoletinOficial.RematesWarrants.RemateWarrant.Demandantes.Demandante();
    }

    /**
     * Create an instance of {@link BoletinOficial.ConvocatoriaAccredores.Convocatoria }
     * 
     */
    public BoletinOficial.ConvocatoriaAccredores.Convocatoria createBoletinOficialConvocatoriaAccredoresConvocatoria() {
        return new BoletinOficial.ConvocatoriaAccredores.Convocatoria();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesInmuebles.RemateInmueble.Mercaderias.Mercaderia }
     * 
     */
    public BoletinOficial.RematesBienesInmuebles.RemateInmueble.Mercaderias.Mercaderia createBoletinOficialRematesBienesInmueblesRemateInmuebleMercaderiasMercaderia() {
        return new BoletinOficial.RematesBienesInmuebles.RemateInmueble.Mercaderias.Mercaderia();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesInmuebles.RemateInmueble.Inmuebles.Inmueble }
     * 
     */
    public BoletinOficial.RematesBienesInmuebles.RemateInmueble.Inmuebles.Inmueble createBoletinOficialRematesBienesInmueblesRemateInmuebleInmueblesInmueble() {
        return new BoletinOficial.RematesBienesInmuebles.RemateInmueble.Inmuebles.Inmueble();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesInmuebles.RemateInmueble.Muebles.Mueble }
     * 
     */
    public BoletinOficial.RematesBienesInmuebles.RemateInmueble.Muebles.Mueble createBoletinOficialRematesBienesInmueblesRemateInmuebleMueblesMueble() {
        return new BoletinOficial.RematesBienesInmuebles.RemateInmueble.Muebles.Mueble();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesInmuebles.RemateInmueble.Demandados.Demandado }
     * 
     */
    public BoletinOficial.RematesBienesInmuebles.RemateInmueble.Demandados.Demandado createBoletinOficialRematesBienesInmueblesRemateInmuebleDemandadosDemandado() {
        return new BoletinOficial.RematesBienesInmuebles.RemateInmueble.Demandados.Demandado();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesInmuebles.RemateInmueble.Demandantes.Demandante }
     * 
     */
    public BoletinOficial.RematesBienesInmuebles.RemateInmueble.Demandantes.Demandante createBoletinOficialRematesBienesInmueblesRemateInmuebleDemandantesDemandante() {
        return new BoletinOficial.RematesBienesInmuebles.RemateInmueble.Demandantes.Demandante();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesMuebles.RemateMueble.Mercaderias.Mercaderia }
     * 
     */
    public BoletinOficial.RematesBienesMuebles.RemateMueble.Mercaderias.Mercaderia createBoletinOficialRematesBienesMueblesRemateMuebleMercaderiasMercaderia() {
        return new BoletinOficial.RematesBienesMuebles.RemateMueble.Mercaderias.Mercaderia();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesMuebles.RemateMueble.Inmuebles.Inmueble }
     * 
     */
    public BoletinOficial.RematesBienesMuebles.RemateMueble.Inmuebles.Inmueble createBoletinOficialRematesBienesMueblesRemateMuebleInmueblesInmueble() {
        return new BoletinOficial.RematesBienesMuebles.RemateMueble.Inmuebles.Inmueble();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesMuebles.RemateMueble.Muebles.Mueble }
     * 
     */
    public BoletinOficial.RematesBienesMuebles.RemateMueble.Muebles.Mueble createBoletinOficialRematesBienesMueblesRemateMuebleMueblesMueble() {
        return new BoletinOficial.RematesBienesMuebles.RemateMueble.Muebles.Mueble();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesMuebles.RemateMueble.Demandados.Demandado }
     * 
     */
    public BoletinOficial.RematesBienesMuebles.RemateMueble.Demandados.Demandado createBoletinOficialRematesBienesMueblesRemateMuebleDemandadosDemandado() {
        return new BoletinOficial.RematesBienesMuebles.RemateMueble.Demandados.Demandado();
    }

    /**
     * Create an instance of {@link BoletinOficial.RematesBienesMuebles.RemateMueble.Demandantes.Demandante }
     * 
     */
    public BoletinOficial.RematesBienesMuebles.RemateMueble.Demandantes.Demandante createBoletinOficialRematesBienesMueblesRemateMuebleDemandantesDemandante() {
        return new BoletinOficial.RematesBienesMuebles.RemateMueble.Demandantes.Demandante();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.creditreport.equifax.com.pe/endpoint", name = "Cuentas")
    public JAXBElement<Object> createCuentas(Object value) {
        return new JAXBElement<Object>(_Cuentas_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EquifaxInterfaceException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.creditreport.equifax.com.pe/endpoint", name = "EquifaxInterfaceException")
    public JAXBElement<EquifaxInterfaceException> createEquifaxInterfaceException(EquifaxInterfaceException value) {
        return new JAXBElement<EquifaxInterfaceException>(_EquifaxInterfaceException_QNAME, EquifaxInterfaceException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryDataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.creditreport.equifax.com.pe/document", name = "DatosConsulta")
    public JAXBElement<QueryDataType> createDatosConsulta(QueryDataType value) {
        return new JAXBElement<QueryDataType>(_DatosConsulta_QNAME, QueryDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetReporteOnlineResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.creditreport.equifax.com.pe/endpoint", name = "GetReporteOnlineResponse")
    public JAXBElement<GetReporteOnlineResponse> createGetReporteOnlineResponse(GetReporteOnlineResponse value) {
        return new JAXBElement<GetReporteOnlineResponse>(_GetReporteOnlineResponse_QNAME, GetReporteOnlineResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetReporteHistoricoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.creditreport.equifax.com.pe/endpoint", name = "GetReporteHistoricoResponse")
    public JAXBElement<GetReporteHistoricoResponse> createGetReporteHistoricoResponse(GetReporteHistoricoResponse value) {
        return new JAXBElement<GetReporteHistoricoResponse>(_GetReporteHistoricoResponse_QNAME, GetReporteHistoricoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HistoricType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.creditreport.equifax.com.pe/document", name = "ConsultaHistorica")
    public JAXBElement<HistoricType> createConsultaHistorica(HistoricType value) {
        return new JAXBElement<HistoricType>(_ConsultaHistorica_QNAME, HistoricType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.creditreport.equifax.com.pe/endpoint", name = "Cuenta")
    public JAXBElement<Object> createCuenta(Object value) {
        return new JAXBElement<Object>(_Cuenta_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetReporteOnline }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.creditreport.equifax.com.pe/endpoint", name = "GetReporteOnline")
    public JAXBElement<GetReporteOnline> createGetReporteOnline(GetReporteOnline value) {
        return new JAXBElement<GetReporteOnline>(_GetReporteOnline_QNAME, GetReporteOnline.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetReporteHistorico }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.creditreport.equifax.com.pe/endpoint", name = "GetReporteHistorico")
    public JAXBElement<GetReporteHistorico> createGetReporteHistorico(GetReporteHistorico value) {
        return new JAXBElement<GetReporteHistorico>(_GetReporteHistorico_QNAME, GetReporteHistorico.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CarteraMorosa.DetalleProtestos }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DetalleProtestos", scope = CarteraMorosa.class)
    public JAXBElement<CarteraMorosa.DetalleProtestos> createCarteraMorosaDetalleProtestos(CarteraMorosa.DetalleProtestos value) {
        return new JAXBElement<CarteraMorosa.DetalleProtestos>(_CarteraMorosaDetalleProtestos_QNAME, CarteraMorosa.DetalleProtestos.class, CarteraMorosa.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CarteraMorosa.ResumenProtestos }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ResumenProtestos", scope = CarteraMorosa.class)
    public JAXBElement<CarteraMorosa.ResumenProtestos> createCarteraMorosaResumenProtestos(CarteraMorosa.ResumenProtestos value) {
        return new JAXBElement<CarteraMorosa.ResumenProtestos>(_CarteraMorosaResumenProtestos_QNAME, CarteraMorosa.ResumenProtestos.class, CarteraMorosa.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "NumeroOperacion", scope = ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Exportaciones.Exportacion.class)
    public JAXBElement<String> createComercioExteriorComercioExteriorAnualPeriodosPeriodoExportacionesExportacionNumeroOperacion(String value) {
        return new JAXBElement<String>(_ComercioExteriorComercioExteriorAnualPeriodosPeriodoExportacionesExportacionNumeroOperacion_QNAME, String.class, ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Exportaciones.Exportacion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "TotalFOB", scope = ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Exportaciones.Exportacion.class)
    public JAXBElement<BigDecimal> createComercioExteriorComercioExteriorAnualPeriodosPeriodoExportacionesExportacionTotalFOB(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_ComercioExteriorComercioExteriorAnualPeriodosPeriodoExportacionesExportacionTotalFOB_QNAME, BigDecimal.class, ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Exportaciones.Exportacion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "PaisDestino", scope = ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Exportaciones.Exportacion.class)
    public JAXBElement<String> createComercioExteriorComercioExteriorAnualPeriodosPeriodoExportacionesExportacionPaisDestino(String value) {
        return new JAXBElement<String>(_ComercioExteriorComercioExteriorAnualPeriodosPeriodoExportacionesExportacionPaisDestino_QNAME, String.class, ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Exportaciones.Exportacion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SistemaFinanciero.RCC.Periodos }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Periodos", scope = SistemaFinanciero.RCC.class)
    public JAXBElement<SistemaFinanciero.RCC.Periodos> createSistemaFinancieroRCCPeriodos(SistemaFinanciero.RCC.Periodos value) {
        return new JAXBElement<SistemaFinanciero.RCC.Periodos>(_SistemaFinancieroRCCPeriodos_QNAME, SistemaFinanciero.RCC.Periodos.class, SistemaFinanciero.RCC.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SistemaFinanciero.RCC.DetalleEntidades }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DetalleEntidades", scope = SistemaFinanciero.RCC.class)
    public JAXBElement<SistemaFinanciero.RCC.DetalleEntidades> createSistemaFinancieroRCCDetalleEntidades(SistemaFinanciero.RCC.DetalleEntidades value) {
        return new JAXBElement<SistemaFinanciero.RCC.DetalleEntidades>(_SistemaFinancieroRCCDetalleEntidades_QNAME, SistemaFinanciero.RCC.DetalleEntidades.class, SistemaFinanciero.RCC.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SistemaFinanciero.Rectificaciones.Rectificacion.Entidades }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Entidades", scope = SistemaFinanciero.Rectificaciones.Rectificacion.class)
    public JAXBElement<SistemaFinanciero.Rectificaciones.Rectificacion.Entidades> createSistemaFinancieroRectificacionesRectificacionEntidades(SistemaFinanciero.Rectificaciones.Rectificacion.Entidades value) {
        return new JAXBElement<SistemaFinanciero.Rectificaciones.Rectificacion.Entidades>(_SistemaFinancieroRectificacionesRectificacionEntidades_QNAME, SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.class, SistemaFinanciero.Rectificaciones.Rectificacion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Monto", scope = SistemaFinanciero.Microfinanzas.Periodos.Periodo.Deudas.Deuda.class)
    public JAXBElement<BigDecimal> createSistemaFinancieroMicrofinanzasPeriodosPeriodoDeudasDeudaMonto(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_SistemaFinancieroMicrofinanzasPeriodosPeriodoDeudasDeudaMonto_QNAME, BigDecimal.class, SistemaFinanciero.Microfinanzas.Periodos.Periodo.Deudas.Deuda.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "NombreEntidad", scope = SistemaFinanciero.Microfinanzas.Periodos.Periodo.Deudas.Deuda.class)
    public JAXBElement<String> createSistemaFinancieroMicrofinanzasPeriodosPeriodoDeudasDeudaNombreEntidad(String value) {
        return new JAXBElement<String>(_SistemaFinancieroMicrofinanzasPeriodosPeriodoDeudasDeudaNombreEntidad_QNAME, String.class, SistemaFinanciero.Microfinanzas.Periodos.Periodo.Deudas.Deuda.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CodigoEntidad", scope = SistemaFinanciero.Microfinanzas.Periodos.Periodo.Deudas.Deuda.class)
    public JAXBElement<String> createSistemaFinancieroMicrofinanzasPeriodosPeriodoDeudasDeudaCodigoEntidad(String value) {
        return new JAXBElement<String>(_SistemaFinancieroMicrofinanzasPeriodosPeriodoDeudasDeudaCodigoEntidad_QNAME, String.class, SistemaFinanciero.Microfinanzas.Periodos.Periodo.Deudas.Deuda.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Cuenta", scope = SistemaFinanciero.Microfinanzas.Periodos.Periodo.Deudas.Deuda.class)
    public JAXBElement<String> createSistemaFinancieroMicrofinanzasPeriodosPeriodoDeudasDeudaCuenta(String value) {
        return new JAXBElement<String>(_SistemaFinancieroMicrofinanzasPeriodosPeriodoDeudasDeudaCuenta_QNAME, String.class, SistemaFinanciero.Microfinanzas.Periodos.Periodo.Deudas.Deuda.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CodigoCIIU", scope = DirectorioSUNAT.Directorio.class)
    public JAXBElement<String> createDirectorioSUNATDirectorioCodigoCIIU(String value) {
        return new JAXBElement<String>(_DirectorioSUNATDirectorioCodigoCIIU_QNAME, String.class, DirectorioSUNAT.Directorio.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Exportaciones }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Exportaciones", scope = ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.class)
    public JAXBElement<ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Exportaciones> createComercioExteriorComercioExteriorAnualPeriodosPeriodoExportaciones(ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Exportaciones value) {
        return new JAXBElement<ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Exportaciones>(_ComercioExteriorComercioExteriorAnualPeriodosPeriodoExportaciones_QNAME, ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Exportaciones.class, ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Importaciones }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Importaciones", scope = ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.class)
    public JAXBElement<ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Importaciones> createComercioExteriorComercioExteriorAnualPeriodosPeriodoImportaciones(ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Importaciones value) {
        return new JAXBElement<ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Importaciones>(_ComercioExteriorComercioExteriorAnualPeriodosPeriodoImportaciones_QNAME, ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Importaciones.class, ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "TipoDocumento", scope = CarteraMorosa.DetalleProtestos.Protesto.class)
    public JAXBElement<String> createCarteraMorosaDetalleProtestosProtestoTipoDocumento(String value) {
        return new JAXBElement<String>(_CarteraMorosaDetalleProtestosProtestoTipoDocumento_QNAME, String.class, CarteraMorosa.DetalleProtestos.Protesto.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Moneda", scope = CarteraMorosa.DetalleProtestos.Protesto.class)
    public JAXBElement<String> createCarteraMorosaDetalleProtestosProtestoMoneda(String value) {
        return new JAXBElement<String>(_CarteraMorosaDetalleProtestosProtestoMoneda_QNAME, String.class, CarteraMorosa.DetalleProtestos.Protesto.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Monto", scope = CarteraMorosa.DetalleProtestos.Protesto.class)
    public JAXBElement<BigDecimal> createCarteraMorosaDetalleProtestosProtestoMonto(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_SistemaFinancieroMicrofinanzasPeriodosPeriodoDeudasDeudaMonto_QNAME, BigDecimal.class, CarteraMorosa.DetalleProtestos.Protesto.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Emisor", scope = CarteraMorosa.DetalleProtestos.Protesto.class)
    public JAXBElement<String> createCarteraMorosaDetalleProtestosProtestoEmisor(String value) {
        return new JAXBElement<String>(_CarteraMorosaDetalleProtestosProtestoEmisor_QNAME, String.class, CarteraMorosa.DetalleProtestos.Protesto.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "FechaVencimiento", scope = CarteraMorosa.DetalleProtestos.Protesto.class)
    public JAXBElement<String> createCarteraMorosaDetalleProtestosProtestoFechaVencimiento(String value) {
        return new JAXBElement<String>(_CarteraMorosaDetalleProtestosProtestoFechaVencimiento_QNAME, String.class, CarteraMorosa.DetalleProtestos.Protesto.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "FechaAclaracion", scope = CarteraMorosa.DetalleProtestos.Protesto.class)
    public JAXBElement<String> createCarteraMorosaDetalleProtestosProtestoFechaAclaracion(String value) {
        return new JAXBElement<String>(_CarteraMorosaDetalleProtestosProtestoFechaAclaracion_QNAME, String.class, CarteraMorosa.DetalleProtestos.Protesto.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Notaria", scope = CarteraMorosa.DetalleProtestos.Protesto.class)
    public JAXBElement<String> createCarteraMorosaDetalleProtestosProtestoNotaria(String value) {
        return new JAXBElement<String>(_CarteraMorosaDetalleProtestosProtestoNotaria_QNAME, String.class, CarteraMorosa.DetalleProtestos.Protesto.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "NumeroBoletin", scope = CarteraMorosa.DetalleProtestos.Protesto.class)
    public JAXBElement<String> createCarteraMorosaDetalleProtestosProtestoNumeroBoletin(String value) {
        return new JAXBElement<String>(_CarteraMorosaDetalleProtestosProtestoNumeroBoletin_QNAME, String.class, CarteraMorosa.DetalleProtestos.Protesto.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "NumeroOperacion", scope = ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Exportaciones.Exportacion.class)
    public JAXBElement<String> createComercioExteriorComercioExteriorMensualPeriodosPeriodoExportacionesExportacionNumeroOperacion(String value) {
        return new JAXBElement<String>(_ComercioExteriorComercioExteriorAnualPeriodosPeriodoExportacionesExportacionNumeroOperacion_QNAME, String.class, ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Exportaciones.Exportacion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "TotalFOB", scope = ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Exportaciones.Exportacion.class)
    public JAXBElement<BigDecimal> createComercioExteriorComercioExteriorMensualPeriodosPeriodoExportacionesExportacionTotalFOB(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_ComercioExteriorComercioExteriorAnualPeriodosPeriodoExportacionesExportacionTotalFOB_QNAME, BigDecimal.class, ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Exportaciones.Exportacion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "PaisDestino", scope = ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Exportaciones.Exportacion.class)
    public JAXBElement<String> createComercioExteriorComercioExteriorMensualPeriodosPeriodoExportacionesExportacionPaisDestino(String value) {
        return new JAXBElement<String>(_ComercioExteriorComercioExteriorAnualPeriodosPeriodoExportacionesExportacionPaisDestino_QNAME, String.class, ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Exportaciones.Exportacion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Fecha", scope = com.affirm.equifax.ws.Direcciones.Direccion.class)
    public JAXBElement<String> createDireccionesDireccionFecha(String value) {
        return new JAXBElement<String>(_DireccionesDireccionFecha_QNAME, String.class, com.affirm.equifax.ws.Direcciones.Direccion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Telefono", scope = com.affirm.equifax.ws.Direcciones.Direccion.class)
    public JAXBElement<String> createDireccionesDireccionTelefono(String value) {
        return new JAXBElement<String>(_DireccionesDireccionTelefono_QNAME, String.class, com.affirm.equifax.ws.Direcciones.Direccion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Ubigeo", scope = com.affirm.equifax.ws.Direcciones.Direccion.class)
    public JAXBElement<String> createDireccionesDireccionUbigeo(String value) {
        return new JAXBElement<String>(_DireccionesDireccionUbigeo_QNAME, String.class, com.affirm.equifax.ws.Direcciones.Direccion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Anexo", scope = com.affirm.equifax.ws.Direcciones.Direccion.class)
    public JAXBElement<String> createDireccionesDireccionAnexo(String value) {
        return new JAXBElement<String>(_DireccionesDireccionAnexo_QNAME, String.class, com.affirm.equifax.ws.Direcciones.Direccion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Descripcion", scope = com.affirm.equifax.ws.Direcciones.Direccion.class)
    public JAXBElement<String> createDireccionesDireccionDescripcion(String value) {
        return new JAXBElement<String>(_DireccionesDireccionDescripcion_QNAME, String.class, com.affirm.equifax.ws.Direcciones.Direccion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Fuente", scope = com.affirm.equifax.ws.Direcciones.Direccion.class)
    public JAXBElement<String> createDireccionesDireccionFuente(String value) {
        return new JAXBElement<String>(_DireccionesDireccionFuente_QNAME, String.class, com.affirm.equifax.ws.Direcciones.Direccion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Exportaciones }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Exportaciones", scope = ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.class)
    public JAXBElement<ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Exportaciones> createComercioExteriorComercioExteriorMensualPeriodosPeriodoExportaciones(ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Exportaciones value) {
        return new JAXBElement<ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Exportaciones>(_ComercioExteriorComercioExteriorAnualPeriodosPeriodoExportaciones_QNAME, ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Exportaciones.class, ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Importaciones }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Importaciones", scope = ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.class)
    public JAXBElement<ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Importaciones> createComercioExteriorComercioExteriorMensualPeriodosPeriodoImportaciones(ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Importaciones value) {
        return new JAXBElement<ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Importaciones>(_ComercioExteriorComercioExteriorAnualPeriodosPeriodoImportaciones_QNAME, ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Importaciones.class, ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "NombreCuenta", scope = SistemaFinanciero.RCC.Periodos.Periodo.Deudas.Deuda.class)
    public JAXBElement<String> createSistemaFinancieroRCCPeriodosPeriodoDeudasDeudaNombreCuenta(String value) {
        return new JAXBElement<String>(_SistemaFinancieroRCCPeriodosPeriodoDeudasDeudaNombreCuenta_QNAME, String.class, SistemaFinanciero.RCC.Periodos.Periodo.Deudas.Deuda.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Monto", scope = SistemaFinanciero.RCC.Periodos.Periodo.Deudas.Deuda.class)
    public JAXBElement<BigDecimal> createSistemaFinancieroRCCPeriodosPeriodoDeudasDeudaMonto(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_SistemaFinancieroMicrofinanzasPeriodosPeriodoDeudasDeudaMonto_QNAME, BigDecimal.class, SistemaFinanciero.RCC.Periodos.Periodo.Deudas.Deuda.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DescripcionCuenta", scope = SistemaFinanciero.RCC.Periodos.Periodo.Deudas.Deuda.class)
    public JAXBElement<String> createSistemaFinancieroRCCPeriodosPeriodoDeudasDeudaDescripcionCuenta(String value) {
        return new JAXBElement<String>(_SistemaFinancieroRCCPeriodosPeriodoDeudasDeudaDescripcionCuenta_QNAME, String.class, SistemaFinanciero.RCC.Periodos.Periodo.Deudas.Deuda.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "NombreEntidad", scope = SistemaFinanciero.RCC.Periodos.Periodo.Deudas.Deuda.class)
    public JAXBElement<String> createSistemaFinancieroRCCPeriodosPeriodoDeudasDeudaNombreEntidad(String value) {
        return new JAXBElement<String>(_SistemaFinancieroMicrofinanzasPeriodosPeriodoDeudasDeudaNombreEntidad_QNAME, String.class, SistemaFinanciero.RCC.Periodos.Periodo.Deudas.Deuda.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CodigoEntidad", scope = SistemaFinanciero.RCC.Periodos.Periodo.Deudas.Deuda.class)
    public JAXBElement<String> createSistemaFinancieroRCCPeriodosPeriodoDeudasDeudaCodigoEntidad(String value) {
        return new JAXBElement<String>(_SistemaFinancieroMicrofinanzasPeriodosPeriodoDeudasDeudaCodigoEntidad_QNAME, String.class, SistemaFinanciero.RCC.Periodos.Periodo.Deudas.Deuda.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CodigoCuenta", scope = SistemaFinanciero.RCC.Periodos.Periodo.Deudas.Deuda.class)
    public JAXBElement<String> createSistemaFinancieroRCCPeriodosPeriodoDeudasDeudaCodigoCuenta(String value) {
        return new JAXBElement<String>(_SistemaFinancieroRCCPeriodosPeriodoDeudasDeudaCodigoCuenta_QNAME, String.class, SistemaFinanciero.RCC.Periodos.Periodo.Deudas.Deuda.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Calificacion", scope = SistemaFinanciero.RCC.Periodos.Periodo.Deudas.Deuda.class, defaultValue = "")
    public JAXBElement<String> createSistemaFinancieroRCCPeriodosPeriodoDeudasDeudaCalificacion(String value) {
        return new JAXBElement<String>(_SistemaFinancieroRCCPeriodosPeriodoDeudasDeudaCalificacion_QNAME, String.class, SistemaFinanciero.RCC.Periodos.Periodo.Deudas.Deuda.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComercioExterior.ComercioExteriorPais.Paises }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Paises", scope = ComercioExterior.ComercioExteriorPais.class)
    public JAXBElement<ComercioExterior.ComercioExteriorPais.Paises> createComercioExteriorComercioExteriorPaisPaises(ComercioExterior.ComercioExteriorPais.Paises value) {
        return new JAXBElement<ComercioExterior.ComercioExteriorPais.Paises>(_ComercioExteriorComercioExteriorPaisPaises_QNAME, ComercioExterior.ComercioExteriorPais.Paises.class, ComercioExterior.ComercioExteriorPais.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "NivelRiesgo", scope = ScoreEmpresas.class)
    public JAXBElement<String> createScoreEmpresasNivelRiesgo(String value) {
        return new JAXBElement<String>(_ScoreEmpresasNivelRiesgo_QNAME, String.class, ScoreEmpresas.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Puntaje", scope = ScoreEmpresas.class)
    public JAXBElement<String> createScoreEmpresasPuntaje(String value) {
        return new JAXBElement<String>(_ScoreEmpresasPuntaje_QNAME, String.class, ScoreEmpresas.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Conclusion", scope = ScoreEmpresas.class)
    public JAXBElement<String> createScoreEmpresasConclusion(String value) {
        return new JAXBElement<String>(_ScoreEmpresasConclusion_QNAME, String.class, ScoreEmpresas.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "NumeroOperacion", scope = ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Importaciones.Importacion.class)
    public JAXBElement<String> createComercioExteriorComercioExteriorMensualPeriodosPeriodoImportacionesImportacionNumeroOperacion(String value) {
        return new JAXBElement<String>(_ComercioExteriorComercioExteriorAnualPeriodosPeriodoExportacionesExportacionNumeroOperacion_QNAME, String.class, ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Importaciones.Importacion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "TotalFOB", scope = ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Importaciones.Importacion.class)
    public JAXBElement<String> createComercioExteriorComercioExteriorMensualPeriodosPeriodoImportacionesImportacionTotalFOB(String value) {
        return new JAXBElement<String>(_ComercioExteriorComercioExteriorAnualPeriodosPeriodoExportacionesExportacionTotalFOB_QNAME, String.class, ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Importaciones.Importacion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "PaisProcedencia", scope = ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Importaciones.Importacion.class)
    public JAXBElement<String> createComercioExteriorComercioExteriorMensualPeriodosPeriodoImportacionesImportacionPaisProcedencia(String value) {
        return new JAXBElement<String>(_ComercioExteriorComercioExteriorMensualPeriodosPeriodoImportacionesImportacionPaisProcedencia_QNAME, String.class, ComercioExterior.ComercioExteriorMensual.Periodos.Periodo.Importaciones.Importacion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Nombre", scope = SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.class)
    public JAXBElement<String> createSistemaFinancieroRectificacionesRectificacionEntidadesEntidadNombre(String value) {
        return new JAXBElement<String>(_SistemaFinancieroRectificacionesRectificacionEntidadesEntidadNombre_QNAME, String.class, SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Codigo", scope = SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.class)
    public JAXBElement<String> createSistemaFinancieroRectificacionesRectificacionEntidadesEntidadCodigo(String value) {
        return new JAXBElement<String>(_SistemaFinancieroRectificacionesRectificacionEntidadesEntidadCodigo_QNAME, String.class, SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.Detalles }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Detalles", scope = SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.class)
    public JAXBElement<SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.Detalles> createSistemaFinancieroRectificacionesRectificacionEntidadesEntidadDetalles(SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.Detalles value) {
        return new JAXBElement<SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.Detalles>(_SistemaFinancieroRectificacionesRectificacionEntidadesEntidadDetalles_QNAME, SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.Detalles.class, SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComercioExterior.ComercioExteriorPais.Paises.Pais.Exportaciones }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Exportaciones", scope = ComercioExterior.ComercioExteriorPais.Paises.Pais.class)
    public JAXBElement<ComercioExterior.ComercioExteriorPais.Paises.Pais.Exportaciones> createComercioExteriorComercioExteriorPaisPaisesPaisExportaciones(ComercioExterior.ComercioExteriorPais.Paises.Pais.Exportaciones value) {
        return new JAXBElement<ComercioExterior.ComercioExteriorPais.Paises.Pais.Exportaciones>(_ComercioExteriorComercioExteriorAnualPeriodosPeriodoExportaciones_QNAME, ComercioExterior.ComercioExteriorPais.Paises.Pais.Exportaciones.class, ComercioExterior.ComercioExteriorPais.Paises.Pais.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComercioExterior.ComercioExteriorPais.Paises.Pais.Importaciones }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Importaciones", scope = ComercioExterior.ComercioExteriorPais.Paises.Pais.class)
    public JAXBElement<ComercioExterior.ComercioExteriorPais.Paises.Pais.Importaciones> createComercioExteriorComercioExteriorPaisPaisesPaisImportaciones(ComercioExterior.ComercioExteriorPais.Paises.Pais.Importaciones value) {
        return new JAXBElement<ComercioExterior.ComercioExteriorPais.Paises.Pais.Importaciones>(_ComercioExteriorComercioExteriorAnualPeriodosPeriodoImportaciones_QNAME, ComercioExterior.ComercioExteriorPais.Paises.Pais.Importaciones.class, ComercioExterior.ComercioExteriorPais.Paises.Pais.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComercioExterior.ComercioExteriorAnual }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ComercioExteriorAnual", scope = ComercioExterior.class)
    public JAXBElement<ComercioExterior.ComercioExteriorAnual> createComercioExteriorComercioExteriorAnual(ComercioExterior.ComercioExteriorAnual value) {
        return new JAXBElement<ComercioExterior.ComercioExteriorAnual>(_ComercioExteriorComercioExteriorAnual_QNAME, ComercioExterior.ComercioExteriorAnual.class, ComercioExterior.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComercioExterior.ComercioExteriorMensual }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ComercioExteriorMensual", scope = ComercioExterior.class)
    public JAXBElement<ComercioExterior.ComercioExteriorMensual> createComercioExteriorComercioExteriorMensual(ComercioExterior.ComercioExteriorMensual value) {
        return new JAXBElement<ComercioExterior.ComercioExteriorMensual>(_ComercioExteriorComercioExteriorMensual_QNAME, ComercioExterior.ComercioExteriorMensual.class, ComercioExterior.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComercioExterior.ComercioExteriorPais }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ComercioExteriorPais", scope = ComercioExterior.class)
    public JAXBElement<ComercioExterior.ComercioExteriorPais> createComercioExteriorComercioExteriorPais(ComercioExterior.ComercioExteriorPais value) {
        return new JAXBElement<ComercioExterior.ComercioExteriorPais>(_ComercioExteriorComercioExteriorPais_QNAME, ComercioExterior.ComercioExteriorPais.class, ComercioExterior.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Fecha", scope = Reclamos.Reclamo.class)
    public JAXBElement<String> createReclamosReclamoFecha(String value) {
        return new JAXBElement<String>(_DireccionesDireccionFecha_QNAME, String.class, Reclamos.Reclamo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Producto", scope = Reclamos.Reclamo.class)
    public JAXBElement<String> createReclamosReclamoProducto(String value) {
        return new JAXBElement<String>(_ReclamosReclamoProducto_QNAME, String.class, Reclamos.Reclamo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "MontoSolesAclarados", scope = CarteraMorosa.ResumenProtestos.Periodo.class)
    public JAXBElement<BigDecimal> createCarteraMorosaResumenProtestosPeriodoMontoSolesAclarados(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_CarteraMorosaResumenProtestosPeriodoMontoSolesAclarados_QNAME, BigDecimal.class, CarteraMorosa.ResumenProtestos.Periodo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "MontoSolesNoAclarados", scope = CarteraMorosa.ResumenProtestos.Periodo.class)
    public JAXBElement<BigDecimal> createCarteraMorosaResumenProtestosPeriodoMontoSolesNoAclarados(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_CarteraMorosaResumenProtestosPeriodoMontoSolesNoAclarados_QNAME, BigDecimal.class, CarteraMorosa.ResumenProtestos.Periodo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "MontoDolaresAclarados", scope = CarteraMorosa.ResumenProtestos.Periodo.class)
    public JAXBElement<BigDecimal> createCarteraMorosaResumenProtestosPeriodoMontoDolaresAclarados(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_CarteraMorosaResumenProtestosPeriodoMontoDolaresAclarados_QNAME, BigDecimal.class, CarteraMorosa.ResumenProtestos.Periodo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "MontoDolaresNoAclarados", scope = CarteraMorosa.ResumenProtestos.Periodo.class)
    public JAXBElement<BigDecimal> createCarteraMorosaResumenProtestosPeriodoMontoDolaresNoAclarados(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_CarteraMorosaResumenProtestosPeriodoMontoDolaresNoAclarados_QNAME, BigDecimal.class, CarteraMorosa.ResumenProtestos.Periodo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "NumeroOperacion", scope = ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Importaciones.Importacion.class)
    public JAXBElement<String> createComercioExteriorComercioExteriorAnualPeriodosPeriodoImportacionesImportacionNumeroOperacion(String value) {
        return new JAXBElement<String>(_ComercioExteriorComercioExteriorAnualPeriodosPeriodoExportacionesExportacionNumeroOperacion_QNAME, String.class, ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Importaciones.Importacion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "TotalFOB", scope = ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Importaciones.Importacion.class)
    public JAXBElement<String> createComercioExteriorComercioExteriorAnualPeriodosPeriodoImportacionesImportacionTotalFOB(String value) {
        return new JAXBElement<String>(_ComercioExteriorComercioExteriorAnualPeriodosPeriodoExportacionesExportacionTotalFOB_QNAME, String.class, ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Importaciones.Importacion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "PaisProcedencia", scope = ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Importaciones.Importacion.class)
    public JAXBElement<String> createComercioExteriorComercioExteriorAnualPeriodosPeriodoImportacionesImportacionPaisProcedencia(String value) {
        return new JAXBElement<String>(_ComercioExteriorComercioExteriorMensualPeriodosPeriodoImportacionesImportacionPaisProcedencia_QNAME, String.class, ComercioExterior.ComercioExteriorAnual.Periodos.Periodo.Importaciones.Importacion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Nombre", scope = SistemaFinanciero.RCC.DetalleEntidades.Entidad.class)
    public JAXBElement<String> createSistemaFinancieroRCCDetalleEntidadesEntidadNombre(String value) {
        return new JAXBElement<String>(_SistemaFinancieroRectificacionesRectificacionEntidadesEntidadNombre_QNAME, String.class, SistemaFinanciero.RCC.DetalleEntidades.Entidad.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CreditosRefinanciados", scope = SistemaFinanciero.RCC.DetalleEntidades.Entidad.class)
    public JAXBElement<BigDecimal> createSistemaFinancieroRCCDetalleEntidadesEntidadCreditosRefinanciados(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_SistemaFinancieroRCCDetalleEntidadesEntidadCreditosRefinanciados_QNAME, BigDecimal.class, SistemaFinanciero.RCC.DetalleEntidades.Entidad.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Codigo", scope = SistemaFinanciero.RCC.DetalleEntidades.Entidad.class)
    public JAXBElement<String> createSistemaFinancieroRCCDetalleEntidadesEntidadCodigo(String value) {
        return new JAXBElement<String>(_SistemaFinancieroRectificacionesRectificacionEntidadesEntidadCodigo_QNAME, String.class, SistemaFinanciero.RCC.DetalleEntidades.Entidad.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CreditosVencidos", scope = SistemaFinanciero.RCC.DetalleEntidades.Entidad.class)
    public JAXBElement<BigDecimal> createSistemaFinancieroRCCDetalleEntidadesEntidadCreditosVencidos(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_SistemaFinancieroRCCDetalleEntidadesEntidadCreditosVencidos_QNAME, BigDecimal.class, SistemaFinanciero.RCC.DetalleEntidades.Entidad.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CreditosVigentes", scope = SistemaFinanciero.RCC.DetalleEntidades.Entidad.class)
    public JAXBElement<BigDecimal> createSistemaFinancieroRCCDetalleEntidadesEntidadCreditosVigentes(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_SistemaFinancieroRCCDetalleEntidadesEntidadCreditosVigentes_QNAME, BigDecimal.class, SistemaFinanciero.RCC.DetalleEntidades.Entidad.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CreditosJudicial", scope = SistemaFinanciero.RCC.DetalleEntidades.Entidad.class)
    public JAXBElement<BigDecimal> createSistemaFinancieroRCCDetalleEntidadesEntidadCreditosJudicial(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_SistemaFinancieroRCCDetalleEntidadesEntidadCreditosJudicial_QNAME, BigDecimal.class, SistemaFinanciero.RCC.DetalleEntidades.Entidad.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Calificacion", scope = SistemaFinanciero.RCC.DetalleEntidades.Entidad.class)
    public JAXBElement<String> createSistemaFinancieroRCCDetalleEntidadesEntidadCalificacion(String value) {
        return new JAXBElement<String>(_SistemaFinancieroRCCPeriodosPeriodoDeudasDeudaCalificacion_QNAME, String.class, SistemaFinanciero.RCC.DetalleEntidades.Entidad.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Dice", scope = SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.Detalles.Detalle.class, defaultValue = "")
    public JAXBElement<String> createSistemaFinancieroRectificacionesRectificacionEntidadesEntidadDetallesDetalleDice(String value) {
        return new JAXBElement<String>(_SistemaFinancieroRectificacionesRectificacionEntidadesEntidadDetallesDetalleDice_QNAME, String.class, SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.Detalles.Detalle.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Debedecir", scope = SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.Detalles.Detalle.class, defaultValue = "")
    public JAXBElement<String> createSistemaFinancieroRectificacionesRectificacionEntidadesEntidadDetallesDetalleDebedecir(String value) {
        return new JAXBElement<String>(_SistemaFinancieroRectificacionesRectificacionEntidadesEntidadDetallesDetalleDebedecir_QNAME, String.class, SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.Detalles.Detalle.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Concepto", scope = SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.Detalles.Detalle.class, defaultValue = "")
    public JAXBElement<String> createSistemaFinancieroRectificacionesRectificacionEntidadesEntidadDetallesDetalleConcepto(String value) {
        return new JAXBElement<String>(_SistemaFinancieroRectificacionesRectificacionEntidadesEntidadDetallesDetalleConcepto_QNAME, String.class, SistemaFinanciero.Rectificaciones.Rectificacion.Entidades.Entidad.Detalles.Detalle.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "NivelRiesgo", scope = RiskPredictor.class)
    public JAXBElement<String> createRiskPredictorNivelRiesgo(String value) {
        return new JAXBElement<String>(_ScoreEmpresasNivelRiesgo_QNAME, String.class, RiskPredictor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Puntaje", scope = RiskPredictor.class)
    public JAXBElement<String> createRiskPredictorPuntaje(String value) {
        return new JAXBElement<String>(_ScoreEmpresasPuntaje_QNAME, String.class, RiskPredictor.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Conclusion", scope = RiskPredictor.class)
    public JAXBElement<String> createRiskPredictorConclusion(String value) {
        return new JAXBElement<String>(_ScoreEmpresasConclusion_QNAME, String.class, RiskPredictor.class, value);
    }

}
