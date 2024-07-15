package com.qubedcare.postmark_integration.controller;

import com.qubedcare.postmark_integration.dto.DeliveryEventDTO;
import com.qubedcare.postmark_integration.dto.OpenEventDTO;
import com.qubedcare.postmark_integration.dto.BounceEventDTO;
import com.qubedcare.postmark_integration.service.WebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/webhook/delivery")
    public ResponseEntity<String> handleDeliveryEvent(
            @RequestBody DeliveryEventDTO payload,
            @RequestHeader("X-Postmark-Signature") String signature) {
        
        if (webhookService.verifySignature(payload, signature)) {
            webhookService.processDeliveryEvent(payload);
            return ResponseEntity.ok("Delivery event processed successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid signature");
        }
    }

    @PostMapping("/webhook/open")
    public ResponseEntity<String> handleOpenEvent(
            @RequestBody OpenEventDTO payload,
            @RequestHeader("X-Postmark-Signature") String signature) {
        
        if (webhookService.verifySignature(payload, signature)) {
            webhookService.processOpenEvent(payload);
            return ResponseEntity.ok("Open event processed successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid signature");
        }
    }

    @PostMapping("/webhook/bounce")
    public ResponseEntity<String> handleBounceEvent(
            @RequestBody BounceEventDTO payload,
            @RequestHeader("X-Postmark-Signature") String signature) {
        
        if (webhookService.verifySignature(payload, signature)) {
            webhookService.processBounceEvent(payload);
            return ResponseEntity.ok("Bounce event processed successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid signature");
        }
    }
}