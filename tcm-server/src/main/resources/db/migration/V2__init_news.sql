CREATE TABLE article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(300) NOT NULL,
    summary VARCHAR(500),
    content LONGTEXT,
    category VARCHAR(30) NOT NULL,
    sub_category VARCHAR(50),
    cover_image VARCHAR(500),
    herb_ids JSON,
    view_count INT DEFAULT 0,
    is_top TINYINT DEFAULT 0,
    status VARCHAR(10) DEFAULT 'draft',
    author_id BIGINT,
    published_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_status_published (status, published_at)
);
