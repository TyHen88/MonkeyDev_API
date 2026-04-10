-- Mock seed data for local development

-- Ensure the core roles exist for auth flows and seeded users.
INSERT INTO roles (name, description)
VALUES
  ('ADMIN', 'Administrator'),
  ('USER', 'User'),
  ('SELLER', 'Seller')
ON CONFLICT (name) DO NOTHING;

-- Seed categories used by the product catalog.
INSERT INTO categories (name, slug, description, image_url, is_active)
VALUES
  ('Electronics', 'electronics', 'Electronic gadgets and devices', 'https://picsum.photos/seed/category-electronics/800/600', true),
  ('Accessories', 'accessories', 'Everyday accessories and add-ons', 'https://picsum.photos/seed/category-accessories/800/600', true),
  ('Home Office', 'home-office', 'Home and office essentials', 'https://picsum.photos/seed/category-home-office/800/600', true),
  ('Gaming', 'gaming', 'Gaming accessories and peripherals', 'https://picsum.photos/seed/category-gaming/800/600', true),
  ('Travel', 'travel', 'Travel-friendly products and gear', 'https://picsum.photos/seed/category-travel/800/600', true),
  ('Fitness', 'fitness', 'Fitness and wellness gear', 'https://picsum.photos/seed/category-fitness/800/600', true),
  ('Kitchen', 'kitchen', 'Kitchen and cooking products', 'https://picsum.photos/seed/category-kitchen/800/600', true),
  ('Audio', 'audio', 'Speakers, headphones, and sound gear', 'https://picsum.photos/seed/category-audio/800/600', true),
  ('Mobile', 'mobile', 'Mobile phone accessories', 'https://picsum.photos/seed/category-mobile/800/600', true),
  ('Outdoor', 'outdoor', 'Outdoor and lifestyle essentials', 'https://picsum.photos/seed/category-outdoor/800/600', true);

-- Seed a seller account that owns all mock products.
INSERT INTO users (
  full_name,
  username,
  email,
  password,
  profile_image_url,
  auth_provider,
  is_active
)
VALUES (
  'Mock Seller',
  'mock_seller',
  'mock.seller@example.com',
  '$2a$10$mockseedpasswordhashmockseedpasswordhashmockseedpass',
  'https://picsum.photos/seed/mock-seller/256/256',
  'LOCAL',
  true
)
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'SELLER'
WHERE u.username = 'mock_seller'
ON CONFLICT DO NOTHING;

-- Seed 50 mock products.
WITH seed_user AS (
  SELECT id AS user_id
  FROM users
  WHERE username = 'mock_seller'
  LIMIT 1
),
product_catalog AS (
  SELECT
    gs AS n,
    (ARRAY[
      'Wireless Earbuds',
      'Smart Watch',
      'Bluetooth Speaker',
      'Gaming Mouse',
      'Laptop Stand',
      'Portable Charger',
      'Desk Lamp',
      'Water Bottle',
      'Backpack',
      'Phone Case'
    ])[((gs - 1) % 10) + 1] AS base_name
  FROM generate_series(1, 50) AS gs
)
INSERT INTO products (
  user_id,
  slug,
  title,
  description,
  price,
  sku,
  image_url,
  currency,
  weight,
  length,
  width,
  height,
  tax_rate,
  is_active,
  is_featured,
  is_new
)
SELECT
  seed_user.user_id,
  lower(replace(product_catalog.base_name, ' ', '-')) || '-' || lpad(product_catalog.n::text, 2, '0') AS slug,
  product_catalog.base_name || ' ' || product_catalog.n AS title,
  'Mock product generated for development and testing: ' || product_catalog.base_name || ' #' || product_catalog.n AS description,
  round((12.50 + (product_catalog.n * 1.35))::numeric, 2) AS price,
  'SKU-' || lpad(product_catalog.n::text, 5, '0') AS sku,
  'https://picsum.photos/seed/product-' || product_catalog.n || '/800/800' AS image_url,
  'USD' AS currency,
  round((0.25 + (product_catalog.n * 0.03))::numeric, 2) AS weight,
  round((10 + ((product_catalog.n - 1) % 8) * 1.20)::numeric, 2) AS length,
  round((8 + ((product_catalog.n - 1) % 6) * 1.10)::numeric, 2) AS width,
  round((3 + ((product_catalog.n - 1) % 5) * 0.90)::numeric, 2) AS height,
  round((((product_catalog.n - 1) % 12) * 0.75)::numeric, 2) AS tax_rate,
  true AS is_active,
  (product_catalog.n % 4 = 0) AS is_featured,
  (product_catalog.n % 6 = 0) AS is_new
FROM product_catalog
CROSS JOIN seed_user
ON CONFLICT (slug) DO NOTHING;

-- Link products to categories.
WITH seeded_products AS (
  SELECT id, row_number() OVER (ORDER BY id) AS rn
  FROM products
  WHERE user_id = (SELECT id FROM users WHERE username = 'mock_seller')
),
seeded_categories AS (
  SELECT id, row_number() OVER (ORDER BY id) AS rn
  FROM categories
)
INSERT INTO product_categories (product_id, category_id)
SELECT p.id, c.id
FROM seeded_products p
JOIN seeded_categories c
  ON c.rn = (((p.rn - 1) % 10) + 1)
ON CONFLICT DO NOTHING;

-- Seed a primary image for each product.
WITH seeded_products AS (
  SELECT id, title, row_number() OVER (ORDER BY id) AS rn
  FROM products
  WHERE user_id = (SELECT id FROM users WHERE username = 'mock_seller')
)
INSERT INTO product_images (product_id, image_url, alt_text, display_order, is_primary)
SELECT
  p.id,
  'https://picsum.photos/seed/product-image-' || p.rn || '/1000/1000',
  p.title || ' image',
  1,
  true
FROM seeded_products p
ON CONFLICT DO NOTHING;

-- Seed one variation per product.
WITH seeded_products AS (
  SELECT id, sku, row_number() OVER (ORDER BY id) AS rn
  FROM products
  WHERE user_id = (SELECT id FROM users WHERE username = 'mock_seller')
)
INSERT INTO product_variations (product_id, name, value, price_adjustment, sku, stock_quantity)
SELECT
  p.id,
  'Color',
  CASE (p.rn % 5)
    WHEN 0 THEN 'Black'
    WHEN 1 THEN 'White'
    WHEN 2 THEN 'Blue'
    WHEN 3 THEN 'Red'
    ELSE 'Green'
  END,
  round(((p.rn % 6) * 2.50)::numeric, 2),
  p.sku || '-VAR',
  100 - ((p.rn - 1) % 30)
FROM seeded_products p
ON CONFLICT DO NOTHING;

-- Seed inventory for each product variation.
INSERT INTO inventory (
  product_id,
  product_variation_id,
  quantity,
  reserved_quantity,
  low_stock_threshold,
  location,
  version
)
SELECT
  p.id,
  v.id,
  100 - ((p.rn - 1) % 35),
  0,
  10,
  'Warehouse A',
  0
FROM (
  SELECT id, row_number() OVER (ORDER BY id) AS rn
  FROM products
  WHERE user_id = (SELECT id FROM users WHERE username = 'mock_seller')
) p
JOIN product_variations v
  ON v.product_id = p.id
ON CONFLICT DO NOTHING;
