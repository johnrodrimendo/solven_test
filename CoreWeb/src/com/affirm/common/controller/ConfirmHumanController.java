package com.affirm.common.controller;

import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.Util;
import com.affirm.security.service.ReCaptchaService;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Created by jarmando on 26/12/16.
 * <p>
 * This class asks checks if the user is human and if the user succeeds then it will be redirected
 * to the URL in session.
 */
@Controller
@Scope("request")
public class ConfirmHumanController {
    final String CHECK_SUCCESS = "/checkSuccess";

    @Autowired
    ReCaptchaService reCaptchaService;

    @RequestMapping(value = ReCaptchaService.CONFIRM_URL, method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String confirmHuman(ModelMap model, Locale locale, HttpServletRequest req) throws Exception {
        Boolean countryUnlocked = (Boolean) SecurityUtils.getSubject().getSession().getAttribute("countryUnlocked");
        System.out.println("cc 1: " + countryUnlocked);
        countryUnlocked = countryUnlocked != null ? countryUnlocked : false;
        System.out.println("cc 2: " + countryUnlocked);
        if (countryUnlocked) {//country is already unlocked, bye
            return "redirect:/";
        }
        return "confirmHuman";
    }

    @RequestMapping(value = CHECK_SUCCESS, method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object checkSuccess(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("g-recaptcha-response") String recaptchaResponse) throws Exception {
        String address;
            if (reCaptchaService.checkSuccess(request, recaptchaResponse)) {
            String destiny = reCaptchaService.destinyUrl(request);
            reCaptchaService.cleanDestinyUrl();
            if (Util.anyMatchCaseInsensitive(destiny, ReCaptchaService.CONFIRM_URL, CHECK_SUCCESS))
                destiny = "/";
            return AjaxResponse.redirect(request.getContextPath() + destiny);
        } else {
            return AjaxResponse.redirect(request.getContextPath() + "/confirmHuman");
        }
    }
}

