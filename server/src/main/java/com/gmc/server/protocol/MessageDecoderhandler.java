package com.gmc.server.protocol;

import com.gmc.server.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageDecoderhandler extends LengthFieldBasedFrameDecoder {
    private static final int header_length = 20;
    Serializer serializer;

    public MessageDecoderhandler(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast,Serializer serializer) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
        this.serializer = serializer;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        buf = (ByteBuf) super.decode(ctx,buf);
        log.info("准备解码");
        if(buf == null){
            log.warn("ByteBuf为空");
            return null;
        }
        if(buf.readableBytes() < header_length){
            log.error("字节数不够");
            throw new Exception("字节数不够");
        }
        short magic = buf.readShort();
        if(magic != (short)0xC713){
            log.error("magic错误");
            throw new Exception();
        }
        byte type = buf.readByte();
        byte serialize = buf.readByte();
        int splitnum = buf.readInt();

        long requestId = buf.readLong();
        int length = buf.readInt();
        if(buf.readableBytes() < length){
            log.error("标记的长度大于当前长度，等待");
            return null;
        }
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        Object obj = null;
        Message message = null;
        try {
            switch (type){
                case 0x01: obj = serializer.deserialize(bytes, Request.class);
                    System.out.println(type+"req");break;
                case 0x02: obj = serializer.deserialize(bytes, Response.class);
                    System.out.println(type+"resp");break;
                default:obj = null;
            }
            System.out.println(obj);
            message = new Message(type,obj);
        } catch (Exception ex) {
            log.error("解码异常");
            throw new RuntimeException(ex);
        }
        return message;
    }
}
