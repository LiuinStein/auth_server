package cn.shaoqunliu.c.hub.auth.service.impl;

import cn.shaoqunliu.c.hub.auth.jpa.DockerRepositoryDetailsRepository;
import cn.shaoqunliu.c.hub.auth.jpa.DockerUserDetailsRepository;
import cn.shaoqunliu.c.hub.auth.jpa.ImagePermissionRepository;
import cn.shaoqunliu.c.hub.auth.po.DockerAuth;
import cn.shaoqunliu.c.hub.auth.po.DockerRepository;
import cn.shaoqunliu.c.hub.auth.po.DockerRepositoryPermission;
import cn.shaoqunliu.c.hub.auth.security.common.DockerImageIdentifier;
import cn.shaoqunliu.c.hub.auth.security.common.Scope;
import cn.shaoqunliu.c.hub.auth.service.DockerUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("myDockerUserDetailsService")
public class MyDockerUserDetailsService implements DockerUserDetailsService {

    private final DockerUserDetailsRepository userDetailsRepository;
    private final DockerRepositoryDetailsRepository repositoryDetailsRepository;
    private final ImagePermissionRepository permissionRepository;

    @Autowired
    public MyDockerUserDetailsService(DockerUserDetailsRepository userDetailsRepository, DockerRepositoryDetailsRepository repositoryDetailsRepository, ImagePermissionRepository permissionRepository) {
        this.userDetailsRepository = userDetailsRepository;
        this.repositoryDetailsRepository = repositoryDetailsRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public DockerAuth loadUserDetails(String username) throws UsernameNotFoundException {
        DockerAuth result = userDetailsRepository.getDockerAuthByUsername(username);
        if (result == null) {
            throw new UsernameNotFoundException("user not found");
        }
        return result;
    }

    @Override
    public Scope loadDockerAuthScope(int uid, String repository) throws BadCredentialsException {
        DockerImageIdentifier identifier = new DockerImageIdentifier(repository);
        DockerRepository dockerRepository = repositoryDetailsRepository.getDockerRepositoryByIdentifier(identifier.getNamespace(), identifier.getRepository());
        // check if the repository exists
        if (dockerRepository == null) {
            throw new BadCredentialsException("repository not found");
        }
        // the owner of this repository or its namespace have full access
        if (uid == dockerRepository.getOwner().getId() ||
                uid == dockerRepository.getNamespace().getOwner().getId()) {
            return new Scope(identifier.getFullRepositoryName(), Scope.Action.BOTH);
        }
        // not the owner
//        Integer action = permissionRepository.getActionByImageIdentifier(uid, identifier.getNamespace(), identifier.getRepository());
        DockerAuth user = new DockerAuth();
        user.setId(uid);
        DockerRepositoryPermission permission = permissionRepository.getActionByUserAndRepository(user, dockerRepository);
        if (permission != null) {
            Integer action = permission.getAction();
            if (action == null || action == Scope.Action.NULL.value()) {
                action = dockerRepository.getOpened() ? Scope.Action.PULL.value() :
                        Scope.Action.NULL.value();
            }
        }
        return new Scope(identifier.getFullRepositoryName(), 1);
    }
}
