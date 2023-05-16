package org.example;

import io.grpc.stub.StreamObserver;
import org.example.grpc.Config;
import org.example.grpc.GreetingServiceGrpc;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {

    @Autowired
    private ChangeResponseFormatJob changeResponseFormatJob;
    @Autowired
    private ResponseManager responseProvider;

    @Override
    public void greeting(Config.HelloRequest request, StreamObserver<Config.HelloResponse> streamObserver) {
        var task = CompletableFuture.supplyAsync(() -> {
            try{
                Thread.sleep(5000);
            } catch (InterruptedException e){
                throw new RuntimeException();
            }

            BackgroundJob.enqueue(() -> changeResponseFormatJob.doJob());

            var response = Config.HelloResponse
                    .newBuilder()
                    .setResponse("Hello from server, " + request.getName())
                    .build();

            return response;
        });

        task.thenAccept(helloResponse -> {
            streamObserver.onNext(helloResponse);
            streamObserver.onCompleted();
        });

        task.join();
    }

    @Override
    public void sendStream(Config.EmptyRequest request, StreamObserver<Config.HelloResponse> streamObserver) {
        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e){
                throw new RuntimeException();
            }

            var response = Config.HelloResponse
                    .newBuilder()
                    .setResponse(responseProvider.getResponse() + i)
                    .build();

            streamObserver.onNext(response);
        }

        streamObserver.onCompleted();
    }
}
