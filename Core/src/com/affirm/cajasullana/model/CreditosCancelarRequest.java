package com.affirm.cajasullana.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.transactional.Person;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dev5 on 22/02/18.
 */
public class CreditosCancelarRequest {

    private static final String OPERATION_ID = "340";
    //private static final String PRODUCT_ID = "215"; //ToDo por definir en Caja Sullana
    public static final String PRODUCT_ID = "234"; //Para ambiente de calidad

    @SerializedName("claveProducto")
    private String claveProducto;
    @SerializedName("tipDocumentoTitular")
    private String tipoDocumentoTitular;
    @SerializedName("numDocumentoTitular")
    private String numeroDocumentoTitular;
    @SerializedName("ID_OPER")
    private String operationID;

    public CreditosCancelarRequest(TranslatorDAO translatorDAO, Person person) throws Exception{
        setClaveProducto(PRODUCT_ID);
        setTipoDocumentoTitular(translatorDAO.translate(Entity.CAJASULLANA, 1, person.getDocumentType().getId().toString(), null));
        setNumeroDocumentoTitular(person.getDocumentNumber());
        setOperationID(OPERATION_ID);
    }

    public static String getOperationId() {
        return OPERATION_ID;
    }

    public static String getProductId() {
        return PRODUCT_ID;
    }

    public String getClaveProducto() {
        return claveProducto;
    }

    public void setClaveProducto(String claveProducto) {
        this.claveProducto = claveProducto;
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

    public String getOperationID() {
        return operationID;
    }

    public void setOperationID(String operationID) {
        this.operationID = operationID;
    }
}
