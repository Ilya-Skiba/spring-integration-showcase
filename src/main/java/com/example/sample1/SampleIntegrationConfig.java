package com.example.sample1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

import java.util.concurrent.Future;

/**
 * @author LuckyMan
 */
@Configuration
public class SampleIntegrationConfig {

    @Bean
    public IntegrationFlow sampleFlow() {
        return IntegrationFlows.from("in")

                .handle(message -> {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(message.getPayload());
                }).get();
    }

    @Bean
    public IntegrationFlow asyncFlow() {
        return f -> f.channel("in2").<Integer>filter(value -> value % 2 == 0)
                .channel("out");
    }


    @MessagingGateway
    public interface InputGateway {
        @Gateway(requestChannel = "in")
        void run(String payload);

        @Gateway(requestChannel = "in2", replyChannel = "out")
        Future<Void> runAsync(Integer payload);
    }
}
