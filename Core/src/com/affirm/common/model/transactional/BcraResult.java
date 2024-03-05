package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author Romulo Galindo Tanta
 */
public class BcraResult implements Serializable {

    private Integer queryId;
    private Integer inDocumentType;
    private String inDocumentNumber;
    private List<Deudor> deudores = new ArrayList<>();
    private List<Cheque> cheques = new ArrayList<>();
    private List<DeudaBanco> historialDeudas = new ArrayList<>();
    private String originDate;

    private final Comparator<DeudaBanco.RegistroDeuda> comparatorMonto = Comparator.comparingDouble(d -> d.getMontoDouble());
    private final Comparator<DeudaBanco.RegistroDeuda> comparatorSituacion = Comparator.comparingDouble(d -> d.getSituacionInteger());

    public String getJsonDeudores(){
        Type listType = new TypeToken<List<Deudor>>() {}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(deudores, listType);
        return json;
    }

    public String getJsonCheques(){
        Type listType = new TypeToken<List<Cheque>>() {}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(cheques, listType);
        return json;
    }

    public String getJsonHistorialDeudas(){
        Type listType = new TypeToken<List<DeudaBanco>>() {}.getType();
        Gson gson = new Gson();
        return gson.toJson(historialDeudas, listType);
    }

    public void fillFromDb(JSONObject json) {
        setQueryId(JsonUtil.getIntFromJson(json, "queryId", null));
        setInDocumentType(JsonUtil.getIntFromJson(json, "in_document_type", null));
        setInDocumentNumber(JsonUtil.getStringFromJson(json, "in_document_number", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_deudor", null) != null) {
            setDeudores(new Gson().fromJson(
                    JsonUtil.getJsonArrayFromJson(json, "js_deudor", null).toString(),
                    new TypeToken<List<Deudor>>() {
                    }.getType()));
        }
        if (JsonUtil.getJsonArrayFromJson(json, "js_cheque", null) != null) {
            setCheques(new Gson().fromJson(
                    JsonUtil.getJsonArrayFromJson(json, "js_cheque", null).toString(),
                    new TypeToken<List<Cheque>>() {
                    }.getType()));
        }
        //
        if (JsonUtil.getJsonArrayFromJson(json, "js_deudas", null) != null) {
            setHistorialDeudas(new Gson().fromJson(
                    JsonUtil.getJsonArrayFromJson(json, "js_deudas", null).toString(),
                    new TypeToken<List<DeudaBanco>>() {
                    }.getType()));
        }

        setOriginDate(JsonUtil.getStringFromJson(json, "origin_date", null));
    }

    public Integer getQueryId() {
        return queryId;
    }

    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }

    public Integer getInDocumentType() {
        return inDocumentType;
    }

    public void setInDocumentType(Integer inDocumentType) {
        this.inDocumentType = inDocumentType;
    }

    public String getInDocumentNumber() {
        return inDocumentNumber;
    }

    public void setInDocumentNumber(String inDocumentNumber) {
        this.inDocumentNumber = inDocumentNumber;
    }

    public List<Deudor> getDeudores() {
        return deudores;
    }

    public void setDeudores(List<Deudor> deudores) {
        this.deudores = deudores;
    }

    public List<Cheque> getCheques() {
        return cheques;
    }

    public void setCheques(List<Cheque> cheques) {
        this.cheques = cheques;
    }

    public String getOriginDate() { return originDate; }

    public void setOriginDate(String originDate) { this.originDate = originDate; }

    public List<DeudaBanco> getHistorialDeudas() {
        return historialDeudas;
    }

    public void setHistorialDeudas(List<DeudaBanco> historialDeudas) {
        this.historialDeudas = historialDeudas;
    }

    public Double getDeudaTotalByPeriod(int i) {
        return historialDeudas.stream().mapToDouble(h -> h.getHistorial().get(i).getMontoDouble()).sum();
    }

    public Integer getWorstSituacionByPeriod(int i) {
        return historialDeudas.stream().map(h -> h.getHistorial().get(i)).max(comparatorSituacion).get().getSituacionInteger();
    }

    public Double getDeudaWorstSituacionByPeriod(int i) {
        Double total = 0.0;

        Integer worstSituation = getWorstSituacionByPeriod(i);

        for (DeudaBanco deudaBanco: historialDeudas) {
            if (deudaBanco.getHistorial().get(i).getSituacionInteger().equals(worstSituation)) {
                total += deudaBanco.getHistorial().get(i).getMontoDouble();
            }
        }

        return total;
    }

    public Integer getWorstSituationBetween(int from, int to) {
        int worstSituation = 1;

        for (DeudaBanco deudaBanco: historialDeudas) {
            for (DeudaBanco.RegistroDeuda r: deudaBanco.getHistorial().subList(from, to)) {
                if (r.getSituacionInteger() > worstSituation) {
                    worstSituation = r.getSituacionInteger();
                }
            }
        }

        return worstSituation;
    }

