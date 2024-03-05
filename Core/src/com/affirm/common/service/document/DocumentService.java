package com.affirm.common.service.document;

import com.affirm.common.model.transactional.Credit;

public interface DocumentService {
    byte[] generateRipleyLoanSpreadSheet(Credit credit) throws Exception;
    byte[] generateLeadReportSpreadSheet(Integer entityId, Integer month, Integer year) throws Exception;
}
