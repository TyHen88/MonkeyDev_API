<!-- 5e5b242e-95bf-4f7f-9abc-6e3c3e69d1b3 f2041d7d-bd88-4f87-81f6-aec7f1d58c4e -->
# E-Commerce API Endpoints Implementation Plan

## API Base Pattern

- **Base URL**: `/api/wb/v1/{resource}`
- **Response Format**: `ApiResponse<T>` wrapper
- **Authentication**: JWT Bearer token (except public endpoints)
- **Documentation**: Swagger/OpenAPI annotations

---

## 1. Product Management

### 1.1 Products Controller (`/api/wb/v1/products`)

**Public Endpoints:**

- `GET /api/wb/v1/products` - Get all products (with pagination, filtering, sorting)
- Query params: `page`, `size`, `sort`, `category`, `minPrice`, `maxPrice`, `isActive`, `isFeatured`, `search`
- `GET /api/wb/v1/products/{id}` - Get product by ID
- `GET /api/wb/v1/products/slug/{slug}` - Get product by slug
- `GET /api/wb/v1/products/featured` - Get featured products
- `GET /api/wb/v1/products/new` - Get new products
- `GET /api/wb/v1/products/search` - Search products by keyword
- Query params: `q`, `page`, `size`

**Seller/Admin Endpoints:**

- `POST /api/wb/v1/products` - Create product (seller/admin)
- `PUT /api/wb/v1/products/{id}` - Update product (owner/admin)
- `DELETE /api/wb/v1/products/{id}` - Delete product (owner/admin)
- `PATCH /api/wb/v1/products/{id}/status` - Update product status (active/inactive)
- `PATCH /api/wb/v1/products/{id}/featured` - Toggle featured status (admin only)

---

## 2. Category Management

### 2.1 Categories Controller (`/api/wb/v1/categories`)

**Public Endpoints:**

- `GET /api/wb/v1/categories` - Get all categories
- `GET /api/wb/v1/categories/{id}` - Get category by ID
- `GET /api/wb/v1/categories/{id}/products` - Get products in category
- `GET /api/wb/v1/categories/tree` - Get category tree (hierarchical)

**Admin Endpoints:**

- `POST /api/wb/v1/categories` - Create category
- `PUT /api/wb/v1/categories/{id}` - Update category
- `DELETE /api/wb/v1/categories/{id}` - Delete category
- `POST /api/wb/v1/categories/{id}/products/{productId}` - Add product to category
- `DELETE /api/wb/v1/categories/{id}/products/{productId}` - Remove product from category

---

## 3. Product Images

### 3.1 Product Images Controller (`/api/wb/v1/products/{productId}/images`)

**Public Endpoints:**

- `GET /api/wb/v1/products/{productId}/images` - Get all images for product

**Seller/Admin Endpoints:**

- `POST /api/wb/v1/products/{productId}/images` - Upload product image
- `PUT /api/wb/v1/products/{productId}/images/{imageId}` - Update image (alt text, order)
- `DELETE /api/wb/v1/products/{productId}/images/{imageId}` - Delete image
- `PATCH /api/wb/v1/products/{productId}/images/{imageId}/primary` - Set as primary image

---

## 4. Product Variations

### 4.1 Product Variations Controller (`/api/wb/v1/products/{productId}/variations`)

**Public Endpoints:**

- `GET /api/wb/v1/products/{productId}/variations` - Get all variations for product

**Seller/Admin Endpoints:**

- `POST /api/wb/v1/products/{productId}/variations` - Create product variation
- `PUT /api/wb/v1/products/{productId}/variations/{variationId}` - Update variation
- `DELETE /api/wb/v1/products/{productId}/variations/{variationId}` - Delete variation

---

## 5. Inventory Management

### 5.1 Inventory Controller (`/api/wb/v1/inventory`)

**Seller/Admin Endpoints:**

- `GET /api/wb/v1/inventory` - Get inventory list (with filtering)
- Query params: `productId`, `lowStock`, `location`
- `GET /api/wb/v1/inventory/{id}` - Get inventory by ID
- `POST /api/wb/v1/inventory` - Create inventory record
- `PUT /api/wb/v1/inventory/{id}` - Update inventory
- `PATCH /api/wb/v1/inventory/{id}/quantity` - Update quantity (with optimistic locking)
- `GET /api/wb/v1/inventory/low-stock` - Get low stock items
- `POST /api/wb/v1/inventory/{id}/reserve` - Reserve quantity
- `POST /api/wb/v1/inventory/{id}/release` - Release reserved quantity

---

## 6. Shopping Cart

