# User Service

Frontend-facing contract for the authenticated user profile and user lookup.

## Base URL

`/api/wb/v1/user`

## Response Envelope

All JSON endpoints return `ApiResponse<Object>`.

## Access Rules

- All endpoints require authentication.
- `POST /create` additionally requires `ADMIN_MANAGE`.

## Request Model

`UserRequestDto`

```json
{
  "fullName": "John Doe",
  "username": "john_doe",
  "email": "john.doe@example.com",
  "password": "securePassword123",
  "profileImageUrl": "https://placehold.co/256x256/png?text=John+Doe"
}
```

## Response Model

`UserResponseDto`

```json
{
  "id": 1,
  "fullName": "John Doe",
  "username": "john_doe",
  "email": "john.doe@example.com",
  "active": true,
  "createdAt": "2026-04-10T10:30:00",
  "updatedAt": "2026-04-10T10:30:00",
  "role": "USER",
  "roles": ["USER"],
  "permissions": [],
  "profileImageUrl": "https://placehold.co/256x256/png?text=John+Doe",
  "authProvider": "LOCAL",
  "addresses": []
}
```

## Endpoints

### Get user profile

`GET /api/wb/v1/user/profile`

### Create user

`POST /api/wb/v1/user/create`

### Update user

`PUT /api/wb/v1/user/update-profile`

### Search user

`GET /api/wb/v1/user/search?email=john.doe@example.com`

or

`GET /api/wb/v1/user/search?username=john_doe`

## Frontend Notes

- Use `profile` for the current session user.
- `search` is useful for admin and support lookups.
- The response includes embedded addresses for profile screens.

