import argparse
import logging
import sys

from apscheduler.schedulers.blocking import BlockingScheduler

from config import SCHEDULE_CONFIG
from pipelines.price_pipeline import PricePipeline
from pipelines.news_pipeline import NewsPipeline
from spiders.zyctd_price_spider import ZyctdPriceSpider
from spiders.yt1998_price_spider import Yt1998PriceSpider
from spiders.zyctd_news_spider import ZyctdNewsSpider
from spiders.yt1998_news_spider import Yt1998NewsSpider

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(name)s - %(message)s',
    datefmt='%Y-%m-%d %H:%M:%S',
)
logger = logging.getLogger(__name__)


def run_price_crawlers():
    logger.info('>>> 开始执行价格爬虫任务 <<<')
    all_items = []

    zyctd_spider = ZyctdPriceSpider()
    all_items.extend(zyctd_spider.run())

    yt1998_spider = Yt1998PriceSpider()
    all_items.extend(yt1998_spider.run())

    if all_items:
        pipeline = PricePipeline()
        count = pipeline.process_items(all_items)
        logger.info(f'价格爬虫任务完成，入库 {count}/{len(all_items)} 条')
    else:
        logger.info('价格爬虫任务完成，无数据入库')


def run_news_crawlers():
    logger.info('>>> 开始执行资讯爬虫任务 <<<')
    all_items = []

    zyctd_spider = ZyctdNewsSpider()
    all_items.extend(zyctd_spider.run())

    yt1998_spider = Yt1998NewsSpider()
    all_items.extend(yt1998_spider.run())

    if all_items:
        pipeline = NewsPipeline()
        count = pipeline.process_items(all_items)
        logger.info(f'资讯爬虫任务完成，入库 {count}/{len(all_items)} 条')
    else:
        logger.info('资讯爬虫任务完成，无数据入库')


def run_scheduler():
    scheduler = BlockingScheduler()

    price_cfg = SCHEDULE_CONFIG['price_crawler']
    scheduler.add_job(run_price_crawlers, **price_cfg, id='price_crawler', name='价格爬虫')

    news_cfg = SCHEDULE_CONFIG['news_crawler']
    scheduler.add_job(run_news_crawlers, **news_cfg, id='news_crawler', name='资讯爬虫')

    logger.info('=' * 60)
    logger.info('TCM 爬虫调度服务已启动')
    logger.info(f'  价格爬虫调度: {price_cfg}')
    logger.info(f'  资讯爬虫调度: {news_cfg}')
    logger.info('=' * 60)

    try:
        scheduler.start()
    except (KeyboardInterrupt, SystemExit):
        logger.info('调度服务已停止')
        scheduler.shutdown()


def main():
    parser = argparse.ArgumentParser(description='TCM 中药材市场爬虫服务')
    parser.add_argument(
        '--spider',
        choices=['price', 'news', 'all'],
        help='手动触发爬虫: price=价格, news=资讯, all=全部',
    )
    args = parser.parse_args()

    if args.spider:
        logger.info(f'手动触发模式: spider={args.spider}')
        if args.spider in ('price', 'all'):
            run_price_crawlers()
        if args.spider in ('news', 'all'):
            run_news_crawlers()
    else:
        run_scheduler()


if __name__ == '__main__':
    main()
