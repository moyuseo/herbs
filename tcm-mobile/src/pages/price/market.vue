<template>
  <view class="page">
    <view class="search-bar">
      <view class="search-input-wrap">
        <text class="search-icon">&#x1F50D;</text>
        <input
          class="search-input"
          v-model="keyword"
          placeholder="搜索品种名称"
          confirm-type="search"
          @confirm="onSearch"
        />
      </view>
    </view>

    <scroll-view scroll-x class="market-tabs">
      <view
        v-for="(tab, idx) in marketTabs"
        :key="idx"
        :class="['market-tab', { active: currentMarket === tab }]"
        @tap="switchMarket(tab)"
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
      <view class="price-list">
        <view
          v-for="item in priceList"
          :key="item.id"
          class="price-item"
          @tap="goDetail(item.id)"
        >
          <view class="price-left">
            <text class="price-name">{{ item.name }}</text>
            <text class="price-spec">{{ item.spec }} | {{ item.market }}</text>
          </view>
          <view class="price-right">
            <text class="price-value">¥{{ item.price.toFixed(2) }}</text>
            <PriceTag :value="item.changePercent" />
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
import PriceTag from '@/components/PriceTag.vue'

const keyword = ref('')
const marketTabs = ref(['全部', '亳州', '安国', '成都', '玉林', '廉桥'])
const currentMarket = ref('全部')
const priceList = ref<any[]>([])
const page = ref(1)
const pageSize = 20
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
    { id: 1, name: '黄芪', spec: '统片', market: '亳州', price: 22.5, changePercent: 3.2 },
    { id: 2, name: '当归', spec: '草把', market: '安国', price: 58.0, changePercent: -1.5 },
    { id: 3, name: '党参', spec: '白条', market: '成都', price: 45.0, changePercent: 0 },
    { id: 4, name: '甘草', spec: '毛草', market: '亳州', price: 16.8, changePercent: 2.1 },
    { id: 5, name: '川芎', spec: '统个', market: '玉林', price: 28.5, changePercent: -0.8 },
    { id: 6, name: '白术', spec: '统个', market: '亳州', price: 120.0, changePercent: 5.3 },
    { id: 7, name: '茯苓', spec: '白块', market: '廉桥', price: 35.0, changePercent: -2.1 },
    { id: 8, name: '丹参', spec: '统条', market: '安国', price: 18.5, changePercent: 1.2 },
    { id: 9, name: '柴胡', spec: '北柴胡', market: '成都', price: 85.0, changePercent: 0.5 },
    { id: 10, name: '板蓝根', spec: '统个', market: '亳州', price: 12.0, changePercent: -3.4 }
  ]

  setTimeout(() => {
    if (reset) {
      priceList.value = mockData
    } else {
      priceList.value = [...priceList.value, ...mockData.map((item, i) => ({
        ...item,
        id: priceList.value.length + i + 1
      }))]
    }
    if (mockData.length < pageSize) {
      noMore.value = true
    }
    loading.value = false
    refreshing.value = false
  }, 500)
}

function switchMarket(tab: string) {
  currentMarket.value = tab
  loadList(true)
}

function onSearch() {
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
  uni.navigateTo({ url: `/pages/price/detail?id=${id}` })
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

.search-bar {
  padding: 20rpx 30rpx;
  background: #e74c3c;
}

.search-input-wrap {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 36rpx;
  padding: 12rpx 24rpx;
}

.search-icon {
  font-size: 28rpx;
  margin-right: 12rpx;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.market-tabs {
  white-space: nowrap;
  padding: 20rpx 30rpx;
  background: #fff;
  border-bottom: 1rpx solid #eee;
}

.market-tab {
  display: inline-block;
  padding: 10rpx 28rpx;
  font-size: 26rpx;
  color: #666;
  border-radius: 30rpx;
  margin-right: 16rpx;
  background: #f5f5f5;
}

.market-tab.active {
  background: #e74c3c;
  color: #fff;
}

.list-scroll {
  flex: 1;
}

.price-list {
  padding: 0 30rpx;
  background: #fff;
}

.price-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.price-item:last-child {
  border-bottom: none;
}

.price-left {
  display: flex;
  flex-direction: column;
}

.price-name {
  font-size: 30rpx;
  color: #333;
  font-weight: 500;
}

.price-spec {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
}

.price-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.price-value {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.load-tip {
  text-align: center;
  padding: 30rpx 0;
  color: #999;
  font-size: 26rpx;
}
</style>
