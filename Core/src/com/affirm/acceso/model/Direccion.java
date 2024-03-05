package com.affirm.acceso.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.catalog.District;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.Ubigeo;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by dev5 on 27/07/17.
 */
public class Direccion {

    @SerializedName("p_id_sesion")
    private Integer sesionId;
    @SerializedName("p_co_expedi")
    private Integer nroExpediente;
    @SerializedName("p_ti_solici")
    private String tipoSolicitante;
    @SerializedName("p_ti_domici")
    private String tipoDomicilio;
    @SerializedName("p_ti_camino")
    private Integer tipoVia;
    @SerializedName("p_no_camino")
    private String nombreVia;
    @SerializedName("p_nu_camino")
    private String numeroVia;
    @SerializedName("p_nu_manzan")
    private String manzana;
    @SerializedName("p_nu_lotiza")
    private String lote;
    @SerializedName("p_ti_interi")
    private Integer tipoInterior;
    @SerializedName("p_nu_interi")
    private String numeroInterior;
    @SerializedName("p_ti_zongeo")
    private Integer tipoZona;
    @SerializedName("p_no_zongeo")
    private String nombreZona;
    @SerializedName("p_no_refere")
    private String referencia;
    @SerializedName("p_nu_bloque")
    private String bloque;
    @SerializedName("p_nu_kilmet")
    private String kilometro;
    @SerializedName("p_no_barrio")
    private String barrio;
    @SerializedName("p_nu_etapas")
    private String etapa;
    @SerializedName("p_nu_grupos")
    private String grupo;
    @SerializedName("p_nu_sector")
    private String sector;
    @SerializedName("p_no_parcel")
    private String parcela;

    private String ubigeoInei;
    @SerializedName("p_co_ubigeo")
    private String ubigeo;

    @SerializedName("p_no_direcc")
    private String direccionCompleta;

    private Long districtId;
    private Long localityId;
    private String floor;
    private Boolean withoutNumber;
    private String postalCode;

    private String searchQuery;
    private Double latitude;
    private Double longitude;

    public Direccion(){

    }

    public Direccion(Integer sesionId, Integer nroExpediente, String tipoSolicitante, String tipoDomicilio, Integer tipoVia, String nombreVia, String direccionCompleta, String referencia, Integer tipoZona, String ubigeo){
        setSesionId(sesionId);
        setNroExpediente(nroExpediente);
        setTipoSolicitante(tipoSolicitante);
        setTipoDomicilio(tipoDomicilio);
        setTipoVia(tipoVia);
        setNombreVia(nombreVia);
        setTipoZona(tipoZona);
        setReferencia(referencia);
        setUbigeo(ubigeo);
        setDireccionCompleta(direccionCompleta);
    }

    public Direccion(CatalogService catalogService, String tipoDomicilio, String tipoVia, String nombreVia, String numeroVia, String interior, String manzana, String lote, String ubigeo, String referencia, String tipoZona, String nombreZona, Long districtId, String floor, Boolean withoutNumber, String postalCode) throws Exception{
        setTipoDomicilio(tipoDomicilio);
        setTipoVia(tipoVia != null ? Integer.valueOf(tipoVia) : null);
        setNombreVia(nombreVia);
        setNumeroVia(numeroVia);
        setNumeroInterior(interior);
        setManzana(manzana);
        setLote(lote);
        setTipoZona(tipoZona != null ? Integer.valueOf(tipoZona) : null);
        setNombreZona(nombreZona);
        setUbigeo(ubigeo);
        setReferencia(referencia);
        setDistrictId(districtId);
        setFloor(floor == null || floor.equals("") ? null : floor);
        setWithoutNumber(withoutNumber);
        setPostalCode(postalCode);
        setDireccionCompleta(getFullAddress(catalogService));
    }

