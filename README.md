# EventHub

> A Spring Boot backend for managing events, selling digital tickets, and validating attendees at the venue.

EventHub gives organizers a secure API for creating and operating events, while attendees can discover published events, purchase tickets, retrieve QR codes, and have tickets validated at entry. The project uses a layered architecture, PostgreSQL persistence, JWT authentication, Redis-backed rate limiting, and Docker-based local infrastructure.

## Features

### Event management

- Create an event with one or more ticket types.
- View, update, list, and delete events belonging to the authenticated organizer.
- Maintain the event lifecycle with `DRAFT`, `PUBLISHED`, `CANCELLED`, and `COMPLETED` statuses.
- Validate event and ticket-sale dates.
- Add, update, or remove ticket types as part of an event update.

### Public discovery and ticketing

- Browse and paginate published events without authentication.
- Search published events with the `q` query parameter.
- Purchase a ticket for an event ticket type.
- Enforce ticket availability and return a domain error when inventory is exhausted.
- List an attendee’s tickets and retrieve ticket details.

### QR codes and venue validation

- Generate a QR code when a ticket is purchased.
- Download a ticket’s QR code as a PNG image.
- Validate entry by QR scan or manually with a ticket ID.
- Record validation method and status, preventing duplicate entry.

### Security and reliability

- OAuth2 resource-server security with JWT bearer tokens.
- Role-based authorization for organizers and attendees.
- Stateless Spring Security configuration with a user-provisioning filter.
- Redis-backed rate limiting on ticket purchases: **5 requests per user and purchase path per 60 seconds**.
- Request validation, domain-specific exceptions, and consistent error responses.
- Swagger/OpenAPI documentation with bearer-token support.

## Technology stack

| Area | Technology |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 3 |
| API | Spring Web / REST |
| Security | Spring Security, OAuth2 Resource Server, JWT |
| Database | PostgreSQL |
| Persistence | Spring Data JPA, Hibernate |
| Cache / rate limiting | Redis |
| API docs | springdoc-openapi / Swagger UI |
| Mapping | MapStruct |
| QR generation | ZXing |
| Build | Maven Wrapper |
| Local infrastructure | Docker Compose |
| Identity provider | Keycloak |

## Architecture

```text
src/main/java/com/eventbook/EventHub
├── Config/          # Security, Redis, JPA, QR-code, and OpenAPI configuration
├── controller/      # REST endpoints
├── domain/
│   ├── DTOs/        # API request and response payloads
│   ├── entity/      # JPA entities and enums
│   └── models/      # Service-layer request models
├── exceptions/      # Domain exceptions
├── filters/         # User provisioning and rate limiting
├── mappers/         # MapStruct DTO/entity mapping
├── repositories/    # Spring Data repositories
├── services/        # Business rules and workflows
└── EventHubApplication.java
```

The request flow is:

```text
Client → Security filters → Controller → Service → Repository → PostgreSQL
                              │              │
                              │              └→ Redis (rate limiting / caching)
                              └→ DTO mapper / standardized error response
```

## Domain model

- **User** — authenticated organizer, attendee, or staff member.
- **Event** — has an organizer, schedule, venue, lifecycle status, and ticket types.
- **TicketType** — a ticket tier such as General, VIP, or Early Bird, with price and available capacity.
- **Ticket** — a purchased ticket owned by an attendee.
- **QrCode** — a unique code generated for a ticket.
- **TicketValidation** — an audit record for a QR or manual validation attempt.

### Status values

| Entity | Values |
| --- | --- |
| Event | `DRAFT`, `PUBLISHED`, `CANCELLED`, `COMPLETED` |
| Ticket | `PURCHASED`, `CANCELLED` |
| Ticket validation | `VALID`, `INVALID`, `EXPIRED` |
| Validation method | `QR_SCAN`, `MANUAL` |

## Prerequisites

- Java 21
- Docker and Docker Compose
- Git
- A Keycloak realm named `event-ticket-platform` configured to issue JWTs

Maven is optional because the repository includes Maven Wrapper scripts.

## Getting started

### 1. Clone the repository

```bash
git clone https://github.com/AyushRVerma/EventHub.git
cd EventHub
```

### 2. Start the local services

```bash
docker compose up -d
```

This starts the following development services:

| Service | Address | Purpose |
| --- | --- | --- |
| PostgreSQL | `localhost:5432` | Primary application database |
| Redis | `localhost:6379` | Rate-limit counter storage and cache support |
| Adminer | `http://localhost:8888` | Database administration UI |
| Keycloak | `http://localhost:9090` | Local identity provider |

> The compose file contains development-only credentials. Use environment variables or a secrets manager before deploying anywhere other than local development.

### 3. Configure Keycloak

