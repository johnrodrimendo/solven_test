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
public @interface RequiresPermissionOr403 {

    public enum Type {
        AJAX, WEB
    }

    String[] permissions() default "";

    Type type() default Type.WEB;

    boolean saveLog() default false;

    int webApp() default -1;

}
