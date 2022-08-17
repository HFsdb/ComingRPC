package com.gmc.server.serializer;

public interface Serializer {
    /**
     * 序列化
     * @param obj
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    <T> Object deserialize(byte[] bytes, Class<T> clazz);
}
