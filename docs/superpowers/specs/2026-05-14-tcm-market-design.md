# 中药材行业行情网站 - 设计文档

> 日期: 2026-05-14
> 状态: 已批准
> 参考网站: zyctd.com (中药材天地网), yt1998.com (药通网)

## 1. 项目概述

### 1.1 项目定位

中药材行业综合信息门户，覆盖行情数据、资讯、供求撮合、品种百科、产地地图等全链路功能。

### 1.2 核心目标

- 先做流量，让用户多起来
- 功能不能少，可以不用但不能没有
- 后期通过会员订阅和广告收入变现

### 1.3 技术栈

| 层级 | 技术选型 |
|------|---------|
| 前端 | Vue 3 + Vite + Element Plus + Pinia + ECharts + Leaflet |
| 移动端 | Uni-app (Vue 3) 编译微信/百度小程序 + iOS/Android |
| 后端 | Java Spring Boot 单体起步 |
| 爬虫 | Python Scrapy/Crawl4AI |
| 数据库 | MySQL + Redis + Elasticsearch |
| 消息队列 | Kafka (爬虫 → 后端数据管道) |
| 部署 | Docker Compose |

### 1.4 开发节奏

1. **第一阶段**: 响应式 PC 端网站 (Vue 3 SPA)
2. **第二阶段**: 微信小程序 / 百度小程序 (Uni-app)
3. **第三阶段**: 原生 App (Uni-app 编译)

## 2. 架构设计

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────┐
│                    Nginx 反向代理                      │
├──────────────┬──────────────┬────────────────────────┤
│  Vue 3 前端   │  Uni-app H5  │   静态资源 / CDN        │
│  (响应式SPA)  │  (同一套代码)  │                        │
├──────────────┴──────────────┴────────────────────────┤
│              Spring Boot 单体应用                      │
│  ┌──────────┬──────────┬──────────┬──────────┐       │
│  │ 用户模块  │ 行情模块  │ 资讯模块  │ 供求模块  │       │
│  ├──────────┼──────────┼──────────┼──────────┤       │
│  │ 百科模块  │ 溯源模块  │ 搜索模块  │ 后台管理  │       │
│  └──────────┴──────────┴──────────┴──────────┘       │
├─────────────────────────────────────────────────────┤
│           Python 爬虫服务 (独立进程)                    │
│  Scrapy/Crawl4AI → Kafka → Spring Boot Consumer      │
├─────────────────────────────────────────────────────┤
│     MySQL (业务数据)  │  Redis (缓存/排行)  │  ES (搜索) │
└─────────────────────────────────────────────────────┘
```

### 2.2 数据流

```
爬虫定时采集 ──→ Kafka Topic ──→ Spring Boot Consumer ──→ 数据清洗/去重 ──→ MySQL
                                                                      │
                                                              同步到 Elasticsearch
                                                                      │
                                                              更新 Redis 缓存（排行榜/热门）
                                                                      │
                                                              WebSocket 推送价格变动通知