    public void fillFromDb(JSONObject json, CatalogService catalogService, TranslatorDAO translatorDAO, Locale locale) throws Exception {
        setTipoSolicitante("TI");
        setTipoDomicilio(translatorDAO.translate(Entity.ACCESO, 37, JsonUtil.getStringFromJson(json, "disaggregated_address_type", null), null));
        setTipoVia(JsonUtil.getIntFromJson(json, "street_type_id", null));
        setNombreVia(JsonUtil.getStringFromJson(json, "street_name", null));
        setNumeroVia(JsonUtil.getStringFromJson(json, "street_number", null));
        setManzana(JsonUtil.getStringFromJson(json, "square", null));
        setLote(JsonUtil.getStringFromJson(json, "allotment", null));
        setTipoInterior(JsonUtil.getIntFromJson(json, "interior_type", null));
        setNumeroInterior(JsonUtil.getStringFromJson(json, "interior_number", null));
        setTipoZona(JsonUtil.getIntFromJson(json, "zone_type", null));
        setNombreZona(JsonUtil.getStringFromJson(json, "zone_name", null));
        setReferencia(JsonUtil.getStringFromJson(json, "reference", null));
        setBloque(JsonUtil.getStringFromJson(json, "block", null));
        setKilometro(JsonUtil.getStringFromJson(json, "kilometer", null));
        setBarrio(JsonUtil.getStringFromJson(json, "ward", null));
        setEtapa(JsonUtil.getStringFromJson(json, "stage", null));
        setGrupo(JsonUtil.getStringFromJson(json, "group", null));
        setSector(JsonUtil.getStringFromJson(json, "sector", null));
        setParcela(JsonUtil.getStringFromJson(json, "parcel", null));
        setUbigeo(JsonUtil.getStringFromJson(json, "ubigeo_id", null));
        setUbigeoInei(JsonUtil.getStringFromJson(json, "inei_ubigeo_id", null));
        setLocalityId(JsonUtil.getLongFromJson(json, "locality_id", null));
        setFloor(JsonUtil.getStringFromJson(json, "floor", null));
        setWithoutNumber(JsonUtil.getBooleanFromJson(json, "without_number", null));

        setDireccionCompleta(getFullAddress(catalogService));
    }

    public Direccion fillCoordinatesFromDb(JSONObject dbJson, Direccion direccion){
        direccion.setSearchQuery(JsonUtil.getStringFromJson(dbJson, "search_query", null));
        direccion.setLatitude(JsonUtil.getDoubleFromJson(dbJson, "address_latitude", null));
        direccion.setLongitude(JsonUtil.getDoubleFromJson(dbJson, "address_longitude", null));
        return direccion;
    }

    private String getFullAddress(CatalogService catalogService) throws Exception{
        Ubigeo ubigeo = catalogService.getUbigeo(getUbigeo());

        String direccion = "";
        if(getTipoVia() != null  && catalogService.getAvenuesById(getTipoVia()) != null) direccion = direccion.concat(catalogService.getAvenuesById(getTipoVia()).getName());
        if(getNombreVia() != null && !getNombreVia().isEmpty()) direccion = direccion.concat(" ").concat(getNombreVia());
        if(getNumeroVia() != null && !getNumeroVia().isEmpty()) direccion = direccion.concat(" ").concat(getNumeroVia());
        if(getWithoutNumber() != null && getWithoutNumber()) direccion = direccion.concat(" ").concat("S/N");
        if(getNumeroInterior() != null) direccion = direccion.concat(" Dep.: ").concat(getNumeroInterior().toString());
        if(getFloor() != null) direccion = direccion.concat(" Piso: ").concat(getFloor().toString());
        if(getManzana() != null && !getManzana().isEmpty()) direccion = direccion.concat(" ").concat(getManzana());
        if(getLote() != null && !getLote().isEmpty()) direccion = direccion.concat(" ").concat(getLote());
        if(getTipoInterior() != null && getTipoInterior() != null && catalogService.getHouseTypeById(getTipoInterior()) != null) direccion = direccion.concat(" ").concat(catalogService.getHouseTypeById(getTipoInterior()).getName());
        if(getTipoZona() != null && catalogService.getAreaTypeById(getTipoZona()) != null) direccion = direccion.concat(" ").concat(catalogService.getAreaTypeById(getTipoZona()).getName());
        if(getNombreZona() != null && !getNombreZona().isEmpty()) direccion = direccion.concat(" ").concat(getNombreZona());
        if(getReferencia() != null && !getReferencia().isEmpty()) direccion = direccion.concat(" Ref.: ").concat(getReferencia());
        if(getBloque() != null && !getBloque().isEmpty()) direccion = direccion.concat(" ").concat(getBloque());
        if(getKilometro() != null && !getKilometro().isEmpty()) direccion = direccion.concat(" ").concat(getKilometro());
        if(getBarrio() != null && !getBarrio().isEmpty()) direccion = direccion.concat(" ").concat(getBarrio());
        if(getEtapa() != null && !getEtapa().isEmpty()) direccion = direccion.concat(" ").concat(getEtapa());
        if(getGrupo() != null && !getGrupo().isEmpty()) direccion = direccion.concat(" ").concat(getGrupo());
        if(getSector() !=null && !getSector().isEmpty()) direccion = direccion.concat(" ").concat(getSector());
        if(getParcela() != null && !getParcela().isEmpty()) direccion = direccion.concat(" ").concat(getParcela());
        if(getPostalCode() != null && !getPostalCode().isEmpty()) direccion = direccion.concat(" ").concat("Cod. Postal: ").concat(getPostalCode());
        else{
            District district = catalogService.getGeneralDistrict().stream().filter(e->e.getDistrictId().equals(getDistrictId())).findFirst().orElse(null);
            if(district != null && district.getPostalCode() != null)
                direccion = direccion.concat(" ").concat("Cod. Postal: ").concat(district.getPostalCode());
        }
        if(ubigeo != null && ubigeo.getDepartment() != null && ubigeo.getProvince() != null && ubigeo.getDistrict() != null)
            direccion = direccion.concat(" ").concat(ubigeo.getDistrict().getName()).concat(" ").concat(ubigeo.getProvince().getName()).concat(" ").concat(ubigeo.getDepartment().getName());
        else{
            direccion = direccion + " " +
                    (getDistrictId() != null ? catalogService.getGeneralProvinceByDistrict(getDistrictId()).getName() + "," : "") + " " +
                    (getDistrictId() != null ? catalogService.getGeneralDistrictById(getDistrictId()).getName() : "");
        }

        return direccion.trim();
    }

