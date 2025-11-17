package com.tkforgeworks.cookconnect.socialservice.common;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        String url = requestTemplate.url();
        if (url.contains("/realms/") && url.contains("/protocol/openid-connect/token")) {
            log.debug("Skipping UserContext headers for Keycloak token endpoint");
            return;
        }
        if (url.contains("/admin/realms/")) {
            log.debug("Skipping UserContext headers for Keycloak admin endpoint");
            return;
        }

        log.debug("****FeignInterceptor intercept start****");
        log.debug("Feign headers before: {}", requestTemplate.headers());
        requestTemplate.header(UserContext.CORRELATION_ID_HEADER, UserContextHolder.getUserContext().getCorrelationId());
        requestTemplate.header(UserContext.USER_ID_HEADER, UserContextHolder.getUserContext().getUserId());
        requestTemplate.header(UserContext.SOCIAL_ID_HEADER, UserContextHolder.getUserContext().getSocialId());
        log.debug("Feign headers after: {}", requestTemplate.headers());
        log.debug("****FeignInterceptor intercept finish****");
    }
}
