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
import cn.shaoqunliu.c.hub.auth.po.projection.DockerNamespaceWithoutOwner;
import cn.shaoqunliu.c.hub.auth.po.projection.DockerPermissionWithoutOwner;
import cn.shaoqunliu.c.hub.auth.po.projection.DockerRepositoryWithoutOwner;
import cn.shaoqunliu.c.hub.auth.security.common.DockerImageIdentifier;
import cn.shaoqunliu.c.hub.auth.security.common.Scope;
import cn.shaoqunliu.c.hub.auth.service.DockerUserDetailsService;
import cn.shaoqunliu.c.hub.auth.vo.MgrAuthorities;
import cn.shaoqunliu.c.hub.auth.vo.authority.Ownership;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        DockerAuth result = username.contains("@") ?
                userDetailsRepository.getDockerAuthByEmail(username) :
                userDetailsRepository.getDockerAuthByUsername(username);
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

    @Override
    public MgrAuthorities loadMgrAuthorities(int uid) {
        MgrAuthorities result = new MgrAuthorities();
        Ownership ownership = new Ownership();
        DockerAuthNonConfidential user = new DockerAuthNonConfidential();
        user.setId(uid);
        // get ownership of namespaces
        List<String> namespace = new ArrayList<>();
        List<DockerNamespaceWithoutOwner> namespaceWithoutOwners =
                namespaceRepository.getDockerNamespacesByOwner(user);
        if (namespaceWithoutOwners != null && namespaceWithoutOwners.size() > 0) {
            namespaceWithoutOwners.forEach(x -> namespace.add(x.getName()));
        }
        ownership.setNamespace(namespace);
        // get ownership of repositories
        List<String> repository = new ArrayList<>();
        List<DockerRepositoryWithoutOwner> repositoryWithoutNamespaceAndOwners =
                repositoryDetailsRepository.getDockerRepositoriesByOwner(user);
        if (repositoryWithoutNamespaceAndOwners != null &&
                repositoryWithoutNamespaceAndOwners.size() > 0) {
            repositoryWithoutNamespaceAndOwners.forEach(
                    x -> repository.add(x.getNamespace().getName() + "/" + x.getName()));
        }
        ownership.setRepository(repository);
        // get user permissions of other repositories
        List<DockerPermissionWithoutOwner> permissionWithoutOwners =
                permissionRepository.getDockerRepositoryPermissionsByUser(user);
        List<String> readOnly = new ArrayList<>();
        List<String> writable = new ArrayList<>();
        if (permissionWithoutOwners != null &&
                permissionWithoutOwners.size() > 0) {
            permissionWithoutOwners.forEach(x -> {
                if (x.getRepository() == null ||
                        x.getRepository().getNamespace() == null) {
                    // goes here when the database integrity got damaged
                    // and should TODO: Logging for the future improvement
                    // here we ignore the situation of bad database integrity,
                    // but for the future, we need to log details here
                    // and fix the data manually,
                    // but normally without destroy it directly through database
                    // intentionally, this problems may never be caused.
                    return;
                }
                String identifier = x.getRepository().getNamespace().getName()
                        + "/" + x.getRepository().getName();
                switch (Scope.Action.valueOf(x.getAction())) {
                    case PULL:
                        // get read-only
                        readOnly.add(identifier);
                        break;
                    case PUSH:
                    case BOTH:
                        writable.add(identifier);
                        break;
                }
            });
        }
        // set result
        result.setOwnership(ownership);
        result.setReadOnly(readOnly);
        result.setWritable(writable);
        return result;
    }
}
