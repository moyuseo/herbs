<template>
  <el-footer class="app-footer">
    <div class="footer-inner container">
      <div class="footer-top">
        <div class="footer-col">
          <h4>中药材行情</h4>
          <p>专注中药材行业数据服务</p>
        </div>
        <div class="footer-col">
          <h4>行情中心</h4>
          <router-link to="/market">市场价格</router-link>
          <router-link to="/market/origin">产地价格</router-link>
          <router-link to="/market/index">价格指数</router-link>
          <router-link to="/market/ranking">涨跌排行</router-link>
        </div>
        <div class="footer-col">
          <h4>资讯中心</h4>
          <router-link to="/news">市场动态</router-link>
          <router-link to="/news/analysis">行情分析</router-link>
          <router-link to="/news/origin-report">产地报道</router-link>
        </div>
        <div class="footer-col">
          <h4>关于我们</h4>
          <a href="javascript:;">联系方式</a>
          <a href="javascript:;">服务协议</a>
          <a href="javascript:;">隐私政策</a>
        </div>
      </div>
      <div class="footer-bottom">
        <p>© 2026 中药材行情 版权所有</p>
      </div>
    </div>
  </el-footer>
</template>

<script setup>
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.app-footer {
  height: auto;
  background: #2c3e50;
  color: #ccc;
  padding: 40px 0 20px;
}

.footer-top {
  display: flex;
  justify-content: space-between;
  padding-bottom: 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-col {
  h4 {
    color: #fff;
    font-size: 16px;
    margin-bottom: 16px;
  }

  a, p {
    display: block;
    color: #aaa;
    font-size: 13px;
    line-height: 2;
    text-decoration: none;
    &:hover {
      color: #fff;
    }
  }
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  font-size: 12px;
  color: #888;
}
</style>
