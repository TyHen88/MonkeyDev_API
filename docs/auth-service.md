# Authentication Service

Frontend-facing auth contract for login, registration, token refresh, and password flows.

## Base URL

`/api/wb/v1/auth`

## Response Envelope

All JSON endpoints return the shared `ApiResponse<Object>` shape.

```json
{
  "message": "Success",
  "statusCode": 200,
  "timestamp": "2026-04-10T10:30:00",
  "data": {}
}
```

## Access Rules

- `POST /login` is public.
- `POST /register` is public.
- `POST /refresh` is public.
- `POST /encrypt` is public.
- `GET /generate-password` is public.
- `POST /forgot-password` is public.
- `POST /reset-password` is public.
- `PATCH /update-password` requires an authenticated user.
- `POST /setup-password` requires an authenticated user.

## Endpoints

### Login

`POST /api/wb/v1/auth/login`

Request:

```json
{
  "username": "john_doe",
  "password": "securePassword123"
}
```

Notes:

- `username` can be username or email.
- Use this response to store access and refresh tokens.

### Register

`POST /api/wb/v1/auth/register`

Request:

```json
{
  "fullName": "John Doe",
  "username": "john_doe",
  "email": "john.doe@example.com",
  "password": "securePassword123",
  "role": "USER"
}
```

Notes:

- `role` is optional for frontend registration.
- The backend may normalize the role to the public registration role.

### Refresh Token

`POST /api/wb/v1/auth/refresh`

Request:

```json
{
  "refresh_token": "refresh_token_here"
}
```

### Encrypt Password

`POST /api/wb/v1/auth/encrypt`

Request body is a raw string, not an object:

```json
"plainTextPassword"
```

### Generate Password

`GET /api/wb/v1/auth/generate-password?length=12`

Notes:

- `length` is optional.
- Default length is `12`.

### Forgot Password

`POST /api/wb/v1/auth/forgot-password`

Request:

```json
{
  "email": "john.doe@example.com"
}
```

### Reset Password

`POST /api/wb/v1/auth/reset-password`

Request:

```json
{
  "token": "reset-token",
  "new_password": "newPassword123",
  "confirm_password": "newPassword123"
}
```

### Update Password

`PATCH /api/wb/v1/auth/update-password`

Request:

```json
{
  "old_password": "oldPassword123",
  "new_password": "newPassword123",
  "confirm_password": "newPassword123"
}
```

### Setup Password

`POST /api/wb/v1/auth/setup-password`

Request:

```json
{
  "new_password": "newPassword123",
  "confirm_password": "newPassword123"
}
```

## Frontend Notes

- Use `login` for local auth and `refresh` for silent token renewal.
- Keep `encrypt` off the normal login flow unless you explicitly need password encoding on the client side.
- Use `forgot-password` and `reset-password` for the reset flow after email delivery.
- Redirect to login on `401`.
- Show permission denied UI on `403`.

