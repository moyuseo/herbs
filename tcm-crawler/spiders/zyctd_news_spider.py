import logging
import re
from datetime import datetime

import requests
from bs4 import BeautifulSoup
from fake_useragent import UserAgent

from config import PROXY_CONFIG
from items.news_item import NewsItem

logger = logging.getLogger(__name__)


class ZyctdNewsSpider:
    name = 'zyctd_news'

    BASE_URL = 'https://www.zyctd.com'

    NEWS_LIST_URL = f'{BASE_URL}/zixun/'

    def __init__(self):
        self.ua = UserAgent()
        self.session = requests.Session()
        self.session.headers.update({
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
            'Accept-Language': 'zh-CN,zh;q=0.9',
        })

    def _get_page(self, url, encoding='utf-8'):
        headers = {'User-Agent': self.ua.random}
        try:
            resp = self.session.get(url, headers=headers, proxies=PROXY_CONFIG, timeout=30)
            resp.encoding = encoding
            logger.info(f'请求成功: {url} 状态码={resp.status_code}')
            return resp.text
        except Exception as e:
            logger.error(f'请求失败: {url} 错误={e}')
            return None

    def parse_news_list(self, html):
        urls = []
        soup = BeautifulSoup(html, 'lxml')

        # TODO: 根据天地网实际页面结构调整以下选择器
        links = soup.find_all('a', href=re.compile(r'/zixun/\d+\.html'))
        for link in links:
            href = link.get('href', '')
            if href and not href.startswith('http'):
                href = self.BASE_URL + href
            if href not in urls:
                urls.append(href)

        if not urls:
            news_items = soup.find_all('div', class_='news-item')
            for item_div in news_items:
                a_tag = item_div.find('a')
                if a_tag:
                    href = a_tag.get('href', '')
                    if href and not href.startswith('http'):
                        href = self.BASE_URL + href
                    if href not in urls:
                        urls.append(href)

        logger.info(f'解析到 {len(urls)} 条资讯链接')
        return urls

    def parse_news_detail(self, html, url):
        soup = BeautifulSoup(html, 'lxml')
        item = NewsItem()
        item.source = '天地网'

        # TODO: 根据天地网实际页面结构调整以下选择器
        title_tag = soup.find('h1') or soup.find('div', class_='title')
        item.title = title_tag.get_text(strip=True) if title_tag else ''

        content_tag = soup.find('div', class_='content') or soup.find('div', class_='article-content')
        item.content = content_tag.get_text(strip=True) if content_tag else ''

        summary_tag = soup.find('meta', attrs={'name': 'description'})
        item.summary = summary_tag.get('content', '') if summary_tag else ''

        cover_tag = content_tag.find('img') if content_tag else None
        item.cover_image = cover_tag.get('src', '') if cover_tag else ''

        category_tag = soup.find('div', class_='category') or soup.find('span', class_='cate')
        item.category = category_tag.get_text(strip=True) if category_tag else '行业资讯'

        author_tag = soup.find('span', class_='author') or soup.find('meta', attrs={'name': 'author'})
        if author_tag:
            item.author = author_tag.get_text(strip=True) if author_tag.name != 'meta' else author_tag.get('content', '')

        date_tag = soup.find('span', class_='date') or soup.find('time')
        if date_tag:
            date_text = date_tag.get_text(strip=True)
            item.published_at = date_text
        else:
            item.published_at = datetime.now().strftime('%Y-%m-%d %H:%M:%S')

        return item

    def run(self):
        logger.info('=== 天地网资讯爬虫启动 ===')
        all_items = []

        html = self._get_page(self.NEWS_LIST_URL)
        if not html:
            logger.error('获取天地网资讯列表页失败')
            return all_items

        detail_urls = self.parse_news_list(html)
        for url in detail_urls:
            detail_html = self._get_page(url)
            if not detail_html:
                continue
            try:
                news_item = self.parse_news_detail(detail_html, url)
                if news_item.title:
                    all_items.append(news_item)
            except Exception as e:
                logger.error(f'解析资讯详情失败: {url} 错误={e}')
                continue

        logger.info(f'天地网资讯爬取完成，共 {len(all_items)} 条')
        return all_items
