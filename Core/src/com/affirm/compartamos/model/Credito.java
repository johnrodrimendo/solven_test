package com.affirm.compartamos.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanOffer;
import com.affirm.common.model.transactional.PersonBankAccountInformation;
import com.affirm.common.util.JsonUtil;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dev5 on 29/11/17.
 */
public class Credito {

    /*TraerVariablesPreEvaluacion*/
    @SerializedName("pcTipCre")
    private String tipoCredito;
    @SerializedName("pnAtrMax")
    private Integer maximoAtraso;
    @SerializedName("pnCreVig")
    private Integer creditosVigentes;
    @SerializedName("pnCrViCy")
    private Integer creditosVigentesConyugue;
    @SerializedName("pnCrPrTi")
    private Integer creditosPreAprobadosTitular;
    @SerializedName("pnCrPrCy")
    private Integer creditosPreAprobadosConyugue;
    @SerializedName("pnFiaVen")
    private Integer creditosFiadorVencidos;
    @SerializedName("pnFiaJud")
    private Integer creditosFiadorJudicial;
    @SerializedName("pnFiaCas")
    private Integer creditosFiadorCastigado;
    @SerializedName("pnCreSol")
    private Integer creditosSolven;
    /****************************/

    /*GeneracionCreditoSolven*/
    /*********REQUEST*********/
    @SerializedName("pcDesCre")
    private String motivoCredito;
    @SerializedName("pnCuoSol")
    private Integer cuotas;
    @SerializedName("pcResEle")
    private String respuestaEleccion;
    @SerializedName("pcTipEle")
    private String tipoEleccion;
    @SerializedName("pcCtaAho")
    private String numeroCuenta;
    @SerializedName("pcCodCan")
    private String banco;
    @SerializedName("plDebAut")
    private Boolean debitoAutomatico;
    @SerializedName("pcCodPda")
    private String cuentaAhorros;
    @SerializedName("pcTipApp")
    private String pagoAnticipado;
    /*************************/

    /*GeneracionCreditoSolven*/
    /********RESPONSE*********/
    @SerializedName("pcCodCta")
    private String cuenta;
    @SerializedName("pnTasEfe")
    private Double tea;
    @SerializedName("pcNomPro")
    private String tipoProducto;
    @SerializedName("pnMonTca")
    private Double tcea;
    @SerializedName("pcNomCan")
    private String canalDesembolso;
    /*************************/

