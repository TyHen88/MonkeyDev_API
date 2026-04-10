-- Add explicit delete permission for product management.

INSERT INTO permissions (name, description)
VALUES ('PRODUCT_DELETE', 'Delete products')
ON CONFLICT (name) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.name = 'PRODUCT_DELETE'
WHERE r.name IN ('SELLER', 'ADMIN')
ON CONFLICT DO NOTHING;
