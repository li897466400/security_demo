package com.security.service;


import com.security.pojo.SecurityUser;
import com.security.pojo.User;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Li
 * @creat 2020-12-12-20:22
 */
@Service("userDetailsService")
public class UserDetailServiceImpl implements UserDetailsService {

    private Map<String, User> userList = new HashMap();  // 使用容器模拟数据库

    private Map<String, Set<String>> permissionValues =new HashMap<>();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询数据库
        User user = userList.get(username);

        if (user == null) {
            throw new UsernameNotFoundException("没有指定用户");
        }
        // 根据用户明，查询权限列表
        Set<String> permissions = permissionValues.get(username);

        // 返回自定义的UserDetails对象
        return new SecurityUser(user, permissions);
    }
}
