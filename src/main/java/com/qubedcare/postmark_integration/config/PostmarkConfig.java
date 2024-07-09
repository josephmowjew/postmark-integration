package com.qubedcare.postmark_integration.config;

import com.postmarkapp.postmark.Postmark;
import com.postmarkapp.postmark.client.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostmarkConfig {

    @Value("${postmark.api.token}")
    private String postmarkApiToken;

    @Bean
    public ApiClient postmarkClient() {
        return Postmark.getApiClient(postmarkApiToken);
    }
}