package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AztecaGetawayBase implements Serializable {

    public static final int CARTERA_PROPIA_TIPO = 0;
    public static final int CARTERA_VENTA_TIPO = 1;

    public static final int TIPO_CAMPANIA_LIQUIDACION = 16;
    public static final int TIPO_CAMPANIA_PKM_1_CUOTA = 19;
    public static final int TIPO_CAMPANIA_PKM_NORMAL = 20;
    public static final int TIPO_CAMPANIA_REPROGRAMACION = 17;
    public static final int TIPO_CAMPANIA_REFINANCIAMIENTO = 18;

    public static final String MIN_HOUR_FOR_PAYMENTS = "08:10";
    public static final String MAX_HOUR_FOR_PAYMENTS = "22:00";
    public static final List<Integer> TIPO_CAMPANIA_ORDER = Arrays.asList(
            TIPO_CAMPANIA_LIQUIDACION,
            TIPO_CAMPANIA_PKM_1_CUOTA,
            TIPO_CAMPANIA_PKM_NORMAL,
            TIPO_CAMPANIA_REPROGRAMACION,
            TIPO_CAMPANIA_REFINANCIAMIENTO
    );

    private Long id;
    private String pais;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombre;
    private String apPaterno;
    private String apMaterno;
    private String celular1;
    private String celular2;
    private String celular3;
    private String celular4;
    private String celular5;
    private Double saldoCapital;
    private Double saldoInteres;
    private Double saldoMoratorio;
    private Double saldoTotal;
    private Integer diasAtrazo;
    private Double montoCampania;
    private Date vencimientoCampania;
    private String domicilio;
    private String departamento;
    private String provincia;
    private String distrito;
    private String codigoClienteExterno;
    private String correo1;
    private String correo2;
    private Integer tipoCliente;
    private Integer campaniaId;
    private Long operacionUId;
    private Integer tipoOperacionBT;

    private String codigo;

    public void fillFromDb(JSONObject json) {
        setPais(JsonUtil.getStringFromJson(json, "pais", null));
        setTipoDocumento(JsonUtil.getStringFromJson(json, "tipo_documento", null));
        setNumeroDocumento(JsonUtil.getStringFromJson(json, "numero_documento", null));
        setNombre(JsonUtil.getStringFromJson(json, "nombre", null));
        setApPaterno(JsonUtil.getStringFromJson(json, "ap_paterno", null));
        setApMaterno(JsonUtil.getStringFromJson(json, "ap_materno", null));
        setCelular1(JsonUtil.getStringFromJson(json, "celular_1", null));
        setCelular2(JsonUtil.getStringFromJson(json, "celular_2", null));
        setCelular3(JsonUtil.getStringFromJson(json, "celular_3", null));
        setCelular4(JsonUtil.getStringFromJson(json, "celular_4", null));
        setCelular5(JsonUtil.getStringFromJson(json, "celular_5", null));
        setSaldoCapital(JsonUtil.getDoubleFromJson(json, "saldo_capital", null));
        setSaldoInteres(JsonUtil.getDoubleFromJson(json, "saldo_interes", null));
        setSaldoMoratorio(JsonUtil.getDoubleFromJson(json, "saldo_moratorio", null));
        setSaldoTotal(JsonUtil.getDoubleFromJson(json, "saldo_total", null));
        setDiasAtrazo(JsonUtil.getIntFromJson(json, "dias_atrazo", null));
        setMontoCampania(JsonUtil.getDoubleFromJson(json, "monto_campania", null));
        setVencimientoCampania(JsonUtil.getPostgresDateFromJson(json, "vencimiento_campania", null));
        setDomicilio(JsonUtil.getStringFromJson(json, "domicilio", null));
        setDepartamento(JsonUtil.getStringFromJson(json, "departamento", null));
        setProvincia(JsonUtil.getStringFromJson(json, "provincia", null));
        setDistrito(JsonUtil.getStringFromJson(json, "distrito", null));
        setCodigoClienteExterno(JsonUtil.getStringFromJson(json, "codigo_cliente_externo", null));
        setId(JsonUtil.getLongFromJson(json, "id", null));
        setCorreo1(JsonUtil.getStringFromJson(json, "correo_1", null));
        setCorreo2(JsonUtil.getStringFromJson(json, "correo_2", null));
        setTipoCliente(JsonUtil.getIntFromJson(json, "tipo_cliente", 0));
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApPaterno() {
        return apPaterno;
    }

    public void setApPaterno(String apPaterno) {
        this.apPaterno = apPaterno;
    }

    public String getApMaterno() {
        return apMaterno;
    }

    public void setApMaterno(String apMaterno) {
        this.apMaterno = apMaterno;
    }

    public String getCelular1() {
        return celular1;
    }

    public void setCelular1(String celular1) {
        this.celular1 = celular1;
    }

    public String getCelular2() {
        return celular2;
    }

    public void setCelular2(String celular2) {
        this.celular2 = celular2;
    }

    public String getCelular3() {
        return celular3;
    }

    public void setCelular3(String celular3) {
        this.celular3 = celular3;
    }

    public String getCelular4() {
        return celular4;
    }

    public void setCelular4(String celular4) {
        this.celular4 = celular4;
    }

    public String getCelular5() {
        return celular5;
    }

    public void setCelular5(String celular5) {
        this.celular5 = celular5;
    }

    public Double getSaldoCapital() {
        return saldoCapital;
    }

    public void setSaldoCapital(Double saldoCapital) {
        this.saldoCapital = saldoCapital;
    }

    public Double getSaldoInteres() {
        return saldoInteres;
    }

    public void setSaldoInteres(Double saldoInteres) {
        this.saldoInteres = saldoInteres;
    }

    public Double getSaldoMoratorio() {
        return saldoMoratorio;
    }

    public void setSaldoMoratorio(Double saldoMoratorio) {
        this.saldoMoratorio = saldoMoratorio;
    }

    public Double getSaldoTotal() {
        return saldoTotal;
    }

    public void setSaldoTotal(Double saldoTotal) {
        this.saldoTotal = saldoTotal;
    }

    public Integer getDiasAtrazo() {
        return diasAtrazo;
    }

    public void setDiasAtrazo(Integer diasAtrazo) {
        this.diasAtrazo = diasAtrazo;
    }

    public Double getMontoCampania() {
        return montoCampania;
    }

    public void setMontoCampania(Double montoCampania) {
        this.montoCampania = montoCampania;
    }

    public Date getVencimientoCampania() {
        return vencimientoCampania;
    }

    public void setVencimientoCampania(Date vencimientoCampania) {
        this.vencimientoCampania = vencimientoCampania;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getCodigoClienteExterno() {
        return codigoClienteExterno;
    }

    public void setCodigoClienteExterno(String codigoClienteExterno) {
        this.codigoClienteExterno = codigoClienteExterno;
    }

    public Double getMontoAhorro(){
        if(montoCampania != null && saldoTotal != null){
            return saldoTotal - montoCampania;
        }
        return null;
    }

    public int getPorcentajeAhorro(){
        if(montoCampania != null && saldoTotal != null){
            return (int) (100 - ((montoCampania*100)/saldoTotal));
        }
        return 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCorreo1() {
        return correo1;
    }

    public void setCorreo1(String correo1) {
        this.correo1 = correo1;
    }

    public String getCorreo2() {
        return correo2;
    }

    public void setCorreo2(String correo2) {
        this.correo2 = correo2;
    }

    public Integer getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(Integer tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public Integer getCampaniaId() { return campaniaId; }

    public void setCampaniaId(Integer campaniaId) { this.campaniaId = campaniaId; }

    public Long getOperacionUId() {
        return operacionUId;
    }

    public void setOperacionUId(Long operacionUId) {
        this.operacionUId = operacionUId;
    }

    public Integer getTipoOperacionBT() {
        return tipoOperacionBT;
    }

    public void setTipoOperacionBT(Integer tipoOperacionBT) {
        this.tipoOperacionBT = tipoOperacionBT;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
