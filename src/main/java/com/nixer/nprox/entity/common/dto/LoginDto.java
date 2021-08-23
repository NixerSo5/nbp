package com.nixer.nprox.entity.common.dto;

public class LoginDto extends BindLoginDto{

    private String password;

    private String vrifycode;

    public String getVrifycode() {
        return vrifycode;
    }

    public void setVrifycode(String vrifycode) {
        this.vrifycode = vrifycode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
