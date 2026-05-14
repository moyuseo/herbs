<template>
  <view class="page">
    <view class="search-bar" @tap="goSearch">
      <view class="search-input">
        <text class="search-icon">&#x1F50D;</text>
        <text class="search-placeholder">搜索中药材品种...</text>
      </view>
    </view>

    <view class="overview-section">
      <view class="overview-card up">
        <text class="overview-num">{{ overview.upCount }}</text>
        <text class="overview-label">上涨</text>
      </view>
      <view class="overview-card down">
        <text class="overview-num">{{ overview.downCount }}</text>
        <text class="overview-label">下跌</text>
      </view>
      <view class="overview-card flat">
        <text class="overview-num">{{ overview.flatCount }}</text>
        <text class="overview-label">持平</text>
      </view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="section-title">市场价格</text>
        <text class="section-more" @tap="goMarket">更多 ›</text>
      </view>
      <scroll-view scroll-x class="market-tabs">
        <view
          v-for="(tab, idx) in marketTabs"
          :key="idx"
          :class="['market-tab', { active: currentMarket === tab }]"
          @tap="currentMarket = tab"
        >
          {{ tab }}
        </view>
      </scroll-view>
      <view class="price-list">
        <view
          v-for="item in priceList"
          :key="item.id"
          class="price-item"
          @tap="goDetail(item.id)"
        >
          <view class="price-info">
            <text class="price-name">{{ item.name }}</text>
            <text class="price-spec">{{ item.spec }}</text>
          </view>
          <view class="price-right">
            <text class="price-value">¥{{ item.price }}</text>
            <PriceTag :value="item.changePercent" />
          </view>
        </view>
        <view v-if="priceList.length === 0" class="empty-tip">暂无数据</view>
      </view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="section-title">最新资讯</text>
        <text class="section-more" @tap="goNews">更多 ›</text>
      </view>
      <view class="news-list">
        <view
          v-for="item in newsList"
          :key="item.id"
          class="news-item"
          @tap="goNewsDetail(item.id)"
        >
          <view class="news-content">
            <text class="news-title">{{ item.title }}</text>
            <text class="news-date">{{ item.date }}</text>
          </view>
        </view>
        <view v-if="newsList.length === 0" class="empty-tip">暂无资讯</view>
      </view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="section-title">供应信息</text>
        <text class="section-more" @tap="goSupply">更多 ›</text>
      </view>
      <view class="supply-list">
        <view
          v-for="item in supplyList"
          :key="item.id"
          class="supply-item"
        >
          <view class="supply-info">
            <text class="supply-name">{{ item.name }}</text>
            <text class="supply-spec">{{ item.spec }} | {{ item.quantity }}</text>
          </view>
          <text :class="['supply-type', item.type === '供应' ? 'supply' : 'demand']">{{ item.type }}</text>
        </view>
        <view v-if="supplyList.length === 0" class="empty-tip">暂无供求信息</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import PriceTag from '@/components/PriceTag.vue'

const overview = ref({ upCount: 0, downCount: 0, flatCount: 0 })
const marketTabs = ref(['全部', '亳州', '安国', '成都', '玉林', '廉桥'])
const currentMarket = ref('全部')
const priceList = ref<any[]>([])
const newsList = ref<any[]>([])
const supplyList = ref<any[]>([])

function loadOverview() {
  overview.value = { upCount: 128, downCount: 56, flatCount: 34 }
}

function loadPriceList() {
  priceList.value = [
    { id: 1, name: '黄芪', spec: '统片', price: 22.5, changePercent: 3.2 },
    { id: 2, name: '当归', spec: '草把', price: 58.0, changePercent: -1.5 },
    { id: 3, name: '党参', spec: '白条', price: 45.0, changePercent: 0 },
    { id: 4, name: '甘草', spec: '毛草', price: 16.8, changePercent: 2.1 },
    { id: 5, name: '川芎', spec: '统个', price: 28.5, changePercent: -0.8 }
  ]
}

