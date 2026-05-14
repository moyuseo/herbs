<template>
  <div class="news-detail">
    <div v-loading="loading">
      <template v-if="detail">
        <div class="detail-header">
          <h1 class="detail-title">{{ detail.title }}</h1>
          <div class="detail-meta">
            <span v-if="detail.author">
              <el-icon><User /></el-icon>
              {{ detail.author }}
            </span>
            <span v-if="detail.source">
              <el-icon><Notebook /></el-icon>
              {{ detail.source }}
            </span>
            <span>
              <el-icon><Clock /></el-icon>
              {{ detail.publishTime }}
            </span>
            <span>
              <el-icon><View /></el-icon>
              {{ detail.readCount }}
            </span>
          </div>
        </div>

        <el-divider />

        <div class="detail-content" v-html="detail.content"></div>

        <el-divider />

        <div class="nav-links">
          <el-link
            v-if="detail.prevId"
            :underline="false"
            @click="goDetail(detail.prevId)"
          >
            <el-icon><ArrowLeft /></el-icon>
            上一篇：{{ detail.prevTitle }}
          </el-link>
          <span v-else class="nav-disabled">没有更早的资讯了</span>
          <el-link
            v-if="detail.nextId"
            :underline="false"
            @click="goDetail(detail.nextId)"
          >
            下一篇：{{ detail.nextTitle }}
            <el-icon><ArrowRight /></el-icon>
          </el-link>
          <span v-else class="nav-disabled">没有更新的资讯了</span>
        </div>
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
  publishTime: string
  readCount: number
  content: string
  prevId: number | string | null
  prevTitle: string
  nextId: number | string | null
  nextTitle: string
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

<style scoped>
.news-detail {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.detail-header {
  text-align: center;
}

.detail-title {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 16px;
  line-height: 1.4;
}

.detail-meta {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  font-size: 13px;
  color: #909399;
}

.detail-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.detail-content {
  line-height: 1.8;
  font-size: 15px;
  color: #303133;
  word-break: break-word;
}

.detail-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  margin: 12px 0;
}

.nav-links {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.nav-disabled {
  color: #c0c4cc;
  font-size: 13px;
}
</style>
