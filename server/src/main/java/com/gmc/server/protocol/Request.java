package com.gmc.server.protocol;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class Request {
    private final long requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] params;
    private String version;

    private static final AtomicLong al = new AtomicLong(0);

    public Request(){
        requestId = newId();
    }
    public Request(long requestId){
        this.requestId = requestId;
    }
    private long newId(){
        return al.getAndIncrement();
    }

    public long getRequestId() {
        return requestId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
