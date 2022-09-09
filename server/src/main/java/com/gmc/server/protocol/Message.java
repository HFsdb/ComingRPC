package com.gmc.server.protocol;

public class Message {
    final private byte magic = 0x0A;

    private byte type;

    private int length;

    private Object data;


    public byte getMagic() {
        return magic;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Message(byte type, int length, Object data) {
        this.type = type;
        this.length = length;
        this.data = data;
    }
}
