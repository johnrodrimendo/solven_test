package com.affirm.client.service;

import com.affirm.client.model.ExtranetPartnerClient;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;

import java.util.List;

public interface EntityExtranetPartnerClientService {

//    TODO: PUEDE MEJORAR
    List<List<ExtranetPartnerClient>> getPartnerClients(Integer entityId) throws Exception;

    List<ExtranetPartnerClient> getPartnerClientsByListType(Integer entityId, String listType) throws Exception;

    JSONArray parsePartnerClientFile(Workbook workbook);

    void saveRecords(Integer entityId, JSONArray json, String listType) throws Exception;

}
