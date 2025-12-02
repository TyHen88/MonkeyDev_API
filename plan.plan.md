<!-- 5e5b242e-95bf-4f7f-9abc-6e3c3e69d1b3 ea3527ae-d2d1-4597-ba99-af8a35ed49c9 -->
# E-commerce Data Model Analysis & Design Plan

## Current State Analysis

### Existing Entities

**Users Entity** (`src/main/java/com/dev/monkey_dev/domain/entity/Users.java`)

- Well-structured with authentication, roles (USER/ADMIN), OAuth2 support
- Has audit fields via BaseEntity (createdAt, updatedAt)
- Includes profile image, active status
- **Gap**: Missing seller-specific fields (store name, seller rating, etc.)

**Products Entity** (`src/main/java/com/dev/monkey_dev/domain/entity/Products.java`)

- Basic structure with user relationship (ManyToOne)
- Has slug, title, description, price, single image_url
- Includes flags: isActive, isFeatured, isNew
- **Gaps Identified**:
- No category relationship
- Single image only (need multiple images)
- No inventory/stock tracking
- No product variations (sizes, colors)
- No SKU/barcode
- Price should support currency
- Missing weight, dimensions for shipping
- No tax information

**BaseEntity** (`src/main/java/com/dev/monkey_dev/domain/entity/BaseEntity.java`)

- Provides createdAt, updatedAt audit fields
- Good foundation for all entities

## New Entities to Design

### 1. Core Product Entities

**Category** - Product categorization

- id, name, slug, description, parent_id (self-referencing for hierarchy), image_url, is_active
- Supports nested categories (e.g., Electronics > Phones > Smartphones)

**ProductImage** - Multiple images per product

- id, product_id (FK), image_url, alt_text, display_order, is_primary

**ProductVariation** - Sizes, colors, etc.

- id, product_id (FK), name (e.g., "Size", "Color"), value (e.g., "Large", "Red"), price_adjustment, sku, stock_quantity

**Inventory** - Stock management

- id, product_id (FK), product_variation_id (FK, nullable), quantity, reserved_quantity, low_stock_threshold, location

### 2. Shopping & Order Entities

**ShoppingCart** - User cart

- id, user_id (FK), created_at, updated_at

**CartItem** - Items in cart

- id, cart_id (FK), product_id (FK), product_variation_id (FK, nullable), quantity, price_at_add (snapshot)

**Order** - Customer orders

- id, user_id (FK), order_number (unique), status (enum: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED), 
subtotal, tax_amount, shipping_cost, discount_amount, total_amount, currency,
shipping_address_id (FK), billing_address_id (FK), payment_id (FK), notes, ordered_at

**OrderItem** - Order line items

- id, order_id (FK), product_id (FK), product_variation_id (FK, nullable), quantity, unit_price, total_price, product_snapshot (JSONB for historical data)

### 3. Address & Shipping

**Address** - User addresses (shipping/billing)

- id, user_id (FK), type (enum: SHIPPING, BILLING, BOTH), full_name, phone, 
address_line1, address_line2, city, state, postal_code, country, is_default

### 4. Payment Entities

**Payment** - Payment records

- id, order_id (FK), payment_method (enum: CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER, etc.), 
payment_status (enum: PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED),
transaction_id, amount, currency, payment_date, gateway_response (JSONB)

**PaymentMethod** - Saved payment methods (optional)

- id, user_id (FK), type, last_four_digits, expiry_date, is_default, token (encrypted)

### 5. Reviews & Ratings

**ProductReview** - Product reviews

- id, product_id (FK), user_id (FK), order_id (FK, nullable - verified purchase), 
rating (1-5), title, comment, is_verified_purchase, is_approved, helpful_count, created_at

### 6. Wishlist

**Wishlist** - User wishlists

- id, user_id (FK), product_id (FK), created_at
- Unique constraint on (user_id, product_id)

### 7. Discounts & Coupons

**Coupon** - Discount coupons

