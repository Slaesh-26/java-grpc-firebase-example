package org.example.interceptors;

import io.grpc.*;

public class TestInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall,
        Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {

        var forwardingServerCall = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(serverCall) {
            @Override
            public void sendMessage(RespT message) {
                super.sendMessage(message);
                System.out.println("ServerInterceptor (sent): " + message.toString());
            }
        };

        var listener = serverCallHandler.startCall(forwardingServerCall, metadata);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(listener) {
            @Override
            public void onMessage(ReqT message) {
                super.onMessage(message);
                System.out.println("ServerInterceptor (received): " + message.toString());
            }
        };
    }
}
