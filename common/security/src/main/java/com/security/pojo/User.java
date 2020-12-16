package com.security.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**  用户实体类
 * @author Li
 * @creat 2020-12-13-16:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String username;
    private String password;
    // 权限列表
    private List<String> permissionValues;
}
