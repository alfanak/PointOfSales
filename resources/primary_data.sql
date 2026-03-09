INSERT INTO users(name, pass, permissions) SELECT 'المدير', md5('المدير'), 'ALL' WHERE NOT EXISTS (SELECT * FROM users);
INSERT INTO suppliers(name) SELECT 'بدون اسم' WHERE NOT EXISTS (SELECT * FROM suppliers);
INSERT INTO clients(name) SELECT 'بدون اسم' WHERE NOT EXISTS (SELECT * FROM clients);
INSERT INTO product_categories(name) SELECT 'غير محدد' WHERE NOT EXISTS (SELECT * FROM product_categories);
INSERT INTO chosen_product_groups(name) SELECT 'مجموعة1' WHERE NOT EXISTS (SELECT * FROM chosen_product_groups);
