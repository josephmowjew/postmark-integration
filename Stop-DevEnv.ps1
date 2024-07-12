# Stop Spring Boot application
Write-Host "Stopping Spring Boot application..."
Stop-Process -Name "java" -ErrorAction SilentlyContinue

# Stop Kafka
Write-Host "Stopping Kafka..."
docker-compose down