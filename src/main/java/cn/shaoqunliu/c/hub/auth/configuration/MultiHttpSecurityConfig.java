package cn.shaoqunliu.c.hub.auth.configuration;

import cn.shaoqunliu.c.hub.auth.security.cli.CliAuthenticationProvider;
import cn.shaoqunliu.c.hub.auth.security.cli.filter.CliUsernamePasswordAuthenticationFilter;
import cn.shaoqunliu.c.hub.auth.security.cli.handler.CliAuthenticationFailureHandler;
import cn.shaoqunliu.c.hub.auth.security.cli.handler.CliAuthenticationSuccessHandler;
import cn.shaoqunliu.c.hub.auth.security.mgr.MgrFirstAuthenticationProvider;
import cn.shaoqunliu.c.hub.auth.security.mgr.MgrSecondAuthenticationProvider;
import cn.shaoqunliu.c.hub.auth.security.mgr.filter.MgrUsernamePasswordAuthenticationFilter;
import cn.shaoqunliu.c.hub.auth.security.mgr.handler.MgrAuthenticationFailureHandler;
import cn.shaoqunliu.c.hub.auth.security.mgr.handler.MgrAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.Collections;

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
        public DockerRegistryAuthSecurityConfiguration(CliAuthenticationProvider cliAuthenticationProvider) {
            this.cliAuthenticationProvider = cliAuthenticationProvider;
        }

        @Override
        public ProviderManager authenticationManager() {
            return new ProviderManager(Collections.singletonList(
                    cliAuthenticationProvider
            ));
        }

        // with this solution of adding authentication provider
        // the authenticationManager() method of super class
        // may returns null with unknown reasons
//        @Override
//        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//            super.configure(auth);
//            auth.authenticationProvider(cliAuthenticationProvider);
//        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/v1/auth/docker/token**")
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/v1/auth/docker/token").authenticated()
                    .and()
                    .addFilterAt(new CliUsernamePasswordAuthenticationFilter(
                                    authenticationManager(),
                                    "/v1/auth/docker/token",
                                    new CliAuthenticationSuccessHandler(),
                                    new CliAuthenticationFailureHandler()),
                            UsernamePasswordAuthenticationFilter.class);
        }
    }

    @Configuration
    @Order(2)
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        private final AuthenticationProvider mgrFirstAuthenticationProvider;
        private final AuthenticationProvider mgrSecondAuthenticationProvider;

        @Autowired
        public FormLoginWebSecurityConfigurerAdapter(MgrFirstAuthenticationProvider mgrFirstAuthenticationProvider, MgrSecondAuthenticationProvider mgrSecondAuthenticationProvider) {
            this.mgrFirstAuthenticationProvider = mgrFirstAuthenticationProvider;
            this.mgrSecondAuthenticationProvider = mgrSecondAuthenticationProvider;
        }

        @Override
        public ProviderManager authenticationManager() {
            return new ProviderManager(Arrays.asList(
                    mgrFirstAuthenticationProvider,
                    mgrSecondAuthenticationProvider
            ));
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable() // prevent post request from 403 forbidden by CSRF policies
                    .antMatcher("/v1/auth/mgr/token**")
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/v1/auth/mgr/token").authenticated()
                    .and()
                    .addFilterAt(new MgrUsernamePasswordAuthenticationFilter(
                                    authenticationManager(),
                                    "/v1/auth/mgr/token",
                                    new MgrAuthenticationSuccessHandler(),
                                    new MgrAuthenticationFailureHandler()),
                            UsernamePasswordAuthenticationFilter.class);
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
