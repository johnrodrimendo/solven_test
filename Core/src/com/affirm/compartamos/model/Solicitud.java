package com.affirm.compartamos.model;

import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.LoanApplication;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by dev5 on 30/11/17.
 */
public class Solicitud {

    /*TraerVariablesPreEvaluacion*/
    /**********RESPONSE***********/
    @SerializedName("pnCrTrTi")
    private Integer creditoTramiteTitular;
    @SerializedName("pnCrTrCy")
    private Integer creditoTramiteConyugue;
    /****************************/

    /*GeneracionCreditoSolven*/
    /********RESPONSE*********/
    @SerializedName("pnMonSol")
    private Double monto;
    @SerializedName("pcMoneda")
    private String moneda;
    @SerializedName("pnDiaApr")
    private Integer diaPago;
    @SerializedName("pdFecSol")
    private String fechaSolicitud;
    /*************************/

    private transient DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public Solicitud(){}

    /*GeneracionCreditoSolven*/
    /*********REQUEST*********/
    public Solicitud(LoanApplication loanApplication, Credit credit){
        setMonto(credit.getAmount());
        setMoneda("1");
        Calendar cal = Calendar.getInstance();
        cal.setTime(loanApplication.getFirstDueDate());
        setDiaPago(cal.get(Calendar.DAY_OF_MONTH));
        setFechaSolicitud(df.format(loanApplication.getRegisterDate()));
    }
    /**************************/

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public Integer getDiaPago() {
        return diaPago;
    }

    public void setDiaPago(Integer diaPago) {
        this.diaPago = diaPago;
    }

    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(String fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public Integer getCreditoTramiteConyugue() {
        return creditoTramiteConyugue;
    }

    public void setCreditoTramiteConyugue(Integer creditoTramiteConyugue) {
        this.creditoTramiteConyugue = creditoTramiteConyugue;
    }

    public Integer getCreditoTramiteTitular() {
        return creditoTramiteTitular;
    }

    public void setCreditoTramiteTitular(Integer creditoTramiteTitular) {
        this.creditoTramiteTitular = creditoTramiteTitular;
    }
}
