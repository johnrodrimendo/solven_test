package com.affirm.acceso.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.catalog.AreaType;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.transactional.*;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dev5 on 26/07/17.
 */
public class AccesoExpedienteLibreDisponibilidad {

    @SerializedName("Expediente")
    private Integer nroExpediente;
    @SerializedName("Producto")
    private Integer productoId;
    @SerializedName("SubProducto")
    private Integer subProductoId;
    @SerializedName("Concesionario")
    private Integer concesionario;
    @SerializedName("UbigeodelConcesionerio")
    private String ubigeoConcesionario;
    @SerializedName("CentrodeOperacion")
    private Integer centroOperacion;
    @SerializedName("CentrodeFinanciamiento")
    private Integer centroFinanciamiento;
    @SerializedName("Operador")
    private Integer operador;
    @SerializedName("TipodeDocumentodeIdentidadCliente")
    private String tipoDocumento;
    @SerializedName("DocumentodeIdentidadCliente")
    private String nroDocumento;
    @SerializedName("NombresdelCliente")
    private String nombres;
    @SerializedName("ApellidoPaternodelCliente")
    private String apellidoPaterno;
    @SerializedName("ApellidoMaternodelCliente")
    private String apellidoMaterno;
    @SerializedName("FechadeNacimientoCliente")
    private String fechaNacimiento;
    @SerializedName("GeneroCliente")
    private String genero;
    @SerializedName("EstadoCivilCliente")
    private String estadoCivil;
    @SerializedName("TelefonoCliente")
    private String telefono;
    @SerializedName("CorreoElectronicoCliente")
    private String email;
    @SerializedName("CargaFamiliarCliente")
    private Integer numDependientes;
    @SerializedName("TipodeDocumentodeIdentidadConyuge")
    private String conyugueTipoDocumento;
    @SerializedName("DocumentodeIdentidadConyuge")
    private String conyugueNumeroDocumento;
    @SerializedName("NombresdelConyuge")
    private String conyugueNombres;
    @SerializedName("ApellidoPaternodelConyuge")
    private String conyugueApellidoPaterno;
    @SerializedName("ApellidoMaternodelConyuge")
    private String conyugueApellidoMaterno;
    @SerializedName("FechadeNacimientoConyuge")
    private String conyugueFechaNacimiento;

