
package com.affirm.abaco.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Clase Java para eCredito complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="eCredito">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoCredito" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="monedaCredito" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="montoCredito" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="montoDesembolsar" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="numeroCuotas" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="razonCredito" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="tipoCredito" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="tea" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="tcea" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="comision" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="impuestoComision" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="totalComision" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="fechaFirma" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigoBanco" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="numeroCuentaAsociado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tipoCuentaAsociado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cuotas">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="cuota" type="{http://servicio.ws/}eCuota" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="codigoCliente" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigoCreditoCancelar" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="montoCreditoCancelar" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="urlDescargaDNI" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="urlDescargaSelfie" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="urlDescargaResumen" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="urlDescargaContrato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eCredito", propOrder = {
    "codigoCredito",
    "codigoCreditoAbaco",
    "monedaCredito",
    "montoCredito",
    "montoDesembolsar",
    "numeroCuotas",
    "razonCredito",
    "tipoCredito",
    "tea",
    "tcea",
    "comision",
    "impuestoComision",
    "totalComision",
    "fechaFirma",
    "codigoBanco",
    "numeroCuentaAsociado",
    "tipoCuentaAsociado",
    "cuotas",
    "codigoCliente",
    "codigoCreditoCancelar",
    "montoCreditoCancelar",
    "urlDescargaDNI",
    "urlDescargaSelfie",
    "urlDescargaResumen",
    "urlDescargaContrato"
})
public class ECredito {

    protected int codigoCredito;
    protected int codigoCreditoAbaco;
    @XmlElement(required = true)
    protected String monedaCredito;
    protected double montoCredito;
    protected double montoDesembolsar;
    protected int numeroCuotas;
    protected int razonCredito;
    protected int tipoCredito;
    protected double tea;
    protected double tcea;
    protected double comision;
    protected double impuestoComision;
    protected double totalComision;
    @XmlElement(required = true)
    protected String fechaFirma;
    protected int codigoBanco;
    @XmlElement(required = true)
    protected String numeroCuentaAsociado;
    @XmlElement(required = true)
    protected String tipoCuentaAsociado;
    @XmlElement(required = true)
    protected ECredito.Cuotas cuotas;
    @XmlElement(required = true)
    protected String codigoCliente;
    @XmlElement(required = true)
    protected String codigoCreditoCancelar;
    protected double montoCreditoCancelar;
    @XmlElement(required = true)
    protected String urlDescargaDNI;
    @XmlElement(required = true)
    protected String urlDescargaSelfie;
    @XmlElement(required = true)
    protected String urlDescargaResumen;
    @XmlElement(required = true)
    protected String urlDescargaContrato;

    @Override
    public String toString() {
        return "ECredito { " +
                "codigoCredito=" + codigoCredito +
                ", codigoCreditoAbaco=" + codigoCreditoAbaco +
                ", monedaCredito='" + monedaCredito + '\'' +
                ", montoCredito=" + montoCredito +
                ", montoDesembolsar=" + montoDesembolsar +
                ", numeroCuotas=" + numeroCuotas +
                ", razonCredito=" + razonCredito +
                ", tipoCredito=" + tipoCredito +
                ", tea=" + tea +
                ", tcea=" + tcea +
                ", comision=" + comision +
                ", impuestoComision=" + impuestoComision +
                ", totalComision=" + totalComision +
                ", fechaFirma='" + fechaFirma + '\'' +
                ", codigoBanco=" + codigoBanco +
                ", numeroCuentaAsociado='" + numeroCuentaAsociado + '\'' +
                ", tipoCuentaAsociado='" + tipoCuentaAsociado + '\'' +
                ", cuotas=" + cuotas +
                ", codigoCliente='" + codigoCliente + '\'' +
                ", codigoCreditoCancelar='" + codigoCreditoCancelar + '\'' +
                ", montoCreditoCancelar=" + montoCreditoCancelar +
                ", urlDescargaDNI='" + urlDescargaDNI + '\'' +
                ", urlDescargaSelfie='" + urlDescargaSelfie + '\'' +
                ", urlDescargaResumen='" + urlDescargaResumen + '\'' +
                ", urlDescargaContrato='" + urlDescargaContrato + '\'' +
                '}';
    }

