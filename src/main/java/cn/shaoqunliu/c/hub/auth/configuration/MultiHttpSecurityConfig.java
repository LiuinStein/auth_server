package cn.shaoqunliu.c.hub.auth.configuration;

import cn.shaoqunliu.c.hub.auth.security.cli.filter.CliUsernamePasswordAuthenticationFilter;
import cn.shaoqunliu.c.hub.auth.security.cli.handler.CliAuthenticationFailureHandler;
import cn.shaoqunliu.c.hub.auth.security.cli.handler.CliAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class MultiHttpSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // universally password encoder
        // do not put it into any sub-configuration-class within this class
        // otherwise it will cause circled dependencies problems
        return new BCryptPasswordEncoder(11);
    }

    @Configuration
    @Order(1)
    public static class DockerRegistryAuthSecurityConfiguration extends WebSecurityConfigurerAdapter {

        private final AuthenticationProvider cliAuthenticationProvider;

        @Autowired
        public DockerRegistryAuthSecurityConfiguration(AuthenticationProvider cliAuthenticationProvider) {
            this.cliAuthenticationProvider = cliAuthenticationProvider;
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            super.configure(auth);
            auth.authenticationProvider(cliAuthenticationProvider);
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
                            UsernamePasswordAuthenticationFilter.class);
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
