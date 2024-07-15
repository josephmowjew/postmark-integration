package com.qubedcare.postmark_integration.controller;

import com.qubedcare.postmark_integration.dto.DeliveryEventDTO;
import com.qubedcare.postmark_integration.service.WebhookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class WebhookControllerTest {

    @Mock
    private WebhookService webhookService;

    private WebhookController webhookController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webhookController = new WebhookController(webhookService);
    }

    @Test
    void handleDeliveryEvent_ValidSignature_ReturnsOk() {
        when(webhookService.verifySignature(any(DeliveryEventDTO.class), anyString())).thenReturn(true);
        ResponseEntity<String> response = webhookController.handleDeliveryEvent(new DeliveryEventDTO(), "validSignature");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delivery event processed successfully", response.getBody());
        verify(webhookService).processDeliveryEvent(any(DeliveryEventDTO.class));
    }

    @Test
    void handleDeliveryEvent_InvalidSignature_ReturnsBadRequest() {
        when(webhookService.verifySignature(any(DeliveryEventDTO.class), anyString())).thenReturn(false);
        ResponseEntity<String> response = webhookController.handleDeliveryEvent(new DeliveryEventDTO(), "invalidSignature");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid signature", response.getBody());
        verify(webhookService, never()).processDeliveryEvent(any(DeliveryEventDTO.class));
    }

    // Implement tests for handleOpenEvent and handleBounceEvent similarly
}