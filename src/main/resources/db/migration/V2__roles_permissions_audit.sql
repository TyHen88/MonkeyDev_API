-- Incremental changes to align existing databases with new schema

-- Roles & permissions
CREATE TABLE IF NOT EXISTS roles (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE,
  description VARCHAR(255),
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by BIGINT,
  updated_by BIGINT
);

CREATE TABLE IF NOT EXISTS permissions (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE,
  description VARCHAR(255),
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by BIGINT,
  updated_by BIGINT
);

CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_roles_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_user_roles_role_id FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS role_permissions (
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  CONSTRAINT pk_role_permissions PRIMARY KEY (role_id, permission_id),
  CONSTRAINT fk_role_permissions_role_id FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
  CONSTRAINT fk_role_permissions_permission_id FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE RESTRICT
);

-- Audit logs
CREATE TABLE IF NOT EXISTS audit_logs (
  id BIGSERIAL PRIMARY KEY,
  actor_user_id BIGINT,
  action VARCHAR(100) NOT NULL,
  entity_type VARCHAR(100),
  entity_id BIGINT,
  description TEXT,
  request_id VARCHAR(100),
  ip_address VARCHAR(50),
  user_agent VARCHAR(255),
  success BOOLEAN NOT NULL DEFAULT true,
  metadata JSONB,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by BIGINT,
  updated_by BIGINT,
  CONSTRAINT fk_audit_logs_actor_user_id FOREIGN KEY (actor_user_id) REFERENCES users (id) ON DELETE SET NULL
);

