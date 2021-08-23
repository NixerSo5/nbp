package com.nixer.nprox.entity.common.dto;

import io.swagger.annotations.ApiModelProperty;

public class BindLoginDto {

    @ApiModelProperty(value="账号")
    private String username;

    @ApiModelProperty(value="账号验证码  如果是手机号注册为手机号验证码  如果为邮箱注册为邮箱验证码")
    private String code;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
