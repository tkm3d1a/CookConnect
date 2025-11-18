package com.tkforgeworks.cookconnect.gatewayserver.filters;

import com.tkforgeworks.cookconnect.gatewayserver.utils.FilterUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class ResponseFilter {
    @Bean
    public GlobalFilter postGlobalFilter(FilterUtils filterUtils) {
        return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
            HttpHeaders headers = exchange.getRequest().getHeaders();
            String correlationId = filterUtils.getCorrelationIdHeader(headers);
            log.debug("Adding correlationId to outbound headers: {}", correlationId);
            exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID_HEADER, correlationId);
            log.debug("Completing outbound request for: {}", exchange.getRequest().getURI());
        }));
    }
}
