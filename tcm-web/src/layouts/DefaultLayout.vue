<template>
  <div class="default-layout">
    <AppHeader />
    <main class="page-wrapper">
      <router-view />
    </main>
    <AppFooter />
  </div>
</template>

<script setup>
import AppHeader from '@/components/common/AppHeader.vue'
import AppFooter from '@/components/common/AppFooter.vue'
</script>

<style lang="scss" scoped>
.default-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
</style>
