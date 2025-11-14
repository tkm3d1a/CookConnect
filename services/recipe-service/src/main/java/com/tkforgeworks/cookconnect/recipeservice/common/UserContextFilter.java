package com.tkforgeworks.cookconnect.recipeservice.common;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class UserContextFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        UserContextHolder.getUserContext()
                .setCorrelationId(request.getHeader(UserContext.CORRELATION_ID_HEADER));
        UserContextHolder.getUserContext()
                .setAuthToken(request.getHeader(UserContext.AUTHORIZATION_HEADER));
        UserContextHolder.getUserContext()
                .setUserId(request.getHeader(UserContext.USER_ID_HEADER));
        UserContextHolder.getUserContext()
                .setSocialId(request.getHeader(UserContext.SOCIAL_ID_HEADER));

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
