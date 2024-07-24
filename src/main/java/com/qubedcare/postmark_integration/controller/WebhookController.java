package com.qubedcare.postmark_integration.controller;

import com.qubedcare.postmark_integration.dto.DeliveryEventDTO;
import com.qubedcare.postmark_integration.dto.OpenEventDTO;
import com.qubedcare.postmark_integration.dto.BounceEventDTO;
import com.qubedcare.postmark_integration.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
@RestController
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);
    private static final String SUCCESS_MESSAGE = "Event processed successfully";
    private static final String INVALID_SIGNATURE_MESSAGE = "Invalid signature";
    private static final String PROCESSING_ERROR_MESSAGE = "Error processing event";

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/webhook/delivery")
    public ResponseEntity<String> handleDeliveryEvent(
            @Valid @RequestBody DeliveryEventDTO payload,
            @RequestHeader("X-Postmark-Signature") String signature) {
        
        return processEvent(payload, signature, "Delivery");
    }

    @PostMapping("/webhook/open")
    public ResponseEntity<String> handleOpenEvent(
            @Valid @RequestBody OpenEventDTO payload,
            @RequestHeader("X-Postmark-Signature") String signature) {
        
        return processEvent(payload, signature, "Open");
    }

    @PostMapping("/webhook/bounce")
    public ResponseEntity<String> handleBounceEvent(
            @Valid @RequestBody BounceEventDTO payload,
            @RequestHeader("X-Postmark-Signature") String signature) {
        
        return processEvent(payload, signature, "Bounce");
    }

    private ResponseEntity<String> processEvent(Object payload, String signature, String eventType) {
        try {
            if (webhookService.verifySignature(payload, signature)) {
                switch (eventType) {
                    case "Delivery":
                        webhookService.processDeliveryEvent((DeliveryEventDTO) payload);
                        break;
                    case "Open":
                        webhookService.processOpenEvent((OpenEventDTO) payload);
                        break;
                    case "Bounce":
                        webhookService.processBounceEvent((BounceEventDTO) payload);
                        break;
                }
                logger.info("{} event processed successfully for payload: {}", eventType, payload);
                return ResponseEntity.ok(SUCCESS_MESSAGE);
            } else {
                logger.warn("Invalid signature for {} event payload: {}", eventType, payload);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_SIGNATURE_MESSAGE);
            }
        } catch (Exception e) {
            logger.error("Error processing {} event for payload: {}", eventType, payload, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PROCESSING_ERROR_MESSAGE);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        logger.error("Unhandled exception occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PROCESSING_ERROR_MESSAGE);
    }
}
