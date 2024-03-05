package com.affirm.common.service.impl;

import com.affirm.common.service.SessionService;
import com.affirm.common.service.UrlService;
import com.affirm.system.configuration.Configuration;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by jarmando on 26/01/17.
 */
@Service("sessionService")
public class SessionServiceImpl implements SessionService {

    @Override
    public Object getMutex() {
        // NOTE:  We cannot create the mutex object if it is absent from
        // the session in this method without locking on a global
        // constant, as two concurrent calls to this method may then
        // return two different objects!
        //
        // To avoid having to lock on a global even just once, the mutex
        // object is instead created when the session is created in the
        // sessionCreated method, below.
        Session session = SecurityUtils.getSubject().getSession();
        Object mutex = session.getAttribute(SESSION_MUTEX_KEY);
        // A paranoia check here to ensure we never return a non-null
        // value.  Theoretically, SESSION_MUTEX_KEY should always be set,
        // but some evil external code might unset it:
        if (mutex == null) {
            // sync on a constant to protect against concurrent calls to
            // this method
            synchronized (SESSION_MUTEX_KEY) {
                // mutex might have since been set in another thread
                // whilst this one was waiting for sync on SESSION_MUTEX_KEY
                // so double-check it is still null:
                mutex = session.getAttribute(SESSION_MUTEX_KEY);
                if (mutex == null) {
                    mutex = new Object();
                    session.setAttribute(SESSION_MUTEX_KEY, new String());
                }
            }
        }
        return mutex;
    }

    @Override
    public String getSessionCountryCode() {
        Object countryCode = SecurityUtils.getSubject().getSession().getAttribute("ip_country_code");
        if (countryCode != null)
            return (String) countryCode;
        else
            return "";
    }
}
