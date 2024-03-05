package com.affirm.cajasullana.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dev5 on 21/02/18.
 */
public class ValidarExperianResponse {

    @SerializedName("score")
    private String score;
    @SerializedName("idEmail")
    private String emailId;
    @SerializedName("plazoMaximo")
    private String plazoMaximo;
    @SerializedName("propuestaFinal")
    private String propuestaFinal;
    @SerializedName("idEvaluacionExperian")
    private String experianId;
    @SerializedName("importeMaximo")
    private String importeMaximo;
    @SerializedName("quanto")
    private String quanto;
    @SerializedName("afforability")
    private String afforability;
    @SerializedName("importe")
    private String importe;
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("email")
    private String email;
    @SerializedName("numeroCelular")
    private String numeroCelular;
    @SerializedName("idCelular")
    private String celularId;


    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPlazoMaximo() {
        return plazoMaximo;
    }

    public void setPlazoMaximo(String plazoMaximo) {
        this.plazoMaximo = plazoMaximo;
    }

    public String getPropuestaFinal() {
        return propuestaFinal;
    }

    public void setPropuestaFinal(String propuestaFinal) {
        this.propuestaFinal = propuestaFinal;
    }

    public String getExperianId() {
        return experianId;
    }

    public void setExperianId(String experianId) {
        this.experianId = experianId;
    }

    public String getImporteMaximo() {
        return importeMaximo;
    }

    public void setImporteMaximo(String importeMaximo) {
        this.importeMaximo = importeMaximo;
    }

    public String getQuanto() {
        return quanto;
    }

    public void setQuanto(String quanto) {
        this.quanto = quanto;
    }

    public String getAfforability() {
        return afforability;
    }

    public void setAfforability(String afforability) {
        this.afforability = afforability;
    }

    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumeroCelular() {
        return numeroCelular;
    }

    public void setNumeroCelular(String numeroCelular) {
        this.numeroCelular = numeroCelular;
    }

    public String getCelularId() {
        return celularId;
    }

    public void setCelularId(String celularId) {
        this.celularId = celularId;
    }
}
