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
