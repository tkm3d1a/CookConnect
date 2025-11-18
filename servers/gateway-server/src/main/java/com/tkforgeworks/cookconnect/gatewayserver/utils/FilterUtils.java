package com.tkforgeworks.cookconnect.gatewayserver.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.Optional;

@Component
public class FilterUtils {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHENTICATION_HEADER = "X-Authentication-Token";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String SOCIAL_ID_HEADER = "X-Social-Id";

    public String getAuthorizationHeader(HttpHeaders headers) {
        List<String> header = headers.get(AUTHORIZATION_HEADER);
        return Optional.ofNullable(header).flatMap(h -> h.stream().findFirst()).orElse(null);
    }

    public String getAuthenticationToken(HttpHeaders headers) {
        List<String> header = headers.get(AUTHENTICATION_HEADER);
        return Optional.ofNullable(header).flatMap(h -> h.stream().findFirst()).orElse(null);
    }

    public String getCorrelationIdHeader(HttpHeaders headers) {
        List<String> header = headers.get(CORRELATION_ID_HEADER);
        return Optional.ofNullable(header).flatMap(h -> h.stream().findFirst()).orElse(null);
    }

    public String getUserIdHeader(HttpHeaders headers) {
        List<String> header = headers.get(USER_ID_HEADER);
        return Optional.ofNullable(header).flatMap(h -> h.stream().findFirst()).orElse(null);
    }

    public String getSocialId(HttpHeaders headers) {
        List<String> header = headers.get(SOCIAL_ID_HEADER);
        return Optional.ofNullable(header).flatMap(h -> h.stream().findFirst()).orElse(null);
    }

    public ServerWebExchange setAuthorizationHeader(ServerWebExchange exchange, String authorization) {
        return this.setRequestHeader(exchange, AUTHORIZATION_HEADER, authorization);
    }

    public ServerWebExchange setAuthenticationToken(ServerWebExchange exchange, String authenticationToken) {
        return this.setRequestHeader(exchange, AUTHENTICATION_HEADER, authenticationToken);
    }

    public ServerWebExchange setCorrelationIdHeader(ServerWebExchange exchange, String correlationId) {
        return this.setRequestHeader(exchange, CORRELATION_ID_HEADER, correlationId);
    }

    public ServerWebExchange setUserIdHeader(ServerWebExchange exchange, String userId) {
        return this.setRequestHeader(exchange, USER_ID_HEADER, userId);
    }

    public ServerWebExchange setSocialId(ServerWebExchange exchange, String socialId) {
        return this.setRequestHeader(exchange, SOCIAL_ID_HEADER, socialId);
    }

    private ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
        return exchange.mutate().request(exchange.getRequest().mutate().header(name, value).build()).build();
    }


}
