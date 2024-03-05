package com.affirm.acceso.util;

import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.transactional.EntityWebServiceLog;
import com.affirm.common.service.EntityWebServiceUtil;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dev5 on 26/07/17.
 */
@Component
public class AccesoUtilCall {

    @Autowired
    private EntityWebServiceUtil entityWebServiceUtil;

    public EntityWebServiceLog<JSONObject> call(EntityWebService entityWebService, String request, Integer loanApplicationId) throws Exception {

        String securityKey = Configuration.hostEnvIsProduction() ? entityWebService.getProductionSecurityKey() : entityWebService.getSandboxSecurityKey();
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of("content-type", "application/json"));
        headers.add(Pair.of("security-key", securityKey));

        EntityWebServiceLog<JSONObject> response = entityWebServiceUtil.callRestWs(entityWebService, request, headers, loanApplicationId, false);
        response.setRestResponse(new JSONObject(response.getResponse()));
        return response;
    }

    public EntityWebServiceLog<JSONObject> callV2(EntityWebService entityWebService, String request, Integer loanApplicationId, Map<String, String> urlParams) throws Exception {

        String securityKey = Configuration.hostEnvIsProduction() ? entityWebService.getProductionSecurityKey() : entityWebService.getSandboxSecurityKey();
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of("content-type", "application/json"));
        headers.add(Pair.of("Authorization", "Basic " + securityKey));

        EntityWebServiceLog<JSONObject> response = entityWebServiceUtil.callRestWs(entityWebService, request, headers, loanApplicationId, false, urlParams);
        response.setRestResponse(new JSONObject(response.getResponse()));
        return response;
    }

    public String sendErrorMessage(Integer codigoError){
        return sendErrorMessage(codigoError , null);
    }

    public String sendErrorMessage(Integer codigoError, String codigoMensaje) {
        switch (codigoError) {
//            case 92 : return "Error de comunicación con Acceso Crediticio";
//            case 107 : return "El prospecto ya está siendo gestionado por Acceso Crediticio";
//            case 12	: return "El cliente ya tiene un expediente generado en el último periodo";
//            case 32	: return "La antiguedad laboral debe ser mayor de 11 meses";
//            case 40	: return "Registrar el ingreso neto mayor o igual a S/ 1 500.00";
//            case 74	: return "El expediente ya fue enviado al AC";
//            case 87 : return "El expediente ha sido enviado a evaluación";
//            case 131 : return "No existen variables crediticias para el vehículo";
//            case 98	: return "El expediente encuentra aprobado por evaluación";
//            case 99 : return "El expediente se encuentra en estado despachado";
//            case 100 : return "El expediente se encuentra en estado activado";
//            case 101 : return "El expediente ya cuenta con verificación de datos aprobado";

//            NUEVO LISTADO DE CATALOGO DE RESPUESTAS (NO SOLO ERROR)
            case 1:
                return "Ingrese el Tipo de Servicio";
            case 2:
                return "Ingrese la Informacion del JSON";
            case 11:
                return "Los DNI ingresados no pueden ser iguales";
            case 12:
                return "El cliente ya tiene un expediente generado en el último periodo";
            case 13:
                return "Ingresar el producto";
            case 14:
                return "Ingresar el sub producto";
            case 15:
                return "Ingresar el concesionario";
            case 16:
                return "Ingresar el ubigeo del concesionario";
            case 17:
                return "Ingresar el centro de operaciones";
            case 18:
                return "Ingresar el centro de financiamiento";
            case 19:
                return "Ingresar el Operador";
            case 20:
                return "Ingresar el tipo de documento del titular";
            case 21:
                return "Ingresar el Nro.de documento del titular";
            case 22:
                return "Ingresar el nombre del titular";
            case 23:
                return "Ingresar el apellido paterno del titular";
            case 24:
                return "Ingresar el apellido materno del titular";
            case 25:
                return "Ingresar la fecha de nacimiento del titular";
            case 26:
                return "Ingresar el género del titular";
            case 27:
                return "Ingresar el estado civil del titular";
            case 28:
                return "Ingresar el teléfono móvil del titular";
            case 29:
                return "Ingresar el correo electrónico";
            case 30:
                return "Ingresar la carga familiar";
            case 31:
                return "Ingresar el tipo de documento del cónyuge";
            case 32:
                return "Ingresar el nro.de documento del cónyuge";
            case 33:
                return "Ingresar el nombre del cónyuge";
            case 34:
                return "Ingresar el apellido paterno del cónyuge";
            case 35:
                return "Ingresar el apellido materno del cónyuge";
            case 36:
                return "Ingresar la fecha de nacimiento del cónyuge";
            case 37:
                return "Ingresar el ingreso fijo cliente (Mensual)";
            case 38:
                return "Ingresar el Ingreso Variable del Cliente(Mensual)";
            case 39:
                return "Ingresar la Fecha de Ingreso Laboral del Cliente";
            case 40:
                return "Ingresar si la Cónyuge Sustenta de Ingresos";
            case 41:
                return "Ingresar el Ingreso Fijo Cónyuge";
            case 42:
                return "Ingresar el Ingreso Variable Cónyuge";
            case 43:
                return "Ingresar el Monto de Desembolso";
            case 44:
                return "Ingresar la TEA %";
            case 45:
                return "Ingresar el numero de cuotas";
            case 46:
                return "Ingresar la fecha de activación";
            case 47:
                return "Ingresar la fecha de primer vencimiento";
            case 48:
                return "Ingresar la Nacionalidad";
            case 49:
                return "Ingresar la Residencia";
            case 50:
                return "Ingresar la Formalidad Laboral";
            case 51:
                return "Ingresar el nivel de educación";
            case 52:
                return "Ingresar el Sector Económico";
            case 53:
                return "Ingresar la Actividad Económica";
            case 54:
                return "Ingresar la Profesión y Ocupación";
            case 55:
                return "Ingresar si es PEP";
            case 56:
                return "Ingresar Domicilio –Latitud";
            case 57:
                return "Ingresar Domicilio –Longitud";
            case 58:
                return "Ingresar el ubigeo del domicilio";
            case 59:
                return "Ingresar el Tipo de vía -Domicilio";
            case 60:
                return "Ingresar el Nombre de vía –Domicilio";
            case 61:
                return "Ingresar el Tipo de zona –Domicilio";
            case 62:
                return "Ingresar el Nombre de zona –Domicilio";
            case 63:
                return "Ingresar el Nombre de Centro laboral";
            case 64:
                return "Ingresar RUC";
            case 65:
                return "Ingresar el Ubigeo del Centro Laboral";
            case 66:
                return "Ingresar el Tipo de vía – Laboral";
            case 67:
                return "Ingresar el Nombre de vía – Laboral";
            case 68:
                return "Ingresar el Tipo de zona – Laboral";
            case 69:
                return "Ingresar el Nombre de zona – Laboral";
            case 70:
                return "Ingresar Laboral – Latitud";
            case 71:
                return "Ingresar Laboral – Longitud";
            case 72:
                return "Ingresar el Teléfono";
            case 73:
                return "Ingresar el Cargo";
            case 74:
                return "Ingresar la Entidad Bancaria";
            case 75:
                return "Ingresar el Tipo de Cuenta";
            case 76:
                return "Ingresar el Número de Cuenta bancaria";
            case 77:
                return "Ingresar el Número de Cuenta Interbancaria";
            case 78:
                return "Creación de expediente conforme";
            case 79:
                return "Creación y Simulación conforme";
            case 80:
                return "Ingresar el Selfie del Cliente";
            case 81:
                return "Ingresar el DOI del Cliente";
            case 82:
                return "Ingresar la Boleta de Remuneraciones / Recibo por Honorarios";
            case 83:
                return "Ingresar el Pagaré";
            case 84:
                return "Ingresar la Hoja Resumen";
            case 85:
                return "Ingresar el Cronograma";
            case 86:
                return "Ingresar la Solicitud de Crédito";
            case 87:
                return "Ingresar la Contrato de Crédito";
            case 88:
                return "Ingrese la cadena de URL(S)";
            case 89:
                return "Los documentos ya se encuentran registrados";
            case 90:
                return "Registro Conforme";
            case 92:
                return "Error interno no controlado";
            case 100:
                return "Expediente Rechazado";
            case 101:
                return "Expediente Despachado";
            case 102:
                return "Ingrese un Expediente";
            case 103:
                return "Ingresar el Estado de Información";
            case 104:
                return "Expediente Aprobado";
            case 105:
                return "Ingresa el Expediente para su Evaluación";
            case 106:
                return "No tiene fecha de deporte SBS";
            case 107:
                return "Expediente Entregado";
            case 108:
                return "El Estado de Información para la aprobación debe ser 1";
            case 109:
                return "Actualización y Simulación conforme";
            case 110:
                return "La Fecha de primer vencimiento debe ser los días 02 ó 24 de cada mes";
            case 111:
                return "La fecha de activación máxima puede ser hasta 7 días desde hoy, y como mí­nimo hoy";
            case 112:
                return "El expediente Aprobado, no se pueden realizar cambios.";
        }
        return codigoMensaje != null ? codigoMensaje : "Error de comunicación con Acceso Crediticio";
    }

}
