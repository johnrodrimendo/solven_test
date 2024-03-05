package com.affirm.referrerExt.controller;

import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.referrerExt.dao.ReferrerUserDao;
import com.affirm.referrerExt.model.ReferrerUser;
import com.affirm.referrerExt.service.ReferralExtranetAuthService;
import com.affirm.referrerExt.util.LoggedReferrerUser;
import com.affirm.referrerExt.util.RegisterReferrerForm;
import com.affirm.referrerExt.util.UpdateReferrerForm;
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
public class ReferrerExtUserDataController {

    @Autowired
    private ReferrerUserDao referrerUserDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private ReferralExtranetAuthService referralExtranetAuthService;

    @RequestMapping(value = "/" + ReferrerExtLandingController.BASE_URI_EXT_REFERRAL + "/userData", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String getLanding(ModelMap model, Locale locale, HttpServletRequest req) throws Exception {

        LoggedReferrerUser loggedReferrerUser = referralExtranetAuthService.getSessionReferralUser();
        UpdateReferrerForm form = new UpdateReferrerForm();
        form.setDocumentType(loggedReferrerUser.getReferrerUser().getDocumentType().getId());
        form.setDocumentNumber(loggedReferrerUser.getReferrerUser().getDocumentNumber());
        form.setName(loggedReferrerUser.getReferrerUser().getName());
        form.setFirstSurname(loggedReferrerUser.getReferrerUser().getFirstSurname());
        form.setEmail(loggedReferrerUser.getReferrerUser().getEmail());
        form.setPhoneNumber(loggedReferrerUser.getReferrerUser().getPhoneNumber());
        form.setBank(loggedReferrerUser.getReferrerUser().getBank() != null ?
                loggedReferrerUser.getReferrerUser().getBank().getId() : null);
        form.setBankAccountNumber(loggedReferrerUser.getReferrerUser().getBankAccountNumber());
        form.setCci(loggedReferrerUser.getReferrerUser().getCci());
        model.addAttribute("updateReferrerForm", form);
        return "/referred/userData";
    }

    @RequestMapping(value = "/" + ReferrerExtLandingController.BASE_URI_EXT_REFERRAL + "/userData", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object loginOrRegister(ModelMap model, Locale locale, HttpServletRequest req, UpdateReferrerForm form) throws Exception {

        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().toJson(locale));
        }

        LoggedReferrerUser loggedReferrerUser = referralExtranetAuthService.getSessionReferralUser();
        loggedReferrerUser.getReferrerUser().setName(form.getName());
        loggedReferrerUser.getReferrerUser().setFirstSurname(form.getFirstSurname());
        loggedReferrerUser.getReferrerUser().setPhoneNumber(form.getPhoneNumber());
        loggedReferrerUser.getReferrerUser().setEmail(form.getEmail());
        loggedReferrerUser.getReferrerUser().setBank(form.getBank() != null ?
                catalogService.getBank(form.getBank()) : null);
        loggedReferrerUser.getReferrerUser().setBankAccountNumber(form.getBankAccountNumber());
        loggedReferrerUser.getReferrerUser().setCci(form.getCci());

        referrerUserDao.updateReferralUser(loggedReferrerUser.getReferrerUser());

        return AjaxResponse.ok(null);
    }
}
