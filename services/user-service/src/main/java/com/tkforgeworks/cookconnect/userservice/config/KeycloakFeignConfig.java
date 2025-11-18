package com.tkforgeworks.cookconnect.userservice.config;

import feign.Logger;
import feign.Response;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.FormEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class KeycloakFeignConfig {
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return new ErrorDecoder.Default();
    }

    @Bean
    public Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new FormEncoder(new SpringEncoder(messageConverters));
    }

    @Slf4j
    public static class KeycloakErrorDecoder implements ErrorDecoder {

        @Override
        public Exception decode(String methodKey, Response response) {
            String message = String.format("Keycloak API error: %s - %s",
                    response.status(), response.reason());
            log.error(message);

            return switch (response.status()) {
                case 400 -> new RuntimeException("Bad request to Keycloak: " + response.reason());
                case 401 -> new RuntimeException("Unauthorized access to Keycloak");
                case 403 -> new RuntimeException("Forbidden: Insufficient permissions");
                case 404 -> new RuntimeException("Keycloak resource not found");
                case 409 -> new RuntimeException("User already exists in Keycloak");
                default -> new RuntimeException("Keycloak error: " + message);
            };
        }
    }
}
