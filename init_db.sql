CREATE DATABASE IF NOT EXISTS tcm_market DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tcm_market;

CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `phone` varchar(20) NOT NULL UNIQUE,
  `nickname` varchar(50) DEFAULT NULL,
  `avatar` varchar(200) DEFAULT '',
  `user_type` tinyint DEFAULT 1,
  `company` varchar(100) DEFAULT '',
  `membership_level` tinyint DEFAULT 0,
  `membership_expire` datetime DEFAULT NULL,
  `status` tinyint DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `herb_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `parent_id` bigint DEFAULT 0,
  `sort_order` int DEFAULT 0,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `herb` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `alias` varchar(255) DEFAULT '',
  `pinyin` varchar(100) DEFAULT '',
  `category_id` bigint DEFAULT NULL,
  `medicinal_part` varchar(100) DEFAULT '',
  `nature_flavor` varchar(255) DEFAULT '',
  `meridian_tropism` varchar(255) DEFAULT '',
  `efficacy` text,
  `indication` text,
  `description` text,
  `status` tinyint DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `price` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `herb_id` bigint DEFAULT NULL,
  `spec` varchar(100) DEFAULT '',
  `origin` varchar(100) DEFAULT '',
  `market` varchar(100) DEFAULT '',
  `price_type` tinyint DEFAULT 1,
  `price` decimal(10,2) DEFAULT NULL,
  `unit` varchar(20) DEFAULT '公斤',
  `trend` varchar(20) DEFAULT 'stable',
  `price_date` date DEFAULT NULL,
  `source` varchar(255) DEFAULT '',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `price_index` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `index_date` date DEFAULT NULL,
  `index_type` varchar(50) DEFAULT 'composite',
  `index_value` decimal(10,4) DEFAULT NULL,
  `change_rate` decimal(10,4) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `price_alert` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `herb_id` bigint DEFAULT NULL,
  `condition_type` tinyint DEFAULT 1,
  `threshold` decimal(10,2) DEFAULT NULL,
  `is_active` tinyint DEFAULT 1,
  `last_triggered` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `news` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `content` longtext,
  `summary` varchar(500) DEFAULT '',
  `cover_image` varchar(255) DEFAULT '',
  `category` varchar(50) DEFAULT '',
  `tags` varchar(255) DEFAULT '',
  `author` varchar(100) DEFAULT '',
  `source` varchar(100) DEFAULT '',
  `view_count` int DEFAULT 0,
  `is_top` tinyint DEFAULT 0,
  `status` tinyint DEFAULT 1,
  `published_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `supply` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT 1,
  `herb_id` bigint DEFAULT NULL,
  `spec` varchar(100) DEFAULT '',
  `origin` varchar(100) DEFAULT '',
  `quantity` decimal(10,2) DEFAULT NULL,
  `unit` varchar(20) DEFAULT '公斤',
  `price_type` tinyint DEFAULT 1,
  `price` decimal(10,2) DEFAULT NULL,
  `contact_name` varchar(100) DEFAULT '',
  `contact_phone` varchar(20) DEFAULT '',
  `images` varchar(500) DEFAULT '',
  `description` text,
  `is_top` tinyint DEFAULT 0,
  `expire_at` datetime DEFAULT NULL,
  `status` tinyint DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `demand` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT 1,
  `herb_id` bigint DEFAULT NULL,
  `spec` varchar(100) DEFAULT '',
  `quantity` decimal(10,2) DEFAULT NULL,
  `unit` varchar(20) DEFAULT '公斤',
  `delivery_address` varchar(255) DEFAULT '',
  `quote_count` int DEFAULT 0,
  `expire_at` datetime DEFAULT NULL,
  `description` text,
  `status` tinyint DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `user_watchlist` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `herb_id` bigint DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 测试用户
INSERT IGNORE INTO `user` (id, phone, nickname, user_type, membership_level, status) VALUES
(1, '13800138000', '测试用户', 1, 1, 1);

-- 药材分类
INSERT INTO `herb_category` (id, name, sort_order) VALUES
(1, '根茎类', 1), (2, '果实类', 2), (3, '全草类', 3),
(4, '花类', 4), (5, '叶类', 5), (6, '树皮类', 6),
(7, '藤木类', 7), (8, '树脂类', 8), (9, '菌藻类', 9),
(10, '动物类', 10), (11, '矿物类', 11), (12, '其他类', 12);

