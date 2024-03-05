package com.affirm.backoffice.service.impl;

import com.affirm.backoffice.dao.CreditPaymentDAO;
import com.affirm.backoffice.model.CreditPayment;
import com.affirm.backoffice.service.CreditPaymentService;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.EmployerDAO;
import com.affirm.common.model.catalog.Bank;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.MultiCreditPayment;
import com.affirm.common.model.transactional.SalaryAdvancePayment;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.IntegerFieldValidator;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by jrodriguez on 23/08/16.
 */
@Service
public class CreditPaymentServiceImpl implements CreditPaymentService {

    @Autowired
    private CreditPaymentDAO creditPaymentDao;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private EmployerDAO employerDao;
    @Autowired
    private CatalogService catalogService;

    @Override
    public JSONArray parseOpenMarketFile(Sheet datatypeSheet) throws Exception {
        JSONArray payments = new JSONArray();
        Iterator<Row> iterator = datatypeSheet.iterator();
        boolean invalidate=false;

        String countryCode;
        String documentyType;
        String documentNumber;
        String creditCode;
        String personCode;
        String paymentDate;
        String paymentAmount;
        String operationNumber;

        while (iterator.hasNext()) {
            JSONObject paymentJson = new JSONObject();
            Row currentRow = iterator.next();
            if(currentRow.getRowNum() > 0){
                countryCode=currentRow.getCell(0).getStringCellValue();
                paymentJson.put("codPais",countryCode );
                Integer documentTypeId = 0;
                switch (Integer.valueOf(countryCode)){
                    case CountryParam.COUNTRY_PERU :
                        switch (currentRow.getCell(1).getStringCellValue().toUpperCase()){
                            case "DNI" : documentTypeId = IdentityDocumentType.DNI; break;
                            case "CE" : documentTypeId = IdentityDocumentType.CE; break;
                        }
                        break;
                    case CountryParam.COUNTRY_ARGENTINA :
                        switch (currentRow.getCell(1).getStringCellValue().toUpperCase()){
                            case "CDI" : documentTypeId = IdentityDocumentType.CDI; break;
                            case "CUIL" : documentTypeId = IdentityDocumentType.CUIL; break;
                            case "CUIT" : documentTypeId = IdentityDocumentType.CUIT; break;
                            case "DNI" : documentTypeId = IdentityDocumentType.ARG_DNI; break;
                        }
                        break;
                }

                documentyType=documentTypeId.toString();
                documentNumber=currentRow.getCell(2).getStringCellValue();
                creditCode=currentRow.getCell(3).getStringCellValue();
                personCode=currentRow.getCell(4).getStringCellValue();
                paymentDate =currentRow.getCell(5).getStringCellValue();
                currentRow.getCell(6).setCellType(Cell.CELL_TYPE_STRING);
                paymentAmount=currentRow.getCell(6).getStringCellValue();
                operationNumber = currentRow.getCell(7).getStringCellValue();
                //validate values in some fields
                if(documentTypeId==0 || !documentNumber.matches("[0-9]+") ||
                   !paymentAmount.replace(".","").replace(",","").matches("[0-9]+") ||
                   !paymentDate.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")||
                        creditCode==null || creditCode.trim().length()==0){
                    invalidate=true;
                }

                paymentJson.put("tipoDocumento", documentyType);
                paymentJson.put("numDocumento", documentNumber );
                paymentJson.put("codCredito", creditCode);
                paymentJson.put("codDepositante", personCode);

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                Date date = format.parse(paymentDate);
                SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");

                paymentJson.put("fechaPago", newFormat.format(date));
                paymentJson.put("montoPago",paymentAmount);
                paymentJson.put("numOperacion", operationNumber );
            }else{
                //validate the headers
                if( ("País*").equals(currentRow.getCell(0).getStringCellValue()) &&
                    ("Tipo de Documento*").equals(currentRow.getCell(1).getStringCellValue()) &&
                    ("Nro. Documento*").equals(currentRow.getCell(2).getStringCellValue()) &&
                    ("Código de Crédito*").equals(currentRow.getCell(3).getStringCellValue()) &&
                    ("Código de Depositante*").equals(currentRow.getCell(4).getStringCellValue()) &&
                    ("Fecha de Pago*").equals(currentRow.getCell(5).getStringCellValue()) &&
                    ("Monto de Pago*").equals(currentRow.getCell(6).getStringCellValue()) &&
                    ("Número de Operación del Banco*").equals(currentRow.getCell(7).getStringCellValue())){
                }else{
                    invalidate=true;
                }
            }
            if(!paymentJson.isNull("codPais"))
                payments.put(paymentJson);

            if(invalidate){
                payments=new JSONArray();
                break;
            }
        }
        return payments;
    }