### 6.1 Shopping Cart Controller (`/api/wb/v1/cart`)

**Customer Endpoints:**

- `GET /api/wb/v1/cart` - Get current user's cart
- `POST /api/wb/v1/cart/items` - Add item to cart
- `PUT /api/wb/v1/cart/items/{itemId}` - Update cart item quantity
- `DELETE /api/wb/v1/cart/items/{itemId}` - Remove item from cart
- `DELETE /api/wb/v1/cart` - Clear cart
- `GET /api/wb/v1/cart/count` - Get cart item count
- `GET /api/wb/v1/cart/total` - Get cart total

---

## 7. Orders

### 7.1 Orders Controller (`/api/wb/v1/orders`)

**Customer Endpoints:**

- `POST /api/wb/v1/orders` - Create order from cart
- `GET /api/wb/v1/orders` - Get user's orders (with pagination)
- Query params: `page`, `size`, `status`
- `GET /api/wb/v1/orders/{id}` - Get order by ID
- `GET /api/wb/v1/orders/{orderNumber}` - Get order by order number
- `PATCH /api/wb/v1/orders/{id}/cancel` - Cancel order (if status allows)

**Admin/Seller Endpoints:**

- `GET /api/wb/v1/orders/all` - Get all orders (admin)
- Query params: `page`, `size`, `status`, `userId`, `dateFrom`, `dateTo`
- `PATCH /api/wb/v1/orders/{id}/status` - Update order status
- `GET /api/wb/v1/orders/seller` - Get seller's orders (for products they sell)

---

## 8. Order Items

### 8.1 Order Items Controller (`/api/wb/v1/orders/{orderId}/items`)

**Public Endpoints:**

- `GET /api/wb/v1/orders/{orderId}/items` - Get order items

---

## 9. Addresses

### 9.1 Addresses Controller (`/api/wb/v1/addresses`)

**Customer Endpoints:**

- `GET /api/wb/v1/addresses` - Get user's addresses
- `GET /api/wb/v1/addresses/{id}` - Get address by ID
- `POST /api/wb/v1/addresses` - Create address
- `PUT /api/wb/v1/addresses/{id}` - Update address
- `DELETE /api/wb/v1/addresses/{id}` - Delete address
- `PATCH /api/wb/v1/addresses/{id}/default` - Set as default address

---

## 10. Payments

### 10.1 Payments Controller (`/api/wb/v1/payments`)

**Customer Endpoints:**

- `POST /api/wb/v1/payments` - Create payment for order
- `GET /api/wb/v1/payments/{id}` - Get payment by ID
- `GET /api/wb/v1/payments/order/{orderId}` - Get payment by order ID
- `POST /api/wb/v1/payments/{id}/verify` - Verify payment status
- `POST /api/wb/v1/payments/{id}/refund` - Request refund

**Admin Endpoints:**

- `GET /api/wb/v1/payments` - Get all payments (with filtering)
- Query params: `page`, `size`, `status`, `orderId`, `dateFrom`, `dateTo`

---

## 11. Saved Payment Methods

### 11.1 Saved Payment Methods Controller (`/api/wb/v1/payment-methods`)

**Customer Endpoints:**

- `GET /api/wb/v1/payment-methods` - Get user's saved payment methods
- `POST /api/wb/v1/payment-methods` - Save payment method
- `DELETE /api/wb/v1/payment-methods/{id}` - Delete saved payment method
- `PATCH /api/wb/v1/payment-methods/{id}/default` - Set as default

---

## 12. Product Reviews

### 12.1 Product Reviews Controller (`/api/wb/v1/products/{productId}/reviews`)

**Public Endpoints:**

- `GET /api/wb/v1/products/{productId}/reviews` - Get product reviews
- Query params: `page`, `size`, `rating`, `verified`
- `GET /api/wb/v1/products/{productId}/reviews/{reviewId}` - Get review by ID
- `GET /api/wb/v1/products/{productId}/reviews/statistics` - Get review statistics (average rating, count)

**Customer Endpoints:**

- `POST /api/wb/v1/products/{productId}/reviews` - Create review (requires order)
- `PUT /api/wb/v1/products/{productId}/reviews/{reviewId}` - Update own review
- `DELETE /api/wb/v1/products/{productId}/reviews/{reviewId}` - Delete own review
- `POST /api/wb/v1/products/{productId}/reviews/{reviewId}/helpful` - Mark review as helpful

**Admin Endpoints:**

- `PATCH /api/wb/v1/products/{productId}/reviews/{reviewId}/approve` - Approve/reject review

---

## 13. Wishlist

