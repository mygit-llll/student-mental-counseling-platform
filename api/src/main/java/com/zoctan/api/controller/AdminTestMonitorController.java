package com.zoctan.api.controller;

import com.zoctan.api.core.response.Result;
import com.zoctan.api.core.response.ResultGenerator;
import com.zoctan.api.entity.UserTestRecord;
import com.zoctan.api.service.UserTestRecordService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/admin/monitor")
public class AdminTestMonitorController {

    @Resource
    private UserTestRecordService userTestRecordService;

    @GetMapping("/high-score-tests")
    public Result<List<UserTestRecord>> getHighScoreTests(
            @RequestParam(defaultValue = "30") Integer threshold) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        boolean isAdmin = false;

        if (principal instanceof User) {
            // 方案：检查权限列表中是否包含 "超级管理员"
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if ("超级管理员".equals(authority.getAuthority())) {
                    isAdmin = true;
                    break;
                }
            }
        }

        if (!isAdmin) {
            return ResultGenerator.genFailedResult("权限不足：仅管理员可访问");
        }

        List<UserTestRecord> records = userTestRecordService.findHighRiskRecordsWithUsername(threshold);
        return ResultGenerator.genOkResult(records);
    }
}