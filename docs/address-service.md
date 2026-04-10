# Address Service

Frontend-facing contract for user addresses.

## Base URL

`/api/wb/v1/addresses`

## Response Envelope

All JSON endpoints return `ApiResponse<Object>`.

## Access Rules

- All address endpoints require authentication.

## Request Model

`AddressRequestDto`

```json
{
  "type": "home",
  "fullName": "John Doe",
  "phone": "012345678",
  "addressLine1": "123 Main Street",
  "addressLine2": "District 1",
  "city": "Phnom Penh",
  "state": "Phnom Penh",
  "postalCode": "12000",
  "country": "Cambodia",
  "isDefault": true
}
```

## Response Model

`AddressResponseDto`

```json
{
  "id": 1,
  "type": "home",
  "fullName": "John Doe",
  "phone": "012345678",
  "addressLine1": "123 Main Street",
  "addressLine2": "District 1",
  "city": "Phnom Penh",
  "state": "Phnom Penh",
  "postalCode": "12000",
  "country": "Cambodia",
  "isDefault": true,
  "createdAt": "2026-04-10T10:30:00",
  "updatedAt": "2026-04-10T10:30:00"
}
```

## Endpoints

### Create address

`POST /api/wb/v1/addresses`

### Get address by id

`GET /api/wb/v1/addresses/{id}`

### Get all user addresses

`GET /api/wb/v1/addresses/all-addresses`

### Update address

`PUT /api/wb/v1/addresses/{id}`

### Delete address

`DELETE /api/wb/v1/addresses/{id}`

### Set primary address

`PATCH /api/wb/v1/addresses/{id}/is-primary`

## Frontend Notes

- Use this service for profile shipping addresses and checkout address selection.
- Treat `isDefault` as the primary address flag.
- Render `all-addresses` directly into an address picker or address book view.