    public Double getMaxDeudaBetween(int from, int to) {
        Double maxDeuda = 0.0;

        for (DeudaBanco deudaBanco: historialDeudas) {
            for (DeudaBanco.RegistroDeuda r: deudaBanco.getHistorial().subList(from, to)) {
                if (r.getMontoDouble() > maxDeuda) {
                    maxDeuda = r.getMontoDouble();
                }
            }
        }

        return maxDeuda;
    }

    public Integer getNumberEntitiesBetween(int from, int to) {
        ArrayList<String> entities = new ArrayList<>();

        for (DeudaBanco deudaBanco: historialDeudas) {
            if (!entities.contains(deudaBanco.getNombre())) {
                for (DeudaBanco.RegistroDeuda r : deudaBanco.getHistorial().subList(from, to)) {
                    if (r.getMontoDouble() > 0) {
                        entities.add(deudaBanco.getNombre());
                        break;
                    }
                }
            }
        }

        return entities.size();
    }

    public Double getDeudaWorstSituacionBetween(int from, int to) {
        Integer worstSituation = 0;
        List<String> periodsWithWorstSituation = new ArrayList<>();

        // Get the periods/months with the worst situation and update the worst situation
        for (DeudaBanco deudaBanco: historialDeudas) {
            for (DeudaBanco.RegistroDeuda r: deudaBanco.getHistorial().subList(from, to)) {
                if (r.getSituacionInteger() >= worstSituation) {
                    if (r.getSituacionInteger() > worstSituation) {
                        worstSituation = r.getSituacionInteger();
                        periodsWithWorstSituation.clear();
                    }
                    if (!periodsWithWorstSituation.contains(r.getPeriodo())) {
                        periodsWithWorstSituation.add(r.getPeriodo());
                    }
                }
            }
        }

        // Sum  amount by period with the worst situation by bank
        Map<String, Double> deudasPeriodos = new HashMap<>();
        for (DeudaBanco deudaBanco: historialDeudas) {
            for (DeudaBanco.RegistroDeuda r: deudaBanco.getHistorial().subList(from, to)) {
                if (periodsWithWorstSituation.contains(r.getPeriodo()) && worstSituation.equals(r.getSituacionInteger())) {
                    if (deudasPeriodos.containsKey(r.getPeriodo())) {
                        deudasPeriodos.put(r.getPeriodo(), deudasPeriodos.get(r.getPeriodo()) + r.getMontoDouble());
                    } else {
                        deudasPeriodos.put(r.getPeriodo(), r.getMontoDouble());
                    }
                }
            }
        }

        return deudasPeriodos.values().stream().max(Comparator.comparingDouble(m -> m)).orElse(0.0);
    }

    public void addDepthToHistorical() {
        for (Deudor deudor: this.deudores) {
            for (DeudaBanco db: this.historialDeudas) {
                if (deudor.getEntidad().equals(db.getNombre())){
                    db.getHistorial().add(0, db.new RegistroDeuda(this.formatDate(deudor.getPeriodo()),deudor.getSituacion(),deudor.getMonto()));
                    break;
                }
            }
        }
    }

    public String formatDate(String period){
        String periodo[] = period.split("/");
        StringBuilder periodFormatted = new StringBuilder();
        String month = periodo[0];
        String year  = periodo[1];

        if (month.length()==2) {
            periodFormatted.append(month);
        }

        String aux = "";
        switch (year.length()) {
            case 1 :aux="/200"; break;
            case 2 :aux="/20" ; break;
            case 3 :aux="/2"  ; break;
            case 4 :aux="/"   ; break;
        }

        periodFormatted.append(aux.concat(year));
        return periodFormatted.toString();
    }

    public class Deudor implements Serializable {

        private String denominacionDeudor;
        private String entidad;
        private String periodo;
        private String situacion;
        private String monto;
        private String diasRetraso;
        private String observaciones;

        public Deudor(String denominacionDeudor, String entidad, String periodo, String situacion, String monto, String diasRetraso, String observaciones) {
            this.denominacionDeudor = denominacionDeudor;
            this.entidad = entidad;
            this.periodo = periodo;
            this.situacion = situacion;
            this.monto = monto;
            this.diasRetraso = diasRetraso;
            this.observaciones = observaciones;
        }

        public String getDenominacionDeudor() {
            return denominacionDeudor;
        }

        public void setDenominacionDeudor(String denominacionDeudor) {
            this.denominacionDeudor = denominacionDeudor;
        }

        public String getEntidad() {
            return entidad;
        }

        public void setEntidad(String entidad) {
            this.entidad = entidad;
        }

        public String getPeriodo() {
            return periodo;
        }

        public void setPeriodo(String periodo) {
            this.periodo = periodo;
        }

