package cn.shaoqunliu.c.hub.auth.vo;

public class MgrAccessDetails {

    private Integer uid;
    private String username;
    private MgrAuthorities authorities;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public MgrAuthorities getAuthorities() {
        return authorities;
    }

    public void setAuthorities(MgrAuthorities authorities) {
        this.authorities = authorities;
    }
}