-- 药材
INSERT INTO `herb` (id, name, alias, pinyin, category_id, medicinal_part, nature_flavor, meridian_tropism, efficacy, indication, description) VALUES
(1, '三七', '田七、金不换', 'sanqi', 1, '根及根茎', '甘、微苦，温', '肝、胃经', '散瘀止血，消肿定痛', '咯血，吐血，衄血，便血，崩漏，外伤出血，胸腹刺痛，跌扑肿痛', '三七为五加科植物三七的干燥根和根茎。主产于云南、广西等地。秋季花开前采挖，洗净，分开主根、支根及根茎，干燥。'),
(2, '当归', '秦归、云归', 'danggui', 1, '根', '甘、辛，温', '肝、心、脾经', '补血活血，调经止痛，润肠通便', '血虚萎黄，眩晕心悸，月经不调，经闭痛经，虚寒腹痛，风湿痹痛，跌扑损伤，痈疽疮疡，肠燥便秘', '当归为伞形科植物当归的干燥根。主产于甘肃、云南、四川等地。秋末采挖，除去须根及泥沙，待水分稍蒸发后，捆成小把，上棚，用烟火慢慢熏干。'),
(3, '黄芪', '绵芪', 'huangqi', 1, '根', '甘，微温', '脾、肺经', '补气升阳，固表止汗，利水消肿，生津养血，行滞通痹，托毒排脓，敛疮生肌', '气虚乏力，食少便溏，中气下陷，久泻脱肛，便血崩漏，表虚自汗，气虚水肿，内热消渴，血虚萎黄，半身不遂，痹痛麻木，痈疽难溃，久溃不敛', '黄芪为豆科植物蒙古黄芪或膜荚黄芪的干燥根。主产于山西、甘肃、黑龙江等地。春、秋二季采挖，除去须根及根头，晒干。'),
(4, '党参', '潞党、台党', 'dangshen', 1, '根', '甘，平', '脾、肺经', '健脾益肺，养血生津', '脾肺气虚，食少倦怠，咳嗽虚喘，气血不足，面色萎黄，心悸气短，津伤口渴，内热消渴', '党参为桔梗科植物党参的干燥根。主产于山西、陕西、甘肃等地。秋季采挖，洗净，晒干。'),
(5, '川芎', '芎藭', 'chuanxiong', 1, '根茎', '辛，温', '肝、胆、心包经', '活血行气，祛风止痛', '胸痹心痛，胸胁刺痛，跌扑肿痛，月经不调，经闭痛经，癥瘕腹痛，头痛，风湿痹痛', '川芎为伞形科植物川芎的干燥根茎。主产于四川等地。夏季当茎上的节盘显著突出，并略带紫色时采挖，除去泥沙，晒后烘干，再去须根。'),
(6, '枸杞子', '枸杞红实', 'gouqizi', 2, '果实', '甘，平', '肝、肾经', '滋补肝肾，益精明目', '虚劳精亏，腰膝酸痛，眩晕耳鸣，阳萎遗精，内热消渴，血虚萎黄，目昏不明', '枸杞子为茄科植物宁夏枸杞的干燥成熟果实。主产于宁夏、甘肃、青海等地。夏、秋二季果实呈红色时采收，热风烘干，除去果梗，或晾至皮皱后，晒干，除去果梗。'),
(7, '金银花', '忍冬花', 'jinyinhua', 4, '花蕾', '甘，寒', '肺、心、胃经', '清热解毒，疏散风热', '痈肿疔疮，喉痹，丹毒，热毒血痢，风热感冒，温病发热', '金银花为忍冬科植物忍冬的干燥花蕾或带初开的花。主产于山东、河南等地。夏初花开放前采收，干燥。'),
(8, '茯苓', '云苓', 'fuling', 9, '菌核', '甘、淡，平', '心、肺、脾、肾经', '利水渗湿，健脾，宁心', '水肿尿少，痰饮眩悸，脾虚食少，便溏泄泻，心神不安，惊悸失眠', '茯苓为多孔菌科真菌茯苓的干燥菌核。主产于云南、安徽、湖北等地。多于7-9月采挖，挖出后除去泥沙，堆置"发汗"后，摊开晾至表面干燥，再"发汗"，反复数次至现皱纹、内部水分大部散失后，阴干。'),
(9, '白芍', '金芍药', 'baishao', 1, '根', '苦、酸，微寒', '肝、脾经', '养血调经，敛阴止汗，柔肝止痛，平抑肝阳', '血虚萎黄，月经不调，自汗，盗汗，胁痛，腹痛，四肢挛痛，头痛眩晕', '白芍为毛茛科植物芍药的干燥根。主产于浙江、安徽、四川等地。夏、秋二季采挖，洗净，除去头尾及细根，置沸水中煮后除去外皮或去皮后再煮，晒干。'),
(10, '丹参', '血参', 'danshen', 1, '根及根茎', '苦，微寒', '心、肝经', '活血祛瘀，通经止痛，清心除烦，凉血消痈', '胸痹心痛，脘腹胁痛，癥瘕积聚，热痹疼痛，心烦不眠，月经不调，痛经经闭，疮疡肿痛', '丹参为唇形科植物丹参的干燥根及根茎。主产于四川、山东、河南等地。春、秋二季采挖，除去泥沙，干燥。'),
(11, '人参', '棒槌', 'rencan', 1, '根', '甘、微苦，微温', '脾、肺、心、肾经', '大补元气，复脉固脱，补脾益肺，生津养血，安神益智', '体虚欲脱，肢冷脉微，脾虚食少，肺虚喘咳，津伤口渴，内热消渴，气血亏虚，久病虚羸，惊悸失眠，阳痿宫冷', '人参为五加科植物人参的干燥根和根茎。主产于吉林、辽宁、黑龙江等地。多于秋季采挖，洗净后经晒干或烘干。'),
(12, '半夏', '地文', 'banxia', 1, '块茎', '辛，温', '脾、胃、肺经', '燥湿化痰，降逆止呕，消痞散结', '湿痰寒痰，咳喘痰多，痰饮眩悸，风痰眩晕，痰厥头痛，呕吐反胃，胸脘痞闷，梅核气', '半夏为天南星科植物半夏的干燥块茎。主产于四川、湖北、河南等地。夏、秋二季采挖，洗净，除去外皮及须根，晒干。'),
(13, '陈皮', '橘皮', 'chenpi', 2, '果皮', '苦、辛，温', '脾、肺经', '理气健脾，燥湿化痰', '脘腹胀满，食少吐泻，咳嗽痰多', '陈皮为芸香科植物橘及其栽培变种的干燥成熟果皮。主产于广东、福建、四川等地。采摘成熟果实，剥取果皮，晒干或低温干燥。'),
(14, '柴胡', '地熏', 'chaihu', 3, '全草', '辛、苦，微寒', '肝、胆、肺经', '和解表里，疏肝升阳', '感冒发热，寒热往来，胸胁胀痛，月经不调，子宫脱垂，脱肛', '柴胡为伞形科植物柴胡或狭叶柴胡的干燥根。主产于河北、河南、辽宁等地。春、秋二季采挖，除去茎叶及泥沙，干燥。'),
(15, '板蓝根', '靛青根', 'banlangen', 1, '根', '苦，寒', '心、胃经', '清热解毒，凉血利咽', '温毒发斑，舌绛紫暗，痄腮，喉痹，烂喉丹痧，大头瘟疫，丹毒，痈肿', '板蓝根为十字花科植物菘蓝的干燥根。主产于河北、江苏、安徽等地。秋季采挖，除去泥沙，晒干。'),
(16, '甘草', '国老', 'gancao', 1, '根及根茎', '甘，平', '心、肺、脾、胃经', '补脾益气，清热解毒，祛痰止咳，缓急止痛，调和诸药', '脾胃虚弱，倦怠乏力，心悸气短，咳嗽痰多，脘腹四肢挛急疼痛，痈肿疮毒，缓解药物毒性', '甘草为豆科植物甘草的干燥根和根茎。主产于内蒙古、甘肃、新疆等地。春、秋二季采挖，除去须根，晒干。'),
(17, '黄连', '王连', 'huanglian', 1, '根茎', '苦，寒', '心、脾、胃、肝、胆、大肠经', '清热燥湿，泻火解毒', '湿热痞满，呕吐吞酸，泻痢，黄疸，高热神昏，心火亢盛，心烦不寐，心悸不宁，血热吐衄，目赤，牙痛，消渴，痈肿疔疮', '黄连为毛茛科植物黄连的干燥根茎。主产于四川、湖北、云南等地。秋季采挖，除去须根及泥沙，干燥，撞去残留须根。'),
(18, '川贝母', '贝母', 'chuanbeimu', 1, '鳞茎', '苦、甘，微寒', '肺、心经', '清热润肺，化痰止咳，散结消痈', '肺热燥咳，干咳少痰，阴虚劳嗽，痰中带血，瘰疬，乳痈，肺痈', '川贝母为百合科植物川贝母的干燥鳞茎。主产于四川、云南、西藏等地。夏、秋二季或积雪融化后采挖，除去须根、粗皮及泥沙，晒干或低温干燥。'),
(19, '红花', '草红花', 'honghua', 4, '花', '辛，温', '心、肝经', '活血通经，散瘀止痛', '经闭，痛经，恶露不行，癥瘕痞块，胸痹心痛，瘀滞腹痛，胸胁刺痛，跌扑损伤，疮疡肿痛', '红花为菊科植物红花的干燥花。主产于新疆、河南、四川等地。夏季花冠由黄变红时采摘，阴干或晒干。'),
(20, '连翘', '黄花条', 'lianqiao', 2, '果实', '苦，微寒', '肺、心、小肠经', '清热解毒，消肿散结，疏散风热', '痈疽，瘰疬，乳痈，丹毒，风热感冒，温病初起，温热入营，高热烦渴，神昏发斑，热淋涩痛', '连翘为木犀科植物连翘的干燥果实。主产于山西、河南、陕西等地。秋季果实初熟尚带绿色时采收，除去杂质，蒸熟，晒干，习称"青翘"；果实熟透时采收，晒干，除去杂质，习称"老翘"。'),
(21, '白术', '冬白术', 'baizhu', 1, '根茎', '苦、甘，温', '脾、胃经', '健脾益气，燥湿利水，止汗，安胎', '脾虚食少，腹胀泄泻，痰饮眩悸，水肿，自汗，胎动不安', '白术为菊科植物白术的干燥根茎。主产于浙江、安徽、湖南等地。冬季下部叶枯黄、上部叶变脆时采挖，除去泥沙，烘干或晒干，再除去须根。'),
(22, '地黄', '生地', 'dihuang', 1, '根', '甘，寒', '心、肝、肾经', '清热凉血，养阴生津', '热入营血，温毒发斑，吐血衄血，热病伤阴，舌绛烦渴，津伤便秘，阴虚发热，骨蒸劳热，内热消渴', '地黄为玄参科植物地黄的干燥根。主产于河南、河北、山东等地。秋季采挖，除去芦头、须根及泥沙，鲜用或干燥。'),
(23, '麦冬', '麦门冬', 'maidong', 1, '块根', '甘、微苦，微寒', '心、肺、胃经', '养阴生津，润肺清心', '肺燥干咳，阴虚痨嗽，喉痹咽痛，津伤口渴，内热消渴，心烦失眠，肠燥便秘', '麦冬为百合科植物麦冬的干燥块根。主产于浙江、四川、江苏等地。夏季采挖，洗净，反复暴晒、堆置，至七八成干，除去须根，干燥。'),
(24, '防风', '屏风', 'fangfeng', 1, '根', '辛、甘，微温', '膀胱、肝、脾经', '祛风解表，胜湿止痛，止痉', '感冒头痛，风湿痹痛，风疹瘙痒，破伤风', '防风为伞形科植物防风的干燥根。主产于东北、内蒙古、河北等地。春、秋二季采挖未抽花茎植株的根，除去须根及泥沙，晒干。'),
(25, '薄荷', '银丹草', 'bohe', 3, '全草', '辛，凉', '肺、肝经', '疏散风热，清利头目，利咽，透疹，疏肝行气', '风热感冒，风温初起，头痛，目赤，喉痹，口疮，风疹，麻疹，胸胁胀闷', '薄荷为唇形科植物薄荷的干燥地上部分。主产于江苏、浙江、湖南等地。夏、秋二季茎叶茂盛或花开至三轮时，选晴天，分次采割，晒干或阴干。'),
(26, '藿香', '土藿香', 'huoxiang', 3, '全草', '辛，微温', '脾、胃、肺经', '芳香化浊，和中止呕，发表解暑', '湿浊中阻，脘痞呕吐，暑湿表证，湿温初起，发热倦怠，胸闷不舒，寒湿闭暑，腹痛吐泻，鼻渊头痛', '藿香为唇形科植物广藿香的干燥地上部分。主产于广东、海南等地。枝叶茂盛时采割，日晒夜闷，反复至干。'),
(27, '杜仲', '丝连皮', 'duzhong', 6, '树皮', '甘，温', '肝、肾经', '补肝肾，强筋骨，安胎', '肝肾不足，腰膝酸痛，筋骨无力，头晕目眩，妊娠漏血，胎动不安', '杜仲为杜仲科植物杜仲的干燥树皮。主产于贵州、四川、陕西等地。4-6月剥取，刮去粗皮，堆置"发汗"至内皮呈紫褐色，晒干。'),
(28, '厚朴', '赤朴', 'houpo', 6, '树皮', '苦、辛，温', '脾、胃、肺、大肠经', '燥湿消痰，下气除满', '湿滞伤中，脘痞吐泻，食积气滞，腹胀便秘，痰饮喘咳', '厚朴为木兰科植物厚朴的干燥干皮、根皮及枝皮。主产于四川、湖北、浙江等地。4-6月剥取，根皮及枝皮直接阴干；干皮置沸水中微煮后，堆置阴湿处，"发汗"至内表面变紫褐色或棕褐色时，蒸软，取出，卷成筒状，干燥。'),
(29, '酸枣仁', '枣仁', 'suanzaoren', 2, '种子', '甘、酸，平', '肝、胆、心经', '养心补肝，宁心安神，敛汗，生津', '虚烦不眠，惊悸多梦，体虚多汗，津伤口渴', '酸枣仁为鼠李科植物酸枣的干燥成熟种子。主产于河北、陕西、辽宁等地。秋末冬初采收成熟果实，除去果肉及核壳，收集种子，晒干。'),
(30, '决明子', '草决明', 'juemingzi', 2, '种子', '甘、苦、咸，微寒', '肝、大肠经', '清热明目，润肠通便', '目赤涩痛，羞明多泪，头痛眩晕，目暗不明，大便秘结', '决明子为豆科植物决明或小决明的干燥成熟种子。主产于安徽、广西、四川等地。秋季采收成熟果实，晒干，打下种子，除去杂质。');

