package com.qubedcare.postmark_integration.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class GuerrillaMailService {

    private static final Logger logger = LoggerFactory.getLogger(GuerrillaMailService.class);
    private static final String GUERRILLA_MAIL_API = "https://api.guerrillamail.com/ajax.php?f=get_email_address";

    private final RestTemplate restTemplate;

    public GuerrillaMailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getEmailAddress() throws IOException {
        try {
            String response = restTemplate.getForObject(GUERRILLA_MAIL_API, String.class);
            // Parse the response to extract the email address
            // This is a simplified version and might need adjustment based on the actual API response
            String emailAddress = response.split("email_addr\":\"")[1].split("\"")[0];
            logger.info("Generated temporary email address: {}", emailAddress);
            return emailAddress;
        } catch (Exception e) {
            logger.error("Failed to generate temporary email address", e);
            throw new IOException("Failed to generate temporary email address", e);
        }
    }
}