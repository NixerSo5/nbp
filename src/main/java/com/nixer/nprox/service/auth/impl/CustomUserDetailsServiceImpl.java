package com.nixer.nprox.service.auth.impl;

import com.nixer.nprox.dao.AuthDao;
import com.nixer.nprox.dao.SysLoginTypeDao;
import com.nixer.nprox.entity.common.SysLoginType;
import com.nixer.nprox.entity.common.Role;
import com.nixer.nprox.entity.common.UserDetail;
import com.nixer.nprox.tools.StringUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 登陆身份认证
 * @author: JoeTao
 * createAt: 2018/9/14
 */
@Component(value="CustomUserDetailsService")
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private final AuthDao authMapper;
    private final SysLoginTypeDao sysLoginTypeDao;

    public CustomUserDetailsServiceImpl(AuthDao authMapper,SysLoginTypeDao sysLoginTypeDao) {
        this.authMapper = authMapper;
        this.sysLoginTypeDao = sysLoginTypeDao;
    }

    @Override
    public UserDetail loadUserByUsername(String name) throws UsernameNotFoundException {
        UserDetail userDetail =null;
        SysLoginType sysLoginType =null;
        if(!StringUtils.isEmpty(name)){
             sysLoginType = sysLoginTypeDao.findByLoginName(name);
        }
        if(sysLoginType!=null){
             userDetail = authMapper.findByUsername(sysLoginType.getSys_username());
        }
        if (userDetail == null) {
            throw new UsernameNotFoundException(String.format("No userDetail found with username '%s'.", name));
        }
        Role role = authMapper.findRoleByUserId(userDetail.getId());
        userDetail.setRole(role);
        return userDetail;
    }
}
