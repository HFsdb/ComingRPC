package com.gmc.server.protocol;

import com.gmc.server.protocol.Message;
import com.gmc.server.protocol.RpcRequest;
import com.gmc.server.protocol.RpcResponse;
import com.gmc.server.serializer.Serializer;
import com.gmc.server.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageDecoderhandler extends LengthFieldBasedFrameDecoder {
    private static final int head_size = 6;
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
            log.info("ByteBuf为空");
            return null;
        }
        if(buf.readableBytes() < head_size){
            log.info("字节数不够");
            throw new Exception("字节数不够");
        }
        byte magic = buf.readByte();
        byte type = buf.readByte();
        int length = buf.readInt();
        if(buf.readableBytes() != length){
            log.error("标记的长度不符合当前长度");
            throw new Exception("标记的长度不符合当前长度");
        }
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        Object obj;
        Message message = null;
        try {
            if(type == 0x01){
                obj = serializer.deserialize(bytes, RpcRequest.class);
            }
            else{
                obj = serializer.deserialize(bytes, RpcResponse.class);
            }
            message = new Message(type,length,obj);
        } catch (Exception ex) {
            log.error("解码异常");
        }
        return message;
    }
}
