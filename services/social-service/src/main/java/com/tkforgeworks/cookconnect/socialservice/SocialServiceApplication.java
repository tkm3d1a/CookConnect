package com.tkforgeworks.cookconnect.socialservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@EnableDiscoveryClient
@RefreshScope
public class SocialServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialServiceApplication.class, args);
    }

}