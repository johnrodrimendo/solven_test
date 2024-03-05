package com.affirm.nosis;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Dato")
public class Dato {
    // Prefijo VI - Verificacion de Identidad

    @XmlElement(name = "Clave")
    private Clave clave;

    @XmlElement(name = "RZ")
    private String rz;

    @XmlElement(name = "Doc")
    private String doc;

    @XmlElement(name = "Apellido")
    private String apellido;

    @XmlElement(name = "Nombre")
    private String nombre;

    @XmlElement(name = "Doc_Tipo")
    private String docTipo;

    @XmlElement(name = "Tipo")
    private String tipo;

    @XmlElement(name = "TipoVD")
    private String tipoVD;

    @XmlElement(name = "FecAfip")
    private String fecAfip;

    @XmlElement(name = "Sexo")
    private String sexo;

    @XmlElement(name = "FecNac")
    private String fecNac;

    @XmlElement(name = "Edad")
    private String edad;

    @XmlElement(name = "Act01")
    private String act01;

    @XmlElement(name = "Act01_Descrip")
    private String act01Descrip;

    @XmlElement(name = "CLANAE_Nivel3_Cod")
    private String clanaeNivel3Cod;

    @XmlElement(name = "CLANAE_Nivel3_Desc")
    private String clanaeNivel3Desc;

    @XmlElement(name = "CLANAE_Nivel5_Cod")
    private String clanaeNivel5Cod;

    @XmlElement(name = "CLANAE_Nivel5_Desc")
    private String clanaeNivel5Desc;

    @XmlElement(name = "CatMonotributo")
    private String catMonotributo;

    @XmlElement(name = "RelDependencia")
    private String relDependencia;

    @XmlElement(name = "Aut_Monotrib")
    private String autMonotrib;

    @XmlElement(name = "SinActividad")
    private String sinActividad;

    @XmlElement(name = "DomFiscal")
    private DomFiscal domFiscal;

    @XmlElement(name = "DomAlternativos")
    private DomAlternativos domAlternativos;

    @XmlElement(name = "AgenciaAFIP")
    private AgenciaAFIP agenciaAFIP;

    @XmlElement(name = "Impuestos")
    private Impuestos impuestos;

    @XmlElement(name = "SSSalud")
    private SSSalud ssSalud;

    @XmlElement(name = "Beneficiarios")
    private Beneficiarios beneficiarios;

    @XmlElement(name = "IVA")
    private String iva;

    @XmlElement(name = "Jubilado")
    private String jubilado;

    public Clave getClave() { return clave; }

    public void setClave(Clave clave) { this.clave = clave; }

    public String getRz() { return rz; }

    public void setRz(String rz) { this.rz = rz; }

    public String getDoc() { return doc; }

    public void setDoc(String doc) { this.doc = doc; }

    public String getApellido() { return apellido; }

    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDocTipo() { return docTipo; }

    public void setDocTipo(String docTipo) { this.docTipo = docTipo; }

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getTipoVD() { return tipoVD; }

    public void setTipoVD(String tipoVD) { this.tipoVD = tipoVD; }

    public String getFecAfip() { return fecAfip; }

    public void setFecAfip(String fecAfip) { this.fecAfip = fecAfip; }

    public String getSexo() { return sexo; }

    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getFecNac() { return fecNac; }

    public void setFecNac(String fecNac) { this.fecNac = fecNac; }

    public String getEdad() { return edad; }

    public void setEdad(String edad) { this.edad = edad; }

    public String getAct01() { return act01; }

    public void setAct01(String act01) { this.act01 = act01; }

    public String getAct01Descrip() { return act01Descrip; }

    public void setAct01Descrip(String act01Descrip) { this.act01Descrip = act01Descrip; }

    public String getClanaeNivel3Cod() { return clanaeNivel3Cod; }

    public void setClanaeNivel3Cod(String clanaeNivel3Cod) { this.clanaeNivel3Cod = clanaeNivel3Cod; }

    public String getClanaeNivel3Desc() { return clanaeNivel3Desc; }

    public void setClanaeNivel3Desc(String clanaeNivel3Desc) { this.clanaeNivel3Desc = clanaeNivel3Desc; }

    public String getClanaeNivel5Cod() { return clanaeNivel5Cod; }

    public void setClanaeNivel5Cod(String clanaeNivel5Cod) { this.clanaeNivel5Cod = clanaeNivel5Cod; }

    public String getClanaeNivel5Desc() { return clanaeNivel5Desc; }

    public void setClanaeNivel5Desc(String clanaeNivel5Desc) { this.clanaeNivel5Desc = clanaeNivel5Desc; }

