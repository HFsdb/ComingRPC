package com.gmc.server.info;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MetaData implements Serializable {
    /**
     * 地址 -> 127.0.0.1:2181
     */
    private String address;

    /**
     * 服务列表 -> [helloServiceName,version]
     */
    private List<ServiceInfo> serviceInfoList;

    private static final long seriaVersionID = 12345678L;
}
