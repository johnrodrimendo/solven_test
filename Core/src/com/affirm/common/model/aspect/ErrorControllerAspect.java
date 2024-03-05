package com.affirm.common.model.aspect;

import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.service.ErrorService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.SqlErrorMessageException;
import com.affirm.security.model.StaticRegistry;
import com.codahale.metrics.Timer;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import java.util.Locale;

/**
 * Created by john on 07/10/16.
 */
@Aspect
@Component
public class ErrorControllerAspect {

    private static Logger logger = Logger.getLogger(ErrorControllerAspect.class);

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ErrorService errorService;

    @Pointcut("execution(* com.affirm.common.controller.*.*(..))")
    public void pointCutCommon() {
    }

    @Pointcut("execution(* com.affirm.client.controller.*.*(..)) " +
            "|| execution(* com.affirm.acquisition.controller.*.*(..)) " +
            "|| execution(* com.affirm.acquisition.controller.questions.*.*(..)) " +
            "|| execution(* com.affirm.landing.controller.*.*(..)) " +
            "|| execution(* com.affirm.companyExt.controller.*.*(..)) " +
            "|| execution(* com.affirm.entityExt.controller.*.*(..))")
    public void pointCutClient() {
    }

    @Pointcut("execution(* com.affirm.backoffice.controller.*.*(..))")
    public void pointCutBackoffice() {
    }

    @Around("(pointCutCommon() || pointCutClient() || pointCutBackoffice()) && @annotation(annotation)")
    public Object WebErrorAction(ProceedingJoinPoint jp, ErrorControllerAnnotation annotation) throws Throwable {

        // Register Metrics
        StaticRegistry.appRequestsMeter.mark();
        final Timer.Context timeContext = StaticRegistry.appControllerTime.time();

        Object response = null;
        try {
            // Do action
            response = jp.proceed();
        } catch (Throwable throwable) {
            // Metrics
            StaticRegistry.appErrorsMeter.mark();

            // notify error only if its an non controlled exception
            if (!(throwable instanceof SqlErrorMessageException)) {
                errorService.onError(throwable);
            }

            // Get the locale and modelmap instance from the method
            Locale locale = null;
            ModelMap modelMap = null;
            for (Object arg : jp.getArgs()) {
                if (arg instanceof Locale) {
                    locale = (Locale) arg;
                }
                if (arg instanceof ModelMap) {
                    modelMap = (ModelMap) arg;
                }
            }

            // Get the error message from the throwable
            String errorMessage = null;
            if (locale != null) {
                if (throwable instanceof SqlErrorMessageException) {
                    if (((SqlErrorMessageException) throwable).getMessageBody() != null) {
                        errorMessage = ((SqlErrorMessageException) throwable).getMessageBody();
                    } else if (((SqlErrorMessageException) throwable).getMessageKey() != null) {
                        errorMessage = messageSource.getMessage(((SqlErrorMessageException) throwable).getMessageKey(), null, locale);
                    }
                } else {
                    errorMessage = messageSource.getMessage("system.error.default", null, locale);
                }
            }

            // Return it to the client
            if (annotation.responseType() == ErrorControllerAnnotation.ResponseType.HTML) {
                if (modelMap != null)
                    modelMap.addAttribute("errorMessage", errorMessage);
                response = "500";
            } else if (annotation.responseType() == ErrorControllerAnnotation.ResponseType.JSON) {
                response = AjaxResponse.errorMessage(errorMessage);
            } else {
                response = null;
            }
        } finally {
            timeContext.stop();
            return response;
        }
    }
}