- id, code (unique), description, discount_type (enum: PERCENTAGE, FIXED_AMOUNT), 
discount_value, min_purchase_amount, max_discount_amount,
usage_limit, used_count, valid_from, valid_until, is_active

**OrderCoupon** - Coupon usage tracking

- id, order_id (FK), coupon_id (FK), discount_amount_applied

### 8. Seller Enhancements

**SellerProfile** - Extended seller information

- id, user_id (FK, unique), store_name, store_description, store_logo_url, 
seller_rating (average), total_sales, is_verified_seller, business_registration_number

## Relationships & Constraints

### Key Relationships

- Products → Users (ManyToOne) - seller relationship
- Products → Category (ManyToMany via junction table)
- Products → ProductImage (OneToMany)
- Products → ProductVariation (OneToMany)
- Products → Inventory (OneToMany)
- Users → ShoppingCart (OneToOne)
- ShoppingCart → CartItem (OneToMany)
- Users → Order (OneToMany)
- Order → OrderItem (OneToMany)
- Order → Address (ManyToOne for shipping/billing)
- Order → Payment (OneToOne)
- Users → Address (OneToMany)
- Products → ProductReview (OneToMany)
- Users → Wishlist (OneToMany)
- Order → OrderCoupon (OneToMany)

### PostgreSQL-Specific Optimizations

1. **Indexes**:

- Products: slug (unique), user_id, is_active, is_featured, price range
- Orders: user_id, order_number (unique), status, ordered_at
- ProductReview: product_id, user_id, rating
- Category: slug, parent_id
- Coupon: code (unique), is_active, valid_from, valid_until

2. **Constraints**:

- Check constraints for price > 0, quantity >= 0, rating 1-5
- Foreign key constraints with appropriate cascade/restrict rules
- Unique constraints on critical fields

3. **Data Types**:

- Use DECIMAL(10,2) for monetary values (price, amounts)
- Use JSONB for flexible data (product_snapshot, gateway_response)
- Use ENUM types for status fields
- Use TIMESTAMP WITH TIME ZONE for dates

4. **Performance**:

- Partial indexes for active products, pending orders
- Composite indexes for common query patterns
- Consider partitioning for large tables (orders, order_items) by date

## Implementation Files

### New Entity Files to Create

- `Category.java`
- `ProductImage.java`
- `ProductVariation.java`
- `Inventory.java`
- `ShoppingCart.java`
- `CartItem.java`
- `Order.java`
- `OrderItem.java`
- `Address.java`
- `Payment.java`
- `PaymentMethod.java` (optional)
- `ProductReview.java`
- `Wishlist.java`
- `Coupon.java`
- `OrderCoupon.java`
- `SellerProfile.java`

### Enums to Create

- `OrderStatus.java`
- `PaymentMethod.java`
- `PaymentStatus.java`
- `AddressType.java`
- `DiscountType.java`
- `CouponStatus.java`

### Updates to Existing Files

- `Products.java` - Add missing fields (SKU, weight, dimensions, currency, tax_rate)
- `Users.java` - Consider adding seller-specific fields or keep separate SellerProfile

## Database Schema Considerations

1. **Naming Conventions**: Follow existing pattern (snake_case for columns, camelCase for Java)
2. **Audit Fields**: All entities extend BaseEntity for createdAt/updatedAt
3. **Soft Deletes**: Consider adding deleted_at for important entities (Products, Orders)
4. **Versioning**: Use @Version for optimistic locking on critical entities (Inventory, Order)
5. **Cascade Rules**: 

- Products → ProductImage: CASCADE DELETE
- Order → OrderItem: CASCADE DELETE
- ShoppingCart → CartItem: CASCADE DELETE

## Documentation Deliverables

1. Entity Relationship Diagram (ERD) description
2. Database schema documentation
3. Index strategy documentation
4. Migration considerations for existing Products table

### To-dos

