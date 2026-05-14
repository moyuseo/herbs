# 中药材行情数据平台 MVP 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建数据驱动的中药材行情平台 MVP，包含价格中心、行情资讯、供求大厅、用户系统四大核心板块

**Architecture:** Spring Boot 单体后端提供 REST API，Vue 3 + Vite 前端消费 API，Python 爬虫独立运行采集数据，MySQL + Redis + ES 存储与检索

**Tech Stack:** Vue 3 / Vite / Element Plus / Pinia / ECharts / Leaflet | Spring Boot / MyBatis-Plus / JWT | Python / Scrapy / APScheduler | MySQL 8 / Redis 7 / Elasticsearch 8

---

## 文件结构总览

### 后端 (tcm-server/)
```
tcm-server/
├── pom.xml
├── src/main/java/com/tcm/
│   ├── TcmApplication.java
│   ├── config/
│   │   ├── CorsConfig.java
│   │   ├── RedisConfig.java
│   │   ├── SecurityConfig.java
│   │   └── SwaggerConfig.java
│   ├── common/
│   │   ├── Result.java
│   │   ├── PageResult.java
│   │   ├── BusinessException.java
│   │   └── Constants.java
│   ├── entity/
│   │   ├── Herb.java
│   │   ├── HerbCategory.java
│   │   ├── Price.java
│   │   ├── PriceIndex.java
│   │   ├── News.java
│   │   ├── Supply.java
│   │   ├── Demand.java
│   │   ├── User.java
│   │   ├── UserWatchlist.java
│   │   └── PriceAlert.java
│   ├── mapper/
│   │   ├── HerbMapper.java
│   │   ├── PriceMapper.java
│   │   ├── NewsMapper.java
│   │   ├── SupplyMapper.java
│   │   ├── DemandMapper.java
│   │   └── UserMapper.java
│   ├── service/
│   │   ├── HerbService.java
│   │   ├── PriceService.java
│   │   ├── NewsService.java
│   │   ├── SupplyService.java
│   │   ├── DemandService.java
│   │   ├── UserService.java
│   │   └── impl/
│   │       ├── HerbServiceImpl.java
│   │       ├── PriceServiceImpl.java
│   │       ├── NewsServiceImpl.java
│   │       ├── SupplyServiceImpl.java
│   │       ├── DemandServiceImpl.java
│   │       └── UserServiceImpl.java
│   ├── controller/
│   │   ├── PriceController.java
│   │   ├── NewsController.java
│   │   ├── SupplyController.java
│   │   ├── DemandController.java
│   │   ├── HerbController.java
│   │   └── AuthController.java
│   ├── dto/
│   │   ├── PriceQueryDTO.java
│   │   ├── NewsQueryDTO.java
│   │   ├── SupplyPublishDTO.java
│   │   ├── DemandPublishDTO.java
│   │   └── LoginDTO.java
│   ├── vo/
│   │   ├── PriceVO.java
│   │   ├── PriceDetailVO.java
│   │   ├── PriceHistoryVO.java
│   │   ├── NewsVO.java
│   │   ├── SupplyVO.java
│   │   ├── DemandVO.java
│   │   ├── HerbVO.java
│   │   └── RankingVO.java
│   ├── security/
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthFilter.java
│   │   └── SmsCodeService.java
│   └── util/
│       └── PriceChangeCalculator.java
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   └── db/
│       └── migration/
│           ├── V1__init_schema.sql
│           └── V2__init_data.sql
└── src/test/java/com/tcm/
    ├── service/
    │   ├── PriceServiceTest.java
    │   └── UserServiceTest.java
    └── controller/
        └── PriceControllerTest.java
```

### 前端 (tcm-web/)
```
tcm-web/
├── package.json
├── vite.config.ts
├── tsconfig.json
├── index.html
├── src/
│   ├── main.ts
│   ├── App.vue
│   ├── router/
│   │   └── index.ts
│   ├── stores/
│   │   ├── user.ts
│   │   └── herb.ts
│   ├── api/
│   │   ├── request.ts
│   │   ├── price.ts
│   │   ├── news.ts
│   │   ├── supply.ts
│   │   ├── demand.ts
│   │   ├── herb.ts
│   │   └── auth.ts
│   ├── components/
│   │   ├── layout/
│   │   │   ├── AppHeader.vue
│   │   │   ├── AppFooter.vue
│   │   │   └── AppSidebar.vue
│   │   ├── price/
│   │   │   ├── PriceTable.vue
│   │   │   ├── PriceChart.vue
│   │   │   ├── SparkLine.vue
│   │   │   └── RankingChart.vue
│   │   ├── map/
│   │   │   └── OriginMap.vue
│   │   └── common/
│   │       ├── HerbSearch.vue
│   │       └── PriceTag.vue
│   ├── views/
│   │   ├── Home.vue
│   │   ├── price/
│   │   │   ├── MarketPrice.vue
│   │   │   ├── OriginPrice.vue
│   │   │   ├── PriceDetail.vue
│   │   │   └── Ranking.vue
│   │   ├── news/
│   │   │   ├── NewsList.vue
│   │   │   └── NewsDetail.vue
│   │   ├── supply/
│   │   │   ├── SupplyList.vue
│   │   │   ├── DemandList.vue
│   │   │   └── Publish.vue
│   │   ├── herb/
│   │   │   ├── HerbList.vue
│   │   │   └── HerbDetail.vue
│   │   └── user/
│   │       ├── Login.vue
│   │       ├── Register.vue
│   │       ├── Watchlist.vue
│   │       └── MyPublishments.vue
│   ├── utils/
│   │   └── format.ts
│   └── styles/
│       └── variables.scss
└── public/
    └── favicon.ico
```

