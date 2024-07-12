# Email Microservice

## Overview
This email microservice is designed to handle client communications and system notifications for LyvePulse. It uses an event-driven architecture with Apache Kafka for message processing and Postmark for email delivery.

## Features
- Automated email address generation for new clients
- Processing of client-related events via Kafka topics
- Sending of welcome emails and other notifications using Postmark
- Integration with GuerrillaMail for temporary email address generation (development only)

## Architecture
- Java 17
- Spring Boot 3.2.2
- Apache Kafka for event streaming
- Postmark API for email delivery
- Docker for local Kafka setup

## Prerequisites
- Java Development Kit (JDK) 17
- Docker and Docker Compose
- Maven (or use the included Maven Wrapper)
- PowerShell (for running scripts on Windows)

## Setup

### 1. Clone the repository
```
git clone https://github.com/josephmowjew/postmark-integration.git
cd email-microservice
```

### 2. Configure environment variables
Create a `.env` file in the project root and add the following:
```
POSTMARK_API_TOKEN=your_postmark_api_token
POSTMARK_FROM_EMAIL=your_sender_email@example.com
```

### 3. Start the development environment
Run the following PowerShell script:
```
.\Start-DevEnv.ps1
```
This script will:
- Start Kafka using Docker Compose
- Create necessary Kafka topics
- Run the Spring Boot application

## Usage

### Kafka Topics
- `new_client_alert`: Notifies of new client registrations
- `crm_update`: Handles CRM update events
- `triage_request`: Manages triage requests for incoming emails

### API Endpoints
- POST /api/webhook/email-event: Handles email events from Postmark
- POST /api/email/send-2fa: Sends two-factor authentication emails
- POST /api/email/send-welcome: Sends welcome emails to new clients

## Development Workflow

1. Make changes to the codebase
2. Run tests: `.\mvnw test`
3. Start the development environment: `.\Start-DevEnv.ps1`
4. Stop the development environment: `.\Stop-DevEnv.ps1`

## Testing
- Unit tests: `.\mvnw test`
- Integration tests: Ensure Docker is running, then `.\mvnw verify`

## Important Notes
- The current implementation uses GuerrillaMail for temporary email address generation. This is not suitable for production use.
- TODO: Replace GuerrillaMail with a production-ready email service before deployment.

## Troubleshooting
- If Kafka fails to start, ensure Docker is running and ports 29092 and 22181 are available.
- For Postmark-related issues, verify your API token and sender email in the `.env` file.

## Contributing
Please read CONTRIBUTING.md for details on our code of conduct and the process for submitting pull requests.

## License
This project is licensed under the MIT License - see the LICENSE.md file for details.