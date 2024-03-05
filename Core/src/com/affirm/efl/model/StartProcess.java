package com.affirm.efl.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.transactional.*;

import java.util.List;

/**
 * Created by dev5 on 04/07/17.
 */
public class StartProcess {

    private transient int entityId = 10; //EFL

    private String authToken;
    private String reqToken;
    private Data data;

    public StartProcess(String application, TranslatorDAO translatorDAO, String authToken, String reqToken, int loanApplicationId, Person person, User user, PersonContactInformation contactInfo, PersonOcupationalInformation ocupationalInfo, List<EntityConsolidableDebt> products) throws Exception{
        this.setAuthToken(authToken);
        this.setReqToken(reqToken);
        this.setData(new Data(application, entityId, translatorDAO, loanApplicationId, person, user, contactInfo, ocupationalInfo, products));
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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