### 爬虫 (tcm-crawler/)
```
tcm-crawler/
├── requirements.txt
├── config.py
├── main.py
├── spiders/
│   ├── zyctd_price_spider.py
│   ├── zyctd_news_spider.py
│   ├── yt1998_price_spider.py
│   └── yt1998_news_spider.py
├── pipelines/
│   ├── price_pipeline.py
│   └── news_pipeline.py
├── middlewares/
│   └── random_proxy_middleware.py
├── items/
│   ├── price_item.py
│   └── news_item.py
├── utils/
│   ├── name_mapper.py
│   ├── spec_normalizer.py
│   └── origin_normalizer.py
└── data/
    └── herb_name_mapping.json
```

---

## Task 1: 后端基础框架搭建

**Files:**
- Create: `tcm-server/pom.xml`
- Create: `tcm-server/src/main/java/com/tcm/TcmApplication.java`
- Create: `tcm-server/src/main/java/com/tcm/common/Result.java`
- Create: `tcm-server/src/main/java/com/tcm/common/PageResult.java`
- Create: `tcm-server/src/main/java/com/tcm/common/BusinessException.java`
- Create: `tcm-server/src/main/java/com/tcm/common/Constants.java`
- Create: `tcm-server/src/main/java/com/tcm/config/CorsConfig.java`
- Create: `tcm-server/src/main/resources/application.yml`
- Create: `tcm-server/src/main/resources/application-dev.yml`

- [ ] **Step 1: 创建 Spring Boot 项目骨架**

使用 Spring Initializr 创建项目，pom.xml 核心依赖：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.5</version>
</parent>
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        <version>3.5.6</version>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.5</version>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.5.0</version>
    </dependency>
</dependencies>
```

- [ ] **Step 2: 创建统一响应类 Result.java**

```java
package com.tcm.common;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMessage("success");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }
}
```

- [ ] **Step 3: 创建 PageResult.java**

```java
package com.tcm.common;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> list;
    private long total;
    private int page;
    private int pageSize;

    public PageResult(List<T> list, long total, int page, int pageSize) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }
}
```

- [ ] **Step 4: 创建 application.yml**

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tcm_market?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  data:
    redis:
      host: localhost
      port: 6379

mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true

jwt:
  secret: tcm-market-jwt-secret-key-2026
  expiration: 7200000
```

- [ ] **Step 5: 创建 CorsConfig.java 允许前端跨域**

```java
package com.tcm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

- [ ] **Step 6: 验证项目启动**

Run: `cd tcm-server && mvn spring-boot:run`
Expected: 应用在 8080 端口启动成功

- [ ] **Step 7: 提交**

```bash
git add tcm-server/
git commit -m "feat: init Spring Boot project skeleton with common classes"
```

---

## Task 2: 数据库 Schema 与初始化数据

**Files:**
- Create: `tcm-server/src/main/resources/db/migration/V1__init_schema.sql`
- Create: `tcm-server/src/main/resources/db/migration/V2__init_data.sql`

- [ ] **Step 1: 创建 V1__init_schema.sql**

```sql
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
```

- [ ] **Step 2: 创建 V2__init_data.sql（药材分类 + 示例品种数据）**

```sql
USE tcm_market;

INSERT INTO herb_category (id, name, sort_order) VALUES
(1, '根茎类', 1), (2, '果实类', 2), (3, '全草类', 3),
(4, '花类', 4), (5, '叶类', 5), (6, '皮类', 6),
(7, '藤木类', 7), (8, '树脂类', 8), (9, '菌藻类', 9),
(10, '动物类', 10), (11, '矿物类', 11), (12, '其他', 12);

