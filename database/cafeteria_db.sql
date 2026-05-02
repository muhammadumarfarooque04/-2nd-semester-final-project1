-- ============================================
-- Cafeteria Management System - Database Schema
-- Course: SW121 – Object Oriented Programming
-- Batch: K25SW
-- ============================================

CREATE DATABASE IF NOT EXISTS cafeteria_db;
USE cafeteria_db;

-- Users Table (Admin & User roles)
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Categories Table
CREATE TABLE IF NOT EXISTS categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Menu Items Table
CREATE TABLE IF NOT EXISTS menu_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(100) NOT NULL,
    category_id INT,
    price DECIMAL(10, 2) NOT NULL,
    description VARCHAR(255),
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL
);

-- Orders Table
CREATE TABLE IF NOT EXISTS orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status ENUM('PENDING', 'PREPARING', 'READY', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    notes VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- Order Items Table
CREATE TABLE IF NOT EXISTS order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES menu_items(item_id) ON DELETE CASCADE
);

-- ============================================
-- Default Data
-- ============================================

-- Default Admin User (password: admin123)
INSERT INTO users (username, password, full_name, role, email) VALUES
('admin', 'admin123', 'System Administrator', 'ADMIN', 'admin@cafeteria.com'),
('user1', 'user123', 'Ali Hassan', 'USER', 'ali@example.com'),
('user2', 'user123', 'Sara Khan', 'USER', 'sara@example.com');

-- Default Categories
INSERT INTO categories (category_name, description) VALUES
('Breakfast', 'Morning meals and snacks'),
('Lunch', 'Midday full meals'),
('Beverages', 'Hot and cold drinks'),
('Snacks', 'Light bites and snacks'),
('Desserts', 'Sweet treats and desserts');

-- Default Menu Items
INSERT INTO menu_items (item_name, category_id, price, description) VALUES
('Paratha with Egg', 1, 80.00, 'Crispy paratha served with fried egg'),
('Halwa Puri', 1, 120.00, 'Traditional halwa puri with chana'),
('Biryani', 2, 200.00, 'Aromatic rice with spiced meat'),
('Daal Chawal', 2, 150.00, 'Lentil curry with steamed rice'),
('Karahi', 2, 300.00, 'Spicy karahi with naan'),
('Chai', 3, 30.00, 'Hot traditional Pakistani tea'),
('Lassi', 3, 60.00, 'Sweet or salty yogurt drink'),
('Cold Drink', 3, 50.00, 'Assorted cold beverages'),
('Samosa', 4, 20.00, 'Crispy fried samosa'),
('Pakora', 4, 40.00, 'Vegetable fritters'),
('Gulab Jamun', 5, 60.00, 'Sweet milk solid dessert'),
('Kheer', 5, 80.00, 'Rice pudding with nuts');
