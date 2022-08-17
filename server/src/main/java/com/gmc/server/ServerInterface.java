package com.gmc.server;

import com.gmc.server.annotation.Consumer;
import com.gmc.server.discovery.Discovery;
import com.gmc.server.discovery.zookeeper.ZKDiscovery;
import com.gmc.server.factory.SingletonFactory;
import com.gmc.server.loadbalance.LoadBalance;
import com.gmc.server.proxy.jdk.ClientProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class ServerInterface implements ApplicationContextAware, DisposableBean {
    private Discovery discovery;

    private static ThreadPoolExecutor threadPoolExecutor;

    public ServerInterface() throws Exception {
        this.discovery = new ZKDiscovery();
        this.discovery.discovery();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getProxy(Class<T> clazz, String version, LoadBalance loadBalance, long timeout) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz},
                new ClientProxy(version,loadBalance,timeout));
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for(String beanName : beanNames){
            Object bean = applicationContext.getBean(beanName);
            Field[] fields = bean.getClass().getFields();
            try{
                for(Field field : fields){
                    Consumer consumer = field.getAnnotation(Consumer.class);
                    if(consumer != null){
                        String version = consumer.version();
                        LoadBalance loadBalance = (LoadBalance) SingletonFactory.getInstance(consumer.loadBalance());
                        long timeout = consumer.timeout();
                        field.setAccessible(true);
                        field.set(bean,getProxy(field.getType(),version,loadBalance,timeout));
                    }
                }
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
    }
}