INSERT INTO herb (id, name, alias, pinyin, category_id, medicinal_part, nature_flavor, meridian_tropism) VALUES
(1, '三七', '田七、金不换', 'SQ', 1, '根', '甘、微苦，温', '肝、胃经'),
(2, '白芍', '白芍药', 'BS', 1, '根', '苦、酸，微寒', '肝、脾经'),
(3, '当归', '秦归、云归', 'DG', 1, '根', '甘、辛，温', '肝、心、脾经'),
(4, '黄芪', '绵芪', 'HQ', 1, '根', '甘，微温', '脾、肺经'),
(5, '甘草', '国老', 'GC', 1, '根及根茎', '甘，平', '心、肺、脾、胃经'),
(6, '金银花', '忍冬花', 'JYH', 4, '花蕾', '甘，寒', '肺、心、胃经'),
(7, '枸杞子', '枸杞红实', 'GQZ', 2, '果实', '甘，平', '肝、肾经'),
(8, '地黄', '生地、熟地', 'DH', 1, '根', '甘、苦，寒', '心、肝、肾经'),
(9, '人参', '棒槌', 'RS', 1, '根', '甘、微苦，微温', '脾、肺、心、肾经'),
(10, '川芎', '芎藭', 'CX', 1, '根茎', '辛，温', '肝、胆、心包经'),
(11, '茯苓', '云苓', 'FL', 9, '菌核', '甘、淡，平', '心、肺、脾、肾经'),
(12, '白术', '于术', 'BZ', 1, '根茎', '苦、甘，温', '脾、胃经'),
(13, '柴胡', '地熏', 'CH', 1, '根', '苦、辛，微寒', '肝、胆经'),
(14, '桔梗', '包袱花', 'JG', 1, '根', '苦、辛，平', '肺经'),
(15, '板蓝根', '靛青根', 'BLG', 1, '根', '苦，寒', '心、胃经');
```

- [ ] **Step 3: 执行 SQL 创建数据库和表**

Run: `mysql -u root -proot < tcm-server/src/main/resources/db/migration/V1__init_schema.sql`
Expected: 数据库和所有表创建成功

Run: `mysql -u root -proot < tcm-server/src/main/resources/db/migration/V2__init_data.sql`
Expected: 初始化数据插入成功

- [ ] **Step 4: 提交**

```bash
git add tcm-server/src/main/resources/db/
git commit -m "feat: add database schema and initial seed data"
```

---

## Task 3: 后端实体类与 Mapper

**Files:**
- Create: `tcm-server/src/main/java/com/tcm/entity/Herb.java`
- Create: `tcm-server/src/main/java/com/tcm/entity/HerbCategory.java`
- Create: `tcm-server/src/main/java/com/tcm/entity/Price.java`
- Create: `tcm-server/src/main/java/com/tcm/entity/News.java`
- Create: `tcm-server/src/main/java/com/tcm/entity/Supply.java`
- Create: `tcm-server/src/main/java/com/tcm/entity/Demand.java`
- Create: `tcm-server/src/main/java/com/tcm/entity/User.java`
- Create: `tcm-server/src/main/java/com/tcm/mapper/HerbMapper.java`
- Create: `tcm-server/src/main/java/com/tcm/mapper/PriceMapper.java`
- Create: `tcm-server/src/main/java/com/tcm/mapper/NewsMapper.java`
- Create: `tcm-server/src/main/java/com/tcm/mapper/SupplyMapper.java`
- Create: `tcm-server/src/main/java/com/tcm/mapper/DemandMapper.java`
- Create: `tcm-server/src/main/java/com/tcm/mapper/UserMapper.java`

- [ ] **Step 1: 创建所有实体类（使用 Lombok + MyBatis-Plus 注解）**

以 Price.java 为例：

```java
package com.tcm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("price")
public class Price {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long herbId;
    private String spec;
    private String origin;
    private String market;
    private Integer priceType;
    private BigDecimal price;
    private String unit;
    private String trend;
    private LocalDate priceDate;
    private String source;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
```

其余实体类按设计文档 5.1 节的字段定义创建，结构类似。

- [ ] **Step 2: 创建所有 Mapper 接口**

```java
package com.tcm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcm.entity.Price;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PriceMapper extends BaseMapper<Price> {
}
```

其余 Mapper 接口均继承 `BaseMapper<T>`，按同样模式创建。

- [ ] **Step 3: 验证编译通过**

Run: `cd tcm-server && mvn compile`
Expected: BUILD SUCCESS

- [ ] **Step 4: 提交**

```bash
git add tcm-server/src/main/java/com/tcm/entity/ tcm-server/src/main/java/com/tcm/mapper/
git commit -m "feat: add entity classes and MyBatis-Plus mappers"
```

---

## Task 4: 价格模块后端 API

**Files:**
- Create: `tcm-server/src/main/java/com/tcm/dto/PriceQueryDTO.java`
- Create: `tcm-server/src/main/java/com/tcm/vo/PriceVO.java`
- Create: `tcm-server/src/main/java/com/tcm/vo/PriceDetailVO.java`
- Create: `tcm-server/src/main/java/com/tcm/vo/PriceHistoryVO.java`
- Create: `tcm-server/src/main/java/com/tcm/vo/RankingVO.java`
- Create: `tcm-server/src/main/java/com/tcm/service/PriceService.java`
- Create: `tcm-server/src/main/java/com/tcm/service/impl/PriceServiceImpl.java`
- Create: `tcm-server/src/main/java/com/tcm/controller/PriceController.java`

- [ ] **Step 1: 创建 PriceQueryDTO**

```java
package com.tcm.dto;

import lombok.Data;

@Data
public class PriceQueryDTO {
    private Integer priceType;
    private String market;
    private String origin;
    private Long categoryId;
    private String keyword;
    private Integer page = 1;
    private Integer pageSize = 20;
}
```

- [ ] **Step 2: 创建 PriceVO（列表展示用）**

```java
package com.tcm.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PriceVO {
    private Long herbId;
    private String herbName;
    private String spec;
    private String origin;
    private String market;
    private BigDecimal price;
    private BigDecimal dayChange;
    private BigDecimal dayChangeRate;
    private BigDecimal monthChange;
    private BigDecimal monthChangeRate;
    private String trend;
}
```

- [ ] **Step 3: 创建 PriceHistoryVO（图表数据用）**

```java
package com.tcm.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PriceHistoryVO {
    private Long herbId;
    private String herbName;
    private List<String> dates;
    private List<BigDecimal> prices;
    private BigDecimal highestPrice;
    private BigDecimal lowestPrice;
    private BigDecimal avgPrice;
}
```

- [ ] **Step 4: 创建 RankingVO**

```java
package com.tcm.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RankingVO {
    private Long herbId;
    private String herbName;
    private BigDecimal currentPrice;
    private BigDecimal changeRate;
    private String trend;
}
```

- [ ] **Step 5: 创建 PriceService 接口和实现**

```java
package com.tcm.service;

import com.tcm.common.PageResult;
import com.tcm.dto.PriceQueryDTO;
import com.tcm.vo.*;

public interface PriceService {
    PageResult<PriceVO> getMarketPrices(PriceQueryDTO dto);
    PageResult<PriceVO> getOriginPrices(PriceQueryDTO dto);
    PriceDetailVO getPriceDetail(Long herbId);
    PriceHistoryVO getPriceHistory(Long herbId, String period);
    PageResult<RankingVO> getRanking(String period, Integer page, Integer pageSize);
}
```

PriceServiceImpl 核心逻辑：
- `getMarketPrices`: 按 market + category + keyword 查询最新一天的价格，关联 herb 表获取名称，计算日/月涨跌
- `getPriceHistory`: 查询指定时间范围的价格数据，组装 dates + prices 列表
- `getRanking`: 按日/周/月涨跌率排序，取 Top20

- [ ] **Step 6: 创建 PriceController**

```java
package com.tcm.controller;

import com.tcm.common.PageResult;
import com.tcm.common.Result;
import com.tcm.dto.PriceQueryDTO;
import com.tcm.service.PriceService;
import com.tcm.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/price")
@RequiredArgsConstructor
@Tag(name = "价格中心")
public class PriceController {