```

### 2.3 数据来源策略

- **初期**: Python 爬虫采集天地网/药通网等现有平台数据 + 人工编辑审核
- **中期**: 接入第三方开放 API（药通网 API、政府开放数据）
- **后期**: 用户自主发布 + 平台数据积累

## 3. 功能模块

### 3.1 模块总览

| 模块 | 核心价值 | 优先级 |
|------|---------|--------|
| 行情中心 | 价格数据是网站的核心流量入口 | P0 |
| 资讯中心 | 品种分析、药市动态，用户粘性 | P0 |
| 供求中心 | 供应/求购撮合，B2B 交易基础 | P0 |
| 品种百科 | 药材知识库，SEO 长尾流量 | P1 |
| 产地地图 | 地图可视化产地分布，差异化 | P1 |
| 用户中心 | 注册/登录/会员/收藏/订阅 | P1 |
| 搜索中心 | 全站搜索，ES 驱动 | P1 |
| 后台管理 | 数据管理、爬虫监控、内容审核 | P0 |

## 4. 页面规划

### 4.1 行情中心 (P0)

| 页面 | 路由 | 功能说明 |
|------|------|---------|
| 行情首页 | `/market` | 涨跌榜、热门品种、市场快讯、价格指数 |
| 市场价格列表 | `/market/market-price` | 按市场（亳州/安国/成都等）筛选，表格+走势标签 |
| 产地价格列表 | `/market/origin-price` | 按产地筛选，含产地均价 |
| 品种详情页 | `/market/herb/:id` | 当前价、历史走势图(ECharts)、规格价格、产地价格、相关资讯、供需信息 |
| 历史价格查询 | `/market/history` | 自定义时间范围查询，支持导出 |
| 价格指数 | `/market/index` | 综合指数、分类指数走势图 |
| 涨跌排行 | `/market/ranking` | 日/周/月涨跌排行，升/降/稳分布 |
| 集采专区 | `/market/procurement` | 集中采购信息、政策动态 |

### 4.2 资讯中心 (P0)

| 页面 | 路由 | 功能说明 |
|------|------|---------|
| 资讯首页 | `/news` | 最新资讯、分类导航、推荐阅读 |
| 品种分析 | `/news/analysis` | 单品种深度分析文章 |
| 药市动态 | `/news/market-dynamic` | 市场整体走势评论 |
| 产地快报 | `/news/origin-report` | 各产地第一手产新信息 |
| 采购招标 | `/news/bidding` | 企业采购招标公告 |
| 集采资讯 | `/news/central-procurement` | 全国集采政策与进展 |
| 资讯详情 | `/news/:id` | 文章详情页，含相关品种链接 |
| 视频资讯 | `/news/video` | 行情直播回放、产地视频 |

### 4.3 供求中心 (P0)

| 页面 | 路由 | 功能说明 |
|------|------|---------|
| 供求首页 | `/trade` | 最新供应/求购、热门品种供求 |
| 供应列表 | `/trade/supply` | 按品种/产地/规格筛选 |
| 求购列表 | `/trade/demand` | 按品种/规格筛选，支持报价 |
| 发布供应 | `/trade/supply/publish` | 表单：品种、规格、数量、价格、产地、图片 |
| 发布求购 | `/trade/demand/publish` | 表单：品种、规格、数量、交货地、截止日期 |
| 供求详情 | `/trade/:type/:id` | 详情页，含联系方式（登录可见） |
| 企业采购 | `/trade/enterprise` | 药企大宗采购计划 |

### 4.4 品种百科 (P1)

| 页面 | 路由 | 功能说明 |
|------|------|---------|
| 百科首页 | `/wiki` | 分类浏览、热门药材、搜索 |
| 品种百科详情 | `/wiki/:id` | 药材性味归经、功效、产地、规格、种植技术、配伍 |
| 种植技术 | `/wiki/cultivation` | 种植技术文章列表 |
| 药材知识 | `/wiki/knowledge` | 中药知识科普文章 |
| 新闻法规 | `/wiki/regulation` | 行业政策法规 |

### 4.5 产地地图 (P1)

| 页面 | 路由 | 功能说明 |
|------|------|---------|
| 产地地图 | `/map` | Leaflet 地图，标注各产地位置、品种、价格 |
| 产地详情 | `/map/origin/:id` | 某产地的品种列表、价格、产新时间 |

### 4.6 用户中心 (P1)

| 页面 | 路由 | 功能说明 |
|------|------|---------|
| 登录/注册 | `/login`, `/register` | 手机号+验证码、微信扫码登录 |
| 个人中心 | `/user/profile` | 基本信息管理 |
| 我的收藏 | `/user/favorites` | 收藏的品种、资讯 |
| 我的发布 | `/user/posts` | 发布的供应/求购 |
| 价格订阅 | `/user/subscriptions` | 订阅品种价格变动提醒 |
| 浏览历史 | `/user/history` | 最近浏览的品种和资讯 |
| 会员中心 | `/user/membership` | 会员等级、权益、订阅管理 |

### 4.7 搜索中心 (P1)

| 页面 | 路由 | 功能说明 |
|------|------|---------|
| 全站搜索 | `/search?q=` | 品种、资讯、供求、百科统一搜索 |
| 搜索结果 | `/search` | 分类 Tab 展示，搜索建议/热词 |

### 4.8 后台管理 (P0)

| 页面 | 路由 | 功能说明 |
|------|------|---------|
| 仪表盘 | `/admin/dashboard` | 核心数据概览、爬虫状态监控 |
| 品种管理 | `/admin/herbs` | 品种CRUD、分类管理、规格管理 |
| 价格管理 | `/admin/prices` | 价格数据审核、人工录入、修正 |
| 资讯管理 | `/admin/articles` | 文章发布/编辑/审核 |
| 供求管理 | `/admin/trade` | 供求信息审核、违规处理 |
| 用户管理 | `/admin/users` | 用户列表、权限、会员管理 |
| 爬虫管理 | `/admin/crawler` | 爬虫任务配置、运行状态、数据预览 |
| 系统设置 | `/admin/settings` | 站点配置、广告位管理、友情链接 |

## 5. 前端架构

### 5.1 目录结构

```
tcm-web/
├── public/
├── src/
│   ├── api/              # API 请求封装
│   │   ├── market.js     # 行情相关
│   │   ├── news.js       # 资讯相关
│   │   ├── trade.js      # 供求相关
│   │   ├── wiki.js       # 百科相关
│   │   ├── user.js       # 用户相关
│   │   └── search.js     # 搜索相关
│   ├── assets/           # 静态资源
│   ├── components/       # 公共组件
│   │   ├── common/       # 通用：Header, Footer, Sidebar, Pagination
│   │   ├── market/       # 行情：PriceTable, TrendChart, RankingList
│   │   ├── trade/        # 供求：SupplyCard, DemandCard
│   │   └── map/          # 地图：MapView, MarkerPopup
│   ├── composables/      # 组合式函数
│   │   ├── usePrice.js   # 价格数据逻辑
│   │   ├── useAuth.js    # 认证逻辑
│   │   └── useMap.js     # 地图逻辑
│   ├── layouts/          # 布局组件
│   │   ├── DefaultLayout.vue
│   │   └── AdminLayout.vue
│   ├── router/           # 路由配置
│   ├── stores/           # Pinia 状态管理
│   │   ├── user.js
│   │   ├── market.js
│   │   └── app.js
│   ├── styles/           # 全局样式
│   ├── utils/            # 工具函数
│   └── views/            # 页面组件
│       ├── market/       # 行情页面
│       ├── news/         # 资讯页面
│       ├── trade/        # 供求页面
│       ├── wiki/         # 百科页面
│       ├── map/          # 地图页面
│       ├── user/         # 用户页面
│       ├── search/       # 搜索页面
│       └── admin/        # 后台页面
├── index.html
├── vite.config.js
└── package.json
```

### 5.2 关键组件

#### ECharts 图表组件

- **价格走势图**: 折线图，支持 1月/3月/6月/1年/3年/全部 时间范围切换
- **涨跌分布图**: 饼图/柱状图，展示升/降/稳/少占比
- **价格指数图**: 多线折线图，综合指数+分类指数
- **产地对比图**: 多产地价格对比折线图

#### Leaflet 地图组件

- 中国地图底图，标注各中药材产地
- 气泡大小表示品种数量，颜色表示价格趋势
- 点击产地弹出品种列表和最新价格
- 支持按品种筛选产地

## 6. 后端架构

### 6.1 目录结构

```
tcm-server/
├── src/main/java/com/tcm/
│   ├── TcmApplication.java
│   ├── config/              # 配置类
│   │   ├── SecurityConfig   # Spring Security + JWT
│   │   ├── ElasticsearchConfig
│   │   ├── RedisConfig
│   │   └── CorsConfig
│   ├── common/              # 公共模块
│   │   ├── result/          # 统一响应 Result<T>
│   │   ├── exception/       # 全局异常处理
│   │   ├── constants/       # 常量
│   │   └── utils/           # 工具类
│   ├── module/
│   │   ├── user/            # 用户模块
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── mapper/
│   │   │   ├── entity/
│   │   │   └── dto/
│   │   ├── market/          # 行情模块
│   │   ├── news/            # 资讯模块
│   │   ├── trade/           # 供求模块
│   │   ├── wiki/            # 百科模块
│   │   ├── search/          # 搜索模块
│   │   └── admin/           # 后台模块
│   └── crawler/             # 爬虫集成层（消费Kafka消息）
├── src/main/resources/
│   ├── application.yml
│   ├── mapper/              # MyBatis XML
│   └── db/migration/        # Flyway 数据库迁移
└── pom.xml
```

### 6.2 API 设计规范

- RESTful 风格
- 统一响应格式: `{ code: 200, message: "success", data: T }`
- JWT 认证，Token 放 Header: `Authorization: Bearer <token>`
- 分页参数: `page`, `size`, 返回 `total`, `pages`
- 版本控制: URL 路径 `/api/v1/`

### 6.3 核心 API 列表

```
# 行情
GET    /api/v1/market/prices          # 市场价格列表
GET    /api/v1/market/origin-prices   # 产地价格列表
GET    /api/v1/market/herbs/{id}      # 品种详情
GET    /api/v1/market/herbs/{id}/history  # 历史价格
GET    /api/v1/market/ranking         # 涨跌排行
GET    /api/v1/market/index           # 价格指数