        public String getSituacion() {
            return situacion;
        }

        public void setSituacion(String situacion) {
            this.situacion = situacion;
        }

        public String getMonto() {
            return monto;
        }

        public void setMonto(String monto) {
            this.monto = monto;
        }

        public String getDiasRetraso() {
            return diasRetraso;
        }

        public void setDiasRetraso(String diasRetraso) {
            this.diasRetraso = diasRetraso;
        }

        public String getObservaciones() {
            return observaciones;
        }

        public void setObservaciones(String observaciones) {
            this.observaciones = observaciones;
        }
    }

    public class Cheque implements Serializable {

        private String nroCheque;
        private String fecharetrazo;
        private String monto;
        private String casual;
        private String fechaPago;
        private String fechaPagoMulta;
        private String revision;
        private String prjud;

        public Cheque(String nroCheque, String fecharetrazo, String monto, String casual, String fechaPago, String fechaPagoMulta, String revision, String prjud) {
            this.nroCheque = nroCheque;
            this.fecharetrazo = fecharetrazo;
            this.monto = monto;
            this.casual = casual;
            this.fechaPago = fechaPago;
            this.fechaPagoMulta = fechaPagoMulta;
            this.revision = revision;
            this.prjud = prjud;
        }

        public Date getFechaRetrazoAsDate() {
            if(fecharetrazo == null){
                return null;
            }

            try {
                return new SimpleDateFormat("dd/MM/yyyy").parse(fecharetrazo);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        public String getNroCheque() {
            return nroCheque;
        }

        public void setNroCheque(String nroCheque) {
            this.nroCheque = nroCheque;
        }

        public String getFecharetrazo() {
            return fecharetrazo;
        }

        public void setFecharetrazo(String fecharetrazo) {
            this.fecharetrazo = fecharetrazo;
        }

        public String getMonto() {
            return monto;
        }

        public void setMonto(String monto) {
            this.monto = monto;
        }

        public String getCasual() {
            return casual;
        }

        public void setCasual(String casual) {
            this.casual = casual;
        }

        public String getFechaPago() {
            return fechaPago;
        }

        public void setFechaPago(String fechaPago) {
            this.fechaPago = fechaPago;
        }

        public String getFechaPagoMulta() {
            return fechaPagoMulta;
        }

        public void setFechaPagoMulta(String fechaPagoMulta) {
            this.fechaPagoMulta = fechaPagoMulta;
        }

        public String getRevision() {
            return revision;
        }

        public void setRevision(String revision) {
            this.revision = revision;
        }

        public String getPrjud() {
            return prjud;
        }

        public void setPrjud(String prjud) {
            this.prjud = prjud;
        }
    }


    public class DeudaBanco implements Serializable{

        private String nombre;
        private List <RegistroDeuda> historial;

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getNombre() {
            return nombre;
        }

        public void setHistorial(List<RegistroDeuda> historial) {
            this.historial = historial;
        }

        public List<RegistroDeuda> getHistorial() {
            return historial;
        }

        public class RegistroDeuda implements  Serializable{

            String periodo;
            String situacion;
            String monto;

            public RegistroDeuda(String periodo, String situacion, String monto){
                this.periodo=periodo;
                this.situacion= situacion;
                this.monto=monto;
            }

            public Date getPeriodoAsDate() {
                if(periodo == null){
                    return null;
                }

                try {
                    return new SimpleDateFormat("MM/yyyy").parse(periodo);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            public void setMonto(String monto) {
                this.monto = monto;
            }

            public void setPeriodo(String periodo) {
                this.periodo = periodo;
            }

            public void setSituacion(String situacion) {
                this.situacion = situacion;
            }

            public String getMonto() {
                return monto;
            }

            public Double getMontoDouble() {
                NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                try {
                    return format.parse(getMonto()).doubleValue() * 1000;
                } catch (ParseException e) {
                    return 0.0;
                }
            }

            public String getSituacion() {
                return situacion;
            }

            public int getSituacionAsInt() {
                if(situacion.equalsIgnoreCase("N/A"))
                    return 1;
                else if(situacion.equalsIgnoreCase("-"))
                    return 0;
                else
                    return Integer.parseInt(situacion);
            }

            public String getSituacionFormatted() {
                return situacion.equals("N/A") ? "-" : situacion;
            }

            public Integer getSituacionInteger() {
                try {
                    return Integer.parseInt(situacion);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }

            public String getPeriodo() {
                return periodo;
            }

            public Double getMontoForHardFilter() {
                if(monto == null || monto.isEmpty() || monto.equalsIgnoreCase("N/A") || monto.equalsIgnoreCase("-"))
                    return 0.0;
                try {
                    NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                    return format.parse(getMonto().replace(".", "")).doubleValue();
                } catch (ParseException e) {
                    return 0.0;
                }
            }
        }
    }

}
