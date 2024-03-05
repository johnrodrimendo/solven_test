/**
 *
 */
package com.affirm.common.util;

import com.affirm.system.configuration.Configuration;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.castor.core.util.Base64Decoder;
import org.castor.core.util.Base64Encoder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.thymeleaf.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author jrodriguez
 */
public class CryptoUtil {

    private static String key;
    private static final String UNICODE_FORMAT = "UTF8";
    public static final String DES_ENCRYPTION_SCHEME = "DES";

    static {
        CryptoUtil.setKey(Configuration.CRYPTO_KEY);
    }

    public static String encrypt(String cleartext) {
        String encryptedString = null;
        try {
            KeySpec keySpec = new DESKeySpec(key.getBytes(UNICODE_FORMAT));
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(DES_ENCRYPTION_SCHEME);
            Cipher cipher = Cipher.getInstance(DES_ENCRYPTION_SCHEME);
            SecretKey key = secretKeyFactory.generateSecret(keySpec);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainText = cleartext.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = new String(Base64Encoder.encode(encryptedText));
            encryptedString = encryptedString.replaceAll("/", ",").replaceAll("\n", ";").replaceAll("\\+", "*");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return encryptedString;
    }

    public static String decrypt(String encrypted) throws Exception {
        // return encrypted;
        KeySpec myKeySpec = new DESKeySpec(key.getBytes(UNICODE_FORMAT));
        SecretKeyFactory mySecretKeyFactory = SecretKeyFactory.getInstance(DES_ENCRYPTION_SCHEME);
        Cipher cipher = Cipher.getInstance(DES_ENCRYPTION_SCHEME);
        SecretKey key = mySecretKeyFactory.generateSecret(myKeySpec);

        String decryptedText = null;
        try {
            encrypted = encrypted.replaceAll(",", "/").replaceAll(";", "\n").replaceAll("\\*", "+");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64Decoder.decode(encrypted);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText = bytes2String(plainText);
        } catch (Exception ignored) {
        }
        return decryptedText;
    }


    // Encripts with AES
    public static SecretKeySpec getAESKey(String myKey) {
        try {
            byte[] key = myKey.getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            return new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encryptAES(String strToEncrypt, String secret) {
        try {
            SecretKeySpec secretKey = getAESKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decryptAES(String strToDecrypt, String secret) {
        try {
            SecretKeySpec secretKey = getAESKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    private static String bytes2String(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            stringBuffer.append((char) bytes[i]);
        }
        return stringBuffer.toString();
    }

    public static void setKey(String key) {
        CryptoUtil.key = key;
    }

    public static String hashPassword(String pwd) {
        String salt = BCrypt.gensalt(13);
        String bCryptedPassword = BCrypt.hashpw(pwd, salt);
        return bCryptedPassword;
    }

    public static boolean validatePassword(String pwd, String hashedPassword) {
        return BCrypt.checkpw(pwd, hashedPassword);
    }

    public static void main(String... vargs) throws Exception{

        String encryptedData = "f3ZiUqvbd+oUEoesUGIjDnYPN65JMRRkOLDGpyT0fr5sAGke4/XbKfVUJ8h0P1TU2X5udNGNKZ/N8qfHi5cg6VqgMv+VwNC5d25F0W6zKBj6IBCevvVjMOnzSqyFBo+OS/gNHOH/tb7JQ0nVGLMzhx9Ry2hgJEMSUZMSouVP1viXNiaJQeWLBfIX5byh+DG1//uigHPQBnKlMXHqWmhkWExz1TVwrt/fV+5RchDEftD73N/gtZp7D53eKwdHxTJjiVqGceoGdTe47SGjFTmW6DIfpLtI45oKVYmip3BpWeme0v7dOHYijFc3C2FSltXvzIXcAQRou8KXrvVE5RqX+xRaq8uKrYj49JqqflkwNiLHW0wHqMfQjvf8szeb4kvhV2bLr+KDaDzAts+UUydiGqEMqHt2DRNR7siYoSnMtXM=";
        String response = CryptoUtil.aesDecryptRequest(encryptedData, "$$KR1P0PO#95Dl#V");


        //GoogleAuthenticatorKey key  = new GoogleAuthenticator().createCredentials();
        //System.out.println(key.getKey());
       // System.out.println(CryptoUtil.decrypt("bavxHVFSW29aFJ1Vo0F15wgHk7Fj30XovannOIC7zVzqRC2D5tAoq7x9kMf3Z4WR"));
       // System.out.println(CryptoUtil.decrypt("bavxHVFSW2,ITgf07qrCmggHk7Fj30XoczJyBRy2OSby3b83nUopTS*BEVioYmk3"));
       // System.out.println(CryptoUtil.decrypt("bavxHVFSW2*wBp5Hfta6LAgHk7Fj30XoXhsMrAw*5JXy3b83nUopTXyFRmH2tNHI"));


      //  System.out.println(CryptoUtil.hashPassword("B?5p!Xts"));
      //  System.out.println(CryptoUtil.hashPassword("GmOUD2rv"));
      //  System.out.println(CryptoUtil.hashPassword("PbsnT07T"));

        System.out.println(CryptoUtil.encrypt("{\"entityId\": 20}"));
        System.out.println(decrypt("bavxHVFSW28eJPHYDY2AJ4cARFTp,FwFXd6CIz0fwqTn2L8,n9arj,OlnWHc2mAe"));
//
//        JSONObject json = new JSONObject();
//        json.put("creditId", 423);
//        json.put("entityId", 1);
//        System.out.println(CryptoUtil.encrypt(json.toString()));
/*

        //hash example (with known hash, as validate would interpret)
        long nanos111 = System.nanoTime();
        System.out.println(BCrypt.hashpw("fturconi", BCrypt.gensalt(13)));//prints fturconi hash on bd



        long nanos222 = System.nanoTime();
        System.out.println("Time to hash generating new salt "+TimeUnit.MILLISECONDS.convert(nanos222-nanos111, TimeUnit.NANOSECONDS));
        long nanos11 = System.nanoTime();
        System.out.println(BCrypt.hashpw("fturconi", "$2a$13$498j5uP9MjHaMBVBn23WU.Z9pc"));//prints fturconi hash on bd
        long nanos22 = System.nanoTime();
        System.out.println("Time to hash with known salt "+TimeUnit.MILLISECONDS.convert(nanos22-nanos11, TimeUnit.NANOSECONDS));
        long nanos1 = System.nanoTime();
        System.out.println(validatePassword("fturconi","$2a$13$498j5uP9MjHaMBVBn23WU.Z9pcQllCqWMgxEoEwoWyXKDscwF2lwe"));
        long nanos2 = System.nanoTime();
        System.out.println("Time to validate: "+TimeUnit.MILLISECONDS.convert(nanos2-nanos1, TimeUnit.NANOSECONDS));

        long nanos1111 = System.nanoTime();
        Md5PasswordEncoder md5pe = new Md5PasswordEncoder();
        md5pe.encodePassword("fturconi","$2a$13$498j5uP9MjHaMBVBn23WU.Z9pc");
        long nanos2222 = System.nanoTime();
        System.out.println("Time to hash generating new salt "+TimeUnit.MILLISECONDS.convert(nanos2222-nanos111, TimeUnit.NANOSECONDS));
    */
    }

    public static String encryptAuthSecret(String username, String secret) {
        return CryptoUtil.encryptAES(secret, new String(Arrays.copyOf(username.toCharArray(), 4)) + new String(Arrays.copyOf("32lu#19F$T$#0qsq".toCharArray(), 12)));

    }

    public static String decryptAuthSecret(String username, String hashedSecret) {
        return CryptoUtil.decryptAES(hashedSecret, new String(Arrays.copyOf(username.toCharArray(), 4)) + new String(Arrays.copyOf(System.getenv("SECRET_ENCRYPT_KEY").toCharArray(), 12)));

    }

    public static String aesDecryptRequest(String encryptStr, String decryptKey) throws Exception {
        return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
    }

    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }

    public static byte[] base64Decode(String base64Code) throws Exception{
        return StringUtils.isEmpty(base64Code) ? null : Base64Decoder.decode(base64Code);
    }
}
