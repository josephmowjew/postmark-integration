package com.qubedcare.postmark_integration.controller;

import com.qubedcare.postmark_integration.dto.DeliveryEventDTO;
import com.qubedcare.postmark_integration.dto.OpenEventDTO;
import com.qubedcare.postmark_integration.dto.BounceEventDTO;
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

    private static final String SUCCESS_MESSAGE = "Event processed successfully";
    private static final String INVALID_SIGNATURE_MESSAGE = "Invalid signature";

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
        assertEquals(SUCCESS_MESSAGE, response.getBody());
        verify(webhookService).processDeliveryEvent(any(DeliveryEventDTO.class));
    }

    @Test
    void handleDeliveryEvent_InvalidSignature_ReturnsBadRequest() {
        when(webhookService.verifySignature(any(DeliveryEventDTO.class), anyString())).thenReturn(false);
        ResponseEntity<String> response = webhookController.handleDeliveryEvent(new DeliveryEventDTO(), "invalidSignature");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(INVALID_SIGNATURE_MESSAGE, response.getBody());
        verify(webhookService, never()).processDeliveryEvent(any(DeliveryEventDTO.class));
    }

    @Test
    void handleOpenEvent_ValidSignature_ReturnsOk() {
        when(webhookService.verifySignature(any(OpenEventDTO.class), anyString())).thenReturn(true);
        ResponseEntity<String> response = webhookController.handleOpenEvent(new OpenEventDTO(), "validSignature");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SUCCESS_MESSAGE, response.getBody());
        verify(webhookService).processOpenEvent(any(OpenEventDTO.class));
    }

    @Test
    void handleOpenEvent_InvalidSignature_ReturnsBadRequest() {
        when(webhookService.verifySignature(any(OpenEventDTO.class), anyString())).thenReturn(false);
        ResponseEntity<String> response = webhookController.handleOpenEvent(new OpenEventDTO(), "invalidSignature");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(INVALID_SIGNATURE_MESSAGE, response.getBody());
        verify(webhookService, never()).processOpenEvent(any(OpenEventDTO.class));
    }

    @Test
    void handleBounceEvent_ValidSignature_ReturnsOk() {
        when(webhookService.verifySignature(any(BounceEventDTO.class), anyString())).thenReturn(true);
        ResponseEntity<String> response = webhookController.handleBounceEvent(new BounceEventDTO(), "validSignature");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SUCCESS_MESSAGE, response.getBody());
        verify(webhookService).processBounceEvent(any(BounceEventDTO.class));
    }

    @Test
    void handleBounceEvent_InvalidSignature_ReturnsBadRequest() {
        when(webhookService.verifySignature(any(BounceEventDTO.class), anyString())).thenReturn(false);
        ResponseEntity<String> response = webhookController.handleBounceEvent(new BounceEventDTO(), "invalidSignature");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(INVALID_SIGNATURE_MESSAGE, response.getBody());
        verify(webhookService, never()).processBounceEvent(any(BounceEventDTO.class));
    }
}
