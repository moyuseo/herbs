<template>
  <div class="herb-list">
    <div class="page-header">
      <div class="page-header__inner">
        <h1 class="page-title">中药材名录</h1>
        <p class="page-subtitle">传承千年智慧 · 汇聚百草精华</p>
      </div>
      <div class="search-box">
        <el-input
          v-model="filters.keyword"
          placeholder="搜索药材名称、别名、功效..."
          clearable
          :prefix-icon="Search"
          size="large"
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>
    </div>

    <div class="category-pills">
      <span
        :class="['category-pill', { active: filters.categoryId === null }]"
        @click="selectCategory(null)"
      >全部</span>
      <span
        v-for="cat in categories"
        :key="cat.id"
        :class="['category-pill', { active: filters.categoryId === cat.id }]"
        @click="selectCategory(cat.id)"
      >{{ cat.name }}</span>
    </div>

    <div class="pinyin-nav">
      <span
        v-for="letter in pinyinLetters"
        :key="letter"
        :class="['pinyin-nav__item', { active: activePinyin === letter }]"
        @click="handlePinyinClick(letter)"
      >{{ letter }}</span>
    </div>

    <div v-loading="loading" class="herb-grid">
      <el-empty v-if="!loading && herbList.length === 0" description="暂无药材数据" />
      <div
        v-for="item in herbList"
        :key="item.herbId"
        class="herb-card"
        @click="goDetail(item.id || item.herbId)"
      >
        <div class="herb-card__name">{{ item.name }}</div>
        <div class="herb-card__alias">{{ item.alias || '暂无别名' }}</div>
        <div class="herb-card__tags">
          <span class="herb-tag herb-tag--nature">{{ item.natureFlavor || item.property || '-' }}</span>
          <span class="herb-tag herb-tag--meridian">{{ item.meridianTropism || item.meridian || '-' }}</span>
        </div>
        <div class="herb-card__effect">{{ item.efficacy || item.effect || '-' }}</div>
        <div class="herb-card__footer">
          <span class="herb-card__category">{{ item.categoryName || '' }}</span>
          <span class="herb-card__arrow">→</span>
        </div>
      </div>
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
  { id: 1, name: '根茎类' }, { id: 2, name: '果实类' }, { id: 3, name: '全草类' },
  { id: 4, name: '花类' }, { id: 5, name: '叶类' }, { id: 6, name: '树皮类' },
  { id: 7, name: '藤木类' }, { id: 8, name: '树脂类' }, { id: 9, name: '菌藻类' },
  { id: 10, name: '动物类' }, { id: 11, name: '矿物类' }, { id: 12, name: '其他类' },
]

const pinyinLetters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('')

interface HerbItem {
  id: number | string
  herbId: number | string
  name: string
  alias: string
  natureFlavor: string
  property: string
  meridianTropism: string
  meridian: string
  efficacy: string
  effect: string
  pinyin: string
  categoryId: number
  categoryName: string
  medicinalPart: string
}

const loading = ref(false)
const herbList = ref<HerbItem[]>([])
const activePinyin = ref('')
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)

const filters = reactive({
  keyword: '',
  categoryId: null as number | null,
})

