package com.gmc.server.proxy.jdk;

import com.gmc.server.protocol.Request;
import com.gmc.server.protocol.Response;
import com.gmc.server.loadbalance.LoadBalance;
import com.gmc.server.netty.NettyClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ClientProxy implements InvocationHandler {
    private String version;
    private LoadBalance loadBalance;
    private long timeout;

    public ClientProxy(String version, LoadBalance loadBalance, long timeout){
        this.version = version;
        this.loadBalance = loadBalance;
        this.timeout = timeout;
    }
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //封装RpcReques
        log.info("反射方法名[{}]", method.getName());

        Request request = new Request();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setVersion(version);
        request.setParams(args);
        //String key =  request.getRequestId() + "#" + request.getVersion();
        CompletableFuture<Response> future = (CompletableFuture<Response>) new NettyClient().sendRequest(request,loadBalance);
        log.info("得到future，等待返回结果......");
        Response response = future.get();
        return response.getResult();
    }
}

