package com.affirm.client.model.aspect;

import com.affirm.client.model.annotation.ErrorRestControllerAnnotation;
import com.affirm.client.util.ApiWsResponse;
import com.affirm.common.service.ErrorService;
import com.affirm.common.util.SqlErrorMessageException;
import com.affirm.security.model.StaticRegistry;
import com.affirm.system.configuration.Configuration;
import com.codahale.metrics.Timer;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Created by john on 07/10/16.
 */
@Aspect
@Component
public class ErrorControllerRestAspect {

    private static Logger logger = Logger.getLogger(ErrorControllerRestAspect.class);

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ErrorService errorService;

    @Pointcut("execution(* com.affirm.client.controller.rest.*.*(..)) " +
            "|| execution(* com.affirm.acquisition.controller.rest.*.*(..)) ")
    public void pointCutClient() {
    }

    @Around("(pointCutClient()) && @annotation(annotation)")
    public Object WebErrorAction(ProceedingJoinPoint jp, ErrorRestControllerAnnotation annotation) throws Throwable {

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
            Locale locale = Configuration.getDefaultLocale();
            for (Object arg : jp.getArgs()) {
                if (arg instanceof Locale) {
                    locale = (Locale) arg;
                }
            }

            // Get the error message from the throwable
            String errorMessage = null;

            // When REST, should not be sending the error message to the client
//            if (throwable instanceof SqlErrorMessageException) {
//                SqlErrorMessageException sqlExc = ((SqlErrorMessageException) throwable);
//                errorMessage = sqlExc.getMessageBody() != null ? sqlExc.getMessageBody() : (sqlExc.getMessageKey() != null ? messageSource.getMessage(sqlExc.getMessageKey(), null, locale) : null);
//            }

            // Return it to the client
            response = ApiWsResponse.error(null, errorMessage);
        } finally {
            timeContext.stop();
        }
        return response;
    }
}