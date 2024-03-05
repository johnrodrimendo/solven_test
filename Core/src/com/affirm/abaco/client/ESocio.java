
package com.affirm.abaco.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para eSocio complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="eSocio">
 *   &lt;complexContent>
 *     &lt;extension base="{http://servicio.ws/}ePersona">
 *       &lt;sequence>
 *         &lt;element name="nombreEmpleador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fechaIngresoEmpleador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="direccionEmpleador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="telefonoEmpleador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tiempoPermanencia" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="rucEmpleador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nombresConyugue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="apellidoPaternoConyugue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="apellidoMaternoConyugue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nacionalidadConyugue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fechaNacimientoConyugue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipoDocumentoConyugue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nroDocumentoConyugue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="telefonoConyugue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="generoConyugue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="estadoCivilConyugue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ingresoNetoMensual" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="ingresoBrutoMensual" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="entidadFinanciadora" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="montoLineaCredito" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="tipoPrestamo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="empresaEsSocio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eSocio", propOrder = {
    "nombreEmpleador",
    "fechaIngresoEmpleador",
    "direccionEmpleador",
    "telefonoEmpleador",
    "tiempoPermanencia",
    "rucEmpleador",
    "nombresConyugue",
    "apellidoPaternoConyugue",
    "apellidoMaternoConyugue",
    "nacionalidadConyugue",
    "fechaNacimientoConyugue",
    "tipoDocumentoConyugue",
    "nroDocumentoConyugue",
    "telefonoConyugue",
    "generoConyugue",
    "estadoCivilConyugue",
    "ingresoNetoMensual",
    "ingresoBrutoMensual",
    "entidadFinanciadora",
    "montoLineaCredito",
    "tipoPrestamo",
    "empresaEsSocio"
})
public class ESocio
    extends EPersona
{

    @XmlElement(required = true)
    protected String nombreEmpleador;
    @XmlElement(required = true)
    protected String fechaIngresoEmpleador;
    protected String direccionEmpleador;
    @XmlElement(required = true)
    protected String telefonoEmpleador;
    @XmlElement(required = true)
    protected String tiempoPermanencia;
    @XmlElement(required = true)
    protected String rucEmpleador;
    protected String nombresConyugue;
    protected String apellidoPaternoConyugue;
    protected String apellidoMaternoConyugue;
    protected String nacionalidadConyugue;
    protected String fechaNacimientoConyugue;
    protected String tipoDocumentoConyugue;
    protected String nroDocumentoConyugue;
    protected String telefonoConyugue;
    protected String generoConyugue;
    protected String estadoCivilConyugue;
    protected double ingresoNetoMensual;
    protected double ingresoBrutoMensual;
    protected String entidadFinanciadora;
    protected double montoLineaCredito;
    protected String tipoPrestamo;
    protected String empresaEsSocio;

    @Override
    public String toString() {
        return super.toString() + "- ESocio{" +
                "nombreEmpleador='" + nombreEmpleador + '\'' +
                ", fechaIngresoEmpleador='" + fechaIngresoEmpleador + '\'' +
                ", direccionEmpleador='" + direccionEmpleador + '\'' +
                ", telefonoEmpleador='" + telefonoEmpleador + '\'' +
                ", tiempoPermanencia='" + tiempoPermanencia + '\'' +
                ", rucEmpleador='" + rucEmpleador + '\'' +
                ", nombresConyugue='" + nombresConyugue + '\'' +
                ", apellidoPaternoConyugue='" + apellidoPaternoConyugue + '\'' +
                ", apellidoMaternoConyugue='" + apellidoMaternoConyugue + '\'' +
                ", nacionalidadConyugue='" + nacionalidadConyugue + '\'' +
                ", fechaNacimientoConyugue='" + fechaNacimientoConyugue + '\'' +
                ", tipoDocumentoConyugue='" + tipoDocumentoConyugue + '\'' +
                ", nroDocumentoConyugue='" + nroDocumentoConyugue + '\'' +
                ", telefonoConyugue='" + telefonoConyugue + '\'' +
                ", generoConyugue='" + generoConyugue + '\'' +
                ", estadoCivilConyugue='" + estadoCivilConyugue + '\'' +
                ", ingresoNetoMensual=" + ingresoNetoMensual +
                ", ingresoBrutoMensual=" + ingresoBrutoMensual +
                ", entidadFinanciadora='" + entidadFinanciadora + '\'' +
                ", montoLineaCredito=" + montoLineaCredito +
                ", tipoPrestamo='" + tipoPrestamo + '\'' +
                ", empresaEsSocio='" + empresaEsSocio + '\'' +
                '}';
    }

    /**
     * Obtiene el valor de la propiedad nombreEmpleador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreEmpleador() {
        return nombreEmpleador;
    }

    /**
     * Define el valor de la propiedad nombreEmpleador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreEmpleador(String value) {
        this.nombreEmpleador = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaIngresoEmpleador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaIngresoEmpleador() {
        return fechaIngresoEmpleador;
    }

    /**
     * Define el valor de la propiedad fechaIngresoEmpleador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaIngresoEmpleador(String value) {
        this.fechaIngresoEmpleador = value;
    }

    /**
     * Obtiene el valor de la propiedad direccionEmpleador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDireccionEmpleador() {
        return direccionEmpleador;
    }

    /**
     * Define el valor de la propiedad direccionEmpleador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDireccionEmpleador(String value) {
        this.direccionEmpleador = value;
    }

    /**
     * Obtiene el valor de la propiedad telefonoEmpleador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelefonoEmpleador() {
        return telefonoEmpleador;
    }

    /**
     * Define el valor de la propiedad telefonoEmpleador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelefonoEmpleador(String value) {
        this.telefonoEmpleador = value;
    }

    /**
     * Obtiene el valor de la propiedad tiempoPermanencia.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTiempoPermanencia() {
        return tiempoPermanencia;
    }

    /**
     * Define el valor de la propiedad tiempoPermanencia.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTiempoPermanencia(String value) {
        this.tiempoPermanencia = value;
    }

    /**
     * Obtiene el valor de la propiedad rucEmpleador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRucEmpleador() {
        return rucEmpleador;
    }

    /**
     * Define el valor de la propiedad rucEmpleador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRucEmpleador(String value) {
        this.rucEmpleador = value;
    }

    /**
     * Obtiene el valor de la propiedad nombresConyugue.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombresConyugue() {
        return nombresConyugue;
    }

    /**
     * Define el valor de la propiedad nombresConyugue.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombresConyugue(String value) {
        this.nombresConyugue = value;
    }

    /**
     * Obtiene el valor de la propiedad apellidoPaternoConyugue.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApellidoPaternoConyugue() {
        return apellidoPaternoConyugue;
    }

    /**
     * Define el valor de la propiedad apellidoPaternoConyugue.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApellidoPaternoConyugue(String value) {
        this.apellidoPaternoConyugue = value;
    }

    /**
     * Obtiene el valor de la propiedad apellidoMaternoConyugue.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApellidoMaternoConyugue() {
        return apellidoMaternoConyugue;
    }

    /**
     * Define el valor de la propiedad apellidoMaternoConyugue.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApellidoMaternoConyugue(String value) {
        this.apellidoMaternoConyugue = value;
    }

    /**
     * Obtiene el valor de la propiedad nacionalidadConyugue.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNacionalidadConyugue() {
        return nacionalidadConyugue;
    }

    /**
     * Define el valor de la propiedad nacionalidadConyugue.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNacionalidadConyugue(String value) {
        this.nacionalidadConyugue = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaNacimientoConyugue.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaNacimientoConyugue() {
        return fechaNacimientoConyugue;
    }

    /**
     * Define el valor de la propiedad fechaNacimientoConyugue.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaNacimientoConyugue(String value) {
        this.fechaNacimientoConyugue = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoDocumentoConyugue.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoDocumentoConyugue() {
        return tipoDocumentoConyugue;
    }

    /**
     * Define el valor de la propiedad tipoDocumentoConyugue.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoDocumentoConyugue(String value) {
        this.tipoDocumentoConyugue = value;
    }

    /**
     * Obtiene el valor de la propiedad nroDocumentoConyugue.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNroDocumentoConyugue() {
        return nroDocumentoConyugue;
    }

    /**
     * Define el valor de la propiedad nroDocumentoConyugue.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNroDocumentoConyugue(String value) {
        this.nroDocumentoConyugue = value;
    }

    /**
     * Obtiene el valor de la propiedad telefonoConyugue.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelefonoConyugue() {
        return telefonoConyugue;
    }

    /**
     * Define el valor de la propiedad telefonoConyugue.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelefonoConyugue(String value) {
        this.telefonoConyugue = value;
    }

    /**
     * Obtiene el valor de la propiedad generoConyugue.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGeneroConyugue() {
        return generoConyugue;
    }

    /**
     * Define el valor de la propiedad generoConyugue.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGeneroConyugue(String value) {
        this.generoConyugue = value;
    }

    /**
     * Obtiene el valor de la propiedad estadoCivilConyugue.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstadoCivilConyugue() {
        return estadoCivilConyugue;
    }

    /**
     * Define el valor de la propiedad estadoCivilConyugue.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstadoCivilConyugue(String value) {
        this.estadoCivilConyugue = value;
    }

    /**
     * Obtiene el valor de la propiedad ingresoNetoMensual.
     * 
     */
    public double getIngresoNetoMensual() {
        return ingresoNetoMensual;
    }

    /**
     * Define el valor de la propiedad ingresoNetoMensual.
     * 
     */
    public void setIngresoNetoMensual(double value) {
        this.ingresoNetoMensual = value;
    }

    /**
     * Obtiene el valor de la propiedad ingresoBrutoMensual.
     * 
     */
    public double getIngresoBrutoMensual() {
        return ingresoBrutoMensual;
    }

    /**
     * Define el valor de la propiedad ingresoBrutoMensual.
     * 
     */
    public void setIngresoBrutoMensual(double value) {
        this.ingresoBrutoMensual = value;
    }

    /**
     * Obtiene el valor de la propiedad entidadFinanciadora.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntidadFinanciadora() {
        return entidadFinanciadora;
    }

    /**
     * Define el valor de la propiedad entidadFinanciadora.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntidadFinanciadora(String value) {
        this.entidadFinanciadora = value;
    }

    /**
     * Obtiene el valor de la propiedad montoLineaCredito.
     * 
     */
    public double getMontoLineaCredito() {
        return montoLineaCredito;
    }

    /**
     * Define el valor de la propiedad montoLineaCredito.
     * 
     */
    public void setMontoLineaCredito(double value) {
        this.montoLineaCredito = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoPrestamo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoPrestamo() {
        return tipoPrestamo;
    }

    /**
     * Define el valor de la propiedad tipoPrestamo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoPrestamo(String value) {
        this.tipoPrestamo = value;
    }

    /**
     * Obtiene el valor de la propiedad empresaEsSocio.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmpresaEsSocio() {
        return empresaEsSocio;
    }

    /**
     * Define el valor de la propiedad empresaEsSocio.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmpresaEsSocio(String value) {
        this.empresaEsSocio = value;
    }

}
