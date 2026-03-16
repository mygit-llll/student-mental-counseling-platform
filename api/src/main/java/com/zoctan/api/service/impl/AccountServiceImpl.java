package com.zoctan.api.service.impl;

import com.zoctan.api.core.exception.ServiceException;
import com.zoctan.api.core.jwt.JwtUtil;
import com.zoctan.api.core.service.AbstractService;
import com.zoctan.api.dto.AccountDto;
import com.zoctan.api.dto.AccountWithRole;
import com.zoctan.api.dto.AccountWithRolePermission;
import com.zoctan.api.entity.Account;
import com.zoctan.api.entity.AccountRole;
import com.zoctan.api.mapper.*;
import com.zoctan.api.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zoctan
 * @date 2018/06/09
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class AccountServiceImpl extends AbstractService<Account> implements AccountService {
  @Resource private AccountMapper accountMapper;
  @Resource private AccountRoleMapper accountRoleMapper;
  @Resource private PermissionMapper permissionMapper;
  @Resource private PasswordEncoder passwordEncoder;
  @Resource private JwtUtil jwtUtil;
  // 普通用户角色Id
  private final Long defaultRoleId = 2L;

  @Override
  public List<AccountWithRole> listAllWithRole() {
    return this.accountMapper.listAllWithRole();
  }

  /** 重写save方法，密码加密后再存 */
  @Override
  public void save(final AccountDto accountDto) {
    Account a = this.getBy("name", accountDto.getName());
    if (a != null) {
      throw new ServiceException("用户名已存在");
    } else {
      a = this.getBy("email", accountDto.getEmail());
      if (a != null) {
        throw new ServiceException("邮箱已存在");
      } else {
        // log.info("before password : {}", account.getPassword().trim());
        accountDto.setPassword(this.passwordEncoder.encode(accountDto.getPassword().trim()));
        // log.info("after password : {}", account.getPassword());
        final Account account = new Account();
        BeanUtils.copyProperties(accountDto, account);
        this.accountMapper.insertSelective(account);
        // log.info("Account<{}> id : {}", account.getName(), account.getId());
        this.saveRole(account.getId(), accountDto.getRoleId());
      }
    }
  }

  private void saveRole(final Long accountId, Long roleId) {
    // 如果没有指定角色Id，以默认普通用户roleId保存
    if (roleId == null) {
      roleId = this.defaultRoleId;
    }
    final AccountRole accountRole = new AccountRole();
    accountRole.setAccountId(accountId);
    accountRole.setRoleId(roleId);
    this.accountRoleMapper.insertSelective(accountRole);
  }

  /** 重写update方法 */
  @Override
  public void update(final Account account) {

    // 如果是修改密码操作（由 changePassword 调用），此时 account.getPassword() 已是加密后的值
    if (account.getPassword() != null && !account.getPassword().isEmpty()) {
      // 使旧 token 失效（只要密码字段非空，就认为是改密）
      final Account oldAccount = this.getById(account.getId());
      if (oldAccount != null && !oldAccount.getName().isEmpty()) {
        this.jwtUtil.invalidRedisToken(oldAccount.getName());
        log.info("账户<{}>密码已修改，旧 token 已失效", oldAccount.getName());
      }
    }
    this.accountMapper.updateByPrimaryKeySelective(account);
  }

  @Override
  public List<AccountWithRole> findWithRoleBy(final Map<String, Object> params) {
    return this.accountMapper.findWithRoleBy(params);
  }

  @Override
  public AccountWithRolePermission findDetailBy(final String column, final Object params) {
    final Map<String, Object> map = new HashMap<>(1);
    map.put(column, params);
    return this.accountMapper.findDetailBy(map);
  }

  @Override
  public AccountWithRolePermission findDetailByName(final String name)
      throws UsernameNotFoundException {
    final AccountWithRolePermission account = this.findDetailBy("name", name);
    if (account == null) {
      throw new UsernameNotFoundException("没有找到该用户名");
    }
    if ("超级管理员".equals(account.getRoleName())) {
      // 超级管理员所有权限都有
      account.setPermissionCodeList(this.permissionMapper.listAllCode());
    }
    return account;
  }

  @Override
  public boolean verifyPassword(final String rawPassword, final String encodedPassword) {
    return this.passwordEncoder.matches(rawPassword, encodedPassword);
  }

  @Override
  public void updateLoginTimeByName(final String name) {
    this.accountMapper.updateLoginTimeByName(name);
  }

  //注册
  @Override
  public boolean existsByName(String name) {
    return this.accountMapper.existsByName(name) > 0;
  }

  @Override
  public boolean existsByEmail(String email) {
    return this.accountMapper.existsByEmail(email) > 0;
  }

  @Override
  @Transactional
  public void registerPublicUser(Account account) {
    // 1. 密码加密
    account.setPassword(passwordEncoder.encode(account.getPassword()));

    // 2. 保存账户
    this.accountMapper.insert(account);

    // 3. 关联默认角色（普通用户）
    // 假设 role 表中：admin=1, consultant=2, user=3
    AccountRole ar = new AccountRole();
    ar.setAccountId(account.getId());
    ar.setRoleId(3L); // 固定为普通用户，不可更改！
    this.accountRoleMapper.insert(ar);
  }

  @Override
  public List<Long> findRoleIdsByAccountId(Long accountId) {
    return this.accountMapper.findRoleIdsByAccountId(accountId);
  }

  @Override
  public Account findByName(String name) {
    return this.accountMapper.findByName(name);
  }
}
