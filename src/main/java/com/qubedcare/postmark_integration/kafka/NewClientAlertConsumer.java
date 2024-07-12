package com.qubedcare.postmark_integration.kafka;

import com.qubedcare.postmark_integration.model.Client;
import com.qubedcare.postmark_integration.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NewClientAlertConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NewClientAlertConsumer.class);
    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    public NewClientAlertConsumer(ObjectMapper objectMapper, EmailService emailService) {
        this.objectMapper = objectMapper;
        this.emailService = emailService;
    }

    @KafkaListener(topics = "${kafka.topic.new-client-alert}")
    public void consume(String message) {
        try {
            Client client = objectMapper.readValue(message, Client.class);
            logger.info("Received new client alert: {}", client);
            
            String emailAddress = emailService.generateEmailAddress(client.getName());
            client.setEmailAddress(emailAddress);
            
            emailService.sendWelcomeEmail(client);
            
            logger.info("Processed new client: {}", client);
        } catch (Exception e) {
            logger.error("Error processing new client alert: {}", message, e);
            // TODO: Implement retry mechanism
        }
    }
}