-- 价格数据
INSERT INTO `price` (herb_id, spec, origin, market, price_type, price, unit, trend, price_date) VALUES
(1, '120头', '云南', '亳州', 1, 135.00, '公斤', 'up', '2026-05-14'),
(1, '80头', '云南', '亳州', 1, 185.00, '公斤', 'up', '2026-05-14'),
(1, '无数头', '云南', '亳州', 1, 105.00, '公斤', 'stable', '2026-05-14'),
(2, '统货', '甘肃', '亳州', 1, 58.00, '公斤', 'down', '2026-05-14'),
(2, '选货', '甘肃', '亳州', 1, 82.00, '公斤', 'stable', '2026-05-14'),
(3, '统货', '甘肃', '亳州', 1, 22.00, '公斤', 'up', '2026-05-14'),
(3, '选货', '内蒙古', '亳州', 1, 35.00, '公斤', 'up', '2026-05-14'),
(4, '统货', '甘肃', '亳州', 1, 48.00, '公斤', 'stable', '2026-05-14'),
(5, '统货', '四川', '亳州', 1, 32.00, '公斤', 'down', '2026-05-14'),
(6, '宁夏统', '宁夏', '亳州', 1, 52.00, '公斤', 'up', '2026-05-14'),
(7, '统货', '山东', '亳州', 1, 165.00, '公斤', 'stable', '2026-05-14'),
(8, '白块', '云南', '亳州', 1, 28.00, '公斤', 'stable', '2026-05-14'),
(9, '统货', '浙江', '亳州', 1, 25.00, '公斤', 'down', '2026-05-14'),
(10, '统货', '四川', '亳州', 1, 18.00, '公斤', 'up', '2026-05-14'),
(11, '生晒参', '吉林', '亳州', 1, 320.00, '公斤', 'up', '2026-05-14'),
(12, '统货', '四川', '亳州', 1, 95.00, '公斤', 'stable', '2026-05-14'),
(13, '广陈皮', '广东', '亳州', 1, 450.00, '公斤', 'up', '2026-05-14'),
(14, '统货', '河北', '亳州', 1, 68.00, '公斤', 'down', '2026-05-14'),
(15, '统货', '河北', '亳州', 1, 22.00, '公斤', 'stable', '2026-05-14'),
(16, '统货', '内蒙古', '亳州', 1, 16.00, '公斤', 'stable', '2026-05-14'),
(17, '味连', '四川', '亳州', 1, 145.00, '公斤', 'up', '2026-05-14'),
(18, '松贝', '四川', '亳州', 1, 3800.00, '公斤', 'stable', '2026-05-14'),
(19, '统货', '新疆', '亳州', 1, 85.00, '公斤', 'down', '2026-05-14'),
(20, '青翘', '山西', '亳州', 1, 62.00, '公斤', 'up', '2026-05-14'),
(1, '120头', '云南', '安国', 1, 132.00, '公斤', 'up', '2026-05-14'),
(2, '统货', '甘肃', '安国', 1, 55.00, '公斤', 'stable', '2026-05-14'),
(3, '统货', '甘肃', '安国', 1, 21.00, '公斤', 'up', '2026-05-14'),
(6, '宁夏统', '宁夏', '安国', 1, 50.00, '公斤', 'up', '2026-05-14'),
(1, '120头', '云南', '成都', 1, 138.00, '公斤', 'up', '2026-05-14'),
(2, '统货', '甘肃', '成都', 1, 60.00, '公斤', 'down', '2026-05-14'),
(1, '120头', '云南', '玉林', 1, 130.00, '公斤', 'stable', '2026-05-14'),
(1, '120头', '云南', '廉桥', 1, 128.00, '公斤', 'stable', '2026-05-14'),
(1, '120头', '云南', '普宁', 1, 126.00, '公斤', 'down', '2026-05-14');

