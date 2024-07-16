package com.qubedcare.postmark_integration.service;

import com.qubedcare.postmark_integration.dto.DeliveryEventDTO;


public interface ICrmService {
    void updateClientEmail(String clientId, String emailAddress);
    void updateEmailOpenStatus(String clientId, String messageId);
    void updateEmailDeliveryStatus(DeliveryEventDTO deliveryEventDTO);
}