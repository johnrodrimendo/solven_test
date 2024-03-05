package com.affirm.common.dao;

import com.affirm.negativebase.model.NegativeBaseProcessed;
import com.affirm.preapprovedbase.model.PreApprovedBaseProcessed;

import java.util.Date;
import java.util.List;

public interface NegativeBaseProcessDAO {

    Integer registerUploadNegativeBaseProcess(Integer entityId, String url, Integer entityUserId, Character type);

    List<NegativeBaseProcessed> getHistoricListNegativeBase(Integer entityId, Integer limit, Integer offset);

    NegativeBaseProcessed getNegativeBaseProcessed(Integer processId);

    void updateProcessDate(int processId, Date processDate);

    void updateProcessStatus(int processId, Character status);

    void updateProcessMessage(int processId, NegativeBaseProcessed.ErrorDetail error);
}
