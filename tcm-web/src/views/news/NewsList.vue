<template>
  <div class="news-list">
    <el-tabs v-model="activeCategory" @tab-change="handleCategoryChange">
      <el-tab-pane label="全部" name="" />
      <el-tab-pane label="品种分析" name="品种分析" />
      <el-tab-pane label="药市动态" name="药市动态" />
      <el-tab-pane label="集采资讯" name="集采资讯" />
      <el-tab-pane label="采购招标" name="采购招标" />
    </el-tabs>

    <div v-loading="loading" class="news-cards">
      <el-empty v-if="!loading && newsList.length === 0" description="暂无资讯" />
      <el-card
        v-for="item in newsList"
        :key="item.id"
        shadow="hover"
        class="news-card"
        @click="goDetail(item.id)"
      >
        <div class="card-body">
          <el-image
            :src="item.coverUrl || defaultCover"
            fit="cover"
            class="cover-img"
          >
            <template #error>
              <div class="image-placeholder">
                <el-icon :size="24"><Picture /></el-icon>
              </div>
            </template>
          </el-image>
          <div class="card-info">
            <h3 class="title">{{ item.title }}</h3>
            <p class="summary">{{ item.summary }}</p>
            <div class="meta">
              <el-tag size="small" type="info">{{ item.category }}</el-tag>
              <span class="publish-time">{{ item.publishTime }}</span>
              <span class="read-count">
                <el-icon><View /></el-icon>
                {{ item.readCount }}
              </span>
            </div>
          </div>
        </div>
      </el-card>
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
  coverUrl: string
  category: string
  publishTime: string
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

<style scoped>
.news-list {
  padding: 20px;
  max-width: 960px;
  margin: 0 auto;
}

.news-cards {
  min-height: 300px;
}

.news-card {
  margin-bottom: 12px;
  cursor: pointer;
  transition: transform 0.2s;
}

.news-card:hover {
  transform: translateY(-2px);
}

.card-body {
  display: flex;
  gap: 16px;
}

.cover-img {
  width: 120px;
  height: 90px;
  flex-shrink: 0;
  border-radius: 4px;
  overflow: hidden;
}

.image-placeholder {
  width: 120px;
  height: 90px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
}

.card-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.summary {
  font-size: 13px;
  color: #909399;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #c0c4cc;
}

.read-count {
  display: flex;
  align-items: center;
  gap: 2px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
