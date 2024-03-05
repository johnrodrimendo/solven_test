package com.affirm.nosis;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "consulta",
    "parteXML",
    "parteHTML"
})
@XmlRootElement(name = "Respuesta")
public class NosisResult {

    @XmlElement(name = "Consulta")
    private Consulta consulta;

    @XmlElement(name = "ParteXML")
    private ParteXML parteXML;

    @XmlElement(name = "ParteHTML")
    private ParteHTML parteHTML;

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

    public ParteXML getParteXML() {
        return parteXML;
    }

    public void setParteXML(ParteXML parteXML) {
        this.parteXML = parteXML;
    }

    public ParteHTML getParteHTML() {
        return parteHTML;
    }

    public void setParteHTML(ParteHTML parteHTML) {
        this.parteHTML = parteHTML;
    }

    public Integer getScore() {
        Dato dato = parteXML.getDatos().stream().filter(d -> d.getClave().getPrefijo().equals("CD")).findFirst().orElse(null);

        if (dato != null) {
            Dato.CalculoCDA.Item item = dato.getCalculoCDA().getItems().stream().filter(
                    itm -> itm.getClave().equals("SCORE")
            ).findFirst().orElse(null);

            if (item != null) {
                return Integer.parseInt(item.getValor());
            }
        }

        return 0;
    }

    public Integer getCommitment() {

        if (parteXML == null || parteXML.getDatos() == null) {
            return null;
        }

        Dato dato = parteXML.getDatos().stream().filter(d -> d.getClave().getPrefijo().equals("CD")).findFirst().orElse(null);

        if (dato != null) {
            Dato.CalculoCDA.Item item = dato.getCalculoCDA().getItems().stream().filter(
                    itm -> itm.getClave().equals("COMPMENSUALES")
            ).findFirst().orElse(null);

            if (item != null) {
                return Integer.parseInt(item.getValor());
            }
        }

        return null;
    }

    public String getNSE() {

        if (parteXML == null || parteXML.getDatos() == null) {
            return null;
        }

        Dato dato = parteXML.getDatos().stream().filter(d -> d.getClave().getPrefijo().equals("CD")).findFirst().orElse(null);

        if (dato != null) {
            Dato.CalculoCDA.Item item = dato.getCalculoCDA().getItems().stream().filter(
                    itm -> itm.getClave().equals("NSE")
            ).findFirst().orElse(null);

            if (item != null) {
                return item.getValor();
            }
        }

        return null;
    }

    public String getCategoriaIva() {
        boolean esRelacionDependencia = parteXML.getDatos().stream().anyMatch(d -> d.getRelDependencia() != null && d.getRelDependencia().equalsIgnoreCase("si"));
        if(esRelacionDependencia){
            return "CF"; //Consumidor Final
        }
        boolean esJubilado = parteXML.getDatos().stream().anyMatch(d -> d.getJubilado() != null && d.getJubilado().equalsIgnoreCase("si"));
        if(esJubilado){
            return "CF"; //Consumidor Final
        }
        boolean esAutonomoMonotributista = parteXML.getDatos().stream().anyMatch(d -> d.getAutMonotrib() != null && d.getAutMonotrib().equalsIgnoreCase("si"));
        if (esAutonomoMonotributista) {
            Dato datoConImpuestos = parteXML.getDatos().stream().filter(d -> d.getImpuestos() != null && d.getImpuestos().getImps() != null).findFirst().orElse(null);
            boolean autonomo = datoConImpuestos.getImpuestos().getImps().stream().anyMatch(i -> i.getDescrip().toLowerCase().contains("Reg Trab Autonomo".toLowerCase()));
            if(autonomo){
                boolean iva = datoConImpuestos.getImpuestos().getImps().stream().anyMatch(i -> i.getDescrip().toLowerCase().equals("Iva".toLowerCase()));
                if(iva)
                    return "RI"; //Inscrito
                else
                    return "RN"; //No Inscripto
            }
            boolean monotributo = datoConImpuestos.getImpuestos().getImps().stream().anyMatch(i -> i.getDescrip().toLowerCase().contains("Monotributo".toLowerCase()));
            if(monotributo)
                return "MT"; //Monotributo
        }
        return "CF"; // DEfault is CF
    }
}