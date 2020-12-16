package com.security.filter;

import com.security.pojo.SecurityUser;
import com.security.utils.TokenManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/** 认证过滤器
 * @author Li
 * @creat 2020-12-13-17:15
 */
public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {


    private TokenManager tokenManager;
    private RedisTemplate redisTemplate;
    private AuthenticationManager authenticationManager; // 认证管理工具类

    public TokenLoginFilter(TokenManager tokenManager, RedisTemplate redisTemplate, AuthenticationManager authenticationManager) {
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
        this.authenticationManager = authenticationManager;
        // 设置不只接受POST提交
        this.setPostOnly(false);
        // 设置登录的url以及提交方式
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/admin/login", "POST"));
    }

    // 获取用户名和密码进行验证的方法
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (username == null) {
           username = "";
        }
        if (password == null) {
            password = "";
        }
        // 获取客户端提交过来的用户名和密码，封装成一个标记已认证的Authentication对象 (三个参数的就是已认证的Authentication对象)
        // 交给authenticationManager去验证，它会调用自定义的
        return this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username
                , password
                , new ArrayList<>()));

    }

    // 验证成功后调用的方法
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // 验证成功后，会有一个SecurityUser对象(在userDetailService的方法中存入)
        SecurityUser user= (SecurityUser) authResult.getPrincipal();

        // 根据用户名生成token
        String token = tokenManager.createToken(user.getUsername());

        // 把用户名去权限列表存入redis中
        redisTemplate.boundHashOps("user_permissionValues").put(user.getUsername(), user.getPermissionValueList());

        // 将token写入响应头返回
        response.addHeader("token", token);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(req,res);
    }
}
