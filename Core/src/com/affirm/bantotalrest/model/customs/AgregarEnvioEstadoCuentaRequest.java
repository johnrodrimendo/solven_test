package com.affirm.bantotalrest.model.customs;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class AgregarEnvioEstadoCuentaRequest extends BtRequestData {

    private Long OperacionUId;
    private String EnvioEC;
    private String ModoEnvioEC;
    private String ModoEnvioDesc;

    public Long getOperacionUId() {
        return OperacionUId;
    }

    public void setOperacionUId(Long operacionUId) {
        OperacionUId = operacionUId;
    }

    public String getEnvioEC() {
        return EnvioEC;
    }

    public void setEnvioEC(String envioEC) {
        EnvioEC = envioEC;
    }

    public String getModoEnvioEC() {
        return ModoEnvioEC;
    }

    public void setModoEnvioEC(String modoEnvioEC) {
        ModoEnvioEC = modoEnvioEC;
    }

    public String getModoEnvioDesc() {
        return ModoEnvioDesc;
    }

    public void setModoEnvioDesc(String modoEnvioDesc) {
        ModoEnvioDesc = modoEnvioDesc;
    }
}
