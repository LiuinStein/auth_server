package cn.shaoqunliu.c.hub.auth.jpa;

import cn.shaoqunliu.c.hub.auth.po.DockerAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DockerUserDetailsRepository extends JpaRepository<DockerAuth, Integer> {

    DockerAuth getDockerAuthByUsername(String username);

    DockerAuth getDockerAuthByEmail(String email);
}