    @SerializedName("IngresoFijoClienteEco")
    private Double ingresoFijoMensual;
    @SerializedName("IngresoVariabledelClienteEco")
    private Double ingresoVariableMensual;
    @SerializedName("FechadeIngresoLaboraldelClienteEco")
    private String ingresoFecha;
    @SerializedName("ConyugeSustentadeIngresosEco")
    private Integer ingresoConyugueSustenta;
    @SerializedName("IngresoFijoConyugeEco")
    private Double ingresoConyugueFijoMensual;
    @SerializedName("IngresoVariableConyugeEco")
    private Double ingresoConyugueVariableMensual;
    @SerializedName("MontodeDesembolsoSim")
    private Double simulacionMonto;
    @SerializedName("TEASim")
    private Double simulacionTea;
    @SerializedName("NumerodecuotasSim")
    private Integer simulacionCuotas;
    @SerializedName("FechadeactivacionSim")
    private String simulacionFecActivacion;
    @SerializedName("FechadeprimervencimientoSim")
    private String simulacionFecPrimerVenc;
    @SerializedName("NacionalidadCom")
    private Integer nacionalidad;
    @SerializedName("ResidenciaCom")
    private Integer residencia;
    @SerializedName("FormalidadLaboralCom")
    private String ocupacion;
    @SerializedName("NiveldeEducacionCom")
    private String nivelEducacion;
    @SerializedName("SectorEconomicoCom")
    private String sectorEconomico;
    @SerializedName("ActividadEconomicaCom")
    private Integer actividadEconomica;
    @SerializedName("ProfesionyOcupacionCom")
    private Integer profesionOcupacion;
    @SerializedName("PEPCom")
    private Integer pep;
    @SerializedName("DomicilioLatitudCom")
    private Double direccionLatitud;
    @SerializedName("DomicilioLongitudCom")
    private Double direccionLongitud;
    @SerializedName("UbigeodelDomicilioCom")
    private String direccionUbigeo;
    @SerializedName("TipodeviaCom")
    private Integer direccionTipoVia;
    @SerializedName("NombredeviaCom")
    private String direccionNombreVia;
    @SerializedName("TipodezonaCom")
    private Integer direccionTipoZona;
    @SerializedName("NombredezonaCom")
    private String direccionNombreZona;
    @SerializedName("NombredeCentrolaboralAct")
    private String cenlabNombre;
    @SerializedName("RUCAct")
    private String cenlabRuc;
    @SerializedName("UbigeodelCentroLaboralAct")
    private String cenlabUbigeo;
    @SerializedName("TipodeviaAct")
    private Integer cenlabTipoVia;
    @SerializedName("NombredeviaAct")
    private String cenlabNombreVia;
    @SerializedName("TipodezonaAct")
    private Integer cenlabTipoZona;
    @SerializedName("NombredezonaAct")
    private String cenlabNombreZona;
    @SerializedName("LaboralLatitudAct")
    private Double cenlabLatitud;
    @SerializedName("LaboralLongitudAct")
    private Double cenlabLongitud;
    @SerializedName("TelefonoAct")
    private String cenlabTelefono;
    @SerializedName("CargoAct")
    private String cenlabCargo;
    @SerializedName("EntidadBancariaBanc")
    private Integer entbanCodigo;
    @SerializedName("TipodeCuentaBanc")
    private Integer entbanTipoCuenta;
    @SerializedName("NumerodeCuentabancariaBanc")
    private String entbanNumeroCuenta;
    @SerializedName("NumerodeCuentaInterbancariaBanc")
    private String entbanCci;
    @SerializedName("EstadodeInformacionBanc")
    private Integer estadoInformacion;
    @SerializedName("CreditoEntidad")
    private String creditoEntidad;
    @SerializedName("CodigoEvaluacion")
    private String codigoEvaluacion;

    public AccesoExpedienteLibreDisponibilidad(Integer expediente,
                                               TranslatorDAO translatorDAO,
                                               Person person,
                                               User user,
                                               PersonOcupationalInformation ocupationalInformation,
                                               PersonContactInformation contactInformation,
                                               LoanOffer loanOffer,
                                               DisggregatedAddress disggregatedHomeAddres,
                                               DisggregatedAddress disggregatedJobAddres,
                                               PersonBankAccountInformation personBankAccountInformation) throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        setNroExpediente(expediente);
        setProductoId(34);
        setSubProductoId(3);
        setConcesionario(1);
        setUbigeoConcesionario("150101");
        setCentroOperacion(5);
        setCentroFinanciamiento(3);
        setOperador(19);
        setTipoDocumento(translatorDAO.translate(Entity.ACCESO, 22, person.getDocumentType().getId() + "", null));
        setNroDocumento(person.getDocumentNumber());
        setNombres(person.getName());
        setApellidoPaterno(person.getFirstSurname());
        setApellidoMaterno(person.getLastSurname());
        setFechaNacimiento(person.getBirthday() != null ? dateFormat.format(person.getBirthday()) : null);
        setGenero(person.getGender() != null ? person.getGender() + "" : null);
        setEstadoCivil(translatorDAO.translate(Entity.ACCESO, 23, person.getMaritalStatus().getId() + "", null));
        setTelefono(user.getPhoneNumber());
        setEmail(user.getEmail());
        setNumDependientes(person.getDependents());
        if(person.getPartner() != null){
            setConyugueTipoDocumento(translatorDAO.translate(Entity.ACCESO, 22, person.getPartner().getDocumentType().getId() + "", null));
            setConyugueNumeroDocumento(person.getPartner().getDocumentNumber());
            setConyugueNombres(person.getPartner().getName());
            setConyugueApellidoPaterno(person.getPartner().getFirstSurname());
            setConyugueApellidoMaterno(person.getPartner().getLastSurname());
            setConyugueFechaNacimiento(person.getPartner().getBirthday() != null ? dateFormat.format(person.getPartner().getBirthday()) : null);
        }
        setIngresoFijoMensual(ocupationalInformation.getFixedGrossIncome());
        setIngresoVariableMensual(0.0);// Always 0
        setIngresoFecha(ocupationalInformation.getStartDate() != null ? dateFormat.format(ocupationalInformation.getStartDate()) : null);
        setIngresoConyugueSustenta(2); // Always no
        setSimulacionMonto(loanOffer.getAmmount());
        setSimulacionTea(loanOffer.getEffectiveAnualRate());
        setSimulacionCuotas(loanOffer.getInstallments());
        setSimulacionFecActivacion(dateFormat.format(new Date()));
        setSimulacionFecPrimerVenc(loanOffer.getFirstDueDate() != null ? dateFormat.format(loanOffer.getFirstDueDate()) : null);
        setNacionalidad(Integer.parseInt(translatorDAO.translate(Entity.ACCESO, 41, person.getNationality().getId() + "", null)));
        setResidencia(187); // Always peruvian
        setOcupacion(translatorDAO.translate(Entity.ACCESO, 27, ocupationalInformation.getActivityType().getId() + "", null));
        if(person.getStudyLevel()!= null)
            setNivelEducacion(translatorDAO.translate(Entity.ACCESO, 24, person.getStudyLevel().getId() + "", null));
        setSectorEconomico(person.getProfession() != null ? translatorDAO.translate(Entity.ACCESO, 42, person.getProfession().getId() + "", null) : null);

