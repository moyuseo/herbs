<template>
  <div class="app">
    <AppHeader />
    <main class="main-content">
      <router-view />
    </main>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
</script>

<style lang="scss">
@import '@/styles/variables.scss';
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;600;700&family=Noto+Serif+SC:wght@400;600;700&display=swap');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html {
  scroll-behavior: smooth;
}

body {
  font-family: $font-body;
  color: $text-color;
  background: $bg-color;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.main-content {
  min-height: calc(100vh - #{$header-height} - 200px);
  padding-top: $header-height;
}

:deep(.el-card) {
  border: none !important;
  border-radius: $radius-md !important;
  box-shadow: $shadow-sm !important;
  transition: box-shadow 0.3s ease, transform 0.3s ease;

  &:hover {
    box-shadow: $shadow-md !important;
  }
}

:deep(.el-card__header) {
  border-bottom: 1px solid $border-light !important;
  padding: 14px 20px !important;
}

:deep(.el-button--primary) {
  --el-button-bg-color: #{$primary-color};
  --el-button-border-color: #{$primary-color};
  --el-button-hover-bg-color: #{$primary-light};
  --el-button-hover-border-color: #{$primary-light};
  --el-button-active-bg-color: #{$primary-dark};
  --el-button-active-border-color: #{$primary-dark};
}

:deep(.el-menu--horizontal) {
  border-bottom: none !important;

  .el-menu-item,
  .el-sub-menu__title {
    font-size: 14px !important;
    font-weight: 500 !important;
    color: $text-secondary !important;
    border-bottom: none !important;
    transition: color 0.2s;

    &:hover,
    &.is-active {
      color: $primary-color !important;
      background: transparent !important;
    }
  }

  .el-sub-menu.is-active > .el-sub-menu__title {
    color: $primary-color !important;
  }
}

:deep(.el-tabs__item) {
  font-weight: 500 !important;

  &.is-active {
    color: $primary-color !important;
  }
}

:deep(.el-tabs__active-bar) {
  background-color: $primary-color !important;
}

:deep(.el-tag) {
  border-radius: 4px !important;
}

:deep(.el-table) {
  --el-table-border-color: #{$border-light};
  --el-table-header-bg-color: #{$bg-warm};

  th.el-table__cell {
    font-weight: 600 !important;
    color: $text-color !important;
    background: $bg-warm !important;
  }
}

:deep(.el-pagination) {
  --el-pagination-button-bg-color: #{$card-bg};
  --el-pagination-hover-color: #{$primary-color};
}

:deep(.el-link--primary) {
  color: $primary-color !important;

  &:hover {
    color: $primary-light !important;
  }
}

:deep(.el-empty__description p) {
  color: $text-muted !important;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.fade-in-up {
  animation: fadeInUp 0.5s ease forwards;
}
</style>
