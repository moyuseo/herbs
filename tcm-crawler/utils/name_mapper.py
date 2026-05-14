import json
import os
import logging

logger = logging.getLogger(__name__)

DEFAULT_NAME_MAPPING = {
    '田七': '三七',
    '生晒参': '人参',
    '红参': '人参',
    '高丽参': '人参',
    '西洋参': '西洋参',
    '党参': '党参',
    '黄芪': '黄芪',
    '当归': '当归',
    '川芎': '川芎',
    '白芍': '白芍',
    '赤芍': '赤芍',
    '生地': '地黄',
    '熟地': '地黄',
    '地黄': '地黄',
    '茯苓': '茯苓',
    '白术': '白术',
    '苍术': '苍术',
    '甘草': '甘草',
    '柴胡': '柴胡',
    '黄连': '黄连',
    '黄芩': '黄芩',
    '黄柏': '黄柏',
    '栀子': '栀子',
    '连翘': '连翘',
    '金银花': '金银花',
    '菊花': '菊花',
    '陈皮': '陈皮',
    '半夏': '半夏',
    '厚朴': '厚朴',
    '丹参': '丹参',
    '红花': '红花',
    '桃仁': '桃仁',
    '川贝母': '川贝母',
    '浙贝母': '浙贝母',
    '麦冬': '麦冬',
    '枸杞子': '枸杞子',
    '山药': '山药',
    '泽泻': '泽泻',
    '薏苡仁': '薏苡仁',
    '防风': '防风',
    '独活': '独活',
    '羌活': '羌活',
    '细辛': '细辛',
    '荆芥': '荆芥',
    '薄荷': '薄荷',
    '牛膝': '牛膝',
    '杜仲': '杜仲',
    '五味子': '五味子',
    '山茱萸': '山茱萸',
    '酸枣仁': '酸枣仁',
    '柏子仁': '柏子仁',
    '远志': '远志',
    '天麻': '天麻',
    '钩藤': '钩藤',
    '地龙': '地龙',
    '全蝎': '全蝎',
    '蜈蚣': '蜈蚣',
    '水蛭': '水蛭',
    '僵蚕': '僵蚕',
}

HERB_ID_MAP = {
    '三七': 1,
    '人参': 2,
    '西洋参': 3,
    '党参': 4,
    '黄芪': 5,
    '当归': 6,
    '川芎': 7,
    '白芍': 8,
    '赤芍': 9,
    '地黄': 10,
    '茯苓': 11,
    '白术': 12,
    '苍术': 13,
    '甘草': 14,
    '柴胡': 15,
    '黄连': 16,
    '黄芩': 17,
    '黄柏': 18,
    '栀子': 19,
    '连翘': 20,
    '金银花': 21,
    '菊花': 22,
    '陈皮': 23,
    '半夏': 24,
    '厚朴': 25,
    '丹参': 26,
    '红花': 27,
    '桃仁': 28,
    '川贝母': 29,
    '浙贝母': 30,
    '麦冬': 31,
    '枸杞子': 32,
    '山药': 33,
    '泽泻': 34,
    '薏苡仁': 35,
    '防风': 36,
    '独活': 37,
    '羌活': 38,
    '细辛': 39,
    '荆芥': 40,
    '薄荷': 41,
    '牛膝': 42,
    '杜仲': 43,
    '五味子': 44,
    '山茱萸': 45,
    '酸枣仁': 46,
    '柏子仁': 47,
    '远志': 48,
    '天麻': 49,
    '钩藤': 50,
    '地龙': 51,
    '全蝎': 52,
    '蜈蚣': 53,
    '水蛭': 54,
    '僵蚕': 55,
}

_json_mapping = None
_json_herb_ids = None


def _load_json_mapping():
    global _json_mapping, _json_herb_ids
    if _json_mapping is not None:
        return
    json_path = os.path.join(os.path.dirname(os.path.dirname(__file__)), 'herb_name_mapping.json')
    if os.path.exists(json_path):
        try:
            with open(json_path, 'r', encoding='utf-8') as f:
                data = json.load(f)
            _json_mapping = data.get('name_mapping', {})
            _json_herb_ids = data.get('herb_ids', {})
            logger.info(f'已加载外部名称映射文件: {json_path}')
        except Exception as e:
            logger.warning(f'加载外部名称映射文件失败: {e}')
            _json_mapping = {}
            _json_herb_ids = {}
    else:
        _json_mapping = {}
        _json_herb_ids = {}


def map_herb_name(raw_name):
    if not raw_name:
        return None
    raw_name = raw_name.strip()

    _load_json_mapping()

    if _json_mapping and raw_name in _json_mapping:
        standard_name = _json_mapping[raw_name]
    elif raw_name in DEFAULT_NAME_MAPPING:
        standard_name = DEFAULT_NAME_MAPPING[raw_name]
    else:
        standard_name = raw_name

    if _json_herb_ids and standard_name in _json_herb_ids:
        return _json_herb_ids[standard_name]

    return HERB_ID_MAP.get(standard_name, None)
