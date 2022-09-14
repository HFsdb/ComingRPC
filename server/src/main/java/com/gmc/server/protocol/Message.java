package com.gmc.server.protocol;

import lombok.Data;

import java.io.Serializable;

public class Message {

    private byte type;

    private Object data;

    public Message(byte type, Object data) {
        this.type = type;
        this.data = data;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
