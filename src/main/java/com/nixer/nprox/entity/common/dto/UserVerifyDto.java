package com.nixer.nprox.entity.common.dto;

import io.swagger.annotations.ApiModelProperty;

public class UserVerifyDto {

    @ApiModelProperty(value="验证码")
    private String vrifyCode;
    @ApiModelProperty(value="解锁方式0手机  1邮箱")
    private String unlockType;


    public String getVrifyCode() {
        return vrifyCode;
    }

    public void setVrifyCode(String vrifyCode) {
        this.vrifyCode = vrifyCode;
    }

    public String getUnlockType() {
        return unlockType;
    }

    public void setUnlockType(String unlockType) {
        this.unlockType = unlockType;
    }
}
