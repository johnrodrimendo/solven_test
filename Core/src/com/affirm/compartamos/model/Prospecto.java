package com.affirm.compartamos.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.PersonContactInformation;
import com.affirm.common.model.transactional.User;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dev5 on 30/11/17.
 */
public class Prospecto {

    @SerializedName("pcNomPro")
    private String nombreCompleto;
    @SerializedName("pcSexo")
    private String sexo;
    @SerializedName("pcNumTel")
    private String telefono;
    @SerializedName("pcDirDom")
    private String direccion;
    @SerializedName("pdFecReg")
    private String fechaRegistro;
    @SerializedName("pnMonSol")
    private Double monto;

    private transient DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public Prospecto(Integer entityId, Person person, User user, LoanApplication loanApplication, PersonContactInformation personContactInformation, TranslatorDAO translatorDAO) throws Exception{
        setNombreCompleto(person.getFullName());
        setSexo(translatorDAO.translate(entityId, 2,person.getGender().toString(), null));
        setTelefono(user.getPhoneNumber());
        setDireccion(personContactInformation.getFullAddress());
        setFechaRegistro(df.format(new Date()));
        setMonto(Double.valueOf(loanApplication.getAmount()));
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }
}