- [ ] Analyze current Products and Users entities, document gaps and issues
- [ ] Design core product entities: Category, ProductImage, ProductVariation, Inventory
- [ ] Design shopping and order entities: ShoppingCart, CartItem, Order, OrderItem
- [ ] Design Address and Payment entities with relationships
- [ ] Design ProductReview and Wishlist entities
- [ ] Design Coupon and OrderCoupon entities for discount management
- [ ] Design SellerProfile entity for marketplace seller features
- [ ] Create all required enum classes: OrderStatus, PaymentMethod, PaymentStatus, AddressType, DiscountType
- [ ] Update Products entity with missing fields (SKU, weight, dimensions, currency, tax_rate)
- [ ] Document all entity relationships, constraints, and cascade rules
- [ ] Document PostgreSQL-specific optimizations: indexes, constraints, data types, partitioning strategy
- [ ] Create Entity Relationship Diagram description and database schema documentation



# E-commerce Database Schema Documentation

## Overview

This document describes the complete relational database schema for the e-commerce platform built with PostgreSQL. The schema supports a marketplace model where both administrators and regular users can sell products.

## Entity Relationship Diagram (ERD) Description

### Core Entities

#### Users
- **Purpose**: User accounts for the platform (both customers and sellers)
- **Key Fields**: id, fullName, username, email, password, role, authProvider, isActive
- **Relationships**:
  - OneToMany: Products (seller relationship)
  - OneToMany: Orders (customer relationship)
  - OneToOne: ShoppingCart
  - OneToMany: Address
  - OneToMany: ProductReview
  - OneToMany: Wishlist
  - OneToOne: SellerProfile
  - OneToMany: SavedPaymentMethod

#### Products
- **Purpose**: Product listings in the marketplace
- **Key Fields**: id, user_id (seller), slug (unique), title, description, price, sku, currency, weight, dimensions, tax_rate, isActive, isFeatured, isNew
- **Relationships**:
  - ManyToOne: Users (seller)
  - ManyToMany: Category (via product_categories junction table)
  - OneToMany: ProductImage
  - OneToMany: ProductVariation
  - OneToMany: Inventory
  - OneToMany: CartItem
  - OneToMany: OrderItem
  - OneToMany: ProductReview
  - OneToMany: Wishlist

#### Category
- **Purpose**: Product categorization with hierarchical support
- **Key Fields**: id, name, slug, description, image_url, parent_id (self-referencing), isActive
- **Relationships**:
  - ManyToMany: Products (via product_categories junction table)
  - ManyToOne: Category (parent category for hierarchy)

### Product Management Entities

#### ProductImage
- **Purpose**: Multiple images per product
- **Key Fields**: id, product_id, image_url, alt_text, display_order, is_primary
- **Relationships**:
  - ManyToOne: Products

#### ProductVariation
- **Purpose**: Product variations (sizes, colors, etc.)
- **Key Fields**: id, product_id, name, value, price_adjustment, sku, stock_quantity
- **Relationships**:
  - ManyToOne: Products
  - OneToMany: Inventory
  - OneToMany: CartItem
  - OneToMany: OrderItem

#### Inventory
- **Purpose**: Stock management with location tracking
- **Key Fields**: id, product_id, product_variation_id (nullable), quantity, reserved_quantity, low_stock_threshold, location, version (optimistic locking)
- **Relationships**:
  - ManyToOne: Products
  - ManyToOne: ProductVariation (nullable)

### Shopping & Order Entities

#### ShoppingCart
- **Purpose**: User shopping cart
- **Key Fields**: id, user_id, created_at, updated_at
- **Relationships**:
  - OneToOne: Users
  - OneToMany: CartItem

#### CartItem
- **Purpose**: Items in shopping cart
- **Key Fields**: id, cart_id, product_id, product_variation_id (nullable), quantity, price_at_add
- **Relationships**:
  - ManyToOne: ShoppingCart
  - ManyToOne: Products
  - ManyToOne: ProductVariation (nullable)

