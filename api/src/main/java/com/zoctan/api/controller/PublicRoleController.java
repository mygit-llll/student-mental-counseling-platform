package com.zoctan.api.controller;

import com.zoctan.api.core.response.Result;
import com.zoctan.api.core.response.ResultGenerator;
import com.zoctan.api.entity.Role;
import com.zoctan.api.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公共角色接口（无需登录）
 */
@Slf4j
@RestController
@RequestMapping("/public")
public class PublicRoleController {

    @Resource
    private RoleService roleService;

    @GetMapping("/roles")
    public Result<List<Role>> getRoles() {
        try {
            List<Role> roles = this.roleService.findAll();
            return ResultGenerator.genOkResult(roles);
        } catch (Exception e) {
            log.error("Failed to load roles", e);
            return ResultGenerator.genFailedResult("加载角色失败: " + e.getMessage());
        }
    }
}