Create or import the `event-ticket-platform` realm, configure a client for this API, and ensure its JWT issuer matches:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9090/realms/event-ticket-platform
```

The API expects organizer and attendee roles. Include the appropriate role in the access token used for each request.

### 4. Configure the application

The default development configuration is in `src/main/resources/application.properties`.

| Setting | Default |
| --- | --- |
| PostgreSQL URL | `jdbc:postgresql://localhost:5432/postgres` |
| PostgreSQL user | `postgres` |
| Redis host / port | `localhost:6379` |
| JWT issuer | Keycloak on port `9090` |

For a real environment, externalize database passwords, JWT issuer URLs, CORS origins, and all other deployment-specific values.

### 5. Run the application

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

macOS / Linux:

```bash
./mvnw spring-boot:run
```

The API runs on `http://localhost:8080`.

## API documentation

After starting the application, open Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

Use the **Authorize** button to provide a JWT bearer token for protected endpoints.

## API overview

All request and response schemas are available in Swagger UI. UUID path parameters below are shown as `{id}`.

### Public endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/v1/published-events` | List published events; supports pagination. |
| `GET` | `/api/v1/published-events?q={query}` | Search published events. |
| `GET` | `/api/v1/published-events/{eventId}` | Get a published event and its details. |

### Organizer event endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/v1/events` | Create an event, including its initial ticket types. |
| `GET` | `/api/v1/events` | List events owned by the authenticated organizer. |
| `GET` | `/api/v1/events/{eventId}` | Get an organizer-owned event. |
| `PUT` | `/api/v1/events/{eventId}` | Update event details and ticket types. |
| `DELETE` | `/api/v1/events/{eventId}` | Delete an organizer-owned event. |

### Attendee ticket endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/v1/events/{eventId}/ticket-types/{ticketTypeId}/tickets` | Purchase a ticket; requires the attendee role and is rate-limited. |
| `GET` | `/api/v1/tickets` | List tickets owned by the authenticated user. |
| `GET` | `/api/v1/tickets/{ticketId}` | Get one owned ticket. |
| `GET` | `/api/v1/tickets/{ticketId}/qr-codes` | Download the ticket QR code as a PNG. |

### Ticket validation endpoint

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/v1/ticket-validations` | Validate a ticket by QR code or manually. |

Example validation request:

```json
{
  "id": "11111111-1111-1111-1111-111111111111",
  "method": "QR_SCAN"
}
```

For manual validation, set `method` to `MANUAL` and provide a ticket ID. For QR validation, provide the QR code ID.

### Example event creation request

```json
{
  "name": "Spring Music Festival",
  "start": "2026-12-05T18:00:00",
  "end": "2026-12-05T23:30:00",
  "venue": "City Arena",
  "salesStart": "2026-10-01T00:00:00",
  "salesEnd": "2026-12-04T23:59:59",
  "status": "PUBLISHED",
  "ticketTypes": [
    {
      "name": "General Admission",
      "price": 999.0,
      "description": "Standard entry",
      "totalAvailable": 500
    },
    {
      "name": "VIP",
      "price": 2499.0,
      "description": "Priority entry and VIP area access",
      "totalAvailable": 50
    }
  ]
}
```

## Authentication and authorization

Protected routes require this header:

```http
Authorization: Bearer <access-token>
```

- Public published-event endpoints do not require a token.
- Event creation is restricted to users with the `ORGANIZER` role; organizer ownership is also enforced in the service layer for event operations.
- Ticket purchase is restricted to users with the `ATTENDEE` role.
- Other protected endpoints require an authenticated JWT.

The API is stateless and CORS is configured for local frontend origins on ports `3000`, `5173`, and `5174`.

## Rate limiting

Ticket purchases are protected by the `RateLimiterFilter`:

- Scope: `POST /api/v1/events/{eventId}/ticket-types/{ticketTypeId}/tickets`
- Key: authenticated user and request path
- Limit: 5 requests
- Window: 60 seconds
- Response when exceeded: `429 Too Many Requests`

## Error handling

`GlobalExceptionHandler` maps business and validation failures into a consistent error response. Examples of handled domain errors include:

- Event, ticket, ticket type, QR code, and user not found
- Event update failures and invalid event dates
- Sold-out ticket types
- Unauthorized organizer ticket purchase
- QR code generation failures

## Testing

Run the test suite with:

Windows:

```powershell
.\mvnw.cmd test
```

macOS / Linux:

```bash
./mvnw test
```

The current test suite includes application-context coverage plus event controller and event service tests.

## Suggested next steps

- Add a payment gateway and payment-state workflow before completing ticket purchases.
- Send email confirmations with ticket and QR-code delivery.
- Add CI/CD, environment profiles, and production secret management.
- Expand authorization rules for venue staff and validation operations.
- Add event analytics, cancellation/refund workflows, and seat selection.
- Add integration tests with PostgreSQL, Redis, and Keycloak containers.

## Author

**Ayush Raj Verma**

- GitHub: [@AyushRVerma](https://github.com/AyushRVerma)
- Repository: [AyushRVerma/EventHub](https://github.com/AyushRVerma/EventHub)
