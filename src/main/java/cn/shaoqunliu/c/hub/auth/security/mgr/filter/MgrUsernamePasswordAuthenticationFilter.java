package cn.shaoqunliu.c.hub.auth.security.mgr.filter;

import cn.shaoqunliu.c.hub.auth.security.common.HttpServletRequestUtils;
import cn.shaoqunliu.c.hub.auth.security.common.JwtsUtils;
import cn.shaoqunliu.c.hub.auth.security.mgr.token.MgrFirstAuthenticationToken;
import cn.shaoqunliu.c.hub.auth.security.mgr.token.MgrSecondAuthenticationToken;
import cn.shaoqunliu.c.hub.auth.vo.MgrAccessDetails;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
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
import java.util.Map;

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
                String type = request.getParameter("type");
                String identifier = request.getParameter("identifier");
                if (auth == null || type == null || identifier == null) {
                    return super.attemptAuthentication(request, response);
                }
                // check if the Jwt is valid
                Claims claims = JwtsUtils.getClaimsFromToken(auth);
                if (claims == null) {
                    // invalid Jwt
                    return super.attemptAuthentication(request, response);
                }
                Object accessMap = claims.get("access");
                // claims.get will return a mother fucking LinkedHashMap
                // when the required field is also a json object
                if (!(accessMap instanceof Map)) {
                    // invalid Jwt
                    return super.attemptAuthentication(request, response);
                }
                @SuppressWarnings("unchecked")
                MgrAccessDetails accessDetails = new JSONObject((Map) accessMap)
                        .toJavaObject(MgrAccessDetails.class);
                if (accessDetails == null ||
                        accessDetails.getUsername() == null ||
                        accessDetails.getUid() == null) {
                    // invalid Jwt
                    return super.attemptAuthentication(request, response);
                }
                MgrSecondAuthenticationToken secondAuthenticationToken =
                        new MgrSecondAuthenticationToken(accessDetails,
                                MgrSecondAuthenticationToken.ResourceType.resolve(type),
                                identifier);
                return this.getAuthenticationManager().authenticate(secondAuthenticationToken);
            }
        } catch (IOException e) {
            throw new BadCredentialsException(e.getMessage());
        }
        return super.attemptAuthentication(request, response);
    }
}
