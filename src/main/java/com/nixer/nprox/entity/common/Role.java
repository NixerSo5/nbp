package com.nixer.nprox.entity.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Role {
    private Long id;
    private String name;
    private String nameZh;
}
