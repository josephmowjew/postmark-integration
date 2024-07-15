package com.qubedcare.postmark_integration.service;

import com.qubedcare.postmark_integration.model.Client;
import com.qubedcare.postmark_integration.exception.EmailGenerationException;
import com.qubedcare.postmark_integration.exception.SendingFailureException;
import com.postmarkapp.postmark.client.ApiClient;
import com.postmarkapp.postmark.client.data.model.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import java.io.IOException;

@Service
public class EmailService implements IEmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final ApiClient postmarkClient;
    private final GuerrillaMailService guerrillaMailService;

    @Value("${email.from.address}")
    private String fromEmail;

    @Value("${email.welcome.subject}")
    private String welcomeEmailSubject;

    @Value("${email.welcome.template}")
    private String welcomeEmailTemplate;

    @Value("${email.domain}")
    private String emailDomain;

    public EmailService(ApiClient postmarkClient, GuerrillaMailService guerrillaMailService) {
        this.postmarkClient = postmarkClient;
        this.guerrillaMailService = guerrillaMailService;
    }

    @Override
    public String generateEmailAddress(String clientName) throws EmailGenerationException {
        try {
            // Use GuerrillaMail to generate a temporary email address
            String email = guerrillaMailService.getEmailAddress();
            logger.info("Generated email address for client {}: {}", clientName, email);
            return email;
        } catch (IOException e) {
            logger.error("Failed to generate email address for client: {}", clientName, e);
            throw new EmailGenerationException("Failed to generate email address", e);
        }

        // Future implementation (keep as comment for now):
        /*
        try {
            // Check if the input is already an email address
            if (clientName.contains("@")) {
                String[] parts = clientName.split("@");
                clientName = parts[0];
            }

            // Generate email based on client's name and domain
            String emailPrefix = clientName.toLowerCase()
                    .replaceAll("[^a-z0-9]", ".")
                    .replaceAll("\\.+", ".") // Replace multiple dots with a single dot
                    .replaceAll("^\\.|\\.$", ""); // Remove leading and trailing dots

            String email = emailPrefix + "@" + emailDomain;
           
            logger.info("Generated email address for client {}: {}", clientName, email);
            return email;
        } catch (Exception e) {
            logger.error("Failed to generate email address for client: {}", clientName, e);
            throw new EmailGenerationException("Failed to generate email address", e);
        }
        */
    }

    @Override
    @Retryable(value = SendingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendWelcomeEmail(Client client) throws SendingFailureException {
        String emailContent = String.format(welcomeEmailTemplate, client.getName());
        Message message = new Message(fromEmail, client.getEmailAddress(), welcomeEmailSubject, emailContent);
        try {
            postmarkClient.deliverMessage(message);
            logger.info("Welcome email sent to client: {}", client.getEmailAddress());
        } catch (Exception e) {
            logger.error("Failed to send welcome email to client: {}", client.getEmailAddress(), e);
            throw new SendingFailureException("Failed to send welcome email", e);
        }
    }
}