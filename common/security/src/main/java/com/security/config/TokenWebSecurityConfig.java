package com.security.config;
import com.security.filter.TokenAuthenticationFilter;
import com.security.filter.TokenLoginFilter;
import com.security.utils.TokenLogoutHandler;
import com.security.utils.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** 核心配置类
 * @author Li
 * @creat 2020-12-12-19:45
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TokenWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private TokenManager tokenManager;
    private UserDetailsService userDetailsService;
    private RedisTemplate redisTemplate;
    private BCryptPasswordEncoder defaultPasswordEncoder;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public TokenWebSecurityConfig(TokenManager tokenManager, BCryptPasswordEncoder defaultPasswordEncoder,
                                  UserDetailsService userDetailsService, RedisTemplate redisTemplate) {
        this.tokenManager = tokenManager;
        this.defaultPasswordEncoder = defaultPasswordEncoder;
        this.userDetailsService = userDetailsService;
        this.redisTemplate = redisTemplate;
    }

    // 自定义过滤链
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .and().csrf().disable()  // 不开启csrf防护

                .authorizeRequests()
                .anyRequest().authenticated()
                .and().logout().logoutUrl("/admin/acl/index/logout")  //设置用户注销转跳url
                .addLogoutHandler(new TokenLogoutHandler(redisTemplate, tokenManager)) // 用户退出处理器

                .and()
                .addFilter(new TokenLoginFilter(tokenManager, redisTemplate,authenticationManager())) //设置认证过滤器
                // 设置授权过滤器
                .addFilter(new TokenAuthenticationFilter(authenticationManager(), tokenManager, redisTemplate))
                .httpBasic();

    }

    // 自定义userDetailsService和密码处理器
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(defaultPasswordEncoder);
    }

    // 设置免认证的路径(也可以通过HttpSecurity设置)
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/api/**","/aa/**");
    }
}
