package cn.shaoqunliu.c.hub.auth.security.cli.filter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CliUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public CliUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager, String processesUrl, AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler) {
        setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl(processesUrl);
        setAuthenticationSuccessHandler(successHandler);
        setAuthenticationFailureHandler(failureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = "", password = "";
        String auth = request.getHeader("Authorization");
        if (auth == null) {
            return super.attemptAuthentication(request, response);
        }
        if (auth.toLowerCase().startsWith("basic ")) {
            String[] tokens = extractAndDecodeHeader(auth);
            assert tokens.length == 2;
            username = tokens[0];
            password = tokens[1];
        }
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                username, password);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private String[] extractAndDecodeHeader(String header) {

        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(
                    "Failed to decode basic authentication token");
        }

        String token = new String(decoded, StandardCharsets.UTF_8);

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }
}
