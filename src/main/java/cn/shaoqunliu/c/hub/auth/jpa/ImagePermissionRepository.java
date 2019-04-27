package cn.shaoqunliu.c.hub.auth.jpa;

import cn.shaoqunliu.c.hub.auth.po.DockerAuth;
import cn.shaoqunliu.c.hub.auth.po.DockerRepository;
import cn.shaoqunliu.c.hub.auth.po.DockerRepositoryPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagePermissionRepository extends JpaRepository<DockerRepositoryPermission, Integer> {

//    @Query("SELECT action FROM DockerRepositoryPermission WHERE uid=:uid AND rid=(SELECT id FROM DockerRepository WHERE nid=(SELECT id FROM DockerNamespace WHERE name=:namespace) AND name=:repository)")
//    Integer getActionByImageIdentifier(@Param("uid") Integer uid,
//                                       @Param("namespace") String namespace,
//                                       @Param("repository") String repository);

    DockerRepositoryPermission getActionByUserAndRepository(DockerAuth user, DockerRepository repository);
}
