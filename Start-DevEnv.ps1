# Start Kafka
Write-Host "Starting Kafka..."
docker-compose up -d

# Wait for Kafka to be ready
Write-Host "Waiting for Kafka to be ready..."
$maxAttempts = 30
$attempt = 0
$kafkaReady = $false

while (-not $kafkaReady -and $attempt -lt $maxAttempts) {
    try {
        $result = docker-compose exec kafka kafka-topics --list --bootstrap-server localhost:9092 2>&1
        if ($result -notlike "*ERROR*") {
            $kafkaReady = $true
            Write-Host "Kafka is ready!"
        }
    } catch {
        Write-Host "Kafka not ready yet. Retrying... (Attempt $($attempt + 1) of $maxAttempts)"
    }
    $attempt++
    if (-not $kafkaReady) {
        Start-Sleep -Seconds 5
    }
}

if (-not $kafkaReady) {
    Write-Host "Error: Kafka did not become ready in time. Please check your Kafka configuration and try again."
    exit 1
}

# Create Kafka topics
Write-Host "Creating Kafka topics..."
docker-compose exec kafka kafka-topics --create --if-not-exists --topic new_client_alert --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
docker-compose exec kafka kafka-topics --create --if-not-exists --topic crm_update --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
docker-compose exec kafka kafka-topics --create --if-not-exists --topic triage_request --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1

# Start Spring Boot application using Maven Wrapper
Write-Host "Starting Spring Boot application..."
.\mvnw spring-boot:run