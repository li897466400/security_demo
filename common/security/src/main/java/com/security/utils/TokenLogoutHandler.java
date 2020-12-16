package com.security.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Li
 * @creat 2020-12-13-16:48
 */
public class TokenLogoutHandler implements LogoutHandler {

    private RedisTemplate redisTemplate;
    private TokenManager tokenManager;

    public TokenLogoutHandler(RedisTemplate redisTemplate, TokenManager tokenManager) {
        this.redisTemplate = redisTemplate;
        this.tokenManager = tokenManager;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        /***
         * 具体逻辑是：1、从请求头中获取token，解析获得username
         *            2、将用户信息从权限列表从redis中移除
         */
        String token = request.getHeader("my_token");

        if (!StringUtils.isEmpty(token)) {
            String username = tokenManager.parseToken(token);
            redisTemplate.boundHashOps("user_permissionValues").delete(username);
        }
    }
}
