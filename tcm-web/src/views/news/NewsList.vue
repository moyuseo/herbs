<template>
  <div class="news-list">
    <div class="page-header">
      <h1 class="page-title">资讯中心</h1>
      <p class="page-subtitle">洞察中药材市场动态，把握行业前沿资讯</p>
    </div>

    <div class="category-filter">
      <button
        class="filter-pill"
        :class="{ active: activeCategory === '' }"
        @click="activeCategory = ''; handleCategoryChange()"
      >
        全部
      </button>
      <button
        class="filter-pill"
        :class="{ active: activeCategory === '品种分析' }"
        @click="activeCategory = '品种分析'; handleCategoryChange()"
      >
        品种分析
      </button>
      <button
        class="filter-pill"
        :class="{ active: activeCategory === '药市动态' }"
        @click="activeCategory = '药市动态'; handleCategoryChange()"
      >
        药市动态
      </button>
      <button
        class="filter-pill"
        :class="{ active: activeCategory === '集采资讯' }"
        @click="activeCategory = '集采资讯'; handleCategoryChange()"
      >
        集采资讯
      </button>
      <button
        class="filter-pill"
        :class="{ active: activeCategory === '采购招标' }"
        @click="activeCategory = '采购招标'; handleCategoryChange()"
      >
        采购招标
      </button>
    </div>

    <div v-loading="loading" class="news-cards">
      <el-empty v-if="!loading && newsList.length === 0" description="暂无资讯" />
      <div
        v-for="item in newsList"
        :key="item.id"
        class="news-card"
        @click="goDetail(item.id)"
      >
        <div class="card-cover">
          <el-image
            :src="item.coverImage || item.coverUrl || defaultCover"
            fit="cover"
            class="cover-img"
          >
            <template #error>
              <div class="image-placeholder">
                <el-icon :size="28"><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </div>
        <div class="card-content">
          <h3 class="card-title">{{ item.title }}</h3>
          <p class="card-summary">{{ item.summary }}</p>
          <div class="card-meta">
            <span class="meta-category">
              <el-tag size="small" effect="plain" round>{{ item.category }}</el-tag>
            </span>
            <span class="meta-date">
              <el-icon><Clock /></el-icon>
              {{ item.publishedAt || item.publishTime }}
            </span>
            <span class="meta-views">
              <el-icon><View /></el-icon>
              {{ item.viewCount ?? item.readCount ?? 0 }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchList"
        @current-change="fetchList"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getNewsList } from '@/api/news'

const router = useRouter()

const defaultCover = 'https://via.placeholder.com/120x90?text=TCM'

interface NewsItem {
  id: number | string
  title: string
  summary: string
  coverImage: string
  coverUrl: string
  category: string
  publishedAt: string
  publishTime: string
  viewCount: number
  readCount: number
}

const activeCategory = ref('')
const loading = ref(false)
const newsList = ref<NewsItem[]>([])
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0,
})

async function fetchList() {
  loading.value = true
  try {
    const res = await getNewsList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      category: activeCategory.value || undefined,
    })
    newsList.value = res.list || []
    pagination.total = res.total || 0
  } catch {
    newsList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

function handleCategoryChange() {
  pagination.page = 1
  fetchList()
}

function goDetail(id: number | string) {
  router.push(`/news/detail/${id}`)
}

onMounted(() => {
  fetchList()
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

.news-list {
  max-width: $container-max;
  margin: 0 auto;
  padding: 32px 24px 48px;
  background: $bg-color;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 28px;
}

.page-title {
  font-family: $font-display;
  font-size: 28px;
  font-weight: 700;
  color: $text-color;
  margin: 0 0 8px;
  padding-left: 16px;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 4px;
    bottom: 4px;
    width: 4px;
    background: $primary-color;
    border-radius: 2px;
  }
}

.page-subtitle {
  font-size: 14px;
  color: $text-secondary;
  margin: 0;
  padding-left: 16px;
}

.category-filter {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 24px;
}

.filter-pill {
  display: inline-flex;
  align-items: center;
  padding: 7px 20px;
  font-size: 14px;
  color: $text-secondary;
  background: $card-bg;
  border: 1px solid $border-color;
  border-radius: 24px;
  cursor: pointer;
  transition: all 0.25s ease;
  font-family: inherit;
  line-height: 1;

  &:hover {
    color: $primary-color;
    border-color: $primary-light;
    background: $primary-lighter;
  }

  &.active {
    color: #fff;
    background: $primary-color;
    border-color: $primary-color;
    box-shadow: 0 2px 8px rgba($primary-color, 0.3);
  }
}

.news-cards {
  min-height: 300px;
}

.news-card {
  display: flex;
  gap: 20px;
  background: $card-bg;
  border: 1px solid $border-light;
  border-radius: $radius-md;
  padding: 20px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: $shadow-sm;

  &:hover {
    box-shadow: $shadow-md;
    border-color: rgba($accent-color, 0.4);
    transform: translateY(-2px);

    .card-title {
      color: $primary-color;
    }

    .cover-img {
      :deep(img) {
        transform: scale(1.05);
      }
    }
  }
}

.card-cover {
  width: 200px;
  height: 140px;
  flex-shrink: 0;
  border-radius: $radius-sm;
  overflow: hidden;

  .cover-img {
    width: 100%;
    height: 100%;
    border-radius: $radius-sm;

    :deep(img) {
      transition: transform 0.4s ease;
    }
  }
}

.image-placeholder {
  width: 100%;
  height: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $primary-lighter;
  color: $primary-light;
}

.card-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.card-title {
  font-size: 17px;
  font-weight: 600;
  color: $text-color;
  margin: 0 0 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.25s ease;
}

.card-summary {
  font-size: 14px;
  color: $text-secondary;
  margin: 0 0 14px;
  line-height: 1.7;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: $text-secondary;
}

.meta-category {
  :deep(.el-tag) {
    background: $primary-lighter;
    color: $primary-color;
    border-color: transparent;
  }
}

.meta-date,
.meta-views {
  display: inline-flex;
  align-items: center;
  gap: 4px;

  .el-icon {
    font-size: 14px;
    color: $accent-color;
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid $border-light;

  :deep(.el-pagination) {
    .el-pager li.is-active {
      background: $primary-color;
      color: #fff;
    }

    button:hover,
    .el-pager li:hover {
      color: $primary-color;
    }
  }
}

@media (max-width: 768px) {
  .news-list {
    padding: 20px 16px 36px;
  }

  .page-title {
    font-size: 22px;
  }

  .news-card {
    flex-direction: column;
    gap: 14px;
    padding: 16px;
  }

  .card-cover {
    width: 100%;
    height: 180px;
  }

  .image-placeholder {
    height: 180px;
  }
}
</style>
