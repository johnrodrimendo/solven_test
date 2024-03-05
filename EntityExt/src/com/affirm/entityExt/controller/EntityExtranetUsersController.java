package com.affirm.entityExt.controller;

import com.affirm.client.model.form.RegisterEntityUserRolesForm;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.common.dao.EntityExtranetDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.EntityBranding;
import com.affirm.common.model.catalog.EntityUserType;
import com.affirm.common.model.transactional.UserEntity;
import com.affirm.common.service.BrandingService;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.SqlErrorMessageException;
import com.google.gson.Gson;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Controller("entityExtranetUsersController")
public class EntityExtranetUsersController {

    public static final String URL = "users";

    private EntityExtranetService entityExtranetService;
    private EntityExtranetDAO entityExtranetDAO;
    private CatalogService catalogService;
    private UtilService utilService;
    private UserDAO userDAO;
    private BrandingService brandingService;

    @Autowired
    public EntityExtranetUsersController(EntityExtranetService entityExtranetService, EntityExtranetDAO entityExtranetDAO, CatalogService catalogService, UtilService utilService, UserDAO userDAO, BrandingService brandingService) {
        this.entityExtranetService = entityExtranetService;
        this.entityExtranetDAO = entityExtranetDAO;
        this.catalogService = catalogService;
        this.utilService = utilService;
        this.userDAO = userDAO;
        this.brandingService = brandingService;
    }

    @RequestMapping(value = "/" + URL, method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:user:managementv2", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showEntityClusters(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        model.addAttribute("users", userDAO.getEntityUsers(entityExtranetService.getPrincipalEntity().getId()));
        return new ModelAndView("/entityExtranet/extranetUsers");
    }

