package com.affirm.compartamos.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.catalog.ActivityType;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.SubActivityType;
import com.affirm.common.model.transactional.*;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dev5 on 14/12/17.
 */
public class FuenteIngreso {

    @SerializedName("pcNomFue")
    private String nombre;
    @SerializedName("pdIniFue")
    private String  inicioActividades;
    @SerializedName("pcTiDoFu")
    private String tipoDocumento;
    @SerializedName("pcNuDoFu")
    private String numeroDocumento;
    @SerializedName("pcSecEco")
    private String sectorEconomico;
    @SerializedName("pcCodAct")
    private String ciiu;
    @SerializedName("pcTipEnt")
    private String tipoEntidad;
    @SerializedName("pcTipRel")
    private String relacion;
    @SerializedName("pcCargo")
    private String cargo;
    @SerializedName("pdIngres")
    private String fechaIngreso;
    @SerializedName("pnMonIng")
    private Double ingresoBruto;
    @SerializedName("pcDocpag")
    private String documentoPago;
    @SerializedName("pcDirEmp")
    private String direccion;
    @SerializedName("pcNumTel")
    private String telefono;

    private transient DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public FuenteIngreso(Person person, PersonContactInformation personContactInformation, PersonOcupationalInformation personOcupationalInformation, User user, SunatResult sunatResult, RucInfo rucInfo, TranslatorDAO translatorDAO) throws Exception{
        setNombre(personOcupationalInformation.getCompanyName() != null ? personOcupationalInformation.getCompanyName() : person.getFullName());
        setTipoDocumento("R"); //RUC
        setNumeroDocumento(personOcupationalInformation.getRuc() != null ? personOcupationalInformation.getRuc() : "00000000000");
        setSectorEconomico(translatorDAO.translate(Entity.COMPARTAMOS, 20, person.getProfession().getId().toString(), null));
        setCiiu(sunatResult != null ? sunatResult.getPrincipalCIIU() : "74996"); //Default value if robot fails - ZL
        setTipoEntidad(translatorDAO.translate(Entity.COMPARTAMOS, 19, personOcupationalInformation.getSector().getId(), null));
        setRelacion(translatorDAO.translate(Entity.COMPARTAMOS, 10, personOcupationalInformation.getActivityType().getId().toString(), null));
        if(personOcupationalInformation.getActivityType().getId().equals(ActivityType.DEPENDENT))
            setCargo(translatorDAO.translate(Entity.COMPARTAMOS, 11, personOcupationalInformation.getOcupation().getId().toString(), null));
        else
            setCargo("10"); //Default ZL - FT
        setInicioActividades(rucInfo != null ? rucInfo.getRegisterDate() : df.format(new Date()));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, Integer.valueOf(personOcupationalInformation.getEmploymentTime())*-1);
        setFechaIngreso(df.format(cal.getTime()));

        setIngresoBruto(personOcupationalInformation.getFixedGrossIncome());
        String documento = "";
        if(personOcupationalInformation.getActivityType().getId().equals(ActivityType.DEPENDENT)) documento = "035";
        else if(personOcupationalInformation.getActivityType().getId().equals(ActivityType.RENTIER) || personOcupationalInformation.getActivityType().getId().equals(ActivityType.RENTIER)) documento = "056";
        else if(personOcupationalInformation.getActivityType().getId().equals(ActivityType.PENSIONER)) documento = "024";
        else if(personOcupationalInformation.getActivityType().getId().equals(ActivityType.INDEPENDENT)){
            if(personOcupationalInformation.getSubActivityType().getId().equals(SubActivityType.PROFESSIONAL_SERVICE)) documento = "024";
            else if(personOcupationalInformation.getSubActivityType().getId().equals(SubActivityType.OWN_BUSINESS) || personOcupationalInformation.getSubActivityType().getId().equals(SubActivityType.RENT) || personOcupationalInformation.getSubActivityType().getId().equals(SubActivityType.SHAREHOLDER)) documento = "056";
            else documento = "056";
        }else
            documento = "056";
        setDocumentoPago(documento);
        setDireccion(personOcupationalInformation.getAddress() != null ? personOcupationalInformation.getAddress(): personContactInformation.getFullAddressBO());
        setTelefono(personOcupationalInformation.getPhoneNumber() != null ? personOcupationalInformation.getPhoneNumber() : user.getPhoneNumber());
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getInicioActividades() {
        return inicioActividades;
    }

    public void setInicioActividades(String inicioActividades) {
        this.inicioActividades = inicioActividades;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getSectorEconomico() {
        return sectorEconomico;
    }

    public void setSectorEconomico(String sectorEconomico) {
        this.sectorEconomico = sectorEconomico;
    }

    public String getCiiu() {
        return ciiu;
    }

    public void setCiiu(String ciiu) {
        this.ciiu = ciiu;
    }

    public String getTipoEntidad() {
        return tipoEntidad;
    }

    public void setTipoEntidad(String tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    public String getRelacion() {
        return relacion;
    }

    public void setRelacion(String relacion) {
        this.relacion = relacion;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Double getIngresoBruto() {
        return ingresoBruto;
    }

    public void setIngresoBruto(Double ingresoBruto) {
        this.ingresoBruto = ingresoBruto;
    }

    public String getDocumentoPago() {
        return documentoPago;
    }

    public void setDocumentoPago(String documentoPago) {
        this.documentoPago = documentoPago;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }
}