    /**
     * Obtiene el valor de la propiedad codigoCredito.
     * 
     */
    public int getCodigoCredito() {
        return codigoCredito;
    }

    /**
     * Define el valor de la propiedad codigoCredito.
     * 
     */
    public void setCodigoCredito(int value) {
        this.codigoCredito = value;
    }

    public int getCodigoCreditoAbaco() {
        return codigoCreditoAbaco;
    }

    public void setCodigoCreditoAbaco(int codigoCreditoAbaco) {
        this.codigoCreditoAbaco = codigoCreditoAbaco;
    }

    /**
     * Obtiene el valor de la propiedad monedaCredito.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMonedaCredito() {
        return monedaCredito;
    }

    /**
     * Define el valor de la propiedad monedaCredito.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMonedaCredito(String value) {
        this.monedaCredito = value;
    }

    /**
     * Obtiene el valor de la propiedad montoCredito.
     * 
     */
    public double getMontoCredito() {
        return montoCredito;
    }

    /**
     * Define el valor de la propiedad montoCredito.
     * 
     */
    public void setMontoCredito(double value) {
        this.montoCredito = value;
    }

    /**
     * Obtiene el valor de la propiedad montoDesembolsar.
     * 
     */
    public double getMontoDesembolsar() {
        return montoDesembolsar;
    }

    /**
     * Define el valor de la propiedad montoDesembolsar.
     * 
     */
    public void setMontoDesembolsar(double value) {
        this.montoDesembolsar = value;
    }

    /**
     * Obtiene el valor de la propiedad numeroCuotas.
     * 
     */
    public int getNumeroCuotas() {
        return numeroCuotas;
    }

    /**
     * Define el valor de la propiedad numeroCuotas.
     * 
     */
    public void setNumeroCuotas(int value) {
        this.numeroCuotas = value;
    }

    /**
     * Obtiene el valor de la propiedad razonCredito.
     * 
     */
    public int getRazonCredito() {
        return razonCredito;
    }