# 资讯
GET    /api/v1/news                   # 资讯列表
GET    /api/v1/news/{id}              # 资讯详情
GET    /api/v1/news/categories        # 资讯分类

# 供求
GET    /api/v1/trade/supply           # 供应列表
GET    /api/v1/trade/demand           # 求购列表
POST   /api/v1/trade/supply           # 发布供应
POST   /api/v1/trade/demand           # 发布求购
GET    /api/v1/trade/{type}/{id}      # 供求详情

# 百科
GET    /api/v1/wiki/herbs             # 百科品种列表
GET    /api/v1/wiki/herbs/{id}        # 百科详情
GET    /api/v1/wiki/cultivation       # 种植技术
GET    /api/v1/wiki/knowledge         # 药材知识

# 搜索
GET    /api/v1/search?q=              # 全站搜索
GET    /api/v1/search/suggest?q=      # 搜索建议
GET    /api/v1/search/hot             # 热门搜索

# 用户
POST   /api/v1/auth/login             # 登录
POST   /api/v1/auth/register          # 注册
POST   /api/v1/auth/sms-code          # 发送验证码
GET    /api/v1/user/profile           # 个人信息
GET    /api/v1/user/favorites         # 收藏列表
POST   /api/v1/user/favorites         # 添加收藏
GET    /api/v1/user/subscriptions     # 订阅列表
POST   /api/v1/user/subscriptions     # 添加订阅

