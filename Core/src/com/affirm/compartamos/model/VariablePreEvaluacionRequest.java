package com.affirm.compartamos.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.transactional.Person;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dev5 on 30/11/17.
 */
public class VariablePreEvaluacionRequest {

    /*TraerVariablesPreEvaluacion*/
    @SerializedName("pcNuDoTi")
    private String numeroDocumento;
    @SerializedName("pcTiDoTi")
    private String tipoDocumento;
    @SerializedName("pcNomSol")
    private String nombreCompleto;
    @SerializedName("pcNuDoCy")
    private String numeroDocumentoConyugue;
    @SerializedName("pcTiDoCy")
    private String tipoDocumentoConyugue;
    /****************************/

    public VariablePreEvaluacionRequest(Person person, TranslatorDAO translatorDAO) throws Exception{
        setNumeroDocumento(person.getDocumentNumber());
        setTipoDocumento(translatorDAO.translate(Entity.COMPARTAMOS, 1, person.getDocumentType().getId().toString(), null));
        if(person.getFirstSurname() != null && !person.getFirstSurname().isEmpty() && person.getLastSurname() != null && !person.getLastSurname().isEmpty() && person.getName() != null && !person.getName().isEmpty())
            setNombreCompleto(person.getFirstSurname().concat("/").concat(person.getLastSurname()).concat(",").concat(person.getName()));
        else
            setNombreCompleto("");
        if(person.getPartner() != null){
            setNumeroDocumentoConyugue(person.getPartner().getDocumentNumber());
            setTipoDocumentoConyugue(translatorDAO.translate(Entity.COMPARTAMOS, 1, person.getPartner().getDocumentType().getId().toString(), null));
        }
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumentoConyugue() {
        return numeroDocumentoConyugue;
    }

    public void setNumeroDocumentoConyugue(String numeroDocumentoConyugue) { this.numeroDocumentoConyugue = numeroDocumentoConyugue; }

    public String getTipoDocumentoConyugue() {
        return tipoDocumentoConyugue;
    }

    public void setTipoDocumentoConyugue(String tipoDocumentoConyugue) { this.tipoDocumentoConyugue = tipoDocumentoConyugue; }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
}
