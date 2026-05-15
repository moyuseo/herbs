<template>
  <div class="news-detail">
    <div v-loading="loading">
      <template v-if="detail">
        <nav class="breadcrumb">
          <router-link to="/" class="breadcrumb-link">首页</router-link>
          <span class="breadcrumb-sep">/</span>
          <router-link to="/news" class="breadcrumb-link">资讯中心</router-link>
          <span class="breadcrumb-sep">/</span>
          <span class="breadcrumb-current">正文</span>
        </nav>

        <article class="article-wrapper">
          <header class="article-header">
            <h1 class="article-title">{{ detail.title }}</h1>
            <div class="article-meta">
              <span v-if="detail.author" class="meta-item">
                <el-icon><User /></el-icon>
                {{ detail.author }}
              </span>
              <span v-if="detail.source" class="meta-item">
                <el-icon><Notebook /></el-icon>
                {{ detail.source }}
              </span>
              <span class="meta-item">
                <el-icon><Clock /></el-icon>
                {{ detail.publishedAt || detail.publishTime }}
              </span>
              <span class="meta-item">
                <el-icon><View /></el-icon>
                {{ detail.viewCount ?? detail.readCount ?? 0 }} 阅读
              </span>
            </div>
          </header>

          <div class="article-divider"></div>

          <div class="article-content" v-html="detail.content"></div>

          <div class="article-divider"></div>

          <nav class="nav-links">
            <div class="nav-item nav-prev">
              <template v-if="detail.prevNewsId || detail.prevId">
                <span class="nav-label">上一篇</span>
                <a
                  class="nav-link"
                  @click.prevent="goDetail(detail.prevNewsId || detail.prevId)"
                >
                  <el-icon><ArrowLeft /></el-icon>
                  {{ detail.prevTitle }}
                </a>
              </template>
              <span v-else class="nav-disabled">没有更早的资讯了</span>
            </div>
            <div class="nav-item nav-next">
              <template v-if="detail.nextNewsId || detail.nextId">
                <span class="nav-label">下一篇</span>
                <a
                  class="nav-link"
                  @click.prevent="goDetail(detail.nextNewsId || detail.nextId)"
                >
                  {{ detail.nextTitle }}
                  <el-icon><ArrowRight /></el-icon>
                </a>
              </template>
              <span v-else class="nav-disabled">没有更新的资讯了</span>
            </div>
          </nav>
        </article>
      </template>

      <el-empty v-if="!loading && !detail" description="资讯不存在或已删除" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNewsDetail } from '@/api/news'

const route = useRoute()
const router = useRouter()

interface NewsDetail {
  id: number | string
  title: string
  author: string
  source: string
  publishedAt: string
  publishTime: string
  viewCount: number
  readCount: number
  content: string
  prevNewsId: number | string | null
  prevTitle: string
  nextNewsId: number | string | null
  nextTitle: string
  prevId: number | string | null
  nextId: number | string | null
}

const loading = ref(false)
const detail = ref<NewsDetail | null>(null)

async function fetchDetail() {
  const id = route.params.id as string
  if (!id) return
  loading.value = true
  try {
    detail.value = await getNewsDetail(id)
  } catch {
    detail.value = null
  } finally {
    loading.value = false
  }
}

function goDetail(id: number | string) {
  router.push(`/news/detail/${id}`)
}

watch(() => route.params.id, () => {
  if (route.params.id) fetchDetail()
})

onMounted(() => {
  fetchDetail()
})
</script>

<style lang="scss" scoped>
$primary-color: #1a5632;
$primary-light: #2d7a4a;
$primary-lighter: #e8f5ee;
$accent-color: #c8953e;
$accent-lighter: #fdf6e8;
$text-color: #1a1a1a;
$text-secondary: #5a5a5a;
$bg-color: #f7f6f3;
$bg-warm: #faf9f6;
$card-bg: #ffffff;
$border-color: #e8e5df;
$border-light: #f0ede8;
$shadow-sm: 0 1px 3px rgba(0, 0, 0, 0.06);
$shadow-md: 0 4px 12px rgba(0, 0, 0, 0.08);
$radius-sm: 6px;
$radius-md: 10px;
$font-display: 'Noto Serif SC', serif;
$container-max: 1240px;

.news-detail {
  max-width: $container-max;
  margin: 0 auto;
  padding: 32px 24px 48px;
  background: $bg-color;
  min-height: 100vh;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  margin-bottom: 24px;
  padding: 12px 16px;
  background: $card-bg;
  border-radius: $radius-sm;
  box-shadow: $shadow-sm;
}

