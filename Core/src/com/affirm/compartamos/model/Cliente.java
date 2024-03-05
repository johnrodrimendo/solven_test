package com.affirm.compartamos.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.catalog.ActivityType;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.transactional.*;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by dev5 on 29/11/17.
 */
public class Cliente{

    /*TraerVariablesPreEvaluacion*/
    /**********RESPONSE***********/
    @SerializedName("pcCauBdn")
    private String causaBaseNegativa;
    @SerializedName("plCliBdn")
    private Boolean baseNegativa;
    @SerializedName("pnPorPar")
    private Double porcentajeParticipacion;
    @SerializedName("plVincul")
    private Boolean indicadorVinculados;
    @SerializedName("plTraCmp")
    private Boolean indicadorTrabajador;
    /****************************/

    /*ConsultarVariablesEvaluacion*/
    @SerializedName("pcNuDoCi")
    private String numeroDocumento;
    @SerializedName("pcApePat")
    private String apellidoPaterno;
    @SerializedName("pcPerSbs")
    private String clasePersonaSBS;
    @SerializedName("pcDoCiCy")
    private String numeroDocumentoConyugue;
    @SerializedName("pcApeCny")
    private String apellidoPaternoConyugue;
    @SerializedName("pcPerCny")
    private String clasePersonaSBSConyugue;
    /******************************/


    /*TraerVariablesEvaluacion*/
    /********RESPONSE**********/
    @SerializedName("pcClaCli")
    private String score;
    @SerializedName("pnIngNet")
    private Double ingresoNeto;
    @SerializedName("pnPunExp")
    private Integer experian;
    /*************************/

    /*GeneracionCreditoSolven*/
    @SerializedName("pcTipOpe")
    private String tipoOperacion;
    @SerializedName("pcClaPer")
    private String clasePersona;
    @SerializedName("pcNomCli")
    private String nombreCompleto;
    @SerializedName("pcTiDoCi")
    private String tipoDocumento;
    @SerializedName("pdNacimi")
    private String fechaNacimiento;
    @SerializedName("pcSexo")
    private String sexo;
    @SerializedName("pcNumTel")
    private String telefono;
    @SerializedName("pcDirCoe")
    private String email;
    @SerializedName("pcCodPro")
    private String ocupacion;
    @SerializedName("pnIngBru")
    private Double ingresoBruto;
    @SerializedName("pcEstCiv")
    private String estadoCivil;
    @SerializedName("pcTiDoTr")
    private String tipoDocumentoTributario;
    @SerializedName("pcNuDoTr")
    private String numeroDocumentoTributario;
    @SerializedName("pcTelDom")
    private String telefonoDomicilio;
    @SerializedName("pcLugNac")
    private String lugarNacimiento;
    @SerializedName("plCarFam")
    private Boolean cargaFamiliar;
    @SerializedName("plUsoDat")
    private Boolean usoDatos;
    @SerializedName("pcTipTel")
    private String tipoTelefono;
    @SerializedName("plVenCel")
    private Boolean ventasOfertas;
    @SerializedName("pdFecSbs")
    private String fechaRCC;
    @SerializedName("plCueDig")
    private Boolean cuentaDigital;
    @SerializedName("plAutMsj")
    private Boolean textoLlamada;
    @SerializedName("pnPatrim")
    private Double patrimonio;
    @SerializedName("pcIndNac")
    private String nacionalidad;
    /*************************/

    /*GeneracionCreditoSolven*/
    /********RESPONSE*********/
    @SerializedName("pcCodCli")
    private String codigoCliente;

    private Cliente conyugue;
    private transient DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public Cliente(){}

    /*ConsultarVariablesEvaluacion*/
    /***********REQUEST************/
    public Cliente(Person person){
        setNumeroDocumento(person.getDocumentNumber());
        setApellidoPaterno(person.getFirstSurname());
        setClasePersonaSBS("1");
        if(person.getPartner() != null){
            setNumeroDocumentoConyugue(person.getPartner().getDocumentNumber());
            setApellidoPaternoConyugue(person.getPartner().getFirstSurname());
            setClasePersonaSBSConyugue("1");
        }
    }
    /*****************************/

