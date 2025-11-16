package com.tkforgeworks.cookconnect.socialservice.clients;

import com.tkforgeworks.cookconnect.socialservice.config.FeignClientConfig;
import com.tkforgeworks.cookconnect.socialservice.model.dto.SocialCreateResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "user-service",
        configuration = FeignClientConfig.class
)
public interface UserServiceFeignClient {
    @GetMapping("/api/v1/internal/{userId}")
    SocialCreateResponseDto getUserById(@PathVariable("userId") String userId);

    @PostMapping("/api/v1/internal/{userId}/social")
    void addSocialInteraction(@PathVariable("userId") String userId);

    @DeleteMapping("/api/v1/internal/{userId}/social")
    void removeSocialInteraction(@PathVariable("userId") String userId);
}