.breadcrumb-link {
  color: $text-secondary;
  text-decoration: none;
  transition: color 0.2s ease;

  &:hover {
    color: $primary-color;
  }
}

.breadcrumb-sep {
  color: $border-color;
  font-size: 12px;
}

.breadcrumb-current {
  color: $primary-color;
  font-weight: 500;
}

.article-wrapper {
  background: $card-bg;
  border-radius: $radius-md;
  padding: 40px 48px;
  box-shadow: $shadow-sm;
  border: 1px solid $border-light;
}

.article-header {
  text-align: center;
  margin-bottom: 0;
}

.article-title {
  font-family: $font-display;
  font-size: 26px;
  font-weight: 700;
  color: $text-color;
  margin: 0 0 20px;
  line-height: 1.5;
  letter-spacing: 0.5px;
}

.article-meta {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 20px;
  font-size: 13px;
  color: $text-secondary;
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 5px;

  .el-icon {
    font-size: 15px;
    color: $accent-color;
  }
}

.article-divider {
  height: 1px;
  background: linear-gradient(
    to right,
    transparent,
    $border-color 20%,
    $border-color 80%,
    transparent
  );
  margin: 28px 0;
}

.article-content {
  line-height: 2;
  font-size: 16px;
  color: $text-color;
  word-break: break-word;

  :deep(p) {
    margin: 0 0 20px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  :deep(h2),
  :deep(h3),
  :deep(h4) {
    font-family: $font-display;
    color: $text-color;
    margin: 28px 0 14px;
    line-height: 1.4;
  }

  :deep(h2) {
    font-size: 20px;
    padding-bottom: 8px;
    border-bottom: 2px solid $primary-lighter;
  }

  :deep(h3) {
    font-size: 18px;
    padding-left: 12px;
    border-left: 3px solid $accent-color;
  }

  :deep(img) {
    max-width: 100%;
    height: auto;
    border-radius: $radius-sm;
    margin: 16px 0;
    box-shadow: $shadow-sm;
  }

  :deep(blockquote) {
    margin: 16px 0;
    padding: 12px 20px;
    border-left: 3px solid $accent-color;
    background: $accent-lighter;
    border-radius: 0 $radius-sm $radius-sm 0;
    color: $text-secondary;
    font-style: italic;
  }

  :deep(a) {
    color: $primary-color;
    text-decoration: none;
    border-bottom: 1px solid rgba($primary-color, 0.3);
    transition: border-color 0.2s;

    &:hover {
      border-bottom-color: $primary-color;
    }
  }

  :deep(ul),
  :deep(ol) {
    padding-left: 24px;
    margin: 12px 0;

    li {
      margin-bottom: 6px;
      line-height: 1.8;
    }
  }

  :deep(table) {
    width: 100%;
    border-collapse: collapse;
    margin: 16px 0;
    font-size: 14px;

    th,
    td {
      padding: 10px 14px;
      border: 1px solid $border-color;
      text-align: left;
    }

    th {
      background: $primary-lighter;
      color: $primary-color;
      font-weight: 600;
    }

    tr:nth-child(even) td {
      background: $bg-warm;
    }
  }
}

.nav-links {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
}

.nav-item {
  flex: 1;
  min-width: 0;
}

.nav-next {
  text-align: right;
}

.nav-label {
  display: block;
  font-size: 12px;
  color: $accent-color;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 6px;
}

.nav-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: $primary-color;
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
  line-height: 1.5;

  .el-icon {
    font-size: 14px;
    flex-shrink: 0;
  }

  &:hover {
    color: $accent-color;
  }
}

.nav-next .nav-link {
  flex-direction: row-reverse;
}

.nav-disabled {
  font-size: 13px;
  color: #c0c4cc;
  font-style: italic;
}

@media (max-width: 768px) {
  .news-detail {
    padding: 20px 16px 36px;
  }

  .article-wrapper {
    padding: 24px 20px;
  }

  .article-title {
    font-size: 20px;
  }

  .article-meta {
    gap: 12px;
    font-size: 12px;
  }

  .nav-links {
    flex-direction: column;
    gap: 16px;
  }

  .nav-next {
    text-align: left;
  }

  .nav-next .nav-link {
    flex-direction: row;
  }
}
</style>