# 地图
GET    /api/v1/map/origins            # 产地列表（含坐标）
GET    /api/v1/map/origins/{id}       # 产地详情
```

## 7. 数据库设计

### 7.1 核心表

```sql
-- 品种分类表
herb_category (
  id BIGINT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  parent_id BIGINT,
  sort_order INT DEFAULT 0,
  created_at DATETIME,
  updated_at DATETIME
)

-- 品种表
herb (
  id BIGINT PRIMARY KEY,
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
  created_at DATETIME,
  updated_at DATETIME,
  INDEX idx_category (category_id),
  INDEX idx_pinyin (pinyin),
  INDEX idx_name (name)
)

-- 规格表
herb_spec (
  id BIGINT PRIMARY KEY,
  herb_id BIGINT NOT NULL,
  spec_name VARCHAR(100) NOT NULL,
  parent_id BIGINT,
  sort_order INT DEFAULT 0,
  created_at DATETIME,
  updated_at DATETIME,
  INDEX idx_herb (herb_id)
)

-- 市场表
market (
  id BIGINT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  province VARCHAR(50),
  city VARCHAR(50),
  address VARCHAR(300),
  created_at DATETIME
)

-- 市场价格表
market_price (
  id BIGINT PRIMARY KEY,
  herb_spec_id BIGINT NOT NULL,
  market_id BIGINT NOT NULL,
  price DECIMAL(10,2),
  change_amount DECIMAL(10,2),
  change_percent DECIMAL(8,2),
  trend ENUM('up','down','stable','rare'),
  price_date DATE NOT NULL,
  source VARCHAR(100),
  created_at DATETIME,
  INDEX idx_herb_spec_date (herb_spec_id, price_date),
  INDEX idx_market_date (market_id, price_date),
  INDEX idx_price_date (price_date)
)

-- 产地价格表
origin_price (
  id BIGINT PRIMARY KEY,
  herb_spec_id BIGINT NOT NULL,
  area_id BIGINT NOT NULL,
  price DECIMAL(10,2),
  change_amount DECIMAL(10,2),
  change_percent DECIMAL(8,2),
  trend ENUM('up','down','stable','rare'),
  price_date DATE NOT NULL,
  source VARCHAR(100),
  created_at DATETIME,
  INDEX idx_herb_spec_date (herb_spec_id, price_date),
  INDEX idx_area_date (area_id, price_date)
)

