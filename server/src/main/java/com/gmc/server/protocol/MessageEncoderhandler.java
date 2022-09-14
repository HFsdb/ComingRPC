package com.gmc.server.protocol;

import com.gmc.server.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageEncoderhandler extends MessageToByteEncoder {

    private Serializer serializer;

    protected final short magic = (short) 0xC713;

    protected final static int header_length = 20;

    protected static final byte serializeKryo = 0x01;
    protected static final byte serializeHessian = 0x02;
    protected static final byte serializeProtobuff = 0x04;

    public MessageEncoderhandler(Serializer serializer) {
        this.serializer = serializer;
    }

    public static void short2bytes(short s,byte[] b,int off){
        b[off + 1] = (byte) s;
        b[off + 0] = (byte) (s >>> 8);
    }

    public static void int2bytes(int v, byte[] b, int off) {
        b[off + 3] = (byte) v;
        b[off + 2] = (byte) (v >>> 8);
        b[off + 1] = (byte) (v >>> 16);
        b[off + 0] = (byte) (v >>> 24);
    }

    public static void long2bytes(long v, byte[] b, int off) {
        b[off + 7] = (byte) v;
        b[off + 6] = (byte) (v >>> 8);
        b[off + 5] = (byte) (v >>> 16);
        b[off + 4] = (byte) (v >>> 24);
        b[off + 3] = (byte) (v >>> 32);
        b[off + 2] = (byte) (v >>> 40);
        b[off + 1] = (byte) (v >>> 48);
        b[off + 0] = (byte) (v >>> 56);
    }
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object in, ByteBuf out){
        log.info("准备编码");
        try{
            Message message = (Message) in;
            byte[] header = new byte[header_length];
            short2bytes(magic,header,0);
            byte type = message.getType();
            header[2] = type;
            header[3] = serializeKryo;
            int2bytes(0,header,4);
            if(type == (byte) 0x01) {
                Request request = (Request) message.getData();
                long2bytes(request.getRequestId(), header, 8);
            }else{
                Response response = (Response) message.getData();
                long2bytes(response.getRequestId(),header,8);
            }
            byte[] data = serializer.serialize(message.getData());
            int2bytes(data.length,header,16);
            System.out.println(data.length);
            out.writeBytes(header);
            out.writeBytes(data);
        }catch (Exception e){
            log.info("编码异常");
        }
    }
}