    public String getCatMonotributo() { return catMonotributo; }

    public void setCatMonotributo(String catMonotributo) { this.catMonotributo = catMonotributo; }

    public String getRelDependencia() { return relDependencia; }

    public void setRelDependencia(String relDependencia) { this.relDependencia = relDependencia; }

    public String getAutMonotrib() { return autMonotrib; }

    public void setAutMonotrib(String autMonotrib) { this.autMonotrib = autMonotrib; }

    public String getSinActividad() { return sinActividad; }

    public void setSinActividad(String sinActividad) { this.sinActividad = sinActividad; }

    public DomFiscal getDomFiscal() { return domFiscal; }

    public void setDomFiscal(DomFiscal domFiscal) { this.domFiscal = domFiscal; }

    public DomAlternativos getDomAlternativos() { return domAlternativos; }

    public void setDomAlternativos(DomAlternativos domAlternativos) { this.domAlternativos = domAlternativos; }

    public AgenciaAFIP getAgenciaAFIP() { return agenciaAFIP; }

    public void setAgenciaAFIP(AgenciaAFIP agenciaAFIP) { this.agenciaAFIP = agenciaAFIP; }

    public Impuestos getImpuestos() { return impuestos; }

    public void setImpuestos(Impuestos impuestos) { this.impuestos = impuestos; }

    public SSSalud getSsSalud() { return ssSalud; }

    public void setSsSalud(SSSalud ssSalud) { this.ssSalud = ssSalud; }

    public Beneficiarios getBeneficiarios() { return beneficiarios; }

