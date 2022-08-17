package com.gmc.server.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SingletonFactory {
    private static Map<String,Object> objectMap = new ConcurrentHashMap<>();

    public SingletonFactory(){}

    public static <T> T getInstance(Class<T> clazz){
        if(clazz == null){
            throw new IllegalArgumentException();
        }
        String key = clazz.toString();
        if(objectMap.containsKey(key)){
            return clazz.cast(objectMap.get(key));
        }else{
            return clazz.cast(objectMap.computeIfAbsent(key,k->{
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (InstantiationException |NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }));
        }
    }



}
