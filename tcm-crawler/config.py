DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': 'root',
    'database': 'tcm_market',
    'charset': 'utf8mb4',
}

SCHEDULE_CONFIG = {
    'price_crawler': {'trigger': 'cron', 'hour': '8,12,18', 'minute': '0'},
    'news_crawler': {'trigger': 'cron', 'hour': '*/2', 'minute': '0'},
}

PROXY_CONFIG = {
    'http': 'http://127.0.0.1:18080',
    'https': 'http://127.0.0.1:18080',
}
