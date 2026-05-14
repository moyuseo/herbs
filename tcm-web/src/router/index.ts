import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: () => import('@/views/Home.vue'),
    },
    {
      path: '/price/market',
      name: 'MarketPrice',
      component: () => import('@/views/price/MarketPrice.vue'),
    },
    {
      path: '/price/origin',
      name: 'OriginPrice',
      component: () => import('@/views/price/OriginPrice.vue'),
    },
    {
      path: '/price/detail/:herbId',
      name: 'PriceDetail',
      component: () => import('@/views/price/PriceDetail.vue'),
    },
    {
      path: '/price/ranking',
      name: 'Ranking',
      component: () => import('@/views/price/Ranking.vue'),
    },
    {
      path: '/news/list',
      name: 'NewsList',
      component: () => import('@/views/news/NewsList.vue'),
    },
    {
      path: '/news/detail/:id',
      name: 'NewsDetail',
      component: () => import('@/views/news/NewsDetail.vue'),
    },
    {
      path: '/supply/list',
      name: 'SupplyList',
      component: () => import('@/views/supply/SupplyList.vue'),
    },
    {
      path: '/demand/list',
      name: 'DemandList',
      component: () => import('@/views/supply/DemandList.vue'),
    },
    {
      path: '/supply/publish',
      name: 'Publish',
      component: () => import('@/views/supply/Publish.vue'),
    },
    {
      path: '/herb/list',
      name: 'HerbList',
      component: () => import('@/views/herb/HerbList.vue'),
    },
    {
      path: '/herb/detail/:herbId',
      name: 'HerbDetail',
      component: () => import('@/views/herb/HerbDetail.vue'),
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/user/Login.vue'),
    },
    {
      path: '/user/watchlist',
      name: 'Watchlist',
      component: () => import('@/views/user/Watchlist.vue'),
    },
    {
      path: '/user/publishments',
      name: 'MyPublishments',
      component: () => import('@/views/user/MyPublishments.vue'),
    },
    {
      path: '/user/alerts',
      name: 'PriceAlerts',
      component: () => import('@/views/user/PriceAlerts.vue'),
    },
    {
      path: '/user/membership',
      name: 'Membership',
      component: () => import('@/views/user/Membership.vue'),
    },
  ],
})

export default router
