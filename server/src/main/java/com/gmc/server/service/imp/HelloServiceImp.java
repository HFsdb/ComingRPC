package com.gmc.server.service.imp;

import com.gmc.server.service.HelloService;

public class HelloServiceImp implements HelloService {
    @Override
    public String hello(String name) {
        return name;
    }
}
