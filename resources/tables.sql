CREATE TABLE IF NOT EXISTS user_roles
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255),
	permissions VARCHAR (4096)
);


CREATE TABLE IF NOT EXISTS users
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255),
	pass VARCHAR(255),
    role_id INT,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (role_id) REFERENCES user_roles(id)
);

CREATE TABLE IF NOT EXISTS product_categories
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255),
	description TEXT
);

CREATE TABLE IF NOT EXISTS suppliers
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	company VARCHAR(255),
	name VARCHAR(255),
	address VARCHAR(255),
	email VARCHAR(255),
	phone VARCHAR(255),
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS clients
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255),
	address VARCHAR(255),
	email VARCHAR(255),
	phone VARCHAR(255),
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	category_id INT,
    barcode VARCHAR(255),
	name VARCHAR(255),
	reference VARCHAR(255),
	units_in_pack INT,
	selling_price DECIMAL(15, 2),
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	
    FOREIGN KEY (category_id) REFERENCES product_categories(id)
);

CREATE TABLE IF NOT EXISTS barcodes
(
    barcode VARCHAR(100) PRIMARY KEY,
    product_id INT,
    
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS product_images
(
	id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT,
    path VARCHAR(4096),
    data BLOB,
	
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS chosen_product_groups
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS chosen_products
(
	id INT AUTO_INCREMENT PRIMARY KEY,
    group_id int,
	product_id INT,

    FOREIGN KEY (group_id) REFERENCES chosen_product_groups(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS inventory
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	product_id INT,
	units INT,
    units_on_shelves INT,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS invoices
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	client_id INT,
	date DATETIME,
	due_date DATETIME,
	total_amount DECIMAL(15, 2),
	coupon_id INT,
	discount DECIMAL(15, 2),
	net_amount DECIMAL(15, 2),
	pay_status INT,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	
	FOREIGN KEY (client_id) REFERENCES clients(id)
);

CREATE TABLE IF NOT EXISTS invoice_manual_items
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id INT,
    name VARCHAR(255),
    quantity INT DEFAULT 1,
    price DECIMAL(15, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

CREATE TABLE IF NOT EXISTS client_payments
(
	id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT,
    invoice_id INT,
	date DATE,
	amount DECIMAL(15, 2),
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (client_id) REFERENCES clients(id),
    FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

CREATE TABLE IF NOT EXISTS buying_invoices
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	supplier_id INT,
	date DATETIME,
	due_date DATETIME,
   	number INT,
	total_amount DECIMAL(15, 2),
	discount DECIMAL(15, 2),
	net_amount DECIMAL(15, 2),
	payed_amount DECIMAL(15, 2),
	pay_status INT,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	
	FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

CREATE TABLE IF NOT EXISTS invoice_items
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	invoice_id INT,
	product_id INT,
    name VARCHAR(255),
	units INT,
	price DECIMAL(15, 2),
    
	FOREIGN KEY (invoice_id) REFERENCES invoices(id),
	FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS buying_invoice_items
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	invoice_id INT,
	product_id INT,
	quantity INT,
	unit_price DECIMAL(15, 2),
	pack_price DECIMAL(15, 2),
    
	FOREIGN KEY (invoice_id) REFERENCES buying_invoices(id),
	FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS employees
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255),
	wage DECIMAL(15, 2),
	wage_type SET('DAY', 'WEEK', 'MONTH'),
	start_date DATE,
	end_date DATE,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS expenses
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	purpose VARCHAR(255),
	amount DECIMAL(15, 2),
	date DATE,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS shopping_lists
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	title VARCHAR(255),
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS shopping_list_items
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	list_id INT,
    name VARCHAR(255),
    quantity INT,
    price DECIMAL(15, 2),
    status INT,
    
	FOREIGN KEY (list_id) REFERENCES shopping_lists(id)
);

CREATE TABLE IF NOT EXISTS user_log
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	user_id INT,
    subject_id INT,
    operation VARCHAR(255),
    description VARCHAR(255),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

	FOREIGN KEY (user_id) REFERENCES users(id)
);

