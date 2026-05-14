<template>
  <div class="home-page">
    <section class="hero-section">
      <div class="hero-inner container">
        <h1 class="hero-title">中药材行情网</h1>
        <p class="hero-subtitle">专注中药材行业数据服务，实时行情一手掌握</p>
        <div class="hero-search">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索中药材品种，如：黄芪、当归、三七..."
            size="large"
            clearable
            :prefix-icon="Search"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
            </template>
          </el-input>
        </div>
        <div class="hot-tags">
          <span class="hot-label">热门：</span>
          <el-tag
            v-for="tag in hotTags"
            :key="tag"
            effect="plain"
            class="hot-tag"
            @click="handleTagClick(tag)"
          >
            {{ tag }}
          </el-tag>
        </div>
      </div>
    </section>

    <section class="container quick-entry">
      <el-row :gutter="16">
        <el-col :xs="12" :sm="8" :md="4" v-for="entry in quickEntries" :key="entry.title">
          <router-link :to="entry.path" class="entry-card card-box">
            <el-icon :size="32" :color="entry.color"><component :is="entry.icon" /></el-icon>
            <span class="entry-title">{{ entry.title }}</span>
            <span class="entry-desc">{{ entry.desc }}</span>
          </router-link>
        </el-col>
      </el-row>
    </section>

    <section class="container market-summary">
      <div class="card-box">
        <div class="section-header">
          <h2>最新行情摘要</h2>
          <router-link to="/market" class="more-link">更多 →</router-link>
        </div>
        <PriceTable :table-data="summaryData" area-label="市场" :loading="summaryLoading" />
      </div>
    </section>

    <section class="container news-section">
      <div class="card-box">
        <div class="section-header">
          <h2>最新资讯</h2>
          <router-link to="/news" class="more-link">更多 →</router-link>
        </div>
        <el-row :gutter="20">
          <el-col :xs="24" :md="12" v-for="item in newsList" :key="item.id">
            <router-link :to="`/news/detail/${item.id}`" class="news-item">
              <span class="news-tag" :class="item.category === '行情分析' ? 'tag-analysis' : 'tag-dynamic'">
                {{ item.category }}
              </span>
              <span class="news-title">{{ item.title }}</span>
              <span class="news-date">{{ item.date }}</span>
            </router-link>
          </el-col>
        </el-row>
        <div v-if="!newsList.length" class="empty-tip">暂无资讯</div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, TrendCharts, Document, Connection, Reading, Location } from '@element-plus/icons-vue'
import { getMarketPrices } from '@/api/market'
import PriceTable from '@/components/market/PriceTable.vue'

const router = useRouter()
const searchKeyword = ref('')
const summaryLoading = ref(false)
const summaryData = ref([])
const newsList = ref([])

const hotTags = ['黄芪', '当归', '三七', '党参', '甘草', '川芎', '白术', '茯苓']

const quickEntries = [
  { title: '行情中心', desc: '实时价格', path: '/market', icon: TrendCharts, color: '#2D8C4E' },
  { title: '资讯中心', desc: '行业动态', path: '/news', icon: Document, color: '#409EFF' },
  { title: '供求中心', desc: '供需对接', path: '/trade', icon: Connection, color: '#E6A23C' },
  { title: '品种百科', desc: '品种知识', path: '/wiki', icon: Reading, color: '#F56C6C' },
  { title: '产地地图', desc: '产地分布', path: '/map', icon: Location, color: '#9B59B6' }
]

function handleSearch() {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/search', query: { q: searchKeyword.value.trim() } })
  }
}

function handleTagClick(tag) {
  searchKeyword.value = tag
  handleSearch()
}

async function fetchSummary() {
  summaryLoading.value = true
  try {
    const data = await getMarketPrices({ page: 1, size: 8 })
    summaryData.value = data?.list || data || []
  } catch {
    summaryData.value = []
  } finally {
    summaryLoading.value = false
  }
}

function fetchNews() {
  newsList.value = [
    { id: 1, title: '黄芪市场行情分析：需求稳定，价格小幅上涨', category: '行情分析', date: '2026-05-14' },
    { id: 2, title: '当归产地调研报告：甘肃产区产量预计增长', category: '产地报道', date: '2026-05-13' },
    { id: 3, title: '三七价格持续走低，后市如何？', category: '行情分析', date: '2026-05-13' },
    { id: 4, title: '2026年中药材集采目录公布，多个品种入选', category: '集采信息', date: '2026-05-12' },
    { id: 5, title: '党参产新临近，市场关注度提升', category: '市场动态', date: '2026-05-12' },
    { id: 6, title: '甘草出口数据亮眼，国际需求持续增长', category: '市场动态', date: '2026-05-11' }
  ]
}

onMounted(() => {
  fetchSummary()
  fetchNews()
})
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.hero-section {
  background: linear-gradient(135deg, $primary-dark 0%, $primary-color 100%);
  padding: 60px 0 50px;
  color: #fff;
}

.hero-inner {
  text-align: center;
}

.hero-title {
  font-size: 40px;
  font-weight: 700;
  margin-bottom: 12px;
  letter-spacing: 4px;
}

.hero-subtitle {
  font-size: 16px;
  opacity: 0.85;
  margin-bottom: 32px;
}

.hero-search {
  max-width: 600px;
  margin: 0 auto 20px;
  :deep(.el-input__wrapper) {
    border-radius: 8px;
  }
  :deep(.el-input-group__append) {
    background: $primary-color;
    border-color: $primary-color;
    color: #fff;
    .el-button {
      color: #fff;
    }
  }
}

.hot-tags {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 8px;
}

.hot-label {
  font-size: 13px;
  opacity: 0.8;
}

.hot-tag {
  cursor: pointer;
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.3);
  color: #fff;
  &:hover {
    background: rgba(255, 255, 255, 0.25);
  }
}

.quick-entry {
  margin-top: -24px;
  position: relative;
  z-index: 10;
  margin-bottom: 24px;
}

.entry-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 12px;
  text-decoration: none;
  transition: transform 0.2s, box-shadow 0.2s;
  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-medium;
  }
}

.entry-title {
  margin-top: 12px;
  font-size: 15px;
  font-weight: 600;
  color: $text-color;
}

.entry-desc {
  margin-top: 4px;
  font-size: 12px;
  color: $text-secondary;
}

.market-summary {
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  h2 {
    font-size: 18px;
    font-weight: 600;
    color: $text-color;
  }
}

.more-link {
  font-size: 13px;
  color: $primary-color;
  text-decoration: none;
  &:hover {
    text-decoration: underline;
  }
}

.news-section {
  margin-bottom: 24px;
}

.news-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
  text-decoration: none;
  &:last-child {
    border-bottom: none;
  }
  &:hover .news-title {
    color: $primary-color;
  }
}

.news-tag {
  flex-shrink: 0;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 3px;
  margin-right: 12px;
  color: #fff;
}

.tag-analysis {
  background: $up-color;
}

.tag-dynamic {
  background: $primary-color;
}

.news-title {
  flex: 1;
  font-size: 14px;
  color: $text-color;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.2s;
}

.news-date {
  flex-shrink: 0;
  margin-left: 16px;
  font-size: 12px;
  color: $text-secondary;
}

.empty-tip {
  text-align: center;
  color: $text-secondary;
  padding: 40px 0;
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 28px;
    letter-spacing: 2px;
  }
  .hero-subtitle {
    font-size: 14px;
  }
  .hero-section {
    padding: 40px 0 36px;
  }
  .quick-entry {
    margin-top: -16px;
  }
}
</style>
