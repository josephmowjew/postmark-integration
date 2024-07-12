# Start Kafka
Write-Host "Starting Kafka..."
docker-compose up -d

# Wait for Kafka to be ready
Write-Host "Waiting for Kafka to be ready..."
Start-Sleep -Seconds 10

# Create Kafka topics
Write-Host "Creating Kafka topics..."
docker-compose exec kafka kafka-topics --create --topic new_client_alert --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
docker-compose exec kafka kafka-topics --create --topic crm_update --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
docker-compose exec kafka kafka-topics --create --topic triage_request --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1

# Start Spring Boot application using Maven Wrapper
Write-Host "Starting Spring Boot application..."
.\mvnw spring-boot:run