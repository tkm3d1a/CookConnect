package com.tkforgeworks.cookconnect.recipeservice.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class UserContextFilter extends OncePerRequestFilter {

    @Value("${tkforgeworks.jwt.converter.principle-attribute}")
    private String principleAttribute;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        UserContextHolder.getUserContext()
                .setCorrelationId(request.getHeader(UserContext.CORRELATION_ID_HEADER));
        UserContextHolder.getUserContext()
                .setAuthToken(request.getHeader(UserContext.AUTHORIZATION_HEADER));
        UserContextHolder.getUserContext()
                .setUserId(request.getHeader(UserContext.USER_ID_HEADER));
        UserContextHolder.getUserContext()
                .setSocialId(request.getHeader(UserContext.SOCIAL_ID_HEADER));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();

            if (UserContextHolder.getUserContext().getUsername() == null ||
                    UserContextHolder.getUserContext().getUsername().isEmpty()) {
                String username = jwt.getClaim(principleAttribute);
                UserContextHolder.getUserContext().setUsername(username);
                log.debug("Extracted username from JWT: {}", username);
            }

            if (UserContextHolder.getUserContext().getUserId() == null ||
                    UserContextHolder.getUserContext().getUserId().isEmpty()) {
                String userId = jwt.getClaimAsString("sub");
                UserContextHolder.getUserContext().setUserId(userId);
                log.debug("Extracted userId from JWT: {}", userId);
            }
        }

        log.debug("UserContext populated - CorrelationId: {}, Username: {}, UserId: {}",
                UserContextHolder.getUserContext().getCorrelationId(),
                UserContextHolder.getUserContext().getUsername(),
                UserContextHolder.getUserContext().getUserId());

        filterChain.doFilter(request, response);
    }
}
