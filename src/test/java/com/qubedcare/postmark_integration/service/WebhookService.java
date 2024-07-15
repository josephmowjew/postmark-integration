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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

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
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            String computedSignature = Base64.getEncoder().encodeToString(mac.doFinal(jsonPayload.getBytes(StandardCharsets.UTF_8)));
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

    // Implement processOpenEvent and processBounceEvent similarly

    private TaskDTO createTaskFromDeliveryEvent(DeliveryEventDTO payload) {
        TaskDTO task = new TaskDTO();
        task.setType("Delivery");
        task.setEmailAddress(payload.getRecipient());
        task.setDetails("Email delivered at " + payload.getDeliveredAt());
        return task;
    }

    private void sendTaskToKafka(TaskDTO task) {
        try {
            kafkaTemplate.send(triageRequestTopic, task);
            logger.info("Task sent to Kafka topic: {}", triageRequestTopic);
        } catch (Exception e) {
            logger.error("Error sending task to Kafka", e);
            // Implement retry logic here if needed
        }
    }
}