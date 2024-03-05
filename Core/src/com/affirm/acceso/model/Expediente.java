package com.affirm.acceso.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.MaritalStatus;
import com.affirm.common.model.catalog.SubActivityType;
import com.affirm.common.model.catalog.UserFileType;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.FileService;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dev5 on 26/07/17.
 */
public class Expediente {

    private static final int ENTITY_ID = Entity.ACCESO;

    @SerializedName("p_id_sesion")
    private Integer sesionId;
    @SerializedName("p_co_expedi")
    private Integer nroExpediente;
    @SerializedName("p_co_produc")
    private Integer productoId;
    @SerializedName("p_ti_modelo")
    private Integer subProductoId;
    @SerializedName("p_co_conces")
    private Integer concesionario;
    @SerializedName("p_co_ubigeo")
    private String ubigeoConcesionario;
    @SerializedName("p_co_cenope")
    private Integer centroOperacion;
    @SerializedName("p_ti_canent")
    private Integer tipoEntrada;
    @SerializedName("p_co_cenfin")
    private Integer centroFinanciamiento;
    @SerializedName("p_co_operad")
    private Integer operador;
    @SerializedName("p_ti_indapp")
    private Integer tieneAPP;
    @SerializedName("p_co_tipapp")
    private Integer tipoAPP;
    @SerializedName("p_nu_mesapp")
    private Integer mesesAPP;
    @SerializedName("p_ti_liccon")
    private String tipoBrevete;
    @SerializedName("p_fe_explic")
    private String fechaExpBrevete;
    @SerializedName("p_ti_geslic")
    private Integer gestionBrevete;
    @SerializedName("p_ti_modser")
    private Integer modalidadServicio;
    @SerializedName("p_ti_exppol")
    private Integer expuestaPublicamente;
    @SerializedName("p_ti_docide")
    private String tipoDocumento;
    @SerializedName("p_co_docide")
    private String nroDocumento;
    @SerializedName("p_no_apepat")
    private String apellidoPaterno;
    @SerializedName("p_no_apemat")
    private String apellidoMaterno;
    @SerializedName("p_no_nombre")
    private String nombres;
    @SerializedName("p_fe_nacimi")
    private String fechaNacimiento;
    @SerializedName("p_ti_genero")
    private String genero;
    @SerializedName("p_ti_estciv")
    private String estadoCivil;
    @SerializedName("p_nu_telmov")
    private String telefonoMovil;
    @SerializedName("p_nu_teldom")
    private String telefonoCasa;
    @SerializedName("p_nu_tellab")
    private String telefonoOficina;
    @SerializedName("p_co_correo")
    private String email;
    @SerializedName("p_ti_nivedu")
    private String gradoInstruccion;
    @SerializedName("p_ti_ocupac")
    private String ocupacion;
    @SerializedName("p_ti_catocu")
    private String tipoRenta;
    @SerializedName("p_nu_carfam")
    private Integer numDependientes;
    @SerializedName("p_nu_meslab")
    private Integer antiguedadLaboral;
    @SerializedName("p_im_ingnet")
    private Double ingresoNeto;
    @SerializedName("p_nu_geolat")
    private Double domicilioLatitud;
    @SerializedName("p_nu_geolon")
    private Double domicilioLongitud;
    @SerializedName("p_co_ubidom")
    private String domicilioUbigeo;
    @SerializedName("p_ti_reside")
    private String tipoResidencia;
    @SerializedName("p_in_pregar")
    private Integer garante;
    @SerializedName("p_ti_matviv")
    private String materialVivienda;
    @SerializedName("p_ti_doccon")
    private String conyugueTipoDocumento;
    @SerializedName("p_co_doccon")
    private String conyugueNumeroDocumento;
    @SerializedName("p_no_apepco")
    private String conyugueApellidoPaterno;
    @SerializedName("p_no_apemco")
    private String conyugueApellidoMaterno;
    @SerializedName("p_no_nomcon")
    private String conyugueNombres;
    @SerializedName("p_fe_naccon")
    private String conyugueFechaNacimiento;
    @SerializedName("p_ti_gencon")
    private String conyugueGenero;
    @SerializedName("p_ti_docgar")
    private String garanteTipoDocumento;
    @SerializedName("p_co_docgar")
    private String garanteNumeroDocumento;
    @SerializedName("p_no_apepga")
    private String garanteApellidoPaterno;
    @SerializedName("p_no_apemga")
    private String garanteApellidoMaterno;
    @SerializedName("p_no_nomgar")
    private String garanteNombres;
    @SerializedName("p_fe_nacgar")
    private String garanteFechaNacimiento;
    @SerializedName("p_ti_gengar")
    private String garanteGenero;
    @SerializedName("p_ti_estcga")
    private String garanteEstadoCivil;
    @SerializedName("p_ti_congar")
    private Integer garanteTipo;
    @SerializedName("p_ti_doccga")
    private String garanteConyugueTipoDocumento;
    @SerializedName("p_co_doccga")
    private String garanteConyugueNumeroDocumento;
    @SerializedName("p_no_apepcg")
    private String garanteConyugueApellidoPaterno;
    @SerializedName("p_no_apemcg")
    private String garanteConyugueApellidoMaterno;
    @SerializedName("p_no_nomcga")
    private String garanteConyugueNombres;
    @SerializedName("p_fe_naccga")
    private String garanteConyugueFechaNacmiento;
    @SerializedName("p_ti_gencga")
    private String garanteConyugueGenero;
    private Date fechaDespacho;
    @SerializedName("p_js_urldoc")
    private List<Documento> documentos;

