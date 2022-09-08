package com.gmc.server.register.zookeeper;

import com.gmc.server.config.Config;
import com.gmc.server.container.ServerContainer;
import com.gmc.server.discovery.zookeeper.zkclient.CuratorClient;
import com.gmc.server.factory.SingletonFactory;
import com.gmc.server.info.MetaData;
import com.gmc.server.info.ServiceInfo;
import com.gmc.server.register.Register;
import com.gmc.server.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.state.ConnectionState;

import java.util.HashMap;
import java.util.List;

@Slf4j
public class ZKRegister implements Register {
    private String address;
    private String zkPath;

    private ServerContainer container = SingletonFactory.getInstance(ServerContainer.class);

    private CuratorClient curatorClient = new CuratorClient();

    public ZKRegister(String address){
        this.address = address;
    }

    @Override
    public void register() {
        List<ServiceInfo> serviceInfoList = container.getContainerServiceInfo();
        try {
            MetaData metaData = new MetaData();
            metaData.setAddress(address);
            metaData.setServiceInfoList(serviceInfoList);
            String data = JsonUtil.Object2Json(metaData);
            byte[] bytes = data.getBytes();
            String path = Config.ZK_REGISTER_PARH.getValue() + "/datas-" + metaData.hashCode();

            zkPath = curatorClient.createZKNode(path, bytes);
            log.info("注册新服务成功");
        } catch (Exception e) {
            log.error("注册新服务异常");
        }
        //curatorClient.watchNode(zkPath);
        curatorClient.addConnect((curatorFramework,connectionState) -> {
            if(connectionState == ConnectionState.RECONNECTED){
                register();
            }
        });
    }
    public void delete() {
        log.info("删除服务");

        try {
            curatorClient.deletePath(zkPath);
        } catch (Exception e) {
            log.error("删除服务异常 : {}", e.getMessage());
        }
        curatorClient.close();
    }
}