-- 价格指数（30天数据）
DROP PROCEDURE IF EXISTS fill_price_index;
DELIMITER //
CREATE PROCEDURE fill_price_index()
BEGIN
  DECLARE i INT DEFAULT 0;
  DECLARE v_date DATE;
  DECLARE v_value DECIMAL(10,4) DEFAULT 1200;
  WHILE i < 30 DO
    SET v_date = DATE_SUB('2026-05-14', INTERVAL (29-i) DAY);
    SET v_value = v_value + (RAND() - 0.45) * 5;
    INSERT INTO price_index (index_date, index_type, index_value, change_rate) VALUES
    (v_date, 'composite', v_value, (RAND() - 0.45) * 0.01),
    (v_date, 'root', v_value * 0.95, (RAND() - 0.45) * 0.01),
    (v_date, 'fruit', v_value * 1.02, (RAND() - 0.45) * 0.01),
    (v_date, 'flower', v_value * 0.88, (RAND() - 0.45) * 0.01),
    (v_date, 'herb', v_value * 0.92, (RAND() - 0.45) * 0.01);
    SET i = i + 1;
  END WHILE;
END //
DELIMITER ;
CALL fill_price_index();
DROP PROCEDURE fill_price_index;

-- 资讯
INSERT INTO `news` (title, content, summary, category, author, source, view_count, is_top, status, published_at) VALUES
('2026年5月中药材市场行情综述', '5月份中药材市场整体呈现稳中有升的态势。综合200指数持续走高，较上月上涨0.52%。其中根茎类药材涨幅明显，三七、黄芪、当归等品种价格均有不同程度上涨。果实类药材表现平稳，枸杞子、陈皮等品种价格基本持平。花类药材中金银花价格有所回落，红花价格小幅上涨。\n\n从市场供需来看，春季为中药材传统销售旺季，下游需求持续释放。同时，部分药材产区受天气影响，产量有所下降，推动价格上涨。预计短期内市场将继续保持温和上涨态势。', '5月份中药材市场整体稳中有升，综合指数上涨0.52%', '药市动态', '编辑部', '中药材天地网', 1256, 1, 1, '2026-05-14 10:00:00'),
('三七价格持续走高 产区货源偏紧', '近期三七价格持续走高，120头三七亳州市场报价135元/公斤，较上月上涨8%。据了解，云南主产区受前期干旱影响，新货产量有所减少，加之陈货库存消化殆尽，市场货源偏紧。商家惜售心理增强，进一步推高价格。\n\n业内人士分析，短期内三七价格仍有上涨空间，但需关注后期产区天气变化及新货上市情况。建议采购方合理安排进货节奏，避免追高。', '三七120头亳州市场135元/公斤，较上月上涨8%', '品种分析', '张明', '药通网', 892, 0, 1, '2026-05-13 15:30:00'),
('国家中医药管理局发布2026年中药材种植规划', '国家中医药管理局近日发布《2026年中药材种植产业发展规划》，提出到2026年底，全国中药材种植面积稳定在5600万亩以上，标准化种植率达到45%以上。规划强调要加强道地药材生产基地建设，推进中药材种植绿色发展。\n\n规划还提出，将加大对中药材种业创新的支持力度，培育一批具有自主知识产权的优良品种。同时，完善中药材质量追溯体系，确保中药材质量安全。', '国家发布中药材种植规划，种植面积将稳定在5600万亩以上', '集采资讯', '李华', '中国中医药报', 2341, 0, 1, '2026-05-12 09:00:00'),
('黄芪产新在即 行情如何演绎', '黄芪作为大宗常用药材，年需求量巨大。目前甘肃、内蒙古等主产区即将进入产新期，市场关注度持续升温。当前黄芪统货亳州市场报价22元/公斤，较年初上涨15%。\n\n从供应端看，今年黄芪种植面积较去年有所增加，但部分产区受旱情影响，单产可能下降。需求端保持稳定增长，出口订单也有所增加。预计产新后价格将有所回调，但幅度有限。', '黄芪统货22元/公斤较年初涨15%，产新后价格或小幅回调', '品种分析', '王芳', '中药材天地网', 678, 0, 1, '2026-05-11 14:00:00'),
('2026年第一批中药材集采结果公布', '2026年第一批中药材集中采购结果今日公布，本次集采涉及党参、当归、黄芪等20个品种，平均降价幅度12.6%。其中党参降幅最大，达到18.3%。\n\n本次集采吸引了全国300余家中药企业参与，采购总量超过5万吨。业内人士表示，集采有利于降低中药企业原料成本，推动中药材市场规范化发展。但同时也需关注集采对种植户收益的影响。', '第一批中药材集采平均降价12.6%，党参降幅最大达18.3%', '集采资讯', '赵刚', '医药经济报', 3456, 0, 1, '2026-05-10 11:00:00'),
('金银花产新量增 价格承压下行', '目前山东、河南等金银花主产区已进入产新期，今年产量较去年增加约15%。受供应增加影响，金银花价格持续走低，统货亳州市场报价165元/公斤，较上月下跌5%。\n\n业内人士预计，随着产新推进，金银花价格仍有下行压力。但考虑到人工采摘成本上升，跌幅有限。建议种植户合理安排采摘和销售节奏。', '金银花统货165元/公斤较上月下跌5%，产量增加15%', '品种分析', '刘洋', '药通网', 534, 0, 1, '2026-05-09 16:00:00'),
('中药材出口持续增长 一季度出口额同比增长8.5%', '据海关统计，2026年第一季度我国中药材出口额达12.3亿美元，同比增长8.5%。其中，人参、枸杞、当归等品种出口量位居前列。东南亚和日韩仍是最主要的出口市场。\n\n业内人士分析，中医药在海外认可度持续提升，是中药材出口增长的主要驱动力。同时，"一带一路"沿线国家对中药材的需求也在快速增长。', '一季度中药材出口额12.3亿美元，同比增长8.5%', '药市动态', '陈静', '中国医药报', 1890, 0, 1, '2026-05-08 10:30:00'),
('当归价格回调 后市如何操作', '当归统货亳州市场报价58元/公斤，较上月下跌3%。前期当归价格涨幅较大，近期获利盘出逃导致价格回调。从基本面看，当归库存仍处于偏低水平，甘肃产区新货尚未大量上市。\n\n建议商家关注产区天气及新货上市情况，短期内不宜盲目追跌。对于有刚性需求的采购方，可适当补货。', '当归统货58元/公斤较上月下跌3%，库存仍偏低', '品种分析', '周磊', '中药材天地网', 723, 0, 1, '2026-05-07 13:00:00'),
('2026年全国中药材采购招标信息汇总（5月）', '2026年5月全国中药材采购招标信息汇总，本期共收集整理有效招标信息86条，涉及28个省区市。其中，医院采购占比42%，药企采购占比35%，其他占比23%。\n\n招标数量前五的品种依次为：黄芪、当归、党参、白术、甘草。招标金额最大的项目为某省中医院年度中药材采购项目，预算金额超过2000万元。', '5月共收集86条招标信息，涉及28个省区市', '采购招标', '编辑部', '中药材天地网', 1567, 0, 1, '2026-05-06 09:00:00'),
('川芎价格走低 产区货源充足', '川芎统货亳州市场报价32元/公斤，较上月下跌4%。四川主产区货源充足，商家出货积极，市场走销一般。预计短期内川芎价格仍将偏弱运行。', '川芎统货32元/公斤较上月下跌4%，产区货源充足', '品种分析', '张明', '药通网', 445, 0, 1, '2026-05-05 15:00:00');