#### Order
- **Purpose**: Customer orders
- **Key Fields**: id, user_id, order_number (unique), status, subtotal, tax_amount, shipping_cost, discount_amount, total_amount, currency, shipping_address_id, billing_address_id, payment_id, notes, ordered_at, version (optimistic locking)
- **Relationships**:
  - ManyToOne: Users
  - ManyToOne: Address (shipping)
  - ManyToOne: Address (billing)
  - OneToOne: Payment
  - OneToMany: OrderItem
  - OneToMany: OrderCoupon
  - OneToMany: ProductReview

#### OrderItem
- **Purpose**: Order line items with product snapshot
- **Key Fields**: id, order_id, product_id, product_variation_id (nullable), quantity, unit_price, total_price, product_snapshot (JSONB)
- **Relationships**:
  - ManyToOne: Order
  - ManyToOne: Products
  - ManyToOne: ProductVariation (nullable)

### Address & Shipping

#### Address
- **Purpose**: User addresses for shipping and billing
- **Key Fields**: id, user_id, type (SHIPPING/BILLING/BOTH), full_name, phone, address_line1, address_line2, city, state, postal_code, country, is_default
- **Relationships**:
  - ManyToOne: Users
  - OneToMany: Order (as shipping address)
  - OneToMany: Order (as billing address)

### Payment Entities

#### Payment
- **Purpose**: Payment records for orders
- **Key Fields**: id, order_id (unique), payment_method, payment_status, transaction_id, amount, currency, payment_date, gateway_response (JSONB)
- **Relationships**:
  - OneToOne: Order

#### SavedPaymentMethod
- **Purpose**: Saved payment methods for users (optional)
- **Key Fields**: id, user_id, type, last_four_digits, expiry_date, is_default, token (encrypted)
- **Relationships**:
  - ManyToOne: Users

### Reviews & Ratings

#### ProductReview
- **Purpose**: Product reviews and ratings
- **Key Fields**: id, product_id, user_id, order_id (nullable for verified purchase), rating (1-5), title, comment, is_verified_purchase, is_approved, helpful_count
- **Relationships**:
  - ManyToOne: Products
  - ManyToOne: Users
  - ManyToOne: Order (nullable)

### Wishlist

#### Wishlist
- **Purpose**: User wishlists
- **Key Fields**: id, user_id, product_id, created_at
- **Unique Constraint**: (user_id, product_id)
- **Relationships**:
  - ManyToOne: Users
  - ManyToOne: Products

### Discounts & Coupons

#### Coupon
- **Purpose**: Discount coupons
- **Key Fields**: id, code (unique), description, discount_type (PERCENTAGE/FIXED_AMOUNT), discount_value, min_purchase_amount, max_discount_amount, usage_limit, used_count, valid_from, valid_until, is_active
- **Relationships**:
  - OneToMany: OrderCoupon

#### OrderCoupon
- **Purpose**: Coupon usage tracking per order
- **Key Fields**: id, order_id, coupon_id, discount_amount_applied
- **Relationships**:
  - ManyToOne: Order
  - ManyToOne: Coupon

### Seller Enhancements

#### SellerProfile
- **Purpose**: Extended seller information
- **Key Fields**: id, user_id (unique), store_name, store_description, store_logo_url, seller_rating, total_sales, is_verified_seller, business_registration_number
- **Relationships**:
  - OneToOne: Users

## Relationships & Constraints

### Key Relationships Summary

1. **Products → Users**: ManyToOne (seller relationship)
2. **Products → Category**: ManyToMany (via product_categories junction table)
3. **Products → ProductImage**: OneToMany
4. **Products → ProductVariation**: OneToMany
5. **Products → Inventory**: OneToMany
6. **Users → ShoppingCart**: OneToOne
7. **ShoppingCart → CartItem**: OneToMany
8. **Users → Order**: OneToMany
9. **Order → OrderItem**: OneToMany
10. **Order → Address**: ManyToOne (for shipping and billing)
11. **Order → Payment**: OneToOne
12. **Users → Address**: OneToMany
13. **Products → ProductReview**: OneToMany
14. **Users → Wishlist**: OneToMany
15. **Order → OrderCoupon**: OneToMany

### Cascade Rules

