package com.affirm.client.controller;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * @author jrodriguez
 */
@Controller
@Scope("request")
public class OauthController {

    private static Logger logger = Logger.getLogger(OauthController.class);

    @RequestMapping(value = "/oauth/response/{network}", method = RequestMethod.GET)
    public String responseTwitter(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable String network) {
        switch (network) {
            case "linkedin":
                model.addAttribute("code", request.getParameter("code"));
                break;
            case "facebook":
                model.addAttribute("code", request.getParameter("code"));
                break;
            case "google":
                model.addAttribute("code", request.getParameter("code"));
                break;
            case "windows":
                model.addAttribute("code", request.getParameter("code"));
                break;
            case "yahoo":
                model.addAttribute("code", request.getParameter("code"));
                break;
            case "mercadolibre":
                model.addAttribute("code", request.getParameter("code"));
                break;
        }
        return "oauthResponse";
    }
}
