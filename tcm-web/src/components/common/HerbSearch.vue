<template>
  <el-autocomplete
    v-model="keyword"
    :fetch-suggestions="handleSearch"
    placeholder="搜索药材品种..."
    :trigger-on-focus="false"
    clearable
    class="herb-search"
    @select="handleSelect"
  >
    <template #prefix>
      <el-icon><Search /></el-icon>
    </template>
  </el-autocomplete>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { searchHerb } from '@/api/herb'

const router = useRouter()
const keyword = ref('')

interface SearchItem {
  value: string
  herbId: string | number
}

async function handleSearch(queryString: string, cb: (results: SearchItem[]) => void) {
  if (!queryString) {
    cb([])
    return
  }
  try {
    const data = await searchHerb(queryString)
    const list = Array.isArray(data) ? data : data?.list || []
    const results: SearchItem[] = list.map((item: any) => ({
      value: item.name || item.herbName,
      herbId: item.herbId || item.id,
    }))
    cb(results)
  } catch {
    cb([])
  }
}

function handleSelect(item: SearchItem) {
  router.push(`/price/detail/${item.herbId}`)
}
</script>

<style scoped lang="scss">
.herb-search {
  width: 240px;

  :deep(.el-input__wrapper) {
    border-radius: 20px;
  }
}
</style>
