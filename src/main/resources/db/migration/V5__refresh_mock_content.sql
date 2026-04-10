-- Refresh mock catalog content so seeded products and categories have
-- data-driven descriptions and placeholder images.

-- Update category copy and cover art.
UPDATE categories c
SET
  description = CASE c.slug
    WHEN 'electronics' THEN 'Smart devices, gadgets, and connected tech for everyday storefront testing.'
    WHEN 'accessories' THEN 'Compact add-ons and lifestyle accessories for browsing, upsell, and cross-sell flows.'
    WHEN 'home-office' THEN 'Workspace essentials that fit home desks, office setups, and productivity catalogs.'
    WHEN 'gaming' THEN 'Gaming gear and accessories for performance, styling, and featured product layouts.'
    WHEN 'travel' THEN 'Travel-ready items designed for luggage, carry-on, and mobile lifestyle scenarios.'
    WHEN 'fitness' THEN 'Fitness and wellness products for active lifestyle and seasonal catalog pages.'
    WHEN 'kitchen' THEN 'Kitchen and cooking items for home essentials, bundles, and practical shopping views.'
    WHEN 'audio' THEN 'Headphones, speakers, and sound gear for feature cards and detailed product pages.'
    WHEN 'mobile' THEN 'Phone accessories and mobile essentials for daily-use and impulse-buy scenarios.'
    WHEN 'outdoor' THEN 'Outdoor and lifestyle essentials for adventure, utility, and giftable collections.'
    ELSE c.description
  END,
  image_url = 'https://placehold.co/800x600/png?text=' || replace(initcap(c.name), ' ', '+')
WHERE c.slug IN (
  'electronics', 'accessories', 'home-office', 'gaming', 'travel',
  'fitness', 'kitchen', 'audio', 'mobile', 'outdoor'
);

-- Update product descriptions and hero image URLs from the seeded category.
WITH seeded_products AS (
  SELECT
    p.id,
    p.title,
    p.slug,
    c.name AS category_name,
    c.slug AS category_slug
  FROM products p
  JOIN users u ON u.id = p.user_id
  LEFT JOIN product_categories pc ON pc.product_id = p.id
  LEFT JOIN categories c ON c.id = pc.category_id
  WHERE u.username = 'mock_seller'
)
UPDATE products p
SET
  description = format(
    'Mock %s for %s category testing. Use this product to validate listing cards, product details, cart, checkout, and admin catalog flows.',
    sp.title,
    COALESCE(sp.category_name, 'general')
  ),
  image_url = 'https://placehold.co/800x800/png?text=' || replace(sp.title, ' ', '+')
FROM seeded_products sp
WHERE p.id = sp.id;

-- Update the existing primary image row for each seeded product.
WITH seeded_products AS (
  SELECT
    p.id,
    p.title,
    p.slug,
    c.name AS category_name,
    c.slug AS category_slug
  FROM products p
  JOIN users u ON u.id = p.user_id
  LEFT JOIN product_categories pc ON pc.product_id = p.id
  LEFT JOIN categories c ON c.id = pc.category_id
  WHERE u.username = 'mock_seller'
)
UPDATE product_images pi
SET
  image_url = 'https://placehold.co/1000x1000/png?text=' || replace(sp.title, ' ', '+'),
  alt_text = sp.title || ' primary mock image'
FROM seeded_products sp
WHERE pi.product_id = sp.id
  AND pi.is_primary = true;

-- Add a second mock image per product for gallery/detail views.
WITH seeded_products AS (
  SELECT
    p.id,
    p.title,
    p.slug,
    c.name AS category_name,
    c.slug AS category_slug
  FROM products p
  JOIN users u ON u.id = p.user_id
  LEFT JOIN product_categories pc ON pc.product_id = p.id
  LEFT JOIN categories c ON c.id = pc.category_id
  WHERE u.username = 'mock_seller'
)
INSERT INTO product_images (product_id, image_url, alt_text, display_order, is_primary)
SELECT
  sp.id,
  'https://placehold.co/1000x1000/png?text=' || replace(sp.title || '+alt+view', ' ', '+'),
  sp.title || ' alternate view',
  2,
  false
FROM seeded_products sp
WHERE NOT EXISTS (
  SELECT 1
  FROM product_images pi
  WHERE pi.product_id = sp.id
    AND pi.display_order = 2
);
