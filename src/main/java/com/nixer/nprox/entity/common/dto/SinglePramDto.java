package com.nixer.nprox.entity.common.dto;

import io.swagger.annotations.ApiModelProperty;

public class SinglePramDto {

    @ApiModelProperty(value="操作id")
    private String doid;

    public String getDoid() {
        return doid;
    }

    public void setDoid(String doid) {
        this.doid = doid;
    }
}
