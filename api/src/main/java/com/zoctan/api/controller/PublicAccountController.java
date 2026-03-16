package com.zoctan.api.controller;

import com.zoctan.api.core.jwt.JwtUtil;
import com.zoctan.api.core.response.Result;
import com.zoctan.api.core.response.ResultGenerator;
import com.zoctan.api.dto.RegisterDTO;
import com.zoctan.api.entity.Account;
import com.zoctan.api.service.AccountService;
import com.zoctan.api.service.impl.AccountDetailsServiceImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 公共注册接口（无需登录）
 */
@RestController
@RequestMapping("/public")
@Validated
public class PublicAccountController {

    @Resource
    private AccountService accountService;

    // 邮箱正则（简化版）
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,}$");

    @PostMapping("/register")
    public Result register(@RequestBody @Valid RegisterDTO registerDTO, BindingResult bindingResult) {
        // 1. 参数校验（BindingResult + 手动校验）
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getFieldError().getDefaultMessage();
            return ResultGenerator.genFailedResult(msg);
        }

        // 2. 用户名格式：3-20位，字母数字下划线
        if (!registerDTO.getName().matches("^[a-zA-Z0-9_]{3,20}$")) {
            return ResultGenerator.genFailedResult("用户名只能包含字母、数字、下划线，长度3-20");
        }

        // 3. 邮箱格式
        if (!EMAIL_PATTERN.matcher(registerDTO.getEmail()).matches()) {
            return ResultGenerator.genFailedResult("邮箱格式不正确");
        }

        // 4. 密码长度
        if (registerDTO.getPassword().length() < 6) {
            return ResultGenerator.genFailedResult("密码长度至少6位");
        }

        // 5. 检查用户名/邮箱是否已存在
        if (accountService.existsByName(registerDTO.getName())) {
            return ResultGenerator.genFailedResult("用户名已存在");
        }
        if (accountService.existsByEmail(registerDTO.getEmail())) {
            return ResultGenerator.genFailedResult("邮箱已被注册");
        }

        // 6. 创建账户（密码需加密！）
        Account account = new Account();
        account.setName(registerDTO.getName());
        account.setEmail(registerDTO.getEmail());
        account.setPassword(registerDTO.getPassword()); // Service 层会加密
        account.setRegisterTime(new Timestamp(System.currentTimeMillis()));

        // 7. 调用专门的注册方法（内部固定角色为 user）
        accountService.registerPublicUser(account);

        return ResultGenerator.genOkResult("注册成功，请登录");
    }
    @PostMapping("/login")
    public Result login(@RequestBody final Account account) {
        // {"name":"admin", "password":"admin123"}
        // {"email":"admin@qq.com", "password":"admin123"}
        if (account.getName() == null && account.getEmail() == null) {
            return ResultGenerator.genFailedResult("用户名或邮箱为空");
        }
        if (account.getPassword() == null) {
            return ResultGenerator.genFailedResult("密码为空");
        }
        // 用户名登录
        Account dbAccount = null;
        if (account.getName() != null) {
            dbAccount = this.accountService.getBy("name", account.getName());
            if (dbAccount == null) {
                return ResultGenerator.genFailedResult("用户名错误");
            }
        }
        // 邮箱登录
        if (account.getEmail() != null) {
            dbAccount = this.accountService.getBy("email", account.getEmail());
            if (dbAccount == null) {
                return ResultGenerator.genFailedResult("邮箱错误");
            }
            account.setName(dbAccount.getName());
        }
        // 验证密码
        //noinspection ConstantConditions
        if (!this.accountService.verifyPassword(account.getPassword(), dbAccount.getPassword())) {
            return ResultGenerator.genFailedResult("密码错误");
        }
        // 校验 roleId
        if (account.getRoleId() == null) {
            return ResultGenerator.genFailedResult("请选择角色");
        }

        List<Long> userRoleIds = this.accountService.findRoleIdsByAccountId(dbAccount.getId());
        if (!userRoleIds.contains(account.getRoleId())) {
            return ResultGenerator.genFailedResult("用户未分配该角色");
        }
        // 更新登录时间
        this.accountService.updateLoginTimeByName(account.getName());
        return this.getToken(account.getName());
    }
    @Resource private AccountDetailsServiceImpl userDetailsService;
    @Resource private JwtUtil jwtUtil;
    /** 获得 token */
    private Result getToken(final String name) {
        Account account = this.accountService.getBy("name", name);
        if (account == null) {
            return ResultGenerator.genFailedResult("用户不存在");
        }
        final UserDetails accountDetails = this.userDetailsService.loadUserByUsername(name);
        // 调用新版 sign 方法，传入 accountId
        final String token = this.jwtUtil.sign(
                account.getId(),
                name,
                accountDetails.getAuthorities(),
                true
        );
        return ResultGenerator.genOkResult(token);
    }
}
