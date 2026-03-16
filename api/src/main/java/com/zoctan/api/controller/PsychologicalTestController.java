package com.zoctan.api.controller;

import com.zoctan.api.core.response.Result;
import com.zoctan.api.core.response.ResultGenerator;
import com.zoctan.api.entity.Account;
import com.zoctan.api.entity.PsychologicalTest;
import com.zoctan.api.entity.UserTestRecord;
import com.zoctan.api.service.AccountService;
import com.zoctan.api.service.PsychologicalTestService;
import com.zoctan.api.util.ContextUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PsychologicalTestController {

    @Resource
    private PsychologicalTestService psychologicalTestService;

    // 获取所有心理测试（公开）
    @GetMapping("/tests")
    public Result<List<PsychologicalTest>> listTests() {
        List<PsychologicalTest> tests = psychologicalTestService.getAllTests();
        return ResultGenerator.genOkResult(tests); // ✅ 改为 genOkResult
    }

    // 获取测试题目详情（公开）
    @GetMapping("/tests/{id}")
    public Result<PsychologicalTest> getTest(@PathVariable Long id) {
        PsychologicalTest test = psychologicalTestService.getTestWithQuestions(id);
        if (test == null) {
            return ResultGenerator.genFailedResult("测试不存在"); // ✅ 改为 genFailedResult
        }
        return ResultGenerator.genOkResult(test); // ✅
    }

    // 提交测试答案（需登录）
    @Resource
    private AccountService accountService;

    @PostMapping("/submit")
    public Result<String> submitTest(@RequestBody UserTestRecord record) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Account currentUser = this.accountService.findByName(currentUsername);
        if (currentUser == null) {
            return ResultGenerator.genFailedResult("用户未登录或已失效");
        }
        record.setUserId(currentUser.getId());
        psychologicalTestService.submitTest(record);
        return ResultGenerator.genOkResult("提交成功");
    }
}