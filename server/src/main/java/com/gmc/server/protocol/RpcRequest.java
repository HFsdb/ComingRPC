package com.gmc.server.protocol;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RpcRequest {
    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] params;
    private String version;
}
