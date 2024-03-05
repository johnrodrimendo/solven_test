
package com.affirm.efectiva.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para MovilCMERpt complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="MovilCMERpt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TipoLinea" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CodigoError" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="DescripcionError" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ResultadoEvaluacion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CodigoRechazo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DescripcionRechazo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InicialMinima" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="PlazoMaximo" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="Aval" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CodigoProceso" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="DescripcionProceso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LineaDisponible" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="Cme" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="File1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="File2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="File3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="File4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="File5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MovilCMERpt", propOrder = {
    "tipoLinea",
    "codigoError",
    "descripcionError",
    "resultadoEvaluacion",
    "codigoRechazo",
    "descripcionRechazo",
    "inicialMinima",
    "plazoMaximo",
    "aval",
    "codigoProceso",
    "descripcionProceso",
    "lineaDisponible",
    "cme",
    "file1",
    "file2",
    "file3",
    "file4",
    "file5"
})
public class MovilCMERpt {

    @XmlElement(name = "TipoLinea")
    protected int tipoLinea;
    @XmlElement(name = "CodigoError")
    protected int codigoError;
    @XmlElement(name = "DescripcionError")
    protected String descripcionError;
    @XmlElement(name = "ResultadoEvaluacion")
    protected String resultadoEvaluacion;
    @XmlElement(name = "CodigoRechazo")
    protected String codigoRechazo;
    @XmlElement(name = "DescripcionRechazo")
    protected String descripcionRechazo;
    @XmlElement(name = "InicialMinima")
    protected double inicialMinima;
    @XmlElement(name = "PlazoMaximo")
    protected double plazoMaximo;
    @XmlElement(name = "Aval")
    protected String aval;
    @XmlElement(name = "CodigoProceso")
    protected int codigoProceso;
    @XmlElement(name = "DescripcionProceso")
    protected String descripcionProceso;
    @XmlElement(name = "LineaDisponible")
    protected double lineaDisponible;
    @XmlElement(name = "Cme")
    protected double cme;
    @XmlElement(name = "File1")
    protected String file1;
    @XmlElement(name = "File2")
    protected String file2;
    @XmlElement(name = "File3")
    protected String file3;
    @XmlElement(name = "File4")
    protected String file4;
    @XmlElement(name = "File5")
    protected String file5;

    @Override
    public String toString() {
        return "MovilCMERpt{" +
                "tipoLinea=" + tipoLinea +
                ", codigoError=" + codigoError +
                ", descripcionError='" + descripcionError + '\'' +
                ", resultadoEvaluacion='" + resultadoEvaluacion + '\'' +
                ", codigoRechazo='" + codigoRechazo + '\'' +
                ", descripcionRechazo='" + descripcionRechazo + '\'' +
                ", inicialMinima=" + inicialMinima +
                ", plazoMaximo=" + plazoMaximo +
                ", aval='" + aval + '\'' +
                ", codigoProceso=" + codigoProceso +
                ", descripcionProceso='" + descripcionProceso + '\'' +
                ", lineaDisponible=" + lineaDisponible +
                ", cme=" + cme +
                ", file1='" + file1 + '\'' +
                ", file2='" + file2 + '\'' +
                ", file3='" + file3 + '\'' +
                ", file4='" + file4 + '\'' +
                ", file5='" + file5 + '\'' +
                '}';
    }

    /**
     * Obtiene el valor de la propiedad tipoLinea.
     * 
     */
    public int getTipoLinea() {
        return tipoLinea;
    }

