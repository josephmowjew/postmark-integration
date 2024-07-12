package com.qubedcare.postmark_integration.service;

import com.qubedcare.postmark_integration.model.Client;
import com.postmarkapp.postmark.Postmark;
import com.postmarkapp.postmark.client.ApiClient;
import com.postmarkapp.postmark.client.data.model.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final ApiClient postmarkClient;
    private final GuerrillaMailService guerrillaMailService;

    @Value("${postmark.from.email:noreply@example.com}")
    private String fromEmail;

    public EmailService(ApiClient postmarkClient, GuerrillaMailService guerrillaMailService) {
        this.postmarkClient = postmarkClient;
        this.guerrillaMailService = guerrillaMailService;
    }

    public String generateEmailAddress(String clientName) {
        // TODO: This is a temporary solution using GuerrillaMail for development purposes.
        // Replace with a permanent email service (e.g., Google Workspace) in the production environment.
        
        // TODO: Implement a mechanism to ensure generated email addresses are unique within the system.
        // This is crucial for a production environment but not implemented in this temporary solution.
        
        try {
            return guerrillaMailService.getEmailAddress();
        } catch (IOException e) {
            logger.error("Failed to generate email address for client: {}", clientName, e);
            throw new RuntimeException("Email address generation failed", e);
        }
    }

    public void sendWelcomeEmail(Client client) {
        Message message = new Message(
            fromEmail,
            client.getEmailAddress(),
            "Welcome to Our Service",
            "Dear " + client.getName() + ",\n\nWelcome to our service! We're excited to have you on board."
        );

        try {
            postmarkClient.deliverMessage(message);
            logger.info("Welcome email sent to client: {}", client.getEmailAddress());
        } catch (Exception e) {
            logger.error("Failed to send welcome email to client: {}", client.getEmailAddress(), e);
            // TODO: Implement retry mechanism for failed emails
        }
    }
}