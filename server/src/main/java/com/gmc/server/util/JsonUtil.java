package com.gmc.server.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtil {
    private static ObjectMapper objMapper = new ObjectMapper();

    public static String Object2Json(Object o) {
        String json;
        try {
            json = objMapper.writeValueAsString(o);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return json;
    }

    public static <T> T Json2Object(String json,Class<?> clazz){
        T obj;
        JavaType javaType = objMapper.getTypeFactory().constructType((clazz));
        try {
            obj = objMapper.readValue(json,javaType);
        }catch (IOException e){
            throw new IllegalStateException(e.getMessage(),e);
        }
        return obj;
    }
}
