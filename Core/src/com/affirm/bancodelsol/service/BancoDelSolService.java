package com.affirm.bancodelsol.service;

import com.affirm.common.model.transactional.LoanApplication;
import org.json.JSONArray;
import org.json.JSONObject;

public interface BancoDelSolService {

    String generarCSVCliente(int personId, int loanApplicationId, JSONObject jsonRisk) throws Exception;

    String generarCSVCredito(int creditId, int personId, int loanApplicationId) throws Exception;

    String generarCSVCuotas(int creditId) throws Exception;

    String generarCSVRisk(int loanApplicationId, JSONObject jsonRisk) throws Exception;

    String generarReportCSVRisk(JSONArray jsonRisk, boolean showReportFields) throws Exception;

    void uploadCSVdocsToFTP(int creditId, int personId, String documentNumber, int loanApplicationId) throws Exception;

    boolean isValidCUITByCBU(String cbu, String cuit) throws Exception;

    JSONObject commissionClusterByClientType() throws Exception;

    void updateBaseValuesInLoan(LoanApplication loanApplication, String documentNumber) throws Exception;

    boolean isValidCBUforBDS(String cbu);
}
