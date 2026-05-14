<template>
  <view class="page">
    <scroll-view scroll-x class="category-tabs">
      <view
        v-for="(tab, idx) in categories"
        :key="idx"
        :class="['category-tab', { active: currentCategory === tab }]"
        @tap="switchCategory(tab)"
      >
        {{ tab }}
      </view>
    </scroll-view>

    <scroll-view
      scroll-y
      class="list-scroll"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="onLoadMore"
    >
      <view class="news-list">
        <view
          v-for="item in newsList"
          :key="item.id"
          class="news-card"
          @tap="goDetail(item.id)"
        >
          <text class="news-title">{{ item.title }}</text>
          <view class="news-meta">
            <text class="news-category">{{ item.category }}</text>
            <text class="news-date">{{ item.date }}</text>
            <text class="news-views">{{ item.views }}阅读</text>
          </view>
        </view>
      </view>

      <view class="load-tip">
        <text v-if="loading">加载中...</text>
        <text v-else-if="noMore">没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'

const categories = ref(['全部', '市场分析', '政策法规', '产地动态', '品种解析', '行业数据'])
const currentCategory = ref('全部')
const newsList = ref<any[]>([])
const page = ref(1)
const loading = ref(false)
const refreshing = ref(false)
const noMore = ref(false)

function loadList(reset = false) {
  if (reset) {
    page.value = 1
    noMore.value = false
  }
  if (loading.value || noMore.value) return

  loading.value = true

  const mockData = [
    { id: 1, title: '2026年黄芪产新在即，市场行情分析', category: '市场分析', date: '2026-05-14', views: 1256 },
    { id: 2, title: '当归价格持续走高，后市如何？', category: '品种解析', date: '2026-05-13', views: 983 },
    { id: 3, title: '中药材进出口数据月报：4月出口额同比增长12%', category: '行业数据', date: '2026-05-12', views: 2341 },
    { id: 4, title: '国家药监局发布新版中药材GAP指导原则', category: '政策法规', date: '2026-05-11', views: 3567 },
    { id: 5, title: '甘肃岷县当归产区走访纪实', category: '产地动态', date: '2026-05-10', views: 876 },
    { id: 6, title: '白术价格暴涨背后：供需失衡还是资本炒作？', category: '市场分析', date: '2026-05-09', views: 4123 },
    { id: 7, title: '川芎产新临近，行情何去何从', category: '品种解析', date: '2026-05-08', views: 654 },
    { id: 8, title: '安徽亳州药市一周行情综述', category: '市场分析', date: '2026-05-07', views: 1890 }
  ]

  setTimeout(() => {
    if (reset) {
      newsList.value = mockData
    } else {
      newsList.value = [...newsList.value, ...mockData.map((item, i) => ({
        ...item,
        id: newsList.value.length + i + 1
      }))]
    }
    if (mockData.length < 20) {
      noMore.value = true
    }
    loading.value = false
    refreshing.value = false
  }, 500)
}

function switchCategory(tab: string) {
  currentCategory.value = tab
  loadList(true)
}

function onRefresh() {
  refreshing.value = true
  loadList(true)
}

function onLoadMore() {
  page.value++
  loadList()
}

function goDetail(id: number) {
  uni.navigateTo({ url: `/pages/news/detail?id=${id}` })
}

onShow(() => {
  loadList(true)
})
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.category-tabs {
  white-space: nowrap;
  padding: 20rpx 30rpx;
  background: #fff;
  border-bottom: 1rpx solid #eee;
}

.category-tab {
  display: inline-block;
  padding: 10rpx 28rpx;
  font-size: 26rpx;
  color: #666;
  border-radius: 30rpx;
  margin-right: 16rpx;
  background: #f5f5f5;
}

.category-tab.active {
  background: #e74c3c;
  color: #fff;
}

.list-scroll {
  flex: 1;
}

.news-list {
  padding: 20rpx 30rpx;
}

.news-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.news-title {
  font-size: 30rpx;
  color: #333;
  font-weight: 500;
  line-height: 1.6;
  display: block;
}

.news-meta {
  display: flex;
  align-items: center;
  margin-top: 16rpx;
}

.news-category {
  font-size: 22rpx;
  color: #e74c3c;
  background: #fff0ed;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
  margin-right: 16rpx;
}

.news-date {
  font-size: 24rpx;
  color: #999;
  margin-right: 20rpx;
}

.news-views {
  font-size: 24rpx;
  color: #999;
}

.load-tip {
  text-align: center;
  padding: 30rpx 0;
  color: #999;
  font-size: 26rpx;
}
</style>
