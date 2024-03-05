/**
 *
 */
package com.affirm.common.controller;

import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.Nationality;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.CryptoUtil;
import com.affirm.security.model.ITwoFactorAuthLoggedUser;
import com.google.gson.Gson;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */

@Controller("twoFactorAuthenticationController")
@Scope("request")
public class TwoFactorAuthenticationController {

    private static final Logger logger = Logger.getLogger(TwoFactorAuthenticationController.class);
    public static final String LOGIN_URL = "2falogin";

    @Autowired
    private CatalogService catalogService;

    @RequestMapping(value = "/" + LOGIN_URL, method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object getLogin(Locale locale) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        Object principal = subject.getPrincipal();
        if (principal != null && subject.isAuthenticated()
                && principal instanceof ITwoFactorAuthLoggedUser
                && ((ITwoFactorAuthLoggedUser) principal).need2FA()
                && !((ITwoFactorAuthLoggedUser) principal).is2FALogged()) {
            return new ModelAndView("2FactorAuthenticationLogin");
        }
        return new ModelAndView("404");
    }

    @RequestMapping(value = "/" + LOGIN_URL, method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object postLogin(Locale locale, @RequestParam("token") Integer token) throws Exception {
        ITwoFactorAuthLoggedUser loggedUser = (ITwoFactorAuthLoggedUser) SecurityUtils.getSubject().getPrincipal();

        String decryptedSecret = CryptoUtil.decryptAES(loggedUser.get2FASharedSecret(), System.getenv("SECRET_ENCRYPT_KEY"));
        if (new GoogleAuthenticator().authorize(decryptedSecret, token)) {
            loggedUser.set2FALogged();
            String urlToRedirect = SecurityUtils.getSubject().getSession().getAttribute("twoFactorSuccessRedirectUrl") + "";
            return AjaxResponse.redirect(urlToRedirect);
        }

        return AjaxResponse.errorMessage("El token ingresado no es correcto.");
    }
}
