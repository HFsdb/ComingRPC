package com.gmc.server.loadbalance.pooling;

import com.gmc.server.info.MetaData;
import com.gmc.server.protocol.Request;
import com.gmc.server.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PoolingLoadBalance extends AbstractLoadBalance {

    private final Map<MetaData, Integer> map = new ConcurrentHashMap<>();

    @Override
    protected MetaData executeLoadBalance(List<MetaData> serviceAddress, Request request) {
        Integer integer = map.get(serviceAddress.get(0));
        if(integer == null){
            integer = 0;
        }else{
            integer = (integer + 1) % (serviceAddress.size());
        }
        map.put(serviceAddress.get(0),integer);
        return serviceAddress.get(integer);
    }
}
