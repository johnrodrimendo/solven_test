package com.affirm.emailage;

import com.affirm.system.configuration.Configuration;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.Key;
import java.util.*;

/**
 * Created by jarmando on 21/12/16.
 */
public class EmailAgeApi {
    private static final String ACCOUNT_SID = Configuration.hostEnvIsProduction() ? System.getenv("EMAILAGE_ACCOUNT_SID") : "xxxxxxxxxxxxxxxxxxxx";
    private static final String AUTH_TOKEN = Configuration.hostEnvIsProduction() ? System.getenv("EMAILAGE_AUTH_TOKEN") : "xxxxxxxxxxxxxxxxxxxx";
    private static String requestBaseUrl = Configuration.hostEnvIsProduction() ? "https://api.emailage.com/EmailAgeValidator/" : "https://sandbox.emailage.com/EmailAgeValidator/";

    /**
     * Urid optional
     */
//    public String validEmailJson(String email, String optionalUri) throws IOException {
//        return validEmailOrIP(email
//                , ACCOUNT_SID
//                , AUTH_TOKEN
//                , "json"
//                , optionalUri
//                , requestBaseUrl
//                , null);
//    }
//
//    public String validIpJson(String ip) throws IOException {
//        return validEmailOrIP(ip
//                , ACCOUNT_SID
//                , AUTH_TOKEN
//                , "json"
//                , null
//                , requestBaseUrl
//                , null);
//    }

    /**
     * Urid optional
     */
    public String validEmailIpJson(String email, String ip, String optionalUri) throws IOException {
        return validEmailOrIP(email + "+" + ip
                , ACCOUNT_SID
                , AUTH_TOKEN
                , "json"
                , optionalUri
                , requestBaseUrl
                , null);
    }

    private String validEmailOrIP(String email, String accountSId,
                                  String authToken, String resultFormat, String optionalUri, String requestBaseUrl,
                                  String user_email) throws IOException {
        String oriUrl = requestBaseUrl + "?format=" + resultFormat + (optionalUri == null ? "" : "&urid=" + optionalUri);
        if (user_email != null && user_email.trim().length() > 0) {
            oriUrl = oriUrl + "&user_email=" + user_email;
        }

        String requestUrl = OAuth.getUrl("POST", null, oriUrl, accountSId,
                authToken);

        URL url = new URL(requestUrl);
        System.out.println(url);
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("Content-Language", "en-US");
        conn.setRequestProperty("Charset", "utf-8");
        conn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(
                conn.getOutputStream(), "utf-8");

        // write parameters
        writer.write(email);
        writer.flush();

        // Get the response
        StringBuffer answer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), "utf-8"));

        String line;
        while ((line = reader.readLine()) != null) {
            answer.append(line);
        }
        writer.close();
        reader.close();

        return answer.toString();
    }

    static class OAuth {
        private static Random _Random = new Random();
        private static String _UnreservedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.~";
        public final static String HMACSHA1 = "HMAC-SHA1";

        public static String getUrl(String method, String hashAlgorithm,
                                    String url, String consumerKey, String consumerSecret) {
            if (method == null)
                method = "GET";
           /* if (hashAlgorithm == null)
                hashAlgorithm = HMACSHA1;*/
            double nowDates = (new Date()).getTime(); // from 1970.1.1

            String timestamp = String.valueOf(Math.floor(nowDates));
            String nonce = GetRandomString(10);

            int index = url.indexOf('?');
            String querystring;
            if (index == -1) {
                querystring = "";
            } else {
                querystring = url.substring(index + 1);
                url = url.substring(0, index);
            }

            String[] splString = querystring.split("&");
            Map<String, String> query = new HashMap<String, String>();
            for (int i = 0; i < splString.length; i++) {
                int in = splString[i].indexOf("=");
                if (in == -1)
                    query.put(splString[i], null);
                else {
                    String key = splString[i].substring(0, in);
                    String value = splString[i].substring(in + 1);
                    query.put(key, value);
                }
            }

            // query.put("oauth_version", "1.0");
            // query.put("oauth_timestamp", timestamp);
            // query.put("oauth_signature_method", "HMAC-SHA1");
            // query.put("oauth_nonce", nonce);
            // query.put("oauth_consumer_key", consumerKey);
            StringBuilder sb = new StringBuilder();
            for (Object key : query.keySet()) {
                sb.append(((String) key).concat(query.get(key) == null ? "" : "="
                        + query.get(key))
                        + "&");
            }
            sb.append("oauth_consumer_key=" + consumerKey + "&");
            sb.append("oauth_nonce=" + nonce + "&");
            sb.append("oauth_signature_method=" + "HMAC-SHA1" + "&");
            sb.append("oauth_timestamp=" + timestamp + "&");
            sb.append("oauth_version=" + 1.0);
            querystring = sb.toString();

            String data = "";
            try {
                data = method.toUpperCase().concat("&")
                        .concat(URLEncoder.encode(url, "utf-8")).concat("&")
                        .concat(URLEncoder.encode(querystring, "utf-8"));
            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            String sig = "";
            try {
                sig = URLEncoder.encode(
                        new String(Base64.getEncoder().encode(HmacSHA1Encrypt(data,
                                consumerSecret + "&")), "ASCII"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (RuntimeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return url.concat("?").concat(querystring).concat("&oauth_signature=")
                    .concat(sig);
        }

        private static String GetRandomString(int length) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < length; i++)
                result.append(_UnreservedChars.charAt(_Random.nextInt(25)));
            return result.toString();
        }

        public static byte[] HmacSHA1Encrypt(String encryptText, String encryptKey)
                throws Exception {
            byte[] keybytes = encryptKey.getBytes("UTF-8");
            Mac mac = Mac.getInstance("HMACSHA1");
            Key key = new SecretKeySpec(keybytes, "HMACSHA1");
            mac.init(key);
            byte[] text = encryptText.getBytes("UTF-8");
            return mac.doFinal(text);
        }
    }

    public static void main(String[] args) throws Exception{
        EmailAgeApi emailAgeApi = new EmailAgeApi();
        System.out.println(emailAgeApi.validEmailIpJson("jignacio.barreiro@gmail.com", null, null).trim());
    }

}