        String actividadEconomicaId = translatorDAO.translate(Entity.ACCESO, 43, person.getProfession().getId() + "", null);
        setActividadEconomica(person.getProfession() != null ? actividadEconomicaId == null || "".equalsIgnoreCase(actividadEconomicaId) ? null : Integer.parseInt(actividadEconomicaId) : null);// TODO PUEDE NO ESTAR EN TRANSLATE

        setProfesionOcupacion(Integer.parseInt(translatorDAO.translate(Entity.ACCESO, 44, person.getProfessionOccupationId() + "", null)));
        setPep(2);
        setDireccionLatitud(contactInformation.getAddressLatitude());
        setDireccionLongitud(contactInformation.getAddressLongitude());
        setDireccionUbigeo(disggregatedHomeAddres.getIneiUbigeo());
        setDireccionTipoVia(Integer.parseInt(translatorDAO.translate(Entity.ACCESO, 45, disggregatedHomeAddres.getStreetType().getId() + "", null)));
        setDireccionNombreVia(disggregatedHomeAddres.getStreetName());
        if(disggregatedHomeAddres.getAreaType().getId() == AreaType.NO_APLICA){
            setDireccionTipoZona(Integer.parseInt(translatorDAO.translate(Entity.ACCESO, 40, AreaType.URBANIZACION + "", null)));
            setDireccionNombreZona("N/A");
        }else{
            setDireccionTipoZona(Integer.parseInt(translatorDAO.translate(Entity.ACCESO, 40, disggregatedHomeAddres.getAreaType().getId() + "", null)));
            setDireccionNombreZona(disggregatedHomeAddres.getZoneName());
        }
        setCenlabNombre(ocupationalInformation.getCompanyName());
        setCenlabRuc(ocupationalInformation.getRuc());
        setCenlabUbigeo(disggregatedJobAddres.getIneiUbigeo());
        setCenlabTipoVia(Integer.parseInt(translatorDAO.translate(Entity.ACCESO, 45, disggregatedJobAddres.getStreetType().getId() + "", null)));
        setCenlabNombreVia(disggregatedJobAddres.getStreetName());
        if(disggregatedJobAddres.getAreaType().getId() == AreaType.NO_APLICA){
            setCenlabTipoZona(Integer.parseInt(translatorDAO.translate(Entity.ACCESO, 40, AreaType.URBANIZACION + "", null)));
            setCenlabNombreZona("N/A");
        }else{
            setCenlabTipoZona(Integer.parseInt(translatorDAO.translate(Entity.ACCESO, 40, disggregatedJobAddres.getAreaType().getId() + "", null)));
            setCenlabNombreZona(disggregatedJobAddres.getZoneName());
        }
        setCenlabLatitud(ocupationalInformation.getAddressLatitude());
        setCenlabLongitud(ocupationalInformation.getAddressLongitude());
        setCenlabTelefono(ocupationalInformation.getPhoneNumber());
        setCenlabCargo("98"); // No declara