    @Override
    public JSONArray parseCloseMarketFile(Sheet datatypeSheet) throws Exception {
        JSONArray payments = new JSONArray();
        Iterator<Row> iterator = datatypeSheet.iterator();

        boolean invalidate=false;

        String countryCode;
        String ruc;
        String companyName;
        String documentyType;
        String documentNumber;
        String creditCode;
        String personCode;
        String paymentDate;
        String paymentAmount;
        String operationNumber;

        while (iterator.hasNext()) {
            JSONObject paymentJson = new JSONObject();
            Row currentRow = iterator.next();
            if(currentRow.getRowNum() > 0){
                if(currentRow.getPhysicalNumberOfCells() == 8) {
                    currentRow.getCell(0).setCellType(CellType.STRING);
                    countryCode = currentRow.getCell(0).getStringCellValue();
                    documentyType = currentRow.getCell(3).getStringCellValue().toUpperCase();
                    paymentJson.put("codPais", countryCode);
                    Integer documentTypeId = 0;
                    switch (Integer.valueOf(countryCode)) {
                        case CountryParam.COUNTRY_PERU:
                            switch (documentyType) {
                                case "DNI":
                                    documentTypeId = IdentityDocumentType.DNI;
                                    break;
                                case "CE":
                                    documentTypeId = IdentityDocumentType.CE;
                                    break;
                            }
                            break;
                        case CountryParam.COUNTRY_ARGENTINA:
                            switch (documentyType) {
                                case "CDI":
                                    documentTypeId = IdentityDocumentType.CDI;
                                    break;
                                case "CUIL":
                                    documentTypeId = IdentityDocumentType.CUIL;
                                    break;
                                case "CUIT":
                                    documentTypeId = IdentityDocumentType.CUIT;
                                    break;
                                case "DNI":
                                    documentTypeId = IdentityDocumentType.ARG_DNI;
                                    break;
                            }
                            break;
                    }

                    currentRow.getCell(1).setCellType(CellType.STRING);
                    ruc = currentRow.getCell(1).getStringCellValue();
                    companyName = currentRow.getCell(2).getStringCellValue();

                    currentRow.getCell(4).setCellType(CellType.STRING);
                    documentNumber = currentRow.getCell(4).getStringCellValue();
                    currentRow.getCell(5).setCellType(CellType.STRING);
                    creditCode = currentRow.getCell(5).getStringCellValue();
                    currentRow.getCell(6).setCellType(CellType.STRING);
                    paymentDate = currentRow.getCell(6).getStringCellValue();
                    currentRow.getCell(7).setCellType(CellType.STRING);
                    paymentAmount = currentRow.getCell(7).getStringCellValue();

                    if (documentTypeId == 0 || !documentNumber.matches("[0-9]+") ||
                            !paymentAmount.replace(".", "").replace(",", "").matches("[0-9]+") || !ruc.matches("[0-9]+") ||
                            !paymentDate.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})") ||
                            creditCode == null || creditCode.trim().length() == 0
                            ) {
                        invalidate = true;
                    }

                    paymentJson.put("ruc", ruc);
                    paymentJson.put("nombreEmpresa", companyName);
                    paymentJson.put("tipoDocumento", documentTypeId.toString());
                    paymentJson.put("numDocumento", documentNumber);
                    paymentJson.put("codCredito", creditCode);
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = format.parse(paymentDate);
                    SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
                    paymentJson.put("fechaPago", newFormat.format(date));
                    paymentJson.put("montoPago", paymentAmount);
                }
            }else{
                //validate the headers}
                if( ("País*").equals(currentRow.getCell(0).getStringCellValue()) &&
                        ("RUC de la Empresa*").equals(currentRow.getCell(1).getStringCellValue()) &&
                        ("Nombre de la Empresa*").equals(currentRow.getCell(2).getStringCellValue()) &&
                        ("Tipo de Documento*").equals(currentRow.getCell(3).getStringCellValue()) &&
                        ("Número de Documento*").equals(currentRow.getCell(4).getStringCellValue()) &&
                        ("Código de Crédito*").equals(currentRow.getCell(5).getStringCellValue()) &&
                        ("Fecha de Pago*").equals(currentRow.getCell(6).getStringCellValue()) &&
                        ("Monto de Pago*").equals(currentRow.getCell(7).getStringCellValue())){
                }else{
                    invalidate=true;
                }
            }
            if(!paymentJson.isNull("codPais"))
                payments.put(paymentJson);

