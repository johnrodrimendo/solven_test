package com.affirm.veraz.model;

import java.util.List;

public class VerazRestResponse {

    private String clientImplementationStatus;
    private String loteFecha;
    private String loteUsuCodigo;
    private String loteNum;
    private String clientImplementationUUID;
    private String clientImplementationDateCreated;
    private String clientImplementationDateModified;
    private List<ApplicantData> applicants;

    public String getClientImplementationStatus() {
        return clientImplementationStatus;
    }

    public void setClientImplementationStatus(String clientImplementationStatus) {
        this.clientImplementationStatus = clientImplementationStatus;
    }

    public String getLoteFecha() {
        return loteFecha;
    }

    public void setLoteFecha(String loteFecha) {
        this.loteFecha = loteFecha;
    }

    public String getLoteUsuCodigo() {
        return loteUsuCodigo;
    }

    public void setLoteUsuCodigo(String loteUsuCodigo) {
        this.loteUsuCodigo = loteUsuCodigo;
    }

    public String getLoteNum() {
        return loteNum;
    }

    public void setLoteNum(String loteNum) {
        this.loteNum = loteNum;
    }

    public String getClientImplementationUUID() {
        return clientImplementationUUID;
    }

    public void setClientImplementationUUID(String clientImplementationUUID) {
        this.clientImplementationUUID = clientImplementationUUID;
    }

    public String getClientImplementationDateCreated() {
        return clientImplementationDateCreated;
    }

    public void setClientImplementationDateCreated(String clientImplementationDateCreated) {
        this.clientImplementationDateCreated = clientImplementationDateCreated;
    }

    public String getClientImplementationDateModified() {
        return clientImplementationDateModified;
    }

    public void setClientImplementationDateModified(String clientImplementationDateModified) {
        this.clientImplementationDateModified = clientImplementationDateModified;
    }

    public List<ApplicantData> getApplicants() {
        return applicants;
    }

    public void setApplicants(List<ApplicantData> applicants) {
        this.applicants = applicants;
    }

    public static class VariablesDeEntrada {
        private String documento;
        private String nombre;
        private String sexo;

        public String getDocumento() {
            return documento;
        }

        public void setDocumento(String documento) {
            this.documento = documento;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getSexo() {
            return sexo;
        }

        public void setSexo(String sexo) {
            this.sexo = sexo;
        }
    }

    public static class VariablesDeSalida {
        private String Consultas_fintech;
        private String cant_cheques_no_pagados_12m;
        private String categoria;
        private String compromiso_total;
        private String consultas_financieras_1M;
        private String consultas_financieras_6M;
        private String consultas_no_financieras_6M;
        private String documento;
        private String income_predictor;
        private String job_category;
        private String max_lim_tar_act_bancarias;
        private String max_lim_tar_act_no_bancarias;
        private String observaciones_12M;
        private String observaciones_basecerrada_monto_1M;
        private String peor_bcra_6m;
        private String peor_bcra_7_12;
        private String peor_bureau_actual;
        private String peor_situacion_bureau_3M;
        private String peor_situacion_bureau_4_12M;
        private String prom_lim_tarj_6_meses;
        private String score_poblacion;
        private String score_veraz;
        private String sexo;
        private String sum_cuotas_pp;
        private String sum_pago_minimo;
        private String val_doc_status;
        private String vcb_deuda_vencida;
        private String vcb_pre_gral_total;
        private String vcb_tar_gral_alta;
        private String vcb_tar_gral_total;

        public String getConsultas_fintech() {
            return Consultas_fintech;
        }

        public void setConsultas_fintech(String consultas_fintech) {
            Consultas_fintech = consultas_fintech;
        }

        public String getCant_cheques_no_pagados_12m() {
            return cant_cheques_no_pagados_12m;
        }

        public void setCant_cheques_no_pagados_12m(String cant_cheques_no_pagados_12m) {
            this.cant_cheques_no_pagados_12m = cant_cheques_no_pagados_12m;
        }

        public String getCategoria() {
            return categoria;
        }

        public void setCategoria(String categoria) {
            this.categoria = categoria;
        }

        public String getCompromiso_total() {
            return compromiso_total;
        }

        public void setCompromiso_total(String compromiso_total) {
            this.compromiso_total = compromiso_total;
        }

        public String getConsultas_financieras_1M() {
            return consultas_financieras_1M;
        }

        public void setConsultas_financieras_1M(String consultas_financieras_1M) {
            this.consultas_financieras_1M = consultas_financieras_1M;
        }

        public String getConsultas_financieras_6M() {
            return consultas_financieras_6M;
        }

        public void setConsultas_financieras_6M(String consultas_financieras_6M) {
            this.consultas_financieras_6M = consultas_financieras_6M;
        }

        public String getConsultas_no_financieras_6M() {
            return consultas_no_financieras_6M;
        }

        public void setConsultas_no_financieras_6M(String consultas_no_financieras_6M) {
            this.consultas_no_financieras_6M = consultas_no_financieras_6M;
        }

        public String getDocumento() {
            return documento;
        }

        public void setDocumento(String documento) {
            this.documento = documento;
        }

        public String getIncome_predictor() {
            return income_predictor;
        }

        public void setIncome_predictor(String income_predictor) {
            this.income_predictor = income_predictor;
        }

        public String getJob_category() {
            return job_category;
        }

        public void setJob_category(String job_category) {
            this.job_category = job_category;
        }

        public String getMax_lim_tar_act_bancarias() {
            return max_lim_tar_act_bancarias;
        }

        public void setMax_lim_tar_act_bancarias(String max_lim_tar_act_bancarias) {
            this.max_lim_tar_act_bancarias = max_lim_tar_act_bancarias;
        }

