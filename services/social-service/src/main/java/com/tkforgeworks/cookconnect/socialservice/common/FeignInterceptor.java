package com.tkforgeworks.cookconnect.socialservice.common;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        log.debug("****FeignInterceptor intercept start****");
        log.debug("Feign headers before: {}", requestTemplate.headers());
        requestTemplate.header(UserContext.CORRELATION_ID_HEADER, UserContextHolder.getUserContext().getCorrelationId());
//        requestTemplate.header(UserContext.AUTHENTICATION_HEADER, UserContextHolder.getUserContext().getAuthToken());
        requestTemplate.header(UserContext.USER_ID_HEADER, UserContextHolder.getUserContext().getUserId());
        requestTemplate.header(UserContext.SOCIAL_ID_HEADER, UserContextHolder.getUserContext().getSocialId());
        log.debug("Feign headers after: {}", requestTemplate.headers());
        log.debug("****FeignInterceptor intercept finish****");
    }
}
