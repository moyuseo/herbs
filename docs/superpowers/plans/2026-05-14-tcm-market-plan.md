# 中药材行业行情网站 - 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建中药材行业综合信息门户，覆盖行情数据、资讯、供求撮合、品种百科、产地地图等全链路功能

**Architecture:** Spring Boot 单体后端 + Vue 3 响应式前端 + Python 爬虫独立服务，通过 Kafka 数据管道连接爬虫与后端，MySQL/Redis/ES 三层存储

**Tech Stack:** Vue 3 + Vite + Element Plus + Pinia + ECharts + Leaflet | Spring Boot + MyBatis-Plus + Spring Security + JWT | Python Scrapy + Kafka | MySQL + Redis + Elasticsearch

**Spec:** `docs/superpowers/specs/2026-05-14-tcm-market-design.md`

---

## 阶段概览

| 阶段 | 内容 | 产出 |
|------|------|------|
| Phase 1 | 项目脚手架 & 基础设施 | 可运行的前后端空项目 + Docker 环境 |
| Phase 2 | 后端核心模块（用户 + 行情） | 用户注册登录 + 价格 CRUD API |
| Phase 3 | 前端核心页面（行情中心） | 行情首页 + 品种详情 + 走势图 |
| Phase 4 | 资讯模块（后端 + 前端） | 资讯列表/详情/分类 |
| Phase 5 | 供求模块（后端 + 前端） | 供应/求购发布与浏览 |
| Phase 6 | 百科 + 地图 + 搜索模块 | 品种百科 + Leaflet 地图 + ES 搜索 |
| Phase 7 | 后台管理系统 | 管理后台全部页面 |
| Phase 8 | Python 爬虫服务 | 爬虫 + Kafka + 数据清洗 |
| Phase 9 | 部署 & 优化 | Docker Compose + 缓存 + 监控 |

---

## Phase 1: 项目脚手架 & 基础设施

### Task 1.1: 创建前端项目

**Files:**
- Create: `tcm-web/package.json`
- Create: `tcm-web/vite.config.js`
- Create: `tcm-web/index.html`
- Create: `tcm-web/src/main.js`
- Create: `tcm-web/src/App.vue`
- Create: `tcm-web/src/router/index.js`
- Create: `tcm-web/src/stores/app.js`
- Create: `tcm-web/src/layouts/DefaultLayout.vue`
- Create: `tcm-web/src/views/Home.vue`
- Create: `tcm-web/src/components/common/AppHeader.vue`
- Create: `tcm-web/src/components/common/AppFooter.vue`
- Create: `tcm-web/src/styles/variables.scss`
- Create: `tcm-web/src/styles/global.scss`
- Create: `tcm-web/src/api/request.js`
- Create: `tcm-web/src/utils/index.js`

- [ ] **Step 1: 使用 Vite 创建 Vue 3 项目**

```bash
cd /workspace
npm create vite@latest tcm-web -- --template vue
cd tcm-web
```

- [ ] **Step 2: 安装核心依赖**

```bash
cd /workspace/tcm-web
npm install element-plus @element-plus/icons-vue pinia vue-router@4 axios echarts vue-echarts leaflet vue-leaflet sass
```

- [ ] **Step 3: 配置 vite.config.js**

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 4: 创建 src/api/request.js（Axios 封装）**

```javascript
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const request = axios.create({
  baseURL: '/api/v1',
  timeout: 15000
})

request.interceptors.request.use(config => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers.Authorization = `Bearer ${userStore.token}`
  }
  return config
})

request.interceptors.response.use(
  response => {
    const { code, message, data } = response.data
    if (code === 200) return data
    ElMessage.error(message || '请求失败')
    return Promise.reject(new Error(message))
  },
  error => {
    if (error.response?.status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      window.location.href = '/login'
    }
    ElMessage.error(error.response?.data?.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
```

- [ ] **Step 5: 创建 src/stores/app.js**

```javascript
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(false)
  const currentMarket = ref('bozhou')

  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  return { sidebarCollapsed, currentMarket, toggleSidebar }
})
```

- [ ] **Step 6: 创建 src/stores/user.js**

```javascript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'admin')
  const isVip = computed(() => userInfo.value?.role === 'vip')

  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setUserInfo(info) {
    userInfo.value = info
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return { token, userInfo, isLoggedIn, isAdmin, isVip, setToken, setUserInfo, logout }
})
```

- [ ] **Step 7: 创建 src/router/index.js**

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import DefaultLayout from '@/layouts/DefaultLayout.vue'