-- Refresh tokens
CREATE TABLE IF NOT EXISTS refresh_tokens (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  token VARCHAR(500) NOT NULL,
  expires_at TIMESTAMPTZ NOT NULL,
  is_revoked BOOLEAN NOT NULL DEFAULT false,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by BIGINT,
  updated_by BIGINT,
  CONSTRAINT uk_refresh_tokens_token UNIQUE (token),
  CONSTRAINT fk_refresh_tokens_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Users adjustments
ALTER TABLE users ADD COLUMN IF NOT EXISTS profile_image_url VARCHAR(500);
ALTER TABLE users ADD COLUMN IF NOT EXISTS auth_provider VARCHAR(50);
ALTER TABLE users ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE users ADD COLUMN IF NOT EXISTS updated_by BIGINT;
ALTER TABLE users ALTER COLUMN password DROP NOT NULL;

-- Seller profiles adjustments
ALTER TABLE seller_profiles ADD COLUMN IF NOT EXISTS store_banner_url VARCHAR(500);
ALTER TABLE seller_profiles ADD COLUMN IF NOT EXISTS total_reviews BIGINT;
ALTER TABLE seller_profiles ADD COLUMN IF NOT EXISTS business_address VARCHAR(500);
ALTER TABLE seller_profiles ADD COLUMN IF NOT EXISTS contact_phone VARCHAR(100);
ALTER TABLE seller_profiles ADD COLUMN IF NOT EXISTS contact_email VARCHAR(200);
ALTER TABLE seller_profiles ADD COLUMN IF NOT EXISTS established_date TIMESTAMP;
ALTER TABLE seller_profiles ADD COLUMN IF NOT EXISTS return_policy VARCHAR(1000);
ALTER TABLE seller_profiles ADD COLUMN IF NOT EXISTS shipping_policy VARCHAR(1000);
ALTER TABLE seller_profiles ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE seller_profiles ADD COLUMN IF NOT EXISTS verification_date TIMESTAMP;
ALTER TABLE seller_profiles ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE seller_profiles ADD COLUMN IF NOT EXISTS updated_by BIGINT;

-- Categories adjustments
ALTER TABLE categories ADD COLUMN IF NOT EXISTS image_url TEXT;
ALTER TABLE categories ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE categories ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE categories ADD COLUMN IF NOT EXISTS updated_by BIGINT;

-- Products adjustments
ALTER TABLE products ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE products ADD COLUMN IF NOT EXISTS updated_by BIGINT;

-- Product images adjustments
ALTER TABLE product_images ALTER COLUMN alt_text DROP NOT NULL;
ALTER TABLE product_images ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE product_images ADD COLUMN IF NOT EXISTS updated_by BIGINT;

-- Product variations adjustments
ALTER TABLE product_variations ALTER COLUMN sku DROP NOT NULL;
ALTER TABLE product_variations ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE product_variations ADD COLUMN IF NOT EXISTS updated_by BIGINT;

-- Inventory adjustments
ALTER TABLE inventory ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE inventory ADD COLUMN IF NOT EXISTS updated_by BIGINT;

-- Product categories adjustments
ALTER TABLE product_categories ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT now();
ALTER TABLE product_categories ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT now();

-- Shopping carts adjustments
ALTER TABLE shopping_carts ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE shopping_carts ADD COLUMN IF NOT EXISTS updated_by BIGINT;

-- Cart items adjustments
ALTER TABLE cart_items ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE cart_items ADD COLUMN IF NOT EXISTS updated_by BIGINT;

-- Addresses adjustments
ALTER TABLE addresses ADD COLUMN IF NOT EXISTS delete_at TIMESTAMPTZ;
ALTER TABLE addresses ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE addresses ADD COLUMN IF NOT EXISTS updated_by BIGINT;

-- Orders adjustments
ALTER TABLE orders ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS updated_by BIGINT;

-- Order items adjustments
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS updated_by BIGINT;

-- Payments adjustments
ALTER TABLE payments ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE payments ADD COLUMN IF NOT EXISTS updated_by BIGINT;

-- Saved payment methods adjustments
ALTER TABLE saved_payment_methods ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE saved_payment_methods ADD COLUMN IF NOT EXISTS updated_by BIGINT;

-- Product reviews adjustments
ALTER TABLE product_reviews ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE product_reviews ADD COLUMN IF NOT EXISTS updated_by BIGINT;

-- Wishlists adjustments
ALTER TABLE wishlists ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE wishlists ADD COLUMN IF NOT EXISTS updated_by BIGINT;

-- Coupons adjustments
ALTER TABLE coupons ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE coupons ADD COLUMN IF NOT EXISTS updated_by BIGINT;

-- Order coupons adjustments
ALTER TABLE order_coupons ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE order_coupons ADD COLUMN IF NOT EXISTS updated_by BIGINT;

-- Convert enum-based columns to VARCHAR (if enums exist)
ALTER TABLE addresses ALTER COLUMN type TYPE VARCHAR(50) USING type::text;
-- Drop any check constraints on orders.status that assume numeric ordering
DO $$
DECLARE r RECORD;
BEGIN
  FOR r IN
    SELECT c.conname
    FROM pg_constraint c
    JOIN pg_class t ON c.conrelid = t.oid
    JOIN pg_attribute a ON a.attrelid = t.oid AND a.attnum = ANY (c.conkey)
    WHERE t.relname = 'orders'
      AND a.attname = 'status'
      AND c.contype = 'c'
  LOOP
    EXECUTE format('ALTER TABLE orders DROP CONSTRAINT %I', r.conname);
  END LOOP;
END $$;

ALTER TABLE orders ALTER COLUMN status TYPE VARCHAR(50) USING status::text;
ALTER TABLE payments ALTER COLUMN payment_method TYPE VARCHAR(50) USING payment_method::text;
ALTER TABLE payments ALTER COLUMN payment_status TYPE VARCHAR(50) USING payment_status::text;
ALTER TABLE coupons ALTER COLUMN discount_type TYPE VARCHAR(50) USING discount_type::text;
ALTER TABLE saved_payment_methods ALTER COLUMN type TYPE VARCHAR(50) USING type::text;

-- Normalize numeric/date columns for updated entities
ALTER TABLE cart_items ALTER COLUMN price_at_add TYPE DECIMAL(10, 2) USING price_at_add::numeric;
ALTER TABLE product_variations ALTER COLUMN price_adjustment TYPE DECIMAL(10, 2) USING price_adjustment::numeric;
ALTER TABLE saved_payment_methods ALTER COLUMN expiry_date TYPE DATE USING expiry_date::date;

DROP TYPE IF EXISTS order_status;
DROP TYPE IF EXISTS payment_status;
DROP TYPE IF EXISTS payment_method;
DROP TYPE IF EXISTS address_type;
DROP TYPE IF EXISTS discount_type;

-- Seed roles and permissions
INSERT INTO roles (name, description) VALUES
  ('ADMIN', 'Administrator'),
  ('USER', 'User'),
  ('SELLER', 'Seller')
ON CONFLICT DO NOTHING;

INSERT INTO permissions (name, description) VALUES
  ('USER_READ', 'Read user data'),
  ('USER_WRITE', 'Create or update users'),
  ('PRODUCT_READ', 'Read product data'),
  ('PRODUCT_WRITE', 'Create or update products'),
  ('ORDER_READ', 'Read orders'),
  ('ORDER_WRITE', 'Create or update orders'),
  ('ADMIN_MANAGE', 'Administrative operations')
ON CONFLICT DO NOTHING;

-- Map legacy users.role into user_roles (if column exists)
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'role') THEN
    INSERT INTO user_roles (user_id, role_id)
    SELECT u.id, r.id
    FROM users u
    JOIN roles r ON r.name = u.role
    ON CONFLICT DO NOTHING;

    ALTER TABLE users DROP COLUMN IF EXISTS role;
  END IF;
END $$;

-- Role-permission wiring (idempotent)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r
JOIN permissions p ON p.name IN ('USER_READ', 'PRODUCT_READ', 'ORDER_READ')
WHERE r.name = 'USER'
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r
JOIN permissions p ON p.name IN ('USER_READ', 'PRODUCT_READ', 'ORDER_READ', 'PRODUCT_WRITE', 'ORDER_WRITE')
WHERE r.name = 'SELLER'
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r
JOIN permissions p ON p.name IN ('USER_READ', 'USER_WRITE', 'PRODUCT_READ', 'PRODUCT_WRITE', 'ORDER_READ', 'ORDER_WRITE', 'ADMIN_MANAGE')
WHERE r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Indexes
CREATE INDEX IF NOT EXISTS idx_users_is_active ON users (is_active);
CREATE INDEX IF NOT EXISTS idx_roles_name ON roles (name);
CREATE INDEX IF NOT EXISTS idx_permissions_name ON permissions (name);
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles (user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles (role_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_role_id ON role_permissions (role_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_permission_id ON role_permissions (permission_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_actor_user_id ON audit_logs (actor_user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity ON audit_logs (entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON audit_logs (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens (token);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens (user_id);
