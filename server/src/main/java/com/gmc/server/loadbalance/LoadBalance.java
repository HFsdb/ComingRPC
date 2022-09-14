package com.gmc.server.loadbalance;

import com.gmc.server.info.MetaData;
import com.gmc.server.protocol.Request;

import java.util.List;

public interface LoadBalance {
    MetaData distribute(List<MetaData> serviceAddress, Request request);
}
