package com.affirm.banbif.service;

import com.affirm.banbif.model.BanbifFileData;
import com.affirm.common.model.transactional.BanbifPreApprovedBase;
import com.affirm.common.model.transactional.LoanApplication;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public interface BanBifService {

    public void readBaseFile() throws Exception;

    List<BanbifFileData> getFileData(String filePath) throws FileNotFoundException;

    InputStream csvToXlsx(InputStream inputStream, String delimiter);

    void updateBaseValuesInLoan(LoanApplication loanApplication, String documentNumber) throws Exception;

    BanbifPreApprovedBase getBanbifPreApprovedBase(String documentType, String documentNumber, LoanApplication loanApplication);

    void callKonectaLead(Integer loanApplicationId) throws Exception;
}
