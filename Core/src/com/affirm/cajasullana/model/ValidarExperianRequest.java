package com.affirm.cajasullana.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.catalog.ActivityType;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.SubActivityType;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dev5 on 21/02/18.
 */
public class ValidarExperianRequest {

    public static final String OPERATION_ID = "341";
    //public static final String PRODUCT_ID = "215"; //ToDo por definir en Caja Sullana
    public static final String PRODUCT_ID = "234"; //Para ambiente de calidad

    @SerializedName("codigoCliente")
    private String codigoCliente;
    @SerializedName("tipDocumentoTitular")
    private String tipoDocumentoTitular;
    @SerializedName("numDocumentoTitular")
    private String numeroDocumentoTitular;
    @SerializedName("nombreTitular")
    private String nombreTitular;
    @SerializedName("apellidoPaternoTitular")
    private String apellidoPaternoTitular;
    @SerializedName("apellidoMaternoTitular")
    private String apellidoMaternoTitular;
    @SerializedName("tipoPersoneria")
    private String tipoPersona;
    @SerializedName("estadoCivil")
    private String estadoCivil;
    @SerializedName("tipDocumentoConyuge")
    private String tipoDocumentoConyugue;
    @SerializedName("numDocumentoConyuge")
    private String numeroDocumentoConyugue;
    @SerializedName("nombreConyuge")
    private String nombreConyugue;
    @SerializedName("apellidoPaternoConyuge")
    private String apellidoPaternoConyugue;
    @SerializedName("apellidoMaternoConyuge")
    private String apellidoMaternoConyugue;
    @SerializedName("claveProducto")
    private String claveProducto;
    @SerializedName("tipoRenta")
    private String tipoRenta;
    @SerializedName("importe")
    private String monto;
    @SerializedName("ingresoMensual")
    private String ingresoMensual;
    @SerializedName("ID_OPER")
    private String operationID;

    public ValidarExperianRequest(TranslatorDAO translatorDAO, Person person, String personEntityCode, LoanApplication loanApplication, PersonOcupationalInformation personOcupationalInformation) throws Exception{
        setCodigoCliente(personEntityCode);
        setTipoDocumentoTitular(translatorDAO.translate(Entity.CAJASULLANA, 1, person.getDocumentType().getId().toString(), null));
        setNumeroDocumentoTitular(person.getDocumentNumber());
        setNombreTitular(person.getName());
        setApellidoPaternoTitular(person.getFirstSurname());
        setApellidoMaternoTitular(person.getLastSurname());
        setTipoPersona("N");
        setEstadoCivil(translatorDAO.translate(Entity.CAJASULLANA, 2,person.getMaritalStatus().getId().toString(), null));
        if(person.getPartner() != null){
            setTipoDocumentoConyugue(translatorDAO.translate(Entity.CAJASULLANA, 1, person.getPartner().getDocumentType().getId().toString(), null));
            setNumeroDocumentoConyugue(person.getPartner().getDocumentNumber());
            setNombreConyugue(person.getPartner().getName());
            setApellidoPaternoConyugue(person.getPartner().getFirstSurname());
            setApellidoMaternoConyugue(person.getPartner().getLastSurname());
        }else{
            setTipoDocumentoConyugue("");
            setNumeroDocumentoConyugue("");
            setNombreConyugue("");
            setApellidoPaternoConyugue("");
            setApellidoMaternoConyugue("");
        }
        setClaveProducto(PRODUCT_ID);
        if(personOcupationalInformation.getActivityType().getId().equals(ActivityType.DEPENDENT)) setTipoRenta("3");
        else if(personOcupationalInformation.getActivityType().getId().equals(ActivityType.INDEPENDENT)){
            if(personOcupationalInformation.getSubActivityType().getId().equals(SubActivityType.PROFESSIONAL_SERVICE))
                setTipoRenta("2");
        }
        setMonto(loanApplication.getAmount().toString());
        setIngresoMensual(personOcupationalInformation.getFixedGrossIncome().toString());
        setOperationID(OPERATION_ID);
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

    public String getApellidoPaternoTitular() {
        return apellidoPaternoTitular;
    }

    public void setApellidoPaternoTitular(String apellidoPaternoTitular) {
        this.apellidoPaternoTitular = apellidoPaternoTitular;
    }

    public String getApellidoMaternoTitular() {
        return apellidoMaternoTitular;
    }

    public void setApellidoMaternoTitular(String apellidoMaternoTitular) {
        this.apellidoMaternoTitular = apellidoMaternoTitular;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getTipoDocumentoConyugue() {
        return tipoDocumentoConyugue;
    }

    public void setTipoDocumentoConyugue(String tipoDocumentoConyugue) {
        this.tipoDocumentoConyugue = tipoDocumentoConyugue;
    }

    public String getNumeroDocumentoConyugue() {
        return numeroDocumentoConyugue;
    }

    public void setNumeroDocumentoConyugue(String numeroDocumentoConyugue) {
        this.numeroDocumentoConyugue = numeroDocumentoConyugue;
    }

    public String getNombreConyugue() {
        return nombreConyugue;
    }

    public void setNombreConyugue(String nombreConyugue) {
        this.nombreConyugue = nombreConyugue;
    }

    public String getApellidoPaternoConyugue() {
        return apellidoPaternoConyugue;
    }

    public void setApellidoPaternoConyugue(String apellidoPaternoConyugue) {
        this.apellidoPaternoConyugue = apellidoPaternoConyugue;
    }

    public String getApellidoMaternoConyugue() {
        return apellidoMaternoConyugue;
    }

    public void setApellidoMaternoConyugue(String apellidoMaternoConyugue) {
        this.apellidoMaternoConyugue = apellidoMaternoConyugue;
    }

    public String getClaveProducto() {
        return claveProducto;
    }

    public void setClaveProducto(String claveProducto) {
        this.claveProducto = claveProducto;
    }

    public String getTipoRenta() {
        return tipoRenta;
    }

    public void setTipoRenta(String tipoRenta) {
        this.tipoRenta = tipoRenta;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getIngresoMensual() {
        return ingresoMensual;
    }

    public void setIngresoMensual(String ingresoMensual) {
        this.ingresoMensual = ingresoMensual;
    }

    public String getOperationID() {
        return operationID;
    }

    public void setOperationID(String operationID) {
        this.operationID = operationID;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }
}