    public String getFullAddressWithoutUbigeo(CatalogService catalogService) throws Exception{
        Ubigeo ubigeo = catalogService.getUbigeo(getUbigeo());

        String direccion = "";
        if(getTipoVia() != null  && catalogService.getAvenuesById(getTipoVia()) != null) direccion = direccion.concat(catalogService.getAvenuesById(getTipoVia()).getName());
        if(getNombreVia() != null && !getNombreVia().isEmpty()) direccion = direccion.concat(" ").concat(getNombreVia());
        if(getNumeroVia() != null && !getNumeroVia().isEmpty()) direccion = direccion.concat(" ").concat(getNumeroVia());
        if(getWithoutNumber() != null && getWithoutNumber()) direccion = direccion.concat(" ").concat("S/N");
        if(getNumeroInterior() != null) direccion = direccion.concat(" Dep.: ").concat(getNumeroInterior().toString());
        if(getFloor() != null) direccion = direccion.concat(" Piso: ").concat(getFloor().toString());
        if(getManzana() != null && !getManzana().isEmpty()) direccion = direccion.concat(" ").concat(getManzana());
        if(getLote() != null && !getLote().isEmpty()) direccion = direccion.concat(" ").concat(getLote());
        if(getTipoInterior() != null && getTipoInterior() != null && catalogService.getHouseTypeById(getTipoInterior()) != null) direccion = direccion.concat(" ").concat(catalogService.getHouseTypeById(getTipoInterior()).getName());
        if(getTipoZona() != null && catalogService.getAreaTypeById(getTipoZona()) != null) direccion = direccion.concat(" ").concat(catalogService.getAreaTypeById(getTipoZona()).getName());
        if(getNombreZona() != null && !getNombreZona().isEmpty()) direccion = direccion.concat(" ").concat(getNombreZona());
        if(getReferencia() != null && !getReferencia().isEmpty()) direccion = direccion.concat(" Ref.: ").concat(getReferencia());
        if(getBloque() != null && !getBloque().isEmpty()) direccion = direccion.concat(" ").concat(getBloque());
        if(getKilometro() != null && !getKilometro().isEmpty()) direccion = direccion.concat(" ").concat(getKilometro());
        if(getBarrio() != null && !getBarrio().isEmpty()) direccion = direccion.concat(" ").concat(getBarrio());
        if(getEtapa() != null && !getEtapa().isEmpty()) direccion = direccion.concat(" ").concat(getEtapa());
        if(getGrupo() != null && !getGrupo().isEmpty()) direccion = direccion.concat(" ").concat(getGrupo());
        if(getSector() !=null && !getSector().isEmpty()) direccion = direccion.concat(" ").concat(getSector());
        if(getParcela() != null && !getParcela().isEmpty()) direccion = direccion.concat(" ").concat(getParcela());
        if(getPostalCode() != null && !getPostalCode().isEmpty()) direccion = direccion.concat(" ").concat("Cod. Postal: ").concat(getPostalCode());
        else{
            District district = catalogService.getGeneralDistrict().stream().filter(e->e.getDistrictId().equals(getDistrictId())).findFirst().orElse(null);
            if(district != null && district.getPostalCode() != null)
                direccion = direccion.concat(" ").concat("Cod. Postal: ").concat(district.getPostalCode());
        }


        return direccion.trim();
    }

