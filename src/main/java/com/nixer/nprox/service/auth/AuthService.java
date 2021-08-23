package com.nixer.nprox.service.auth;


import com.nixer.nprox.entity.common.ResponseUserToken;
import com.nixer.nprox.entity.common.SuperLoginDto;
import com.nixer.nprox.entity.common.UserDetail;
import com.nixer.nprox.entity.common.dto.FindPassWordDto;
import com.nixer.nprox.entity.common.dto.ModifyPasswordDto;
import com.nixer.nprox.entity.common.dto.SendVerificationCodeDto;
import com.nixer.nprox.entity.common.dto.UserVerifyDto;
import com.nixer.nprox.tools.ResultJson;

import java.io.IOException;

/**
 * @author: JoeTao
 * createAt: 2018/9/17
 */
public interface AuthService {
    /**
     * 注册用户
     * @param userDetail
     * @param i
     * @return
     */
    UserDetail register(UserDetail userDetail, int i);

    /**
     * 登陆
     * @param username
     * @param password
     * @return
     */
    ResponseUserToken login(String username, String password);

    /**
     * 登出
     * @param token
     */
    void logout(String token);

    /**
     * 刷新Token
     * @param oldToken
     * @return
     */
    ResponseUserToken refresh(String oldToken);

    /**
     * 根据Token获取用户信息
     * @param token
     * @return
     */
    UserDetail getUserByToken(String token);

    ResultJson sendVerificationCode(SendVerificationCodeDto codeDto) throws IOException;

    ResultJson sendEmailVerificationCode(SendVerificationCodeDto codeDto);

    ResponseUserToken login(SuperLoginDto superLoginDto);

    ResultJson modifyPassword(ModifyPasswordDto modifyPasswordDto, String ipaddress);

    ResultJson findPassword(FindPassWordDto modifyPasswordDto, String ipaddress);

    ResultJson userVerify(UserDetail userid, UserVerifyDto userVerifyDto, String ipaddress) throws IOException;

    UserDetail getSysUserByUserId(long id);
}
