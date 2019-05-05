package cn.shaoqunliu.c.hub.auth.po.projection;

public interface DockerPermissionWithoutOwner {

    Integer getId();

    Integer getAction();

    DockerRepositoryWithoutOwner getRepository();
}
