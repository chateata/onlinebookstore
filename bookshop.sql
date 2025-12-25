/*
 Navicat MySQL Data Transfer

 Source Server         : xing
 Source Server Type    : MySQL
 Source Server Version : 50562
 Source Host           : localhost:3306
 Source Schema         : bookshop

 Target Server Type    : MySQL
 Target Server Version : 50562
 File Encoding         : 65001

 Date: 21/07/2022 21:23:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for book
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book`  (
  `book_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '书籍编号',
  `category_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '书籍分类代码',
  `book_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '书籍名称',
  `isbn` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'ISBN',
  `author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '作者',
  `press` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '出版社',
  `pub_date` date NOT NULL COMMENT '出版日期',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '书籍图片',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '书籍描述',
  `price` decimal(10, 2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '书籍单价',
  `stock` int(10) UNSIGNED NOT NULL COMMENT '书籍库存',
  `create_time` datetime NOT NULL COMMENT '上架时间',
  PRIMARY KEY (`book_id`) USING BTREE,
  UNIQUE INDEX `book_id`(`book_id`) USING BTREE,
  INDEX `category_code`(`category_code`) USING BTREE,
  CONSTRAINT `book_ibfk_1` FOREIGN KEY (`category_code`) REFERENCES `category` (`category_code`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1037 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of book
-- ----------------------------
INSERT INTO `book` VALUES (1001, 'novel', '失乐园', '9787555257035', '渡边淳一[著]  林少华[译]', '青岛出版社', '2017-11-01', '25182491-1_l_6.jpg', '渡边淳一代表作，长期雄踞日本畅销书排行榜榜首，由黑木瞳、役所广司主演的电影引发“失乐园”热。此次的全新林少华译本，将带你体味不一样的渡边淳一，不一样的失乐园。', 45.00, 99, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1002, 'novel', '假面之夜+假面饭店+假面前夜', '25286485', '东野圭吾', '南海出版公司', '2018-06-01', '25286485-1_l_2.jpg', '新系列新CP。富丽堂皇的五星级大酒店，各怀心事的客人。平静的表面下暗流汹涌。杀人凶手即将在此现身。新田尚美联手揭开假面。', 115.90, 98, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1003, 'novel', '布隆夫曼脱单历险记', '9787559431394', '[美]丹尼尔·华莱士 著 宁蒙 译 时代华语', '江苏凤凰文艺出版社', '2019-04-01', '26916949-1_l_6.jpg', '为什么不结婚？为什么想恋爱却不主动？单身人士，可能是对自我和世界有着独特认知，对他们来说，脱单之旅，不止是一次成长冒险，更是与原生家庭的和解，与人类社会的碰撞。单身族群，比起伴侣，更需要的是找到自己。', 35.80, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1004, 'novel', '光与影', '9787555269397', '渡边淳一', '青岛出版社', '2018-05-25', '25283531-1_l_3.jpg', '渡边淳一文学的原点之作 日本文学奖直木奖获奖作品 关于命运和与人性 关于死亡与热爱 关于病痛与尊严 关于爱情与复仇', 32.00, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1005, 'novel', '三体：全三册', '23579654', '刘慈欣', '重庆出版社', '2010-11-01', '23579654-1_l_3.jpg', '刘慈欣代表作，亚洲首部“雨果奖”获奖作品！', 55.80, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1006, 'novel', '鲛在水中央', '9787540490645', '孙频', '湖南文艺出版社', '2017-08-01', '27855831-1_l_4.jpg', '阎连科、韩少功、苏童鼎力推荐。这个人世间，有谁不是在努力地活着。这是一本让你流着泪读完的书！那些孤独的、无奈的却又不向命运低头的人，都是生活的勇者。', 42.70, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1007, 'manage', '彼得·林奇点评版股票作手回忆录', '9787515303628', '[美]杰西·利弗莫尔', '中国青年出版社', '2019-05-01', '27855149-1_l_3.jpg', ' 利弗莫尔，从5元本金到1亿资本额，这是每代股神都难以忽略的股市传奇。《彼得林奇点评版股票作手回忆录》是由彼得林奇点评，凯恩斯作序的经典之作', 32.20, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1008, 'manage', '财务自由之路（全三册）', '26511903', '[德]博多·舍费尔', '现代出版社', '2019-02-20', '26511903-1_l_2.jpg', '理念指导+操作技巧 助力投资新手、投资高手！', 121.10, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1009, 'manage', '稻盛和夫阿米巴经营经典套装（理论+实践）', '9787520202824', '[日]稻盛和夫', '中国大百科全书出版社', '2018-05-01', '25288392-1_l_1.jpg', '日本经营之圣稻盛和夫亲笔撰写，首次全公开曾秘不外传的阿米巴经营要领！学习阿米巴经营的经典教材，畅销70万册', 74.70, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1010, 'manage', '斯坦福极简经济学', '24010635', '[美]泰勒', '湖南人民出版社', '2016-08-16', '24010635-1_l_6.jpg', '《斯坦福极简经济学》被评为2015年度经管类好书。斯坦福大学十分受欢迎的经济学教授给你36个经济法则关键词，带你看懂复杂世界的真实运作！', 45.50, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1011, 'manage', '工匠精神：向价值型员工进化——精装典藏新版', '9787515814940', '付守永', '中华工商联合出版社', '2015-12-01', '23811600-1_l_1.jpg', '2016政府工作报告首倡工匠精神。精耕细作3年，销量近20万册，首部让精益求精、创新突破的工匠精神成为员工信仰的著作。移动互联时代更需要工匠精神！', 37.00, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1012, 'manage', '高效能学习：思维力+学习力（套装共2册）', '26514401', '王世民', '电子工业出版社', '2019-02-19', '26514401-1_l_3.jpg', '思维力与学习力的碰撞，升级国民大脑，颠覆职场学习！', 101.40, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1013, 'manage', '谈判：如何在博弈中获得更多', '9787513918930', '[英] 盖温·肯尼迪 (Gavin Kennedy)', '民主与建设出版社', '2018-04-06', '25247638-1_l_1.jpg', '全球十余种版本累计销售超200万册，中国入世首席谈判官龙永图作序推荐，世界著名谈判专家盖温·肯尼迪畅销35年的谈判宝典全新增订，亿万富翁的枕边书', 40.50, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1014, 'sheke', '曾国藩的正面与侧面', '27856398', '张宏杰', '岳麓出版社', '2018-04-06', '27856398-1_l_4.jpg', '知名历史学者张宏杰，百万畅销《曾国藩的正面与侧面》经典系列，郑重收官。三本书分别阐述了中国特色官场生存哲学、治家典范书以及曾国藩领导力，展现一个更加立体的曾国藩。', 134.60, 99, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1015, 'sheke', '第二性（ⅠⅡ 全两册）', '23429879', '[法]西蒙娜德波伏瓦', '上海译文出版社', '2011-09-01', '23429879-1_l_5.jpg', '《第二性I》副标题为“事实与神话”，作者从生物学、精神分析学和历史唯物主义关于女性的观点出发，剖析女人变成“他者”的原因；随后，通过对人类历史的梳理，深刻地揭示了从原始社会到现今女性的命运；', 68.60, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1016, 'sheke', '易经杂说', '9787520707862', '南怀瑾', '东方出版社', '2019-04-01', '26914951-1_l_8.jpg', '南怀瑾先生生前亲笔签约授权，亲加审阅的定本，当当网独家定制，典藏必备。 ', 45.50, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1017, 'sheke', '中国文史哲大辞典（全六册）', '25282178', '郑天挺、谭其骧、钱仲联、章培恒、傅璇琮、张岱年等', '上海辞书出版社', '2018-08-15', '25282178-1_l_4.jpg', '研究中国历史、哲学、文学必备工具书（荣获第十一届国家图书奖、第三届国家辞书奖二等奖、上海市优秀图书奖一等奖、第五届国家图书奖提名奖、第四届中国辞书奖一等奖。） ', 945.60, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1018, 'life', '范志红健康日历', '9787122327475', '范志红', '化学工业出版社', '2019-12-01', '25343879-1_l_9.jpg', '本书为范志红教授的日历体新作。全书以2019年日历为时间顺序，分12个月12大健康关键词，对糖类、脂肪、蛋白质、维生素、减肥、运动、慢性病等营养健康热点进行通俗易懂的一句话解读，科学靠谱。', 70.50, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1019, 'life', '闪电增肌', '9787121359552', '仰望尾迹云', '电子工业出版社', '2019-03-01', '26911042-1_l_2.jpg', '肌肉健美指南，运动解剖及增肌动作图解，器械健身+囚徒增肌+增肌营养+增肌补充剂+运动损伤修复与预防全书', 46.80, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1020, 'life', '你不懂茶', '9787559430076', '[日]三宅贵男', '江苏凤凰文艺出版社', '2019-09-01', '26910987-1_l_29.jpg', '茶文化入门必读经典，日本插画师精心手绘300余幅插图，时尚、有料、有趣的茶知识百科', 39.50, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1021, 'life', '男人这东西', '9787555269410', '渡边淳一', '青岛出版社', '2018-05-31', '25310942-1_l_3.jpg', '渡边淳一深度剖析男女两性价值观的异同，从男女性心理学角度撰写的两性关系读本', 39.00, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1022, 'life', '孩子受益一生的思维力', '9787554613122', '杨瑜君 万玲', '古吴轩出版社', '2019-01-20', '26445780-1_l_3.jpg', '美国幼儿园和小学都在使用的学习工具。一看就懂、一学就能上手的八大思维导图，阅读、写作、演讲、课堂学习……处处都受用的思维方法，让摸不着看不见的思维过程直观呈现，帮助孩子学习…', 44.30, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1023, 'life', '刚刚好的养育：如何培养高情商的孩子', '9787555277750', '鱼爸 ', '青岛出版社', '2019-03-01', '25478869-1_l_24.jpg', '谢谢你，愿做我的孩子；孩子的高情商藏于父母的育儿思维里；看见孩子，共情养育，建立独立又亲密的亲子观 ', 40.10, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1024, 'edu', '四大名著', '25111286', '[清]曹雪芹、[明]吴承恩、[明]罗贯中、[明]施耐庵', '民主与建设出版社', '2017-08-01', '25111286-1_l_10.jpg', '青少版插图本 新课标课外阅读 畅销5周年新版修订 好评如潮 三国演义 红楼梦 水浒传 西游记', 97.74, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1025, 'edu', '中华上下五千年', '9787570404506', '刘敬余主编', '北京教育出版社', '2019-02-20', '25536242-1_l_5.jpg', ' 青少彩图版(全4册)各类历史文化常识，让历史更有趣，清晰的历史路线图，丰富的彩色插图，让历史更鲜活。', 89.70, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1026, 'edu', '古史·文言·今论', '9787303227259', '杨洋', '北京师范大学出版社', '2018-05-01', '25168544-1_l_11.jpg', '《古史·文言·今论》赋予你征服高考文言文的能力！ 文言阅读、文化积累两手抓，为高中师生供文言文教和学的全新解决方案。', 65.30, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1027, 'edu', '星火英语六级真题试卷', '9787313086716', '汪开虎', '上海交通大学出版社', '2016-08-16', '26317585-1_l_8.jpg', '2019年6月全真试题+标准模拟（6级）刷题真题词汇写作听力口语', 36.80, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1028, 'edu', '现代汉语词典(第7版)', '9787100124508', '中国社会科学院语言研究所词典编辑室 ', '商务印书馆', '2015-12-01', '24039082-1_l_10.jpg', '一部久享盛誉的规范型词典：《现代汉语词典》;一个专业权威的学术机构：中国社会科学院语言研究所;一家百年历史的出版名社：商务印书馆', 93.00, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1029, 'edu', '居里夫人传', '9787512655942', '[法]玛丽.居里', '团结出版社', '2019-02-19', '25193296-1_l_344.jpg', '教育部部编初中语文教材八年级上指定阅读图书，一位坚强、高尚的伟大女性，两次获得诺贝尔奖的女性科学家，著名翻译家陈筱卿授权经典全译本', 21.60, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1030, 'wenyi', '艺术的故事', '9787807463726', '[英] 贡布里希', '广西美术出版社', '2018-04-06', '20357456-1_l_5.jpg', '清华校长赠2017年本科新生——《艺术的故事》推荐理由：在艺术中获得人生乐趣！在艺术中回望历史 ！', 182.00, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1031, 'wenyi', '梵高：化世间痛苦为激情洋溢的美', '9787568041638', '[法]扬·布朗', '华中科技大学出版社', '2018-04-06', '25336743-1_l_3.jpg', '300幅珍贵的艺术作品完整、真实、系统地呈现梵高10年人生与艺术全貌、看尽天才疯子梵高的绘画理念进化全过程，普通人也能读懂的艺术著作，还原大师书信手稿，再诉梵高想要传达给世人却从未... ', 353.80, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1032, 'wenyi', '林徽因传：人生从来都靠自己成全', '9787202134948', '程碧', '河北人民出版社', '2011-09-01', '26514792-1_l_10.jpg', '畅销书作家程碧的林徽因传精装升级，平装本热销20万册）十点读书、中央人民广播电台《品味书香》专题推荐！', 51.60, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1033, 'wenyi', '三毛典藏全集', '9787530216545', '三毛 ', '北京十月文艺出版社', '2019-04-01', '25071464-1_l_3.jpg', '集结十四部传世经典 三十年写作成果全新呈现。', 336.00, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1034, 'wenyi', '雨夜短文', '9787545536829', '余秋雨', '天地出版社', '2018-08-15', '26922758-1_l_10.jpg', '余秋雨首部全新短篇散文重磅上市！用诙谐、讽刺的笔法直面流言蜚语的无奈与孤独，用智慧让谎言失重；用大白话使读者在有限的时间内读懂中国千年文化魂，用短文撬起一部文学史', 55.70, 100, '2020-06-01 20:12:00');
INSERT INTO `book` VALUES (1035, 'wenyi', '鲁迅全集', '9787547711101', '鲁迅', '北京日报出版社', '2019-03-01', '23473587-1_l_5.jpg', '全20卷，纪念鲁迅先生逝世80周年！', 457.50, 100, '2020-06-01 20:12:00');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `category_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类代码',
  `category_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '分类名称',
  PRIMARY KEY (`category_code`) USING BTREE,
  UNIQUE INDEX `category_code`(`category_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES ('edu', '教育');
INSERT INTO `category` VALUES ('life', '生活');
INSERT INTO `category` VALUES ('manage', '经营');
INSERT INTO `category` VALUES ('novel', '小说');
INSERT INTO `category` VALUES ('sheke', '社科');
INSERT INTO `category` VALUES ('wenyi', '文艺');
INSERT INTO `category` VALUES ('default', '默认分类');

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `order_item_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单子项ID',
  `order_id` int(10) UNSIGNED NOT NULL COMMENT '订单ID',
  `book_id` int(10) UNSIGNED NOT NULL COMMENT '书籍ID',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `quantity` int(10) UNSIGNED NOT NULL COMMENT '购买数量',
  PRIMARY KEY (`order_item_id`) USING BTREE,
  INDEX `order_id`(`order_id`) USING BTREE,
  INDEX `book_id`(`book_id`) USING BTREE,
  CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `order_item_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (12, 2, 1014, 134.60, 1);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `order_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `user_id` int(10) UNSIGNED NOT NULL COMMENT '用户ID',
  `consignee_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收货人姓名',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收货地址',
  `zip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '邮政编号',
  `phone_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '联系方式',
  `status` bit(1) NOT NULL DEFAULT b'0' COMMENT '审核状态',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`order_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `order_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (2, 2020002, 'xin', '西安', '400000', '15891589413', b'0', NULL);

-- ----------------------------
-- Table structure for shopping_cart
-- ----------------------------
DROP TABLE IF EXISTS `shopping_cart`;
CREATE TABLE `shopping_cart`  (
  `cart_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` int(10) UNSIGNED NOT NULL COMMENT '用户ID',
  `book_id` int(10) UNSIGNED NOT NULL COMMENT '书籍ID',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '书籍价格',
  `quantity` int(10) UNSIGNED NOT NULL COMMENT '购买数量',
  PRIMARY KEY (`cart_id`) USING BTREE,
  UNIQUE INDEX `cart_id`(`cart_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `book_id`(`book_id`) USING BTREE,
  CONSTRAINT `shopping_cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `shopping_cart_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `user_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户密码',
  `email` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户头像',
  `join_time` datetime NULL DEFAULT NULL COMMENT '注册时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2020003 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES (2020002, 'xin', 'xingxing', '123@126.com', '', NULL);
/*
新增表：publisher/author/book_author/keyword/book_keyword/series/supplier/book_supplier/shortage/purchase_order/purchase_order_item/credit_level
以及对现有表的部分字段扩展（book, user_info）
*/

