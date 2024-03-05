package com.affirm.common.service;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.service.impl.FunnelStepService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Service
public class MassiveDisbursementService {



    private static final String SPLITER = ",";
    private static final String PATH_FOLDER = "/home/amendoza/Vídeos/";
    private static final String FILENAME = "ARCHIVO_DESEMBOLSO.csv";
    private static final String FIXED_FILENAME = "ARCHIVO_DESEMBOLSO_FIXED.csv";
    private final List<String> validDateFormats = Arrays.asList("dd/MM/yyyy", "dd-MM-yyyy");

    @Autowired
    private PersonDAO personDAO;
    @Autowired
    private CreditDAO creditDAO;
    @Autowired
    private LoanApplicationDAO loanApplicationDAO;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CreditService creditService;
    @Autowired
    private LoanNotifierService loanNotifierService;
    @Autowired
    private OfflineConversionService offlineConversionService;
    @Autowired
    private FunnelStepService funnelStepService;

    public void generateFileWithoutErrorLines(int entityId, boolean skipFirstRow) throws Exception {
        Integer maxColumns = null;
        switch (entityId){
            case Entity.AZTECA:
                maxColumns = 9;
                break;
        }
        if(maxColumns == null) throw new Exception("Variable: maxColumns cant be null");
        List<String> correctLines = new ArrayList<>();
        Stream<String> lines = Files.lines(Paths.get(PATH_FOLDER + FILENAME));
        Integer finalMaxColumns = maxColumns;
        AtomicInteger count = new AtomicInteger();
        lines.forEach(l -> {
            if(skipFirstRow && count.get() == 0){
                System.out.println("Skip first row enabled");
            }else{
                if (l.split(SPLITER).length > finalMaxColumns) {
                    System.out.println(l);
                } else {
                    correctLines.add(l);
                }
            }
            count.getAndIncrement();
        });

        Files.write(Paths.get(PATH_FOLDER + FIXED_FILENAME),
                correctLines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }

    public void executeMassiveDisbursement(int entityId) throws Exception {
        Stream<String> lines = Files.lines(Paths.get(PATH_FOLDER + FIXED_FILENAME));
        List<JSONObject> data = new ArrayList<>();
        Integer loggedUserId = !Configuration.hostEnvIsProduction() ? 923 : 923;
        int errorsCount = 0;
        String additionalMessage = "";
        List<String> successProcess = new ArrayList<>();
        AtomicInteger count = new AtomicInteger();
        lines.forEach(l -> {
            count.getAndIncrement();
            List<String> rowValues = Arrays.asList(l.split(SPLITER).clone());
            try {
                data.add(validateData(rowValues,entityId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        if(count.get() != data.size()) throw new Exception("ERRORS FOUNDED");
        for (int i = 0; i < data.size(); i++) {
            Integer personId = personDAO.getPersonIdByDocument(JsonUtil.getIntFromJson(data.get(i),"documentType",null),JsonUtil.getStringFromJson(data.get(i),"documentNumber",null));
            if(personId != null){
                Integer[] creditIds = creditDAO.getActiveCreditIdsByPerson(Configuration.getDefaultLocale(), personId, entityId);
                if(creditIds == null || creditIds.length == 0) {
                    errorsCount++;
                    additionalMessage += String.format("Fila %s - Documento: %s Error: La persona no posee creditos activos con la entidad. \n", i, JsonUtil.getStringFromJson(data.get(i),"documentNumber",null));
                }
                else{
                    for (Integer creditId : creditIds) {
                        Credit credit = creditDAO.getCreditByID(creditId, Configuration.getDefaultLocale(), false, Credit.class);
                        if(credit.getStatus().getId() == CreditStatus.ORIGINATED && credit.getProduct().getProductCategoryId().equals(ProductCategory.CONSUMO)){

                            Double amount = JsonUtil.getDoubleFromJson(data.get(i), "amount", null);
                            Integer installments = JsonUtil.getIntFromJson(data.get(i), "installments", null);
                            Date disbursementDate = (Date) data.get(i).get("disbursementDate");
                            Double tea = JsonUtil.getDoubleFromJson(data.get(i), "tea", null);

                            if(amount == null) amount = credit.getAmount();
                            if(installments == null) installments = credit.getInstallments();
                            if(tea == null) tea = credit.getEffectiveAnnualRate();

                            creditService.updateCreditConditionsAmountInstallmentsAndTea(creditId, amount, installments, tea, loggedUserId);
                            creditDAO.updateDisbursementDate(creditId, disbursementDate);
                            creditDAO.updateDisbursmentInInEntity(creditId, loggedUserId);
                            creditDAO.updateCreditStatusExtranet(creditId, CreditStatus.ORIGINATED_DISBURSED, loggedUserId);
                            loanNotifierService.notifyDisbursement(credit.getLoanApplicationId(), Configuration.getDefaultLocale());
                            offlineConversionService.sendOfflineConversion(credit);
                            LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(credit.getLoanApplicationId(),Configuration.getDefaultLocale());
                            funnelStepService.registerStep(loanApplication);
                            successProcess.add(credit.getCode()+"-"+JsonUtil.getStringFromJson(data.get(i),"documentNumber",null));
                        }
                        else{
                            System.out.println(String.format("Skip credit with id %s",credit.getId()));
                        }
                    }
                }
            }
            else {
                errorsCount++;
                additionalMessage += String.format("Fila %s - Documento: %s Error: No se ha encontrado información de la persona en nuestros registros. \n", i, JsonUtil.getStringFromJson(data.get(i),"documentNumber",null));
            }
        }

        System.out.println("<-----------------------FAILED-------------------------->");
        System.out.println(additionalMessage);
        System.out.println("<-----------------------END FAILED-------------------------->");
        System.out.println("<-----------------------SUCCESS-------------------------->");
        System.out.println(new Gson().toJson(successProcess));
        System.out.println("<-----------------------END SUCCESS-------------------------->");
    }

    private JSONObject validateData(List<String> rowData, int entityId) throws Exception {
        List<String> headers = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        String decimalSeparator = null;
        String thousandSeparator = null;
        switch (entityId){
            case Entity.AZTECA:
                headers = Arrays.asList("Último","Tipo","Documento","Apellidos","Nombres","Fecha Surtimiento","Monto","Plazo","Campaña");
                decimalSeparator = ",";
                thousandSeparator = ".";
                break;
        }
        if(headers.size() != rowData.size()) throw new Exception(String.format("ERROR ROW %s",new Gson().toJson(rowData)));
        switch (entityId){
            case Entity.AZTECA:

                for (int i = 0; i < headers.size(); i++) {
                    switch (i) {
                        case 1:
                            if(rowData.get(i) != null && !rowData.get(i).isEmpty()){
                                if (rowData.get(i).equalsIgnoreCase("DNI")) {
                                    jsonObject.put("documentType",IdentityDocumentType.DNI);
                                }
                                else if (rowData.get(i).equalsIgnoreCase("CE")) {
                                    jsonObject.put("documentType",IdentityDocumentType.CE);
                                }
                                else errors.add("DOCUMENT TYPE IS INVALID");
                            }
                            else errors.add("DOCUMENT TYPE IS REQUIRED");
                            break;
                        case 2:
                            if(rowData.get(i) != null && !rowData.get(i).isEmpty()){
                                Integer documentType = JsonUtil.getIntFromJson(jsonObject,"documentType",null);
                                if(documentType != null){
                                    if (documentType.equals(IdentityDocumentType.DNI) && !rowData.get(i).matches("^[0-9]{5,8}$")) {
                                        errors.add("DOCUMENT NUMBER IS INVALID");
                                    }
                                    else if (documentType.equals(IdentityDocumentType.CE) && !rowData.get(i).matches("^[0-9]{5,9}$")) {
                                        errors.add("DOCUMENT NUMBER IS INVALID");
                                    }
                                    else jsonObject.put("documentNumber",rowData.get(i));
                                }
                                else errors.add("DOCUMENT TYPE CANT BE NULL");
                            }
                            else errors.add("DOCUMENT NUMBER IS REQUIRED");
                            break;
                        case 5:
                            if(rowData.get(i) != null && !rowData.get(i).isEmpty()){
                                try{
                                    if(rowData.get(i).contains("/")){
                                        jsonObject.put("disbursementDate",new SimpleDateFormat(this.validDateFormats.get(0)).parse(rowData.get(i)));
                                    }
                                    else if(rowData.get(i).contains("-")){
                                        jsonObject.put("disbursementDate",new SimpleDateFormat(this.validDateFormats.get(1)).parse(rowData.get(i)));
                                    }
                                    else errors.add("INVALID DISBURSEMENT DATE FORMAT");
                                }
                                catch (ParseException e){
                                    errors.add("DISBURSEMENT DATE IS INVALID");
                                }
                            }
                            else errors.add("DISBURSEMENT DATE IS REQUIRED");
                            break;
                        case 6:
                            if(rowData.get(i) != null && !rowData.get(i).isEmpty()){
                                String amount = rowData.get(i).trim();
                                if(thousandSeparator.equals(".")) amount = amount.replaceAll("[.]","");
                                if (amount.matches("^[+-]?([0-9]{1,3}(,[0-9]{3})*(\\.[0-9]+)?|\\d*\\.\\d+|\\d+)$")) {
                                    jsonObject.put("amount",Double.valueOf(amount));
                                }
                                else errors.add("INVALID AMOUNT FORMAT");
                            }
                            break;
                        case 7:
                            if(rowData.get(i) != null && !rowData.get(i).isEmpty()){
                                if (rowData.get(i).trim().matches("^[0-9]+$")) {
                                    jsonObject.put("installments",Integer.valueOf(rowData.get(i).trim()));
                                }
                                else errors.add("INVALID INSTALLMENTS FORMAT");
                            }
                            break;
                    }
                }

                break;
        }
        if(errors.size() > 0) throw new Exception(String.format("ERROR ROW %s. Errors: %s",new Gson().toJson(rowData),new Gson().toJson(errors)));
        return jsonObject;
    }

}
