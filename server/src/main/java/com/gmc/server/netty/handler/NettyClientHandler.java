package com.gmc.server.netty.handler;

import com.gmc.server.factory.SingletonFactory;
import com.gmc.server.protocol.RpcResponse;
import com.gmc.server.netty.NettyClient;
import com.gmc.server.netty.future.PendingFuture;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private PendingFuture pendingFuture;

    private NettyClient nettyClient;

    private SocketAddress address;
    private Channel channel;

    public NettyClientHandler(){
        this.pendingFuture = SingletonFactory.getInstance(PendingFuture.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        RpcResponse response = (RpcResponse) msg;
        log.info("接收服务端的回复体");
        pendingFuture.complete(response);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.address = this.channel.remoteAddress();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    /**
     * 空闲时间超时则主动关闭连接
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();
            log.warn("通道空闲");
            close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable){
        log.error("客户端异常 {}", throwable);
        throwable.printStackTrace();
        ctx.close();
    }

    public void close(){
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        log.info("关闭通道");
    }
}