    /**
     * Define el valor de la propiedad tipoLinea.
     * 
     */
    public void setTipoLinea(int value) {
        this.tipoLinea = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoError.
     * 
     */
    public int getCodigoError() {
        return codigoError;
    }

    /**
     * Define el valor de la propiedad codigoError.
     * 
     */
    public void setCodigoError(int value) {
        this.codigoError = value;
    }

    /**
     * Obtiene el valor de la propiedad descripcionError.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcionError() {
        return descripcionError;
    }

    /**
     * Define el valor de la propiedad descripcionError.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcionError(String value) {
        this.descripcionError = value;
    }

    /**
     * Obtiene el valor de la propiedad resultadoEvaluacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultadoEvaluacion() {
        return resultadoEvaluacion;
    }

    /**
     * Define el valor de la propiedad resultadoEvaluacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultadoEvaluacion(String value) {
        this.resultadoEvaluacion = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoRechazo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoRechazo() {
        return codigoRechazo;
    }

    /**
     * Define el valor de la propiedad codigoRechazo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoRechazo(String value) {
        this.codigoRechazo = value;
    }

    /**
     * Obtiene el valor de la propiedad descripcionRechazo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcionRechazo() {
        return descripcionRechazo;
    }

    /**
     * Define el valor de la propiedad descripcionRechazo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcionRechazo(String value) {
        this.descripcionRechazo = value;
    }

    /**
     * Obtiene el valor de la propiedad inicialMinima.
     * 
     */
    public double getInicialMinima() {
        return inicialMinima;
    }

    /**
     * Define el valor de la propiedad inicialMinima.
     * 
     */
    public void setInicialMinima(double value) {
        this.inicialMinima = value;
    }

    /**
     * Obtiene el valor de la propiedad plazoMaximo.
     * 
     */
    public double getPlazoMaximo() {
        return plazoMaximo;
    }

    /**
     * Define el valor de la propiedad plazoMaximo.
     * 
     */
    public void setPlazoMaximo(double value) {
        this.plazoMaximo = value;
    }

    /**
     * Obtiene el valor de la propiedad aval.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAval() {
        return aval;
    }

    /**
     * Define el valor de la propiedad aval.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAval(String value) {
        this.aval = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoProceso.
     * 
     */
    public int getCodigoProceso() {
        return codigoProceso;
    }

    /**
     * Define el valor de la propiedad codigoProceso.
     * 
     */
    public void setCodigoProceso(int value) {
        this.codigoProceso = value;
    }

    /**
     * Obtiene el valor de la propiedad descripcionProceso.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcionProceso() {
        return descripcionProceso;
    }

    /**
     * Define el valor de la propiedad descripcionProceso.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcionProceso(String value) {
        this.descripcionProceso = value;
    }

    /**
     * Obtiene el valor de la propiedad lineaDisponible.
     * 
     */
    public double getLineaDisponible() {
        return lineaDisponible;
    }

    /**
     * Define el valor de la propiedad lineaDisponible.
     * 
     */
    public void setLineaDisponible(double value) {
        this.lineaDisponible = value;
    }

    /**
     * Obtiene el valor de la propiedad cme.
     * 
     */
    public double getCme() {
        return cme;
    }

    /**
     * Define el valor de la propiedad cme.
     * 
     */
    public void setCme(double value) {
        this.cme = value;
    }

    /**
     * Obtiene el valor de la propiedad file1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFile1() {
        return file1;
    }

    /**
     * Define el valor de la propiedad file1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFile1(String value) {
        this.file1 = value;
    }

    /**
     * Obtiene el valor de la propiedad file2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFile2() {
        return file2;
    }

    /**
     * Define el valor de la propiedad file2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFile2(String value) {
        this.file2 = value;
    }

    /**
     * Obtiene el valor de la propiedad file3.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFile3() {
        return file3;
    }

    /**
     * Define el valor de la propiedad file3.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFile3(String value) {
        this.file3 = value;
    }

    /**
     * Obtiene el valor de la propiedad file4.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFile4() {
        return file4;
    }

    /**
     * Define el valor de la propiedad file4.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFile4(String value) {
        this.file4 = value;
    }

    /**
     * Obtiene el valor de la propiedad file5.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFile5() {
        return file5;
    }

    /**
     * Define el valor de la propiedad file5.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFile5(String value) {
        this.file5 = value;
    }

}
