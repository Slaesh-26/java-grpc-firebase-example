package org.example;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ResponseManager {
    private final String[] prompts = new String[] {
        "Hello from hell, ",
        "Hello from server, ",
        "Hello from nowhere, "
    };

    private final Random random = new Random();
    private String response = prompts[0];

    public String getResponse() {
        return response;
    }

    public void setRandomResponse() {
        var randomIndex = random.nextInt(0, prompts.length);
        response = prompts[randomIndex];
    }
}
