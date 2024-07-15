package com.qubedcare.postmark_integration.service;

import com.qubedcare.postmark_integration.dto.DeliveryEventDTO;
import com.qubedcare.postmark_integration.dto.OpenEventDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qubedcare.postmark_integration.dto.BounceEventDTO;
import com.qubedcare.postmark_integration.dto.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

@Service
public class WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    @Value("${postmark.webhook.secret}")
    private String webhookSecret;

    @Value("${kafka.topic.triage-request}")
    private String triageRequestTopic;

    private final ObjectMapper objectMapper;

    private final KafkaTemplate<String, TaskDTO> kafkaTemplate;

    public WebhookService(KafkaTemplate<String, TaskDTO> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    public boolean verifySignature(Object payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            String jsonPayload = objectMapper.writeValueAsString(payload);
            String computedSignature = Base64.getEncoder().encodeToString(mac.doFinal(jsonPayload.getBytes(StandardCharsets.UTF_8)));
            return computedSignature.equals(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | com.fasterxml.jackson.core.JsonProcessingException e) {
            logger.error("Error verifying webhook signature", e);
            return false;
        }
    }


    public void processDeliveryEvent(DeliveryEventDTO payload) {
        logger.info("Processing delivery event: {}", payload);
        TaskDTO task = createTaskFromDeliveryEvent(payload);
        sendTaskToKafka(task);
    }

    public void processOpenEvent(OpenEventDTO payload) {
        logger.info("Processing open event: {}", payload);
        TaskDTO task = createTaskFromOpenEvent(payload);
        sendTaskToKafka(task);
    }

    public void processBounceEvent(BounceEventDTO payload) {
        logger.info("Processing bounce event: {}", payload);
        TaskDTO task = createTaskFromBounceEvent(payload);
        sendTaskToKafka(task);
    }

    private TaskDTO createTaskFromDeliveryEvent(DeliveryEventDTO payload) {
        TaskDTO task = new TaskDTO();
        task.setType("Delivery");
        task.setEmailAddress(payload.getRecipient());
        task.setDetails("Email delivered at " + payload.getDeliveredAt());
        // Set other task properties as needed
        return task;
    }

    private TaskDTO createTaskFromOpenEvent(OpenEventDTO payload) {
        TaskDTO task = new TaskDTO();
        task.setType("Open");
        task.setEmailAddress(payload.getRecipient());
        task.setDetails("Email opened at " + payload.getReceivedAt() + " using " + payload.getClient().getName());
        // Set other task properties as needed
        return task;
    }

    private TaskDTO createTaskFromBounceEvent(BounceEventDTO payload) {
        TaskDTO task = new TaskDTO();
        task.setType("Bounce");
        task.setEmailAddress(payload.getEmail());
        task.setDetails("Email bounced at " + payload.getBouncedAt() + ". Reason: " + payload.getDescription());
        // Set other task properties as needed
        return task;
    }

   @Retryable(value = { Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    private void sendTaskToKafka(TaskDTO task) {
        CompletableFuture<SendResult<String, TaskDTO>> future = kafkaTemplate.send(triageRequestTopic, task);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Task sent to Kafka topic: {} with offset: {}", triageRequestTopic, result.getRecordMetadata().offset());
            } else {
                logger.error("Failed to send task to Kafka topic: {}", triageRequestTopic, ex);
                throw new RuntimeException("Failed to send task to Kafka", ex);
            }
        });
    }
}