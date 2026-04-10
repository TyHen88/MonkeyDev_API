# Category Service

Frontend-facing contract for browsing and managing product categories.

## Base URL

`/api/wb/v1/categories`

## Response Envelope

All JSON endpoints return `ApiResponse<Object>`.

## Access Rules

- `GET /categories` is public.
- `GET /categories/{id}` is public.
- `POST /categories` requires `ADMIN_MANAGE`.
- `PUT /categories/{id}` requires `ADMIN_MANAGE`.
- `DELETE /categories/{id}` requires `ADMIN_MANAGE`.
- Product-category link operations require `PRODUCT_WRITE` or `ADMIN_MANAGE`.

## Request Model

`CategoryRequestDto`

```json
{
  "name": "Electronics",
  "slug": "electronics",
  "description": "Consumer electronic products",
  "imageUrl": "https://placehold.co/600x400/png?text=Electronics",
  "isActive": true,
  "parentId": null
}
```

## Response Model

`CategorySummaryDto`

```json
{
  "id": 1,
  "name": "Electronics",
  "slug": "electronics"
}
```

## Endpoints

### Get all categories

`GET /api/wb/v1/categories`

### Get category by id

`GET /api/wb/v1/categories/{id}`

### Create category

`POST /api/wb/v1/categories`

### Update category

`PUT /api/wb/v1/categories/{id}`

### Delete category

`DELETE /api/wb/v1/categories/{id}`

### Remove product from category

`DELETE /api/wb/v1/categories/{categoryId}/products/{productId}`

### Add product to category

`POST /api/wb/v1/categories/{categoryId}/products/{productId}`

### Bulk delete products from category

`DELETE /api/wb/v1/categories/{categoryId}/products/bulk-delete`

Request body:

```json
[1, 2, 3]
```

### Bulk add products to category

`POST /api/wb/v1/categories/{categoryId}/products/bulk-add`

Request body:

```json
[1, 2, 3]
```

## Frontend Notes

- Use category slugs for filters, navigation, and storefront URLs.
- The public category list is enough for product forms and category menus.
- Use `bulk-add` and `bulk-delete` for admin product assignment screens.

