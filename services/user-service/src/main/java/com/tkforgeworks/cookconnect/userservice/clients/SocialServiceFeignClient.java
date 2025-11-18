package com.tkforgeworks.cookconnect.userservice.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "social-service"
)
public interface SocialServiceFeignClient {
}
