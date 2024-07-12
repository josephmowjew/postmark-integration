# Wait for Kafka to be ready
Start-Sleep -Seconds 10

# Create topics
docker-compose exec kafka kafka-topics --create --topic new_client_alert --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
docker-compose exec kafka kafka-topics --create --topic crm_update --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
docker-compose exec kafka kafka-topics --create --topic triage_request --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1

Write-Host "Kafka topics created."