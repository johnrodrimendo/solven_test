package com.affirm.cajasullana.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.catalog.Department;
import com.affirm.common.model.catalog.District;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.Province;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.PersonContactInformation;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.service.CatalogService;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dev5 on 23/02/18.
 */
public class    GenerarSolicitudRequest {

    private static final String OPERATION_ID = "391";

    @SerializedName("codigoCliente")
    private String codigoCliente;
    @SerializedName("tipDocumentoTitular")
    private String tipoDocumentoTitular;
    @SerializedName("numDocumentoTitular")
    private String numeroDocumentoTitular;
    @SerializedName("idEvaluacionExperian")
    private String evaluacionExperianId;
    @SerializedName("importe")
    private String monto;
    @SerializedName("tea")
    private String tea;
    @SerializedName("plazo")
    private String plazo;
    @SerializedName("cuotaPropuesta")
    private String cuotaPropuesta;
    @SerializedName("sexo")
    private String genero;
    @SerializedName("direccionLegal")
    private String direccionLegal;
    @SerializedName("codigoPais")
    private String codigoPais;
    @SerializedName("departamentoLegal")
    private String departamentoLegal;
    @SerializedName("provinciaLegal")
    private String provinciaLegal;
    @SerializedName("distritoLegal")
    private String distritoLegal;
    @SerializedName("idEmail")
    private String emailId;
    @SerializedName("email")
    private String email;
    @SerializedName("idCelular")
    private String phoneId;
    @SerializedName("numeroCelular")
    private String numeroCelular;
    @SerializedName("tipoModalidad")
    private String tipoModalidad;
    @SerializedName("listaCreditosCancelacion")
    private String listaCreditosCancelacion;
    @SerializedName("idOcupacion")
    private String ocupacion;
    @SerializedName("idProfesion")
    private String profesion;
    @SerializedName("idDepTrabajo")
    private String trabajoDepartamento;
    @SerializedName("idProvTrabajo")
    private String trabajoProvincia;
    @SerializedName("idDistTrabajo")
    private String trabajoDistrito;
    @SerializedName("rucEmpresa")
    private String empresaRUC;
    @SerializedName("nombreEmpresa")
    private String empresaNombre;
    @SerializedName("ID_OPER")
    private String operationID;

    public GenerarSolicitudRequest(TranslatorDAO translatorDAO, CatalogService catalogService, Person person, String personEntityCode, String experianId, Credit credit, PersonContactInformation personContactInformation, PersonOcupationalInformation personOcupationalInformation, List<CreditoCancelar> creditoCancelarList, boolean newClient, String emailId, String phoneNumberId) throws Exception{
        setCodigoCliente(personEntityCode);
        setTipoDocumentoTitular(translatorDAO.translate(Entity.CAJASULLANA, 1, person.getDocumentType().getId().toString(), null));
        setNumeroDocumentoTitular(person.getDocumentNumber());
        setEvaluacionExperianId(experianId);
        setMonto(credit.getAmount().toString());
        setTea(credit.getEffectiveAnnualRate().toString());
        setPlazo(credit.getInstallments().toString());
        setCuotaPropuesta(credit.getInstallmentAmountAvg().toString());
        setGenero(translatorDAO.translate(Entity.CAJASULLANA, 6, person.getGender().toString(), null));
        setDireccionLegal(personContactInformation.getFullAddressBO());
        setCodigoPais("10"); //Peru

        Department department = catalogService.getDepartments().stream().filter(e->e.getId().equals(personContactInformation.getAddressUbigeo().getDepartment().getId())).findFirst().orElse(null);
        if(department != null){
            setDepartamentoLegal(department.getIneiId().replaceFirst("^0+(?!$)", ""));
            Province province = department.getProvinces().entrySet().stream().filter(e->e.getValue().getId().equals(personContactInformation.getAddressUbigeo().getProvince().getId())).map(e -> e.getValue()).findFirst().orElse(null);
            setProvinciaLegal(province.getIneiId().replaceFirst("^0+(?!$)", ""));
            District district = province.getDistricts().entrySet().stream().filter(e->e.getValue().getId().equals(personContactInformation.getAddressUbigeo().getDistrict().getId())).map(e -> e.getValue()).findFirst().orElse(null);
            setDistritoLegal(district.getIneiId().replaceFirst("^0+(?!$)", ""));
        }else{
            setDepartamentoLegal("");
            setProvinciaLegal("");
            setDistritoLegal("");
        }

        setEmailId(emailId);
        setEmail(personContactInformation.getEmail());
        setPhoneId(phoneNumberId);
        setNumeroCelular(personContactInformation.getPhoneNumber());
        setTipoModalidad(newClient ? "0" : "3");
        setListaCreditosCancelacion("");

        //ToDo DO NOT ERASE - Will be use in Caja Sullana v2.0
        /*if(creditoCancelarList != null && creditoCancelarList.size() > 0 && personEntityCode != null && !personEntityCode.isEmpty()){
            setTipoModalidad("7"); // Recurrente con Saldo.
            setListaCreditosCancelacion(creditoCancelarList.toString());
        }else if(creditoCancelarList != null && creditoCancelarList.size() == 0 && personEntityCode != null && !personEntityCode.isEmpty()){
            setTipoModalidad("3"); //Recurrente sin Saldo.
        }else if(personEntityCode == null || personEntityCode.isEmpty()){
            setTipoModalidad("0"); //Nuevo
        }else{
            setTipoModalidad("0"); //Nuevo
            setListaCreditosCancelacion("");
        }*/

        if(personOcupationalInformation != null){
            setOcupacion(translatorDAO.translate(Entity.CAJASULLANA, 5, personOcupationalInformation.getOcupation().getId().toString(), null));
            setEmpresaRUC(personOcupationalInformation.getRuc());
            setEmpresaNombre(personOcupationalInformation.getCompanyName() != null && !personOcupationalInformation.getCompanyName().isEmpty() ? personOcupationalInformation.getCompanyName() : "");
        }else{
            setOcupacion("");
            setEmpresaRUC("");
            setEmpresaNombre("");
        }

        if(person.getProfession() != null) {
            setProfesion(translatorDAO.translate(Entity.CAJASULLANA, 4, person.getProfession().getId().toString(), null));
        }else{
            setProfesion("");
        }

        Department departmentOf = catalogService.getDepartments().stream().filter(e->e.getId().equals(personOcupationalInformation.getAddressUbigeo().getDepartment().getId())).findFirst().orElse(null);
        if(departmentOf != null){
            setTrabajoDepartamento(departmentOf.getIneiId().replaceFirst("^0+(?!$)", ""));
            Province province = departmentOf.getProvinces().entrySet().stream().filter(e->e.getValue().getId().equals(personOcupationalInformation.getAddressUbigeo().getProvince().getId())).map(e -> e.getValue()).findFirst().orElse(null);
            setTrabajoProvincia(province.getIneiId().replaceFirst("^0+(?!$)", ""));
            District district = province.getDistricts().entrySet().stream().filter(e->e.getValue().getId().equals(personOcupationalInformation.getAddressUbigeo().getDistrict().getId())).map(e -> e.getValue()).findFirst().orElse(null);
            setTrabajoDistrito(district.getIneiId().replaceFirst("^0+(?!$)", ""));
        }else{
            setTrabajoDepartamento("");
            setTrabajoProvincia("");
            setTrabajoDistrito("");
        }

        setOperationID(OPERATION_ID);
    }


