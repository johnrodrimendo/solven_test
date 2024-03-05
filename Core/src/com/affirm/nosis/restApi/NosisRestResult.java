package com.affirm.nosis.restApi;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NosisRestResult {

    private List<Variable> variables;

    public void fillFromJson(JSONObject json) {
        variables = new ArrayList<>();
        JSONObject jsonDatos = json.getJSONObject("Contenido").optJSONObject("Datos");
        JSONArray jsonVariables = jsonDatos != null ? jsonDatos.getJSONArray("Variables") : new JSONArray();
        for(int i=0; i<jsonVariables.length(); i++){
            Variable variable = new Variable();
            variable.fillFromJson(jsonVariables.getJSONObject(i));
            variables.add(variable);
        }
    }

    public Integer getScore(){
        Variable variableScore = variables.stream().filter(v -> v.getNombre().equals("SCO_Vig")).findFirst().orElse(null);
        if(variableScore != null){
            try{
                return Integer.parseInt(variableScore.getValor());
            }catch (Exception ex){
            }
        }
        return null;
    }

    public Integer getSco3MTendencia(){
        Variable variableScore = variables.stream().filter(v -> v.getNombre().equalsIgnoreCase("SCO_3m_Tendencia")).findFirst().orElse(null);
        if(variableScore != null){
            try{
                return Integer.parseInt(variableScore.getValor());
            }catch (Exception ex){
            }
        }
        return null;
    }

    public Boolean getCiBancarizado(){
        Variable variableScore = variables.stream().filter(v -> v.getNombre().equalsIgnoreCase("CI_Bancarizado")).findFirst().orElse(null);
        if(variableScore != null){
            try{
                return variableScore.getValor().equalsIgnoreCase("si");
            }catch (Exception ex){
            }
        }
        return null;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    private class Variable{

        private String nombre;
        private String valor;
        private String descripcion;
        private String tipo;
        private String fechaAct;

        public void fillFromJson(JSONObject json) {
            setNombre(JsonUtil.getStringFromJson(json, "Nombre", null));
            setValor(JsonUtil.getStringFromJson(json, "Valor", null));
            setDescripcion(JsonUtil.getStringFromJson(json, "Descripcion", null));
            setTipo(JsonUtil.getStringFromJson(json, "Tipo", null));
            setFechaAct(JsonUtil.getStringFromJson(json, "FechaAct", null));
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public String getFechaAct() {
            return fechaAct;
        }

        public void setFechaAct(String fechaAct) {
            this.fechaAct = fechaAct;
        }
    }

}
