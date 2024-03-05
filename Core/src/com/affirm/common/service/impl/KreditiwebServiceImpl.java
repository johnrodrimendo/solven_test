package com.affirm.common.service.impl;

import com.affirm.common.service.KreditiwebService;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


/**
 * Created by solven9 on 06/02/18.
 */
@Service
public class KreditiwebServiceImpl implements KreditiwebService{

    private final String URL_KREDITIWEB="https://clean.tracksacai.com/aff_l?offer_id=2701";
    private final String USER_AGENT = "Mozilla/5.0";

    public void notifyDisbursement(){
        notifyUsingGet();
    }

    private void notifyUsingGet(){
        try{
            URL obj = new URL(URL_KREDITIWEB);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            //con.setRequestProperty("User-Agent", USER_AGENT);
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            //print result
            System.out.println("Sending request to Kreditiweb pixel URL: " + URL_KREDITIWEB);
            System.out.println("Kreditiweb response Code : " + responseCode);
            System.out.println(response.toString().substring(0,20));

        }catch(MalformedURLException mue){
            mue.printStackTrace();
        }catch(ProtocolException pe){
            pe.printStackTrace();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    private void notifyUsingImageIO(){
        try{
            URL url = new URL(URL_KREDITIWEB);
            BufferedImage img = ImageIO.read(url);
            System.out.println("Loading GIF imge from Kreditiweb pixel URL: " + URL_KREDITIWEB);
            System.out.println("Kreditiweb pixel size :  W:" + img.getWidth()+" H:"+img.getHeight());
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

}
