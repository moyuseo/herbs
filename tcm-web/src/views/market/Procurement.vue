<template>
  <div class="procurement page-wrapper">
    <div class="container">
      <div class="card-box">
        <h2 class="page-title">集采专区</h2>

        <el-alert
          title="集采政策公告"
          type="info"
          :closable="false"
          show-icon
          class="policy-alert"
        >
          <template #default>
            <p>根据国家医疗保障局发布的《关于做好中药材集中采购工作的指导意见》，各医疗机构须通过省级集采平台进行中药材采购，确保药材质量与价格透明。请关注最新集采动态，及时参与投标。</p>
          </template>
        </el-alert>

        <div class="news-section">
          <h3 class="section-title">集采资讯</h3>
          <el-row :gutter="16">
            <el-col v-for="item in newsList" :key="item.id" :xs="24" :sm="12" :md="8">
              <el-card shadow="hover" class="news-card">
                <div class="news-title">{{ item.title }}</div>
                <div class="news-date">{{ item.publishDate }}</div>
                <div class="news-tags">
                  <el-tag
                    v-for="tag in item.herbs"
                    :key="tag"
                    size="small"
                    type="success"
                    class="herb-tag"
                  >
                    {{ tag }}
                  </el-tag>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <div class="result-section">
          <h3 class="section-title">历史集采结果</h3>
          <el-table :data="historyResults" stripe border style="width: 100%">
            <el-table-column prop="batchName" label="集采批次" min-width="160" />
            <el-table-column prop="herbName" label="品种" width="120" />
            <el-table-column prop="spec" label="规格" width="120" />
            <el-table-column prop="winPrice" label="中标价(元)" width="120">
              <template #default="{ row }">
                <span class="price-down">{{ row.winPrice }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="winCompany" label="中标企业" min-width="160" />
            <el-table-column prop="quantity" label="采购量(吨)" width="120" />
            <el-table-column prop="date" label="公布日期" width="120" />
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const newsList = ref([
  {
    id: 1,
    title: '2026年第一批中药材集中采购公告发布',
    publishDate: '2026-05-10',
    herbs: ['当归', '黄芪', '党参']
  },
  {
    id: 2,
    title: '关于规范中药材集采投标企业资质的通知',
    publishDate: '2026-04-28',
    herbs: ['白术', '茯苓']
  },
  {
    id: 3,
    title: '2025年第四批集采结果公示及执行安排',
    publishDate: '2026-04-15',
    herbs: ['川芎', '丹参', '甘草']
  },
  {
    id: 4,
    title: '中药材集采质量标准修订征求意见稿',
    publishDate: '2026-03-20',
    herbs: ['板蓝根', '连翘']
  },
  {
    id: 5,
    title: '省级中药材集采平台操作指南更新',
    publishDate: '2026-03-05',
    herbs: ['金银花', '黄芩', '柴胡']
  },
  {
    id: 6,
    title: '2025年第三批集采中标企业履约情况通报',
    publishDate: '2026-02-18',
    herbs: ['枸杞', '红花']
  }
])

const historyResults = ref([
  {
    batchName: '2025年第四批集采',
    herbName: '当归',
    spec: '统货',
    winPrice: 68.5,
    winCompany: '甘肃陇药集团有限公司',
    quantity: 500,
    date: '2026-01-15'
  },
  {
    batchName: '2025年第四批集采',
    herbName: '黄芪',
    spec: '选片',
    winPrice: 45.0,
    winCompany: '内蒙古药材股份有限公司',
    quantity: 800,
    date: '2026-01-15'
  },
  {
    batchName: '2025年第三批集采',
    herbName: '白术',
    spec: '统个',
    winPrice: 32.0,
    winCompany: '安徽亳州中药科技有限公司',
    quantity: 300,
    date: '2025-10-20'
  },
  {
    batchName: '2025年第三批集采',
    herbName: '茯苓',
    spec: '白块',
    winPrice: 28.5,
    winCompany: '云南药材集团有限责任公司',
    quantity: 450,
    date: '2025-10-20'
  },
  {
    batchName: '2025年第二批集采',
    herbName: '川芎',
    spec: '统个',
    winPrice: 22.0,
    winCompany: '四川中药饮片有限公司',
    quantity: 600,
    date: '2025-07-10'
  },
  {
    batchName: '2025年第二批集采',
    herbName: '丹参',
    spec: '统片',
    winPrice: 18.5,
    winCompany: '山东步长制药股份有限公司',
    quantity: 550,
    date: '2025-07-10'
  }
])
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.page-title {
  font-size: 20px;
  color: $text-color;
  margin-bottom: 20px;
}

.policy-alert {
  margin-bottom: 24px;

  p {
    line-height: 1.8;
    color: $text-regular;
  }
}

.section-title {
  font-size: 16px;
  color: $text-color;
  margin-bottom: 16px;
}

.news-section {
  margin-bottom: 30px;
}

.news-card {
  margin-bottom: 16px;
  cursor: pointer;
  transition: transform 0.2s;

  &:hover {
    transform: translateY(-2px);
  }

  .news-title {
    font-size: 15px;
    font-weight: 500;
    color: $text-color;
    line-height: 1.6;
    margin-bottom: 8px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .news-date {
    font-size: 13px;
    color: $text-secondary;
    margin-bottom: 8px;
  }

  .news-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;

    .herb-tag {
      cursor: pointer;
    }
  }
}

.result-section {
  margin-top: 10px;
}

@media (max-width: 768px) {
  .policy-alert {
    :deep(.el-alert__content) {
      padding: 0;
    }
  }
}
</style>