    /**
     * Constructor creado especificamente para el expediente de acceso de consumo vehicular
     */
    public Expediente(TranslatorDAO translatorDAO, Person person, User user, PersonOcupationalInformation ocupationalInformation, PersonContactInformation contactInformation) throws Exception {
        setProductoId(19);
        setSubProductoId(1);
        setConcesionario(716);
        setUbigeoConcesionario("150101");
        setCentroOperacion(5);
        setTipoEntrada(2);
        setOperador(19);
        setCentroFinanciamiento(3);
        setTieneAPP(2);
        if (person.getPep() != null)
            setExpuestaPublicamente(person.getPep() ? 1 : 2); // ToDo Mandar al translator
        setTipoDocumento(translatorDAO.translate(ENTITY_ID, 22, String.valueOf(person.getDocumentType().getId()), null));
        setNroDocumento(person.getDocumentNumber());
        setApellidoPaterno(person.getFirstSurname());
        setApellidoMaterno(person.getLastSurname());
        setNombres(person.getName());
        setFechaNacimiento(person.getBirthday() != null ? new SimpleDateFormat("yyyy-MM-dd").format(person.getBirthday()) : null);
        setGenero(translatorDAO.translate(ENTITY_ID, 39, person.getGender().toString(), null));
        setTelefonoMovil(user.getPhoneNumber() != null ? user.getPhoneNumber() : "999999999");
        if (getProductoId() == 19) {
            setGradoInstruccion(person.getStudyLevel() != null ? translatorDAO.translate(ENTITY_ID, 24, String.valueOf(person.getStudyLevel().getId()), null) : null);
            setOcupacion(ocupationalInformation.getActivityType() != null ? translatorDAO.translate(ENTITY_ID, 27, String.valueOf(ocupationalInformation.getActivityType().getId()), null) + "" : null);
            if (ocupationalInformation.getActivityType() != null) {
                setTipoRenta(translatorDAO.translate(ENTITY_ID, 31, ocupationalInformation.getActivityType().getId().toString(), null));
            }
        }
        setNumDependientes(person.getDependents());
        setAntiguedadLaboral(ocupationalInformation.getEmploymentTime() != null ? Integer.valueOf(ocupationalInformation.getEmploymentTime()) : null);
        if(ocupationalInformation.getSubActivityType() != null && ocupationalInformation.getSubActivityType().getId().equals(SubActivityType.OWN_BUSINESS))
            setIngresoNeto(ocupationalInformation.getLastYearCompensation());
        else
            setIngresoNeto(ocupationalInformation.getFixedGrossIncome());
        setDomicilioLongitud(contactInformation.getAddressLongitude());
        setDomicilioLatitud(contactInformation.getAddressLatitude());
        setDomicilioUbigeo(contactInformation.getAddressUbigeo() != null ? contactInformation.getAddressUbigeo().getUbigeo() : "150101"); // ToDo Confirmar con AC que no tenemos este campo
        setTipoResidencia(contactInformation.getHousingType() != null ? translatorDAO.translate(ENTITY_ID, 25, String.valueOf(contactInformation.getHousingType().getId()), null) : null);
        setGarante(2);
        setMaterialVivienda("B"); // ToDo mandar aleatoriamente?? o queda esto en duro??
        if (person.getPartner() != null && person.getPartner().getGender() != null) {
            setEstadoCivil(person.getMaritalStatus() != null ? translatorDAO.translate(ENTITY_ID, 23, String.valueOf(person.getMaritalStatus().getId()), null) : null);
            setConyugueTipoDocumento(translatorDAO.translate(ENTITY_ID, 22, String.valueOf(person.getPartner().getDocumentType().getId()), null) + "");
            setConyugueNumeroDocumento(person.getPartner().getDocumentNumber());
            setConyugueApellidoPaterno(person.getPartner().getFirstSurname());
            setConyugueApellidoMaterno(person.getPartner().getLastSurname());
            setConyugueNombres(person.getPartner().getName());
            setConyugueFechaNacimiento(person.getPartner().getBirthday() != null ? new SimpleDateFormat("yyyy-MM-dd").format(person.getPartner().getBirthday()) : null);
            setConyugueGenero(translatorDAO.translate(ENTITY_ID, 39, person.getPartner().getGender().toString(), null));
        } else {
            setEstadoCivil(person.getMaritalStatus() != null ? translatorDAO.translate(ENTITY_ID, 23, String.valueOf(MaritalStatus.SINGLE), null) : null);
        }
    }

