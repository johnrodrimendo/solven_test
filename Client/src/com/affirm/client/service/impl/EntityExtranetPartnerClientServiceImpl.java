package com.affirm.client.service.impl;

import com.affirm.client.dao.ExtranetPartnerClientDAO;
import com.affirm.client.model.ExtranetPartnerClient;
import com.affirm.client.service.EntityExtranetPartnerClientService;
import com.affirm.common.model.catalog.IdentityDocumentType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntityExtranetPartnerClientServiceImpl implements EntityExtranetPartnerClientService {

    private ExtranetPartnerClientDAO extranetPartnerClientDAO;

    @Autowired
    public EntityExtranetPartnerClientServiceImpl(
            ExtranetPartnerClientDAO extranetPartnerClientDAO) {
        this.extranetPartnerClientDAO = extranetPartnerClientDAO;
    }

    @Override
    public List<List<ExtranetPartnerClient>> getPartnerClients(Integer entityId) throws Exception {
        List<ExtranetPartnerClient> allExtranetPartnerClients = extranetPartnerClientDAO.getPartnerClients(entityId);

        List<List<ExtranetPartnerClient>> lists = new ArrayList<>();

        for (ExtranetPartnerClient.ListType listType: ExtranetPartnerClient.ListType.values()) {
            lists.add(allExtranetPartnerClients.stream().filter(extranetPartnerClient -> listType.getCode().equals(extranetPartnerClient.getListType())).collect(Collectors.toList()));
        }

        return lists;
    }

    @Override
    public List<ExtranetPartnerClient> getPartnerClientsByListType(Integer entityId, String listType) throws Exception {
        return extranetPartnerClientDAO.getPartnerClients(entityId).stream().filter(extranetPartnerClient -> listType.equals(extranetPartnerClient.getListType())).collect(Collectors.toList());
    }

    @Override
    public JSONArray parsePartnerClientFile(Workbook workbook) {

        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> iterator = sheet.iterator();
        JSONArray results = new JSONArray();
        DataFormatter formatter = new DataFormatter();

//        CABECERA DE PLANTILLA
        String fileDocTypeHeader = "Tipo de Documento*";
        String fileDocNumberHeader = "Número de Documento*";
        String fileAssociatedCodeHeader = "Código de Asociado";

//        JSON RESPUESTA AJAX
        String jsonDocTypeIdKey = "document_id";
        String jsonDocTypeNameKey = "document_name";
        String jsonDocNumberKey = "document_number";
        String jsonAssociatedIdKey = "associated_id";

        boolean invalidate = false;

        while (iterator.hasNext()) {

            JSONObject fileJson = new JSONObject();
            Row currentRow = iterator.next();

            if(currentRow.getRowNum() > 0) {

                if(currentRow.getPhysicalNumberOfCells() == 2) {

                    String cellDocumentType = currentRow.getCell(0).getStringCellValue() != null ? currentRow.getCell(0).getStringCellValue().toUpperCase() : null;
                    String cellDocumentNumber = formatter.formatCellValue(currentRow.getCell(1));
                    String cellAssociatedId = formatter.formatCellValue(currentRow.getCell(2));

                    int tempDocumentType = 0;
//                    TODO: DOCUMENT BY USER COUNTRY??
                    switch (cellDocumentType) {
                        case "DNI":
                            tempDocumentType = IdentityDocumentType.DNI;
                            break;
                        case "CE":
                            tempDocumentType = IdentityDocumentType.CE;
                            break;
                        case "RUC":
                            tempDocumentType = IdentityDocumentType.RUC;
                            break;
                    }

                    if (tempDocumentType == 0 || !cellDocumentNumber.matches("[0-9]+")) {
                        invalidate = true;
                    }

                    fileJson.put(jsonDocTypeIdKey, tempDocumentType);
                    fileJson.put(jsonDocTypeNameKey, cellDocumentType);
                    fileJson.put(jsonDocNumberKey, cellDocumentNumber);
                    fileJson.put(jsonAssociatedIdKey, cellAssociatedId);

                }

            } else {
                //validate the headers
                if(
                        !(fileDocTypeHeader).equals(currentRow.getCell(0).getStringCellValue()) ||
                                !(fileDocNumberHeader).equals(currentRow.getCell(1).getStringCellValue()) ||
                                !(fileAssociatedCodeHeader).equals(currentRow.getCell(2).getStringCellValue())
                ){
                    invalidate = true;
                }
            }

            if(!fileJson.isNull(jsonDocTypeNameKey) && !fileJson.isNull(jsonDocNumberKey)) {
                results.put(fileJson);
            }

            if (invalidate) {
                results = new JSONArray();
                break;
            }

        }

        return results;
    }

    @Override
    public void saveRecords(Integer entityId, JSONArray array, String listType) throws Exception {
        if(!array.toList().isEmpty()) {
            extranetPartnerClientDAO.registerPartnerClient(entityId, array, listType);
        }
    }
}
