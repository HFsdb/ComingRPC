package com.gmc.server.netty.handler;

import com.gmc.server.factory.SingletonFactory;
import com.gmc.server.protocol.Message;
import com.gmc.server.protocol.Response;
import com.gmc.server.netty.NettyClient;
import com.gmc.server.netty.future.PendingFuture;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

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
        Message message = (Message) msg;

        Response response = (Response) message.getData();
        log.info("接收服务端的回复体"+response.getResult());
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
        log.warn("客户端检测到通道空闲");
        if (evt instanceof IdleStateEvent) {
            IdleState idleState = ((IdleStateEvent) evt).state();
            if(idleState == IdleState.ALL_IDLE){
                log.info("关闭通道");
                ctx.channel().close();
            }
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