        public String getMax_lim_tar_act_no_bancarias() {
            return max_lim_tar_act_no_bancarias;
        }

        public void setMax_lim_tar_act_no_bancarias(String max_lim_tar_act_no_bancarias) {
            this.max_lim_tar_act_no_bancarias = max_lim_tar_act_no_bancarias;
        }

        public String getObservaciones_12M() {
            return observaciones_12M;
        }

        public void setObservaciones_12M(String observaciones_12M) {
            this.observaciones_12M = observaciones_12M;
        }

        public String getObservaciones_basecerrada_monto_1M() {
            return observaciones_basecerrada_monto_1M;
        }

        public void setObservaciones_basecerrada_monto_1M(String observaciones_basecerrada_monto_1M) {
            this.observaciones_basecerrada_monto_1M = observaciones_basecerrada_monto_1M;
        }

        public String getPeor_bcra_6m() {
            return peor_bcra_6m;
        }

        public void setPeor_bcra_6m(String peor_bcra_6m) {
            this.peor_bcra_6m = peor_bcra_6m;
        }

        public String getPeor_bcra_7_12() {
            return peor_bcra_7_12;
        }

        public void setPeor_bcra_7_12(String peor_bcra_7_12) {
            this.peor_bcra_7_12 = peor_bcra_7_12;
        }

        public String getPeor_bureau_actual() {
            return peor_bureau_actual;
        }

        public void setPeor_bureau_actual(String peor_bureau_actual) {
            this.peor_bureau_actual = peor_bureau_actual;
        }

        public String getPeor_situacion_bureau_3M() {
            return peor_situacion_bureau_3M;
        }

        public void setPeor_situacion_bureau_3M(String peor_situacion_bureau_3M) {
            this.peor_situacion_bureau_3M = peor_situacion_bureau_3M;
        }

        public String getPeor_situacion_bureau_4_12M() {
            return peor_situacion_bureau_4_12M;
        }

        public void setPeor_situacion_bureau_4_12M(String peor_situacion_bureau_4_12M) {
            this.peor_situacion_bureau_4_12M = peor_situacion_bureau_4_12M;
        }

        public String getProm_lim_tarj_6_meses() {
            return prom_lim_tarj_6_meses;
        }

        public void setProm_lim_tarj_6_meses(String prom_lim_tarj_6_meses) {
            this.prom_lim_tarj_6_meses = prom_lim_tarj_6_meses;
        }

        public String getScore_poblacion() {
            return score_poblacion;
        }

        public void setScore_poblacion(String score_poblacion) {
            this.score_poblacion = score_poblacion;
        }

        public String getScore_veraz() {
            return score_veraz;
        }

        public void setScore_veraz(String score_veraz) {
            this.score_veraz = score_veraz;
        }

        public String getSexo() {
            return sexo;
        }

        public void setSexo(String sexo) {
            this.sexo = sexo;
        }

        public String getSum_cuotas_pp() {
            return sum_cuotas_pp;
        }

        public void setSum_cuotas_pp(String sum_cuotas_pp) {
            this.sum_cuotas_pp = sum_cuotas_pp;
        }

        public String getSum_pago_minimo() {
            return sum_pago_minimo;
        }

        public void setSum_pago_minimo(String sum_pago_minimo) {
            this.sum_pago_minimo = sum_pago_minimo;
        }

        public String getVal_doc_status() {
            return val_doc_status;
        }

        public void setVal_doc_status(String val_doc_status) {
            this.val_doc_status = val_doc_status;
        }

        public String getVcb_deuda_vencida() {
            return vcb_deuda_vencida;
        }

        public void setVcb_deuda_vencida(String vcb_deuda_vencida) {
            this.vcb_deuda_vencida = vcb_deuda_vencida;
        }

        public String getVcb_pre_gral_total() {
            return vcb_pre_gral_total;
        }

        public void setVcb_pre_gral_total(String vcb_pre_gral_total) {
            this.vcb_pre_gral_total = vcb_pre_gral_total;
        }

        public String getVcb_tar_gral_alta() {
            return vcb_tar_gral_alta;
        }

        public void setVcb_tar_gral_alta(String vcb_tar_gral_alta) {
            this.vcb_tar_gral_alta = vcb_tar_gral_alta;
        }

        public String getVcb_tar_gral_total() {
            return vcb_tar_gral_total;
        }

        public void setVcb_tar_gral_total(String vcb_tar_gral_total) {
            this.vcb_tar_gral_total = vcb_tar_gral_total;
        }
    }

    public static class ApplicantData{
        private SmartsResponse SMARTS_RESPONSE;

        public SmartsResponse getSMARTS_RESPONSE() {
            return SMARTS_RESPONSE;
        }

        public void setSMARTS_RESPONSE(SmartsResponse SMARTS_RESPONSE) {
            this.SMARTS_RESPONSE = SMARTS_RESPONSE;
        }
    }

    public static class SmartsResponse{
        private VariablesDeEntrada variablesDeEntrada;
        private VariablesDeSalida VariablesDeSalida;

        public VariablesDeEntrada getVariablesDeEntrada() {
            return variablesDeEntrada;
        }

        public void setVariablesDeEntrada(VariablesDeEntrada variablesDeEntrada) {
            this.variablesDeEntrada = variablesDeEntrada;
        }

        public VerazRestResponse.VariablesDeSalida getVariablesDeSalida() {
            return VariablesDeSalida;
        }

        public void setVariablesDeSalida(VerazRestResponse.VariablesDeSalida variablesDeSalida) {
            VariablesDeSalida = variablesDeSalida;
        }
    }
}
