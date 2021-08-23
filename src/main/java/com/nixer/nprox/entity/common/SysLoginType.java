package com.nixer.nprox.entity.common;

import java.io.Serializable;

/**
 * (SysLoginType)实体类
 *
 * @author makejava
 * @since 2021-07-01 18:39:20
 */
public class SysLoginType implements Serializable {
    private static final long serialVersionUID = -19772786169705277L;

    private Integer id;

    private Integer userid;
    /**
     * 0 账号  1手机  2邮箱
     */
    private Integer login_type;

    private String login_name;

    private String sys_username;

    public String getSys_username() {
        return sys_username;
    }

    public void setSys_username(String sys_username) {
        this.sys_username = sys_username;
    }

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

    public Integer getLogin_type() {
        return login_type;
    }

    public void setLogin_type(Integer login_type) {
        this.login_type = login_type;
    }

    public String getLogin_name() {
        return login_name;
    }

    public void setLogin_name(String login_name) {
        this.login_name = login_name;
    }
}
