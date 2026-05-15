<template>
  <header class="app-header">
    <div class="app-header__gold-line"></div>
    <div class="app-header__inner">
      <div class="app-header__left">
        <router-link to="/" class="app-header__logo">
          <span class="app-header__logo-icon">
            <svg viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="16" cy="16" r="14" stroke="currentColor" stroke-width="1.5" opacity="0.6"/>
              <path d="M16 6C16 6 10 12 10 18C10 21.3 12.7 24 16 24C19.3 24 22 21.3 22 18C22 12 16 6 16 6Z" fill="currentColor" opacity="0.8"/>
              <path d="M16 10V22" stroke="#1a5632" stroke-width="1" stroke-linecap="round"/>
              <path d="M13 16H19" stroke="#1a5632" stroke-width="1" stroke-linecap="round"/>
            </svg>
          </span>
          <span class="app-header__logo-text">
            <span class="app-header__title">中药材行情</span>
            <span class="app-header__subtitle">TCM Market Insight</span>
          </span>
          <span class="app-header__logo-accent"></span>
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

          <el-menu-item index="/data/index">价格指数</el-menu-item>

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
        <div class="app-header__search">
          <HerbSearch />
        </div>

        <template v-if="userStore.token">
          <el-dropdown trigger="click" @command="handleUserCommand">
            <div class="app-header__user">
              <el-avatar :size="30" :src="userStore.userInfo?.avatar">
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
          <el-button class="app-header__login-btn" size="small" @click="router.push('/login')">
            登录 / 注册
          </el-button>
        </template>

        <div
          class="app-header__menu-toggle"
          @click="mobileMenuVisible = !mobileMenuVisible"
        >
          <span class="app-header__hamburger" :class="{ 'is-active': mobileMenuVisible }">
            <span></span>
            <span></span>
            <span></span>
          </span>
        </div>
      </div>
    </div>

    <transition name="slide-down">
      <div v-if="mobileMenuVisible" class="app-header__mobile-menu">
        <el-menu :default-active="activeMenu" @select="handleMobileMenuSelect">
          <el-sub-menu index="price">
            <template #title>价格中心</template>
            <el-menu-item index="/price/market">市场价格</el-menu-item>
            <el-menu-item index="/price/origin">产地价格</el-menu-item>
            <el-menu-item index="/price/ranking">涨跌排行</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/data/index">价格指数</el-menu-item>

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
    </transition>
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
  background: $primary-color;
  box-shadow: $shadow-md;

  &__gold-line {
    height: 2px;
    background: linear-gradient(
      90deg,
      transparent 0%,
      $accent-color 15%,
      $accent-light 50%,
      $accent-color 85%,
      transparent 100%
    );
  }

  &__inner {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: $header-height;
    padding: 0 32px;
    max-width: $container-max;
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
    color: #fff;
    margin-right: 32px;
    flex-shrink: 0;
    position: relative;
    gap: 10px;
  }

  &__logo-icon {
    width: 32px;
    height: 32px;
    color: $accent-color;
    display: flex;
    align-items: center;
    flex-shrink: 0;

    svg {
      width: 100%;
      height: 100%;
    }
  }

  &__logo-text {
    display: flex;
    flex-direction: column;
    line-height: 1.2;
  }

  &__title {
    font-family: $font-display;
    font-size: 20px;
    font-weight: 700;
    color: #fff;
    letter-spacing: 2px;
    white-space: nowrap;
  }

  &__subtitle {
    font-family: $font-body;
    font-size: 9px;
    color: rgba(255, 255, 255, 0.5);
    letter-spacing: 1.5px;
    text-transform: uppercase;
    margin-top: 1px;
  }

  &__logo-accent {
    width: 3px;
    height: 28px;
    background: linear-gradient(180deg, $accent-color, $accent-light, $accent-color);
    border-radius: 2px;
    margin-left: 14px;
    opacity: 0.7;
  }

  &__menu {
    border-bottom: none !important;
    background: transparent !important;

    &.el-menu--horizontal {
      border-bottom: none;
      background: transparent;
    }

    :deep(.el-menu-item),
    :deep(.el-sub-menu__title) {
      height: $header-height;
      line-height: $header-height;
      color: rgba(255, 255, 255, 0.85);
      font-size: 14px;
      font-weight: 500;
      border-bottom: 2px solid transparent !important;
      background: transparent !important;
      transition: color 0.25s, border-color 0.25s;

      &:hover {
        color: #fff;
        background: rgba(255, 255, 255, 0.08) !important;
        border-bottom-color: $accent-color !important;
      }
    }

    :deep(.el-menu-item.is-active) {
      color: #fff !important;
      border-bottom-color: $accent-color !important;
      background: rgba(255, 255, 255, 0.06) !important;
    }

    :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
      color: #fff !important;
      border-bottom-color: $accent-color !important;
    }

    :deep(.el-sub-menu__icon-arrow) {
      color: rgba(255, 255, 255, 0.6);
    }
  }

  &__right {
    display: flex;
    align-items: center;
    gap: 16px;
    flex-shrink: 0;
  }

  &__search {
    :deep(.herb-search) {
      .el-input__wrapper {
        background: rgba(255, 255, 255, 0.1);
        border: 1px solid rgba(255, 255, 255, 0.15);
        border-radius: 20px;
        box-shadow: none;
        transition: all 0.25s;

        &:hover {
          background: rgba(255, 255, 255, 0.15);
          border-color: rgba(255, 255, 255, 0.25);
        }

        &.is-focus {
          background: rgba(255, 255, 255, 0.18);
          border-color: $accent-color;
          box-shadow: 0 0 0 2px rgba($accent-color, 0.15);
        }
      }

      .el-input__inner {
        color: #fff;

        &::placeholder {
          color: rgba(255, 255, 255, 0.45);
        }
      }

      .el-input__prefix .el-icon,
      .el-input__suffix .el-icon {
        color: rgba(255, 255, 255, 0.5);
      }
    }
  }

  &__login-btn {
    background: linear-gradient(135deg, $accent-color, darken($accent-color, 5%)) !important;
    border: none !important;
    color: #fff !important;
    font-weight: 600;
    border-radius: $radius-sm !important;
    padding: 8px 20px !important;
    letter-spacing: 0.5px;
    transition: all 0.25s;
    box-shadow: 0 2px 8px rgba($accent-color, 0.3);

    &:hover {
      background: linear-gradient(135deg, lighten($accent-color, 5%), $accent-color) !important;
      box-shadow: 0 4px 12px rgba($accent-color, 0.4);
      transform: translateY(-1px);
    }

    &:active {
      transform: translateY(0);
    }
  }

  &__user {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: $radius-sm;
    transition: background 0.2s;

    &:hover {
      background: rgba(255, 255, 255, 0.1);
    }
  }

  &__username {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.9);
    max-width: 80px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__menu-toggle {
    display: none;
    cursor: pointer;
    width: 28px;
    height: 28px;
    align-items: center;
    justify-content: center;
  }

  &__hamburger {
    width: 22px;
    height: 16px;
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: space-between;

    span {
      display: block;
      width: 100%;
      height: 2px;
      background: #fff;
      border-radius: 2px;
      transition: all 0.3s ease;

      &:nth-child(2) {
        width: 70%;
        transition: width 0.3s ease;
      }
    }

    &:hover span:nth-child(2) {
      width: 100%;
    }

    &.is-active {
      span:nth-child(1) {
        transform: translateY(7px) rotate(45deg);
      }
      span:nth-child(2) {
        opacity: 0;
        width: 0;
      }
      span:nth-child(3) {
        transform: translateY(-7px) rotate(-45deg);
      }
    }
  }

  &__mobile-menu {
    background: $primary-dark;
    border-top: 1px solid rgba(255, 255, 255, 0.08);
    padding: 8px 0;
    max-height: 60vh;
    overflow-y: auto;

    :deep(.el-menu) {
      border-right: none;
      background: transparent;
    }

    :deep(.el-menu-item),
    :deep(.el-sub-menu__title) {
      color: rgba(255, 255, 255, 0.8);
      background: transparent !important;

      &:hover {
        color: #fff;
        background: rgba(255, 255, 255, 0.06) !important;
      }
    }

    :deep(.el-menu-item.is-active) {
      color: $accent-light !important;
    }

    :deep(.el-sub-menu__icon-arrow) {
      color: rgba(255, 255, 255, 0.5);
    }
  }
}

.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
  overflow: hidden;
}

.slide-down-enter-from,
.slide-down-leave-to {
  max-height: 0;
  opacity: 0;
  padding-top: 0;
  padding-bottom: 0;
}

@media (max-width: 768px) {
  .app-header {
    &__inner {
      padding: 0 16px;
    }

    &__logo {
      margin-right: 16px;
    }

    &__logo-accent {
      display: none;
    }

    &__subtitle {
      display: none;
    }

    &__title {
      font-size: 17px;
      letter-spacing: 1px;
    }

    &__menu {
      display: none !important;
    }

    &__menu-toggle {
      display: flex;
    }

    &__mobile-menu {
      display: block;
    }

    &__right {
      gap: 10px;

      .herb-search {
        width: 140px;
      }
    }

    &__login-btn {
      padding: 6px 14px !important;
      font-size: 13px !important;
    }
  }
}

@media (max-width: 480px) {
  .app-header {
    &__right {
      .herb-search {
        width: 100px;
      }
    }

    &__login-btn {
      padding: 5px 10px !important;
      font-size: 12px !important;
    }
  }
}
</style>