    public static String getOperationId() {
        return OPERATION_ID;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getTipoDocumentoTitular() {
        return tipoDocumentoTitular;
    }

    public void setTipoDocumentoTitular(String tipoDocumentoTitular) {
        this.tipoDocumentoTitular = tipoDocumentoTitular;
    }

    public String getNumeroDocumentoTitular() {
        return numeroDocumentoTitular;
    }

    public void setNumeroDocumentoTitular(String numeroDocumentoTitular) {
        this.numeroDocumentoTitular = numeroDocumentoTitular;
    }

    public String getEvaluacionExperianId() {
        return evaluacionExperianId;
    }

    public void setEvaluacionExperianId(String evaluacionExperianId) {
        this.evaluacionExperianId = evaluacionExperianId;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getTea() {
        return tea;
    }

    public void setTea(String tea) {
        this.tea = tea;
    }

    public String getPlazo() {
        return plazo;
    }

    public void setPlazo(String plazo) {
        this.plazo = plazo;
    }

    public String getCuotaPropuesta() {
        return cuotaPropuesta;
    }

    public void setCuotaPropuesta(String cuotaPropuesta) {
        this.cuotaPropuesta = cuotaPropuesta;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDireccionLegal() {
        return direccionLegal;
    }

    public void setDireccionLegal(String direccionLegal) {
        this.direccionLegal = direccionLegal;
    }

    public String getCodigoPais() {
        return codigoPais;
    }

    public void setCodigoPais(String codigoPais) {
        this.codigoPais = codigoPais;
    }

    public String getDepartamentoLegal() {
        return departamentoLegal;
    }

    public void setDepartamentoLegal(String departamentoLegal) {
        this.departamentoLegal = departamentoLegal;
    }

    public String getProvinciaLegal() {
        return provinciaLegal;
    }

    public void setProvinciaLegal(String provinciaLegal) {
        this.provinciaLegal = provinciaLegal;
    }

    public String getDistritoLegal() {
        return distritoLegal;
    }

    public void setDistritoLegal(String distritoLegal) {
        this.distritoLegal = distritoLegal;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    public String getNumeroCelular() {
        return numeroCelular;
    }

    public void setNumeroCelular(String numeroCelular) {
        this.numeroCelular = numeroCelular;
    }

    public String getTipoModalidad() {
        return tipoModalidad;
    }

    public void setTipoModalidad(String tipoModalidad) {
        this.tipoModalidad = tipoModalidad;
    }

    public String getListaCreditosCancelacion() {
        return listaCreditosCancelacion;
    }

    public void setListaCreditosCancelacion(String listaCreditosCancelacion) {
        this.listaCreditosCancelacion = listaCreditosCancelacion;
    }

    public String getOperationID() {
        return operationID;
    }

    public void setOperationID(String operationID) {
        this.operationID = operationID;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public String getProfesion() {
        return profesion;
    }

    public void setProfesion(String profesion) {
        this.profesion = profesion;
    }

    public String getTrabajoDepartamento() {
        return trabajoDepartamento;
    }

    public void setTrabajoDepartamento(String trabajoDepartamento) {
        this.trabajoDepartamento = trabajoDepartamento;
    }

    public String getTrabajoProvincia() {
        return trabajoProvincia;
    }

    public void setTrabajoProvincia(String trabajoProvincia) {
        this.trabajoProvincia = trabajoProvincia;
    }

    public String getTrabajoDistrito() {
        return trabajoDistrito;
    }

    public void setTrabajoDistrito(String trabajoDistrito) {
        this.trabajoDistrito = trabajoDistrito;
    }

    public String getEmpresaRUC() {
        return empresaRUC;
    }

    public void setEmpresaRUC(String empresaRUC) {
        this.empresaRUC = empresaRUC;
    }

    public String getEmpresaNombre() {
        return empresaNombre;
    }

    public void setEmpresaNombre(String empresaNombre) {
        this.empresaNombre = empresaNombre;
    }
}
