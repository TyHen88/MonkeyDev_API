# E-Commerce Database Schema Documentation

## Overview

This document describes the complete relational database schema for the e-commerce marketplace platform built with PostgreSQL. The schema supports a marketplace where both administrators and users can sell products, with full e-commerce functionality including orders, payments, reviews, wishlists, coupons, and product variations.

## Database Technology

- **Database**: PostgreSQL
- **ORM**: JPA/Hibernate
- **Naming Convention**: snake_case for database columns, camelCase for Java properties

## Entity Relationship Diagram Description

### Core Entities

#### Users

- **Table**: `users`
- **Purpose**: User accounts for both customers and sellers
- **Key Fields**: id, full_name, username, email, password, role, is_active
- **Relationships**:
  - One-to-Many: Products (as seller)
  - One-to-Many: Orders (as customer)
  - One-to-Many: Addresses
  - One-to-Many: ShoppingCart
  - One-to-Many: Wishlist
  - One-to-Many: ProductReview
  - One-to-Many: SavedPaymentMethod
  - One-to-One: SellerProfile

#### Products

- **Table**: `products`
- **Purpose**: Product catalog items
- **Key Fields**: id, user_id (seller), slug, title, description, price, sku, currency, weight, dimensions, tax_rate
- **Relationships**:
  - Many-to-One: Users (seller)
  - Many-to-Many: Categories
  - One-to-Many: ProductImage
  - One-to-Many: ProductVariation
  - One-to-Many: Inventory
  - One-to-Many: CartItem
  - One-to-Many: OrderItem
  - One-to-Many: ProductReview
  - One-to-Many: Wishlist

#### Category

- **Table**: `categories`
- **Purpose**: Product categorization with hierarchical support
- **Key Fields**: id, name, slug, description, parent_id
- **Relationships**:
  - Many-to-One: Category (self-referencing for hierarchy)
  - Many-to-Many: Products

#### ProductImage

- **Table**: `product_images`
- **Purpose**: Multiple images per product
- **Key Fields**: id, product_id, image_url, alt_text, display_order, is_primary
- **Relationships**:
  - Many-to-One: Products

#### ProductVariation

- **Table**: `product_variations`
- **Purpose**: Product variations (sizes, colors, etc.)
- **Key Fields**: id, product_id, name, value, price_adjustment, sku, stock_quantity
- **Relationships**:
  - Many-to-One: Products
  - One-to-Many: Inventory
  - One-to-Many: CartItem
  - One-to-Many: OrderItem

#### Inventory

- **Table**: `inventory`
- **Purpose**: Stock management with location tracking
- **Key Fields**: id, product_id, product_variation_id, quantity, reserved_quantity, low_stock_threshold, location, version
- **Relationships**:
  - Many-to-One: Products
  - Many-to-One: ProductVariation (nullable)
- **Optimistic Locking**: Uses @Version for concurrent updates

### Shopping & Order Entities

#### ShoppingCart

- **Table**: `shopping_carts`
- **Purpose**: User shopping carts
- **Key Fields**: id, user_id
- **Relationships**:
  - Many-to-One: Users
  - One-to-Many: CartItem

#### CartItem

- **Table**: `cart_items`
- **Purpose**: Items in shopping cart
- **Key Fields**: id, cart_id, product_id, product_variation_id, quantity, price_at_add
- **Relationships**:
  - Many-to-One: ShoppingCart
  - Many-to-One: Products
  - Many-to-One: ProductVariation (nullable)

#### Order

- **Table**: `orders`
- **Purpose**: Customer orders
- **Key Fields**: id, user_id, order_number (unique), status, subtotal, tax_amount, shipping_cost, discount_amount, total_amount, currency, shipping_address_id, billing_address_id, payment_id, notes, ordered_at, version
- **Relationships**:
  - Many-to-One: Users
  - Many-to-One: Address (shipping)
  - Many-to-One: Address (billing)
  - One-to-One: Payment
  - One-to-Many: OrderItem
  - One-to-Many: OrderCoupon
  - One-to-Many: ProductReview
- **Optimistic Locking**: Uses @Version for concurrent updates

#### OrderItem

- **Table**: `order_items`
- **Purpose**: Order line items with product snapshots
- **Key Fields**: id, order_id, product_id, product_variation_id, quantity, unit_price, total_price, product_snapshot (JSONB)
- **Relationships**:
  - Many-to-One: Order
  - Many-to-One: Products
  - Many-to-One: ProductVariation (nullable)

### Address & Shipping

#### Address

