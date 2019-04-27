package cn.shaoqunliu.c.hub.auth.po;

import javax.persistence.*;

// po for docker_auth table
@Entity
@Table(name = "docker_auth", indexes = {
        @Index(name = "idx_auth_username", columnList = "username"),
        @Index(columnList = "username", unique = true)
})
public class DockerAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String mpassword;
    private String cpassword;
    private Boolean enabled;

    public String getMpassword() {
        return mpassword;
    }

    public String getUsername() {
        return username;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMpassword(String mpassword) {
        this.mpassword = mpassword;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getCpassword() {
        return cpassword;
    }

    public void setCpassword(String cpassword) {
        this.cpassword = cpassword;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
