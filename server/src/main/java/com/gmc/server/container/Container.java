package com.gmc.server.container;

import com.gmc.server.discovery.Discovery;
import com.gmc.server.discovery.zookeeper.ZKDiscovery;
import com.gmc.server.discovery.zookeeper.zkclient.CuratorClient;
import com.gmc.server.info.MetaData;
import com.gmc.server.info.ServiceInfo;
import com.gmc.server.netty.NettyClient;
import com.gmc.server.netty.handler.NettyServerHandler;
import com.gmc.server.register.Register;
import com.gmc.server.register.zookeeper.ZKRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class Container {
    /**
     * map中的键是地址 + "#" + 版本号 -> 127.0.0.1:2181#1.0
     * map中的值是服务名 -> serviceImp
     */
    public Map<String,Object> beanMap = new ConcurrentHashMap<>();
    public CopyOnWriteArraySet<MetaData> metaDataSet = new CopyOnWriteArraySet<>();

    public Container(){}

    public void putBeanMap(String key,Object o){beanMap.put(key,o);}
    public Map<String, Object> getBeanMap(){
        return beanMap;
    }

    public void putMetaData(MetaData metaData){
        metaDataSet.add(metaData);
    }
    public CopyOnWriteArraySet<MetaData> getMetaDataSet(){return metaDataSet;}



    public List<ServiceInfo> getContainerServiceInfo(){
        List<ServiceInfo> serviceInfoList = new ArrayList<>();
        for(String key : beanMap.keySet()){
            String[] subkey= key.split("#");
            if(subkey.length > 0){
                ServiceInfo serviceInfo = new ServiceInfo();
                serviceInfo.setServiceName(subkey[0]);
                if(subkey.length > 1){
                    serviceInfo.setVersion(subkey[1]);
                }else{
                    serviceInfo.setVersion("");
                }
                serviceInfoList.add(serviceInfo);
            }else{
                log.warn("存在不合法键值对");
            }
        }
        return serviceInfoList;
    }

}
