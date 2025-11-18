package com.tkforgeworks.cookconnect.recipeservice.clients;


import com.tkforgeworks.cookconnect.recipeservice.common.dto.UserServiceResponseDto;
import com.tkforgeworks.cookconnect.recipeservice.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        configuration = FeignClientConfig.class
)
public interface UserServiceFeignClient {
    @GetMapping("/api/v1/internal/{userId}")
    UserServiceResponseDto getUserById(@PathVariable("userId") String userId);
}