- **Table**: `addresses`
- **Purpose**: User shipping and billing addresses
- **Key Fields**: id, user_id, type, full_name, phone, address_line1, address_line2, city, state, postal_code, country, is_default
- **Relationships**:
  - Many-to-One: Users
  - One-to-Many: Order (as shipping_address)
  - One-to-Many: Order (as billing_address)

### Payment Entities

#### Payment

- **Table**: `payments`
- **Purpose**: Payment records
- **Key Fields**: id, order_id, payment_method, payment_status, transaction_id, amount, currency, payment_date, gateway_response (JSONB)
- **Relationships**:
  - One-to-One: Order

#### SavedPaymentMethod

- **Table**: `saved_payment_methods`
- **Purpose**: User saved payment methods
- **Key Fields**: id, user_id, type, last_four_digits, expiry_date, is_default, token (encrypted)
- **Relationships**:
  - Many-to-One: Users

### Reviews & Ratings

#### ProductReview

- **Table**: `product_reviews`
- **Purpose**: Product reviews and ratings
- **Key Fields**: id, product_id, user_id, order_id, rating (1-5), title, comment, is_verified_purchase, is_approved, helpful_count
- **Relationships**:
  - Many-to-One: Products
  - Many-to-One: Users
  - Many-to-One: Order (nullable, for verified purchases)

### Wishlist

#### Wishlist

- **Table**: `wishlists`
- **Purpose**: User wishlists
- **Key Fields**: id, user_id, product_id
- **Unique Constraint**: (user_id, product_id)
- **Relationships**:
  - Many-to-One: Users
  - Many-to-One: Products

### Discounts & Coupons

#### Coupon

- **Table**: `coupons`
- **Purpose**: Discount coupons
- **Key Fields**: id, code (unique), description, discount_type, discount_value, min_purchase_amount, max_discount_amount, usage_limit, used_count, valid_from, valid_until, is_active
- **Relationships**:
  - One-to-Many: OrderCoupon

#### OrderCoupon

- **Table**: `order_coupons`
- **Purpose**: Coupon usage tracking per order
- **Key Fields**: id, order_id, coupon_id, discount_amount_applied
- **Relationships**:
  - Many-to-One: Order
  - Many-to-One: Coupon

### Seller Profile

#### SellerProfile

- **Table**: `seller_profiles`
- **Purpose**: Extended seller information
- **Key Fields**: id, user_id (unique), store_name, store_description, store_logo_url, store_banner_url, seller_rating, total_reviews, total_sales, is_verified_seller, business_registration_number, business_address, contact_phone, contact_email, established_date, return_policy, shipping_policy, is_active, verification_date
- **Relationships**:
  - One-to-One: Users
- **Additional Fields**:
  - `store_banner_url` - URL for store banner image
  - `total_reviews` - Count of total reviews received
  - `business_address` - Physical business address
  - `contact_phone` - Primary contact phone number
  - `contact_email` - Business contact email
  - `established_date` - When the business was established
  - `return_policy` - Store's return policy
  - `shipping_policy` - Store's shipping policy
  - `is_active` - Whether the seller profile is active
  - `verification_date` - When seller was verified

## Entity Relationships Summary

### One-to-Many Relationships

- Users → Products (seller)
- Users → Orders
- Users → Addresses
- Users → ShoppingCart
- Users → Wishlist
- Users → ProductReview
- Users → SavedPaymentMethod
- Products → ProductImage
- Products → ProductVariation
- Products → Inventory
- Products → CartItem
- Products → OrderItem
- Products → ProductReview
- Products → Wishlist
- Category → Category (self-referencing, parent-child)
- ShoppingCart → CartItem
- Order → OrderItem
- Order → OrderCoupon
- Order → ProductReview
- ProductVariation → Inventory
- ProductVariation → CartItem
- ProductVariation → OrderItem
- Coupon → OrderCoupon

### Many-to-Many Relationships

- Products ↔ Categories (via `product_categories` junction table)

### One-to-One Relationships

- Users ↔ SellerProfile
- Order ↔ Payment

## Constraints

### Unique Constraints

- `users.username` - Unique username
- `users.email` - Unique email
- `products.slug` - Unique product slug
- `orders.order_number` - Unique order number
- `coupons.code` - Unique coupon code
- `wishlists(user_id, product_id)` - Unique wishlist entry per user-product
- `seller_profiles.user_id` - One seller profile per user

### Foreign Key Constraints

All foreign key relationships are enforced with appropriate cascade rules:

- Most relationships use RESTRICT on delete to prevent accidental data loss
- Order → OrderItem: Cascade delete (when order is deleted, items are deleted)
- ShoppingCart → CartItem: Cascade delete (when cart is deleted, items are deleted)

