package cn.shaoqunliu.c.hub.auth.security.mgr;

import cn.shaoqunliu.c.hub.auth.jpa.DockerNamespaceRepository;
import cn.shaoqunliu.c.hub.auth.jpa.DockerRepositoryDetailsRepository;
import cn.shaoqunliu.c.hub.auth.jpa.ImagePermissionRepository;
import cn.shaoqunliu.c.hub.auth.po.DockerNamespace;
import cn.shaoqunliu.c.hub.auth.po.DockerRepository;
import cn.shaoqunliu.c.hub.auth.po.DockerRepositoryPermission;
import cn.shaoqunliu.c.hub.auth.po.projection.DockerAuthNonConfidential;
import cn.shaoqunliu.c.hub.auth.security.common.DockerImageIdentifier;
import cn.shaoqunliu.c.hub.auth.security.common.Scope;
import cn.shaoqunliu.c.hub.auth.security.mgr.token.MgrSecondAuthenticationToken;
import cn.shaoqunliu.c.hub.auth.vo.MgrAccessDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class MgrSecondAuthenticationProvider implements AuthenticationProvider {

    private final DockerNamespaceRepository namespaceRepository;
    private final DockerRepositoryDetailsRepository repositoryDetailsRepository;
    private final ImagePermissionRepository permissionRepository;

    @Autowired
    public MgrSecondAuthenticationProvider(DockerNamespaceRepository namespaceRepository, DockerRepositoryDetailsRepository repositoryDetailsRepository, ImagePermissionRepository permissionRepository) {
        this.namespaceRepository = namespaceRepository;
        this.repositoryDetailsRepository = repositoryDetailsRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof MgrSecondAuthenticationToken) {
            MgrSecondAuthenticationToken given =
                    (MgrSecondAuthenticationToken) authentication;
            MgrAccessDetails accessDetails = given.getAccessDetails();
            // replenish authentication
            if (given.getResourceType() == MgrSecondAuthenticationToken.ResourceType.NAMESPACE) {
                // re-authenticate a given namespace
                // check if the user is the owner of given namespace
                DockerNamespace namespace = namespaceRepository.getDockerNamespaceByName(given.getIdentifier());
                if (namespace != null &&
                        namespace.getOwner().getId()
                                .equals(given.getAccessDetails().getUid())) {
                    accessDetails.getAuthorities().getOwnership().addNamespace(namespace.getName());
                    given.setAccessDetails(accessDetails);
                }
            } else if (given.getResourceType() == MgrSecondAuthenticationToken.ResourceType.REPOSITORY) {
                // re-authenticate a given repository
                // check if the user is owner
                DockerImageIdentifier identifier =
                        new DockerImageIdentifier(given.getIdentifier());
                DockerRepository repository = repositoryDetailsRepository
                        .getDockerRepositoryByIdentifier(identifier.getNamespace(),
                                identifier.getRepository());
                if (repository != null) {
                    // the required repository exists
                    if (repository.getOwner().getId()
                            .equals(given.getAccessDetails().getUid())) {
                        // is the owner
                        accessDetails.getAuthorities().getOwnership()
                                .addRepository(repository.retrieveIdentifier());
                    }
                    // check permissions of this user for the required docker repository
                    DockerAuthNonConfidential user = new DockerAuthNonConfidential();
                    user.setId(accessDetails.getUid());
                    DockerRepositoryPermission permission = permissionRepository
                            .getActionByUserAndRepository(user, repository);
                    if (permission != null) {
                        switch (Scope.Action.valueOf(permission.getAction())) {
                            case PULL:
                                accessDetails.getAuthorities()
                                        .addReadOnly(repository.retrieveIdentifier());
                                break;
                            case PUSH:
                            case BOTH:
                                accessDetails.getAuthorities()
                                        .addWritable(repository.retrieveIdentifier());
                                break;
                            default:
                                return null;
                        }
                    }
                }
            }
            given.setAuthenticated(true);
            return given;
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(MgrSecondAuthenticationToken.class);
    }
}
