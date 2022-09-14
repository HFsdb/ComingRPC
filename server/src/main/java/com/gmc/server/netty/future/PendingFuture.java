package com.gmc.server.netty.future;

import com.gmc.server.protocol.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class PendingFuture {

    private static final ConcurrentHashMap<String, CompletableFuture<Response>> pendingMap = new ConcurrentHashMap<>();

    public void complete(Response response) {
        CompletableFuture<Response> future = pendingMap.remove(String.valueOf(response.getRequestId()));
        if (future != null) {
            future.complete(response);
        } else {
            throw new IllegalStateException();
        }
    }

    public void put(String requestId, CompletableFuture<Response> future) {
        pendingMap.put(requestId, future);
    }
}
