package com.affirm.common.model.aspect;

import com.affirm.common.dao.EntityExtranetDAO;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.WebApplication;
import com.affirm.common.model.transactional.UserEntity;
import com.affirm.common.util.AjaxResponse;
import com.affirm.security.dao.SecurityDAO;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by john on 07/10/16.
 */
@Aspect
@Component
public class RequirePermissionOr403Aspect {

    private static Logger logger = Logger.getLogger(RequirePermissionOr403Aspect.class);

    @Autowired
    private EntityExtranetDAO entityExtranetDao;
    @Autowired
    private SecurityDAO securityDao;

    @Pointcut("execution(* com.affirm.backoffice.controller.*.*(..))")
    public void pointCutBackoffice() {
    }

    @Pointcut("execution(* com.affirm.client.controller.*.*(..))")
    public void pointCutClient() {
    }

    @Pointcut("execution(* com.affirm.entityExt.controller.*.*(..))")
    public void pointCutExtranet() {
    }

    @Around("(pointCutBackoffice() || pointCutClient() || pointCutExtranet()) && @annotation(annotation)")
    public Object permissionAction(ProceedingJoinPoint jp, RequiresPermissionOr403 annotation) throws Throwable {
        if (!SecurityUtils.getSubject().isPermittedAll(annotation.permissions())) {
            logger.warn("The user doesnt have the permission to access this site");
            if (annotation.type() == RequiresPermissionOr403.Type.WEB) {
                return new ModelAndView("403");
            } else {
                return AjaxResponse.errorForbidden();
            }
        } else {
            Object response = jp.proceed();
            if (annotation.saveLog()) {
                if (annotation.webApp() == -1)
                    throw new Exception("The annotation doesnt have a webapp seted for saving log.");

                if (response instanceof ResponseEntity) {
                    if (((ResponseEntity) response).getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                        return response;
                    }
                }

                switch (annotation.webApp()) {
                    case WebApplication.ENTITY_EXTRANET: {
                        UserEntity loggedUser = ((UserEntity) SecurityUtils.getSubject().getPrincipal());
                        for (int i = 0; i < annotation.permissions().length; i++) {
                            entityExtranetDao.registerLogActivity(annotation.permissions()[i], loggedUser.getId());
                        }
                        break;
                    }
                    default:
                        throw new Exception("No save log configured for the webapp " + annotation.webApp());
                }
            }
            return response;
        }
    }
}
