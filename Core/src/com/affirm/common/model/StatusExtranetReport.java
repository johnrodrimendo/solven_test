package com.affirm.common.model;

import com.affirm.common.model.catalog.CreditStatus;
import com.affirm.common.model.catalog.LoanApplicationStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class StatusExtranetReport {

    public static LinkedHashMap<Integer, String> getLoanStatuses() {
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        map.put(LoanApplicationStatus.APPROVED, "Aprobada");
        map.put(LoanApplicationStatus.EVAL_APPROVED, "Evaluación aprobada");
        map.put(LoanApplicationStatus.EXPIRED, "Expirada");
        map.put(LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION, "Rechazada evaluación");
        map.put(LoanApplicationStatus.REJECTED_AUTOMATIC, "Rechazada pre evaluación");

        return map;
    }

    public static LinkedHashMap<Integer, String> getLoanStatusesFDLM() {
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        map.put(LoanApplicationStatus.NEW, "Iniciadas");
        map.put(LoanApplicationStatus.EVAL_APPROVED, "Evaluación aprobada");
        map.put(LoanApplicationStatus.APPROVED, "Aprobada");
        map.put(LoanApplicationStatus.REJECTED, "Rechazado");
        return map;
    }

    public static List<String> getInternalStatuses() {
        List<String> list = new ArrayList<>();
        list.add("Doc. a revisar");
        list.add("Doc. enviada");
        list.add("Doc. por enviar");
        list.add("Doc. recibida");


        return list;
    }

    public static LinkedHashMap<Integer, String> getCreditStatuses() {
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        map.put(3, "Desembolsado");
        map.put(2, "Desembolso rechazado");
        map.put(1, "Pendiente de desembolso");

        return map;
    }

    public static LinkedHashMap<Integer, String> getCreditStatusesFDLM() {
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        map.put(CreditStatus.ORIGINATED_DISBURSED, "Desembolsado");

        return map;
    }
}
