package com.nixer.nprox.entity.common;

import com.nixer.nprox.entity.common.dto.LoginDto;

public class SuperLoginDto extends LoginDto {


    private String lastip;

    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastip() {
        return lastip;
    }

    public void setLastip(String lastip) {
        this.lastip = lastip;
    }
}
