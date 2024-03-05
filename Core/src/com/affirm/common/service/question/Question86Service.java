package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.form.Question86Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.User;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.util.Util;
import com.affirm.iovation.ws.transaction.check.CheckTransactionDetails;
import com.affirm.iovation.ws.transaction.check.CheckTransactionDetailsResponse;
import com.affirm.iovation.ws.transaction.check.PortType;
import com.affirm.system.configuration.Configuration;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service("question86Service")
public class Question86Service extends AbstractQuestionService<Question86Form> {

    private static Logger logger = Logger.getLogger(Question86Service.class);
    public static final String staticSandboxSnare = "/static/io/sandbox/latest/first_third.js";
    public static final String staticProductionSnare = "/static/io/latest/first_third.js";
    private static final String dynamicSandbox = "https://ci-first.iovation.com/";
    private static final String dynamicProduction = "https://first.iovation.com/";

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private LoanApplicationService loanApplicationService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);

                // If the preliminaryevauation still doest run, run it!
//                LoanApplicationEvaluationsProcess evaluationsProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplication.getId());
//                if(evaluationsProcess.getReadyForPreEvaluation() == null){
//                    loanApplicationDao.updateEvaluationProcessReadyPreEvaluation(loanApplication.getId(), true);
//                    loanApplicationService.runEvaluationBot(loanApplication.getId(), false);
//                }

                if (loanApplicationDao.mustCallIovation(loanApplication.getId()))
                    addAttributesToExecuteIO(attributes, loanApplication.getUserId(), loanApplication.getId());

                attributes.put("showMessageOffer", true);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question86Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question86Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question86Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                User user = userDao.getUser(loanApplication.getUserId());

                Session session = SecurityUtils.getSubject().getSession();

                String subscriberid = Configuration.hostEnvIsProduction() ? System.getenv("IO_SID") : "993202";
                String subscriberaccount = Configuration.hostEnvIsProduction() ? System.getenv("IO_SACCOUNT") : "OLTP";
                String subscriberpasscode = Configuration.hostEnvIsProduction() ? System.getenv("IO_SPASSCODE") : "2NN2R9DS";
                String draSOAPURL = Configuration.hostEnvIsProduction() ? "https://soap.iovation.com/api/CheckTransactionDetails" : "https://ci-soap.iovation.com/api/CheckTransactionDetails";

                String rules = "application";
                String usercode = user.getId().toString();
                String enduserip = Util.getClientIpAddres(form.getRequest());

                String blackbox = form.getIoBlackbox();

                final ExecutorService executor = Executors.newSingleThreadExecutor();
                final Future future = executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Corriendo IO with ip: "+enduserip);
                        CheckTransactionDetails.TxnProperties pii = new CheckTransactionDetails.TxnProperties();
                        CheckTransactionDetails.TxnProperties.Property prop = new CheckTransactionDetails.TxnProperties.Property();

                        String phone = user.getPhoneNumber();
                        String email = user.getEmail();

                        prop.setName("Email");
                        prop.setValue(email);

                        prop = new CheckTransactionDetails.TxnProperties.Property();
                        prop.setName("MobilePhoneNumber");
                        prop.setValue("+51" + phone);
                        pii.getProperty().add(prop);


                        Holder<String> result = new Holder<>("");
                        Holder<String> reason = new Holder<>("");
                        Holder<String> endblackbox = new Holder<>("");
                        Holder<String> trackingnumber = new Holder<>("");
                        Holder<CheckTransactionDetailsResponse.Details> details = new Holder<>();

                        com.affirm.iovation.ws.transaction.check.Service client = new com.affirm.iovation.ws.transaction.check.Service();
                        PortType binding = client.getCheckTransactionDetails();

                        // set the endpoint URL to the appropriate environment
                        BindingProvider bp = (BindingProvider) binding;
                        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, draSOAPURL);
                        bp.getRequestContext().put("com.sun.xml.internal.ws.request.timeout", 3000);
                        bp.getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", 1000);

                        binding.checkTransactionDetails(subscriberid, subscriberaccount, subscriberpasscode, enduserip,
                                usercode, blackbox, rules, pii, result, reason, trackingnumber, endblackbox, details
                        );

                        JSONObject js = new JSONObject();
                        js.put("result", result.value);
                        js.put("reason", reason.value);
                        js.put("endblackbox", endblackbox.value);
                        js.put("trackingnumber", trackingnumber.value);
                        js.put("details", new JSONArray(details.value == null ? new ArrayList<>() : details.value.getDetail()));


                        loanApplicationDao.registerIovationResponse(loanApplication.getId(), js);

                        session.setAttribute("doIovation", false);
                        session.setAttribute("io_userId", null);
                        session.setAttribute("io_laId", null);

                    }
                });
                executor.shutdown(); // This does not cancel the already-scheduled task.


                try {
                    future.get(20, TimeUnit.SECONDS);
                } catch (Throwable e) {
                    logger.error("Error on waiting iovation", e);
                }
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                if (loanApplication.getEntityUserId() != null)
                    return "DEFAULT";

                if (!loanApplicationDao.mustCallIovation(loanApplication.getId())) {
                    return "DEFAULT";
                }
                break;
        }
        return null;
    }

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                switch (path) {
                    case "iojs":
                        HttpServletRequest request = (HttpServletRequest)params.get("request");
                        HttpServletResponse response = (HttpServletResponse) params.get("response");

                        String server = dynamicSandbox;
                        if (Configuration.hostEnvIsProduction())
                            server = dynamicProduction;

                        Header[] frontHeaders = getHeaders(request);
                        String reqUri = request.getRequestURI();
                        int io = reqUri.indexOf("/") + 1;
                        reqUri = reqUri.substring(io);
                        io = reqUri.indexOf("/") + 1;
                        reqUri = reqUri.substring(io);
                        String urlString = server + reqUri + (request.getQueryString() != null ? ("?" + request.getQueryString()) : "");
                        johnsWay(urlString, frontHeaders, response);
                        return null;
                }
                break;
        }
        throw new Exception("No method configured");
    }

    public void johnsWay(String urlString, Header[] frontHeaders, HttpServletResponse frontResponse) throws Exception {
        URL obj = new URL(urlString);
        HttpURLConnection middleManConnection = (HttpURLConnection) obj.openConnection();
        // optional default is GET
        middleManConnection.setRequestMethod("GET");
        middleManConnection.setDoOutput(true);

        //add request frontHeaders to middleManHeaders
        for (int i = 0; i < frontHeaders.length; i++) {
            middleManConnection.setRequestProperty(frontHeaders[i].getName(), frontHeaders[i].getValue());
        }

        int responseCode = middleManConnection.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(middleManConnection.getInputStream()));

        String inputLine;

        StringBuffer middleManResponse = new StringBuffer();

        ServletOutputStream frontSout = frontResponse.getOutputStream();

        while ((inputLine = in.readLine()) != null) {
            middleManResponse.append(inputLine);
            frontSout.write(inputLine.getBytes());
        }
        in.close();

        Map<String, List<String>> map = middleManConnection.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            try {
                String listString = String.valueOf(entry.getValue());
                if (listString == null)
                    continue;
                if (entry.getValue().size() == 0) {
                    if (entry.getKey() != null) {
                        frontResponse.addHeader(entry.getKey(), "\r\n");
                    }
                } else {
                    if (entry.getKey() != null) {
                        frontResponse.addHeader(entry.getKey(), listString.substring(1, listString.length() - 1) + "\r\n");
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        frontResponse.setContentType("application/javascript; charset=utf-8");
        frontSout.flush();
    }

    public void addAttributesToExecuteIO(Map<String, Object> model, Integer userId, Integer laId) {
        model.put("doIovation", true);
        if (Configuration.hostEnvIsLocal()) {
            model.put("base", Configuration.CLIENT_LOC_URL);
            model.put("static", staticSandboxSnare);
        } else if (Configuration.hostEnvIsDev()) {
            model.put("base", Configuration.CLIENT_DEV_URL);
            model.put("static", staticSandboxSnare);
        } else if (Configuration.hostEnvIsStage()) {
            model.put("base", Configuration.CLIENT_STG_URL);
            model.put("static", staticSandboxSnare);
        } else if (Configuration.hostEnvIsProduction()) {
            model.put("base", Configuration.CLIENT_PRD_URL);
            model.put("static", staticProductionSnare);
        }

        model.put("isLoc", Configuration.hostEnvIsLocal());
        model.put("isStg", Configuration.hostEnvIsStage());
        model.put("isDev", Configuration.hostEnvIsDev());
        model.put("isPrd", Configuration.hostEnvIsProduction());
    }

    public Header[] getHeaders(HttpServletRequest request) {
        ArrayList<BasicHeader> headers = new ArrayList();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = ((String) headerNames.nextElement()).toLowerCase();
            String value = request.getHeader(name);
            if (isValidHeader(name)) headers.add(new BasicHeader(name, value));
        }
        Header[] x = new Header[headers.size()];
        return headers.toArray(x);
    }

    boolean isValidHeader(String name) {
        if (name.toLowerCase().contains("content-length")) {
            return false;
        }

        if (name.toLowerCase().equals("host")) {
            return false;
        }
        if (name.toLowerCase().contains("accept-encoding")) {
            boolean bool = true;//both work ask Paul
            System.out.println("accept-encoding: " + bool);
            return bool;
        }
        return true;
    }

}

