# Admin User Service

Frontend-facing contract for admin user management.

## Base URL

`/api/wb/v1/admin/users`

## Response Envelope

All JSON endpoints return `ApiResponse<Object>`.

## Access Rules

- All endpoints require `ADMIN_MANAGE`.

## Request Model

`UserAdminRequestDto`

```json
{
  "fullName": "Admin User",
  "username": "admin_user",
  "email": "admin@example.com",
  "password": "securePassword123",
  "role": "ADMIN",
  "roles": ["ADMIN", "PRODUCT_WRITE"],
  "active": true
}
```

## Response Handling

The list endpoint returns a paginated wrapper:

```json
{
  "data": [],
  "pagination": {
    "current_page": 1,
    "page_size": 10
  }
}
```

## Endpoints

### Get all users

`GET /api/wb/v1/admin/users`

Query params:

- `isActive`
- `search`
- `sort`
- `page`
- `size`

### Register user

`POST /api/wb/v1/admin/users/register`

### Update user status

`PUT /api/wb/v1/admin/users/update-status/{id}/{status}`

## Frontend Notes

- Use this service for admin dashboards only.
- The list endpoint is paginated and should drive table paging controls.
- The register endpoint is for back-office user creation, not public signup.

