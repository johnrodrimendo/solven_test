package com.affirm.backoffice.controller;

import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.backoffice.service.SysUserService;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.service.InteractionService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.Util;
import com.affirm.security.model.SysUser;
import com.affirm.security.service.TokenAuthService;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author jrodriguez
 */
@Controller
@Scope("request")
public class SysUserController {

    private static Logger logger = Logger.getLogger(SysUserController.class);

    @Autowired
    SysUserService sysUserService;

    @Autowired
    TokenAuthService tokenAuthService;

    @Autowired
    InteractionService interactionService;





    /**
     * Sets token generator on session and sends the shared secret to the user for vinculation
     */
    @RequestMapping(value = "/profile/newQr", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> newQr(ModelMap model, Locale locale) throws Exception {
        String s;
        Map.Entry<String, JSONArray> credentials = tokenAuthService.newTokenCredentials();
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("new_profile_secret", credentials.getKey());
//        session.setAttribute("new_profile_scratchs", credentials.getValue());
        s = credentials.getKey();
        return AjaxResponse.ok(s);
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String getProfile(ModelMap model, Locale locale) throws Exception {
        String username = ((SysUser) SecurityUtils.getSubject().getPrincipal()).getUserName();
        Subject subject = SecurityUtils.getSubject();
        SysUser sysUser = (SysUser) subject.getPrincipal();

        SimpleDateFormat spd = new SimpleDateFormat("dd/MM/yyyy");
        String roles = sysUserService.getCommaSeparatedRoleNames(sysUser.getId());

        boolean active = sysUser.getActive() == true ? true : false;
        Date cessation = sysUser.getCessationDate();
        if (cessation != null) {
            if (cessation.after(new Date())) {
                active = false;
            }
        }
        model.addAttribute("username", username);
        model.addAttribute("active", active == true ? "Activo" : "Inactivo");
        model.addAttribute("roles", roles);
        model.addAttribute("email", sysUser.getEmail());
        return "profile";
    }

    @RequestMapping(value = "/profile/vinculateQr", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> associateQr(ModelMap model, Locale locale, @RequestParam String token) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        SysUser sysUser = (SysUser) subject.getPrincipal();
        Session session = subject.getSession();
        String secret = (String) session.getAttribute("new_profile_secret");
//        JSONArray scratchs = (JSONArray) session.getAttribute("new_profile_scratchs");
        boolean success = tokenAuthService.vinculate(sysUser, secret, null, Util.intOrNull(token));
        String mensaje;
        if (success) {
            mensaje = "El nuevo qr para token quedó asociado a la cuenta.";
        } else {
            mensaje = "Token incorrecto. Inténtelo de nuevo";
            return AjaxResponse.errorMessage(mensaje);
        }
        return AjaxResponse.ok(mensaje);
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "sysuser:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String getUsers(ModelMap model, Locale locale) throws Exception {
        List<SysUser> users=sysUserService.getUsers();
        model.addAttribute("users",users);
        return "users";
    }


    @RequestMapping(value = "/user/activate", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "sysuser:unlock", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String activateUser(ModelMap model, Locale locale,
    @RequestParam(value = "userId", required = true) Integer userId
    ) throws Exception {
        sysUserService.activateSysUserById(userId);
        List<SysUser> users=sysUserService.getUsers();
        model.addAttribute("users",users);
        return "users";
    }


    @RequestMapping(value = "/user/resetPassword", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "sysuser:resetPassword", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object resetCredentials(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("user") String user,
            @RequestParam("email") String email
            ) throws Exception {

        if (email == null)
            return AjaxResponse.errorMessage("Debe colocar un email asociado a una cuenta.");

        if (user == null)
            return AjaxResponse.errorMessage("Debe colocar un usuario asociado a una cuenta.");

        sysUserService.generatePasswordResetEmail(user);
        return AjaxResponse.ok(null);
    }

}