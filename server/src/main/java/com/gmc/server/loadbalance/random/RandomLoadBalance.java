package com.gmc.server.loadbalance.random;

import com.gmc.server.info.MetaData;
import com.gmc.server.loadbalance.AbstractLoadBalance;
import com.gmc.server.protocol.Request;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected MetaData executeLoadBalance(List<MetaData> serviceAddress, Request request) {
        Random random = new Random();
        return serviceAddress.get(random.nextInt(serviceAddress.size()));
    }
}