-- 产地表
origin_area (
  id BIGINT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  province VARCHAR(50),
  city VARCHAR(50),
  county VARCHAR(50),
  latitude DECIMAL(10,7),
  longitude DECIMAL(10,7),
  herb_count INT DEFAULT 0,
  created_at DATETIME,
  updated_at DATETIME,
  INDEX idx_location (latitude, longitude)
)

-- 价格历史表（按月分区）
price_history (
  id BIGINT PRIMARY KEY,
  herb_spec_id BIGINT NOT NULL,
  market_id BIGINT,
  area_id BIGINT,
  price_type ENUM('market','origin'),
  price DECIMAL(10,2),
  record_date DATE NOT NULL,
  created_at DATETIME,
  INDEX idx_herb_spec_date (herb_spec_id, record_date),
  INDEX idx_record_date (record_date)
)

-- 资讯表
article (
  id BIGINT PRIMARY KEY,
  title VARCHAR(300) NOT NULL,
  summary VARCHAR(500),
  content LONGTEXT,
  category ENUM('analysis','market_dynamic','origin_report','bidding','central_procurement','video'),
  sub_category VARCHAR(50),
  cover_image VARCHAR(500),
  herb_ids JSON,
  view_count INT DEFAULT 0,
  is_top TINYINT DEFAULT 0,
  status ENUM('draft','pending','published','rejected') DEFAULT 'draft',
  author_id BIGINT,
  published_at DATETIME,
  created_at DATETIME,
  updated_at DATETIME,
  INDEX idx_category (category),
  INDEX idx_status_published (status, published_at),
  FULLTEXT idx_title_content (title, content)
)

-- 供应信息
supply (
  id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  herb_id BIGINT NOT NULL,
  spec_id BIGINT,
  quantity DECIMAL(10,2),
  unit VARCHAR(20),
  price DECIMAL(10,2),
  price_type ENUM('fixed','negotiable','face_to_face') DEFAULT 'negotiable',
  origin_area_id BIGINT,
  origin_area_name VARCHAR(200),
  images JSON,
  contact_name VARCHAR(50),
  contact_phone VARCHAR(20),
  status ENUM('pending','active','expired','rejected','sold') DEFAULT 'pending',
  expire_at DATETIME,
  view_count INT DEFAULT 0,
  created_at DATETIME,
  updated_at DATETIME,
  INDEX idx_herb (herb_id),
  INDEX idx_status (status),
  INDEX idx_user (user_id)
)

-- 求购信息
demand (
  id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  herb_id BIGINT NOT NULL,
  spec_id BIGINT,
  quantity DECIMAL(10,2),
  unit VARCHAR(20),
  budget_price DECIMAL(10,2),
  origin_requirement VARCHAR(200),
  delivery_address VARCHAR(300),
  deadline DATETIME,
  contact_name VARCHAR(50),
  contact_phone VARCHAR(20),
  status ENUM('pending','active','expired','rejected','closed') DEFAULT 'pending',
  quote_count INT DEFAULT 0,
  view_count INT DEFAULT 0,
  created_at DATETIME,
  updated_at DATETIME,
  INDEX idx_herb (herb_id),
  INDEX idx_status (status),
  INDEX idx_user (user_id)
)

-- 用户表
user (
  id BIGINT PRIMARY KEY,
  phone VARCHAR(20) UNIQUE,
  password VARCHAR(200),
  nickname VARCHAR(50),
  avatar VARCHAR(500),
  company VARCHAR(200),
  role ENUM('guest','user','vip','admin') DEFAULT 'user',
  membership_level INT DEFAULT 0,
  membership_expire_at DATETIME,
  status ENUM('active','disabled') DEFAULT 'active',
  last_login_at DATETIME,
  created_at DATETIME,
  updated_at DATETIME,
  INDEX idx_phone (phone)
)

-- 收藏表
user_favorite (
  id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  target_type ENUM('herb','article','supply','demand'),
  target_id BIGINT NOT NULL,
  created_at DATETIME,
  UNIQUE INDEX idx_user_target (user_id, target_type, target_id)
)

-- 订阅表
user_subscription (
  id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  herb_id BIGINT NOT NULL,
  price_threshold DECIMAL(10,2),
  notify_type ENUM('email','sms','wechat','push'),
  is_active TINYINT DEFAULT 1,
  created_at DATETIME,
  updated_at DATETIME,
  INDEX idx_user (user_id),
  INDEX idx_herb (herb_id)
)

