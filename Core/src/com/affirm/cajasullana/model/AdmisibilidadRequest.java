package com.affirm.cajasullana.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.transactional.Person;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dev5 on 21/02/18.
 */
public class AdmisibilidadRequest {

    public static final String OPERATION_ID = "354";

    @SerializedName("tipDocumentoTitular")
    private String tipoDocumento;
    @SerializedName("numDocumentoTitular")
    private String numeroDocumento;
    @SerializedName("ID_OPER")
    private String operationID;

    public AdmisibilidadRequest(TranslatorDAO translatorDAO, Person person) throws Exception{
        setTipoDocumento(translatorDAO.translate(Entity.CAJASULLANA, 1, person.getDocumentType().getId().toString(), null));
        setNumeroDocumento(person.getDocumentNumber());
        setOperationID(OPERATION_ID);
    }

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

    public String getOperationID() {
        return operationID;
    }

    public void setOperationID(String operationID) {
        this.operationID = operationID;
    }
}
