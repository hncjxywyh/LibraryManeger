/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80031 (8.0.31)
 Source Host           : localhost:3306
 Source Schema         : library

 Target Server Type    : MySQL
 Target Server Version : 80031 (8.0.31)
 File Encoding         : 65001

 Date: 19/04/2026 21:46:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for book
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `isbn` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ISBN',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '书名',
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者',
  `publisher` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '出版社',
  `publish_date` date NULL DEFAULT NULL COMMENT '出版日期',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '价格',
  `stock` int NULL DEFAULT 0 COMMENT '库存数量',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '简介',
  `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图URL',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态: 0-下架 1-上架',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'purchase' COMMENT '来源：purchase购买/donation捐赠',
  `donor_id` bigint NULL DEFAULT NULL COMMENT '捐赠者用户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_isbn`(`isbn` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_title`(`title` ASC) USING BTREE,
  INDEX `idx_author`(`author` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '图书表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of book
-- ----------------------------
INSERT INTO `book` VALUES (2, '978-7-115-42826-7', 'Python编程: 从入门到实践', 'Eric Matthes', '人民邮电出版社', '2016-07-01', 5, 89.00, 9, 'Python入门经典', NULL, 1, '2026-04-18 14:24:41', '2026-04-18 14:24:41', 'purchase', NULL);
INSERT INTO `book` VALUES (3, '978-7-5322-5006-6', '三体', '刘慈欣', '重庆出版社', '2008-01-01', 4, 68.00, 0, '科幻巨著', NULL, 1, '2026-04-18 14:24:41', '2026-04-18 14:24:41', 'purchase', NULL);
INSERT INTO `book` VALUES (4, '006', 'langchain入门', '黑马程序员', '黑马程序员出版社', NULL, 5, 25.00, 6, '一本langchain入门指南', NULL, 1, '2026-04-18 15:49:31', '2026-04-18 15:49:31', 'purchase', NULL);
INSERT INTO `book` VALUES (5, '173263244', 'Java核心卷', '黑马程序员', '清华同方出版社', NULL, NULL, 18.00, 7, NULL, NULL, 1, '2026-04-19 20:46:07', '2026-04-19 20:46:07', 'donation', 4);
INSERT INTO `book` VALUES (6, NULL, '浅影歌舞大全', '浅影', '福建电信出版社', NULL, NULL, 0.00, 2, NULL, NULL, 1, '2026-04-19 21:41:14', '2026-04-19 21:41:14', 'donation', 2);

-- ----------------------------
-- Table structure for book_category
-- ----------------------------
DROP TABLE IF EXISTS `book_category`;
CREATE TABLE `book_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父分类ID',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '图书分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of book_category
-- ----------------------------
INSERT INTO `book_category` VALUES (1, '文学', 0, 1, '2026-04-18 14:24:41');
INSERT INTO `book_category` VALUES (2, '小说', 1, 1, '2026-04-18 14:24:41');
INSERT INTO `book_category` VALUES (3, '诗歌', 1, 2, '2026-04-18 14:24:41');
INSERT INTO `book_category` VALUES (4, '科幻', 0, 2, '2026-04-18 14:24:41');
INSERT INTO `book_category` VALUES (5, '技术', 0, 3, '2026-04-18 14:24:41');
INSERT INTO `book_category` VALUES (6, '编程', 5, 1, '2026-04-18 14:24:41');
INSERT INTO `book_category` VALUES (7, '设计', 0, 4, '2026-04-18 14:24:41');

-- ----------------------------
-- Table structure for book_donation
-- ----------------------------
DROP TABLE IF EXISTS `book_donation`;
CREATE TABLE `book_donation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `donor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '捐赠者姓名',
  `donor_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系方式',
  `donor_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '捐赠留言',
  `book_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图书名称',
  `book_author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者',
  `book_publisher` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '出版社',
  `quantity` int NULL DEFAULT 1 COMMENT '数量',
  `status` int NULL DEFAULT 1 COMMENT '1=待审核 2=已通过 3=已拒绝',
  `review_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核备注',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `user_id` bigint NULL DEFAULT NULL COMMENT '捐赠用户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_donation
-- ----------------------------
INSERT INTO `book_donation` VALUES (1, '亚鹏', '13223825993', '我是一名langchain爱好者', 'Java核心卷', '黑马程序员', '清华同方出版社', 7, 2, '你是个好人', '2026-04-19 20:46:07', 4, '2026-04-19 20:45:41');
INSERT INTO `book_donation` VALUES (2, '浅影啊', '13223836794', '送给粉丝的福利', '浅影歌舞大全', '浅影', '福建电信出版社', 2, 2, '谢谢up\n', '2026-04-19 21:41:14', 2, '2026-04-19 21:35:43');

-- ----------------------------
-- Table structure for book_reservation
-- ----------------------------
DROP TABLE IF EXISTS `book_reservation`;
CREATE TABLE `book_reservation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `book_id` bigint NOT NULL COMMENT '预约的图书ID',
  `user_id` bigint NOT NULL COMMENT '预约用户ID',
  `status` int NULL DEFAULT 1 COMMENT '1=排队中 2=已通知 3=已取消 4=已失效',
  `position` int NOT NULL COMMENT '队列位置',
  `notify_time` datetime NULL DEFAULT NULL COMMENT '通知时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '预约过期时间（notify_time+7天）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_book_status`(`book_id` ASC, `status` ASC) USING BTREE,
  INDEX `idx_user_status`(`user_id` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_reservation
-- ----------------------------
INSERT INTO `book_reservation` VALUES (1, 3, 2, 3, 1, NULL, NULL, '2026-04-19 21:05:39');
INSERT INTO `book_reservation` VALUES (2, 3, 2, 3, 1, NULL, NULL, '2026-04-19 21:17:38');
INSERT INTO `book_reservation` VALUES (3, 3, 5, 3, 2, NULL, NULL, '2026-04-19 21:19:57');
INSERT INTO `book_reservation` VALUES (5, 3, 2, 2, 1, '2026-04-19 21:32:05', '2026-04-26 21:32:05', '2026-04-19 21:27:51');

-- ----------------------------
-- Table structure for borrow_record
-- ----------------------------
DROP TABLE IF EXISTS `borrow_record`;
CREATE TABLE `borrow_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `book_id` bigint NOT NULL COMMENT '图书ID',
  `user_id` bigint NOT NULL COMMENT '读者ID',
  `borrow_date` datetime NOT NULL COMMENT '借书日期',
  `due_date` datetime NOT NULL COMMENT '应还日期',
  `return_date` datetime NULL DEFAULT NULL COMMENT '实际还书日期',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态: 1-借阅中 2-已归还 3-逾期',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remind_sent` tinyint NOT NULL DEFAULT 0 COMMENT '是否已发送到期提醒 0=未发 1=已发',
  `overdue_remind_count` int NOT NULL DEFAULT 0 COMMENT '逾期提醒次数',
  `overdue_fee` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '滞纳金金额',
  `renew_count` int DEFAULT 0 COMMENT '续借次数',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_book_id`(`book_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '借阅记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of borrow_record
-- ----------------------------
INSERT INTO `borrow_record` VALUES (1, 1, 2, '2026-04-18 14:57:05', '2026-07-17 14:57:05', '2026-04-18 15:33:08', 2, '2026-04-18 14:57:05', '2026-04-18 14:57:05', 0, 0, 0.00);
INSERT INTO `borrow_record` VALUES (2, 2, 2, '2026-04-18 14:58:02', '2026-05-18 14:58:02', '2026-04-18 14:58:47', 2, '2026-04-18 14:58:02', '2026-04-18 14:58:02', 0, 0, 0.00);
INSERT INTO `borrow_record` VALUES (3, 3, 2, '2026-04-18 14:58:08', '2026-05-18 14:58:08', '2026-04-18 14:58:40', 2, '2026-04-18 14:58:08', '2026-04-18 14:58:08', 0, 0, 0.00);
INSERT INTO `borrow_record` VALUES (4, 1, 2, '2026-04-18 15:34:28', '2026-05-18 15:34:28', '2026-04-19 19:46:52', 2, '2026-04-18 15:34:28', '2026-04-18 15:34:28', 0, 0, 0.00);
INSERT INTO `borrow_record` VALUES (5, 2, 2, '2026-04-18 15:34:30', '2026-05-18 15:34:30', '2026-04-19 19:46:56', 2, '2026-04-18 15:34:30', '2026-04-18 15:34:30', 0, 0, 0.00);
INSERT INTO `borrow_record` VALUES (6, 3, 2, '2026-04-18 15:34:35', '2026-05-18 15:34:35', '2026-04-19 19:46:56', 2, '2026-04-18 15:34:35', '2026-04-18 15:34:35', 0, 0, 0.00);
INSERT INTO `borrow_record` VALUES (7, 3, 2, '2026-04-19 19:47:03', '2026-07-18 19:47:03', '2026-04-19 20:51:58', 2, '2026-04-19 19:47:03', '2026-04-19 19:47:03', 0, 0, 0.00);
INSERT INTO `borrow_record` VALUES (8, 3, 2, '2026-04-19 20:52:05', '2026-05-19 20:52:05', '2026-04-19 20:52:12', 2, '2026-04-19 20:52:05', '2026-04-19 20:52:05', 0, 0, 0.00);
INSERT INTO `borrow_record` VALUES (9, 3, 2, '2026-04-19 21:21:33', '2026-05-19 21:21:33', '2026-04-19 21:22:25', 2, '2026-04-19 21:21:33', '2026-04-19 21:21:33', 0, 0, 0.00);
INSERT INTO `borrow_record` VALUES (10, 3, 5, '2026-04-19 21:27:16', '2026-05-19 21:27:16', '2026-04-19 21:29:05', 2, '2026-04-19 21:27:16', '2026-04-19 21:27:16', 0, 0, 0.00);
INSERT INTO `borrow_record` VALUES (11, 3, 5, '2026-04-19 21:29:21', '2026-05-19 21:29:21', '2026-04-19 21:32:05', 2, '2026-04-19 21:29:21', '2026-04-19 21:29:21', 0, 0, 0.00);
INSERT INTO `borrow_record` VALUES (12, 4, 2, '2026-04-19 21:33:15', '2026-06-18 21:33:15', NULL, 1, '2026-04-19 21:33:15', '2026-04-19 21:33:15', 0, 0, 0.00);
INSERT INTO `borrow_record` VALUES (13, 2, 2, '2026-04-19 21:33:36', '2026-06-18 21:33:36', NULL, 1, '2026-04-19 21:33:36', '2026-04-19 21:33:36', 0, 0, 0.00);
INSERT INTO `borrow_record` VALUES (14, 3, 2, '2026-04-19 21:33:47', '2026-08-17 21:33:47', NULL, 1, '2026-04-19 21:33:47', '2026-04-19 21:33:47', 0, 0, 0.00);

-- ----------------------------
-- Table structure for borrow_remind_log
-- ----------------------------
DROP TABLE IF EXISTS `borrow_remind_log`;
CREATE TABLE `borrow_remind_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `borrow_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `remind_type` tinyint NOT NULL COMMENT '1=即将到期 2=已逾期',
  `remind_days` int NOT NULL COMMENT '提前几天提醒/逾期几天',
  `remind_status` tinyint NOT NULL DEFAULT 0 COMMENT '0=待发送 1=已发送 2=发送失败',
  `send_time` datetime NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_borrow_id`(`borrow_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of borrow_remind_log
-- ----------------------------

-- ----------------------------
-- Table structure for overdue_fee_config
-- ----------------------------
DROP TABLE IF EXISTS `overdue_fee_config`;
CREATE TABLE `overdue_fee_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tier1_days` int NULL DEFAULT 7 COMMENT '第一档天数',
  `tier1_rate` decimal(10, 2) NULL DEFAULT 0.20 COMMENT '第一档费率',
  `tier2_days` int NULL DEFAULT 7 COMMENT '第二档天数',
  `tier2_rate` decimal(10, 2) NULL DEFAULT 0.50 COMMENT '第二档费率',
  `tier3_rate` decimal(10, 2) NULL DEFAULT 1.00 COMMENT '第三档费率（15天以上）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of overdue_fee_config
-- ----------------------------
INSERT INTO `overdue_fee_config` VALUES (1, 7, 0.20, 7, 0.50, 1.00, '2026-04-19 20:33:19', '2026-04-19 20:33:19');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `role` tinyint NOT NULL DEFAULT 2 COMMENT '角色: 1-管理员 2-读者',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (2, 'user', '$2a$10$thA2y3/avyF/.h2CGOZb/O89iABm9dRBwo8mL7G/hTeNibMJhe83.', '亚鹏Test', '12223225925', 'lma250725003@gmail.com', 2, 1, '2026-04-18 14:51:03', '2026-04-18 15:31:34');
INSERT INTO `sys_user` VALUES (4, 'admin', '$2a$10$XLqCCbAlw5UDZsVpnbVY5OR.AvAsUwNavSCqSrgBXWAN0YRMUpLDC', '超级管理员', '13523525425', '1575215231@qq.com', 1, 1, '2026-04-18 15:31:23', '2026-04-18 15:31:36');
INSERT INTO `sys_user` VALUES (5, 'user1', '$2a$10$j7xenE7SmvtPYsvWeniPnO.b16f83Jc2/NK4W4IQuS0DdAmSmCCnm', '王总', '13223927865', '', 2, 0, '2026-04-19 21:19:32', '2026-04-19 21:19:32');

-- ----------------------------
-- Table structure for user_message
-- ----------------------------
DROP TABLE IF EXISTS `user_message`;
CREATE TABLE `user_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `type` tinyint NOT NULL DEFAULT 1 COMMENT '1=借阅提醒 2=系统通知 3=预约到货',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '0=未读 1=已读',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_is_read`(`is_read` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_message
-- ----------------------------
INSERT INTO `user_message` VALUES (1, 5, '预约到货通知', '您预约的《三体》已到货，请尽快到图书馆取书。取书截止日期：2026-04-26', 3, 1, '2026-04-19 21:29:05');
INSERT INTO `user_message` VALUES (2, 2, '预约到货通知', '您预约的《三体》已到货，请尽快到图书馆取书。取书截止日期：2026-04-26', 3, 1, '2026-04-19 21:32:05');

SET FOREIGN_KEY_CHECKS = 1;
