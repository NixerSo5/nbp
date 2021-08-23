package com.nixer.nprox.entity.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
@ApiModel
public class ModifyPasswordDto {

    @ApiModelProperty(value="旧密码")
    private String oldPsw;

    @ApiModelProperty(value="新密码")
    private String newPsw;

    @ApiModelProperty(value="短信验证码")
    private String smscode;

    @ApiModelProperty(hidden = true)
    private Long userid;

    @ApiModelProperty(hidden = true)
    private String token;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getOldPsw() {
        return oldPsw;
    }

    public void setOldPsw(String oldPsw) {
        this.oldPsw = oldPsw;
    }

    public String getNewPsw() {
        return newPsw;
    }

    public void setNewPsw(String newPsw) {
        this.newPsw = newPsw;
    }

    public String getSmscode() {
        return smscode;
    }

    public void setSmscode(String smscode) {
        this.smscode = smscode;
    }
}
