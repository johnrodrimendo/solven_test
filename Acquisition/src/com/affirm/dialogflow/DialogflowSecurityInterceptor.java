package com.affirm.dialogflow;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by john on 18/11/16.
 */
@Component
public class DialogflowSecurityInterceptor extends HandlerInterceptorAdapter {

    private static Logger logger = Logger.getLogger(DialogflowSecurityInterceptor.class);

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // Get the signature
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String decoded = new String(Base64.decodeBase64(authHeader));
        if (decoded.split(":").length != 2) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String source = decoded.split(":")[0];
        String signature = decoded.split(":")[1];

        if(source.equals("dialogflow") && signature.equals("6w_G&?gMK!")){
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}
