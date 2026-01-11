-- PostgreSQL schema for the e-commerce marketplace platform.
-- Use this script to initialize the database schema.

-- Enum types
CREATE TYPE order_status AS ENUM (
  'PENDING',
  'PROCESSING',
  'SHIPPED',
  'DELIVERED',
  'CANCELLED',
  'REFUNDED'
);

CREATE TYPE payment_status AS ENUM (
  'PENDING',
  'PROCESSING',
  'COMPLETED',
  'FAILED',
  'REFUNDED',
  'CANCELLED'
);

CREATE TYPE payment_method AS ENUM (
  'CREDIT_CARD',
  'DEBIT_CARD',
  'PAYPAL',
  'BANK_TRANSFER',
  'CASH_ON_DELIVERY',
  'DIGITAL_WALLET',
  'CRYPTOCURRENCY'
);

CREATE TYPE address_type AS ENUM ('SHIPPING', 'BILLING', 'BOTH');
CREATE TYPE discount_type AS ENUM ('PERCENTAGE', 'FIXED_AMOUNT');

-- Core entities
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  full_name VARCHAR(255) NOT NULL,
  username VARCHAR(100) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL,
  is_active BOOLEAN NOT NULL DEFAULT true,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uk_users_username UNIQUE (username),
  CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE seller_profiles (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  store_name VARCHAR(255) NOT NULL,
  store_description TEXT,
  store_logo_url TEXT,
  seller_rating DECIMAL(3, 2),
  total_sales BIGINT NOT NULL DEFAULT 0,
  is_verified_seller BOOLEAN NOT NULL DEFAULT false,
  business_registration_number VARCHAR(100),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uk_seller_profile_user UNIQUE (user_id),
  CONSTRAINT fk_seller_profiles_user_id
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT
);

CREATE TABLE categories (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  slug VARCHAR(255) NOT NULL,
  description TEXT,
  parent_id BIGINT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT fk_categories_parent_id
    FOREIGN KEY (parent_id) REFERENCES categories (id) ON DELETE RESTRICT
);

CREATE TABLE products (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  slug VARCHAR(255) NOT NULL,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  price DECIMAL(10, 2) NOT NULL,
  sku VARCHAR(100),
  currency VARCHAR(3) NOT NULL DEFAULT 'USD',
  weight DECIMAL(10, 2),
  length DECIMAL(10, 2),
  width DECIMAL(10, 2),
  height DECIMAL(10, 2),
  tax_rate DECIMAL(5, 2),
  is_active BOOLEAN NOT NULL DEFAULT true,
  is_featured BOOLEAN NOT NULL DEFAULT false,
  is_new BOOLEAN NOT NULL DEFAULT false,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uk_product_slug UNIQUE (slug),
  CONSTRAINT fk_products_user_id
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT,
  CONSTRAINT chk_products_price_non_negative CHECK (price >= 0)
);

CREATE TABLE product_images (
  id BIGSERIAL PRIMARY KEY,
  product_id BIGINT NOT NULL,
  image_url TEXT NOT NULL,
  alt_text VARCHAR(255),
  display_order INTEGER NOT NULL DEFAULT 0,
  is_primary BOOLEAN NOT NULL DEFAULT false,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT fk_product_images_product_id
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE RESTRICT
);

CREATE TABLE product_variations (
  id BIGSERIAL PRIMARY KEY,
  product_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  value VARCHAR(100) NOT NULL,
  price_adjustment DECIMAL(10, 2) NOT NULL DEFAULT 0,
  sku VARCHAR(100),
  stock_quantity INTEGER NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT fk_product_variations_product_id
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE RESTRICT,
  CONSTRAINT chk_product_variations_stock_non_negative CHECK (stock_quantity >= 0)
);

CREATE TABLE inventory (
  id BIGSERIAL PRIMARY KEY,
  product_id BIGINT NOT NULL,
  product_variation_id BIGINT,
  quantity INTEGER NOT NULL DEFAULT 0,
  reserved_quantity INTEGER NOT NULL DEFAULT 0,
  low_stock_threshold INTEGER NOT NULL DEFAULT 0,
  location VARCHAR(255),
  version INTEGER NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT fk_inventory_product_id
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE RESTRICT,
  CONSTRAINT fk_inventory_product_variation_id
    FOREIGN KEY (product_variation_id) REFERENCES product_variations (id) ON DELETE RESTRICT,
  CONSTRAINT chk_inventory_quantity_non_negative CHECK (quantity >= 0),
  CONSTRAINT chk_inventory_reserved_non_negative CHECK (reserved_quantity >= 0)
);

