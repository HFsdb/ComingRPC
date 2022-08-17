package com.gmc.server.config;

import com.gmc.server.register.zookeeper.ZKRegister;

public enum Config {
    ZK_REGISTER_PARH("/Gaming_RPC1"),
    ZK_NAME_SPACE("RPC1"),
    ZK_SESSION_TIMEOUT(5000),
    ZK_CONNECTION_TIMEOUT(5000),
    NACOS_REGISTRY_PATH("RPC."),

    ZK_SERVER_ADDRESS("127.0.0.1:18879"),
    ZK_ADDRESS("127.0.0.1:2181");


    private String value;
    private int time;
    Config(String value) {
        this.value = value;
    }
    Config(int time){this.time = time;}

    public String getValue(){return value;}

    public int getTime(){return time;}
}
