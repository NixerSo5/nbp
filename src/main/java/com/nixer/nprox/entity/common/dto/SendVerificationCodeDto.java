package com.nixer.nprox.entity.common.dto;

public class SendVerificationCodeDto {
    private String send;
    private String vrifycode;

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getVrifycode() {
        return vrifycode;
    }

    public void setVrifycode(String vrifycode) {
        this.vrifycode = vrifycode;
    }
}
