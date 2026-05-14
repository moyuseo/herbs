<template>
  <header class="app-header">
    <div class="app-header__inner">
      <div class="app-header__left">
        <router-link to="/" class="app-header__logo">
          <el-icon :size="24"><DataAnalysis /></el-icon>
          <span class="app-header__title">中药材行情</span>
        </router-link>

        <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          :ellipsis="false"
          class="app-header__menu"
          :collapse="isMobile"
          @select="handleMenuSelect"
        >
          <el-sub-menu index="price">
            <template #title>价格中心</template>
            <el-menu-item index="/price/market">市场价格</el-menu-item>
            <el-menu-item index="/price/origin">产地价格</el-menu-item>
            <el-menu-item index="/price/ranking">涨跌排行</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/news/list">行情资讯</el-menu-item>

          <el-sub-menu index="supply">
            <template #title>供求大厅</template>
            <el-menu-item index="/supply/list">供应信息</el-menu-item>
            <el-menu-item index="/demand/list">求购信息</el-menu-item>
            <el-menu-item index="/supply/publish">发布信息</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/herb/list">药材百科</el-menu-item>
        </el-menu>
      </div>

      <div class="app-header__right">
        <HerbSearch />

        <template v-if="userStore.token">
          <el-dropdown trigger="click" @command="handleUserCommand">
            <div class="app-header__user">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar">
                {{ userStore.userInfo?.nickname?.charAt(0) || '用' }}
              </el-avatar>
              <span class="app-header__username">{{ userStore.userInfo?.nickname || '用户' }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="watchlist">我的关注</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" size="small" @click="router.push('/login')">
            登录 / 注册
          </el-button>
        </template>

        <el-icon
          class="app-header__menu-toggle"
          :size="20"
          @click="mobileMenuVisible = !mobileMenuVisible"
        >
          <component :is="mobileMenuVisible ? 'Close' : 'Menu'" />
        </el-icon>
      </div>
    </div>

    <div v-if="mobileMenuVisible" class="app-header__mobile-menu">
      <el-menu :default-active="activeMenu" @select="handleMobileMenuSelect">
        <el-sub-menu index="price">
          <template #title>价格中心</template>
          <el-menu-item index="/price/market">市场价格</el-menu-item>
          <el-menu-item index="/price/origin">产地价格</el-menu-item>
          <el-menu-item index="/price/ranking">涨跌排行</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/news/list">行情资讯</el-menu-item>

        <el-sub-menu index="supply">
          <template #title>供求大厅</template>
          <el-menu-item index="/supply/list">供应信息</el-menu-item>
          <el-menu-item index="/demand/list">求购信息</el-menu-item>
          <el-menu-item index="/supply/publish">发布信息</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/herb/list">药材百科</el-menu-item>
      </el-menu>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import HerbSearch from '@/components/common/HerbSearch.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const mobileMenuVisible = ref(false)
const windowWidth = ref(window.innerWidth)

const isMobile = computed(() => windowWidth.value < 768)

const activeMenu = computed(() => route.path)

function handleMenuSelect(index: string) {
  router.push(index)
}

function handleMobileMenuSelect(index: string) {
  router.push(index)
  mobileMenuVisible.value = false
}

function handleUserCommand(command: string) {
  if (command === 'watchlist') {
    router.push('/user/watchlist')
  } else if (command === 'logout') {
    userStore.logout()
    router.push('/')
  }
}

function handleResize() {
  windowWidth.value = window.innerWidth
  if (windowWidth.value >= 768) {
    mobileMenuVisible.value = false
  }
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
  if (userStore.token && !userStore.userInfo) {
    userStore.fetchProfile()
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

  &__inner {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: $header-height;
    padding: 0 24px;
    max-width: 1200px;
    margin: 0 auto;
  }

  &__left {
    display: flex;
    align-items: center;
    flex: 1;
    min-width: 0;
  }

  &__logo {
    display: flex;
    align-items: center;
    text-decoration: none;
    color: $primary-color;
    margin-right: 24px;
    flex-shrink: 0;
  }

  &__title {
    font-size: 18px;
    font-weight: 700;
    margin-left: 6px;
    white-space: nowrap;
  }

  &__menu {
    border-bottom: none !important;

    &.el-menu--horizontal {
      border-bottom: none;
    }

    :deep(.el-menu-item),
    :deep(.el-sub-menu__title) {
      height: $header-height;
      line-height: $header-height;
    }
  }

  &__right {
    display: flex;
    align-items: center;
    gap: 16px;
    flex-shrink: 0;
  }

  &__user {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
  }

  &__username {
    font-size: 14px;
    color: $text-color;
    max-width: 80px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__menu-toggle {
    display: none;
    cursor: pointer;
    color: $text-color;
  }

  &__mobile-menu {
    display: none;
    border-top: 1px solid $border-color;
    padding: 8px 0;
    background: #fff;

    :deep(.el-menu) {
      border-right: none;
    }
  }
}

@media (max-width: 768px) {
  .app-header {
    &__menu {
      display: none !important;
    }

    &__menu-toggle {
      display: block;
    }

    &__mobile-menu {
      display: block;
    }

    &__right {
      .herb-search {
        width: 160px;
      }
    }

    &__title {
      font-size: 15px;
    }
  }
}
</style>
