package com.gmc.server.info;

import lombok.Data;

import java.io.Serializable;

@Data
public class ServiceInfo implements Serializable {
    /**
     * 服务名 -> helloService
     */
    private String serviceName;

    /**
     * 版本号
     */
    private String version;

    private static final long seriaVersionID = 1234567L;
}