async function fetchList() {
  loading.value = true
  try {
    const res = (await getHerbList({
      keyword: filters.keyword || undefined,
      categoryId: filters.categoryId || undefined,
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
  filters.categoryId = null
  activePinyin.value = ''
  handleSearch()
}

function handlePinyinClick(letter: string) {
  activePinyin.value = activePinyin.value === letter ? '' : letter
  page.value = 1
  fetchList()
}

function selectCategory(id: number | null) {
  filters.categoryId = id
  handleSearch()
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
  min-height: 100vh;
  background: $bg-color;
  padding-bottom: 40px;
}

.page-header {
  background: linear-gradient(135deg, $primary-color 0%, $primary-light 100%);
  padding: 40px 24px 36px;
  position: relative;
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    top: -40px;
    right: -40px;
    width: 200px;
    height: 200px;
    border-radius: 50%;
    background: rgba($accent-color, 0.1);
  }

  &__inner {
    max-width: $container-max;
    margin: 0 auto;
  }
}

.page-title {
  font-family: $font-display;
  font-size: 32px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 4px;
  padding-left: 16px;
  border-left: 4px solid $accent-color;
  line-height: 1.3;
}

.page-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 20px;
  padding-left: 20px;
  letter-spacing: 2px;
}

.search-box {
  max-width: $container-max;
  margin: 0 auto;

  :deep(.el-input__wrapper) {
    border-radius: $radius-md;
    box-shadow: $shadow-md;
  }

  :deep(.el-input-group__append) {
    background: $accent-color;
    border-color: $accent-color;
    color: #fff;
    border-radius: 0 $radius-md $radius-md 0;

    .el-button {
      color: #fff;
    }
  }
}

.category-pills {
  max-width: $container-max;
  margin: 0 auto;
  padding: 20px 24px 0;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.category-pill {
  display: inline-flex;
  align-items: center;
  padding: 6px 18px;
  font-size: 13px;
  color: $text-secondary;
  background: $card-bg;
  border: 1px solid $border-color;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.25s ease;
  user-select: none;

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

.pinyin-nav {
  max-width: $container-max;
  margin: 0 auto;
  padding: 16px 24px;
  display: flex;
  flex-wrap: wrap;
  gap: 4px;

  &__item {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    font-size: 13px;
    font-weight: 500;
    color: $text-secondary;
    cursor: pointer;
    border-radius: $radius-sm;
    transition: all 0.2s ease;
    user-select: none;

    &:hover {
      background: $accent-lighter;
      color: $accent-color;
    }

    &.active {
      background: $accent-color;
      color: #fff;
      box-shadow: 0 2px 6px rgba($accent-color, 0.35);
    }
  }
}

.herb-grid {
  max-width: $container-max;
  margin: 0 auto;
  padding: 0 24px;
  min-height: 400px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;

  @media (max-width: 1200px) {
    grid-template-columns: repeat(3, 1fr);
  }

  @media (max-width: 900px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: 600px) {
    grid-template-columns: 1fr;
  }

  :deep(.el-empty) {
    grid-column: 1 / -1;
  }
}

.herb-card {
  background: $card-bg;
  border-radius: $radius-md;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid $border-light;
  position: relative;
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-6px);
    box-shadow: $shadow-md;
    border-color: $primary-light;

    .herb-card__arrow {
      color: $accent-color;
      transform: translateX(4px);
    }
  }

  &__name {
    font-family: $font-display;
    font-size: 20px;
    font-weight: 700;
    color: $primary-color;
    margin-bottom: 4px;
    line-height: 1.4;
  }

  &__alias {
    font-size: 12px;
    color: $text-muted;
    margin-bottom: 12px;
  }

  &__tags {
    display: flex;
    gap: 8px;
    margin-bottom: 12px;
    flex-wrap: wrap;
  }

  &__effect {
    font-size: 13px;
    color: $text-secondary;
    line-height: 1.7;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    flex: 1;
  }

  &__footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 14px;
    padding-top: 12px;
    border-top: 1px solid $border-light;
  }

  &__category {
    font-size: 12px;
    color: $text-muted;
  }

  &__arrow {
    font-size: 14px;
    color: $border-color;
    transition: all 0.25s ease;
  }
}

.herb-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 10px;
  font-size: 12px;
  border-radius: 12px;
  line-height: 1.6;
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;

  &--nature {
    background: $primary-lighter;
    color: $primary-color;
  }

  &--meridian {
    background: $accent-lighter;
    color: $accent-color;
  }
}

.herb-list__pagination {
  max-width: $container-max;
  margin: 32px auto 0;
  padding: 0 24px;
  display: flex;
  justify-content: center;

  :deep(.el-pagination) {
    .is-active {
      background-color: $primary-color !important;
    }
  }
}
</style>