CREATE TABLE product_categories (
  product_id BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT pk_product_categories PRIMARY KEY (product_id, category_id),
  CONSTRAINT fk_product_categories_product_id
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE RESTRICT,
  CONSTRAINT fk_product_categories_category_id
    FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE RESTRICT
);

-- Shopping & orders
CREATE TABLE shopping_carts (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT fk_shopping_carts_user_id
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT
);

CREATE TABLE cart_items (
  id BIGSERIAL PRIMARY KEY,
  cart_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  product_variation_id BIGINT,
  quantity INTEGER NOT NULL,
  price_at_add DECIMAL(10, 2) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT fk_cart_items_cart_id
    FOREIGN KEY (cart_id) REFERENCES shopping_carts (id) ON DELETE CASCADE,
  CONSTRAINT fk_cart_items_product_id
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE RESTRICT,
  CONSTRAINT fk_cart_items_product_variation_id
    FOREIGN KEY (product_variation_id) REFERENCES product_variations (id) ON DELETE RESTRICT,
  CONSTRAINT chk_cart_items_quantity_positive CHECK (quantity > 0)
);

CREATE TABLE addresses (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  type address_type NOT NULL,
  full_name VARCHAR(255) NOT NULL,
  phone VARCHAR(50),
  address_line1 VARCHAR(255) NOT NULL,
  address_line2 VARCHAR(255),
  city VARCHAR(100) NOT NULL,
  state VARCHAR(100),
  postal_code VARCHAR(20) NOT NULL,
  country VARCHAR(100) NOT NULL,
  is_default BOOLEAN NOT NULL DEFAULT false,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT fk_addresses_user_id
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT
);

CREATE TABLE orders (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  order_number VARCHAR(100) NOT NULL,
  status order_status NOT NULL DEFAULT 'PENDING',
  subtotal DECIMAL(10, 2) NOT NULL,
  tax_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
  shipping_cost DECIMAL(10, 2) NOT NULL DEFAULT 0,
  discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
  total_amount DECIMAL(10, 2) NOT NULL,
  currency VARCHAR(3) NOT NULL DEFAULT 'USD',
  shipping_address_id BIGINT NOT NULL,
  billing_address_id BIGINT NOT NULL,
  payment_id BIGINT,
  notes TEXT,
  ordered_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  version INTEGER NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uk_orders_order_number UNIQUE (order_number),
  CONSTRAINT fk_orders_user_id
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT,
  CONSTRAINT fk_orders_shipping_address_id
    FOREIGN KEY (shipping_address_id) REFERENCES addresses (id) ON DELETE RESTRICT,
  CONSTRAINT fk_orders_billing_address_id
    FOREIGN KEY (billing_address_id) REFERENCES addresses (id) ON DELETE RESTRICT
);

CREATE TABLE order_items (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  product_variation_id BIGINT,
  quantity INTEGER NOT NULL,
  unit_price DECIMAL(10, 2) NOT NULL,
  total_price DECIMAL(10, 2) NOT NULL,
  product_snapshot JSONB,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT fk_order_items_order_id
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
  CONSTRAINT fk_order_items_product_id
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE RESTRICT,
  CONSTRAINT fk_order_items_product_variation_id
    FOREIGN KEY (product_variation_id) REFERENCES product_variations (id) ON DELETE RESTRICT,
  CONSTRAINT chk_order_items_quantity_positive CHECK (quantity > 0)
);

-- Payments
CREATE TABLE payments (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT NOT NULL,
  payment_method payment_method NOT NULL,
  payment_status payment_status NOT NULL DEFAULT 'PENDING',
  transaction_id VARCHAR(255),
  amount DECIMAL(10, 2) NOT NULL,
  currency VARCHAR(3) NOT NULL DEFAULT 'USD',
  payment_date TIMESTAMPTZ,
  gateway_response JSONB,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT fk_payments_order_id
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE RESTRICT
);

ALTER TABLE orders
  ADD CONSTRAINT fk_orders_payment_id
  FOREIGN KEY (payment_id) REFERENCES payments (id) ON DELETE RESTRICT;

