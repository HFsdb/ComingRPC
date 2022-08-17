package com.gmc.server.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.gmc.server.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerializer implements Serializer {

    private KryoPool pool = KryoPoolFactory.getInstance();

    @Override
    public <T> byte[] serialize(T obj) {
        Kryo kryo = pool.borrow();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        try{
            kryo.writeObject(output,obj);
            output.close();
            return byteArrayOutputStream.toByteArray();
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            try {
                byteArrayOutputStream.close();
            }catch (IOException e){
                throw new RuntimeException(e);
            }finally {
                pool.release(kryo);
            }
        }
    }

    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) {
        Kryo kryo = pool.borrow();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input in = new Input(byteArrayInputStream);
        try{
            Object object = kryo.readObject(in,clazz);
            in.close();
            return object;
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            try{
                byteArrayInputStream.close();
            }catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                pool.release(kryo);
            }
        }
    }
}