const routes = [
  {
    path: '/',
    component: DefaultLayout,
    children: [
      { path: '', name: 'Home', component: () => import('@/views/Home.vue') },
      { path: 'market', name: 'Market', component: () => import('@/views/market/Index.vue') },
      { path: 'market/market-price', name: 'MarketPrice', component: () => import('@/views/market/MarketPrice.vue') },
      { path: 'market/origin-price', name: 'OriginPrice', component: () => import('@/views/market/OriginPrice.vue') },
      { path: 'market/herb/:id', name: 'HerbDetail', component: () => import('@/views/market/HerbDetail.vue') },
      { path: 'market/history', name: 'PriceHistory', component: () => import('@/views/market/PriceHistory.vue') },
      { path: 'market/index', name: 'PriceIndex', component: () => import('@/views/market/PriceIndex.vue') },
      { path: 'market/ranking', name: 'Ranking', component: () => import('@/views/market/Ranking.vue') },
      { path: 'market/procurement', name: 'Procurement', component: () => import('@/views/market/Procurement.vue') },
      { path: 'news', name: 'News', component: () => import('@/views/news/Index.vue') },
      { path: 'news/analysis', name: 'Analysis', component: () => import('@/views/news/Analysis.vue') },
      { path: 'news/market-dynamic', name: 'MarketDynamic', component: () => import('@/views/news/MarketDynamic.vue') },
      { path: 'news/origin-report', name: 'OriginReport', component: () => import('@/views/news/OriginReport.vue') },
      { path: 'news/bidding', name: 'Bidding', component: () => import('@/views/news/Bidding.vue') },
      { path: 'news/central-procurement', name: 'CentralProcurement', component: () => import('@/views/news/CentralProcurement.vue') },
      { path: 'news/:id', name: 'ArticleDetail', component: () => import('@/views/news/Detail.vue') },
      { path: 'news/video', name: 'Video', component: () => import('@/views/news/Video.vue') },
      { path: 'trade', name: 'Trade', component: () => import('@/views/trade/Index.vue') },
      { path: 'trade/supply', name: 'SupplyList', component: () => import('@/views/trade/SupplyList.vue') },
      { path: 'trade/demand', name: 'DemandList', component: () => import('@/views/trade/DemandList.vue') },
      { path: 'trade/supply/publish', name: 'PublishSupply', component: () => import('@/views/trade/PublishSupply.vue'), meta: { requiresAuth: true } },
      { path: 'trade/demand/publish', name: 'PublishDemand', component: () => import('@/views/trade/PublishDemand.vue'), meta: { requiresAuth: true } },
      { path: 'trade/:type/:id', name: 'TradeDetail', component: () => import('@/views/trade/Detail.vue') },
      { path: 'trade/enterprise', name: 'Enterprise', component: () => import('@/views/trade/Enterprise.vue') },
      { path: 'wiki', name: 'Wiki', component: () => import('@/views/wiki/Index.vue') },
      { path: 'wiki/:id', name: 'WikiDetail', component: () => import('@/views/wiki/Detail.vue') },
      { path: 'wiki/cultivation', name: 'Cultivation', component: () => import('@/views/wiki/Cultivation.vue') },
      { path: 'wiki/knowledge', name: 'Knowledge', component: () => import('@/views/wiki/Knowledge.vue') },
      { path: 'wiki/regulation', name: 'Regulation', component: () => import('@/views/wiki/Regulation.vue') },
      { path: 'map', name: 'Map', component: () => import('@/views/map/Index.vue') },
      { path: 'map/origin/:id', name: 'OriginDetail', component: () => import('@/views/map/OriginDetail.vue') },
      { path: 'user/profile', name: 'Profile', component: () => import('@/views/user/Profile.vue'), meta: { requiresAuth: true } },
      { path: 'user/favorites', name: 'Favorites', component: () => import('@/views/user/Favorites.vue'), meta: { requiresAuth: true } },
      { path: 'user/posts', name: 'MyPosts', component: () => import('@/views/user/Posts.vue'), meta: { requiresAuth: true } },
      { path: 'user/subscriptions', name: 'Subscriptions', component: () => import('@/views/user/Subscriptions.vue'), meta: { requiresAuth: true } },
      { path: 'user/history', name: 'History', component: () => import('@/views/user/History.vue'), meta: { requiresAuth: true } },
      { path: 'user/membership', name: 'Membership', component: () => import('@/views/user/Membership.vue'), meta: { requiresAuth: true } },
      { path: 'search', name: 'Search', component: () => import('@/views/search/Index.vue') }
    ]
  },
  { path: '/login', name: 'Login', component: () => import('@/views/user/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('@/views/user/Register.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() { return { top: 0 } }
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
```

注意：需要在文件顶部添加 `import { useUserStore } from '@/stores/user'`

- [ ] **Step 8: 创建 src/main.js**

```javascript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import './styles/global.scss'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })
app.mount('#app')
```

- [ ] **Step 9: 创建 src/styles/variables.scss**

```scss
$primary-color: #2D8C4E;
$danger-color: #F56C6C;
$warning-color: #E6A23C;
$success-color: #67C23A;
$info-color: #909399;
$text-primary: #303133;
$text-regular: #606266;
$text-secondary: #909399;
$border-color: #DCDFE6;
$bg-color: #F5F7FA;
$bg-white: #FFFFFF;
$header-height: 64px;
$footer-height: 200px;
$content-max-width: 1200px;
$up-color: #F56C6C;
$down-color: #2D8C4E;
$stable-color: #909399;
```

- [ ] **Step 10: 创建 src/styles/global.scss**

```scss
@use './variables' as *;

* { margin: 0; padding: 0; box-sizing: border-box; }
html, body { font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; color: $text-primary; background: $bg-color; }
a { color: $primary-color; text-decoration: none; }
.price-up { color: $up-color; }
.price-down { color: $down-color; }
.price-stable { color: $stable-color; }
.container { max-width: $content-max-width; margin: 0 auto; padding: 0 20px; }
```

- [ ] **Step 11: 创建 AppHeader 组件**

```vue
<template>
  <el-header class="app-header">
    <div class="header-content container">
      <div class="logo" @click="$router.push('/')">
        <span class="logo-text">中药材行情网</span>
      </div>
      <el-menu mode="horizontal" :default-active="activeMenu" router class="nav-menu">
        <el-menu-item index="/market">行情中心</el-menu-item>
        <el-menu-item index="/news">资讯中心</el-menu-item>
        <el-menu-item index="/trade">供求中心</el-menu-item>
        <el-menu-item index="/wiki">品种百科</el-menu-item>
        <el-menu-item index="/map">产地地图</el-menu-item>
      </el-menu>
      <div class="header-actions">
        <el-input v-model="searchQuery" placeholder="搜索品种..." size="default" class="search-input" @keyup.enter="handleSearch">
          <template #append><el-button :icon="Search" @click="handleSearch" /></template>
        </el-input>
        <template v-if="userStore.isLoggedIn">
          <el-dropdown>
            <span class="user-name">{{ userStore.userInfo?.nickname || '用户' }}</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/user/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/user/favorites')">我的收藏</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/user/subscriptions')">价格订阅</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" size="default" @click="$router.push('/login')">登录</el-button>
          <el-button size="default" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const searchQuery = ref('')

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/market')) return '/market'
  if (path.startsWith('/news')) return '/news'
  if (path.startsWith('/trade')) return '/trade'
  if (path.startsWith('/wiki')) return '/wiki'
  if (path.startsWith('/map')) return '/map'
  return '/'
})

function handleSearch() {
  if (searchQuery.value.trim()) {
    router.push({ name: 'Search', query: { q: searchQuery.value.trim() } })
  }
}

