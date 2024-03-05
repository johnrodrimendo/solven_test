package com.affirm.acceso.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dev5 on 28/08/17.
 */
public class CatalogoVehicular {

    @SerializedName("co_polveh")
    private Integer politicaVehicular;
    @SerializedName("co_operad")
    private Integer codOperador;
    @SerializedName("no_operad")
    private String descOperador;
    @SerializedName("co_produc")
    private Integer codProducto;
    @SerializedName("no_produc")
    private String nombreProducto;
    @SerializedName("ti_modelo")
    private Integer codSubProducto;
    @SerializedName("no_subpro")
    private String nombreSubProducto;
    @SerializedName("co_cenope")
    private Integer centroOperaciones;
    @SerializedName("no_cenope")
    private String nombreCentroOperaciones;
    @SerializedName("co_conces")
    private Integer codConcesionario;
    @SerializedName("no_conces")
    private String nombreConcesionario;
    @SerializedName("ti_poleda")
    private Integer tipoEdad;
    @SerializedName("no_poleda")
    private String descTipoEdad;
    @SerializedName("co_marcas")
    private Integer codMarca;
    @SerializedName("no_marcas")
    private String descMarca;
    @SerializedName("co_modelo")
    private Integer codModelo;
    @SerializedName("no_modelo")
    private String descModelo;
    @SerializedName("im_preveh")
    private Double precioLista;
    @SerializedName("im_preven")
    private Double precioVehiculo;
    @SerializedName("de_anofab")
    private Integer anhoFabricacion;
    @SerializedName("co_verveh")
    private Integer versionVehicular;
    @SerializedName("no_verveh")
    private String descVersionVehicular;
    @SerializedName("ti_colveh")
    private Integer codColor;
    @SerializedName("no_colveh")
    private String descColor;
    @SerializedName("ti_traveh")
    private Integer codTipoTransmision;
    @SerializedName("no_traveh")
    private String descTipoTransmision;
    @SerializedName("co_tipcom")
    private String codTipoCombustible;
    @SerializedName("no_tipcom")
    private String descTipoCombustible;
    @SerializedName("ti_cuotas")
    private String codTipoCuotas;
    @SerializedName("no_cuotas")
    private String descTipoCuota;
    @SerializedName("co_mensaj")
    private Integer codError;
    @SerializedName("nu_anofab")
    private Integer anoFabricacion;

    public Integer getPoliticaVehicular() {
        return politicaVehicular;
    }

    public void setPoliticaVehicular(Integer politicaVehicular) {
        this.politicaVehicular = politicaVehicular;
    }

    public Integer getCodOperador() {
        return codOperador;
    }

    public void setCodOperador(Integer codOperador) {
        this.codOperador = codOperador;
    }

    public String getDescOperador() {
        return descOperador;
    }

    public void setDescOperador(String descOperador) {
        this.descOperador = descOperador;
    }

    public Integer getCodProducto() {
        return codProducto;
    }

