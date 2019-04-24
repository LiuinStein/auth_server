package cn.shaoqunliu.c.hub.auth.security.cli;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class CliAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof CliAuthenticationToken) {
            CliAuthenticationToken token = (CliAuthenticationToken) authentication;
            if (token.getPrincipal().equals("user") && token.getCredentials().equals("pwd")
                    && (new Scope("repository:test/hello-world:pull,push").contains(token.getRequiredScope()))) {
                token.setAuthenticated(true);
                return token;
            }
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(CliAuthenticationToken.class);
    }
}
