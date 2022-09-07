package com.gmc.server;

import com.gmc.server.netty.NettyServer;
import com.gmc.server.service.HelloService;
import com.gmc.server.service.imp.HelloServiceImp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
        String serverAddress = "127.0.0.1:18845";
        NettyServer nettyServer = new NettyServer(serverAddress);
        HelloService helloService1 = new HelloServiceImp();

        nettyServer.addService2Container(HelloService.class.getName(), "1.0", helloService1);
        try {
            nettyServer.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
