package com.qubedcare.postmark_integration.kafka;

import com.qubedcare.postmark_integration.model.Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class CrmUpdateConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CrmUpdateConsumer.class);
    private final ObjectMapper objectMapper;

    public CrmUpdateConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topic.crm-update}")
    @RetryableTopic(
        attempts = "5",
        backoff = @Backoff(delay = 1000, multiplier = 2.0))
    public void consume(String message) {
        try {
            Client updatedClient = objectMapper.readValue(message, Client.class);
            logger.info("Received CRM update: {}", updatedClient);
            
            // TODO: Implement logic to update local client information if necessary
            
            logger.info("Processed CRM update for client: {}", updatedClient.getId());
        } catch (Exception e) {
            logger.error("Error processing CRM update: {}", message, e);
            // TODO: Implement retry mechanism
        }
    }
}