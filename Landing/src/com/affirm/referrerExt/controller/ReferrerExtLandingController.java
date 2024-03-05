package com.affirm.referrerExt.controller;

import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.util.AjaxResponse;
import com.affirm.referrerExt.dao.ReferrerUserDao;
import com.affirm.referrerExt.model.ReferrerUser;
import com.affirm.referrerExt.service.ReferralExtranetAuthService;
import com.affirm.referrerExt.util.LoggedReferrerUser;
import com.affirm.referrerExt.util.RegisterReferrerForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Controller
@Scope("request")
public class ReferrerExtLandingController {

    public static final String BASE_URI_EXT_REFERRAL = "solvers";

    @Autowired
    private ReferrerUserDao referrerUserDao;
    @Autowired
    private ReferralExtranetAuthService referralExtranetAuthService;

    @RequestMapping(value = "/" + BASE_URI_EXT_REFERRAL, method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String getLanding(ModelMap model, Locale locale, HttpServletRequest req) throws Exception {
        model.addAttribute("registerReferrerForm", new RegisterReferrerForm());
        return "/referred/landingReferred";
    }

    @RequestMapping(value = "/" + BASE_URI_EXT_REFERRAL, method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object loginOrRegister(ModelMap model, Locale locale, HttpServletRequest req, RegisterReferrerForm form) throws Exception {

        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().toJson(locale));
        }

        LoggedReferrerUser loggedReferrerUser = referralExtranetAuthService.authenticate(form);
        if (loggedReferrerUser == null) {
            return AjaxResponse.errorMessage("La combinaci&oacute; del n&uacute;mero de documento con el email y celular no es correcta.");
        }

        return AjaxResponse.redirect(req.getContextPath() + "/" + BASE_URI_EXT_REFERRAL + "/howItWorks");
    }

    @RequestMapping(value = "/" + BASE_URI_EXT_REFERRAL + "/logout", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object logout(ModelMap model, Locale locale, HttpServletRequest req) throws Exception {
        referralExtranetAuthService.logout();
        return AjaxResponse.redirect(req.getContextPath() + "/" + BASE_URI_EXT_REFERRAL);
    }
}
