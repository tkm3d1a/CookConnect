package com.tkforgeworks.cookconnect.userservice.config;


import com.tkforgeworks.cookconnect.userservice.common.FeignInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor() {
        return template -> {
            if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken token) {
                template.header("Authorization", "Bearer " + token.getToken().getTokenValue());
            }
        };
    }

    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return new FeignInterceptor();
    }
}
