package cn.shaoqunliu.c.hub.auth.jpa;

import cn.shaoqunliu.c.hub.auth.po.DockerRepository;
import cn.shaoqunliu.c.hub.auth.po.projection.DockerAuthNonConfidential;
import cn.shaoqunliu.c.hub.auth.po.projection.DockerRepositoryWithoutOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DockerRepositoryDetailsRepository extends JpaRepository<DockerRepository, Integer> {

    @Query("SELECT dr FROM DockerRepository dr WHERE namespace.id=(SELECT id FROM DockerNamespace WHERE name=:namespace) AND name=:repository")
    DockerRepository getDockerRepositoryByIdentifier(@Param("namespace") String namespace,
                                               @Param("repository") String repository);

    List<DockerRepositoryWithoutOwner> getDockerRepositoriesByOwner(DockerAuthNonConfidential owner);
}
