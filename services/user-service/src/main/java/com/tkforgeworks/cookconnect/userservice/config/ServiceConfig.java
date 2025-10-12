package com.tkforgeworks.cookconnect.userservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "tkforgeworks")
@Getter
@Setter
public class ServiceConfig {
    private Custom custom;

    @Getter
    @Setter
    public static class Custom {
        private String name;
        private String description;
    }
}
