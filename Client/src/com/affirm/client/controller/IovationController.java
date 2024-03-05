package com.affirm.client.controller;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.transactional.User;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.Util;
import com.affirm.iovation.ws.transaction.check.CheckTransactionDetails;
import com.affirm.iovation.ws.transaction.check.CheckTransactionDetailsResponse;
import com.affirm.iovation.ws.transaction.check.PortType;
import com.affirm.iovation.ws.transaction.check.Service;
import com.affirm.system.configuration.Configuration;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by jarmando on 04/01/17.
 */
//@Controller
//@Scope("request")
public class IovationController {
    public static final String staticSandboxSnare = "/js/i/sandbox/latest/first_third.js";
    public static final String staticProductionSnare = "/js/i/latest/first_third.js";

    private static final String dynamicSandbox = "https://ci-first.iovation.com/";
    private static final String dynamicProduction = "https://first.iovation.com/";

    private static final String snareSandbox = "https://ci-mpsnare.iovation.com/snare.js";
    private static final String snareProduction = "https://mpsnare.iovation.com/snare.js";//TODO check if this is the prd snare

    private static final String SNARE = "SNARE";
    private static final String DYN_WDP = "DYN_WDP";

    @Autowired
    UserDAO userDAO;
    @Autowired
    LoanApplicationDAO loanApplicationDAO;

    @RequestMapping(value = "/iodemo", produces = "text/plain; charset=utf-8")
    public String ioDemo(ModelMap model, HttpServletRequest request) throws Exception {
        addAttributesToExecuteIO(request.getSession(), model, 1372, 1569);
        return "ioDemo";
    }

    public static void addAttributesToExecuteIO(HttpSession session, ModelMap model, Integer userId, Integer laId) {
        model.addAttribute("doIovation", true);
        if (Configuration.hostEnvIsLocal()) {
            model.addAttribute("base", Configuration.CLIENT_LOC_URL);
            model.addAttribute("static", staticSandboxSnare);
        } else if (Configuration.hostEnvIsDev()) {
            model.addAttribute("base", Configuration.CLIENT_DEV_URL);
            model.addAttribute("static", staticSandboxSnare);
        } else if (Configuration.hostEnvIsStage()) {
            model.addAttribute("base", Configuration.CLIENT_STG_URL);
            model.addAttribute("static", staticSandboxSnare);
        } else if (Configuration.hostEnvIsProduction()) {
            model.addAttribute("base", Configuration.CLIENT_PRD_URL);
            model.addAttribute("static", staticProductionSnare);
        }

        session.setAttribute("io_userId", userId);
        session.setAttribute("io_laId", laId);
        Util.addEnvAttributes(model);
    }

