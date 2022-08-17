package com.gmc.server.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Provider {
    Class<?> value();

    String version() default "";

    int coreThreadPoolSize() default 30;

    int maxThreadPoolSize() default 60;
}
