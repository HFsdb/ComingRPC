package com.gmc.server.info;

import lombok.Data;

@Data
public class ServiceInfo {
    /**
     * 服务名 -> helloService
     */
    private String serviceName;

    /**
     * 版本号
     */
    private String version;
}
