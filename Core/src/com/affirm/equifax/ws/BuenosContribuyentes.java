
package com.affirm.equifax.ws;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


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
 *         &lt;element name="Contribuyente" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="CodigoEntidad" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="NombreEntidad" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Conceptos">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Concepto" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "contribuyente"
})
@XmlRootElement(name = "BuenosContribuyentes")
public class BuenosContribuyentes {

    @XmlElement(name = "Contribuyente", required = true)
    protected List<BuenosContribuyentes.Contribuyente> contribuyente;

    /**
     * Gets the value of the contribuyente property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contribuyente property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContribuyente().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BuenosContribuyentes.Contribuyente }
     * 
     * 
     */
    public List<BuenosContribuyentes.Contribuyente> getContribuyente() {
        if (contribuyente == null) {
            contribuyente = new ArrayList<BuenosContribuyentes.Contribuyente>();
        }
        return this.contribuyente;
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
     *         &lt;element name="CodigoEntidad" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="NombreEntidad" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Conceptos">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Concepto" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
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
        "codigoEntidad",
        "nombreEntidad",
        "conceptos"
    })
    public static class Contribuyente {

        @XmlElement(name = "CodigoEntidad", required = true)
        protected String codigoEntidad;
        @XmlElement(name = "NombreEntidad", required = true)
        protected String nombreEntidad;
        @XmlElement(name = "Conceptos", required = true)
        protected BuenosContribuyentes.Contribuyente.Conceptos conceptos;

        /**
         * Obtiene el valor de la propiedad codigoEntidad.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCodigoEntidad() {
            return codigoEntidad;
        }

        /**
         * Define el valor de la propiedad codigoEntidad.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCodigoEntidad(String value) {
            this.codigoEntidad = value;
        }

        /**
         * Obtiene el valor de la propiedad nombreEntidad.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNombreEntidad() {
            return nombreEntidad;
        }

        /**
         * Define el valor de la propiedad nombreEntidad.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNombreEntidad(String value) {
            this.nombreEntidad = value;
        }

        /**
         * Obtiene el valor de la propiedad conceptos.
         * 
         * @return
         *     possible object is
         *     {@link BuenosContribuyentes.Contribuyente.Conceptos }
         *     
         */
        public BuenosContribuyentes.Contribuyente.Conceptos getConceptos() {
            return conceptos;
        }

        /**
         * Define el valor de la propiedad conceptos.
         * 
         * @param value
         *     allowed object is
         *     {@link BuenosContribuyentes.Contribuyente.Conceptos }
         *     
         */
        public void setConceptos(BuenosContribuyentes.Contribuyente.Conceptos value) {
            this.conceptos = value;
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
         *         &lt;element name="Concepto" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
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
            "concepto"
        })
        public static class Conceptos {

            @XmlElement(name = "Concepto", required = true)
            protected List<String> concepto;

            /**
             * Gets the value of the concepto property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the concepto property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getConcepto().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link String }
             * 
             * 
             */
            public List<String> getConcepto() {
                if (concepto == null) {
                    concepto = new ArrayList<String>();
                }
                return this.concepto;
            }

        }

    }

}
