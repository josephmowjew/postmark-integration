package com.qubedcare.postmark_integration.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class KafkaConnectivityTest {

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Test
    public void testKafkaConnectivity() throws ExecutionException, InterruptedException {
        AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties());
        
        // Create test topics
        NewTopic newClientAlertTopic = new NewTopic("new_client_alert", 1, (short) 1);
        NewTopic crmUpdateTopic = new NewTopic("crm_update", 1, (short) 1);
        NewTopic triageRequestTopic = new NewTopic("triage_request", 1, (short) 1);
        
        adminClient.createTopics(Arrays.asList(newClientAlertTopic, crmUpdateTopic, triageRequestTopic));

        // List topics
        Set<String> topics = adminClient.listTopics().names().get();

        // Assert that our topics exist
        assertTrue(topics.contains("new_client_alert"));
        assertTrue(topics.contains("crm_update"));
        assertTrue(topics.contains("triage_request"));

        adminClient.close();
    }
}