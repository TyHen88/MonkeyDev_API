# Seller Profile Service

Frontend-facing contract for seller profile management.

## Base URL

`/api/wb/v1/seller-profiles`

## Response Envelope

All JSON endpoints return `ApiResponse<Object>`.

## Access Rules

- `GET /seller-profiles/{id}` is public.
- `GET /seller-profiles/user/{userId}` is public.
- `GET /seller-profiles` is public and paginated.
- `POST /seller-profiles` requires authentication.
- `GET /seller-profiles/my-profile` requires authentication.
- `PUT /seller-profiles` requires authentication.
- `DELETE /seller-profiles` requires authentication.
- `POST /seller-profiles/{id}/verify` requires `ADMIN_MANAGE`.
- `PUT /seller-profiles/{id}/rating` requires `ADMIN_MANAGE`.
- `POST /seller-profiles/{id}/increment-sales` requires `ADMIN_MANAGE`.

## Request Models

### Create

`SellerProfileRequestDto`

```json
{
  "storeName": "Demo Store",
  "storeDescription": "Retail demo seller profile",
  "storeLogoUrl": "https://placehold.co/256x256/png?text=Demo+Store",
  "storeBannerUrl": "https://placehold.co/1200x400/png?text=Banner",
  "businessRegistrationNumber": "BRN-001",
  "businessAddress": "Phnom Penh",
  "contactPhone": "012345678",
  "contactEmail": "store@example.com",
  "establishedDate": "2026-04-10T10:30:00",
  "returnPolicy": "7 day return",
  "shippingPolicy": "Standard shipping"
}
```

### Update

`SellerProfileUpdateRequestDto`

```json
{
  "id": 1,
  "storeName": "Demo Store Plus",
  "storeDescription": "Updated store profile",
  "storeLogoUrl": "https://placehold.co/256x256/png?text=Demo+Store+Plus",
  "storeBannerUrl": "https://placehold.co/1200x400/png?text=Banner",
  "businessRegistrationNumber": "BRN-001",
  "businessAddress": "Phnom Penh",
  "contactPhone": "012345678",
  "contactEmail": "store@example.com",
  "returnPolicy": "14 day return",
  "shippingPolicy": "Express shipping"
}
```

## Response Model

`SellerProfileResponseDto`

```json
{
  "id": 1,
  "userId": 10,
  "username": "mock_seller",
  "userEmail": "seller@example.com",
  "storeName": "Demo Store",
  "storeDescription": "Retail demo seller profile",
  "storeLogoUrl": "https://placehold.co/256x256/png?text=Demo+Store",
  "storeBannerUrl": "https://placehold.co/1200x400/png?text=Banner",
  "sellerRating": 4.8,
  "totalReviews": 120,
  "totalSales": 500,
  "verifiedSeller": true,
  "businessRegistrationNumber": "BRN-001",
  "businessAddress": "Phnom Penh",
  "contactPhone": "012345678",
  "contactEmail": "store@example.com",
  "establishedDate": "2026-04-10T10:30:00",
  "returnPolicy": "7 day return",
  "shippingPolicy": "Standard shipping",
  "active": true,
  "verificationDate": "2026-04-10T10:30:00",
  "createdAt": "2026-04-10T10:30:00",
  "updatedAt": "2026-04-10T10:30:00"
}
```

## Endpoints

### Create seller profile

`POST /api/wb/v1/seller-profiles`

### Get seller profile by id

`GET /api/wb/v1/seller-profiles/{id}`

### Get seller profile by user id

`GET /api/wb/v1/seller-profiles/user/{userId}`

### Get my profile

`GET /api/wb/v1/seller-profiles/my-profile`

### Update seller profile

`PUT /api/wb/v1/seller-profiles`

### Delete seller profile

`DELETE /api/wb/v1/seller-profiles`

### Get all seller profiles

`GET /api/wb/v1/seller-profiles`

Query params:

- `isActive`
- `isVerified`
- `search`
- `sort`
- `page`
- `size`

### Verify seller

`POST /api/wb/v1/seller-profiles/{id}/verify`

### Update seller rating

`PUT /api/wb/v1/seller-profiles/{id}/rating?newRating=4.9&totalReviews=150`

### Increment seller sales

`POST /api/wb/v1/seller-profiles/{id}/increment-sales`

## Frontend Notes

- Use `my-profile` for seller settings pages.
- Use the public list for storefront seller cards and filters.
- Use the paginated response `data` and `pagination` keys for seller listings.

