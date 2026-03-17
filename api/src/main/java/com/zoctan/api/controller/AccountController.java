package com.zoctan.api.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zoctan.api.core.jwt.JwtUtil;
import com.zoctan.api.core.response.Result;
import com.zoctan.api.core.response.ResultGenerator;
import com.zoctan.api.dto.AccountDto;
import com.zoctan.api.dto.AccountWithRole;
import com.zoctan.api.entity.Account;
import com.zoctan.api.service.AccountService;
import com.zoctan.api.service.impl.AccountDetailsServiceImpl;
import com.zoctan.api.util.ContextUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * @author Zoctan
 * @date 2018/06/09
 */
@RestController
@RequestMapping("/account")
@Validated
public class AccountController {
  @Resource private AccountService accountService;
  @Resource private AccountDetailsServiceImpl userDetailsService;
  @Resource private JwtUtil jwtUtil;
  @Resource private PasswordEncoder passwordEncoder;
  @Resource private com.zoctan.api.mapper.AccountMapper accountMapper;

  @PostMapping
  public Result register(
      @RequestBody @Valid final AccountDto account, final BindingResult bindingResult) {
    // {"name":"123456", "password":"123456", "email": "123456@qq.com"}
    if (bindingResult.hasErrors()) {
      //noinspection ConstantConditions
      final String msg = bindingResult.getFieldError().getDefaultMessage();
      return ResultGenerator.genFailedResult(msg);
    } else {
      this.accountService.save(account);
      return this.getToken(account.getName());
    }
  }

  @PreAuthorize("hasAuthority('account:delete')")
  @DeleteMapping("/{id}")
  public Result delete(@PathVariable final Long id, final Principal principal) {
    final Account dbAccount = this.accountService.getById(id);
    if (dbAccount == null) {
      return ResultGenerator.genFailedResult("用户不存在");
    }
    this.accountService.deleteById(id);
    return ResultGenerator.genOkResult();
  }

  @PostMapping("/password")
  public Result validatePassword(@RequestBody final Account account) {
    final Account dbAccount = this.accountService.getById(account.getId());
    final boolean isValidate =
        this.accountService.verifyPassword(account.getPassword(), dbAccount.getPassword());
    return ResultGenerator.genOkResult(isValidate);
  }

  /** 更新其他账户的资料 */
  @PreAuthorize("hasAuthority('account:update')")
  @PutMapping("/{id}")
  public Result updateOthers(
      @PathVariable final Long id, @RequestBody final Account account, final Principal principal) {
    final Account dbAccount = this.accountService.getById(id);
    if (dbAccount == null) {
      return ResultGenerator.genFailedResult("用户不存在");
    }
    this.accountService.update(account);
    return ResultGenerator.genOkResult();
  }

  /** 更新自己的资料 */
  @PutMapping("/detail")
  public Result updateMe(@RequestBody final Account account) {
    this.accountService.update(account);
    final Account dbAccount = this.accountService.getById(account.getId());
    return this.getToken(dbAccount.getName());
  }

  /** 其他账户的资料 */
  @PreAuthorize("hasAuthority('account:detail')")
  @GetMapping("/{id}")
  public Result detail(@PathVariable final Long id) {
    final Account dbAccount = this.accountService.getById(id);
    return ResultGenerator.genOkResult(dbAccount);
  }

  /** 自己的资料 */
  @GetMapping("/detail")
  public Result detail(final Principal principal) {
    final Account dbAccount = this.accountService.findDetailByName(principal.getName());
    return ResultGenerator.genOkResult(dbAccount);
  }

  /**
   * 获取指定用户的完整信息（含角色等，用于管理员查看详情）
   */
  @PreAuthorize("hasAuthority('account:detail')")
  @GetMapping("/{id}/detail")
  public Result<AccountWithRole> getAccountDetail(@PathVariable final Long id) {
    final AccountWithRole account = this.accountService.findWithRoleById(id);
    if (account == null) {
      return ResultGenerator.genFailedResult("用户不存在");
    }
    return ResultGenerator.genOkResult(account);
  }


  @PreAuthorize("hasAuthority('account:list')")
  @GetMapping
  public Result list(
      @RequestParam(defaultValue = "0") final Integer page,
      @RequestParam(defaultValue = "0") final Integer size) {
    PageHelper.startPage(page, size);
    final List<AccountWithRole> list = this.accountService.listAllWithRole();
    final PageInfo<AccountWithRole> pageInfo = new PageInfo<>(list);
    return ResultGenerator.genOkResult(pageInfo);
  }

  @PreAuthorize("hasAuthority('account:search')")
  @PostMapping("/search")
  public Result search(@RequestBody final Map<String, Object> param) {
    PageHelper.startPage((Integer) param.get("page"), (Integer) param.get("size"));
    final List<AccountWithRole> list = this.accountService.findWithRoleBy(param);
    final PageInfo<AccountWithRole> pageInfo = new PageInfo<>(list);
    return ResultGenerator.genOkResult(pageInfo);
  }

  /*@PostMapping("/token")
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
    // 更新登录时间
    this.accountService.updateLoginTimeByName(account.getName());
    return this.getToken(account.getName());
  }*/

  @DeleteMapping("/token")
  public Result logout(final Principal principal) {
    this.jwtUtil.invalidRedisToken(principal.getName());
    return ResultGenerator.genOkResult();
  }

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

  /** 获取当前用户的个人信息（不含敏感字段） */
  @GetMapping("/profile")
  public Result profile(final Principal principal) {
    final Account account = this.accountService.getBy("name", principal.getName());
    if (account == null) {
      return ResultGenerator.genFailedResult("用户不存在");
    }
    account.setPassword(null); // 隐藏密码
    return ResultGenerator.genOkResult(account);
  }

  /** 修改当前用户的密码 */
  @PutMapping("/profile/password")
  public Result changePassword(
          @RequestBody final Map<String, String> payload,
          final Principal principal) {
    final String oldPassword = payload.get("oldPassword");
    final String newPassword = payload.get("newPassword");

    if (oldPassword == null || newPassword == null || oldPassword.trim().isEmpty() || newPassword.trim().isEmpty()) {
      return ResultGenerator.genFailedResult("原密码或新密码不能为空");
    }

    final Account account = this.accountService.getBy("name", principal.getName());
    if (account == null) {
      return ResultGenerator.genFailedResult("用户不存在");
    }

    if (!this.accountService.verifyPassword(oldPassword, account.getPassword())) {
      return ResultGenerator.genFailedResult("原密码错误");
    }

    // 加密新密码并更新
    account.setPassword(passwordEncoder.encode(newPassword));
    this.accountService.update(account);

    this.jwtUtil.invalidRedisToken(principal.getName());
    return ResultGenerator.genOkResult("密码修改成功，请重新登录");
  }

  @PostMapping("/public-key")
  public Result<Void> uploadPublicKey(@RequestBody Map<String, String> payload) {
    Long currentUserId = ContextUtils.getCurrentAccountId();
    if (currentUserId == null) {
      return ResultGenerator.genFailedResult("未登录");
    }

    String publicKey = payload.get("publicKey");
    if (publicKey == null || publicKey.trim().isEmpty()) {
      return ResultGenerator.genFailedResult("公钥不能为空");
    }

    // 可选：校验是否为 PEM 格式
    if (!publicKey.contains("BEGIN PUBLIC KEY")) {
      return ResultGenerator.genFailedResult("公钥格式无效");
    }

    Account account = new Account();
    account.setId(currentUserId);
    account.setPublicKey(publicKey.trim());
    accountMapper.updateByPrimaryKeySelective(account);

    return ResultGenerator.genOkResult();
  }
}
