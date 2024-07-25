package com.qubedcare.postmark_integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@PropertySource("classpath:application.properties")
@EnableRetry
public class PostmarkIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostmarkIntegrationApplication.class, args);
    }
}