    public Expediente() {
        setOperador(19);
    }

    public Expediente(FileService fileService, Integer nroExpediente, List<UserFile> userFiles) throws Exception{
        setNroExpediente(nroExpediente);
        List<Documento> documentos = new ArrayList<>();
        for (UserFile userFile: userFiles) {
            String accesoFileId = "";
            switch (userFile.getFileType().getId()){
                case UserFileType.DNI_MERGED:
                    accesoFileId = "104";
                    break;
                case UserFileType.DOCUMENTO_DOMICILIO:
                    accesoFileId = "121";
                    break;
                case UserFileType.COMPROBANTE_DIRECCION:
                    accesoFileId = "028";
                    break;
                case UserFileType.RECIBO_HONORARIOS:
                    accesoFileId = "071";
                    break;
                case UserFileType.BOLETA_PAGO:
                    accesoFileId = "182";
                    break;
                case UserFileType.BOLETA_PAGO_2:
                    accesoFileId = "056";
                    break;
                case UserFileType.BOLETA_PAGO_3:
                    accesoFileId = "095";
                    break;
            }
            if(accesoFileId != null && !accesoFileId.isEmpty()){
                Documento documento = new Documento(accesoFileId, fileService.generatePublicUserFileUrl(userFile.getId(), true));
                documentos.add(documento);
            }
        }
        if(documentos != null && documentos.size() > 0)
            setDocumentos(documentos);
    }