    /*GeneracionCreditoSolven*/
    /*********REQUEST*********/
    public Cliente(String tipoOperacion,
                   Person person,
                   User user,
                   PersonOcupationalInformation personOcupationalInformation,
                   PersonContactInformation personContactInformation,
                   List<DisggregatedAddress> disggregatedAddressList,
                   Date fechaRCC,
                   ExperianResult experianResult,
                   SunatResult sunatResult,
                   TranslatorDAO translatorDAO) throws Exception{

        setTipoOperacion(tipoOperacion);
        setClasePersona("2"); //Siempre vamos a utilizar persona natural sin negocio
        setNombreCompleto(person.getFirstSurname() + "/" + person.getLastSurname() + "," + person.getName());
        setTipoDocumento(translatorDAO.translate(Entity.COMPARTAMOS, 1, person.getDocumentType().getId().toString(), null));
        setNumeroDocumento(person.getDocumentNumber());
        setFechaNacimiento(df.format(person.getBirthday()));
        setSexo(translatorDAO.translate(Entity.COMPARTAMOS, 2, person.getGender().toString(), null));
        setTelefono(user.getPhoneNumber());
        setEmail(user.getEmail());
        setOcupacion(translatorDAO.translate(Entity.COMPARTAMOS, 3, person.getProfession().getId().toString(), null));
        setIngresoBruto(personOcupationalInformation.getFixedGrossIncome());
        setEstadoCivil(translatorDAO.translate(Entity.COMPARTAMOS, 4, person.getMaritalStatus().getId().toString(), null));

        if(personOcupationalInformation.getActivityType().getId().equals(ActivityType.INDEPENDENT)){
            setTipoDocumentoTributario("3");
            setNumeroDocumentoTributario(sunatResult.getRuc());
        } else {
            setTipoDocumentoTributario("0");
        }

        setTelefonoDomicilio(user.getPhoneNumber());
        setCargaFamiliar(person.getDependents() != null && person.getDependents() > 0);
        setUsoDatos(true);
        setTipoTelefono(translatorDAO.translate(Entity.COMPARTAMOS, 12, personContactInformation.getPhoneNumberType(), null));
        setVentasOfertas(true);
        setFechaRCC(df.format(fechaRCC));
        setTextoLlamada(true);
        setNacionalidad(translatorDAO.translate(Entity.COMPARTAMOS, 5, person.getDocumentType().getId().toString(), null));
        String lugarNacimiento = "";
        switch(person.getDocumentType().getId()){
            case IdentityDocumentType.DNI : lugarNacimiento = person.getBirthUbigeo().getUbigeo();break;
            case IdentityDocumentType.CE : lugarNacimiento = "XX"; break;
        }
        setLugarNacimiento(lugarNacimiento);
        setScore(experianResult.getCluster().getCluster()); //ToDo clean when Compartamos accept
    }

    public Cliente(Person person, ExperianResult experianResult, TranslatorDAO translatorDAO) throws Exception{
        setNombreCompleto(person.getFirstSurname() + "/" + person.getLastSurname() + "," + person.getName());
        setTipoDocumento(translatorDAO.translate(Entity.COMPARTAMOS, 1, person.getDocumentType().getId().toString(), null));
        setNumeroDocumento(person.getDocumentNumber());
        setScore(experianResult.getPartnerCluster().getCluster());
    }
    /*************************/


    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Double getIngresoNeto() {
        return ingresoNeto;
    }

    public void setIngresoNeto(Double ingresoNeto) {
        this.ingresoNeto = ingresoNeto;
    }

    public Integer getExperian() {
        return experian;
    }

