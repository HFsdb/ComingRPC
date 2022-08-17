package com.gmc.server.annotation;

import com.gmc.server.loadbalance.pooling.PoolingLoadBalance;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Consumer {
    String version() default "";

    Class<?> loadBalance() default PoolingLoadBalance.class;

    long timeout() default 3000L;
}