        String bancoId = translatorDAO.translate(Entity.ACCESO, 46, personBankAccountInformation.getBank().getId() + "", null);
        setEntbanCodigo(bancoId == null || "".equalsIgnoreCase(bancoId) ? null : Integer.parseInt(bancoId));// TODO PUEDE NO ESTAR EN TRANSLATE

        setEntbanTipoCuenta(personBankAccountInformation.getBankAccountType() == 'S' ? 1 : 2);
        setEntbanNumeroCuenta(personBankAccountInformation.getBankAccount());
        setEntbanCci(personBankAccountInformation.getCciCode());
    }

    public Integer getNroExpediente() {
        return nroExpediente;
    }

    public void setNroExpediente(Integer nroExpediente) {
        this.nroExpediente = nroExpediente;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public Integer getSubProductoId() {
        return subProductoId;
    }

    public void setSubProductoId(Integer subProductoId) {
        this.subProductoId = subProductoId;
    }

    public Integer getConcesionario() {
        return concesionario;
    }

    public void setConcesionario(Integer concesionario) {
        this.concesionario = concesionario;
    }

    public String getUbigeoConcesionario() {
        return ubigeoConcesionario;
    }

    public void setUbigeoConcesionario(String ubigeoConcesionario) {
        this.ubigeoConcesionario = ubigeoConcesionario;
    }

    public Integer getCentroOperacion() {
        return centroOperacion;
    }

    public void setCentroOperacion(Integer centroOperacion) {
        this.centroOperacion = centroOperacion;
    }

    public Integer getCentroFinanciamiento() {
        return centroFinanciamiento;
    }

    public void setCentroFinanciamiento(Integer centroFinanciamiento) {
        this.centroFinanciamiento = centroFinanciamiento;
    }

    public Integer getOperador() {
        return operador;
    }

    public void setOperador(Integer operador) {
        this.operador = operador;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getNumDependientes() {
        return numDependientes;
    }

    public void setNumDependientes(Integer numDependientes) {
        this.numDependientes = numDependientes;
    }

    public String getConyugueTipoDocumento() {
        return conyugueTipoDocumento;
    }

    public void setConyugueTipoDocumento(String conyugueTipoDocumento) {
        this.conyugueTipoDocumento = conyugueTipoDocumento;
    }

    public String getConyugueNumeroDocumento() {
        return conyugueNumeroDocumento;
    }

    public void setConyugueNumeroDocumento(String conyugueNumeroDocumento) {
        this.conyugueNumeroDocumento = conyugueNumeroDocumento;
    }

    public String getConyugueNombres() {
        return conyugueNombres;
    }

    public void setConyugueNombres(String conyugueNombres) {
        this.conyugueNombres = conyugueNombres;
    }

    public String getConyugueApellidoPaterno() {
        return conyugueApellidoPaterno;
    }

    public void setConyugueApellidoPaterno(String conyugueApellidoPaterno) {
        this.conyugueApellidoPaterno = conyugueApellidoPaterno;
    }

    public String getConyugueApellidoMaterno() {
        return conyugueApellidoMaterno;
    }

    public void setConyugueApellidoMaterno(String conyugueApellidoMaterno) {
        this.conyugueApellidoMaterno = conyugueApellidoMaterno;
    }

    public String getConyugueFechaNacimiento() {
        return conyugueFechaNacimiento;
    }

    public void setConyugueFechaNacimiento(String conyugueFechaNacimiento) {
        this.conyugueFechaNacimiento = conyugueFechaNacimiento;
    }

    public Double getIngresoFijoMensual() {
        return ingresoFijoMensual;
    }

    public void setIngresoFijoMensual(Double ingresoFijoMensual) {
        this.ingresoFijoMensual = ingresoFijoMensual;
    }

    public Double getIngresoVariableMensual() {
        return ingresoVariableMensual;
    }

    public void setIngresoVariableMensual(Double ingresoVariableMensual) {
        this.ingresoVariableMensual = ingresoVariableMensual;
    }

    public String getIngresoFecha() {
        return ingresoFecha;
    }

    public void setIngresoFecha(String ingresoFecha) {
        this.ingresoFecha = ingresoFecha;
    }

    public Integer getIngresoConyugueSustenta() {
        return ingresoConyugueSustenta;
    }

    public void setIngresoConyugueSustenta(Integer ingresoConyugueSustenta) {
        this.ingresoConyugueSustenta = ingresoConyugueSustenta;
    }

    public Double getIngresoConyugueFijoMensual() {
        return ingresoConyugueFijoMensual;
    }

    public void setIngresoConyugueFijoMensual(Double ingresoConyugueFijoMensual) {
        this.ingresoConyugueFijoMensual = ingresoConyugueFijoMensual;
    }

    public Double getIngresoConyugueVariableMensual() {
        return ingresoConyugueVariableMensual;
    }

    public void setIngresoConyugueVariableMensual(Double ingresoConyugueVariableMensual) {
        this.ingresoConyugueVariableMensual = ingresoConyugueVariableMensual;
    }

    public Double getSimulacionMonto() {
        return simulacionMonto;
    }

    public void setSimulacionMonto(Double simulacionMonto) {
        this.simulacionMonto = simulacionMonto;
    }

    public Double getSimulacionTea() {
        return simulacionTea;
    }

    public void setSimulacionTea(Double simulacionTea) {
        this.simulacionTea = simulacionTea;
    }

    public Integer getSimulacionCuotas() {
        return simulacionCuotas;
    }

    public void setSimulacionCuotas(Integer simulacionCuotas) {
        this.simulacionCuotas = simulacionCuotas;
    }

    public String getSimulacionFecActivacion() {
        return simulacionFecActivacion;
    }

    public void setSimulacionFecActivacion(String simulacionFecActivacion) {
        this.simulacionFecActivacion = simulacionFecActivacion;
    }

    public String getSimulacionFecPrimerVenc() {
        return simulacionFecPrimerVenc;
    }

    public void setSimulacionFecPrimerVenc(String simulacionFecPrimerVenc) {
        this.simulacionFecPrimerVenc = simulacionFecPrimerVenc;
    }

    public Integer getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(Integer nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public Integer getResidencia() {
        return residencia;
    }

    public void setResidencia(Integer residencia) {
        this.residencia = residencia;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public String getNivelEducacion() {
        return nivelEducacion;
    }

    public void setNivelEducacion(String nivelEducacion) {
        this.nivelEducacion = nivelEducacion;
    }

    public String getSectorEconomico() {
        return sectorEconomico;
    }

    public void setSectorEconomico(String sectorEconomico) {
        this.sectorEconomico = sectorEconomico;
    }

    public Integer getActividadEconomica() {
        return actividadEconomica;
    }

    public void setActividadEconomica(Integer actividadEconomica) {
        this.actividadEconomica = actividadEconomica;
    }

    public Integer getProfesionOcupacion() {
        return profesionOcupacion;
    }

    public void setProfesionOcupacion(Integer profesionOcupacion) {
        this.profesionOcupacion = profesionOcupacion;
    }

    public Integer getPep() {
        return pep;
    }

    public void setPep(Integer pep) {
        this.pep = pep;
    }

    public Double getDireccionLatitud() {
        return direccionLatitud;
    }

    public void setDireccionLatitud(Double direccionLatitud) {
        this.direccionLatitud = direccionLatitud;
    }

    public Double getDireccionLongitud() {
        return direccionLongitud;
    }

    public void setDireccionLongitud(Double direccionLongitud) {
        this.direccionLongitud = direccionLongitud;
    }

    public String getDireccionUbigeo() {
        return direccionUbigeo;
    }

    public void setDireccionUbigeo(String direccionUbigeo) {
        this.direccionUbigeo = direccionUbigeo;
    }

    public Integer getDireccionTipoVia() {
        return direccionTipoVia;
    }

    public void setDireccionTipoVia(Integer direccionTipoVia) {
        this.direccionTipoVia = direccionTipoVia;
    }

    public String getDireccionNombreVia() {
        return direccionNombreVia;
    }

    public void setDireccionNombreVia(String direccionNombreVia) {
        this.direccionNombreVia = direccionNombreVia;
    }

    public Integer getDireccionTipoZona() {
        return direccionTipoZona;
    }

    public void setDireccionTipoZona(Integer direccionTipoZona) {
        this.direccionTipoZona = direccionTipoZona;
    }

    public String getDireccionNombreZona() {
        return direccionNombreZona;
    }

    public void setDireccionNombreZona(String direccionNombreZona) {
        this.direccionNombreZona = direccionNombreZona;
    }

    public String getCenlabNombre() {
        return cenlabNombre;
    }

    public void setCenlabNombre(String cenlabNombre) {
        this.cenlabNombre = cenlabNombre;
    }

    public String getCenlabRuc() {
        return cenlabRuc;
    }

    public void setCenlabRuc(String cenlabRuc) {
        this.cenlabRuc = cenlabRuc;
    }

    public String getCenlabUbigeo() {
        return cenlabUbigeo;
    }

    public void setCenlabUbigeo(String cenlabUbigeo) {
        this.cenlabUbigeo = cenlabUbigeo;
    }

    public Integer getCenlabTipoVia() {
        return cenlabTipoVia;
    }

    public void setCenlabTipoVia(Integer cenlabTipoVia) {
        this.cenlabTipoVia = cenlabTipoVia;
    }

    public String getCenlabNombreVia() {
        return cenlabNombreVia;
    }

    public void setCenlabNombreVia(String cenlabNombreVia) {
        this.cenlabNombreVia = cenlabNombreVia;
    }

    public Integer getCenlabTipoZona() {
        return cenlabTipoZona;
    }

    public void setCenlabTipoZona(Integer cenlabTipoZona) {
        this.cenlabTipoZona = cenlabTipoZona;
    }

    public String getCenlabNombreZona() {
        return cenlabNombreZona;
    }

    public void setCenlabNombreZona(String cenlabNombreZona) {
        this.cenlabNombreZona = cenlabNombreZona;
    }

    public Double getCenlabLatitud() {
        return cenlabLatitud;
    }

    public void setCenlabLatitud(Double cenlabLatitud) {
        this.cenlabLatitud = cenlabLatitud;
    }

    public Double getCenlabLongitud() {
        return cenlabLongitud;
    }

    public void setCenlabLongitud(Double cenlabLongitud) {
        this.cenlabLongitud = cenlabLongitud;
    }

    public String getCenlabTelefono() {
        return cenlabTelefono;
    }

    public void setCenlabTelefono(String cenlabTelefono) {
        this.cenlabTelefono = cenlabTelefono;
    }

    public String getCenlabCargo() {
        return cenlabCargo;
    }

    public void setCenlabCargo(String cenlabCargo) {
        this.cenlabCargo = cenlabCargo;
    }

    public Integer getEntbanCodigo() {
        return entbanCodigo;
    }

    public void setEntbanCodigo(Integer entbanCodigo) {
        this.entbanCodigo = entbanCodigo;
    }

    public Integer getEntbanTipoCuenta() {
        return entbanTipoCuenta;
    }

    public void setEntbanTipoCuenta(Integer entbanTipoCuenta) {
        this.entbanTipoCuenta = entbanTipoCuenta;
    }

    public String getEntbanNumeroCuenta() {
        return entbanNumeroCuenta;
    }

    public void setEntbanNumeroCuenta(String entbanNumeroCuenta) {
        this.entbanNumeroCuenta = entbanNumeroCuenta;
    }

    public String getEntbanCci() {
        return entbanCci;
    }

    public void setEntbanCci(String entbanCci) {
        this.entbanCci = entbanCci;
    }

    public Integer getEstadoInformacion() {
        return estadoInformacion;
    }

    public void setEstadoInformacion(Integer estadoInformacion) {
        this.estadoInformacion = estadoInformacion;
    }

    public String getCreditoEntidad() {
        return creditoEntidad;
    }

    public void setCreditoEntidad(String creditoEntidad) {
        this.creditoEntidad = creditoEntidad;
    }

    public String getCodigoEvaluacion() {
        return codigoEvaluacion;
    }

    public void setCodigoEvaluacion(String codigoEvaluacion) {
        this.codigoEvaluacion = codigoEvaluacion;
    }
}
