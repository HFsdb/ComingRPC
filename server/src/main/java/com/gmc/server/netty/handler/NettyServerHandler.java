package com.gmc.server.netty.handler;

import com.gmc.server.container.ServerContainer;
import com.gmc.server.factory.SingletonFactory;
import com.gmc.server.protocol.Message;
import com.gmc.server.protocol.Request;
import com.gmc.server.protocol.Response;
import com.gmc.server.reflect.jdk.JdkReflect;
import com.gmc.server.serializer.Serializer;
import com.gmc.server.serializer.kryo.KryoSerializer;
import com.gmc.server.util.ThreadUtil;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter{

    private ServerContainer serverContainer = SingletonFactory.getInstance(ServerContainer.class);
    private ThreadPoolExecutor pool = ThreadUtil.getThreadPool(35,70,600L);
    private Serializer serializer = SingletonFactory.getInstance(KryoSerializer.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        pool.execute(new Runnable() {
            @Override
            public void run() {
                Message message = (Message) msg;
                Request request = (Request) message.getData();
                log.info("接收请求ID:{}", request.getRequestId());
                Response response = new Response();
                response.setRequestId(request.getRequestId());
                try{
                    Object result = handle(request);
                    response.setResult(result);
                    log.info("服务器返回结果：{}",response);
                }catch (Throwable throwable){
                    response.setError((throwable.toString()));
                    throwable.printStackTrace();
                }
                Message message1 = new Message((byte) 0x02,response);
                ctx.writeAndFlush(message1).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        log.info("发送回复成功:{}",response.getRequestId());
                    }
                });
            }
        });
    }

    public Object handle(Request request) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String className = request.getClassName();
        String version = request.getVersion();
        String key = className +"#" + version;
        Object bean = serverContainer.getBeanMap().get(key);
        log.info("Bean:{}",bean);
        //jdk
        return JdkReflect.request2Bean(request,bean);
//        //CGLIB
//        FastMethod fastMethod = CglibReflect.request2Bean(request,bean);
//        return fastMethod.invoke(bean,request.getParams());
    }

    /**
     * 空闲时间超时则主动关闭连接
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.warn("服务器检测到通道空闲");
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
}