    public Expediente(Integer sesionId,
                      Integer productoId,
                      Integer subProductoId,
                      Integer concesionario,
                      String ubigeoConcesionario,
                      Integer centroOperacion,
                      Integer tipoEntrada,
                      Integer centroFinanciamiento,
                      Integer tieneAPP,
                      Integer tipoAPP,
                      Integer mesesAPP,
                      String tipoBrevete,
                      String fechaExpBrevete,
                      Integer gestionBrevete,
                      Integer modalidadServicio,
                      Integer expuestaPublicamente,
                      String tipoDocumento,
                      String nroDocumento,
                      String apellidoPaterno,
                      String apellidoMaterno,
                      String nombres,
                      String fechaNacimiento,
                      String genero,
                      String estadoCivil,
                      String telefonoMovil,
                      String gradoInstruccion,
                      String ocupacion,
                      String tipoRenta,
                      Integer numDependientes,
                      Integer antiguedadLaboral,
                      Double ingresoNeto,
                      Double domicilioLongitud,
                      Double domicilioLatitud,
                      String domicilioUbigeo,
                      String tipoResidencia,
                      Integer garante,
                      String materialVivienda,
                      String conyugueTipoDocumento,
                      String conyugueNumeroDocumento,
                      String conyugueApellidoPaterno,
                      String conyugueApellidoMaterno,
                      String conyugueNombres,
                      String conyugueFechaNacimiento,
                      String conyugueGenero,
                      String garanteTipoDocumento,
                      String garanteNumeroDocumento,
                      String garanteApellidoPaterno,
                      String garanteApellidoMaterno,
                      String garanteNombres,
                      String garanteFechaNacimiento,
                      String garanteGenero,
                      String garanteEstadoCivil,
                      Integer garanteTipo,
                      String garanteConyugueTipoDocumento,
                      String garanteConyugueNumeroDocumento,
                      String garanteConyugueApellidoPaterno,
                      String garanteConyugueApellidoMaterno,
                      String garanteConyugueNombres,
                      String garanteConyugueFechaNacmiento,
                      String garanteConyugueGenero) {
        setSesionId(sesionId);
        setProductoId(productoId);
        setSubProductoId(subProductoId);
        setConcesionario(concesionario);
        setUbigeoConcesionario(ubigeoConcesionario);
        setCentroOperacion(centroOperacion);
        setTipoEntrada(tipoEntrada);
        setCentroFinanciamiento(centroFinanciamiento);
        setTieneAPP(tieneAPP);
        setOperador(19);
        if (getProductoId() == 14) {
            setTieneAPP(tieneAPP);
            setTipoAPP(tipoAPP);
            setMesesAPP(mesesAPP);
            setTipoBrevete(tipoBrevete);
            setFechaExpBrevete(fechaExpBrevete);
            setGestionBrevete(gestionBrevete);
            setModalidadServicio(modalidadServicio);
        }
        setExpuestaPublicamente(expuestaPublicamente);
        setTipoDocumento(tipoDocumento);
        setNroDocumento(nroDocumento);
        setApellidoPaterno(apellidoPaterno);
        setApellidoMaterno(apellidoMaterno);
        setNombres(nombres);
        setFechaNacimiento(fechaNacimiento);
        setGenero(genero);
        setEstadoCivil(estadoCivil);
        setTelefonoMovil(telefonoMovil);
        if (getProductoId() == 19) {
            setGradoInstruccion(gradoInstruccion);
            setOcupacion(ocupacion);
            setTipoRenta(tipoRenta);
        }
        setNumDependientes(numDependientes);
        setAntiguedadLaboral(antiguedadLaboral);
        setIngresoNeto(ingresoNeto);
        setDomicilioLongitud(domicilioLongitud);
        setDomicilioLatitud(domicilioLatitud);
        setDomicilioUbigeo(domicilioUbigeo);
        setTipoResidencia(tipoResidencia);
        setGarante(garante);
        setMaterialVivienda(materialVivienda);
        if (getEstadoCivil() == "CO" || getEstadoCivil() == "CA") {
            setConyugueTipoDocumento(conyugueTipoDocumento);
            setConyugueNumeroDocumento(conyugueNumeroDocumento);
            setConyugueApellidoPaterno(conyugueApellidoPaterno);
            setConyugueApellidoMaterno(conyugueApellidoMaterno);
            setConyugueNombres(conyugueNombres);
            setConyugueFechaNacimiento(conyugueFechaNacimiento);
            setConyugueGenero(conyugueGenero);
        }
        if (getGarante() == 1) {
            setGaranteTipoDocumento(garanteTipoDocumento);
            setGaranteNumeroDocumento(garanteNumeroDocumento);
            setGaranteApellidoPaterno(garanteApellidoPaterno);
            setGaranteApellidoMaterno(garanteApellidoMaterno);
            setGaranteNombres(garanteNombres);
            setGaranteFechaNacimiento(garanteFechaNacimiento);
            setGaranteGenero(garanteGenero);
            setGaranteEstadoCivil(garanteEstadoCivil);
            setGaranteTipo(garanteTipo);
            if (getGaranteEstadoCivil() == "CA" || getGaranteEstadoCivil() == "CO") {
                setGaranteConyugueTipoDocumento(garanteConyugueTipoDocumento);
                setGaranteConyugueNumeroDocumento(garanteConyugueNumeroDocumento);
                setGaranteConyugueApellidoPaterno(garanteConyugueApellidoPaterno);
                setGaranteConyugueApellidoMaterno(garanteConyugueApellidoMaterno);
                setGaranteConyugueNombres(garanteConyugueNombres);
                setGaranteConyugueFechaNacmiento(garanteConyugueFechaNacmiento);
                setGaranteConyugueGenero(garanteConyugueGenero);
            }
        }
    }