    public Integer getNroExpediente() {
        return nroExpediente;
    }

    public void setNroExpediente(Integer nroExpediente) {
        this.nroExpediente = nroExpediente;
    }

    public Integer getSesionId() {
        return sesionId;
    }

    public void setSesionId(Integer sesionId) {
        this.sesionId = sesionId;
    }

    public String getTipoSolicitante() {
        return tipoSolicitante;
    }

    public void setTipoSolicitante(String tipoSolicitante) {
        this.tipoSolicitante = tipoSolicitante;
    }

    public String getTipoDomicilio() {
        return tipoDomicilio;
    }

    public void setTipoDomicilio(String tipoDomicilio) {
        this.tipoDomicilio = tipoDomicilio;
    }

    public Integer getTipoVia() {
        return tipoVia;
    }

    public void setTipoVia(Integer tipoVia) {
        this.tipoVia = tipoVia;
    }

    public String getNombreVia() {
        return nombreVia;
    }

    public void setNombreVia(String nombreVia) {
        this.nombreVia = nombreVia;
    }

    public String getNumeroVia() {
        return numeroVia;
    }

    public void setNumeroVia(String numeroVia) {
        this.numeroVia = numeroVia;
    }

    public String getManzana() {
        return manzana;
    }

    public void setManzana(String manzana) {
        this.manzana = manzana;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public Integer getTipoInterior() {
        return tipoInterior;
    }

    public void setTipoInterior(Integer tipoInterior) {
        this.tipoInterior = tipoInterior;
    }

    public String getNumeroInterior() {
        return numeroInterior;
    }

    public void setNumeroInterior(String numeroInterior) {
        this.numeroInterior = numeroInterior;
    }

    public Integer getTipoZona() {
        return tipoZona;
    }

    public void setTipoZona(Integer tipoZona) {
        this.tipoZona = tipoZona;
    }

    public String getNombreZona() {
        return nombreZona;
    }

    public void setNombreZona(String nombreZona) {
        this.nombreZona = nombreZona;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getBloque() {
        return bloque;
    }

    public void setBloque(String bloque) {
        this.bloque = bloque;
    }

    public String getKilometro() {
        return kilometro;
    }

    public void setKilometro(String kilometro) {
        this.kilometro = kilometro;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getEtapa() {
        return etapa;
    }

    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getParcela() {
        return parcela;
    }

    public void setParcela(String parcela) {
        this.parcela = parcela;
    }

    public String getUbigeo() {
        return ubigeo;
    }

    public void setUbigeo(String ubigeo) {
        this.ubigeo = ubigeo;
    }

    public String getUbigeoInei() { return ubigeoInei; }

    public void setUbigeoInei(String ubigeoInei) { this.ubigeoInei = ubigeoInei; }

    public String getDireccionCompleta() {
        return direccionCompleta;
    }

    public void setDireccionCompleta(String direccionCompleta) {
        this.direccionCompleta = direccionCompleta;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public Long getLocalityId() {
        return localityId;
    }

    public void setLocalityId(Long localityId) {
        this.localityId = localityId;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public Boolean getWithoutNumber() {
        return withoutNumber;
    }

    public void setWithoutNumber(Boolean withoutNumber) {
        this.withoutNumber = withoutNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getSearchQuery() { return searchQuery; }

    public void setSearchQuery(String searchQuery) { this.searchQuery = searchQuery; }

    public Double getLatitude() { return latitude; }

    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }

    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
