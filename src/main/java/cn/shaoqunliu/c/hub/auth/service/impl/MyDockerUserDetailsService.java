package cn.shaoqunliu.c.hub.auth.service.impl;

import cn.shaoqunliu.c.hub.auth.jpa.DockerNamespaceRepository;
import cn.shaoqunliu.c.hub.auth.jpa.DockerRepositoryDetailsRepository;
import cn.shaoqunliu.c.hub.auth.jpa.DockerUserDetailsRepository;
import cn.shaoqunliu.c.hub.auth.jpa.ImagePermissionRepository;
import cn.shaoqunliu.c.hub.auth.po.DockerAuth;
import cn.shaoqunliu.c.hub.auth.po.DockerNamespace;
import cn.shaoqunliu.c.hub.auth.po.DockerRepository;
import cn.shaoqunliu.c.hub.auth.po.DockerRepositoryPermission;
import cn.shaoqunliu.c.hub.auth.po.projection.DockerAuthNonConfidential;
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
    private final DockerNamespaceRepository namespaceRepository;
    private final DockerRepositoryDetailsRepository repositoryDetailsRepository;
    private final ImagePermissionRepository permissionRepository;

    @Autowired
    public MyDockerUserDetailsService(DockerUserDetailsRepository userDetailsRepository, DockerNamespaceRepository namespaceRepository, DockerRepositoryDetailsRepository repositoryDetailsRepository, ImagePermissionRepository permissionRepository) {
        this.userDetailsRepository = userDetailsRepository;
        this.namespaceRepository = namespaceRepository;
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
            // if the operated repository not exists,
            // once the user credentials is valid and the namespace corresponded exists
            // we can grant the PUSH permission in order to allow the registry
            // to receive and create such repository.
            // when the docker client do a push operation, the scope within
            // the authentication request is required both PUSH and PULL
            // permission, so that we need to grant the BOTH permission here.
            DockerNamespace namespace = namespaceRepository.getDockerNamespaceByName(identifier.getNamespace());
            if (namespace != null && namespace.getOwner().getId() == uid) {
                // namespace exists and belongs to the current requested user
                // so we allow him to create a new repository within his namespace
                return new Scope(repository, Scope.Action.BOTH);
            }
            // otherwise
            throw new BadCredentialsException("namespace not found or not belongs to you");
        }
        // the owner of this repository or its namespace have full access
        if (uid == dockerRepository.getOwner().getId() ||
                uid == dockerRepository.getNamespace().getOwner().getId()) {
            return new Scope(identifier.getFullRepositoryName(), Scope.Action.BOTH);
        }
        // not the owner
//        Integer action = permissionRepository.getActionByImageIdentifier(uid, identifier.getNamespace(), identifier.getRepository());
        DockerAuthNonConfidential user = new DockerAuthNonConfidential();
        user.setId(uid);
        DockerRepositoryPermission permission = permissionRepository.getActionByUserAndRepository(user, dockerRepository);
        Integer action = 0;
        if (permission != null) {
            action = permission.getAction();
        }
        if (action == 0 || action == Scope.Action.NULL.value()) {
            action = dockerRepository.getOpened() ? Scope.Action.PULL.value() :
                    Scope.Action.NULL.value();
        }
        return new Scope(identifier.getFullRepositoryName(), action);
    }
}
