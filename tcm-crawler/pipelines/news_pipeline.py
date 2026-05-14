import pymysql
import logging
from datetime import datetime

from config import DB_CONFIG

logger = logging.getLogger(__name__)


class NewsPipeline:
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

        sql = """
            INSERT INTO news (title, content, summary, cover_image, category,
                              tags, author, source, published_at, created_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE
                content = VALUES(content),
                summary = VALUES(summary),
                cover_image = VALUES(cover_image),
                tags = VALUES(tags),
                updated_at = VALUES(created_at)
        """
        now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        params = (
            item.title,
            item.content,
            item.summary,
            item.cover_image,
            item.category,
            item.tags,
            item.author,
            item.source,
            item.published_at,
            now,
        )
        try:
            self.cursor.execute(sql, params)
            self.conn.commit()
            logger.info(f'资讯入库成功: {item.title}')
            return True
        except Exception as e:
            self.conn.rollback()
            logger.error(f'资讯入库失败: {e}')
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
        logger.info(f'资讯入库完成: 成功 {success_count} 条')
        return success_count
