<template>
  <div class="herb-list">
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="搜索">
          <el-input
            v-model="filters.keyword"
            placeholder="输入药材名称搜索"
            clearable
            :prefix-icon="Search"
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select
            v-model="filters.category"
            placeholder="全部分类"
            clearable
            @change="handleSearch"
          >
            <el-option
              v-for="cat in categories"
              :key="cat"
              :label="cat"
              :value="cat"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="pinyin-index">
      <span
        v-for="letter in pinyinLetters"
        :key="letter"
        :class="['pinyin-index__item', { active: activePinyin === letter }]"
        @click="handlePinyinClick(letter)"
      >{{ letter }}</span>
    </div>

    <div v-loading="loading" class="herb-grid">
      <el-empty v-if="!loading && herbList.length === 0" description="暂无药材数据" />
      <el-row :gutter="16">
        <el-col
          v-for="item in herbList"
          :key="item.herbId"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <el-card
            shadow="hover"
            class="herb-card"
            @click="goDetail(item.herbId)"
          >
            <div class="herb-card__name">{{ item.name }}</div>
            <div class="herb-card__alias">{{ item.alias || '-' }}</div>
            <div class="herb-card__prop">
              <el-tag size="small" type="info">{{ item.property || '-' }}</el-tag>
              <el-tag size="small" type="warning" class="herb-card__tag">{{ item.meridian || '-' }}</el-tag>
            </div>
            <div class="herb-card__effect">{{ item.effect || '-' }}</div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="herb-list__pagination">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[12, 24, 48, 96]"
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
import { Search } from '@element-plus/icons-vue'
import { getHerbList } from '@/api/herb'

const router = useRouter()

const categories = [
  '根茎类', '果实类', '全草类', '花类', '叶类',
  '树皮类', '藤木类', '树脂类', '菌藻类', '动物类', '矿物类', '其他类',
]

const pinyinLetters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('')

interface HerbItem {
  herbId: number | string
  name: string
  alias: string
  property: string
  meridian: string
  effect: string
  pinyin: string
}

const loading = ref(false)
const herbList = ref<HerbItem[]>([])
const activePinyin = ref('')
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)

const filters = reactive({
  keyword: '',
  category: '',
})

async function fetchList() {
  loading.value = true
  try {
    const res = (await getHerbList({
      keyword: filters.keyword || undefined,
      category: filters.category || undefined,
      pinyin: activePinyin.value || undefined,
      page: page.value,
      pageSize: pageSize.value,
    })) as any
    herbList.value = res?.list || res?.data || res?.records || (Array.isArray(res) ? res : [])
    total.value = res?.total || herbList.value.length
  } catch {
    herbList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  fetchList()
}

function resetFilters() {
  filters.keyword = ''
  filters.category = ''
  activePinyin.value = ''
  handleSearch()
}

function handlePinyinClick(letter: string) {
  activePinyin.value = activePinyin.value === letter ? '' : letter
  page.value = 1
  fetchList()
}

function goDetail(herbId: number | string) {
  router.push(`/herb/detail/${herbId}`)
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

.herb-list {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.filter-card {
  margin-bottom: 16px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.pinyin-index {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-bottom: 16px;
  padding: 12px;
  background: #fff;
  border-radius: 4px;

  &__item {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 28px;
    height: 28px;
    font-size: 13px;
    color: $text-secondary;
    cursor: pointer;
    border-radius: 4px;
    transition: all 0.2s;

    &:hover {
      background: #f0f0f0;
      color: $primary-color;
    }

    &.active {
      background: $primary-color;
      color: #fff;
    }
  }
}

.herb-grid {
  min-height: 400px;
}

.herb-card {
  margin-bottom: 16px;
  cursor: pointer;
  transition: transform 0.2s;

  &:hover {
    transform: translateY(-2px);
  }

  &__name {
    font-size: 18px;
    font-weight: 600;
    color: $text-color;
    margin-bottom: 4px;
  }

  &__alias {
    font-size: 12px;
    color: #999;
    margin-bottom: 8px;
  }

  &__prop {
    display: flex;
    gap: 6px;
    margin-bottom: 8px;
  }

  &__tag {
    max-width: 120px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__effect {
    font-size: 13px;
    color: $text-secondary;
    line-height: 1.6;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}

.herb-list__pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
