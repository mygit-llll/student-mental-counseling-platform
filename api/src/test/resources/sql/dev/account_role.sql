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

 Date: 16/03/2026 20:11:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account_role
-- ----------------------------
DROP TABLE IF EXISTS `account_role`;
CREATE TABLE `account_role`  (
  `account_id` bigint UNSIGNED NOT NULL COMMENT '用户Id',
  `role_id` bigint UNSIGNED NOT NULL COMMENT '角色Id',
  PRIMARY KEY (`account_id`, `role_id`) USING BTREE,
  INDEX `role_id`(`role_id` ASC) USING BTREE,
  CONSTRAINT `account_role_fk_1` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `account_role_fk_2` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of account_role
-- ----------------------------
INSERT INTO `account_role` VALUES (1, 1);
INSERT INTO `account_role` VALUES (2, 2);
INSERT INTO `account_role` VALUES (3, 2);
INSERT INTO `account_role` VALUES (9, 2);
INSERT INTO `account_role` VALUES (10, 2);
INSERT INTO `account_role` VALUES (4, 3);
INSERT INTO `account_role` VALUES (5, 3);
INSERT INTO `account_role` VALUES (6, 3);
INSERT INTO `account_role` VALUES (7, 3);
INSERT INTO `account_role` VALUES (8, 3);

SET FOREIGN_KEY_CHECKS = 1;