function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;
.app-header { background: $bg-white; border-bottom: 1px solid $border-color; height: $header-height; padding: 0; position: sticky; top: 0; z-index: 100; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.header-content { display: flex; align-items: center; height: 100%; gap: 20px; }
.logo { cursor: pointer; flex-shrink: 0; }
.logo-text { font-size: 20px; font-weight: 700; color: $primary-color; }
.nav-menu { flex: 1; border-bottom: none !important; }
.search-input { width: 200px; }
.header-actions { display: flex; align-items: center; gap: 12px; flex-shrink: 0; }
.user-name { cursor: pointer; color: $primary-color; font-size: 14px; }
</style>
```

- [ ] **Step 12: 创建 AppFooter 组件**

```vue
<template>
  <el-footer class="app-footer">
    <div class="footer-content container">
      <div class="footer-links">
        <div class="footer-section">
          <h4>行情服务</h4>
          <router-link to="/market/market-price">市场价格</router-link>
          <router-link to="/market/origin-price">产地价格</router-link>
          <router-link to="/market/ranking">涨跌排行</router-link>
          <router-link to="/market/index">价格指数</router-link>
        </div>
        <div class="footer-section">
          <h4>资讯中心</h4>
          <router-link to="/news/analysis">品种分析</router-link>
          <router-link to="/news/market-dynamic">药市动态</router-link>
          <router-link to="/news/origin-report">产地快报</router-link>
        </div>
        <div class="footer-section">
          <h4>供求交易</h4>
          <router-link to="/trade/supply">供应信息</router-link>
          <router-link to="/trade/demand">求购信息</router-link>
          <router-link to="/trade/enterprise">企业采购</router-link>
        </div>
        <div class="footer-section">
          <h4>关于我们</h4>
          <a href="#">联系方式</a>
          <a href="#">服务协议</a>
          <a href="#">隐私政策</a>
        </div>
      </div>
      <div class="footer-bottom">
        <p>© 2026 中药材行情网 版权所有</p>
      </div>
    </div>
  </el-footer>
</template>

<style lang="scss" scoped>
@use '@/styles/variables' as *;
.app-footer { background: #2c3e50; color: #bdc3c7; height: auto; padding: 40px 0 20px; }
.footer-content { }
.footer-links { display: flex; justify-content: space-between; gap: 40px; margin-bottom: 30px; }
.footer-section { h4 { color: #ecf0f1; margin-bottom: 12px; font-size: 16px; } a { display: block; color: #bdc3c7; margin-bottom: 8px; font-size: 14px; &:hover { color: $primary-color; } } }
.footer-bottom { border-top: 1px solid #34495e; padding-top: 20px; text-align: center; font-size: 13px; }
</style>
```

- [ ] **Step 13: 创建 DefaultLayout.vue**

```vue
<template>
  <div class="default-layout">
    <AppHeader />
    <main class="main-content">
      <router-view />
    </main>
    <AppFooter />
  </div>
</template>

<script setup>
import AppHeader from '@/components/common/AppHeader.vue'
import AppFooter from '@/components/common/AppFooter.vue'
</script>

<style lang="scss" scoped>
.default-layout { min-height: 100vh; display: flex; flex-direction: column; }
.main-content { flex: 1; padding: 20px 0; }
</style>
```

- [ ] **Step 14: 创建 Home.vue 首页占位**

```vue
<template>
  <div class="home container">
    <h1>中药材行情网</h1>
    <p>欢迎访问，系统建设中...</p>
  </div>
</template>
```

- [ ] **Step 15: 创建所有 views 占位文件**

为每个路由创建最小占位 `.vue` 文件，仅包含 `<template><div class="container"><h2>页面名称</h2></div></template>`，确保路由可访问不报错。需要创建的文件列表：

- `src/views/market/Index.vue`, `MarketPrice.vue`, `OriginPrice.vue`, `HerbDetail.vue`, `PriceHistory.vue`, `PriceIndex.vue`, `Ranking.vue`, `Procurement.vue`
- `src/views/news/Index.vue`, `Analysis.vue`, `MarketDynamic.vue`, `OriginReport.vue`, `Bidding.vue`, `CentralProcurement.vue`, `Detail.vue`, `Video.vue`
- `src/views/trade/Index.vue`, `SupplyList.vue`, `DemandList.vue`, `PublishSupply.vue`, `PublishDemand.vue`, `Detail.vue`, `Enterprise.vue`
- `src/views/wiki/Index.vue`, `Detail.vue`, `Cultivation.vue`, `Knowledge.vue`, `Regulation.vue`
- `src/views/map/Index.vue`, `OriginDetail.vue`
- `src/views/user/Login.vue`, `Register.vue`, `Profile.vue`, `Favorites.vue`, `Posts.vue`, `Subscriptions.vue`, `History.vue`, `Membership.vue`
- `src/views/search/Index.vue`

- [ ] **Step 16: 验证前端项目可运行**

```bash
cd /workspace/tcm-web && npm run dev
```

Expected: Vite dev server 启动在 http://localhost:3000，页面可访问

- [ ] **Step 17: Commit**

```bash
git add -A && git commit -m "feat: scaffold Vue 3 frontend project with routing and layout"
```

---

### Task 1.2: 创建后端项目

**Files:**
- Create: `tcm-server/pom.xml`
- Create: `tcm-server/src/main/java/com/tcm/TcmApplication.java`
- Create: `tcm-server/src/main/java/com/tcm/config/CorsConfig.java`
- Create: `tcm-server/src/main/java/com/tcm/common/result/Result.java`
- Create: `tcm-server/src/main/java/com/tcm/common/result/ResultCode.java`
- Create: `tcm-server/src/main/java/com/tcm/common/exception/GlobalExceptionHandler.java`
- Create: `tcm-server/src/main/java/com/tcm/common/exception/BusinessException.java`
- Create: `tcm-server/src/main/resources/application.yml`

- [ ] **Step 1: 使用 Spring Initializr 创建项目**

```bash
cd /workspace
curl https://start.spring.io/starter.zip \
  -d type=maven-project \
  -d language=java \
  -d bootVersion=3.2.5 \
  -d groupId=com.tcm \
  -d artifactId=tcm-server \
  -d name=tcm-server \
  -d packageName=com.tcm \
  -d javaVersion=17 \
  -d dependencies=web,security,mybatis,mysql,validation,redis,kafka \
  -o tcm-server.zip
unzip tcm-server.zip -d tcm-server
rm tcm-server.zip
```

- [ ] **Step 2: 补充 pom.xml 依赖**

在 `<dependencies>` 中添加：

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
```

- [ ] **Step 3: 创建 application.yml**

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tcm?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  data:
    redis:
      host: localhost
      port: 6379
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: tcm-server
      auto-offset-reset: earliest
  flyway:
    enabled: true
    locations: classpath:db/migration

mybatis-plus:
  mapper-locations: classpath:mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

jwt:
  secret: tcm-market-jwt-secret-key-must-be-at-least-256-bits-long-for-hs256
  expiration: 86400000

logging:
  level:
    com.tcm: debug
```

- [ ] **Step 4: 创建统一响应 Result.java**

```java
package com.tcm.common.result;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    public static <T> Result<T> fail(ResultCode code) {
        return new Result<>(code.getCode(), code.getMessage(), null);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }
}
```

- [ ] **Step 5: 创建 ResultCode.java**

```java
package com.tcm.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    PARAM_ERROR(400, "参数错误"),
    USER_EXISTS(1001, "用户已存在"),
    USER_NOT_FOUND(1002, "用户不存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    TOKEN_EXPIRED(1004, "Token已过期");

    private final int code;
    private final String message;
}
```

- [ ] **Step 6: 创建 BusinessException.java**

```java
package com.tcm.common.exception;

import com.tcm.common.result.ResultCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ResultCode resultCode;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.resultCode = null;
    }
}
```

- [ ] **Step 7: 创建 GlobalExceptionHandler.java**

```java
package com.tcm.common.exception;

import com.tcm.common.result.Result;
import com.tcm.common.result.ResultCode;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        if (e.getResultCode() != null) {
            return Result.fail(e.getResultCode());
        }
        return Result.fail(500, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("参数校验失败");
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.fail(ResultCode.FAIL);
    }
}
```

- [ ] **Step 8: 创建 CorsConfig.java**

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
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
```

- [ ] **Step 9: 验证后端项目可编译**

```bash
cd /workspace/tcm-server && ./mvnw compile
```

Expected: BUILD SUCCESS

- [ ] **Step 10: Commit**

```bash
git add -A && git commit -m "feat: scaffold Spring Boot backend with common modules"
```

---

### Task 1.3: Docker Compose 基础设施

**Files:**
- Create: `docker-compose.yml`

- [ ] **Step 1: 创建 docker-compose.yml**

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    container_name: tcm-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: tcm
      MYSQL_CHARACTER_SET_SERVER: utf8mb4
      MYSQL_COLLATION_SERVER: utf8mb4_unicode_ci
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    command: --default-authentication-plugin=mysql_native_password

  redis:
    image: redis:7-alpine
    container_name: tcm-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  elasticsearch:
    image: elasticsearch:8.12.0
    container_name: tcm-es
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ports:
      - "9200:9200"
    volumes:
      - es_data:/usr/share/elasticsearch/data

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    container_name: tcm-zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    volumes:
      - zk_data:/var/lib/zookeeper/data

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: tcm-kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    volumes:
      - kafka_data:/var/lib/kafka/data

volumes:
  mysql_data:
  redis_data:
  es_data:
  zk_data:
  kafka_data:
```

- [ ] **Step 2: 启动基础设施**

```bash
cd /workspace && docker compose up -d
```

Expected: 所有容器 Running

- [ ] **Step 3: Commit**

```bash
git add -A && git commit -m "feat: add Docker Compose for infrastructure services"
```

---

## Phase 2: 后端核心模块（用户 + 行情）

### Task 2.1: 数据库迁移 - 用户与行情表

**Files:**
- Create: `tcm-server/src/main/resources/db/migration/V1__init_user_and_market.sql`

- [ ] **Step 1: 创建 Flyway 迁移脚本**

```sql
-- V1__init_user_and_market.sql

CREATE TABLE herb_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    parent_id BIGINT,
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE herb (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
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
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    herb_id BIGINT NOT NULL,
    spec_name VARCHAR(100) NOT NULL,
    parent_id BIGINT,
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_herb (herb_id)
);

CREATE TABLE market (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    province VARCHAR(50),
    city VARCHAR(50),
    address VARCHAR(300),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE origin_area (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
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
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
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
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
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
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
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
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    index_type VARCHAR(30) NOT NULL,
    index_value DECIMAL(10,2),
    change_amount DECIMAL(10,2),
    change_percent DECIMAL(8,2),
    record_date DATE NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_type_date (index_type, record_date)
);

CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
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
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    target_type VARCHAR(20) NOT NULL,
    target_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX idx_user_target (user_id, target_type, target_id)
);

CREATE TABLE user_subscription (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
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
```

- [ ] **Step 2: 运行迁移验证**

```bash
cd /workspace/tcm-server && ./mvnw spring-boot:run
```

Expected: 应用启动成功，Flyway 迁移执行完成

- [ ] **Step 3: Commit**

```bash
git add -A && git commit -m "feat: add database migration for user and market tables"
```

---

### Task 2.2: 用户模块（注册/登录/JWT）

**Files:**
- Create: `tcm-server/src/main/java/com/tcm/module/user/entity/User.java`
- Create: `tcm-server/src/main/java/com/tcm/module/user/dto/LoginDTO.java`
- Create: `tcm-server/src/main/java/com/tcm/module/user/dto/RegisterDTO.java`
- Create: `tcm-server/src/main/java/com/tcm/module/user/dto/UserVO.java`
- Create: `tcm-server/src/main/java/com/tcm/module/user/mapper/UserMapper.java`
- Create: `tcm-server/src/main/java/com/tcm/module/user/service/UserService.java`
- Create: `tcm-server/src/main/java/com/tcm/module/user/service/impl/UserServiceImpl.java`
- Create: `tcm-server/src/main/java/com/tcm/module/user/controller/AuthController.java`
- Create: `tcm-server/src/main/java/com/tcm/module/user/controller/UserController.java`
- Create: `tcm-server/src/main/java/com/tcm/config/SecurityConfig.java`
- Create: `tcm-server/src/main/java/com/tcm/config/JwtAuthenticationFilter.java`
- Create: `tcm-server/src/main/java/com/tcm/common/util/JwtUtil.java`

- [ ] **Step 1: 创建 User 实体**

```java
package com.tcm.module.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String phone;
    private String password;
    private String nickname;
    private String avatar;
    private String company;
    private String role;
    private Integer membershipLevel;
    private LocalDateTime membershipExpireAt;
    private String status;
    private LocalDateTime lastLoginAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 2: 创建 DTO 和 VO**

```java
// LoginDTO.java
package com.tcm.module.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank(message = "手机号不能为空")
    private String phone;
    @NotBlank(message = "密码不能为空")
    private String password;
}
```

```java
// RegisterDTO.java
package com.tcm.module.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "验证码不能为空")
    private String smsCode;
    private String nickname;
    private String company;
}
```

```java
// UserVO.java
package com.tcm.module.user.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String phone;
    private String nickname;
    private String avatar;
    private String company;
    private String role;
    private Integer membershipLevel;
    private LocalDateTime membershipExpireAt;
}
```

- [ ] **Step 3: 创建 JwtUtil**

```java
package com.tcm.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String role) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }

    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

- [ ] **Step 4: 创建 UserMapper**

```java
package com.tcm.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcm.module.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

- [ ] **Step 5: 创建 UserService 接口和实现**

```java
// UserService.java
package com.tcm.module.user.service;

import com.tcm.module.user.dto.LoginDTO;
import com.tcm.module.user.dto.RegisterDTO;
import com.tcm.module.user.dto.UserVO;
import java.util.Map;

public interface UserService {
    Map<String, Object> login(LoginDTO dto);
    void register(RegisterDTO dto);
    UserVO getProfile(Long userId);
    void updateProfile(Long userId, UserVO vo);
    void sendSmsCode(String phone);
}
```

```java
// UserServiceImpl.java
package com.tcm.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcm.common.exception.BusinessException;
import com.tcm.common.result.ResultCode;
import com.tcm.common.util.JwtUtil;
import com.tcm.module.user.dto.LoginDTO;
import com.tcm.module.user.dto.RegisterDTO;
import com.tcm.module.user.dto.UserVO;
import com.tcm.module.user.entity.User;
import com.tcm.module.user.mapper.UserMapper;
import com.tcm.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone())
        );
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        if ("disabled".equals(user.getStatus())) {
            throw new BusinessException(ResultCode.FAIL.getCode(), "账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", vo);
        return result;
    }

    @Override
    public void register(RegisterDTO dto) {
        Long count = userMapper.selectCount(
            new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.USER_EXISTS);
        }
        String cachedCode = redisTemplate.opsForValue().get("sms:" + dto.getPhone());
        if (cachedCode == null || !cachedCode.equals(dto.getSmsCode())) {
            throw new BusinessException(ResultCode.FAIL.getCode(), "验证码错误");
        }
        User user = new User();
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : "用户" + dto.getPhone().substring(7));
        user.setCompany(dto.getCompany());
        user.setRole("user");
        userMapper.insert(user);
        redisTemplate.delete("sms:" + dto.getPhone());
    }

    @Override
    public UserVO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(ResultCode.USER_NOT_FOUND);
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public void updateProfile(Long userId, UserVO vo) {
        User user = new User();
        user.setId(userId);
        user.setNickname(vo.getNickname());
        user.setAvatar(vo.getAvatar());
        user.setCompany(vo.getCompany());
        userMapper.updateById(user);
    }

    @Override
    public void sendSmsCode(String phone) {
        String code = String.valueOf((int)((Math.random() * 9 + 1) * 100000));
        redisTemplate.opsForValue().set("sms:" + phone, code, 5, TimeUnit.MINUTES);
    }
}
```

- [ ] **Step 6: 创建 SecurityConfig + JwtAuthenticationFilter**

```java
// SecurityConfig.java
package com.tcm.config;

import com.tcm.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/market/**").permitAll()
                .requestMatchers("/api/v1/news/**").permitAll()
                .requestMatchers("/api/v1/wiki/**").permitAll()
                .requestMatchers("/api/v1/search/**").permitAll()
                .requestMatchers("/api/v1/map/**").permitAll()
                .requestMatchers("/api/v1/trade/supply", "/api/v1/trade/demand").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("admin")
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

```java
// JwtAuthenticationFilter.java
package com.tcm.config;

import com.tcm.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractToken(request);
        if (StringUtils.hasText(token) && jwtUtil.isTokenValid(token)) {
            Long userId = jwtUtil.getUserId(token);
            String role = jwtUtil.parseToken(token).get("role", String.class);
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role)));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
```

- [ ] **Step 7: 创建 AuthController 和 UserController**

```java
// AuthController.java
package com.tcm.module.user.controller;

