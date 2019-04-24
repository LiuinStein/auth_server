package cn.shaoqunliu.c.hub.auth.security.cli.handler;

import cn.shaoqunliu.c.hub.auth.po.DockerAccessDetails;
import cn.shaoqunliu.c.hub.auth.security.cli.CliAuthenticationToken;
import cn.shaoqunliu.c.hub.auth.security.common.JwtsUtils;
import cn.shaoqunliu.c.hub.auth.vo.JwtsAuthenticationResult;
import com.alibaba.fastjson.JSON;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

public class CliAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        List<DockerAccessDetails> access = new ArrayList<>();
        if (authentication instanceof CliAuthenticationToken) {
            access.add(new DockerAccessDetails(
                    ((CliAuthenticationToken) authentication).getRequiredScope())
            );
        }
        String token = JwtsUtils.getBuilder()
                .claim("access", access)
                .setSubject(authentication.getName())
                .compact();
        JwtsAuthenticationResult result = new JwtsAuthenticationResult();
        result.setToken(token);
        result.setIssuedAt(new Date());
        result.setExpiresIn(48 * 3600 * 1000);
        response.getWriter().print(JSON.toJSONString(result));
    }
}
