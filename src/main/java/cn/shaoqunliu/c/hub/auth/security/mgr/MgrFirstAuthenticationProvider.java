package cn.shaoqunliu.c.hub.auth.security.mgr;

import cn.shaoqunliu.c.hub.auth.po.DockerAuth;
import cn.shaoqunliu.c.hub.auth.service.DockerUserDetailsService;
import cn.shaoqunliu.c.hub.auth.vo.MgrAccessDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MgrFirstAuthenticationProvider implements AuthenticationProvider {

    private final DockerUserDetailsService dockerUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MgrFirstAuthenticationProvider(DockerUserDetailsService dockerUserDetailsService, PasswordEncoder passwordEncoder) {
        this.dockerUserDetailsService = dockerUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof MgrFirstAuthenticationToken) {
            MgrFirstAuthenticationToken given = (MgrFirstAuthenticationToken) authentication;
            DockerAuth target = dockerUserDetailsService.loadUserDetails(given.getPrincipal().toString());
            if (target.isEnabled() &&
                    passwordEncoder.matches(given.getCredentials().toString(), target.getMpassword())) {
                // username and password matched and user enabled
                MgrAccessDetails accessDetails = new MgrAccessDetails();
                accessDetails.setUid(target.getId());
                accessDetails.setUsername(target.getUsername());
                accessDetails.setAuthorities(dockerUserDetailsService.loadMgrAuthorities(target.getId()));
                given.setAccessDetails(accessDetails);
                given.setAuthenticated(true);
                return given;
            }
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(MgrFirstAuthenticationToken.class);
    }
}
