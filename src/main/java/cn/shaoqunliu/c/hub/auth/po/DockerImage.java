package cn.shaoqunliu.c.hub.auth.po;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "docker_images", indexes = {
        @Index(name = "idx_img_rid", columnList = "rid"),
        @Index(name = "unique_images_rid_name", columnList = "rid, name", unique = true)
})
public class DockerImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rid", referencedColumnName = "id")
    private DockerRepository repository;

    private String name;
    private Long size;
    private Date created;
    private String sha256;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }
}