    public void setBeneficiarios(Beneficiarios beneficiarios) { this.beneficiarios = beneficiarios; }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "DomFiscal")
    public static class DomFiscal {
        @XmlElement(name = "Dom")
        private String dom;

        @XmlElement(name = "Loc")
        private String loc;

        @XmlElement(name = "CP")
        private String cp;

        @XmlElement(name = "Prov")
        private String prov;

        public String getDom() { return dom; }

        public void setDom(String dom) { this.dom = dom; }

        public String getLoc() { return loc; }

        public void setLoc(String loc) { this.loc = loc; }

        public String getCp() { return cp; }

        public void setCp(String cp) { this.cp = cp; }

        public String getProv() { return prov; }

        public void setProv(String prov) { this.prov = prov; }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "DomAlternativos")
    public static class DomAlternativos {
        @XmlElement(name = "Doms")
        private Doms doms;

        @XmlElement(name = "Tels")
        private Tels tels;

        public Doms getDoms() { return doms; }

        public void setDoms(Doms doms) { this.doms = doms; }

        public Tels getTels() { return tels; }

        public void setTels(Tels tels) { this.tels = tels; }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "Doms")
        public static class Doms {
            @XmlElement(name = "Dom")
            List<String> dom;

            public List<String> getDom() { return dom; }

            public void setDom(List<String> dom) { this.dom = dom; }
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "Tels")
        public static class Tels {
            @XmlElement(name = "Tel")
            List<String> tel;

            public List<String> getTel() {return tel; }

            public void setTel(List<String> tel) { this.tel = tel; }
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "AgenciaAFIP")
    public static class AgenciaAFIP {
        @XmlElement(name = "Nro")
        private String nro;
        @XmlElement(name = "Dom")
        private String dom;
        @XmlElement(name = "Loc")
        private String loc;

        public String getNro() { return nro; }

        public void setNro(String nro) { this.nro = nro; }

        public String getDom() { return dom; }

        public void setDom(String dom) { this.dom = dom; }

        public String getLoc() { return loc; }

        public void setLoc(String loc) { this.loc = loc; }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "Impuestos")
    public static class Impuestos {
        @XmlElement(name = "Imp")
        private List<Imp> imps;

        @XmlAttribute(name = "FechaAct")
        private String fechaAct;

        public List<Imp> getImps() { return imps; }

        public void setImps(List<Imp> imps) { this.imps = imps; }

        public String getFechaAct() { return fechaAct; }

        public void setFechaAct(String fechaAct) { this.fechaAct = fechaAct; }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "Imp")
        public static class Imp {
            @XmlAttribute(name = "Descrip")
            private String descrip;

            @XmlAttribute(name = "FecAlta")
            private String fecAlta;

            public String getDescrip() { return descrip; }

            public void setDescrip(String descrip) { this.descrip = descrip; }

            public String getFecAlta() { return fecAlta; }

            public void setFecAlta(String fecAlta) { this.fecAlta = fecAlta; }
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "SSSalud")
    public static class SSSalud {
        @XmlElement(name = "ObraSoc")
        private ObraSoc obraSoc;

        public ObraSoc getObraSoc() { return obraSoc; }

        public void setObraSoc(ObraSoc obraSoc) { this.obraSoc = obraSoc; }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "ObraSoc")
        public static class ObraSoc {

            @XmlElement(name = "Descrip")
            private String descrip;

            @XmlElement(name = "FecAlta")
            private String fecAlta;

            @XmlElement(name = "Titular_Parentesco")
            private String titularParentesco;

            @XmlElement(name = "Titular_Condicion")
            private String titularCondicion;

            @XmlElement(name = "UltPeriodo")
            private String ultPeriodo;

            @XmlElement(name = "FecAct")
            private String fecAct;

            public String getDescrip() { return descrip; }

            public void setDescrip(String descrip) { this.descrip = descrip; }

            public String getFecAlta() { return fecAlta; }

            public void setFecAlta(String fecAlta) { this.fecAlta = fecAlta; }

            public String getTitularParentesco() { return titularParentesco; }

            public void setTitularParentesco(String titularParentesco) { this.titularParentesco = titularParentesco; }

            public String getTitularCondicion() { return titularCondicion; }

            public void setTitularCondicion(String titularCondicion) { this.titularCondicion = titularCondicion; }

            public String getUltPeriodo() { return ultPeriodo; }

            public void setUltPeriodo(String ultPeriodo) { this.ultPeriodo = ultPeriodo; }

            public String getFecAct() { return fecAct; }

            public void setFecAct(String fecAct) { this.fecAct = fecAct; }
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "Beneficiarios")
    public static class Beneficiarios {
        @XmlElement(name = "Mensaje")
        private String mensaje;

        @XmlElement(name = "FecAct")
        private String fecAct;

        public String getMensaje() { return mensaje; }

        public void setMensaje(String mensaje) { this.mensaje = mensaje; }

        public String getFecAct() { return fecAct; }

        public void setFecAct(String fecAct) { this.fecAct = fecAct; }
    }

    // CD - Criterio de aceptacion
    @XmlElement(name = "CalculoCDA")
    private CalculoCDA calculoCDA;

    public CalculoCDA getCalculoCDA() { return calculoCDA; }

    public void setCalculoCDA(CalculoCDA calculoCDA) { this.calculoCDA = calculoCDA; }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "CalculoCDA")
    public static class CalculoCDA {
        @XmlAttribute(name = "Titulo")
        private String titulo;

        @XmlAttribute(name = "NroCDA")
        private String nroCDA;

        @XmlAttribute(name = "Version")
        private String version;

        @XmlAttribute(name = "Fecha")
        private String fecha;

        @XmlElement(name = "Documento")
        private String documento;

        @XmlElement(name = "RazonSocial")
        private String razonSocial;

        @XmlElement(name = "Item")
        private List<Item> items;

        public String getTitulo() { return titulo; }

        public void setTitulo(String titulo) { this.titulo = titulo; }

        public String getNroCDA() { return nroCDA; }

        public void setNroCDA(String nroCDA) { this.nroCDA = nroCDA; }

        public String getVersion() { return version; }

        public void setVersion(String version) { this.version = version; }

        public String getFecha() { return fecha; }

        public void setFecha(String fecha) { this.fecha = fecha; }

        public String getDocumento() { return documento; }

        public void setDocumento(String documento) { this.documento = documento; }

        public String getRazonSocial() { return razonSocial; }

        public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

        public List<Item> getItems() { return items; }

        public void setItems(List<Item> items) { this.items = items; }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "Item")
        public static class Item {
            @XmlAttribute(name = "Nro")
            private String nro;

            @XmlAttribute(name = "Clave")
            private String clave;

            @XmlElement(name = "Descrip")
            private String descrip;

            @XmlElement(name = "Valor")
            private String valor;

            @XmlElement(name = "Detalle")
            private String detalle;

            public String getNro() { return nro; }

            public void setNro(String nro) { this.nro = nro; }

            public String getClave() { return clave; }

            public void setClave(String clave) { this.clave = clave; }

            public String getDescrip() { return descrip; }

            public void setDescrip(String descrip) { this.descrip = descrip; }

            public String getValor() { return valor; }

            public void setValor(String valor) { this.valor = valor; }

            public String getDetalle() { return detalle; }

            public void setDetalle(String detalle) { this.detalle = detalle; }
        }
    }

    // RD - relacion de dependencia
    @XmlElement(name = "Cuil")
    private String cuil;

    @XmlElement(name = "Nov")
    private String nov; // Tambien en SR

    @XmlElement(name = "FecAct")
    private String fecAct;

    @XmlElement(name = "TipoDoc")
    private String tipoDoc;

    @XmlElement(name = "FecUltConsulta")
    private String fecUltConsulta;

    @XmlElement(name = "Titular_SitRevista")
    private String titularSitRevista;

    @XmlElement(name = "Beneficio")
    private String beneficio;

    @XmlElement(name = "Empleadores")
    private Empleadores empleadores;

    @XmlElement(name = "ObrasSociales")
    private ObrasSociales obrasSociales;

    public String getCuil() { return cuil; }

    public void setCuil(String cuil) { this.cuil = cuil; }

    public String getNov() { return nov; }

    public void setNov(String nov) { this.nov = nov; }

    public String getFecAct() { return fecAct; }

    public void setFecAct(String fecAct) { this.fecAct = fecAct; }

    public String getTipoDoc() { return tipoDoc; }

    public void setTipoDoc(String tipoDoc) { this.tipoDoc = tipoDoc; }

    public String getFecUltConsulta() { return fecUltConsulta; }

    public void setFecUltConsulta(String fecUltConsulta) { this.fecUltConsulta = fecUltConsulta; }

    public String getTitularSitRevista() { return titularSitRevista; }

    public void setTitularSitRevista(String titularSitRevista) { this.titularSitRevista = titularSitRevista; }

    public String getBeneficio() { return beneficio; }

    public void setBeneficio(String beneficio) { this.beneficio = beneficio; }

    public Empleadores getEmpleadores() { return empleadores; }

    public void setEmpleadores(Empleadores empleadores) { this.empleadores = empleadores; }

    public ObrasSociales getObrasSociales() { return obrasSociales; }

    public void setObrasSociales(ObrasSociales obrasSociales) { this.obrasSociales = obrasSociales; }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "Empleadores")
    public static class Empleadores {
        @XmlElement(name = "Empleador")
        private List<Empleador> empleador;

        public List<Empleador> getEmpleador() { return empleador; }

        public void setEmpleador(List<Empleador> empleador) { this.empleador = empleador; }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "Empleador")
        @XmlType(name = "Empleadores.Empleador")
        public static class Empleador {
            @XmlElement(name = "Cuit")
            private String cuit;

            @XmlElement(name = "RZ")
            private String rz;

            @XmlElement(name = "SitRevista")
            private String sitRevista;

            @XmlElement(name = "FechaDesde")
            private String fechaDesde;

            @XmlElement(name = "FechaHasta")
            private String fechaHasta;

            public String getCuit() { return cuit; }

            public void setCuit(String cuit) { this.cuit = cuit; }

            public String getRz() { return rz; }

            public void setRz(String rz) { this.rz = rz; }

            public String getSitRevista() { return sitRevista; }

            public void setSitRevista(String sitRevista) { this.sitRevista = sitRevista; }

            public String getFechaDesde() { return fechaDesde; }

            public void setFechaDesde(String fechaDesde) { this.fechaDesde = fechaDesde; }

            public String getFechaHasta() { return fechaHasta; }

            public void setFechaHasta(String fechaHasta) { this.fechaHasta = fechaHasta; }
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "ObrasSociales")
    public static class ObrasSociales {
        @XmlElement(name = "ObraSocial")
        private List<ObraSocial> obraSocial;

        public List<ObraSocial> getObraSocial() { return obraSocial; }

        public void setObraSocial(List<ObraSocial> obraSocial) { this.obraSocial = obraSocial; }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "ObraSocial")
        public static class ObraSocial {
            @XmlElement(name = "Codigo")
            private String codigo;

            @XmlElement(name = "FechaIni")
            private String fechaIni;

            @XmlElement(name = "Descripcion")
            private String descripcion;

            public String getCodigo() { return codigo; }

            public void setCodigo(String codigo) { this.codigo = codigo; }

            public String getFechaIni() { return fechaIni; }

            public void setFechaIni(String fechaIni) { this.fechaIni = fechaIni; }

            public String getDescripcion() { return descripcion; }

            public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        }
    }

    // AFIP - aportes patronales
    @XmlElement(name = "AportesPatronales")
    private AportesPatronales aportesPatronales;

    @XmlElement(name = "AportesComoEmpleador")
    private String aportesComoEmpleador;

    @XmlElement(name = "AportesServicioDomestico")
    private String aportesServicioDomestico;

    public AportesPatronales getAportesPatronales() { return aportesPatronales; }

    public void setAportesPatronales(AportesPatronales aportesPatronales) { this.aportesPatronales = aportesPatronales; }

    public String getAportesComoEmpleador() { return aportesComoEmpleador; }

    public void setAportesComoEmpleador(String aportesComoEmpleador) { this.aportesComoEmpleador = aportesComoEmpleador; }

    public String getAportesServicioDomestico() { return aportesServicioDomestico; }

    public void setAportesServicioDomestico(String aportesServicioDomestico) { this.aportesServicioDomestico = aportesServicioDomestico; }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "AportesPatronales")
    public static class AportesPatronales {
        @XmlElement(name = "Empleador")
        public List<Empleador> empleador;

        public List<Empleador> getEmpleador() { return empleador; }

        public void setEmpleador(List<Empleador> empleador) { this.empleador = empleador; }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "Empleador")
        @XmlType(name = "AportesPatronales.Empleador")
        public static class Empleador {
            @XmlAttribute(name = "CUIT")
            private String cuit;

            @XmlAttribute(name = "RZ")
            private String rz;

            @XmlElement(name = "Aporte")
            private List<Aporte> aporte;

            public String getCuit() { return cuit; }

            public void setCuit(String cuit) { this.cuit = cuit; }

            public String getRz() { return rz; }

            public void setRz(String rz) { this.rz = rz; }

            public List<Aporte> getAporte() { return aporte; }

            public void setAporte(List<Aporte> aporte) { this.aporte = aporte; }

            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlRootElement(name = "Aporte")
            public static class Aporte {
                @XmlElement(name = "Periodo")
                private String periodo;

                @XmlElement(name = "InclDeclJurada")
                private String inclDeclJurada;

                @XmlElement(name = "AportesSegSocial")
                private String aportesSegSocial;

                @XmlElement(name = "AportesOOSS")
                private String aportesOOSS;

                @XmlElement(name = "ContribOOSS")
                private String contribOOSS;

                public String getPeriodo() { return periodo; }

                public void setPeriodo(String periodo) { this.periodo = periodo; }

                public String getInclDeclJurada() { return inclDeclJurada; }

                public void setInclDeclJurada(String inclDeclJurada) { this.inclDeclJurada = inclDeclJurada; }

                public String getAportesSegSocial() { return aportesSegSocial; }

                public void setAportesSegSocial(String aportesSegSocial) { this.aportesSegSocial = aportesSegSocial; }

                public String getAportesOOSS() { return aportesOOSS; }

                public void setAportesOOSS(String aportesOOSS) { this.aportesOOSS = aportesOOSS; }

                public String getContribOOSS() { return contribOOSS; }

                public void setContribOOSS(String contribOOSS) { this.contribOOSS = contribOOSS; }
            }
        }
    }

    // Cheques rechazados del BCRA y
    @XmlElement(name = "Novedad")
    private String novedad;

    public String getNovedad() { return novedad; }

    public void setNovedad(String novedad) { this.novedad = novedad; }

    // Consultados
    @XmlElement(name = "Consultas")
    //private Integer consultas;
    private Consultas consultas;

    @XmlElement(name = "Seguimientos_Temp")
    private String seguimientosTemp;

    @XmlElement(name = "Seguimientos_Perm")
    private String seguimientosPerm;

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "Consultas")
    public static class Consultas {

        @XmlElement(name = "Consulta")
        List<Consultado> consultado;

        public List<Consultado> getConsulta() { return consultado; }

        public void setConsulta(List<Consultado> consultado) { this.consultado = consultado; }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "Consulta")
        public static class Consultado {
            @XmlAttribute(name = "Mes")
            private String mes;

            @XmlAttribute(name = "Cant")
            private String cant;

            public String getMes() { return mes; }

            public void setMes(String mes) { this.mes = mes; }

            public String getCant() { return cant; }

            public void setCant(String cant) { this.cant = cant; }
        }
    }

    public Consultas getConsultas() { return consultas; }

    public void setConsultas(Consultas consultas) { this.consultas = consultas; }

    public String getSeguimientosTemp() { return seguimientosTemp; }

    public void setSeguimientosTemp(String seguimientosTemp) { this.seguimientosTemp = seguimientosTemp; }

    public String getSeguimientosPerm() { return seguimientosPerm; }

    public void setSeguimientosPerm(String seguimientosPerm) { this.seguimientosPerm = seguimientosPerm; }

    // Monotributo
    @XmlElement(name = "InscripcionOS")
    private String inscripcionOS;

    @XmlElement(name = "SituacionActual")
    private String situacionActual;

    @XmlElement(name = "SituacionActual_Motivo")
    private String situacionActualMotivo;

    @XmlElement(name = "CategoriaAutonomo")
    private String categoriaAutonomo;

    @XmlElement(name = "Categoria_Actividad")
    private String categoriaActividad;

    @XmlElement(name = "Categoria_Tipo")
    private String categoriaTipo;

    @XmlElement(name = "Eventual")
    private String eventual;

    @XmlElement(name = "Cooperativas")
    private String cooperativas;

    @XmlElement(name = "Cooperativas_RazonSocial")
    private String cooperativasRazonSocial;

    @XmlElement(name = "Padron_GrupoFamiliar")
    private PadronGrupoFamiliar padronGrupoFamiliar;

    @XmlElement(name = "HistorialPagos")
    private HistorialPagos historialPagos;

    public String getInscripcionOS() { return inscripcionOS; }

    public void setInscripcionOS(String inscripcionOS) { this.inscripcionOS = inscripcionOS; }

    public String getSituacionActual() { return situacionActual; }

    public void setSituacionActual(String situacionActual) { this.situacionActual = situacionActual; }

    public String getSituacionActualMotivo() { return situacionActualMotivo; }

    public void setSituacionActualMotivo(String situacionActualMotivo) { this.situacionActualMotivo = situacionActualMotivo; }

    public String getCategoriaAutonomo() { return categoriaAutonomo; }

    public void setCategoriaAutonomo(String categoriaAutonomo) { this.categoriaAutonomo = categoriaAutonomo; }

    public String getCategoriaActividad() { return categoriaActividad; }

    public void setCategoriaActividad(String categoriaActividad) { this.categoriaActividad = categoriaActividad; }

    public String getCategoriaTipo() { return categoriaTipo; }

    public void setCategoriaTipo(String categoriaTipo) { this.categoriaTipo = categoriaTipo; }

    public String getEventual() { return eventual; }

    public void setEventual(String eventual) { this.eventual = eventual; }

    public String getCooperativas() { return cooperativas; }

    public void setCooperativas(String cooperativas) { this.cooperativas = cooperativas; }

    public String getCooperativasRazonSocial() { return cooperativasRazonSocial; }

    public void setCooperativasRazonSocial(String cooperativasRazonSocial) { this.cooperativasRazonSocial = cooperativasRazonSocial; }

    public PadronGrupoFamiliar getPadronGrupoFamiliar() { return padronGrupoFamiliar; }

    public void setPadronGrupoFamiliar(PadronGrupoFamiliar padronGrupoFamiliar) { this.padronGrupoFamiliar = padronGrupoFamiliar; }

    public HistorialPagos getHistorialPagos() { return historialPagos; }

    public void setHistorialPagos(HistorialPagos historialPagos) { this.historialPagos = historialPagos; }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "PadronGrupoFamiliar")
    public static class PadronGrupoFamiliar {
        @XmlElement(name = "Nov")
        private String nov;

        @XmlElement(name = "FecAct")
        private String fecAct;

        @XmlElement(name = "EvolucionPadron")
        private EvolucionPadron evolucionPadron;

        @XmlElement(name = "GrupoFamiliar")
        private GrupoFamiliar grupoFamiliar;

        public String getNov() { return nov; }

        public void setNov(String nov) { this.nov = nov; }

        public String getFecAct() { return fecAct; }

        public void setFecAct(String fecAct) { this.fecAct = fecAct; }

        public EvolucionPadron getEvolucionPadron() { return evolucionPadron; }

        public void setEvolucionPadron(EvolucionPadron evolucionPadron) { this.evolucionPadron = evolucionPadron; }

        public GrupoFamiliar getGrupoFamiliar() { return grupoFamiliar; }

        public void setGrupoFamiliar(GrupoFamiliar grupoFamiliar) { this.grupoFamiliar = grupoFamiliar; }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "EvolucionPadron")
        public static class EvolucionPadron {
            @XmlElement(name = "Registros")
            private Registros registros;

            public Registros getRegistros() { return registros; }

            public void setRegistros(Registros registros) { this.registros = registros; }

            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlRootElement(name = "Registros")
            @XmlType(name = "EvolucionPadron.Registros")
            public static class Registros {
                @XmlElement(name = "Registro")
                private List<Registro> registro;

                public List<Registro> getRegistro() { return registro; }

                public void setRegistro(List<Registro> registro) { this.registro = registro; }

                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlRootElement(name = "Registro")
                @XmlType(name = "EvolucionPadron.Registro")
                public static class Registro {
                    @XmlElement(name = "Origen")
                    private String origen;

                    @XmlElement(name = "FechaDesde")
                    private String fechaDesde;

                    @XmlElement(name = "FechaHasta")
                    private String fechaHasta;

                    @XmlElement(name = "ObraSocial")
                    private String obraSocial;

                    public String getOrigen() { return origen; }

                    public void setOrigen(String origen) { this.origen = origen; }

                    public String getFechaDesde() { return fechaDesde; }

                    public void setFechaDesde(String fechaDesde) { this.fechaDesde = fechaDesde; }

                    public String getFechaHasta() { return fechaHasta; }

                    public void setFechaHasta(String fechaHasta) { this.fechaHasta = fechaHasta; }

                    public String getObraSocial() { return obraSocial; }

                    public void setObraSocial(String obraSocial) { this.obraSocial = obraSocial; }
                }
            }
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "GrupoFamiliar")
        public static class GrupoFamiliar {
            @XmlElement(name = "Registros")
            private Registros registros;

            public Registros getRegistros() { return registros; }

            public void setRegistros(Registros registros) { this.registros = registros; }

            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlRootElement(name = "Registros")
            @XmlType(name = "GrupoFamiliar.Registros")
            public static class Registros {
                @XmlElement(name = "Registro")
                private List<Registro> registro;

                public List<Registro> getRegistro() { return registro; }

                public void setRegistro(List<Registro> registro) { this.registro = registro; }

                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlRootElement(name = "Registro")
                @XmlType(name = "GrupoFamiliar.Registro")
                public static class Registro {
                    @XmlElement(name = "Documento")
                    private String documento;

                    @XmlElement(name = "RZ")
                    private String rz;

                    @XmlElement(name = "FechaIncorporacion")
                    private String fechaIncorporacion;

                    @XmlElement(name = "Parentesco")
                    private String parentesco;

                    public String getDocumento() { return documento; }

                    public void setDocumento(String documento) { this.documento = documento; }

                    public String getRz() { return rz; }

                    public void setRz(String rz) { this.rz = rz; }

                    public String getFechaIncorporacion() { return fechaIncorporacion; }

                    public void setFechaIncorporacion(String fechaIncorporacion) { this.fechaIncorporacion = fechaIncorporacion; }

                    public String getParentesco() { return parentesco; }

                    public void setParentesco(String parentesco) { this.parentesco = parentesco; }
                }
            }
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "HistorialPagos")
    public static class HistorialPagos {
        @XmlElement(name = "Nov")
        private String nov;

        @XmlElement(name = "FecAct")
        private String fecAct;

        @XmlElement(name = "Registros")
        public Registros registros;

        public String getNov() { return nov; }

        public void setNov(String nov) { this.nov = nov; }

        public String getFecAct() { return fecAct; }

        public void setFecAct(String fecAct) { this.fecAct = fecAct; }

        public Registros getRegistros() { return registros; }

        public void setRegistros(Registros registros) { this.registros = registros; }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "Registros")
        @XmlType(name = "HistorialPagos.Registros")
        public static class Registros {
            @XmlElement(name = "Registro")
            private List<Registro> registro;

            public List<Registro> getRegistro() { return registro; }

            public void setRegistro(List<Registro> registro) { this.registro = registro; }

            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlRootElement(name = "Registro")
            @XmlType(name = "HistorialPagos.Registro")
            public static class Registro {
                @XmlElement(name = "Periodo")
                private String periodo;

                @XmlElement(name = "FechaPago")
                private String fechaPago;

                @XmlElement(name = "Concepto")
                private String concepto;

                @XmlElement(name = "Credito")
                private String credito;

                @XmlElement(name = "Debito")
                private String debito;

                @XmlElement(name = "ObraSocial")
                private String obraSocial;

                public String getPeriodo() { return periodo; }

                public void setPeriodo(String periodo) { this.periodo = periodo; }

                public String getFechaPago() { return fechaPago; }

                public void setFechaPago(String fechaPago) { this.fechaPago = fechaPago; }

                public String getConcepto() { return concepto; }

                public void setConcepto(String concepto) { this.concepto = concepto; }

                public String getCredito() { return credito; }

                public void setCredito(String credito) { this.credito = credito; }

                public String getDebito() { return debito; }

                public void setDebito(String debito) { this.debito = debito; }

                public String getObraSocial() { return obraSocial; }

                public void setObraSocial(String obraSocial) { this.obraSocial = obraSocial; }
            }
        }
    }

    // Bureau del credito del BCRA
    @XmlElement(name = "IncluyeBM")
    private String incluyeBM;

    @XmlElement(name = "Bancarizado")
    private String bancarizado;

    @XmlElement(name = "UltimoInforme")
    private UltimoInforme ultimoInforme;

    public String getIncluyeBM() { return incluyeBM; }

    public void setIncluyeBM(String incluyeBM) { this.incluyeBM = incluyeBM; }

    public String getBancarizado() { return bancarizado; }

    public void setBancarizado(String bancarizado) { this.bancarizado = bancarizado; }

    public UltimoInforme getUltimoInforme() { return ultimoInforme; }

    public void setUltimoInforme(UltimoInforme ultimoInforme) { this.ultimoInforme = ultimoInforme; }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "UltimoInforme")
    public static class UltimoInforme {
        @XmlElement(name = "CompromisosMensuales")
        private String compromisosMensuales;

        @XmlElement(name = "Deudas")
        private Deudas deudas;

        public String getCompromisosMensuales() { return compromisosMensuales; }

        public void setCompromisosMensuales(String compromisosMensuales) { this.compromisosMensuales = compromisosMensuales; }

        public Deudas getDeudas() { return deudas; }

        public void setDeudas(Deudas deudas) { this.deudas = deudas; }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "Deudas")
        public static class Deudas {
            @XmlAttribute(name = "Cant")
            private Integer cant;

            @XmlElement(name = "Deuda")
            List<Deuda> deuda;

            public Integer getCant() { return cant; }

            public void setCant(Integer cant) { this.cant = cant; }

            public List<Deuda> getDeuda() { return deuda; }

            public void setDeuda(List<Deuda> deuda) { this.deuda = deuda; }

            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlRootElement(name = "Deuda")
            public static class Deuda {
                @XmlAttribute(name = "Ent_Cod")
                private String entCod;

                @XmlAttribute(name = "Sit")
                private String sit;

                @XmlAttribute(name = "Periodo")
                private String periodo;

                @XmlAttribute(name = "Monto")
                private String monto;

                @XmlAttribute(name = "Proc_Jud")
                private String procJud;

                @XmlAttribute(name = "CodEntidad")
                private String codEntidad;

                @XmlAttribute(name = "Entidad")
                private String entidad;

                public String getEntCod() { return entCod; }

                public void setEntCod(String entCod) { this.entCod = entCod; }

                public String getSit() { return sit; }

                public void setSit(String sit) { this.sit = sit; }

                public String getPeriodo() { return periodo; }

                public void setPeriodo(String periodo) { this.periodo = periodo; }

                public String getMonto() { return monto; }

                public void setMonto(String monto) { this.monto = monto; }

                public String getProcJud() { return procJud; }

                public void setProcJud(String procJud) { this.procJud = procJud; }

                public String getCodEntidad() { return codEntidad; }

                public void setCodEntidad(String codEntidad) { this.codEntidad = codEntidad; }

                public String getEntidad() { return entidad; }

                public void setEntidad(String entidad) { this.entidad = entidad; }
            }
        }
    }

    // SC - Otras fuentes
    @XmlElement(name = "Detalle")
    private String detalle;

    @XmlElement(name = "Relacionados")
    private String relacionados;

    public String getDetalle() { return detalle; }

    public void setDetalle(String detalle) { this.detalle = detalle; }

    public String getRelacionados() { return relacionados; }

    public void setRelacionados(String relacionados) { this.relacionados = relacionados; }

    @XmlElement(name = "Historia")
    private Historia historia;

    public Historia getHistoria() { return historia; }

    public void setHistoria(Historia historia) { this.historia = historia; }

    @XmlElement(name = "Actor")
    private String actor;

    @XmlElement(name = "Demandado")
    private String demandado;

    @XmlElement(name = "Juicio")
    private String juicio;

    @XmlElement(name = "Relacionados")
    private JuicioRelacionado juicioRelacionado;

    public String getActor() { return actor; }

    public void setActor(String actor) { this.actor = actor; }

    public String getDemandado() { return demandado; }

    public void setDemandado(String demandado) { this.demandado = demandado; }

    public String getJuicio() { return juicio; }

    public void setJuicio(String juicio) { this.juicio = juicio; }

    public JuicioRelacionado getJuicioRelacionado() { return juicioRelacionado; }

    public void setJuicioRelacionado(JuicioRelacionado juicioRelacionado) { this.juicioRelacionado = juicioRelacionado; }

    public String getIva() {
        return iva;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public String getJubilado() {
        return jubilado;
    }

    public void setJubilado(String jubilado) {
        this.jubilado = jubilado;
    }
}
