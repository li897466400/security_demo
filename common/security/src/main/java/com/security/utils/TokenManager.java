package com.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import sun.applet.Main;

import java.util.Date;

/**  token处理类
 * @author Li
 * @creat 2020-12-13-16:52
 */
@Component
public class TokenManager {
    // token有效时长，1小时
    private Long tokenEcpiration = 24*60*60*1000L;
    // token的盐
    private String tokenSingKey = "123654";

    // 根据传入的用户名，制作token
    public String createToken (String username) {
        String token = Jwts.builder().setSubject(username) // 将username作为主体传入
                .setExpiration(new Date(System.currentTimeMillis() + tokenEcpiration))
                .signWith(SignatureAlgorithm.HS256, tokenSingKey)
                .compact();

        return token;
    }

    // 解析token获取用户名
    public String parseToken (String token) {
        String username = Jwts.parser().setSigningKey(tokenSingKey)
                .parseClaimsJws(token).getBody().getSubject();
        return username;
    }

}
