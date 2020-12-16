package com.security.filter;

import com.security.utils.TokenManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** 授权过滤器
 * @author Li
 * @creat 2020-12-12-17:57
 */
public class TokenAuthenticationFilter extends BasicAuthenticationFilter {
    
    private TokenManager tokenManager;
    private RedisTemplate redisTemplate;

    public TokenAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    public TokenAuthenticationFilter(AuthenticationManager authenticationManager, TokenManager tokenManager
                                       , RedisTemplate redisTemplate) {
        this(authenticationManager);
        this.redisTemplate = redisTemplate;
        this.tokenManager =tokenManager;
    }

    // 使用这个方法，完成token授权登录
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 获取当前认证成功的用户的权限信息
        Authentication authResult = getAuthentication(request);
        
        // 如果当前登录用户有权限，就放入权限上下文中
        if (authResult!=null) {
            SecurityContextHolder.getContext().setAuthentication(authResult);
        }
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        // 从请求头中获取token，并解析出username
        String token = request.getHeader("token");
        if (token!=null) {
            String username = tokenManager.parseToken(token);

            // 根据username从redis中获取权限列表
            List<String> permissionList = (List<String>) redisTemplate.boundHashOps("user_permissionValues").get(token);

            // 把权限列表转化成目的的参数
            Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            for (String permission : permissionList) {
                grantedAuthorities.add(new SimpleGrantedAuthority(permission));
            }

            // 返回一个UsernamePasswordAuthenticationToken对象
            new UsernamePasswordAuthenticationToken(username, token, grantedAuthorities);
        }
        return null;
    }

}
