package com.affirm.solven.client;

import com.affirm.abaco.client.EMensaje;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.model.transactional.User;
import com.affirm.equifax.ws.CuotaHistorica;
import com.affirm.equifax.ws.DirectorioPersona;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

/**
 * Created by dev5 on 24/04/17.
 */

@Service
public class SolvenClient {

    public ERptaCredito esSocio(int documentType, String documentNumber) throws Exception {
        ERptaCredito socio = new ERptaCredito();
        socio.setCodigoMensaje(0);
        socio.setIdSocio("13268");
        socio.setIdCredito("078006044130");
        socio.setSaldoCredito(407.00);
        socio.setCapitalCredito(1500.00);
        socio.setCuotasTotales(24);
        socio.setCuotasPagadas(18);
        socio.setCodigoOperacion(0);
        return socio;
    }

    public EMensaje enviarSocio(String socioAbacoId, User user, DirectorioPersona directorioPersona, PersonOcupationalInformation personOcupationalInformation, CuotaHistorica cuotaHistorica) throws Exception {
        EMensaje response = new EMensaje();
        response.setIdSocio("13268");
        response.setCodigoMensaje(0);
        if (socioAbacoId == null || socioAbacoId.isEmpty())
            return response;
        else
            return response;
    }

    public ERptaCredito crearCredito(String socioId, String creditoCancelar, Double montoCreditoCancelar, Credit objCredito) throws Exception {
        ERptaCredito credito = new ERptaCredito();
        credito.setIdCredito(RandomStringUtils.randomAlphanumeric(6));
        credito.setCodigoMensaje(0);
        credito.setSaldoCredito(0);
        credito.setCapitalCredito(0.0);
        credito.setCuotasTotales(0);
        credito.setCuotasPagadas(0);
        credito.setCodigoOperacion(5000);
        return credito;
    }


}

