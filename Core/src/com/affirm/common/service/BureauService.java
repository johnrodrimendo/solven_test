package com.affirm.common.service;

import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;

/**
 * Created by john on 21/10/16.
 */
public interface BureauService {
    void runBureau(LoanApplication loanApplication, Person person, int bureauId) throws Exception;

    void runBureau(LoanApplication loanApplication, int docType, String docNumber, int bureauId) throws Exception;

    byte[] renderBureauResultFromHtml(LoanApplication loanApplication, int bureauId) throws Exception;

    void callNosisUpdateExternalData(String docNumber);
}
