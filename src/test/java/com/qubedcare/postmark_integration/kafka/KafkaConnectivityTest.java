package com.qubedcare.postmark_integration.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" },
               topics = {"${kafka.topic.new-client-alert}", "${kafka.topic.crm-update}", "${kafka.topic.triage-request}"})
public class KafkaConnectivityTest {

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Value("${kafka.topic.new-client-alert}")
    private String newClientAlertTopic;

    @Value("${kafka.topic.crm-update}")
    private String crmUpdateTopic;

    @Value("${kafka.topic.triage-request}")
    private String triageRequestTopic;

    @Test
    public void testKafkaConnectivity() throws ExecutionException, InterruptedException {
        AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties());
        
        Set<String> topics = adminClient.listTopics().names().get();

        assertTrue(topics.contains(newClientAlertTopic), "New client alert topic should exist");
        assertTrue(topics.contains(crmUpdateTopic), "CRM update topic should exist");
        assertTrue(topics.contains(triageRequestTopic), "Triage request topic should exist");

        adminClient.close();
    }
}