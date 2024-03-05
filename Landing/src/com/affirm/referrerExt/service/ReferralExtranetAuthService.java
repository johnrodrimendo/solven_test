package com.affirm.referrerExt.service;

import com.affirm.referrerExt.dao.ReferrerUserDao;
import com.affirm.referrerExt.model.ReferrerUser;
import com.affirm.referrerExt.util.LoggedReferrerUser;
import com.affirm.referrerExt.util.RegisterReferrerForm;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReferralExtranetAuthService {

    private static final String LOGGED_REFERRAL_SESSION_KEY = "loggedReferralUser";

    @Autowired
    private ReferrerUserDao referrerUserDao;

    public LoggedReferrerUser authenticate(RegisterReferrerForm form) throws Exception {

        // If the referrer user is alredy registered, the email and cellphone should match. If not, then register the user
        ReferrerUser referrerUser = referrerUserDao.getReferrerUser(form.getDocType(), form.getDocNumber());
        if (referrerUser != null) {
            if (referrerUser.getEmail().trim().equalsIgnoreCase(form.getEmail().trim())) {
                if (referrerUser.getPhoneNumber().trim().equalsIgnoreCase(form.getPhoneNumber().trim())) {
                    LoggedReferrerUser loggedReferrerUser = new LoggedReferrerUser();
                    loggedReferrerUser.setReferrerUser(referrerUser);
                    SecurityUtils.getSubject().getSession().setAttribute(LOGGED_REFERRAL_SESSION_KEY, loggedReferrerUser);
                    return loggedReferrerUser;
                }
            }
            return null;
        } else {
            referrerUserDao.registerReferrerUser(form.getDocType(), form.getDocNumber(), form.getEmail(), form.getPhoneNumber());
            referrerUser = referrerUserDao.getReferrerUser(form.getDocType(), form.getDocNumber());
            LoggedReferrerUser loggedReferrerUser = new LoggedReferrerUser();
            loggedReferrerUser.setReferrerUser(referrerUser);
            SecurityUtils.getSubject().getSession().setAttribute(LOGGED_REFERRAL_SESSION_KEY, loggedReferrerUser);
            return loggedReferrerUser;
        }
    }

    public LoggedReferrerUser getSessionReferralUser() {
        Object loggedReferralObj = SecurityUtils.getSubject().getSession().getAttribute(LOGGED_REFERRAL_SESSION_KEY);
        if (loggedReferralObj != null)
            return (LoggedReferrerUser) loggedReferralObj;
        return null;
    }

    public void logout() {
        Object loggedReferralObj = SecurityUtils.getSubject().getSession().getAttribute(LOGGED_REFERRAL_SESSION_KEY);
        if (loggedReferralObj != null)
            SecurityUtils.getSubject().getSession().removeAttribute(LOGGED_REFERRAL_SESSION_KEY);
    }

}
