package com.tkforgeworks.cookconnect.gatewayserver.filters;

import com.tkforgeworks.cookconnect.gatewayserver.utils.FilterUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Order(1)
@Component
@Slf4j
@RequiredArgsConstructor
public class TrackingFilter implements GlobalFilter{
    private final FilterUtils filterUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        if(isCorrelationIdHeaderPresent(headers)){
            log.debug("Found correlationId in headers: {}", filterUtils.getCorrelationIdHeader(headers));
        } else {
            String correlationId = generateCorrelationId();
            exchange = filterUtils.setCorrelationIdHeader(exchange, correlationId);
            log.debug("Created correlationId: {}", correlationId);
        }

        return chain.filter(exchange);
    }

    private boolean isCorrelationIdHeaderPresent(HttpHeaders headers) {
        return filterUtils.getCorrelationIdHeader(headers) != null;
    }

    private String generateCorrelationId(){
        return UUID.randomUUID().toString();
    }
}
