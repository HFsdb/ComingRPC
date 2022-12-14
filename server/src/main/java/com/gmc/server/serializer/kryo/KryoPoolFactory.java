package com.gmc.server.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.gmc.server.protocol.Request;
import com.gmc.server.protocol.Response;
import org.objenesis.strategy.StdInstantiatorStrategy;

class KryoPoolFactory {
    private static volatile KryoPoolFactory poolfactory = null;

    private KryoFactory factory = () -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        kryo.register(Request.class);
        kryo.register(Response.class);
        Kryo.DefaultInstantiatorStrategy strategy = (Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy();
        strategy.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
        return kryo;
    };

    private KryoPool kryoPool = new KryoPool.Builder(factory).build();

    private KryoPoolFactory(){}
    static KryoPool getInstance(){
        if(poolfactory == null){
            synchronized (KryoFactory.class){
                if(poolfactory == null){
                    poolfactory = new KryoPoolFactory();
                }
            }
        }
        return poolfactory.getpool();
    }

    private KryoPool getpool(){
        return kryoPool;
    }



}
