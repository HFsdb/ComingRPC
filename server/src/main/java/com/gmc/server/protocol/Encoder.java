package com.gmc.server.protocol;

import com.gmc.server.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Encoder extends MessageToByteEncoder {

    private Class<?> clazz;
    private Serializer serializer;

    public Encoder(Class<?> clazz, Serializer serializer){
        this.clazz = clazz;
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object in, ByteBuf out){
        if(clazz.isInstance(in)){
            try {
                byte[] data = serializer.serialize(in);
                out.writeInt(data.length);
                out.writeBytes(data);
            }catch (Exception e){
                log.info("编码异常");
            }
        }
    }
}
