package com.affirm.client.dao;

import com.affirm.client.model.ExtranetPartnerClient;
import org.json.JSONArray;

import java.util.List;

public interface ExtranetPartnerClientDAO {

    List<ExtranetPartnerClient> getPartnerClients(Integer entityId) throws Exception;

    void registerPartnerClient(Integer entityId, JSONArray records, String listType) throws Exception;

//    void alternatePartnerClient(ExtranetPartnerClient extranetPartnerClient) throws Exception;
}
