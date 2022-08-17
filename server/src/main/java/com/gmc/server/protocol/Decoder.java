package com.gmc.server.protocol;

import com.gmc.server.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Decoder extends ByteToMessageDecoder {

    private Class<?> clazz;
    private Serializer serializer;

    public Decoder(Class<?> clazz, Serializer serializer) {
        this.clazz = clazz;
        this.serializer = serializer;
    }

    /**
     * 解码 缓冲区头记录消息包载体长度
     * 如果不足4字节 int说明未读完 直接返回
     * 如果后续包长度不足 重置ByteBuf读取 直接返回
     *
     * @param ctx
     * @param in 缓冲区
     * @param out
     */
    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object obj;
        try {
            obj = serializer.deserialize(data, clazz);
            System.out.println(obj);
            out.add(obj);
        } catch (Exception ex) {
            log.error("解码异常");
        }
    }
}
