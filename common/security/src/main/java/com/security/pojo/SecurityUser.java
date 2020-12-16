package com.security.pojo;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**  在验证过程中，会使用UserDetailService去查询数据库，查出的就是UserDetails对象
 * @author Li
 * @creat 2020-12-12-18:32
 */
@Data
public class SecurityUser implements UserDetails {

    // 当前登录用户
    private transient User currentUserInfo;

    // 当前用户权限列表
    private Set<String> permissionValueList;

    public SecurityUser() {
    }

    public SecurityUser(User user, Set<String> permissionValueList) {
        this.currentUserInfo = currentUserInfo;
        this.permissionValueList = permissionValueList;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 将权限列表转化为需要的类型，才能进行比较
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (String permissionValue : permissionValueList) {
            if (!StringUtils.isEmpty(permissionValue)) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(permissionValue);
                authorities.add(authority);
            }
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return currentUserInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return currentUserInfo.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