    @RequestMapping(value = "/i/demo", produces = "text/plain; charset=utf-8", method = RequestMethod.POST)
    public ResponseEntity<String> checkTransaction(Model model,
                                                   HttpServletRequest request,
                                                   @RequestParam(value = "ioBlackbox", required = true) String ioBlackBox) throws Exception {
        if (Configuration.hostEnvIsProduction() || Configuration.hostEnvIsStage() || Configuration.hostEnvIsDev()) {
            System.out.println("Not implemented in production!");
            return AjaxResponse.ok("success no action. not allowed in production.");
        }

        System.out.println("bb = " + request.getParameter("ioBlackbox"));
        System.out.println("ioBlackBox1 " + ioBlackBox);
        Session session = SecurityUtils.getSubject().getSession();
        //System.out.println("DID SENT DATA");
        //System.out.println("ioBlackBox " + ioBlackBox);
        //System.out.println("fpBB       " + fpBB);

        if (session.getAttribute("io_userId") == null || session.getAttribute("io_laId") == null) {
            System.out.println("Session won't allow iovation check. Aborted");
            return AjaxResponse.ok("not allowed.");
        }

        String errMsg;
        String subscriberid = Configuration.hostEnvIsProduction() ? System.getenv("IO_SID") : "993202";
        String subscriberaccount = Configuration.hostEnvIsProduction() ? System.getenv("IO_SACCOUNT") : "OLTP";
        String subscriberpasscode = Configuration.hostEnvIsProduction() ? System.getenv("IO_SPASSCODE") : "2NN2R9DS";
        String draSOAPURL;
        if (Configuration.hostEnvIsProduction())
            draSOAPURL = "https://soap.iovation.com/api/CheckTransactionDetails";
        else
            draSOAPURL = "https://ci-soap.iovation.com/api/CheckTransactionDetails";

        String rules = "application";

        /* initialize input parameters */
        String usercode = session.getAttribute("io_laId").toString();//(request.getParameter("username") != null) ? request.getParameter("username") : "testUserName";
        String enduserip = request.getRemoteAddr();
        /*String blackbox = (request.getParameter("ioBlackBox") != null && request.getParameter("fpBB") != null)
                ? request.getParameter("ioBlackBox") + ";" + request.getParameter("fpBB")
                : ((request.getParameter("ioBlackBox") != null ) ? request.getParameter("ioBlackBox")
                : (( request.getParameter( "fpBB" ) != null ) ? request.getParameter("fpBB") : "" ));*/

        String blackbox = ioBlackBox;
        System.out.println("ioBlackBox " + blackbox);
        /*(ioBlackBox != null && fpBB != null) ? ioBlackBox + ";" + fpBB : //if none is null
                ((ioBlackBox != null) ? ioBlackBox : //only ioBB is present
                        ((fpBB != null) ? fpBB : ""));*///only fpBB is present

        /* This section illustrates how to send transaction properties. This section is entirely optional
           and can be skipped if you do not wish to send extra data to iovation */

        Object userId = session.getAttribute("io_userId");
        Integer laId = (Integer) session.getAttribute("io_laId");


        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future future = executor.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("Corriendo IO");
                CheckTransactionDetails.TxnProperties pii = new CheckTransactionDetails.TxnProperties();
                CheckTransactionDetails.TxnProperties.Property prop = new CheckTransactionDetails.TxnProperties.Property();

                if (userId != null) {
                    Integer intUserId;
                    if (userId instanceof Integer)
                        intUserId = (Integer) userId;
                    else
                        intUserId = Integer.parseInt(userId.toString());
                    User user = userDAO.getUser(intUserId);
                    String phone = user.getPhoneNumber();
                    String email = user.getEmail();

                    prop.setName("Email");
                    prop.setValue(email);

                    prop = new CheckTransactionDetails.TxnProperties.Property();
                    prop.setName("MobilePhoneNumber");
                    prop.setValue("+51" + phone);
                }

/*
        pii.getProperty().add(prop);

        prop = new CheckTransactionDetails.TxnProperties.Property();
        prop.setName("ValueAmount");
        prop.setValue("10.00");

        pii.getProperty().add(prop);

        prop = new CheckTransactionDetails.TxnProperties.Property();
        prop.setName("ValueCurrency");
        prop.setValue("USD");

        pii.getProperty().add(prop);

*/
                pii.getProperty().add(prop);

        /* initialize output parameters */
                Holder<String> result = new Holder<>("");
                Holder<String> reason = new Holder<>("");
                Holder<String> endblackbox = new Holder<>("");
                Holder<String> trackingnumber = new Holder<>("");
                Holder<CheckTransactionDetailsResponse.Details> details = new Holder<>();

            /*/The following code is included as proxy support:
            java.util.ArrayList<Proxy> ps = new java.util.ArrayList<Proxy>();
            java.util.ArrayList<URI> hosts = new java.util.ArrayList<URI>();

            ps.add( new java.net.Proxy( java.net.Proxy.Type.HTTP, new java.net.InetSocketAddress( "[proxy server host]", 80)));
            hosts.add( new java.net.URI("https://ci-snare.iovation.com"));
            hosts.add( new java.net.URI("https://soap.iovation.com"));

            HostProxySelector    hps = new HostProxySelector( java.net.ProxySelector.getDefault(), hosts, ps);
            java.net.ProxySelector.setDefault(hps);*/

                Service client = new Service();
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


                loanApplicationDAO.registerIovationResponse(laId, js);

                session.setAttribute("doIovation", false);
                session.setAttribute("io_userId", null);
                session.setAttribute("io_laId", null);

            }
        });
        executor.shutdown(); // This does not cancel the already-scheduled task.


        try {
            future.get(30, TimeUnit.SECONDS);
            return AjaxResponse.ok("success.");
        } catch (Throwable e) {
//            try {
//                loanApplicationDAO.registerIovationResponse(laId, null);
//            } catch (Throwable throwable) {
//            }
            return AjaxResponse.ok("success with errors.");
        }
    }

    @RequestMapping(value = "/iojs/**", method = {RequestMethod.GET})
    @ResponseBody
    public void iojs(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String server = dynamicSandbox;
        if (Configuration.hostEnvIsProduction()) {
            server = dynamicProduction;
        }
        Header[] frontHeaders = getHeaders(request);
        String reqUri = request.getRequestURI();
        System.out.println(reqUri);
        int io = reqUri.indexOf("/") + 1;
        System.out.println(io);
        reqUri = reqUri.substring(io);
        System.out.println(reqUri);
        io = reqUri.indexOf("/") + 1;
        System.out.println(io);
        reqUri = reqUri.substring(io);
        System.out.println(reqUri);
        String urlString = server + reqUri + (request.getQueryString() != null ? ("?" + request.getQueryString()) : "");
        System.out.println("urlString: " + urlString);
        johnsWay(urlString, frontHeaders, response);
    }

    private void johnsWay(String urlString, Header[] frontHeaders, HttpServletResponse frontResponse) throws Exception {
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
        System.out.println("\nSending 'GET' request to URL : " + urlString);
        System.out.println("Response Code : " + responseCode);

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
                    System.out.println("ResponseHeader> Key : " + entry.getKey() + " ,Value :");
                    if (entry.getKey() != null) {
                        System.out.println("try e");
                        frontResponse.addHeader(entry.getKey(), "\r\n");
                        System.out.println("Successfull e");
                    }
                } else {
                    System.out.println("ResponseHeader> Key : " + entry.getKey() + " ,Value : " + listString.substring(1, listString.length() - 1));
                    if (entry.getKey() != null) {
                        System.out.println("try");
                        frontResponse.addHeader(entry.getKey(), listString.substring(1, listString.length() - 1) + "\r\n");
                        System.out.println("Successfull");
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        frontResponse.setContentType("application/javascript; charset=utf-8");
        frontSout.flush();
    }

    private Header[] getHeaders(HttpServletRequest request) {
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