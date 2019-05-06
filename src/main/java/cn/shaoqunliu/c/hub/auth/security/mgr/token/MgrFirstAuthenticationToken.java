package cn.shaoqunliu.c.hub.auth.security.mgr.token;

import cn.shaoqunliu.c.hub.auth.security.mgr.token.MgrAbstractAuthenticationToken;


public class MgrFirstAuthenticationToken extends MgrAbstractAuthenticationToken {

    private final Object principal;
    private Object credential;

    public MgrFirstAuthenticationToken(Object principal, Object credential) {
        super(null);
        this.principal = principal;
        this.credential = credential;
    }

    @Override
    public Object getCredentials() {
        return credential;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
