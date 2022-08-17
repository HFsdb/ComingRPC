package com.gmc.server.info;

import lombok.Data;

import java.util.List;

@Data
public class MetaData {
    /**
     * 地址 -> 127.0.0.1:2181
     */
    private String address;

    /**
     * 服务列表 -> [helloServiceName,version]
     */
    private List<ServiceInfo> serviceInfoList;
}