ALTER TABLE payments
  ADD CONSTRAINT uk_payments_order_id UNIQUE (order_id);

CREATE TABLE saved_payment_methods (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  type VARCHAR(50) NOT NULL,
  last_four_digits VARCHAR(4) NOT NULL,
  expiry_date VARCHAR(7),
  is_default BOOLEAN NOT NULL DEFAULT false,
  token TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT fk_saved_payment_methods_user_id
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT
);

-- Reviews & ratings
CREATE TABLE product_reviews (
  id BIGSERIAL PRIMARY KEY,
  product_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  order_id BIGINT,
  rating INTEGER NOT NULL,
  title VARCHAR(255),
  comment TEXT,
  is_verified_purchase BOOLEAN NOT NULL DEFAULT false,
  is_approved BOOLEAN NOT NULL DEFAULT false,
  helpful_count INTEGER NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT fk_product_reviews_product_id
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE RESTRICT,
  CONSTRAINT fk_product_reviews_user_id
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT,
  CONSTRAINT fk_product_reviews_order_id
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE RESTRICT,
  CONSTRAINT chk_product_reviews_rating_range CHECK (rating BETWEEN 1 AND 5)
);

-- Wishlist
CREATE TABLE wishlists (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uk_wishlist_user_product UNIQUE (user_id, product_id),
  CONSTRAINT fk_wishlists_user_id
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT,
  CONSTRAINT fk_wishlists_product_id
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE RESTRICT
);

-- Discounts & coupons
CREATE TABLE coupons (
  id BIGSERIAL PRIMARY KEY,
  code VARCHAR(50) NOT NULL,
  description TEXT,
  discount_type discount_type NOT NULL,
  discount_value DECIMAL(10, 2) NOT NULL,
  min_purchase_amount DECIMAL(10, 2),
  max_discount_amount DECIMAL(10, 2),
  usage_limit INTEGER,
  used_count INTEGER NOT NULL DEFAULT 0,
  valid_from TIMESTAMPTZ,
  valid_until TIMESTAMPTZ,
  is_active BOOLEAN NOT NULL DEFAULT true,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uk_coupon_code UNIQUE (code),
  CONSTRAINT chk_coupons_discount_value_positive CHECK (discount_value > 0)
);

CREATE TABLE order_coupons (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT NOT NULL,
  coupon_id BIGINT NOT NULL,
  discount_amount_applied DECIMAL(10, 2) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT fk_order_coupons_order_id
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
  CONSTRAINT fk_order_coupons_coupon_id
    FOREIGN KEY (coupon_id) REFERENCES coupons (id) ON DELETE RESTRICT
);

-- Indexes
CREATE INDEX idx_products_user_id ON products (user_id);
CREATE INDEX idx_products_is_active ON products (is_active) WHERE is_active = true;
CREATE INDEX idx_products_is_featured ON products (is_featured) WHERE is_featured = true;
CREATE INDEX idx_products_price_range ON products (price) WHERE is_active = true;

CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_ordered_at ON orders (ordered_at DESC);
CREATE INDEX idx_orders_status_ordered_at
  ON orders (status, ordered_at DESC) WHERE status IN ('PENDING', 'PROCESSING');

CREATE INDEX idx_product_reviews_product_id ON product_reviews (product_id);
CREATE INDEX idx_product_reviews_user_id ON product_reviews (user_id);
CREATE INDEX idx_product_reviews_rating ON product_reviews (rating);
CREATE INDEX idx_product_reviews_is_approved
  ON product_reviews (is_approved) WHERE is_approved = true;

CREATE INDEX idx_categories_parent_id ON categories (parent_id);
CREATE INDEX idx_categories_slug ON categories (slug);

CREATE INDEX idx_coupons_is_active ON coupons (is_active) WHERE is_active = true;
CREATE INDEX idx_coupons_valid_dates ON coupons (valid_from, valid_until) WHERE is_active = true;

CREATE INDEX idx_inventory_product_id ON inventory (product_id);
CREATE INDEX idx_inventory_product_variation_id ON inventory (product_variation_id);
CREATE INDEX idx_inventory_low_stock
  ON inventory (product_id) WHERE quantity <= low_stock_threshold;

CREATE INDEX idx_cart_items_cart_id ON cart_items (cart_id);
CREATE INDEX idx_cart_items_product_id ON cart_items (product_id);

CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_product_id ON order_items (product_id);
