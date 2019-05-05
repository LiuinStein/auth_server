package cn.shaoqunliu.c.hub.auth.security.mgr.handler;

import cn.shaoqunliu.c.hub.auth.security.common.JwtsUtils;
import cn.shaoqunliu.c.hub.auth.security.mgr.MgrFirstAuthenticationToken;
import cn.shaoqunliu.c.hub.auth.vo.MgrAccessDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class MgrAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        MgrAccessDetails accessDetails = null;
        if (authentication instanceof MgrFirstAuthenticationToken) {
            accessDetails = ((MgrFirstAuthenticationToken) authentication).getAccessDetails();
        }
        String token = JwtsUtils.getBuilder()
                .claim("access", accessDetails)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 48 * 3600 * 1000))
                .compact();
        response.setHeader("Authorization", token);
    }
}
