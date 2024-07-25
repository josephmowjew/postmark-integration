#!/bin/bash

# Wait for Kafka to be ready
sleep 10

# Create Kafka topics
kafka-topics --create --topic new_client_alert --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
kafka-topics --create --topic crm_update --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
kafka-topics --create --topic triage_request --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1

echo "Kafka topics created."