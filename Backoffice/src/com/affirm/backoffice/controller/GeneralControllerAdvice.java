package com.affirm.backoffice.controller;

import com.affirm.common.service.impl.ErrorServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Calendar;

/**
 * Created by john on 05/12/16.
 */
@ControllerAdvice
public class GeneralControllerAdvice {

    @ModelAttribute("currentYear")
    public String currentYear() {
        return String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handle404() {
        return new ModelAndView("404");
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleGeneral(Throwable exception, WebRequest request) {
        ErrorServiceImpl.onErrorStatic(exception);
        return new ModelAndView("500");
    }

}
