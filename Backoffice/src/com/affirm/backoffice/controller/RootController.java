package com.affirm.backoffice.controller;

import com.affirm.backoffice.dao.BackofficeDAO;
import com.affirm.backoffice.service.BackofficeService;
import com.affirm.backoffice.service.SysUserService;
import com.affirm.backoffice.util.InactiveSysuserException;
import com.affirm.backoffice.util.LaboralScheduleException;
import com.affirm.backoffice.util.LoginFailedException;
import com.affirm.backoffice.util.MaxSessionSysuserException;
import com.affirm.common.dao.*;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.WebscrapperService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.security.model.SysUser;
import com.affirm.security.service.SecurityService;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Null;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import com.affirm.backoffice.model.ResetPasswordBackoffice;

/**
 * @author jrodriguez
 */
@Controller
@Scope("request")
public class RootController {
    private static Logger logger = Logger.getLogger(RootController.class);

    @RequestMapping(value = "/unauthorized", method = RequestMethod.GET)
    public String unauthorized() {
        return "403";
    }

    @Autowired
    private SecurityService securityService;
    @Autowired
    private BackofficeDAO backofficeDAO;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserDAO sysUserDAO;
    @Autowired
    private BackofficeService backofficeService;
    @Autowired
    private WebscrapperService webscrapperService;
    @Autowired
    private BotDAO botDAO;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showLogin(ModelMap model, Locale locale) {
//        if (Boolean.FALSE.equals(SecurityUtils.getSubject().getSession().getAttribute("countryUnlocked"))) {
//            return "redirect:/confirmHuman";
//        }
        //check if logged
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isAuthenticated()) {
            return "redirect:/profile";
        }
        //clean previous data
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("TFA_USR", null);
        session.setAttribute("TFA_PWD", null);
        return "login";
    }

    @RequestMapping(value = "/loginStep2", method = RequestMethod.GET)
    public String loginStep2(ModelMap model, Locale locale) {
        Session session = SecurityUtils.getSubject().getSession();
        String username = (String) session.getAttribute("TFA_USR");
        if (username != null) {
            model.addAttribute("username", username);
            return "tfaLogin";
        }
        return "redirect:/login";
    }

    @RequestMapping(value = "/loginX", method = RequestMethod.POST)
    public String login(ModelMap model, Locale locale, HttpServletRequest request,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password) {
        System.out.println("ENTRE AL POST BIEN");
        //reroutear a pagina anterior shiro tiene un savedRequest en session.
        //http://stackoverflow.com/questions/1921230/redirect-back-to-a-page-after-a-login
        try {
            if (backofficeService.isCountryUnlocked())
                return securityService.authenticate(username, password, false, request);
            System.out.println("El login ha sido bloqueado por país. No podrás iniciar sessión");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/loginX";
    }


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Object logintfa(
            ModelMap model, Locale locale, HttpServletRequest request, RedirectAttributes redirectAttributes,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(value = "rememberMe", required = false, defaultValue = "false") String rememberMe) throws Exception {

        if (backofficeService.isCountryUnlocked()) {
            System.out.println("info para loguear en server");

            try {
                if (securityService.isNotReadyAccount(username) && Configuration.hostEnvIsProduction()) {
                    sysUserService.disableUser(username);
                    return AjaxResponse.errorMessage("Su cuenta todavia no es valida.");
                }

                if (securityService.isExpiredPassword(username) && Configuration.hostEnvIsProduction()) {
                    sysUserService.generatePasswordResetEmail(username);
                    return AjaxResponse.errorMessage("Su contraseña esta vencida se envio un enlace a su cuenta de correo para configurar una nueva.");
                }

                String destiny = securityService.authenticate(username.trim(), password, false, request);
                SecurityUtils.getSubject().getSession().setAttribute("TFA_USR", username);

                return (destiny == null) ? "redirect:/" : AjaxResponse.redirect(Configuration.getBackofficeDomain() + destiny);
            } catch (NullPointerException ex) {
                return AjaxResponse.errorMessage("La combinación usuario y clave es incorrecta.");
            } catch (InactiveSysuserException ex) {
                return AjaxResponse.errorMessage(ex.getMessage());
            } catch (LoginFailedException ex) {
                return AjaxResponse.errorMessage(ex.getMessage());
            } catch (LaboralScheduleException ex) {
                return AjaxResponse.errorMessage(ex.getMessage());
            } catch(MaxSessionSysuserException ex){
                return AjaxResponse.errorMessage(ex.getMessage());
            }
        } else {
            Thread.sleep(500);
            System.out.println("El login ha sido bloqueado por país. No podrás iniciar sessión");
            return AjaxResponse.errorMessage("El login ha sido bloqueado por país. No podrás iniciar sessión");
        }
    }

    @RequestMapping(value = "/loginStep2", method = RequestMethod.POST)
    public Object check2factor(ModelMap model, @RequestParam String token, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        Session session = SecurityUtils.getSubject().getSession();
        String username = session.getAttribute("TFA_USR").toString();
        //String password = session.getAttribute("TFA_PWD").toString();

        String sharedSecret = backofficeDAO.getSharedSecret(username);

        String s = "";

        Integer itoken = 0;
        try {
            itoken = Integer.parseInt(token);
        } catch (Exception e) {
        }

        boolean correctToken = gAuth.authorize(CryptoUtil.decryptAuthSecret(username, sharedSecret), itoken);


        if (correctToken) {
            if (backofficeService.isCountryUnlocked()) {
                Subject loginAttempSubject = SecurityUtils.getSubject();
                SavedRequest savedRequest = ((SavedRequest) loginAttempSubject.getSession().getAttribute("shiroSavedRequest"));
                String redirect = "";
                if (savedRequest != null)
                    redirect = savedRequest.getRequestUrl().replaceFirst(request.getContextPath(), "");
                if (redirect.isEmpty())
                    redirect = "/profile";
                session.setAttribute("step2Success", "success");
                return AjaxResponse.redirect(Configuration.getBackofficeDomain() + redirect);
            } else {
                s = "El login ha sido bloqueado por país. No podrás iniciar sessión.";
                System.out.println(s);
                return AjaxResponse.errorMessage(s);
            }
        } else {
            s = "El token es incorrecto.";
            return AjaxResponse.errorMessage(s);
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object logout(HttpServletRequest request, HttpServletResponse response) {
        sysUserService.onLogout(Integer.parseInt(SecurityUtils.getSubject().getSession().getAttribute("boLoginId").toString()), new Date());
        SecurityUtils.getSubject().logout();
        /*for (int i = 0; i < request.getCookies().length; i++) {
            Cookie c = request.getCookies()[0];
            System.out.println(c.getName());
            c.setValue(null);
            c.setMaxAge(0);
            c.setPath(request.getContextPath());
            response.addCookie(c);
        }*/
        return AjaxResponse.redirect(request.getContextPath());
    }

    @RequestMapping(value = "/country", method = RequestMethod.POST)
    public void country(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
                        @RequestParam("country") Integer country,
                        @RequestParam("class") String cssClass) {
        backofficeService.setCountryActiveSysuser(country, cssClass);
    }

    @RequestMapping(value = "/500", method = RequestMethod.GET)
    public String erorPage(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        return "500";
    }

    @RequestMapping(value = "/404", method = RequestMethod.GET)
    public String notFoundPage(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        return "404";
    }


    @RequestMapping(value = "/sendToQueue", method = RequestMethod.GET)
    public String sendToQueue(ModelMap model, Locale locale) {

        List<QueryBot> queryBots = botDAO.getQueryBotQueue();

        for(QueryBot queryBot: queryBots) {
            webscrapperService.sendToQueue(queryBot.getId());
        }

        return "404";
    }

    @RequestMapping(value = "/resetSysUserPassword", method = RequestMethod.POST)
    public Object doReset(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("repassword") String repassword) throws Exception {

        if (!password.equals(repassword)) return AjaxResponse.errorMessage("Ambas contraseñas deben ser iguales.");

        if (password.length() < Configuration.MIN_PASSWORD_BACKOFFICE)
            return AjaxResponse.errorMessage("La contraseña debe contener por lo menos " + String.valueOf(Configuration.MIN_PASSWORD) + " caracteres.");

        Pattern pattern = Pattern.compile(StringFieldValidator.PATTER_REGEX_PASSWORD);
        if (!pattern.matcher(password).matches())
            return AjaxResponse.errorMessage("El password debe contener por lo menos un número, una letra y/o un caracter especial [!@#$%^&*_].");


        Gson gson = new Gson();
        ResetPasswordBackoffice resetPassword = gson.fromJson(CryptoUtil.decrypt(token), ResetPasswordBackoffice.class);

        if (resetPassword.getEmail().equals(password))
            return AjaxResponse.errorMessage("La contraseña no puede ser igual al usuario.");

        SysUser user = sysUserService.getSysUserByEmail(resetPassword.getEmail());
        if (!sysUserService.validPassword(user.getId(), password)) {
            if (Configuration.OLD_PASSWORDS_BACKOFFICE > 1)
                return AjaxResponse.errorMessage("La contraseña no puede ser igual a las últimas " + Configuration.OLD_PASSWORDS_BACKOFFICE + " contraseñas utilizadas.");
            else if (Configuration.OLD_PASSWORDS_BACKOFFICE == 1)
                return AjaxResponse.errorMessage("La contraseña no puede ser igual a las última contraseña utilizada.");
        }

        if (sysUserService.updateResetPassword(token, resetPassword.getEmail(), CryptoUtil.hashPassword(password)))
            return AjaxResponse.ok("La contraseña se actualizo correctamente");
        else
            return AjaxResponse.errorMessage("El token ha expirado");
    }

    @RequestMapping(value = "/resetPassword/{token}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String resetPassword(ModelMap model, Locale locale,
                                @PathVariable(value = "token", required = true) String resetToken
    ) throws Exception {
        String URL="backoffice";

        if (sysUserDAO.isResetPasswordTokenUsed(resetToken))
            return "redirect:/" + URL + "/login?message=" + CryptoUtil.encrypt("El token  para cambiar contraseña ha expirado.");

        Gson gson = new Gson();
        System.out.println(CryptoUtil.decrypt(resetToken));
        ResetPasswordBackoffice resetPassword = gson.fromJson(CryptoUtil.decrypt(resetToken), ResetPasswordBackoffice.class);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        calendar.setTime(sdf.parse(resetPassword.getExpireTime()));

        if (!calendar.getTime().after(new Date()))
            return "redirect:/" + URL + "/login?message=" + CryptoUtil.encrypt("El token  para cambiar contraseña ha expirado.");
        model.addAttribute("urlLogin", Configuration.getClientDomain().concat("/backoffice/login"));
        model.addAttribute("token", resetToken);

        return "sysUserResetPassword";
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object resetPassword(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("user") String user
    ) throws Exception {

        if (user == null)
            return AjaxResponse.errorMessage("Debe colocar un usuario asociado a una cuenta.");
        String email=sysUserService.generatePasswordResetEmail(user);
        if(email==null)
            return AjaxResponse.errorMessage("El usuario es incorrecto");
        return AjaxResponse.ok("Se envio un enlace a su cuenta de correo para configurar una nueva contraseña.");
    }

}