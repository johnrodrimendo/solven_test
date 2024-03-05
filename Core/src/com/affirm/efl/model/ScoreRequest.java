package com.affirm.efl.model;

import com.affirm.common.model.transactional.LoanApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev5 on 06/07/17.
 */
public class ScoreRequest {

    private String authToken;
    private String reqToken;
    private List<Identifications> subjects;

    public ScoreRequest(LoanApplication loanApplication, EFLTokens tokens){
        this.authToken = tokens.getAuthToken();
        this.reqToken = tokens.getReqToken();
        Identification identification = new Identification("externalKey", String.valueOf(loanApplication.getId()));
        List<Identification> identificationList = new ArrayList<>();
        identificationList.add(identification);
        Identifications identifications = new Identifications(identificationList);
        subjects = new ArrayList<>();
        subjects.add(identifications);
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getReqToken() {
        return reqToken;
    }

    public void setReqToken(String reqToken) {
        this.reqToken = reqToken;
    }

}
