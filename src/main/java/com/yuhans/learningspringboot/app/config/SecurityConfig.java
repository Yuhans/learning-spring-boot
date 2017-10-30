package com.yuhans.learningspringboot.app.config;

import com.yuhans.learningspringboot.service.SpringDataUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers(HttpMethod.GET,"/", "/images/**", "/main.css", "/webjars/**").permitAll()
                .antMatchers(HttpMethod.POST, "/images").hasRole("USER")
                .antMatchers("/imageMessages/**").permitAll()
                .and()
            .formLogin()
                .permitAll()
                .and()
            .logout()
                .logoutSuccessUrl("/");
    }

    @Autowired
    public void configureJpaBasedUsers(AuthenticationManagerBuilder auth,
                                       SpringDataUserDetailsService userDetailsService) throws Exception {
        auth.userDetailsService(userDetailsService);
    }
}
