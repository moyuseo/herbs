<template>
  <footer class="app-footer">
    <div class="app-footer__inner">
      <el-row :gutter="24">
        <el-col :xs="24" :sm="8">
          <div class="app-footer__section">
            <h4 class="app-footer__title">关于我们</h4>
            <p class="app-footer__text">
              中药材行情平台致力于为中药材行业提供全面、及时、准确的价格行情信息，
              助力中药材产业数字化发展。
            </p>
          </div>
        </el-col>

        <el-col :xs="24" :sm="8">
          <div class="app-footer__section">
            <h4 class="app-footer__title">快速链接</h4>
            <ul class="app-footer__links">
              <li><router-link to="/price/market">市场价格</router-link></li>
              <li><router-link to="/price/ranking">涨跌排行</router-link></li>
              <li><router-link to="/news/list">行情资讯</router-link></li>
              <li><router-link to="/herb/list">药材百科</router-link></li>
            </ul>
          </div>
        </el-col>

        <el-col :xs="24" :sm="8">
          <div class="app-footer__section">
            <h4 class="app-footer__title">联系方式</h4>
            <ul class="app-footer__links">
              <li>客服电话：400-888-8888</li>
              <li>工作时间：周一至周五 9:00-18:00</li>
              <li>邮箱：service@tcm-price.com</li>
            </ul>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="app-footer__bottom">
      <p>© {{ currentYear }} 中药材行情平台 版权所有</p>
    </div>
  </footer>
</template>

<script setup lang="ts">
const currentYear = new Date().getFullYear()
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

.app-footer {
  background: #2c3e50;
  color: #fff;

  &__inner {
    max-width: 1200px;
    margin: 0 auto;
    padding: 40px 24px 24px;
  }

  &__section {
    margin-bottom: 24px;
  }

  &__title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
    position: relative;
    padding-bottom: 10px;

    &::after {
      content: '';
      position: absolute;
      left: 0;
      bottom: 0;
      width: 32px;
      height: 2px;
      background: $primary-color;
    }
  }

  &__text {
    font-size: 14px;
    line-height: 1.8;
    color: rgba(255, 255, 255, 0.7);
  }

  &__links {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      font-size: 14px;
      line-height: 2;
      color: rgba(255, 255, 255, 0.7);

      a {
        color: rgba(255, 255, 255, 0.7);
        text-decoration: none;
        transition: color 0.2s;

        &:hover {
          color: #fff;
        }
      }
    }
  }

  &__bottom {
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    text-align: center;
    padding: 16px 24px;

    p {
      font-size: 13px;
      color: rgba(255, 255, 255, 0.5);
      margin: 0;
    }
  }
}
</style>
