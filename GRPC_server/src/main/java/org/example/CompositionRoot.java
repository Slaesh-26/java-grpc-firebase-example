package org.example;

import io.grpc.ServerBuilder;
import org.example.interceptors.TestInterceptor;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class CompositionRoot {
    @Autowired
    private GreetingServiceImpl greetingService;

    @Autowired
    private FirebaseExample firebaseExample;

    @Autowired
    private HealthCheckService healthCheckService;

    // Custom entry point. Runs after dependencies injected.
    @PostConstruct
    public void start() {
        System.out.println("Startup");

        initializeGRPC();

        try {
            firebaseExample.initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public StorageProvider storageProvider(JobMapper jobMapper) {
        var storageProvider = new InMemoryStorageProvider();
        storageProvider.setJobMapper(jobMapper);
        return storageProvider;
    }

    private void initializeGRPC() {
        var server = ServerBuilder.forPort(8080)
                .addService(greetingService)
                .addService(healthCheckService)
                .intercept(new TestInterceptor())
                .build();
        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}