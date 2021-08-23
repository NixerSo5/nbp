package com.nixer.nprox.entity.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * (UserInfo)实体类
 *
 * @author makejava
 * @since 2021-06-18 11:24:37
 */
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 857455065390907227L;

    private Integer id;

    private Integer userid;

    private String address;

    private String nickname;

    private String imgurl;

    private String email;

    private String phone;

    private Date utime;

    private String lastip;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public Date getUtime() {
        return utime;
    }

    public void setUtime(Date utime) {
        this.utime = utime;
    }

    public String getLastip() {
        return lastip;
    }

    public void setLastip(String lastip) {
        this.lastip = lastip;
    }
}
