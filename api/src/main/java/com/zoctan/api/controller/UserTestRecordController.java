package com.zoctan.api.controller;

import com.zoctan.api.core.response.Result;
import com.zoctan.api.core.response.ResultGenerator;
import com.zoctan.api.entity.UserTestRecord;
import com.zoctan.api.service.UserTestRecordService;
import com.zoctan.api.util.ContextUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/my/tests")
public class UserTestRecordController {

    @Resource
    private UserTestRecordService userTestRecordService;

    /**
     * 获取某次测试的结果（含分数、解读、完成时间等）
     */
    @GetMapping("/{testId}/result")
    public Result<UserTestRecord> getTestResult(@PathVariable Long testId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserTestRecord record = userTestRecordService.findUserTestResultByUsernameAndTestId(username, testId);
        if (record == null) {
            return ResultGenerator.genFailedResult("未找到该测试记录");
        }
        return ResultGenerator.genOkResult(record);
    }

    /**
     * 获取当前用户的所有心理测试历史记录
     */
    @GetMapping("/history")
    public Result<List<UserTestRecord>> getMyTestHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<UserTestRecord> records = userTestRecordService.findUserTestHistoryByUsername(username);
        return ResultGenerator.genOkResult(records);
    }
}