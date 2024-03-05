package com.affirm.compartamos.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.transactional.*;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dev5 on 30/11/17.
 */
public class GenerarCreditoRequest {

    @SerializedName("poCliente")
    private Cliente cliente;
    @SerializedName("poConyuge")
    private Cliente conyugue;
    @SerializedName("poSolicitud")
    private Solicitud solicitud;
    @SerializedName("poCredito")
    private Credito credito;
    @SerializedName("paDireccion")
    private List<Direccion> direcciones;
    @SerializedName("paFuenteIngreso")
    private List<FuenteIngreso> fuestesIngreso;

    public GenerarCreditoRequest(Cliente cliente, Cliente conyugue, Solicitud solicitud, Credito credito, List<Direccion> direcciones, List<FuenteIngreso> fuenteIngresos){
        setCliente(cliente);
        setConyugue(conyugue);
        setSolicitud(solicitud);
        setCredito(credito);
        setDirecciones(direcciones);
        setFuestesIngreso(fuenteIngresos);
    }


    public GenerarCreditoRequest(String tipoOperacion,
                                 Person person,
                                 User user,
                                 PersonOcupationalInformation personOcupationalInformation,
                                 List<DisggregatedAddress> disggregatedAddresses,
                                 Date fechaRCC,
                                 LoanApplication loanApplication,
                                 LoanOffer loanOffer,
                                 PersonBankAccountInformation personBankAccountInformation,
                                 PersonContactInformation personContactInformation,
                                 SunatResult sunatResult,
                                 ExperianResult experianResult,
                                 RucInfo rucInfo,
                                 TranslatorDAO translatorDAO,
                                 String entityCreditCode,
                                 SunatResult sunatResultRuc,
                                 Credit credit) throws Exception{


        setCliente(new Cliente(tipoOperacion, person,user,personOcupationalInformation, personContactInformation, disggregatedAddresses, fechaRCC, experianResult, sunatResult, translatorDAO));
        getCliente().setCuentaDigital(loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANK_ACCOUNT_STATEMENT.getKey()) == null ? false : Boolean.valueOf(loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANK_ACCOUNT_STATEMENT.getKey())));
        if(person.getPartner() != null) setConyugue(new Cliente(person.getPartner(), experianResult, translatorDAO));
        setSolicitud(new Solicitud(loanApplication, credit));
        setCredito(new Credito(personBankAccountInformation, loanApplication, loanOffer, translatorDAO, entityCreditCode));
        List<Direccion> direcciones = new ArrayList<>();
        if(disggregatedAddresses.stream().anyMatch(d -> d.getType() == 'H')){
            Direccion direccionCasa = new Direccion(disggregatedAddresses.stream().filter(d -> d.getType() == 'H').findFirst().orElse(null), personContactInformation, translatorDAO, personOcupationalInformation);
            direcciones.add(direccionCasa);
        }
        if(disggregatedAddresses.stream().anyMatch(d -> d.getType() == 'J')){
            Direccion direccionTrabajo = new Direccion(disggregatedAddresses.stream().filter(d -> d.getType() == 'J').findFirst().orElse(null), personContactInformation, translatorDAO, personOcupationalInformation);
            direcciones.add(direccionTrabajo);
        }
        setDirecciones(direcciones);

        FuenteIngreso fuenteIngreso = new FuenteIngreso(person, personContactInformation, personOcupationalInformation, user, sunatResultRuc, rucInfo, translatorDAO);
        List<FuenteIngreso> fuenteIngresos = new ArrayList<>();
        fuenteIngresos.add(fuenteIngreso);
        setFuestesIngreso(fuenteIngresos);

    }



    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Cliente getConyugue() {
        return conyugue;
    }

    public void setConyugue(Cliente conyugue) {
        this.conyugue = conyugue;
    }

    public Solicitud getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

    public Credito getCredito() {
        return credito;
    }

    public void setCredito(Credito credito) {
        this.credito = credito;
    }


    public List<Direccion> getDirecciones() {
        return direcciones;
    }

    public void setDirecciones(List<Direccion> direcciones) {
        this.direcciones = direcciones;
    }

    public List<FuenteIngreso> getFuestesIngreso() {
        return fuestesIngreso;
    }

    public void setFuestesIngreso(List<FuenteIngreso> fuestesIngreso) {
        this.fuestesIngreso = fuestesIngreso;
    }
}
