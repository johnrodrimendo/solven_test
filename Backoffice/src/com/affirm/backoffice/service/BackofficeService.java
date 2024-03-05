package com.affirm.backoffice.service;

import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.security.model.SysUser;

import java.io.OutputStream;
import java.util.Locale;

/**
 * Created by jarmando on 27/02/17.
 */
public interface BackofficeService {
    SysUser getLoggedSysuser() throws Exception;

    void setCountryActiveSysuser(Integer countryId, String cssClass);

    String getCountryActiveSysuser();

    String getCurrencySymbol();

    String getSeparator();

    boolean isCountryUnlocked();

    void createImportImputationOpenMarketExcelTemplate(OutputStream outputStream) throws Exception;

    void createImportImputationCloseMarketExcelTemplate(OutputStream outputStream) throws Exception;
}
