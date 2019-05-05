package cn.shaoqunliu.c.hub.auth.security.mgr;

import cn.shaoqunliu.c.hub.auth.vo.MgrAccessDetails;
import org.springframework.security.authentication.AbstractAuthenticationToken;


public class MgrFirstAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private Object credential;
    private MgrAccessDetails accessDetails;

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

    public MgrAccessDetails getAccessDetails() {
        return accessDetails;
    }

    public void setAccessDetails(MgrAccessDetails accessDetails) {
        this.accessDetails = accessDetails;
    }
}
