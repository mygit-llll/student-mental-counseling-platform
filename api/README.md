# 项目简介

本项目为大学生心理辅导平台后端服务，基于 Spring Boot 构建，
提供用户管理、心理咨询预约、文章发布、心理测评等功能接口。

系统采用 RESTful API 设计，前端通过 HTTP 调用接口完成业务交互。

## 技术栈

- 核心框架：Spring Boot 2.1.6
- 开发语言：Java 8
- 构建工具：Maven
- Web 容器：Jetty（替代默认 Tomcat）
- 安全认证：
        Spring Security
        JWT（JSON Web Token） + RSA256 非对称加密
        密码配置使用 Jasypt 加密
- 数据访问层：
        MyBatis 1.3.2
        通用 Mapper（tk.mybatis） —— 注意：非 MyBatis-Plus
        PageHelper 分页插件
- 数据库：MySQL 8.0（Druid 连接池）
- 缓存：Redis（Jedis 客户端）
- 工具库：
        Lombok（简化 POJO）
        FastJSON（JSON 序列化）
        Guava、Apache Commons（Lang3 / Codec / BeanUtils）
        Bouncy Castle（RSA 密钥处理）
- API 文档：Swagger2（springfox）
- 热部署：Spring Boot DevTools
- 代码生成：MyBatis Generator + FreeMarker 模板

## 系统架构

系统采用经典三层架构：
- Controller  控制层（处理 HTTP 请求）
- Service     业务层（核心业务逻辑）
- Mapper/DAO  数据访问层（数据库操作）
### 大概结构
```bash
Application.java
│
├── controller ← 接口层
├── service ← 业务层
├── mapper ← 数据访问层
├── entity ← 数据模型
├── dto ← 组合查询模型
│
├── core ← 框架级支持（安全/缓存/异常）
│
├── filter ← JWT + Spring Security 过滤器
├── aspect ← AOP 日志
└── util ← 工具类
```
## 数据库设计

本系统采用 RBAC（基于角色的访问控制） 模型管理权限，并围绕大学生心理健康服务设计核心业务表。所有敏感数据（如咨询消息）均通过 AES 加密存储，保障用户隐私。

权限管理（5 张表）
- account	用户主表，存储学生/咨询师账号信息（用户名、邮箱等

- role	角色表（如 ROLE_ADMIN, ROLE_COUNSELOR, ROLE_STUDENT）
- account_role	用户-角色关联表，支持一用户多角色
- permission	权限资源表，定义可操作的 API 资源（如 user:list, record:delete）
- role_permission	角色-权限关联表，控制角色可访问的资源

心理测评模块（2 张表）
- psychological_test	心理测试模板表（如“焦虑自评量表”），含标题、描述、题目总数
- psychological_question	测试题目表，包含题干、选项（JSON）、选项分值映射（JSON）

测评记录（1 张表）
- user_test_record	用户测评记录表，存储：用户答案（JSON）、总得分、结果解读（如“轻度焦虑”）

心理咨询模块（2 张表）
- consultation_session	咨询会话表，记录学生与咨询师的配对关系及会话状态
- consultation_message	加密消息表，存储双方通信内容：消息体为 AES 加密后的 Base64 文本、会话密钥通过双方公钥加密保护

心理知识库（2 张表）
- knowledge_category	知识分类表（如“情绪管理”、“人际关系”）
- knowledge_article	知识文章表，支持富文本、图片（JSON 存储）、审核状态（待审/已发布）

ER 关系简图
```bash
account ──┬── account_role ── role ── role_permission ── permission
          │
          ├── user_test_record ── psychological_test ── psychological_question
          │
          └── consultation_session ── consultation_message
          
knowledge_category ── knowledge_article
```
## 数据库配置

创建数据库
>表结构都放在 psychology-backend\api\src\test\resources\sql\dev 导入即可

## 启动项目
```bash
1.修改数据库、redis、接口文档等配置

2.后端目录命令行运行：mvn spring-boot:run
```
## 核心功能

### 用户模块（学生端）
- 用户注册 / 登录	支持手机号/邮箱注册，JWT 身份认证
- 心理测评	在线完成标准化量表（如 SCL-90），自动生成结果解读
- 在线咨询	实时聊天咨询，消息端到端 AES 加密
- 心理健康知识浏览	查阅分类知识库文章（情绪管理、人际关系等）
- 个人中心	查看测评记录、咨询历史、个人信息
### 咨询师模块（专业端）
- 咨询记录填写	完整记录每次会话内容，支持富文本编辑
- 咨询统计	查看咨询次数、学生反馈、服务时长等数据分析
### 管理员模块（后台）
- 用户管理	查看/禁用用户账号，处理异常行为
- 咨询师管理	审核、分配、监控咨询师资质与工作状态
- 系统公告发布	向全体用户推送重要通知
- 敏感数据监控	实时监控高风险行为并告警


## 角色权限控制

Spring Security + Json Web Token 鉴权：

最终效果，在控制器上的注解：

```java
@PreAuthorize("hasAuthority('user:list')")
```

实现思路：用户登录 -> 服务端生成 token -> 客户端保存 token，之后的每次请求都携带该 token，服务端认证解析。

所以在服务端认证解析的 token 就要保存有用户的角色和相应的权限：

```java
// service/impl/UserDetailsServiceImpl.java

// 为了方便，角色和权限都放在一起
// 权限
List<SimpleGrantedAuthority> authorities =
        user.getPermissionCodeList().stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
// 角色
authorities.add(new SimpleGrantedAuthority(user.getRoleName()));
// [ROLE_TEST, role:list, user:list]
```

JWT 生成 token：

```java
// core/jwt/JwtUtil.java

Jwts.builder()
                // 设置账户名
                .setSubject(name)
                // 设置账户ID
                .claim("accountId", accountId)
                // 添加权限属性
                .claim(this.jwtProperties.getClaimKeyAuth(), authorities)
                // 设置失效时间
                .setExpiration(expireDate)
                // 私钥加密生成签名
                //.signWith(SignatureAlgorithm.RS256, this.rsaUtils.loadPrivateKey())
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                // 使用LZ77算法与哈夫曼编码结合的压缩算法进行压缩
                .compressWith(CompressionCodecs.DEFLATE)
                .compact();
```

Base64 解码 JWT 生成的 token

之后的控制器就可以使用 hasAuthority 和 hasRole 注解控制权限访问了：

```java
@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('user:list')")
```

## axios 预请求和跨域

由于前后端分离，会出现跨域问题，参考[跨域资源共享 CORS 详解](http://www.ruanyifeng.com/blog/2016/04/cors)。

```java
// core/jwt/JwtAuthenticationFilter.java

// 解决跨域问题
response.setHeader("Access-Control-Allow-Origin", "*");
response.setHeader("Access-Control-Allow-Credentials", "true");
response.setHeader("Access-Control-Allow-Headers", "Content-Type, Content-Length, Authorization, Accept, X-Requested-With");

// 明确允许通过的方法，不建议使用*
response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
response.setHeader("Access-Control-Max-Age", "3600");
response.setHeader("Access-Control-Expose-Headers", "*");

// 预请求后，直接返回
// 返回码必须为 200 否则视为请求失败
if (request.getMethod().equals("OPTIONS")) {
    return;
}
```
