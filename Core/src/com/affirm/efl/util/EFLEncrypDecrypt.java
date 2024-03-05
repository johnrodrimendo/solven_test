package com.affirm.efl.util;

import com.affirm.common.util.JsonUtil;
import com.affirm.efl.model.EFLTokens;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

/**
 * Created by dev5 on 03/07/17.
 */
public class EFLEncrypDecrypt {

    private byte[] decode(String s) throws Exception{
        byte[] decoded = Base64.decodeBase64(s);
        return decoded;
    }

    private String encode(byte[] s) throws Exception{
        byte[] encoded = Base64.encodeBase64(s);
        return new String(encoded);
    }

    private byte[] encryptBCP(String key, byte[] initVector, byte[] value) throws Exception{
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING", new BouncyCastleProvider());
        IvParameterSpec iv = new IvParameterSpec(initVector);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encryptedData = cipher.doFinal(value);
        return encryptedData;
    }

    private byte[] dencryptBCP(String key, byte[] initVector, byte[] value) throws Exception{
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING", new BouncyCastleProvider());
        IvParameterSpec iv = new IvParameterSpec(initVector);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] decryptedData = cipher.doFinal(value);
        return decryptedData;
    }

    public EFLTokens getTokens(boolean flagApplicant) throws Exception{
        EFLTokens tokens = new EFLTokens();
        final String uri;
        final String decryptKey;
        final String encryptKey;
        final String identifier;
        if(flagApplicant){
            uri = System.getenv("EFL_LOGIN_URL");
            decryptKey = System.getenv("EFL_DECRYPTION_KEY");
            encryptKey = System.getenv("EFL_ENCRYPTION_KEY");
            identifier = System.getenv("EFL_SOLVEN_IDENTIFIER");
        }else{
            uri = System.getenv("EFL_SCORE_LOGIN");
            decryptKey = System.getenv("EFL_SCORE_DECRYPTION_KEY");
            encryptKey = System.getenv("EFL_SCORE_ENCRYPTION_KEY");
            identifier = System.getenv("EFL_SCORE_SOLVEN_IDENTIFIER");
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>("{\"identifier\": \"" + identifier + "\"}", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
        JSONObject json = new JSONObject(result.getBody());
        JSONObject data;
        if(flagApplicant) data = JsonUtil.getJsonObjectFromJson(json, "data", null);
        else data = json;

        String authToken = JsonUtil.getStringFromJson(data, "authToken", null);
        String reqToken = JsonUtil.getStringFromJson(data, "reqToken", null);

        /*Encryption - Decryption - Encode - Decode*/
        byte[] initVector = decode(authToken);
        byte[] encryptedReqToken = decode(reqToken);
        byte[] decryptedReqToken = dencryptBCP(decryptKey, initVector, encryptedReqToken);
        byte[] reEncryptedReqToken = encryptBCP(encryptKey, initVector, decryptedReqToken);
        String reqTokenFinal = encode(reEncryptedReqToken);
        /*******************************************/
        tokens.setAuthToken(authToken);
        tokens.setReqToken(reqTokenFinal);
        System.out.println(tokens.toString());
        return tokens;
    }

}
