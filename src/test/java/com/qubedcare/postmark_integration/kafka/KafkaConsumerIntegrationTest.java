package com.qubedcare.postmark_integration.kafka;

import com.qubedcare.postmark_integration.model.Client;
import com.qubedcare.postmark_integration.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class KafkaConsumerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    @Value("${kafka.topic.new-client-alert}")
    private String topic;

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<String, String> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
        consumer = consumerFactory.createConsumer("testGroup", "");
        consumer.subscribe(Collections.singleton(topic));
        // Clear any existing messages
        KafkaTestUtils.getRecords(consumer);
        
        // Reset mocks before each test
        reset(emailService);
    }

    @Test
    void testNewClientAlertConsumer() throws Exception {
        // Arrange
        Client client = new Client("1", "John Doe", null);
        String clientJson = objectMapper.writeValueAsString(client);
        
        // Set up mock behavior
        when(emailService.generateEmailAddress(client.getName())).thenReturn("john.doe@lyvepulse.com");

        // Act
        kafkaTemplate.send(topic, clientJson);

        // Assert
        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, topic, Duration.ofSeconds(10));
        assertNotNull(singleRecord);
        assertEquals(clientJson, singleRecord.value());

        // Verify EmailService method calls with timeout
        verify(emailService, timeout(5000)).generateEmailAddress(client.getName());
        verify(emailService, timeout(5000)).sendWelcomeEmail(any(Client.class));
    }
    
}