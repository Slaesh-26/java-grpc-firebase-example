package org.example;

import io.grpc.ManagedChannelBuilder;
import org.example.grpc.Config;
import org.example.grpc.GreetingServiceGrpc;
import org.example.grpc.HealthGrpc;
import org.example.grpc.HealthOuterClass;

public class App {
    public static void main( String[] args ) {
        var channel = ManagedChannelBuilder
                .forTarget("localhost:8080")
                .usePlaintext()
                .build();

        // Health check
        var healthCheckStub = HealthGrpc.newBlockingStub(channel);
        var healthRequest = HealthOuterClass.HealthCheckRequest
                .newBuilder()
                .setService("Some service")
                .build();

        var healthCheckResponse = healthCheckStub.check(healthRequest);
        System.out.println(healthCheckResponse);

        // Greeting server
        var stub = GreetingServiceGrpc.newBlockingStub(channel);
        var request = Config.HelloRequest
                .newBuilder()
                .setName("NeDanil")
                .build();

        var response = stub.greeting(request);

        // Receiving stream from server
        var emptyRequest = Config.EmptyRequest
                .newBuilder()
                .build();

        System.out.println(response.getResponse());

        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {}

        var stream = stub.sendStream(emptyRequest);

        while (stream.hasNext()) {
            System.out.println(stream.next().getResponse());
        }

        channel.shutdownNow();
    }
}