            if(invalidate){
                payments=new JSONArray();
                break;
            }
        }
        return payments;
    }

    @Override
    public JSONArray parseBankPaymentFile(String data, int bankId) throws Exception {
        if (data == null || data.trim().isEmpty())
            return null;

        JSONArray payments = new JSONArray();
        String lines[] = data.split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            JSONObject paymentJson = new JSONObject();
            switch (bankId) {
                case Bank.BCP:
                    paymentJson.put("tipo", lines[i].substring(0, 2));
                    if (lines[i].substring(0, 2).equals("DD")) {
                        paymentJson.put("codSucursal", Integer.parseInt(lines[i].substring(2, 5)));
                        paymentJson.put("codMoneda", Integer.parseInt(lines[i].substring(5, 6)));
                        paymentJson.put("numCuenta", lines[i].substring(6, 13).trim());
                        paymentJson.put("codIdDepositante", lines[i].substring(13, 27).trim());
                        paymentJson.put("nomDepositante", lines[i].substring(27, 67).trim());
                        paymentJson.put("infoRetorno", lines[i].substring(67, 97).trim());
                        paymentJson.put("fecEmisionCupon", lines[i].substring(97, 105).trim());
                        paymentJson.put("fecVencCupon", lines[i].substring(105, 113).trim());
                        String montoCuponOrigin = lines[i].substring(113, 128);
                        paymentJson.put("montoCupon", Double.parseDouble(new StringBuilder(montoCuponOrigin).insert(montoCuponOrigin.length() - 2, ".").toString()));
                        String montoMoraOrigin = lines[i].substring(128, 143);
                        paymentJson.put("montoMora", Double.parseDouble(new StringBuilder(montoMoraOrigin).insert(montoMoraOrigin.length() - 2, ".").toString()));
                        String montoMinimoOrigin = lines[i].substring(143, 152);
                        paymentJson.put("montoMinimo", Double.parseDouble(new StringBuilder(montoMinimoOrigin).insert(montoMinimoOrigin.length() - 2, ".").toString()));
                        paymentJson.put("tipoRegistroAct", lines[i].substring(152, 153).trim());
                        paymentJson.put("nroDocPago", lines[i].substring(153, 173).trim());
                        paymentJson.put("nroDocIdentidad", lines[i].substring(173, 189).trim());
                        paymentJson.put("filler", lines[i].substring(189).trim());
                    }else{
                        continue;
                    }
                    payments.put(paymentJson);
                    break;
                default:
                    throw new Exception("There is no parser for the bankId " + bankId);
            }
        }
        return payments;
    }

    @Override
    public void processBankPayments(JSONArray payments, int bankId) throws Exception {
        switch (bankId) {
            case Bank.BCP:
                creditPaymentDao.registerCreditPayments(payments.toString(), Bank.BCP);
                break;
            default:
                throw new Exception("Cant proccess payments for the bankId " + bankId);
        }
    }

    @Override
    public void registerMultiPayment(MultiCreditPayment multiCreditPayment, int creditId, double paymentAmount) throws Exception {
        JSONObject json = new JSONObject();
        json.put("tipo", "DD");
        json.put("codSucursal", multiCreditPayment.getSubsidiary());
        json.put("codMoneda", multiCreditPayment.getCurrency());
        json.put("numCuenta", multiCreditPayment.getAccountNumber());
        json.put("codIdDepositante", multiCreditPayment.getDepositorId());
        json.put("nomDepositante", multiCreditPayment.getDepositorName());
        json.put("infoRetorno", multiCreditPayment.getReturningInfo());
        json.put("fecEmisionCupon", multiCreditPayment.getPaymentDate());
        json.put("fecVencCupon", multiCreditPayment.getExpirationDate());
        json.put("montoCupon", paymentAmount);
        json.put("montoMora", multiCreditPayment.getArrearsAmount());
        json.put("montoMinimo", multiCreditPayment.getMinAmount());
        json.put("tipoRegistroAct", multiCreditPayment.getRegisterType());
        json.put("nroDocPago", multiCreditPayment.getPaymentDocNumber());
        json.put("nroDocIdentidad", multiCreditPayment.getIdentificationDocNumber());

        // Registre the payment
        Integer paymentId = creditPaymentDao.registerMultiCreditPayment(multiCreditPayment.getId(), creditId, json.toString());

        // Confirm the payment
        JSONArray arrayIds = new JSONArray();
        JSONObject jsonId = new JSONObject();
        jsonId.put("id", paymentId);
        arrayIds.put(jsonId);
        employerDao.registerCreditPaymentConfirmation(arrayIds, null, true);
    }

    @Override
    public void processMultipaymentAutomatically(Locale locale) throws Exception {

        List<SalaryAdvancePayment> payments = creditPaymentDao.getPendingSalaryAdvancePayments(locale);
        if (payments == null)
            return;

        for (SalaryAdvancePayment payment : payments) {
            if (payment.getCreditIds() == null)
                continue;

            // Validate that the credit pending amounts is lower than the payments amount
            payment.setCredits(creditDao.getCreditsByIds(payment.getCreditIds(), locale, Credit.class));
            double totalPaymentAmount = payment.getTotalPaymentAmount() + payment.getTotalSurplusAmount();
            MutableDouble totalPendingAmount = new MutableDouble();
            payment.getCredits().forEach(c -> totalPendingAmount.add(c.getPendingInstallmentAmount()));
            if (totalPaymentAmount < totalPendingAmount.doubleValue()) {
                continue;
            }

            // Register the payments
            Map<Integer, Double> creditAmounts = new HashMap<>();
            payment.getCredits().forEach(c -> {
                creditAmounts.put(c.getId(), c.getPendingInstallmentAmount());
            });
            subProcessMultipaymentsByMap(creditAmounts, payment);
        }
    }

    private void subProcessMultipaymentsByMap(Map<Integer, Double> creditAmounts, SalaryAdvancePayment employerPayment) throws Exception {
        if (creditAmounts == null)
            return;

        for (Map.Entry<Integer, Double> entry : creditAmounts.entrySet()) {
            double paymentAmount = entry.getValue();
            int creditId = entry.getKey();
            if (paymentAmount == 0)
                continue;

            while (paymentAmount > 0) {
                MultiCreditPayment multiPayment;
                if (employerPayment.getCashSurplus() != null) {
                    multiPayment = employerPayment.getCashSurplus().stream().filter(s -> s.getCashSurplus() > 0).findFirst().orElse(null);
                    if (multiPayment != null) {
                        double amountToPay = multiPayment.getCashSurplus() >= paymentAmount ? paymentAmount : multiPayment.getCashSurplus();
                        registerMultiPayment(multiPayment, creditId, amountToPay);
                        paymentAmount = paymentAmount - amountToPay;
                        multiPayment.setCashSurplus(multiPayment.getCashSurplus() - amountToPay);
                        continue;
                    }
                }
                if (employerPayment.getPayments() != null) {
                    multiPayment = employerPayment.getPayments().stream().filter(s -> s.getAmount() > 0).findFirst().orElse(null);
                    if (multiPayment != null) {
                        double amountToPay = multiPayment.getAmount() >= paymentAmount ? paymentAmount : multiPayment.getAmount();
                        registerMultiPayment(multiPayment, creditId, amountToPay);
                        paymentAmount = paymentAmount - amountToPay;
                        multiPayment.setAmount(multiPayment.getAmount() - amountToPay);
                        continue;
                    }
                }
                // If it comes to here means that there is no more payments availables, so break it
                break;
            }
        }
    }

    @Override
    public void processMarketPayment(JSONArray payments, int sysUserId) throws Exception{
        creditPaymentDao.registerMarketPayment(payments, sysUserId);
    }


}
