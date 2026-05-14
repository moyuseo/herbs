CREATE DATABASE IF NOT EXISTS tcm_market DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tcm_market;

CREATE TABLE herb_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE herb (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    alias VARCHAR(200) DEFAULT '',
    pinyin VARCHAR(50) DEFAULT '',
    category_id BIGINT,
    medicinal_part VARCHAR(20) DEFAULT '',
    nature_flavor VARCHAR(50) DEFAULT '',
    meridian_tropism VARCHAR(50) DEFAULT '',
    efficacy TEXT,
    indication TEXT,
    description TEXT,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_pinyin (pinyin),
    INDEX idx_category (category_id),
    INDEX idx_name (name)
);

CREATE TABLE price (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    herb_id BIGINT NOT NULL,
    spec VARCHAR(50) DEFAULT '',
    origin VARCHAR(50) DEFAULT '',
    market VARCHAR(20) DEFAULT '',
    price_type TINYINT NOT NULL COMMENT '1-market 2-origin',
    price DECIMAL(10,2) NOT NULL,
    unit VARCHAR(10) DEFAULT '元/公斤',
    trend VARCHAR(10) DEFAULT '稳',
    price_date DATE NOT NULL,
    source VARCHAR(50) DEFAULT '',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_herb_date (herb_id, price_date),
    INDEX idx_market_date (market, price_date),
    INDEX idx_price_type_date (price_type, price_date)
);

CREATE TABLE price_index (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    index_date DATE NOT NULL,
    index_type VARCHAR(20) NOT NULL,
    index_value DECIMAL(10,4) NOT NULL,
    change_rate DECIMAL(6,4) DEFAULT 0,
    INDEX idx_type_date (index_type, index_date)
);

CREATE TABLE news (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content LONGTEXT,
    summary VARCHAR(500) DEFAULT '',
    cover_image VARCHAR(200) DEFAULT '',
    category VARCHAR(20) NOT NULL,
    tags VARCHAR(200) DEFAULT '',
    author VARCHAR(50) DEFAULT '',
    source VARCHAR(100) DEFAULT '',
    view_count INT DEFAULT 0,
    is_top TINYINT DEFAULT 0,
    status TINYINT DEFAULT 1,
    published_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_published (published_at)
);

CREATE TABLE supply (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    herb_id BIGINT NOT NULL,
    spec VARCHAR(50) DEFAULT '',
    origin VARCHAR(100) DEFAULT '',
    quantity DECIMAL(10,2) DEFAULT 0,
    unit VARCHAR(10) DEFAULT '公斤',
    price_type TINYINT DEFAULT 1 COMMENT '1-explicit 2-negotiate',
    price DECIMAL(10,2) DEFAULT 0,
    contact_name VARCHAR(20) DEFAULT '',
    contact_phone VARCHAR(20) DEFAULT '',
    images VARCHAR(500) DEFAULT '',
    description TEXT,
    is_top TINYINT DEFAULT 0,
    expire_at DATETIME,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_herb (herb_id),
    INDEX idx_created (created_at)
);

CREATE TABLE demand (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    herb_id BIGINT NOT NULL,
    spec VARCHAR(50) DEFAULT '',
    quantity DECIMAL(10,2) DEFAULT 0,
    unit VARCHAR(10) DEFAULT '公斤',
    delivery_address VARCHAR(200) DEFAULT '',
    quote_count INT DEFAULT 0,
    expire_at DATETIME,
    description TEXT,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_herb (herb_id),
    INDEX idx_created (created_at)
);

CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone VARCHAR(20) NOT NULL UNIQUE,
    nickname VARCHAR(50) DEFAULT '',
    avatar VARCHAR(200) DEFAULT '',
    user_type TINYINT DEFAULT 1 COMMENT '1-personal 2-enterprise',
    company VARCHAR(100) DEFAULT '',
    membership_level TINYINT DEFAULT 0 COMMENT '0-free 1-monthly 2-quarterly 3-yearly',
    membership_expire DATETIME,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_phone (phone)
);

CREATE TABLE user_watchlist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    herb_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_herb (user_id, herb_id)
);

CREATE TABLE price_alert (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    herb_id BIGINT NOT NULL,
    condition_type TINYINT NOT NULL COMMENT '1-above 2-below 3-rise_above 4-drop_below',
    threshold DECIMAL(10,2) NOT NULL,
    is_active TINYINT DEFAULT 1,
    last_triggered DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_active (user_id, is_active)
);
