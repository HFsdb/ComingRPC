package com.gmc.server.netty.future;

import com.gmc.server.protocol.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class PendingFuture {

    private static final ConcurrentHashMap<String, CompletableFuture<RpcResponse>> pendingMap = new ConcurrentHashMap<>();

    public void complete(RpcResponse response) {
        CompletableFuture<RpcResponse> future = pendingMap.remove(response.getRequestId());
        if (future != null) {
            future.complete(response);
        } else {
            throw new IllegalStateException();
        }
    }

    public void put(String requestId, CompletableFuture<RpcResponse> future) {
        pendingMap.put(requestId, future);
    }
}
