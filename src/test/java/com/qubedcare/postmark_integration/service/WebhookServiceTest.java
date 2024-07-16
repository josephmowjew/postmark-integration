package com.qubedcare.postmark_integration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qubedcare.postmark_integration.dto.DeliveryEventDTO;
import com.qubedcare.postmark_integration.dto.OpenEventDTO;
import com.qubedcare.postmark_integration.dto.BounceEventDTO;
import com.qubedcare.postmark_integration.dto.TaskDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class WebhookServiceTest {

    @Mock
    private KafkaTemplate<String, TaskDTO> kafkaTemplate;

    private WebhookService webhookService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        webhookService = new WebhookService(kafkaTemplate, objectMapper);
        ReflectionTestUtils.setField(webhookService, "webhookSecret", "testSecret");
        ReflectionTestUtils.setField(webhookService, "triageRequestTopic", "testTopic");
    }
    @Test
    void verifySignature_ValidSignature_ReturnsTrue() throws Exception {
        DeliveryEventDTO payload = new DeliveryEventDTO();
        payload.setMessageId("test-message-id");
        String jsonPayload = objectMapper.writeValueAsString(payload);
        String signature = computeSignature(jsonPayload, "testSecret");

        assertTrue(webhookService.verifySignature(payload, signature));
    }

    @Test
    void verifySignature_InvalidSignature_ReturnsFalse() throws Exception {
        DeliveryEventDTO payload = new DeliveryEventDTO();
        payload.setMessageId("test-message-id");
        String invalidSignature = "invalidSignature";

        assertFalse(webhookService.verifySignature(payload, invalidSignature));
    }

    @Test
    void processDeliveryEvent_Success() {
        DeliveryEventDTO deliveryEvent = new DeliveryEventDTO();
        deliveryEvent.setMessageId("test-message-id");
        deliveryEvent.setRecipient("test@example.com");

        when(kafkaTemplate.send(eq("testTopic"), any(TaskDTO.class)))
            .thenReturn(CompletableFuture.completedFuture(null));

        assertDoesNotThrow(() -> webhookService.processDeliveryEvent(deliveryEvent));

        verify(kafkaTemplate).send(eq("testTopic"), any(TaskDTO.class));
    }

    @Test
    void processOpenEvent_Success() {
        OpenEventDTO openEvent = new OpenEventDTO();
        openEvent.setMessageId("test-message-id");
        openEvent.setRecipient("test@example.com");
        OpenEventDTO.ClientInfo clientInfo = new OpenEventDTO.ClientInfo();
        clientInfo.setName("Test Client");
        openEvent.setClient(clientInfo);

        when(kafkaTemplate.send(eq("testTopic"), any(TaskDTO.class)))
            .thenReturn(CompletableFuture.completedFuture(null));

        assertDoesNotThrow(() -> webhookService.processOpenEvent(openEvent));

        verify(kafkaTemplate).send(eq("testTopic"), any(TaskDTO.class));
    }

    @Test
    void processBounceEvent_Success() {
        BounceEventDTO bounceEvent = new BounceEventDTO();
        bounceEvent.setMessageId("test-message-id");
        bounceEvent.setEmail("test@example.com");

        when(kafkaTemplate.send(eq("testTopic"), any(TaskDTO.class)))
            .thenReturn(CompletableFuture.completedFuture(null));

        assertDoesNotThrow(() -> webhookService.processBounceEvent(bounceEvent));

        verify(kafkaTemplate).send(eq("testTopic"), any(TaskDTO.class));
    }

    @Test
    void processEvent_KafkaFailure() {
        DeliveryEventDTO deliveryEvent = new DeliveryEventDTO();
        deliveryEvent.setMessageId("test-message-id");
        deliveryEvent.setRecipient("test@example.com");

        CompletableFuture<SendResult<String, TaskDTO>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka error"));

        when(kafkaTemplate.send(any(), any())).thenReturn(failedFuture);

        assertThrows(RuntimeException.class, () -> webhookService.processDeliveryEvent(deliveryEvent));
    }


    private String computeSignature(String payload, String secret) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}