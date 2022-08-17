package com.gmc.server.discovery.zookeeper.zkclient;

import com.gmc.server.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
public class CuratorClient {

    private CuratorFramework curator;

    public CuratorClient() {
        curator = CuratorFrameworkFactory.builder()
                .namespace(Config.ZK_NAME_SPACE.getValue())
                .connectString(Config.ZK_ADDRESS.getValue())
                .sessionTimeoutMs(Config.ZK_SESSION_TIMEOUT.getTime())
                .connectionTimeoutMs(Config.ZK_CONNECTION_TIMEOUT.getTime())
                .retryPolicy(new ExponentialBackoffRetry(1000,10))
                .build();
        curator.start();
    }

    /**
     * 创建zk节点
     * @param path
     */
    public String createZKNode(String path,byte[] data){
        String createPath = null;
        try {
            createPath = curator.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path,data);
            log.info("子节点 {} 创建成功", path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createPath;
    }

    public List<String> getZKNodes(String path) throws Exception {
        return curator.getChildren().forPath(path);

    }

    public void addConnect(ConnectionStateListener connectionStateListener){
        curator.getConnectionStateListenable().addListener(connectionStateListener);
    }

    public CuratorFramework getCurator(){
        return curator;
    }

    /**
     * 创建一系列CuratorCache监听器，都是通过lambda表达式指定
     * @param path
     */
    public void watchNode(String path){

        CuratorCache cache = CuratorCache.build(curator,path);
        CuratorCacheListener listener = CuratorCacheListener.builder()
                // 初始化完成时调用
                .forInitialized(() -> System.out.println("Cache initialized"))
                // 添加或更改缓存中的数据时调用
                .forCreatesAndChanges((oldNode, node) -> System.out.printf("[forCreatesAndChanges] : Node changed: Old: [%s] New: [%s]\n", oldNode, node))
                // 添加缓存中的数据时调用
                .forCreates(childData -> System.out.printf("[forCreates] : Node created: [%s]\n", childData))
                // 更改缓存中的数据时调用
                .forChanges((oldNode, node) -> System.out.printf("[forChanges] : Node changed: Old: [%s] New: [%s]\n", oldNode, node))
                // 删除缓存中的数据时调用
                .forDeletes(childData -> System.out.printf("[forDeletes] : Node deleted: data: [%s]\n", childData))
                // 添加、更改或删除缓存中的数据时调用
                .forAll((type, oldData, data) -> System.out.printf("[forAll] : type: [%s] [%s] [%s]\n", type, oldData, data))
                .build();

        // 给CuratorCache实例添加监听器
        cache.listenable().addListener(listener);

        // 启动CuratorCache
        cache.start();
    }

    public void deletePath(String path) throws Exception {
        curator.delete().forPath(path);
    }

    public byte[] getNodeData(String path) throws Exception {
        return curator.getData().forPath(path);
    }

    public void close(){
        curator.close();
    }



}