The following cascade delete rules should be implemented at the service layer or using JPA orphanRemoval:

- **Products → ProductImage**: When a product is deleted, all associated images should be deleted
- **Order → OrderItem**: When an order is deleted, all associated order items should be deleted
- **ShoppingCart → CartItem**: When a shopping cart is deleted, all associated cart items should be deleted

### Unique Constraints

1. **products.slug**: Unique product slug
2. **orders.order_number**: Unique order number
3. **coupons.code**: Unique coupon code
4. **wishlists(user_id, product_id)**: Unique combination of user and product in wishlist
5. **seller_profiles.user_id**: One seller profile per user
6. **users.username**: Unique username
7. **users.email**: Unique email

### Check Constraints (Recommended for PostgreSQL)

The following check constraints should be added via database migrations:

1. **products.price**: `price > 0`
2. **order_items.quantity**: `quantity > 0`
3. **order_items.unit_price**: `unit_price >= 0`
4. **order_items.total_price**: `total_price >= 0`
5. **product_reviews.rating**: `rating >= 1 AND rating <= 5`
6. **inventory.quantity**: `quantity >= 0`
7. **inventory.reserved_quantity**: `reserved_quantity >= 0`
8. **cart_items.quantity**: `quantity > 0`
9. **payments.amount**: `amount > 0`
10. **orders.subtotal**: `subtotal >= 0`
11. **orders.total_amount**: `total_amount >= 0`

## PostgreSQL-Specific Optimizations

### Indexes

#### Products Table
```sql
CREATE UNIQUE INDEX idx_products_slug ON products(slug);
CREATE INDEX idx_products_user_id ON products(user_id);
CREATE INDEX idx_products_is_active ON products(is_active) WHERE is_active = true;
CREATE INDEX idx_products_is_featured ON products(is_featured) WHERE is_featured = true;
CREATE INDEX idx_products_price_range ON products(price) WHERE is_active = true;
CREATE INDEX idx_products_created_at ON products(created_at DESC);
```

#### Orders Table
```sql
CREATE UNIQUE INDEX idx_orders_order_number ON orders(order_number);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_ordered_at ON orders(ordered_at DESC);
CREATE INDEX idx_orders_status_ordered_at ON orders(status, ordered_at DESC) WHERE status IN ('PENDING', 'PROCESSING');
```

#### ProductReview Table
```sql
CREATE INDEX idx_product_reviews_product_id ON product_reviews(product_id);
CREATE INDEX idx_product_reviews_user_id ON product_reviews(user_id);
CREATE INDEX idx_product_reviews_rating ON product_reviews(rating);
CREATE INDEX idx_product_reviews_is_approved ON product_reviews(is_approved) WHERE is_approved = true;
```

#### Category Table
```sql
CREATE UNIQUE INDEX idx_categories_slug ON categories(slug);
CREATE INDEX idx_categories_parent_id ON categories(parent_id) WHERE parent_id IS NOT NULL;
CREATE INDEX idx_categories_is_active ON categories(is_active) WHERE is_active = true;
```

#### Coupon Table
```sql
CREATE UNIQUE INDEX idx_coupons_code ON coupons(code);
CREATE INDEX idx_coupons_is_active ON coupons(is_active) WHERE is_active = true;
CREATE INDEX idx_coupons_valid_dates ON coupons(valid_from, valid_until) WHERE is_active = true;
```

#### Inventory Table
```sql
CREATE INDEX idx_inventory_product_id ON inventory(product_id);
CREATE INDEX idx_inventory_product_variation_id ON inventory(product_variation_id) WHERE product_variation_id IS NOT NULL;
CREATE INDEX idx_inventory_low_stock ON inventory(product_id, quantity) WHERE quantity <= low_stock_threshold;
```

#### Wishlist Table
```sql
CREATE UNIQUE INDEX idx_wishlist_user_product ON wishlists(user_id, product_id);
CREATE INDEX idx_wishlist_user_id ON wishlists(user_id);
```

