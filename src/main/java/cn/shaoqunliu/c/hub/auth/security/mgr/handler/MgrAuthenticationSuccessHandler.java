package cn.shaoqunliu.c.hub.auth.security.mgr.handler;

import cn.shaoqunliu.c.hub.auth.security.common.JwtsUtils;
import cn.shaoqunliu.c.hub.auth.security.mgr.token.MgrAbstractAuthenticationToken;
import cn.shaoqunliu.c.hub.auth.vo.MgrAccessDetails;
import cn.shaoqunliu.c.hub.auth.vo.MgrAuthenticationResult;
import com.alibaba.fastjson.JSON;
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
        MgrAuthenticationResult authenticationResult = new MgrAuthenticationResult();
        if (authentication instanceof MgrAbstractAuthenticationToken) {
            accessDetails = ((MgrAbstractAuthenticationToken) authentication).getAccessDetails();
            authenticationResult.setUid(accessDetails.getUid());
            authenticationResult.setUsername(accessDetails.getUsername());
        }
        String token = JwtsUtils.getBuilder()
                .claim("access", accessDetails)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 48 * 3600 * 1000))
                .compact();
        authenticationResult.setToken(token);
        response.getWriter().println(JSON.toJSONString(authenticationResult));
    }
}
