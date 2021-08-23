package com.nixer.nprox.entity.common.dto;

public class ModifyPasswordDtoExt extends ModifyPasswordDto{


    private String username;

    private String phonenum;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }
}
