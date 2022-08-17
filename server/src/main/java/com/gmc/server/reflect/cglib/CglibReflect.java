package com.gmc.server.reflect.cglib;

import com.gmc.server.protocol.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

@Slf4j
public class CglibReflect {

    public static FastMethod request2Bean(RpcRequest request, Object bean){
        Class<?> serviceClass = bean.getClass();
        log.info("服务类:{}",serviceClass);
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] params = request.getParams();
        String version = request.getVersion();
        //CGLIB
        FastClass fastClass = FastClass.create(serviceClass);
        FastMethod fastMethod = fastClass.getMethod(methodName,parameterTypes);
        log.info("CGLIB反射执行服务方法");
        return fastMethod;
    }
}
