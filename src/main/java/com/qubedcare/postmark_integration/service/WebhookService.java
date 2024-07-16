package com.qubedcare.postmark_integration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qubedcare.postmark_integration.dto.DeliveryEventDTO;
import com.qubedcare.postmark_integration.dto.OpenEventDTO;
import com.qubedcare.postmark_integration.dto.BounceEventDTO;
import com.qubedcare.postmark_integration.dto.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    @Value("${postmark.webhook.secret}")
    private String webhookSecret;

    @Value("${kafka.topic.triage-request}")
    private String triageRequestTopic;

    private final KafkaTemplate<String, TaskDTO> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public WebhookService(KafkaTemplate<String, TaskDTO> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    

    public boolean verifySignature(Object payload, String signature) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            String computedSignature = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(jsonPayload.getBytes(StandardCharsets.UTF_8)));
            return computedSignature.equals(signature);
        } catch (Exception e) {
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
        return task;
    }

    private TaskDTO createTaskFromOpenEvent(OpenEventDTO payload) {
        TaskDTO task = new TaskDTO();
        task.setType("Open");
        task.setEmailAddress(payload.getRecipient());
        String clientName = payload.getClient() != null ? payload.getClient().getName() : "Unknown";
        task.setDetails("Email opened at " + payload.getReceivedAt() + " using " + clientName);
        return task;
    }

    private TaskDTO createTaskFromBounceEvent(BounceEventDTO payload) {
        TaskDTO task = new TaskDTO();
        task.setType("Bounce");
        task.setEmailAddress(payload.getEmail());
        task.setDetails("Email bounced at " + payload.getBouncedAt() + ". Reason: " + payload.getDescription());
        return task;
    }

    private void sendTaskToKafka(TaskDTO task) {
        try {
            kafkaTemplate.send(triageRequestTopic, task).get(); // This will block and throw an exception if the send fails
            logger.info("Task sent to Kafka topic: {}", triageRequestTopic);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to send task to Kafka topic: {}", triageRequestTopic, e);
            throw new RuntimeException("Failed to send task to Kafka", e);
        }
    }
}