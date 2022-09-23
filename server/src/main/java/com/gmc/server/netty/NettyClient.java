package com.gmc.server.netty;

import com.gmc.server.container.ClientContainer;
import com.gmc.server.info.MetaData;
import com.gmc.server.loadbalance.LoadBalance;
import com.gmc.server.protocol.MessageDecoderhandler;
import com.gmc.server.protocol.MessageEncoderhandler;
import com.gmc.server.protocol.*;
import com.gmc.server.factory.SingletonFactory;
import com.gmc.server.netty.future.PendingFuture;
import com.gmc.server.netty.handler.NettyClientHandler;
import com.gmc.server.serializer.Serializer;
import com.gmc.server.serializer.kryo.KryoSerializer;
import com.gmc.server.util.ThreadUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

@Slf4j
public class NettyClient {

    private ThreadPoolExecutor pool = ThreadUtil.getThreadPool(30,60,600L);
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(NettyRuntime.availableProcessors() / 2);
    private final Bootstrap bootstrap = new Bootstrap();
    private final Map<String,Channel> channelMap = new ConcurrentHashMap<>();
    private Map<MetaData,NettyClientHandler> metaDataNettyClientHandlerMap = new ConcurrentHashMap<>();
    private final NettyClient nettyClient = this;
    private Serializer serializer = SingletonFactory.getInstance(KryoSerializer.class);

    private PendingFuture pendingFuture = SingletonFactory.getInstance(PendingFuture.class);
    private ClientContainer clientContainer = SingletonFactory.getInstance(ClientContainer.class);
    public NettyClient(){}

    public Object sendRequest(Request request, LoadBalance loadBalance) {
        Set<MetaData> metaDataSet = clientContainer.getMetaDataSet();
        List<MetaData> list = new ArrayList<>();
        for(MetaData metaData : metaDataSet) list.add(metaData);
        MetaData metaData = loadBalance.distribute(list,request);
        String newAddress = metaData.getAddress();
        String host = newAddress.split(":")[0];
        int port = Integer.parseInt(newAddress.split(":")[1]);

        InetSocketAddress inetSocketAddress = new InetSocketAddress(host,port);
        Channel channel = channelMap.get(inetSocketAddress.toString());
        if(channel == null){
            try{
                channel = connect(inetSocketAddress);
                channelMap.put(inetSocketAddress.toString(),channel);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        CompletableFuture<Response> future = new CompletableFuture<>();
        if(channel.isActive()) {
            Message message = new Message((byte) 0x01,request);
            pendingFuture.put(String.valueOf(request.getRequestId()), future);
            log.info("通道正常");

            channel.writeAndFlush(message).addListener((ChannelFutureListener) f -> {
                if (f.isSuccess()) {
                    log.info("客户端发送消息成功");
                } else {
                    f.channel().close();
                    future.completeExceptionally(f.cause());
                    log.error("发送消息失败");
                }
            });
        }else{
            throw new IllegalStateException();
        }
        return future;
    }
    public Channel connect(InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        pool.submit(()-> {
                bootstrap.group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                ChannelPipeline cp = socketChannel.pipeline();
                                cp.addLast(new IdleStateHandler(5,5,30,TimeUnit.SECONDS));
                                cp.addLast(new MessageEncoderhandler(serializer));
                                cp.addLast(new MessageDecoderhandler(1024*1024,16,4,0,0,false,serializer));
                                cp.addLast(new NettyClientHandler());
                            }
                        });
                bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
                    if(future.isSuccess()){
                        log.info("客户端连接成功");
                        completableFuture.complete(future.channel());
                    }else{
                        throw new IllegalStateException();
                    }
                });
        });
        return completableFuture.get();
    }

    public void close(){

    }

}
