package com.qubedcare.postmark_integration.kafka;

import com.qubedcare.postmark_integration.model.Client;
import com.qubedcare.postmark_integration.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class NewClientAlertConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private CountDownLatch latch = new CountDownLatch(1);

    public NewClientAlertConsumer(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topic.new-client-alert}")
    public void consume(String message) throws Exception {
        System.out.println("Consumed message: " + message);
        Client client = objectMapper.readValue(message, Client.class);
        String emailAddress = emailService.generateEmailAddress(client.getName());
        client.setEmailAddress(emailAddress);
        emailService.sendWelcomeEmail(client);
        latch.countDown();
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
}
