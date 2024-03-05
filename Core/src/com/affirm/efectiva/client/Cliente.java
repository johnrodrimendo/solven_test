
package com.affirm.efectiva.client;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para Cliente complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Cliente">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DniVendedor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Plaza" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Producto" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Canal" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="DniCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MontoFinanciar" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="TipoAfiliacion" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="TipoCliente" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OficioActividad" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Perfil" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SituacionLaboral" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CondicionLaboral" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ActividadComercial" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NegocioPropio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PuestoFijo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AntiguedadLaboral" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="TipoIngresos" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OtrosIngresos" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="IngresosConyuge" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="IngresosComplementoRenta" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="TipoVivienda" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="AntiguedadDomiciliaria" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="Ingresos" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="EstadoCivil" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumeroHijos" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Cliente", propOrder = {
    "dniVendedor",
    "plaza",
    "producto",
    "canal",
    "dniCliente",
    "montoFinanciar",
    "tipoAfiliacion",
    "tipoCliente",
    "oficioActividad",
    "perfil",
    "situacionLaboral",
    "condicionLaboral",
    "actividadComercial",
    "negocioPropio",
    "puestoFijo",
    "antiguedadLaboral",
    "tipoIngresos",
    "otrosIngresos",
    "ingresosConyuge",
    "ingresosComplementoRenta",
    "tipoVivienda",
    "antiguedadDomiciliaria",
    "ingresos",
    "estadoCivil",
    "numeroHijos"
})
public class Cliente {

