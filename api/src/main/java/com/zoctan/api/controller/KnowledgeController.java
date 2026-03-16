package com.zoctan.api.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zoctan.api.core.response.Result;
import com.zoctan.api.core.response.ResultCode;
import com.zoctan.api.core.response.ResultGenerator;
import com.zoctan.api.dto.KnowledgeArticleDTO;
import com.zoctan.api.entity.KnowledgeArticle;
import com.zoctan.api.entity.KnowledgeCategory;
import com.zoctan.api.mapper.AccountRoleMapper;
import com.zoctan.api.mapper.KnowledgeArticleMapper;
import com.zoctan.api.mapper.KnowledgeCategoryMapper;
import com.zoctan.api.service.KnowledgeArticleService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {

    private static final byte[] SECRET_KEY_BYTES =
            "zoctan-spring-boot-vue-admin-jwt-secret-key-2026-secure-long-key!".getBytes(StandardCharsets.UTF_8);
    @Resource
    private AccountRoleMapper accountRoleMapper;

    @Resource
    private KnowledgeArticleService knowledgeArticleService;

    @Resource
    private KnowledgeArticleMapper knowledgeArticleMapper;

    @Resource
    private KnowledgeCategoryMapper knowledgeCategoryMapper;

    @Resource
    private HttpServletRequest request;

    @GetMapping("/public/categories")
    public Result getCategories() {
        List<KnowledgeCategory> categories = knowledgeCategoryMapper.selectAll();
        return ResultGenerator.genOkResult(categories);
    }

    @GetMapping("/public/list")
    public Result listPublic(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId) {
        PageHelper.startPage(page, size);
        KnowledgeArticle example = new KnowledgeArticle();
        example.setStatus((byte) 2);
        if (categoryId != null) {
            example.setCategoryId(categoryId);
        }
        List<KnowledgeArticle> list = knowledgeArticleMapper.select(example);
        PageInfo<KnowledgeArticle> pageInfo = new PageInfo<>(list);
        return ResultGenerator.genOkResult(pageInfo);
    }

    @GetMapping("/public/{id}")
    public Result detail(@PathVariable Long id) {
        KnowledgeArticle article = knowledgeArticleService.getById(id);
        if (article == null || article.getStatus() != 2) {
            return ResultGenerator.genFailedResult(ResultCode.FIND_FAILED, "文章不存在或未发布");
        }
        return ResultGenerator.genOkResult(article);
    }

    @PostMapping
    public Result create(@RequestBody KnowledgeArticle article) {
        if (article.getImages() != null && !article.getImages().trim().isEmpty()) {
            try {
                List<?> urls = com.alibaba.fastjson.JSON.parseArray(article.getImages());
                if (urls.size() > 5) {
                    return ResultGenerator.genFailedResult(ResultCode.SAVE_FAILED, "图片最多上传5张");
                }
            } catch (Exception e) {
                return ResultGenerator.genFailedResult(ResultCode.SAVE_FAILED, "图片格式错误");
            }
        }

        Long currentUserId = getCurrentAccountId();
        if (currentUserId == null) {
            return ResultGenerator.genFailedResult(ResultCode.UNAUTHORIZED_EXCEPTION, "用户未登录或 token 无效");
        }

        article.setAuthorId(currentUserId);
        article.setStatus((byte) 1);
        article.setCreateTime(new Date());
        knowledgeArticleService.save(article);
        return ResultGenerator.genOkResult();
    }

    @PutMapping("/{id}/audit")
    public Result audit(@PathVariable Long id, @RequestParam Byte result) {
        if (result != 2 && result != 3) {
            return ResultGenerator.genFailedResult(ResultCode.UPDATE_FAILED, "审核结果无效");
        }

        KnowledgeArticle article = knowledgeArticleService.getById(id);
        if (article == null || article.getStatus() != 1) {
            return ResultGenerator.genFailedResult(ResultCode.FIND_FAILED, "文章不存在或无需审核");
        }

        article.setStatus(result);
        if (result == 2) {
            article.setPublishTime(new Date());
        }
        knowledgeArticleService.update(article);
        return ResultGenerator.genOkResult();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        Long currentUserId = getCurrentAccountId();
        if (currentUserId == null) {
            return ResultGenerator.genFailedResult(ResultCode.UNAUTHORIZED_EXCEPTION, "请先登录");
        }

        KnowledgeArticle article = knowledgeArticleService.getById(id);
        if (article == null) {
            return ResultGenerator.genFailedResult(ResultCode.FIND_FAILED, "文章不存在");
        }

        // 判断是否是超级管理员
        boolean isAdmin = isSuperAdmin();
        // 判断是否是作者本人
        boolean isAuthor = article.getAuthorId().equals(currentUserId);

        if (!isAdmin && !isAuthor) {
            return ResultGenerator.genFailedResult(ResultCode.UNAUTHORIZED_EXCEPTION, "仅作者或管理员可删除该文章");
        }

        // 执行删除（逻辑删除）
        article.setStatus((byte) 4);
        knowledgeArticleService.update(article);
        return ResultGenerator.genOkResult();
    }

    private Long getCurrentAccountId() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(SECRET_KEY_BYTES) // byte[] 直接传入
                        .parseClaimsJws(token)
                        .getBody();
                Object accountId = claims.get("accountId");
                if (accountId instanceof Integer) {
                    return ((Integer) accountId).longValue();
                } else if (accountId instanceof Long) {
                    return (Long) accountId;
                } else if (accountId instanceof String) {
                    return Long.parseLong((String) accountId);
                }
            } catch (Exception e) {
                // 签名无效、过期、格式错误等
                return null;
            }
        }
        return null;
    }
    private boolean isSuperAdmin() {
        Long accountId = getCurrentAccountId();
        if (accountId == null) return false;

        // 查询该用户的所有 role_id
        List<Long> roleIds = accountRoleMapper.selectRoleIdsByAccountId(accountId);
        return roleIds != null && roleIds.contains(1L); // role_id = 1 是超级管理员
    }
    // 管理员查看待审核文章列表
    @GetMapping("/admin/list")
    public Result listForAdmin(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,      // 模糊匹配标题
            @RequestParam(required = false) Long authorId,     // 作者ID
            @RequestParam(required = false) Byte status) {     // 状态：1/2/3/4

        // 权限校验：必须是超级管理员（role_id = 1）
        if (!isSuperAdmin()) {
            return ResultGenerator.genFailedResult(ResultCode.UNAUTHORIZED_EXCEPTION, "仅超级管理员可操作");
        }

        PageHelper.startPage(page, size);

        // 构建查询条件（使用 KnowledgeArticle 作为 example 有局限，建议用自定义 SQL）
        // 更好的做法是写自定义 Mapper 方法（见下方说明）

        KnowledgeArticle example = new KnowledgeArticle();
        if (status != null) {
            example.setStatus(status);
        }

        // 自定义查询方法
        List<KnowledgeArticleDTO> list = knowledgeArticleMapper.selectForAdminWithDetails(title, authorId, status);
        PageInfo<KnowledgeArticleDTO> pageInfo = new PageInfo<>(list);
        return ResultGenerator.genOkResult(pageInfo);
    }

    @GetMapping("/admin/detail/{id}")
    public Result adminDetail(@PathVariable Long id) {
        // 权限校验：必须是超级管理员
        if (!isSuperAdmin()) {
            return ResultGenerator.genFailedResult(ResultCode.UNAUTHORIZED_EXCEPTION, "仅超级管理员可查看");
        }

        KnowledgeArticle article = knowledgeArticleService.getById(id);
        if (article == null) {
            return ResultGenerator.genFailedResult(ResultCode.FIND_FAILED, "文章不存在");
        }

        // 注意：这里 **不检查 status**，允许查看任何状态的文章
        return ResultGenerator.genOkResult(article);
    }
}