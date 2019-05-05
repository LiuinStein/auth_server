package cn.shaoqunliu.c.hub.auth.security.common;

import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public class HttpServletRequestUtils {

    private HttpServletRequest request;

    public HttpServletRequestUtils(HttpServletRequest request) {
        this.request = request;
    }

    public HttpMethod getMethod() {
        return HttpMethod.resolve(request.getMethod());
    }

    public String getRequestBody() throws IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}
