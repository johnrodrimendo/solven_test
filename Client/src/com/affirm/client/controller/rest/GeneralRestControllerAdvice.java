package com.affirm.client.controller.rest;

import com.affirm.common.service.impl.ErrorServiceImpl;
import com.affirm.common.util.AjaxResponse;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Calendar;

/**
 * Created by john on 05/12/16.
 */
@ControllerAdvice
public class GeneralRestControllerAdvice {

    private static Logger logger = Logger.getLogger(GeneralRestControllerAdvice.class);

    @ModelAttribute("currentYear")
    public String currentYear() {
        return String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)
    public Object maximunUploadFileSize(Throwable exception, WebRequest request) {
        try{
            logger.error("maximunUploadFileSize: " + request != null && request.getParameterMap() != null ? new Gson().toJson(request.getParameterMap()) : null, exception);
        }catch (Exception ex){
        }
        if (isRest(request)) {
            return ResponseEntity.status(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED).body("");
        } else {
            return AjaxResponse.errorMessage("El archivo tiene un tamaño más grande que el permitido");
        }
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handle404(Throwable exception, WebRequest request) {
        try{
            logger.error("handle404: " + request != null && request.getParameterMap() != null ? new Gson().toJson(request.getParameterMap()) : null, exception);
        }catch (Exception ex){
        }
        if (isRest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        } else {
            return new ModelAndView("404");
        }
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Object handle405(Throwable exception, WebRequest request) {
        try{
            logger.error("handle405: " + request != null && request.getParameterMap() != null ? new Gson().toJson(request.getParameterMap()) : null, exception);
        }catch (Exception ex){
        }
        if (isRest(request)) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("");
        } else {
            return new ModelAndView("404");
        }
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handle400(Throwable exception, WebRequest request) {
        try{
            logger.error("handle400: " + request != null && request.getParameterMap() != null ? new Gson().toJson(request.getParameterMap()) : null, exception);
        }catch (Exception ex){
        }
        if (isRest(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        } else {
            return new ModelAndView("404");
        }
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleGeneral(Throwable exception, WebRequest request) {
        ErrorServiceImpl.onErrorStatic(exception);
        if (isRest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        } else {
            return new ModelAndView("500");
        }
    }

    private boolean isRest(WebRequest request) {
        try {
            return ((ServletWebRequest) request).getRequest().getRequestURI().startsWith("/api");
        } catch (Throwable ex) {
        }
        return false;
    }
}
