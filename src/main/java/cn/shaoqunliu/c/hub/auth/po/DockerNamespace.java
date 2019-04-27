package cn.shaoqunliu.c.hub.auth.po;

import javax.persistence.*;

@Entity
@Table(name = "docker_namespace", indexes = {
        @Index(name = "unique_namespace_name", columnList = "name", unique = true)
})
public class DockerNamespace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner", referencedColumnName = "id")
    private DockerAuth owner;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DockerAuth getOwner() {
        return owner;
    }

    public void setOwner(DockerAuth owner) {
        this.owner = owner;
    }
}
