package com.gmc.server.protocol;

import com.gmc.server.protocol.Message;
import com.gmc.server.protocol.RpcRequest;
import com.gmc.server.serializer.Serializer;
import com.gmc.server.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import jdk.security.jarsigner.JarSigner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageEncoderhandler extends MessageToByteEncoder {

    private Serializer serializer;

    public MessageEncoderhandler(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object in, ByteBuf out){
        log.info("准备编码");
        try{
            Message message = (Message) in;
            out.writeByte(message.getMagic());
            out.writeByte(message.getType());
            out.writeInt(message.getLength());
            byte[] data = serializer.serialize(message.getData());
            out.writeBytes(data);
        }catch (Exception e){
            log.info("编码异常");
        }
    }
}
