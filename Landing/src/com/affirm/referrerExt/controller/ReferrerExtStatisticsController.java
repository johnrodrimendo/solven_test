package com.affirm.referrerExt.controller;

import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.referrerExt.dao.ReferrerUserDao;
import com.affirm.referrerExt.model.ReferrerUserStatistics;
import com.affirm.referrerExt.service.ReferralExtranetAuthService;
import com.affirm.referrerExt.util.LoggedReferrerUser;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Controller
@Scope("request")
public class ReferrerExtStatisticsController {

    @Autowired
    private ReferralExtranetAuthService referralExtranetAuthService;
    @Autowired
    private ReferrerUserDao referrerUserDao;


    @RequestMapping(value = "/" + ReferrerExtLandingController.BASE_URI_EXT_REFERRAL + "/statisticsPayments", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String getStatistics(ModelMap model, Locale locale, HttpServletRequest req) throws Exception {
        LoggedReferrerUser loggedReferrerUser = referralExtranetAuthService.getSessionReferralUser();
        ReferrerUserStatistics referrerUserStatistics = referrerUserDao.getReferrerUserStatistics(loggedReferrerUser.getReferrerUser().getPersonId());
        model.addAttribute("initiatedLoans", referrerUserStatistics.getInitiatedLoans());
        model.addAttribute("completedLoans", referrerUserStatistics.getCompletedLoans());
        model.addAttribute("offerLoans", referrerUserStatistics.getOfferLoans());
        model.addAttribute("disbursedCredits", referrerUserStatistics.getDisbursedCredits());
        model.addAttribute("generatedCommission", referrerUserStatistics.getDisbursedCredits() * 100);
        return "/referred/statisticsPayments";
    }
}