    public void setCodProducto(Integer codProducto) {
        this.codProducto = codProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getCodSubProducto() {
        return codSubProducto;
    }

    public void setCodSubProducto(Integer codSubProducto) {
        this.codSubProducto = codSubProducto;
    }

    public String getNombreSubProducto() {
        return nombreSubProducto;
    }

    public void setNombreSubProducto(String nombreSubProducto) {
        this.nombreSubProducto = nombreSubProducto;
    }

    public Integer getCentroOperaciones() {
        return centroOperaciones;
    }

    public void setCentroOperaciones(Integer centroOperaciones) {
        this.centroOperaciones = centroOperaciones;
    }

    public String getNombreCentroOperaciones() {
        return nombreCentroOperaciones;
    }

    public void setNombreCentroOperaciones(String nombreCentroOperaciones) {
        this.nombreCentroOperaciones = nombreCentroOperaciones;
    }

    public Integer getCodConcesionario() {
        return codConcesionario;
    }

    public void setCodConcesionario(Integer codConcesionario) {
        this.codConcesionario = codConcesionario;
    }

    public String getNombreConcesionario() {
        return nombreConcesionario;
    }

    public void setNombreConcesionario(String nombreConcesionario) {
        this.nombreConcesionario = nombreConcesionario;
    }

    public Integer getTipoEdad() {
        return tipoEdad;
    }

    public void setTipoEdad(Integer tipoEdad) {
        this.tipoEdad = tipoEdad;
    }

    public String getDescTipoEdad() {
        return descTipoEdad;
    }

    public void setDescTipoEdad(String descTipoEdad) {
        this.descTipoEdad = descTipoEdad;
    }

    public Integer getCodMarca() {
        return codMarca;
    }

    public void setCodMarca(Integer codMarca) {
        this.codMarca = codMarca;
    }

    public String getDescMarca() {
        return descMarca;
    }

    public void setDescMarca(String descMarca) {
        this.descMarca = descMarca;
    }

    public Integer getCodModelo() {
        return codModelo;
    }

    public void setCodModelo(Integer codModelo) {
        this.codModelo = codModelo;
    }

    public String getDescModelo() {
        return descModelo;
    }

    public void setDescModelo(String descModelo) {
        this.descModelo = descModelo;
    }

    public Double getPrecioVehiculo() {
        return precioVehiculo;
    }

    public void setPrecioVehiculo(Double precioVehiculo) {
        this.precioVehiculo = precioVehiculo;
    }

    public Integer getAnhoFabricacion() {
        return anhoFabricacion;
    }

    public void setAnhoFabricacion(Integer anhoFabricacion) {
        this.anhoFabricacion = anhoFabricacion;
    }

    public Integer getVersionVehicular() {
        return versionVehicular;
    }

    public void setVersionVehicular(Integer versionVehicular) {
        this.versionVehicular = versionVehicular;
    }

    public String getDescVersionVehicular() {
        return descVersionVehicular;
    }

    public void setDescVersionVehicular(String descVersionVehicular) {
        this.descVersionVehicular = descVersionVehicular;
    }

    public Integer getCodColor() {
        return codColor;
    }

    public void setCodColor(Integer codColor) {
        this.codColor = codColor;
    }

    public String getDescColor() {
        return descColor;
    }

    public void setDescColor(String descColor) {
        this.descColor = descColor;
    }

    public Integer getCodTipoTransmision() {
        return codTipoTransmision;
    }

    public void setCodTipoTransmision(Integer codTipoTransmision) {
        this.codTipoTransmision = codTipoTransmision;
    }

    public String getDescTipoTransmision() {
        return descTipoTransmision;
    }

    public void setDescTipoTransmision(String descTipoTransmision) {
        this.descTipoTransmision = descTipoTransmision;
    }

    public String getCodTipoCombustible() {
        return codTipoCombustible;
    }

    public void setCodTipoCombustible(String codTipoCombustible) {
        this.codTipoCombustible = codTipoCombustible;
    }

    public String getDescTipoCombustible() {
        return descTipoCombustible;
    }

    public void setDescTipoCombustible(String descTipoCombustible) {
        this.descTipoCombustible = descTipoCombustible;
    }

    public String getCodTipoCuotas() {
        return codTipoCuotas;
    }

    public void setCodTipoCuotas(String codTipoCuotas) {
        this.codTipoCuotas = codTipoCuotas;
    }

    public String getDescTipoCuota() {
        return descTipoCuota;
    }

    public void setDescTipoCuota(String descTipoCuota) {
        this.descTipoCuota = descTipoCuota;
    }

    public Integer getCodError() {
        return codError;
    }

    public void setCodError(Integer codError) {
        this.codError = codError;
    }

    public Integer getAnoFabricacion() {
        return anoFabricacion;
    }

    public void setAnoFabricacion(Integer anoFabricacion) {
        this.anoFabricacion = anoFabricacion;
    }


    public Double getPrecioLista() {
        return precioLista;
    }

    public void setPrecioLista(Double precioLista) {
        this.precioLista = precioLista;
    }
}
