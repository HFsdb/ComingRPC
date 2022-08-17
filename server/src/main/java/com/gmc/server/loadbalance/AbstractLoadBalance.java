package com.gmc.server.loadbalance;

import com.gmc.server.info.MetaData;
import com.gmc.server.protocol.RpcRequest;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance{

    @Override
    public MetaData distribute(List<MetaData> serviceAddress, RpcRequest request){
        if(serviceAddress == null || serviceAddress.isEmpty()){
            return null;
        }
        if(serviceAddress.size() == 1){
            System.out.println(serviceAddress.get(0));
            return serviceAddress.get(0);
        }
        return executeLoadBalance(serviceAddress,request);
    }

    protected abstract MetaData executeLoadBalance(List<MetaData> serviceAddress,RpcRequest request);

}
