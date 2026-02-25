package com.tkforgeworks.cookconnect.socialservice.message.consumer;

import com.tkforgeworks.cookconnect.socialservice.message.model.UserChangeEvent;
import com.tkforgeworks.cookconnect.socialservice.service.SocialInteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class UserEventConsumer {
    private final SocialInteractionService socialInteractionService;

    @Bean
    public Consumer<UserChangeEvent> handleUserChange(){
        return event -> {
            log.debug("Received UserChange event: {}", event);

            try {
                switch (event.getChangeType()){
                    case ACCOUNT_CLOSED -> {
                        socialInteractionService.handleUserAccountStatus(event, "delete");
                        log.debug("Account closed");
                    }
                    case ACCOUNT_DELETED -> {
                        socialInteractionService.handleUserAccountStatus(event, "closed");
                        log.debug("Account deleted");
                    }
                    case PRIVACY_STATUS ->  {
                        socialInteractionService.handleUserAccountStatus(event, "private");
                        log.debug("Privacy status");
                    }
                    default -> {
                        log.debug("Event of type {} not handled", event.getChangeType());
                    }
                }
            } catch (Exception ex) {
                log.error("Error handling UserChange event", ex);
                throw ex;
            }
        };
    }
}
