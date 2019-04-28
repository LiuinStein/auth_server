package cn.shaoqunliu.c.hub.auth.security.cli;

import cn.shaoqunliu.c.hub.auth.po.DockerAuth;
import cn.shaoqunliu.c.hub.auth.service.DockerUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CliAuthenticationProvider implements AuthenticationProvider {

    private final DockerUserDetailsService dockerUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CliAuthenticationProvider(DockerUserDetailsService dockerUserDetailsService, PasswordEncoder passwordEncoder) {
        this.dockerUserDetailsService = dockerUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof CliAuthenticationToken) {
            CliAuthenticationToken given = (CliAuthenticationToken) authentication;
            // load user
            DockerAuth target = dockerUserDetailsService.loadUserDetails(
                    given.getPrincipal().toString()
            );
            // target will not be null here due to if the input username not found
            // the user details service will throw UsernameNotFoundException
            // check if user enabled and password matched
            if (target.isEnabled() &&
                    passwordEncoder.matches(given.getCredentials().toString(),
                            target.getCpassword()) &&
                    (given.getRequiredScope() == null ||
                            // check if user have enough permissions
                            dockerUserDetailsService.loadDockerAuthScope(target.getId(),
                                    given.getRequiredScope().getRepository())
                                    .contains(given.getRequiredScope()))

            ) {
                given.setAuthenticated(true);
                return given;
            }
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(CliAuthenticationToken.class);
    }
}