-- ----------------------------
-- Alter existing table `book` 添加出版社/丛书/最小库存/封面路径字段
-- ----------------------------
ALTER TABLE `book`
  ADD COLUMN `publisher_id` int(10) UNSIGNED NULL AFTER `isbn`,
  ADD COLUMN `series_id` int(10) UNSIGNED NULL AFTER `publisher_id`,
  ADD COLUMN `min_stock` int(10) UNSIGNED NOT NULL DEFAULT 0 AFTER `stock`,
  ADD COLUMN `cover_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' AFTER `description`;

-- ----------------------------
-- Table structure for publisher
-- ----------------------------
DROP TABLE IF EXISTS `publisher`;
CREATE TABLE `publisher`  (
  `publisher_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '出版社ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '出版社名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地址',
  `contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系人/电话',
  PRIMARY KEY (`publisher_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for author
-- ----------------------------
DROP TABLE IF EXISTS `author`;
CREATE TABLE `author`  (
  `author_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '作者ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '作者姓名',
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '作者简介',
  PRIMARY KEY (`author_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for book_author (关联表，记录作者顺序)
-- ----------------------------
DROP TABLE IF EXISTS `book_author`;
CREATE TABLE `book_author`  (
  `book_id` int(10) UNSIGNED NOT NULL COMMENT '书籍ID',
  `author_id` int(10) UNSIGNED NOT NULL COMMENT '作者ID',
  `author_order` int(3) NOT NULL COMMENT '作者顺序（1..4）',
  PRIMARY KEY (`book_id`,`author_id`) USING BTREE,
  UNIQUE KEY `ux_book_author_order` (`book_id`,`author_order`) USING BTREE,
  INDEX `idx_book_author_book`(`book_id`) USING BTREE,
  INDEX `idx_book_author_author`(`author_id`) USING BTREE,
  CONSTRAINT `book_author_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `book_author_ibfk_2` FOREIGN KEY (`author_id`) REFERENCES `author` (`author_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for keyword
-- ----------------------------
DROP TABLE IF EXISTS `keyword`;
CREATE TABLE `keyword`  (
  `keyword_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关键字ID',
  `word` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关键字内容',
  PRIMARY KEY (`keyword_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for book_keyword (关联表)
-- ----------------------------
DROP TABLE IF EXISTS `book_keyword`;
CREATE TABLE `book_keyword`  (
  `book_id` int(10) UNSIGNED NOT NULL COMMENT '书籍ID',
  `keyword_id` int(10) UNSIGNED NOT NULL COMMENT '关键字ID',
  PRIMARY KEY (`book_id`,`keyword_id`) USING BTREE,
  INDEX `idx_book_keyword_book`(`book_id`) USING BTREE,
  INDEX `idx_book_keyword_keyword`(`keyword_id`) USING BTREE,
  CONSTRAINT `book_keyword_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `book_keyword_ibfk_2` FOREIGN KEY (`keyword_id`) REFERENCES `keyword` (`keyword_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for series
-- ----------------------------
DROP TABLE IF EXISTS `series`;
CREATE TABLE `series`  (
  `series_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '丛书ID',
  `series_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '丛书名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '描述',
  PRIMARY KEY (`series_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for supplier
-- ----------------------------
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier`  (
  `supplier_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '供应商ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '供应商名称',
  `contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '联系人/电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '地址',
  PRIMARY KEY (`supplier_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for book_supplier (供货信息)
-- ----------------------------
DROP TABLE IF EXISTS `book_supplier`;
CREATE TABLE `book_supplier`  (
  `book_id` int(10) UNSIGNED NOT NULL COMMENT '书籍ID',
  `supplier_id` int(10) UNSIGNED NOT NULL COMMENT '供应商ID',
  `supply_price` decimal(10,2) NULL COMMENT '供货价格',
  PRIMARY KEY (`book_id`,`supplier_id`) USING BTREE,
  INDEX `idx_book_supplier_book`(`book_id`) USING BTREE,
  INDEX `idx_book_supplier_supplier`(`supplier_id`) USING BTREE,
  CONSTRAINT `book_supplier_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `book_supplier_ibfk_2` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for supplier_book (供应商书目)
-- ----------------------------
DROP TABLE IF EXISTS `supplier_book`;
CREATE TABLE `supplier_book`  (
  `supplier_book_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '供应商书目ID',
  `supplier_id` int(10) UNSIGNED NOT NULL COMMENT '供应商ID',
  `isbn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ISBN',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '书名',
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '作者',
  `press` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '出版社',
  `price` decimal(10,2) NULL COMMENT '定价',
  `supply_price` decimal(10,2) NULL COMMENT '供货价',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '描述',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-活跃，INACTIVE-停用',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`supplier_book_id`) USING BTREE,
  INDEX `idx_supplier_book_supplier`(`supplier_id`) USING BTREE,
  INDEX `idx_supplier_book_isbn`(`isbn`) USING BTREE,
  INDEX `idx_supplier_book_title`(`title`) USING BTREE,
  CONSTRAINT `supplier_book_ibfk_1` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for shortage (缺书记录)
-- ----------------------------
DROP TABLE IF EXISTS `shortage`;
CREATE TABLE `shortage`  (
  `shortage_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '缺书记录ID',
  `book_id` int(10) UNSIGNED NOT NULL COMMENT '缺书书籍ID',
  `quantity` int(10) UNSIGNED NOT NULL COMMENT '缺书数量',
  `register_date` datetime NOT NULL COMMENT '登记日期',
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '来源',
  `is_processed` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已处理',
  `customer_request_id` int(10) UNSIGNED NULL COMMENT '关联客户订单项ID（可选）',
  PRIMARY KEY (`shortage_id`) USING BTREE,
  INDEX `idx_shortage_book`(`book_id`) USING BTREE,
  CONSTRAINT `shortage_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for purchase_order
-- ----------------------------
DROP TABLE IF EXISTS `purchase_order`;
CREATE TABLE `purchase_order`  (
  `po_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '采购单ID',
  `supplier_id` int(10) UNSIGNED NOT NULL COMMENT '供应商ID',
  `order_date` datetime NOT NULL COMMENT '采购单生成日期',
  `expected_arrival_date` date NULL COMMENT '预计到货日期',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态',
  `total_amount` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '采购总金额',
  PRIMARY KEY (`po_id`) USING BTREE,
  INDEX `idx_po_supplier`(`supplier_id`) USING BTREE,
  CONSTRAINT `purchase_order_ibfk_1` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for purchase_order_item
-- ----------------------------
DROP TABLE IF EXISTS `purchase_order_item`;
CREATE TABLE `purchase_order_item`  (
  `po_item_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '采购单项ID',
  `po_id` int(10) UNSIGNED NOT NULL COMMENT '采购单ID',
  `book_id` int(10) UNSIGNED NOT NULL COMMENT '书籍ID',
  `quantity` int(10) UNSIGNED NOT NULL COMMENT '采购数量',
  `unit_price` decimal(10,2) NOT NULL COMMENT '采购单价',
  `shortage_id` int(10) UNSIGNED NULL COMMENT '关联缺书记录ID',
  `received_quantity` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '已收货数量',
  PRIMARY KEY (`po_item_id`) USING BTREE,
  INDEX `idx_poi_po`(`po_id`) USING BTREE,
  INDEX `idx_poi_book`(`book_id`) USING BTREE,
  CONSTRAINT `purchase_order_item_ibfk_1` FOREIGN KEY (`po_id`) REFERENCES `purchase_order` (`po_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `purchase_order_item_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `purchase_order_item_ibfk_3` FOREIGN KEY (`shortage_id`) REFERENCES `shortage` (`shortage_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for credit_level
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `admin_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '管理员用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  PRIMARY KEY (`admin_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

DROP TABLE IF EXISTS `credit_level`;
CREATE TABLE `credit_level`  (
  `level_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '信用等级ID',
  `level_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '等级名称',
  `discount_rate` decimal(3,2) NOT NULL DEFAULT 0.00 COMMENT '折扣率',
  `overdraft_limit` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '透支额度',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '描述',
  PRIMARY KEY (`level_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- 扩展 user_info：账户余额与信用等级
-- ----------------------------
ALTER TABLE `user_info`
  ADD COLUMN `account_balance` decimal(10,2) NOT NULL DEFAULT 0.00 AFTER `avatar`,
  ADD COLUMN `credit_level_id` int(10) UNSIGNED NULL AFTER `account_balance`,
  ADD INDEX `idx_user_creditlevel`(`credit_level_id`);

ALTER TABLE `user_info`
  ADD CONSTRAINT `user_info_ibfk_creditlevel` FOREIGN KEY (`credit_level_id`) REFERENCES `credit_level` (`level_id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- ----------------------------
-- 关联 book.publisher_id 与 series_id 外键
-- ----------------------------
ALTER TABLE `book`
  ADD INDEX `idx_book_publisher` (`publisher_id`),
  ADD INDEX `idx_book_series` (`series_id`);

ALTER TABLE `book`
  ADD CONSTRAINT `book_ibfk_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `publisher` (`publisher_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `book_ibfk_series` FOREIGN KEY (`series_id`) REFERENCES `series` (`series_id`) ON DELETE SET NULL ON UPDATE CASCADE;

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Orders / OrderItem - 补充字段与约束（符合设计）
-- ----------------------------
ALTER TABLE `orders`
  ADD COLUMN `total_amount` decimal(10,2) NOT NULL DEFAULT 0.00 AFTER `create_time`,
  ADD COLUMN `payment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PENDING' AFTER `total_amount`,
  ADD COLUMN `shipping_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PENDING' AFTER `payment_status`;

ALTER TABLE `order_item`
  ADD COLUMN `unit_price` decimal(10,2) NOT NULL DEFAULT 0.00 AFTER `price`,
  ADD COLUMN `subtotal` decimal(10,2) NOT NULL DEFAULT 0.00 AFTER `unit_price`,
  ADD COLUMN `shipped_quantity` int(10) UNSIGNED NOT NULL DEFAULT 0 AFTER `quantity`;

-- 确保一个订单中一本书只出现一次
ALTER TABLE `order_item`
  ADD UNIQUE INDEX `ux_order_item_order_book` (`order_id`, `book_id`);

-- 用已有数据填充 subtotal/unit_price，并更新 orders.total_amount
UPDATE `order_item` SET `unit_price` = `price`, `subtotal` = `price` * `quantity` WHERE `unit_price` = 0;
UPDATE `orders` o SET `total_amount` = (
  SELECT IFNULL(SUM(subtotal),0) FROM `order_item` WHERE `order_id` = o.`order_id`
);

-- ----------------------------
-- shortage 唯一约束：避免重复未处理记录
-- ----------------------------
ALTER TABLE `shortage`
  ADD UNIQUE INDEX `ux_shortage_book_isprocessed` (`book_id`, `is_processed`);

-- ----------------------------
-- Views：BookCatalogView / CustomerCreditView / OrderDetailView / UnprocessedShortageView
-- ----------------------------
DROP VIEW IF EXISTS `BookCatalogView`;
CREATE VIEW `BookCatalogView` AS
SELECT
  b.book_id AS book_id,
  b.isbn AS isbn,
  b.book_name AS title,
  b.price AS price,
  b.stock AS stock_quantity,
  p.name AS publisher_name,
  s.series_name AS series_name,
  (SELECT GROUP_CONCAT(a.name ORDER BY ba.author_order SEPARATOR ', ')
     FROM book_author ba JOIN author a ON ba.author_id = a.author_id WHERE ba.book_id = b.book_id) AS authors,
  (SELECT GROUP_CONCAT(k.word SEPARATOR ', ')
     FROM book_keyword bk JOIN keyword k ON bk.keyword_id = k.keyword_id WHERE bk.book_id = b.book_id) AS keywords
FROM book b
LEFT JOIN publisher p ON b.publisher_id = p.publisher_id
LEFT JOIN series s ON b.series_id = s.series_id;

DROP VIEW IF EXISTS `CustomerCreditView`;
CREATE VIEW `CustomerCreditView` AS
SELECT u.user_id AS customer_id, u.user_name AS online_id, u.email, u.account_balance, cl.level_id AS credit_level_id, cl.level_name, cl.discount_rate, cl.overdraft_limit
FROM user_info u
LEFT JOIN credit_level cl ON u.credit_level_id = cl.level_id;

DROP VIEW IF EXISTS `OrderDetailView`;
CREATE VIEW `OrderDetailView` AS
SELECT o.order_id, u.user_name AS customer_name, o.create_time AS order_date, o.shipping_status, o.payment_status, b.book_name AS book_title, oi.quantity, oi.unit_price, oi.subtotal, oi.shipped_quantity
FROM orders o
JOIN user_info u ON o.user_id = u.user_id
JOIN order_item oi ON o.order_id = oi.order_id
JOIN book b ON oi.book_id = b.book_id;

DROP VIEW IF EXISTS `UnprocessedShortageView`;
CREATE VIEW `UnprocessedShortageView` AS
SELECT shortage_id, book_id, quantity, register_date, source, customer_request_id
FROM shortage
WHERE is_processed = b'0';

-- ----------------------------
-- Triggers：UpdateOrderTotal / DecreaseStock / AutoShortage / IncreaseStockOnReceive
-- ----------------------------
DELIMITER $$
DROP TRIGGER IF EXISTS `trg_order_item_after_insert`$$
CREATE TRIGGER `trg_order_item_after_insert` AFTER INSERT ON `order_item`
FOR EACH ROW
BEGIN
  -- 变量声明（必须在 BEGIN 后立即声明）
  DECLARE current_stock INT DEFAULT 0;
  DECLARE deficit INT DEFAULT 0;
  -- 更新订单总金额
  UPDATE `orders` SET `total_amount` = (
    SELECT IFNULL(SUM(subtotal),0) FROM `order_item` WHERE `order_id` = NEW.order_id
  ) WHERE `order_id` = NEW.order_id;
  -- 扣减库存并在不足时生成缺书记录
  SELECT `stock` INTO current_stock FROM `book` WHERE `book_id` = NEW.book_id;
  IF current_stock >= NEW.quantity THEN
    UPDATE `book` SET `stock` = current_stock - NEW.quantity WHERE `book_id` = NEW.book_id;
  ELSE
    SET deficit = NEW.quantity - current_stock;
    UPDATE `book` SET `stock` = 0 WHERE `book_id` = NEW.book_id;
    IF (SELECT COUNT(*) FROM `shortage` WHERE `book_id` = NEW.book_id AND `is_processed` = b'0') = 0 THEN
      INSERT INTO `shortage` (`book_id`,`quantity`,`register_date`,`source`,`is_processed`,`customer_request_id`)
      VALUES (NEW.book_id, deficit, NOW(), 'customer_order', b'0', NULL);
    END IF;
  END IF;
END$$

DROP TRIGGER IF EXISTS `trg_order_item_after_update`$$
CREATE TRIGGER `trg_order_item_after_update` AFTER UPDATE ON `order_item`
FOR EACH ROW
BEGIN
  -- 更新订单总金额（针对 price/quantity/subtotal 变更）
  UPDATE `orders` SET `total_amount` = (
    SELECT IFNULL(SUM(subtotal),0) FROM `order_item` WHERE `order_id` = NEW.order_id
  ) WHERE `order_id` = NEW.order_id;
END$$

DROP TRIGGER IF EXISTS `trg_order_item_after_delete`$$
CREATE TRIGGER `trg_order_item_after_delete` AFTER DELETE ON `order_item`
FOR EACH ROW
BEGIN
  UPDATE `orders` SET `total_amount` = (
    SELECT IFNULL(SUM(subtotal),0) FROM `order_item` WHERE `order_id` = OLD.order_id
  ) WHERE `order_id` = OLD.order_id;
END$$

DROP TRIGGER IF EXISTS `trg_book_after_update_autoshortage`$$
CREATE TRIGGER `trg_book_after_update_autoshortage` AFTER UPDATE ON `book`
FOR EACH ROW
BEGIN
  IF NEW.stock < NEW.min_stock THEN
    IF (SELECT COUNT(*) FROM `shortage` WHERE `book_id` = NEW.book_id AND `is_processed` = b'0') = 0 THEN
      INSERT INTO `shortage` (`book_id`,`quantity`,`register_date`,`source`,`is_processed`,`customer_request_id`)
      VALUES (NEW.book_id, NEW.min_stock - NEW.stock, NOW(), 'auto_low_stock', b'0', NULL);
    END IF;
  END IF;
END$$

-- 移除触发器，避免与Java代码重复增加库存
-- DROP TRIGGER IF EXISTS `trg_purchase_order_item_after_update_receive`$$
-- CREATE TRIGGER `trg_purchase_order_item_after_update_receive` AFTER UPDATE ON `purchase_order_item`
-- FOR EACH ROW
-- BEGIN
--   DECLARE delta_received INT DEFAULT 0;
--   SET delta_received = NEW.received_quantity - OLD.received_quantity;
--   IF delta_received > 0 THEN
--     -- 增加库存
--     UPDATE `book` SET `stock` = `stock` + delta_received WHERE `book_id` = NEW.book_id;
--     -- 标记关联缺书记录为已处理（如果收到数量覆盖缺书数量）
--     UPDATE `shortage` s
--       JOIN `purchase_order_item` poi ON poi.shortage_id = s.shortage_id
--       SET s.is_processed = b'1'
--       WHERE poi.po_item_id = NEW.po_item_id AND s.is_processed = b'0' AND NEW.received_quantity >= s.quantity;
--   END IF;
-- END$$
-- DELIMITER ;

-- ----------------------------
-- 初始数据：admin、credit_level、publisher（从现有 book.press 提取）、author（从现有 book.author 提取）并建立映射
-- ----------------------------
INSERT INTO `admin` (`admin_name`,`password`) VALUES
('admin','123456');

INSERT INTO `credit_level` (`level_id`,`level_name`,`discount_rate`,`overdraft_limit`,`description`) VALUES
(1,'一级客户',0.00,0.00,'不能透支，0%折扣'),
(2,'二级客户',0.02,0.00,'不能透支，2%折扣'),
(3,'三级客户',0.05,100.00,'可小额透支，5%折扣'),
(4,'四级客户',0.08,500.00,'可中额透支，8%折扣'),
(5,'五级客户',0.10,1000.00,'可高额透支，10%折扣');

-- 将现有 book.press 去重并插入 publisher 表（忽略空值）
INSERT INTO `publisher` (`name`)
  SELECT DISTINCT TRIM(press) FROM `book` WHERE IFNULL(TRIM(press),'') <> '';

-- 将现有 book.author 去重并插入 author 表（以整个字段作为作者名占位，后续可细分）
INSERT INTO `author` (`name`)
  SELECT DISTINCT TRIM(author) FROM `book` WHERE IFNULL(TRIM(author),'') <> '';

-- 更新 book.publisher_id（基于 press 名称匹配）
UPDATE `book` b
  JOIN `publisher` p ON TRIM(b.press) = p.name
  SET b.publisher_id = p.publisher_id;

-- 为每本书创建 book_author 关联（将原 author 字段作为单一作者，author_order=1）
INSERT IGNORE INTO `book_author` (`book_id`,`author_id`,`author_order`)
  SELECT b.book_id, a.author_id, 1 FROM `book` b
  JOIN `author` a ON TRIM(b.author) = a.name;

-- 若需要，可插入示例 supplier 与 purchase_order
INSERT INTO `supplier` (`name`,`contact`,`address`) VALUES ('示例供应商','010-00000000','示例地址');

-- ----------------------------
-- 示例供应商及其书目数据
-- ----------------------------
INSERT INTO `supplier` (`name`,`contact`,`address`) VALUES
('北京新华书店','010-12345678','北京市朝阳区建国门外大街1号'),
('上海文艺出版社','021-87654321','上海市徐汇区漕溪北路336号'),
('广东教育出版社','020-11223344','广州市天河区天润路123号'),
('浙江大学出版社','0571-55667788','杭州市浙大路38号');

-- 获取新插入的供应商ID（由于是AUTO_INCREMENT，这里我们假设ID从2开始分配）
-- 北京新华书店 ID=2, 上海文艺出版社 ID=3, 广东教育出版社 ID=4, 浙江大学出版社 ID=5

-- ----------------------------
-- 示例供应商书目数据
-- ----------------------------
-- ----------------------------
-- 示例丛书数据
-- ----------------------------
INSERT INTO `series` (`series_name`,`description`) VALUES
('哈利·波特系列','J.K.罗琳创作的魔法世界经典系列'),
('魔戒系列','J.R.R.托尔金的史诗奇幻巨作'),
('三体系列','刘慈欣的科幻小说三部曲'),
('四大名著','中国古典文学四大名著'),
('经济学人智库','经济学人出版的商业管理系列'),
('计算机科学丛书','计算机科学经典教材系列'),
('福尔摩斯探案','柯南·道尔的侦探小说系列'),
('阿加莎·克里斯蒂推理小说','著名推理小说家阿加莎·克里斯蒂的作品集'),
('高中数学教材系列','人民教育出版社高中数学必修教材'),
('大学物理教材系列','高等教育出版社大学物理教材');

-- ----------------------------
-- 示例供应商书目数据（包含丛书关联）
-- ----------------------------
INSERT INTO `supplier_book` (`supplier_id`,`series_id`,`isbn`,`title`,`author`,`press`,`price`,`supply_price`,`description`,`status`,`create_time`,`update_time`) VALUES
(1,NULL,'9787555257036','失乐园（修订版）','渡边淳一','青岛出版社',48.00,35.00,'渡边淳一文学代表作，探讨中年危机与婚外情','ACTIVE','2024-01-10 10:00:00','2024-01-10 10:00:00'),
(1,NULL,'9787544258601','追风筝的人（20周年纪念版）','卡勒德·胡赛尼','上海人民出版社',42.00,30.00,'关于友情、救赎与爱的阿富汗故事','ACTIVE','2024-01-10 10:00:00','2024-01-10 10:00:00'),
(2,1,'9787020000001','哈利·波特与魔法石','J.K.罗琳','人民文学出版社',68.00,48.00,'哈利·波特系列第一部，魔法世界的冒险开始','ACTIVE','2024-01-15 10:00:00','2024-01-15 10:00:00'),
(2,1,'9787020000002','哈利·波特与密室','J.K.罗琳','人民文学出版社',72.00,50.00,'哈利·波特系列第二部，神秘的密室之谜','ACTIVE','2024-01-15 10:00:00','2024-01-15 10:00:00'),
(2,1,'9787020000003','哈利·波特与阿兹卡班的囚徒','J.K.罗琳','人民文学出版社',78.00,55.00,'哈利·波特系列第三部，魔法学校的新冒险','ACTIVE','2024-01-15 10:00:00','2024-01-15 10:00:00'),
(2,2,'9787020000004','魔戒前传：霍比特人','J.R.R.托尔金','译林出版社',45.00,32.00,'魔戒系列的前传，霍比特人的冒险故事','ACTIVE','2024-01-15 10:00:00','2024-01-15 10:00:00'),
(2,2,'9787020000005','魔戒同盟：王者归来','J.R.R.托尔金','译林出版社',88.00,62.00,'魔戒三部曲终结篇，终极大战','ACTIVE','2024-01-15 10:00:00','2024-01-15 10:00:00'),
(2,NULL,'9787020000001','百年孤独（精装纪念版）','加西亚·马尔克斯','南海出版公司',89.00,65.00,'诺贝尔文学奖得主马尔克斯代表作，魔幻现实主义巅峰之作','ACTIVE','2024-01-15 10:00:00','2024-01-15 10:00:00'),
(2,NULL,'9787544250001','追风筝的人','卡勒德·胡赛尼','上海人民出版社',39.50,28.00,'一本关于友情、背叛和救赎的故事，让人读完后泪流满面','ACTIVE','2024-01-15 10:00:00','2024-01-15 10:00:00'),
(2,NULL,'9787506360001','小王子（法文原版）','安托万·德·圣埃克苏佩里','人民文学出版社',25.00,18.00,'永远的童话，关于爱、友谊和生命的哲学寓言','ACTIVE','2024-01-15 10:00:00','2024-01-15 10:00:00'),
(2,4,'9787532740001','红楼梦（全二册）','曹雪芹','人民文学出版社',68.00,48.00,'中国古典小说巅峰之作，封建社会百科全书','ACTIVE','2024-01-15 10:00:00','2024-01-15 10:00:00'),
(3,NULL,'9787208060001','围城','钱锺书','人民文学出版社',32.00,24.00,'钱锺书唯一长篇小说，讽刺小说经典','ACTIVE','2024-01-16 10:00:00','2024-01-16 10:00:00'),
(3,NULL,'9787530210001','活着','余华','南海出版公司',28.00,20.00,'余华代表作，讲述一个普通人一生的苦难与坚韧','ACTIVE','2024-01-16 10:00:00','2024-01-16 10:00:00'),
(3,NULL,'9787544240001','许三观卖血记','余华','南海出版公司',26.00,18.00,'余华作品，关于贫穷与尊严的故事','ACTIVE','2024-01-16 10:00:00','2024-01-16 10:00:00'),
(3,NULL,'9787540480001','兄弟','余华','南海出版公司',45.00,32.00,'余华长篇小说，探讨中国近现代历史变迁','ACTIVE','2024-01-16 10:00:00','2024-01-16 10:00:00'),
(3,3,'9787530210002','三体','刘慈欣','重庆出版社',68.00,48.00,'科幻小说巅峰之作，三体系列第一部','ACTIVE','2024-01-16 10:00:00','2024-01-16 10:00:00'),
(3,3,'9787530210003','三体II：黑暗森林','刘慈欣','重庆出版社',72.00,50.00,'三体系列第二部，宇宙社会学的经典','ACTIVE','2024-01-16 10:00:00','2024-01-16 10:00:00'),
(3,3,'9787530210004','三体III：死神永生','刘慈欣','重庆出版社',78.00,55.00,'三体系列终结篇，人类命运的终极思考','ACTIVE','2024-01-16 10:00:00','2024-01-16 10:00:00'),
(4,9,'9787107170001','高中数学必修1','人民教育出版社','人民教育出版社',28.00,20.00,'高中数学教材，必修第一册','ACTIVE','2024-01-17 10:00:00','2024-01-17 10:00:00'),
(4,9,'9787107170002','高中数学必修2','人民教育出版社','人民教育出版社',32.00,23.00,'高中数学教材，必修第二册','ACTIVE','2024-01-17 10:00:00','2024-01-17 10:00:00'),
(4,9,'9787107170003','高中数学必修3','人民教育出版社','人民教育出版社',30.00,21.00,'高中数学教材，必修第三册','ACTIVE','2024-01-17 10:00:00','2024-01-17 10:00:00'),
(4,9,'9787107170004','高中数学必修4','人民教育出版社','人民教育出版社',35.00,25.00,'高中数学教材，必修第四册','ACTIVE','2024-01-17 10:00:00','2024-01-17 10:00:00'),
(4,9,'9787107170005','高中数学必修5','人民教育出版社','人民教育出版社',33.00,23.00,'高中数学教材，必修第五册','ACTIVE','2024-01-17 10:00:00','2024-01-17 10:00:00'),
(4,10,'9787111120001','大学物理（力学）','程守洙','高等教育出版社',38.00,27.00,'大学物理教材，力学部分','ACTIVE','2024-01-17 10:00:00','2024-01-17 10:00:00'),
(4,10,'9787111120002','大学物理（电磁学）','程守洙','高等教育出版社',42.00,30.00,'大学物理教材，电磁学部分','ACTIVE','2024-01-17 10:00:00','2024-01-17 10:00:00'),
(5,6,'9787111123001','计算机程序设计基础','谭浩强','清华大学出版社',45.00,32.00,'C语言程序设计经典教材','ACTIVE','2024-01-18 10:00:00','2024-01-18 10:00:00'),
(5,6,'9787111123002','数据结构（C语言版）','严蔚敏','清华大学出版社',48.00,34.00,'数据结构经典教材，C语言实现','ACTIVE','2024-01-18 10:00:00','2024-01-18 10:00:00'),
(5,6,'9787111123003','算法设计与分析','王晓东','电子工业出版社',52.00,37.00,'算法设计与分析基础教程','ACTIVE','2024-01-18 10:00:00','2024-01-18 10:00:00'),
(5,6,'9787111123004','操作系统原理','汤子瀛','电子工业出版社',55.00,39.00,'操作系统原理经典教材','ACTIVE','2024-01-18 10:00:00','2024-01-18 10:00:00'),
(5,6,'9787111123005','数据库系统概论','王珊','高等教育出版社',58.00,41.00,'数据库系统原理与应用','ACTIVE','2024-01-18 10:00:00','2024-01-18 10:00:00'),
(2,7,'9787020000006','福尔摩斯探案集：血字的研究','柯南·道尔','上海译文出版社',25.00,18.00,'福尔摩斯系列第一部，侦探小说的开山之作','ACTIVE','2024-01-15 10:00:00','2024-01-15 10:00:00'),
(2,7,'9787020000007','福尔摩斯探案集：四个签名','柯南·道尔','上海译文出版社',28.00,20.00,'福尔摩斯经典探案故事','ACTIVE','2024-01-15 10:00:00','2024-01-15 10:00:00'),
(2,8,'9787020000008','东方快车谋杀案','阿加莎·克里斯蒂','人民文学出版社',35.00,25.00,'阿加莎·克里斯蒂推理小说经典','ACTIVE','2024-01-15 10:00:00','2024-01-15 10:00:00'),
(2,8,'9787020000009','尼罗河上的惨案','阿加莎·克里斯蒂','人民文学出版社',38.00,27.00,'赫尔克里·波洛系列经典推理小说','ACTIVE','2024-01-15 10:00:00','2024-01-15 10:00:00');

-- ----------------------------
-- 扩展 purchase_order_item 表，支持供应商书目
-- ----------------------------
ALTER TABLE `purchase_order_item`
  ADD COLUMN `supplier_book_id` int(10) UNSIGNED NULL COMMENT '供应商书目ID' AFTER `book_id`,
  ADD INDEX `idx_poi_supplier_book`(`supplier_book_id`),
  ADD CONSTRAINT `purchase_order_item_ibfk_supplier_book` FOREIGN KEY (`supplier_book_id`) REFERENCES `supplier_book` (`supplier_book_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- 修改book_id字段为可空，因为现在可能使用supplier_book_id
ALTER TABLE `purchase_order_item`
  MODIFY COLUMN `book_id` int(10) UNSIGNED NULL COMMENT '书籍ID（普通书籍）';

-- ----------------------------
-- 扩展 supplier_book 表，支持丛书
-- ----------------------------
ALTER TABLE `supplier_book`
  ADD COLUMN `series_id` int(10) UNSIGNED NULL COMMENT '丛书ID' AFTER `supplier_id`,
  ADD INDEX `idx_supplier_book_series`(`series_id`),
  ADD CONSTRAINT `supplier_book_ibfk_series` FOREIGN KEY (`series_id`) REFERENCES `series` (`series_id`) ON DELETE SET NULL ON UPDATE CASCADE;
INSERT INTO `purchase_order` (`supplier_id`,`order_date`,`expected_arrival_date`,`status`,`total_amount`)
  VALUES (LAST_INSERT_ID(), NOW(), DATE_ADD(CURDATE(), INTERVAL 7 DAY), '待发送', 0.00);

-- 若需为已有缺书或采购生成联动，可在后续步骤补充

-- 结束：已完成数据库模型同步与初始数据填充
