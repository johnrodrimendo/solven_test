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

@Service("leadGidService")
public class LeadGidService {

    private static Logger logger = Logger.getLogger(LeadGidService.class);

    private static final String CPL_URL = "https://go.leadgid.ru/aff_l?offer_id=4271&adv_sub={CLICK_ID}";

    @Autowired
    private ErrorService errorService;

    public void callCPLPostback(String clickId) {
        try {
            callUrl(CPL_URL.replace("{CLICK_ID}", clickId.trim()));
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
        LeadGidService s = new LeadGidService();
        s.callUrl("https://www.google.com/");
    }

}
