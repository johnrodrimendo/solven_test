package com.affirm.common.service.external;

import com.affirm.common.service.ErrorService;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service("toroLeadService")
public class ToroLeadService {

    private static Logger logger = Logger.getLogger(ToroLeadService.class);

    private static final String CPL_URL = "https://conv.affcpatrack.com?transaction_id={TRX_ID}&event=cpl";
    private static final String CPA_URL = "https://conv.affcpatrack.com?transaction_id={TRX_ID}&event=cpa";

    @Autowired
    private ErrorService errorService;

    public void callCPLPostback(String clickId) {
        try {
            callUrl(CPL_URL.replace("{TRX_ID}", clickId.trim()));
        } catch (Exception ex) {
            errorService.onError(ex);
        }
    }

    public void callCPAPostback(String clickId) {
        try {
            callUrl(CPA_URL.replace("{TRX_ID}", clickId.trim()));
        } catch (Exception ex) {
            errorService.onError(ex);
        }
    }

    private void callUrl(String urlToCall) throws Exception {
        if(Configuration.hostEnvIsProduction()) {
            URL url = new URL(urlToCall);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setRequestMethod("GET");
//        con.setDoOutput(true);

            StringBuilder builder = new StringBuilder();
            builder.append(con.getResponseCode())
                    .append(" -> ");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                builder.append(inputLine);
            in.close();

            logger.info("ToroLead Postback: " + urlToCall + " :: response: " + builder.toString());
        }
    }

    public static void main(String[] args) throws Exception{
        ToroLeadService s = new ToroLeadService();
        s.callUrl("https://www.google.com/");
    }

}
