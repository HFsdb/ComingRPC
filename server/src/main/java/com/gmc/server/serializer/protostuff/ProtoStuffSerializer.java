package com.gmc.server.serializer.protostuff;

import com.gmc.server.serializer.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class ProtoStuffSerializer implements Serializer {

    private static final LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    @Override
    public <T> byte[] serialize(T obj) {
        Class<?> clazz = obj.getClass();
        Schema schema = RuntimeSchema.getSchema(clazz);
        byte[] bytes;
        try {
            bytes = ProtobufIOUtil.toByteArray(obj,schema,buffer);
        }finally {
            buffer.clear();
        }
        return bytes;
    }

    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T obj = schema.newMessage();
        ProtobufIOUtil.mergeFrom(bytes,obj,schema);
        return obj;
    }
}
