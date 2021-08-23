package com.nixer.nprox.entity.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
public class FindPassWordDto implements Serializable {

    @ApiModelProperty(value="登陆账号")
    private String username;

    @ApiModelProperty(value="验证码")
    private String verifycode;

    @ApiModelProperty(value="新密码")
    private String new_password;

    @ApiModelProperty(value="1手机 2邮箱")
    private Integer findtype;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVerifycode() {
        return verifycode;
    }

    public void setVerifycode(String verifycode) {
        this.verifycode = verifycode;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public Integer getFindtype() {
        return findtype;
    }

    public void setFindtype(Integer findtype) {
        this.findtype = findtype;
    }
}
