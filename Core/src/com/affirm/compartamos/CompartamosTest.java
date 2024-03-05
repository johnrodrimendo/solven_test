package com.affirm.compartamos;

import com.google.gson.Gson;

/**
 * Created by dev5 on 29/11/17.
 */
public class CompartamosTest {

    public static void main(String[] args){
        try{

            CompartamosServiceCall serviceCall = new CompartamosServiceCall();
            Gson gson = new Gson();

            /*VariablePreEvaluacionRequest variablePreEvaluacionRequest = new VariablePreEvaluacionRequest();
            VariablePreEvaluacionResponse variablePreEvaluacionResponse = serviceCall.callTraerVariablesPreevaluacion(variablePreEvaluacionRequest, null);
            System.out.println(gson.toJson(variablePreEvaluacionResponse));


            Boolean result = serviceCall.callConsultarVariablesEvaluacion(new Cliente());
            System.out.println(result);

            DocumentoIdentidad documentoIdentidad = new DocumentoIdentidad("1", "42608040");
            Cliente cliente = serviceCall.callTraerVariablesEvaluacion(documentoIdentidad, null);

            gson = new Gson();
            System.out.println(gson.toJson(cliente));*/

            /*GenerarCreditoRequest generarCreditoRequest = new GenerarCreditoRequest(
                    new Cliente(),
                    new Cliente(),
                    new Solicitud(),
                    new Credito(),
                    new ArrayList<Direccion>(),
                    new ArrayList<FuenteIngreso>()); */

            /*GenerarCreditoResponse generarCreditoResponse = serviceCall.callGeneracionCredito(generarCreditoRequest);
            System.out.println(gson.toJson(generarCreditoResponse));*/

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
