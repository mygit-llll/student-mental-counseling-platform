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

 Date: 16/03/2026 20:11:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户Id',
  `email` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '邮箱',
  `name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '用户名',
  `password` varchar(512) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '密码',
  `register_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `login_time` datetime NULL DEFAULT NULL COMMENT '上一次登录时间',
  `public_key` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'RSA 公钥（PEM 格式，用于 E2EE）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ix_account_name`(`name` ASC) USING BTREE,
  UNIQUE INDEX `ix_account_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of account
-- ----------------------------
INSERT INTO `account` VALUES (1, 'admin@qq.com', 'admin', '$2a$10$wg0f10n.30UbU.9hPpucbef/ya62LdTKs72xJfjxvTFsL0Xaewbra', '2019-07-01 00:00:00', '2026-03-16 20:09:39', NULL);
INSERT INTO `account` VALUES (2, 'editor@qq.com', 'editor', '$2a$10$/m4SgZ.ZFVZ7rcbQpJW2tezmuhf/UzQtpAtXb0WZiAE3TeHxq2DYu', '2019-07-02 00:00:00', '2026-03-15 00:15:30', NULL);
INSERT INTO `account` VALUES (3, 'test@qq.com', 'test', '$2a$10$.0gBYBHAtdkxUUQNg3kII.fqGOngF4BLe8JavthZFalt2QIXHlrhm', '2019-07-03 00:00:00', '2026-03-10 18:12:55', NULL);
INSERT INTO `account` VALUES (4, '3398178699@qq.com', 'test1', '$2a$10$ryAkNfrdnhuOaUuZHV67Gey14OuBRcrXZCGjcVZ.a8tMXtmkR7kAS', '2026-02-13 02:06:51', NULL, NULL);
INSERT INTO `account` VALUES (5, 'test2@example.com', 'test2', '$2a$10$aful5cgyO0GcyqVcZCP.tOS6c.3QmWJ/3yN3ynvZP/ywHaPAdhfuK', '2026-03-04 06:09:49', '2026-03-10 17:41:33', NULL);
INSERT INTO `account` VALUES (6, 'test3@qq.com', 'test3', '$2a$10$b2Pz7PGwewxMbx46AXTsPu6lcwSXLPfL9wJWFR9zM1czHUefQGAXS', '2026-03-04 08:14:44', '2026-03-10 14:01:12', NULL);
INSERT INTO `account` VALUES (7, 'linyixin54@dmail.com', 'test4', '$2a$10$SDtv400vRs9njan98O/0eum1F2BT0siWgR26MU4o5EEng7Dedsmdq', '2026-03-10 10:10:31', '2026-03-15 21:36:58', NULL);
INSERT INTO `account` VALUES (8, 'test5@qq.com', 'test5', '$2a$10$7mqa0GJHvmqfJEwU4W5o8.G/bqkF4xxDTzeFzGdee9kHAJh5Kit9K', '2026-03-15 15:25:26', '2026-03-16 00:10:18', NULL);
INSERT INTO `account` VALUES (9, 'test6@qq.com', 'test6', '$2a$10$NinA1wVPB4xxkkx.ff.OLOsP8iAVIj4uxmBoui/Dcom9hhNHCIXwG', '2026-03-15 16:40:33', '2026-03-16 00:40:48', NULL);
INSERT INTO `account` VALUES (10, 'test7@qq.com', 'test7', '$2a$10$10q5EGEVxuKtSXRVx2GAuO8fxdMUMNJ5TKEOXPvyaqX9SgtAqYXMu', '2026-03-15 16:46:23', '2026-03-16 20:09:25', '-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAm8m/1dyeoEfWkkHpkFAs\n3qLXE6qHKtfgrpTI5RdTDMFfWlv2QZTqBm4jrYwGVytM39x1Vk9LfJY1HJISgF0T\n9BYMys3iaKIMH3qRbHW9ErtTENKG/z6LJq/j13hXpkgzbx8qZ4WWAWLL6bLD3itM\nlRMMroOM9werKiKZW3zaphuAifGiTDsoHnvOQs2UnwZtEqxf26RfcxVbZcFRx2ec\nYu5dWAxytBx3RBdXdoHc9F5nhmjIBQ0xZGzJpb68p/2SgxrFpPkxQkQPxG/kEQ27\ny6GpPpe+VZqCxpORjh5DEcB98GlwQ5btBjyAiEhWynHiqy3vEA6HkFKD1xkC0TaO\n2QIDAQAB\n-----END PUBLIC KEY-----');

SET FOREIGN_KEY_CHECKS = 1;
