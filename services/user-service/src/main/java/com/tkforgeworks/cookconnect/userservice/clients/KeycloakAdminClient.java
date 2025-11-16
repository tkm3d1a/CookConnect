package com.tkforgeworks.cookconnect.userservice.clients;

import com.tkforgeworks.cookconnect.userservice.model.dto.KeycloakTokenResponseDto;
import com.tkforgeworks.cookconnect.userservice.model.dto.KeycloakUserRequestDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminClient {

    private final KeycloakFeignClient keycloakFeignClient;

    @Value("${tkforgeworks.keycloak.admin.client-id}")
    private String adminClientId;
    @Value("${tkforgeworks.keycloak.admin.client-secret}")
    private String adminClientSecret;
    @Value("${tkforgeworks.keycloak.realm}")
    private String realm;

    public String createUser(String username, String email, String password,
                             String firstName, String lastName) {
        String adminToken = getAdminToken();
        log.debug("Admin Token: {}", adminToken);
        Map<String,Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", password);
        credentials.put("temporary", false);

        KeycloakUserRequestDto userRequestDto = new KeycloakUserRequestDto(
                username,
                email,
                firstName,
                lastName,
                true,
                true,
                List.of(credentials)
        );

        try{
            keycloakFeignClient.createUser(realm,"Bearer " + adminToken, userRequestDto);
            log.info("Keycloak User created successfully: {}", username);
            return userIdFromResponse(username, adminToken);
        } catch(FeignException feignException){
            log.error("Failed to create keycloak user: {}",  feignException.getMessage());
            throw new RuntimeException("Failed to create keycloak user: " + feignException.getMessage());
        }
    }

    public void deleteUser(String userId) {
        String adminToken = getAdminToken();

        try{
            keycloakFeignClient.deleteUser(realm,"Bearer " + adminToken, userId);
            log.info("Keycloak User deleted successfully: {}", userId);
        } catch(FeignException feignException){
            log.error("Failed to delete keycloak user: {}",  feignException.getMessage());
        }
    }

    private String getAdminToken() {
        try{
            Map<String, String> formParams = Map.of(
                    "client_id", adminClientId,
                    "client_secret", adminClientSecret,
                    "grant_type", "client_credentials"
            );
            KeycloakTokenResponseDto response = keycloakFeignClient
                    .getToken(realm,formParams);
            log.debug("accessToken: {}", response.accessToken());
            return response.accessToken();
        } catch(FeignException feignException){
            log.error("Failed to get admin token: {}",  feignException.getMessage());
            throw new RuntimeException("Failed to get authenticate with keycloak");
        }
    }

    private String userIdFromResponse(String username, String adminToken) {
        try{
            List<Map<String, Object>> users = keycloakFeignClient.getUsers(
                    realm,
                    "Bearer " + adminToken,
                    username,
                    true
            );

            if(users != null && !users.isEmpty()){
                String userId = users.getFirst().get("id").toString();
                log.info("User {} found: {}", userId, username);
                return userId;
            }

            throw new RuntimeException("User not found");
        } catch(FeignException feignException){
            log.error("Failed to get search for user: {}",  feignException.getMessage());
            throw new RuntimeException("Failed to retrieve user ID from Keycloak");
        }
    }

}
