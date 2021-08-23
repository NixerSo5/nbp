package com.nixer.nprox.entity.common.dto;

import io.swagger.annotations.ApiModelProperty;

public class PageFindDto {

    @ApiModelProperty(value="当前页")
    private Integer index;

    @ApiModelProperty(value="页面size")
    private Integer size;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
