<template>
  <view class="page">
    <view class="article-header">
      <text class="article-title">{{ article.title }}</text>
      <view class="article-meta">
        <text class="meta-item">{{ article.source }}</text>
        <text class="meta-item">{{ article.date }}</text>
        <text class="meta-item">{{ article.views }}阅读</text>
      </view>
    </view>

    <view class="article-body">
      <rich-text :nodes="article.content" />
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const article = ref({
  id: 0,
  title: '',
  source: '',
  date: '',
  views: 0,
  content: ''
})

function loadArticle(id: number) {
  article.value = {
    id,
    title: '2026年黄芪产新在即，市场行情分析',
    source: '中药材行情网',
    date: '2026-05-14',
    views: 1256,
    content: `<h3>一、市场概况</h3>
<p>2026年黄芪产新季即将到来，目前市场黄芪（统片）价格在22.5元/公斤左右，较上月同期上涨3.2%。受产地天气影响，今年黄芪产量预计有所下降。</p>
<h3>二、产地情况</h3>
<p>甘肃主产区今年春季降水偏少，黄芪出苗率受到一定影响。内蒙古产区情况相对稳定，但种植面积较去年有所缩减。</p>
<h3>三、后市展望</h3>
<p>综合来看，在产新前黄芪价格仍有支撑，但需关注产新后的实际产量情况。建议经营户理性购进，避免盲目追高。</p>`
  }
}

onLoad((options) => {
  const id = Number(options?.id || 1)
  loadArticle(id)
})
</script>

<style scoped>
.page {
  background: #fff;
  min-height: 100vh;
}

.article-header {
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.article-title {
  font-size: 38rpx;
  font-weight: bold;
  color: #333;
  line-height: 1.5;
  display: block;
}

.article-meta {
  display: flex;
  align-items: center;
  margin-top: 20rpx;
}

.meta-item {
  font-size: 24rpx;
  color: #999;
  margin-right: 24rpx;
}

.article-body {
  padding: 30rpx;
  font-size: 30rpx;
  line-height: 1.8;
  color: #333;
}

.article-body :deep(h3) {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin: 30rpx 0 16rpx;
}

.article-body :deep(p) {
  margin-bottom: 20rpx;
  text-indent: 2em;
}
</style>
