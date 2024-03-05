package com.affirm.common.dao;

import com.affirm.negativebase.model.NegativeBaseProcessed;
import com.affirm.preapprovedbase.model.PreApprovedBaseProcessed;
import java.util.Date;
import java.util.List;

public interface PreApprovedDAO {

    Integer registerUploadPreApprovedProcess(Integer entityId, String url, Integer entityUserId);

    List<PreApprovedBaseProcessed> getHistoricListPreAppovedBase(Integer entityId, Integer limit, Integer offset);

    PreApprovedBaseProcessed getPreApprovedBaseProcessed(Integer processId);

    void updateProcessDate(int processId, Date processDate);

    void updateProcessStatus(int processId, Character status);

    void updateProcessMessage(int processId, PreApprovedBaseProcessed.ErrorDetail error);
}
