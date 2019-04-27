package cn.shaoqunliu.c.hub.auth.jpa;

import cn.shaoqunliu.c.hub.auth.po.DockerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DockerRepositoryDetailsRepository extends JpaRepository<DockerRepository, Integer> {

    @Query("SELECT dr FROM DockerRepository dr WHERE namespace.id=(SELECT id FROM DockerNamespace WHERE name=:namespace) AND name=:repository")
    DockerRepository getDockerRepositoryByIdentifier(@Param("namespace") String namespace,
                                               @Param("repository") String repository);
}
