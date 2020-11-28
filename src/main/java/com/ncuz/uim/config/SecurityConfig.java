package com.ncuz.uim.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig
        extends WebSecurityConfigurerAdapter
{
    @Autowired
    Environment env;
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http
                .csrf().disable()
                .authorizeRequests()
//                .antMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception
    {
        System.out.println("configureGlobal :"+env.getProperty("spring.security.user.name")+" | "+env.getProperty("spring.security.user.password")+
                " | "+encoder().encode(env.getProperty("spring.security.user.password"))+
                " | "+env.getProperty("server.token"));
        auth.inMemoryAuthentication()
                .withUser(env.getProperty("spring.security.user.name"))
                .password(encoder().encode(env.getProperty("spring.security.user.password")))
                .roles("");
    }
}
