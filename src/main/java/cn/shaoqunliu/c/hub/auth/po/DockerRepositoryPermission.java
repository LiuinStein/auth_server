package cn.shaoqunliu.c.hub.auth.po;

import cn.shaoqunliu.c.hub.auth.po.projection.DockerAuthNonConfidential;

import javax.persistence.*;

@Entity
@Table(name = "cli_permission", indexes = {
        @Index(name = "unique_permission_uid_rid", columnList = "uid, rid", unique = true)
})
public class DockerRepositoryPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uid", referencedColumnName = "id")
    private DockerAuthNonConfidential user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rid", referencedColumnName = "id")
    private DockerRepository repository;

    private Integer action;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public DockerAuthNonConfidential getUser() {
        return user;
    }

    public void setUser(DockerAuthNonConfidential user) {
        this.user = user;
    }

    public DockerRepository getRepository() {
        return repository;
    }

    public void setRepository(DockerRepository repository) {
        this.repository = repository;
    }
}
