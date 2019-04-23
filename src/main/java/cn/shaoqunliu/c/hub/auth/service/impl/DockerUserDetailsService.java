package cn.shaoqunliu.c.hub.auth.service.impl;

import cn.shaoqunliu.c.hub.auth.po.MyUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("dockerUserDetailsService")
public class DockerUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        if (s.equals("user")) {
            return new MyUserDetails();
        }
        throw new UsernameNotFoundException("User not found.");
    }
}
