package com.zoctan.api.util;

import com.zoctan.api.core.jwt.JwtUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 上下文工具
 *
 * @author Zoctan
 * @date 2018/07/20
 */
public class ContextUtils {
  private ContextUtils() {}

  /**
   * 获取 request
   */
  public static HttpServletRequest getRequest() {
    final ServletRequestAttributes attributes =
            ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
    return attributes == null ? null : attributes.getRequest();
  }

  /**
   * 获取当前登录用户的 Account ID
   */
  public static Long getCurrentAccountId() {
    HttpServletRequest request = getRequest();
    if (request == null) {
      return null;
    }

    JwtUtil jwtUtil = ApplicationContextHolder.getBean(JwtUtil.class);
    if (jwtUtil == null) {
      return null;
    }

    String token = jwtUtil.getTokenFromRequest(request);
    if (token == null) {
      return null;
    }


    io.jsonwebtoken.Claims claims = jwtUtil.getClaimsFromToken(token);
    if (claims == null) {
      return null;
    }

    Object accountIdObj = claims.get("accountId");
    if (accountIdObj instanceof Integer) {
      return ((Integer) accountIdObj).longValue();
    } else if (accountIdObj instanceof Long) {
      return (Long) accountIdObj;
    } else if (accountIdObj instanceof String) {
      try {
        return Long.parseLong((String) accountIdObj);
      } catch (NumberFormatException e) {
        return null;
      }
    }
    return null;
  }
}