    public void setExperian(Integer experian) {
        this.experian = experian;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getClasePersonaSBS() {
        return clasePersonaSBS;
    }

    public void setClasePersonaSBS(String clasePersonaSBS) {
        this.clasePersonaSBS = clasePersonaSBS;
    }

    public String getClasePersona() {
        return clasePersona;
    }

    public void setClasePersona(String clasePersona) {
        this.clasePersona = clasePersona;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
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

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public Double getIngresoBruto() {
        return ingresoBruto;
    }

    public void setIngresoBruto(Double ingresoBruto) {
        this.ingresoBruto = ingresoBruto;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getTipoDocumentoTributario() {
        return tipoDocumentoTributario;
    }

    public void setTipoDocumentoTributario(String tipoDocumentoTributario) {
        this.tipoDocumentoTributario = tipoDocumentoTributario;
    }

    public String getNumeroDocumentoTributario() {
        return numeroDocumentoTributario;
    }

    public void setNumeroDocumentoTributario(String numeroDocumentoTributario) {
        this.numeroDocumentoTributario = numeroDocumentoTributario;
    }

    public String getCausaBaseNegativa() {
        return causaBaseNegativa;
    }

    public void setCausaBaseNegativa(String causaBaseNegativa) {
        this.causaBaseNegativa = causaBaseNegativa;
    }

    public Boolean getBaseNegativa() {
        return baseNegativa;
    }

    public void setBaseNegativa(Boolean baseNegativa) {
        this.baseNegativa = baseNegativa;
    }

    public Double getPorcentajeParticipacion() {
        return porcentajeParticipacion;
    }

    public void setPorcentajeParticipacion(Double porcentajeParticipacion) {
        this.porcentajeParticipacion = porcentajeParticipacion;
    }

    public Boolean getIndicadorVinculados() {
        return indicadorVinculados;
    }

    public void setIndicadorVinculados(Boolean indicadorVinculados) {
        this.indicadorVinculados = indicadorVinculados;
    }

    public Boolean getIndicadorTrabajador() {
        return indicadorTrabajador;
    }

    public void setIndicadorTrabajador(Boolean indicadorTrabajador) {
        this.indicadorTrabajador = indicadorTrabajador;
    }

    public String getTelefonoDomicilio() {
        return telefonoDomicilio;
    }

    public void setTelefonoDomicilio(String telefonoDomicilio) {
        this.telefonoDomicilio = telefonoDomicilio;
    }

    public String getLugarNacimiento() {
        return lugarNacimiento;
    }

    public void setLugarNacimiento(String lugarNacimiento) {
        this.lugarNacimiento = lugarNacimiento;
    }

    public Boolean getCargaFamiliar() {
        return cargaFamiliar;
    }

    public void setCargaFamiliar(Boolean cargaFamiliar) {
        this.cargaFamiliar = cargaFamiliar;
    }

    public Boolean getUsoDatos() {
        return usoDatos;
    }

    public void setUsoDatos(Boolean usoDatos) {
        this.usoDatos = usoDatos;
    }

    public String getTipoTelefono() {
        return tipoTelefono;
    }

    public void setTipoTelefono(String tipoTelefono) {
        this.tipoTelefono = tipoTelefono;
    }

    public Boolean getVentasOfertas() {
        return ventasOfertas;
    }

    public void setVentasOfertas(Boolean ventasOfertas) {
        this.ventasOfertas = ventasOfertas;
    }

    public String getFechaRCC() {
        return fechaRCC;
    }

    public void setFechaRCC(String fechaRCC) {
        this.fechaRCC = fechaRCC;
    }

    public Boolean getCuentaDigital() {
        return cuentaDigital;
    }

    public void setCuentaDigital(Boolean cuentaDigital) {
        this.cuentaDigital = cuentaDigital;
    }

    public Boolean getTextoLlamada() {
        return textoLlamada;
    }

    public void setTextoLlamada(Boolean textoLlamada) {
        this.textoLlamada = textoLlamada;
    }

    public Double getPatrimonio() {
        return patrimonio;
    }

    public void setPatrimonio(Double patrimonio) {
        this.patrimonio = patrimonio;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getNumeroDocumento() { return numeroDocumento; }

    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNumeroDocumentoConyugue() {
        return numeroDocumentoConyugue;
    }

    public void setNumeroDocumentoConyugue(String numeroDocumentoConyugue) {
        this.numeroDocumentoConyugue = numeroDocumentoConyugue;
    }

    public String getApellidoPaternoConyugue() {
        return apellidoPaternoConyugue;
    }

    public void setApellidoPaternoConyugue(String apellidoPaternoConyugue) {
        this.apellidoPaternoConyugue = apellidoPaternoConyugue;
    }

    public String getClasePersonaSBSConyugue() {
        return clasePersonaSBSConyugue;
    }

    public void setClasePersonaSBSConyugue(String clasePersonaSBSConyugue) {
        this.clasePersonaSBSConyugue = clasePersonaSBSConyugue;
    }

    /*************************/
    public Cliente getConyugue() {
        return conyugue;
    }

    public void setConyugue(Cliente conyugue) {
        this.conyugue = conyugue;
    }

    /*********REQUEST*********/
    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }
}
