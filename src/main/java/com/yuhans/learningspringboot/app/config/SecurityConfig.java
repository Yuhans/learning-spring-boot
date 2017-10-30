package com.yuhans.learningspringboot.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .permitAll();
    }

    @Autowired
    public void confiugureInMemoryUsers(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("yuhans").password("admin").roles("ADMIN", "USER")
                .and()
                .withUser("guest").password("guest").roles("USER")
                .and()
                .withUser("baddie").password("baddie").roles("USER").disabled(true)
                .and()
                .withUser("baddie2").password("baddie").roles("USER").accountLocked(true);
    }
}
