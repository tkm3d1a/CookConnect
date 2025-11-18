package com.tkforgeworks.cookconnect.userservice.clients;

import com.tkforgeworks.cookconnect.userservice.config.KeycloakFeignConfig;
import com.tkforgeworks.cookconnect.userservice.model.dto.KeycloakTokenResponseDto;
import com.tkforgeworks.cookconnect.userservice.model.dto.KeycloakUserRequestDto;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "keycloak-admin-client",
        url = "${tkforgeworks.keycloak.auth-server-url}",
        configuration = KeycloakFeignConfig.class
)
public interface KeycloakFeignClient {

    //Auth admin/get admin token
    @PostMapping(
            value = "/realms/{realm}/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    KeycloakTokenResponseDto getToken(
            @PathVariable("realm") String realm,
            @QueryMap Map<String, ?> queryParams
    );

    //Create new user
    @PostMapping(
            value = "/admin/realms/{realm}/users",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void createUser(
            @PathVariable("realm") String realm,
            @RequestHeader("Authorization") String token,
            @RequestBody KeycloakUserRequestDto request
    );

    //Delete user
    @DeleteMapping(
            value = "/admin/realms/{realm}/users/{userId}"
    )
    void deleteUser(
            @PathVariable("realm") String realm,
            @RequestHeader("Authorization") String token,
            @PathVariable("userId") String userId
    );

    //get user
    @GetMapping(
            value = "/admin/realms/{realm}/users"
    )
    List<Map<String, Object>> getUsers(
            @PathVariable("realm") String realm,
            @RequestHeader("Authorization") String token,
            @RequestParam("username") String username,
            @RequestParam("exact") boolean exact
    );
}