-- 供应信息
INSERT INTO `supply` (user_id, herb_id, spec, origin, quantity, unit, price_type, price, contact_name, contact_phone, description, status, created_at) VALUES
(1, 1, '120头', '云南文山', 500, '公斤', 1, 132.00, '李经理', '13900139001', '云南文山产地直供，质量保证，可提供检测报告', 1, '2026-05-14 09:00:00'),
(1, 2, '统货', '甘肃岷县', 1000, '公斤', 1, 55.00, '王经理', '13900139002', '甘肃岷县当归，产地直发，量大从优', 1, '2026-05-13 14:00:00'),
(1, 3, '统货', '甘肃陇西', 2000, '公斤', 1, 21.00, '张经理', '13900139003', '黄芪统货，片型好，色泽佳', 1, '2026-05-12 10:00:00'),
(1, 6, '宁夏统', '宁夏中宁', 800, '公斤', 1, 50.00, '赵经理', '13900139004', '宁夏中宁枸杞，颗粒饱满，糖分足', 1, '2026-05-11 16:00:00'),
(1, 8, '白块', '云南普洱', 1500, '公斤', 2, NULL, '刘经理', '13900139005', '茯苓白块，产地直销，价格电议', 1, '2026-05-10 11:00:00'),
(1, 10, '统货', '四川中江', 600, '公斤', 1, 17.50, '陈经理', '13900139006', '丹参统货，含量达标，可开票', 1, '2026-05-09 08:30:00');

-- 求购信息
INSERT INTO `demand` (user_id, herb_id, spec, quantity, unit, delivery_address, description, status, created_at) VALUES
(1, 1, '120头', 300, '公斤', '安徽省亳州市', '急购三七120头，要求云南产地货，含量达标', 1, '2026-05-14 10:00:00'),
(1, 4, '统货', 500, '公斤', '河北省安国市', '求购党参统货，甘肃产地优先', 1, '2026-05-13 15:00:00'),
(1, 7, '统货', 200, '公斤', '山东省平邑县', '求购金银花统货，色绿条匀', 1, '2026-05-12 09:00:00'),
(1, 11, '生晒参', 100, '公斤', '吉林省抚松县', '求购生晒参，吉林产地，规格齐全', 1, '2026-05-11 14:00:00'),
(1, 16, '统货', 2000, '公斤', '安徽省亳州市', '大量求购甘草统货，内蒙古产地优先', 1, '2026-05-10 11:00:00'),
(1, 18, '松贝', 50, '公斤', '四川省成都市', '求购川贝母松贝，要求四川产地，含量合格', 1, '2026-05-09 16:00:00');
