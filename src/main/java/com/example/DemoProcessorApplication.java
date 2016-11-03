package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@EnableIntegration
@IntegrationComponentScan
@ImportResource(locations = "classpath:/twitter-config.xml")
public class DemoProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoProcessorApplication.class, args);
    }
}
