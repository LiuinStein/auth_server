package cn.shaoqunliu.c.hub.auth.service;

import cn.shaoqunliu.c.hub.auth.po.DockerAuth;
import cn.shaoqunliu.c.hub.auth.security.common.Scope;
import cn.shaoqunliu.c.hub.auth.vo.MgrAuthorities;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface DockerUserDetailsService {

    DockerAuth loadUserDetails(String username) throws UsernameNotFoundException;

    Scope loadDockerAuthScope(int uid, String repository) throws BadCredentialsException;

    MgrAuthorities loadMgrAuthorities(int uid);
}
