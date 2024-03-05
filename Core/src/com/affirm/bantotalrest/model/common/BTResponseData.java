package com.affirm.bantotalrest.model.common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BTResponseData {

    private static final String INVALID_SESSION_CODE = "10011";

    private BtInRequest Btinreq;
    private BtOutRequest Btoutreq;
    private BTErrorNegocio Erroresnegocio;

    public BtInRequest getBtinreq() {
        return Btinreq;
    }

    public void setBtinreq(BtInRequest btinreq) {
        Btinreq = btinreq;
    }

    public BtOutRequest getBtoutreq() {
        return Btoutreq;
    }

    public void setBtoutreq(BtOutRequest btoutreq) {
        Btoutreq = btoutreq;
    }

    public BTErrorNegocio getErroresnegocio() {
        return Erroresnegocio;
    }

    public void setErroresnegocio(BTErrorNegocio erroresnegocio) {
        Erroresnegocio = erroresnegocio;
    }

    public boolean isInvalidSession(){
        if(getErroresnegocio()!= null && getErroresnegocio().getBTErrorNegocio() != null && !getErroresnegocio().getBTErrorNegocio().isEmpty()){
            return getErroresnegocio().getBTErrorNegocio().stream().anyMatch(e -> e.getCodigo() != null && e.getCodigo().equalsIgnoreCase(INVALID_SESSION_CODE));
        }
        return false;
    }

    public boolean isError(){
        return isError(new ArrayList<>());
    }

    public boolean isError(List<String> excludeErrorCodes){
        if(excludeErrorCodes == null) excludeErrorCodes = new ArrayList<>();
        if(getErroresnegocio() != null && getErroresnegocio().getBTErrorNegocio() != null && !getErroresnegocio().getBTErrorNegocio().isEmpty()){
            if(excludeErrorCodes.isEmpty()) return true;
            else{
                return getErroresnegocio().getBTErrorNegocio().stream().filter(e -> e.getCodigo() == null || (e.getCodigo() != null && !getErroresnegocio().getBTErrorNegocio().contains(e.getCodigo()))).collect(Collectors.toList()).isEmpty() ? false : true;
            }
        }
        return false;
    }

    public String getErrorDetail(){
        if(isError()){
            return getErroresnegocio().getBTErrorNegocio().get(0).getDescripcion();
        }
        return null;
    }

    public String getErrorCode(){
        if(isError()){
            return getErroresnegocio().getBTErrorNegocio().get(0).getCodigo();
        }
        return null;
    }



}