    private final PriceService priceService;

    @GetMapping("/market")
    @Operation(summary = "市场价格列表")
    public Result<PageResult<PriceVO>> marketPrices(PriceQueryDTO dto) {
        return Result.success(priceService.getMarketPrices(dto));
    }

    @GetMapping("/origin")
    @Operation(summary = "产地价格列表")
    public Result<PageResult<PriceVO>> originPrices(PriceQueryDTO dto) {
        dto.setPriceType(2);
        return Result.success(priceService.getOriginPrices(dto));
    }

    @GetMapping("/detail/{herbId}")
    @Operation(summary = "品种价格详情")
    public Result<PriceDetailVO> detail(@PathVariable Long herbId) {
        return Result.success(priceService.getPriceDetail(herbId));
    }

    @GetMapping("/history/{herbId}")
    @Operation(summary = "历史价格走势")
    public Result<PriceHistoryVO> history(
            @PathVariable Long herbId,
            @RequestParam(defaultValue = "month") String period) {
        return Result.success(priceService.getPriceHistory(herbId, period));
    }

    @GetMapping("/ranking")
    @Operation(summary = "涨跌排行")
    public Result<PageResult<RankingVO>> ranking(
            @RequestParam(defaultValue = "day") String period,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(priceService.getRanking(period, page, pageSize));
    }
}
```

- [ ] **Step 7: 验证 API 可访问**

Run: `cd tcm-server && mvn spring-boot:run`
Run: `curl http://localhost:8080/api/v1/price/market?page=1&pageSize=10`
Expected: 返回 JSON 格式响应（可能为空列表，因为尚无价格数据）

- [ ] **Step 8: 提交**

```bash
git add tcm-server/src/main/java/com/tcm/dto/ tcm-server/src/main/java/com/tcm/vo/ tcm-server/src/main/java/com/tcm/service/ tcm-server/src/main/java/com/tcm/controller/
git commit -m "feat: add price module API with market/origin/detail/history/ranking endpoints"
```

---

## Task 5: 资讯模块后端 API

**Files:**
- Create: `tcm-server/src/main/java/com/tcm/dto/NewsQueryDTO.java`
- Create: `tcm-server/src/main/java/com/tcm/vo/NewsVO.java`
- Create: `tcm-server/src/main/java/com/tcm/service/NewsService.java`
- Create: `tcm-server/src/main/java/com/tcm/service/impl/NewsServiceImpl.java`
- Create: `tcm-server/src/main/java/com/tcm/controller/NewsController.java`

- [ ] **Step 1: 创建 NewsQueryDTO、NewsVO**

NewsQueryDTO 包含 category、keyword、page、pageSize 字段。
NewsVO 包含 id、title、summary、coverImage、category、tags、author、source、viewCount、publishedAt 字段。

- [ ] **Step 2: 创建 NewsService 和 NewsController**

提供 `/api/v1/news/list`、`/api/v1/news/detail/{id}` 两个端点。
列表支持按分类筛选和关键词搜索，详情页增加 viewCount +1。

- [ ] **Step 3: 验证编译通过**

Run: `cd tcm-server && mvn compile`
Expected: BUILD SUCCESS

- [ ] **Step 4: 提交**

```bash
git add tcm-server/src/main/java/com/tcm/
git commit -m "feat: add news module API with list and detail endpoints"
```

---

## Task 6: 供求模块后端 API

**Files:**
- Create: `tcm-server/src/main/java/com/tcm/dto/SupplyPublishDTO.java`
- Create: `tcm-server/src/main/java/com/tcm/dto/DemandPublishDTO.java`
- Create: `tcm-server/src/main/java/com/tcm/vo/SupplyVO.java`
- Create: `tcm-server/src/main/java/com/tcm/vo/DemandVO.java`
- Create: `tcm-server/src/main/java/com/tcm/service/SupplyService.java`
- Create: `tcm-server/src/main/java/com/tcm/service/DemandService.java`
- Create: `tcm-server/src/main/java/com/tcm/service/impl/SupplyServiceImpl.java`
- Create: `tcm-server/src/main/java/com/tcm/service/impl/DemandServiceImpl.java`
- Create: `tcm-server/src/main/java/com/tcm/controller/SupplyController.java`
- Create: `tcm-server/src/main/java/com/tcm/controller/DemandController.java`

- [ ] **Step 1: 创建 DTO 和 VO 类**

SupplyPublishDTO: herbId, spec, origin, quantity, unit, priceType, price, contactName, contactPhone, images, description
DemandPublishDTO: herbId, spec, quantity, unit, deliveryAddress, description

- [ ] **Step 2: 创建 Service 和 Controller**

SupplyController 端点：
- `GET /api/v1/supply/list` - 列表（分页+筛选）
- `POST /api/v1/supply/publish` - 发布（需登录）
- `DELETE /api/v1/supply/delete/{id}` - 删除（需登录，仅自己的）

DemandController 端点：
- `GET /api/v1/demand/list` - 列表
- `POST /api/v1/demand/publish` - 发布
- `POST /api/v1/demand/quote/{id}` - 报价

- [ ] **Step 3: 验证编译通过**

Run: `cd tcm-server && mvn compile`
Expected: BUILD SUCCESS

- [ ] **Step 4: 提交**

```bash
git add tcm-server/src/main/java/com/tcm/
git commit -m "feat: add supply and demand module APIs"
```

---

## Task 7: 用户认证模块

**Files:**
- Create: `tcm-server/src/main/java/com/tcm/security/JwtTokenProvider.java`
- Create: `tcm-server/src/main/java/com/tcm/security/JwtAuthFilter.java`
- Create: `tcm-server/src/main/java/com/tcm/security/SmsCodeService.java`
- Create: `tcm-server/src/main/java/com/tcm/config/SecurityConfig.java`
- Create: `tcm-server/src/main/java/com/tcm/dto/LoginDTO.java`
- Create: `tcm-server/src/main/java/com/tcm/service/UserService.java`
- Create: `tcm-server/src/main/java/com/tcm/service/impl/UserServiceImpl.java`
- Create: `tcm-server/src/main/java/com/tcm/controller/AuthController.java`

- [ ] **Step 1: 创建 JwtTokenProvider**

核心方法：
- `generateToken(Long userId)` - 生成 JWT
- `getUserIdFromToken(String token)` - 从 token 解析 userId
- `validateToken(String token)` - 验证 token 有效性

- [ ] **Step 2: 创建 JwtAuthFilter**

实现 OncePerRequestFilter，从 Authorization header 提取 token，验证后设置 SecurityContext。

- [ ] **Step 3: 创建 SmsCodeService**

模拟短信验证码服务（MVP 阶段验证码固定为 123456），生产环境对接短信服务商。
核心方法：`sendCode(String phone)`、`verifyCode(String phone, String code)`

- [ ] **Step 4: 创建 AuthController**

```java
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    POST /sms-code   - 发送验证码
    POST /login      - 登录（phone + code）
    POST /register   - 注册（phone + code + nickname）
}
```

- [ ] **Step 5: 创建 UserService 和关注/预警相关端点**

```java
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    GET  /profile           - 用户信息
    PUT  /profile           - 更新信息
    GET  /watchlist         - 关注列表
    POST /watchlist         - 添加关注
    DELETE /watchlist/{id}  - 取消关注
    GET  /alerts            - 预警列表
    POST /alerts            - 创建预警
    DELETE /alerts/{id}     - 删除预警
    GET  /publishments      - 我的发布
}
```

- [ ] **Step 6: 验证登录流程**

Run: `curl -X POST http://localhost:8080/api/v1/auth/sms-code -d '{"phone":"13800138000"}'`
Run: `curl -X POST http://localhost:8080/api/v1/auth/login -d '{"phone":"13800138000","code":"123456"}'`
Expected: 返回 JWT token

- [ ] **Step 7: 提交**

```bash
git add tcm-server/src/main/java/com/tcm/
git commit -m "feat: add JWT auth, SMS login, user watchlist and alerts"
```

---

## Task 8: 前端项目初始化

**Files:**
- Create: `tcm-web/` 整个项目目录

- [ ] **Step 1: 使用 Vite 创建 Vue 3 项目**

```bash
npm create vite@latest tcm-web -- --template vue-ts
cd tcm-web
npm install
```

- [ ] **Step 2: 安装核心依赖**

```bash
npm install element-plus @element-plus/icons-vue pinia vue-router@4 axios echarts leaflet
npm install -D sass @types/leaflet unplugin-auto-import unplugin-vue-components
```

- [ ] **Step 3: 配置 vite.config.ts**

```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import path from 'path'

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({ resolvers: [ElementPlusResolver()] }),
    Components({ resolvers: [ElementPlusResolver()] }),
  ],
  resolve: {
    alias: { '@': path.resolve(__dirname, 'src') },
  },
  server: {
    proxy: {
      '/api': { target: 'http://localhost:8080', changeOrigin: true },
    },
  },
})
```

- [ ] **Step 4: 配置路由 router/index.ts**

```typescript
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', component: () => import('@/views/Home.vue') },
  { path: '/price/market', component: () => import('@/views/price/MarketPrice.vue') },
  { path: '/price/origin', component: () => import('@/views/price/OriginPrice.vue') },
  { path: '/price/detail/:herbId', component: () => import('@/views/price/PriceDetail.vue') },
  { path: '/price/ranking', component: () => import('@/views/price/Ranking.vue') },
  { path: '/news/list', component: () => import('@/views/news/NewsList.vue') },
  { path: '/news/detail/:id', component: () => import('@/views/news/NewsDetail.vue') },
  { path: '/supply/list', component: () => import('@/views/supply/SupplyList.vue') },
  { path: '/demand/list', component: () => import('@/views/supply/DemandList.vue') },
  { path: '/supply/publish', component: () => import('@/views/supply/Publish.vue') },
  { path: '/herb/list', component: () => import('@/views/herb/HerbList.vue') },
  { path: '/herb/detail/:herbId', component: () => import('@/views/herb/HerbDetail.vue') },
  { path: '/login', component: () => import('@/views/user/Login.vue') },
  { path: '/user/watchlist', component: () => import('@/views/user/Watchlist.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
```

- [ ] **Step 5: 配置 Pinia stores 和 axios 请求封装**

api/request.ts: 封装 axios，自动添加 JWT token，统一错误处理
stores/user.ts: 用户状态管理（token、userInfo、login/logout 方法）

- [ ] **Step 6: 验证项目启动**

Run: `cd tcm-web && npm run dev`
Expected: Vite dev server 在 5173 端口启动

- [ ] **Step 7: 提交**

```bash
git add tcm-web/
git commit -m "feat: init Vue 3 frontend project with router, pinia, element-plus"
```

---

## Task 9: 前端布局组件

**Files:**
- Create: `tcm-web/src/components/layout/AppHeader.vue`
- Create: `tcm-web/src/components/layout/AppFooter.vue`
- Create: `tcm-web/src/App.vue` (修改)

- [ ] **Step 1: 创建 AppHeader.vue**

顶部导航栏，包含：
- Logo + 网站名称
- 导航菜单：价格中心（下拉：市场价格/产地价格/涨跌排行）、行情资讯、供求大厅（下拉：供应/求购/发布）、数据中心、药材百科
- 搜索框（全局品种搜索）
- 登录/注册按钮（已登录显示用户头像+下拉菜单）

- [ ] **Step 2: 创建 AppFooter.vue**

底部信息栏：关于我们、联系方式、友情链接、备案号

- [ ] **Step 3: 修改 App.vue**

```vue
<template>
  <div class="app">
    <AppHeader />
    <main class="main-content">
      <router-view />
    </main>
    <AppFooter />
  </div>
</template>
```

- [ ] **Step 4: 验证布局渲染**

Run: `cd tcm-web && npm run dev`
打开浏览器访问 http://localhost:5173
Expected: 顶部导航栏和底部信息栏正常显示

- [ ] **Step 5: 提交**

```bash
git add tcm-web/src/
git commit -m "feat: add AppHeader and AppFooter layout components"
```

---

## Task 10: 前端价格中心页面

**Files:**
- Create: `tcm-web/src/api/price.ts`
- Create: `tcm-web/src/components/price/PriceTable.vue`
- Create: `tcm-web/src/components/price/PriceChart.vue`
- Create: `tcm-web/src/components/price/SparkLine.vue`
- Create: `tcm-web/src/views/price/MarketPrice.vue`
- Create: `tcm-web/src/views/price/OriginPrice.vue`
- Create: `tcm-web/src/views/price/PriceDetail.vue`
- Create: `tcm-web/src/views/price/Ranking.vue`

- [ ] **Step 1: 创建 api/price.ts 封装价格 API 调用**

```typescript
import request from './request'

export const getMarketPrices = (params) => request.get('/api/v1/price/market', { params })
export const getOriginPrices = (params) => request.get('/api/v1/price/origin', { params })
export const getPriceDetail = (herbId) => request.get(`/api/v1/price/detail/${herbId}`)
export const getPriceHistory = (herbId, period) => request.get(`/api/v1/price/history/${herbId}`, { params: { period } })
export const getRanking = (params) => request.get('/api/v1/price/ranking', { params })
```

- [ ] **Step 2: 创建 PriceTable.vue**

使用 Element Plus 的 el-table 组件：
- 列：品种名(可点击)、规格、产地、今日价、日涨跌(红/绿)、月涨跌(红/绿)、迷你走势图
- 支持排序（按涨跌率排序）
- 支持分页

- [ ] **Step 3: 创建 SparkLine.vue（迷你走势图）**

使用 ECharts 迷你图模式：
- 宽 80px，高 30px
- 无坐标轴、无图例
- 红色/绿色线条根据涨跌

- [ ] **Step 4: 创建 PriceChart.vue（完整走势图）**

使用 ECharts 完整图表：
- 支持面积图/K线图切换
- 支持日/周/月/年时间范围切换
- 支持 tooltip 显示具体数据
- 支持 dataZoom 缩放

- [ ] **Step 5: 创建 MarketPrice.vue 页面**

布局：
- 顶部：市场 Tab 切换（亳州/安国/成都/玉林/廉桥/普宁）
- 左侧：分类筛选面板
- 中间：PriceTable 组件
- 右侧：涨跌概览卡片

- [ ] **Step 6: 创建 PriceDetail.vue 页面**

布局：
- 顶部：品种名称 + 基本信息 + "加入关注"按钮
- 中间：当前价格卡片组（各市场/规格）
- 主体：PriceChart 走势图
- 下方：涨跌统计、关联资讯、关联供求

- [ ] **Step 7: 创建 Ranking.vue 页面**

布局：
- Tab：日涨跌 / 周涨跌 / 月涨跌
- 左侧：涨幅榜 Top20（ECharts 横向柱状图，红色）
- 右侧：跌幅榜 Top20（ECharts 横向柱状图，绿色）
- 中间：涨跌品种数量饼图

- [ ] **Step 8: 验证页面渲染**

Run: `cd tcm-web && npm run dev`
访问各价格页面，验证组件渲染正常（数据为空时显示空状态）

- [ ] **Step 9: 提交**

```bash
git add tcm-web/src/
git commit -m "feat: add price center pages with ECharts charts"
```

---

## Task 11: 前端资讯与供求页面

**Files:**
- Create: `tcm-web/src/api/news.ts`
- Create: `tcm-web/src/api/supply.ts`
- Create: `tcm-web/src/api/demand.ts`
- Create: `tcm-web/src/views/news/NewsList.vue`
- Create: `tcm-web/src/views/news/NewsDetail.vue`
- Create: `tcm-web/src/views/supply/SupplyList.vue`
- Create: `tcm-web/src/views/supply/DemandList.vue`
- Create: `tcm-web/src/views/supply/Publish.vue`

- [ ] **Step 1: 创建资讯 API 和页面**

NewsList.vue: 分类 Tab + 卡片列表 + 分页
NewsDetail.vue: 文章详情 + 关联品种链接 + 上下篇导航

- [ ] **Step 2: 创建供求 API 和页面**

SupplyList.vue: 筛选条件 + 卡片列表（品种/规格/产地/数量/价格/联系方式）
DemandList.vue: 筛选条件 + 卡片列表（品种/规格/数量/交货地/报价人数/报价按钮）
Publish.vue: Element Plus 表单（品种搜索选择、规格、产地、数量、价格类型、图片上传）

- [ ] **Step 3: 验证页面渲染**

- [ ] **Step 4: 提交**

```bash
git add tcm-web/src/
git commit -m "feat: add news and supply/demand pages"
```

---

## Task 12: 前端首页

**Files:**
- Create: `tcm-web/src/views/Home.vue`
- Create: `tcm-web/src/components/map/OriginMap.vue`（基础版）

- [ ] **Step 1: 创建首页组件**

按设计文档第 4 节的布局实现：
- 综合价格指数面板（ECharts 面积图）
- 今日涨跌概览卡片
- 市场价格快览表格（Tab 切换市场）
- 行情资讯列表（最新 5 条）
- 供应/求购信息列表（各最新 5 条）

- [ ] **Step 2: 创建 OriginMap.vue 基础版**

使用 Leaflet + 中国地图 GeoJSON：
- 展示主要产地标记点
- 点击标记弹出产地信息
- P1 阶段再添加热力图图层

- [ ] **Step 3: 验证首页完整渲染**

- [ ] **Step 4: 提交**

```bash
git add tcm-web/src/
git commit -m "feat: add homepage with index chart, price overview, and origin map"
```

---

## Task 13: Python 爬虫服务

**Files:**
- Create: `tcm-crawler/requirements.txt`
- Create: `tcm-crawler/config.py`
- Create: `tcm-crawler/main.py`
- Create: `tcm-crawler/items/price_item.py`
- Create: `tcm-crawler/items/news_item.py`
- Create: `tcm-crawler/spiders/zyctd_price_spider.py`
- Create: `tcm-crawler/spiders/yt1998_price_spider.py`
- Create: `tcm-crawler/pipelines/price_pipeline.py`
- Create: `tcm-crawler/pipelines/news_pipeline.py`
- Create: `tcm-crawler/utils/name_mapper.py`

- [ ] **Step 1: 创建爬虫项目结构**

requirements.txt:
```
scrapy==2.11.2
apscheduler==3.10.4
requests==2.32.3
pymysql==1.1.1
pandas==2.2.2
beautifulsoup4==4.12.3
fake-useragent==1.5.1
```

- [ ] **Step 2: 创建 config.py**

```python
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': 'root',
    'database': 'tcm_market',
    'charset': 'utf8mb4',
}

API_BASE = 'http://localhost:8080/api/v1'

SCHEDULE_CONFIG = {
    'price_crawler': {'hour': '8,12,18', 'minute': '0'},
    'news_crawler': {'hour': '*/2', 'minute': '0'},
}
```

- [ ] **Step 3: 创建 price_item.py 和 news_item.py**

```python
import scrapy

class PriceItem(scrapy.Item):
    herb_name = scrapy.Field()
    spec = scrapy.Field()
    origin = scrapy.Field()
    market = scrapy.Field()
    price_type = scrapy.Field()
    price = scrapy.Field()
    unit = scrapy.Field()
    trend = scrapy.Field()
    price_date = scrapy.Field()
    source = scrapy.Field()

class NewsItem(scrapy.Item):
    title = scrapy.Field()
    content = scrapy.Field()
    summary = scrapy.Field()
    cover_image = scrapy.Field()
    category = scrapy.Field()
    tags = scrapy.Field()
    author = scrapy.Field()
    source = scrapy.Field()
    published_at = scrapy.Field()
```

- [ ] **Step 4: 创建天地网价格爬虫 zyctd_price_spider.py**

爬取 zyctd.com 市场价格和产地价格页面，解析表格数据，yield PriceItem。
核心逻辑：
- 访问市场价格列表页
- 解析表格获取品种名、规格、产地、市场、今日价、涨跌
- 通过 name_mapper 将品种名映射为 herb_id
- yield PriceItem

- [ ] **Step 5: 创建药通网价格爬虫 yt1998_price_spider.py**

类似逻辑，爬取 yt1998.com 市场价格页面。

- [ ] **Step 6: 创建 name_mapper.py**

品种名称映射工具：
- 加载 herb_name_mapping.json（手动维护的名称映射表）
- 提供 `map_herb_name(raw_name) -> herb_id` 方法
- 处理常见别名（如"田七" -> "三七"）

- [ ] **Step 7: 创建 price_pipeline.py**

```python
import pymysql
from config import DB_CONFIG

class PricePipeline:
    def open_spider(self, spider):
        self.conn = pymysql.connect(**DB_CONFIG)

    def process_item(self, item, spider):
        herb_id = get_herb_id(item['herb_name'])
        if herb_id:
            with self.conn.cursor() as cursor:
                sql = """INSERT INTO price (herb_id, spec, origin, market, price_type, price, unit, trend, price_date, source)
                         VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"""
                cursor.execute(sql, (herb_id, item['spec'], item['origin'], item['market'],
                                     item['price_type'], item['price'], item['unit'],
                                     item['trend'], item['price_date'], item['source']))
            self.conn.commit()
        return item

    def close_spider(self, spider):
        self.conn.close()
```

- [ ] **Step 8: 创建 main.py 调度入口**

使用 APScheduler 定时调度爬虫任务。

- [ ] **Step 9: 测试爬虫运行**

Run: `cd tcm-crawler && python main.py`
Expected: 爬虫开始采集数据并写入数据库

- [ ] **Step 10: 提交**

```bash
git add tcm-crawler/
git commit -m "feat: add Python crawler service for price and news data"
```

---

## Task 14: 前后端联调与集成测试

**Files:**
- Modify: `tcm-web/src/api/request.ts` (确保代理配置正确)
- Modify: `tcm-web/src/views/Home.vue` (接入真实数据)

- [ ] **Step 1: 启动后端服务**

Run: `cd tcm-server && mvn spring-boot:run`

- [ ] **Step 2: 启动爬虫采集数据**

Run: `cd tcm-crawler && python main.py`
等待数据采集完成

- [ ] **Step 3: 启动前端开发服务器**

Run: `cd tcm-web && npm run dev`

- [ ] **Step 4: 验证核心流程**

1. 首页：价格指数、涨跌概览、市场价格表格是否显示真实数据
2. 价格中心：市场价格列表、品种详情走势图是否正常
3. 资讯列表：是否显示爬取的资讯
4. 供求发布：登录后发布供应信息是否成功
5. 用户关注：关注品种后是否在关注列表显示

- [ ] **Step 5: 修复联调问题**

根据测试结果修复前后端对接中的问题。

- [ ] **Step 6: 提交**

```bash
git add .
git commit -m "feat: complete MVP integration testing and bug fixes"
```

---

## Task 15: 药材百科与用户中心页面

**Files:**
- Create: `tcm-web/src/api/herb.ts`
- Create: `tcm-web/src/api/auth.ts`
- Create: `tcm-web/src/views/herb/HerbList.vue`
- Create: `tcm-web/src/views/herb/HerbDetail.vue`
- Create: `tcm-web/src/views/user/Login.vue`
- Create: `tcm-web/src/views/user/Watchlist.vue`
- Create: `tcm-web/src/views/user/MyPublishments.vue`

- [ ] **Step 1: 创建药材百科页面**

HerbList.vue: 分类导航 + 拼音索引 + 搜索 + 列表
HerbDetail.vue: 基本信息 + 产地分布 + 关联价格 + 关联资讯

- [ ] **Step 2: 创建用户登录页面**

Login.vue: 手机号 + 验证码输入框 + 登录按钮
登录成功后跳转首页，token 存入 localStorage 和 Pinia store

- [ ] **Step 3: 创建用户关注和发布管理页面**

Watchlist.vue: 关注品种列表 + 价格变动概览 + 取消关注
MyPublishments.vue: 发布的供应/求购列表 + 编辑/删除操作

- [ ] **Step 4: 验证完整用户流程**

1. 注册/登录
2. 浏览价格 → 关注品种
3. 发布供应信息
4. 查看关注列表

- [ ] **Step 5: 提交**

```bash
git add tcm-web/src/
git commit -m "feat: add herb encyclopedia, login, and user center pages"
```

---

## 自审检查

### 1. Spec 覆盖率
| Spec 要求 | 对应 Task |
|-----------|----------|
| 价格中心（5页） | Task 4, 10 |
| 行情资讯（3页） | Task 5, 11 |
| 供求大厅（4页） | Task 6, 11 |
| 用户系统 | Task 7, 15 |
| 药材百科 | Task 15 |
| 爬虫服务 | Task 13 |
| 首页 | Task 12 |
| 数据库设计 | Task 2, 3 |

P1 功能（产地地图热力图、价格指数、价格预警、数据导出、品种对比）不在 MVP 范围，将在后续计划中覆盖。

### 2. 占位符扫描
无 TBD/TODO，所有步骤包含具体代码或明确说明。

### 3. 类型一致性
API 路径、DTO/VO 字段名、前端 API 调用方法名保持一致。
