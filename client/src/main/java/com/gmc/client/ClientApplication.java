package com.gmc.client;

import com.gmc.server.ServerInterface;
import com.gmc.server.loadbalance.hash.HashLoadBalance;
import com.gmc.server.loadbalance.pooling.PoolingLoadBalance;
import com.gmc.server.service.HelloService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ClientApplication.class, args);
        new ServerInterface();
        HelloService helloService = ServerInterface.getProxy(HelloService.class,"1.0",new HashLoadBalance(),3000L);
        String str = "";
        for(int i = 0; i < 20000; i++) {
            str += 'a';
        }
        String message = helloService.hello(str);
        System.out.println(message.length());
        System.out.println("name:" + message);

    }

}
