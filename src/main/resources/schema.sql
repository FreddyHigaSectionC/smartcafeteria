-- Create users table --
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
);

-- Create orders table --
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    order_time DATETIME NOT NULL,
    total DECIMAL(10,2) NOT NULL DEFAULT 0,

    -- foreign key constraints
    CONSTRAINT fk_order_user
        FOREIGN KEY (user_id) REFERENCES users(id)
	ON DELETE CASCADE
);

-- Create order_item table --
CREATE TABLE order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_item_id BIGINT NOT NULL,
    cart_id BIGINT,
    quantity INT DEFAULT 1,
    price DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,

    -- foreign key constraints
    CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_order_item_menu_item
        FOREIGN KEY (menu_item_id) REFERENCES menu_item(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_cart
            FOREIGN KEY (cart_id) REFERENCES cart(id)
            ON DELETE SET NULL
);

-- Create menu_item table --
CREATE TABLE menu_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    category VARCHAR(50),
    available BOOLEAN DEFAULT TRUE,
    image_path VARCHAR(255)
);

-- Create cart table --
CREATE TABLE cart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL,

    -- foreign key constraint
    CONSTRAINT fk_cart_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE
);