    /**
     * Define el valor de la propiedad razonCredito.
     * 
     */
    public void setRazonCredito(int value) {
        this.razonCredito = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoCredito.
     * 
     */
    public int getTipoCredito() {
        return tipoCredito;
    }

    /**
     * Define el valor de la propiedad tipoCredito.
     * 
     */
    public void setTipoCredito(int value) {
        this.tipoCredito = value;
    }

    /**
     * Obtiene el valor de la propiedad tea.
     * 
     */
    public double getTea() {
        return tea;
    }

    /**
     * Define el valor de la propiedad tea.
     * 
     */
    public void setTea(double value) {
        this.tea = value;
    }

    /**
     * Obtiene el valor de la propiedad tcea.
     * 
     */
    public double getTcea() {
        return tcea;
    }

    /**
     * Define el valor de la propiedad tcea.
     * 
     */
    public void setTcea(double value) {
        this.tcea = value;
    }

    /**
     * Obtiene el valor de la propiedad comision.
     * 
     */
    public double getComision() {
        return comision;
    }

    /**
     * Define el valor de la propiedad comision.
     * 
     */
    public void setComision(double value) {
        this.comision = value;
    }

    /**
     * Obtiene el valor de la propiedad impuestoComision.
     * 
     */
    public double getImpuestoComision() {
        return impuestoComision;
    }

    /**
     * Define el valor de la propiedad impuestoComision.
     * 
     */
    public void setImpuestoComision(double value) {
        this.impuestoComision = value;
    }

    /**
     * Obtiene el valor de la propiedad totalComision.
     * 
     */
    public double getTotalComision() {
        return totalComision;
    }

    /**
     * Define el valor de la propiedad totalComision.
     * 
     */
    public void setTotalComision(double value) {
        this.totalComision = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaFirma.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaFirma() {
        return fechaFirma;
    }

    /**
     * Define el valor de la propiedad fechaFirma.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaFirma(String value) {
        this.fechaFirma = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoBanco.
     * 
     */
    public int getCodigoBanco() {
        return codigoBanco;
    }

    /**
     * Define el valor de la propiedad codigoBanco.
     * 
     */
    public void setCodigoBanco(int value) {
        this.codigoBanco = value;
    }

    /**
     * Obtiene el valor de la propiedad numeroCuentaAsociado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroCuentaAsociado() {
        return numeroCuentaAsociado;
    }

    /**
     * Define el valor de la propiedad numeroCuentaAsociado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroCuentaAsociado(String value) {
        this.numeroCuentaAsociado = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoCuentaAsociado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoCuentaAsociado() {
        return tipoCuentaAsociado;
    }

    /**
     * Define el valor de la propiedad tipoCuentaAsociado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoCuentaAsociado(String value) {
        this.tipoCuentaAsociado = value;
    }

    /**
     * Obtiene el valor de la propiedad cuotas.
     * 
     * @return
     *     possible object is
     *     {@link ECredito.Cuotas }
     *     
     */
    public ECredito.Cuotas getCuotas() {
        return cuotas;
    }

    /**
     * Define el valor de la propiedad cuotas.
     * 
     * @param value
     *     allowed object is
     *     {@link ECredito.Cuotas }
     *     
     */
    public void setCuotas(ECredito.Cuotas value) {
        this.cuotas = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoCliente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoCliente() {
        return codigoCliente;
    }

    /**
     * Define el valor de la propiedad codigoCliente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoCliente(String value) {
        this.codigoCliente = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoCreditoCancelar.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoCreditoCancelar() {
        return codigoCreditoCancelar;
    }

    /**
     * Define el valor de la propiedad codigoCreditoCancelar.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoCreditoCancelar(String value) {
        this.codigoCreditoCancelar = value;
    }

    /**
     * Obtiene el valor de la propiedad montoCreditoCancelar.
     * 
     */
    public double getMontoCreditoCancelar() {
        return montoCreditoCancelar;
    }

    /**
     * Define el valor de la propiedad montoCreditoCancelar.
     * 
     */
    public void setMontoCreditoCancelar(double value) {
        this.montoCreditoCancelar = value;
    }

    /**
     * Obtiene el valor de la propiedad urlDescargaDNI.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrlDescargaDNI() {
        return urlDescargaDNI;
    }

    /**
     * Define el valor de la propiedad urlDescargaDNI.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrlDescargaDNI(String value) {
        this.urlDescargaDNI = value;
    }

    /**
     * Obtiene el valor de la propiedad urlDescargaSelfie.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrlDescargaSelfie() {
        return urlDescargaSelfie;
    }

    /**
     * Define el valor de la propiedad urlDescargaSelfie.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrlDescargaSelfie(String value) {
        this.urlDescargaSelfie = value;
    }

    /**
     * Obtiene el valor de la propiedad urlDescargaResumen.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrlDescargaResumen() {
        return urlDescargaResumen;
    }

    /**
     * Define el valor de la propiedad urlDescargaResumen.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrlDescargaResumen(String value) {
        this.urlDescargaResumen = value;
    }

    /**
     * Obtiene el valor de la propiedad urlDescargaContrato.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrlDescargaContrato() {
        return urlDescargaContrato;
    }

    /**
     * Define el valor de la propiedad urlDescargaContrato.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrlDescargaContrato(String value) {
        this.urlDescargaContrato = value;
    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="cuota" type="{http://servicio.ws/}eCuota" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "cuota"
    })
    public static class Cuotas {

        protected List<ECuota> cuota;

        /**
         * Gets the value of the cuota property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the cuota property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCuota().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ECuota }
         * 
         * 
         */
        public List<ECuota> getCuota() {
            if (cuota == null) {
                cuota = new ArrayList<ECuota>();
            }
            return this.cuota;
        }

        public void setCuota(List<ECuota> cuota) {
            this.cuota = cuota;
        }

    }

}