-- 价格指数表
price_index (
  id BIGINT PRIMARY KEY,
  index_type ENUM('composite','root_rhizome','fruit_seed','flower','leaf','bark','animal','mineral'),
  index_value DECIMAL(10,2),
  change_amount DECIMAL(10,2),
  change_percent DECIMAL(8,2),
  record_date DATE NOT NULL,
  created_at DATETIME,
  INDEX idx_type_date (index_type, record_date)
)
```

## 8. 权限模型

| 角色 | 行情 | 资讯 | 供求 | 百科 | 后台 |
|------|------|------|------|------|------|
| 游客 | 基础行情 | 可浏览 | 联系方式隐藏 | 可浏览 | 无 |
| 注册用户 | 完整行情 | 可浏览 | 可发布/联系方式可见 | 可浏览 | 无 |
| 会员用户 | 历史数据/导出 | 付费文章 | 优先展示 | 可浏览 | 无 |
| 管理员 | 全部 | 全部 | 全部 | 全部 | 全部 |

## 9. 爬虫服务设计

### 9.1 爬虫架构

```
tcm-crawler/
├── scrapy.cfg
├── tcm_crawler/
│   ├── __init__.py
│   ├── settings.py
│   ├── items.py
│   ├── pipelines.py
│   ├── middlewares.py
│   └── spiders/
│       ├── zyctd_price.py      # 天地网价格爬虫
│       ├── zyctd_news.py       # 天地网资讯爬虫
│       ├── yt1998_price.py     # 药通网价格爬虫
│       ├── yt1998_news.py      # 药通网资讯爬虫
│       └── gov_policy.py       # 政策法规爬虫
├── producer.py                  # Kafka 生产者
├── scheduler.py                 # 定时调度（APScheduler）
└── requirements.txt
```

### 9.2 爬虫调度

| 爬虫 | 频率 | 数据量 | Kafka Topic |
|------|------|--------|-------------|
| 天地网价格 | 每日 2 次 | ~1000 条/次 | tcm-price |
| 天地网资讯 | 每日 1 次 | ~50 条/次 | tcm-news |
| 药通网价格 | 每日 2 次 | ~800 条/次 | tcm-price |
| 药通网资讯 | 每日 1 次 | ~30 条/次 | tcm-news |
| 政策法规 | 每周 1 次 | ~10 条/次 | tcm-policy |

### 9.3 数据清洗规则

- 价格数据：去重（品种+规格+市场+日期唯一）、异常值过滤（涨跌超 200% 人工审核）
- 资讯数据：去重（标题相似度 > 90%）、HTML 清洗、图片下载到 OSS
- 产地数据：地址标准化、经纬度补全

## 10. 部署架构

### 10.1 Docker Compose

```yaml
services:
  nginx:        # 反向代理 + 静态资源
  tcm-web:      # Vue 3 前端 (Nginx 容器)
  tcm-server:   # Spring Boot 后端
  tcm-crawler:  # Python 爬虫
  mysql:        # MySQL 8.0
  redis:        # Redis 7
  elasticsearch: # ES 8.x
  kafka:        # Kafka + Zookeeper
```

### 10.2 缓存策略

| 数据类型 | 缓存 Key | TTL | 说明 |
|---------|----------|-----|------|
| 热门品种价格 | `market:hot` | 5min | 首页涨跌榜 |
| 品种详情 | `herb:{id}` | 30min | 品种基础信息 |
| 价格走势 | `herb:{id}:trend` | 1h | 历史走势数据 |
| 涨跌排行 | `market:ranking:{period}` | 10min | 日/周/月排行 |
| 搜索热词 | `search:hot` | 1h | 热门搜索词 |
| 资讯列表 | `news:list:{category}` | 10min | 分类资讯 |

## 11. 未来演进

1. **微服务拆分**: 按模块拆分为独立服务（行情服务、资讯服务、供求服务等）
2. **第三方 API 对接**: 政府开放数据、药通网 API
3. **实时推送**: WebSocket 价格变动推送
4. **AI 功能**: 价格预测、智能推荐、资讯摘要
5. **溯源系统**: 区块链溯源
6. **交易系统**: 在线交易、支付集成
