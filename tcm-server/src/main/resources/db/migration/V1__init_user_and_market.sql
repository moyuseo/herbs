ALTER DATABASE tcm CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE herb_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    parent_id BIGINT,
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE herb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    pinyin VARCHAR(200),
    category_id BIGINT,
    alias VARCHAR(500),
    origin_areas VARCHAR(500),
    properties VARCHAR(200),
    meridian VARCHAR(200),
    efficacy TEXT,
    description TEXT,
    image_url VARCHAR(500),
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category_id),
    INDEX idx_pinyin (pinyin),
    INDEX idx_name (name)
);

CREATE TABLE herb_spec (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    herb_id BIGINT NOT NULL,
    spec_name VARCHAR(100) NOT NULL,
    parent_id BIGINT,
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_herb (herb_id)
);

CREATE TABLE market (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    province VARCHAR(50),
    city VARCHAR(50),
    address VARCHAR(300),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE origin_area (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    province VARCHAR(50),
    city VARCHAR(50),
    county VARCHAR(50),
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    herb_count INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_location (latitude, longitude)
);

CREATE TABLE market_price (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    herb_spec_id BIGINT NOT NULL,
    market_id BIGINT NOT NULL,
    price DECIMAL(10,2),
    change_amount DECIMAL(10,2),
    change_percent DECIMAL(8,2),
    trend VARCHAR(10) DEFAULT 'stable',
    price_date DATE NOT NULL,
    source VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_herb_spec_date (herb_spec_id, price_date),
    INDEX idx_market_date (market_id, price_date),
    INDEX idx_price_date (price_date)
);

CREATE TABLE origin_price (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    herb_spec_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL,
    price DECIMAL(10,2),
    change_amount DECIMAL(10,2),
    change_percent DECIMAL(8,2),
    trend VARCHAR(10) DEFAULT 'stable',
    price_date DATE NOT NULL,
    source VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_herb_spec_date (herb_spec_id, price_date),
    INDEX idx_area_date (area_id, price_date)
);

CREATE TABLE price_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    herb_spec_id BIGINT NOT NULL,
    market_id BIGINT,
    area_id BIGINT,
    price_type VARCHAR(10) NOT NULL,
    price DECIMAL(10,2),
    record_date DATE NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_herb_spec_date (herb_spec_id, record_date),
    INDEX idx_record_date (record_date)
);

CREATE TABLE price_index (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    index_type VARCHAR(30) NOT NULL,
    index_value DECIMAL(10,2),
    change_amount DECIMAL(10,2),
    change_percent DECIMAL(8,2),
    record_date DATE NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_type_date (index_type, record_date)
);

CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) UNIQUE,
    password VARCHAR(200),
    nickname VARCHAR(50),
    avatar VARCHAR(500),
    company VARCHAR(200),
    role VARCHAR(10) DEFAULT 'user',
    membership_level INT DEFAULT 0,
    membership_expire_at DATETIME,
    status VARCHAR(10) DEFAULT 'active',
    last_login_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_phone (phone)
);

CREATE TABLE user_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    target_type VARCHAR(20) NOT NULL,
    target_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX idx_user_target (user_id, target_type, target_id)
);

CREATE TABLE user_subscription (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    herb_id BIGINT NOT NULL,
    price_threshold DECIMAL(10,2),
    notify_type VARCHAR(10) DEFAULT 'push',
    is_active TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_herb (herb_id)
);
