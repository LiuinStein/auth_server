package cn.shaoqunliu.c.hub.auth.configuration;

import cn.shaoqunliu.c.hub.auth.security.cli.filter.CliUsernamePasswordAuthenticationFilter;
import cn.shaoqunliu.c.hub.auth.security.cli.handler.CliAuthenticationFailureHandler;
import cn.shaoqunliu.c.hub.auth.security.cli.handler.CliAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class MultiHttpSecurityConfig {

    @Configuration
    @Order(1)
    public static class DockerRegistryAuthSecurityConfiguration extends WebSecurityConfigurerAdapter {

        private final UserDetailsService dockerUserDetailsService;

        @Autowired
        public DockerRegistryAuthSecurityConfiguration(UserDetailsService dockerUserDetailsService) {
            this.dockerUserDetailsService = dockerUserDetailsService;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new PasswordEncoder() {
                @Override
                public String encode(CharSequence charSequence) {
                    return charSequence.toString();
                }

                @Override
                public boolean matches(CharSequence charSequence, String s) {
                    return s.equals(charSequence.toString());
                }
            };
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/v1/auth/token**")
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/v1/auth/token").authenticated()
                    .and()
                    .addFilterAt(new CliUsernamePasswordAuthenticationFilter(
                                    authenticationManager(),
                                    "/v1/auth/token",
                                    new CliAuthenticationSuccessHandler(), new CliAuthenticationFailureHandler()),
                            UsernamePasswordAuthenticationFilter.class)
                    .userDetailsService(dockerUserDetailsService);
        }
    }

    @Configuration
    @Order(2)
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/v1/auth/login**")
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                    .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/v1/auth/login").permitAll();
        }
    }

    @Configuration
    public static class Others extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                    .and()
                    .authorizeRequests()
                    .anyRequest().denyAll();
        }
    }

}
