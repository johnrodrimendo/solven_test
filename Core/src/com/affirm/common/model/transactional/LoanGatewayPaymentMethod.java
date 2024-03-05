package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.GatewayPaymentMethod;
import com.affirm.common.model.pagoefectivo.PagoEfectivoCreateCIPResponse;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.Date;

public class LoanGatewayPaymentMethod {

    private Integer id;
    private Integer loanApplicationId;
    private GatewayPaymentMethod gatewayPaymentMethod;
    private Date registerDate;
    private Date expirationDate;
    private Date paymentDate;
    private Boolean isPayed;
    private Boolean isActive;
    private JSONObject paymentVars;
    private JSONObject paymentResponse;

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setId(JsonUtil.getIntFromJson(json, "loan_collection_payment_id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setExpirationDate(JsonUtil.getPostgresDateFromJson(json, "expiration_date", null));
        setPaymentDate(JsonUtil.getPostgresDateFromJson(json, "payment_date", null));
        setPayed(JsonUtil.getBooleanFromJson(json, "is_payed", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setPaymentVars(JsonUtil.getJsonObjectFromJson(json, "js_payment_vars", null));
        setPaymentResponse(JsonUtil.getJsonObjectFromJson(json, "js_payment_response", null));
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public GatewayPaymentMethod getCollectionPaymentMethod() {
        return gatewayPaymentMethod;
    }

    public void setCollectionPaymentMethod(GatewayPaymentMethod gatewayPaymentMethod) {
        this.gatewayPaymentMethod = gatewayPaymentMethod;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Boolean getPayed() {
        return isPayed;
    }

    public void setPayed(Boolean payed) {
        isPayed = payed;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public JSONObject getPaymentVars() {
        return paymentVars;
    }

    public void setPaymentVars(JSONObject paymentVars) {
        this.paymentVars = paymentVars;
    }

    public JSONObject getPaymentResponse() {
        return paymentResponse;
    }

    public void setPaymentResponse(JSONObject paymentResponse) {
        this.paymentResponse = paymentResponse;
    }

    public String getCip(){
        if(paymentVars == null) return null;
        if(JsonUtil.getJsonObjectFromJson(paymentVars, "data", null) != null){
            if(JsonUtil.getIntFromJson(JsonUtil.getJsonObjectFromJson(paymentVars, "data", null), "cip", null) != null) return JsonUtil.getIntFromJson(JsonUtil.getJsonObjectFromJson(paymentVars, "data", null), "cip", null).toString();
        }
        return null;
    }

    public String getUrlFile(){
        if(gatewayPaymentMethod == null || paymentVars == null) return null;
        switch (gatewayPaymentMethod.getId()) {
            case GatewayPaymentMethod.PAGO_EFECTIVO:
                PagoEfectivoCreateCIPResponse pagoEfectivoData = new Gson().fromJson(paymentVars.toString(), PagoEfectivoCreateCIPResponse.class);
                if(pagoEfectivoData.getData() != null && pagoEfectivoData.getData().getCipUrl() != null){
                    String[] urlSplitted =  pagoEfectivoData.getData().getCipUrl().replaceAll(".html","").split("pagoefectivo.pe/");
                    if(urlSplitted.length > 1){
                        if(Configuration.hostEnvIsProduction()){
                            //POR DEFINIR
                            return "http://admin.pagoefectivo.pe/CIPImpresion.aspx?token="+urlSplitted[1];
                        }
                        else{
                            return "http://admin.pre1a.pagoefectivo.pe/CIPImpresion.aspx?token="+urlSplitted[1];
                        }
                    }

                }

        }
        return null;
    }
}
