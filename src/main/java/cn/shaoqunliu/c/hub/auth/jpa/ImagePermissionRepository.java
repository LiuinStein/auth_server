package cn.shaoqunliu.c.hub.auth.jpa;

import cn.shaoqunliu.c.hub.auth.po.DockerRepository;
import cn.shaoqunliu.c.hub.auth.po.DockerRepositoryPermission;
import cn.shaoqunliu.c.hub.auth.po.projection.DockerAuthNonConfidential;
import cn.shaoqunliu.c.hub.auth.po.projection.DockerPermissionWithoutOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagePermissionRepository extends JpaRepository<DockerRepositoryPermission, Integer> {

//    @Query("SELECT action FROM DockerRepositoryPermission WHERE uid=:uid AND rid=(SELECT id FROM DockerRepository WHERE nid=(SELECT id FROM DockerNamespace WHERE name=:namespace) AND name=:repository)")
//    Integer getActionByImageIdentifier(@Param("uid") Integer uid,
//                                       @Param("namespace") String namespace,
//                                       @Param("repository") String repository);

    DockerRepositoryPermission getActionByUserAndRepository(DockerAuthNonConfidential user, DockerRepository repository);

    List<DockerPermissionWithoutOwner> getDockerRepositoryPermissionsByUser(DockerAuthNonConfidential user);
}
