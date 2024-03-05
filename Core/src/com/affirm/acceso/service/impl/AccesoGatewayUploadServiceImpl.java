package com.affirm.acceso.service.impl;

import com.affirm.acceso.service.AccesoGatewayUploadService;
import com.affirm.common.model.catalog.InteractionType;
import com.affirm.common.service.WebscrapperService;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AccesoGatewayUploadServiceImpl implements AccesoGatewayUploadService {

    private static final String HEADER_DOCUMENT_NUMBER = "DNI";
    //    private static final String HEADER_FULLNAME = "Nombre";
    private static final String HEADER_INSTALLMENT_NUMBER = "Cuota";
    private static final String HEADER_AMOUNT = "Monto a pagar";
    private static final String HEADER_EXPIRE_DATE = "Fecha vencimiento";
    private static final String HEADER_ENTITY_CREDIT = "Credito";

    private WebscrapperService webscrapperService;

    public AccesoGatewayUploadServiceImpl(WebscrapperService webscrapperService) {
        this.webscrapperService = webscrapperService;
    }

    @Override
    public void lookupForNotCollectedSoonToExpireInstallments(Sheet report, int[] kindsOfInteraction, Integer userId) throws Exception {
        Iterator<Row> iterator = report.iterator();
        JSONArray jsonArray = new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);

        int documentNumberHeaderNumber = 0;
//        int fullnameHeaderNumber = 0;
        int installmentNumberHeaderNumber = 0;
        int amountHeaderNumber = 0;
        int expireDateHeaderNumber = 0;
        int entityCreditHeaderNumber = 0;

        int totalVencimiento = 0;
        int totalMora = 0;

        while (iterator.hasNext()) {
            Row currentRow = iterator.next();

//            HEADER
            if (currentRow.getRowNum() == 0) {
                for (int i = 0; i < currentRow.getPhysicalNumberOfCells(); i++) {
//                    if (currentRow.getCell(i).getStringCellValue().equalsIgnoreCase(HEADER_FULLNAME)) {
//                        fullnameHeaderNumber = i;
//                    }
                    if (currentRow.getCell(i).getStringCellValue().equalsIgnoreCase(HEADER_EXPIRE_DATE)) {
                        expireDateHeaderNumber = i;
                    }
                    if (currentRow.getCell(i).getStringCellValue().equalsIgnoreCase(HEADER_DOCUMENT_NUMBER)) {
                        documentNumberHeaderNumber = i;
                    }
                    if (currentRow.getCell(i).getStringCellValue().equalsIgnoreCase(HEADER_INSTALLMENT_NUMBER)) {
                        installmentNumberHeaderNumber = i;
                    }
                    if (currentRow.getCell(i).getStringCellValue().equalsIgnoreCase(HEADER_AMOUNT)) {
                        amountHeaderNumber = i;
                    }
                    if (currentRow.getCell(i).getStringCellValue().equalsIgnoreCase(HEADER_ENTITY_CREDIT)) {
                        entityCreditHeaderNumber = i;
                    }
                }

//                SI NO RECONOCIO UNA DE LAS CABECERAS (OBVIANDO DNI QUE ES EL PRIMER) LANZAR EXCEPCION
                if (/*fullnameHeaderNumber == 0 || */installmentNumberHeaderNumber == 0 || amountHeaderNumber == 0 || expireDateHeaderNumber == 0 || entityCreditHeaderNumber == 0) {
                    throw new IOException("No se reconocio una o más cabeceras");
                }

                continue;
            }

            try {
                if (
                        (currentRow.getCell(expireDateHeaderNumber) == null || currentRow.getCell(expireDateHeaderNumber).getCellTypeEnum() == CellType.BLANK) &&
                                (currentRow.getCell(documentNumberHeaderNumber) == null || currentRow.getCell(documentNumberHeaderNumber).getCellTypeEnum() == CellType.BLANK) &&
                                (currentRow.getCell(installmentNumberHeaderNumber) == null || currentRow.getCell(installmentNumberHeaderNumber).getCellTypeEnum() == CellType.BLANK) &&
                                (currentRow.getCell(amountHeaderNumber) == null || currentRow.getCell(amountHeaderNumber).getCellTypeEnum() == CellType.BLANK) &&
                                (currentRow.getCell(entityCreditHeaderNumber) == null || currentRow.getCell(entityCreditHeaderNumber).getCellTypeEnum() == CellType.BLANK)
                ) {
//                    NO MORE ROWS
                    break;
                }

//                currentRow.getCell(fullnameHeaderNumber).setCellType(CellType.STRING);
//                String fullnameCellValue = currentRow.getCell(fullnameHeaderNumber).getStringCellValue();

                Date expireDateCellValue;
                if (currentRow.getCell(expireDateHeaderNumber).getCellTypeEnum() == CellType.NUMERIC) {
                    expireDateCellValue = currentRow.getCell(expireDateHeaderNumber).getDateCellValue();
                } else {
                    currentRow.getCell(expireDateHeaderNumber).setCellType(CellType.STRING);
                    expireDateCellValue = sdf.parse(currentRow.getCell(expireDateHeaderNumber).getStringCellValue());
                }

                String documentNumberCellValue;
                if (currentRow.getCell(documentNumberHeaderNumber).getCellTypeEnum() == CellType.STRING) {
                    documentNumberCellValue = String.valueOf(Double.valueOf(currentRow.getCell(documentNumberHeaderNumber).getStringCellValue()).intValue());
                } else if (currentRow.getCell(documentNumberHeaderNumber).getCellTypeEnum() == CellType.NUMERIC) {
                    documentNumberCellValue = String.valueOf(Double.valueOf(currentRow.getCell(documentNumberHeaderNumber).getNumericCellValue()).intValue());
                } else {
                    documentNumberCellValue = currentRow.getCell(documentNumberHeaderNumber).getStringCellValue();
                }

                currentRow.getCell(installmentNumberHeaderNumber).setCellType(CellType.STRING);
                int installmentNumberCellValue = (int) Double.parseDouble(currentRow.getCell(installmentNumberHeaderNumber).getStringCellValue());

                currentRow.getCell(amountHeaderNumber).setCellType(CellType.STRING);
                Double amountCellValue = Double.parseDouble(currentRow.getCell(amountHeaderNumber).getStringCellValue());

                currentRow.getCell(entityCreditHeaderNumber).setCellType(CellType.STRING);
                String entityCreditCellValue = currentRow.getCell(entityCreditHeaderNumber).getStringCellValue();

                JSONObject jsonRow = new JSONObject();
                jsonRow.put("document_number", String.format("%08d", Integer.parseInt(documentNumberCellValue)));
//                jsonRow.put("name", fullnameCellValue);
                jsonRow.put("expire_date", sdf.format(expireDateCellValue));
                jsonRow.put("installment", installmentNumberCellValue);
                jsonRow.put("amount", amountCellValue);
                jsonRow.put("credit_code", entityCreditCellValue);

//                CALCULATE HOW MANY ROWS ARE FROM MORA AND VENCIMIENTO
                long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), expireDateCellValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                if (daysBetween >= 0) {
                    totalVencimiento++;
                } else {
                    totalMora++;
                }

                jsonArray.put(jsonRow);
            } catch (Exception e) {
                throw new IOException(e instanceof ParseException ? "El formato de la fecha no es el correcto" : null);
            }
        }

        boolean sendEmail = false;
        boolean sendWhatsapp = false;

        for (int value : kindsOfInteraction) {
            if (!sendEmail && value == InteractionType.MAIL) {
                sendEmail = true;
            }

            if (!sendWhatsapp && value == InteractionType.CHAT) {
                sendWhatsapp = true;
            }
        }

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("sendWhatsapp", sendWhatsapp);
        params.put("sendEmail", sendEmail);
        params.put("totalVencimiento", totalVencimiento);
        params.put("totalMora", totalMora);

        webscrapperService.callSendAccesoExpirationInteractions(jsonArray, params, userId);
    }
}
