package com.affirm.acceso;

import com.affirm.acceso.model.*;
import com.affirm.acceso.util.AccesoUtilCall;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.SqlErrorMessageException;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev5 on 26/07/17.
 */
public class AccesoTest {

    public static void main(String[] args){
        try {
            Gson gson = new Gson();
            AccesoUtilCall serviceCall = new AccesoUtilCall();


            String user = "sln01fturc";
            String password = "873c9cd310a9ab04e713df9a76dfb5ee";

            /*****************************************/
            /*****************************************/
            /******************LOGIN******************/
            /*****************************************/
            /*****************************************/
            Login login = new Login(user, password, "", "", "", 1, 3);
            String jsonLoginData = gson.toJson(login);
            String url = "https://sd1.accesocrediticio.com/api/acr/ppregsesini";
            String key = "DCB8C813C642DDA755ABE7C1FD374659";
            JSONObject jsonResponse = null/*serviceCall.call(jsonLoginData, url, key)*/;
            JSONArray arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
            Integer estado = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_estado", null);
            Integer id_sesion = 0;
            if(estado == 0){
                id_sesion = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "id_sesion", null);
                System.out.println("ID Sesion : " + id_sesion);
                System.out.println("****************************");
                //id_sesion = 13245838;

                /*****************************************/
                /*****************************************/
                /**************VEHICULOS******************/
                /***************CATALOGO******************/
                /*****************************************/
                /*****************************************/
                url = "https://sd1.accesocrediticio.com/api/acr/f_ws_catalogosmarveh";
                key = "D9357E3A147AA7E225CF2DF60B3D6BE6";
                Expediente expediente = new Expediente(id_sesion);
                String jsonCatalogoData = gson.toJson(expediente);
                jsonResponse = null/*serviceCall.call(jsonCatalogoData, url, key)*/;
                arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
                List<CatalogoVehicular> catalogoVehiculos = new ArrayList<>();
                for (int i = 0; i < arrResultado.length(); i++) {
                    CatalogoVehicular catalogoVehicular = new CatalogoVehicular();
                    JSONObject catalogo = arrResultado.getJSONObject(i);
                    catalogoVehicular.setCodError(JsonUtil.getIntFromJson(catalogo, "co_mensaj", null));

                    if(catalogoVehicular.getCodError() == 10){
                        catalogoVehicular.setPoliticaVehicular(JsonUtil.getIntFromJson(catalogo, "co_polveh", null));
                        catalogoVehicular.setCodOperador(JsonUtil.getIntFromJson(catalogo, "co_operad", null));
                        catalogoVehicular.setDescOperador(JsonUtil.getStringFromJson(catalogo, "no_operad", null));
                        catalogoVehicular.setCodProducto(JsonUtil.getIntFromJson(catalogo, "co_produc", null));
                        catalogoVehicular.setNombreProducto(JsonUtil.getStringFromJson(catalogo, "no_produc", null));
                        catalogoVehicular.setCodSubProducto(JsonUtil.getIntFromJson(catalogo, "ti_modelo", null));
                        catalogoVehicular.setNombreSubProducto(JsonUtil.getStringFromJson(catalogo, "no_subpro", null));
                        catalogoVehicular.setCentroOperaciones(JsonUtil.getIntFromJson(catalogo, "co_cenope", null));
                        catalogoVehicular.setNombreCentroOperaciones(JsonUtil.getStringFromJson(catalogo, "no_cenope", null));
                        catalogoVehicular.setCodConcesionario(JsonUtil.getIntFromJson(catalogo, "co_conces", null));
                        catalogoVehicular.setNombreConcesionario(JsonUtil.getStringFromJson(catalogo, "no_conces", null));
                        catalogoVehicular.setTipoEdad(JsonUtil.getIntFromJson(catalogo, "ti_poleda", null));
                        catalogoVehicular.setDescTipoEdad(JsonUtil.getStringFromJson(catalogo, "no_poleda", null));
                        catalogoVehicular.setCodMarca(JsonUtil.getIntFromJson(catalogo, "co_marcas", null));
                        catalogoVehicular.setDescMarca(JsonUtil.getStringFromJson(catalogo, "no_marcas", null));
                        catalogoVehicular.setCodModelo(JsonUtil.getIntFromJson(catalogo, "co_modelo", null));
                        catalogoVehicular.setDescModelo(JsonUtil.getStringFromJson(catalogo, "no_modelo", null));
                        catalogoVehicular.setPrecioVehiculo(JsonUtil.getDoubleFromJson(catalogo, "im_preveh", null));
                        catalogoVehicular.setAnhoFabricacion(JsonUtil.getIntFromJson(catalogo, "de_anofab", null));
                        catalogoVehicular.setVersionVehicular(JsonUtil.getIntFromJson(catalogo, "co_verveh", null));
                        catalogoVehicular.setDescVersionVehicular(JsonUtil.getStringFromJson(catalogo, "no_verveh", null));
                        catalogoVehicular.setCodColor(JsonUtil.getIntFromJson(catalogo, "ti_colveh", null));
                        catalogoVehicular.setDescColor(JsonUtil.getStringFromJson(catalogo, "no_colveh", null));
                        catalogoVehicular.setCodTipoTransmision(JsonUtil.getIntFromJson(catalogo, "ti_traveh", null));
                        catalogoVehicular.setDescTipoTransmision(JsonUtil.getStringFromJson(catalogo, "no_traveh", null));
                        catalogoVehicular.setCodTipoCombustible(JsonUtil.getStringFromJson(catalogo, "co_tipcom", null));
                        catalogoVehicular.setDescTipoCombustible(JsonUtil.getStringFromJson(catalogo, "no_tipcom", null));
                        catalogoVehicular.setCodTipoCuotas(JsonUtil.getStringFromJson(catalogo, "ti_cuotas", null));
                        catalogoVehicular.setDescTipoCuota(JsonUtil.getStringFromJson(catalogo, "no_cuotas", null));
                        catalogoVehiculos.add(catalogoVehicular);
                    }else{
                        throw new SqlErrorMessageException(null, "");
                    }
                }
                System.out.println("****************************");


                /*****************************************/
                /*****************************************/
                /**************EXPEDIENTE*****************/
                /***************CONSULTA******************/
                /*****************************************/
                /*****************************************/
                url = "https://sd1.accesocrediticio.com/api/acr/f_ws_consultaexpediente";
                key = "D9357E3A147AA7E225CF2DF60B3D6BE6";
                expediente = new Expediente(id_sesion, "40235638");
                String jsonExpedienteData = gson.toJson(expediente);
                jsonResponse = null/*serviceCall.call(jsonExpedienteData, url, key)*/;
                arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
                estado = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_estado", null);
                Integer resultTI = 0;
                if(estado == 0){
                    resultTI = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "ti_result", null);
                    if(resultTI == 2){
                        System.out.println("****************************");
                        /*****************************************/
                        /*****************************************/
                        /**************EXPEDIENTE*****************/
                        /***************CREACION******************/
                        /*****************************************/
                        /*****************************************/
                        url = "https://sd1.accesocrediticio.com/api/acr/f_ws_crearexpediente";
                        key = "C902FD49F471D606C19D4421C3706481";
                        Expediente expedienteFull = new Expediente(
                                id_sesion,
                                19,
                                1,
                                716,
                                "150101",
                                5,
                                2,
                                3,
                                2, null, null, "", null, null, null,
                                2,
                                "D",
                                "42608040",
                                "IBERICO",
                                "HIDALGO",
                                "MARTIN",
                                "1984-07-24",
                                "M",
                                "SO",
                                "988153580",
                                "GI",
                                "DF",
                                "5",
                                0,
                                12,
                                5000.00,
                                0.0,
                                0.0,
                                "150101",
                                "P",
                                2,
                                "N",
                                "","","","","",null, "",
                                "","","","","",null, "", "", 0,
                                "","","","","",null, "");
                        String jsonExpedienteFullData = gson.toJson(expedienteFull);
                        jsonResponse = null/*serviceCall.call(jsonExpedienteFullData, url, key)*/;
                        arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
                        estado = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_estado", null);
                        expediente.setNroExpediente(JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_expedi", null));
                        Integer calificacion = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "ti_califi", null);
                        if(calificacion == 1) System.out.println("Nro. Expediente : " + expediente.getNroExpediente());
                        else if(calificacion == 2) System.out.println("Nro. Expediente : Observado");
                        else if(calificacion == 3) System.out.println("Nro. Expediente : Rechazado");
                        if(estado == 0 && calificacion != 3){
                            System.out.println("****************************");
                            /*****************************************/
                            /*****************************************/
                            /***************DIRECCION*****************/
                            /***************CREACION******************/
                            /*****************************************/
                            /*****************************************/
                            url = "https://sd1.accesocrediticio.com/api/acr/f_ws_direccion";
                            key = "D9357E3A147AA7E225CF2DF60B3D6BE6";
                            Direccion direccionDomicilio = new Direccion(id_sesion, expediente.getNroExpediente(), "TI", "D", 1, "Mariscal La Mar", "Av. Mariscal La Mar 398 Miraflores", "Referencia", 1, "150101");
                            String jsonDireccionDomiclioData = gson.toJson(direccionDomicilio);
                            jsonResponse = null/*serviceCall.call(jsonDireccionDomiclioData, url, key)*/;
                            arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
                            Integer estadoDomicilio = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_estado", null);

                            Direccion direccionLaboral = new Direccion(id_sesion, expediente.getNroExpediente(), "TI", "L", 1, "Mariscal La Mar", "Av. Mariscal La Mar 398 Miraflores", "Referencia", 1, "150101");
                            String jsonDireccionLaboralData = gson.toJson(direccionLaboral);
                            jsonResponse = null/*serviceCall.call(jsonDireccionLaboralData, url, key)*/;
                            arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
                            Integer estadoLaboral = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_estado", null);
                            if(estadoDomicilio == 0 && estadoLaboral == 0){
                                System.out.println("****************************");
                                /*****************************************/
                                /*****************************************/
                                /****************COTIZAR******************/
                                /*****************************************/
                                /*****************************************/
                                url = "https://sd1.accesocrediticio.com/api/acr/f_ws_cotizador";
                                key = "C902FD49F471D606C19D4421C3706481";
                                Cotizador cotizador = new Cotizador(
                                        id_sesion,
                                        expediente.getNroExpediente(),
                                        "S",
                                        "M",
                                        null,
                                        14,
                                        1103,
                                        4,
                                        "2017-08-05",
                                        17000.00,
                                        0.0,
                                        0,
                                        1
                                );
                                String jsonCotizadorData = gson.toJson(cotizador);
                                jsonResponse = null/*serviceCall.call(jsonCotizadorData, url, key)*/;
                                arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
                                estado = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_estado", null);
                                if(estado == 0){
                                    Integer score = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "ti_rescro", null);
                                    if(score != 5){
                                        List<Oferta> ofertas = new ArrayList<>();
                                        for(int i=0;i<arrResultado.length();i++){
                                            Oferta oferta = new Oferta();
                                            oferta.setNumExpediente(JsonUtil.getIntFromJson(arrResultado.getJSONObject(i), "co_expedi", null));
                                            oferta.setNumCuotas(JsonUtil.getIntFromJson(arrResultado.getJSONObject(i), "ca_cuotas", null));
                                            oferta.setMonto(JsonUtil.getDoubleFromJson(arrResultado.getJSONObject(i), "im_financ", null));
                                            oferta.setCuota(JsonUtil.getDoubleFromJson(arrResultado.getJSONObject(i), "im_cuotas", null));
                                            oferta.setScore(JsonUtil.getIntFromJson(arrResultado.getJSONObject(i), "ti_rescro", null));
                                            oferta.setDescripion(JsonUtil.getStringFromJson(arrResultado.getJSONObject(i), "no_consco", null));
                                            oferta.setTipoCambio(JsonUtil.getDoubleFromJson(arrResultado.getJSONObject(i), "ti_cambio", null));
                                            oferta.setMoneda(JsonUtil.getStringFromJson(arrResultado.getJSONObject(i), "ti_moneda", null));
                                            oferta.setCuotaInicial(JsonUtil.getDoubleFromJson(arrResultado.getJSONObject(i), "im_cuoini", null));
                                            oferta.setTea(JsonUtil.getDoubleFromJson(arrResultado.getJSONObject(i), "va_tasanu", null));
                                            oferta.setTcea(JsonUtil.getDoubleFromJson(arrResultado.getJSONObject(i), "va_tascea", null));
                                            ofertas.add(oferta);
                                        }
                                        System.out.println("Ofertas obtenidas");
                                        System.out.println("****************************");
                                        /*****************************************/
                                        /*****************************************/
                                        /***************CRONOGRAMA****************/
                                        /****************CONSULTA*****************/
                                        /*****************************************/
                                        /*****************************************/
                                        url = "https://sd1.accesocrediticio.com/api/acr/f_ws_cronograma";
                                        key = "D9357E3A147AA7E225CF2DF60B3D6BE6";
                                        Expediente expediente1 = new Expediente(id_sesion, expediente.getNroExpediente());
                                        String jsonCronogramaData = gson.toJson(expediente1);
                                        jsonResponse = null/*serviceCall.call(jsonCronogramaData, url, key)*/;
                                        arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
                                        estado = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_estado", null);
                                        if(estado == 0){
                                            List<Cronograma> cronograma = new ArrayList<>();
                                            for(int i=0;i<arrResultado.length();i++){
                                                Cronograma cuotaCronograma = new Cronograma();
                                                JSONObject cuota = arrResultado.getJSONObject(i);
                                                cuotaCronograma.setNumCuotas(JsonUtil.getIntFromJson(cuota, "nu_cuotas", null));
                                                cuotaCronograma.setFechaVencimiento(JsonUtil.getPostgresDateFromJson(cuota, "fe_vencim", null));
                                                cuotaCronograma.setDiasCuota(JsonUtil.getIntFromJson(cuota, "ca_diacuo",  null));
                                                cuotaCronograma.setSaldoInicial(JsonUtil.getDoubleFromJson(cuota, "im_salini",  null));
                                                cuotaCronograma.setCapital(JsonUtil.getDoubleFromJson(cuota, "im_capita",  null));
                                                cuotaCronograma.setInteres(JsonUtil.getDoubleFromJson(cuota, "im_intere",  null));
                                                cuotaCronograma.setInteresCapitalizado(JsonUtil.getDoubleFromJson(cuota, "im_intcap",  null));
                                                cuotaCronograma.setCuota(JsonUtil.getDoubleFromJson(cuota, "im_cuotas",  null));
                                                cuotaCronograma.setSaldoFinal(JsonUtil.getDoubleFromJson(cuota, "im_salfin",  null));
                                                cuotaCronograma.setSeguroVehicular(JsonUtil.getDoubleFromJson(cuota, "im_seguro",  null));
                                                cuotaCronograma.setDesgravamen(JsonUtil.getDoubleFromJson(cuota, "im_desgra",  null));
                                                cuotaCronograma.setCuotaTotal(JsonUtil.getDoubleFromJson(cuota, "im_cuotot",  null));
                                                cronograma.add(cuotaCronograma);
                                            }
                                            System.out.println("Cronograma obtenido");
                                            System.out.println("****************************");
                                            /*****************************************/
                                            /*****************************************/
                                            /***************INFORMACION***************/
                                            /****************ADICIONAL****************/
                                            /*****************************************/
                                            /*****************************************/
                                            url = "https://sd1.accesocrediticio.com/api/acr/f_ws_informacionadicional";
                                            key = "D9357E3A147AA7E225CF2DF60B3D6BE6";
                                            InformacionAdicional informacionAdicional = new InformacionAdicional(
                                                    id_sesion,
                                                    expediente.getNroExpediente(),
                                                    "TI",
                                                    187,
                                                    "Dev",
                                                    "Solven",
                                                    "20550465494",
                                                    "FinTech",
                                                    "GI",
                                                    "miberico@solven.pe",
                                                    "2008-10-14",
                                                    "3365878",
                                                    10000.00,
                                                    ""
                                            );
                                            String jsonInformacionAdicionalData = gson.toJson(informacionAdicional);
                                            jsonResponse = null/*serviceCall.call(jsonInformacionAdicionalData, url, key)*/;
                                            arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
                                            estado = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_estado", null);
                                            if(estado == 0) {
                                                System.out.println("Informacion Adicional registrada");
                                                System.out.println("****************************");
                                                /*****************************************/
                                                /*****************************************/
                                                /***************AGENDAMIENTO**************/
                                                /******************FIRMAS*****************/
                                                /*****************************************/
                                                /*****************************************/
                                                url = "https://sd1.accesocrediticio.com/api/acr/f_ws_agendarfirmas";
                                                key = "D9357E3A147AA7E225CF2DF60B3D6BE6";
                                                Firma firma = new Firma(expediente.getNroExpediente(), "2017-12-21", 1, "Direccion firma", "", "", " ");
                                                String jsonAgendamientoFirmasData = gson.toJson(firma);
                                                jsonResponse = null/*serviceCall.call(jsonAgendamientoFirmasData, url, key)*/;
                                                arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
                                                estado = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_estado", null);
                                                if (estado == 0) {
                                                    System.out.println("[Expediente - " + firma.getNroExpediente() + "] - Agendamiento de firmas generado");
                                                }
                                            }

                                        }
                                    }else{
                                        System.out.println("Rechazado");
                                    }
                                }
                                System.out.println("****************************");
                            }else{
                                System.out.println("Error en el servicio");
                            }
                        }
                    } else if(resultTI == 1){
                        String fecha = JsonUtil.getStringFromJson(arrResultado.getJSONObject(0), "no_result", null);
                        System.out.println("Fecha : " + fecha);
                    }
                }else{
                    System.out.println("Error en el servicio");
                }
            }else{
                Integer codigoError = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_mensaj", null);
                String mensajeError = JsonUtil.getStringFromJson(arrResultado.getJSONObject(0), "no_estado", null);
                System.out.println("[Acceso Crediticio Login] - [" + codigoError + "] : " + mensajeError);
            }

            System.out.println("****************************");
            System.out.println("****************************");
            System.out.println("****************************");

            url = "https://sd1.accesocrediticio.com/api/acr/f_ws_estadosdefirmas";
            key = "D9357E3A147AA7E225CF2DF60B3D6BE6";
            Expediente expediente = new Expediente(id_sesion);
            String jsonExpedienteData = gson.toJson(expediente);
            jsonResponse = null/*serviceCall.call(jsonExpedienteData, url, key)*/;
            arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
            estado = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_estado", null);
            if(estado == 0){
                List<EstadoFirma> estadoFirmas = new ArrayList<>();
                for(int i=0;i<arrResultado.length();i++){
                    EstadoFirma estadoFirma = new EstadoFirma();
                    JSONObject resultadoFirma = arrResultado.getJSONObject(i);
                    estadoFirma.setNroExpediente(JsonUtil.getIntFromJson(resultadoFirma, "co_expedi", null));
                    estadoFirma.setResultadoVerificacion(JsonUtil.getIntFromJson(resultadoFirma, "co_verifi", null));
                    estadoFirma.setComentarioVerificacion(JsonUtil.getStringFromJson(resultadoFirma, "de_verifi", null));
                    estadoFirma.setFechaVerificacion(JsonUtil.getPostgresDateFromJson(resultadoFirma, "fe_verifi", null));
                    estadoFirmas.add(estadoFirma);
                }
                System.out.println("Reporte de Resultado de Firmas generado");
            }
            System.out.println("****************************");
            System.out.println("****************************");
            System.out.println("****************************");

            url = "https://sd1.accesocrediticio.com/api/acr/f_ws_expedientesdespachados";
            key = "D9357E3A147AA7E225CF2DF60B3D6BE6";
            expediente = new Expediente(id_sesion);
            jsonExpedienteData = gson.toJson(expediente);
            jsonResponse = null/*serviceCall.call(jsonExpedienteData, url, key)*/;
            arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
            estado = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_estado", null);
            if(estado == 0){
                List<Expediente> expedientes = new ArrayList<>();
                for(int i=0;i<arrResultado.length();i++){
                    Expediente expedientesDespachados = new Expediente();
                    JSONObject despacho = arrResultado.getJSONObject(i);
                    expedientesDespachados.setNroExpediente(JsonUtil.getIntFromJson(despacho, "co_expedi", null));
                    expedientesDespachados.setFechaDespacho(JsonUtil.getPostgresDateFromJson(despacho, "fe_estdes", null));
                    expedientes.add(expedientesDespachados);
                }
                System.out.println("Reporte de Expedientes Despachados generado");
            }

            System.out.println("****************************");
            System.out.println("****************************");
            System.out.println("****************************");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
