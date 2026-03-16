/*
 Navicat Premium Dump SQL

 Source Server         : springjdbc
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3306
 Source Schema         : psych

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 16/03/2026 20:13:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for psychological_question
-- ----------------------------
DROP TABLE IF EXISTS `psychological_question`;
CREATE TABLE `psychological_question`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `test_id` bigint NOT NULL COMMENT '所属测试ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目内容，如“我感到紧张”',
  `score_map` json NOT NULL COMMENT '选项分值映射，如 {\"A\":1, \"B\":2, \"C\":3, \"D\":4}',
  `options` json NOT NULL COMMENT '选项列表，如 [\"从不\", \"偶尔\", \"经常\", \"总是\"]',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序序号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `test_id`(`test_id` ASC) USING BTREE,
  CONSTRAINT `psychological_question_ibfk_1` FOREIGN KEY (`test_id`) REFERENCES `psychological_test` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of psychological_question
-- ----------------------------
INSERT INTO `psychological_question` VALUES (1, 1, '我感到紧张或容易烦躁', '{\"A\": 1, \"B\": 2, \"C\": 3, \"D\": 4}', '[\"从不\", \"偶尔\", \"经常\", \"总是\"]', 1);
INSERT INTO `psychological_question` VALUES (2, 1, '我感到坐立不安，难以放松', '{\"A\": 1, \"B\": 2, \"C\": 3, \"D\": 4}', '[\"从不\", \"偶尔\", \"经常\", \"总是\"]', 2);
INSERT INTO `psychological_question` VALUES (3, 1, '我容易心慌或心跳加速', '{\"A\": 1, \"B\": 2, \"C\": 3, \"D\": 4}', '[\"从不\", \"偶尔\", \"经常\", \"总是\"]', 3);
INSERT INTO `psychological_question` VALUES (4, 1, '我担心有不好的事情发生', '{\"A\": 1, \"B\": 2, \"C\": 3, \"D\": 4}', '[\"从不\", \"偶尔\", \"经常\", \"总是\"]', 4);
INSERT INTO `psychological_question` VALUES (5, 1, '我难以集中注意力', '{\"A\": 1, \"B\": 2, \"C\": 3, \"D\": 4}', '[\"从不\", \"偶尔\", \"经常\", \"总是\"]', 5);

SET FOREIGN_KEY_CHECKS = 1;
