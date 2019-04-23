package cn.shaoqunliu.c.hub.auth;

import cn.shaoqunliu.c.hub.auth.security.common.JwtsUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthServerApplication {

    public static void main(String[] args) throws Exception {
        JwtsUtils.init();
        SpringApplication.run(AuthServerApplication.class, args);
    }

}

