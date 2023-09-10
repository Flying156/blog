package com.fly.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

/**
 * 权限赋予
 * @author Milk
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomizedGrantedAuthority implements GrantedAuthority {

    private String role;

    @Override
    public String getAuthority() {
        return this.role;
    }

}