### 13.1 Wishlist Controller (`/api/wb/v1/wishlist`)

**Customer Endpoints:**

- `GET /api/wb/v1/wishlist` - Get user's wishlist
- `POST /api/wb/v1/wishlist/products/{productId}` - Add product to wishlist
- `DELETE /api/wb/v1/wishlist/products/{productId}` - Remove product from wishlist
- `GET /api/wb/v1/wishlist/count` - Get wishlist count
- `POST /api/wb/v1/wishlist/products/{productId}/move-to-cart` - Move wishlist item to cart

---

## 14. Coupons

### 14.1 Coupons Controller (`/api/wb/v1/coupons`)

**Public Endpoints:**

- `GET /api/wb/v1/coupons/validate/{code}` - Validate coupon code
- Query params: `amount` (purchase amount)

**Customer Endpoints:**

- `GET /api/wb/v1/coupons/available` - Get available coupons

**Admin Endpoints:**

- `GET /api/wb/v1/coupons` - Get all coupons (with filtering)
- Query params: `page`, `size`, `isActive`, `code`
- `GET /api/wb/v1/coupons/{id}` - Get coupon by ID
- `POST /api/wb/v1/coupons` - Create coupon
- `PUT /api/wb/v1/coupons/{id}` - Update coupon
- `DELETE /api/wb/v1/coupons/{id}` - Delete coupon
- `PATCH /api/wb/v1/coupons/{id}/status` - Activate/deactivate coupon

---

## 15. Seller Profile

### 15.1 Seller Profile Controller (`/api/wb/v1/seller`)

**Public Endpoints:**

- `GET /api/wb/v1/seller/{userId}` - Get seller profile by user ID
- `GET /api/wb/v1/seller/{userId}/products` - Get seller's products

**Seller Endpoints:**

- `GET /api/wb/v1/seller/profile` - Get own seller profile
- `POST /api/wb/v1/seller/profile` - Create seller profile
- `PUT /api/wb/v1/seller/profile` - Update seller profile
- `GET /api/wb/v1/seller/dashboard` - Get seller dashboard stats
- Returns: total_sales, total_products, average_rating, recent_orders

**Admin Endpoints:**

- `PATCH /api/wb/v1/seller/{userId}/verify` - Verify seller account

---

## 16. Search & Discovery

### 16.1 Search Controller (`/api/wb/v1/search`)

**Public Endpoints:**

- `GET /api/wb/v1/search/products` - Full-text search products
- Query params: `q`, `page`, `size`, `category`, `minPrice`, `maxPrice`, `sort`
- `GET /api/wb/v1/search/suggestions` - Get search suggestions/autocomplete
- Query params: `q`, `limit`

---

## Summary of Controllers to Implement

1. **ProductController** - Product CRUD and listing
2. **CategoryController** - Category management
3. **ProductImageController** - Product image management
4. **ProductVariationController** - Product variations
5. **InventoryController** - Inventory management
6. **ShoppingCartController** - Shopping cart operations
7. **OrderController** - Order management
8. **OrderItemController** - Order items (nested under orders)
9. **AddressController** - Address management
10. **PaymentController** - Payment processing
11. **SavedPaymentMethodController** - Saved payment methods
12. **ProductReviewController** - Product reviews
13. **WishlistController** - Wishlist management
14. **CouponController** - Coupon management
15. **SellerProfileController** - Seller profiles
16. **SearchController** - Search functionality

## Implementation Notes

- All endpoints should follow RESTful conventions
- Use pagination for list endpoints (page, size, sort)
- Implement proper authorization checks (owner/admin)
- Use DTOs for request/response mapping
- Add validation annotations
- Include Swagger/OpenAPI documentation
- Handle exceptions with GlobalExceptionHandler
- Use transactions for multi-step operations (e.g., order creation)
- Implement optimistic locking for inventory updates
- Add rate limiting for public endpoints if needed

### To-dos

- [ ] Implement ProductController with all CRUD and listing endpoints
- [ ] Implement CategoryController with category management endpoints
- [ ] Implement ProductImageController for image management
- [ ] Implement ProductVariationController for variations
- [ ] Implement InventoryController with stock management
- [ ] Implement ShoppingCartController for cart operations
- [ ] Implement OrderController with order management
- [ ] Implement AddressController for address management
- [ ] Implement PaymentController for payment processing
- [ ] Implement SavedPaymentMethodController
- [ ] Implement ProductReviewController for reviews
- [ ] Implement WishlistController for wishlist management
- [ ] Implement CouponController for coupon management
- [ ] Implement SellerProfileController for seller profiles
- [ ] Implement SearchController for product search