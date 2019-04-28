package cn.shaoqunliu.c.hub.auth.po.projection;

import javax.persistence.*;

@Entity
@Table(name = "docker_auth", indexes = {
        @Index(name = "idx_auth_username", columnList = "username"),
        @Index(columnList = "username", unique = true)
})
// Disabled any write database operations through this entity
// it only acts as a table-view lying on application layer
public class DockerAuthNonConfidential {

    @Id
    @Column(insertable = false, updatable = false)
    private Integer id;

    @Column(insertable = false, updatable = false)
    private String username;

    @Column(insertable = false, updatable = false)
    private Boolean enabled;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
