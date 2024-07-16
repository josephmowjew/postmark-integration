package com.qubedcare.postmark_integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qubedcare.postmark_integration.kafka.NewClientAlertConsumer;
import com.qubedcare.postmark_integration.model.Client;
import com.qubedcare.postmark_integration.service.EmailService;
import com.qubedcare.postmark_integration.service.CrmService;
import com.qubedcare.postmark_integration.dto.DeliveryEventDTO;
import com.qubedcare.postmark_integration.dto.OpenEventDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@ActiveProfiles("test")
@DirtiesContext
public class ComprehensiveEmailMicroserviceE2ETest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private EmailService emailService;

    @SpyBean
    private CrmService crmService;
    

    @Autowired
    private NewClientAlertConsumer newClientAlertConsumer;

    @Value("${kafka.topic.new-client-alert}")
    private String newClientAlertTopic;

    private CountDownLatch latch;

    @BeforeEach
    void setUp() {
        latch = new CountDownLatch(1);
        newClientAlertConsumer.setLatch(latch);
    }

    @Test
    void testCompleteEmailFlow() throws Exception {
        // 1. Set up test data
        Client client = new Client("1", "John Doe", null);
        String clientJson = objectMapper.writeValueAsString(client);

        // 2. Mock EmailService behavior
        when(emailService.generateEmailAddress(any(String.class))).thenReturn("john.doe@example.com");
        doNothing().when(emailService).sendWelcomeEmail(any(Client.class));

        // 3. Send new client alert to Kafka
        kafkaTemplate.send(newClientAlertTopic, clientJson);
        System.out.println("Sent new client alert to Kafka");

        // 4. Wait for Kafka consumer to process the message
        boolean messageProcessed = latch.await(10, TimeUnit.SECONDS);
        System.out.println("Kafka consumer processed message: " + messageProcessed);

        // 5. Verify email generation and welcome email sending
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(emailService).generateEmailAddress("John Doe");
            verify(emailService).sendWelcomeEmail(any(Client.class));
        });

        // 6. Verify CRM update
        Client updatedClient = new Client("1", "John Doe", "john.doe@example.com");
        kafkaTemplate.send("crm-update", objectMapper.writeValueAsString(updatedClient));

        // await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
        //     verify(crmService).updateClientEmail("1", "john.doe@example.com");
        // });

        // 7. Simulate delivery event
        DeliveryEventDTO deliveryEvent = new DeliveryEventDTO();
        deliveryEvent.setRecipient("john.doe@example.com");
        deliveryEvent.setMessageId("test-message-id");
        restTemplate.postForEntity("/webhook/delivery", deliveryEvent, String.class);
        System.out.println("Simulated delivery event");

        // 8. Simulate open event
        OpenEventDTO openEvent = new OpenEventDTO();
        openEvent.setRecipient("john.doe@example.com");
        openEvent.setMessageId("test-message-id");
        restTemplate.postForEntity("/webhook/open", openEvent, String.class);
        System.out.println("Simulated open event");

        // // 9. Verify event processing
        // verify(crmService, timeout(10000)).updateEmailDeliveryStatus(any(DeliveryEventDTO.class));
        // verify(crmService, timeout(10000)).updateEmailOpenStatus(eq("1"), eq("test-message-id"));
    }
}