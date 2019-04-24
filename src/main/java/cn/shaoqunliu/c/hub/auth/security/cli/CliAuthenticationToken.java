package cn.shaoqunliu.c.hub.auth.security.cli;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class CliAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private Object credentials;
    private Scope requiredScope;

    public CliAuthenticationToken(Object principal, Object credentials, Scope requiredScope) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.requiredScope = requiredScope;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    public Object getPrincipal() {
        return principal;
    }

    public Scope getRequiredScope() {
        return requiredScope;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CliAuthenticationToken &&
                super.equals(obj) &&
                ((CliAuthenticationToken) obj).getRequiredScope().equals(getRequiredScope());
    }
}
