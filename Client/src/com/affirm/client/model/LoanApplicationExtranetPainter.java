package com.affirm.client.model;

import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanApplicationUserFiles;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.service.CatalogService;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */
public class LoanApplicationExtranetPainter extends LoanApplication {

    private String rejectedMessage;
    private Date rejectedExpirationDate;
    private LoanApplicationUserFiles loanApplicationUserFiles;
    private boolean waitingForApproval;
    private PersonOcupationalInformation principalOcupation;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        super.fillFromDb(json, catalog, locale);
    }

    public String getRejectedMessage() {
        return rejectedMessage;
    }

    public void setRejectedMessage(String rejectedMessage) {
        this.rejectedMessage = rejectedMessage;
    }

    public Date getRejectedExpirationDate() {
        return rejectedExpirationDate;
    }

    public void setRejectedExpirationDate(Date rejectedExpirationDate) {
        this.rejectedExpirationDate = rejectedExpirationDate;
    }

    public LoanApplicationUserFiles getLoanApplicationUserFiles() {
        return loanApplicationUserFiles;
    }

    public void setLoanApplicationUserFiles(LoanApplicationUserFiles loanApplicationUserFiles) {
        this.loanApplicationUserFiles = loanApplicationUserFiles;
    }

    public boolean getWaitingForApproval() {
        return waitingForApproval;
    }

    public void setWaitingForApproval(boolean waitingForApproval) {
        this.waitingForApproval = waitingForApproval;
    }

    public PersonOcupationalInformation getPrincipalOcupation() {
        return principalOcupation;
    }

    public void setPrincipalOcupation(PersonOcupationalInformation principalOcupation) {
        this.principalOcupation = principalOcupation;
    }
}