    @XmlElement(name = "DniVendedor")
    protected String dniVendedor;
    @XmlElement(name = "Plaza")
    protected int plaza;
    @XmlElement(name = "Producto")
    protected int producto;
    @XmlElement(name = "Canal")
    protected int canal;
    @XmlElement(name = "DniCliente")
    protected String dniCliente;
    @XmlElement(name = "MontoFinanciar")
    protected double montoFinanciar;
    @XmlElement(name = "TipoAfiliacion")
    protected int tipoAfiliacion;
    @XmlElement(name = "TipoCliente")
    protected int tipoCliente;
    @XmlElement(name = "OficioActividad")
    protected int oficioActividad;
    @XmlElement(name = "Perfil")
    protected int perfil;
    @XmlElement(name = "SituacionLaboral")
    protected int situacionLaboral;
    @XmlElement(name = "CondicionLaboral")
    protected int condicionLaboral;
    @XmlElement(name = "ActividadComercial")
    protected int actividadComercial;
    @XmlElement(name = "NegocioPropio")
    protected String negocioPropio;
    @XmlElement(name = "PuestoFijo")
    protected String puestoFijo;
    @XmlElement(name = "AntiguedadLaboral", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar antiguedadLaboral;
    @XmlElement(name = "TipoIngresos")
    protected int tipoIngresos;
    @XmlElement(name = "OtrosIngresos")
    protected double otrosIngresos;
    @XmlElement(name = "IngresosConyuge")
    protected double ingresosConyuge;
    @XmlElement(name = "IngresosComplementoRenta")
    protected double ingresosComplementoRenta;
    @XmlElement(name = "TipoVivienda")
    protected int tipoVivienda;
    @XmlElement(name = "AntiguedadDomiciliaria", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar antiguedadDomiciliaria;
    @XmlElement(name = "Ingresos")
    protected double ingresos;
    @XmlElement(name = "EstadoCivil")
    protected int estadoCivil;
    @XmlElement(name = "NumeroHijos")
    protected int numeroHijos;

    /**
     * Obtiene el valor de la propiedad dniVendedor.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDniVendedor() {
        return dniVendedor;
    }

    /**
     * Define el valor de la propiedad dniVendedor.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDniVendedor(String value) {
        this.dniVendedor = value;
    }

    /**
     * Obtiene el valor de la propiedad plaza.
     * 
     */
    public int getPlaza() {
        return plaza;
    }

    /**
     * Define el valor de la propiedad plaza.
     * 
     */
    public void setPlaza(int value) {
        this.plaza = value;
    }

    /**
     * Obtiene el valor de la propiedad producto.
     * 
     */
    public int getProducto() {
        return producto;
    }

    /**
     * Define el valor de la propiedad producto.
     * 
     */
    public void setProducto(int value) {
        this.producto = value;
    }

    /**
     * Obtiene el valor de la propiedad canal.
     * 
     */
    public int getCanal() {
        return canal;
    }

    /**
     * Define el valor de la propiedad canal.
     * 
     */
    public void setCanal(int value) {
        this.canal = value;
    }

    /**
     * Obtiene el valor de la propiedad dniCliente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDniCliente() {
        return dniCliente;
    }

    /**
     * Define el valor de la propiedad dniCliente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDniCliente(String value) {
        this.dniCliente = value;
    }

    /**
     * Obtiene el valor de la propiedad montoFinanciar.
     * 
     */
    public double getMontoFinanciar() {
        return montoFinanciar;
    }

    /**
     * Define el valor de la propiedad montoFinanciar.
     * 
     */
    public void setMontoFinanciar(double value) {
        this.montoFinanciar = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoAfiliacion.
     * 
     */
    public int getTipoAfiliacion() {
        return tipoAfiliacion;
    }

    /**
     * Define el valor de la propiedad tipoAfiliacion.
     * 
     */
    public void setTipoAfiliacion(int value) {
        this.tipoAfiliacion = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoCliente.
     * 
     */
    public int getTipoCliente() {
        return tipoCliente;
    }

    /**
     * Define el valor de la propiedad tipoCliente.
     * 
     */
    public void setTipoCliente(int value) {
        this.tipoCliente = value;
    }

    /**
     * Obtiene el valor de la propiedad oficioActividad.
     * 
     */
    public int getOficioActividad() {
        return oficioActividad;
    }

    /**
     * Define el valor de la propiedad oficioActividad.
     * 
     */
    public void setOficioActividad(int value) {
        this.oficioActividad = value;
    }

    /**
     * Obtiene el valor de la propiedad perfil.
     * 
     */
    public int getPerfil() {
        return perfil;
    }

    /**
     * Define el valor de la propiedad perfil.
     * 
     */
    public void setPerfil(int value) {
        this.perfil = value;
    }

    /**
     * Obtiene el valor de la propiedad situacionLaboral.
     * 
     */
    public int getSituacionLaboral() {
        return situacionLaboral;
    }

    /**
     * Define el valor de la propiedad situacionLaboral.
     * 
     */
    public void setSituacionLaboral(int value) {
        this.situacionLaboral = value;
    }

    /**
     * Obtiene el valor de la propiedad condicionLaboral.
     * 
     */
    public int getCondicionLaboral() {
        return condicionLaboral;
    }

    /**
     * Define el valor de la propiedad condicionLaboral.
     * 
     */
    public void setCondicionLaboral(int value) {
        this.condicionLaboral = value;
    }

    /**
     * Obtiene el valor de la propiedad actividadComercial.
     * 
     */
    public int getActividadComercial() {
        return actividadComercial;
    }

    /**
     * Define el valor de la propiedad actividadComercial.
     * 
     */
    public void setActividadComercial(int value) {
        this.actividadComercial = value;
    }

    /**
     * Obtiene el valor de la propiedad negocioPropio.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNegocioPropio() {
        return negocioPropio;
    }

    /**
     * Define el valor de la propiedad negocioPropio.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNegocioPropio(String value) {
        this.negocioPropio = value;
    }

    /**
     * Obtiene el valor de la propiedad puestoFijo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPuestoFijo() {
        return puestoFijo;
    }

    /**
     * Define el valor de la propiedad puestoFijo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPuestoFijo(String value) {
        this.puestoFijo = value;
    }

    /**
     * Obtiene el valor de la propiedad antiguedadLaboral.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAntiguedadLaboral() {
        return antiguedadLaboral;
    }

    /**
     * Define el valor de la propiedad antiguedadLaboral.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAntiguedadLaboral(XMLGregorianCalendar value) {
        this.antiguedadLaboral = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoIngresos.
     * 
     */
    public int getTipoIngresos() {
        return tipoIngresos;
    }

    /**
     * Define el valor de la propiedad tipoIngresos.
     * 
     */
    public void setTipoIngresos(int value) {
        this.tipoIngresos = value;
    }

    /**
     * Obtiene el valor de la propiedad otrosIngresos.
     * 
     */
    public double getOtrosIngresos() {
        return otrosIngresos;
    }

    /**
     * Define el valor de la propiedad otrosIngresos.
     * 
     */
    public void setOtrosIngresos(double value) {
        this.otrosIngresos = value;
    }

    /**
     * Obtiene el valor de la propiedad ingresosConyuge.
     * 
     */
    public double getIngresosConyuge() {
        return ingresosConyuge;
    }

    /**
     * Define el valor de la propiedad ingresosConyuge.
     * 
     */
    public void setIngresosConyuge(double value) {
        this.ingresosConyuge = value;
    }

    /**
     * Obtiene el valor de la propiedad ingresosComplementoRenta.
     * 
     */
    public double getIngresosComplementoRenta() {
        return ingresosComplementoRenta;
    }

    /**
     * Define el valor de la propiedad ingresosComplementoRenta.
     * 
     */
    public void setIngresosComplementoRenta(double value) {
        this.ingresosComplementoRenta = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoVivienda.
     * 
     */
    public int getTipoVivienda() {
        return tipoVivienda;
    }

    /**
     * Define el valor de la propiedad tipoVivienda.
     * 
     */
    public void setTipoVivienda(int value) {
        this.tipoVivienda = value;
    }

    /**
     * Obtiene el valor de la propiedad antiguedadDomiciliaria.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAntiguedadDomiciliaria() {
        return antiguedadDomiciliaria;
    }

    /**
     * Define el valor de la propiedad antiguedadDomiciliaria.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAntiguedadDomiciliaria(XMLGregorianCalendar value) {
        this.antiguedadDomiciliaria = value;
    }

    /**
     * Obtiene el valor de la propiedad ingresos.
     * 
     */
    public double getIngresos() {
        return ingresos;
    }

    /**
     * Define el valor de la propiedad ingresos.
     * 
     */
    public void setIngresos(double value) {
        this.ingresos = value;
    }

    /**
     * Obtiene el valor de la propiedad estadoCivil.
     * 
     */
    public int getEstadoCivil() {
        return estadoCivil;
    }

    /**
     * Define el valor de la propiedad estadoCivil.
     * 
     */
    public void setEstadoCivil(int value) {
        this.estadoCivil = value;
    }

    /**
     * Obtiene el valor de la propiedad numeroHijos.
     * 
     */
    public int getNumeroHijos() {
        return numeroHijos;
    }

    /**
     * Define el valor de la propiedad numeroHijos.
     * 
     */
    public void setNumeroHijos(int value) {
        this.numeroHijos = value;
    }

}
