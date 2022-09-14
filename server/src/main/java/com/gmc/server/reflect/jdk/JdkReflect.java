package com.gmc.server.reflect.jdk;

import com.gmc.server.protocol.Request;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class JdkReflect {

    public static Object request2Bean(Request request, Object bean) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Class<?> serviceClass = bean.getClass();
        log.info("服务类:{}",serviceClass);
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] params = request.getParams();
        String version = request.getVersion();
        Method method = serviceClass.getMethod(methodName,parameterTypes);
        method.setAccessible(true);
        log.info("JDK反射执行服务方法");
        return method.invoke(bean,params);
    }
}
