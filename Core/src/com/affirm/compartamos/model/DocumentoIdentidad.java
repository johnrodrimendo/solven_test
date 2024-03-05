package com.affirm.compartamos.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.transactional.Person;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dev5 on 29/11/17.
 */
public class DocumentoIdentidad {

    /*TraerVariablesEvaluacion*/
    /**********REQUEST*********/
    @SerializedName("pcTiDoTi")
    private String tipoDocumento;
    @SerializedName("pcNuDoCi")
    private String numeroDocumento;
    @SerializedName("pcTiDoCy")
    private String tipoDocumentoConyugue;
    @SerializedName("pcNuDoCy")
    private String numeroDocumentoConyugue;
    /****************************/

    public DocumentoIdentidad(String tipoDocumento, String numeroDocumento){
        setTipoDocumento(tipoDocumento);
        setNumeroDocumento(numeroDocumento);
    }

    /*TraerVariablesEvaluacion*/
    /**********REQUEST*********/
    public DocumentoIdentidad(Person person, TranslatorDAO translatorDAO) throws Exception{
        setTipoDocumento(translatorDAO.translate(Entity.COMPARTAMOS, 1, person.getDocumentType().getId().toString(), null));
        setNumeroDocumento(person.getDocumentNumber());
        if(person.getPartner() != null){
            setTipoDocumentoConyugue(translatorDAO.translate(Entity.COMPARTAMOS, 1, person.getPartner().getDocumentType().getId().toString(), null));
            setNumeroDocumentoConyugue(person.getPartner().getDocumentNumber());
        }
    }
    /****************************/

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
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
}
