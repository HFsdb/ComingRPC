package com.gmc.server.netty;

import com.gmc.server.protocol.MessageDecoderhandler;
import com.gmc.server.container.ServerContainer;
import com.gmc.server.factory.SingletonFactory;
import com.gmc.server.protocol.MessageEncoderhandler;
import com.gmc.server.netty.handler.NettyServerHandler;
import com.gmc.server.register.zookeeper.ZKRegister;
import com.gmc.server.serializer.Serializer;
import com.gmc.server.serializer.kryo.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyServer {
    private static String address;

    private ZKRegister register;
    private ServerContainer serverContainer;

    public NettyServer(String address){
        this.address = address;
        this.register = new ZKRegister(address);
        serverContainer = SingletonFactory.getInstance(ServerContainer.class);
    }

    @SneakyThrows
    public void start(){
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        Serializer serializer = SingletonFactory.getInstance(KryoSerializer.class);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline cp = socketChannel.pipeline();
                            cp.addLast(new IdleStateHandler(5,5,30, TimeUnit.SECONDS));
                            cp.addLast(new MessageDecoderhandler(1024*1024,16,4,0,0,false,serializer));
                            cp.addLast(new MessageEncoderhandler(serializer));
                            cp.addLast(new NettyServerHandler());
                        }
                    });
            String host = address.split(":")[0];
            int port = Integer.parseInt(address.split(":")[1]);

            ChannelFuture future = bootstrap.bind(host,port).sync();
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(future.isSuccess()) {
                        log.info("成功连接远程服务器,服务器 = {}",host+":"+port);

                    }else{
                        log.error("绑定端口失败");
                    }
                }
            });
            register.register();
            log.info("服务器开启");
            future.channel().closeFuture().sync();
        }catch (InterruptedException e){
            log.error("服务器启动异常");
        }finally {
            log.error("关闭两个线程");
            register.delete();
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
    public void addService2Container(String newAddress, String version, Object bean){
        log.info("添加服务到容器中");
        String serviceKey = newAddress + "#" + version;
        serverContainer.putBeanMap(serviceKey,bean);
//        this.register.setContainer(container);
//        this.nettyServerHandler.setContainer(container);

    }
}
