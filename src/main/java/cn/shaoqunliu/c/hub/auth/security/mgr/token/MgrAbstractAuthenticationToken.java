package cn.shaoqunliu.c.hub.auth.security.mgr.token;

import cn.shaoqunliu.c.hub.auth.vo.MgrAccessDetails;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public abstract class MgrAbstractAuthenticationToken extends AbstractAuthenticationToken {

    protected MgrAccessDetails accessDetails;

    public MgrAccessDetails getAccessDetails() {
        return accessDetails;
    }

    public void setAccessDetails(MgrAccessDetails accessDetails) {
        this.accessDetails = accessDetails;
    }

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public MgrAbstractAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }
}
