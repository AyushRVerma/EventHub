# EventHub

<p align="center">
  <h3 align="center">A Scalable Event Management & Digital Ticketing Backend</h3>
  <p align="center">
    Built with Java, Spring Boot, Spring Security, PostgreSQL, and Docker.
  </p>
</p>

---

## Overview

EventHub is a production-inspired backend application for event management and digital ticketing. It enables organizers to create and manage events, configure multiple ticket types, generate secure QR-code tickets, and validate attendees through a dedicated ticket validation API.

The project follows a layered architecture with a clean separation of concerns, making it scalable, maintainable, and easy to extend.

---

## Features

### Event Management

- Create, update, retrieve, and manage events
- Draft and publish workflow for events
- Publish only completed events
- Event lifecycle management through REST APIs

### Ticket Management

- Multiple ticket categories (VIP, General, Early Bird, etc.)
- Configurable pricing
- Ticket inventory management
- Capacity validation
- Sold-out protection with custom exception handling

### Authentication & Security

- JWT-based authentication
- Spring Security authorization
- Secure REST APIs
- User provisioning and authentication filters
- Role-based access control

### QR Code Management

- Unique QR code generation for every ticket
- QR code linked to ticket information
- Secure ticket identification

### Ticket Validation

- Real-time ticket validation
- Prevent duplicate entry
- Track ticket validation status
- Support multiple validation methods

### Error Handling

- Global exception handling
- Consistent API error responses
- Custom business exceptions
- Standardized Error DTOs

---

# Technology Stack

| Category | Technology |
|-----------|------------|
| Language | Java 21 |
| Framework | Spring Boot |
| Security | Spring Security, JWT |
| Database | PostgreSQL |
| ORM | Spring Data JPA, Hibernate |
| Object Mapping | MapStruct |
| Boilerplate Reduction | Lombok |
| Build Tool | Maven |
| Containerization | Docker, Docker Compose |

---

# Project Architecture

EventHub follows a layered architecture that separates business logic, persistence, and API layers for better maintainability and scalability.

```
src
└── main
    └── java
        └── com.eventbook.EventHub
            ├── config
            ├── controller
            ├── domain
            │   ├── dto
            │   ├── entity
            │   └── models
            ├── exceptions
            ├── filters
            ├── mappers
            ├── repositories
            ├── services
            └── EventHubApplication.java
```

### Architecture Layers

- **Controllers** – Expose REST APIs
- **Services** – Business logic
- **Repositories** – Database access using Spring Data JPA
- **Entities** – Database models
- **DTOs** – Request and response objects
- **Mappers** – Entity ↔ DTO conversion
- **Configuration** – Security, JPA, QR Code configuration
- **Filters** – Request filtering and user provisioning
- **Exceptions** – Global and custom exception handling

---

# REST API Modules

The application provides REST APIs for:

- Authentication
- User Management
- Event Management
- Published Events
- Ticket Management
- QR Code Generation
- Ticket Validation

---

# Key Functionalities

- JWT Authentication
- Secure REST APIs
- QR Code Generation
- Ticket Validation
- Draft & Publish Workflow
- Multiple Ticket Types
- Inventory Management
- Custom Exception Handling
- DTO Mapping
- Layered Architecture
- Dockerized Database
- PostgreSQL Integration

---

# Getting Started

## Prerequisites

Before running the project, ensure you have installed:

- Java 21 (or Java 17+)
- Maven (optional if using Maven Wrapper)
- Docker
- Docker Compose
- Git

---

## Clone Repository

```bash
git clone https://github.com/AyushRVerma/EventHub.git

cd EventHub
```

---

## Start PostgreSQL Database

```bash
docker-compose up -d
```

---

## Build the Project

### Windows

```cmd
mvnw.cmd clean install
```

### Linux/macOS

```bash
./mvnw clean install
```

---

## Run the Application

### Windows

```cmd
mvnw.cmd spring-boot:run
```

### Linux/macOS

```bash
./mvnw spring-boot:run
```

The application will start on

```
http://localhost:8080
```

---

# Database

The application uses PostgreSQL as its primary database.

Database operations are managed using:

- Spring Data JPA
- Hibernate ORM

---

# Security

Security is implemented using:

- Spring Security
- JWT Authentication
- Authentication Filters
- Authorization Rules
- Stateless Sessions

---

# QR Code Workflow

1. User purchases a ticket.
2. A unique QR code is generated.
3. QR code is linked with the ticket.
4. QR code is scanned at the venue.
5. Ticket validation API verifies authenticity.
6. Duplicate scans are rejected.

---

# Error Handling

A centralized `GlobalExceptionHandler` provides standardized API responses.

Custom exceptions include:

- TicketNotFoundException
- TicketSoldOutException
- EventNotFoundException
- UserNotFoundException

All errors are returned using a consistent `ErrorDto` response format.

---

# Future Enhancements

- Email Notifications
- Payment Gateway Integration
- Event Analytics Dashboard
- Seat Selection
- Redis Caching
- Swagger/OpenAPI Documentation
- CI/CD Pipeline
- Kubernetes Deployment

---

# Author

**Ayush Raj Verma**

GitHub:
https://github.com/AyushRVerma

LinkedIn:
https://www.linkedin.com/in/ayush-raj-verma/

