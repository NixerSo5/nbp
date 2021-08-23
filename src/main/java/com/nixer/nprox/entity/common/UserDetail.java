package com.nixer.nprox.entity.common;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author : JoeTao
 * createAt: 2018/9/14
 */
public class UserDetail implements UserDetails {
    private long id;
    private String username;
    private String password;
    private Role role;
    private Date lastPasswordResetDate;
    private String lastip;
    private Integer freeze;

    public UserDetail(
            long id,
            String username,
            Role role,
//            Date lastPasswordResetDate,
        String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
      //  this.freeze = freeze;
//        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    public UserDetail(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public UserDetail(long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public UserDetail(long id, String username, String password,Integer freeze) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.freeze = freeze;
    }

    //返回分配给用户的角色列表
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getName()));
        return authorities;
    }

    public long getId() {
        return id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 账户是否未过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     *  账户是否未锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    /**
     * 密码是否未过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public String getLastip() {
        return lastip;
    }

    public void setLastip(String lastip) {
        this.lastip = lastip;
    }

    /** 账户是否激活
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }


    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLastPasswordResetDate(Date lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    public Integer getFreeze() {
        return freeze;
    }

    public void setFreeze(Integer freeze) {
        this.freeze = freeze;
    }
}
