package cn.shaoqunliu.c.hub.auth.jpa;

import cn.shaoqunliu.c.hub.auth.po.DockerNamespace;
import cn.shaoqunliu.c.hub.auth.po.projection.DockerAuthNonConfidential;
import cn.shaoqunliu.c.hub.auth.po.projection.DockerNamespaceWithoutOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DockerNamespaceRepository extends JpaRepository<DockerNamespace, Integer> {

    DockerNamespace getDockerNamespaceByName(String name);

    List<DockerNamespaceWithoutOwner> getDockerNamespacesByOwner(DockerAuthNonConfidential owner);
}
