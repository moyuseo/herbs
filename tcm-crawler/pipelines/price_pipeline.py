import pymysql
import logging
from datetime import datetime

from config import DB_CONFIG
from utils.name_mapper import map_herb_name

logger = logging.getLogger(__name__)


class PricePipeline:
    def __init__(self):
        self.conn = None
        self.cursor = None

    def open_connection(self):
        self.conn = pymysql.connect(**DB_CONFIG)
        self.cursor = self.conn.cursor()

    def close_connection(self):
        if self.cursor:
            self.cursor.close()
        if self.conn:
            self.conn.close()

    def process_item(self, item):
        if not self.conn or not self.conn.open:
            self.open_connection()

        herb_id = map_herb_name(item.herb_name)
        if herb_id is None:
            logger.warning(f'未找到品种映射: {item.herb_name}，跳过入库')
            return False

        sql = """
            INSERT INTO price (herb_id, herb_name, spec, origin, market, price_type,
                               price, unit, trend, price_date, source, created_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE
                price = VALUES(price),
                trend = VALUES(trend),
                source = VALUES(source),
                updated_at = VALUES(created_at)
        """
        now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        params = (
            herb_id,
            item.herb_name,
            item.spec,
            item.origin,
            item.market,
            item.price_type,
            item.price,
            item.unit,
            item.trend,
            item.price_date,
            item.source,
            now,
        )
        try:
            self.cursor.execute(sql, params)
            self.conn.commit()
            logger.info(f'价格入库成功: {item.herb_name} {item.spec} {item.market} {item.price_date}')
            return True
        except Exception as e:
            self.conn.rollback()
            logger.error(f'价格入库失败: {e}')
            return False

    def process_items(self, items):
        self.open_connection()
        success_count = 0
        try:
            for item in items:
                if self.process_item(item):
                    success_count += 1
        finally:
            self.close_connection()
        logger.info(f'价格入库完成: 成功 {success_count} 条')
        return success_count
