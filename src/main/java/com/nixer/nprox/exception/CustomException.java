package com.nixer.nprox.exception;

import com.nixer.nprox.tools.ResultJson;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ResultJson resultJson;

    public CustomException(ResultJson resultJson) {
        this.resultJson = resultJson;
    }
}