function loadNewsList() {
  newsList.value = [
    { id: 1, title: '2026年黄芪产新在即，市场行情分析', date: '2026-05-14' },
    { id: 2, title: '当归价格持续走高，后市如何？', date: '2026-05-13' },
    { id: 3, title: '中药材进出口数据月报：4月出口额同比增长12%', date: '2026-05-12' }
  ]
}

function loadSupplyList() {
  supplyList.value = [
    { id: 1, name: '黄芪', spec: '统片', quantity: '500kg', type: '供应' },
    { id: 2, name: '当归', spec: '草把', quantity: '200kg', type: '求购' },
    { id: 3, name: '白术', spec: '统个', quantity: '300kg', type: '供应' }
  ]
}

watch(currentMarket, () => {
  loadPriceList()
})

onShow(() => {
  loadOverview()
  loadPriceList()
  loadNewsList()
  loadSupplyList()
})

function goSearch() {
  uni.navigateTo({ url: '/pages/price/market?search=true' })
}

function goMarket() {
  uni.switchTab({ url: '/pages/price/market' })
}

function goDetail(id: number) {
  uni.navigateTo({ url: `/pages/price/detail?id=${id}` })
}

function goNews() {
  uni.switchTab({ url: '/pages/news/list' })
}

function goNewsDetail(id: number) {
  uni.navigateTo({ url: `/pages/news/detail?id=${id}` })
}

function goSupply() {
  uni.navigateTo({ url: '/pages/supply/list' })
}
</script>

<style scoped>
.page {
  padding-bottom: 20rpx;
}

.search-bar {
  padding: 20rpx 30rpx;
  background: #e74c3c;
}

.search-input {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 36rpx;
  padding: 16rpx 24rpx;
}

.search-icon {
  font-size: 28rpx;
  margin-right: 12rpx;
}

.search-placeholder {
  color: #999;
  font-size: 28rpx;
}

.overview-section {
  display: flex;
  padding: 30rpx;
  background: #fff;
  margin-bottom: 20rpx;
}

.overview-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.overview-num {
  font-size: 48rpx;
  font-weight: bold;
}

.overview-card.up .overview-num {
  color: #e74c3c;
}

.overview-card.down .overview-num {
  color: #27ae60;
}

.overview-card.flat .overview-num {
  color: #999;
}

.overview-label {
  font-size: 24rpx;
  color: #666;
  margin-top: 8rpx;
}

.section {
  background: #fff;
  margin-bottom: 20rpx;
  padding: 24rpx 30rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.section-more {
  font-size: 26rpx;
  color: #999;
}

.market-tabs {
  white-space: nowrap;
  margin-bottom: 20rpx;
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

.price-list {
  margin-top: 10rpx;
}

.price-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.price-item:last-child {
  border-bottom: none;
}

.price-info {
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
  margin-top: 6rpx;
}

.price-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.price-value {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.news-list {
  margin-top: 10rpx;
}

.news-item {
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.news-item:last-child {
  border-bottom: none;
}

.news-content {
  display: flex;
  flex-direction: column;
}

.news-title {
  font-size: 28rpx;
  color: #333;
  line-height: 1.5;
}

.news-date {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
}

.supply-list {
  margin-top: 10rpx;
}

.supply-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.supply-item:last-child {
  border-bottom: none;
}

.supply-info {
  display: flex;
  flex-direction: column;
}

.supply-name {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
}

.supply-spec {
  font-size: 24rpx;
  color: #999;
  margin-top: 6rpx;
}

.supply-type {
  font-size: 24rpx;
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
}

.supply-type.supply {
  background: #fff0ed;
  color: #e74c3c;
}

.supply-type.demand {
  background: #edf7ef;
  color: #27ae60;
}

.empty-tip {
  text-align: center;
  padding: 40rpx 0;
  color: #999;
  font-size: 26rpx;
}
</style>
