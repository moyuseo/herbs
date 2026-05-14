import logging
import re
from datetime import datetime

import requests
from bs4 import BeautifulSoup
from fake_useragent import UserAgent

from config import PROXY_CONFIG
from items.price_item import PriceItem

logger = logging.getLogger(__name__)


class Yt1998PriceSpider:
    name = 'yt1998_price'

    BASE_URL = 'https://www.yt1998.com'

    PRICE_LIST_URL = f'{BASE_URL}/jiage/'

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

    def _parse_price(self, text):
        if not text:
            return 0.0
        text = text.strip().replace(',', '').replace('，', '')
        match = re.search(r'[\d.]+', text)
        return float(match.group()) if match else 0.0

    def _parse_trend(self, text):
        if not text:
            return '稳'
        text = text.strip()
        if '涨' in text or '↑' in text:
            return '涨'
        elif '跌' in text or '↓' in text:
            return '跌'
        return '稳'

    def parse_price_page(self, html):
        items = []
        soup = BeautifulSoup(html, 'lxml')

        # TODO: 根据药通网实际页面结构调整以下选择器
        # 以下为框架代码，需根据实际 DOM 结构修改
        table = soup.find('table', class_='price-table') or soup.find('table')
        if not table:
            logger.warning('未找到价格表格，可能页面结构已变更或被反爬')
            return items

        rows = table.find_all('tr')[1:]
        for row in rows:
            cols = row.find_all('td')
            if len(cols) < 5:
                continue

            try:
                item = PriceItem()
                item.herb_name = cols[0].get_text(strip=True)
                item.spec = cols[1].get_text(strip=True) if len(cols) > 1 else ''
                item.origin = cols[2].get_text(strip=True) if len(cols) > 2 else ''
                item.market = cols[3].get_text(strip=True) if len(cols) > 3 else ''
                item.price = self._parse_price(cols[4].get_text(strip=True)) if len(cols) > 4 else 0.0
                item.trend = self._parse_trend(cols[5].get_text(strip=True)) if len(cols) > 5 else '稳'
                item.price_date = datetime.now().strftime('%Y-%m-%d')
                item.source = '药通网'
                items.append(item)
            except Exception as e:
                logger.error(f'解析行数据失败: {e}')
                continue

        return items

    def run(self):
        logger.info('=== 药通网价格爬虫启动 ===')
        all_items = []

        html = self._get_page(self.PRICE_LIST_URL)
        if not html:
            logger.error('获取药通网价格页面失败')
            return all_items

        items = self.parse_price_page(html)
        all_items.extend(items)
        logger.info(f'药通网价格爬取完成，共 {len(all_items)} 条')

        return all_items
