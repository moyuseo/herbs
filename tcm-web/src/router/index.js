import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'market',
        name: 'MarketIndex',
        component: () => import('@/views/market/Index.vue'),
        meta: { title: '行情中心' },
        children: [
          { path: '', name: 'MarketPrice', component: () => import('@/views/market/MarketPrice.vue'), meta: { title: '市场价格' } },
          { path: 'origin', name: 'OriginPrice', component: () => import('@/views/market/OriginPrice.vue'), meta: { title: '产地价格' } },
          { path: 'index', name: 'PriceIndex', component: () => import('@/views/market/PriceIndex.vue'), meta: { title: '价格指数' } },
          { path: 'ranking', name: 'Ranking', component: () => import('@/views/market/Ranking.vue'), meta: { title: '涨跌排行' } },
          { path: 'procurement', name: 'Procurement', component: () => import('@/views/market/Procurement.vue'), meta: { title: '采购指南' } }
        ]
      },
      { path: 'market/herb/:id', name: 'HerbDetail', component: () => import('@/views/market/HerbDetail.vue'), meta: { title: '品种详情' } },
      { path: 'market/price-history/:id', name: 'PriceHistory', component: () => import('@/views/market/PriceHistory.vue'), meta: { title: '历史价格' } },
      {
        path: 'news',
        name: 'NewsIndex',
        component: () => import('@/views/news/Index.vue'),
        meta: { title: '资讯中心' },
        children: [
          { path: '', name: 'MarketDynamic', component: () => import('@/views/news/MarketDynamic.vue'), meta: { title: '市场动态' } },
          { path: 'analysis', name: 'Analysis', component: () => import('@/views/news/Analysis.vue'), meta: { title: '行情分析' } },
          { path: 'origin-report', name: 'OriginReport', component: () => import('@/views/news/OriginReport.vue'), meta: { title: '产地报道' } },
          { path: 'bidding', name: 'Bidding', component: () => import('@/views/news/Bidding.vue'), meta: { title: '招标信息' } },
          { path: 'central-procurement', name: 'CentralProcurement', component: () => import('@/views/news/CentralProcurement.vue'), meta: { title: '集采信息' } },
          { path: 'video', name: 'Video', component: () => import('@/views/news/Video.vue'), meta: { title: '视频资讯' } }
        ]
      },
      { path: 'news/detail/:id', name: 'NewsDetail', component: () => import('@/views/news/Detail.vue'), meta: { title: '资讯详情' } },
      {
        path: 'trade',
        name: 'TradeIndex',
        component: () => import('@/views/trade/Index.vue'),
        meta: { title: '供求中心' },
        children: [
          { path: '', name: 'SupplyList', component: () => import('@/views/trade/SupplyList.vue'), meta: { title: '供应信息' } },
          { path: 'demand', name: 'DemandList', component: () => import('@/views/trade/DemandList.vue'), meta: { title: '求购信息' } }
        ]
      },
      { path: 'trade/publish-supply', name: 'PublishSupply', component: () => import('@/views/trade/PublishSupply.vue'), meta: { title: '发布供应', auth: true } },
      { path: 'trade/publish-demand', name: 'PublishDemand', component: () => import('@/views/trade/PublishDemand.vue'), meta: { title: '发布求购', auth: true } },
      { path: 'trade/detail/:id', name: 'TradeDetail', component: () => import('@/views/trade/Detail.vue'), meta: { title: '供求详情' } },
      { path: 'trade/enterprise/:id', name: 'Enterprise', component: () => import('@/views/trade/Enterprise.vue'), meta: { title: '企业主页' } },
      {
        path: 'wiki',
        name: 'WikiIndex',
        component: () => import('@/views/wiki/Index.vue'),
        meta: { title: '品种百科' },
        children: [
          { path: '', name: 'Knowledge', component: () => import('@/views/wiki/Knowledge.vue'), meta: { title: '品种知识' } },
          { path: 'cultivation', name: 'Cultivation', component: () => import('@/views/wiki/Cultivation.vue'), meta: { title: '种植技术' } },
          { path: 'regulation', name: 'Regulation', component: () => import('@/views/wiki/Regulation.vue'), meta: { title: '法规标准' } }
        ]
      },
      { path: 'wiki/detail/:id', name: 'WikiDetail', component: () => import('@/views/wiki/Detail.vue'), meta: { title: '百科详情' } },
      {
        path: 'map',
        name: 'MapIndex',
        component: () => import('@/views/map/Index.vue'),
        meta: { title: '产地地图' }
      },
      { path: 'map/origin/:id', name: 'OriginDetail', component: () => import('@/views/map/OriginDetail.vue'), meta: { title: '产地详情' } },
      { path: 'search', name: 'Search', component: () => import('@/views/search/Index.vue'), meta: { title: '搜索结果' } },
      { path: 'user/profile', name: 'Profile', component: () => import('@/views/user/Profile.vue'), meta: { title: '个人中心', auth: true } },
      { path: 'user/favorites', name: 'Favorites', component: () => import('@/views/user/Favorites.vue'), meta: { title: '我的收藏', auth: true } },
      { path: 'user/posts', name: 'Posts', component: () => import('@/views/user/Posts.vue'), meta: { title: '我的发布', auth: true } },
      { path: 'user/subscriptions', name: 'Subscriptions', component: () => import('@/views/user/Subscriptions.vue'), meta: { title: '我的订阅', auth: true } },
      { path: 'user/history', name: 'History', component: () => import('@/views/user/History.vue'), meta: { title: '浏览历史', auth: true } },
      { path: 'user/membership', name: 'Membership', component: () => import('@/views/user/Membership.vue'), meta: { title: '会员中心', auth: true } }
    ]
  },
  { path: '/login', name: 'Login', component: () => import('@/views/user/Login.vue'), meta: { title: '登录' } },
  { path: '/register', name: 'Register', component: () => import('@/views/user/Register.vue'), meta: { title: '注册' } }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 中药材行情` : '中药材行情'
  if (to.meta.auth) {
    const userStore = useUserStore()
    if (!userStore.isLoggedIn) {
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
  }
  next()
})

export default router
