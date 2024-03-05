package com.affirm.compartamos.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.catalog.ActivityType;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.transactional.DisggregatedAddress;
import com.affirm.common.model.transactional.PersonContactInformation;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dev5 on 14/12/17.
 */
public class Direccion {

    @SerializedName("pcZonDis")
    private String ubigeo;
    @SerializedName("pcAgrUrb")
    private String codigoUrbanizacion;
    @SerializedName("pcDetUrb")
    private String urbanizacion;
    @SerializedName("pcAgrSec")
    private String codigoSector;
    @SerializedName("pcDetSec")
    private String sector;
    @SerializedName("pcAgrVia")
    private String codigoVia;
    @SerializedName("pcDetVia")
    private String via;
    @SerializedName("pcDetDir")
    private String direccion;
    @SerializedName("pcTipDom")
    private String tipoDomicilio;
    @SerializedName("pcConviv")
    private String condicionVivienda;
    @SerializedName("pnLatitu")
    private Double latitud;
    @SerializedName("pnLongit")
    private Double longitud;

    public Direccion(DisggregatedAddress disggregatedAddress, PersonContactInformation personContactInformation, TranslatorDAO translatorDAO, PersonOcupationalInformation personOcupationalInformation) throws Exception {
        setUbigeo(disggregatedAddress.getIneiUbigeo());
        setCodigoUrbanizacion(disggregatedAddress.getAreaType() != null ? translatorDAO.translate(Entity.COMPARTAMOS, 14, disggregatedAddress.getAreaType().getId().toString(), null) : null);
        if (getCodigoUrbanizacion() != null && !getCodigoUrbanizacion().trim().equals("0")) {
            setUrbanizacion(disggregatedAddress.getZoneName());
        } else {
            setUrbanizacion("");
        }
        setCodigoSector("0");
        setSector("");
        //setSector(disggregatedAddress.getSector() != null ? disggregatedAddress.getSector() : null);
        setCodigoVia(disggregatedAddress.getStreetType() != null ? translatorDAO.translate(Entity.COMPARTAMOS, 15, disggregatedAddress.getStreetType().getId().toString(), null) : null);
        setVia(disggregatedAddress.getStreetName() + (disggregatedAddress.getStreetnumber() != null ? " " + disggregatedAddress.getStreetnumber() : ""));
        String detalleDireccion = null;
        if (disggregatedAddress.getSquare() != null && !disggregatedAddress.getSquare().isEmpty()) {
            detalleDireccion = "Mz. " + disggregatedAddress.getSquare();
        }
        if (disggregatedAddress.getAllotment() != null && !disggregatedAddress.getAllotment().isEmpty()) {
            detalleDireccion = detalleDireccion + " ";
            detalleDireccion = detalleDireccion + "Lt. " + disggregatedAddress.getAllotment();
        }
        if (detalleDireccion != null && !detalleDireccion.isEmpty())
            setDireccion(detalleDireccion);
        char addressType = disggregatedAddress.getType();
        if (personOcupationalInformation != null && personOcupationalInformation.getActivityType().getId().equals(ActivityType.INDEPENDENT)) {
            addressType = 'J';
        }
        setTipoDomicilio(translatorDAO.translate(Entity.COMPARTAMOS, 16, String.valueOf(addressType), null));
        if(personContactInformation != null){
            setCondicionVivienda(translatorDAO.translate(Entity.COMPARTAMOS, 9, personContactInformation.getHousingType().getId().toString(), null));
            setLatitud(personContactInformation.getAddressLatitude());
            setLongitud(personContactInformation.getAddressLongitude());
        }
    }

    public String getCodigoUrbanizacion() {
        return codigoUrbanizacion;
    }

    public void setCodigoUrbanizacion(String codigoUrbanizacion) {
        this.codigoUrbanizacion = codigoUrbanizacion;
    }

    public String getUrbanizacion() {
        return urbanizacion;
    }

    public void setUrbanizacion(String urbanizacion) {
        this.urbanizacion = urbanizacion;
    }

    public String getCodigoSector() {
        return codigoSector;
    }

    public void setCodigoSector(String codigoSector) {
        this.codigoSector = codigoSector;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getCodigoVia() {
        return codigoVia;
    }

    public void setCodigoVia(String codigoVia) {
        this.codigoVia = codigoVia;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTipoDomicilio() {
        return tipoDomicilio;
    }

    public void setTipoDomicilio(String tipoDomicilio) {
        this.tipoDomicilio = tipoDomicilio;
    }

    public String getCondicionVivienda() {
        return condicionVivienda;
    }

    public void setCondicionVivienda(String condicionVivienda) {
        this.condicionVivienda = condicionVivienda;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getUbigeo() {
        return ubigeo;
    }

    public void setUbigeo(String ubigeo) {
        this.ubigeo = ubigeo;
    }

}
