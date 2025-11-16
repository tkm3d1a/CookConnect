package com.tkforgeworks.cookconnect.socialservice.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationHelper {

    public boolean canAccessUserResource(String requestedUserId){
        log.debug("=== canAccessUserResource called ===");
        log.debug("Requested userId: {}", requestedUserId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Authentication type: {}", authentication.getClass().getName());

        if(hasRole(authentication, "ROLE_cookconnect_admin")){
            log.warn("Authorized access as admin user");
            return true;
        }

        String authenticatedUserId = extractUserIdFromAuthentication(authentication);
        log.debug("Authenticated userId from JWT: {}", authenticatedUserId);

        boolean authorized = requestedUserId != null &&
                requestedUserId.equals(authenticatedUserId);

        if (authorized) {
            log.debug("Authorized access for user: {}", authenticatedUserId);
        } else {
            log.warn("Access denied. Requested userId: {}, Authenticated userId: {}",
                    requestedUserId, authenticatedUserId);
        }
        log.debug("=== canAccessUserResource end ===");
        return authorized;
    }

    private boolean hasRole(Authentication authentication, String role){
        boolean hasRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);

        log.debug("Checking role '{}': {}", role, hasRole);
        log.debug("User authorities: {}", authentication.getAuthorities());

        return hasRole;
    }

    private String extractUserIdFromAuthentication(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return jwt.getClaimAsString("sub");
        }

        log.warn("Authentication is not a JwtAuthenticationToken");
        return null;
    }
}
