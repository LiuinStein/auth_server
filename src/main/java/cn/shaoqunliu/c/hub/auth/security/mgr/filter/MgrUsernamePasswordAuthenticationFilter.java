package cn.shaoqunliu.c.hub.auth.security.mgr.filter;

import cn.shaoqunliu.c.hub.auth.security.common.HttpServletRequestUtils;
import cn.shaoqunliu.c.hub.auth.security.mgr.MgrFirstAuthenticationToken;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MgrUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public MgrUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager,
                                                   String processesUrl,
                                                   AuthenticationSuccessHandler successHandler,
                                                   AuthenticationFailureHandler failureHandler) {
        setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl(processesUrl);
        setAuthenticationSuccessHandler(successHandler);
        setAuthenticationFailureHandler(failureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            HttpServletRequestUtils requestUtils = new HttpServletRequestUtils(request);
            if (requestUtils.getMethod() == HttpMethod.POST) {
                // user login
                JSONObject jsonObject = JSONObject.parseObject(requestUtils.getRequestBody());
                MgrFirstAuthenticationToken firstAuthenticationToken = new MgrFirstAuthenticationToken(
                        jsonObject.getString("username"),
                        jsonObject.getString("password")
                );
                return this.getAuthenticationManager().authenticate(firstAuthenticationToken);
            } else if (requestUtils.getMethod() == HttpMethod.GET) {
                // get uid, role, required resources info from http request
                String auth = request.getHeader("Authorization");
                if (auth == null) {
                    return super.attemptAuthentication(request, response);
                }

            }
        } catch (IOException e) {
            throw new BadCredentialsException(e.getMessage());
        }
        return super.attemptAuthentication(request, response);
    }
}