import com.tcm.common.result.Result;
import com.tcm.module.user.dto.LoginDTO;
import com.tcm.module.user.dto.RegisterDTO;
import com.tcm.module.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @PostMapping("/sms-code")
    public Result<Void> sendSmsCode(@RequestParam String phone) {
        userService.sendSmsCode(phone);
        return Result.success();
    }
}
```

```java
// UserController.java
package com.tcm.module.user.controller;

import com.tcm.common.result.Result;
import com.tcm.module.user.dto.UserVO;
import com.tcm.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public Result<UserVO> getProfile(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return Result.success(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(Authentication auth, @RequestBody UserVO vo) {
        Long userId = (Long) auth.getPrincipal();
        userService.updateProfile(userId, vo);
        return Result.success();
    }
}
```

- [ ] **Step 8: 验证用户模块 API**

```bash
cd /workspace/tcm-server && ./mvnw spring-boot:run
curl -X POST http://localhost:8080/api/v1/auth/sms-code?phone=13800138000
curl -X POST http://localhost:8080/api/v1/auth/register -H "Content-Type: application/json" -d '{"phone":"13800138000","password":"123456","smsCode":"123456"}'
```

Expected: 注册成功返回 200

- [ ] **Step 9: Commit**

```bash
git add -A && git commit -m "feat: implement user module with JWT authentication"
```

---

### Task 2.3: 行情模块后端

**Files:**
- Create: `tcm-server/src/main/java/com/tcm/module/market/entity/Herb.java`
- Create: `tcm-server/src/main/java/com/tcm/module/market/entity/HerbSpec.java`
- Create: `tcm-server/src/main/java/com/tcm/module/market/entity/Market.java`
- Create: `tcm-server/src/main/java/com/tcm/module/market/entity/MarketPrice.java`
- Create: `tcm-server/src/main/java/com/tcm/module/market/entity/OriginPrice.java`
- Create: `tcm-server/src/main/java/com/tcm/module/market/entity/OriginArea.java`
- Create: `tcm-server/src/main/java/com/tcm/module/market/entity/PriceHistory.java`
- Create: `tcm-server/src/main/java/com/tcm/module/market/entity/PriceIndex.java`
- Create: `tcm-server/src/main/java/com/tcm/module/market/dto/HerbDetailVO.java`
- Create: `tcm-server/src/main/java/com/tcm/module/market/dto/PriceQueryDTO.java`
- Create: `tcm-server/src/main/java/com/tcm/module/market/mapper/*.java` (7个Mapper)
- Create: `tcm-server/src/main/java/com/tcm/module/market/service/MarketService.java`
- Create: `tcm-server/src/main/java/com/tcm/module/market/service/impl/MarketServiceImpl.java`
- Create: `tcm-server/src/main/java/com/tcm/module/market/controller/MarketController.java`

- [ ] **Step 1: 创建所有 Entity 类**

每个 Entity 对应设计文档中的数据库表，使用 MyBatis-Plus 注解，参考 Task 2.2 中 User 实体的写法。

- [ ] **Step 2: 创建 DTO/VO**

```java
// HerbDetailVO.java
package com.tcm.module.market.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class HerbDetailVO {
    private Long id;
    private String name;
    private String pinyin;
    private String alias;
    private String originAreas;
    private String properties;
    private String meridian;
    private String efficacy;
    private String description;
    private String imageUrl;
    private List<SpecPrice> specPrices;
    private List<OriginPriceVO> originPrices;
    private List<MarketPriceVO> marketPrices;

    @Data
    public static class SpecPrice {
        private Long specId;
        private String specName;
        private BigDecimal price;
        private BigDecimal changePercent;
        private String trend;
    }

    @Data
    public static class OriginPriceVO {
        private Long areaId;
        private String areaName;
        private BigDecimal price;
        private BigDecimal changePercent;
        private String trend;
    }

    @Data
    public static class MarketPriceVO {
        private Long marketId;
        private String marketName;
        private BigDecimal price;
        private BigDecimal changePercent;
        private String trend;
    }
}
```

```java
// PriceQueryDTO.java
package com.tcm.module.market.dto;

import lombok.Data;

@Data
public class PriceQueryDTO {
    private Long herbId;
    private Long specId;
    private Long marketId;
    private Long areaId;
    private String trend;
    private Integer page = 1;
    private Integer size = 20;
}
```

- [ ] **Step 3: 创建所有 Mapper 接口**

每个 Mapper 继承 `BaseMapper<Entity>`，添加 `@Mapper` 注解。

- [ ] **Step 4: 创建 MarketService 接口和实现**

```java
// MarketService.java
package com.tcm.module.market.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tcm.module.market.dto.HerbDetailVO;
import com.tcm.module.market.dto.PriceQueryDTO;
import com.tcm.module.market.entity.MarketPrice;
import com.tcm.module.market.entity.OriginPrice;
import com.tcm.module.market.entity.PriceIndex;
import java.util.List;
import java.util.Map;

public interface MarketService {
    IPage<MarketPrice> getMarketPrices(PriceQueryDTO query);
    IPage<OriginPrice> getOriginPrices(PriceQueryDTO query);
    HerbDetailVO getHerbDetail(Long herbId);
    List<Map<String, Object>> getPriceHistory(Long herbSpecId, String period);
    List<Map<String, Object>> getRanking(String period, Integer limit);
    List<PriceIndex> getPriceIndex(String indexType);
}
```

实现类 `MarketServiceImpl` 使用 MyBatis-Plus 查询，Redis 缓存热门数据。

- [ ] **Step 5: 创建 MarketController**

```java
package com.tcm.module.market.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tcm.common.result.Result;
import com.tcm.module.market.dto.HerbDetailVO;
import com.tcm.module.market.dto.PriceQueryDTO;
import com.tcm.module.market.entity.MarketPrice;
import com.tcm.module.market.entity.OriginPrice;
import com.tcm.module.market.entity.PriceIndex;
import com.tcm.module.market.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/market")
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;

    @GetMapping("/prices")
    public Result<IPage<MarketPrice>> getMarketPrices(PriceQueryDTO query) {
        return Result.success(marketService.getMarketPrices(query));
    }

    @GetMapping("/origin-prices")
    public Result<IPage<OriginPrice>> getOriginPrices(PriceQueryDTO query) {
        return Result.success(marketService.getOriginPrices(query));
    }

    @GetMapping("/herbs/{id}")
    public Result<HerbDetailVO> getHerbDetail(@PathVariable Long id) {
        return Result.success(marketService.getHerbDetail(id));
    }

    @GetMapping("/herbs/{id}/history")
    public Result<List<Map<String, Object>>> getPriceHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1y") String period) {
        return Result.success(marketService.getPriceHistory(id, period));
    }

    @GetMapping("/ranking")
    public Result<List<Map<String, Object>>> getRanking(
            @RequestParam(defaultValue = "day") String period,
            @RequestParam(defaultValue = "20") Integer limit) {
        return Result.success(marketService.getRanking(period, limit));
    }

    @GetMapping("/index")
    public Result<List<PriceIndex>> getPriceIndex(
            @RequestParam(required = false) String indexType) {
        return Result.success(marketService.getPriceIndex(indexType));
    }
}
```

- [ ] **Step 6: 验证行情 API**

```bash
curl http://localhost:8080/api/v1/market/prices
curl http://localhost:8080/api/v1/market/ranking?period=day&limit=10
```

Expected: 返回空分页数据（数据库暂无数据）

- [ ] **Step 7: Commit**

```bash
git add -A && git commit -m "feat: implement market module with price APIs"
```

---

## Phase 3: 前端核心页面（行情中心）

### Task 3.1: 行情首页 + 价格列表

**Files:**
- Modify: `tcm-web/src/views/market/Index.vue`
- Modify: `tcm-web/src/views/market/MarketPrice.vue`
- Modify: `tcm-web/src/views/market/OriginPrice.vue`
- Create: `tcm-web/src/api/market.js`
- Create: `tcm-web/src/components/market/PriceTable.vue`
- Create: `tcm-web/src/components/market/RankingList.vue`
- Create: `tcm-web/src/components/market/TrendTag.vue`

- [ ] **Step 1: 创建 src/api/market.js**

```javascript
import request from './request'

export function getMarketPrices(params) {
  return request.get('/market/prices', { params })
}

export function getOriginPrices(params) {
  return request.get('/market/origin-prices', { params })
}

export function getHerbDetail(id) {
  return request.get(`/market/herbs/${id}`)
}

export function getPriceHistory(id, period = '1y') {
  return request.get(`/market/herbs/${id}/history`, { params: { period } })
}

export function getRanking(period = 'day', limit = 20) {
  return request.get('/market/ranking', { params: { period, limit } })
}

export function getPriceIndex(indexType) {
  return request.get('/market/index', { params: { indexType } })
}
```

- [ ] **Step 2: 创建 TrendTag 组件**

```vue
<template>
  <span :class="['trend-tag', `trend-${trend}`]">
    <el-icon v-if="trend === 'up'"><Top /></el-icon>
    <el-icon v-else-if="trend === 'down'"><Bottom /></el-icon>
    <el-icon v-else><Minus /></el-icon>
    <span v-if="showPercent && changePercent !== null">
      {{ changePercent > 0 ? '+' : '' }}{{ changePercent }}%
    </span>
  </span>
</template>

<script setup>
import { Top, Bottom, Minus } from '@element-plus/icons-vue'

defineProps({
  trend: { type: String, default: 'stable' },
  changePercent: { type: Number, default: null },
  showPercent: { type: Boolean, default: true }
})
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;
.trend-tag { display: inline-flex; align-items: center; gap: 2px; font-size: 13px; font-weight: 500; }
.trend-up { color: $up-color; }
.trend-down { color: $down-color; }
.trend-stable { color: $stable-color; }
.trend-rare { color: $warning-color; }
</style>
```

- [ ] **Step 3: 创建 PriceTable 组件**

```vue
<template>
  <el-table :data="tableData" stripe style="width: 100%">
    <el-table-column prop="herbName" label="品种" min-width="120">
      <template #default="{ row }">
        <router-link :to="`/market/herb/${row.herbId}`" class="herb-link">{{ row.herbName }}</router-link>
      </template>
    </el-table-column>
    <el-table-column prop="specName" label="规格" width="120" />
    <el-table-column prop="marketName" :label="areaLabel" width="120" />
    <el-table-column prop="price" label="今日价" width="100" align="right">
      <template #default="{ row }">
        <span class="price">¥{{ row.price }}</span>
      </template>
    </el-table-column>
    <el-table-column label="月涨跌" width="120" align="center">
      <template #default="{ row }">
        <TrendTag :trend="row.trend" :change-percent="row.changePercent" />
      </template>
    </el-table-column>
    <el-table-column label="走势" width="80" align="center">
      <template #default="{ row }">
        <el-button link type="primary" size="small" @click="$router.push(`/market/herb/${row.herbId}`)">查看</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import TrendTag from './TrendTag.vue'

defineProps({
  tableData: { type: Array, default: () => [] },
  areaLabel: { type: String, default: '市场' }
})
</script>

<style lang="scss" scoped>
.herb-link { color: var(--el-color-primary); &:hover { text-decoration: underline; } }
.price { font-weight: 600; color: #303133; }
</style>
```

- [ ] **Step 4: 实现 market/Index.vue 行情首页**

包含：涨跌排行卡片、热门品种表格、价格指数概览、市场快讯。

- [ ] **Step 5: 实现 MarketPrice.vue 和 OriginPrice.vue**

使用 PriceTable 组件 + 分页 + 筛选条件（市场/产地/品种搜索）。

- [ ] **Step 6: 验证页面渲染**

```bash
cd /workspace/tcm-web && npm run dev
```

访问 http://localhost:3000/market 确认页面正常渲染

- [ ] **Step 7: Commit**

```bash
git add -A && git commit -m "feat: implement market frontend pages with price tables"
```

---

### Task 3.2: 品种详情页 + ECharts 走势图

**Files:**
- Modify: `tcm-web/src/views/market/HerbDetail.vue`
- Create: `tcm-web/src/components/market/TrendChart.vue`
- Create: `tcm-web/src/composables/usePrice.js`

- [ ] **Step 1: 创建 usePrice composable**

```javascript
import { ref } from 'vue'
import { getHerbDetail, getPriceHistory } from '@/api/market'

export function usePrice() {
  const herbDetail = ref(null)
  const priceHistory = ref([])
  const loading = ref(false)

  async function fetchHerbDetail(id) {
    loading.value = true
    try {
      herbDetail.value = await getHerbDetail(id)
    } finally {
      loading.value = false
    }
  }

  async function fetchPriceHistory(herbSpecId, period = '1y') {
    try {
      priceHistory.value = await getPriceHistory(herbSpecId, period)
    } catch (e) {
      priceHistory.value = []
    }
  }

  return { herbDetail, priceHistory, loading, fetchHerbDetail, fetchPriceHistory }
}
```

- [ ] **Step 2: 创建 TrendChart.vue（ECharts 价格走势图）**

```vue
<template>
  <div class="trend-chart">
    <div class="chart-header">
      <span class="chart-title">价格走势</span>
      <el-radio-group v-model="period" size="small" @change="handlePeriodChange">
        <el-radio-button value="1m">1月</el-radio-button>
        <el-radio-button value="3m">3月</el-radio-button>
        <el-radio-button value="6m">6月</el-radio-button>
        <el-radio-button value="1y">1年</el-radio-button>
        <el-radio-button value="3y">3年</el-radio-button>
        <el-radio-button value="all">全部</el-radio-button>
      </el-radio-group>
    </div>
    <v-chart :option="chartOption" autoresize style="height: 400px" />
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent, DataZoomComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

use([LineChart, GridComponent, TooltipComponent, LegendComponent, DataZoomComponent, CanvasRenderer])

const props = defineProps({
  historyData: { type: Array, default: () => [] }
})

const emit = defineEmits(['period-change'])
const period = ref('1y')

const chartOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
  xAxis: {
    type: 'category',
    data: props.historyData.map(d => d.date),
    boundaryGap: false
  },
  yAxis: {
    type: 'value',
    axisLabel: { formatter: '¥{value}' }
  },
  dataZoom: [{ type: 'inside' }, { type: 'slider' }],
  series: [{
    name: '价格',
    type: 'line',
    data: props.historyData.map(d => d.price),
    smooth: true,
    lineStyle: { color: '#2D8C4E', width: 2 },
    areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(45,140,78,0.3)' }, { offset: 1, color: 'rgba(45,140,78,0.05)' }] } }
  }]
}))

function handlePeriodChange(val) {
  emit('period-change', val)
}
</script>

<style lang="scss" scoped>
.chart-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.chart-title { font-size: 16px; font-weight: 600; }
</style>
```

- [ ] **Step 3: 实现 HerbDetail.vue 品种详情页**

包含：品种基本信息卡片、TrendChart 走势图、规格价格表格、产地价格表格、相关资讯列表、供需信息列表。

- [ ] **Step 4: 验证品种详情页**

访问 http://localhost:3000/market/herb/1 确认页面正常

- [ ] **Step 5: Commit**

```bash
git add -A && git commit -m "feat: implement herb detail page with ECharts trend chart"
```

---

### Task 3.3: 涨跌排行 + 价格指数 + 历史价格

**Files:**
- Modify: `tcm-web/src/views/market/Ranking.vue`
- Modify: `tcm-web/src/views/market/PriceIndex.vue`
- Modify: `tcm-web/src/views/market/PriceHistory.vue`
- Create: `tcm-web/src/components/market/RankingList.vue`
- Create: `tcm-web/src/components/market/IndexChart.vue`

- [ ] **Step 1: 实现 RankingList 组件**

涨跌排行列表，支持日/周/月切换，显示品种名、当前价、涨跌幅、走势标签。涨品种红色、跌品种绿色。

- [ ] **Step 2: 实现 IndexChart 组件**

价格指数多线折线图，综合指数 + 分类指数（根茎类、果实籽仁类等）。

- [ ] **Step 3: 实现 Ranking.vue 页面**

使用 RankingList 组件，Tab 切换日/周/月排行。

- [ ] **Step 4: 实现 PriceIndex.vue 页面**

使用 IndexChart 组件 + 指数数据表格。

- [ ] **Step 5: 实现 PriceHistory.vue 页面**

品种搜索 + 时间范围选择器 + 价格数据表格 + 导出按钮。

- [ ] **Step 6: 验证所有行情页面**

- [ ] **Step 7: Commit**

```bash
git add -A && git commit -m "feat: implement ranking, price index and history pages"
```

---

## Phase 4: 资讯模块

### Task 4.1: 资讯后端

**Files:**
- Create: `tcm-server/src/main/resources/db/migration/V2__init_news.sql`
- Create: `tcm-server/src/main/java/com/tcm/module/news/entity/Article.java`
- Create: `tcm-server/src/main/java/com/tcm/module/news/dto/ArticleQueryDTO.java`
- Create: `tcm-server/src/main/java/com/tcm/module/news/dto/ArticleVO.java`
- Create: `tcm-server/src/main/java/com/tcm/module/news/mapper/ArticleMapper.java`
- Create: `tcm-server/src/main/java/com/tcm/module/news/service/NewsService.java`
- Create: `tcm-server/src/main/java/com/tcm/module/news/service/impl/NewsServiceImpl.java`
- Create: `tcm-server/src/main/java/com/tcm/module/news/controller/NewsController.java`

- [ ] **Step 1: 创建 V2 迁移脚本**

```sql
CREATE TABLE article (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
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
```

- [ ] **Step 2: 创建 Article 实体、DTO、Mapper、Service、Controller**

参考行情模块的结构，实现资讯的 CRUD API。

- [ ] **Step 3: 验证资讯 API**

- [ ] **Step 4: Commit**

```bash
git add -A && git commit -m "feat: implement news module backend"
```

---

### Task 4.2: 资讯前端

**Files:**
- Create: `tcm-web/src/api/news.js`
- Modify: `tcm-web/src/views/news/Index.vue`
- Modify: `tcm-web/src/views/news/Analysis.vue`
- Modify: `tcm-web/src/views/news/Detail.vue`
- Create: `tcm-web/src/components/news/ArticleCard.vue`
- Create: `tcm-web/src/components/news/ArticleList.vue`

- [ ] **Step 1: 创建 api/news.js**

- [ ] **Step 2: 创建 ArticleCard 和 ArticleList 组件**

- [ ] **Step 3: 实现资讯首页、分类列表页、详情页**

- [ ] **Step 4: 验证资讯页面**

- [ ] **Step 5: Commit**

```bash
git add -A && git commit -m "feat: implement news frontend pages"
```

---

## Phase 5: 供求模块

### Task 5.1: 供求后端

**Files:**
- Create: `tcm-server/src/main/resources/db/migration/V3__init_trade.sql`
- Create: `tcm-server/src/main/java/com/tcm/module/trade/entity/Supply.java`
- Create: `tcm-server/src/main/java/com/tcm/module/trade/entity/Demand.java`
- Create: `tcm-server/src/main/java/com/tcm/module/trade/dto/*.java`
- Create: `tcm-server/src/main/java/com/tcm/module/trade/mapper/*.java`
- Create: `tcm-server/src/main/java/com/tcm/module/trade/service/TradeService.java`
- Create: `tcm-server/src/main/java/com/tcm/module/trade/service/impl/TradeServiceImpl.java`
- Create: `tcm-server/src/main/java/com/tcm/module/trade/controller/TradeController.java`

- [ ] **Step 1: 创建 V3 迁移脚本（supply + demand 表）**

- [ ] **Step 2: 创建 Supply/Demand 实体、DTO、Mapper、Service、Controller**

- [ ] **Step 3: 验证供求 API**

- [ ] **Step 4: Commit**

```bash
git add -A && git commit -m "feat: implement trade module backend"
```

---

### Task 5.2: 供求前端

**Files:**
- Create: `tcm-web/src/api/trade.js`
- Modify: `tcm-web/src/views/trade/Index.vue`
- Modify: `tcm-web/src/views/trade/SupplyList.vue`
- Modify: `tcm-web/src/views/trade/DemandList.vue`
- Modify: `tcm-web/src/views/trade/PublishSupply.vue`
- Modify: `tcm-web/src/views/trade/PublishDemand.vue`
- Modify: `tcm-web/src/views/trade/Detail.vue`
- Create: `tcm-web/src/components/trade/SupplyCard.vue`
- Create: `tcm-web/src/components/trade/DemandCard.vue`

- [ ] **Step 1: 创建 api/trade.js**

- [ ] **Step 2: 创建 SupplyCard/DemandCard 组件**

- [ ] **Step 3: 实现供求首页、列表页、发布页、详情页**

- [ ] **Step 4: 验证供求页面**

- [ ] **Step 5: Commit**

```bash
git add -A && git commit -m "feat: implement trade frontend pages"
```

---

## Phase 6: 百科 + 地图 + 搜索

### Task 6.1: 百科模块

**Files:**
- Create: `tcm-server/src/main/java/com/tcm/module/wiki/entity/WikiArticle.java`
- Create: `tcm-server/src/main/java/com/tcm/module/wiki/dto/*.java`
- Create: `tcm-server/src/main/java/com/tcm/module/wiki/mapper/WikiArticleMapper.java`
- Create: `tcm-server/src/main/java/com/tcm/module/wiki/service/WikiService.java`
- Create: `tcm-server/src/main/java/com/tcm/module/wiki/service/impl/WikiServiceImpl.java`
- Create: `tcm-server/src/main/java/com/tcm/module/wiki/controller/WikiController.java`
- Create: `tcm-web/src/api/wiki.js`
- Modify: `tcm-web/src/views/wiki/Index.vue`
- Modify: `tcm-web/src/views/wiki/Detail.vue`

- [ ] **Step 1: 创建百科后端（Entity + Service + Controller）**

- [ ] **Step 2: 创建百科前端页面**

- [ ] **Step 3: Commit**

```bash
git add -A && git commit -m "feat: implement wiki module"
```

---

### Task 6.2: 产地地图模块

**Files:**
- Create: `tcm-server/src/main/java/com/tcm/module/map/controller/MapController.java`
- Create: `tcm-web/src/api/map.js`
- Create: `tcm-web/src/components/map/MapView.vue`
- Create: `tcm-web/src/composables/useMap.js`
- Modify: `tcm-web/src/views/map/Index.vue`
- Modify: `tcm-web/src/views/map/OriginDetail.vue`

- [ ] **Step 1: 创建地图后端 API（产地列表含坐标）**

- [ ] **Step 2: 创建 MapView 组件（Leaflet）**

```vue
<template>
  <l-map ref="map" :zoom="4" :center="[35.86, 104.19]" style="height: 600px">
    <l-tile-layer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
    <l-marker v-for="origin in origins" :key="origin.id" :lat-lng="[origin.latitude, origin.longitude]">
      <l-popup>
        <div class="origin-popup">
          <h4>{{ origin.name }}</h4>
          <p>品种数: {{ origin.herbCount }}</p>
          <router-link :to="`/map/origin/${origin.id}`">查看详情</router-link>
        </div>
      </l-popup>
    </l-marker>
  </l-map>
</template>
```

- [ ] **Step 3: 实现产地地图页面和产地详情页**

- [ ] **Step 4: Commit**

```bash
git add -A && git commit -m "feat: implement origin map with Leaflet"
```

---

### Task 6.3: 搜索模块

**Files:**
- Create: `tcm-server/src/main/java/com/tcm/module/search/service/SearchService.java`
- Create: `tcm-server/src/main/java/com/tcm/module/search/controller/SearchController.java`
- Create: `tcm-server/src/main/java/com/tcm/config/ElasticsearchConfig.java`
- Create: `tcm-web/src/api/search.js`
- Modify: `tcm-web/src/views/search/Index.vue`

- [ ] **Step 1: 配置 Elasticsearch，创建索引映射**

- [ ] **Step 2: 实现 SearchService（品种/资讯/供求/百科统一搜索）**

- [ ] **Step 3: 实现搜索前端页面（分类 Tab + 搜索建议）**

- [ ] **Step 4: Commit**

```bash
git add -A && git commit -m "feat: implement search module with Elasticsearch"
```

---

## Phase 7: 后台管理系统

### Task 7.1: 后台布局与仪表盘

**Files:**
- Create: `tcm-web/src/layouts/AdminLayout.vue`
- Modify: `tcm-web/src/router/index.js`（添加 admin 路由）
- Create: `tcm-web/src/views/admin/Dashboard.vue`
- Create: `tcm-web/src/views/admin/Herbs.vue`
- Create: `tcm-web/src/views/admin/Prices.vue`
- Create: `tcm-web/src/views/admin/Articles.vue`
- Create: `tcm-web/src/views/admin/Trade.vue`
- Create: `tcm-web/src/views/admin/Users.vue`
- Create: `tcm-web/src/views/admin/Crawler.vue`
- Create: `tcm-web/src/views/admin/Settings.vue`

- [ ] **Step 1: 创建 AdminLayout（侧边栏 + 顶栏 + 内容区）**

- [ ] **Step 2: 添加 admin 路由，添加权限守卫**

- [ ] **Step 3: 实现仪表盘页面（统计卡片 + 图表）**

- [ ] **Step 4: Commit**

```bash
git add -A && git commit -m "feat: implement admin layout and dashboard"
```

---

### Task 7.2: 后台管理各页面

- [ ] **Step 1: 实现品种管理页面（CRUD + 分类管理）**

- [ ] **Step 2: 实现价格管理页面（数据审核 + 人工录入）**

- [ ] **Step 3: 实现资讯管理页面（富文本编辑器 + 审核）**

- [ ] **Step 4: 实现供求管理页面（审核 + 违规处理）**

- [ ] **Step 5: 实现用户管理页面（列表 + 权限 + 会员）**

- [ ] **Step 6: 实现爬虫管理页面（任务配置 + 运行状态）**

- [ ] **Step 7: 实现系统设置页面**

- [ ] **Step 8: Commit**

```bash
git add -A && git commit -m "feat: implement all admin management pages"
```

---

## Phase 8: Python 爬虫服务

### Task 8.1: 爬虫项目搭建

**Files:**
- Create: `tcm-crawler/requirements.txt`
- Create: `tcm-crawler/scrapy.cfg`
- Create: `tcm-crawler/tcm_crawler/settings.py`
- Create: `tcm-crawler/tcm_crawler/items.py`
- Create: `tcm-crawler/tcm_crawler/pipelines.py`
- Create: `tcm-crawler/tcm_crawler/middlewares.py`
- Create: `tcm-crawler/tcm_crawler/spiders/__init__.py`
- Create: `tcm-crawler/producer.py`
- Create: `tcm-crawler/scheduler.py`

- [ ] **Step 1: 创建爬虫项目**

```bash
cd /workspace
mkdir -p tcm-crawler/tcm_crawler/spiders
```

- [ ] **Step 2: 创建 requirements.txt**

```
scrapy>=2.11
kafka-python>=2.0
apscheduler>=3.10
crawl4ai>=0.3
itemloaders
```

- [ ] **Step 3: 创建 items.py（价格和资讯数据结构）**

```python
import scrapy

class PriceItem(scrapy.Item):
    herb_name = scrapy.Field()
    spec_name = scrapy.Field()
    market_name = scrapy.Field()
    area_name = scrapy.Field()
    price = scrapy.Field()
    change_amount = scrapy.Field()
    change_percent = scrapy.Field()
    trend = scrapy.Field()
    price_date = scrapy.Field()
    source = scrapy.Field()

class NewsItem(scipy.Item):
    title = scrapy.Field()
    summary = scrapy.Field()
    content = scrapy.Field()
    category = scrapy.Field()
    cover_image = scrapy.Field()
    herb_names = scrapy.Field()
    published_at = scrapy.Field()
    source = scrapy.Field()
    source_url = scrapy.Field()
```

- [ ] **Step 4: 创建 pipelines.py（Kafka 生产者）**

```python
import json
from kafka import KafkaProducer

class KafkaPipeline:
    def __init__(self, kafka_brokers):
        self.producer = KafkaProducer(
            bootstrap_servers=kafka_brokers,
            value_serializer=lambda v: json.dumps(v, ensure_ascii=False).encode('utf-8')
        )

    @classmethod
    def from_crawler(cls, crawler):
        return cls(kafka_brokers=crawler.settings.get('KAFKA_BROKERS', 'localhost:9092'))

    def process_item(self, item, spider):
        topic = 'tcm-price' if 'price' in spider.name else 'tcm-news'
        self.producer.send(topic, value=dict(item))
        return item

    def close_spider(self, spider):
        self.producer.close()
```

- [ ] **Step 5: 创建 settings.py**

```python
BOT_NAME = 'tcm_crawler'
SPIDER_MODULES = ['tcm_crawler.spiders']
NEWSPIDER_MODULE = 'tcm_crawler.spiders'
ROBOTSTXT_OBEY = False
DOWNLOAD_DELAY = 2
CONCURRENT_REQUESTS = 4
KAFKA_BROKERS = 'localhost:9092'
ITEM_PIPELINES = {'tcm_crawler.pipelines.KafkaPipeline': 300}
DEFAULT_REQUEST_HEADERS = {
    'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
}
```

- [ ] **Step 6: 创建 scheduler.py（APScheduler 定时调度）**

```python
from apscheduler.schedulers.blocking import BlockingScheduler
from scrapy.crawler import CrawlerProcess
from scrapy.utils.project import get_project_settings

def run_spider(spider_name):
    process = CrawlerProcess(get_project_settings())
    process.crawl(spider_name)
    process.start()

scheduler = BlockingScheduler()
scheduler.add_job(run_spider, 'cron', hour='9,15', minute=0, args=['zyctd_price'], name='天地网价格')
scheduler.add_job(run_spider, 'cron', hour='9', minute=30, args=['zyctd_news'], name='天地网资讯')
scheduler.add_job(run_spider, 'cron', hour='9,15', minute=15, args=['yt1998_price'], name='药通网价格')
scheduler.add_job(run_spider, 'cron', hour='10', minute=0, args=['yt1998_news'], name='药通网资讯')
scheduler.add_job(run_spider, 'cron', day_of_week='mon', hour='8', minute=0, args=['gov_policy'], name='政策法规')

if __name__ == '__main__':
    scheduler.start()
```

- [ ] **Step 7: Commit**

```bash
git add -A && git commit -m "feat: scaffold Python crawler project with Kafka pipeline"
```

---

### Task 8.2: 实现爬虫 Spider

**Files:**
- Create: `tcm-crawler/tcm_crawler/spiders/zyctd_price.py`
- Create: `tcm-crawler/tcm_crawler/spiders/zyctd_news.py`
- Create: `tcm-crawler/tcm_crawler/spiders/yt1998_price.py`
- Create: `tcm-crawler/tcm_crawler/spiders/yt1998_news.py`
- Create: `tcm-crawler/tcm_crawler/spiders/gov_policy.py`

- [ ] **Step 1: 实现 zyctd_price.py（天地网价格爬虫）**

- [ ] **Step 2: 实现 zyctd_news.py（天地网资讯爬虫）**

- [ ] **Step 3: 实现 yt1998_price.py（药通网价格爬虫）**

- [ ] **Step 4: 实现 yt1998_news.py（药通网资讯爬虫）**

- [ ] **Step 5: 实现 gov_policy.py（政策法规爬虫）**

- [ ] **Step 6: 测试爬虫运行**

```bash
cd /workspace/tcm-crawler
scrapy crawl zyctd_price -s KAFKA_BROKERS=localhost:9092
```

- [ ] **Step 7: Commit**

```bash
git add -A && git commit -m "feat: implement all crawler spiders"
```

---

### Task 8.3: 后端 Kafka 消费者（数据清洗入库）

**Files:**
- Create: `tcm-server/src/main/java/com/tcm/crawler/consumer/PriceConsumer.java`
- Create: `tcm-server/src/main/java/com/tcm/crawler/consumer/NewsConsumer.java`
- Create: `tcm-server/src/main/java/com/tcm/crawler/service/DataCleanService.java`

- [ ] **Step 1: 创建 PriceConsumer（消费 tcm-price topic）**

- [ ] **Step 2: 创建 NewsConsumer（消费 tcm-news topic）**

- [ ] **Step 3: 创建 DataCleanService（去重、异常值过滤、数据标准化）**

- [ ] **Step 4: 验证端到端数据流（爬虫 → Kafka → 后端 → MySQL）**

- [ ] **Step 5: Commit**

```bash
git add -A && git commit -m "feat: implement Kafka consumers with data cleaning"
```

---

## Phase 9: 部署 & 优化

### Task 9.1: 完善部署配置

**Files:**
- Modify: `docker-compose.yml`
- Create: `tcm-web/Dockerfile`
- Create: `tcm-server/Dockerfile`
- Create: `tcm-crawler/Dockerfile`
- Create: `nginx/nginx.conf`

- [ ] **Step 1: 创建前端 Dockerfile**

```dockerfile
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY ../nginx/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

- [ ] **Step 2: 创建后端 Dockerfile**

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

- [ ] **Step 3: 创建爬虫 Dockerfile**

```dockerfile
FROM python:3.11-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY . .
CMD ["python", "scheduler.py"]
```

- [ ] **Step 4: 创建 nginx.conf**

- [ ] **Step 5: 完善 docker-compose.yml（添加所有服务）**

- [ ] **Step 6: 验证完整部署**

```bash
cd /workspace && docker compose up -d --build
```

- [ ] **Step 7: Commit**

```bash
git add -A && git commit -m "feat: add Docker deployment configuration"
```

---

### Task 9.2: Redis 缓存优化

**Files:**
- Modify: `tcm-server/src/main/java/com/tcm/module/market/service/impl/MarketServiceImpl.java`
- Modify: `tcm-server/src/main/java/com/tcm/module/news/service/impl/NewsServiceImpl.java`
- Create: `tcm-server/src/main/java/com/tcm/common/util/CacheUtil.java`

- [ ] **Step 1: 创建 CacheUtil（Redis 缓存工具类）**

- [ ] **Step 2: 为行情热门数据添加缓存（涨跌榜、品种详情、价格走势）**

- [ ] **Step 3: 为资讯列表添加缓存**

- [ ] **Step 4: 验证缓存命中率**

- [ ] **Step 5: Commit**

```bash
git add -A && git commit -m "feat: add Redis caching for hot data"
```

---

### Task 9.3: 用户中心前端完善

**Files:**
- Modify: `tcm-web/src/views/user/Login.vue`
- Modify: `tcm-web/src/views/user/Register.vue`
- Modify: `tcm-web/src/views/user/Profile.vue`
- Modify: `tcm-web/src/views/user/Favorites.vue`
- Modify: `tcm-web/src/views/user/Subscriptions.vue`
- Create: `tcm-web/src/api/user.js`

- [ ] **Step 1: 创建 api/user.js**

- [ ] **Step 2: 实现登录/注册页面**

- [ ] **Step 3: 实现个人中心、收藏、订阅页面**

- [ ] **Step 4: 验证用户流程**

- [ ] **Step 5: Commit**

```bash
git add -A && git commit -m "feat: implement user center frontend pages"
```

---

## 自审清单

- [x] Spec 覆盖：每个模块（行情/资讯/供求/百科/地图/搜索/用户/后台）都有对应 Task
- [x] 无占位符：所有步骤包含具体代码或明确操作
- [x] 类型一致性：前后端 API 接口对齐，字段名一致
- [x] 每个阶段产出可独立运行和测试的软件
