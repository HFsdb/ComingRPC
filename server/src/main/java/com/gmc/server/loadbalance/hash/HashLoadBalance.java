package com.gmc.server.loadbalance.hash;

import com.gmc.server.info.MetaData;
import com.gmc.server.loadbalance.AbstractLoadBalance;
import com.gmc.server.protocol.RpcRequest;
import com.google.common.hash.Hashing;

import java.util.List;

public class HashLoadBalance extends AbstractLoadBalance {
    @Override
    protected MetaData executeLoadBalance(List<MetaData> serviceAddress, RpcRequest request) {
        String key = request.getClassName() + "#" + request.getVersion();
        int index = Hashing.consistentHash(key.hashCode(),serviceAddress.size());
        return serviceAddress.get(index);
    }
}
