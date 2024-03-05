package com.affirm.common.util;

import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jrodriguez
 */
public class Util {

    private static Logger logger = Logger.getLogger(Util.class);

    public static final SimpleDateFormat FACEBOOK_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    private static final String[] HEADERS_TO_TRY_FOR_IP = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"};

    /**
     * Replace the special characters by its normal value
     *
     * @param input
     * @return
     */
    public static String replaceSpecialChars(String input) {
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i = 0; i < original.length(); i++) {
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }
        return output;
    }

    /**
     * Removes the whitespaces on the sides (trim) and inside, if it has more
     * than 2 spaces
     *
     * @return
     */
    public static String removeWhiteSpaces(String input) {
        return input.trim().replaceAll(" +", " ");
    }

    public static String getClientIpAddres(HttpServletRequest request) {
        // TODO This should dentify if there is a proxy ip
        for (String header : HEADERS_TO_TRY_FOR_IP) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                if (ip.contains(",")) {
                    String[] proxyips = ip.split(",");
                    if (proxyips.length > 0) {
                        return proxyips[0].trim();
                    }
                }
                return ip.trim();
            }
        }
        return request.getRemoteAddr();

        // New technique
//        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
//        if (xForwardedForHeader == null) {
//            return request.getRemoteAddr();
//        } else {
//            // As of https://en.wikipedia.org/wiki/X-Forwarded-For
//            // The general format of the field is: X-Forwarded-For: client, proxy1, proxy2 ...
//            // we only want the client
//            return new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
//        }
    }

//    public static JSONObject getJsonLocation(String ip) {
//        JSONObject jsonResponse = null;
//        try {
//            String url = "http://freegeoip.net/json/" + ip;
//
//            //logger.debug("Getting the location of the ip address: " + url);
//            URL obj = new URL(url);
//            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            String inputLine;
//            StringBuffer response = new StringBuffer();
//
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//            in.close();
//
//            String res = response.toString();
//            System.out.println("Retorno: " + res);
//            jsonResponse = new JSONObject(res);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            // logger.error("Error while getting consulting freegeoip", ex);
//        }
//        return jsonResponse;
//    }

    public static Integer intOrNull(String solvenPassword) {
        try {
            return Integer.parseInt(solvenPassword);
        } catch (Exception e) {
        }
        return null;
    }

    public static void addEnvAttributes(ModelMap model) {
        model.addAttribute("isLoc", Configuration.hostEnvIsLocal());
        model.addAttribute("isStg", Configuration.hostEnvIsStage());
        model.addAttribute("isDev", Configuration.hostEnvIsDev());
        model.addAttribute("isPrd", Configuration.hostEnvIsProduction());
    }

    public static Date parseDate(String unparsedDate, String pattern) {
        if (unparsedDate != null) {
            try {
                return new SimpleDateFormat(pattern).parse(unparsedDate);
            } catch (Exception ex) {
                logger.error("Error parsing date", ex);
            }
        }
        return null;
    }

    /**
     * Text matching util
     */
    public static boolean anyMatchCaseInsensitive(String text, String... options) {
        for (int i = 0; i < options.length; i++) {
            String opt = options[i];
            if (text.equalsIgnoreCase(opt))
                return true;
        }
        return false;
    }

    /**
     * Text matching util
     */
    public static boolean anyContainsCaseInsensitive(String text, String... options) {
        for (int i = 0; i < options.length; i++) {
            if (text.toUpperCase().contains(options[i].toUpperCase()))
                return true;
        }
        return false;
    }

    /**
     * Returns object of type clazz from an json api link
     *
     * @param link
     * @param clazz
     * @return
     * @throws Exception
     */
    public static <T> T getObjectFromUrl(String link, Class<T> clazz) throws Exception {
        T t = null;
        URL url;
        String jsonString = "";
        //  try {
        url = new URL(link);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            jsonString = jsonString + inputLine;
        }
        in.close();
       /* } catch (IOException e) {
            e.printStackTrace();
        }*/
        if (!StringUtils.isEmpty(jsonString)) {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, clazz);
        }
        return t;
    }


    public static BufferedImage joinBufferedImage(BufferedImage img1, BufferedImage img2) {
        //do some calculate first
        int offset = 5;
        int wid = img1.getWidth() + img2.getWidth() + offset;
        int height = Math.max(img1.getHeight(), img2.getHeight()) + offset;
        //create a new buffer and draw two image into the new image
        BufferedImage newImage = new BufferedImage(wid, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        //fill background
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, wid, height);
        //draw image
        g2.setColor(oldColor);
        g2.drawImage(img1, null, 0, 0);
        g2.drawImage(img2, null, img1.getWidth() + offset, 0);
        g2.dispose();
        return newImage;
    }

    public static BufferedImage joinByteArray(byte[] bytes1, byte[] bytes2) {
        return joinBufferedImage(toBufferedImage(bytes1), toBufferedImage(bytes2));
    }

    public static BufferedImage toBufferedImage(byte[] imageInByte) {
        try {
            InputStream in = new ByteArrayInputStream(imageInByte);
            BufferedImage bImageFromConvert = ImageIO.read(in);
            return bImageFromConvert;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}