package com.qubedcare.postmark_integration;

import com.qubedcare.postmark_integration.model.Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class EmailMicroserviceE2ETest {

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @BeforeAll
    static void setUp() {
        kafkaContainer.start();
    }

    @Test
    public void testNewClientFlow() throws Exception {
        Client client = new Client("1", "John Doe", null);
        String clientJson = objectMapper.writeValueAsString(client);

        kafkaTemplate.send("new_client_alert", clientJson);

        // Here we would ideally wait and verify:
        // 1. The client's email was generated
        // 2. A welcome email was sent
        // 3. The CRM was updated
        // This might involve adding some test hooks or using a test double for Postmark
    }
}