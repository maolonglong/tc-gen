package com.csl.tcg.annotation;

import java.lang.annotation.*;

/**
 * @author MaoLongLong
 * @date 2020-12-01 15:08
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@Documented
public @interface Prefix {

    String value() default "";

}
