package com.affirm.backoffice.controller;

import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.Util;
import com.affirm.security.service.TokenAuthService;
import com.affirm.system.configuration.Configuration;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import net.sf.ehcache.CacheManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Locale;

/**
 * @author jrodriguez
 */
@Controller
@Scope("request")
public class SystemController {

    private static Logger logger = Logger.getLogger(SystemController.class);

    @Autowired
    private CacheManager ehCacheManager;
    @Autowired
    private TokenAuthService tokenAuthService;
    @Autowired
    private UtilService utilService;

    @RequestMapping(value = "/clearCache", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "system:dbCache:clean", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object clearCache() {
        ehCacheManager.clearAll();
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/clearCacheCli", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object clearCacheCli() throws Exception {
        Integer key = tokenAuthService.getAppSharedToken();
        String s = Util.getObjectFromUrl(Configuration.getClientDomain() + "/clearCache/" + key, String.class);
        return AjaxResponse.ok(s + " " + key);
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:system:administration", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showAdmin(ModelMap model, Locale locale) {
        return "admin";
    }

    @RequestMapping(value = "/vacio", method = RequestMethod.GET)
    @ResponseBody
    public String vacio() {
        return "";
    }

    @RequestMapping(value = "/redirectVacio", method = RequestMethod.GET)
    @ResponseBody
    public String redirectVacio() {
        return "redirect:";
    }

    public static void main(String[] args) throws Exception {
        //GoogleAuthenticator gAuth = new GoogleAuthenticator();
        //Integer s = gAuth.getTotpPassword("");
        //System.out.println("bbvaDemo".substring("bbvaDem  o".length()-4));
        //System.out.println(generateMaterSecret());
        //System.out.println(currentMasterTOPT());
        //System.out.println(CryptoUtil.hashPassword(""));
        /*ENCRIPTAR PASSWORD EN GENERAL*/
        System.out.println(CryptoUtil.hashPassword("#mE6TW"));
        /*VARIAS A LA VEZ*/
        //employerEncryptPassword("K0bsa1_", "K0bsa2-", "K0bsa3+");
        //newUser("edavalos");
    }

    private static void employerEncryptPassword(String... plainpwds) {
        for (int i = 0; i < plainpwds.length; i++) {
            System.out.println(CryptoUtil.hashPassword(plainpwds[i]));
            System.out.println(CryptoUtil.hashPassword(plainpwds[i]));
            System.out.println(CryptoUtil.hashPassword(plainpwds[i]));
            System.out.println("");
        }
    }

    public static String generateMaterSecret() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        String encriptionkey = System.getenv("SECRET_ENCRYPT_KEY").substring(8, 16) + System.getenv("SECRET_ENCRYPT_KEY").substring(0, 8);
        String encriptedSecret = CryptoUtil.encryptAES(gAuth.createCredentials().getKey(), encriptionkey);
        System.out.println(gAuth.createCredentials().getKey());
        return encriptedSecret;
    }

    public static int currentMasterTOPT() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        String encriptedSecret = System.getenv("MASTER_TFA_SECRET");
        System.out.println("Hola");
        System.out.println(encriptedSecret);
        String encriptionkey = System.getenv("SECRET_ENCRYPT_KEY").substring(8, 16) + System.getenv("SECRET_ENCRYPT_KEY").substring(0, 8);
        System.out.println(encriptionkey);
        String decriptedSecret = CryptoUtil.decryptAES(encriptedSecret, encriptionkey);
        System.out.println(decriptedSecret);
        return gAuth.getTotpPassword(decriptedSecret);
    }

    public GoogleAuthenticatorKey newUser(String user) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        final GoogleAuthenticatorKey gaMasterKey = gAuth.createCredentials();
        String url = utilService.qrImageUrl(user, gaMasterKey.getKey());
        System.out.println(gaMasterKey.getKey());


        //String cryptoKey = System.getenv("SECRET_ENCRYPT_KEY").substring(8, 16) + System.getenv("SECRET_ENCRYPT_KEY").substring(0, 8);
        //String encryptedTFA = CryptoUtil.encryptAES(gaMasterKey.getKey(), cryptoKey);

        String encryptedTFA = CryptoUtil.encryptAuthSecret(user, gaMasterKey.getKey());
        //image qr url
        System.out.println(url);
        //tfa_shared_secret
        System.out.println(encryptedTFA);

        //String dec = CryptoUtil.decryptAuthSecret(user, encryptedTFA);
        //System.out.println(dec);

        //scratch
        System.out.println(gaMasterKey.getScratchCodes());
        return gaMasterKey;
    }
}
