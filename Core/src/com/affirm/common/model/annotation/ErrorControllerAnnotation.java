package com.affirm.common.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by john on 07/10/16.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ErrorControllerAnnotation {

    // TODO Change for CallerType -> AJAX,WEB
    enum ResponseType {
        JSON, HTML, OTHER
    }

    ResponseType responseType() default ResponseType.HTML;

}