    public Expediente(Integer sesionId) {

        setOperador(19);
        setSesionId(sesionId);
    }

    public Expediente(Integer sesionId, String numeroDocumento) {
        setSesionId(sesionId);
        setNroDocumento(numeroDocumento);
        setOperador(19);
    }

    public Expediente(Integer sesionId, Integer numeroExpediente) {
        setSesionId(sesionId);
        setNroExpediente(numeroExpediente);
        setOperador(19);
    }

    public Integer getNroExpediente() {
        return nroExpediente;
    }

    public void setNroExpediente(Integer nroExpediente) {
        this.nroExpediente = nroExpediente;
    }

    public Integer getSesionId() {
        return sesionId;
    }

    public void setSesionId(Integer sesionId) {
        this.sesionId = sesionId;
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

    public void setUbigeoConcesionario(String ubgeoConcesionario) {
        this.ubigeoConcesionario = ubgeoConcesionario;
    }

    public Integer getCentroOperacion() {
        return centroOperacion;
    }

    public void setCentroOperacion(Integer centroOperacion) {
        this.centroOperacion = centroOperacion;
    }

    public Integer getTipoEntrada() {
        return tipoEntrada;
    }

    public void setTipoEntrada(Integer tipoEntrada) {
        this.tipoEntrada = tipoEntrada;
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

    public Integer getTieneAPP() {
        return tieneAPP;
    }

    public void setTieneAPP(Integer tieneAPP) {
        this.tieneAPP = tieneAPP;
    }

    public Integer getTipoAPP() {
        return tipoAPP;
    }

    public void setTipoAPP(Integer tipoAPP) {
        this.tipoAPP = tipoAPP;
    }

    public Integer getMesesAPP() {
        return mesesAPP;
    }

    public void setMesesAPP(Integer mesesAPP) {
        this.mesesAPP = mesesAPP;
    }

    public String getTipoBrevete() {
        return tipoBrevete;
    }

    public void setTipoBrevete(String tipoBrevete) {
        this.tipoBrevete = tipoBrevete;
    }

    public String getFechaExpBrevete() {
        return fechaExpBrevete;
    }

    public void setFechaExpBrevete(String fechaExpBrevete) {
        this.fechaExpBrevete = fechaExpBrevete;
    }

    public Integer getGestionBrevete() {
        return gestionBrevete;
    }

    public void setGestionBrevete(Integer gestionBrevete) {
        this.gestionBrevete = gestionBrevete;
    }

    public Integer getModalidadServicio() {
        return modalidadServicio;
    }

    public void setModalidadServicio(Integer modalidadServicio) {
        this.modalidadServicio = modalidadServicio;
    }

    public Integer getExpuestaPublicamente() {
        return expuestaPublicamente;
    }

    public void setExpuestaPublicamente(Integer expuestaPublicamente) {
        this.expuestaPublicamente = expuestaPublicamente;
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

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
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

    public String getTelefonoMovil() {
        return telefonoMovil;
    }

    public void setTelefonoMovil(String telefonoMovil) {
        this.telefonoMovil = telefonoMovil;
    }

    public String getTelefonoCasa() {
        return telefonoCasa;
    }

    public void setTelefonoCasa(String telefonoCasa) {
        this.telefonoCasa = telefonoCasa;
    }

    public String getTelefonoOficina() {
        return telefonoOficina;
    }

    public void setTelefonoOficina(String telefonoOficina) {
        this.telefonoOficina = telefonoOficina;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGradoInstruccion() {
        return gradoInstruccion;
    }

    public void setGradoInstruccion(String gradoInstruccion) {
        this.gradoInstruccion = gradoInstruccion;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public String getTipoRenta() {
        return tipoRenta;
    }

    public void setTipoRenta(String tipoRenta) {
        this.tipoRenta = tipoRenta;
    }

    public Integer getNumDependientes() {
        return numDependientes;
    }

    public void setNumDependientes(Integer numDependientes) {
        this.numDependientes = numDependientes;
    }

    public Integer getAntiguedadLaboral() {
        return antiguedadLaboral;
    }

    public void setAntiguedadLaboral(Integer antiguedadLaboral) {
        this.antiguedadLaboral = antiguedadLaboral;
    }

    public Double getIngresoNeto() {
        return ingresoNeto;
    }

    public void setIngresoNeto(Double ingresoNeto) {
        this.ingresoNeto = ingresoNeto;
    }

    public Double getDomicilioLatitud() {
        return domicilioLatitud;
    }

    public void setDomicilioLatitud(Double domicilioLatitud) {
        this.domicilioLatitud = domicilioLatitud;
    }

    public Double getDomicilioLongitud() {
        return domicilioLongitud;
    }

    public void setDomicilioLongitud(Double domicilioLongitud) {
        this.domicilioLongitud = domicilioLongitud;
    }

    public String getDomicilioUbigeo() {
        return domicilioUbigeo;
    }

    public void setDomicilioUbigeo(String domicilioUbigeo) {
        this.domicilioUbigeo = domicilioUbigeo;
    }

    public String getTipoResidencia() {
        return tipoResidencia;
    }

    public void setTipoResidencia(String tipoResidencia) {
        this.tipoResidencia = tipoResidencia;
    }

    public Integer getGarante() {
        return garante;
    }

    public void setGarante(Integer garante) {
        this.garante = garante;
    }

    public String getMaterialVivienda() {
        return materialVivienda;
    }

    public void setMaterialVivienda(String materialVivienda) {
        this.materialVivienda = materialVivienda;
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

    public String getConyugueNombres() {
        return conyugueNombres;
    }

    public void setConyugueNombres(String conyugueNombres) {
        this.conyugueNombres = conyugueNombres;
    }

    public String getConyugueFechaNacimiento() {
        return conyugueFechaNacimiento;
    }

    public void setConyugueFechaNacimiento(String conyugueFechaNacimiento) {
        this.conyugueFechaNacimiento = conyugueFechaNacimiento;
    }

    public String getConyugueGenero() {
        return conyugueGenero;
    }

    public void setConyugueGenero(String conyugueGenero) {
        this.conyugueGenero = conyugueGenero;
    }

    public String getGaranteTipoDocumento() {
        return garanteTipoDocumento;
    }

    public void setGaranteTipoDocumento(String garanteTipoDocumento) {
        this.garanteTipoDocumento = garanteTipoDocumento;
    }

    public String getGaranteNumeroDocumento() {
        return garanteNumeroDocumento;
    }

    public void setGaranteNumeroDocumento(String garanteNumeroDocumento) {
        this.garanteNumeroDocumento = garanteNumeroDocumento;
    }

    public String getGaranteApellidoPaterno() {
        return garanteApellidoPaterno;
    }

    public void setGaranteApellidoPaterno(String garanteApellidoPaterno) {
        this.garanteApellidoPaterno = garanteApellidoPaterno;
    }

    public String getGaranteApellidoMaterno() {
        return garanteApellidoMaterno;
    }

    public void setGaranteApellidoMaterno(String garanteApellidoMaterno) {
        this.garanteApellidoMaterno = garanteApellidoMaterno;
    }

    public String getGaranteNombres() {
        return garanteNombres;
    }

    public void setGaranteNombres(String garanteNombres) {
        this.garanteNombres = garanteNombres;
    }

    public String getGaranteFechaNacimiento() {
        return garanteFechaNacimiento;
    }

    public void setGaranteFechaNacimiento(String garanteFechaNacimiento) {
        this.garanteFechaNacimiento = garanteFechaNacimiento;
    }

    public String getGaranteGenero() {
        return garanteGenero;
    }

    public void setGaranteGenero(String garanteGenero) {
        this.garanteGenero = garanteGenero;
    }

    public String getGaranteEstadoCivil() {
        return garanteEstadoCivil;
    }

    public void setGaranteEstadoCivil(String garanteEstadoCivil) {
        this.garanteEstadoCivil = garanteEstadoCivil;
    }

    public Integer getGaranteTipo() {
        return garanteTipo;
    }

    public void setGaranteTipo(Integer garanteTipo) {
        this.garanteTipo = garanteTipo;
    }


    public String getGaranteConyugueTipoDocumento() {
        return garanteConyugueTipoDocumento;
    }

    public void setGaranteConyugueTipoDocumento(String garanteConyugueTipoDocumento) {
        this.garanteConyugueTipoDocumento = garanteConyugueTipoDocumento;
    }

    public String getGaranteConyugueNumeroDocumento() {
        return garanteConyugueNumeroDocumento;
    }

    public void setGaranteConyugueNumeroDocumento(String garanteConyugueNumeroDocumento) {
        this.garanteConyugueNumeroDocumento = garanteConyugueNumeroDocumento;
    }

    public String getGaranteConyugueApellidoPaterno() {
        return garanteConyugueApellidoPaterno;
    }

    public void setGaranteConyugueApellidoPaterno(String garanteConyugueApellidoPaterno) {
        this.garanteConyugueApellidoPaterno = garanteConyugueApellidoPaterno;
    }

    public String getGaranteConyugueApellidoMaterno() {
        return garanteConyugueApellidoMaterno;
    }

    public void setGaranteConyugueApellidoMaterno(String garanteConyugueApellidoMaterno) {
        this.garanteConyugueApellidoMaterno = garanteConyugueApellidoMaterno;
    }

    public String getGaranteConyugueNombres() {
        return garanteConyugueNombres;
    }

    public void setGaranteConyugueNombres(String garanteConyugueNombres) {
        this.garanteConyugueNombres = garanteConyugueNombres;
    }

    public String getGaranteConyugueFechaNacmiento() {
        return garanteConyugueFechaNacmiento;
    }

    public void setGaranteConyugueFechaNacmiento(String garanteConyugueFechaNacmiento) {
        this.garanteConyugueFechaNacmiento = garanteConyugueFechaNacmiento;
    }

    public String getGaranteConyugueGenero() {
        return garanteConyugueGenero;
    }

    public void setGaranteConyugueGenero(String garanteConyugueGenero) {
        this.garanteConyugueGenero = garanteConyugueGenero;
    }

    public Date getFechaDespacho() {
        return fechaDespacho;
    }

    public void setFechaDespacho(Date fechaDespacho) {
        this.fechaDespacho = fechaDespacho;
    }

    public List<Documento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<Documento> documentos) {
        this.documentos = documentos;
    }
}
