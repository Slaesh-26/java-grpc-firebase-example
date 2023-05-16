package org.example;

import io.grpc.stub.StreamObserver;
import org.example.grpc.HealthGrpc;
import org.example.grpc.HealthOuterClass;
import org.example.grpc.HealthOuterClass.HealthCheckResponse.ServingStatus;
import org.springframework.stereotype.Service;

@Service
public class HealthCheckService extends HealthGrpc.HealthImplBase {
    @Override
    public void check(HealthOuterClass.HealthCheckRequest request,
                      StreamObserver<HealthOuterClass.HealthCheckResponse> responseObserver) {
        if (isReady()) {
            responseObserver.onNext(HealthOuterClass.HealthCheckResponse.newBuilder()
                    .setStatus(ServingStatus.SERVING)
                    .build());
        } else {
            responseObserver.onNext(HealthOuterClass.HealthCheckResponse.newBuilder()
                    .setStatus(ServingStatus.NOT_SERVING)
                    .build());
        }
        responseObserver.onCompleted();
    }

    private boolean isReady() {
        return true;
    }
}
