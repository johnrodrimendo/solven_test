package com.affirm.ripley.service.Impl;

import com.affirm.ripley.model.RipleyFactor;
import com.affirm.ripley.model.RipleyOferta;
import com.affirm.ripley.model.RipleyPreAprrovedBase;
import com.affirm.ripley.service.RipleyProcess;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miberico on 25/05/17.
 */
@Service("ripleyService")
public class RipleyProcessImpl implements RipleyProcess{

    private List<RipleyFactor> factorTable;
    private double minAmount;
    private double maxAmount;

    private List<RipleyFactor> populateFactorTable(double amount) {
        List<RipleyFactor> factorTable = new ArrayList<>();
        factorTable.add(new RipleyFactor(12, 1.00, amount));
        factorTable.add(new RipleyFactor(13, 1.02, amount));
        factorTable.add(new RipleyFactor(14, 1.05, amount));
        factorTable.add(new RipleyFactor(15, 1.08, amount));
        factorTable.add(new RipleyFactor(16, 1.13, amount));
        factorTable.add(new RipleyFactor(17, 1.19, amount));
        factorTable.add(new RipleyFactor(18, 1.24, amount));
        factorTable.add(new RipleyFactor(19, 1.30, amount));
        factorTable.add(new RipleyFactor(20, 1.34, amount));
        factorTable.add(new RipleyFactor(21, 1.40, amount));
        factorTable.add(new RipleyFactor(22, 1.44, amount));
        factorTable.add(new RipleyFactor(23, 1.49, amount));
        factorTable.add(new RipleyFactor(24, 1.53, amount));
        factorTable.add(new RipleyFactor(25, 1.58, amount));
        factorTable.add(new RipleyFactor(26, 1.62, amount));
        factorTable.add(new RipleyFactor(27, 1.66, amount));
        factorTable.add(new RipleyFactor(28, 1.70, amount));
        factorTable.add(new RipleyFactor(29, 1.75, amount));
        factorTable.add(new RipleyFactor(30, 1.78, amount));
        factorTable.add(new RipleyFactor(31, 1.80, amount));
        factorTable.add(new RipleyFactor(32, 1.85, amount));
        factorTable.add(new RipleyFactor(33, 1.88, amount));
        factorTable.add(new RipleyFactor(34, 1.90, amount));
        factorTable.add(new RipleyFactor(35, 1.95, amount));
        factorTable.add(new RipleyFactor(36, 2.00, amount));
        return factorTable;
    }

    @Override
    public void processRipley(RipleyPreAprrovedBase cliente, int clientPlazo, double clientAmount){
        System.out.println("**********************");
        System.out.println(cliente.getNombres() + " - " + cliente.getNumeroDocumento());
        System.out.println("**********************");
        RipleyOferta ofertaRipley = new RipleyOferta(cliente.getPlazo(), Math.round(cliente.getMonto() * 100.0) / 100.0, cliente.getTasa());
        RipleyOferta ofertaCliente = null;
        RipleyOferta ofertaMismoPlazo = null;

        this.factorTable = populateFactorTable(clientAmount);
        RipleyFactor actualFactor = this.factorTable.stream().filter(row -> cliente.getPlazo() == row.getPlazo()).findFirst().orElse(null);
        RipleyFactor clientFactor = this.factorTable.stream().filter(row -> clientPlazo == row.getPlazo()).findFirst().orElse(null);
        RipleyFactor maxFactor = this.factorTable.stream().filter(row -> 36 == row.getPlazo()).findFirst().orElse(null);
        if(actualFactor != null){
            System.out.println("Plazo Ripley: " + actualFactor.getPlazo());
            System.out.println("Factor Ripley: " + actualFactor.getFactor());
            System.out.println("Monto Ripley: " + cliente.getMonto());
            System.out.println("**********************");
            this.minAmount = Math.round(cliente.getMonto() / actualFactor.getFactor() * 100.0) / 100.0;
            if(clientFactor != null){
                System.out.println("Plazo Cliente : " + clientFactor.getPlazo());
                System.out.println("Factor Cliente : " + clientFactor.getFactor());
                System.out.println("Monto Cliente : " + clientAmount);
                System.out.println("**********************");
                this.maxAmount = Math.round(minAmount*clientFactor.getFactor() * 100.0) / 100.0;
            }else{
                this.maxAmount = Math.round(minAmount*maxFactor.getFactor() * 100.0) / 100.0;
            }
            System.out.println("Monto minimo a 12 cuotas : " + this.minAmount);
            System.out.println("Monto maximo a " + clientPlazo + " cuotas : " + this.maxAmount);
            System.out.println("**********************");
            if(clientAmount <= maxAmount){
                System.out.println("Oferta del cliente aprobada");
                ofertaCliente = new RipleyOferta(clientPlazo, Math.round(clientAmount * 100.0) / 100.0, cliente.getTasa());
                ofertaMismoPlazo = new RipleyOferta(clientPlazo, Math.round(maxAmount * 100.0) / 100.0, cliente.getTasa());
            }else{
                ofertaCliente = new RipleyOferta(clientPlazo, Math.round(maxAmount * 100.0) / 100.0, cliente.getTasa());
                System.out.println("Oferta del cliente rechazada");
            }
            System.out.println("**********************");
            System.out.println("Oferta Ripley : " + ofertaRipley.toString());
            if(ofertaCliente != null && !ofertaRipley.equals(ofertaCliente))
                System.out.println("Oferta Cliente : " + ofertaCliente.toString());
            if(ofertaMismoPlazo != null && !ofertaRipley.equals(ofertaMismoPlazo))
                System.out.println("Oferta Mismo Plazo : " + ofertaMismoPlazo.toString());
        }else{
            System.out.println("El plazo no es correcto");
        }
    }

}
