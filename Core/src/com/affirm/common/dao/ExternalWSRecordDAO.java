package com.affirm.common.dao;

import com.affirm.common.model.ExternalWSRecord;

import java.util.Date;
import java.util.List;

public interface ExternalWSRecordDAO {

    void insertExternalWSRecord(Integer loanApplicationId, Date startDate, String url, String request, String response, Integer responseHttpCode);

    void insertExternalWSRecord(ExternalWSRecord externalWSRecord);

    List<ExternalWSRecord> getLoanApplicationExternalWSRecords(int loanApplicationId);

    ExternalWSRecord getExternalWSRecord(int externalWSRecordId);

    Integer insertWebhookRequest(String url, String request) throws Exception;
}
