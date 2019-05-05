package cn.shaoqunliu.c.hub.auth.po.projection;

public interface DockerRepositoryWithoutOwner {

    Integer getId();

    String getName();

    DockerNamespaceWithoutOwner getNamespace();
}
