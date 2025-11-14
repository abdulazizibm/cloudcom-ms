# Cloud-native E-Commerce Microservices System

A cloud-native e-commerce backend built with Spring Boot, Docker, and AWS, demonstrating microservice architecture, asynchronous communication with Amazon SQS, secure JWT authentication, and CI/CD with GitHub Actions.
This project aims to simulate a realistic backend system for a scalable online shop — complete with user management, product catalog, shopping cart, ordering, and notification subsystems.

## Tech Stack
### Backend
- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- Spring Cloud Gateway
- PostgreSQL

### Messaging
- AWS SQS

### Infrastructure
- Docker & Docker Compose
- AWS Elastic Compute Cloud (EC2)
- AWS Elastic Container Registry (ECR)
- GitHub Actions CI/CD
- IAM Roles
- VPC

## Services

### 1. User-Communication-Service
Handles:
- User Registration
- Login
- JWT issuing
- Password hashing
- User Notification 

Consumes event
```PaymentDoneMessage``` and notifies a customer  about successful order

### 2. Product-Service
Handles:
- Product CRUD
- Price and stock querying

### 3. Cart-Service
Handles:
- Adding & Removing items
- Updating quantities
- Checkout

Publishes event 
```CartCheckedOutMessage```

### 4. Order-Service
Handles:
- Receiving checkout events
- Creating orders
- Orchestrating order workflow

Consumes event
```CartCheckedOutMessage```\
Publishes event
```OrderCreatedMessage```

### 5. Payment-Service
Mocks payment process

Consumes event
```OrderCreatedMessage```\
Publishes event
```PaymentDoneMessage```

