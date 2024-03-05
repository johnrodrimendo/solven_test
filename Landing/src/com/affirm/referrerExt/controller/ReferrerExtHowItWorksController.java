package com.affirm.referrerExt.controller;

import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.util.AjaxResponse;
import com.affirm.referrerExt.dao.ReferrerUserDao;
import com.affirm.referrerExt.model.ReferrerUser;
import com.affirm.referrerExt.service.ReferralExtranetAuthService;
import com.affirm.referrerExt.util.LoggedReferrerUser;
import com.affirm.referrerExt.util.RegisterReferrerForm;
import com.affirm.system.configuration.Configuration;
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
public class ReferrerExtHowItWorksController {

    @Autowired
    private ReferralExtranetAuthService referralExtranetAuthService;

    @RequestMapping(value = "/" + ReferrerExtLandingController.BASE_URI_EXT_REFERRAL + "/howItWorks", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String getLanding(ModelMap model, Locale locale, HttpServletRequest req) throws Exception {
        LoggedReferrerUser loggedReferrerUser = referralExtranetAuthService.getSessionReferralUser();
        model.addAttribute("referralLink", Configuration.getClientDomain(51) +
                "/credito-de-consumo?consumoTrue=true&utm_source=Camp_Referidos&utm_medium=Landing&utm_campaign=Referidos&referrerPersonId=" +
                (loggedReferrerUser != null ? loggedReferrerUser.getReferrerUser().getPersonId() : ""));
        return "/referred/howItWorks";
    }
}
