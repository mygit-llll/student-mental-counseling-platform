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

 Date: 16/03/2026 12:59:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user_test_record
-- ----------------------------
DROP TABLE IF EXISTS `user_test_record`;
CREATE TABLE `user_test_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint UNSIGNED NOT NULL,
  `test_id` bigint NOT NULL,
  `answers` json NOT NULL COMMENT '用户答案，如 {\"1\":\"A\", \"2\":\"B\", ...}',
  `total_score` int NOT NULL,
  `result_text` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '结果解读，如“轻度焦虑”',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `test_id`(`test_id` ASC) USING BTREE,
  CONSTRAINT `user_test_record_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_test_record_ibfk_2` FOREIGN KEY (`test_id`) REFERENCES `psychological_test` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_test_record
-- ----------------------------
INSERT INTO `user_test_record` VALUES (1, 1, 1, '{\"1\": \"2\", \"2\": \"3\", \"3\": \"1\", \"4\": \"4\", \"5\": \"2\"}', 0, '心理状态良好', '2026-03-07 17:21:13');
INSERT INTO `user_test_record` VALUES (2, 1, 1, '{\"1\": \"2\", \"2\": \"2\", \"3\": \"1\", \"4\": \"4\", \"5\": \"2\"}', 0, '心理状态良好', '2026-03-07 17:24:11');
INSERT INTO `user_test_record` VALUES (3, 1, 1, '{\"1\": \"2\", \"2\": \"2\", \"3\": \"1\", \"4\": \"4\", \"5\": \"4\"}', 0, '心理状态良好', '2026-03-07 17:24:44');
INSERT INTO `user_test_record` VALUES (4, 1, 1, '{\"1\": \"B\", \"2\": \"B\", \"3\": \"A\", \"4\": \"D\", \"5\": \"D\"}', 13, '轻度压力，建议放松', '2026-03-07 17:30:58');
INSERT INTO `user_test_record` VALUES (5, 1, 1, '{\"1\": \"B\", \"2\": \"B\", \"3\": \"C\", \"4\": \"B\", \"5\": \"B\"}', 11, '轻度压力，建议放松', '2026-03-07 20:20:40');
INSERT INTO `user_test_record` VALUES (6, 1, 1, '{\"1\": \"D\", \"2\": \"D\", \"3\": \"D\", \"4\": \"D\", \"5\": \"D\"}', 20, '中度焦虑，建议咨询', '2026-03-07 21:24:00');
INSERT INTO `user_test_record` VALUES (7, 1, 1, '{\"1\": \"C\", \"2\": \"C\", \"3\": \"C\", \"4\": \"D\", \"5\": \"D\"}', 17, '轻度压力，建议放松', '2026-03-07 21:39:48');
INSERT INTO `user_test_record` VALUES (8, 1, 1, '{\"1\": \"C\", \"2\": \"C\", \"3\": \"C\", \"4\": \"A\", \"5\": \"B\"}', 12, '轻度压力，建议放松', '2026-03-07 21:40:46');
INSERT INTO `user_test_record` VALUES (9, 1, 1, '{\"1\": \"C\", \"2\": \"C\", \"3\": \"C\", \"4\": \"C\", \"5\": \"D\"}', 16, '轻度压力，建议放松', '2026-03-07 21:52:04');
INSERT INTO `user_test_record` VALUES (10, 1, 1, '{\"1\": \"B\", \"2\": \"C\", \"3\": \"C\", \"4\": \"B\", \"5\": \"B\"}', 12, '轻度压力，建议放松', '2026-03-07 21:53:14');
INSERT INTO `user_test_record` VALUES (11, 1, 1, '{\"1\": \"A\", \"2\": \"A\", \"3\": \"C\", \"4\": \"C\", \"5\": \"B\"}', 10, '轻度压力，建议放松', '2026-03-07 21:56:25');
INSERT INTO `user_test_record` VALUES (12, 1, 1, '{\"1\": \"B\", \"2\": \"B\", \"3\": \"C\", \"4\": \"C\", \"5\": \"B\"}', 12, '轻度压力，建议放松', '2026-03-07 22:00:07');
INSERT INTO `user_test_record` VALUES (13, 1, 1, '{\"1\": \"D\", \"2\": \"B\", \"3\": \"C\", \"4\": \"A\", \"5\": \"B\"}', 12, '轻度压力，建议放松', '2026-03-07 22:02:55');

SET FOREIGN_KEY_CHECKS = 1;