    @RequestMapping(value = {"/" + URL + "/{userId}", "/" + URL + "/create"}, method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showUserEdition(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable(value = "userId", required = false) Integer entityUserId
    ) throws Exception {

        if (entityUserId != null) {
            if (!SecurityUtils.getSubject().isPermitted("user:management:edit"))
                return AjaxResponse.errorForbidden();
        } else {
            if (!SecurityUtils.getSubject().isPermitted("user:management:create"))
                return AjaxResponse.errorForbidden();
        }

        RegisterEntityUserRolesForm form = new RegisterEntityUserRolesForm();
        UserEntity userEntity = null;

        if (entityUserId != null) {
            userEntity = userDAO.getEntityUserById(entityUserId, locale);
            if (userEntity == null)
                return "404";

            if (userEntity.getFirstSurname() != null) {
                form.setName(userEntity.getName());
                form.setFirstSurname(String.format("%s %s", userEntity.getFirstSurname() != null ? userEntity.getFirstSurname() : "", userEntity.getLastSurname() != null ? userEntity.getLastSurname() : "").trim());
            } else {
                form.setName(userEntity.getFullName());
            }

            form.setId(userEntity.getId());
            form.setEmail(userEntity.getEmail());
            form.setRoleId(userEntity.getEntityUserType() != null ? userEntity.getEntityUserType().getId() : null);
            form.setOrganizerId(userEntity.getHierarchyLevel3());
            form.setProducerId(userEntity.getHierarchyLevel2());
            form.setIdInEntity(userEntity.getEntityUserIdFromEntity());
            form.setChannelId(userEntity.getEntityAcquisitionChannelId());
        }

        List<EntityUserType> entityUserTypes = catalogService.getEntityUserTypes();
        entityUserTypes.sort(Comparator.comparing(EntityUserType::getEntityUserType));

        model.addAttribute("entityUserTypes", entityUserTypes);
        model.addAttribute("user", userEntity);
        model.addAttribute("form", form);
        return new ModelAndView("/entityExtranet/extranetUsersMaintenance");
    }

    @RequestMapping(value = "/" + URL + "/create", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object saveEntityUserWithRoles(
            Locale locale,
            RegisterEntityUserRolesForm userForm
    ) throws Exception {

        if (userForm.getId() != null) {
            if (!SecurityUtils.getSubject().isPermitted("user:management:edit"))
                return AjaxResponse.errorForbidden();
        } else {
            if (!SecurityUtils.getSubject().isPermitted("user:management:create"))
                return AjaxResponse.errorForbidden();
        }

        userForm.getValidator().validate(locale);
        if (userForm.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(userForm.getValidator().getErrorsJson());
        }

        if (userForm.getRoleId() == EntityUserType.BDS_USUARIO) {
            userForm.setOrganizerId(null);
        } else if (userForm.getRoleId() == EntityUserType.BDS_PRODUCTOR) {
            userForm.setProducerId(null);
        }

        if (userForm.getRoleId() != EntityUserType.BDS_USUARIO && userForm.getRoleId() != EntityUserType.BDS_PRODUCTOR && userForm.getRoleId() != EntityUserType.BDS_ORGANIZADOR) {
            userForm.setChannelId(null);
        }

        try {
            entityExtranetService.createOrUpdateEntityuser(userForm);
        } catch (SqlErrorMessageException e) {
            return AjaxResponse.errorMessage(e.getMessageBody());
        }

        return AjaxResponse.ok("");
    }

    @RequestMapping(value = "/" + URL + "/resendPassword", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "user:management:resendPassword", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object resetCredentials(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("email") String email) throws Exception {
        EntityBranding entityBranding = brandingService.getEntityBranding(request);
        UserEntity userEntity = userDAO.getUserEntityByEmail(email, locale, entityBranding != null ? entityBranding.getEntity().getId() : null);
        if (userEntity == null)
            return AjaxResponse.errorMessage("El email no está asociado a algun usuario.");

        entityExtranetService.sendResetPasswordEmail(email, locale, null);
        return AjaxResponse.ok("");
    }

    @RequestMapping(value = "/" + URL + "/updateActiveUser", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "user:management:activate", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object userActiveUpdate(
            ModelMap modelMap, Locale locale,
            @RequestParam("valueToUpdate") String value,
            @RequestParam("entityUserId") Integer entityUserId) throws Exception {
        boolean valueToUpdate = Boolean.parseBoolean(value);
        entityExtranetService.activateExtranetEntityUser(entityUserId, valueToUpdate);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/" + URL + "/roleByChannel", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "user:management:activate", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object filterUsersOfRoleByChannel(
            Locale locale,
            @RequestParam(value = "entityUserId", required = false) Integer entityUserId,
            @RequestParam("channelId") int channelId,
            @RequestParam("roleId") int roleId) throws Exception {
        List<UserEntity> entityUsers = userDAO.getEntityUsers(entityExtranetService.getPrincipalEntity().getId());
        UserEntity userEntity = null;

        if (entityUserId != null) {
            userEntity = userDAO.getEntityUserById(entityUserId, locale);
            if (userEntity == null)
                return AjaxResponse.errorMessage("No existe el usuario");
        }

        entityUsers = removeUserEntityFromEntityUsers(userEntity, entityUsers);

        Comparator<UserEntity> comparator = Comparator
                .comparing(UserEntity::getFirstSurname, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(UserEntity::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

        switch(roleId) {
            case EntityUserType.BDS_PRODUCTOR: {
                entityUsers = entityUsers.stream()
                        .filter(u -> u.getEntityUserType() != null && u.getEntityUserType().getId() == EntityUserType.BDS_ORGANIZADOR)
                        .sorted(comparator)
                        .collect(Collectors.toList());
                break;
            }
            case EntityUserType.BDS_USUARIO: {
                entityUsers = entityUsers.stream()
                        .filter(u -> u.getEntityUserType() != null && Arrays.asList(EntityUserType.BDS_PRODUCTOR, EntityUserType.BDS_ORGANIZADOR).contains(u.getEntityUserType().getId()))
                        .sorted(comparator)
                        .collect(Collectors.toList());
                break;
            }
        }

//        entityUsers = entityUsers.stream()
//                .filter(u -> Objects.nonNull(u.getEntityAcquisitionChannelId()))
//                .filter(u -> channelId == u.getEntityAcquisitionChannelId())
//                .collect(Collectors.toList());

        return AjaxResponse.ok(new Gson().toJson(entityUsers));
    }

    private List<UserEntity> removeUserEntityFromEntityUsers(UserEntity userEntity, List<UserEntity> entityUsers) {
        if (userEntity != null && (userEntity.getEntityUserType().getId() == EntityUserType.BDS_PRODUCTOR || userEntity.getEntityUserType().getId() == EntityUserType.BDS_ORGANIZADOR))
            return entityUsers.stream().filter(e -> !e.getId().equals(userEntity.getId())).collect(Collectors.toList());

        return entityUsers;
    }

}
