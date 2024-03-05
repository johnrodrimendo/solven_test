package com.affirm.backoffice.service;

import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.LoanApplication;

import javax.servlet.ServletOutputStream;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jarmando on 27/02/17.
 */
public interface ReportsBoService {
    byte[] createOriginationReportEntityGrouped(String country, String symbol, String separator) throws Exception;
}
