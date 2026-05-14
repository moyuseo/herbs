<template>
  <el-header class="app-header">
    <div class="header-inner container">
      <div class="header-left">
        <router-link to="/" class="logo">
          <el-icon :size="24"><FirstAidKit /></el-icon>
          <span>中药材行情</span>
        </router-link>
        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          :ellipsis="false"
          class="header-menu"
          router
        >
          <el-menu-item index="/market">行情中心</el-menu-item>
          <el-menu-item index="/news">资讯中心</el-menu-item>
          <el-menu-item index="/trade">供求中心</el-menu-item>
          <el-menu-item index="/wiki">品种百科</el-menu-item>
          <el-menu-item index="/map">产地地图</el-menu-item>
        </el-menu>
      </div>
      <div class="header-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索中药材品种"
          class="search-input"
          :prefix-icon="Search"
          @keyup.enter="handleSearch"
        />
        <template v-if="userStore.isLoggedIn">
          <el-dropdown trigger="click">
            <span class="user-info">
              <el-avatar :size="28">{{ userStore.username?.charAt(0) }}</el-avatar>
              <span class="username">{{ userStore.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/user/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" @click="$router.push('/login')">登录</el-button>
        </template>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { Opportunity as Leaf } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const searchKeyword = ref('')

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/market')) return '/market'
  if (path.startsWith('/news')) return '/news'
  if (path.startsWith('/trade')) return '/trade'
  if (path.startsWith('/wiki')) return '/wiki'
  if (path.startsWith('/map')) return '/map'
  return ''
})

function handleSearch() {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/search', query: { q: searchKeyword.value.trim() } })
  }
}

function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.app-header {
  height: $header-height;
  background: $bg-white;
  box-shadow: $shadow-light;
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-inner {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 18px;
  font-weight: 700;
  color: $primary-color;
  margin-right: 20px;
  text-decoration: none;
}

.header-menu {
  border-bottom: none;

  .el-menu-item {
    font-size: 15px;
    &.is-active {
      color: $primary-color;
      border-bottom-color: $primary-color;
    }
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-input {
  width: 220px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: $text-regular;
}
</style>
