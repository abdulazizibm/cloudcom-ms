# Cloud-native E-Commerce Microservices System

CloudCom is a cloud-native e-commerce backend built with Spring Boot, Docker, and AWS, following a clean microservices architecture.
It features secure JWT-based authentication, asynchronous communication with Amazon SQS, and full CI/CD automation using GitHub Actions.
The system simulates a real-world backend for a scalable online shop — including user management, product catalog, shopping cart, order processing, and notification services.
Although originally developed as a learning project to explore AWS, GitHub Actions, containerized microservices, and event-driven design, it follows production-grade principles and patterns throughout.
# Table of Contents
<!-- TOC -->
* [Tech Stack](#tech-stack)
    * [Backend](#backend)
    * [Messaging](#messaging)
    * [Infrastructure](#infrastructure)
* [Architecture Overview](#architecture-overview)
* [Services](#services)
  * [user-communication-service](#user-communication-service)
  * [product-service](#product-service)
  * [cart-service](#cart-service)
  * [order-service](#order-service)
  * [payment-service](#payment-service)
* [GitHub Actions CI/CD Workflow](#github-actions-cicd-workflow)
  * [CI Pipeline](#ciyml)
  * [CD Pipeline](#cdyml)
* [AWS Deployment](#aws-deployment)
<!-- TOC -->
## Tech Stack
### Backend
- Java 21
- Spring Boot 3
- Spring Security (JWT Authentication)
- Spring Data JPA
- Spring Cloud Gateway
- Spring Cloud OpenFeign (Inter-service communication)
- PostgreSQL
- LocalStack (Local AWS simulation)

### Messaging
- Amazon SQS (Asynchronous event-driven communication)

### Infrastructure
- Docker & Docker Compose
- Amazon EC2 (Compute)
- Amazon ECR (Container registry)
- Amazon RDS (PostgreSQL database)
- AWS IAM (Roles & Policies)
- AWS VPC
- AWS Subnets
- AWS Security Groups
- AWS Internet Gateway

## Architecture Overview
The system follows a microservices architecture, where each service is responsible for a  single business  capability. \
Services communicate asynchronously via AWS SQS and synchronously via OpenFeign REST clients. All incoming requests from the frontend/Postman go into Gateway, which routes traffic to the correct service.

<p align="center">
  <img src="docs/architecture.png" alt="Architecture Diagram" width="800"/>
</p>

## Services
### user-communication-service
Handles:
- User Registration
- Login
- JWT issuing
- Password hashing
- User Notification 

Creates a user in the database. Consumes event
```PaymentDoneMessage``` and notifies a customer about successful order

### product-service
Handles:
- Product CRUD
- Price and stock querying

### cart-service
Handles:
- Adding & Removing items
- Fetching product details via OpenFeign → Product Service
- Updating quantities
- Checkout

Publishes event 
```CartCheckedOutMessage```

### order-service
Handles:
- Receiving checkout events
- Creating orders
- Orchestrating order workflow

Consumes event
```CartCheckedOutMessage```\
Publishes event
```OrderCreatedMessage```

### payment-service
Mocks payment process

Consumes event
```OrderCreatedMessage```\
Publishes event
```PaymentDoneMessage```

## GitHub Actions CI/CD Workflow
### CI.yml
Runs automatically on every push to repository.

CI Pipeline:
- builds Maven modules
- builds fat JARs for services (Profile "dist")
- builds Docker Images
- Logs in Amazon ECR using OIDC GitHub -> AWS IAM Role
- Pushes all container images to Amazon ECR

This ensures the system is always buildable, containerized and ready for deployment.

### CD.yml
Runs only when manually triggered. Deploys the latest version of the application to an EC2 instance.

CD Pipeline:
- Uses a dedicated SSH key stored in GitHub Secrets
- Establishes secure connection to the EC2 instance
- On EC2:
   - Pulls the latest code from GitHub
   - Pulls the latest Docker Images from Amazon ECR
   - Stops existing containers (```docker compose down```\)
   - Starts updated services in detached mode (```docker compose up -d```\)

This separation (CI automatic, CD manual) prevents accidental deployments and ensures controlled release cycles.

## AWS Deployment
This project is fully containerized with Docker and deployed on an AWS EC2 instance (m7i-flex.large).
Each microservice runs in its own container, isolated but orchestrated together using Docker Compose.
All container images are built and pushed to Amazon ECR automatically via GitHub Actions.
For persistence, the system uses Amazon RDS (PostgreSQL).
Both EC2 and RDS are deployed inside the same VPC, but placed in separate subnets for security:
- EC2 instance → Public subnet
  - Accessible from the Internet
  - Pulls images from ECR
  - Runs all microservices
- RDS database → Private subnet
  - No public access
  - Accepts connections only from the EC2 security group

With this setup, the database stays protected in a private subnet while EC2 handles all external traffic, pulls the latest images from ECR, and runs the microservices.  
This keeps the system secure, organized, and close to a real-world AWS production environment.




