<template>
  <footer class="app-footer">
    <div class="app-footer__gold-line"></div>
    <div class="app-footer__pattern"></div>
    <div class="app-footer__inner">
      <el-row :gutter="40">
        <el-col :xs="24" :sm="12" :md="6">
          <div class="app-footer__section">
            <h4 class="app-footer__title">关于我们</h4>
            <p class="app-footer__desc">
              中药材行情平台致力于为中药材行业提供全面、及时、准确的价格行情信息，
              助力中药材产业数字化发展，传承国药精粹，赋能产业未来。
            </p>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
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

        <el-col :xs="24" :sm="12" :md="6">
          <div class="app-footer__section">
            <h4 class="app-footer__title">数据服务</h4>
            <ul class="app-footer__links">
              <li><router-link to="/data/price-index">价格指数</router-link></li>
              <li><router-link to="/data/origin-map">产地地图</router-link></li>
              <li><router-link to="/data/price-alert">价格预警</router-link></li>
              <li><router-link to="/data/api">API接口</router-link></li>
            </ul>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <div class="app-footer__section">
            <h4 class="app-footer__title">联系方式</h4>
            <ul class="app-footer__contacts">
              <li class="app-footer__contact-item">
                <span class="app-footer__contact-icon">
                  <el-icon :size="16"><Phone /></el-icon>
                </span>
                <span>400-888-8888</span>
              </li>
              <li class="app-footer__contact-item">
                <span class="app-footer__contact-icon">
                  <el-icon :size="16"><Clock /></el-icon>
                </span>
                <span>周一至周五 9:00-18:00</span>
              </li>
              <li class="app-footer__contact-item">
                <span class="app-footer__contact-icon">
                  <el-icon :size="16"><Message /></el-icon>
                </span>
                <span>service@tcm-price.com</span>
              </li>
            </ul>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="app-footer__bottom">
      <div class="app-footer__bottom-inner">
        <p class="app-footer__copyright">© {{ currentYear }} 中药材行情平台 版权所有</p>
        <p class="app-footer__icp">京ICP备XXXXXXXX号-1</p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { Phone, Clock, Message } from '@element-plus/icons-vue'

const currentYear = new Date().getFullYear()
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

.app-footer {
  position: relative;
  background: $primary-color;
  color: rgba(255, 255, 255, 0.8);
  overflow: hidden;

  &__gold-line {
    height: 3px;
    background: linear-gradient(
      90deg,
      transparent 0%,
      rgba($accent-color, 0.3) 15%,
      $accent-color 50%,
      rgba($accent-color, 0.3) 85%,
      transparent 100%
    );
  }

  &__pattern {
    position: absolute;
    top: 3px;
    left: 0;
    right: 0;
    bottom: 0;
    background-image:
      radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.02) 0%, transparent 50%),
      radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.015) 0%, transparent 40%),
      radial-gradient(circle at 50% 80%, rgba(255, 255, 255, 0.02) 0%, transparent 45%);
    background-size: 100% 100%;
    pointer-events: none;

    &::before {
      content: '';
      position: absolute;
      inset: 0;
      background-image:
        repeating-linear-gradient(
          45deg,
          transparent,
          transparent 40px,
          rgba(255, 255, 255, 0.008) 40px,
          rgba(255, 255, 255, 0.008) 41px
        ),
        repeating-linear-gradient(
          -45deg,
          transparent,
          transparent 40px,
          rgba(255, 255, 255, 0.008) 40px,
          rgba(255, 255, 255, 0.008) 41px
        );
    }
  }

  &__inner {
    position: relative;
    z-index: 1;
    max-width: $container-max;
    margin: 0 auto;
    padding: 56px 24px 40px;
  }

  &__section {
    margin-bottom: 32px;

    @media (min-width: 768px) {
      margin-bottom: 0;
    }
  }

  &__title {
    font-family: $font-display;
    font-size: 18px;
    font-weight: 600;
    color: #fff;
    margin-bottom: 20px;
    padding-bottom: 14px;
    position: relative;
    letter-spacing: 2px;

    &::after {
      content: '';
      position: absolute;
      left: 0;
      bottom: 0;
      width: 36px;
      height: 2px;
      background: linear-gradient(90deg, $accent-color, rgba($accent-color, 0.3));
      border-radius: 1px;
    }

    &::before {
      content: '';
      position: absolute;
      left: 40px;
      bottom: 0;
      width: 60px;
      height: 1px;
      background: linear-gradient(90deg, rgba($accent-color, 0.4), transparent);
    }
  }

  &__desc {
    font-size: 14px;
    line-height: 2;
    color: rgba(255, 255, 255, 0.6);
    margin: 0;
  }

  &__links {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      margin-bottom: 4px;

      a {
        display: inline-flex;
        align-items: center;
        font-size: 14px;
        line-height: 2;
        color: rgba(255, 255, 255, 0.6);
        text-decoration: none;
        transition: all 0.3s ease;
        position: relative;
        padding-left: 14px;

        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 50%;
          width: 5px;
          height: 1px;
          background: rgba(255, 255, 255, 0.3);
          transition: all 0.3s ease;
        }

        &:hover {
          color: $accent-color;

          &::before {
            width: 10px;
            background: $accent-color;
          }
        }
      }
    }
  }

  &__contacts {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  &__contact-item {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 14px;
    line-height: 2.2;
    color: rgba(255, 255, 255, 0.6);
  }

  &__contact-icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 28px;
    height: 28px;
    border-radius: 6px;
    background: rgba(255, 255, 255, 0.08);
    color: $accent-color;
    flex-shrink: 0;
    transition: background 0.3s ease;
  }

  &__contact-item:hover &__contact-icon {
    background: rgba($accent-color, 0.15);
  }

  &__bottom {
    position: relative;
    z-index: 1;
    border-top: 1px solid rgba(255, 255, 255, 0.08);
    background: $primary-dark;
  }

  &__bottom-inner {
    max-width: $container-max;
    margin: 0 auto;
    padding: 18px 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
  }

  &__copyright,
  &__icp {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.35);
    margin: 0;
  }

  &__icp {
    transition: color 0.3s ease;

    &:hover {
      color: rgba(255, 255, 255, 0.55);
    }
  }
}

@media (max-width: 767px) {
  .app-footer {
    &__inner {
      padding: 40px 20px 28px;
    }

    &__title {
      font-size: 16px;
      margin-bottom: 14px;
      padding-bottom: 10px;
    }

    &__bottom-inner {
      flex-direction: column;
      text-align: center;
      padding: 14px 20px;
    }
  }
}
</style>
