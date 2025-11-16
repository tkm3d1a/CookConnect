package com.tkforgeworks.cookconnect.userservice.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class IncomingRequestFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("=======Incoming Request=======\n")
                .append(request.getMethod())
                .append(" ")
                .append(request.getRequestURI())
                .append("\n");

        Collections.list(request.getHeaderNames())
                .forEach(header -> sb.append("\t")
                        .append(header)
                        .append(": ")
                        .append(request.getHeader(header))
                        .append("\n"));

        log.debug(sb.toString());
        filterChain.doFilter(request, response);
    }
}
