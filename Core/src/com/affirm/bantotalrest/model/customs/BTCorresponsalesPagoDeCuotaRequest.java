package com.affirm.bantotalrest.model.customs;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class BTCorresponsalesPagoDeCuotaRequest extends BtRequestData {

    public static final int TIPO_MOVIMIENTO_DEFAULT = 1;

    private String Canal;
    private Integer TipoMov;
    private Long Credito;
    private String FecPago;
    private String HoraPago;
    private String RefUnica;
    private Integer IdTerminal;
    private Integer MdaPago;
    private Double ImpPago;
    private Double ImpCom;
    private Double ImpRed;

    public String getCanal() {
        return Canal;
    }

    public void setCanal(String canal) {
        Canal = canal;
    }

    public Integer getTipoMov() {
        return TipoMov;
    }

    public void setTipoMov(Integer tipoMov) {
        TipoMov = tipoMov;
    }

    public Long getCredito() {
        return Credito;
    }

    public void setCredito(Long credito) {
        Credito = credito;
    }

    public String getFecPago() {
        return FecPago;
    }

    public void setFecPago(String fecPago) {
        FecPago = fecPago;
    }

    public String getHoraPago() {
        return HoraPago;
    }

    public void setHoraPago(String horaPago) {
        HoraPago = horaPago;
    }

    public String getRefUnica() {
        return RefUnica;
    }

    public void setRefUnica(String refUnica) {
        RefUnica = refUnica;
    }

    public Integer getIdTerminal() {
        return IdTerminal;
    }

    public void setIdTerminal(Integer idTerminal) {
        IdTerminal = idTerminal;
    }

    public Integer getMdaPago() {
        return MdaPago;
    }

    public void setMdaPago(Integer mdaPago) {
        MdaPago = mdaPago;
    }

    public Double getImpPago() {
        return ImpPago;
    }

    public void setImpPago(Double impPago) {
        ImpPago = impPago;
    }

    public Double getImpCom() {
        return ImpCom;
    }

    public void setImpCom(Double impCom) {
        ImpCom = impCom;
    }

    public Double getImpRed() {
        return ImpRed;
    }

    public void setImpRed(Double impRed) {
        ImpRed = impRed;
    }
}
