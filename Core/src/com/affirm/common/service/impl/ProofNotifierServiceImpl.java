package com.affirm.common.service.impl;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.SelfEvaluationDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.SelfEvaluation;
import com.affirm.common.model.transactional.User;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.ProofNotifierService;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.collections.map.HashedMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service("proofNotifierService")
public class ProofNotifierServiceImpl implements ProofNotifierService{
    RestTemplate consumer;

    @Autowired
    LoanApplicationDAO loanApplicationDAO;
    @Autowired
    SelfEvaluationDAO selfEvaluationDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    CatalogService catalogService;

    private static String SELF_EVALUATION_WEBOOK = "https://webhook.proofapi.com/cw/KayR6LlKAmWMR1nDp2VjJPyMgQY2/-KwGEB--8ekexH9PPpYu";
    private static String CREDIT_CONSULTATION_WEBHOOK = "https://webhook.proofapi.com/cw/KayR6LlKAmWMR1nDp2VjJPyMgQY2/-KwGGBnoQnnuy9mABPca";

    ProofNotifierServiceImpl() {
        consumer = new RestTemplate();
    }

    @Override
    public void notifySelfEvaluation(SelfEvaluation selfEvaluation) throws  Exception {
        Person person = personDAO.getPerson(catalogService, Configuration.getDefaultLocale(), selfEvaluation.getPersonId(), false);
        User user = userDAO.getUser(person.getUserId());

        Instant instant = Instant.now();

        HashedMap requestBody = new HashedMap();

        requestBody.put("type", "custom");
        requestBody.put("first_name", person.getFirstName());
        requestBody.put("email", user.getEmail());
        requestBody.put("timestamp", instant.toEpochMilli());

        sendPost(SELF_EVALUATION_WEBOOK,requestBody);
    }

    @Override
    public void notifySelfEvaluation(Integer selfEvaluationId) throws Exception {
        SelfEvaluation selfEvaluation = selfEvaluationDAO.getSelfEvaluation(selfEvaluationId,Configuration.getDefaultLocale());
        notifySelfEvaluation(selfEvaluation);
    }

    @Override
    public void notifyCreditConsultation(Integer loanApplicationId) throws Exception {
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
        notifyCreditConsultation(loanApplication);
    }

    @Override
    public void notifyCreditConsultation(LoanApplication loanApplication) throws Exception {
        Person person = personDAO.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
        User user = userDAO.getUser(loanApplication.getUserId());
        Instant instant = Instant.now();

        HashedMap requestBody = new HashedMap();

        requestBody.put("type", "custom");
        requestBody.put("first_name", person.getFirstName());
        requestBody.put("email", user.getEmail());
        requestBody.put("ip", loanApplication.getIpAddress());
        requestBody.put("timestamp", instant.toEpochMilli());

        sendPost(CREDIT_CONSULTATION_WEBHOOK,requestBody);
    }

    void sendPost(String url, HashedMap requestBody) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        Gson gson = new Gson();

        try {

            HttpPost request = new HttpPost(url);
            StringEntity params = new StringEntity(gson.toJson(requestBody));
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);

            System.out.printf("Test");

        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