### Check Constraints (Recommended for PostgreSQL)

- `products.price >= 0` - Price must be non-negative
- `order_items.quantity > 0` - Quantity must be positive
- `product_reviews.rating BETWEEN 1 AND 5` - Rating must be 1-5
- `inventory.quantity >= 0` - Inventory quantity cannot be negative
- `inventory.reserved_quantity >= 0` - Reserved quantity cannot be negative
- `coupons.discount_value > 0` - Discount value must be positive

## PostgreSQL-Specific Optimizations

### Indexes

#### Primary Indexes (Automatic)

All tables have primary key indexes on `id` column.

#### Unique Indexes

- `uk_users_username` on `users(username)`
- `uk_users_email` on `users(email)`
- `uk_product_slug` on `products(slug)`
- `uk_order_number` on `orders(order_number)`
- `uk_coupon_code` on `coupons(code)`
- `uk_wishlist_user_product` on `wishlists(user_id, product_id)`
- `uk_seller_profile_user` on `seller_profiles(user_id)`

#### Performance Indexes (Recommended)

```sql
-- Products
CREATE INDEX idx_products_user_id ON products(user_id);
CREATE INDEX idx_products_is_active ON products(is_active) WHERE is_active = true;
CREATE INDEX idx_products_is_featured ON products(is_featured) WHERE is_featured = true;
CREATE INDEX idx_products_price_range ON products(price) WHERE is_active = true;

-- Orders
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_ordered_at ON orders(ordered_at DESC);
CREATE INDEX idx_orders_status_ordered_at ON orders(status, ordered_at DESC) WHERE status IN ('PENDING', 'PROCESSING');

-- Product Reviews
CREATE INDEX idx_product_reviews_product_id ON product_reviews(product_id);
CREATE INDEX idx_product_reviews_user_id ON product_reviews(user_id);
CREATE INDEX idx_product_reviews_rating ON product_reviews(rating);
CREATE INDEX idx_product_reviews_is_approved ON product_reviews(is_approved) WHERE is_approved = true;

-- Categories
CREATE INDEX idx_categories_parent_id ON categories(parent_id);
CREATE INDEX idx_categories_slug ON categories(slug);

-- Coupons
CREATE INDEX idx_coupons_is_active ON coupons(is_active) WHERE is_active = true;
CREATE INDEX idx_coupons_valid_dates ON coupons(valid_from, valid_until) WHERE is_active = true;

-- Inventory
CREATE INDEX idx_inventory_product_id ON inventory(product_id);
CREATE INDEX idx_inventory_product_variation_id ON inventory(product_variation_id);
CREATE INDEX idx_inventory_low_stock ON inventory(product_id) WHERE quantity <= low_stock_threshold;

-- Shopping Cart
CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX idx_cart_items_product_id ON cart_items(product_id);

-- Order Items
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
```

### Data Types

#### Monetary Values

- Use `DECIMAL(10,2)` for all price, amount, and cost fields
- Examples: `products.price`, `orders.subtotal`, `payments.amount`

#### Text Fields

- Use `VARCHAR` with appropriate length limits
- Use `TEXT` for longer content (descriptions, comments)

#### JSONB Fields

- `order_items.product_snapshot` - JSONB for product data snapshot at order time
- `payments.gateway_response` - JSONB for payment gateway response data

#### Enum Types

- All status and type fields use `VARCHAR` with enum constraints in Java
- PostgreSQL enum types can be created for better type safety:

```sql
CREATE TYPE order_status AS ENUM ('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED');
CREATE TYPE payment_status AS ENUM ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'REFUNDED', 'CANCELLED');
CREATE TYPE payment_method AS ENUM ('CREDIT_CARD', 'DEBIT_CARD', 'PAYPAL', 'BANK_TRANSFER', 'CASH_ON_DELIVERY', 'DIGITAL_WALLET', 'CRYPTOCURRENCY');
CREATE TYPE address_type AS ENUM ('SHIPPING', 'BILLING', 'BOTH');
CREATE TYPE discount_type AS ENUM ('PERCENTAGE', 'FIXED_AMOUNT');
```

#### Timestamps

- Use `TIMESTAMP WITH TIME ZONE` for all date/time fields
- JPA `LocalDateTime` maps to `TIMESTAMP` (consider using `OffsetDateTime` for timezone support)

### Partitioning Strategy (Future Consideration)

For high-volume tables, consider partitioning:

#### Orders Table

- Partition by `ordered_at` (monthly or quarterly)
- Example: `orders_2024_q1`, `orders_2024_q2`, etc.

#### Order Items Table

- Partition by order date (aligned with orders table)

#### Product Reviews Table

