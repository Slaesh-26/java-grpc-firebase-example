package org.example;

import org.jobrunr.jobs.annotations.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChangeResponseFormatJob {
    @Autowired
    private ResponseManager responseManager;

    @Job(name = "ChangeResponse")
    public void doJob() {
        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e){
                System.out.println("INTERRUPTED");
            }

            responseManager.setRandomResponse();
            System.out.println("Response changed");
        }
    }
}
