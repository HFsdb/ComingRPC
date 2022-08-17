package com.gmc.server.discovery;

public interface Discovery {
    /**
     * 服务发现
     */
    void discovery() throws Exception;

    /**
     * 停止订阅
     */
    void stop();
}
