package com.gmc.server.protocol;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse {
    private String requestId;
    private String error;
    private Object result;
}