- Partition by `created_at` (yearly)

### Full-Text Search

For product search, consider adding full-text search indexes:

```sql
-- Add full-text search on products
ALTER TABLE products ADD COLUMN search_vector tsvector;
CREATE INDEX idx_products_search ON products USING GIN(search_vector);

-- Update trigger for search vector
CREATE OR REPLACE FUNCTION products_search_vector_update() RETURNS trigger AS $$
BEGIN
  NEW.search_vector :=
    setweight(to_tsvector('english', COALESCE(NEW.title, '')), 'A') ||
    setweight(to_tsvector('english', COALESCE(NEW.description, '')), 'B');
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER products_search_vector_trigger
  BEFORE INSERT OR UPDATE ON products
  FOR EACH ROW EXECUTE FUNCTION products_search_vector_update();
```

## Audit Fields

All entities extend `BaseEntity` which provides:

- `created_at` - Timestamp of creation (auto-populated)
- `updated_at` - Timestamp of last update (auto-populated)

These are managed by JPA `@EntityListeners(AuditingEntityListener.class)`.

## Optimistic Locking

The following entities use `@Version` for optimistic locking:

- `Inventory` - Prevents concurrent stock updates
- `Order` - Prevents concurrent order modifications

## Migration Considerations

### Existing Products Table

When migrating the existing `products` table:

1. **Add new columns**:

   ```sql
   ALTER TABLE products
     ADD COLUMN sku VARCHAR(100),
     ADD COLUMN currency VARCHAR(3) DEFAULT 'USD' NOT NULL,
     ADD COLUMN weight DECIMAL(10,2),
     ADD COLUMN length DECIMAL(10,2),
     ADD COLUMN width DECIMAL(10,2),
     ADD COLUMN height DECIMAL(10,2),
     ADD COLUMN tax_rate DECIMAL(5,2);
   ```

2. **Update price column**:

   ```sql
   ALTER TABLE products
     ALTER COLUMN price TYPE DECIMAL(10,2);
   ```

3. **Add unique constraint on slug**:
   ```sql
   ALTER TABLE products
     ADD CONSTRAINT uk_product_slug UNIQUE (slug);
   ```

### Data Integrity

1. **Backfill default values**:

   ```sql
   UPDATE products SET currency = 'USD' WHERE currency IS NULL;
   UPDATE products SET is_active = true WHERE is_active IS NULL;
   UPDATE products SET is_featured = false WHERE is_featured IS NULL;
   UPDATE products SET is_new = false WHERE is_new IS NULL;
   ```

2. **Validate existing data**:
   - Ensure all products have valid user_id (seller)
   - Ensure all prices are non-negative
   - Ensure all slugs are unique

## Best Practices

1. **Always use transactions** for order creation and payment processing
2. **Use optimistic locking** for inventory updates to prevent overselling
3. **Snapshot product data** in OrderItem for historical accuracy
4. **Validate coupon usage** before applying discounts
5. **Use JSONB** for flexible data that may change structure
6. **Index foreign keys** for join performance
7. **Use partial indexes** for filtered queries (e.g., active products)
8. **Monitor query performance** and adjust indexes as needed
9. **Regularly update statistics** with `ANALYZE` command
10. **Consider read replicas** for reporting queries

## Security Considerations

1. **Encrypt sensitive data**:

   - `saved_payment_methods.token` - Payment tokens must be encrypted
   - `users.password` - Already handled by Spring Security

2. **Access control**:

   - Users can only modify their own orders, carts, and addresses
   - Sellers can only modify their own products
   - Admins have full access

3. **SQL injection prevention**:

   - Always use parameterized queries (JPA handles this)
   - Never concatenate user input into SQL

4. **Data validation**:
   - Validate all inputs at the application layer
   - Use database constraints as a safety net

## Performance Tuning

1. **Connection pooling**: Configure HikariCP appropriately (already in application-local.yml)
2. **Query optimization**: Use `EXPLAIN ANALYZE` to identify slow queries
3. **Batch operations**: Use batch inserts/updates for bulk operations
4. **Lazy loading**: Use `FetchType.LAZY` for large collections
5. **Pagination**: Always paginate large result sets
6. **Caching**: Consider caching frequently accessed data (categories, active products)

## Monitoring & Maintenance

1. **Monitor table sizes**: Track growth of orders and order_items tables
2. **Archive old data**: Consider archiving orders older than X years
3. **Vacuum regularly**: Run `VACUUM ANALYZE` regularly for optimal performance
4. **Backup strategy**: Implement regular backups with point-in-time recovery
5. **Replication lag**: Monitor replication lag if using read replicas
