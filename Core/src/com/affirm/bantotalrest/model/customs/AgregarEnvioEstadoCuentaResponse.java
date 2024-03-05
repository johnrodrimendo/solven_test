package com.affirm.bantotalrest.model.customs;

import com.affirm.bantotalrest.model.common.BTResponseData;

public class AgregarEnvioEstadoCuentaResponse extends BTResponseData {

    private String EnvioEC;
    private String ModoEnvioEC;
    private String ModoEnvioDesc;

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
