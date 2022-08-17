package com.gmc.server.discovery.zookeeper;

import com.gmc.server.config.Config;
import com.gmc.server.container.Container;
import com.gmc.server.discovery.Discovery;
import com.gmc.server.discovery.zookeeper.zkclient.CuratorClient;
import com.gmc.server.factory.SingletonFactory;
import com.gmc.server.info.MetaData;
import com.gmc.server.info.ServiceInfo;
import com.gmc.server.netty.NettyClient;
import com.gmc.server.util.JsonUtil;
import com.gmc.server.util.ThreadUtil;
import com.google.common.hash.Hashing;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class ZKDiscovery implements Discovery {

//    private CuratorFramework client;
    private CuratorClient curatorclient;
    private NettyClient nettyClient;

    private Container container;

    public ZKDiscovery(){
        this.curatorclient = new CuratorClient();
        nettyClient = new NettyClient();
        container = SingletonFactory.getInstance(Container.class);
    }

    @Override
    public void discovery() throws Exception {
        List<String> childrenList = curatorclient.getZKNodes(Config.ZK_REGISTER_PARH.getValue());
        if (childrenList == null || childrenList.size() == 0) {
            throw new RuntimeException("无服务");
        }

        HashSet<MetaData> metaDataSet = new HashSet<>();//保存节点上的metadata列表
        for (String children : childrenList) {
            log.info("子节点: [{}].", children);
            byte[] bytes = curatorclient.getNodeData(Config.ZK_REGISTER_PARH.getValue() + "/" + children);
            String json = new String(bytes);
            MetaData metaData = JsonUtil.Json2Object(json, MetaData.class);
            metaDataSet.add(metaData);
        }
        log.info("得到服务数据：{}", metaDataSet);
        update(metaDataSet);
        curatorclient.watchNode(Config.ZK_REGISTER_PARH.getValue());
    }

    public void update(HashSet<MetaData> metaDataSet) throws ExecutionException, InterruptedException {
        if(metaDataSet != null && ! metaDataSet.isEmpty()){
            //比较节点上的服务集合和此次服务集合
            for(final MetaData metaData : metaDataSet){
                if(!container.getMetaDataSet().contains(metaData)){
                    connect(metaData);
                }
            }
            //节点没有该服务就移除此次服务集合的服务
            for(MetaData metaData : container.getMetaDataSet()){
                if(!metaDataSet.contains(metaData)){
                    remove(metaData);
                }
            }
        }else{
            System.out.println("无可用的服务");
//            for(MetaData metaData : metaDataSet){
//                nettyClient.close();
//            }
        }

    }

    public void connect(MetaData metaData) throws ExecutionException, InterruptedException {
        if(metaData.getServiceInfoList() == null || metaData.getServiceInfoList().isEmpty()){
            log.info("节点中无服务");
            return;
        }
        container.putMetaData(metaData);
        log.info("添加新的服务");
        for(ServiceInfo serviceInfo : metaData.getServiceInfoList()){
            log.info("新的服务信息,name:{},version:{}",serviceInfo.getServiceName(),serviceInfo.getVersion());
        }
        String host = metaData.getAddress().split(":")[0];
        int port = Integer.parseInt(metaData.getAddress().split(":")[1]);
        final InetSocketAddress address = new InetSocketAddress(host,port);
        Channel channel = nettyClient.connect(address);
    }

    public void remove(MetaData metaData){
        container.getMetaDataSet().remove(metaData);
    }

    @Override
    public void stop() {

    }
}
