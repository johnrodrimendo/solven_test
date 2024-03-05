package com.affirm.backoffice.service;

import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.LoanApplication;
import org.json.JSONArray;

import java.util.Locale;

/**
 * Created by jarmando on 27/02/17.
 */
public interface EmailBoService {
    boolean sendRejectionMail(LoanApplication la, Locale locale) throws Exception;
    boolean sendRejectionMail(Credit la, Locale locale) throws Exception;
    void sendLinkReturningAssistedProcess(LoanApplication loanApplication, Locale locale) throws Exception;
    void sendPasswordEmployersMail(String employerName, String employerRuc, JSONArray users);
}