    /*GeneracionCreditoSolven*/
    /*********REQUEST*********/
    public Credito(PersonBankAccountInformation personBankAccountInformation, LoanApplication loanApplication, LoanOffer loanOffer, TranslatorDAO translatorDAO, String entityCreditCode)throws Exception{
        setMotivoCredito(translatorDAO.translate(Entity.COMPARTAMOS, 6, loanApplication.getReason().getId().toString(), null));
        setCuotas(loanOffer.getInstallments());

        String paymentType = loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.APPLICATION_PAYMENT_TYPE.getKey());
        String paymentTypeEntity = "";
        if(paymentType != null && paymentType.equalsIgnoreCase("A")) paymentTypeEntity = "5";
        else if(paymentType != null && paymentType.equalsIgnoreCase("D")) paymentTypeEntity = "4";
        setRespuestaEleccion(paymentTypeEntity);
        setTipoEleccion("1"); //ENVIO DE EST. DE CTA. CREDITOS
        setNumeroCuenta(personBankAccountInformation.getBankAccount());
        setCuentaAhorros(translatorDAO.translate(Entity.COMPARTAMOS, 8, personBankAccountInformation.getBankAccountType().toString(), null));
        setBanco(translatorDAO.translate(Entity.COMPARTAMOS, 7, personBankAccountInformation.getBank().getId().toString(), null));
        /*************************/
        setDebitoAutomatico(true);
        /*************************/
        setPagoAnticipado(translatorDAO.translate(Entity.COMPARTAMOS, 18, JsonUtil.getStringFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.APPLICATION_PAYMENT_TYPE.getKey(), null), null));
        if(entityCreditCode != null){
            setCuenta(entityCreditCode);
        }
    }
    /*************************/

    public Credito(){}

    public Integer getMaximoAtraso() {
        return maximoAtraso;
    }

    public void setMaximoAtraso(Integer maximoAtraso) {
        this.maximoAtraso = maximoAtraso;
    }

    public Integer getCreditosVigentes() {
        return creditosVigentes;
    }

    public void setCreditosVigentes(Integer creditosVigentes) {
        this.creditosVigentes = creditosVigentes;
    }

    public Integer getCreditosVigentesConyugue() {
        return creditosVigentesConyugue;
    }

    public void setCreditosVigentesConyugue(Integer creditosVigentesConyugue) {
        this.creditosVigentesConyugue = creditosVigentesConyugue;
    }

    public Integer getCreditosPreAprobadosConyugue() {
        return creditosPreAprobadosConyugue;
    }

    public void setCreditosPreAprobadosConyugue(Integer creditosPreAprobadosConyugue) {
        this.creditosPreAprobadosConyugue = creditosPreAprobadosConyugue;
    }

    public Integer getCreditosFiadorVencidos() {
        return creditosFiadorVencidos;
    }

    public void setCreditosFiadorVencidos(Integer creditosFiadorVencidos) {
        this.creditosFiadorVencidos = creditosFiadorVencidos;
    }

    public Integer getCreditosFiadorJudicial() {
        return creditosFiadorJudicial;
    }

    public void setCreditosFiadorJudicial(Integer creditosFiadorJudicial) {
        this.creditosFiadorJudicial = creditosFiadorJudicial;
    }

    public Integer getCreditosFiadorCastigado() {
        return creditosFiadorCastigado;
    }

    public void setCreditosFiadorCastigado(Integer creditosFiadorCastigado) {
        this.creditosFiadorCastigado = creditosFiadorCastigado;
    }

    public Integer getCreditosPreAprobadosTitular() {
        return creditosPreAprobadosTitular;
    }

    public void setCreditosPreAprobadosTitular(Integer creditosPreAprobadosTitular) {
        this.creditosPreAprobadosTitular = creditosPreAprobadosTitular;
    }

    public Integer getCreditosSolven() {
        return creditosSolven;
    }

    public void setCreditosSolven(Integer creditosSolven) {
        this.creditosSolven = creditosSolven;
    }

    /*********REQUEST*********/
    public String getMotivoCredito() {
        return motivoCredito;
    }

    public void setMotivoCredito(String motivoCredito) {
        this.motivoCredito = motivoCredito;
    }

    public Integer getCuotas() {
        return cuotas;
    }

    public void setCuotas(Integer cuotas) {
        this.cuotas = cuotas;
    }

    public String getRespuestaEleccion() {
        return respuestaEleccion;
    }

    public void setRespuestaEleccion(String respuestaEleccion) {
        this.respuestaEleccion = respuestaEleccion;
    }

    public String getTipoEleccion() {
        return tipoEleccion;
    }

    public void setTipoEleccion(String tipoEleccion) {
        this.tipoEleccion = tipoEleccion;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public Boolean getDebitoAutomatico() {
        return debitoAutomatico;
    }

    public void setDebitoAutomatico(Boolean debitoAutomatico) {
        this.debitoAutomatico = debitoAutomatico;
    }

    public String getCuentaAhorros() {
        return cuentaAhorros;
    }

    public void setCuentaAhorros(String cuentaAhorros) {
        this.cuentaAhorros = cuentaAhorros;
    }

    public String getPagoAnticipado() {
        return pagoAnticipado;
    }

    public void setPagoAnticipado(String pagoAnticipado) {
        this.pagoAnticipado = pagoAnticipado;
    }

    /*********RESPONSE********/
    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public Double getTea() {
        return tea;
    }

    public void setTea(Double tea) {
        this.tea = tea;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public Double getTcea() {
        return tcea;
    }

    public void setTcea(Double tcea) {
        this.tcea = tcea;
    }

    public String getCanalDesembolso() {
        return canalDesembolso;
    }

    public void setCanalDesembolso(String canalDesembolso) {
        this.canalDesembolso = canalDesembolso;
    }

    /**********RESPONSE***********/
    public String getTipoCredito() {
        return tipoCredito;
    }

    public void setTipoCredito(String tipoCredito) {
        this.tipoCredito = tipoCredito;
    }
}