#### CartItem Table
```sql
CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX idx_cart_items_product_id ON cart_items(product_id);
```

#### OrderItem Table
```sql
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
```

### Data Types

1. **Monetary Values**: Use `DECIMAL(10,2)` for all price, amount, and cost fields
   - Examples: products.price, orders.subtotal, payments.amount

2. **JSONB Fields**: Use JSONB for flexible data storage
   - `order_items.product_snapshot`: JSON snapshot of product at time of order
   - `payments.gateway_response`: JSON response from payment gateway

3. **Enum Types**: Use VARCHAR with CHECK constraints or native PostgreSQL ENUM types
   - OrderStatus, PaymentStatus, PaymentMethod, AddressType, DiscountType

4. **Timestamps**: Use `TIMESTAMP WITH TIME ZONE` for all date/time fields
   - All entities extend BaseEntity which provides createdAt and updatedAt

5. **Text Fields**: Use appropriate length limits
   - Short text: VARCHAR(50-255)
   - Long text: TEXT or VARCHAR with appropriate limits

### Performance Considerations

1. **Partial Indexes**: Used for frequently queried filtered data (active products, pending orders, approved reviews)

2. **Composite Indexes**: Used for common query patterns (status + ordered_at, product_id + rating)

3. **Partitioning Strategy** (Future Consideration):
   - **orders** and **order_items** tables can be partitioned by date (monthly or yearly) for better performance with large datasets
   - Example: Partition by `ordered_at` date range

4. **Connection Pooling**: Configured via HikariCP in application-local.yml
   - Maximum pool size: 10
   - Connection timeout: 20000ms

5. **Query Optimization**:
   - Use LAZY fetching for large collections
   - Use JOIN FETCH for eager loading when needed
   - Implement pagination for large result sets

## Migration Considerations

### Existing Products Table

When migrating the existing `products` table, the following changes are required:

1. **Add new columns**:
   ```sql
   ALTER TABLE products 
   ADD COLUMN sku VARCHAR(100),
   ADD COLUMN currency VARCHAR(3) NOT NULL DEFAULT 'USD',
   ADD COLUMN weight DECIMAL(10,2),
   ADD COLUMN length DECIMAL(10,2),
   ADD COLUMN width DECIMAL(10,2),
   ADD COLUMN height DECIMAL(10,2),
   ADD COLUMN tax_rate DECIMAL(5,2);
   ```

2. **Modify price column**:
   ```sql
   ALTER TABLE products 
   ALTER COLUMN price TYPE DECIMAL(10,2) USING price::DECIMAL(10,2);
   ```

3. **Add unique constraint on slug**:
   ```sql
   ALTER TABLE products 
   ADD CONSTRAINT uk_product_slug UNIQUE (slug);
   ```

4. **Add indexes** (as specified in Indexes section)

### Database Initialization

The application uses Hibernate's `ddl-auto: update` mode, which will automatically create/update tables based on entity definitions. For production, consider:

1. Using Flyway or Liquibase for version-controlled migrations
2. Setting `ddl-auto: validate` in production
3. Creating indexes and constraints via migration scripts

## Audit Fields

All entities extend `BaseEntity`, which provides:
- `created_at`: TIMESTAMP WITH TIME ZONE (auto-populated on creation)
- `updated_at`: TIMESTAMP WITH TIME ZONE (auto-updated on modification)

These fields are managed by Spring Data JPA's `@EntityListeners(AuditingEntityListener.class)`.

## Optimistic Locking

The following entities use `@Version` for optimistic locking to prevent concurrent modification issues:
- **Inventory**: Prevents race conditions when updating stock quantities
- **Order**: Prevents concurrent order status updates

## Security Considerations

1. **Payment Tokens**: The `saved_payment_methods.token` field should be encrypted at the application level
2. **Passwords**: User passwords are hashed (not stored in plain text)
3. **Sensitive Data**: Consider encrypting sensitive fields like business registration numbers

## Future Enhancements

1. **Soft Deletes**: Consider adding `deleted_at` timestamp for important entities (P