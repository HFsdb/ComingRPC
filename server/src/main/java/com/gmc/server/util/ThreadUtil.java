package com.gmc.server.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {

    private static final long keepAliveTime = 60L;
    private static final int capacity = 1000;
    public static ThreadPoolExecutor getThreadPool(int corePoolSize, int maxPoolSize, long keepAliveTime) {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(capacity),
                Thread::new,
                new ThreadPoolExecutor.AbortPolicy());
    }
}
