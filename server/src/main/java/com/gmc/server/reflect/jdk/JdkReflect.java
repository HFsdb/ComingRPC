package com.gmc.server.reflect.jdk;

import com.gmc.server.protocol.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

@Slf4j
public class JdkReflect {

    public static Object request2Bean(RpcRequest request,Object bean) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
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
