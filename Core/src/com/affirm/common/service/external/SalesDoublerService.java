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

@Service("salesDoublerService")
public class SalesDoublerService {

    private static Logger logger = Logger.getLogger(SalesDoublerService.class);

    private static final String CPL_URL = "https://rdr.salesdoubler.com.ua/in/postback/2732/{CLICK_ID}?trans_id={TRANS_ID}&token=c29sdmVuQHNhbGVzZG91Ymxlci5jb20udWE";
    private static final String CPA_URL = "https://rdr.salesdoubler.com.ua/in/postback/2733/{CLICK_ID}?trans_id={TRANS_ID}&token=c29sdmVuQHNhbGVzZG91Ymxlci5jb20udWE";

    @Autowired
    private ErrorService errorService;

    public void callCPLPostback(String clickId, String transactionId) {
        try {
            callUrl(CPL_URL.replace("{CLICK_ID}", clickId.trim()).replace("{TRANS_ID}", transactionId));
        } catch (Exception ex) {
            errorService.onError(ex);
        }
    }

    public void callCPAPostback(String clickId, String transactionId) {
        try {
            callUrl(CPA_URL.replace("{CLICK_ID}", clickId.trim()).replace("{TRANS_ID}", transactionId));
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

            logger.info("SalesDoubler Postback: " + urlToCall + " :: response: " + builder.toString());
        }
    }

    public static void main(String[] args) throws Exception{
        SalesDoublerService s = new SalesDoublerService();
        s.callUrl("https://www.google.com/");
    }

}
